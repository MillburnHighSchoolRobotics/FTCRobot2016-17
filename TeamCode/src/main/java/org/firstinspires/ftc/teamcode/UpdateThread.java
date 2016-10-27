package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.MPU9250;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.TestingOpModes.TakePictureTest;

import java.util.ArrayList;
import java.util.Map;

import virtualRobot.GodThread;
import virtualRobot.JoystickController;
import virtualRobot.SallyJoeBot;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Command;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.ContinuousRotationServo;
import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.SyncedMotors;
import virtualRobot.godThreads.TeleopGodThread;
import virtualRobot.utils.MathUtils;
import virtualRobot.godThreads.TakePictureTestGod;

public abstract class UpdateThread extends OpMode {
	private static final boolean withServos = true;
	private SallyJoeBot robot;
	protected Class<? extends GodThread> godThread;
	private Thread t;

	//here we will initiate all of our PHYSICAL components. E.g: private DcMotor leftBack...
	//also initiate sensors. E.g. private AnalogInput sonar, private ColorSensor colorSensor, private DigitalChannel ...


	private MPU9250 imu;
	private DcMotor leftFront, leftBack, rightFront, rightBack, reaper;
	private UltrasonicSensor sonar1;

	private CRServo capLeft, capRight;
	private Servo buttonServo;

	private GodThread vuforiaEverywhere;
	private AnalogInput lineSensor;


//Now initiate the VIRTUAL componenents (from VirtualRobot!!), e.g. private Motor vDriveRightMotor, private virtualRobot.components.Servo ..., private Sensor vDriveRightMotorEncoder, private LocationSensor vLocationSensor

	private Sensor vHeadingSensor, vPitchSensor, vRollSensor,vLineSensor,vUltrasonicSensor;
	private LocationSensor vLocationSensor;
	private JoystickController vJoystickController1, vJoystickController2;
	private Motor vLeftFront, vLeftBack, vRightFront, vRightBack, vReaper;
	private Sensor vLeftFrontEncoder, vLeftBackEncoder, vRightFrontEncoder, vRightBackEncoder;

	private virtualRobot.components.Servo vButtonServo;

	private ContinuousRotationServo vCapServo;

    private ElapsedTime runtime = new ElapsedTime();

	private ArrayList<String> robotProgress;
	
	@Override
	public void init() {
        //MOTOR SETUP (with physical componenents, e.g. leftBack = hardwareMap.dcMotor.get("leftBack")
		leftFront = hardwareMap.dcMotor.get("leftFront");
		leftBack = hardwareMap.dcMotor.get("leftBack");
		rightFront = hardwareMap.dcMotor.get("rightFront");
		rightBack = hardwareMap.dcMotor.get("rightBack");
		reaper = hardwareMap.dcMotor.get("reaper");

        //SERVO SETUP (with physical components, e.g. servo = hardwareMap....)
		if (withServos) {
			capLeft = hardwareMap.crservo.get("capLeft");
			capRight = hardwareMap.crservo.get("capRight");
			buttonServo = hardwareMap.servo.get("buttonPusher");
		}

        //REVERSE ONE SIDE (If needed, e.g. rightFront.setDirection(DcMotor.Direction.REVERSE)
		rightFront.setDirection(DcMotor.Direction.REVERSE);
		rightBack.setDirection(DcMotor.Direction.REVERSE);




        //SENSOR SETUP e.g. colorSensor = hardwareMap.colorsensor.get("color"), sonar1 = hardwareMap.analogInput.get("sonar1"), liftEndStop1 = hardwareMap.digitalChannel.get("liftEndStop1")
		imu = MPU9250.getInstance(hardwareMap.deviceInterfaceModule.get("dim"), 0);
		lineSensor = hardwareMap.analogInput.get("lineSensor");
		sonar1 = hardwareMap.ultrasonicSensor.get("sonar1");


        //FETCH VIRTUAL ROBOT FROM COMMAND INTERFACE
		robot = Command.ROBOT;

        //FETCH VIRTUAL COMPONENTS OF VIRTUAL ROBOT from robot. E.g. vDriveLeftMotor = robot.getDriveLeftMotor();
		vHeadingSensor = robot.getHeadingSensor();
		vPitchSensor = robot.getPitchSensor();
		vRollSensor = robot.getRollSensor();
		vLocationSensor = robot.getLocationSensor();
		vLineSensor = robot.getLineSensor();
		vUltrasonicSensor = robot.getUltrasonicSensor();
		vLeftFront = robot.getLFMotor();
		vLeftBack = robot.getLBMotor();
		vRightFront = robot.getRFMotor();
		vRightBack = robot.getRBMotor();
		vReaper = robot.getReaperMotor();
		if (withServos) {
			vCapServo = robot.getCapServo();
			vButtonServo = robot.getButtonServo();
		}
		vLeftFrontEncoder = robot.getLFEncoder();
		vLeftBackEncoder = robot.getLBEncoder();
		vRightFrontEncoder = robot.getRFEncoder();
		vRightBackEncoder = robot.getRBEncoder();
        vJoystickController1 = robot.getJoystickController1();
        vJoystickController2 = robot.getJoystickController2();

		robotProgress = new ArrayList<String>();
		//Setup Physical Components

		if (withServos) {
			capRight.setDirection(DcMotorSimple.Direction.REVERSE);
		}
			//UpdateUtil.setPosition(capLeft,0.3);
		//UpdateUtil.setPosition(capRight,0.3);
		if (withServos) {
			capLeft.getController().setServoPosition(capLeft.getPortNumber(), .2);
			capRight.getController().setServoPosition(capRight.getPortNumber(), .1);
			buttonServo.setPosition(0.5);
		}

		addPresets();
        setGodThread();

		try {
			if (!godThread.equals(TeleopGodThread.class)) {
				VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
				params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
				params.vuforiaLicenseKey = "AcXbD9X/////AAAAGVpq1gdfDkIPp+j5hv1iV5RZXLWAWV4F7je9gks+8lHhZb6mwCj7xy9mapHP6sKO9OrPv5kVQDXhB+T+Rn7V7GUm4Ub4rmCanqv4frx8gT732qJUnTEj9POMufR9skjlXSEODbpThxrLCPqobHeAeSA5dUmUik3Rck0lcwhElw5yOBN45iklYnvC9GpPRv128ALcgt9Zpw/shit0erKmuyrT62NRUKgoHNMm5xV/Xqj8Vgwke8ESap+nK7v+6lx35vDZ6ISNDVMMM8h0VqeL0745MNPJoI1vgiNRo30R7WwtPYME44koOrWMUIxMXghtqxq7AfFxb6sbin0i5KSUJWtLsqmZOrAXxjxdUwY8f8tw";
				Log.d("lalala", "location1");
				VuforiaLocalizerImplSubclass vuforia = new VuforiaLocalizerImplSubclass(params);
				vuforiaEverywhere = godThread.newInstance();
				vuforiaEverywhere.setVuforia(vuforia);
				t = new Thread(vuforiaEverywhere);
			} else {
				t = new Thread(godThread.newInstance());
				Log.d("lalala", "location2");
			}

		} catch (InstantiationException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		}
	}

	public void init_loop () {
		imu.zeroPitch();
		imu.zeroYaw();
		imu.zeroRoll();
		telemetry.addData("Is Running Version: ", Rotate.KP + " 1.7");
        telemetry.addData("Init Loop Time", runtime.toString());
	}

	public void start() {
		//set encoders e.g. vDriveRightMotorEncoder.setRawValue(-rightFront.getCurrentPosition())
			vLeftFrontEncoder.setRawValue(leftFront.getCurrentPosition());
			vLeftBackEncoder.setRawValue(leftBack.getCurrentPosition());
			vRightFrontEncoder.setRawValue(rightFront.getCurrentPosition());
			vRightBackEncoder.setRawValue(rightBack.getCurrentPosition());

		//vCapServo.setPosition((UpdateUtil.getPosition(capLeft) + UpdateUtil.getPosition(capRight))/2);
			if (withServos) {
				vCapServo.setPosition((capLeft.getController().getServoPosition(capLeft.getPortNumber()) + capLeft.getController().getServoPosition(capRight.getPortNumber())) / 2);

				vButtonServo.setPosition(buttonServo.getPosition());
			}
			vUltrasonicSensor.setRawValue(sonar1.getUltrasonicLevel());
		t.start();
	}
	
	public void loop() {
		// Update Location. E.g.: double prevEcnoderValue=?, newEncoderValue=?,

		//TODO: Calculate values for prev and newEncoderValues (Not top priority, locationSensor may not be used)
		double prevEncoderValue = 1;
		double newEncoderValue = 1;
		double headingAngle = imu.getIntegratedYaw();
		vLocationSensor.setAngle(headingAngle);
		vLocationSensor.setX(vLocationSensor.getX() + ((newEncoderValue - prevEncoderValue) * Math.cos(Math.toRadians(vLocationSensor.getAngle()))));
		vLocationSensor.setY(vLocationSensor.getY() + ((newEncoderValue - prevEncoderValue) * Math.sin(Math.toRadians(vLocationSensor.getAngle()))));


		// Update Sensor Values E.g. vPitchSensor.setRawValue(imu.getIntegratedPitch()); vHeadingSensor, vRollSensor, vColorSensor...
		vPitchSensor.setRawValue(imu.getIntegratedPitch());
		vHeadingSensor.setRawValue(headingAngle);
		vRollSensor.setRawValue(imu.getIntegratedRoll());
		vLineSensor.setRawValue(lineSensor.getVoltage());
		vUltrasonicSensor.setRawValue(sonar1.getUltrasonicLevel());

		//Set more values, such as: vDriveRightMotorEncoder.setRawValue((-rightFront.getCurrentPosition());
		vLeftFrontEncoder.setRawValue(leftFront.getCurrentPosition());
		vLeftBackEncoder.setRawValue(leftBack.getCurrentPosition());
		vRightFrontEncoder.setRawValue(rightFront.getCurrentPosition());
		vRightBackEncoder.setRawValue(rightBack.getCurrentPosition());



		try {
            vJoystickController1.copyStates(gamepad1);
            vJoystickController2.copyStates(gamepad2);
        } catch (RobotCoreException e) {
            e.printStackTrace();
        }


		// Capture Motor Powers,E.g. double leftPower = vDriveLeftMotore.getPower();
		double leftFrontPower = vLeftFront.getPower();
		double leftBackPower = vLeftBack.getPower();
		double rightFrontPower = vRightFront.getPower();
		double rightBackPower = vRightBack.getPower();
		double reaperPower = vReaper.getPower();
		double capPosition = 0;
		double buttonPosition = 0;
		if (withServos) {
			capPosition = vCapServo.getPosition();
			buttonPosition = vButtonServo.getPosition();
		}


		// Copy State of Motors and Servos E.g. leftFront.setPower(leftPower), Servo.setPosition(vServo.getPosition());

		telemetry.addData("Servos: ", capPosition + " " + buttonPosition);
		Log.d("servoState", capPosition + " " + buttonPosition);
		telemetry.addData("Motors: ", MathUtils.truncate(leftFrontPower,2) + " " + MathUtils.truncate(leftBackPower,2) + " " + MathUtils.truncate(rightFrontPower,2) + " " + MathUtils.truncate(rightBackPower,2));
		Log.d("motorPower", leftFrontPower + " " + leftBackPower + " " + rightFrontPower + " " + rightBackPower);
		leftFront.setPower(leftFrontPower);
		leftBack.setPower(leftBackPower);
		rightFront.setPower(rightFrontPower);
		rightBack.setPower(rightBackPower);
		reaper.setPower(reaperPower);
		if (withServos) {
			capRight.getController().setServoPosition(capRight.getPortNumber(), capPosition);
			capLeft.getController().setServoPosition(capLeft.getPortNumber(), capPosition);
			buttonServo.setPosition(buttonPosition);
		}

		for (int i = 0; i < robot.getProgress().size(); i++) {
			telemetry.addData("robot progress " + i, robot.getProgress().get(i));
		}
		for (Map.Entry<String,Object> e: robot.getTelemetry().entrySet()) {
			telemetry.addData(e.getKey(),e.getValue());
		}
		telemetry.addData("capServo State", capPosition);
		telemetry.addData("buttonServo Position", buttonPosition);
		telemetry.addData("encoders: ", robot.getLFEncoder().getValue() + " " + robot.getLBEncoder().getValue() + " " + robot.getRFEncoder().getValue() + " " + robot.getRBEncoder().getValue());
		telemetry.addData("Line Sensor: ", robot.getLineSensor().getValue());
		telemetry.addData("Ultrasonic: ", robot.getUltrasonicSensor().getValue());
		Log.d("syncedMotors: ",robot.getLeftRotate().getSpeedA() + " " + robot.getLeftRotate().getSpeedB() + " " + robot.getRightRotate().getSpeedA() + " " + robot.getRightRotate().getSpeedB()) ;
		Log.d("encoders: ", robot.getLFEncoder().getValue() + " " + robot.getLBEncoder().getValue() + " " + robot.getRFEncoder().getValue() + " " + robot.getRBEncoder().getValue());
		telemetry.addData("IMU testing: ", imu.getIntegratedPitch() + " " + imu.getIntegratedRoll() + " " + imu.getIntegratedYaw());
		Log.d("Heading: ", String.valueOf(robot.getHeadingSensor().getValue()) + " " + imu.getIntegratedYaw());
		if (godThread.equals(TakePictureTestGod.class)) {
			telemetry.addData("redIsLeft: ", "" + ((TakePictureTestGod)vuforiaEverywhere).getRedIsLeft().get());
		}
    }
	
	public void stop() {
		imu.close();
		t.interrupt();
	}

	public abstract void setGodThread();

    public void addPresets(){}
}

