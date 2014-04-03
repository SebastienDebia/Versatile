/**
 * 
 */
package ds.gun;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import ds.Math2;
import ds.targeting.IVirtualBot;

/**
 * @author f4 represente une balle virtuelle
 */
public class VirtualBullet implements IVirtualBullet
{
	/**
	 * le gun qui a tire la balle
	 */
	private AbstractGun		m_gun;

	/**
	 * position actuelle de la balle
	 */
	private Point2D.Double	m_currentPosition;

	/**
	 * position de depart de la balle
	 */
	private Point2D.Double	m_startPosition;

	/**
	 * angle de tir
	 */
	private double			m_angle;

	/**
	 * puissance de la balle
	 */
	private double			m_power;

	/**
	 * indique si la balle a touche la cible
	 */
	private boolean			m_hasHit;

	/**
	 * distance initiale entre le robot et la cible
	 */
	private double			m_initialDistance;

	/**
	 * vitesse initiale de la cible
	 */
	private double			m_initialTargetVelocity;

	/**
	 * abs bearing initial de la cible pour le robot
	 */
	private double			m_initialAbsoluteBearingRadians;

	/**
	 * heading initial de la cible
	 */
	private double			m_initialTargetHeadingRadians;

	/**
	 * taux de virage de la cible
	 */
	private double	m_turnrate;

	/**
	 * Position de la cible lors du tir de la balle
	 */
	private Double	m_initialTargetPosition;

	/**
	 * temps depuis la derniere deceleration
	 */
	private double	m_timeSinceLastDeccel;

	/**
	 * temps depuis la derniere acceleration
	 */
	private double	m_timeSinceLastAccel;

	/**
	 * temps depuis la dernière inversion de vitesse
	 */
	private double	m_timeSinceLastVelocityInversion;

	/**
	 * acceleration
	 */
	private double	m_acceleration;
	
	/**
	 * acceleration laterale
	 */
	private double m_lateralAccell;


	/**
	 * Constructeur
	 * 
	 * @param target
	 * @param gun
	 *            le gun qui a tiré la balle
	 * @param position
	 *            position de la balle lors de sa création
	 */
	public VirtualBullet( IVirtualBot target, AbstractGun gun,
			Point2D.Double startPosition, double angle, double power )
	{
		m_gun = gun;
		m_startPosition = startPosition;
		m_currentPosition = startPosition;
		m_angle = angle;
		m_power = power;
		if( target != null )
		{
			m_initialDistance = target.getPosition().distance( startPosition );
			m_initialTargetVelocity = target.getVelocity();
			m_initialAbsoluteBearingRadians = target.getAbsoluteBearingRadians();
			m_initialTargetHeadingRadians = target.getHeadingRadians();
			m_turnrate = target.getTurnRate();
			m_initialTargetPosition = target.getPosition();
			m_timeSinceLastDeccel = target.getTimeSinceLastDeccel();
			m_timeSinceLastAccel = target.getTimeSinceLastAccel();
			m_timeSinceLastVelocityInversion = target.getTimeSinceLastVelocityInversion();
			m_acceleration = target.getAcceleration();
			m_lateralAccell = target.getLateralAccell();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#getPosition()
	 */
	@Override
	public Point2D getStartPosition()
	{
		return m_startPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#getCurrentPosition()
	 */
	@Override
	public Point2D getCurrentPosition()
	{
		return m_currentPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#getCurrentPosition()
	 */
	public AbstractGun getFiringGun()
	{
		return m_gun;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#getAngle()
	 */
	@Override
	public double getAngle()
	{
		return m_angle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#update()
	 */
	@Override
	public void update()
	{
		// distance parcourue en 1 tick
		double distance = (20.0 - 3.0 * m_power);
		m_currentPosition = Math2.getAbsolutePosition( m_currentPosition,
				m_angle, distance );
	}

	/**
	 * distance parcourue par la balle
	 */
	public double travelDistance()
	{
		return m_startPosition.distance( m_currentPosition );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#hasHit()
	 */
	@Override
	public boolean hasHit()
	{
		return m_hasHit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.IVirtualBullet#setHit(boolean)
	 */
	@Override
	public void setHit( boolean b )
	{
		m_hasHit = b;
	}

	/**
	 * @return
	 */
	public double getInitialDistance()
	{
		return m_initialDistance;
	}

	/**
	 * @return
	 */
	public double getInitialTargetVelocity()
	{
		return m_initialTargetVelocity;
	}

	/**
	 * @return
	 */
	public double getInitialAbsoluteBearingRadians()
	{
		return m_initialAbsoluteBearingRadians;
	}

	/**
	 * @return
	 */
	public double getInitialTargetHeadingRadians()
	{
		return m_initialTargetHeadingRadians;
	}


	public double getTurnRate()
	{
		return m_turnrate;
	}

	public Point2D.Double getInitialTargetPosition()
	{
		return m_initialTargetPosition;
	}

	/**
	 * @return
	 */
	public double getTimeSinceLastVelocityChange()
	{
		return Math.min( m_timeSinceLastAccel, m_timeSinceLastDeccel );
	}

	/**
	 * @return
	 */
	public double getTimeSinceLastAccel()
	{
		return m_timeSinceLastAccel;
	}

	/**
	 * @return
	 */
	public double getTimeSinceLastDeccel()
	{
		return m_timeSinceLastDeccel;
	}

	/**
	 * @return
	 */
	public double getTimeSinceLastVelocityInversion()
	{
		return m_timeSinceLastVelocityInversion;
	}

	/**
	 * @return
	 */
	public double getAcceleration()
	{
		return m_acceleration;
	}

	/* (non-Javadoc)
	 * @see ds.IMovingObject#getX()
	 */
	@Override
	public double getX()
	{
		return m_currentPosition.getX();
	}

	/* (non-Javadoc)
	 * @see ds.IMovingObject#getY()
	 */
	@Override
	public double getY()
	{
		return m_currentPosition.getY();
	}

	/* (non-Javadoc)
	 * @see ds.movement.IMovingObject#getHeadingRadians()
	 */
	@Override
	public double getHeadingRadians()
	{
		return m_angle;
	}

	/**
	 * @return
	 */
	public double getLateralAccell()
	{
		return m_lateralAccell;
	}
	
	/**
	 * 
	 */
	public double getBulletPower()
	{
		return m_power;
	}
}
