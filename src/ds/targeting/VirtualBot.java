package ds.targeting;

import java.awt.geom.Point2D;

import ds.DateTime;
import ds.Math2;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class VirtualBot implements IVirtualBot
{
	/**
	 * taille d'un robot
	 */
	private static final double	ROBOT_SIZE	= 35; // real = 40

	/**
	 * nom du robot
	 */
	private String				m_name;

	/**
	 * heading vers le robot
	 */
	private double				m_headingRadians;

	/**
	 * bearing vers le robot
	 */
	private double				m_bearingRadians;

	/**
	 * bearing absolut (non relatif au heading du référent) vers le robot
	 */
	private double				m_absoluteBearingRadians;

	/**
	 * position du robot
	 */
	private Point2D.Double		m_position;

	/**
	 * vitesse du robot
	 */
	private double				m_velocity;

	/**
	 * heading relatif du robot
	 */
	private double				m_relativeHeading;
	
	/**
	 * precedent heading de la cible
	 */
	private double	m_previousHeadingRadians;

	/**
	 * distance du referent vers le robot
	 */
	private double	m_distance;

	/**
	 * temps depuis la dernière acceleration
	 */
	private double	m_timeSinceLastAccel;

	/**
	 * temps depuis la dernirèe acceleration
	 */
	private double	m_timeSinceLastDeccel;

	/**
	 * temps depuis la dernière inversion de vitesse
	 */
	private double	m_timeSinceLastVelocityInversion;

	/**
	 * acceleration
	 */
	private double	m_acceleration;

	/**
	 * gunheat
	 */
	private double	m_energy;

	/**
	 * temps depuis le dernier tir
	 */
	private int	m_timeSinceLastShot;

	/**
	 * Puissance du dernier tir
	 */
	private double	m_lastShotPower;

	/**
	 * temps depuis le dernier domage (perte d'energie autre qu'un tir)
	 */
	private int	m_timeSinceLastDamageTaken;

	/**
	 * Target LateralVelocity: The velocity perpendicular to your direction. 
	 */
	private double	m_lateralVelocity;

	/**
	 * Target last non null LateralVelocity: The velocity perpendicular to your direction. 
	 */
	private double	m_lastNonNullLateralVelocity;

	/**
	 * Target last LateralVelocity: The velocity perpendicular to your direction. 
	 */
	private double	m_previousLateralVelocity;
	
	/**
	 * dernière vitesse non nulle
	 */
	private double	m_previousNonNullVelocity;

	/**
	 * date and time of the last scan
	 */
	private DateTime m_dateTime;

	public VirtualBot( String name )
	{
		m_name = name;
		m_previousHeadingRadians = 0;
	}

	// ///////////////////////////////
	// methodes publiques
	// ///////////////////////////////

	/**
	 * Donne le nom du robot
	 */
	public String getName()
	{
		return m_name;
	}

	@Override
	public void updateFromScan( AdvancedRobot referent, ScannedRobotEvent event )
	{
		m_dateTime = new DateTime( referent.getRoundNum(), referent.getTime() );
		
		double previousVelocity = m_velocity;
		if( m_velocity != 0 )
			m_previousNonNullVelocity = m_velocity;
		m_previousLateralVelocity = m_lateralVelocity;
		if( m_lateralVelocity != 0 )
			m_lastNonNullLateralVelocity = m_lateralVelocity;
		
		m_name = event.getName();
		m_previousHeadingRadians = Utils.normalRelativeAngle( m_headingRadians-event.getHeadingRadians() );
		m_headingRadians = event.getHeadingRadians();
		m_bearingRadians = event.getBearingRadians();
		m_absoluteBearingRadians = getBearingRadians()
				+ referent.getHeadingRadians();
		m_position = Math2.getRelativePosition( getAbsoluteBearingRadians(),
				event.getDistance() );
		m_position.setLocation( m_position.getX() + referent.getX(), m_position
				.getY()
				+ referent.getY() );
		m_velocity = event.getVelocity();
		m_acceleration = m_velocity - previousVelocity;
		m_lateralVelocity = m_velocity * Math.sin(m_headingRadians - m_absoluteBearingRadians);
		m_relativeHeading = Utils.normalRelativeAngle( m_headingRadians
				- m_absoluteBearingRadians );
		m_distance = m_position.distance(referent.getX(), referent.getY());
		if ( previousVelocity < m_velocity )
			m_timeSinceLastAccel=0;
		else
			m_timeSinceLastAccel++;
		if ( previousVelocity > m_velocity )
			m_timeSinceLastDeccel=0;
		else
			m_timeSinceLastDeccel++;
		if ( m_previousNonNullVelocity < 0 && m_velocity > 0 || m_previousNonNullVelocity > 0 && m_velocity < 0 )
			m_timeSinceLastVelocityInversion=0;
		else
			m_timeSinceLastVelocityInversion++;
		
		m_timeSinceLastShot++;
		m_timeSinceLastDamageTaken++;
		double energyDrop = m_energy-event.getEnergy();
		if( energyDrop < 3.01 && energyDrop > 0.0042  )
		{
			m_timeSinceLastShot = 0;
			m_lastShotPower = energyDrop;
		}
		else if( energyDrop > 0 )
		{
			m_timeSinceLastDamageTaken = 0;
		}
		m_energy = event.getEnergy();
	}

	@Override
	public double getHeadingRadians()
	{
		return m_headingRadians;
	}

	@Override
	public double getBearingRadians()
	{
		return m_bearingRadians;
	}

	@Override
	public double getAbsoluteBearingRadians()
	{
		return m_absoluteBearingRadians;
	}

	@Override
	public double getX()
	{
		return m_position.getX();
	}

	@Override
	public double getY()
	{
		return m_position.getY();
	}

	public Point2D.Double getPosition()
	{
		return m_position;
	}

	@Override
	public Point2D getUpperRightCorner()
	{
		return new Point2D.Double( getX() + ROBOT_SIZE / 2, getY() + ROBOT_SIZE / 2 );
	}

	@Override
	public Point2D getBottomLeftCorner()
	{
		return new Point2D.Double( getX() - ROBOT_SIZE / 2, getY() - ROBOT_SIZE / 2 );
	}

	@Override
	public double getSize()
	{
		return ROBOT_SIZE;
	}

	@Override
	public double getVelocity()
	{
		return m_velocity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.targeting.IVirtualBot#getRelativeHeading()
	 */
	@Override
	public double getRelativeHeading()
	{
		return m_relativeHeading;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.targeting.IVirtualBot#getTurnRate()
	 */
	@Override
	public double getTurnRate()
	{
		return m_headingRadians-m_previousHeadingRadians;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.targeting.IVirtualBot#getTurnRate()
	 */
	@Override
	public double getDistance()
	{
		return m_distance;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getTimeSinceLastAccel()
	 */
	@Override
	public double getTimeSinceLastAccel()
	{
		return m_timeSinceLastAccel;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getTimeSinceLastDeccel()
	 */
	@Override
	public double getTimeSinceLastDeccel()
	{
		return m_timeSinceLastDeccel;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getTimeSinceLastVelocityInversion()
	 */
	@Override
	public double getTimeSinceLastVelocityInversion()
	{
		return m_timeSinceLastVelocityInversion;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getTimeSinceLastVelocityChange()
	 */
	@Override
	public double getTimeSinceLastVelocityChange()
	{
		return Math.min( m_timeSinceLastAccel, m_timeSinceLastDeccel );
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getAcceleration()
	 */
	@Override
	public double getAcceleration()
	{
		if ( m_acceleration > 0 )
			return 1;
		if ( m_acceleration < 0 )
			return -1;
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		VirtualBot bot = null;
	    try {
	    	// On récupère l'instance à renvoyer par l'appel de la 
	      	// méthode super.clone()
	    	bot = (VirtualBot) super.clone();
	    } catch(CloneNotSupportedException cnse) {
	      	// Ne devrait jamais arriver car nous implémentons 
	      	// l'interface Cloneable
	      	cnse.printStackTrace(System.err);
	    }
	    
	    bot.m_position = (Point2D.Double)m_position.clone();
	    
	    // on renvoie le clone
	    return bot;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getGunHeat()
	 */
	@Override
	public double getEnergy()
	{
		return m_energy;
	}

	/**
	 * @return the timeSinceLastShot
	 */
	public int getTimeSinceLastShot()
	{
		return m_timeSinceLastShot;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getLastShotPower()
	 */
	@Override
	public double getLastShotPower()
	{
		return m_lastShotPower;
	}

	/**
	 * @return the timeSinceLastDamageTaken
	 */
	public int getTimeSinceLastDamageTaken()
	{
		return m_timeSinceLastDamageTaken;
	}

	/* (non-Javadoc)
	 * @see ds.IMovingObject#update()
	 */
	@Override
	public void update()
	{
		// ne fait rien
	}
	
	/**
	 * @return the lateralVelocity
	 */
	public double getLateralVelocity()
	{
		return m_lateralVelocity;
	}

	/**
	 * @return the last non null lateralVelocity
	 */
	public double getLastNonNullLateralVelocity()
	{
		return m_lastNonNullLateralVelocity;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getLateralDirection()
	 */
	@Override
	public double getLateralDirection()
	{
		return getLastNonNullLateralVelocity()>=0?1:-1;
	}

	/* (non-Javadoc)
	 * @see ds.targeting.IVirtualBot#getLateralAccell()
	 */
	@Override
	public double getLateralAccell()
	{
		return m_lateralVelocity - m_previousLateralVelocity;
	}

	@Override
	public DateTime getDateTime()
	{
		return m_dateTime;
	}

}
