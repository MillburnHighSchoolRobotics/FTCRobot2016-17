package virtualRobot.godThreads;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
//import virtualRobot.logicThreads.BlueDumpPeople;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.AutonomousLayer1.BlueGoToWall;
import virtualRobot.logicThreads.AutonomousLayer1.RedGoToWall;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineNoUltra;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineUltra;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineNoUltraLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineNoUltraNoLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineUltraLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineUltraNoLine;
import virtualRobot.logicThreads.NoSensorAutonomouses.BlueStrafeToRamp;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;
import virtualRobot.logicThreads.UnusedAutonomouses.BlueAutonomousLogic;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;

//import virtualRobot.logicThreads.BlueDumpPeople;

/**
 * Created by shant on 1/5/2016.
 * Runs Blue Autonomous with All Logic Threads
 * THIS IS EXACTLY SAME AS REDAUTOGODTHREAD EXCEPT THE LINETYPE ENUM IS CHANGED FROM RED TO BLUE AND THE GO TO WALL CHANGED TO BLUE (Go Hillary)
 */
public class BlueAutoGodThread extends GodThread {
    private AtomicBoolean firstRedIsLeft = new AtomicBoolean();
    private AtomicBoolean secondRedIsLeft = new AtomicBoolean();
    private AtomicBoolean sonarWorks = new AtomicBoolean();
    private AtomicBoolean lineSensorWorks = new AtomicBoolean();
    private final static boolean WITH_SONAR = UpdateThread.WITH_SONAR;

    @Override
    public void realRun() throws InterruptedException {
        double lineTarget = 4;
        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD
        LogicThread goToWall = new BlueGoToWall(sonarWorks);//Knocks Ball, Goes to first wall
        Thread gtw = new Thread(goToWall);
        gtw.start();
        children.add(gtw);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(gtw, new MonitorThread[]{});

//THE FOLLOWING BLOCK MOVES TO FIRST BEACON, TAKES PIC AND PUSHES BUTTON
//*****************************
        if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
            LogicThread toFirstLine = new ToLineUltra(lineSensorWorks, Line.BLUE_FIRST_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineUltraLine(Line.BLUE_FIRST_LINE, getLineValue(toFirstLine), firstRedIsLeft, vuforia); //ReAdjust to Line, take pic
                lineTarget = getLineValue(toFirstLine);
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineUltraNoLine(Line.BLUE_FIRST_LINE, firstRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }

        } else { //our sonar fails or we're not using one
            LogicThread toFirstLine = new ToLineNoUltra(lineSensorWorks, Line.BLUE_FIRST_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineNoUltraLine(Line.BLUE_FIRST_LINE, getLineValue(toFirstLine), firstRedIsLeft, vuforia); //ReAdjust to Line, take pic
                lineTarget = getLineValue(toFirstLine);
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineNoUltraNoLine(Line.BLUE_FIRST_LINE, firstRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
        }
        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(firstRedIsLeft.get()));
        if (firstRedIsLeft.get()) {
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
       /* if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
            LogicThread toFirstLine = new ToLineUltra(lineSensorWorks, Line.BLUE_SECOND_LINE, lineTarget, lineSensorWorks.get()); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works (note we are rechecking this again even after checking before)
                LogicThread fixAllignment = new AllignLineUltraLine(Line.BLUE_SECOND_LINE, lineTarget, secondRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineUltraNoLine(Line.BLUE_SECOND_LINE, secondRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }

        } else { //our sonar fails, or we're not using one
            LogicThread toFirstLine = new ToLineNoUltra(lineSensorWorks, Line.BLUE_SECOND_LINE, lineTarget, lineSensorWorks.get()); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineNoUltraLine(Line.BLUE_SECOND_LINE, lineTarget, secondRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineNoUltraNoLine(Line.BLUE_SECOND_LINE, secondRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
        }
        Command.ROBOT.addToProgress("second red is left /" + Boolean.toString(secondRedIsLeft.get()));
        if (secondRedIsLeft.get()) {
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
        }*/
//*****************************
//THE FOLLOWING BLOCK STRAFES TO RAMP
//*****************************
        LogicThread strafeToRamp = new BlueStrafeToRamp();
        Thread str = new Thread(strafeToRamp);
        str.start();
        children.add(str);
        delegateMonitor(str, new MonitorThread[]{});

    }
    private double getLineValue(LogicThread<AutonomousRobot> l) {
        double lineValue = (double) l.data.get(0);
        return lineValue;
    }

}













        /*MonitorThread watchingForTime = new TimeMonitor(System.currentTimeMillis(), 30000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);*/

// THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD TO THE LIST
        /*LogicThread moveToFirstBeacon = new BlueAutonomousLogic(redIsLeft, vuforia);
        Thread mtfb = new Thread(moveToFirstBeacon);
        mtfb.start(); //Knocks Ball, Goes to First Beacon, Takes Pic
        children.add(mtfb);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(mtfb, new MonitorThread[]{});


        //Pushes Button
        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (!redIsLeft.get()) {
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

       /* LogicThread rmtscb = new BlueMoveToSecondBeacon(redIsLeft, super.vuforia);
        Thread godThread = new Thread(rmtscb);
        godThread.start();
        children.add(godThread); //Goes to second beacon, takes pic
        delegateMonitor(godThread, new MonitorThread[]{});


        //pushes button
        if (!redIsLeft.get()) {
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
        delegateMonitor(rst, new MonitorThread[]{}); */