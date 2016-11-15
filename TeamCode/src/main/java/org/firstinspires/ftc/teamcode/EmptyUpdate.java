package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.EmptyGod;

/**
 * Created by ethachu19 on 11/4/2016.
 */

@Autonomous(name = "Init: Empty Update", group = "Init")
public class EmptyUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        this.godThread = EmptyGod.class;
    }
}
