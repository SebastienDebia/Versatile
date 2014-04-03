/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

/**
 * @author f4
 *
 */
public class VerticalLine extends AntiGravityObject
{
	/**
	 * Constructeur
	 * @param x
	 * @param weight
	 * @param wallsGravityType 
	 */
	public VerticalLine( double x, double weight, double wallsGravityType )
	{
		super( x, 300, weight, wallsGravityType );
	}

	public Vector2D getForceVector(Point2D.Double referent)
	{
		
		double distance = Math.abs(referent.getX()-getPosition().getX());
		Vector2D vect = new Vector2D(referent.getX()-getPosition().getX(), 0);
		vect.multiply(100);
		double pow = Math.pow( distance, m_gravityType);
		vect.multiply(getWeight()/pow);
		
		return vect;
	}
}
