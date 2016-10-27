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
 * Created by DOSullivan on 9/14/16.
 */
public class SallyJoeBot implements AutonomousRobot, TeleopRobot {
    //Motors, sensors, servos referenced (e.g. private Motor...)
    private Sensor headingSensor, pitchSensor, rollSensor, sonarLeft, lightSensor, sonarRight;
    private JoystickController joystickController1, joystickController2;
    private Sensor LFEncoder, LBEncoder, RFEncoder, RBEncoder;
    private LocationSensor locationSensor;
    private ArrayList<String> robotProgress;
    private HashMap<String, Object> telemetry;
    private Motor LFMotor, LBMotor, RFMotor, RBMotor;
    private Motor Reaper;
    private ContinuousRotationServo CapServo;
    private Servo ButtonServo;
    private SyncedMotors leftRotate, rightRotate;
    private static final double KP = 0.0001; //TBD
    private static final double KI = 0.0001; //TBD
    private static final double KD = 0.0001; //TBD
    public static final double BWTHRESHOLD = 2; //B+W/2 = 3.01

    //Motors, sensors, servos instantiated (e.g Motor = new Motor(), some positions can also be set if desired
    public SallyJoeBot() {

        joystickController1 = new JoystickController();
        joystickController2 = new JoystickController();
        headingSensor = new Sensor();
        pitchSensor = new Sensor();
        rollSensor = new Sensor();
        sonarLeft = new Sensor();
        sonarRight = new Sensor();
        lightSensor = new Sensor();
        robotProgress = new ArrayList<String>();
        telemetry = new HashMap<>();
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
        CapServo = new ContinuousRotationServo();
        ButtonServo = new Servo();

        leftRotate = new SyncedMotors(LFMotor, LBMotor, LFEncoder, LBEncoder, KP, KI, KD, SyncedMotors.SyncAlgo.POSITION);
        rightRotate = new SyncedMotors(RFMotor, RBMotor, RFEncoder, RBEncoder, KP, KI, KD, SyncedMotors.SyncAlgo.POSITION);

        leftRotate.setRatio(1);
        rightRotate.setRatio(1);

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
    public synchronized Sensor getSonarLeft(){return sonarLeft;}

    @Override
    public synchronized Sensor getSonarRight(){return sonarRight;}

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

    @Override
    public synchronized ContinuousRotationServo getCapServo() { return CapServo; }

    @Override
    public synchronized Servo getButtonServo() { return ButtonServo; }

    @Override
    public synchronized LocationSensor getLocationSensor() {
        return locationSensor;
    }

    @Override
    public synchronized Sensor getLineSensor() {
        return lightSensor;
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

    @Override
    public synchronized void addToTelemetry(String s, Object arg) { telemetry.put(s,arg); }

    @Override
    public synchronized HashMap<String, Object> getTelemetry () { return telemetry; }

}
