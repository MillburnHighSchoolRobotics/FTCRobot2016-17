package virtualRobot.components;

/**
 * Created by Alex on 10/1/2015.
 */
public class ContinuousRotationServo extends Servo {

    public synchronized double getSpeed () {
    	double retVal = 0;
    	synchronized (this) {
    		retVal = (getPosition() - 90) / 90.0;
    	}
        return retVal;
    }

    public synchronized void setSpeed (double speed) {    
    	synchronized (this) {
    		setPosition((speed + 1) * 180);
    	}

    }
}
