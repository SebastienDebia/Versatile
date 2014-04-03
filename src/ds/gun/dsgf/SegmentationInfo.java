/**
 * 
 */
package ds.gun.dsgf;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Vector;

import ds.gun.VirtualBullet;
import ds.targeting.IVirtualBot;
import robocode.AdvancedRobot;
import robocode.util.Utils;

/**
 * @author f4
 * 
 */
public class SegmentationInfo
{
	private Vector<Double>	m_values	= new Vector<Double>();
	private static Vector<String>	m_names	= new Vector<String>();

	public SegmentationInfo(VirtualBullet vb)
	{
		// distance robot/cible lors du tir
		double distance = vb.getInitialDistance();
		// vitesse cible
		double enemySpeed = vb.getInitialTargetVelocity();
		// angle relatif ( indique si la cible etait plus ou moins
		// perpendiculaire par exemple)
		double relativeHeading = Utils.normalRelativeAngle( vb
				.getInitialTargetHeadingRadians()
				- vb.getInitialAbsoluteBearingRadians() );
		// taux de virage
		double turnRate = vb.getTurnRate(); 
		// Target LateralVelocity: The velocity perpendicular to your direction. 
		double lateralVelocity = enemySpeed * Math.sin(vb.getInitialTargetHeadingRadians() - vb.getInitialAbsoluteBearingRadians());
		lateralVelocity = Math.abs(lateralVelocity);
		double lateralAccell = vb.getLateralAccell();
		// Target AdvancingVelocity: The velocity parallel to your direcion. 
		double advancingVelocity = enemySpeed * -1 * Math.cos(vb.getInitialTargetHeadingRadians() - vb.getInitialAbsoluteBearingRadians());
		// distance from wall
		//TODO: remplacer 800 par la taille de la zonne
		double distanceFromLeftWall = vb.getInitialTargetPosition().getX();
		double distanceFromRightWall = 800-vb.getInitialTargetPosition().getX();
		double distanceFromTopWall = 600-vb.getInitialTargetPosition().getY();
		double distanceFromBottomWall = vb.getInitialTargetPosition().getY();
		double distanceFromWall = Math.min(Math.min(Math.min(distanceFromLeftWall, distanceFromRightWall), distanceFromTopWall), distanceFromBottomWall);
		
		double enemyVerticalSpeed = enemySpeed * Math.cos(vb.getInitialTargetHeadingRadians());
		double enemyHorizontalSpeed = enemySpeed * Math.sin(vb.getInitialTargetHeadingRadians());
		double frontDistanceFromRightWall = (800-vb.getInitialTargetPosition().getX())/enemyHorizontalSpeed;
		if(frontDistanceFromRightWall<0)frontDistanceFromRightWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromLeftWall = -(vb.getInitialTargetPosition().getX())/enemyHorizontalSpeed;
		if(frontDistanceFromLeftWall<0)frontDistanceFromLeftWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromTopWall = (600-vb.getInitialTargetPosition().getY())/enemyVerticalSpeed;
		if(frontDistanceFromTopWall<0)frontDistanceFromTopWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromBottomWall= -(vb.getInitialTargetPosition().getY())/enemyVerticalSpeed;
		if(frontDistanceFromBottomWall<0)frontDistanceFromBottomWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromWall = Math.min(Math.min(Math.min(frontDistanceFromLeftWall, frontDistanceFromRightWall), frontDistanceFromTopWall), frontDistanceFromBottomWall);
		
		//corner distance
		Point2D targetPos = vb.getInitialTargetPosition();
		Point2D brCorner = new Point2D.Double(800, 0);
		Point2D blCorner = new Point2D.Double(0, 0);
		Point2D trCorner = new Point2D.Double(800, 600);
		Point2D tlCorner = new Point2D.Double(0, 600);
		double distanceFromCorners = Math.min(Math.min(Math.min(targetPos.distance( brCorner ), targetPos.distance( blCorner )), targetPos.distance( trCorner )), targetPos.distance( tlCorner ));
		
		// temps depuis le dernier changement de vitesse
		double timeSinceLastVelocityChange = vb.getTimeSinceLastVelocityChange();
		
		// temps depuis la derniere acceleration
		double timeSinceLastAccel = vb.getTimeSinceLastAccel();
		
		// temps depuis la dernière deceleration
		double timeSinceLastDeccel = vb.getTimeSinceLastDeccel();
		
		// temps depuis la dernière inversion de vitesse 
		double timeSinceLastVelocityInversion = vb.getTimeSinceLastVelocityInversion();
		
		// vitesse d'approche d'un mur
		//double nearestWallSpeed = vb.getNearestWallSpeed();
		
		// acceleration
		double acceleration = vb.getAcceleration();
		
		Build(distance, enemySpeed, relativeHeading, turnRate, lateralVelocity, lateralAccell, advancingVelocity, distanceFromWall, distanceFromCorners, timeSinceLastVelocityChange, timeSinceLastAccel, timeSinceLastDeccel, timeSinceLastVelocityInversion, acceleration, frontDistanceFromWall );
	}
	
	public SegmentationInfo(AdvancedRobot owner, IVirtualBot target)
	{
		// distance robot/cible lors du tir
		double distance = target.getDistance();
		// vitesse cible
		double enemySpeed = target.getVelocity();
		// angle relatif ( indique si la cible etait plus ou moins
		// perpendiculaire par exemple)
		double relativeHeading = Utils.normalRelativeAngle( target.getHeadingRadians()- target.getAbsoluteBearingRadians() );
		// taux de virage
		double turnRate = target.getTurnRate(); 
		// Target LateralVelocity: The velocity perpendicular to your direction. 
		double lateralVelocity = enemySpeed * Math.sin(target.getHeadingRadians() - target.getAbsoluteBearingRadians());
		lateralVelocity = Math.abs(lateralVelocity);
		double lateralAccell = target.getLateralAccell();
		// Target AdvancingVelocity: The velocity parallel to your direcion. 
		double advancingVelocity = enemySpeed * -1 * Math.cos(target.getHeadingRadians() - target.getAbsoluteBearingRadians());
		// distance from wall
		//TODO: remplacer 800 par la taille de la zonne
		double distanceFromLeftWall = target.getPosition().getX();
		double distanceFromRightWall = 800-target.getPosition().getX();
		double distanceFromTopWall = 600-target.getPosition().getY();
		double distanceFromBottomWall = target.getPosition().getY();
		double distanceFromWall = Math.min(Math.min(Math.min(distanceFromLeftWall, distanceFromRightWall), distanceFromTopWall), distanceFromBottomWall);

		double enemyVerticalSpeed = enemySpeed * Math.cos(target.getHeadingRadians());
		double enemyHorizontalSpeed = enemySpeed * Math.sin(target.getHeadingRadians());
		double frontDistanceFromRightWall = (800-target.getPosition().getX())/enemyHorizontalSpeed;
		if(frontDistanceFromRightWall<0)frontDistanceFromRightWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromLeftWall = -(target.getPosition().getX())/enemyHorizontalSpeed;
		if(frontDistanceFromLeftWall<0)frontDistanceFromLeftWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromTopWall = (600-target.getPosition().getY())/enemyVerticalSpeed;
		if(frontDistanceFromTopWall<0)frontDistanceFromTopWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromBottomWall= -(target.getPosition().getY())/enemyVerticalSpeed;
		if(frontDistanceFromBottomWall<0)frontDistanceFromBottomWall=Double.POSITIVE_INFINITY;
		double frontDistanceFromWall = Math.min(Math.min(Math.min(frontDistanceFromLeftWall, frontDistanceFromRightWall), frontDistanceFromTopWall), frontDistanceFromBottomWall);
		
		//corner distance
		Point2D targetPos = target.getPosition();
		Point2D brCorner = new Point2D.Double(800, 0);
		Point2D blCorner = new Point2D.Double(0, 0);
		Point2D trCorner = new Point2D.Double(800, 600);
		Point2D tlCorner = new Point2D.Double(0, 600);
		double distanceFromCorners = Math.min(Math.min(Math.min(targetPos.distance( brCorner ), targetPos.distance( blCorner )), targetPos.distance( trCorner )), targetPos.distance( tlCorner ));

		// temps depuis le dernier changement de vitesse
		double timeSinceLastVelocityChange = target.getTimeSinceLastVelocityChange();
		
		// temps depuis la derniere acceleration
		double timeSinceLastAccel = target.getTimeSinceLastAccel();
		
		// temps depuis la dernière deceleration
		double timeSinceLastDeccel = target.getTimeSinceLastDeccel();
		
		// temps depuis la dernière inversion de vitesse 
		double timeSinceLastVelocityInversion = target.getTimeSinceLastVelocityInversion();
		
		// vitesse d'approche d'un mur
		//double nearestWallSpeed = vb.getNearestWallSpeed();
		
		// acceleration
		double acceleration = target.getAcceleration();
		
		Build(distance, enemySpeed, relativeHeading, turnRate, lateralVelocity, lateralAccell, advancingVelocity, distanceFromWall, distanceFromCorners, timeSinceLastVelocityChange, timeSinceLastAccel, timeSinceLastDeccel, timeSinceLastVelocityInversion, acceleration, frontDistanceFromWall);
	}
	
	/**
	 * @param distance
	 * @param enemySpeed
	 * @param relativeHeading
	 * @param turnRate 
	 * @param advancingVelocity 
	 * @param lateralVelocity 
	 * @param lateralAccell 
	 * @param distanceFromWall 
	 * @param distanceFromCorners
	 * @param timeSinceLastVelocityChange 
	 * @param timeSinceLastDeccel 
	 * @param timeSinceLastAccel 
	 * @param timeSinceLastVelocityInversion 
	 * @param acceleration 
	 */
	public void Build( double distance, double enemySpeed,
			double relativeHeading, double turnRate, double lateralVelocity, double lateralAccell, double advancingVelocity, double distanceFromWall, double distanceFromCorners, double timeSinceLastVelocityChange, double timeSinceLastAccel, double timeSinceLastDeccel, double timeSinceLastVelocityInversion, double acceleration, double frontDistanceFromWall)
	{
		m_names.clear();
		
		m_names.add("distance");
		m_values.add(distance);
		
		m_names.add("enemySpeed");
		m_values.add(enemySpeed);
		
		m_names.add("relativeHeading");
		m_values.add(relativeHeading);
		
		m_names.add("turnRate");
		m_values.add(turnRate);
		
		m_names.add("lateralVelocity");
		m_values.add(lateralVelocity);
		
		m_names.add("lateralAccell");
		m_values.add(lateralAccell);
		
		m_names.add("advancingVelocity");
		m_values.add(advancingVelocity);
		
		m_names.add("distanceFromWall");
		m_values.add(distanceFromWall);
		
		m_names.add("distanceFromCorners");
		m_values.add(distanceFromCorners);
		
		m_names.add("timeSinceLastVelocityChange");
		m_values.add(timeSinceLastVelocityChange);
		
		m_names.add("timeSinceLastDeccel");
		m_values.add(timeSinceLastDeccel);
		
		m_names.add("timeSinceLastAccel");
		m_values.add(timeSinceLastAccel);
		
		m_names.add("timeSinceLastVelocityInversion");
		m_values.add(timeSinceLastVelocityInversion);
		
		m_names.add("acceleration");
		m_values.add(acceleration);
		
		m_names.add("frontDistanceFromWall");
		m_values.add(frontDistanceFromWall);
	}

	/**
	 * @return the distance
	 */
	/*public final double getDistance()
	{
		return m_values[0];
	}*/

	/**
	 * @return the enemySpeed
	 */
	/*public final double getEnemySpeed()
	{
		return m_values[1];
	}*/

	/**
	 * @return the relativeHeading
	 */
	/*public final double getRelativeHeading()
	{
		return m_values[2];
	}*/

	/**
	 * @param segmentingInfoIndex
	 * @return
	 */
	public final double getInfo( int segmentingInfoIndex )
	{
		return m_values.elementAt( segmentingInfoIndex );
	}

	/**
	 * donne le nombre de dimensions
	 * @return
	 */
	public int getDImCount()
	{
		return m_values.size();
	}

	/**
	 * @param segmentingInfoIndex
	 * @return
	 */
	public static String getDimensionName( int segmentingInfoIndex )
	{
		/*switch(segmentingInfoIndex)
		{
		case 0:
			return "distance";
		case 1:
			return "enemySpeed";
		case 2:
			return "relativeHeading";
		case 3:
			return "turnRate";
		case 4:
			return "lateralVelocity";
		case 5:
			return "advancingVelocity";
		case 6:
			return "distanceFromWall";
		case 7:
			return "distanceFromCorners";
		case 8:
			return "timeSinceLastVelocityChange";
		case 9:
			return "timeSinceLastDeccel";
		case 10:
			return "timeSinceLastAccel";
		case 11:
			return "timeSinceLastVelocityInversion";
		case 12:
			return "acceleration";
		case 13:
			return "frontDistanceFromWall";
		default:
			return "unknown dimension";
		}*/
		return m_names.elementAt( segmentingInfoIndex );
	}

}
