package virtualRobot.logicThreads.AutonomousLayer2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.PIDController;
import virtualRobot.SomeoneDunGoofed;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.AllignWithBeacon;
import virtualRobot.commands.Command;
import virtualRobot.utils.Vector2i;

/**
 * Created by 17osullivand on 2/15/17.
 */

public class PreciseAllign implements Command {
    private final static boolean flipUpsideDown = true;
    public final static AllignWithBeacon.Mode currentMode = AllignWithBeacon.Mode.MIDSPLIT;
    public volatile static double startXPercent = 0;
    public volatile static double endXPercent = 1;
    public volatile static double startYPercent = 0.135;
    public volatile static double endYPercent = 1;

    public volatile static double start1XPercent = 0;
    public volatile static double end1XPercent = .2;

    public volatile static double start1YPercent = 0;
    public volatile static double end1YPercent = 1;

    public volatile static double start2XPercent = 0.4;
    public volatile static double end2XPercent = 1;

    public volatile static double start2YPercent = 0;
    public volatile static double end2YPercent = 1;

    AutonomousRobot robot = Command.AUTO_ROBOT;
    VuforiaLocalizerImplSubclass vuforia;
    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };
    private int whiteTape = 13;
    public final static double BLUETHRESHOLD = 0.7; //.65
    public final static double REDTHRESHOLD = 1.43;
    public final static double LINETHRESHOLD = 0.62;
    public final static double FIRST_LINE_TARGET = .475;
    public final static double SECOND_LINE_TARGET = .475;
    public double timeLimit = -1;
    private static double tp = -0.15;
    private PIDController heading = new PIDController(0,0,0,0,0);
    //private PIDController compensate = new PIDController(1.5,.05357,10.5,0,(BLUETHRESHOLD + REDTHRESHOLD)/2);
    //private PIDController compensate = new PIDController(1.05, 0.0241,0,0,(AllignWithBeacon.BLUETHRESHOLD + AllignWithBeacon.REDTHRESHOLD)/2);
    private PIDController compensate = new PIDController(10, 0, 0, 0, 0);

    //1.125
    private AllignWithBeacon.Direction direction;
    AtomicBoolean redIsLeft;
    AtomicBoolean sonarWorks;
    GodThread.Line type;
    public PreciseAllign(double t, AtomicBoolean redIsLeft, AtomicBoolean sonarWorks, VuforiaLocalizerImplSubclass vuforia, GodThread.Line type) {
        this.timeLimit = t;
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
        this.sonarWorks = sonarWorks;
        this.type = type;
    }
    @Override
    public boolean changeRobotState() throws InterruptedException {
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        int width = vuforia.rgb.getWidth(), height = vuforia.rgb.getHeight();
        Vector2i start1;
        Vector2i end1;
        Vector2i start2;
        Vector2i end2;

        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int coF = 12;
        Log.d("width/height", width + " " + height);
        start2 = new Vector2i((int) (startXPercent * width), (int) (startYPercent * height));
        end1 = new Vector2i((int) (endXPercent * width), (int) (endYPercent * height));
        end2 = new Vector2i((start2.x + end1.x) / 2, end1.y);
        start1 = new Vector2i(end2.x, start2.y);
        double power, curr = 0;
        int covered;
       double currLeft = 0;
        double currRight = 0;
        double red;
        double blue;
        boolean isInterrupted = false;
        double adjustedPower = 0;
        bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
        long start = System.currentTimeMillis();
        Vector2i currentPos;
            curr = 0;
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i(start1.x, vuforia.rgb.getHeight()/2);
            Vector2i slope1, slope2;
            if (vuforia.rgb.getHeight() > vuforia.rgb.getWidth()) {
                slope1 = new Vector2i(coF, closestToFrac(((double) (end1.y - start1.y)) / (end1.x - start1.x), coF));
                slope2 = new Vector2i(coF, closestToFrac(((double) (end2.y - start2.y)) / (end2.x - start2.x), coF));
            } else {
                slope1 = new Vector2i(coF, closestToFrac(((double) (end1.y - start1.y)) / (end1.x - start1.x), coF));
                slope2 = new Vector2i(coF, closestToFrac(((double) (end2.y - start2.y)) / (end2.x - start2.x), coF));
            }
            double leftCovered;
            double rightCovered;
            for (leftCovered = 0; currentPos.x < end1.x && currentPos.y < end1.y; ) {
                red = Color.red(bm.getPixel(currentPos.x, currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x, currentPos.y));
                if (blue != 0) {
                    Log.d("Debug", red + " " + blue + " " + currentPos.toString());

                    currLeft += red / blue;
                    leftCovered++;
                }
                currentPos.x += slope1.x;
                currentPos.y += slope1.y;
            }
            if (leftCovered == 0)
                throw new SomeoneDunGoofed("FUCK"); //TODO: Implement
            currLeft /= leftCovered;
            robot.addToTelemetry("currLEFT: ", currLeft);
            currentPos = new Vector2i(start2);
            for (rightCovered = 0; currentPos.x < end2.x && currentPos.y < end2.y; ) {
                red = Color.red(bm.getPixel(currentPos.x, currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x, currentPos.y));
                if (blue != 0) {
                    Log.d("Debug", red + " " + blue + " " + currentPos.toString());
                    currRight += red / blue;
                    rightCovered++;
                }
                currentPos.x += slope2.x;
                currentPos.y += slope2.y;
            }
            if (rightCovered == 0)
                throw new SomeoneDunGoofed("FUCK"); //TODO: Implement
            currRight /= rightCovered;
            robot.addToTelemetry("currRIGHT: ", currRight);
            //  Log.d("currRIGHT", String.valueOf(currRight));
            if (currLeft > currRight) {
                redIsLeft.set(true);
            } else {
                redIsLeft.set(false);
            }


            //currentPos = new Vector2i(start1.x, vuforia.rgb.getHeight()/2);
        boolean allGood = false;
        curr = -1;

        while (!allGood && !exitCondition.isConditionMet() && !isInterrupted && ((System.currentTimeMillis() - start < timeLimit) || timeLimit == -1)) {
            double target = getTarget();
            compensate.setTarget(target);
            curr = 0;
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i((int) (AllignWithBeacon.startXPercent * width), vuforia.rgb.getHeight() / 2);
            for (covered = 0; currentPos.x < end2.x;) {
                red = Color.red(bm.getPixel(currentPos.x, currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x, currentPos.y));
                if (blue != 0 && (blue > 200 || red > 200) && (red/blue < AllignWithBeacon.BLUETHRESHOLD || red/blue > AllignWithBeacon.REDTHRESHOLD)) {
                    curr += red / blue;
                    covered++;
                }
                currentPos.x += 8;
            }
            if (covered == 0)
                continue;
            curr /= covered;
            //power = (redIsLeft.get() ? 1 : -1) * compensate.getPIDOutput(curr);
//            if (curr < (target+.1) && curr> (target-.1) ) {
//                power = 0;
//                allGood = true;
//            }
//            else {
//                power = (redIsLeft.get() ? 1 : -1) * .15 * (curr > target ? -1 : 1);
//            }
            power = (redIsLeft.get() ? 1 : -1) * .15 * compensate.getPIDOutput(curr);

            //power*=.15;
            //adjustedPower = heading.getPIDOutput(robot.getHeadingSensor().getValue());
            //adjustedPower *= tp;
            Log.d("AllignWithBeacon","" + power + " " + adjustedPower + " " + curr + " " + covered);
            robot.addToTelemetry("AllignWithBeacon ", target + " " + curr + " " + covered + " " + power);
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
        robot.stopMotors();
        return isInterrupted;
    }
    private int closestToFrac(double num, double frac) {
        int res = -1;
        double leastDist = Double.MAX_VALUE;
        for (int i = 0; true; i++) {
            if (Math.abs(i/frac - num) < leastDist) {
                res = i;
                leastDist = Math.abs(i/frac - num);
            } else {
                break;
            }
        }
        return res;
    }
    private double getTarget() {
        int val = 0;
        boolean good = true;
        int sonar1 = (int)(robot.getSonarLeft().getFilteredValue());
        int sonar2 = (int)(robot.getSonarRight().getFilteredValue());
        if (sonar1 > 10) {
            val = sonar1;
        }
        else if (sonar2 > 10) {
            val = sonar2;
        }
        else  {
            good = false;
        }
        if (sonarWorks.get() && good) {

            switch(val) {
                case 10:
                    return .5;

                case 11:
                    return .5;


                case 12:
                    return .46;



                case 13:
                    return .47;

                case 14:
                    return .475;

                case 15:
                    return .386;

                case 16:
                    return .343;

                case 17:
                    return .328;

                case 18:
                    return .4;

                case 19:
                    return .4;

                case 20:
                    return .462;

                case 21:
                    return .5;

                case 22:
                    return .52;

                case 23:
                    return .52;

                case 24:
                    return .53;

                case 25:
                    return .54;
                default:
                    return .54;

            }
        }
        else {
           if (type.getLine() == GodThread.LineType.FIRST)  {
               return FIRST_LINE_TARGET;
           }
            else {
               return SECOND_LINE_TARGET;
           }
        }
    }
}
