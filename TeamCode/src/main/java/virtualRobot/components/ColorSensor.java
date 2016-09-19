package virtualRobot.components;

/**
 * Created by shant on 12/10/2015.
 */
public class ColorSensor extends Sensor {

    public double getRed() {
        int color = (int) getValue();
        return (color & 0x00FF0000) >> 16;
    }

    public double getBlue() {
        int color = (int) getValue();
        return (color & 0x000000FF);
    }

    public double getGreen() {
        int color = (int) getValue();
        return (color & 0x0000FF00) >> 8;
    }

    public double getAlpha() {
        int color = (int) getValue();
        return (color & 0xFF000000) >> 24;
    }
}
