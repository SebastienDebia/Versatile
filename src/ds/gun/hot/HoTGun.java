/**
 * 
 */
package ds.gun.hot;

import java.awt.Color;

import robocode.AdvancedRobot;
import ds.Hud;
import ds.Math2;
import ds.PluggableRobot;
import ds.gun.AbstractGun;
import ds.gun.FireSolution;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

/**
 * @author f4
 *
 */
public class HoTGun extends AbstractGun
{
	
	/**
	 * Constructeur
	 * @param owner
	 * @param targetManager
	 */
	public HoTGun( AdvancedRobot owner, ITargetManager targetManager )
	{
		super( "hot", owner, targetManager, Color.blue );
	}

	/* (non-Javadoc)
	 * @see ds.gun.AbstractGun#getFireSolution()
	 */
	@Override
	protected FireSolution computeFireSolution( double bulletPower )
	{
		// retourne une solution de tir (angle, fiabilité ...)
		try
		{
			IVirtualBot target = getTargetManager().getCurrentTarget();
			double absoluteBearing = getOwner().getHeadingRadians() + target.getBearingRadians();
			double angle = robocode.util.Utils.normalRelativeAngle(absoluteBearing - getOwner().getGunHeadingRadians());
			double absoluteAngle = robocode.util.Utils.normalRelativeAngle(absoluteBearing);
			return new FireSolution(angle, absoluteAngle, 1);
		}
		catch(TargetException te)
		{
			// solution avec fiabilité nulle
			return new FireSolution(0, 0, 0);
		}
	}

	/* (non-Javadoc)
	 * @see ds.gun.AbstractGun#paint(ds.Hud, long)
	 */
	@Override
	public void paint( Hud hud, long tick )
	{
	}

}
