package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.PIDLineFollowerGod;
import virtualRobot.godThreads.RedAutoGodThread;

/**
 * Created by ethachu19 on 10/27/2016.
 * update thread used for testing ethan's algos
 */
@Autonomous(name = "Testing PIDLine and java garbage", group =  "Autonomous")
public class PIDLineFollowerUpdate extends UpdateThread {

    @Override
    public void setGodThread() {
        godThread = PIDLineFollowerGod.class;
    }

}
