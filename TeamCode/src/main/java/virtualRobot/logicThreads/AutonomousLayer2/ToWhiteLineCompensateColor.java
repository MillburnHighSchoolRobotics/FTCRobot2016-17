package virtualRobot.logicThreads.AutonomousLayer2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.AllignWithBeacon;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.MoveMotorPID;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;
import virtualRobot.commands.killChildren;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 12/2/16.
 */

public class ToWhiteLineCompensateColor extends LogicThread<AutonomousRobot> {
    //Note that displacement is handled in exitCondition
    public static final double WALL_TRACE_SONAR_THRESHOLD = 17; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE = 2300; //Max Displacement To The First Line
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 2400; //Max Displacement To The Second Line
    public static final double MAX_DISTANCE_WHEN_COLOR_FAILS = 900;
    public static final double BLIND_ADJUSTMENT_FIRST = 870;
    public static final double BLIND_ADJUSTMENT_SECOND = 1085;
    public static final double ESCAPE_WALL = 400;
    AtomicBoolean allSensorsFail; //has other Line Sensor triggered
    AtomicBoolean lastSensorTriggered, firstSensorTriggered, redIsLeft, maxDistanceReached;
    AtomicBoolean sonarWorks;
    AtomicBoolean colorTriggered;
    VuforiaLocalizerImplSubclass vuforia;
    GodThread.Line type;
    private boolean escapeWall = true;
    private boolean withWallTrace = false;
    private Mode mode = Mode.NORMAL;
    private double maxDistance = Double.MAX_VALUE;


    private static final int whiteTape = 13;
    private final ExitCondition atwhitelineSecondTry = new ExitCondition() {
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if ((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)) {
                allSensorsFail.set(false);
                colorTriggered.set(true);
                robot.addToProgress("ColorSensorTriggered");
                return true;
            } else if (robot.getLightSensor1().getRawValue() >= .62) {
                if (type == GodThread.Line.RED_FIRST_LINE || type == GodThread.Line.BLUE_SECOND_LINE) {
                   firstSensorTriggered.set(true);} else {
                   lastSensorTriggered.set(true);
                }
                allSensorsFail.set(false);
                robot.addToProgress("LightSensor1Triggered");
                return true;
            } else if (robot.getLightSensor3().getRawValue() >= .62) {
                allSensorsFail.set(false);
                robot.addToProgress("LightSensor3Triggered");
               return true;
            } else if (robot.getLightSensor2().getRawValue() >= .62) {
               allSensorsFail.set(false);
                robot.addToProgress("LightSensor2Triggered");
                return true;
            } else if ((robot.getLightSensor4().getRawValue() >= .62)) {
                allSensorsFail.set(false);
                if (type == GodThread.Line.BLUE_FIRST_LINE || type == GodThread.Line.RED_SECOND_LINE) {
                    firstSensorTriggered.set(true);
                } else {
                    lastSensorTriggered.set(true);
                }
                robot.addToProgress("LightSensor4Triggered");
                return true;
            }

            return false;
        }
    };
    private final ExitCondition atwhitelineFirstTry = new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if ((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)) {
                allSensorsFail.set(false);
                colorTriggered.set(true);
                robot.addToProgress("ColorSensorTriggered");
                return true;
            }

            return false;
        }
    };

    public ToWhiteLineCompensateColor(GodThread.Line type, AtomicBoolean firstSensorTriggered, AtomicBoolean lastSensorTriggered, AtomicBoolean allSensorsFail, AtomicBoolean sonarWorks, AtomicBoolean redIsLeft, AtomicBoolean colorTriggered, VuforiaLocalizerImplSubclass vuforia, Mode mode) {
        super();
        this.type = type;
        this.allSensorsFail = allSensorsFail;
        this.firstSensorTriggered = firstSensorTriggered;
        this.lastSensorTriggered = lastSensorTriggered;
        this.sonarWorks = sonarWorks;
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
        this.mode = mode;
        this.colorTriggered = colorTriggered;
    }

    @Override
    public void loadCommands() {

        Translate.setGlobalAngleMod(type.getColor() == GodThread.ColorType.RED ? 90 : -90);
        if (mode == Mode.NORMAL) {
            robot.getLFEncoder().clearValue();
            robot.getRFEncoder().clearValue();
            robot.getLBEncoder().clearValue();
            robot.getRBEncoder().clearValue();
            if (type.getLine() == GodThread.LineType.FIRST && sonarWorks.get()) {
                commands.add(new Pause(200));
                commands.add(new Translate(100, Translate.Direction.LEFT, 0).setTolerance(25));
                commands.add(new Pause(200));

            }
            if (type.getLine() == GodThread.LineType.FIRST && !sonarWorks.get() && escapeWall) {
                commands.add(new Translate(ESCAPE_WALL, Translate.Direction.LEFT, 0));
                commands.add(new Pause(200));

            }
            if (type.getLine() == GodThread.LineType.FIRST) {
                if (sonarWorks.get() && withWallTrace) {
                    commands.add(new WallTrace(type.getColor() == GodThread.ColorType.BLUE ? WallTrace.Direction.FORWARD : WallTrace.Direction.BACKWARD, WALL_TRACE_SONAR_THRESHOLD,1500)); //so we don't risk detecting too early

                } else {
                    commands.add(new Translate(1400, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, 1.0)); //so we don't risk detecting too early
                }
                commands.add(new Pause(200));

            }
            if (type.getLine() == GodThread.LineType.SECOND) {
                if (sonarWorks.get() && withWallTrace) {
                    commands.add(new WallTrace(type.getColor() == GodThread.ColorType.BLUE ? WallTrace.Direction.BACKWARD : WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD,1700)); //so we don't recheck the same line

                } else {
                    commands.add(new Translate(2400, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, 1.0)); //so we don't recheck the same line
                }
                    commands.add(new Pause(200));

            }

            allignWithColor();
        } else if (mode == Mode.COLOR_FAILED) {
            colorFailed();
        }
        else if (mode == Mode.ALL_FAILED) {
            allignBlindly();
        }

    }

    private void allignWithColor() {
        colorTriggered.set(false);
        Translate firstDisplacement;
        if (type.getLine() == GodThread.LineType.FIRST)
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .15);
        else
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .15);
        firstDisplacement.setExitCondition(atwhitelineFirstTry);
        commands.add(firstDisplacement);

    }

    private void colorFailed() {
        allSensorsFail.set(true);
        Translate secondDisplacement;
        if (type.getLine() == GodThread.LineType.FIRST)
            secondDisplacement = new Translate(MAX_DISTANCE_WHEN_COLOR_FAILS, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .15);
        else
            secondDisplacement = new Translate(MAX_DISTANCE_WHEN_COLOR_FAILS, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .15);
        secondDisplacement.setExitCondition(atwhitelineSecondTry);
        commands.add(secondDisplacement);
    }

    private void allignBlindly() {
        Translate blindAdjustment;
        if (type.getLine() == GodThread.LineType.FIRST)
            blindAdjustment = new Translate(BLIND_ADJUSTMENT_FIRST, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .15).setTolerance(25);
        else
            blindAdjustment = new Translate(BLIND_ADJUSTMENT_SECOND, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .15).setTolerance(25);
        //commands.add(blindAdjustment);
    }





   /*ALLIGNMENT METHODS PAST THIS SHOULD NOT BE USED




    */














    private void withoutAllign() {
        Translate firstDisplacement;
        if (type.getLine() == GodThread.LineType.FIRST)
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .2);
        else
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .2);
        allSensorsFail.set(true);
        //firstDisplacement.setExitCondition(atwhitelineSecond);
        commands.add(firstDisplacement);
       /* commands.add(new Pause(500));
        Translate secondDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .1);
        secondDisplacement.setExitCondition(atwhitelineFirst);
        commands.add(secondDisplacement);
        commands.add(new Pause(500));
        allSensorsFail.set(true);
        Translate thirdDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .1);
        thirdDisplacement.setExitCondition(atwhitelineSecond);
        commands.add(thirdDisplacement);*/

            /*if(robot.getLightSensor4().getRawValue()> .61)
                commands.add(new Translate(type.getLine() == GodThread.LineType.FIRST ? 75 : 100,Translate.Direction.BACKWARD,0).setTolerance(50));
            else if(robot.getLightSensor1().getRawValue()> .61)
                commands.add(new Translate(type.getLine() == GodThread.LineType.FIRST ? 75 : 100,Translate.Direction.FORWARD,0).setTolerance(50));

        if(type.getLine() == GodThread.LineType.SECOND && robot.getSonarLeft().getValue()> 21)
            commands.add(new Translate(75,Translate.Direction.FORWARD,0).setTolerance(50));

        if (type.getLine() == GodThread.LineType.FIRST) {
            fireBalls();
        }*/
        commands.add(new Pause(200));
        if (type.getLine() == GodThread.LineType.FIRST)
            commands.add(new AllignWithBeacon(vuforia, redIsLeft, type.getColor() == GodThread.ColorType.BLUE ? AllignWithBeacon.Direction.FORWARD : AllignWithBeacon.Direction.BACKWARD));
        else
            commands.add(new AllignWithBeacon(vuforia, redIsLeft, type.getColor() == GodThread.ColorType.RED ? AllignWithBeacon.Direction.FORWARD : AllignWithBeacon.Direction.BACKWARD));
        commands.add(new Pause(1000));
    }

    private void onlyAllign() {
        //TODO: Add wallTrace instead of translate if sonar works
        if (type.getLine() == GodThread.LineType.FIRST) {
            commands.add(new Translate(1500, type.getColor() == GodThread.ColorType.RED ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .2));
            commands.add(new Pause(200));

            commands.add(new AllignWithBeacon(vuforia, redIsLeft, type.getColor() == GodThread.ColorType.BLUE ? AllignWithBeacon.Direction.FORWARD : AllignWithBeacon.Direction.BACKWARD, 3000, maxDistance, maxDistanceReached, type.getColor() == GodThread.ColorType.RED ? 90 : -90));
        } else {
            commands.add(new Translate(1500, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .2));
            commands.add(new Pause(200));
            commands.add(new AllignWithBeacon(vuforia, redIsLeft, type.getColor() == GodThread.ColorType.RED ? AllignWithBeacon.Direction.FORWARD : AllignWithBeacon.Direction.BACKWARD, 3000, maxDistance, maxDistanceReached, type.getColor() == GodThread.ColorType.RED ? 90 : -90));
        }
        commands.add(new Pause(1000));

    }


    private double getAvgDistance() {
        double LFvalue = robot.getLFEncoder().getValue();
        double RFvalue = robot.getRFEncoder().getValue();
        double LBvalue = robot.getLBEncoder().getValue();
        double RBvalue = robot.getRBEncoder().getValue();
        Log.d("AVGDIST", " " + Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue)) / 4));
        return (Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue)) / 4;
    }

    private void fireBalls() {
        commands.add(new Translate(400, Translate.Direction.BACKWARD_LEFT, 0));
        commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{0})); //move flywheel

        LogicThread<AutonomousRobot> spinFlywheel = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new MoveMotorPID(87, robot.getFlywheel(), robot.getFlywheelEncoder()));
                commands.add(new Pause(1000));

            }
        };
        LogicThread<AutonomousRobot> moveReaper = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new Pause(2000));
                commands.add(new MoveMotor(robot.getReaperMotor(), .21));

            }
        };


        List<LogicThread> threads = new ArrayList<LogicThread>();
        threads.add(spinFlywheel);
        threads.add(moveReaper);

        SpawnNewThread fly = new SpawnNewThread((threads));

        commands.add(fly);
        commands.add(new Pause(5000));
        commands.add(new killChildren(this));
        commands.add(new Translate(400, Translate.Direction.FORWARD_RIGHT, 0));

    }

    public enum Mode {
        NORMAL,
        COLOR_FAILED,
        ALL_FAILED;
    }
}
