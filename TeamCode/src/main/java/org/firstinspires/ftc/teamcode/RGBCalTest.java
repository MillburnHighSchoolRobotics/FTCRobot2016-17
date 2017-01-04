package org.firstinspires.ftc.teamcode;

import virtualRobot.godThreads.RGBCalTestGod;

/**
 * Created by ethachu19 on 1/4/2017.
 */

public class RGBCalTest extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RGBCalTestGod.class;
    }
}
