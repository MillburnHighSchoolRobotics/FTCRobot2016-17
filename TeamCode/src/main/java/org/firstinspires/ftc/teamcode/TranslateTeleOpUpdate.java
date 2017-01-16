package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import virtualRobot.godThreads.TranslateTeleopPIDGod;

/**
 * Created by ethachu19 on 1/15/2017.
 */

@TeleOp(name = "PID: Translate", group = "PID")
public class TranslateTeleOpUpdate extends UpdateThread {
    @Override
    public void setGodThread() {
        godThread = TranslateTeleopPIDGod.class;
    }
}
