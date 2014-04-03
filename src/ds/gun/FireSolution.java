/**
 * 
 */
package ds.gun;

/**
 * @author f4
 * représente une solution de tir donnée par un gun
 */
public class FireSolution
{
	/**
	 * angle de tir proposé
	 */
	private double	m_angle;
	
	/**
	 * angle de tir absolut
	 * (contrairement a l'autre qui est le dégres dont le cannon doit tourner)
	 */
	private double	m_absoluteAngle;
	
	/**
	 * estimation de la fiabilité de la solution de tir
	 * porte uniquement sur la fiabilité des données utilisées pour produire la solution
	 * et pas sur des facteurs externes comme la distance la vitesse laterale, etc
	 * TODO: ajouter un facteur de fiabilité pour les facteurs externes
	 * (exemple : faible pour le HoTGun quand la cible a une vitesse laterale élevée)
	 */
	private double	m_fiabilite;

	/**
	 * Constructeur
	 * @param angle angle de tir
	 * @param fiabilite fiabilitée de la solution
	 */
	public FireSolution(double angle, double absoluteAngle, double fiabilite)
	{
		m_angle = angle;
		m_absoluteAngle = absoluteAngle;
		m_fiabilite = fiabilite;
	}

	/**
	 * @return the angle
	 */
	public final double getAngle()
	{
		return m_angle;
	}

	/**
	 * @return the fiabilite
	 */
	public final double getFiabilite()
	{
		return m_fiabilite;
	}

	/**
	 * @return
	 */
	public double getAbsoluteAngle()
	{
		return m_absoluteAngle;
	}
}
