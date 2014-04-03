/*
 * PluggableRobot, by Robert J. Walker
 * Home page: http://robowiki.net/cgi-bin/robowiki?PluggableRobot
 * This software is made available under the RoboWiki Limited Public Code License (RWLPCL). The full
 * text of the license may be found at http://robowiki.net/cgi-bin/robowiki?RWLPCL.
 */
package ds;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import robocode.*;

/**
 * A pluggable listener and strategy architecture for a robot.
 * http://robowiki.net/cgi-bin/robowiki?PluggableRobot
 * 
 * @author Robert J. Walker
 */
public abstract class PluggableRobot extends AdvancedRobot
{
	private static boolean			_battleInitialized	= false;
	private static Hud				_hud;
	private static PluggableRobot	s_instance;

	private ListenerDelegate		_listenerDelegate;
	private ArrayList<IComponent>	_components;
	private ArrayList<Hud.Painter>	_painters;
	private Point2D.Double			_center;

	/**
	 * Sets up the ListenerDelegate and the Component and Painter lists.
	 */
	protected PluggableRobot()
	{
		_listenerDelegate = new ListenerDelegate();
		_components = new ArrayList<IComponent>();
		_painters = new ArrayList<Hud.Painter>();
		s_instance = this;
	}

	/**
	 * Set up the robot, then continuously collect events and invoke components.
	 */
	@Override
	public final void run()
	{

		// Initialize battle (at start of first round only)
		if( !_battleInitialized )
		{
			_hud = new Hud( this );
			initializeBattle();
			_battleInitialized = true;
		}

		// Register custom event test() hook for event manager
		addCustomEvent( new Condition( "eventManager" )
		{
			public boolean test()
			{
				PluggableRobot.this.handleEvents();
				return false;
			}
		} );

		// Round is starting
		initializeRound();

		// Main loop
		while( true )
		{
			for( IComponent component : _components )
			{
				component.Act();
			}

			execute();
		}
	}

	/**
	 * This method will be called at the beginning of a battle. Robots can
	 * override this method to initialize their properties. This is a good place
	 * to initialize static properties or set your tank color.
	 */
	protected void initializeBattle()
	{
		// Default implementation does nothing
	}

	/**
	 * This method will be called at the beginning of each round. Robots can
	 * override this method to initialize their properties. This is a good place
	 * to set up non-static properties and register listeners, painters and
	 * components.
	 */
	protected void initializeRound()
	{
		// Default implementation does nothing
	}

	/**
	 * Called before events get processed each tick. The default implementation
	 * does nothing.
	 */
	public void onBeforeEventsProcessed()
	{
	}
	
	/**
	 * Called after events get processed each tick. The default implementation
	 * does nothing.
	 */
	private void onAfterEventsProcessed()
	{
		Vector<Event> events = getAllEvents();
		for(Event event : events)
		{
			if(!event.getClass().isAssignableFrom(PaintEvent.class))
				onPaint( getGraphics() );
		}
	}

	/**
	 * Returns a Point2D.Double object representing the center of the
	 * battlefield.
	 */
	public Point2D.Double getCenter()
	{
		if( _center == null )
		{
			_center = new Point2D.Double( getBattleFieldWidth() / 2,
					getBattleFieldHeight() / 2 );
		}

		return _center;
	}

	/**
	 * Registers the given EventListener, which will cause it to receive
	 * notifications of the events indicated by the listener interfaces it
	 * implements.
	 */
	protected void registerListener( IEventListener listener )
	{
		_listenerDelegate.register( listener );
	}

	/**
	 * Reigsters the given Component, which will give it the opportunity to act
	 * each turn.
	 */
	protected void registerComponent( IComponent component )
	{
		_components.add( component );
	}

	/**
	 * Reigsters the given Painter, which will give it the opportunity to draw
	 * on the HUD each turn.
	 */
	protected void registerPainter( Hud.Painter painter )
	{
		_painters.add( painter );
	}

	/**
	 * Hand out notifications to the Painters.
	 */
	@Override
	public final void onPaint( java.awt.Graphics2D g )
	{
		_hud.setContext( g ); // Inject the graphics context into the Hud

		for( Hud.Painter painter : _painters )
		{
			painter.paint( _hud, getTime() );
		}

		_hud.setContext( null ); // Clear the injected graphics context
	}


	/**
	 * Process all the events in the queue.
	 */
	private void handleEvents()
	{
		onBeforeEventsProcessed();
		_listenerDelegate.processEvents( getAllEvents() );
		onAfterEventsProcessed();
		clearAllEvents();
	}

	// Since we have our own event manager, we want to prevent overrides of the
	// Robocode event
	// methods, so we'll make them final.

	@Override
	public final void onBattleEnded( BattleEndedEvent event )
	{
	}

	@Override
	public final void onCustomEvent( CustomEvent event )
	{
	}

	@Override
	public final void onDeath( DeathEvent event )
	{
	}

	@Override
	public final void onSkippedTurn( SkippedTurnEvent event )
	{
	}

	@Override
	public final void onBulletHit( BulletHitEvent event )
	{
	}

	@Override
	public final void onBulletHitBullet( BulletHitBulletEvent event )
	{
	}

	@Override
	public final void onBulletMissed( BulletMissedEvent event )
	{
	}

	@Override
	public final void onHitByBullet( HitByBulletEvent event )
	{
	}

	@Override
	public final void onHitRobot( HitRobotEvent event )
	{
	}

	@Override
	public final void onHitWall( HitWallEvent event )
	{
	}

	@Override
	public final void onRobotDeath( RobotDeathEvent event )
	{
	}

	@Override
	public final void onScannedRobot( ScannedRobotEvent event )
	{
	}

	@Override
	public final void onWin( WinEvent event )
	{
	}

	/**
	 * @return
	 */
	public static PluggableRobot getMe()
	{
		return s_instance;
	}

}
