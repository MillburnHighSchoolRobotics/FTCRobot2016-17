package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

/**
 * Created by Yanjun on 12/9/2015.
 */
public class SonarTest extends OpMode {

    AnalogInput sonar;
    //UltrasonicSensor ultrasonicSensor;
    //ColorSensor colorSensor;


    @Override
    public void init() {
        sonar = hardwareMap.analogInput.get("sonar1");
        //ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonic");
        //colorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {
        telemetry.addData("Sonar: ", sonar.getValue());
        /*telemetry.addData("Sonar: ", ultrasonicSensor.getUltrasonicLevel());
        telemetry.addData("Color Red: ", colorSensor.red());
        telemetry.addData("Color Green: ", colorSensor.green());
        telemetry.addData("Color Blue: ", colorSensor.blue());
        telemetry.addData("Color Alpha: ", colorSensor.alpha());*/

    }
}
