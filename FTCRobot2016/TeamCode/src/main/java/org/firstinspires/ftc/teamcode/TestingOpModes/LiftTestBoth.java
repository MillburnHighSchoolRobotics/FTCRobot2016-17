package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import virtualRobot.PIDController;

/**
 * Created by shant on 2/14/2016.
 */
public class LiftTestBoth extends OpMode {
    DcMotor liftLeft;
    DcMotor liftRight;
    int initLiftLeftEncoder;
    int initLiftRightEncoder;

    @Override
    public void init() {
        liftLeft = hardwareMap.dcMotor.get("left");
        liftRight = hardwareMap.dcMotor.get("right");
        liftLeft.setDirection(DcMotor.Direction.REVERSE);
        initLiftLeftEncoder = liftLeft.getCurrentPosition();
        initLiftRightEncoder = liftRight.getCurrentPosition();
    }

    @Override
    public void loop() {
        //PID CONTROLLER TO KEEP LIFT ARMS AT THE SAME EXTENSION
        //TODO TUNE THIS PID CONTROLLER
        double liftLeftPower;
        double liftRightPower;
        PIDController liftController = new PIDController(0.005, 0, 0, 0);
        liftController.setTarget(0);
        double liftPIDOut = liftController.getPIDOutput((liftLeft.getCurrentPosition() - initLiftLeftEncoder) - (liftRight.getCurrentPosition() - initLiftRightEncoder));
        //liftPIDOut = 0;
        if (gamepad1.a && !(gamepad1.a && gamepad1.b)) {
            liftLeftPower = 0.6 + liftPIDOut;
            liftRightPower = 0.6 - liftPIDOut;

        }
        else if (gamepad1.b && !(gamepad1.a && gamepad1.b)) {
            liftLeftPower = -0.6 + liftPIDOut;
            liftRightPower = -0.6 - liftPIDOut;
        }
        else {
            liftLeftPower = 0;
            liftRightPower = 0;
        }

        liftLeftPower = Math.max(Math.min(liftLeftPower, 1), -1);
        liftRightPower = Math.max(Math.min(liftRightPower, 1), -1);


        liftLeft.setPower(liftLeftPower);
        liftRight.setPower(liftRightPower);

        telemetry.addData("pid value", liftPIDOut);
        telemetry.addData("left encoder", liftLeft.getCurrentPosition() - initLiftLeftEncoder);
        telemetry.addData("right encoder", liftRight.getCurrentPosition() - initLiftRightEncoder);
        telemetry.addData("left power", liftLeftPower);
    }
}
