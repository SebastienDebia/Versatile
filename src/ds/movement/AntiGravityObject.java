package ds.movement;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class AntiGravityObject
{
	/**
	 * position de l'objet
	 */
	private Point2D	m_position;
	
	/**
	 * poids
	 */
	private double	m_weight;

	/**
	 * type de gravité
	 */
	protected double	m_gravityType;
	
	public AntiGravityObject(double x, double y, double weight, double gravityType)
	{
		m_position = new Point2D.Double(x, y);
		m_weight = weight*1000;
		m_gravityType = gravityType;
	}

	public Vector2D getForceVector(Point2D.Double referent)
	{
		//double refDistance = 500;
		double distance = referent.distance(m_position);
		Vector2D vect = new Vector2D(referent.getX()-m_position.getX(), referent.getY()-m_position.getY());
		//vect.multiply(100);
		if( Math.abs( vect.getX() ) > Math.abs( vect.getY() ) )
			vect.divide( Math.abs( vect.getX() ) );
		else
			vect.divide( Math.abs( vect.getY() ) );
		double pow = Math.pow( distance, m_gravityType);
		vect.multiply(m_weight/pow);
		
		/*double pow = refDistance - distance;
		if( pow < 0 )
			pow = 0;
		pow /= refDistance;
		pow *= 10;
		vect.multiply(m_weight * pow);*/
		
		return vect;
	}
	
	public Point2D getPosition()
	{
		return m_position;
	}

	/**
	 * donne le poid de l'objet antig
	 * @return
	 */
	protected double getWeight()
	{
		return m_weight;
	}

}
