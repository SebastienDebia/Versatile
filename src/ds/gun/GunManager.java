package ds.gun;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.RobocodeFileOutputStream;
import robocode.RobotDeathEvent;
import ds.Hud;
import ds.IEventListener;
import ds.Math2;
import ds.constant.ConstantManager;
import ds.gun.dsgf.DSGFGun;
import ds.gun.hot.HoTGun;
import ds.gun.linear.LinearGun;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

public class GunManager implements IGunManager, Hud.Painter, IEventListener.BattleEnded
{
	/**
	 * dernier gun selectionne
	 */
	private AbstractGun				m_lastSelectedGun;

	/**
	 * liste des guns
	 */
	private ArrayList<AbstractGun>	m_guns;

	/**
	 * proprietaire du gunmanager
	 */
	private AdvancedRobot			m_owner;

	/**
	 * gestionnaire de cibles
	 */
	private ITargetManager			m_targetManager;

	/**
	 * statistiques des guns
	 */
	private GunStats				m_gunStats;

	/**
	 * Gun actuellement sélectionné
	 */
	AbstractGun 					m_selectedGun = null;
	
	/**
	 * puissance de tir, calcul�e pour le tick suivant
	 */
	private double					m_firePower;

	private IDataSaver	m_dataSaverGun;

	/**
	 * Constructeur construit les differents guns utilisés par le robot
	 */
	public GunManager( AdvancedRobot owner, ITargetManager targetManager )
	{
		m_owner = owner;
		m_targetManager = targetManager;

		// initialisation du tableau des guns
		m_guns = new ArrayList<AbstractGun>();

		if( ConstantManager.getInstance().getBooleanConstant( "gun.hot.active" ) )
			m_guns.add( new HoTGun( owner, targetManager ) );
		
		if( ConstantManager.getInstance().getBooleanConstant( "gun.dsgfFast.active" ) )
			m_guns.add( new DSGFGun( "Fast", owner, targetManager, false ) );
		if( ConstantManager.getInstance().getBooleanConstant( "gun.dsgfSlow.active" ) )
			m_guns.add( new DSGFGun( "Slow", owner, targetManager, true ) );
		if( ConstantManager.getInstance().getBooleanConstant( "gun.dsgfAWS.active" ) )
			m_guns.add( new DSGFGun( "AWS", owner, targetManager, false ) );
		
		if( ConstantManager.getInstance().getBooleanConstant( "gun.linear.active" ) )
			m_guns.add( new LinearGun( owner, targetManager ) );

		m_gunStats = new GunStats( (IVirtualBot) owner, targetManager, m_guns );

		m_lastSelectedGun = null;
		
		m_firePower = 0.0;
	}
	
	int n;
	double medPower;

	@Override
	public void Act()
	{
		m_owner.setAdjustGunForRobotTurn( true );
		
		boolean bShootingRound = readyToShoot();
		
		// verifie qu'on peut tirer
		// TODO: verifier m_owner.getGunTurnRemainingRadians()
		if( bShootingRound )
		{
			// tire
			m_owner.setFire( m_firePower );

			// affiche des stats sur la puissance de tir
			/*try {
				if( n > 0 )
					medPower *= n;
				medPower+=m_firePower;
				n++;
				medPower /= n;
				System.out.println( "median power = " + medPower + " power = " + m_firePower + " dist = " + m_targetManager.getCurrentTarget().getDistance());
			} catch (TargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		if( m_guns.size() <= 0 )
			return;
		
		// certains guns ont du cacul a faire
		for( AbstractGun gun : m_guns )
		{
			gun.Act( m_firePower );
		}

		// calcule les statistiques
		m_gunStats.update();

		// selectionne le meilleur gun
		m_selectedGun = m_gunStats.getBestGun();
		if( m_selectedGun != m_lastSelectedGun )
		{
			m_lastSelectedGun = m_selectedGun;
			if( ConstantManager.getInstance().getBooleanConstant( "debug" ) )
				System.out.println( "Using " + m_lastSelectedGun.getName() );
		}

		// selectionne la puissance du tir
		m_firePower = computeFirePower();

		// tourne le cannon
		FireSolution fs = m_selectedGun.getFireSolution( m_firePower );
		// ajoute un minuscule angle random pour contrer les bots qui ne bougent pas (energyDome)
		double angle = fs.getAngle() + (Math.random()*Math2.PI/512)-Math.PI/256;
		m_owner.setTurnGunRightRadians( angle );
		
		if( bShootingRound )
		{
			try
			{
				for( AbstractGun gun : m_guns )
				{
					// tir d'une balle virtuelle pour les stats des guns
					m_gunStats.virtualFire( gun, m_firePower );
					// action possible de chaque gun lors d'un tir réell
					gun.virtualFire( m_firePower );
				}
			}
			catch( TargetException e )
			{
			}
		}
	}

	private boolean readyToShoot()
	{
		return m_firePower > 0 && m_owner.getGunHeat() <= 0 && m_owner.getEnergy() > m_firePower && m_targetManager.HasTarget() 
				&& Math.abs(m_owner.getGunTurnRemainingRadians()) == 0 /*<= Math.PI/64*/;
	}

	/**
	 * @return the next fire power
	 */
	private double computeFirePower()
	{
		double power = Math.min( 2.5, Math.max( m_owner.getEnergy()-0.2, 0 ) );
		try
		{
			/*double targetDistance = m_targetManager.getCurrentTarget().getDistance();
			if ( m_owner.getEnergy() > 5 && targetDistance > 0 )
				power = (200/targetDistance)*(Math.max(m_owner.getEnergy()+50, 100))/5/10;
			else
				power = 0.1;
			*/

			double targetDistance = m_targetManager.getCurrentTarget().getDistance();
			targetDistance = Math.min( 250/targetDistance, 1 ); // maximum at 250 px
			//targetDistance = Math.min( 1+(250-targetDistance)/400, 1 ); // maximum at 250 px
			double factor = Math.min( m_owner.getEnergy(), 25 ) * 1/25; // maximum at 25 hp
			if ( m_owner.getEnergy() > 0.1 )
				power = targetDistance*factor*robocode.Rules.MAX_BULLET_POWER;
			else
				power = 0;

			power *= Math.min( m_gunStats.getScore( m_selectedGun ) + 0.84, 1 ); // under 0.16% certainty reduce fire power
			power *= m_selectedGun.getConfidence();
			power = Math.min( power, robocode.Rules.MAX_BULLET_POWER );
			power = Math.min( power, (m_targetManager.getCurrentTarget().getEnergy()+5)/4 ); // pas besoin de tirer fort si l'autre est presque mort
		}
		catch( TargetException e1 )
		{
		}
		return power;
	}

	@Override
	public void paint( Hud hud, long tick )
	{
		m_gunStats.paint( hud, tick );
		if( m_selectedGun != null )
			m_selectedGun.paint( hud, tick );
	}

	/* (non-Javadoc)
	 * @see ds.IEventListener.BattleEnded#onBattleEnded(robocode.BattleEndedEvent)
	 */
	@Override
	public void onBattleEnded( BattleEndedEvent event )
	{
		m_dataSaverGun.save();
	}

}
