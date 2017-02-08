package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.RedAutoGodThread;
import virtualRobot.godThreads.RedStatesGodThread;

/**
 * Created by 17osullivand on 10/6/16.
 * Update Thread for our Red autonomous
 */
@Autonomous(name = "Testing: Red Autonomous", group =  "Autonomous")
public class RedAutoUpdate extends UpdateThread {
    @Override
//    public void setGodThread() {
//        godThread = RedStatesGodThread.class;
//    }
      public void setGodThread() { godThread = RedAutoGodThread.class; }
    @Override
    public void addPresets() {

    }
}
