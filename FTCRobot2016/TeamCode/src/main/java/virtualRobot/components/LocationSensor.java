package virtualRobot.components;

/**
 * Created by shant on 2/8/2016.
 */
public class LocationSensor extends Sensor {

    private Point location;

    public LocationSensor() {
        location = new Point();
    }

    public synchronized double getX () {
        synchronized (this) {
            return location.x;
        }
    }

    public synchronized void setX (double newX) {
        synchronized (this) {
            location.x = newX;
        }
    }

    public synchronized double getY() {
        synchronized (this) {
            return location.y;
        }
    }

    public synchronized void setY (double newY) {
        synchronized (this) {
            location.y = newY;
        }
    }

    public synchronized double getAngle() {
        synchronized (this) {
            return location.angle;
        }
    }

    public synchronized void setAngle (double newAngle) {
        synchronized (this) {
            location.angle = newAngle;
        }
    }

    private class Point {
        public volatile double x;
        public volatile double y;
        public volatile double angle;

        public Point () {
            x = 0;
            y = 0;
            angle = 0;
        }
    }
}
