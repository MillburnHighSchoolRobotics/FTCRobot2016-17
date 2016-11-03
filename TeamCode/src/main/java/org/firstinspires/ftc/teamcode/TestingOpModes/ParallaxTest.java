package org.firstinspires.ftc.teamcode.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.sensors.ParallaxPingSensor;

/**
 * Created by ethachu19 on 11/1/2016.
 */

@Autonomous(name = "Sensor: Test Ping", group = "Sensor")
public class ParallaxTest extends OpMode {
    ParallaxPingSensor ping;

    @Override
    public void init() {
        ping = new ParallaxPingSensor(hardwareMap.digitalChannel.get("sonarLeft"));
    }

    @Override
    public void loop() {
        telemetry.addData("Ping 1: ",ping.getDistanceCM());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
