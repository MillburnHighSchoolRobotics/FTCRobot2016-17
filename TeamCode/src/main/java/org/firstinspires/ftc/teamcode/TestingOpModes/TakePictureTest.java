package org.firstinspires.ftc.teamcode.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.godThreads.TakePictureTestGod;

/**
 * Created by mehme_000 on 10/7/2016.
 */

@Autonomous(name ="Sensor: Camera", group="Sensor")
public class TakePictureTest extends OpMode {
    public void init(){

    }

    public void loop(){
        TakePictureTestGod tp = new TakePictureTestGod();
        AtomicBoolean redIsLeft = tp.getRedIsLeft();
        telemetry.addData("redIsLeft: ", redIsLeft);
    }
}
