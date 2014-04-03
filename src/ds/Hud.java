package ds;

/*
 * PluggableRobot, by Robert J. Walker
 * Home page: http://robowiki.net/cgi-bin/robowiki?PluggableRobot
 * This software is made available under the RoboWiki Limited Public Code License (RWLPCL). The full
 * text of the license may be found at http://robowiki.net/cgi-bin/robowiki?RWLPCL.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import robocode.Robot;

/**
 * Facilitates drawing graphics on the arena by automatically converting between
 * values used in calculations to values used for drawing. The graphics context
 * (Graphics2D) is injected into the Hud just before it is handed to Painters,
 * then removed afterward.
 * 
 * @author Robert J. Walker
 */
public class Hud
{
	/**
	 * Objects which implement this interface can be passed to
	 * PluggableRobot.registerPainter(), and will get their paint() methods
	 * called when it's time to draw the HUD.
	 */
	public interface Painter
	{
		/**
		 * This method will be called by PluggableRobot when it's time for this
		 * object to draw.
		 */
		public void paint(Hud hud, long tick);
	}

	private Graphics2D	_g;
	
	private Color _color;

	public double		_w;
	public double		_h;

	/**
	 * Creates a new Hud object.
	 */
	public Hud(Robot bot)
	{
		_w = bot.getBattleFieldWidth();
		_h = bot.getBattleFieldHeight();
		_color = Color.white;
	}

	/**
	 * Changes color 
	 */
	public void setColor(Color c)
	{
		_color = c;
	}
	
	/**
	 * Draws a point at the given coordinates.
	 */
	public void drawPoint(double x, double y)
	{
		int x0 = i(x);
		int y0 = i(y);
		_g.setColor(_color);
		_g.drawLine(x0, y0, x0, y0);
	}

	/**
	 * Draws a line between the two indicated points.
	 */
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		_g.setColor(_color);
		_g.drawLine(i(x1), i(y1), i(x2), i(y2));
	}

	/**
	 * Draws a circle with the given center point and radius.
	 */
	public void drawCircle(double x, double y, double r)
	{
		int d = i(r * 2);
		_g.setColor(_color);
		_g.drawOval(i(x - r), i(y - r), d, d);
	}

	/**
	 * Draws a circle with the given center point and radius.
	 */
	public void drawFilledCircle(double x, double y, double r)
	{
		int d = i(r * 2);
		_g.setColor(_color);
		_g.fillOval(i(x - r), i(y - r), d, d);

	}
	public void drawArc(double x, double y, double r, double start,
			double extend)
	{
		int d = i(r * 2);
		_g.setColor(_color);
		_g.drawArc(i(x - r), i(y - r), d, d, i(Math2.radToDeg(start)), i(Math2
				.radToDeg(extend)));
	}

	/**
	 * Draws a rectangle with the given position and dimensions.
	 */
	public void drawRect(double x, double y, double w, double h)
	{
		_g.setColor(_color);
		_g.drawRect(i(x), i(y), i(w), i(h));
	}

	/**
	 * Draws a filled rectangle with the given position and dimensions.
	 */
	public void drawFilledRect(double x, double y, double w, double h)
	{
		_g.setColor(_color);
		_g.fillRect(i(x), i(y), i(w), i(h));
	}

	/**
	 * Draws a left-aligned String at the given position.
	 */
	public void drawString(String s, double x, double y)
	{
		_g.setColor(_color);
		_g.drawString(s, i(x), i(y));
	}

	/**
	 * Draws a right-aligned String at the given position.
	 */
	public void drawStringRight(String s, double x, double y)
	{
		_g.setColor(_color);
		_g.drawString(s, i(x) - _g.getFontMetrics().stringWidth(s), i(y));
	}

	/**
	 * Sets the Hud's current drawing paint.
	 */
	public void setPaint(Paint c)
	{
		_g.setColor(_color);
		_g.setPaint(c);
	}

	/**
	 * Returns the width of the battlefield. This is useful in situations where
	 * you don't have access to the robot object.
	 */
	public double getWidth()
	{
		return _w;
	}

	/**
	 * Returns the height of the battlefield. This is useful in situations where
	 * you don't have access to the robot object.
	 */
	public double getHeight()
	{
		return _h;
	}

	/**
	 * Allows PluggableRobot to inject the graphics context into the Hud.
	 */
	public void setContext(Graphics2D g)
	{
		_g = g;
	}

	/**
	 * Rounds the given double value to the nearest int.
	 */
	private int i(double val)
	{
		return (int) Math.round(val);
	}
}
