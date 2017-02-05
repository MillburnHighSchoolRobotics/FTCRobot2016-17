package org.firstinspires.ftc.teamcode.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by 17osullivand on 2/4/17.
 */

@Autonomous(name = "Sensor: Motor Run Using Encoders", group = "Sensor")
public class EncoderRunTest extends OpMode {
    DcMotor motor;
    double lastSpeed;
    double lastEncoder;
    double lastTime;

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("flywheel");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void init_loop() {
        lastEncoder = motor.getCurrentPosition();
        lastTime = System.currentTimeMillis();
    }

    public double getSpeed() {
        double time = System.currentTimeMillis() - lastTime;
        double diffEncoder = motor.getCurrentPosition() - lastEncoder;
        if (time > 5 && diffEncoder > 20) {
            lastSpeed = diffEncoder/time * 1000;
            lastEncoder = motor.getCurrentPosition();
            lastTime = System.currentTimeMillis();
            return lastSpeed;
        }
        return lastSpeed;
    }

    @Override
    public void loop() {
        motor.setPower(1.0);
        telemetry.addData("Encoder: ", motor.getCurrentPosition());
        telemetry.addData("Speed: ", getSpeed());
    }
}
