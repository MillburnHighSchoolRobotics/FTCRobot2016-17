package virtualRobot.components;

import virtualRobot.utils.MathUtils;

/**
 * Created by 17osullivand on 11/4/16.
 */

public class UltrasonicSensor extends Sensor {
    public double getFilteredValue() {
        byte levels[] = new byte[10];
        bytefill(levels, (byte) MathUtils.clamp((getValue()), -128, 127));
        for (int i = 1; i < levels.length; i++) {
            byte temp = levels[i];
            int j;
            for (j = i - 1; j >= 0 && temp < levels[j]; j--) {
                levels[j+1] = levels[j];
            }
            levels[j+1] = temp;
        }
        return levels[levels.length/2];
    }
    public static void bytefill(byte[] array, byte value) {
        int len = array.length;
        if (len > 0)
            array[0] = value;
        for (int i = 1; i < len; i += i) {
            System.arraycopy( array, 0, array, i, ((len - i) < i) ? (len - i) : i);
        }
    }

}
