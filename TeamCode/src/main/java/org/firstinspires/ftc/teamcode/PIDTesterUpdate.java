package org.firstinspires.ftc.teamcode;



import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.PIDTesterGodThread;

/**
 * Created by Yanjun on 11/28/2015.
 * update thread used for tuning the pid
 */
@Autonomous(name = "Testing: PID", group = "Testing")
public class PIDTesterUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = PIDTesterGodThread.class;
    }
}
