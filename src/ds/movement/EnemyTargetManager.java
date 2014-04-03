package ds.movement;

import robocode.ScannedRobotEvent;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

public class EnemyTargetManager implements ITargetManager
{
	EnemyTargetManager( IVirtualBot owner )
	{
		m_owner = owner;
	}
	
	@Override
	public void OnScannedRobot(ScannedRobotEvent event)
	{
	}

	@Override
	public void Act()
	{
	}

	@Override
	public IVirtualBot getCurrentTarget() throws TargetException
	{
		return m_owner;
	}

	@Override
	public boolean HasTarget()
	{
		return true;
	}
	
	private IVirtualBot m_owner;

}
