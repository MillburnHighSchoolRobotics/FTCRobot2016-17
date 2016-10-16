package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.BlueAutoGodThread;

/**
 * Created by 17osullivand on 10/6/16.
 */
@Autonomous (name = "Testing: Blue Autonomous", group =  "Autonomous")
public class BlueAutoUpdate extends UpdateThread{
    @Override
    public void setGodThread() {
        godThread = BlueAutoGodThread.class;
    }
    @Override
    public void addPresets() {

    }
}
