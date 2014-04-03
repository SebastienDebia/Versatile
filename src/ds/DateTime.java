package ds;

/**
 * represents a Round/turn
 * to avoid the bug which happens when comparing only turns!
 * @author sdebia
 *
 */
public class DateTime
{
	private long m_round;
	private long m_turn;
	
	public DateTime( long round, long turn )
	{
		m_round = round;
		m_turn = turn;
	}
	
	public boolean before( DateTime other )
	{
		if( m_round < other.m_round  )
			return true;
		else
		{
			if( m_turn < other.m_turn  )
				return true;
			else
				return false;
		}
	}
	
	public boolean after( DateTime other )
	{
		if( m_round > other.m_round  )
			return true;
		else
		{
			if( m_turn > other.m_turn )
				return true;
			else
				return false;
		}
	}
	
	public boolean equals( DateTime other )
	{
		return other.m_round == m_round && other.m_turn == m_turn;
	}

	public long getRound()
	{
		return m_round;
	}

	public long getTurn()
	{
		return m_turn;
	}
}
