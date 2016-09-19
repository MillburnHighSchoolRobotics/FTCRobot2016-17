package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Yanjun on 1/16/2016.
 */
public class WheelTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor a, b, c, d;
        a = hardwareMap.dcMotor.get("a");
        b = hardwareMap.dcMotor.get("b");
        c = hardwareMap.dcMotor.get("c");
        d = hardwareMap.dcMotor.get("d");

        b.setDirection(DcMotor.Direction.REVERSE);
        d.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        a.setPower(1);
        b.setPower(1);
        c.setPower(1);
        d.setPower(1);

        while (true) {
            waitOneFullHardwareCycle();
        }
    }
}
