package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Yanjun on 2/6/2016.
 */
public class ServoCal extends OpMode {

    Servo servo;
    double pos;

    @Override
    public void init() {
        servo = hardwareMap.servo.get("servo");
        pos = 0;
    }

    @Override
    public void loop() {
        if (gamepad1.a) pos -= 0.02;
        if (gamepad1.b) pos += 0.02;

        pos = Math.max(Math.min(pos, 1), 0);

        servo.setPosition(pos);
    }
}
