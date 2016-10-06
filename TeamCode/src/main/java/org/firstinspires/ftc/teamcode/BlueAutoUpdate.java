package org.firstinspires.ftc.teamcode;

import virtualRobot.godThreads.BlueAutoGodThread;

/**
 * Created by 17osullivand on 10/6/16.
 */
public class BlueAutoUpdate extends UpdateThread{
    @Override
    public void setGodThread() {
        godThread = BlueAutoGodThread.class;
    }
    @Override
    public void addPresets() {

    }
}
