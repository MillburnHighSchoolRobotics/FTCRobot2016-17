package virtualRobot.components;
//com.qualcomm.robotcore.hardware.Servo;import com.qualcomm.robotcore.hardware.Servo;


/**
 * Created by Alex on 9/30/2015.
 */
public class Servo {

    private volatile double position;

    public synchronized double getPosition() {
    	double retVal = 0;
    	synchronized (this) {
    		retVal = position;
    	}
        return retVal;
    }


    public synchronized void setPosition(double position) {
    	position = Math.max (Math.min(position, 1), 0);
        synchronized (this) {

            this.position = position;
    	}
    }

}
