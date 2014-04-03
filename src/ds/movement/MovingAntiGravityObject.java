/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

import ds.PluggableRobot;

/**
 * @author Seb
 *
 */
public class MovingAntiGravityObject extends AntiGravityObject
{
	/**
	 * objet lie
	 */
	protected IMovingObject	m_movingObject;
	
	/**
	 * tracking start position
	 */
	private Point2D m_startPosition;

	/**
	 * Constructeur
	 * @param object objet lie
	 * @param weight
	 * @param gravityType 
	 */
	public MovingAntiGravityObject(double weight, double gravityType)
	{
		super(0, 0, weight, gravityType);
		m_startPosition = new Point2D.Double( 0, 0 );
	}
	
	/**
	 * change l'objet en movement
	 * @param object objet en movement
	 */
	public void setMovingObject(IMovingObject object)
	{
		m_movingObject = object;
		m_startPosition.setLocation( object.getX(), object.getY() );
	}
	
	public IMovingObject getMovingObject()
	{
		return m_movingObject;
	}
	
	/**
	 * met a jour la position
	 */
	public void updatePosition()
	{
		if ( m_movingObject!=null )
		{
			m_movingObject.update();
			getPosition().setLocation(m_movingObject.getX(), m_movingObject.getY());
		}
	}

	/**
	 * @return
	 */
	public boolean isOutOfBattlefield()
	{
		return
		m_movingObject != null && 
		(
			m_movingObject.getX()>PluggableRobot.getMe().getBattleFieldWidth()
			|| 
			m_movingObject.getY()>PluggableRobot.getMe().getBattleFieldHeight()
			|| 
			m_movingObject.getX()<0
			|| 
			m_movingObject.getY()<0
		);
	}

	
	public Point2D getStartPosition()
	{
		return m_startPosition;
	}
}
