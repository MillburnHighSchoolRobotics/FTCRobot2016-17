package virtualRobot.godThreads;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.AutonomousLayer1.RedGoToWall;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineNoUltra;
import virtualRobot.logicThreads.AutonomousLayer2.ToLineUltra;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineNoUltraLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineNoUltraNoLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineUltraLine;
import virtualRobot.logicThreads.AutonomousLayer3.AllignLineUltraNoLine;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;
import virtualRobot.logicThreads.UnusedAutonomouses.RedAutonomousLogic;

/**
 * Created by shant on 1/10/2016.
 * Runs Red Autonomous With All LogicThreads
 * THIS IS EXACTLY SAME AS REDAUTOGODTHREAD EXCEPT THE LINETYPE ENUM IS CHANGED FROM BLUE TO RED AND THE GO TO WALL CHANGED TO RED(Go Trump)
 */
public class RedAutoGodThread extends GodThread {
    private AtomicBoolean firstRedIsLeft = new AtomicBoolean();
    private AtomicBoolean secondRedIsLeft = new AtomicBoolean();
    private AtomicBoolean sonarWorks = new AtomicBoolean();
    private AtomicBoolean lineSensorWorks = new AtomicBoolean();
    private final static boolean WITH_SONAR = UpdateThread.WITH_SONAR;

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
        if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
            LogicThread toFirstLine = new ToLineUltra(lineSensorWorks, Line.RED_FIRST_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineUltraLine(Line.RED_FIRST_LINE, getLineValue(toFirstLine), firstRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineUltraNoLine(Line.RED_FIRST_LINE, firstRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }

        } else { //our sonar fails or we're not using one
            LogicThread toFirstLine = new ToLineNoUltra(lineSensorWorks, Line.RED_FIRST_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineNoUltraLine(Line.RED_FIRST_LINE, getLineValue(toFirstLine), firstRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineNoUltraNoLine(Line.RED_FIRST_LINE, firstRedIsLeft, vuforia); //ReAdjust to line, take pic
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
        if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
            LogicThread toFirstLine = new ToLineUltra(lineSensorWorks, Line.RED_SECOND_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works (note we are rechecking this again even after checking before)
                LogicThread fixAllignment = new AllignLineUltraLine(Line.RED_SECOND_LINE, getLineValue(toFirstLine), secondRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineUltraNoLine(Line.RED_SECOND_LINE, secondRedIsLeft, vuforia); //ReAdjust to line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }

        } else { //our sonar fails, or we're not using one
            LogicThread toFirstLine = new ToLineNoUltra(lineSensorWorks, Line.RED_SECOND_LINE); //Goes to firstLine
            Thread tfl = new Thread(toFirstLine);
            tfl.start();
            children.add(tfl);
            delegateMonitor(tfl, new MonitorThread[]{});
            if (lineSensorWorks.get()) { //If our line sensor works
                LogicThread fixAllignment = new AllignLineNoUltraLine(Line.RED_SECOND_LINE, getLineValue(toFirstLine), secondRedIsLeft, vuforia); //ReAdjust to Line, take pic
                Thread fa = new Thread(fixAllignment);
                fa.start();
                children.add(fa);
                delegateMonitor(fa, new MonitorThread[]{});
            }
            else { //our line sensor fails
                LogicThread fixAllignment = new AllignLineNoUltraNoLine(Line.RED_SECOND_LINE, secondRedIsLeft, vuforia); //ReAdjust to line, take pic
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
        }
//*****************************
//THE FOLLOWING BLOCK STRAFES TO RAMP
//*****************************
        LogicThread strafeToRamp = new RedStrafeToRamp();
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