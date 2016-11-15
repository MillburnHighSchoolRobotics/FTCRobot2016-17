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
//import virtualRobot.components.LocationSensor;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.components.SyncedMotors;
import virtualRobot.godThreads.PIDLineFollowerGod;
import virtualRobot.godThreads.PIDTesterGodThread;
import virtualRobot.godThreads.TeleopGodThread;
import virtualRobot.logicThreads.TeleopLogic;
import virtualRobot.utils.MathUtils;
import virtualRobot.godThreads.TakePictureTestGod;
/*
Updates Virtual sensors etc to corresponds to their real components.
Updates Real components (e.g. motors) to correspond to the values of their virtual componennts
 */
public abstract class UpdateThread extends OpMode {
	private static final boolean withServos = true;
	public static final boolean WITH_SONAR = true;
	private SallyJoeBot robot;
	protected Class<? extends GodThread> godThread;
	private Thread t;
	private CreateVuforia cv;

	//here we will initiate all of our PHYSICAL components. E.g: private DcMotor leftBack...
	//also initiate sensors. E.g. private AnalogInput sonar, private ColorSensor colorSensor, private DigitalChannel ...


	private MPU9250 imu;
	private DcMotor leftFront, leftBack, rightFront, rightBack, reaper;
	private UltrasonicSensor sonarLeft, sonarRight;
	private LightSensor nxtLight1, nxtLight2, nxtLight3, nxtLight4;
	private ColorSensor colorSensor;
	private Servo buttonServo, capLeftServo, capRightServo, ballLauncherServo;

	private GodThread vuforiaEverywhere;


//Now initiate the VIRTUAL componenents (from VirtualRobot!!), e.g. private Motor vDriveRightMotor, private virtualRobot.components.Servo ..., private Sensor vDriveRightMotorEncoder, private LocationSensor vLocationSensor

	private Sensor vHeadingSensor, vPitchSensor, vRollSensor,vReaperEncoder;
//	private LocationSensor vLocationSensor;
	private JoystickController vJoystickController1, vJoystickController2;
	private Motor vLeftFront, vLeftBack, vRightFront, vRightBack, vReaper;
	private Sensor vLeftFrontEncoder, vLeftBackEncoder, vRightFrontEncoder, vRightBackEncoder;
	private virtualRobot.components.UltrasonicSensor vSonarLeft, vSonarRight;
	private virtualRobot.components.Servo vButtonServo, vCapLeftServo, vCapRightServo, vBallLauncherServo;
	private Sensor vLightSensor1, vLightSensor2, vLightSensor3, vLightSensor4;
	private virtualRobot.components.ColorSensor vColorSensor;

    private ElapsedTime runtime = new ElapsedTime();

	private ArrayList<String> robotProgress;
	
	@Override
	public void init() {
        //MOTOR SETUP (with physical componenents, e.g. leftBack = hardwareMap.dcMotor.get("leftBack")
		leftFront = hardwareMap.dcMotor.get("leftFront");
		leftBack = hardwareMap.dcMotor.get("leftBack");
		rightFront = hardwareMap.dcMotor.get("rightFront");
		rightBack = hardwareMap.dcMotor.get("rightBack");
//		reaper = hardwareMap.dcMotor.get("reaper");

        //SERVO SETUP (with physical components, e.g. servo = hardwareMap....)
		if (withServos) {
			capLeftServo = hardwareMap.servo.get("capLeft");
			capRightServo = hardwareMap.servo.get("capRight");
			ballLauncherServo = hardwareMap.servo.get("ballLauncher");
			buttonServo = hardwareMap.servo.get("buttonPusher");
		}

        //REVERSE ONE SIDE (If needed, e.g. rightFront.setDirection(DcMotor.Direction.REVERSE)
		rightFront.setDirection(DcMotor.Direction.REVERSE);
		rightBack.setDirection(DcMotor.Direction.REVERSE);




        //SENSOR SETUP e.g. colorSensor = hardwareMap.colorsensor.get("color"), sonar1 = hardwareMap.analogInput.get("sonar1"), liftEndStop1 = hardwareMap.digitalChannel.get("liftEndStop1")

		imu = MPU9250.getInstance(hardwareMap.deviceInterfaceModule.get("dim"), 1);
		if (WITH_SONAR) {
			sonarLeft = hardwareMap.ultrasonicSensor.get("sonarLeft");
			sonarRight = hardwareMap.ultrasonicSensor.get("sonarRight");
		}
		colorSensor = hardwareMap.colorSensor.get("color");
		nxtLight1 = hardwareMap.lightSensor.get("nxtLight1");
		nxtLight2 = hardwareMap.lightSensor.get("nxtLight2");
		nxtLight3 = hardwareMap.lightSensor.get("nxtLight3");
		nxtLight4 = hardwareMap.lightSensor.get("nxtLight4");




		//FETCH VIRTUAL ROBOT FROM COMMAND INTERFACE
		robot = Command.ROBOT;

        //FETCH VIRTUAL COMPONENTS OF VIRTUAL ROBOT from robot. E.g. vDriveLeftMotor = robot.getDriveLeftMotor();
		vHeadingSensor = robot.getHeadingSensor();
		vPitchSensor = robot.getPitchSensor();
		vRollSensor = robot.getRollSensor();
//		vLocationSensor = robot.getLocationSensor();
		vLightSensor1 = robot.getLightSensor1();
		vLightSensor2 = robot.getLightSensor2();
		vLightSensor3 = robot.getLightSensor3();
		vLightSensor4 = robot.getLightSensor4();
		vColorSensor = robot.getColorSensor();
		if (WITH_SONAR) {
			vSonarLeft = robot.getSonarLeft();
			vSonarRight = robot.getSonarRight();
		}
		vLeftFront = robot.getLFMotor();
		vLeftBack = robot.getLBMotor();
		vRightFront = robot.getRFMotor();
		vRightBack = robot.getRBMotor();
//		vReaper = robot.getReaperMotor();
		if (withServos) {
			vButtonServo = robot.getButtonServo();
			vCapLeftServo = robot.getCapLeftServo();
			vCapRightServo = robot.getCapRightServo();
			vBallLauncherServo = robot.getBallLauncherServo();
		}
		vLeftFrontEncoder = robot.getLFEncoder();
		vLeftBackEncoder = robot.getLBEncoder();
		vRightFrontEncoder = robot.getRFEncoder();
		vRightBackEncoder = robot.getRBEncoder();
//		vReaperEncoder = robot.getReaperEncoder();
        vJoystickController1 = robot.getJoystickController1();
        vJoystickController2 = robot.getJoystickController2();

		robotProgress = new ArrayList<String>();
		//Setup Physical Components
		capLeftServo.setPosition(0);
		buttonServo.setPosition(0.5);
		capRightServo.setPosition(0);
		ballLauncherServo.setPosition(0);


			//UpdateUtil.setPosition(capLeft,0.3);
		//UpdateUtil.setPosition(capRight,0.3);
		if (withServos) {
			buttonServo.setPosition(TeleopLogic.BUTTON_PUSHER_STATIONARY);
		}

		addPresets();
        setGodThread();
		cv = new CreateVuforia(godThread, vuforiaEverywhere, t);
		new Thread (cv).start();


	}

	public void init_loop () {
		imu.zeroPitch();
		imu.zeroYaw();
		imu.zeroRoll();
		telemetry.addData("Is Running Version: ", Translate.KPt + " 1.0");
        telemetry.addData("Init Loop Time", runtime.toString());


	}

	public void start() {
		//set encoders e.g. vDriveRightMotorEncoder.setRawValue(-rightFront.getCurrentPosition())
			vLeftFrontEncoder.setRawValue(leftFront.getCurrentPosition());
			vLeftBackEncoder.setRawValue(leftBack.getCurrentPosition());
			vRightFrontEncoder.setRawValue(rightFront.getCurrentPosition());
			vRightBackEncoder.setRawValue(rightBack.getCurrentPosition());
//			vReaperEncoder.setRawValue(reaper.getCurrentPosition());

		//vCapServo.setPosition((UpdateUtil.getPosition(capLeft) + UpdateUtil.getPosition(capRight))/2);
			if (withServos) {
				vButtonServo.setPosition(buttonServo.getPosition());
				vCapLeftServo.setPosition(capLeftServo.getPosition());
				vCapRightServo.setPosition(capRightServo.getPosition());
				vBallLauncherServo.setPosition(ballLauncherServo.getPosition());
			}
		if (WITH_SONAR) {
			vSonarLeft.setRawValue(sonarLeft.getUltrasonicLevel());
			vSonarRight.setRawValue(sonarRight.getUltrasonicLevel());
		}
		while (!cv.getGood()) {
			//Chill
		}
		t = cv.t;
		vuforiaEverywhere = cv.vuforiaEverywhere;
		godThread = cv.godThread;
		t.start();
	}
	
	public void loop() {
		// Update Location. E.g.: double prevEcnoderValue=?, newEncoderValue=?,

		//TODO: Calculate values for prev and newEncoderValues (Not top priority, locationSensor may not be used)
		double prevEncoderValue = 1;
		double newEncoderValue = 1;
		double headingAngle = imu.getIntegratedYaw();
//		vLocationSensor.setAngle(headingAngle);
//		vLocationSensor.setX(vLocationSensor.getX() + ((newEncoderValue - prevEncoderValue) * Math.cos(Math.toRadians(vLocationSensor.getAngle()))));
//		vLocationSensor.setY(vLocationSensor.getY() + ((newEncoderValue - prevEncoderValue) * Math.sin(Math.toRadians(vLocationSensor.getAngle()))));


		// Update Sensor Values E.g. vPitchSensor.setRawValue(imu.getIntegratedPitch()); vHeadingSensor, vRollSensor, vColorSensor...
		vPitchSensor.setRawValue(imu.getIntegratedPitch());
		vHeadingSensor.setRawValue(headingAngle);
		vRollSensor.setRawValue(imu.getIntegratedRoll());
		if (WITH_SONAR) {
			vSonarLeft.setRawValue(sonarLeft.getUltrasonicLevel());
			vSonarRight.setRawValue(sonarRight.getUltrasonicLevel());
		}
		vColorSensor.setRawValue(colorSensor.argb());

		//Set more values, such as: vDriveRightMotorEncoder.setRawValue((-rightFront.getCurrentPosition());
		vLeftFrontEncoder.setRawValue(leftFront.getCurrentPosition());
		vLeftBackEncoder.setRawValue(leftBack.getCurrentPosition());
		vRightFrontEncoder.setRawValue(rightFront.getCurrentPosition());
		vRightBackEncoder.setRawValue(rightBack.getCurrentPosition());
//		vReaperEncoder.setRawValue(reaper.getCurrentPosition());
		vLightSensor1.setRawValue(nxtLight1.getRawLightDetected());
		vLightSensor2.setRawValue(nxtLight2.getRawLightDetected());
		vLightSensor3.setRawValue(nxtLight3.getRawLightDetected());
		vLightSensor4.setRawValue(nxtLight4.getRawLightDetected());



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
//		double reaperPower = vReaper.getPower();
		double buttonPosition = 0;
		double capLeftPosition = 0;
		double capRightPosition = 0;
		double ballLauncherPosition = 0;
		if (withServos) {
			buttonPosition = vButtonServo.getPosition();
			capLeftPosition = vCapLeftServo.getPosition();
			capRightPosition = vCapRightServo.getPosition();
			ballLauncherPosition = vBallLauncherServo.getPosition();
		}


		// Copy State of Motors and Servos E.g. leftFront.setPower(leftPower), Servo.setPosition(vServo.getPosition());

		Log.d("servoState", " " + buttonPosition);
		telemetry.addData("Motors: ", MathUtils.truncate(leftFrontPower,2) + " " + MathUtils.truncate(leftBackPower,2) + " " + MathUtils.truncate(rightFrontPower,2) + " " + MathUtils.truncate(rightBackPower,2));
		Log.d("motorPower", leftFrontPower + " " + leftBackPower + " " + rightFrontPower + " " + rightBackPower);
		leftFront.setPower(leftFrontPower);
		leftBack.setPower(leftBackPower);
		rightFront.setPower(rightFrontPower);
		rightBack.setPower(rightBackPower);
//		reaper.setPower(reaperPower);
		if (withServos) {
			buttonServo.setPosition(buttonPosition);
			capLeftServo.setPosition(capLeftPosition);
			capRightServo.setPosition(capRightPosition);
			ballLauncherServo.setPosition(ballLauncherPosition);


		}

		for (Map.Entry<String,Object> e: robot.getTelemetry().entrySet()) {
			telemetry.addData(e.getKey(),e.getValue());
		}

		for (int i = 0; i < robot.getProgress().size(); i++) {
			telemetry.addData("robot progress " + i, robot.getProgress().get(i));
		}

		telemetry.addData("buttonServo Position", buttonPosition);
		telemetry.addData("encoders: ", robot.getLFEncoder().getValue() + " " + robot.getLBEncoder().getValue() + " " + robot.getRFEncoder().getValue() + " " + robot.getRBEncoder().getValue());
		telemetry.addData("Light Sensor Arrays: ", robot.getLightSensor1().getValue() + " " + robot.getLightSensor2().getValue() + " " + robot.getLightSensor3().getValue() + " " + robot.getLightSensor4().getValue());
		if (WITH_SONAR)
		telemetry.addData("Ultrasonic: ", robot.getSonarLeft().getValue() + " " + robot.getSonarRight().getValue());
		telemetry.addData("Color sensor: ", "Red: " + vColorSensor.getRed() + " Green: " + vColorSensor.getGreen() + " Blue: " + vColorSensor.getBlue());
		Log.d("syncedMotors: ",robot.getLeftRotate().getSpeedA() + " " + robot.getLeftRotate().getSpeedB() + " " + robot.getRightRotate().getSpeedA() + " " + robot.getRightRotate().getSpeedB()) ;
//		Log.d("encoders: ", robot.getLFEncoder().getValue() + " " + robot.getLBEncoder().getValue() + " " + robot.getRFEncoder().getValue() + " " + robot.getRBEncoder().getValue());
		telemetry.addData("IMU testing: ", imu.getIntegratedPitch() + " " + imu.getIntegratedRoll() + " " + imu.getIntegratedYaw());
		Log.d("Heading: ", String.valueOf(robot.getHeadingSensor().getValue()) + " " + imu.getIntegratedYaw());
		if (godThread.equals(TakePictureTestGod.class)) {
			telemetry.addData("redIsLeft: ", "" + ((TakePictureTestGod)vuforiaEverywhere).getRedIsLeft().get());
		}
		if (godThread.equals(PIDTesterGodThread.class)) {
			telemetry.addData("KP: ", Rotate.KP);
		}
    }
	
	public void stop() {
		imu.close();
		t.interrupt();
	}

	public abstract void setGodThread();

    public void addPresets(){}


}

