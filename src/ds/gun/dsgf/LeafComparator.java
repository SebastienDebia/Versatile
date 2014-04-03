package ds.gun.dsgf;

import java.util.Comparator;

public class LeafComparator implements Comparator<Leaf>
{
	private int m_segmentingInfoIndex;
	
	/**
	 * @param segmentingInfoIndex 
	 */
	public LeafComparator(int segmentingInfoIndex)
	{
		m_segmentingInfoIndex = segmentingInfoIndex;
	}
	
	@Override
	public int compare(Leaf firstLeaf, Leaf secondLeaf)
	{
		int segmentingInfoIndex = m_segmentingInfoIndex;
		double firstValue = firstLeaf.getSegmentationInfo().getInfo(segmentingInfoIndex);
		double secondValue = secondLeaf.getSegmentationInfo().getInfo(segmentingInfoIndex);
		if ( firstValue > secondValue )
		{
			return 1;
		}
		if ( firstValue < secondValue )
		{
			return -1;
		}
		return 0;
	}
}