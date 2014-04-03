package ds;

/**
 * Un composant représente une partie physique du robot
 * (radar, tourelle, etc)
 * @author f4
 */
public interface IComponent
{
	/**
	 * Effecture les actions pour ce composant
	 */
	public abstract void Act();
}
