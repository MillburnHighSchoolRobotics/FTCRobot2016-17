package org.firstinspires.ftc.teamcode.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.sensors.ParallaxPingSensor;
import com.sensors.SRF08;

/**
 * Created by ethachu19 on 11/1/2016.
 */

@Autonomous(name = "Sensor: Test Ping", group = "Sensor")
public class ParallaxTest extends OpMode {
    SRF08 ping;

    @Override
    public void init() {
        ping = new SRF08(hardwareMap.i2cDevice.get("sonarLeft"));
    }

    @Override
    public void loop() {
        telemetry.addData("Ping 1: ",ping.getEcho(1));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
