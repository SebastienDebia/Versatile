package ds;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import ds.constant.ConstantManager;
import ds.gun.GunManager;
import ds.movement.IMovementManager;
import ds.movement.MovementManager;
import ds.radar.Radar;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetManager;

public class Versatile extends PluggableRobot implements IVirtualBot
{

	// /////////////////////////////////////////////
	// methodes publiques
	// /////////////////////////////////////////////

	// /////////////////////////////////////////////
	// methodes protected
	// /////////////////////////////////////////////

	/**
	 * initialisation des parametres du robot methode appelee au debut d'une
	 * bataille
	 */
	protected void initializeBattle()
	{
		setColors( Color.black, Color.red, new Color(0x900000) ); // colore le robot
		setBulletColor(new Color(0xFF4D06));
		setScanColor(new Color(0x3679AD));
		
		s_targetManager = new TargetManager( this );

		s_radar = new Radar( this, s_targetManager );
		
		s_gunManager = new GunManager( this, s_targetManager );
		
		s_movementManager = new MovementManager( this, s_targetManager );
	}

	/**
	 * initialise les listenners methode appelee au debut de round
	 */
	protected void initializeRound()
	{
		registerComponent( s_targetManager );
		// registerListener( m_targetManager );
		
		registerComponent( s_radar );
		registerListener( s_radar );
		if( ConstantManager.getInstance().getBooleanConstant( "debug" ) )
			registerPainter( s_radar );
		
		registerComponent( s_gunManager );
		if( ConstantManager.getInstance().getBooleanConstant( "debug" ) )
			registerPainter( s_gunManager );
		
		if ( ConstantManager.getInstance().getBooleanConstant( "movement.active" ) )
		{
			registerComponent( s_movementManager );
			if( ConstantManager.getInstance().getBooleanConstant( "debug" ) )
				registerPainter( s_movementManager );
		}
	}

	// /////////////////////////////////////////////
	// methodes heritees de IVirtualBot
	// /////////////////////////////////////////////
	
	@Override
	public void update()
	{
		m_positionLog.add( new Point2D.Double( getX(), getY() ) );
	}

	@Override
	public void updateFromScan(AdvancedRobot referent, ScannedRobotEvent event)
	{
	}

	@Override
	public double getBearingRadians()
	{
		throw new NullPointerException();
	}

	@Override
	public double getAbsoluteBearingRadians()
	{
		throw new NullPointerException();
	}

	@Override
	public Point2D.Double getPosition()
	{
		return new Point2D.Double( getX(), getY() );
	}

	@Override
	public Point2D getUpperRightCorner()
	{
		return new Point2D.Double( getX() + ROBOT_SIZE / 2, getY() + ROBOT_SIZE
				/ 2 );
	}

	@Override
	public Point2D getBottomLeftCorner()
	{
		return new Point2D.Double( getX() - ROBOT_SIZE / 2, getY() - ROBOT_SIZE
				/ 2 );
	}

	@Override
	public double getSize()
	{
		return ROBOT_SIZE;
	}

	@Override
	public double getRelativeHeading()
	{
		throw new NullPointerException();
	}

	@Override
	public double getTurnRate()
	{
		throw new NullPointerException();
	}

	@Override
	public double getDistance()
	{
		throw new NullPointerException();
	}

	@Override
	public double getTimeSinceLastDeccel()
	{
		throw new NullPointerException();
	}

	@Override
	public double getTimeSinceLastAccel()
	{
		throw new NullPointerException();
	}

	@Override
	public double getTimeSinceLastVelocityInversion()
	{
		throw new NullPointerException();
	}

	@Override
	public double getTimeSinceLastVelocityChange()
	{
		throw new NullPointerException();
	}

	@Override
	public double getAcceleration()
	{
		throw new NullPointerException();
	}

	@Override
	public int getTimeSinceLastShot()
	{
		throw new NullPointerException();
	}

	@Override
	public int getTimeSinceLastDamageTaken()
	{
		throw new NullPointerException();
	}

	@Override
	public double getLastShotPower()
	{
		throw new NullPointerException();
	}

	@Override
	public double getLateralVelocity()
	{
		throw new NullPointerException();
	}

	@Override
	public double getLastNonNullLateralVelocity()
	{
		throw new NullPointerException();
	}

	@Override
	public double getLateralDirection()
	{
		throw new NullPointerException();
	}

	@Override
	public double getLateralAccell()
	{
		throw new NullPointerException();
	}

	@Override
	public DateTime getDateTime()
	{
		return new DateTime( getRoundNum(), getTime() );
	}
	
	/**
	 * returns the position log of the bot
	 */
	@Override
	public PositionLog getPositionLog()
	{
		return m_positionLog;
	}


	// /////////////////////////////////////////////
	// methodes privees
	// /////////////////////////////////////////////

	// /////////////////////////////////////////////
	// membres prives
	// /////////////////////////////////////////////

	private static ITargetManager	s_targetManager;
	private static Radar			s_radar;
	private static GunManager	s_gunManager;
	private static IMovementManager s_movementManager;

	/**
	 * taille d'un robot
	 */
	private static final double	ROBOT_SIZE	= 40;
	
	
	/**
	 * position log
	 */
	private PositionLog m_positionLog;
}
