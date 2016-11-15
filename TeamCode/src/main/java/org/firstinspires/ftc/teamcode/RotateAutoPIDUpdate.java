package org.firstinspires.ftc.teamcode;

import virtualRobot.godThreads.RotateAutoPIDGod;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class RotateAutoPIDUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RotateAutoPIDGod.class;
    }
}
