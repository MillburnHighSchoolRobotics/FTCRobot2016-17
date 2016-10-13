package virtualRobot.utils;

/**
 * Created by 17osullivand on 9/26/16.
 */
public class MathUtils {

    public static double sinDegrees(double  d) {
        return Math.sin(Math.toRadians(d));
    }
    public static double cosDegrees(double  d) {
        return Math.cos(Math.toRadians(d));
    }
    public static double clamp(double number, double lowerBound, double upperBound) {
    	return Math.max(lowerBound, Math.min(upperBound, number));
    }
    public static double truncate(double d, int place) {
        return (double) ((int)(d * place)) / place;
    }
}
