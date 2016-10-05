package org.firstinspires.ftc.teamcode;



import virtualRobot.godThreads.PIDTesterGodThread;

/**
 * Created by Yanjun on 11/28/2015.
 */
public class PIDTesterUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = PIDTesterGodThread.class;
    }
}
