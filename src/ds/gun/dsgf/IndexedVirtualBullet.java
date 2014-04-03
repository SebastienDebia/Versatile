/**
 * 
 */
package ds.gun.dsgf;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import ds.constant.ConstantManager;
import ds.gun.AbstractGun;
import ds.gun.VirtualBullet;
import ds.targeting.IVirtualBot;

/**
 * @author f4
 *
 */
public class IndexedVirtualBullet extends VirtualBullet
{

	/**
	 * index de l'angle de tir
	 */
	private int				m_angleIndex;
	
	/**
	 * Chance de tir
	 */
	private double	m_hitChance;

	/**
	 * @param target
	 * @param gun
	 * @param startPosition
	 * @param angle
	 * @param power
	 * @param angleIndex
	 * @param maxEscapeAngle 
	 * @param hitChances 
	 */
	public IndexedVirtualBullet( IVirtualBot target, AbstractGun gun,
			Double startPosition, int angleIndex, double power, double maxEscapeAngle, double hitChances )
	{
		super( target, gun, startPosition, angleIndex2Angle(target, angleIndex, maxEscapeAngle), power );
		m_angleIndex = angleIndex;
		m_hitChance = hitChances;
	}

	/**
	 * @return
	 */
	public int getAngleIndex()
	{
		return m_angleIndex;
	}
	
	/**
	 * convertie un index en angle en fonction du maxEscapeAngle
	 * @param target 
	 * @param angleIndex index de l'angle
	 * @param maxEscapeAngle angle d'échapement maximum pour la cible
	 * @return l'angle correspondant
	 */
	protected static double angleIndex2Angle(IVirtualBot target, int angleIndex, double maxEscapeAngle)
	{
		int nbSamples = (int)ConstantManager.getInstance().getIntegerConstant( "gun.dsgf.nbSamples" ).longValue();
		double step = 1/(double)nbSamples;
		angleIndex = angleIndex-(nbSamples/2);
		double angle = angleIndex*step*maxEscapeAngle*2;
		//return angle+target.getAbsoluteBearingRadians();
		return (target.getLateralDirection() * angle)+target.getAbsoluteBearingRadians();
	}
	
	/**
	 * @return the hitChance
	 */
	public double getHitChance()
	{
		return m_hitChance;
	}

}
