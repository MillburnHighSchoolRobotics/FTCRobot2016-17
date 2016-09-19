package com.qualcomm.ftcrobotcontroller.opmodes;

import virtualRobot.godThreads.RedAutoGodThread;

/**
 * Created by Yanjun on 11/28/2015.
 */
public class RedAutoUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RedAutoGodThread.class;
    }

    @Override
    public void addPresets() {

    }
}
