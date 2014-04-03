package ds.movement;

import java.awt.geom.Point2D;

import ds.Math2;

public class Vector2D
{
	/** coordonnee x */
	private double	m_x;
	
	/** coordonnee y */
	private double	m_y;

	/**
	 * Constructeur
	 * @param x
	 * @param y
	 */
	public Vector2D(double x, double y)
	{
		m_x = x;
		m_y = y;
	}

	/**
	 * Constructeur
	 * @param point 
	 */
	public Vector2D(Point2D.Double point)
	{
		m_x = point.x;
		m_y = point.y;
	}
	
	/**
	 * Ajoute un autre vecteur
	 * @param v
	 */
	public void add(Vector2D v)
	{
		m_x += v.m_x;
		m_y += v.m_y;
	}

	public void substract(Vector2D v)
	{
		m_x -= v.m_x;
		m_y -= v.m_y;
	}

	public void multiply(double weight)
	{
		m_y*=weight;
		m_x*=weight;
	}

	public void divide(double weight)
	{
		m_y/=weight;
		m_x/=weight;
	}
	
	public double getX()
	{
		return m_x;
	}
	
	public double getY()
	{
		return m_y;
	}
	
	public double getR()
	{
		return Math.sqrt(m_x*m_x+m_y*m_y);
	}
	
	public double getTheta()
	{
		/*if ( m_x > 0 )
		{
			return Math.atan(m_y/m_x);
		}
		if ( m_x < 0 )
		{
			if ( m_y >= 0 )
			{
				return Math.atan(m_y/m_x)+Math2.PI;
			}
			if ( m_y < 0 )
			{
				return Math.atan(m_y/m_x)-Math2.PI;
			}
		}
		if ( m_y >= 0 )
		{
			return Math2.PI_OVER_2;
		}
		return -Math2.PI_OVER_2;*/
		
		return robocode.util.Utils.normalRelativeAngle( Math.atan(m_x/m_y) );
	}
}
