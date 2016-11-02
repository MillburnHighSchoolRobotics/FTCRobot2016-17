package com.sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

/**
 * Created by ethachu19 on 10/31/2016.
 */

public class ParallaxPingSensor extends DigitalChannel {

    public ParallaxPingSensor(DigitalChannelController controller, int channel) {
        super(controller,channel);
    }

    public double getResponseTime() {
        setMode(DigitalChannelController.Mode.OUTPUT);
        try {
            setState(false);
            Thread.sleep(0, 2000);
            setState(true);
            Thread.sleep(0, 5000);
            setState(false);
        } catch (InterruptedException ex) {
            return -1;
        }

        long start = System.nanoTime();
        setMode(DigitalChannelController.Mode.INPUT);
        while (!getState()) {}
        return (System.nanoTime() - start)/1000;
    }

    public double getDistanceCM() {
        return getResponseTime() / 29 / 2;
    }

    public double getDistanceInches() {
        return getResponseTime() / 74 / 2;
    }
}
