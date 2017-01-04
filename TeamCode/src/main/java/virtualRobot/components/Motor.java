package virtualRobot.components;

/*
The virtual Motor component
 */
public class Motor {

	public static final double MAX_POWER = 1;
	public static final double STATIONARY = 0;

    private volatile double power;

    public Motor() {
        power = 0;
    }

    public synchronized double getPower () {
    	double retVal = 0;
    	synchronized (this) {
    		retVal = power;
    	}
        return retVal;
    }

    public synchronized void setPower(double newPower) {
    	
    	synchronized (this) {
    	
	        power = newPower;
	        if (power > MAX_POWER) {
	            power = 1;
	        }
	
	        if (power < -MAX_POWER) {
	            power = -1;
	        }
    	}
    }

	public void setPosition(Sensor encoder, double target){
		target %= 1120;
		double curr = (Math.abs(encoder.getValue())%1120 - target);
		while (Math.abs(curr) < 5) {
			setPower(0.1);
		}
	}

}