package org.firstinspires.ftc.teamcode;

import virtualRobot.godThreads.TranslateAutoPIDGod;

/**
 * Created by ethachu19 on 1/13/2017.
 */

public class TranslateAutoPIDUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = TranslateAutoPIDGod.class;
    }
}
