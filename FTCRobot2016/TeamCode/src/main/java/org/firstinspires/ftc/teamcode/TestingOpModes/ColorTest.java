package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.hardware.AdafruitI2cColorSensor;
import com.qualcomm.hardware.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

/**
 * Created by Yanjun on 11/25/2015.
 */
public class ColorTest extends OpMode {
    DigitalChannel led;
    ColorSensor colorSensor;

    @Override
    public void init() {
        led = hardwareMap.digitalChannel.get("led");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        AdafruitI2cColorSensor colorSensor2 = (AdafruitI2cColorSensor) hardwareMap.colorSensor.get("colorSensor");
        led.setMode(DigitalChannelController.Mode.OUTPUT);
    }

    public void start() {
        led.setState(true);
    }

    @Override
    public void loop() {

        int red = colorSensor.red();
        int blue = colorSensor.blue();
        int green = colorSensor.green();

        red &= 0x0000ffff;
        blue &= 0x0000ffff;
        green &= 0x0000ffff;

        red >>= 8;
        blue >>= 8;
        green >>= 8;

        int color = 0;
        color += red << 16;
        color += green << 8;
        color += blue;
        telemetry.addData("le red", red);
        telemetry.addData("le green", green);
        telemetry.addData("le blue", blue);
        telemetry.addData("le rgb", String.format("%06x", color));
    }
}
