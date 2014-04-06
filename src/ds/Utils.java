package ds;

public class Utils
{
	public static double rollingAvg(double value, double newEntry, double n, double weighting )
	{
	    return (value * n + newEntry * weighting)/(n + weighting);
	} 
}
