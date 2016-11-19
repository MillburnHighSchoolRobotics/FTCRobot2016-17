package virtualRobot;

import java.util.ArrayList;
import java.util.HashMap;

import virtualRobot.components.ColorSensor;
import virtualRobot.components.ContinuousRotationServo;
import virtualRobot.components.StateSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;
import virtualRobot.components.StateSensor;
import virtualRobot.components.SyncedMotors;
import virtualRobot.components.UltrasonicSensor;

/**
 * Created by DOSullivan on 11/18/2015.
 * This interface represents the robot when used in autonomous
 */
public interface AutonomousRobot {
//Motors, sensors, servos will be added here e.g. Motor getMotor();

    Sensor getHeadingSensor();

    Sensor getPitchSensor();

    Sensor getRollSensor();

    ColorSensor getColorSensor();

    StateSensor getStateSensor();

    UltrasonicSensor getSonarLeft();

    UltrasonicSensor getSonarRight();

    Motor getLFMotor();

    Motor getLBMotor();

    Motor getRFMotor();

    Motor getRBMotor();

//    Motor getReaperMotor();

    Servo getButtonServo();

    Servo getCapLeftServo();

    Servo getCapRightServo();

    Servo getBallLauncherServo();

    Sensor getLFEncoder();

    Sensor getLBEncoder();

    Sensor getRFEncoder();

    Sensor getRBEncoder();

//    Sensor getReaperEncoder();

    Sensor getLightSensor1();

    Sensor getLightSensor2();

    Sensor getLightSensor3();

    Sensor getLightSensor4();

    SyncedMotors getRightRotate();

    SyncedMotors getLeftRotate();

    void addToProgress(String s);

    void stopMotors();

    ArrayList<String> getProgress();

    void addToTelemetry(String s, Object arg);

    HashMap<String, Object> getTelemetry ();
}
