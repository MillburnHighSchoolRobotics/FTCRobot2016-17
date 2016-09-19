package com.qualcomm.ftcrobotcontroller.opmodes.TestingOpModes;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Yanjun on 11/24/2015.
 */
public class IMUTest extends OpMode {

    //MPU9250 imu;
    AHRS imu;
    //private navXPerformanceMonitor navx_perfmon;
    private byte sensor_update_rate_hz = 40;
    private ElapsedTime runtime = new ElapsedTime();
    private final int NAVX_DIM_I2C_PORT = 5;

    @Override
    public void init() {
        imu = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get("dim"),
                NAVX_DIM_I2C_PORT,
                AHRS.DeviceDataType.kProcessedData,
                sensor_update_rate_hz);
    }

    public void init_loop() {
        imu.zeroYaw();
        //imu.zeroPitch();
        //imu.zeroRoll();
        telemetry.addData("navX Op Init Loop", runtime.toString());
    }

    public void start() {

    }

    @Override
    public void loop() {
        telemetry.addData("yaw", imu.getYaw());
        telemetry.addData("pitch", imu.getPitch());
        telemetry.addData("roll", imu.getRoll());
    }
}
