package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.RotateAutoPIDGod;

/**
 * Created by ethachu19 on 11/14/2016.
 */


@Autonomous (name = "AutoPID: Rotate", group = "AutoPID")
public class RotateAutoPIDUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RotateAutoPIDGod.class;
    }
}
