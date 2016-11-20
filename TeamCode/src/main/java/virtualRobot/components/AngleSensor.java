package virtualRobot.components;

/**
 * Created by ethachu19 on 11/19/2016.
 */

public class AngleSensor extends Sensor {
    double prevAngle = 0;

    public double getValue() {
        if (Math.abs(hardValue - prevAngle) > 200) {
            if (hardValue < prevAngle) {
                offset += 360;
            }

            if (hardValue > prevAngle) {
                offset -= 360;
            }
        }

        prevAngle = hardValue;
        return hardValue + offset;
    }
}
