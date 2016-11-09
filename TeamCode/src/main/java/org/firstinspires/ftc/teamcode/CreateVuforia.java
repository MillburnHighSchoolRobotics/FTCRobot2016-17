package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import virtualRobot.GodThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
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
            if (!godThread.equals(TeleopGodThread.class) && !godThread.equals(PIDTesterGodThread.class)) {
                VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
                params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
                params.vuforiaLicenseKey = "AcXbD9X/////AAAAGVpq1gdfDkIPp+j5hv1iV5RZXLWAWV4F7je9gks+8lHhZb6mwCj7xy9mapHP6sKO9OrPv5kVQDXhB+T+Rn7V7GUm4Ub4rmCanqv4frx8gT732qJUnTEj9POMufR9skjlXSEODbpThxrLCPqobHeAeSA5dUmUik3Rck0lcwhElw5yOBN45iklYnvC9GpPRv128ALcgt9Zpw/shit0erKmuyrT62NRUKgoHNMm5xV/Xqj8Vgwke8ESap+nK7v+6lx35vDZ6ISNDVMMM8h0VqeL0745MNPJoI1vgiNRo30R7WwtPYME44koOrWMUIxMXghtqxq7AfFxb6sbin0i5KSUJWtLsqmZOrAXxjxdUwY8f8tw";
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