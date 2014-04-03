/**
 * 
 */
package ds.gun.dsgf;

import ds.constant.ConstantManager;

/**
 * @author f4
 * 
 */
public class FireIndex
{
	/**
	 * index de l'angle de tir proposé
	 * valeur de 0 à 1
	 */
	private double	m_angleIndex;

	/**
	 * estimation de la fiabilité de la solution de tir porte uniquement sur la
	 * fiabilité des données utilisées pour produire la solution et pas sur des
	 * facteurs externes comme la distance la vitesse laterale, etc TODO:
	 * ajouter un facteur de fiabilité pour les facteurs externes (exemple :
	 * faible pour le HoTGun quand la cible a une vitesse laterale élevée)
	 */
	private double	m_fiabilite;

	private int	m_nbSamples;

	/**
	 * Constructeur
	 * 
	 * @param angle
	 *            angle de tir
	 * @param fiabilite
	 *            fiabilitée de la solution
	 */
	public FireIndex( double AngleIndex, double fiabilite )
	{
		m_angleIndex = AngleIndex;
		m_fiabilite = fiabilite;
		m_nbSamples = (int)ConstantManager.getInstance().getIntegerConstant( "gun.dsgf.nbSamples" ).longValue();
	}

	/**
	 * @return the angle index
	 */
	public final double getAngleIndex()
	{
		return m_angleIndex;
	}

	/**
	 * sets the angle index
	 */
	public final void setAngleIndex( double AngleIndex )
	{
		m_angleIndex = AngleIndex;
	}

	/**
	 * @return the fiabilite
	 */
	public final double getFiabilite()
	{
		return m_fiabilite;
	}

	/**
	 * donne l'index relatif de l'angle 
	 * (décale de la moitié a vers le négatif)
	 * valeur de -1 à 1
	 */
	public double getRelativeAngleIndex()
	{
		double angleIndex = m_angleIndex;
		return 2*(angleIndex-(m_nbSamples/2));
	}
}
