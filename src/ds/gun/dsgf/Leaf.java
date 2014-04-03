/**
 * 
 */
package ds.gun.dsgf;

import ds.gun.VirtualBullet;
import robocode.util.Utils;

/**
 * @author f4
 * 
 */
public class Leaf
{
	/**
	 * Infos de segmentation
	 */
	private SegmentationInfo	m_si;

	/**
	 * angle de tir
	 */
	private int					m_angleIndex;

	/**
	 * Node mere
	 */
	private Node	m_parentNode;

	/**
	 * Constructeur
	 * 
	 * @param vb
	 *            balle virtuelle
	 */
	public Leaf( Node parent, IndexedVirtualBullet vb )
	{
		m_parentNode = parent;
		
		m_angleIndex = vb.getAngleIndex();
		m_si = new SegmentationInfo( ((VirtualBullet)vb) );
	}

	/**
	 * @return
	 */
	public int getAngleIndex()
	{
		return m_angleIndex;
	}

	/**
	 * retourne les infos de segmentation
	 * @return
	 */
	public SegmentationInfo getSegmentationInfo()
	{
		return m_si;
	}

}
