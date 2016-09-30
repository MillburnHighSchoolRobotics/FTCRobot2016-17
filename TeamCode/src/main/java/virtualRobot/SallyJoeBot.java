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
    private Servo CapServo;
    private SyncedMotors leftRotate, rightRotate;
    private static final KP = 0; //TBD
    private static final KI = 0; //TBD
    private static final KD = 0; //TBD

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
        CapServo = new Servo();
        colorSensor = new ColorSensor();

        leftRotate = new SyncedMotors(LFMotor, LBMotor, LFEncoder, LBEncoder, KP, KI, KD);
        rightRotate = new SyncedMotors(RFMotor, RBMotor, RFEncoder, RBEncoder, KP, KI, KD);

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

    @Override
    public synchronized Sensor getLFEncoder() { return LFEncoder; }

    @Override
    public synchronized Sensor getLBEncoder() { return LBEncoder;}

    @Override
    public synchronized Sensor getRFEncoder() { return RFEncoder;}

    @Override
    public synchronized Sensor getRBEncoder () {return RBEncoder;}

    @Override
    public synchronized Motor getLFMotor() { return LFMotor; }

    @Override
    public synchronized Motor getLBMotor() { return LBMotor; }

    @Override
    public synchronized Motor getRFMotor() { return RFMotor; }

    @Override
    public synchronized Motor getRBMotor() { return RBMotor; }

    @Override
    public synchronized Motor getReaperMotor() { return Reaper; }

    public synchronized Servo getCapServo() { return CapServo; }

    @Override
    public synchronized LocationSensor getLocationSensor() {
        return locationSensor;
    }

    @Override
    public synchronized Sensor getLineSensor() {
        return colorSensor;
    }

    @Override
    public synchronized SyncedMotors getRightRotate() {
        return rightRotate;
    }

    @Override
    public synchronized SyncedMotors getLeftRotate() {
        return leftRotate;
    }

    @Override
    public synchronized JoystickController getJoystickController1() {
        return joystickController1;
    }

    @Override
    public synchronized JoystickController getJoystickController2() {
        return joystickController2;
    }

    @Override
    public synchronized void addToProgress (String s) {
        robotProgress.add(s);
    }

    @Override
    public synchronized ArrayList<String> getProgress () {
        return robotProgress;
    }




}
