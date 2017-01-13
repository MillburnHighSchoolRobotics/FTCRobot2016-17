package virtualRobot.components;

import virtualRobot.utils.MathUtils;

/**
 * Created by Alex on 10/1/2015.
 * Virtual ContinousRotationServo. Set Position
 */
public class ContinuousRotationServo extends Servo {

    double speed = 0;

    public synchronized double getSpeed () {
    	double retVal = 0;
    	synchronized (this) {
    		retVal = speed;
    	}
        return retVal;
    }

    public synchronized void setSpeed (double speed) {
    	synchronized (this) {
    		this.speed = speed;
    	}
    }

    public synchronized void setPositionDegrees(double degrees) {
        setPosition(degrees/360);
    }

    public synchronized double getPositionDegrees() {
        return getPosition() * 360;
    }
}
