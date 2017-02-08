package virtualRobot.commands;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.utils.Vector2i;

/**
 * Created by ethachu19 on 12/7/2016.
 *
 * NOTE FOR TEAM OF 2016 - 2017: CAMERA IS FLIPPED AND 180 DEGREE ROTATE IS TOO COSTLY
 */

public class AllignWithBeacon implements Command {

    public enum Mode {
        MIDSPLIT, TWORECTANGLES
    }
    
    public final static Mode currentMode = Mode.MIDSPLIT;
    public volatile static double startXPercent = 0;
    public volatile static double endXPercent = 1;
    public volatile static double startYPercent = 0.135;
    public volatile static double endYPercent = 1;

    public volatile static double start1XPercent = 0;
    public volatile static double end1XPercent = -1;
    public volatile static double start1YPercent = 0;
    public volatile static double end1YPercent = -1;
    public volatile static double start2XPercent = 0.66;
    public volatile static double end2XPercent = 1;
    public volatile static double start2YPercent = 0;
    public volatile static double end2YPercent = 0.8353;
    
    AtomicBoolean redIsLeft;
    AutonomousRobot robot = Command.AUTO_ROBOT;
    VuforiaLocalizerImplSubclass vuforia;
    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    public final static double BLUETHRESHOLD = 0.65;
    public final static double REDTHRESHOLD = 1.4;
    private static double tp = -0.2;
    private PIDController heading = new PIDController(0,0,0,0,0);
    //private PIDController compensate = new PIDController(0.2,0,(BLUETHRESHOLD + REDTHRESHOLD)/2,0.1);
    private PIDController compensate = new PIDController(.345,0.00,0,0.3,1.025,false,2);

    public AllignWithBeacon(VuforiaLocalizerImplSubclass vuforia, AtomicBoolean redIsLeft) {
        this.vuforia = vuforia;
        this.redIsLeft = redIsLeft;
    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    private int closestToFrac(double num, double frac) {
        int res = -1;
        double leastDist = Double.MAX_VALUE;
        for (int i = 0; i < 12; i++) {
            if (Math.abs(i/11 - num) < leastDist) {
                res = i;
                leastDist = Math.abs(i/frac - num);
            }
        }
        return res;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        int width = vuforia.rgb.getWidth(), height = vuforia.rgb.getHeight();
        Vector2i start1;
        Vector2i end1;
        Vector2i start2;
        Vector2i end2;
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int coF = 12;
        if (currentMode == Mode.MIDSPLIT) {
            start1 = new Vector2i((int) (startXPercent*width), (int) (startYPercent*height));
            end2 = new Vector2i((int) (endXPercent*width), (int) (endYPercent*height));
            end1 = new Vector2i((start1.x + end2.x)/2, end2.y);
            start2 = new Vector2i(end1.x,start1.y);
        } else {
            start1 = new Vector2i((int) start1XPercent*width, (int) start1YPercent*height);
            end2 = new Vector2i((int) end2XPercent*width, (int) end2YPercent*height);
            start2 = new Vector2i((int) start2XPercent*width, (int) start2YPercent*height);
            end1 = new Vector2i((int) end1XPercent*width, (int) end1YPercent*height);
        }
        Vector2i slope1, slope2;
        if (vuforia.rgb.getHeight() > vuforia.rgb.getWidth()) {
            slope1 = new Vector2i(coF, closestToFrac((end1.y - start1.y)/(end1.x - start1.x),coF));
            slope2 = new Vector2i(coF, closestToFrac((end2.y - start2.y) / (end2.x - start2.x),coF));
        } else {
            slope1 = new Vector2i(coF,closestToFrac((end1.y - start1.y)/(end1.x - start1.x), coF));
            slope2 = new Vector2i(coF,closestToFrac((end2.y - start2.y) / (end2.x - start2.x),coF));
        }
        double currLeft = 0, currRight = 0, adjustedPower = 0,red = 0,blue = 0;
        int leftCovered = 0, rightCovered = 0;
        Vector2i currentPos;
        boolean isInterrupted = false, satisfied = false;
        while (!exitCondition.isConditionMet() && !isInterrupted && !satisfied) {
            currLeft = 0;
            currRight = 0;
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i(start1);
            for (leftCovered = 0; currentPos.x < end1.x && currentPos.y < end1.y; leftCovered++) {
                red = Color.red(bm.getPixel(currentPos.x,currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x,currentPos.y));
                currLeft += red/blue;
                currentPos.x += slope1.x;
                currentPos.y += slope1.y;
            }
//            currentPos = new Vector2i(start2);
//            for (rightCovered = 0; currentPos.x < end2.x && currentPos.y < end2.y; rightCovered++) {
//                red = Color.red(bm.getPixel(currentPos.x,currentPos.y));
//                blue = Color.blue(bm.getPixel(currentPos.x,currentPos.y));
//                currRight += red/blue;
//                currentPos.x += slope2.x;
//                currentPos.y += slope2.y;
//            }
            currLeft /= leftCovered;
//            currRight /= rightCovered;
            if (currLeft > REDTHRESHOLD) {
                redIsLeft.set(true);
                satisfied = true;
                break;
            } else if (currLeft < BLUETHRESHOLD) {
                redIsLeft.set(false);
                satisfied = true;
                break;
            }
            adjustedPower = heading.getPIDOutput(robot.getHeadingSensor().getValue());
            robot.getLFMotor().setPower(tp + adjustedPower);
            robot.getLBMotor().setPower(tp + adjustedPower);
            robot.getRFMotor().setPower(tp - adjustedPower);
            robot.getRBMotor().setPower(tp - adjustedPower);
            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                isInterrupted = true;
                break;
            }
        }
        robot.addToProgress("Switched To Precision");
        robot.stopMotors();
        bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
        currentPos = new Vector2i(start2);
        for (rightCovered = 0; currentPos.x < end2.x && currentPos.y < end2.y; rightCovered++) {
            red = Color.red(bm.getPixel(currentPos.x,currentPos.y));
            blue = Color.blue(bm.getPixel(currentPos.x,currentPos.y));
            currRight += red/blue;
            currentPos.x += slope2.x;
            currentPos.y += slope2.y;
        }
        currRight /= rightCovered;
        if(currRight > REDTHRESHOLD) {
            redIsLeft.set(true);
            satisfied = false;
            return isInterrupted;
        }
        if(currRight < BLUETHRESHOLD) {
            redIsLeft.set(false);
            satisfied = false;
            return isInterrupted;
        }
        double power, curr = 0;
        int covered;
        while (!exitCondition.isConditionMet() && !isInterrupted) {
            curr = 0;
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i(start1.x, vuforia.rgb.getHeight()/2);
            for (covered = 0; currentPos.x < end2.x;) {
                red = Color.red(bm.getPixel(currentPos.x, currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x, currentPos.y));
                if (blue != 0 && (blue > 200 || red > 200) && (red/blue < AllignWithBeacon.BLUETHRESHOLD || red/blue > AllignWithBeacon.REDTHRESHOLD)) {
                    curr += red / blue;
                    covered++;
                }
                currentPos.x += 8;
            }
            curr /= covered;
            power = (redIsLeft.get() ? 1 : -1) * compensate.getPIDOutput(curr);
            adjustedPower = heading.getPIDOutput(robot.getHeadingSensor().getValue());
            Log.d("AllignWithBeacon","" + power + " " + adjustedPower + " " + curr + " " + covered);
            robot.addToTelemetry("AllignWithBeacon ", curr + " " + covered + " " + power);
            robot.getLFMotor().setPower(power + adjustedPower);
            robot.getLBMotor().setPower(power + adjustedPower);
            robot.getRFMotor().setPower(power - adjustedPower);
            robot.getRBMotor().setPower(power - adjustedPower);
            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                isInterrupted = true;
                break;
            }
        }
        return isInterrupted;
    }
}
