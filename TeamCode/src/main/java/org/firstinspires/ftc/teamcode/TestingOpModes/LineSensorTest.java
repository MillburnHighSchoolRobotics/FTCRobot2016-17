package org.firstinspires.ftc.teamcode.TestingOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;


/**
 * Created by 17osullivand on 10/5/16.
 */
//note that to test componenets just create a basic OpMode
;
public class LineSensorTest extends OpMode {
   LightSensor linetest;
    @Override
    public void init() {
        linetest = hardwareMap.lightSensor.get("lineSensor");

        //ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonic");
        //colorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {
        telemetry.addData("LineSense: ", linetest.getLightDetected());
        /*telemetry.addData("Sonar: ", ultrasonicSensor.getUltrasonicLevel());
        telemetry.addData("Color Red: ", colorSensor.red());
        telemetry.addData("Color Green: ", colorSensor.green());
        telemetry.addData("Color Blue: ", colorSensor.blue());
        telemetry.addData("Color Alpha: ", colorSensor.alpha());*/

    }

}
