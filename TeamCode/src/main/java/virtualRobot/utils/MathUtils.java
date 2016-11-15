package virtualRobot.utils;

/**
 * Created by 17osullivand on 9/26/16.
 * Various mathematical tools.
 */
public class MathUtils {
    private static final double epsilon = 0.01;
    private static final double epsilonSquared = 0.001;

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
    public static boolean equals(double d, double e) {
        return equals(d,e,epsilon);
    }
    public static boolean equals(double d, double e, double tolerance) {
        return Math.abs(d-e) < tolerance;
    }
}
