package ds;

import robocode.*;

public interface IEventListener
{
	public interface BattleEnded extends IEventListener
	{
		/**
		 * Called by PluggableRobot at the end of the battle
		 */
		public void onBattleEnded( BattleEndedEvent event );
	}
	
	public interface BulletHitBullet extends IEventListener
	{
		/**
		 * Called by PluggableRobot when a bullet fired by your robot has hit
		 * another bullet.
		 */
		public void OnBulletHitBullet(BulletHitBulletEvent event);
	}

	public interface BulletHit extends IEventListener
	{
		/**
		 * Called by PluggableRobot when a bullet fired by your robot has hit
		 * another robot.
		 */
		public void OnBulletHit(BulletHitEvent event);
	}

	public interface BulletMissed extends IEventListener
	{
		/**
		 * Called by PluggableRobot when a bullet fired by your robot has hit a
		 * wall.
		 */
		public void OnBulletMissed(BulletMissedEvent event);
	}

	public interface Death extends IEventListener
	{
		/**
		 * Called by PluggableRobot when your robot has been destroyed.
		 */
		public void OnDeath(DeathEvent event);
	}

	public interface HitByBullet extends IEventListener
	{
		/**
		 * Called by PluggableRobot when your robot has been hit by an enemy
		 * bullet.
		 */
		public void OnHitByBullet(HitByBulletEvent event);
	}

	public interface HitRobot extends IEventListener
	{
		/**
		 * Called by PluggableRobot when your robot has collided with another
		 * robot.
		 */
		public void OnHitRobot(HitRobotEvent event);
	}

	public interface HitWall extends IEventListener
	{
		/**
		 * Called by PluggableRobot when your robot has collided with a wall.
		 */
		public void OnHitWall(HitWallEvent event);
	}

	public interface RobotDeath extends IEventListener
	{
		/**
		 * Called by PluggableRobot when an enemy robot has been destroyed.
		 */
		public void OnRobotDeath(RobotDeathEvent event);
	}

	public interface ScannedRobot extends IEventListener
	{
		/**
		 * Le radar a scanne un robot
		 */
		public void OnScannedRobot(ScannedRobotEvent event);
	}

	public interface SkippedTurn extends IEventListener
	{
		/**
		 * Called by PluggableRobot when your robot skipped a turn.
		 */
		public void OnSkippedTurn(SkippedTurnEvent event);
	}

	public interface Win extends IEventListener
	{
		/**
		 * Called by PluggableRobot when all robots besides yours have been
		 * destroyed.
		 */
		public void OnWin(WinEvent event);
	}

}
