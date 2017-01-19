package virtualRobot.commands;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.utils.Vector2i;

/**
 * Created by ethachu19 on 12/7/2016.
 */

public class AllignWithBeacon implements Command {

    AtomicBoolean redIsLeft;
    AutonomousRobot robot = Command.AUTO_ROBOT;
    VuforiaLocalizerImplSubclass vuforia;
    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    private final static double BLUETHRESHOLD = 0.6;
    private final static double REDTHRESHOLD = 1.45;
    private final static double tp = -0.2;
    private PIDController heading = new PIDController(0,0,0,0,0);

    public AllignWithBeacon(VuforiaLocalizerImplSubclass vuforia, AtomicBoolean redIsLeft) {
        this.vuforia = vuforia;
        this.redIsLeft = redIsLeft;
    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    private int closestTo11(double num) {
        int res = -1;
        double leastDist = Double.MAX_VALUE;
        for (int i = 0; i < 12; i++) {
            if (Math.abs(i/11 - num) < leastDist) {
                res = i;
                leastDist = Math.abs(i/11 - num);
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
        if (DavidClass.currentMode == DavidClass.Mode.MIDSPLIT) {
            start1 = new Vector2i((int) DavidClass.startXPercent*width, (int) DavidClass.startYPercent*height);
            end2 = new Vector2i((int) DavidClass.endXPercent*width, (int) DavidClass.endYPercent*height);
            end1 = new Vector2i((start1.x + end2.x)/2, end2.y);
            start2 = new Vector2i(end1.x,start1.y);
        } else {
            start1 = new Vector2i((int) DavidClass.start1XPercent*width, (int) DavidClass.start1YPercent*height);
            end2 = new Vector2i((int) DavidClass.end2XPercent*width, (int) DavidClass.end2YPercent*height);
            start2 = new Vector2i((int) DavidClass.start2XPercent*width, (int) DavidClass.start2YPercent*height);
            end1 = new Vector2i((int) DavidClass.end1XPercent*width, (int) DavidClass.end1YPercent*height);
        }
        Vector2i slope1, slope2;
        if (vuforia.rgb.getHeight() > vuforia.rgb.getWidth()) {
            slope1 = new Vector2i(11, closestTo11((end1.y - start1.y)/(end1.x - start1.x)));
            slope2 = new Vector2i(11, closestTo11((end2.y - start2.y) / (end2.x - start2.x)));
        } else {
            slope1 = new Vector2i(closestTo11((end1.y - start1.y)/(end1.x - start1.x)),11);
            slope2 = new Vector2i(closestTo11((end2.y - start2.y) / (end2.x - start2.x)),11);
        }
        double currLeft = 0, currRight = 0, adjustedPower;
        int leftCovered, rightCovered;
        Vector2i currentPos;
        boolean isInterrupted = false, satisfied = false;
        while (!exitCondition.isConditionMet() && !isInterrupted && !satisfied) {
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i(start1);
            for (leftCovered = 0; currentPos.x < end1.x && currentPos.y > end1.y; leftCovered++) {
                currLeft += Color.red(bm.getPixel(currentPos.x,currentPos.y))/Color.blue(bm.getPixel(currentPos.x,currentPos.y));
                currentPos.x += slope1.x;
                currentPos.y -= slope1.y;
            }
            currentPos = new Vector2i(start2);
            for (rightCovered = 0; currentPos.x < end2.x && currentPos.y > end2.y; rightCovered++) {
                currRight += Color.red(bm.getPixel(currentPos.x,currentPos.y))/Color.blue(bm.getPixel(currentPos.x,currentPos.y));
                currentPos.x += slope2.x;
                currentPos.y -= slope2.y;
            }
            currLeft /= leftCovered;
            currRight /= rightCovered;
            if (currLeft < BLUETHRESHOLD && currRight > REDTHRESHOLD) {
                redIsLeft.set(true);
                satisfied = true;
                break;
            } else if (currLeft > REDTHRESHOLD && currRight < BLUETHRESHOLD) {
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
        robot.stopMotors();
        return isInterrupted;
    }
}
