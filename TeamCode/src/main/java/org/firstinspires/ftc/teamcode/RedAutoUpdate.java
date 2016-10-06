package org.firstinspires.ftc.teamcode;

import virtualRobot.godThreads.RedAutoGodThread;

/**
 * Created by 17osullivand on 10/6/16.
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
