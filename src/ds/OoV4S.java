package ds;
import robocode.*;
/*
import robocode.robocodeGL.*;
import robocode.robocodeGL.system.*;*/
import java.awt.Color;
import java.util.*;

import java.awt.*;
import java.awt.geom.*;


/* TODO
   [ ] ajustement de lajustement (^^) de la puissance de tir en fct de la cible (tr�s mouvante = plus petit)
   [ ] ba merde alor je c plus ^^
   [ ] switchage de mode de tir
   [ ] annalyse de la mobilitée > switchage de la distance
   [ ] annalyse de l'aleatoiritée

/**
 * OoV2 - a robot by (your name here)
 */
public class OoV4S extends AdvancedRobot
{
	/**
	 * run: OoV2's default behavior
	 */
	
	/*GLRenderer renderer;
	Vector ballesVirtuellesGL = new Vector();
	Vector vaguesGL = new Vector();*/

	int largeurMap = 16;
	int hauteurMap = 12;
	
	//constantes des modes : 
	// RAM = 0
	// FIRE = 1
	// ESCAPE = 2
	int mode = 1;
	
	// GF = 0
	// lineaire = 1
	// simple = 2
	int method = 0;
	
	static double speedRecords[] = new double[3];
	static Point2D posRecords[] = new Point2D[3];
	
	double PI = 3.1415926535897932384626433832795;	// hum, bahh ... comment dire, ... PI XD
	EnemyV4 target;		// Contient les dernières info sur la cible
	double firePower;	// puissance de feu, recalculée en permanence
	Vector balles = new Vector();
	Vector vagues = new Vector();
	double map[][] = new double[largeurMap][hauteurMap];
	
	static int valuesNumber = 0; 	// nb de valeurs pour le GFG
	final static int dataLength = 31;	// taille du tableau
	
	final static int SEG_DIST = 5;	// 6 valeurs de distance � l'enemy (distance*vitesse bullet)
	final static int SEG_ESPEED = 5;	// 6 valeurs de vitesse de l'enemy
	final static int SEG_RELHEAD = 6;	// relative heading pour savoir s'il nouge perpendiculairement ou pas
	
	final static int LEVEL1 = SEG_DIST*SEG_RELHEAD*dataLength*20;
	
	static double[][] data = new double[SEG_DIST][dataLength];
	static double[][][] data1 = new double[SEG_RELHEAD][SEG_DIST][dataLength];
	static double[][][] data2 = new double[SEG_ESPEED][SEG_DIST][dataLength];
	
	
	int bestAngularOffset=0;
	
	public void run() {
		/*renderer = GLRenderer.getInstance();*/
		
		for ( int i=0; i<3; i++ )
		{
			speedRecords[i] = 0;
			posRecords[i] = new Point2D(0,0);
		}
		
		target = new EnemyV4();               // target est un objet de type ennemy. Il gere les informations sur la cible
		target.distance = 100000;			// initialise the distance so that we can select a target
		target.energy = 500;
		setColors(Color.darkGray,Color.green,Color.green);	//colore le robot
		
		//rend le Gun le radar et le robot lui m�me ind�pendant
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		turnRadarRightRadians(2*PI);			//fé un tour complet avec le radar pour avoir une vue complete du champ de bataille
		
		String pourLePrint;
		double posX = getX(), posY = getY();
		while(true) {
				posX = getX();
				posY = getY();
				updateBalles();
				updateMap();
				/*pourLePrint = "\n";
				for ( int i=hauteurMap-1; i>=0; i-- )
				{
					for ( int j=0; j<largeurMap; j++ )
					{
						/*if ( map[j][i] <= 0.2 )
							pourLePrint+="V ";
						else if ( map[j][i] <= 0.7 )
							pourLePrint+="0 ";
						else
							pourLePrint+="X ";*/
							/*
						if ( i==(int)(hauteurMap*posY/getBattleFieldHeight()) && j==(int)(largeurMap*posX/getBattleFieldWidth()) )
							pourLePrint+="0X0";
						else
						{
							if ( (int)map[j][i] < 10 )
								pourLePrint+="0";
							if ( (int)map[j][i] < 100 )
								pourLePrint+="0";
							pourLePrint+=(int)map[j][i];
						}
							pourLePrint+=" ";
					}
					pourLePrint+="\n";
				}
				out.print(pourLePrint);
				
				out.println ( "hauteurMap="+hauteurMap+" largeurMap="+largeurMap+"\nposY="+posY+" posX="+posX+"\ngetBattleFieldHeight()="+getBattleFieldHeight()+" getBattleFieldWidth()="+getBattleFieldWidth());
				out.println ( "Case: ["+(int)(hauteurMap*posY/getBattleFieldHeight())+";"+(int)(largeurMap*posX/getBattleFieldWidth())+"] " );*/
				
			
				switch ( mode )
				{
					case 1:
						doMovement();				//Move the bot
						doFirePower();				//select the fire power to use	
						doScanner();				//Oscillate the scanner over the bot
						doFire();
						doGun();
						//setMaxVelocity(Math.abs(getTurnRemaining()) < 40 ? 100 : .1);
						break;
					case 0:
						doMovement();				//Move the bot
						doScanner();				//Oscillate the scanner over the bot
						break;
				}
				execute();				//execute all commands
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		long time;
		double gunOffset;
		EnemyV4 IAmTheTarget = new EnemyV4(); // Enemy utilisé pour le robot ennemi (prédiction, flemme de mettre la méthode ailleur ^^)
		/*PointGL ptGL;*/
		
		//remplissage de IAmTheTarget
		// (utilisé pour la prédiction du tir de l'ot
			IAmTheTarget.x=((Point2D)posRecords[0]).getX();
			IAmTheTarget.y=((Point2D)posRecords[0]).getY();
			IAmTheTarget.head = getHeadingRadians();
			IAmTheTarget.speed = speedRecords[1];
			IAmTheTarget.distance = e.getDistance();
			
		speedRecords[2] = speedRecords[1];
		speedRecords[1] = speedRecords[0];
		speedRecords[0] = getVelocity();
		posRecords[2] = posRecords[1];
		posRecords[1] = posRecords[0];
		posRecords[0] = new Point2D(getX(), getY());
		
		
        //if we have found a closer robot than our current target or we have another scan on our current target...
        if ((e.getDistance() < target.distance)||(target.name == e.getName()))
		{
				if(e.getVelocity() != 0.0)
				{
					method = 0;
					target.direction = e.getVelocity() * Math.sin(e.getHeadingRadians() - e.getBearingRadians() - getHeadingRadians());
					target.direction /= Math.abs(target.direction);
				}
				else
				{
					method = 1;
				}
				//the next line gets the absolute bearing to the point where the bot is
                double absbearing_rad = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
                //this section sets all the information about our target
                target.name = e.getName();
                target.x = getX()+Math.sin(absbearing_rad)*e.getDistance(); //works out the x coordinate of where the target is
                target.y = getY()+Math.cos(absbearing_rad)*e.getDistance(); //works out the y coordinate of where the target is
                target.bearing = e.getBearingRadians();
                target.head = e.getHeadingRadians();
                target.ctime = getTime();				//game time at which this scan was produced
                target.speed = e.getVelocity();
                target.distance = e.getDistance();
				if (   (target.energy - e.getEnergy()) <= 3
					&& (target.energy - e.getEnergy()) > 0 )
				{
					// il a tiré !			
					//out.println ( "il a tiré: ["+target.x+";"+target.y+"] to ["+getX()+";"+getY()+"] head="+absbearing(target.x,target.y,IAmTheTarget.x,IAmTheTarget.y) );
					// tir de balles virtuelles selon plusieur méthodes
					time = getTime() + (int)(target.distance/(20-(3*(target.energy - e.getEnergy()))));
					// methode simple
					BulletV4 tmpBullet = new BulletV4();
					tmpBullet.x = target.x;
					tmpBullet.y = target.y;
					tmpBullet.time = getTime();
					tmpBullet.speed = 20-(3*(target.energy - e.getEnergy()));
					tmpBullet.head = absbearing(target.x,target.y,IAmTheTarget.guessX(-2),IAmTheTarget.guessY(-2));
					tmpBullet.depX = target.x;	// doit rester sur 
					tmpBullet.depY = target.y;	// le centre du bot
					tmpBullet.method = 2;
					tmpBullet.x = tmpBullet.guessX(getTime()+1);	// pour rejoindre lal balle r�elle
					tmpBullet.y = tmpBullet.guessY(getTime()+1);
					balles.add(tmpBullet);
					/*ptGL = new PointGL();
					ptGL.setPosition(target.x, target.y);
					ptGL.setSize(4.0f);
					ptGL.setColor(new Color(1.0f, 0.0f, 0.0f));
					ballesVirtuellesGL.add(ptGL);
					GLRenderer.getInstance().addRenderElement(ptGL);*/
					// methode avec prediction simple (la mienne)
					tmpBullet = new BulletV4();
					tmpBullet.x = target.x;
					tmpBullet.y = target.y;
					tmpBullet.time = getTime();
					tmpBullet.speed = 20-(3*(target.energy - e.getEnergy()));
					int travelTime = (int)(target.distance/tmpBullet.speed);
					tmpBullet.head = absbearing(target.x ,target.y,IAmTheTarget.guessX(travelTime-2),IAmTheTarget.guessY(travelTime-2));
					tmpBullet.depX = target.x;	// doit rester sur 
					tmpBullet.depY = target.y;	// le centre du bot
					tmpBullet.method = 1;
					tmpBullet.x = tmpBullet.guessX(getTime()+1);	// pour rejoindre la balle r�elle
					tmpBullet.y = tmpBullet.guessY(getTime()+1);
					balles.add(tmpBullet);
					/*ptGL = new PointGL();
					ptGL.setPosition(target.x, target.y);
					ptGL.setColor(new Color(0.0f, 1.0f, 0.0f));
					ptGL.setSize(4.0f);
					ballesVirtuellesGL.add(ptGL);
					GLRenderer.getInstance().addRenderElement(ptGL);*/
				}
				target.energy = e.getEnergy();
				
				if ( target.energy <= 0 )
					mode = 0;	// mode RAM
				else if ( target.energy > 0 )
					mode = 1;
        }

		IAmTheTarget.ctime = 0;//getTime();
	}
	
	public void updateMap ( )
	{
		//init de la map
		for ( int i=0; i<largeurMap; i++ )
		{
			for ( int j=0; j<hauteurMap; j++ )
			{
				map[i][j] = 0;
			}
		}
		for ( int i=0; i<largeurMap; i++ )
		{
			for ( int j=0; j<hauteurMap; j++ )
			{
				if ( i==0 || i==largeurMap-1 || j==0 || j==hauteurMap-1)
				{
					setCoef(i, j, 10, 4);
					setCoef(i, j, 1000, 30);
				}
			}
		}
		
		setCoef((int)largeurMap/2, (int)hauteurMap/2, 4, 4);
		
		// la cible
		int tX;
		int tY;
		
		if ( target.distance < 10000 )
		{
			tX = (int)(largeurMap*target.x/getBattleFieldWidth());
			tY = (int)(hauteurMap*target.y/getBattleFieldHeight());
			if ( mode == 0 ) // RAM
			{
				setCoef(tX, tY, -200, 3);
				setCoef(tX, tY, -40, 1);
			}
			else
			{
				setCoef(tX, tY, 20, 1);
				setCoef(tX, tY, 80, 6);
			}
		}
		
		
		// les balles
		BulletV4 tmpBullet;
		BulletV4 tmpBullet2;
		long deltaTime;
		int i;
		String aPrinter="";
		double nvCoef;
		for ( int k=0; k<balles.size(); k++ )
		{
			tmpBullet = (BulletV4)balles.elementAt(k);
			tmpBullet2 = new BulletV4(tmpBullet);
			tmpBullet2.time = getTime();
			deltaTime = 2;//tmpBullet.time - tmpBullet2.time;
			tX = (int)(largeurMap*tmpBullet.x/getBattleFieldWidth());
			tY = (int)(hauteurMap*tmpBullet.y/getBattleFieldHeight());
			setCoef(tX, tY, 150, 8);
			//aPrinter+="ibullet"+k+" : ["+(int)tmpBullet.x+";"+(int)tmpBullet.y+"] heading : "+tmpBullet.head+"\n";
			
			i=1;
			while ( tmpBullet2.x > 0 && tmpBullet2.x < getBattleFieldWidth() && tmpBullet2.y > 0 && tmpBullet2.y < getBattleFieldHeight())
			{
				tmpBullet2.x = tmpBullet.guessX(getTime()+i*deltaTime);
				tmpBullet2.y = tmpBullet.guessY(getTime()+i*deltaTime);
				tX = (int)(largeurMap*tmpBullet2.x/getBattleFieldWidth());
				tY = (int)(hauteurMap*tmpBullet2.y/getBattleFieldHeight());
				nvCoef = 100-2*i;
				if ( nvCoef < 0 ) nvCoef = 0;
				setCoef(tX, tY, nvCoef, 10);
				i++;
				//aPrinter+="bullet"+k+" : ["+(int)tmpBullet2.x+";"+(int)tmpBullet2.y+"] heading : "+tmpBullet.head+"\n";
			}
		}
		//out.println ( aPrinter );
		/*
		*/
		
	}
	
	public void updateBalles()
	{
		BulletV4 tmpBullet;
		/*PointGL ptGL;*/
		int nbElts = balles.size();
		Point2D tmpPoint = new Point2D();
		Point2D tmpPoint2 = new Point2D(getX(), getY());
		
		// mise a jour de la position des balles des balles
		
		for ( int i=0; i<nbElts; i++ )
		{
			tmpBullet = (BulletV4)balles.elementAt(i);
			tmpBullet.x = tmpBullet.guessX(getTime());
			tmpBullet.y = tmpBullet.guessY(getTime());
			/*ptGL = (PointGL)ballesVirtuellesGL.elementAt(i);
			ptGL.setPosition(tmpBullet.x, tmpBullet.y);*/
			tmpBullet.time = getTime();
		}
		
		// suppression des balles qui sont sorties
		for ( int i=0; i<nbElts; i++ )
		{
			tmpBullet = (BulletV4)balles.elementAt(i);
			/*ptGL = (PointGL)ballesVirtuellesGL.elementAt(i);*/
			if ( tmpBullet.x < 0 || tmpBullet.x > getBattleFieldWidth()
				|| tmpBullet.y < 0 || tmpBullet.y > getBattleFieldHeight() )
			{
				balles.remove(i);
				/*ptGL.remove();
				ballesVirtuellesGL.remove(i);*/
				i--;
				nbElts--;
				continue;
			}
			else
			{
				tmpPoint.x = tmpBullet.depX;
				tmpPoint.y = tmpBullet.depY;
				if ( tmpPoint.distance(new Point2D(tmpBullet.x, tmpBullet.y)) > tmpPoint.distance(tmpPoint2)+30 )
				{
					balles.remove(i);
					/*ptGL.remove();
					ballesVirtuellesGL.remove(i);*/
					i--;
					nbElts--;
					//continue;
				}
			}
		}
		
		
		
		Point2D tmpPt;		
		
		nbElts = vagues.size();
		for ( int i=0; i<nbElts; i++ )
		{
			tmpBullet = (BulletV4)vagues.elementAt(i);
			tmpBullet.x = tmpBullet.guessX(getTime());
			tmpBullet.y = tmpBullet.guessY(getTime());
			/*ptGL = (PointGL)vaguesGL.elementAt(i);
			ptGL.setPosition(tmpBullet.x, tmpBullet.y);*/
			tmpBullet.time = getTime();
		}
		
		// suppression des balles qui sont sorties ou qui ont touché
		for ( int i=0; i<nbElts; i++ )
		{
			tmpBullet = (BulletV4)vagues.elementAt(i);
			/*ptGL = (PointGL)vaguesGL.elementAt(i);*/
			if ( tmpBullet.x < 0 || tmpBullet.x > getBattleFieldWidth()
				|| tmpBullet.y < 0 || tmpBullet.y > getBattleFieldHeight() )
			{
				data[tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data[tmpBullet.distanceIndex][tmpBullet.indexOnWave], 0, 0.05 );
				data1[tmpBullet.enemySpeed][tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data1[tmpBullet.enemySpeed][tmpBullet.distanceIndex][tmpBullet.indexOnWave], 0, 0.05 );
				data1[tmpBullet.relativeHeadingIndex][tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data1[tmpBullet.relativeHeadingIndex][tmpBullet.distanceIndex][tmpBullet.indexOnWave], 0, 0.05 );
				valuesNumber++;
				vagues.remove(i);
				/*ptGL.remove();
				vaguesGL.remove(i);*/
				i--;
				nbElts--;
			}
			tmpPt = new Point2D(tmpBullet.x, tmpBullet.y);
			if ( tmpPt.distance(new Point2D(target.x, target.y)) < 25 )
			{
				// touché
				data[tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data[tmpBullet.distanceIndex][tmpBullet.indexOnWave], 1, 0.05 );
				data1[tmpBullet.enemySpeed][tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data1[tmpBullet.enemySpeed][tmpBullet.distanceIndex][tmpBullet.indexOnWave], 1, tmpBullet.weight );
				data1[tmpBullet.relativeHeadingIndex][tmpBullet.distanceIndex][tmpBullet.indexOnWave] = rollingAvg(data1[tmpBullet.relativeHeadingIndex][tmpBullet.distanceIndex][tmpBullet.indexOnWave], 1, tmpBullet.weight );
				valuesNumber++;
				vagues.remove(i);
				/*ptGL.remove();
				vaguesGL.remove(i);*/
				i--;
				nbElts--;
			}
		}
	}
	
	void doFire()
	{
		double poid;
		
		BulletV4 tmpBullet;
		/*PointGL ptGL;*/
		if ( getGunHeat() == 0 && getEnergy() > firePower && target.distance < 1000)
			poid=0.06;
		else
			poid=0.01;
		{
			if ( method == 0 )
			{
				//lancement d'une vague
				int distanceIndex = (int)Math.round(((Math.sqrt(target.distance * (20-(3*firePower)) / 800.0)))*SEG_DIST/6);
				for ( int i=0; i<dataLength; i++ )
				{					
					tmpBullet = new BulletV4();
					tmpBullet.x = getX();;
					tmpBullet.y = getY();
					tmpBullet.time = getTime();
					tmpBullet.speed = 20-(3*firePower);
					tmpBullet.head = 
									absbearing(	  getX()
												, getY()
												, target.x
												, target.y)
									 + target.direction * (((i - (double)(dataLength-1)/2) / (double)(dataLength-1)*4/4));
					tmpBullet.distanceIndex = distanceIndex;
					tmpBullet.enemySpeed = (int)Math.abs(((Math.max(target.speed-0.01,0)*SEG_ESPEED)/8.0));
					tmpBullet.relativeHeadingIndex = (int)Math.abs((NormaliseBearing(Math.atan2(getX()-target.x, getY()-target.y) - target.head))*(double)SEG_RELHEAD/(PI));
					tmpBullet.indexOnWave = i;
					tmpBullet.weight = poid;
					vagues.add(tmpBullet);
					
					if ( valuesNumber < LEVEL1 )
					{
						if(data[distanceIndex][i] > data[distanceIndex][bestAngularOffset])
							bestAngularOffset = i;
					}
					else
					{
						if(data1[tmpBullet.relativeHeadingIndex][distanceIndex][i] > data1[tmpBullet.relativeHeadingIndex][distanceIndex][bestAngularOffset])
							bestAngularOffset = i;
					}
							
					/*ptGL = new PointGL();
					ptGL.setPosition(tmpBullet.x, tmpBullet.y);
					ptGL.setSize(4.0f);
					ptGL.setColor(new Color(Color.HSBtoRGB((float)(.7 - Math.min(.7, 2.5 * data[distanceIndex][i])),1.0f,1.0f)));
					vaguesGL.add(ptGL);
					GLRenderer.getInstance().addRenderElement(ptGL);*/
				}
			}
			//setFire(firePower);
		}
	}
	
	// ancienne valeur, nouvelle a ajouter, nb de vals, poid de la nouvelle
	static double rollingAvg(double value, double newEntry, double weighting )
	{
    	return (value+newEntry*weighting)/(1+weighting);
	} 

	private void goTo(Point2D point)
	{
		Point2D location = new Point2D(getX(),getY());
		double distance = location.distance(point);
		double angle = robocode.util.Utils.normalRelativeAngle(location.absbearing(point) - getHeadingRadians());
		//out.println ( "de "+getX()+","+getY()+" a "+point.getX()+","+point.getY()+" angle:"+angle+" abs:"+Math.abs(angle));
		if (Math.abs(angle) > PI/2)
		{
			distance *= -1.0;
			if (angle > 0.0)
			{
				angle -= PI;
			}
			else
			{
				angle += PI;
			}
		}
		setTurnRightRadians(angle);
		setAhead(distance);
	}

	public void onRobotDeath(RobotDeathEvent e) {
        if (e.getName() == target.name)
        target.distance = 10000; //this will effectively make it search for a new target
	}
	
	public void onWin(WinEvent e)
	{
		//stats.winRounds++;
		stop();
		while ( (getX() - getBattleFieldWidth()/2) > 10 || (getY() - getBattleFieldHeight()/2) > 10 )
		{
			goTo(new Point2D(getBattleFieldWidth()/2,getBattleFieldHeight()/2));
			execute();
		}
		turnLeft(getHeading());
		turnGunLeft(getGunHeading());
		turnRadarLeft(getRadarHeading());
		while ( true )
		{
			setTurnRight(10);
			setAhead(1000);
			execute();
			setTurnLeft(10);
			setAhead(-1000);
			execute();
		}
	}
	
	void doMovement()
	{
		//bou k'cé moche :p		
		Vector bonnesPos = new Vector();
		
		/*constante*/int largeurRecherche = 1;
		
		int tX = (int)(largeurMap*getX()/getBattleFieldWidth());
		int tY = (int)(hauteurMap*getY()/getBattleFieldHeight());
		
		double min = 1000;
		
		for ( int i=-largeurRecherche; i<largeurRecherche+1; i++ )
		{
			for ( int j=-largeurRecherche; j<largeurRecherche+1; j++ )
			{
				if ( i+tX>=0 && i+tX<largeurMap && j+tY>=0 && j+tY<hauteurMap )
				{
					bonnesPos.add(new Case(i+tX, j+tY, map[i+tX][j+tY]));
					if ( map[i+tX][j+tY] < min )
						min = map[i+tX][j+tY];
				}
			}
		}
				
		Case tmpCase;
		int nbElts=bonnesPos.size();
		int nbCases;
		
		double min2 = 1000;
		
		for ( int k=0; k<nbElts; k++ )
		{
			tmpCase = (Case)bonnesPos.elementAt(k);
			if ( tmpCase.getCoef() == min )
			{
				// on calcule un nouveau coef basé sur les cases allentour
				tmpCase.setCoef(0);
				nbCases=0;
				for ( int i=-1; i<2; i++ )
				{
					for ( int j=-1; j<2; j++ )
					{
						if ( i+tmpCase.getX()>=0 && i+tmpCase.getX()<largeurMap && j+tmpCase.getY()>=0 && j+tmpCase.getY()<hauteurMap )
						{
							nbCases++;
							tmpCase.setCoef(tmpCase.getCoef()+map[i+tmpCase.getX()][j+tmpCase.getY()]);
						}
					}
				}
				if ( tmpCase.getCoef() < min2 )
					min2 = tmpCase.getCoef();
				tmpCase.setCoef(tmpCase.getCoef()/nbCases);
			}
			else
			{
				// on erase
				bonnesPos.remove(k);
				k--;
				nbElts--;
			}
		}
				
		// a changer
		min = 1000;
		Case destination = new Case(5,5); // t content javac? grr
		for ( int i=0; i<bonnesPos.size(); i++ )
		{
			tmpCase = (Case)bonnesPos.elementAt(i);
			if ( tmpCase.getCoef() < min )
			{
				min = tmpCase.getCoef();
				destination = tmpCase;
			}
		}
		
		// si on est Dja a la maison, on se place perpendiculairement � l'ot
		if ( (int)destination.getX() == (int)(hauteurMap*getX()/getBattleFieldHeight())
			&& (int)destination.getY() == (int)(hauteurMap*getY()/getBattleFieldHeight()) )
		{
			double angle = robocode.util.Utils.normalRelativeAngle((new Point2D(target.x, target.y)).absbearing(new Point2D(getX(), getY())) + PI/2 - getHeadingRadians());
			if (Math.abs(angle) > PI/2)
			{
				if (angle > 0.0)
				{
					angle -= PI;
				}
				else
				{
					angle += PI;
				}
			}
		setTurnRightRadians(angle);
		}
		else
		{
			goTo(new Point2D(getBattleFieldWidth()*(destination.getX()+0.5)/largeurMap,
						getBattleFieldHeight()*(destination.getY()+0.5)/hauteurMap));
		}
		
	}
	
	void doFirePower() {
        //firePower = 3*(200/target.distance)*max(getEnergy()+50, 100)/100; //selects a bullet power based on our distance away from the target
		//if ( firePower > 3 ) firePower = 3;
		if ( getEnergy() > 0.1 )
			firePower = (5/target.distance)*max(getEnergy()+50, 100);
		else
			firePower = 0;
		//firePower = (5/target.distance)*max(getEnergy()+50, 100);
	}
	
	double max ( double nb, double max )
	{
		if ( nb > max )
			return max;
		return nb;
	}
	
	void doScanner() {
        double radarOffset;
		double radarHeading;
        if (getTime() - target.ctime > 4) { 	//if we haven't seen anybody for a bit....
                radarOffset = 360;		//rotate the radar to find a target
        } else {
                //next is the amount we need to rotate the radar by to scan where the target is now
                radarOffset = angle ( getRadarHeadingRadians(), absbearing(getX(),getY(),target.x,target.y));

                //this adds or subtracts small amounts from the bearing for the radar
                //to produce the wobbling and make sure we don't lose the target
                if (radarOffset < 0)
                	radarOffset -= PI/16;
                else
                	radarOffset += PI/16;
        }
        //turn the radar
        setTurnRadarLeftRadians(NormaliseBearing(radarOffset));
	}
	
	double angle ( double a1, double a2 )
	{
		double dist = a1-a2;
		if ( dist > PI ) dist -= 2*PI;
		if ( dist < -PI ) dist += 2*PI;
		
		return dist;
	}
	
	void doGun()
	{
		
		long time;
		double gunOffset;
		
		switch ( method )
		{
			case 1:
				// meilleure estimation de la position future de l'enemi sans iteration complikée
				// temps que la balle va mettre a arriver
				time = getTime() + (int)(target.distance/(20-(3*firePower)));
				// de combien va devoir tourner le gun pour etre ds la bonne position
				gunOffset = angle (getGunHeadingRadians(), absbearing(getX(),getY(),target.guessX(time),target.guessY(time)));
				setTurnGunLeftRadians(NormaliseBearing(gunOffset));
				break;
			case 0:
				int distanceIndex = (int)Math.round((Math.sqrt(target.distance / 40.0)));
				setTurnGunRightRadians( Math.sin(target.bearing
										+ getHeadingRadians()
										- getGunHeadingRadians()
										+ (target.direction / Math.abs(target.direction))
										* ((bestAngularOffset - (double)(dataLength-1)/2) / (double)(dataLength-1)*3/4)));
		}
	
	}


	/*
	* This set of helper methods.  You may find several of these very useful
	* They include the ability to find the angle to a point.
	*/
	
	//if a bearing is not within the -pi to pi range, alters it to provide the shortest angle
	double NormaliseBearing(double ang) {
		if (ang > PI)
	    ang -= 2*PI;
	    if (ang < -PI)
	    ang += 2*PI;
	    return ang;
	}

	//if a heading is not within the 0 to 2pi range, alters it to provide the shortest angle
	double NormaliseHeading(double ang) {
        if (ang > 2*PI)
        ang -= 2*PI;
        if (ang < 0)
        ang += 2*PI;
        return ang;
	}

	//returns the distance between two x,y coordinates
	public double getrange( double x1,double y1, double x2,double y2 )
	{
        double xo = x2-x1;
        double yo = y2-y1;
        double h = Math.sqrt( xo*xo + yo*yo );
        return h;
	}

	//gets the absolute bearing between to x,y coordinates
	public double absbearing( double x1,double y1, double x2,double y2 )
	{
        double xo = x2-x1;
        double yo = y2-y1;
        double h = getrange( x1,y1, x2,y2 );
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
                return 2.0*Math.PI - Math.asin( -xo / h );
        }
        return 0;
	}
	
	void setCoef(int pX, int pY, double pCoef)
	{
		setCoef(pX, pY, pCoef, -1);
	}
	
	void setCoef(int pX, int pY, double pCoef, int pFracMap)
	{
		/*constante*/ int fracMap = 6;	// 1/fracMap sera coeficienté
		if ( pFracMap > 0 ) fracMap = pFracMap;
		Point2D ptOrig = new Point2D(0,0);		
		double distMax = ptOrig.distance(new Point2D((getBattleFieldHeight()/fracMap)/(getBattleFieldHeight()/hauteurMap),
													(getBattleFieldWidth()/fracMap)/(getBattleFieldWidth()/largeurMap)));
		ptOrig = new Point2D(pX, pY);
		
		double coef;
		
		for ( int i=(int)-distMax; i<(int)distMax+1; i++ )
		{
			for ( int j=(int)-distMax; j<(int)distMax+1; j++ )
			{
				if ( i+pX>=0 && i+pX<largeurMap && j+pY>=0 && j+pY<hauteurMap )
				{
					//if ( i!=0 || j!=0 )
					{
						coef = distMax-ptOrig.distance(new Point2D(pX+i, pY+j));
						if ( coef < 0 ) coef = 0;
						coef = (coef/distMax);
						map[pX+i][pY+j] += coef*pCoef;
					}
				}
			}
		}
	}
	
	public void onPaint(java.awt.Graphics2D g)
	{
		BulletV4 tmpBullet;
		
		g.setColor(Color.green);
		g.drawString("targetSpeed:"+target.speed,20, 30);
		g.drawString("targetHeading:"+(target.head*360/(2*PI)),20, 40);
		g.drawString("targetBearing:"+(target.bearing*360/(2*PI)),20, 50);
		if ( ((int)((NormaliseBearing(Math.atan2(getX()-target.x, getY()-target.y) - target.head))*SEG_RELHEAD/PI)) < 0 )
			g.drawString ( "ATTENTION!!!!!", 200, 60 );
		g.drawString("Relative heading:"+((int)((NormaliseBearing(Math.atan2(getX()-target.x, getY()-target.y) - target.head))*SEG_RELHEAD/(PI))), 20, 60);
		g.drawString("valuesNumber:"+valuesNumber, 20, 70);
		g.drawString("using dataset:"+(valuesNumber>LEVEL1?1:0), 20, 80);
		
		for ( int i=0; i<balles.size(); i++)
		{
			tmpBullet = (BulletV4)balles.elementAt(i);
			
			g.setColor(Color.green);			
		
			switch ( tmpBullet.method )
			{
				case 0:
					// GF
					g.setColor(new Color(Color.HSBtoRGB((float)(.7 - Math.min(.7, 2.5 * data[tmpBullet.distanceIndex][i])),1.0f,1.0f)));
					break;
				case 1:
					g.setColor(Color.green);
					// lineaire
					break;
				case 2:
					g.setColor(Color.red);
					// simple
					break;
			}
			g.fillOval((int)(tmpBullet.x-2),(int)(600-tmpBullet.y-2),4,4);
		}

		for ( int i=0; i<vagues.size(); i++)
		{
			tmpBullet = (BulletV4)vagues.elementAt(i);
			switch ( tmpBullet.method )
			{
				case 0:
					// GF
					//g.setColor(new Color(Color.HSBtoRGB((float)(.7 - Math.min(.7, 2.5 * data[tmpBullet.distanceIndex][i])),1.0f,1.0f)));
					break;
				case 1:
					g.setColor(Color.green);
					// lineaire
					break;
				case 2:
					g.setColor(Color.red);
					// simple
					break;
			}
			g.fillOval((int)(tmpBullet.x-1),(int)(600-tmpBullet.y-1),1,1);
		}
    }
}



class EnemyV4
{
        String name;
        public double bearing;
        public double head;		// heading de l'enemi
        public long ctime;      // moment ou le scan a été fait
        public double speed;
        public double x,y;
        public double distance;
		public double energy;
		public double direction;
		
        public double guessX(long when)
        {
                long diff = when - ctime;
                return x+Math.sin(head)*speed*diff;
        }
        public double guessY(long when)
        {
                long diff = when - ctime;
                return y+Math.cos(head)*speed*diff;
        }

}

class BulletV4
{
	public double x,y;
	public double depX, depY;
	public long time;
	public double head;
	public double speed;
	public double weight;
	
	public int distanceIndex;
	public int enemySpeed;
	public int relativeHeadingIndex;
	
	public int indexOnWave;
	public int method;
	
	
	public BulletV4()
	{
	}
	
	public BulletV4(BulletV4 pBul)
	{
		x=pBul.getX();
		y=pBul.getY();
		time=pBul.getTime();
		head=pBul.getHead();
		speed=pBul.getSpeed();
	}
	
	double getX() { return x; }
	double getY() { return y; }
	
	long getTime() { return time; }

	double getHead() { return head; }
		
	double getSpeed() { return speed; }
		
	double getDist(double pX, double pY)
	{
		double xo = pX-x;
        double yo = pY-y;
        double h = Math.sqrt( xo*xo + yo*yo );
        return h;
	}
	
	public double guessX(long when)
	{
		long diff = when - time;
		return x+Math.sin(head)*speed*diff;
	}

	public double guessY(long when)
	{
		long diff = when - time;
		return y+Math.cos(head)*speed*diff;
	}
}


class Point2D
{
	public Point2D( double X, double Y )
	{
		x=X;
		y=Y;
	}
	public Point2D(){}
	
	public void setLocation ( double X, double Y )
	{
		x=X;
		y=Y;
	}
	
	public void setLocation ( Point2D thePoint )
	{
		x=thePoint.getX();
		y=thePoint.getY();
	}
	
	public double distance( Point2D thePoint )
	{
        double xo = thePoint.getX()-this.getX();
        double yo = thePoint.getY()-this.getY();
        double h = Math.sqrt( xo*xo + yo*yo );
        return h;
	}
		
	//gets the absolute bearing between to x,y coordinates
	public double absbearing( Point2D thePoint )
	{
		double xo = thePoint.getX()-getX();
        double yo = thePoint.getY()-this.getY();
        double h = distance ( thePoint );
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
                return 2.0*Math.PI - Math.asin( -xo / h );
        }
        return 0;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	double x;
	double y;
}

class Case
{
	private int x, y;
	private double coef;
	
	public Case(int pX, int pY)
	{
		this(pX, pY, 0);
	}
	
	public Case(int pX, int pY, double pCoef)
	{
		x = pX;
		y = pY;
		coef = pCoef;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public double getCoef() { return coef; }
		
	public void setX(int pX) { x = pX; }
	public void setY(int pY) { y = pY; }
	public void setCoef(double pCoef) { coef = pCoef; }
}