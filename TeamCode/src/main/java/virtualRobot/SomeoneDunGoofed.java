package virtualRobot;

/**
 * Created by 17osullivand on 2/10/17.
 */

public class SomeoneDunGoofed extends RuntimeException {
    public SomeoneDunGoofed() {
        super();
    }
    public SomeoneDunGoofed(String message) {
        super(message);
    }
}
