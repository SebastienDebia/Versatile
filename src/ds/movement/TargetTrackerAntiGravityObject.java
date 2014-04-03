/**
 * 
 */
package ds.movement;

import java.awt.geom.Point2D;

import ds.targeting.ITargetManager;
import ds.targeting.TargetException;

/**
 * @author Seb
 *
 */
public class TargetTrackerAntiGravityObject extends MovingAntiGravityObject
{

	private ITargetManager	m_targetManager;

	public TargetTrackerAntiGravityObject(ITargetManager targetManager, double weight, double gravityType)
	{
		super(weight, gravityType);
		m_targetManager = targetManager;
	}

	public void updatePosition()
	{
		try
		{
			setMovingObject(m_targetManager.getCurrentTarget());
			super.updatePosition();
		} catch (TargetException e)
		{
		}
	}
	
	public Vector2D getForceVector(Point2D.Double referent)
	{
		try
		{
			m_targetManager.getCurrentTarget();
			return super.getForceVector(referent);
		} catch (TargetException e)
		{
			return new Vector2D(0, 0);
		}
	}
}
