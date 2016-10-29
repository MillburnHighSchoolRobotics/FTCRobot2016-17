package org.firstinspires.ftc.teamcode.TestingOpModes;

//import com.kauailabs.navx.ftc.MPU9250;
import com.kauailabs.navx.ftc.MPU9250;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;


/**
 * Created by 17osullivand on 10/5/16.
 */
//note that to test componenets just create a basic OpMode
;
@Autonomous(name ="Sensor: Testing All Sensors", group="Sensor")
public class LineSensorTest extends OpMode {
   AnalogInput linetest;
    UltrasonicSensor sonar1;
    private MPU9250 imu;
    @Override
    public void init() {
        linetest = hardwareMap.analogInput.get("lineSensor");
        sonar1 = hardwareMap.ultrasonicSensor.get("sonarLeft");
        imu = MPU9250.getInstance(hardwareMap.deviceInterfaceModule.get("dim"), 0 );
        imu.zeroPitch();
        imu.zeroRoll();
        imu.zeroYaw();
        //ultrasonicSensor = hardwareMap.ultrasonicSensor.get("ultrasonic");
        //colorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {
        telemetry.addData("LineSense: ", linetest.getVoltage());
     double headingAngle = imu.getIntegratedYaw();
     double Pitch = imu.getIntegratedPitch();
      double Roll = imu.getIntegratedRoll();
    telemetry.addData("Angle, Pitch, Roll: ", headingAngle + ", " + Pitch + " ," + Roll);
        telemetry.addData("UltraSound: ", sonar1.getUltrasonicLevel());

    }

}
