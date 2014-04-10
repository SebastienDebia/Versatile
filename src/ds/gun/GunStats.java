/**
 * 
 */
package ds.gun;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import robocode.AdvancedRobot;

import ds.*;
import ds.constant.ConstantManager;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

/**
 * @author f4 Enregistre les statistiques des guns Permet de lancer des balles
 *         virtuelles et de les suivre
 */
public class GunStats implements Hud.Painter
{
	/**
	 * liste des guns
	 */
	private ArrayList<AbstractGun>			m_guns;

	/**
	 * liste des scores
	 */
	private HashMap<AbstractGun, Double>	m_scores;

	/**
	 * dernière vague lancée
	 */
	private BulletWave						m_lastWave;

	/**
	 * liste des vagues actuellement en l'air
	 */
	private ArrayList<BulletWave>			m_waves;

	/**
	 * proprietaire
	 */
	private IVirtualBot						m_owner;

	/**
	 * gestionnaire de cibles
	 */
	private ITargetManager					m_targetManager;

	/**
	 * rolling average depth pour la selection du gun 
	 */
	private int	m_rollingDepth;
	
	private AbstractGun						m_currentBestGun = null;

	/**
	 * constructeur
	 * 
	 * @param owner
	 *            proprietaire
	 * @param guns
	 *            liste des guns dont on tient les stats
	 */
	public GunStats( IVirtualBot owner, ITargetManager targetManager,
			ArrayList<AbstractGun> guns )
	{
		m_owner = owner;
		m_targetManager = targetManager;
		m_guns = guns;
		m_waves = new ArrayList<BulletWave>();
		m_lastWave = null;
		m_scores = new HashMap<AbstractGun, Double>();
		m_rollingDepth = (int)ConstantManager.getInstance().getIntegerConstant( "gun.selector.statsDepth" ).longValue();
		for( AbstractGun gun : m_guns )
		{
			m_scores.put( gun, 0.0 );
		}
	}

	public void virtualFire( AbstractGun gun, double power ) throws TargetException
	{
		// si on a change de tour depuis la derniere vague ou s'il n'y en a pas
		// encore
		if( m_lastWave == null || m_lastWave.getDateTime().before( m_owner.getDateTime() ) )
		{
			// creation d'une nouvelle vague
			m_lastWave = new BulletWave( m_owner.getDateTime().getRound(), m_owner
					.getDateTime().getTurn(), true );
			m_waves.add( m_lastWave );
		}
		Point2D.Double position = new Point2D.Double( m_owner.getX(), m_owner
				.getY() );
		FireSolution fs = gun.getFireSolution( power );

		VirtualBullet vb = new VirtualBullet( m_targetManager
				.getCurrentTarget(), gun, position, fs.getAbsoluteAngle(),
				power );
		m_lastWave.addBullet( vb );
		//vb.update(); // advance one tick
	}

	/**
	 * donne le gun de score le plus élevé
	 * 
	 * @return
	 */
	public AbstractGun getBestGun()
	{
		Double bestScore = -1.0;
		AbstractGun bestGun = null;
		for( AbstractGun gun : m_guns )
		{
			Double score = m_scores.get( gun );
			if( score >= bestScore )
			{
				bestScore = score;
				bestGun = gun;
			}
		}
		m_currentBestGun = bestGun;
		return bestGun;
	}

	/**
	 * mise à jour des statistiques
	 */
	public void update()
	{
		// mise a jour des vagues
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
			try
			{
				VirtualBullet vb = wave.getBullets().get( 0 );
				
				// si la balle a dépassé la cible + 30unités
				if( vb.travelDistance() - 30 > vb.getStartPosition().distance(
						m_targetManager.getCurrentTarget().getPosition() ) )
				{
					// maj des stats
					computeStats( wave );
					// suppresion de la vague
					itBW.remove();
				}
			}
			catch( TargetException e )
			{
				// suppresion de la vague
				itBW.remove();
			}
			catch( java.lang.IndexOutOfBoundsException e )
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
				IVirtualBot target;
				try
				{
					target = m_targetManager.getCurrentTarget();
					if( vb.getCurrentPosition().getX() >= target
							.getBottomLeftCorner().getX()
							&& vb.getCurrentPosition().getY() >= target
									.getBottomLeftCorner().getY()
							&& vb.getCurrentPosition().getX() <= target
									.getUpperRightCorner().getX()
							&& vb.getCurrentPosition().getY() <= target
									.getUpperRightCorner().getY() )
					{
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
	 * calcule les statistiques en fonction des hits de la vague
	 * methode apellee quand la vague a depasse la cible et va etre supprimee
	 * @param wave
	 *            vague dont on calcule les stats
	 */
	private void computeStats( BulletWave wave )
	{
		// pour chaque balle
		for( VirtualBullet vb : wave.getBullets() )
		{
			if( vb.hasHit() )
			{
				Double score = m_scores.get( vb.getFiringGun() );
				score = ds.Utils.rollingAvg(score, 1, m_rollingDepth, 1);
				m_scores.put( vb.getFiringGun(), score );
			}
			else
			{
				Double score = m_scores.get( vb.getFiringGun() );
				score = ds.Utils.rollingAvg(score, 0, m_rollingDepth, 1);
				m_scores.put( vb.getFiringGun(), score );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.Hud.Painter#paint(ds.Hud, long)
	 */
	@Override
	public void paint( Hud hud, long tick )
	{
		int i = 0;
		for( BulletWave wave : m_waves )
		{
			int j = 0;
			hud.setColor( Color.lightGray );
			hud.drawString( "wave " + i, 5, 5 + i * 10 );
			for( VirtualBullet bullet : wave.getBullets() )
			{
				hud.setColor( bullet.getFiringGun().getColor() );
				if( m_currentBestGun == bullet.getFiringGun() )
					hud.setColor( Color.red );
				if( bullet.hasHit() )
					hud.setColor( Color.orange );
				hud.drawString( "[" + bullet.hasHit() + "]", 50 + j * 50,
						5 + i * 10 );
				hud.drawLine( bullet.getStartPosition().getX(), bullet
						.getStartPosition().getY(), bullet.getCurrentPosition()
						.getX(), bullet.getCurrentPosition().getY() );
				j++;
			}
			i++;
		}

		i = 0;
		for( AbstractGun gun : m_guns )
		{
			if( gun == m_currentBestGun )
				hud.setColor( Color.green );
			else
				hud.setColor( Color.lightGray );
			hud.drawString( gun.getName() + ":\t" + String.format( "%.4f", m_scores.get( gun ) ), 400,
					5 + i * 10 );
			i++;
		}
	}
	
	public double getScore( AbstractGun gun )
	{
		return m_scores.get( gun );
	}

}
