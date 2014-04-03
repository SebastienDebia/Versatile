/**
 * 
 */
package ds.gun;

import java.util.ArrayList;
import java.util.Iterator;

import ds.DateTime;

/**
 * @author f4
 * Conteneur
 * vague de balles
 */
public class BulletWave
{
	/**
	 * liste des balles
	 */
	private ArrayList<VirtualBullet>	m_bullets;

	private boolean	m_bReelle;

	private DateTime m_dateTime;
	
	/**
	 * Constructeur
	 * @param bReelle poids de la vague (plus important pour les vagues réeelles
	 */
	public BulletWave( long round, long time, boolean bReelle )
	{
		m_bullets = new ArrayList<VirtualBullet>();
		m_dateTime = new DateTime( round, time );
		m_bReelle = bReelle;
	}
	
	/**
	 * ajout d'une balle a la vague
	 * @param vb nouvelle balle virtuelle
	 */
	public void addBullet( VirtualBullet vb )
	{
		m_bullets.add( vb );
		//vb.update();
	}
	
	/**
	 * mise à jour de la position des balles
	 */
	public void update()
	{
		Iterator<VirtualBullet> itVb = m_bullets.iterator();
		
		// pour chaque balle
		while ( itVb.hasNext() )
		{
			VirtualBullet vb = itVb.next();
			
			// mise a jour de la position
			vb.update();
		}
	}

	/**
	 * @return
	 */
	public long getLaunchTurn()
	{
		return m_dateTime.getTurn();
	}

	/**
	 * @return
	 */
	public long getLaunchRound()
	{
		return m_dateTime.getRound();
	}

	/**
	 * liste des balles
	 */
	public ArrayList<VirtualBullet> getBullets()
	{
		return m_bullets;
	}

	/**
	 * @return le poid de la vague
	 */
	public boolean estReelle()
	{
		return m_bReelle;
	}

	public DateTime getDateTime()
	{
		return m_dateTime;
	}
}
