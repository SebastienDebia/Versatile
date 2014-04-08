/**
 * 
 */
package ds;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author sdebia
 *
 */
public class PositionLog implements Cloneable
{
	private ArrayList<Point2D.Double>	m_positions;
	
	private int m_size;
	
	public PositionLog( int size )
	{
		m_positions = new ArrayList<Point2D.Double>();
		m_size = size;
	}
	
	public void add( Point2D.Double position )
	{
		m_positions.add( position );
		
		if( m_positions.size() > m_size )
		{
			m_positions.remove(0);
		}
	}
	
	public Point2D.Double get( int n )
	{
		return m_positions.get( n );
	}
	
	public Point2D.Double getNOrLast( int n )
	{
		// will return IndexOutOfBoundsException if empty, that's fine
		int realN = n>=m_positions.size()?m_positions.size()-1:n;
		return m_positions.get( realN );
	}
	
	public double distance( int from, int to )
	{
		if( m_positions.isEmpty() )
			return 0;
		
		return getNOrLast(from).distance( getNOrLast(to) );
	}
	
	public double distanceSince( int n )
	{
		if( m_positions.isEmpty() )
			return 0;
		
		return get(0).distance( getNOrLast(n) );
	}

	@SuppressWarnings("unchecked")
	public PositionLog clone()
	{
		PositionLog o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = (PositionLog)super.clone();
			o.m_positions = ((ArrayList<Point2D.Double>)m_positions.clone());
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}
}
