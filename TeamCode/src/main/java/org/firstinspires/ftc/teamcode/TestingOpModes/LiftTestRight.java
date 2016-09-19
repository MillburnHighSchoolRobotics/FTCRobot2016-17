package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by shant on 2/13/2016.
 */
public class LiftTestRight extends OpMode {
    DcMotor motor1;
    double initMotorEncoder;
    double motorEncoder = 0;
    @Override
    public void init() {
        motor1 = hardwareMap.dcMotor.get("butts");
        initMotorEncoder = motor1.getCurrentPosition();
    }

    @Override
    public void loop() {
        if (gamepad1.a && gamepad1.b) {

        }
        else {
            if (gamepad1.a) {
                motor1.setPower(1);
            }
            else if (gamepad1.b) {
                motor1.setPower(-1);
            }
            else {
                motor1.setPower(0);
            }
        }
        motorEncoder = motor1.getCurrentPosition() - initMotorEncoder;

        telemetry.addData("motor Encoder", motorEncoder);

    }
}
