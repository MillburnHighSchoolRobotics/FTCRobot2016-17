package virtualRobot;

import java.util.ArrayList;
import java.util.HashMap;

import virtualRobot.components.ColorSensor;
import virtualRobot.components.ContinuousRotationServo;
import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;
import virtualRobot.components.SyncedMotors;

/**
 * Created by DOSullivan on 11/18/2015.
 */
public interface AutonomousRobot {
//Motors, sensors, servos will be added here e.g. Motor getMotor();

    Sensor getHeadingSensor();

    Sensor getPitchSensor();

    Sensor getRollSensor();

    LocationSensor getLocationSensor();

    Sensor getSonarLeft();

    Sensor getSonarRight();

    Motor getLFMotor();

    Motor getLBMotor();

    Motor getRFMotor();

    Motor getRBMotor();

    Motor getReaperMotor();

    ContinuousRotationServo getCapServo();

    Servo getButtonServo();

    Sensor getLFEncoder();

    Sensor getLBEncoder();

    Sensor getRFEncoder();

    Sensor getRBEncoder();

    Sensor getLineSensor();

    SyncedMotors getRightRotate();

    SyncedMotors getLeftRotate();

    void addToProgress(String s);

    void stopMotors();

    ArrayList<String> getProgress();

    void addToTelemetry(String s, Object arg);

    HashMap<String, Object> getTelemetry ();
}
