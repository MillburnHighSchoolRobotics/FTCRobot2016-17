package com.qualcomm.ftcrobotcontroller.opmodes;

import com.kauailabs.navx.ftc.MPU9250;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;

import virtualRobot.GodThread;
import virtualRobot.JoystickController;
import virtualRobot.SallyJoeBot;
import virtualRobot.commands.Command;
import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;

public abstract class UpdateThread extends OpMode {
	
	private SallyJoeBot robot;
	protected Class<? extends GodThread> godThread;
	private Thread t;

	private DcMotor rightFront, rightBack, leftFront, leftBack, reaper, liftRight, liftLeft, tapeMeasureMotor;
	private Servo backShieldRight, backShieldLeft, tapeMeasureServo, flipperRight, flipperLeft, dumper;
    protected Servo basket, gate, scoop, permaHang;

	private MPU9250 imu;
	private AnalogInput sonar1, sonar2, sonar3;
	private ColorSensor colorSensor;
	private DigitalChannel liftEndStop1, liftEndStop2;

	private Motor vDriveRightMotor, vDriveLeftMotor, vReaperMotor, vLiftLeftMotor, vLiftRightMotor, vTapeMeasureMotor;
	private virtualRobot.components.Servo vPermaHangServo;
    private virtualRobot.components.Servo vBackShieldServo, vTapeMeasureServo, vFlipperLeftServo, vFlipperRightServo, vBasketServo, vGateServo, vDumperServo, vScoopServo;
	private Sensor vDriveLeftMotorEncoder, vDriveRightMotorEncoder, vTapeMeasureMotorEncoder, vLiftRightMotorEncoder, vLiftLeftMotorEncoder, vHeadingSensor, vPitchSensor, vRollSensor, vUltrasoundSensor1, vUltrasoundSensor2, vUltrasoundSensor3;
	private LocationSensor vLocationSensor;

	private virtualRobot.components.ColorSensor vColorSensor;
	private JoystickController vJoystickController1, vJoystickController2;

    private double initLiftRightEncoder;
    private double initLiftLeftEncoder;
    private ElapsedTime runtime = new ElapsedTime();

	private ArrayList<String> robotProgress;
	
	@Override
	public void init() {
        //MOTOR SETUP
		rightFront = hardwareMap.dcMotor.get("rightFront");
		rightBack = hardwareMap.dcMotor.get("rightBack");
		leftFront = hardwareMap.dcMotor.get("leftFront");
		leftBack = hardwareMap.dcMotor.get("leftBack");
        tapeMeasureMotor = hardwareMap.dcMotor.get("tapeMeasureMotor");
		reaper = hardwareMap.dcMotor.get("reaper");
		liftLeft = hardwareMap.dcMotor.get("liftLeft");
		liftRight = hardwareMap.dcMotor.get("liftRight");

        //SERVO SETUP
        tapeMeasureServo = hardwareMap.servo.get("tapeMeasure");
        flipperLeft = hardwareMap.servo.get("flipperLeft");
        flipperRight = hardwareMap.servo.get("flipperRight");
		dumper = hardwareMap.servo.get("dumper");
		backShieldLeft = hardwareMap.servo.get("backShieldLeft");
        backShieldRight = hardwareMap.servo.get("backShieldRight");
		basket = hardwareMap.servo.get("basket");
		gate = hardwareMap.servo.get("gate");
        scoop = hardwareMap.servo.get("scoop");
        permaHang = hardwareMap.servo.get("permaHang");

        //REVERSE RIGHT SIDE
        backShieldRight.setDirection(Servo.Direction.REVERSE);
        flipperRight.setDirection(Servo.Direction.REVERSE);
		rightFront.setDirection(DcMotor.Direction.REVERSE);
		rightBack.setDirection(DcMotor.Direction.REVERSE);
		liftRight.setDirection(DcMotor.Direction.REVERSE);

        //RESETTING THE LIFT VALUES TO MAKE PID WORK
        initLiftLeftEncoder = liftLeft.getCurrentPosition();
        initLiftRightEncoder = liftRight.getCurrentPosition();


        //SENSOR SETUP
		imu = MPU9250.getInstance(hardwareMap.deviceInterfaceModule.get("dim"), 0);
		colorSensor = hardwareMap.colorSensor.get("color");
		sonar1 = hardwareMap.analogInput.get("sonar1");
		sonar2 = hardwareMap.analogInput.get("sonar2");
		sonar3 = hardwareMap.analogInput.get("sonar3");

		liftEndStop1 = hardwareMap.digitalChannel.get("liftEndStop1");
		liftEndStop2 = hardwareMap.digitalChannel.get("liftEndStop2");

        //FETCH VIRTUAL ROBOT FROM COMMAND INTERFACE
		robot = Command.ROBOT;

        //FETCH VIRTUAL COMPONENTS OF VIRTUAL ROBOT
        vDriveLeftMotor = robot.getDriveLeftMotor();
        vDriveRightMotor = robot.getDriveRightMotor();
        vReaperMotor = robot.getReaperMotor();
		vLiftLeftMotor = robot.getLiftLeftMotor();
        vLiftRightMotor = robot.getLiftRightMotor();
		vTapeMeasureMotor = robot.getTapeMeasureMotor();
		vDriveLeftMotorEncoder = robot.getDriveLeftMotorEncoder();
        vDriveRightMotorEncoder = robot.getDriveRightMotorEncoder();
        vTapeMeasureMotorEncoder = robot.getTapeMeasureMotorEncoder();
		vLiftRightMotorEncoder = robot.getLiftRightMotorEncoder();
        vLiftLeftMotorEncoder = robot.getLiftLeftMotorEncoder();
        vHeadingSensor = robot.getHeadingSensor();
		vPitchSensor = robot.getPitchSensor();
		vRollSensor = robot.getRollSensor();
		vColorSensor = robot.getColorSensor();
		vUltrasoundSensor1 = robot.getUltrasoundSensor1();
		vUltrasoundSensor2 = robot.getUltrasoundSensor2();
		vUltrasoundSensor3 = robot.getUltrasoundSensor3();

		vTapeMeasureServo = robot.getTapeMeasureServo();
		vFlipperLeftServo = robot.getFlipperLeftServo();
		vFlipperRightServo = robot.getFlipperRightServo();
		vDumperServo = robot.getDumperServo();
		vBackShieldServo = robot.getBackShieldServo();
		vBasketServo = robot.getBasketServo();
		vGateServo = robot.getGateServo();
        vScoopServo = robot.getScoopServo();
		vLocationSensor = robot.getLocationSensor();

        vPermaHangServo = robot.getPermaHang();

        vJoystickController1 = robot.getJoystickController1();
        vJoystickController2 = robot.getJoystickController2();

		robotProgress = new ArrayList<String>();

        tapeMeasureServo.setPosition(0.35);
        backShieldLeft.setPosition(0);
        backShieldRight.setPosition(0);

        addPresets();
        setGodThread();

		try {
			t = new Thread(godThread.newInstance());
		} catch (InstantiationException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		}

	}

	public void init_loop () {
        imu.zeroYaw();
		imu.zeroPitch();
		imu.zeroRoll();
        telemetry.addData("Init Loop Time", runtime.toString());
	}

	public void start() {

		vDriveLeftMotorEncoder.setRawValue((-leftFront.getCurrentPosition() + -leftBack.getCurrentPosition())/2);
		vDriveRightMotorEncoder.setRawValue((-rightFront.getCurrentPosition()));
        vTapeMeasureMotorEncoder.setRawValue(-tapeMeasureMotor.getCurrentPosition());
        vLiftRightMotorEncoder.setRawValue(-liftRight.getCurrentPosition());
        vLiftLeftMotorEncoder.setRawValue(-liftLeft.getCurrentPosition());
		tapeMeasureServo.setPosition(0.25);

		t.start();
	}
	
	public void loop() {
		// Update Location
		double prevEncoderValue = (vDriveLeftMotorEncoder.getValue() + vDriveRightMotorEncoder.getValue())/2;
		double newEncoderValue = ((-leftFront.getCurrentPosition() + -leftBack.getCurrentPosition())/2) + ((-rightFront.getCurrentPosition() + -rightBack.getCurrentPosition())/2) / 2;
		double headingAngle = imu.getIntegratedYaw();
		vLocationSensor.setAngle(headingAngle);
		vLocationSensor.setX(vLocationSensor.getX() + ((newEncoderValue - prevEncoderValue) * Math.cos(Math.toRadians(vLocationSensor.getAngle()))));
		vLocationSensor.setY(vLocationSensor.getY() + ((newEncoderValue - prevEncoderValue) * Math.sin(Math.toRadians(vLocationSensor.getAngle()))));

		// Update Sensor Values
		vPitchSensor.setRawValue(imu.getIntegratedPitch());
        vHeadingSensor.setRawValue(headingAngle);
		vRollSensor.setRawValue(imu.getIntegratedRoll());
        vColorSensor.setRawValue(colorSensor.argb());
		if (sonar1.getValue() > 0) {
			vUltrasoundSensor1.setRawValue(sonar1.getValue());
		}
		else { /*don't update*/ }
		if (sonar2.getValue() > 0) {
			vUltrasoundSensor2.setRawValue(sonar2.getValue());
		}
		else { /*don't update*/ }
		if (sonar3.getValue() > 0) {
			vUltrasoundSensor3.setRawValue(sonar3.getValue());
		}
		else { /*don't update*/ }

		vDriveLeftMotorEncoder.setRawValue((-leftFront.getCurrentPosition() + -leftBack.getCurrentPosition())/2);
		vDriveRightMotorEncoder.setRawValue((-rightFront.getCurrentPosition()));
		vTapeMeasureMotorEncoder.setRawValue(-tapeMeasureMotor.getCurrentPosition());
        vLiftLeftMotorEncoder.setRawValue(-liftLeft.getCurrentPosition());
        vLiftRightMotorEncoder.setRawValue(-liftRight.getCurrentPosition());

		try {
            vJoystickController1.copyStates(gamepad1);
            vJoystickController2.copyStates(gamepad2);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }


		// Capture Motor Powers
		double leftPower = vDriveLeftMotor.getPower();
		double rightPower = vDriveRightMotor.getPower();
		double tapeMeasurePower = vTapeMeasureMotor.getPower();
		double liftRightPower = vLiftRightMotor.getPower();
        double liftLeftPower = vLiftLeftMotor.getPower();
		double reaperPower = vReaperMotor.getPower();

        //PID CONTROLLER TO KEEP LIFT ARMS AT THE SAME EXTENSION


        if (vLiftRightMotor.getPower() == 0) {
            liftRightPower = 0;
        }
        if (vLiftLeftMotor.getPower() == 0) {
            liftLeftPower = 0;
        }

		// Copy State of Motors and Servos
		rightFront.setPower(rightPower);
		rightBack.setPower(rightPower);
		leftFront.setPower(leftPower);
		leftBack.setPower(leftPower);
		tapeMeasureMotor.setPower(tapeMeasurePower);
		reaper.setPower(reaperPower);
		liftLeft.setPower(liftLeftPower);
		liftRight.setPower(liftRightPower);


		tapeMeasureServo.setPosition(vTapeMeasureServo.getPosition());
		flipperLeft.setPosition(vFlipperLeftServo.getPosition());
		flipperRight.setPosition(vFlipperRightServo.getPosition());
		dumper.setPosition(vDumperServo.getPosition());
		backShieldLeft.setPosition(vBackShieldServo.getPosition());
		backShieldRight.setPosition(vBackShieldServo.getPosition());
		basket.setPosition(vBasketServo.getPosition());
		gate.setPosition(vGateServo.getPosition());
        scoop.setPosition(vScoopServo.getPosition());
        permaHang.setPosition(vPermaHangServo.getPosition());

		for (int i = 0; i < robot.getProgress().size(); i++) {
			telemetry.addData("robot progress " + i, robot.getProgress().get(i));
		}

        telemetry.addData("left enc", vDriveLeftMotorEncoder.getValue());
        telemetry.addData("right enc", vDriveRightMotorEncoder.getValue());
        telemetry.addData("right power", rightPower);
        telemetry.addData("left power", leftPower);
        telemetry.addData("heading", "Yaw: " + imu.getIntegratedYaw() + " ");
        telemetry.addData("Color sensor: ", "Red: " + vColorSensor.getRed() + " Green: " + vColorSensor.getGreen() + " Blue: " + vColorSensor.getBlue());
	    telemetry.addData("Sonar Front", vUltrasoundSensor2.getValue());
        telemetry.addData("Sonar Back", vUltrasoundSensor1.getValue());
		telemetry.addData("Lift End Stop 1", liftEndStop1.getState());
		telemetry.addData("Lift End Stop 2", liftEndStop2.getState());
    }
	
	public void stop() {
		imu.close();
		t.interrupt();
	}

	public abstract void setGodThread();

    public void addPresets(){}

}
