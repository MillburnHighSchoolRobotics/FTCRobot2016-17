package virtualRobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import virtualRobot.components.AxisSensor;
import virtualRobot.components.ColorSensor;
import virtualRobot.components.ContinuousRotationServo;
import virtualRobot.components.StateSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;
import virtualRobot.components.SyncedMotors;
import virtualRobot.components.UltrasonicSensor;

/**
 * Created by DOSullivan on 9/14/16.
 * All of our our virtual components and there getters are housed in SallyJoeBot
 */
public class SallyJoeBot implements AutonomousRobot, TeleopRobot {
    //Motors, sensors, servos referenced (e.g. private Motor...)
    private Sensor nxtlightSensor1, nxtlightSensor2, nxtlightSensor3, nxtlightSensor4;
    private Sensor headingSensor, pitchSensor, rollSensor;
    private Sensor voltageSensor;
    private AxisSensor rawAccel, worldAccel;
    private ColorSensor colorSensor;
    private UltrasonicSensor sonarLeft, sonarRight;
    private JoystickController joystickController1, joystickController2;
    private Sensor LFEncoder, LBEncoder, RFEncoder, RBEncoder;
    private Sensor LiftLeftEncoder, LiftRightEncoder;
    private Sensor ReaperEncoder, FlywheelEncoder;
    private StateSensor stateSensor;
    private ArrayList<String> robotProgress;
    private ConcurrentHashMap<String, Object> telemetry;
    private Motor LFMotor, LBMotor, RFMotor, RBMotor;
    private Motor LiftLeftMotor, LiftRightMotor;
    private Motor Reaper, Flywheel;
    private Servo ButtonServo;
    private ContinuousRotationServo ClawLeft, ClawRight;
    private Servo FlywheelStopper;
    private SyncedMotors leftRotate, rightRotate;
    private SyncedMotors capLift;
    public double initialBattery;
    private static final double KP = 0.0001; //TBD
    private static final double KI = 0.0001; //TBD
    private static final double KD = 0.0001; //TBD
    private static final float mmPerInch = 25.4f;
    public static final float mmBotWidth = 16 * mmPerInch; // ... or whatever is right for your robot
    public static final float mmFTCFieldWidth = (12 * 12 - 2) * mmPerInch; // the FTC field is ~11'10" center-to-center of the glass panels
    public static final double wheelDiameter = 10; // in cm
    public static final double botWidth = 40, botLength = 32.5 ; // in cm
    public static final double BWTHRESHOLD = 3.7; //B+W/2 = 3.01

    //Motors, sensors, servos instantiated (e.g Motor = new Motor(), some positions can also be set if desired
    public SallyJoeBot() {
        rawAccel = new AxisSensor();
        worldAccel = new AxisSensor();
        joystickController1 = new JoystickController();
        joystickController2 = new JoystickController();
        headingSensor = new Sensor();
        pitchSensor = new Sensor();
        rollSensor = new Sensor();
        voltageSensor = new Sensor();
        colorSensor = new ColorSensor();
        nxtlightSensor1 = new Sensor();
        nxtlightSensor2 = new Sensor();
        nxtlightSensor3 = new Sensor();
        nxtlightSensor4 = new Sensor();
        sonarLeft = new UltrasonicSensor();
        sonarRight = new UltrasonicSensor();
        robotProgress = new ArrayList<String>();
        telemetry = new ConcurrentHashMap<>();
        stateSensor = new StateSensor();
        LFMotor = new Motor(Motor.MotorType.NeveRest40);
        LBMotor = new Motor(Motor.MotorType.NeveRest40);
        RFMotor = new Motor(Motor.MotorType.NeveRest40);
        RBMotor = new Motor(Motor.MotorType.NeveRest40);
        LiftLeftMotor = new Motor(Motor.MotorType.NeveRest60);
        LiftRightMotor = new Motor(Motor.MotorType.NeveRest60);
        Reaper = new Motor(Motor.MotorType.NeveRest40);
        Flywheel = new Motor(Motor.MotorType.NeveRest3_7);
        LFEncoder = new Sensor();
        LBEncoder = new Sensor();
        RFEncoder = new Sensor();
        RBEncoder = new Sensor();
        LiftLeftEncoder = new Sensor();
        LiftRightEncoder = new Sensor();
        ReaperEncoder = new Sensor();
        FlywheelEncoder = new Sensor();
        ButtonServo = new Servo();
        ClawLeft = new ContinuousRotationServo();
        ClawRight = new ContinuousRotationServo();
        FlywheelStopper = new Servo();
        leftRotate = new SyncedMotors(LFMotor, LBMotor, LFEncoder, LBEncoder, KP, KI, KD, SyncedMotors.SyncAlgo.POSITION);
        rightRotate = new SyncedMotors(RFMotor, RBMotor, RFEncoder, RBEncoder, KP, KI, KD, SyncedMotors.SyncAlgo.POSITION);
        //capLift = new SyncedMotors(LiftLeftMotor, LiftRightMotor, LiftLeftEncoder, LiftRightEncoder, KP, KI, KD, SyncedMotors.SyncAlgo.POSITION);
        //capLift.setRatio(1);
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
    public Sensor getVoltageSensor() { return voltageSensor; }

    @Override
    public AxisSensor getWorldAccel() { return worldAccel; }

    @Override
    public AxisSensor getRawAccel() { return rawAccel; }

    @Override
    public synchronized UltrasonicSensor getSonarLeft(){return sonarLeft;}

    @Override
    public synchronized UltrasonicSensor getSonarRight(){return sonarRight;}

    @Override
    public synchronized Sensor getLFEncoder() { return LFEncoder; }

    @Override
    public synchronized Sensor getLBEncoder() { return LBEncoder;}

    @Override
    public synchronized Sensor getRFEncoder() { return RFEncoder;}

    @Override
    public synchronized Sensor getRBEncoder () {return RBEncoder;}

    @Override
    public Sensor getLiftLeftEncoder() { return LiftLeftEncoder; }

    @Override
    public Sensor getLiftRightEncoder() { return LiftRightEncoder; }

    @Override
    public Sensor getReaperEncoder() { return ReaperEncoder; }

    @Override
    public Sensor getFlywheelEncoder() { return FlywheelEncoder; }

    @Override
    public synchronized Sensor getLightSensor1() {return nxtlightSensor1;}

    @Override
    public synchronized Sensor getLightSensor2() {return nxtlightSensor2;}

    @Override
    public synchronized Sensor getLightSensor3() {return nxtlightSensor3;}

    @Override
    public synchronized Sensor getLightSensor4() {return nxtlightSensor4;}

    @Override
    public synchronized Motor getLFMotor() { return LFMotor; }

    @Override
    public synchronized Motor getLBMotor() { return LBMotor; }

    @Override
    public synchronized Motor getRFMotor() { return RFMotor; }

    @Override
    public synchronized Motor getRBMotor() { return RBMotor; }

    @Override
    public Motor getLiftLeftMotor() { return LiftLeftMotor; }

    @Override
    public Motor getLiftRightMotor() { return LiftRightMotor; }

    @Override
    public synchronized Motor getReaperMotor() { return Reaper; }

    @Override
    public Motor getFlywheel() { return Flywheel; }

    @Override
    public synchronized Servo getButtonServo() { return ButtonServo; }

    @Override
    public Servo getFlywheelStopper() { return FlywheelStopper; }

    @Override
    public ContinuousRotationServo getClawLeft() { return ClawLeft; }

    @Override
    public ContinuousRotationServo getClawRight() { return ClawRight; }

    @Override
    public synchronized StateSensor getStateSensor() { return stateSensor; }

    @Override
    public synchronized SyncedMotors getRightRotate() {
        return rightRotate;
    }

    @Override
    public synchronized SyncedMotors getCapLift() { return capLift; }

    @Override
    public synchronized void stopMotors() {LFMotor.setPower(0); RFMotor.setPower(0); LBMotor.setPower(0); RBMotor.setPower(0);}

    @Override
    public synchronized SyncedMotors getLeftRotate() { return leftRotate; }

    @Override
    public synchronized ColorSensor getColorSensor(){return colorSensor;}

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
    public synchronized ConcurrentHashMap<String, Object> getTelemetry () { return telemetry; }


}
