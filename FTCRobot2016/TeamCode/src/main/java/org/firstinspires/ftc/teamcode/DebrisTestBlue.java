package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by shant on 2/26/2016.
 */
public class DebrisTestBlue extends LinearOpMode {
    Servo basket;
    Servo gate;
    Servo scoop;

    @Override
    public void runOpMode() throws InterruptedException {
        basket = hardwareMap.servo.get("basket");
        gate = hardwareMap.servo.get("gate");
        scoop = hardwareMap.servo.get("scoop");

        sleep (500);

        scoop.setPosition(SCOOP_DOWN);
        gate.setPosition(GATE_OPEN);
        basket.setPosition(BASKET_UP);

        sleep (1500);

        basket.setPosition(BASKET_DOWN);
        gate.setPosition(GATE_CLOSED);
        scoop.setPosition(SCOOP_UP);

        sleep (1500);
    }

    private final double SCOOP_UP = 0.7;
    private final double SCOOP_DOWN = 0;
    private final double GATE_OPEN = 0.6;
    private final double GATE_CLOSED = 0;
    private final double BASKET_UP = 0.6;
    private final double BASKET_DOWN = 0;
}
