package virtualRobot.godThreads;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.logicThreads.AutonomousLayer1.BlueGoToWall;
import virtualRobot.logicThreads.AutonomousLayer1.RedGoToWall;
import virtualRobot.logicThreads.AutonomousLayer2.ColorCompensator;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineNoUltra;
import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLine;
import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLineCompensateColor;
import virtualRobot.logicThreads.AutonomousLayer2.justRedIsLeft;
import virtualRobot.logicThreads.CompensateForMiss;
import virtualRobot.logicThreads.NoSensorAutonomouses.BlueStrafeToCenterGoal;
import virtualRobot.logicThreads.NoSensorAutonomouses.Pauselogic;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToCenterGoal;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;
import virtualRobot.logicThreads.NoSensorAutonomouses.moveAndFireBalls;
import virtualRobot.monitorThreads.TimeMonitor;

/**
 * Created by shant on 1/10/2016.
 * Runs Red Autonomous With All LogicThreads
 * THIS IS EXACTLY SAME AS BLUEAUTOGODTHREAD EXCEPT THE LINETYPE ENUM IS CHANGED FROM BLUE TO RED AND THE GO TO WALL CHANGED TO RED(Go Trump)
 */
public class RedAutoGodThread extends GodThread {
    private final static boolean WITH_SONAR = true;
    private AtomicBoolean redIsLeft = new AtomicBoolean();
    private AtomicBoolean sonarWorks = new AtomicBoolean();
    private AtomicBoolean allSensorsFailed = new AtomicBoolean(false);
    private AtomicBoolean lastSensorTriggered = new AtomicBoolean(false);
    private AtomicBoolean firstSensorTriggered = new AtomicBoolean(false);
    private AtomicBoolean colorTriggered = new AtomicBoolean(false);
    @Override
    public void realRun() throws InterruptedException {
        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD
        MonitorThread watchingForTime = new TimeMonitor(7000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);

        LogicThread fireBalls = new moveAndFireBalls();
        Thread fB = new Thread(fireBalls);
        fB.start();
        children.add(fB);
        delegateMonitor(fB, new MonitorThread[]{watchingForTime});


        LogicThread goToWall = new RedGoToWall(sonarWorks);//Knocks Ball, Goes to first wall
        Thread gtw = new Thread(goToWall);
        gtw.start();
        children.add(gtw);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(gtw, new MonitorThread[]{});
        if (!WITH_SONAR)
            sonarWorks.set(false);
//THE FOLLOWING BLOCK MOVES TO FIRST BEACON, TAKES PIC AND PUSHES BUTTON
//*****************************


        LogicThread toFirstLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.NORMAL);
        Thread tfl = new Thread(toFirstLine);
        tfl.start();
        children.add(tfl);
        delegateMonitor(tfl, new MonitorThread[]{});
            if (!colorTriggered.get()) {
                LogicThread withLights = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.COLOR_FAILED);
                Thread wl = new Thread(withLights);
                wl.start();
                children.add(wl);
                delegateMonitor(wl, new MonitorThread[]{});

            }
            if (allSensorsFailed.get()) {
                Command.AUTO_ROBOT.addToProgress("RunningAllSensorsFailed");

                LogicThread blindAllign = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.ALL_FAILED);
                Thread bl = new Thread(blindAllign);
                bl.start();
                children.add(bl);
                delegateMonitor(bl, new MonitorThread[]{});


                LogicThread isRedLeft = new justRedIsLeft(Line.RED_FIRST_LINE, vuforia, redIsLeft);
                Thread irl = new Thread(isRedLeft);
                 irl.start();
                children.add(irl);
                delegateMonitor(irl, new MonitorThread[]{});

            } else {
                Command.AUTO_ROBOT.addToProgress("CompensatingColor");
                LogicThread allignToLine = new ColorCompensator(Line.RED_FIRST_LINE, 2500, redIsLeft, sonarWorks, colorTriggered, vuforia);
                Thread atl = new Thread(allignToLine);
                atl.start();
                children.add(atl);
                delegateMonitor(atl, new MonitorThread[]{});
            }

        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR, Line.RED_FIRST_LINE, allSensorsFailed, colorTriggered);
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        } else {
            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR, Line.RED_FIRST_LINE, allSensorsFailed, colorTriggered);
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }

//*****************************
//THE FOLLOWING BLOCK MOVES TO SECOND BEACON, TAKES PIC AND PUSHES BUTTON (note that it's the same as above, but the Linetype is changed to second beacon)
//*****************************
        redIsLeft.set(false);
        lastSensorTriggered.set(false);
        allSensorsFailed.set(false);
        colorTriggered.set(false);

        LogicThread toSecondLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.NORMAL);
        //FIRST LINE = first line we go to
        Thread tsl = new Thread(toSecondLine);
        tsl.start();
        children.add(tsl);
        delegateMonitor(tsl, new MonitorThread[]{});
        if (!colorTriggered.get()) {
            LogicThread withLights = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.COLOR_FAILED);
            Thread wl = new Thread(withLights);
            wl.start();
            children.add(wl);
            delegateMonitor(wl, new MonitorThread[]{});

        }
        if (allSensorsFailed.get()) {
            Command.AUTO_ROBOT.addToProgress("RunningAllSensorsFailed");

            LogicThread blindAllign = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.ALL_FAILED);
            Thread bl = new Thread(blindAllign);
            bl.start();
            children.add(bl);
            delegateMonitor(bl, new MonitorThread[]{});


            LogicThread isRedLeft = new justRedIsLeft(Line.RED_SECOND_LINE, vuforia, redIsLeft);
            Thread irl = new Thread(isRedLeft);
            irl.start();
            children.add(irl);
            delegateMonitor(irl, new MonitorThread[]{});

        } else {
            Command.AUTO_ROBOT.addToProgress("CompensatingColor");
            LogicThread allignToLine = new ColorCompensator(Line.RED_SECOND_LINE, 2500, redIsLeft, sonarWorks, colorTriggered, vuforia);
            Thread atl = new Thread(allignToLine);
            atl.start();
            children.add(atl);
            delegateMonitor(atl, new MonitorThread[]{});
        }

        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR, Line.RED_SECOND_LINE, allSensorsFailed, colorTriggered);
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR, Line.RED_SECOND_LINE, allSensorsFailed, colorTriggered);
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
}










  /*  // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD
    MonitorThread watchingForTime = new TimeMonitor(7000);
    Thread tm = new Thread(watchingForTime);
tm.start();
        children.add(tm);

        LogicThread fireBalls = new moveAndFireBalls();
        Thread fB = new Thread(fireBalls);
        fB.start();
        children.add(fB);
        delegateMonitor(fB, new MonitorThread[]{watchingForTime});


        LogicThread goToWall = new RedGoToWall(sonarWorks);//Knocks Ball, Goes to first wall
        Thread gtw = new Thread(goToWall);
        gtw.start();
        children.add(gtw);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(gtw, new MonitorThread[]{});

//THE FOLLOWING BLOCK MOVES TO FIRST BEACON, TAKES PIC AND PUSHES BUTTON
/*//*****************************


        boolean weCanUseSonar = sonarWorks.get() && WITH_SONAR;
        LogicThread toFirstLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.NORMAL);
        //FIRST LINE = first line we go to
        Thread tfl = new Thread(toFirstLine);
        tfl.start();
        children.add(tfl);
        delegateMonitor(tfl, new MonitorThread[]{});
        //if (maxDistanceReached.get()) {
//            LogicThread correction = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, vuforia, ToWhiteLineCompensateColor.Mode.CORRECTION);
//            Thread cor = new Thread(correction);
//            cor.start();
//            children.add(cor);
//            delegateMonitor(cor, new MonitorThread[]{});

        if (lastSensorTriggered.get() && false) {
        Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (firstSensorTriggered.get() && false) {
        Command.AUTO_ROBOT.addToProgress("FirstSensorTriggered");
        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (allSensorsFailed.get()) {
        Command.AUTO_ROBOT.addToProgress("RunningAllSensorsFailed");

        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        // TIME TO TAKE PICTURE
        Thread takepicturenow = new Thread(takePicture);
        takepicturenow.start();
        children.add(takepicturenow);
        delegateMonitor(takepicturenow, new MonitorThread[]{});

        } else {
        Command.AUTO_ROBOT.addToProgress("CompensatingColor");
        LogicThread allignToLine = new ColorCompensator(Line.RED_FIRST_LINE, 1000, redIsLeft, sonarWorks, colorTriggered, vuforia);
        Thread atl = new Thread(allignToLine);
        atl.start();
        children.add(atl);
        delegateMonitor(atl, new MonitorThread[]{});
        }
//            //TIME TO TAKE PICTURE
//            Thread takepicturenow = new Thread(takePicture);
//            takepicturenow.start();
//            children.add(takepicturenow);
//            delegateMonitor(takepicturenow, new MonitorThread[]{});
//        //}
        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
        LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR, Line.RED_FIRST_LINE);
        Thread pl = new Thread(pushLeft);
        pl.start();
        children.add(pl);
        delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
        LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR, Line.RED_FIRST_LINE);
        Thread pr = new Thread(pushRight);
        pr.start();
        children.add(pr);
        delegateMonitor(pr, new MonitorThread[]{});
        }

/*//*****************************
//THE FOLLOWING BLOCK MOVES TO SECOND BEACON, TAKES PIC AND PUSHES BUTTON (note that it's the same as above, but the Linetype is changed to second beacon)
/*//*****************************
        redIsLeft.set(false);
        lastSensorTriggered.set(false);
        allSensorsFailed.set(false);
        maxDistanceReached.set(false);
        colorTriggered.set(false);
        LogicThread toSecondLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, colorTriggered, vuforia, ToWhiteLineCompensateColor.Mode.NORMAL);
        //FIRST LINE = first line we go to
        Thread tsl = new Thread(toSecondLine);
        tsl.start();
        children.add(tsl);
        delegateMonitor(tsl, new MonitorThread[]{});
        //if (maxDistanceReached.get()) {
//            LogicThread correction2 = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks, redIsLeft, vuforia, ToWhiteLineCompensateColor.Mode.CORRECTION);
//            Thread cor2 = new Thread(correction2);
//            cor2.start();
//            children.add(cor2);
//            delegateMonitor(cor2, new MonitorThread[]{});



        if (lastSensorTriggered.get() && false) {
        Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (firstSensorTriggered.get() && false) {
        Command.AUTO_ROBOT.addToProgress("FirstSensorTriggered");
        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (allSensorsFailed.get()) {
        Command.AUTO_ROBOT.addToProgress("RunningAllSensorsFailed");

        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        //TIME TO TAKE PICTURE
        Thread takepicturenow2 = new Thread(takePicture);
        takepicturenow2.start();
        children.add(takepicturenow2);
        delegateMonitor(takepicturenow2, new MonitorThread[]{});
        } else {
        Command.AUTO_ROBOT.addToProgress("Compensating color");

        LogicThread allignToLine = new ColorCompensator(Line.RED_SECOND_LINE, 1000, redIsLeft, sonarWorks, colorTriggered, vuforia);
        Thread atl = new Thread(allignToLine);
        atl.start();
        children.add(atl);
        delegateMonitor(atl, new MonitorThread[]{});
        }


//            //TIME TO TAKE PICTURE
//            Thread takepicturenow2 = new Thread(takePicture);
//            takepicturenow2.start();
//            children.add(takepicturenow2);
//            delegateMonitor(takepicturenow2, new MonitorThread[]{});

        if (secondSmallCorrect) {
        Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
        LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.SMALLCORRECTION, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
        Thread adjust = new Thread(reAdjust);
        adjust.start();
        children.add(adjust);
        delegateMonitor(adjust, new MonitorThread[]{});
        }
        //}

        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (redIsLeft.get()) {
        LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR, Line.RED_SECOND_LINE);
        Thread pl = new Thread(pushLeft);
        pl.start();
        children.add(pl);
        delegateMonitor(pl, new MonitorThread[]{});
        }

        else {
        LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR, Line.RED_SECOND_LINE);
        Thread pr = new Thread(pushRight);
        pr.start();
        children.add(pr);
        delegateMonitor(pr, new MonitorThread[]{});
        }

/*//*****************************
//THE FOLLOWING BLOCK STRAFES TO RAMP
/*//*****************************
        LogicThread strafeToGoal = new RedStrafeToCenterGoal();
        Thread str = new Thread(strafeToGoal);
        str.start();
        children.add(str);
        delegateMonitor(str, new MonitorThread[]{});

        }*/

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