package virtualRobot;

import java.util.ArrayList;

import virtualRobot.components.ColorSensor;
import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;

/**
 * Created by DOSullivan on 11/18/2015.
 */
public interface AutonomousRobot {
//Motors, sensors, servos will be added here e.g. Motor getMotor();

    Sensor getHeadingSensor();

    Sensor getPitchSensor();

    Sensor getRollSensor();

    LocationSensor getLocationSensor();

    void addToProgress(String s);

    ArrayList<String> getProgress();
}
