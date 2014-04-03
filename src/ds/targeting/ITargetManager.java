package ds.targeting;

import ds.IComponent;
import ds.IEventListener;

public interface ITargetManager extends IEventListener.ScannedRobot, IComponent
{
	/**
	 * Retourne la cible courante
	 */
	public abstract IVirtualBot getCurrentTarget() throws TargetException;
	
	/**
	 * Indique si une cible est acquise
	 */
	public abstract boolean HasTarget();
}
