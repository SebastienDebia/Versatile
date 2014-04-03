package ds.targeting;

import ds.Versatile;
import robocode.ScannedRobotEvent;

public class TargetManager implements ITargetManager
{
	/**
	 * numero du dernier round
	 */
	private int	m_lastRoundNum;

	/**
	 * Constructeur
	 * @param owner
	 */
	public TargetManager( Versatile owner )
	{
		m_owner = owner;
		m_target = null;
	}

	// ///////////////////////////////
	// methodes publiques
	// ///////////////////////////////

	@Override
	public IVirtualBot getCurrentTarget() throws TargetException
	{
		if( m_target == null )
			throw new TargetException();
		return m_target;
	}

	@Override
	public boolean HasTarget()
	{
		return(m_target != null);
	}

	@Override
	public void OnScannedRobot( ScannedRobotEvent event )
	{
		m_lastScanTime = event.getTime();
		m_lastRoundNum = m_owner.getRoundNum();

		if( m_target != null && m_target.getName() == event.getName() )
		{
			m_target.updateFromScan( m_owner, event );
		}
		else
		{
			m_target = null;
			m_target = new VirtualBot( event.getName() );
			m_target.updateFromScan( m_owner, event );
		}
	}

	@Override
	public void Act()
	{
		if( m_owner.getTime() - m_lastScanTime > 4 || m_owner.getRoundNum() > m_lastRoundNum )
		{
			// cible perdue
			m_target = null;
		}
	}

	// ///////////////////////////////
	// membres prives
	// ///////////////////////////////

	/**
	 * Proprietaire du module
	 */
	private Versatile	m_owner;

	/**
	 * Cible actuelle
	 */
	private IVirtualBot	m_target;

	/**
	 * Derniere acquisition de la cible
	 */
	private long		m_lastScanTime;
}
