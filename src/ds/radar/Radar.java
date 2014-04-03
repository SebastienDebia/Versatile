package ds.radar;

import java.awt.Color;
import java.awt.geom.Point2D;

import ds.IComponent;
import ds.IEventListener;
import ds.Math2;
import ds.Hud;
import ds.Versatile;
import ds.constant.ConstantManager;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Radar implements Hud.Painter, IComponent,
		IEventListener.ScannedRobot
{
	private boolean m_bRadarCorrection;

	// ///////////////////////////////
	// methodes publiques
	// ///////////////////////////////

	public Radar( Versatile owner, ITargetManager targetManager )
	{
		m_owner = owner;
		m_targetManager = targetManager;
		m_focus = Math2.degToRad( ConstantManager.getInstance()
				.getIntegerConstant( "radar.focus" ) );
		m_bRadarCorrection = false;
	}

	/**
	 * Tourne le radar vers la droite
	 */
	@Override
	public void Act()
	{
		// Turn the radar if we have no more turn, starts it if it stops and at the start of round
        /*if ( m_owner.getRadarTurnRemaining() == 0.0 )
        {
        	m_owner.setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
        	System.out.println( "setTurnRadarRightRadians( Double.POSITIVE_INFINITY )" );
        }*/

		m_owner.setAdjustRadarForGunTurn( true );
		m_owner.setAdjustRadarForRobotTurn( true );

        double radarOffset = 0;
		double radarHeading;
		
		try
		{
			if ( !m_targetManager.HasTarget() )
			{ 	//if we haven't seen anybody for a bit....
	        	radarOffset = 360;		//rotate the radar to find a target
			}
			else
			{
				//next is the amount we need to rotate the radar by to scan where the target is now
				radarOffset = robocode.util.Utils.normalRelativeAngle( m_owner.getRadarHeadingRadians()
						- absbearing(m_owner.getX(),m_owner.getY(),m_targetManager.getCurrentTarget().getX(),m_targetManager.getCurrentTarget().getY()));
				
				//this adds or subtracts small amounts from the bearing for the radar
				//to produce the wobbling and make sure we don't lose the target
				if (radarOffset < 0)
					radarOffset -= Math2.PI_OVER_32;
				else
					radarOffset += Math2.PI_OVER_32;
			}
		}
		catch( TargetException te )
		{

		}
		//turn the radar
		m_owner.setTurnRadarLeftRadians(robocode.util.Utils.normalRelativeAngle(radarOffset));
	}

	/**
	 * ramene le radar en arriere quand un robot est scanne
	 */
	@Override
	public void OnScannedRobot( ScannedRobotEvent event )
	{
		m_targetManager.OnScannedRobot( event );
		/*
		// Absolute angle towards target
	    double angleToEnemy = m_owner.getHeadingRadians() + event.getBearingRadians();
	 
	    // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
	    double radarTurn = Utils.normalRelativeAngle( angleToEnemy - m_owner.getRadarHeadingRadians() );
	 
	    // Distance we want to scan from middle of enemy to either side
	    // The 42.0 is how many units from the center of the enemy robot it scans.
	    double extraTurn = Math.min( Math.atan( 42.0 / event.getDistance() ), robocode.Rules.RADAR_TURN_RATE_RADIANS );
	 
	    // Adjust the radar turn so it goes that much further in the direction it is going to turn
	    // Basically if we were going to turn it left, turn it even more left, if right, turn more right.
	    // This allows us to overshoot our enemy so that we get a good sweep that will not slip.
	    radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
	 
	    //Turn the radar
	    m_owner.setTurnRadarRightRadians(radarTurn);
	    
    	System.out.println( "setTurnRadarRightRadians( " + radarTurn + " )" );*/
	}

	@Override
	public void paint( Hud hud, long tick )
	{
		IVirtualBot target = null;
		try
		{
			target = m_targetManager.getCurrentTarget();
			hud.setColor( Color.red );
			hud.drawRect( target.getBottomLeftCorner().getX(), target
					.getBottomLeftCorner().getY(), target.getSize(), target
					.getSize() );

		}
		catch( TargetException te )
		{

		}
	}

	// ///////////////////////////////
	// methodes privees
	// ///////////////////////////////
	private double absbearing( double x1, double y1, double x2, double y2 )
	{
		Point2D pt1 = new Point2D.Double( x1, y1 );
		Point2D pt2 = new Point2D.Double( x2, y2 );
		
		double xo = pt2.getX() - pt1.getX();
		double yo = pt2.getY() - pt1.getY();
		double h = pt1.distance( pt2 );
		if( xo > 0 && yo > 0 )
		{
			return Math.asin( xo / h );
		}
		if( xo > 0 && yo < 0 )
		{
			return Math.PI - Math.asin( xo / h );
		}
		if( xo < 0 && yo < 0 )
		{
			return Math.PI + Math.asin( -xo / h );
		}
		if( xo < 0 && yo > 0 )
		{
			return 2.0 * Math.PI - Math.asin( -xo / h );
		}
		return 0;
	}

	// ///////////////////////////////
	// membres prives
	// ///////////////////////////////

	/**
	 * Proprietaire du module
	 */
	private Versatile		m_owner;

	/**
	 * gestionnaire de cibles
	 */
	private ITargetManager	m_targetManager;

	/**
	 * focus (ecartement du radar)
	 */
	private double			m_focus;
}
