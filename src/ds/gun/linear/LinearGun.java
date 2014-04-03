/**
 * 
 */
package ds.gun.linear;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.util.Utils;
import ds.Hud;
import ds.Math2;
import ds.gun.AbstractGun;
import ds.gun.FireSolution;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;

/**
 * @author f4
 * 
 */
public class LinearGun extends AbstractGun
{

	/**
	 * constructeur
	 * 
	 * @param owner
	 *            proprietaire (robot)
	 * @param targetManager
	 *            getstionnaire de cibles
	 */
	public LinearGun( AdvancedRobot owner, ITargetManager targetManager )
	{
		super( "linear", owner, targetManager, Color.green );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.gun.AbstractGun#getFireSolution()
	 */
	@Override
	protected FireSolution computeFireSolution( double bulletPower )
	{
		try
		{
			IVirtualBot target = getTargetManager().getCurrentTarget();

			double myX = getOwner().getX();
			double myY = getOwner().getY();
			double enemyX = target.getX();
			double enemyY = target.getY();
			double enemyHeading = target.getHeadingRadians();
			double enemyVelocity = target.getVelocity();

			double deltaTime = 0;
			double battleFieldHeight = getOwner().getBattleFieldHeight();
			double battleFieldWidth = getOwner().getBattleFieldWidth();
			double predictedX = enemyX;
			double predictedY = enemyY;
			
			while( (++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double
					.distance( myX, myY, predictedX, predictedY ) )
			{
				predictedX += Math.sin( enemyHeading ) * enemyVelocity;
				predictedY += Math.cos( enemyHeading ) * enemyVelocity;
				if( predictedX < 18.0 || predictedY < 18.0
						|| predictedX > battleFieldWidth - 18.0
						|| predictedY > battleFieldHeight - 18.0 )
				{
					predictedX = Math.min( Math.max( 18.0, predictedX ),
							battleFieldWidth - 18.0 );
					predictedY = Math.min( Math.max( 18.0, predictedY ),
							battleFieldHeight - 18.0 );
					break;
				}
			}
			double theta = Utils.normalAbsoluteAngle( Math.atan2( predictedX
					- getOwner().getX(), predictedY - getOwner().getY() ) );
			
			double angle = Utils.normalRelativeAngle( theta
					- getOwner().getGunHeadingRadians() );
			double absoluteAngle = Utils.normalRelativeAngle( theta );
			return new FireSolution( angle, absoluteAngle, 1 );
		}
		catch( TargetException te )
		{
			// solution avec fiabilité nulle
			return new FireSolution( 0, 0, 0 );
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
