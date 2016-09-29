package org.firstinspires.ftc.teamcode;

import com.kauailabs.navx.ftc.MPU9250;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.LightSensor;
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

	//here we will initiate all of our PHYSICAL components. E.g: private DcMotor leftBack...
	//also initiate sensors. E.g. private AnalogInput sonar, private ColorSensor colorSensor, private DigitalChannel ...


	private MPU9250 imu;
	private DcMotor leftFront, leftBack, rightFront, rightBack;
	private LightSensor lineSensor;

//Now initiate the VIRTUAL componenents (from VirtualRobot!!), e.g. private Motor vDriveRightMotor, private virtualRobot.components.Servo ..., private Sensor vDriveRightMotorEncoder, private LocationSensor vLocationSensor

	private Sensor vHeadingSensor, vPitchSensor, vRollSensor,vLineSensor;
	private LocationSensor vLocationSensor;
	private JoystickController vJoystickController1, vJoystickController2;
	private Motor vLeftFront, vLeftBack, vRightFront, vRightBack;
	private Sensor vLeftFrontEncoder, vLeftBackEncoder, vRightFrontEncoder, vRightBackEncoder;


    private ElapsedTime runtime = new ElapsedTime();

	private ArrayList<String> robotProgress;
	
	@Override
	public void init() {

        //MOTOR SETUP (with physical componenents, e.g. leftBack = hardwareMap.dcMotor.get("leftBack")
		leftFront = hardwareMap.dcMotor.get("leftFront");
		leftBack = hardwareMap.dcMotor.get("leftBack");
		rightFront = hardwareMap.dcMotor.get("rightFront");
		rightBack = hardwareMap.dcMotor.get("rightBack");


        //SERVO SETUP (with physical components, e.g. servo = hardwareMap....)


        //REVERSE RIGHT SIDE (If needed, e.g. rightFront.setDirection(DcMotor.Direction.REVERSE)





        //SENSOR SETUP e.g. colorSensor = hardwareMap.colorsensor.get("color"), sonar1 = hardwareMap.analogInput.get("sonar1"), liftEndStop1 = hardwareMap.digitalChannel.get("liftEndStop1")
		imu = MPU9250.getInstance(hardwareMap.deviceInterfaceModule.get("dim"), 0);
		lineSensor = hardwareMap.lightSensor.get("lineSensor");


        //FETCH VIRTUAL ROBOT FROM COMMAND INTERFACE
		robot = Command.ROBOT;

        //FETCH VIRTUAL COMPONENTS OF VIRTUAL ROBOT from robot. E.g. vDriveLeftMotor = robot.getDriveLeftMotor();
		vHeadingSensor = robot.getHeadingSensor();
		vPitchSensor = robot.getPitchSensor();
		vRollSensor = robot.getRollSensor();
		vLocationSensor = robot.getLocationSensor();
		vLineSensor = robot.getLineSensor();
		vLeftFront = robot.getLFMotor();
		vLeftBack = robot.getLBMotor();
		vRightFront = robot.getRFMotor();
		vRightBack = robot.getRBMotor();

        vJoystickController1 = robot.getJoystickController1();
        vJoystickController2 = robot.getJoystickController2();

		robotProgress = new ArrayList<String>();


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
		//set encoders e.g. vDriveRightMotorEncoder.setRawValue(-rightFront.getCurrentPosition())
			vLeftFrontEncoder.setRawValue(-leftFront.getCurrentPosition());
			vLeftBackEncoder.setRawValue(-leftBack.getCurrentPosition());
			vRightFrontEncoder.setRawValue(-rightFront.getCurrentPosition());
			vRightBackEncoder.setRawValue(-rightBack.getCurrentPosition());

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
		vLineSensor.setRawValue(lineSensor.getLightDetected());

		//Set more values, such as: vDriveRightMotorEncoder.setRawValue((-rightFront.getCurrentPosition());
		vLeftFrontEncoder.setRawValue(-leftFront.getCurrentPosition());
		vLeftBackEncoder.setRawValue(-leftBack.getCurrentPosition());
		vRightFrontEncoder.setRawValue(-rightFront.getCurrentPosition());
		vRightBackEncoder.setRawValue(-rightBack.getCurrentPosition());


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



		// Copy State of Motors and Servos E.g. leftFront.setPower(leftPower), Servo.setPosition(vServo.getPosition());
		leftFront.setPower(leftFrontPower);
		leftBack.setPower(leftBackPower);
		rightFront.setPower(rightFrontPower);
		rightBack.setPower(rightBackPower);

		for (int i = 0; i < robot.getProgress().size(); i++) {
			telemetry.addData("robot progress " + i, robot.getProgress().get(i));
		}
//then add additional ones, like telemetry.addData("left power", leftPower);
		telemetry.addData("leftFront Power", leftFrontPower);
		telemetry.addData("leftBack Power", leftBackPower);
		telemetry.addData("rightFront Power", rightFrontPower);
		telemetry.addData("rightBack Power", rightBackPower);

    }
	
	public void stop() {
		imu.close();
		t.interrupt();
	}

	public abstract void setGodThread();

    public void addPresets(){}

}
