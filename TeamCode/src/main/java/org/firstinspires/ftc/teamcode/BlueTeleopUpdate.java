package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Servo;

import virtualRobot.godThreads.TeleopGodThread;

/**
 * _____ ______   ___  ___  ________
 * |\   _ \  _   \|\  \|\  \|\   ____\             .-""-.
 * \ \  \\\__\ \  \ \  \\\  \ \  \___|_           /[] _ _\
 * \ \  \\|__| \  \ \   __  \ \_____  \         _|_o_LII|_
 * \ \  \    \ \  \ \  \ \  \|____|\  \       / | ==== | \
 * \ \__\    \ \__\ \__\ \__\____\_\  \      |_| ==== |_|
 * \|__|     \|__|\|__|\|__|\_________\      ||" ||  ||
 * \|_________|      ||LI  o ||
 * ||'----'||
 * /__|    |__\
 * <p>
 * Created by shant on 11/27/2015.
 */
public class BlueTeleopUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = TeleopGodThread.class;
    }

    @Override
    public void addPresets() {
        basket.setDirection(Servo.Direction.REVERSE);
        scoop.setDirection(Servo.Direction.REVERSE);
        gate.setDirection(Servo.Direction.REVERSE);
    }

}
