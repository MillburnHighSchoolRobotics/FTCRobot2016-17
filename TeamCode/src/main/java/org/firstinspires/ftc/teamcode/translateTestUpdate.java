package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.testingTranslateGodThread;

/**
 * Created by ethachu19 on 10/11/2016.
 */
@Autonomous(name = "Testing: Translate", group = "Testing")
public class translateTestUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = testingTranslateGodThread.class;
    }
}
