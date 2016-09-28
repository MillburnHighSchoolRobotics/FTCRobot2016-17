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
    public static double clamp(int number, int lowerBound, int upperBound) {
    	Math.max(lowerBound, Math.min(upperBound, number));
    }
}
