package ds;

/*
 * PluggableRobot, by Robert J. Walker
 * Home page: http://robowiki.net/cgi-bin/robowiki?PluggableRobot
 * This software is made available under the RoboWiki Limited Public Code License (RWLPCL). The full
 * text of the license may be found at http://robowiki.net/cgi-bin/robowiki?RWLPCL.
 */

import java.util.*;

import robocode.*;

/**
 * Class that manages all the event listeners for a PluggableRobot and delegates events to the
 * appropriate EventListeners. Unlike the default Robocode behavior, events are doled out first by
 * listener (first registered, first notified), then by the order of the listener interfaces
 * declared on the listener implementation. So a class with a declaration like this:
 *     public class MyClass implements EventListener.ScannedRobot, EventListener.Death
 * ...will get notified of the ScannedRobotEvent *before* the DeathEvent!
 * @author Robert J. Walker
 */
public class ListenerDelegate {
	private ArrayList<IEventListener> _listeners = new ArrayList<IEventListener>();
	private static HashMap<Class<? extends IEventListener>, ListenerInvoker> _invokers;

	// Build the invoker map, allowing us to look up invokers by listener class
	static {
		_invokers = new HashMap<Class<? extends IEventListener>, ListenerInvoker>();
		_invokers.put(IEventListener.Death.class,
				new ListenerInvoker<IEventListener.Death, DeathEvent>() {
					protected Class<DeathEvent> eventClass() { return DeathEvent.class; }
					protected void invokeListener(IEventListener.Death listener, DeathEvent event) {
						listener.OnDeath(event);
					}
				}
		);
		_invokers.put(IEventListener.BattleEnded.class,
				new ListenerInvoker<IEventListener.BattleEnded, BattleEndedEvent>() {
					protected Class<BattleEndedEvent> eventClass() { return BattleEndedEvent.class; }
					protected void invokeListener(IEventListener.BattleEnded listener, BattleEndedEvent event) {
						listener.onBattleEnded(event);
					}
				}
		);
		_invokers.put(IEventListener.Win.class,
				new ListenerInvoker<IEventListener.Win, WinEvent>() {
					protected Class<WinEvent> eventClass() { return WinEvent.class; }
					protected void invokeListener(IEventListener.Win listener, WinEvent event) {
						listener.OnWin(event);
					}
				}
		);
		_invokers.put(IEventListener.SkippedTurn.class,
				new ListenerInvoker<IEventListener.SkippedTurn, SkippedTurnEvent>() {
					protected Class<SkippedTurnEvent> eventClass() { return SkippedTurnEvent.class; }
					protected void invokeListener(IEventListener.SkippedTurn listener, SkippedTurnEvent event) {
						listener.OnSkippedTurn(event);
					}
				}
		);
		_invokers.put(IEventListener.ScannedRobot.class,
				new ListenerInvoker<IEventListener.ScannedRobot, ScannedRobotEvent>() {
					protected Class<ScannedRobotEvent> eventClass() { return ScannedRobotEvent.class; }
					protected void invokeListener(IEventListener.ScannedRobot listener, ScannedRobotEvent event) {
						listener.OnScannedRobot(event);
					}
				}
		);
		_invokers.put(IEventListener.HitByBullet.class,
				new ListenerInvoker<IEventListener.HitByBullet, HitByBulletEvent>() {
					protected Class<HitByBulletEvent> eventClass() { return HitByBulletEvent.class; }
					protected void invokeListener(IEventListener.HitByBullet listener, HitByBulletEvent event) {
						listener.OnHitByBullet(event);
					}
				}
		);
		_invokers.put(IEventListener.BulletHit.class,
				new ListenerInvoker<IEventListener.BulletHit, BulletHitEvent>() {
					protected Class<BulletHitEvent> eventClass() { return BulletHitEvent.class; }
					protected void invokeListener(IEventListener.BulletHit listener, BulletHitEvent event) {
						listener.OnBulletHit(event);
					}
				}
		);
		_invokers.put(IEventListener.BulletHitBullet.class,
				new ListenerInvoker<IEventListener.BulletHitBullet, BulletHitBulletEvent>() {
					protected Class<BulletHitBulletEvent> eventClass() { return BulletHitBulletEvent.class; }
					protected void invokeListener(IEventListener.BulletHitBullet listener, BulletHitBulletEvent event) {
						listener.OnBulletHitBullet(event);
					}
				}
		);
		_invokers.put(IEventListener.BulletMissed.class,
				new ListenerInvoker<IEventListener.BulletMissed, BulletMissedEvent>() {
					protected Class<BulletMissedEvent> eventClass() { return BulletMissedEvent.class; }
					protected void invokeListener(IEventListener.BulletMissed listener, BulletMissedEvent event) {
						listener.OnBulletMissed(event);
					}
				}
		);
		_invokers.put(IEventListener.HitRobot.class,
				new ListenerInvoker<IEventListener.HitRobot, HitRobotEvent>() {
					protected Class<HitRobotEvent> eventClass() { return HitRobotEvent.class; }
					protected void invokeListener(IEventListener.HitRobot listener, HitRobotEvent event) {
						listener.OnHitRobot(event);
					}
				}
		);
		_invokers.put(IEventListener.HitWall.class,
				new ListenerInvoker<IEventListener.HitWall, HitWallEvent>() {
					protected Class<HitWallEvent> eventClass() { return HitWallEvent.class; }
					protected void invokeListener(IEventListener.HitWall listener, HitWallEvent event) {
						listener.OnHitWall(event);
					}
				}
		);
		_invokers.put(IEventListener.RobotDeath.class,
				new ListenerInvoker<IEventListener.RobotDeath, RobotDeathEvent>() {
					protected Class<RobotDeathEvent> eventClass() { return RobotDeathEvent.class; }
					protected void invokeListener(IEventListener.RobotDeath listener, RobotDeathEvent event) {
						listener.OnRobotDeath(event);
					}
				}
		);
	}

	/**
	 * Register a new IEventListener.
	 */
	public void register(IEventListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Hand out event notifications to the EventListeners. 
	 */
	public void processEvents(Vector<Event> events) {
		// Notify listeners in the order they were registered
		for(IEventListener listener : _listeners) {
			Class[] interfaces = listener.getClass().getInterfaces();

			// Iterate the interfaces on each listener that descend from IEventListener
			for(Class iface : interfaces) {
				if(!IEventListener.class.isAssignableFrom(iface)) continue;

				// Get the invoker and the corresponding event class for this interface
				ListenerInvoker invoker = _invokers.get(iface);
				Class<? extends Event> eventClass = invoker.eventClass();

				// Iterate the events and hand the ones of the proper type to the invoker
				for(Event event : events) {
					if(!eventClass.isAssignableFrom(event.getClass())) continue;
					invoker.invokeListener(listener, event);
				}
			}
		}
	}

	
	/**
	 * An object that knows about a Robocode Event class and how to invoke its corresponding
	 * IEventListener.
	 * @author Robert J. Walker
	 */
	private static abstract class ListenerInvoker<K extends IEventListener, V extends Event> {
		/**
		 * Returns the Robocode Event class handled by this ListenerInvoker.
		 */
		protected abstract Class<V> eventClass();

		/**
		 * Invokes the given IEventListener, passing in a Robocode Event object.
		 */
		protected abstract void invokeListener(K listener, V event);
	}
}
