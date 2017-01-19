package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.godThreads.RGBCalTestGod;

/**
 * Created by ethachu19 on 1/4/2017.
 */

@Autonomous(name = "Sensor: RGB Test", group = "Sensor")
public class RGBCalTest extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = RGBCalTestGod.class;
    }
}
