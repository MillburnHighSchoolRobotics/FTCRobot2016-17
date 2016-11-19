package virtualRobot.godThreads;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.logicThreads.AutonomousLayer1.RedGoToWall;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineNoUltra;
import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLine;
import virtualRobot.logicThreads.CompensateForMiss;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToCenterGoal;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;

/**
 * Created by shant on 1/10/2016.
 * Runs Red Autonomous With All LogicThreads
 * THIS IS EXACTLY SAME AS REDAUTOGODTHREAD EXCEPT THE LINETYPE ENUM IS CHANGED FROM BLUE TO RED AND THE GO TO WALL CHANGED TO RED(Go Trump)
 */
public class RedAutoGodThread extends GodThread {
    private final static boolean WITH_SONAR = UpdateThread.WITH_SONAR;
    private AtomicBoolean redIsLeft = new AtomicBoolean();
    private AtomicBoolean isAllRed = new AtomicBoolean();
    LogicThread takePicture = new LogicThread() {
        @Override
        public void loadCommands() {
            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.TAKING_PICTURE, redIsLeft,vuforia); //Take a picture of beacon
            commands.add(pic);
        }
    };
    LogicThread checkPicture = new LogicThread() {
        @Override
        public void loadCommands() {
            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.CHECKING_PICTURE,isAllRed,vuforia); //Take a picture of beacon
            commands.add(pic);
        }
    };
    private AtomicBoolean sonarWorks = new AtomicBoolean();
    private AtomicBoolean failedFirstSensorSecondTriggered = new AtomicBoolean(false);
    private AtomicBoolean exceededMaxDistance = new AtomicBoolean(false);

    @Override
    public void realRun() throws InterruptedException {
        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD

        LogicThread goToWall = new RedGoToWall(sonarWorks);//Knocks Ball, Goes to first wall
        Thread gtw = new Thread(goToWall);
        gtw.start();
        children.add(gtw);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(gtw, new MonitorThread[]{});

//THE FOLLOWING BLOCK MOVES TO FIRST BEACON, TAKES PIC AND PUSHES BUTTON
//*****************************


        boolean weCanUseSonar = sonarWorks.get() && WITH_SONAR;
        LogicThread toFirstLine;
        if (weCanUseSonar) { //If our sonar works, and we're using one
            toFirstLine = new ToWhiteLine(true, Line.RED_FIRST_LINE, failedFirstSensorSecondTriggered
                    , exceededMaxDistance); //Goes to firstLine
        }else {
            toFirstLine = new ToWhiteLine(false, Line.RED_FIRST_LINE, failedFirstSensorSecondTriggered
                    , exceededMaxDistance);
        }
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if(failedFirstSensorSecondTriggered.get()){
                LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, Line.RED_FIRST_LINE, weCanUseSonar);
                Thread adjust = new Thread(reAdjust);
                adjust.start();
                children.add(adjust);
                delegateMonitor(adjust, new MonitorThread[]{});
            }else if(exceededMaxDistance.get()){
                exceededMaxDistance.set(false);
                failedFirstSensorSecondTriggered.set(false);
                LogicThread firstFail= new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTFAILS,failedFirstSensorSecondTriggered
                        ,exceededMaxDistance, Line.RED_FIRST_LINE, weCanUseSonar);
                Thread ff = new Thread(firstFail);
                ff.start();
                children.add(ff);
                delegateMonitor(ff, new MonitorThread[]{});
                if(failedFirstSensorSecondTriggered.get()){
                    LogicThread lastWorks = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED,failedFirstSensorSecondTriggered
                            ,exceededMaxDistance, Line.RED_FIRST_LINE, weCanUseSonar);
                    Thread lw = new Thread(lastWorks);
                    lw.start();
                    children.add(lw);
                    delegateMonitor(lw, new MonitorThread[]{});
                }else if(exceededMaxDistance.get()){
                    LogicThread lastFails = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS
                            ,failedFirstSensorSecondTriggered
                            ,exceededMaxDistance, Line.RED_FIRST_LINE, weCanUseSonar);
                    Thread lf = new Thread(lastFails);
                    lf.start();
                    children.add(lf);
                    delegateMonitor(lf, new MonitorThread[]{});
                }
            }


        //TIME TO TAKE PICTURE
        Thread takepicturenow = new Thread(takePicture);
        takepicturenow.start();
        children.add(takepicturenow);
        delegateMonitor(takepicturenow, new MonitorThread[]{});

        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR);
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR);
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }
        //TIME TO CHECK PICTURE
        Thread checkpicturenow = new Thread(checkPicture);
        checkpicturenow.start();
        children.add(checkpicturenow);
        delegateMonitor(checkpicturenow, new MonitorThread[]{});

        Command.ROBOT.addToProgress("isAllRed /" + Boolean.toString(isAllRed.get()));
        if (!isAllRed.get()) {
            LogicThread pushRight = new PushRightButton(weCanUseSonar);
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }

//*****************************
//THE FOLLOWING BLOCK MOVES TO SECOND BEACON, TAKES PIC AND PUSHES BUTTON (note that it's the same as above, but the Linetype is changed to second beacon)
//*****************************
        exceededMaxDistance.set(false);
        failedFirstSensorSecondTriggered.set(false);
        LogicThread toSecondLine;
        if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
            toSecondLine = new ToWhiteLine(true, Line.RED_SECOND_LINE, failedFirstSensorSecondTriggered
                    , exceededMaxDistance);
        }else {
            toSecondLine = new ToWhiteLine(false, Line.RED_SECOND_LINE, failedFirstSensorSecondTriggered
                    , exceededMaxDistance);
        }
            Thread tsl = new Thread(toSecondLine);
            tsl.start();
            children.add(tsl);
            delegateMonitor(tsl, new MonitorThread[]{});
        if(failedFirstSensorSecondTriggered.get()){
            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, Line.RED_SECOND_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        }else if(exceededMaxDistance.get()){
            exceededMaxDistance.set(false);
            failedFirstSensorSecondTriggered.set(false);
            LogicThread firstFail= new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTFAILS,failedFirstSensorSecondTriggered
                    ,exceededMaxDistance, Line.RED_SECOND_LINE, weCanUseSonar);
            Thread ff = new Thread(firstFail);
            ff.start();
            children.add(ff);
            delegateMonitor(ff, new MonitorThread[]{});
            if(failedFirstSensorSecondTriggered.get()){
                LogicThread lastWorks = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED,failedFirstSensorSecondTriggered
                        ,exceededMaxDistance, Line.RED_SECOND_LINE, weCanUseSonar);
                Thread lw = new Thread(lastWorks);
                lw.start();
                children.add(lw);
                delegateMonitor(lw, new MonitorThread[]{});
            }else if(exceededMaxDistance.get()){
                LogicThread lastFails = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS
                        ,failedFirstSensorSecondTriggered
                        ,exceededMaxDistance, Line.RED_SECOND_LINE, weCanUseSonar);
                Thread lf = new Thread(lastFails);
                lf.start();
                children.add(lf);
                delegateMonitor(lf, new MonitorThread[]{});
            }
        }

        //Takes second picture
        Thread takepicturenow2 = new Thread(takePicture);
        takepicturenow2.start();
        children.add(takepicturenow2);
        delegateMonitor(takepicturenow2, new MonitorThread[]{});

        Command.ROBOT.addToProgress("second red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR);
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR);
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }
        //Checks Second picture
        Thread checkpicturenow2 = new Thread(checkPicture);
        checkpicturenow2.start();
        children.add(checkpicturenow2);
        delegateMonitor(checkpicturenow2, new MonitorThread[]{});

        Command.ROBOT.addToProgress("isAllRed /" + Boolean.toString(isAllRed.get()));
        if (!isAllRed.get()) {
            LogicThread pushRight = new PushRightButton(weCanUseSonar);
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }
//*****************************
//THE FOLLOWING BLOCK STRAFES TO RAMP
//*****************************
        LogicThread strafeToGoal = new RedStrafeToCenterGoal();
        Thread str = new Thread(strafeToGoal);
        str.start();
        children.add(str);
        delegateMonitor(str, new MonitorThread[]{});

    }
    private double getLineValue(LogicThread<AutonomousRobot> l) {
        double lineValue = (double) l.data.get(0);
        return lineValue;
    }
}












 /*// THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD TO THE LIST
        LogicThread moveToFirstBeacon = new RedAutonomousLogic(redIsLeft, vuforia);
        Thread mtfb = new Thread(moveToFirstBeacon);
        mtfb.start(); //Knocks Ball, Goes to First Beacon, Takes Pic
        children.add(mtfb);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(mtfb, new MonitorThread[]{});


        //Pushes Button
        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton();
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }
        /*
        LogicThread rmtscb = new RedMoveToSecondBeacon(redIsLeft, super.vuforia);
        Thread godThread = new Thread(rmtscb);
        godThread.start();
        children.add(godThread); //Goes to second beacon, takes pic
        delegateMonitor(godThread, new MonitorThread[]{});


        //pushes button
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton();
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }

        //strafes to ramp, deposits balls
        LogicThread rstr = new RedStrafeToRamp();
        Thread rst = new Thread(rstr);
        rst.start();
        children.add(rst);
        delegateMonitor(rst, new MonitorThread[]{});*/