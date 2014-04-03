package ds.targeting;

import java.awt.geom.Point2D;

import ds.DateTime;
import ds.movement.IMovingObject;
import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 * Repr�sente un bot virtuel
 * @author f4
 */
public interface IVirtualBot extends IMovingObject, Cloneable
{
	/**
	 * Donne le nom du robot
	 * @return nom du robot
	 */
	public abstract String getName();
	
	/**
	 * update depuis un evenement de scan d'une cible
	 * @param event
	 */
	public abstract void updateFromScan(AdvancedRobot referent, ScannedRobotEvent event);

	/**
	 * Donne le heading en radians
	 * @return heading vers le robot en radians
	 */
	public abstract double getHeadingRadians();
	
	/**
	 * Donne le bearing en radians
	 * @return bearing vers le robot en radians
	 */
	public abstract double getBearingRadians();

	/**
	 * Donne l'absolute bearing en radians
	 * @return abs bearing vers le robot en radians
	 */
	public abstract double getAbsoluteBearingRadians();
	
	/**
	 * coordonnée X du robot
	 */
	public abstract double getX();
	
	/**
	 * coordonnée Y du robot
	 */
	public abstract double getY();
	
	/**
	 * Coordonnées du robot
	 */
	public abstract Point2D.Double getPosition();

	/**
	 * coin haut gauche de la boite contenant le robot
	 */
	public abstract Point2D getBottomLeftCorner();

	/**
	 * coin bas droite de la boite contenant le robot
	 */
	public abstract Point2D getUpperRightCorner();

	/**
	 * taille du robot
	 */
	public abstract double getSize();

	/**
	 * Vitesse du robot
	 */
	public abstract double getVelocity();

	/**
	 * Heading relatif de la cible par rapport au robot
	 * ex : 0 si les robots vont dans la même direction
	 */
	public abstract double getRelativeHeading();

	/**
	 * taux de virage de la cible
	 * @return taux de virage
	 */
	public abstract double getTurnRate();

	/**
	 * distance du referent vers la cible
	 * @return
	 */
	public abstract double getDistance();

	/**
	 * @return
	 */
	public abstract double getTimeSinceLastDeccel();

	/**
	 * @return
	 */
	public abstract double getTimeSinceLastAccel();

	/**
	 * @return
	 */
	public abstract double getTimeSinceLastVelocityInversion();

	/**
	 * @return
	 */
	public abstract double getTimeSinceLastVelocityChange();

	/**
	 * @return
	 */
	public abstract double getAcceleration();

	/**
	 * @return
	 */
	//public abstract Object clone() throws CloneNotSupportedException;

	/**
	 * @return
	 */
	public abstract double getEnergy();

	/**
	 * @return the timeSinceLastShot
	 */
	public abstract int getTimeSinceLastShot();

	/**
	 * @return the timeSinceLastDamageTaken
	 */
	public abstract int getTimeSinceLastDamageTaken();

	/**
	 * @return
	 */
	public abstract double getLastShotPower();
	
	/**
	 * @return the lateralVelocity
	 */
	public abstract double getLateralVelocity();

	/**
	 * @return the last non null lateralVelocity
	 */
	public abstract double getLastNonNullLateralVelocity();

	/**
	 * @return the lateral direction (direction perpendiculaire au bot)
	 * +1 pour le sens des aiguilles d'une montre 
	 * -1 pour l'inverse
	 * ou le contraire ^^
	 */
	public abstract double getLateralDirection();

	/**
	 * @return latteral acceleration
	 */
	public abstract double getLateralAccell();

	/**
	 * @return the current Date/Time
	 */
	public abstract DateTime getDateTime();
}
