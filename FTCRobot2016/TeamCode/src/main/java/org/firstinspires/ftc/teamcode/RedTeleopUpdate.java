package com.qualcomm.ftcrobotcontroller.opmodes;

import virtualRobot.godThreads.RedTeleopGodThread;

/**
 * Created by shant on 2/26/2016.
 */
public class RedTeleopUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RedTeleopGodThread.class;
    }

    @Override
    public void addPresets() {

    }
}
