package ds;

/*
 * PluggableRobot, by Robert J. Walker
 * Home page: http://robowiki.net/cgi-bin/robowiki?PluggableRobot
 * This software is made available under the RoboWiki Limited Public Code License (RWLPCL). The full
 * text of the license may be found at http://robowiki.net/cgi-bin/robowiki?RWLPCL.
 */

import java.awt.geom.Point2D;
import java.util.Random;

import robocode.util.Utils;

/**
 * Math utility class.
 * 
 * @author Robert J. Walker
 */
public final class Math2
{
	public static final double	PI			= Math.PI;
	public static final double	PI_OVER_2	= Math.PI / 2;
	public static final double	PI_OVER_32	= Math.PI / 32;

	private static final Random	random		= new Random();

	private Math2()
	{
	}

	/**
	 * Returns a random int between the min and max arguments, inclusive.
	 */
	public static int randomInteger(int min, int max)
	{
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * Returns a random double value between 0.0 (inclusive) and 1.0
	 * (exclusive).
	 */
	public static double randomDouble()
	{
		return random.nextDouble();
	}

	/**
	 * If value is less than min, returns min. If value is greater than max,
	 * returns max. Otherwise, returns value.
	 */
	public static double limit(double min, double value, double max)
	{
		return Math.max(min, Math.min(value, max));
	}

	/**
	 * Adds the X and Y components of the given Point2D.Double objects and
	 * returns a new Point2D.Double object with the result.
	 */
	public static Point2D.Double add(Point2D.Double point1,
			Point2D.Double point2)
	{
		return new Point2D.Double(point1.x + point2.x, point1.y + point2.y);
	}

	/**
	 * Subtracts the X and Y components of the second given Point2D.Double
	 * object from those of the first and returns a new Point2D.Double object
	 * with the result.
	 */
	public static Point2D.Double subtract(Point2D.Double point1,
			Point2D.Double point2)
	{
		return new Point2D.Double(point1.x - point2.x, point1.y - point2.y);
	}

	/**
	 * Returns the absolute bearing in radians from the given origin point to
	 * the given target point.
	 */
	public static double getAbsoluteTargetBearing(Point2D.Double origin,
			Point2D.Double target)
	{
		return Utils.normalAbsoluteAngle(Math.atan2(target.x - origin.x,
				target.y - origin.y));
	}

	/**
	 * Returns a Point2D.Double object indicating the relative position of an
	 * object at the given angle and distance from the origin.
	 */
	public static Point2D.Double getRelativePosition(double angle,
			double distance)
	{
		double dx = distance * Math.sin(angle);
		double dy = distance * Math.cos(angle);
		return new Point2D.Double(dx, dy);
	}

	/**
	 * Returns a Point2D.Double object indicating the position of an object at
	 * the given angle and distance from the given origin point.
	 */
	public static Point2D.Double getAbsolutePosition(Point2D.Double origin,
			double angle, double distance)
	{
		double x = origin.x + distance * Math.sin(angle);
		double y = origin.y + distance * Math.cos(angle);
		return new Point2D.Double(x, y);
	}

	/**
	 * Converts degrees to radians.
	 */
	public static double degToRad(double degrees)
	{
		return degrees * Math.PI / 180;
	}

	/**
	 * Converts radians to degrees.
	 */
	public static double radToDeg(double radians)
	{
		return radians * 180 / Math.PI;
	}
}
