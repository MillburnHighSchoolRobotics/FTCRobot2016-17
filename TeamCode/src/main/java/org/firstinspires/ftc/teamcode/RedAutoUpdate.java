package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.RedAutoGodThread;

/**
 * Created by 17osullivand on 10/6/16.
 */
@Autonomous(name = "Testing: Red Autonomous", group =  "Autonomous")
public class RedAutoUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RedAutoGodThread.class;
    }
    @Override
    public void addPresets() {

    }
}
