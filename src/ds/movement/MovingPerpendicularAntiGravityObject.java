/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

import ds.Math2;
import ds.targeting.IVirtualBot;

/**
 * @author f4
 *
 */
public class MovingPerpendicularAntiGravityObject extends
		MovingAntiGravityObject
{

	private IVirtualBot m_originBot;

	/**
	 * @param originBot 
	 * @param weight
	 * @param gravityType
	 */
	public MovingPerpendicularAntiGravityObject( IVirtualBot originBot, double weight, double gravityType )
	{
		super( weight, gravityType );
		m_originBot = originBot;
	}

	public Vector2D getForceVector(Point2D.Double referent)
	{
		double distance = referent.distance(getPosition());
		
		//double angle = m_movingObject.getHeadingRadians();
		double angle = absbearing( referent, m_originBot.getPosition() );
		// normalisation
		angle = robocode.util.Utils.normalRelativeAngle( angle );
		
		// vecteur de l'objet vers le referent
		Vector2D vectRef = new Vector2D(referent.getX()-getPosition().getX(), referent.getY()-getPosition().getY());

		//double angleObj = vectObj.getTheta();
		double angleRef = vectRef.getTheta();
		angleRef = Math2.getAbsoluteTargetBearing(referent, new Point2D.Double(getPosition().getX(), getPosition().getY()));
		
		double angleDiff = robocode.util.Utils.normalRelativeAngle( angle-angleRef );
		
		if( angleDiff > 0 )
			angle += Math.PI/2;
		else
			angle -= Math.PI/2;
		
		float X = (float)Math.sin(angle);
		float Y = (float)Math.cos(angle);
		
		Vector2D vect = new Vector2D(X, Y);
		
		vect.multiply(100);
		double pow = Math.pow( distance, m_gravityType);
		vect.multiply(getWeight()/pow);
		
		// si la balle est passée on réduit son impact sur la trajectoire
		if( getStartPosition().distance(getPosition()) > getStartPosition().distance( referent ) + 15 )
		{
			vect.divide(10);
		}
		
		return vect;
	}

	public double absbearing( Point2D pt1, Point2D pt2 )
	{
		double xo = pt2.getX() - pt1.getX();
		double yo = pt2.getY() - pt1.getY();
		double h = pt1.distance( pt2 );
		if( xo > 0 && yo > 0 )
		{
			return Math.asin( xo / h );
		}
		if( xo > 0 && yo < 0 )
		{
			return Math.PI - Math.asin( xo / h );
		}
		if( xo < 0 && yo < 0 )
		{
			return Math.PI + Math.asin( -xo / h );
		}
		if( xo < 0 && yo > 0 )
		{
			return 2.0 * Math.PI - Math.asin( -xo / h );
		}
		return 0;
	}

}
