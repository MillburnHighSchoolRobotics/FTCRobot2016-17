package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Servo;

import virtualRobot.godThreads.BlueAutoGodThread;

/**
 * Created by Yanjun on 11/28/2015.
 */
public class BlueClimberDumpUpdate extends UpdateThread {
    @Override
    public void setGodThread() {

        godThread = BlueAutoGodThread.class;
    }

    @Override
    public void addPresets() {
        basket.setDirection(Servo.Direction.REVERSE);
        scoop.setDirection(Servo.Direction.REVERSE);
        gate.setDirection(Servo.Direction.REVERSE);
    }
}
