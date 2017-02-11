package virtualRobot.logicThreads.TestingAutonomouses;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.PIDController;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.AllignWithBeacon;
import virtualRobot.commands.Command;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.DavidClass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLine;
import virtualRobot.utils.Vector2i;

/**
 * Created by ethachu19 on 10/27/2016.
 * used to test ethan's algos
 */

public class ScrewTesterMax extends LogicThread<AutonomousRobot> {

    private static final double SCALE = .7;
    private VuforiaLocalizerImplSubclass vuforia;

    public ScrewTesterMax(VuforiaLocalizerImplSubclass vuforia) {
        this.vuforia = vuforia;
    }

    @Override
    public void loadCommands() {
//        commands.add(new Command(){
//            @Override
//            public boolean changeRobotState() throws InterruptedException {
//                boolean isInterrupted = false;
//                while (!isInterrupted) {
//                    PIDController allign = new PIDController(0.2,0,0,0,SallyJoeBot.BWTHRESHOLD);
//                    double adjustedPower;
//                    double basePower = 0.2;
//                    while (robot.getLineSensor().getValue() < 400) {
//                        adjustedPower = allign.getPIDOutput(robot.getLineSensor().getValue());
////                        adjustedPower = 0;
//                        Log.d("PIDOUTROTATE", "" + adjustedPower);
//                        robot.addToTelemetry("Adujusted Power",adjustedPower);
//                        robot.getLFMotor().setPower(-basePower - adjustedPower);
//                        robot.getLBMotor().setPower(SCALE* (basePower - adjustedPower));
//                        robot.getRFMotor().setPower(SCALE*(basePower + adjustedPower));
//                        robot.getRBMotor().setPower(-basePower + adjustedPower);
//                        Thread.sleep(10);
//                    }
//                    robot.addToTelemetry("INTERRUPTED ", "");
//                    Log.d("Interrupt", "Line Follower");
////                    robot.getLeftRotate().setPower(0);
////                    robot.getRightRotate().setPower(0);
//                    isInterrupted = Thread.currentThread().isInterrupted();
//                }
//                return isInterrupted;
//            }
//        });

//        commands.add(new Command() {
//            @Override
//            public boolean changeRobotState() throws InterruptedException
//            {
////                double threshold = 1;
////                double curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
////                double sign = Math.signum(curr);
////                double power = 0.15;
////                robot.addToTelemetry("Wall Values: ", curr + " " + sign);
////                Log.d("WallTrace", curr + " " + sign);
////                while (Math.abs(curr) > threshold) {
////                    robot.getLeftRotate().setPower(power*sign);
////                    robot.getRightRotate().setPower(-power*sign);
////                    curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
////                    sign = Math.signum(curr);
////                    robot.addToTelemetry("Wall Values: ", curr + " " + sign);
////                    Log.d("WallTrace", curr + " " + sign);
////                }
////                Log.d("WallTrace", "Ended Orient");
//
////                PIDController allign = new PIDController(0.5,0,0,0,robot.getLineSensor().getValue()-0.7);
////                double adjustedPower;
////                double basePower = 0.2;
////                while (robot.getSonarRight().getValue() > 12) {
////                    adjustedPower = allign.getPIDOutput(robot.getLineSensor().getValue());
////                    robot.getLFMotor().setPower(-basePower - adjustedPower);
////                    robot.getLBMotor().setPower(basePower - adjustedPower);
////                    robot.getRFMotor().setPower(basePower + adjustedPower);
////                    robot.getRBMotor().setPower(-basePower + adjustedPower);
////                }
////                robot.stopMotors();
////                Log.d("WallTrace", "Ended Forward");
////                robot.stopMotors();
////                return Thread.currentThread().isInterrupted();
//                double tp = 0.2;
//                boolean isInterrupted = false;
//                WallTrace.Direction direction = WallTrace.Direction.BACKWARD;
//                double target = 7;
//                UltrasonicSensor sonarLeft = direction == WallTrace.Direction.FORWARD ? robot.getSonarLeft() : robot.getSonarRight();
//                UltrasonicSensor sonarRight = direction == WallTrace.Direction.FORWARD ? robot.getSonarRight() : robot.getSonarLeft();
//                PIDController close = new PIDController(0.008,0,0,0,target);
//                PIDController allign = new PIDController(0.008,0,0,0,0);
//                double currLeft, currRight, errClose = 0, errAllign, powLeft, powRight;
//                robot.getLFEncoder().clearValue();
//                robot.getRFEncoder().clearValue();
//                robot.getLBEncoder().clearValue();
//                robot.getRBEncoder().clearValue();
//                while (!isInterrupted) {
//                    currLeft = sonarLeft.getFilteredValue();
//                    currRight = sonarRight.getFilteredValue();
//
//                    errClose = close.getPIDOutput(currLeft);
//                    errAllign = allign.getPIDOutput(robot.getHeadingSensor().getValue());
//
//                    if (direction == WallTrace.Direction.FORWARD) {
//                        powLeft = tp - errClose - errAllign;
//                        powRight = tp + errClose + errAllign;
//                        robot.getLBMotor().setPower(powLeft);
//                        robot.getLFMotor().setPower(powLeft);
//                        robot.getRFMotor().setPower(powRight);
//                        robot.getRBMotor().setPower(powRight);
//                    } else {
//                        powLeft = (tp - errClose - errAllign)*-1;
//                        powRight = (tp + errClose + errAllign)*-1;
//                        robot.getLBMotor().setPower((tp - errClose - errAllign)*-1);
//                        robot.getLFMotor().setPower((tp - errClose - errAllign)*-1);
//                        robot.getRFMotor().setPower((tp + errClose + errAllign)*-1);
//                        robot.getRBMotor().setPower((tp + errClose + errAllign)*-1);
//                    }
//                    Log.d("WallTrace",currLeft + " " + currRight + " " + errClose + " " + errAllign + " " + powLeft + " " + powRight);
//
//                    if(Thread.currentThread().isInterrupted()) {
//                        isInterrupted = true;
//                        break;
//                    }
//
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        isInterrupted = true;
//                        break;
//                    }
//                }
//                robot.stopMotors();
//                return isInterrupted;
//            }
//        });
//        LineTrace lt = new LineTrace();
//        final int whiteTape = 13;
//        ExitCondition whiteLine = new ExitCondition() {
//            @Override
//            public boolean isConditionMet() {
//                if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)){
//                    robot.addToProgress("ColorSensorTriggered");
//                    return true;
//                }
//                else if(robot.getLightSensor1().getRawValue()> .61) {
//                    robot.addToProgress("LightSensor1Triggered");
//                    return true;
//                }
//                else if(robot.getLightSensor3().getRawValue()> .61) {
//                    robot.addToProgress("LightSensor3Triggered");
//                    return true;
//                }
//                else if(robot.getLightSensor2().getRawValue()> .61) {
//                    robot.addToProgress("LightSensor2Triggered");
//                    return true;
//                }else if((robot.getLightSensor4().getRawValue()> .61)){
//                    robot.addToProgress("LightSensor4Triggered");
//                    return true;
//                }
//                return false;
//            }
//        };
//        Translate t1 = new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.FORWARD, 0, 1);
//        t1.setExitCondition(whiteLine);
//        commands.add(t1);
//        commands.add(new Pause(200));
//        Translate newT1 = new Translate(1500, Translate.Direction.BACKWARD,0,0.2);
//        newT1.setExitCondition(whiteLine);
//        commands.add(newT1);
//        //commands.add(new Pause(200));
//        //commands.add(new Rotate(0, .5, 2000));
//        commands.add(new Pause(200));
//        commands.add(new CompensateColor(2000));
//        commands.add(new Pause(200));
//        commands.add(new Translate(50, Translate.Direction.BACKWARD,0,0.2).setTolerance(25));
//        commands.add(new Pause(5000));
//        commands.add(new Translate(500, Translate.Direction.FORWARD, 0));
//        commands.add(new Pause(200));
//        Translate t2 = new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.FORWARD, 0, 1);
//        t2.setExitCondition(whiteLine);
//        commands.add(t2);
////        Translate.setGlobalTolerance(50);
//        commands.add(new Pause(200));
//        Translate newT = new Translate(1500, Translate.Direction.BACKWARD,0,0.2);
//        newT.setExitCondition(whiteLine);
//        commands.add(newT);
//        //Translate.setGlobalTolerance(50);
//        //commands.add(new Rotate(0, .5, 2000)); //Straighten out (note that rotate takes in a target value, not a relative value). So this will return us to the angle we started our bot at.
//        commands.add(new Pause(200));
//        CompensateColor lt = new CompensateColor(2000);
//        lt.setExitCondition(new ExitCondition() {
//            @Override
//            public boolean isConditionMet() {
//                return false;
//            }
//        });
//        robot.stopMotors();
//        robot.addToProgress("Replace on Line Done");
//        commands.add(new Pause(200));
//        commands.add(lt);
//        robot.stopMotors();
//        commands.add(new Pause(200));
//        robot.addToProgress("Pause Finished, Translate Now");
//        commands.add(new Translate(50, Translate.Direction.BACKWARD,0,0.2).setTolerance(25));
//        robot.addToProgress("Translate Done");
        final AtomicBoolean ab = new AtomicBoolean();
        commands.add(new AllignWithBeacon(vuforia,ab, AllignWithBeacon.Direction.BACKWARD, 2000));
        commands.add(new Command() {
            @Override
            public boolean changeRobotState() throws InterruptedException {
                robot.addToProgress("Red is Left: " + ab.get());
                robot.addToProgress("Finished");
                return Thread.currentThread().isInterrupted();
            }
        });
//        commands.add(new Command () {
//            PIDController compensate = new PIDController(0.335,0,0,0.15,1.025);
//            PIDController heading = new PIDController(0.4,0,0,0,0);
//            //final double TOLERANCE = 0.04;
//            double timeLimit;
//            @Override
//            public boolean changeRobotState() throws InterruptedException {
//                double power, curr = 0, red, blue, adjustedPower = 0;
//                int covered;
//                boolean isInterrupted = false;
//                int width = vuforia.rgb.getWidth(), height = vuforia.rgb.getHeight();
//                int end = (int) (AllignWithBeacon.endXPercent * width);
//                Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//                Vector2i currentPos;
//                while (!isInterrupted) {
//                    curr = 0;
//                    bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
//                    currentPos = new Vector2i((int) (AllignWithBeacon.startXPercent * width), vuforia.rgb.getHeight() / 2);
//                    for (covered = 0; currentPos.x < end;) {
//                        red = Color.red(bm.getPixel(currentPos.x, currentPos.y));
//                        blue = Color.blue(bm.getPixel(currentPos.x, currentPos.y));
//                        if (blue != 0 && (blue > 200 || red > 200) && (red/blue < AllignWithBeacon.BLUETHRESHOLD || red/blue > AllignWithBeacon.REDTHRESHOLD)) {
//                            curr += red / blue;
//                            covered++;
//                        }
//                        currentPos.x += 8;
//                    }
//                    if (covered == 0)
//                        continue;
//                    curr /= covered;
//                    power = compensate.getPIDOutput(curr);
//                    //adjustedPower = heading.getPIDOutput(robot.getHeadingSensor().getValue());
//                    Log.d("AllignWithBeacon", "" + power + " " + adjustedPower + " " + curr + " " + covered);
//                    robot.addToTelemetry("AllignWithBeacon ", curr + " " + covered + " " + power);
//                    robot.getLFMotor().setPower(power + adjustedPower);
//                    robot.getLBMotor().setPower(power + adjustedPower);
//                    robot.getRFMotor().setPower(power - adjustedPower);
//                    robot.getRBMotor().setPower(power - adjustedPower);
//                    if (Thread.currentThread().isInterrupted()) {
//                        isInterrupted = true;
//                        break;
//                    }
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        isInterrupted = true;
//                        break;
//                    }
//                }
//                robot.addToProgress("EXITED COMMAND");
//                robot.stopMotors();
//                return isInterrupted;
//            }
//        });
    }
}
