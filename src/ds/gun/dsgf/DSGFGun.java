/**
 * 
 */
package ds.gun.dsgf;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;
import ds.Hud;
import ds.constant.ConstantManager;
import ds.gun.AbstractGun;
import ds.gun.BulletWave;
import ds.gun.FireSolution;
import ds.gun.IDataSaver;
import ds.gun.IVirtualBullet;
import ds.gun.VirtualBullet;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

/**
 * @author f4
 * 
 */
public class DSGFGun extends AbstractGun implements IDataSaver
{
	/**
	 * arbre des statistiques
	 */
	private Tree					m_tree;

	/**
	 * derniere vague lancee
	 */
	private BulletWave				m_lastWave;

	/**
	 * liste des vagues actuellement en l'air
	 */
	private ArrayList<BulletWave>	m_waves;

	private int						m_nbSamples;
	
	/**
	 * si true balance des vagues en permanence
	 * sinon uniquement lors d'un vrai tir
	 */
	private int 				m_flood;

	/**
	 * Constructeur
	 * 
	 * @param owner
	 *            proprietaire du gun
	 * @param targetManager
	 *            gestionnaire de cibles
	 */
	public DSGFGun( String specialisation, AdvancedRobot owner, ITargetManager targetManager, boolean bIsDataSaver )
	{
		super( "dsgf"+specialisation, owner, targetManager, Color.orange );

		ConstantManager cm = ConstantManager.getInstance();
		int maxNodeLeafCount = (int)cm.getIntegerConstant( "gun.dsgf"+specialisation+".maxNodeLeafCount" ).longValue();
		int minNodeLeafCount = (int)cm.getIntegerConstant( "gun.dsgf"+specialisation+".minNodeLeafCount" ).longValue();
		m_flood = (int)cm.getIntegerConstant( "gun.dsgf"+specialisation+".flood" ).longValue();
		
		m_tree = new Tree(maxNodeLeafCount, minNodeLeafCount, bIsDataSaver);
		m_waves = new ArrayList<BulletWave>();
		m_lastWave = null;

		m_nbSamples = (int)ConstantManager.getInstance().getIntegerConstant(
				"gun.dsgf.nbSamples" ).longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.AbstractGun#Act()
	 */
	public void Act( double power )
	{
		IVirtualBot target;
		try
		{
			target = getTargetManager().getCurrentTarget();

			// on ne lance pas de vague si l'on est disable
			// l'enemi risque de ne pas avoir un comportement normal
			if( getOwner().getEnergy() > 0.1 )
			{
				// lance une vague
				fireWave( target, power );
			}
		}
		catch( TargetException e )
		{
			// rien a faire on ne lance pas de vague s'il n'y a pas de cible
		}

		// TODO: methode partiellement en commun avec GunStats::update
		// met a jour les vagues existantes
		for( BulletWave wave : m_waves )
		{
			wave.update();
		}

		// suppression des vagues perdues
		Iterator<BulletWave> itBW = m_waves.iterator();
		// pour chaque vague
		while( itBW.hasNext() )
		{
			BulletWave wave = itBW.next();
			VirtualBullet vb = wave.getBullets().get( 0 );
			try
			{
				target = getTargetManager().getCurrentTarget();
				// si la balle a dépassé la cible + 30unités
				if( vb.travelDistance() - 30 > vb.getStartPosition().distance(
						target.getPosition() ) )
				{
					// suppresion de la vague
					itBW.remove();
				}
			}
			catch( TargetException e )
			{
				// suppresion de la vague
				itBW.remove();
			}
		}

		// calcul des statistiques
		// pour chaque vague
		for( BulletWave wave : m_waves )
		{
			// pour chaque balle
			for( VirtualBullet vb : wave.getBullets() )
			{
				try
				{
					target = getTargetManager().getCurrentTarget();
					// si la balle a touché
					if( vb.getCurrentPosition().distance( target.getPosition() ) < target.getSize() / 2 )
					{
						// si la balle n'avait pas encore touché avant ce turn
						if( !vb.hasHit() )
						{
							if( !(m_flood>0 && wave.estReelle()) )
							{
								// ajout de la stat
								m_tree.add( (IndexedVirtualBullet)vb );
							}
						}
						vb.setHit( true );
					}
				}
				catch( TargetException e )
				{
					// rien a faire
				}
			}
		}
	}

	/**
	 * lance une vague
	 * @param target cible
	 * @param power puissance du tir
	 * @param bReelle true si c'est une vague qui correspond a un vrai tir
	 */
	private void fireWave( IVirtualBot target, double power, boolean bReelle )
	{
		// pas de vague si on floode et que c'est une vague réelle
		/*if( m_flood>0 && bReelle )
			return;*/
		
		// pas de vague si on ne floode pas et que ce n'est pas une reellle
		if( m_flood==0 && !bReelle )
			return;
		
		if( m_lastWave == null
				|| m_lastWave.getLaunchTurn() <= getOwner().getTime() - m_flood
				|| m_lastWave.getLaunchRound() < getOwner().getRoundNum()
				|| bReelle )
		{
			BulletWave bw = new BulletWave( getOwner().getRoundNum(),
					getOwner().getTime(), bReelle );
			m_waves.add( bw );
			m_lastWave = bw;
			double maxEscapeAngle = Math.asin( 8.0 / (20 - 3.0 * power) );
			
			// récupère le tableau des stats choisis par le gun pour pouvoir colorer les balles
			SegmentationInfo si = new SegmentationInfo( getOwner(), target );
			// l'appel suivant donne l'index de la solution de tir, qu'il faut
			// convertir en angle
			double[] hitChances = m_tree.getSolutionSamples( si );
			
			
			for( int i = 0; i < m_nbSamples; i++ )
			{
				int angleIndex = i;
				IndexedVirtualBullet ivb = new IndexedVirtualBullet(
						target, this, new Point2D.Double( getOwner()
								.getX(), getOwner().getY() ),
						angleIndex, power, maxEscapeAngle, hitChances[i] );
				m_lastWave.addBullet( ivb );
				ivb.update(); // advance one tick
			}
		}
	}

	/**
	 * lance une vague
	 * @param target cible
	 * @param power puissance du tir
	 */
	private void fireWave( IVirtualBot target, double power )
	{
		fireWave( target, power, false );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.AbstractGun#computeFireSolution(double)
	 */
	@Override
	protected FireSolution computeFireSolution( double bulletPower )
	{
		IVirtualBot target;
		double fiabilite = 0;

		double angle = 0;
		try
		{
			target = getTargetManager().getCurrentTarget();
			SegmentationInfo si = new SegmentationInfo( getOwner(), target );
			// l'appel suivant donne l'index de la solution de tir, qu'il faut
			// convertir en angle
			FireIndex solutionIndex = m_tree.getSolution( si );
			double relativeSolutionIndex = solutionIndex.getRelativeAngleIndex();
			double maxEscapeAngle = Math.asin( 8.0 / (21.0 - 3.0 * bulletPower) );
			double step = 1 / (double)m_nbSamples;
			angle = target.getLateralDirection() * relativeSolutionIndex * step * maxEscapeAngle;
			angle = robocode.util.Utils.normalRelativeAngle( angle
					+ target.getAbsoluteBearingRadians()
					- getOwner().getGunHeadingRadians() );
			fiabilite = solutionIndex.getFiabilite();
		}
		catch( TargetException e )
		{
			// retourne une fiabilité 0
		}
		return new FireSolution( angle, angle
				+ getOwner().getGunHeadingRadians(), fiabilite );
	}

	/* (non-Javadoc)
	 * @see ds.gun.AbstractGun#virtualFire(double)
	 */
	@Override
	public void virtualFire( double power )
	{
		try
		{
			IVirtualBot target = getTargetManager().getCurrentTarget();
			fireWave( target, power, true );
		}
		catch( TargetException e )
		{
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.AbstractGun#paint(ds.Hud, long)
	 */
	@Override
	public void paint( Hud hud, long tick )
	{
		hud.setColor( Color.gray );
		for( BulletWave bw : m_waves )
		{
			if( bw.estReelle() )
			{
				for( IVirtualBullet vb : bw.getBullets() )
				{
					float blue = (float)((IndexedVirtualBullet)vb).getHitChance();
					if ( vb.hasHit() )
						hud.setColor( Color.red );
					else
						hud.setColor( new Color( 0, 0, blue ) );
					hud.drawFilledCircle( vb.getCurrentPosition().getX(), vb.getCurrentPosition().getY(), 3 );
				}
			}
		}
		
		try
		{
			hud.setColor( Color.red );
			IVirtualBot target;
			target = getTargetManager().getCurrentTarget();
			SegmentationInfo si = new SegmentationInfo( getOwner(), target );
			Vector<String> v = m_tree.getSolutionString( si );
			int i = 0;
			for( String s : v )
			{
				if( i < 20 )
					hud.drawString( s, 50, 500-i*15 );
				i++;
			}
		}
		catch( TargetException e )
		{
		}
		
		m_tree.paint(hud, tick);
	}

	/* (non-Javadoc)
	 * @see ds.gun.IDataSaver#save()
	 */
	@Override
	public void save()
	{
	}

}
