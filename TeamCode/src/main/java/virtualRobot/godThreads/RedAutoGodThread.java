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
import virtualRobot.logicThreads.CompensateForMiss;
import virtualRobot.logicThreads.NoSensorAutonomouses.BlueStrafeToCenterGoal;
import virtualRobot.logicThreads.NoSensorAutonomouses.Pauselogic;
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
    private final static boolean WITH_SONAR = true;
    private AtomicBoolean redIsLeft = new AtomicBoolean();
    boolean firstSmallCorrect = false;
    boolean secondSmallCorrect = false;
    LogicThread takePicture = new LogicThread() {
        @Override
        public void loadCommands() {
            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.TAKING_PICTURE, redIsLeft,vuforia); //Take a picture of beacon
            commands.add(pic);
        }
    };

    private AtomicBoolean sonarWorks = new AtomicBoolean();
    private AtomicBoolean allSensorsFailed = new AtomicBoolean(false);
    private AtomicBoolean lastSensorTriggered = new AtomicBoolean(false);
    private AtomicBoolean firstSensorTriggered = new AtomicBoolean(false);

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
        LogicThread toFirstLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_FIRST_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks);
        Thread tfl = new Thread(toFirstLine);
        tfl.start();
        children.add(tfl);
        delegateMonitor(tfl, new MonitorThread[]{});
       /* if (lastSensorTriggered.get()) {
            Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (firstSensorTriggered.get()) {
            Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        }*/
        if (allSensorsFailed.get()) {
            Command.AUTO_ROBOT.addToProgress("RunningAllSensorsFailed");

            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS, GodThread.Line.RED_FIRST_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        } else{
            Command.AUTO_ROBOT.addToProgress("CompensatingColor");
            LogicThread allignToLine = new ColorCompensator(Line.RED_FIRST_LINE);
            Thread atl = new Thread(allignToLine);
            atl.start();
            children.add(atl);
            delegateMonitor(atl, new MonitorThread[]{});
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

//*****************************
//THE FOLLOWING BLOCK MOVES TO SECOND BEACON, TAKES PIC AND PUSHES BUTTON (note that it's the same as above, but the Linetype is changed to second beacon)
//*****************************
        redIsLeft.set(false);
        lastSensorTriggered.set(false);
        allSensorsFailed.set(false);
        LogicThread toSecondLine = new ToWhiteLineCompensateColor(GodThread.Line.RED_SECOND_LINE, firstSensorTriggered, lastSensorTriggered, allSensorsFailed, sonarWorks );
        Thread tsl = new Thread(toSecondLine);
        tsl.start();
        children.add(tsl);
        delegateMonitor(tsl, new MonitorThread[]{});
        if (lastSensorTriggered.get()) {
            Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        }
        if (firstSensorTriggered.get()) {
            Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
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
        } else{
            Command.AUTO_ROBOT.addToProgress("Compensating color");

            LogicThread allignToLine = new ColorCompensator(Line.RED_SECOND_LINE);
            Thread atl = new Thread(allignToLine);
            atl.start();
            children.add(atl);
            delegateMonitor(atl, new MonitorThread[]{});
        }
        //TIME TO TAKE PICTURE
        Thread takepicturenow2 = new Thread(takePicture);
        takepicturenow2.start();
        children.add(takepicturenow2);
        delegateMonitor(takepicturenow2, new MonitorThread[]{});
        if (secondSmallCorrect) {
            Command.AUTO_ROBOT.addToProgress("LastSensorTriggered");
            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.SMALLCORRECTION, GodThread.Line.RED_SECOND_LINE, weCanUseSonar);
            Thread adjust = new Thread(reAdjust);
            adjust.start();
            children.add(adjust);
            delegateMonitor(adjust, new MonitorThread[]{});
        }
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