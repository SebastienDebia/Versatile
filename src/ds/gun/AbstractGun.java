package ds.gun;

import java.awt.Color;

import robocode.AdvancedRobot;
import ds.DateTime;
import ds.Hud;
import ds.constant.ConstantManager;
import ds.targeting.ITargetManager;
import ds.targeting.VirtualBot;

/**
 * représente une methode de tir classe abstraite mère
 * 
 * @author f4
 */
public abstract class AbstractGun
{
	/**
	 * temps de la derniere solution de tir
	 */
	private DateTime		m_lastFsTime;

	/**
	 * derniere solution de tir
	 */
	private FireSolution	m_lastFs;

	/**
	 * propriétaire du gun
	 */
	private AdvancedRobot	m_owner;

	/**
	 * gestionnaire de cibles
	 */
	private ITargetManager	m_targetManager;

	/**
	 * couleur du cannon pour les affichages
	 */
	private Color			m_color;

	/**
	 * nom du gun
	 */
	private String			m_name;

	/**
	 * constructeur
	 * 
	 * @param owner
	 *            proprietaire du gun
	 * @param targetManager
	 *            gestionnaire de cibles
	 */
	public AbstractGun( String name, AdvancedRobot owner, ITargetManager targetManager,
			Color color )
	{
		m_owner = owner;
		m_targetManager = targetManager;
		m_color = color;
		m_lastFsTime = new DateTime( -1, -1 );
		m_name = name;
	}

	/**
	 * Donne la solution de tir ne recalcule pas si ce n'est pas nécéssaire
	 * 
	 * @param bulletPower
	 *            puissance de la balle pour ce tir
	 * @return la solution de tir calculée
	 */
	public final FireSolution getFireSolution( double bulletPower )
	{
		DateTime currentDateTime = new DateTime( m_owner.getRoundNum(), m_owner.getTime() );
		if( m_lastFsTime.before( currentDateTime ) )
			m_lastFs = computeFireSolution( bulletPower );

		m_lastFsTime = currentDateTime;

		return m_lastFs;
	}

	/**
	 * Calcule la solution de tir
	 * 
	 * @param bulletPower
	 *            puissance de la balle pour ce tir
	 * @return la solution de tir calculée
	 */
	protected abstract FireSolution computeFireSolution( double bulletPower );

	/**
	 * Donne le propriétaire du gun
	 * 
	 * @return le propriétaire du gun (le robot)
	 */
	protected final AdvancedRobot getOwner()
	{
		return m_owner;
	}

	/**
	 * Donne le target manager
	 * 
	 * @return le target manager utilisé par le gun
	 */
	protected final ITargetManager getTargetManager()
	{
		return m_targetManager;
	}

	/**
	 * Couleur du canon pour les affichages
	 */
	public Color getColor()
	{
		return m_color;
	}

	/**
	 * Nom du gun
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * confidence level
	 */
	public double getConfidence()
	{
		return ConstantManager.getInstance().getDoubleConstant("gun."+getName()+".confidence");
	}

	/**
	 * Fait un peu de boulot si besoin
	 * (statistiques etc)
	 * @param power puissance de tir
	 */
	public void Act(double power)
	{
		// l'implementation de base ne fait rien
	}

	/**
	 * Affiche les infos de debogage
	 * @param hud
	 * @param tick
	 */
	public abstract void paint( Hud hud, long tick );

	/**
	 * Permet de faire quelque chose lors d'un tir réell
	 * @param power puissance du tir
	 */
	public void virtualFire( double power )
	{
		// l'implementation de base ne fait rien
	}
}
