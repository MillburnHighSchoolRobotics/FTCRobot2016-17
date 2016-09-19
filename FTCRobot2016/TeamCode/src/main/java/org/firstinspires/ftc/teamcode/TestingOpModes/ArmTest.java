package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by shant on 10/15/2015.
 */
public class ArmTest extends OpMode {
    //Servo tape;
    Servo normal;

    double normalcurPos = 0;
    final double normaldelta = .05;
    //DcMotor motorSweeper;
    double currentPos = .25;
    final double servoDelta = 0.00115;

    final double ARM_TOP_CAP = 0.3;
    final double ARM_BOTTOM_CAP = 0.2;

    @Override
    public void init() {
        //tape = hardwareMap.servo.get("tape");
        normal = hardwareMap.servo.get("normal");

        //Log.d("robot", "servo ports: " + tape.getPortNumber());

    }

    @Override
    public void loop() {
        if (gamepad1.a && gamepad1.b) {

        }
        else {
            if (gamepad1.a) {
                currentPos += servoDelta;
                normalcurPos += normaldelta;
                telemetry.addData("key", "adding value");
                Log.d("robot", "adding value");

            }
            if (gamepad1.b) {
                normalcurPos -= normaldelta;
                currentPos -= servoDelta;
                telemetry.addData("key", "subtracting value");
                Log.d("robot", "subtracting value");

            }

        }

            currentPos = Range.clip(currentPos, ARM_BOTTOM_CAP, ARM_TOP_CAP);
        /*if (currentPos == ARM_BOTTOM_CAP) {
            motorSweeper.setPower(-.5);
        } else {
            motorSweeper.setPower(0);
        }*/
        normalcurPos = Math.max(Math.min(normalcurPos, 1), 0);
        normal.setPosition(normalcurPos);
        //tape.setPosition(currentPos);
        Log.d("robot", "setting value to " + currentPos);
        telemetry.addData("servo value: ", normalcurPos);
    }
}
