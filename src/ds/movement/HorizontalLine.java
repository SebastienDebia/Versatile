/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

/**
 * @author f4
 *
 */
public class HorizontalLine extends AntiGravityObject
{

	/**
	 * Constructeur
	 * @param y
	 * @param weight
	 * @param gravityType 
	 */
	public HorizontalLine( double y, double weight, double gravityType )
	{
		super( 400, y, weight, gravityType );
	}

	public Vector2D getForceVector(Point2D.Double referent)
	{
		double distance = Math.abs(referent.getY()-getPosition().getY());
		Vector2D vect = new Vector2D(0, referent.getY()-getPosition().getY());
		vect.multiply(100);
		double pow = Math.pow( distance, m_gravityType);
		vect.multiply(getWeight()/pow);
		
		return vect;
	}
}
