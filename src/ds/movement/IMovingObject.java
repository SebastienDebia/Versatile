/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

/**
 * @author Seb
 *
 */
public interface IMovingObject
{
	/**
	 * coordonnee X de l'objet
	 * @return
	 */
	public abstract double getX();

	/**
	 * coordonnee Y de l'objet
	 * @return
	 */
	public abstract double getY();
	
	/**
	 * Met a jour la position de l'objet
	 */
	public void update();

	/**
	 * @return
	 */
	public abstract double getHeadingRadians();
}
