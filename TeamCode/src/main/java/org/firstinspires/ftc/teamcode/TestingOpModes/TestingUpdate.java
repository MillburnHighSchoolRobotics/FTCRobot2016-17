package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.ftcrobotcontroller.opmodes.UpdateThread;

import virtualRobot.godThreads.TestingGodThread;

/**
 * Created by shant on 1/15/2016.
 */
public class TestingUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = TestingGodThread.class;
    }
}
