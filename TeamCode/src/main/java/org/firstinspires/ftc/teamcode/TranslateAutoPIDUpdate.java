package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.TranslateAutoPIDGod;

/**
 * Created by ethachu19 on 1/13/2017.
 */

@Autonomous(name = "AutoPID: Translate", group = "AutoPID")
public class TranslateAutoPIDUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = TranslateAutoPIDGod.class;
    }
}
