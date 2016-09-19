package virtualRobot.components;


public class Motor {

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
	        if (power > 1) {
	            power = 1;
	        }
	
	        if (power < -1) {
	            power = -1;
	        }
    	}
    }

}