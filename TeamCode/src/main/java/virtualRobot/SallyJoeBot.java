package virtualRobot;

import java.util.ArrayList;

import virtualRobot.components.ColorSensor;
import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;
import virtualRobot.components.SyncedMotors;

/**
 * Created by DOSullivan on 9/14/16.
 */
public class SallyJoeBot implements AutonomousRobot, TeleopRobot {
    //Motors, sensors, servos referenced (e.g. private Motor...)
    private Sensor headingSensor, pitchSensor, rollSensor;
    private JoystickController joystickController1, joystickController2;
    private Sensor LFEncoder, LBEncoder, RFEncoder, RBEncoder;
    private LocationSensor locationSensor;
    private ColorSensor colorSensor;
    private ArrayList<String> robotProgress;
    private Motor LFMotor, LBMotor, RFMotor, RBMotor;
    private Motor Reaper;
    private Servo CapLeft, CapRight;

    //Motors, sensors, servos instantiated (e.g Motor = new Motor(), some positions can also be set if desired
    public SallyJoeBot() {

        headingSensor = new Sensor();
        pitchSensor = new Sensor();
        rollSensor = new Sensor();
        robotProgress = new ArrayList<String>();
        locationSensor = new LocationSensor();
        LFMotor = new Motor();
        LBMotor = new Motor();
        RFMotor = new Motor();
        RBMotor = new Motor();
        Reaper = new Motor();
        LFEncoder = new Sensor();
        LBEncoder = new Sensor();
        RFEncoder = new Sensor();
        RBEncoder = new Sensor();
        CapLeft = new Servo();
        colorSensor = new ColorSensor();
        CapRight = new Servo();
    }
    //All of Autonomous and TeleopRobot's functions are created e.g. (public synchronized Motor getMotor() {return Motor;}

    @Override
    public synchronized Sensor getHeadingSensor() {
        return headingSensor;
    }

    @Override
    public synchronized Sensor getPitchSensor() {
        return pitchSensor;
    }

    @Override
    public synchronized Sensor getRollSensor() {
        return rollSensor;
    }

    public synchronized Sensor getLFEncoder() { return LFEncoder; }

    public synchronized Sensor getLBEncoder() { return LBEncoder;}

    public synchronized Sensor getRFEncoder() { return RFEncoder;}

    public synchronized Sensor getRBEncoder () {return RBEncoder;}


    public synchronized Motor getLFMotor() { return LFMotor; }

    public synchronized Motor getLBMotor() { return LBMotor; }

    public synchronized Motor getRFMotor() { return RFMotor; }

    public synchronized Motor getRBMotor() { return RBMotor; }

    public synchronized Motor getReaperMotor() { return Reaper; }

    public synchronized Servo getCapLeft() { return CapLeft; }

    public synchronized Servo getCapRight() { return CapRight; }

    public synchronized LocationSensor getLocationSensor() {
        return locationSensor;
    }

    public synchronized ColorSensor getColorSensor() {
        return colorSensor;
    }

    public synchronized JoystickController getJoystickController1() {
        return joystickController1;
    }

    public synchronized JoystickController getJoystickController2() {
        return joystickController2;
    }

    public synchronized void addToProgress (String s) {
        robotProgress.add(s);
    }

    public synchronized ArrayList<String> getProgress () {
        return robotProgress;
    }




}
