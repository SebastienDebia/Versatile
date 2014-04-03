/**
 * 
 */
package ds.gun;

import java.awt.geom.Point2D;

import ds.movement.IMovingObject;

/**
 * @author f4
 * interface pour les balles vituelles
 */
public interface IVirtualBullet extends IMovingObject
{
	/**
	 * Position initiale de la balle virtuelle
	 */
	public Point2D getStartPosition();
	
	/**
	 * Position initiale de la balle virtuelle
	 */
	public Point2D getCurrentPosition();
	
	/**
	 * Angle de tir
	 */
	public double getAngle();
	
	/**
	 * Gun qui a tiré la balle
	 */
	public AbstractGun getFiringGun();
	
	/**
	 * mise a jour de la position
	 */
	public void update();
	
	/**
	 * indique si la balle a touche
	 * @param b true si elle a touche
	 */
	public void setHit( boolean b );
	
	/**
	 * indique si la balle a touche
	 * @return true si elle a touche
	 */
	public boolean hasHit();
}
