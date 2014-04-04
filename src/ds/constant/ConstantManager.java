/**
 * 
 */
package ds.constant;

import java.util.HashMap;

/**
 * @author f4
 * 
 */
public class ConstantManager
{
	private HashMap<String, String>		m_stringConstants;
	private HashMap<String, Long>		m_integerConstants;
	private HashMap<String, Double>		m_doubleConstants;
	private HashMap<String, Boolean>	m_booleanConstants;

	private static ConstantManager		s_instance	= null;

	private ConstantManager()
	{
		m_stringConstants = new HashMap<String, String>();
		m_integerConstants = new HashMap<String, Long>();
		m_doubleConstants = new HashMap<String, Double>();
		m_booleanConstants = new HashMap<String, Boolean>();
		
		loadBuiltIn();
	}

	public static ConstantManager getInstance()
	{
		if( s_instance == null )
		{
			s_instance = new ConstantManager();
		}
		return s_instance;
	}

	public void loadFromFile()
	{

	}

	public void loadBuiltIn()
	{
		registerConstant( "debug", true );
		registerConstant( "debugData", false );
		registerConstant( "radar.focus", 5 );
		registerConstant( "gun.selector.statsDepth", 100 );
		registerConstant( "gun.hot.active", true );
		registerConstant( "gun.hot.confidence", 0.95 );
		registerConstant( "gun.linear.active", true );
		registerConstant( "gun.linear.confidence", 0.97 );
		registerConstant( "gun.dsgf.nbSamples", 36 );
		registerConstant( "gun.dsgfFast.active", false );
		registerConstant( "gun.dsgfFast.maxNodeLeafCount", 150 );
		registerConstant( "gun.dsgfFast.minNodeLeafCount", 50 );
		registerConstant( "gun.dsgfFast.flood", 2 );
		registerConstant( "gun.dsgfFast.confidence", 1.0 );
		registerConstant( "gun.dsgfSlow.active", true );
		registerConstant( "gun.dsgfSlow.maxNodeLeafCount", 400 );
		registerConstant( "gun.dsgfSlow.minNodeLeafCount", 50 );
		registerConstant( "gun.dsgfSlow.flood", 7 );
		registerConstant( "gun.dsgfSlow.confidence", 1.05 );
		registerConstant( "gun.dsgfAWS.active", true );
		registerConstant( "gun.dsgfAWS.maxNodeLeafCount", 150 );
		registerConstant( "gun.dsgfAWS.minNodeLeafCount", 50 );
		registerConstant( "gun.dsgfAWS.flood", 0 );
		registerConstant( "gun.dsgfAWS.confidence", 1.1 );
		registerConstant( "gun.dsgf.maxChildNodeCount", 2);
		registerConstant( "gun.dsgf.segmentationFactor", 10);
		registerConstant( "gun.dsgf.densityEstimationWindow", 1 );
		registerConstant( "movement.active", true );
		registerConstant( "movement.cornerGravity", 5000.0);
		registerConstant( "movement.cornerGravityType", 2.0);
		registerConstant( "movement.centerGravity", 1.0);
		registerConstant( "movement.centerGravityType", 1.0);
		registerConstant( "movement.targetGravity", 30.0);
		registerConstant( "movement.targetGravityType", 1.5);
		registerConstant( "movement.wallsGravity", .08);
		registerConstant( "movement.wallsGravityType", 2.0);
		registerConstant( "movement.BulletGravity", .050);
		registerConstant( "movement.BulletGravityType", 1.0);
		//registerConstant( "gun.dsgfFast.minNodeLeafCount", 20 );
		//registerConstant( "gun.dsgfSlow.minNodeLeafCount", 50 );
		//registerConstant( "gun.dsgfAWS.minNodeLeafCount", 30 );
	}

	public void registerConstant( String name, String value )
	{
		m_stringConstants.put( name, value );
	}

	public void registerConstant( String name, long value )
	{
		m_integerConstants.put( name, value );
	}

	public void registerConstant( String name, double value )
	{
		m_doubleConstants.put( name, value );
	}

	public void registerConstant( String name, boolean value )
	{
		m_booleanConstants.put( name, value );
	}

	public String getStringConstant( String name )
	{
		return m_stringConstants.get( name );
	}

	public Long getIntegerConstant( String name )
	{
		return m_integerConstants.get( name );
	}

	public Double getDoubleConstant( String name )
	{
		return m_doubleConstants.get( name );
	}

	public boolean getBooleanConstant( String name )
	{
		return m_booleanConstants.get( name );
	}
}
