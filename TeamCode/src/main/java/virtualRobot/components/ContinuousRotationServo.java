package virtualRobot.components;

import virtualRobot.utils.MathUtils;

/**
 * Created by Alex on 10/1/2015.
 */
public class ContinuousRotationServo extends Servo {
//
//    public synchronized double getSpeed () {
//    	double retVal = 0;
//    	synchronized (this) {
//    		retVal = (getPosition() - 90) / 90.0;
//    	}
//        return retVal;
//    }
//
//    public synchronized void setSpeed (double speed) {
//    	synchronized (this) {
//    		setPosition((speed + 1) * 180);
//    	}
//    }
    public synchronized void setPositionDegrees(double degrees) {
        setPosition(degrees/360);
    }

    public synchronized double getPositionDegrees() {
        return getPosition() * 360;
    }
}
