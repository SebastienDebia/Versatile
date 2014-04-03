/**
 * 
 */
package ds.gun.dsgf;

import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import robocode.RobocodeFileOutputStream;
import ds.Hud;
import ds.Versatile;
import ds.constant.ConstantManager;


/**
 * @author f4
 *
 */
public class Tree
{
	/** 
	 * racine
	 */
	private Node m_root;
	
	/**
	 * nombre de samples
	 */
	private final int	m_nbSamples;
	
	/**
	 * Constructeur
	 */
	public Tree( int maxNodeLeafCount, int minNodeLeafCount, boolean bIsDataSaver )
	{
		m_root = new Node(null, maxNodeLeafCount, minNodeLeafCount, bIsDataSaver);
		
		m_nbSamples = (int)ConstantManager.getInstance().getIntegerConstant(
		"gun.dsgf.nbSamples" ).longValue();
	}
	
	/**
	 * Ajout d'une statistique
	 * @param vb balle virtuelle associée
	 */
	public void add(IndexedVirtualBullet vb)
	{
		m_root.add(vb);
	}

	/**
	 * @param si info sur la cible
	 * @return
	 */
	public double[] getSolutionSamples( SegmentationInfo si )
	{
		Node noeud = m_root.getSolutionSamples( si );
		if( noeud != null )
		{
			return noeud.getSamplesHitChances();
		}
		return new double[m_nbSamples];
	}
	
	/**
	 * retourne l'angle de tir
	 */
	public FireIndex getSolution(SegmentationInfo si)
	{
		return m_root.getSolution(si);
	}

	public void paint(Hud hud, long tick)
	{
		m_root.paint(hud, 0);
	}

	/**
	 * pour le debug
	 * affiche le chemin choisi dans l'arbre pour la solution de tir
	 * @param si
	 * @return
	 */
	public Vector<String> getSolutionString( SegmentationInfo si )
	{
		return m_root.getSolutionString(si);
	}

	/**
	 * @return
	 */
	public Node getRoot()
	{
		return m_root;
	}
}
