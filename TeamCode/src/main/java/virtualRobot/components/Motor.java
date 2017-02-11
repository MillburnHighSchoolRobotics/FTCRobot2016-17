package virtualRobot.components;

import virtualRobot.SomeoneDunGoofed;

/*
The virtual Motor component
 */
public class Motor {

	public static final double MAX_POWER = 1;
	public static final double STATIONARY = 0;
	private volatile MotorType motorType;

    private volatile double power;

    public Motor(MotorType motorType) {
        power = 0;
		this.motorType = motorType;
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
			if (Double.isNaN(newPower)) {
				throw new SomeoneDunGoofed("FUCK YOU BRO");
			}
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

	public MotorType getMotorType() {
		return motorType;
	}

	public enum MotorType {
		NeveRest3_7(29.1,17.308,12.232,25.9,335,.06,103), //NeveRest 3.7
		NeveRest20(-1,-1,-1,-1,-1,-1,560),
		NeveRest40(-1,-1,-1,28,375,-1,1120),
		NeveRest60(-1,-1,-1,-1,571.428571,-1,1680);
		private final double KP;
		private final double KI;
		private final double KD;
		private final double PPC; //Pulses per cycle

		public synchronized double getKP() {
			return KP;
		}

		public synchronized double getKI() {
			return KI;
		}

		public synchronized double getKD() {
			return KD;
		}

		public synchronized double getPPC() {
			return PPC;
		}

		public synchronized double getMSC() {
			return MSC;
		}

		public synchronized double getMaxActSpeed() {
			return maxActSpeed;
		}

		public synchronized double getTicksPerRevolution() {
			return ticksPerRevolution;
		}

		private final double MSC; //Milliseconds per cycle
		private final double maxActSpeed;
		private final double ticksPerRevolution;



		private MotorType(double KP, double KI, double KD, double PPC,  double MSC, double maxAS, double ticksPerRevolution) {
			this.KP = KP;
			this.KI = KI;
			this.KD = KD;
			this.PPC = PPC;
			this.MSC = MSC;
			this.maxActSpeed = maxAS;
			this.ticksPerRevolution = ticksPerRevolution;
		} //Kp, Ki, Kd, Pulses per cycle of motor, Milliseconds per cycle of motor, max actual speed (e.g. speed w/o mapping)
	}
}