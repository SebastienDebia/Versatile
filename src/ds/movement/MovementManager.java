/**
 * 
 */
package ds.movement;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import com.sun.corba.se.impl.orbutil.closure.Constant;

import ds.Hud;
import ds.Math2;
import ds.PluggableRobot;
import ds.Versatile;
import ds.constant.ConstantManager;
import ds.gun.VirtualBullet;
import ds.targeting.ITargetManager;
import ds.targeting.IVirtualBot;
import ds.targeting.TargetException;
import robocode.AdvancedRobot;
import robocode.util.Utils;

/**
 * @author Seb
 * 
 */
public class MovementManager implements IMovementManager
{

	/**
	 * proprietaire
	 */
	private PluggableRobot					m_owner;

	/**
	 * liste des objets
	 */
	private ArrayList<AntiGravityObject>	m_agObjects;

	/**
	 * gestionnaire de cible
	 */
	private ITargetManager					m_targetManager;

	/**
	 * Vecteur de mouvement (uniquement pour l'affichage)
	 */
	private Vector2D						m_moveVector;

	/**
	 * gravité des balles
	 */
	private Double							m_BulletGravity;

	/**
	 * type de gravité des balles
	 */
	private double							m_BulletGravityType;
	
	/**
	 * TargetManager � utiliser pour le point de vue de l'ennemi
	 */
	private EnemyTargetManager 				m_enemyTargetManager;

	/**
	 * Constructeur
	 * 
	 * @param owner
	 *            proprietaire du module
	 */
	public MovementManager( Versatile owner, ITargetManager targetManager )
	{
		m_moveVector = new Vector2D( 0, 0 );
		
		m_enemyTargetManager = new EnemyTargetManager( owner );
		
		ConstantManager cm = ConstantManager.getInstance();
		m_BulletGravity = cm.getDoubleConstant(	"movement.BulletGravity" );
		m_BulletGravityType = cm.getDoubleConstant(	"movement.BulletGravityType" ).longValue();

		m_targetManager = targetManager;

		m_owner = owner;
		m_agObjects = new ArrayList<AntiGravityObject>();

		// coins
		double cornersGravity = cm.getDoubleConstant( "movement.cornerGravity" );
		double cornersGravityType = cm.getDoubleConstant( "movement.cornerGravityType" ).longValue();
		m_agObjects.add( new AntiGravityObject( 0, 0, cornersGravity, cornersGravityType ) );
		m_agObjects.add( new AntiGravityObject( m_owner.getBattleFieldWidth(),
				0, cornersGravity, cornersGravityType ) );
		m_agObjects.add( new AntiGravityObject( m_owner.getBattleFieldWidth(),
				m_owner.getBattleFieldHeight(), cornersGravity, cornersGravityType ) );
		m_agObjects.add( new AntiGravityObject( 0, m_owner
				.getBattleFieldHeight(), cornersGravity, cornersGravityType ) );

		// centre
		double centerGravity = cm.getDoubleConstant( "movement.centerGravity" );
		double centerGravityType = cm.getDoubleConstant( "movement.centerGravityType" ).longValue();
		m_agObjects.add( new AntiGravityObject( m_owner.getCenter().getX(),
				m_owner.getCenter().getY(), centerGravity, centerGravityType ) );

		// cible
		double targetGravity = cm.getDoubleConstant( "movement.targetGravity" );
		double targetGravityType = cm.getDoubleConstant( "movement.targetGravityType" ).longValue();
		m_agObjects.add( new TargetTrackerAntiGravityObject( m_targetManager,
				targetGravity, targetGravityType ) );

		// murs
		double wallsGravity = cm.getDoubleConstant( "movement.wallsGravity" );
		double wallsGravityType = cm.getDoubleConstant( "movement.wallsGravityType" ).longValue();
		m_agObjects.add( new VerticalLine( 0, wallsGravity*1.33, wallsGravityType ) );
		m_agObjects.add( new VerticalLine( m_owner.getBattleFieldWidth(),
				wallsGravity, wallsGravityType ) );
		m_agObjects.add( new HorizontalLine( 0, wallsGravity*1.33, wallsGravityType ) );
		m_agObjects.add( new HorizontalLine( m_owner.getBattleFieldHeight(),
				wallsGravity, wallsGravityType ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ds.IComponent#Act()
	 */
	@Override
	public void Act()
	{
		// detection du tir
		try
		{
			IVirtualBot target = m_targetManager.getCurrentTarget();
			if( target.getTimeSinceLastShot() == 0 )
			{
				double angleHot = 0;
				double angleLinear = 0;
				Point2D.Double targetPosition = m_targetManager.getCurrentTarget().getPosition();
				Point2D.Double myposition
					= new Point2D.Double(	m_owner.getX() - Math.sin( m_owner.getHeading() ) * m_owner.getVelocity(),
											m_owner.getY() - Math.cos( m_owner.getHeading() ) * m_owner.getVelocity() );
				// tir hot
				{
					MovingPerpendicularAntiGravityObject bulletTracker = new MovingPerpendicularAntiGravityObject( target, m_BulletGravity, m_BulletGravityType );
					double angle = absbearing( targetPosition, myposition );
					angle += (Math.random()*Math2.PI/16)-Math.PI/32;
					IMovingObject vb = new VirtualBullet( null, null, target.getPosition(), angle, target.getLastShotPower() );
					bulletTracker.setMovingObject( vb  );
					vb.update();
					m_agObjects.add( bulletTracker );
					angleHot = angle;
				}
				
				// tir linear
				{
					MovingPerpendicularAntiGravityObject bulletTracker = new MovingPerpendicularAntiGravityObject( target, m_BulletGravity, m_BulletGravityType );
					double hotAngle = absbearing( targetPosition, myposition );
					double angle = hotAngle + Math.asin(m_owner.getVelocity() / robocode.Rules.getBulletSpeed(target.getLastShotPower()) * Math.sin(m_owner.getHeadingRadians() - hotAngle));
					angle += (Math.random()*Math2.PI/16)-Math.PI/32;
					IMovingObject vb = new VirtualBullet( null, null, target.getPosition(), angle, target.getLastShotPower() );
					bulletTracker.setMovingObject( vb  );
					vb.update();
					m_agObjects.add( bulletTracker );
					angleLinear = angle;
				}
				
				// si les 2 précédentes balles virtuelles sont proches, ajoute une 3ème entre les 2 pour eviter que le robot ne reste coincé au milieu et se les mange!
				double distanceBetweenBullets = Math.tan( Math.abs(angleHot - angleLinear) ) * myposition.distance(targetPosition);
				if( distanceBetweenBullets < 80 ) // 2*taille robot
				{
					MovingPerpendicularAntiGravityObject bulletTracker = new MovingPerpendicularAntiGravityObject( target, m_BulletGravity/3, m_BulletGravityType );
					double andleMedian = (angleHot + angleLinear)/2;
					IMovingObject vb = new VirtualBullet( null, null, target.getPosition(), andleMedian, target.getLastShotPower() );
					bulletTracker.setMovingObject( vb  );
					vb.update();
					m_agObjects.add( bulletTracker );
				}
				/*
				// tir random
				{
					MovingPerpendicularAntiGravityObject bulletTracker = new MovingPerpendicularAntiGravityObject( m_BulletGravity/5, m_BulletGravityType );
					double maxEscapeAngle = Math.asin( 8.0 / (20 - 3.0 * target.getLastShotPower()) );
					double angle = angleHot + (maxEscapeAngle * 2 * Math.random() - maxEscapeAngle);
					IMovingObject vb = new VirtualBullet( null, null, target.getPosition(), angle, target.getLastShotPower() );
					bulletTracker.setMovingObject( vb  );
					vb.update();
					m_agObjects.add( bulletTracker );
				}*/
				/*for( int i = -18; i < 18; ++i )
				{
					MovingPerpendicularAntiGravityObject bulletTracker = new MovingPerpendicularAntiGravityObject( target, m_BulletGravity/10, m_BulletGravityType );
					double maxEscapeAngle = 2*Math.asin( 8.0 / (20 - 3.0 * target.getLastShotPower()) );
					double step = maxEscapeAngle/36;
					double angle = i*step+angleHot;
					IMovingObject vb = new VirtualBullet( null, null, target.getPosition(), angle, target.getLastShotPower() );
					bulletTracker.setMovingObject( vb  );
					vb.update();
					m_agObjects.add( bulletTracker );
				}*/
			}
		}
		catch( TargetException e1 )
		{
		}


		m_moveVector = new Vector2D( 0, 0 );
		Point2D.Double referent = new Point2D.Double( m_owner.getX(), m_owner
				.getY() );

		for(int i=0; i<m_agObjects.size(); i++ )
		{
			AntiGravityObject ago = (AntiGravityObject)m_agObjects.get( i );
			if( MovingAntiGravityObject.class.isAssignableFrom( ago.getClass() ) )
			{
				MovingAntiGravityObject mago = (MovingAntiGravityObject)ago;
				mago.updatePosition();
				if( mago.isOutOfBattlefield() )
				{
					m_agObjects.remove( mago );
					i--;
				}
			}
		}
		
		for( AntiGravityObject ago : m_agObjects )
		{
			m_moveVector.add( ago.getForceVector( referent ) );
		}
		
		// wall smoothing, pour éviter de rebondir sur le mur
		{
			if( m_moveVector.getR() > 150 )
			{
				m_moveVector.divide( m_moveVector.getR() );
				m_moveVector.multiply( 150 );
			}
			Point2D.Double myMovePosition = new Point2D.Double( m_owner.getX() + m_moveVector.getX(), m_owner.getY() + m_moveVector.getY() );
			Point2D.Double myposition = new Point2D.Double( m_owner.getX(), m_owner.getY() );
			Point2D.Double orbitPosition = new Point2D.Double( m_owner.getBattleFieldWidth()/2, m_owner.getBattleFieldHeight()/2 );
			IVirtualBot target;
			try
			{
				target = m_targetManager.getCurrentTarget();
				orbitPosition = target.getPosition();
			} catch (TargetException e) { /*not an error*/ }
			m_moveVector = new Vector2D( fastWallSmooth( orbitPosition, myposition, m_moveVector ) );
			m_moveVector.substract( new Vector2D( myposition ) );
		}
		
		// mouvement
		if( m_moveVector.getR() <= 20 )
		{
			// au lieu de faire du sur place on se met perpendiculaire a l'enemi
			try
			{
				Point2D.Double targetPosition = m_targetManager.getCurrentTarget().getPosition();
				Point2D.Double myposition = new Point2D.Double( m_owner.getX(), m_owner.getY() );
				// angle vers la cible
				double angle = absbearing( targetPosition, myposition );
				// rotation
				angle += Math2.PI_OVER_2 - m_owner.getHeadingRadians();
				// normalisation
				angle = robocode.util.Utils.normalRelativeAngle( angle );
				m_owner.setTurnRightRadians( angle );
				m_owner.setAhead(0);
			}
			catch( TargetException e )
			{
			}
		}
		else
		{
			goTo( new Point2D.Double( m_moveVector.getX() + m_owner.getX(),
					m_moveVector.getY() + m_owner.getY() ) );
		}
	}

	private void goTo( Point2D point )
	{
		Point2D location = new Point2D.Double( m_owner.getX(), m_owner.getY() );
		double distance = location.distance( point );
		double angle = robocode.util.Utils.normalRelativeAngle( absbearing(
				location, point )
				- m_owner.getHeadingRadians() );
		if( Math.abs( angle ) > Math.PI / 2 )
		{
			distance *= -1.0;
			if( angle > 0.0 )
			{
				angle -= Math.PI;
			}
			else
			{
				angle += Math.PI;
			}
		}
		m_owner.setTurnRightRadians( angle );
		m_owner.setAhead( distance );
	}

	public double absbearing( Point2D pt1, Point2D pt2 )
	{
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

	@Override
	public void paint( Hud hud, long tick )
	{
		hud.setColor( Color.white );
		hud.drawLine( m_owner.getX(), m_owner.getY(), m_owner.getX()
				+ m_moveVector.getX(), m_owner.getY() + m_moveVector.getY() );

		hud.setColor( Color.cyan );
		Point2D.Double referent = new Point2D.Double( m_owner.getX(), m_owner
				.getY() );
		for( AntiGravityObject ago : m_agObjects )
		{
			Vector2D forceVector = ago.getForceVector( referent );
			double X = ago.getPosition().getX();
			double Y = ago.getPosition().getY();
			double X2 = X + forceVector.getX();
			double Y2 = Y + forceVector.getY();
			hud.drawLine( X, Y, X2, Y2 );
			hud.drawCircle( X, Y, 2);
		}
	}
	
	private Point2D.Double fastWallSmooth(Point2D.Double orbitCenter, Point2D.Double position, Vector2D vStick )
	{
		final double MARGIN = 30;
		Point2D.Double projected = new Point2D.Double( position.x + vStick.getX(), position.y + vStick.getY() );
		double direction = isLeft(position, orbitCenter, projected)?1:-1;
		double distanceToOrbitCenter = orbitCenter.distance( position );
		double stickLength = vStick.getR();
	 
		double fieldWidth = m_owner.getBattleFieldWidth(), fieldHeight = m_owner.getBattleFieldHeight();
	 
		double stick = Math.min(stickLength, distanceToOrbitCenter);
		double stickSquared = square(stick);
	 
		int LEFT = -1, RIGHT = 1, TOP = 1, BOTTOM = -1;
	 
		int topOrBottomWall = 0;
		int leftOrRightWall = 0;
	 
		//double desiredAngle = Utils.normalAbsoluteAngle(absoluteAngle(position, orbitCenter) - direction * Math.PI / 2.0);
		//double desiredAngle = Utils.normalAbsoluteAngle(absbearing(position, initialProjectedPosition));
		//Point2D.Double projected = projectPoint(position, desiredAngle, stick);
		if(projected.x >= MARGIN && projected.x <= fieldWidth - MARGIN && projected.y >= MARGIN && projected.y <= fieldHeight - MARGIN)
			return projected;
	 
		if(projected.x  > fieldWidth - MARGIN || position.x  > fieldWidth - stick - MARGIN) leftOrRightWall = RIGHT;
		else if (projected.x < MARGIN || position.x < stick + MARGIN) leftOrRightWall = LEFT;
	 
		if(projected.y > fieldHeight - MARGIN || position.y > fieldHeight - stick - MARGIN) topOrBottomWall = TOP;
		else if (projected.y < MARGIN || position.y < stick + MARGIN) topOrBottomWall = BOTTOM;
	 
		if(topOrBottomWall == TOP){
			if(leftOrRightWall == LEFT){
				if(direction > 0)
					//smooth against top wall
					return new Point2D.Double(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN);
				else
					//smooth against left wall
					return new Point2D.Double(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
	 
			} else if(leftOrRightWall == RIGHT){
				if(direction > 0)
					//smooth against right wall
					return new Point2D.Double(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
				else 
					//smooth against top wall
					return new Point2D.Double(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN);
	 
			}
			//Smooth against top wall
			return new Point2D.Double(position.x + direction * Math.sqrt(stickSquared - square(fieldHeight - MARGIN - position.y)), fieldHeight - MARGIN); 
		} else if(topOrBottomWall == BOTTOM){
			if(leftOrRightWall == LEFT){
				if(direction > 0)
					//smooth against left wall
					return new Point2D.Double(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
				else
					//smooth against bottom wall
					return new Point2D.Double(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
			} else if(leftOrRightWall == RIGHT){
				if(direction > 0)
					//smooth against bottom wall
					return new Point2D.Double(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
				else
					//smooth against right wall
					return new Point2D.Double(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
	 
			}
			//Smooth against bottom wall
			return new Point2D.Double(position.x - direction * Math.sqrt(stickSquared - square(position.y - MARGIN)), MARGIN);
		}
	 
		if(leftOrRightWall == LEFT){
			//smooth against left wall
			return new Point2D.Double(MARGIN, position.y + direction * Math.sqrt(stickSquared - square(position.x - MARGIN)));
		} else if(leftOrRightWall == RIGHT){
			//smooth against right wall
			return new Point2D.Double(fieldWidth - MARGIN, position.y - direction * Math.sqrt(stickSquared - square(fieldWidth - MARGIN - position.x)));
		}
	 
		throw new RuntimeException("This code should be unreachable. position = " + position.x + ", " + position.y + "  orbitCenter = " + orbitCenter.x + ", " + orbitCenter.y + " direction = " + direction);
	}
	 
	private static Point2D.Double projectPoint(Point2D.Double origin, double angle, double distance){
		return new Point2D.Double(origin.x + distance * Math.sin(angle), origin.y + distance * Math.cos(angle));
	}
	 
	private static double absoluteAngle(Point2D.Double origin, Point2D.Double target) {
	    return Math.atan2(target.x - origin.x, target.y - origin.y);
	}
	 
	private double square(double x){
		return x*x;
	}
	
	private boolean isLeft(Point2D.Double a, Point2D.Double b, Point2D.Double c)
	{
	     return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)) > 0;
	}

}
