package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by shant on 2/20/2016.
 */
public class TeleopDiagnosticsOpMode extends OpMode {

    private DcMotor rightFront, rightBack, leftFront, leftBack, reaper, liftRight, liftLeft, tapeMeasureMotor;
    private Servo backShieldRight, backShieldLeft, tapeMeasureServo, flipperRight, flipperLeft, basket, gate, dumper, scoop;

    //dumper servo caps and deltas
    double dumperCurrentPos = 0;
    double dumperDelta = 0.005;

    //TAPE Measure Servo caps and delta
    double tapeMeasureCurrentPos = 0.25;
    final double servoDelta = 0.0023; //0.00115
    final double ARM_BOTTOM_CAP = 0.2;
    final double ARM_TOP_CAP = 0.3;

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

        //REVERSE RIGHT SIDE
        backShieldRight.setDirection(Servo.Direction.REVERSE);
        flipperRight.setDirection(Servo.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        liftRight.setDirection(DcMotor.Direction.REVERSE);
    }



    @Override
    public void loop() {
        rightFront.setPower(-gamepad1.right_stick_y);
        leftFront.setPower(-gamepad1.left_stick_x);
        rightBack.setPower(-gamepad2.right_stick_y);
        leftBack.setPower(-gamepad2.right_stick_y);

        /** 2. Tape Measure Platform - Bumpers*/
        if (gamepad1.right_bumper && !gamepad1.left_bumper) {
            tapeMeasureCurrentPos += servoDelta;
        }

        if (!gamepad1.right_bumper && gamepad1.left_bumper) {
            tapeMeasureCurrentPos -= servoDelta;
        }

        tapeMeasureCurrentPos = Math.max(Math.min(tapeMeasureCurrentPos, ARM_TOP_CAP), ARM_BOTTOM_CAP);
        tapeMeasureServo.setPosition(tapeMeasureCurrentPos);


    }
}
