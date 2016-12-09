package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import virtualRobot.GodThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.godThreads.PIDLineFollowerGod;
import virtualRobot.godThreads.PIDTesterGodThread;
import virtualRobot.godThreads.TeleopGodThread;

/**
 * Created by 17osullivand on 11/9/16.
 */

public class CreateVuforia implements Runnable {
    Class<? extends GodThread> godThread;
    GodThread vuforiaEverywhere;
    Thread t;
    private boolean good = false;

    public CreateVuforia(Class<? extends GodThread> g, GodThread vuforiaEverywhere, Thread t) {
        godThread = g;
        this.vuforiaEverywhere = vuforiaEverywhere;
        this.t = t;
    }


    @Override
    public void run() {
        try {
            if (!godThread.equals(TeleopGodThread.class) ){//&& !godThread.equals(PIDTesterGodThread.class) && !godThread.equals(PIDLineFollowerGod.class)) {
                VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
                params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
                params.vuforiaLicenseKey = "AdVGalv/////AAAAGYhiDIdk+UI+ivt0Y7WGvUJnm5cKX/lWesW2pH7gnK3eOLTKThLekYSO1q65ttw7X1FvNhxxhdQl3McS+mzYjO+HkaFNJlHxltsI5+b4giqNQKWhyKjzbYbNw8aWarI5YCYUFnyiPPjH39/CbBzzFk3G2RWIzNB7cy4AYhjwYRKRiL3k33YvXv0ZHRzJRkMpnytgvdv5jEQyWa20DIkriC+ZBaj8dph8/akyYfyD1/U19vowknmzxef3ncefgOZoI9yrK82T4GBWazgWvZkIz7bPy/ApGiwnkVzp44gVGsCJCUFERiPVwfFa0SBLeCrQMrQaMDy3kOIVcWTotFn4m1ridgE5ZP/lvRzEC4/vcuV0";
                Log.d("lalala", "location1");
                VuforiaLocalizerImplSubclass vuforia = new VuforiaLocalizerImplSubclass(params);
                vuforiaEverywhere = godThread.newInstance();
                vuforiaEverywhere.setVuforia(vuforia);
                t = new Thread(vuforiaEverywhere);
            } else {
                t = new Thread(godThread.newInstance());
                Log.d("lalala", "location2");
            }
        } catch (InstantiationException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        }
        good = true;
    }

    public boolean getGood() { //son
        return good;
    }
}