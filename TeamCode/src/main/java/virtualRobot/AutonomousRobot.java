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

    Motor getLFMotor();

    Motor getLBMotor();

    Motor getRFMotor();

    Motor getRBMotor();

    Motor getReaperMotor();

    Servo getCapServo();

    Sensor getLFEncoder();

    Sensor getLBEncoder();

    Sensor getRFEncoder();

    Sensor getRBEncoder();

    Sensor getLineSensor();

    SyncedMotors getRightRotate();

    SyncedMotors getLeftRotate();

    void addToProgress(String s);

    ArrayList<String> getProgress();
}
