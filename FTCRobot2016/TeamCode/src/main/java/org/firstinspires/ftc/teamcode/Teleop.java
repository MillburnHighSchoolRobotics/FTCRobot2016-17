package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by shant on 2/28/2016.
 */
public class Teleop extends OpMode {
    private DcMotor rightFront, rightBack, leftFront, leftBack, tapeMeasureMotor;
    private Servo backShieldRight, backShieldLeft, tapeMeasureServo, dumper;
    private Servo ziplineRight, ziplineLeft;

    private boolean rightIsDown, leftIsDown;

    private double tapeCurPos = 0.35;
    private double tapeTop = 0.45;
    private double tapeBot = 0.2;
    private double tapeDelta = 0.0023;
    @Override
    public void init() {
        //MOTOR SETUP
        rightFront = hardwareMap.dcMotor.get("rightFront");
        rightBack = hardwareMap.dcMotor.get("rightBack");
        leftFront = hardwareMap.dcMotor.get("leftFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        tapeMeasureMotor = hardwareMap.dcMotor.get("tapeMeasureMotor");

        ziplineLeft = hardwareMap.servo.get("basket");
        ziplineRight = hardwareMap.servo.get("gate");
        ziplineRight.setDirection(Servo.Direction.REVERSE);

        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        dumper = hardwareMap.servo.get("dumper");
        backShieldLeft = hardwareMap.servo.get("backShieldLeft");
        backShieldRight = hardwareMap.servo.get("backShieldRight");
        backShieldRight.setDirection(Servo.Direction.REVERSE);
        tapeMeasureServo = hardwareMap.servo.get("tapeMeasure");
    }

    @Override
    public void loop() {
        double leftJoystick = -gamepad1.left_stick_y;
        double rightJoystick = -gamepad1.right_stick_y;

        rightBack.setPower(rightJoystick);
        rightFront.setPower(rightJoystick);

        leftBack.setPower(leftJoystick);
        leftFront.setPower(leftJoystick);

        if (gamepad1.dpad_down) {
            backShieldLeft.setPosition(0);
            backShieldRight.setPosition(0);
        }
        if (gamepad1.dpad_up) {
            backShieldRight.setPosition(1);
            backShieldLeft.setPosition(1);
        }

        if (gamepad1.right_trigger > 0.5) {
            tapeMeasureMotor.setPower(-1);
        }
        if (gamepad1.left_trigger > 0.5) {
            tapeMeasureMotor.setPower(1);
        }
        if (gamepad1.right_bumper) {
            tapeCurPos += tapeDelta;
        }
        if (gamepad1.left_bumper) {
            tapeCurPos -= tapeDelta;
        }
        tapeCurPos = Math.min(Math.max(tapeCurPos, tapeBot), tapeTop);
        tapeMeasureServo.setPosition(tapeCurPos);

        if (gamepad1.x) {
            dumper.setPosition(0);
        }
        if (gamepad1.y) {
            dumper.setPosition(1);
        }

        if (gamepad1.a) {
            rightIsDown = !rightIsDown;
        }

        if (gamepad1.b) {
            leftIsDown = !leftIsDown;
        }

        ziplineLeft.setPosition(leftIsDown ? 0 : 1);
        ziplineRight.setPosition(rightIsDown ? 0 : 1);

    }
}
