//package virtualRobot.godThreads;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import virtualRobot.AutonomousRobot;
//import virtualRobot.GodThread;
//import virtualRobot.LogicThread;
//import virtualRobot.MonitorThread;
//import virtualRobot.commands.Command;
//import virtualRobot.commands.FTCTakePicture;
//import virtualRobot.logicThreads.AutonomousLayer1.BlueGoToWall;
//import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLine;
//import virtualRobot.logicThreads.CompensateForMiss;
//import virtualRobot.logicThreads.NoSensorAutonomouses.BlueStrafeToCenterGoal;
//import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
//import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;
//
///**
// * Created by 17osullivand on 12/2/16.
// */
//@Deprecated
//public class OldBlueAutoGodThread extends GodThread{
//    private final static boolean WITH_SONAR = true;
//    private AtomicBoolean redIsLeft = new AtomicBoolean();
//    private AtomicBoolean isAllRed = new AtomicBoolean();
//    private AtomicBoolean isAllRedAndRedIsLeft = new AtomicBoolean();
//    private boolean firstSmallCorrection = true;
//    private boolean secondSmallCorrection = true;
//    LogicThread takePicture = new LogicThread() {
//        @Override
//        public void loadCommands() {
//            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.TAKING_PICTURE, redIsLeft,vuforia); //Take a picture of beacon
//            commands.add(pic);
//        }
//    };
//    LogicThread checkPicture = new LogicThread() {
//        @Override
//        public void loadCommands() {
//            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.CHECKING_PICTURE,isAllRed, isAllRedAndRedIsLeft, vuforia); //Take a picture of beacon
//            commands.add(pic);
//        }
//    };
//    private AtomicBoolean sonarWorks = new AtomicBoolean();
//    private AtomicBoolean failedFirstSensorSecondTriggered = new AtomicBoolean(false);
//    private AtomicBoolean exceededMaxDistance = new AtomicBoolean(false);
//    private AtomicBoolean smallCorrection = new AtomicBoolean(false);
//
//    @Override
//    public void realRun() throws InterruptedException {
//        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD
//
//        LogicThread goToWall = new BlueGoToWall(sonarWorks);//Knocks Ball, Goes to first wall
//        Thread gtw = new Thread(goToWall);
//        gtw.start();
//        children.add(gtw);
//
//        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
//        delegateMonitor(gtw, new MonitorThread[]{});
//
////THE FOLLOWING BLOCK MOVES TO FIRST BEACON, TAKES PIC AND PUSHES BUTTON
////*****************************
//
//
//        boolean weCanUseSonar = sonarWorks.get() && WITH_SONAR;
//        LogicThread toFirstLine;
//        if (weCanUseSonar) { //If our sonar works, and we're using one
//            toFirstLine = new ToWhiteLine(true, GodThread.Line.BLUE_FIRST_LINE, failedFirstSensorSecondTriggered
//                    , exceededMaxDistance, smallCorrection); //Goes to firstLine
//        }else {
//            toFirstLine = new ToWhiteLine(false, GodThread.Line.BLUE_FIRST_LINE, failedFirstSensorSecondTriggered
//                    , exceededMaxDistance, smallCorrection);
//        }
//        Thread tfl = new Thread(toFirstLine);
//        tfl.start();
//        children.add(tfl);
//        delegateMonitor(tfl, new MonitorThread[]{});
//        if (smallCorrection.get() && firstSmallCorrection) {
//            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.SMALLCORRECTION, GodThread.Line.BLUE_FIRST_LINE, weCanUseSonar);
//            Thread adjust = new Thread(reAdjust);
//            adjust.start();
//            children.add(adjust);
//            delegateMonitor(adjust, new MonitorThread[]{});
//        }
//        if(failedFirstSensorSecondTriggered.get()){
//            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, GodThread.Line.BLUE_FIRST_LINE, weCanUseSonar);
//            Thread adjust = new Thread(reAdjust);
//            adjust.start();
//            children.add(adjust);
//            delegateMonitor(adjust, new MonitorThread[]{});
//        }else if(exceededMaxDistance.get()){
//            exceededMaxDistance.set(false);
//            failedFirstSensorSecondTriggered.set(false);
//            LogicThread firstFail= new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTFAILS,failedFirstSensorSecondTriggered
//                    ,exceededMaxDistance, GodThread.Line.BLUE_FIRST_LINE, weCanUseSonar);
//            Thread ff = new Thread(firstFail);
//            ff.start();
//            children.add(ff);
//            delegateMonitor(ff, new MonitorThread[]{});
//            if(failedFirstSensorSecondTriggered.get()){
//                LogicThread lastWorks = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED,failedFirstSensorSecondTriggered
//                        ,exceededMaxDistance, GodThread.Line.BLUE_FIRST_LINE, weCanUseSonar);
//                Thread lw = new Thread(lastWorks);
//                lw.start();
//                children.add(lw);
//                delegateMonitor(lw, new MonitorThread[]{});
//            }else if(exceededMaxDistance.get()){
//                LogicThread lastFails = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS
//                        ,failedFirstSensorSecondTriggered
//                        ,exceededMaxDistance, GodThread.Line.BLUE_FIRST_LINE, weCanUseSonar);
//                Thread lf = new Thread(lastFails);
//                lf.start();
//                children.add(lf);
//                delegateMonitor(lf, new MonitorThread[]{});
//            }
//        }
//
//
//        //TIME TO TAKE PICTURE
//        Thread takepicturenow = new Thread(takePicture);
//        takepicturenow.start();
//        children.add(takepicturenow);
//        delegateMonitor(takepicturenow, new MonitorThread[]{});
//
//        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
//        if (!redIsLeft.get()) {
//            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR, );
//            Thread pl = new Thread(pushLeft);
//            pl.start();
//            children.add(pl);
//            delegateMonitor(pl, new MonitorThread[]{});
//        }
//
//        else {
//            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR);
//            Thread pr = new Thread(pushRight);
//            pr.start();
//            children.add(pr);
//            delegateMonitor(pr, new MonitorThread[]{});
//        }
//        /*//TIME TO CHECK PICTURE
//        Thread checkpicturenow = new Thread(checkPicture);
//        checkpicturenow.start();
//        children.add(checkpicturenow);
//        delegateMonitor(checkpicturenow, new MonitorThread[]{});
//
//        Command.ROBOT.addToProgress("isAllRed /" + Boolean.toString(isAllRed.get()));
//        if (isAllRed.get()) {
//                LogicThread pauseLogic = new Pauselogic();
//                Thread pl = new Thread(pauseLogic);
//                pl.start();
//                delegateMonitor(pl, new MonitorThread[]{});
//             if (isAllRedAndRedIsLeft.get()) {
//                LogicThread pushRight = new PushRightButton(weCanUseSonar);
//                Thread pr = new Thread(pushRight);
//                pr.start();
//                children.add(pr);
//                delegateMonitor(pr, new MonitorThread[]{});
//            }
//                else {
//                LogicThread pushLeft = new PushLeftButton(weCanUseSonar);
//                Thread pr = new Thread(pushLeft);
//                pr.start();
//                children.add(pr);
//                delegateMonitor(pr, new MonitorThread[]{});
//            }
//        }*/
////*****************************
////THE FOLLOWING BLOCK MOVES TO SECOND BEACON, TAKES PIC AND PUSHES BUTTON (note that it's the same as above, but the Linetype is changed to second beacon)
////*****************************
//        exceededMaxDistance.set(false);
//        failedFirstSensorSecondTriggered.set(false);
//        smallCorrection.set(false);
//        LogicThread toSecondLine;
//        if (sonarWorks.get() && WITH_SONAR) { //If our sonar works, and we're using one
//            toSecondLine = new ToWhiteLine(true, GodThread.Line.BLUE_SECOND_LINE, failedFirstSensorSecondTriggered
//                    , exceededMaxDistance, smallCorrection);
//        }else {
//            toSecondLine = new ToWhiteLine(false, GodThread.Line.BLUE_SECOND_LINE, failedFirstSensorSecondTriggered
//                    , exceededMaxDistance, smallCorrection);
//        }
//        Thread tsl = new Thread(toSecondLine);
//        tsl.start();
//        children.add(tsl);
//        delegateMonitor(tsl, new MonitorThread[]{});
//        if (smallCorrection.get() && secondSmallCorrection) {
//            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.SMALLCORRECTION, GodThread.Line.BLUE_SECOND_LINE, weCanUseSonar);
//            Thread adjust = new Thread(reAdjust);
//            adjust.start();
//            children.add(adjust);
//            delegateMonitor(adjust, new MonitorThread[]{});
//        }
//        if(failedFirstSensorSecondTriggered.get()){
//            LogicThread reAdjust = new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTTRIGGERED, GodThread.Line.BLUE_SECOND_LINE, weCanUseSonar);
//            Thread adjust = new Thread(reAdjust);
//            adjust.start();
//            children.add(adjust);
//            delegateMonitor(adjust, new MonitorThread[]{});
//        }else if(exceededMaxDistance.get()){
//            exceededMaxDistance.set(false);
//            failedFirstSensorSecondTriggered.set(false);
//            LogicThread firstFail= new CompensateForMiss(CompensateForMiss.TriggerLevel.FIRSTLIGHTFAILS,failedFirstSensorSecondTriggered
//                    ,exceededMaxDistance, GodThread.Line.BLUE_SECOND_LINE, weCanUseSonar);
//            Thread ff = new Thread(firstFail);
//            ff.start();
//            children.add(ff);
//            delegateMonitor(ff, new MonitorThread[]{});
//            if(failedFirstSensorSecondTriggered.get()){
//                LogicThread lastWorks = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTTRIGGERED,failedFirstSensorSecondTriggered
//                        ,exceededMaxDistance, GodThread.Line.BLUE_SECOND_LINE, weCanUseSonar);
//                Thread lw = new Thread(lastWorks);
//                lw.start();
//                children.add(lw);
//                delegateMonitor(lw, new MonitorThread[]{});
//            }else if(exceededMaxDistance.get()){
//                LogicThread lastFails = new CompensateForMiss(CompensateForMiss.TriggerLevel.LASTLIGHTFAILS
//                        ,failedFirstSensorSecondTriggered
//                        ,exceededMaxDistance, GodThread.Line.BLUE_SECOND_LINE, weCanUseSonar);
//                Thread lf = new Thread(lastFails);
//                lf.start();
//                children.add(lf);
//                delegateMonitor(lf, new MonitorThread[]{});
//            }
//        }
//
//        //Takes second picture
//        Thread takepicturenow2 = new Thread(takePicture);
//        takepicturenow2.start();
//        children.add(takepicturenow2);
//        delegateMonitor(takepicturenow2, new MonitorThread[]{});
//
//        Command.ROBOT.addToProgress("second red is left /" + Boolean.toString(redIsLeft.get()));
//        if (!redIsLeft.get()) {
//            LogicThread pushLeft = new PushLeftButton(sonarWorks.get() && WITH_SONAR);
//            Thread pl = new Thread(pushLeft);
//            pl.start();
//            children.add(pl);
//            delegateMonitor(pl, new MonitorThread[]{});
//        }
//
//        else {
//            LogicThread pushRight = new PushRightButton(sonarWorks.get() && WITH_SONAR);
//            Thread pr = new Thread(pushRight);
//            pr.start();
//            children.add(pr);
//            delegateMonitor(pr, new MonitorThread[]{});
//        }
//        /*//Checks Second picture
//        Thread checkpicturenow2 = new Thread(checkPicture);
//        checkpicturenow2.start();
//        children.add(checkpicturenow2);
//        delegateMonitor(checkpicturenow2, new MonitorThread[]{});
//
//        Command.ROBOT.addToProgress("isAllRed /" + Boolean.toString(isAllRed.get()));
//        if (isAllRed.get()) {
//            LogicThread pauseLogic = new Pauselogic();
//            Thread pl = new Thread(pauseLogic);
//            pl.start();
//            delegateMonitor(pl, new MonitorThread[]{});
//
//            LogicThread pushRight = new PushRightButton(weCanUseSonar);
//            Thread pr = new Thread(pushRight);
//            pr.start();
//            children.add(pr);
//            delegateMonitor(pr, new MonitorThread[]{});
//        }*/
////*****************************
////THE FOLLOWING BLOCK STRAFES TO RAMP
////*****************************
//        LogicThread strafeToGoal = new BlueStrafeToCenterGoal();
//        Thread str = new Thread(strafeToGoal);
//        str.start();
//        children.add(str);
//        delegateMonitor(str, new MonitorThread[]{});
//
//    }
//    private double getLineValue(LogicThread<AutonomousRobot> l) {
//        double lineValue = (double) l.data.get(0);
//        return lineValue;
//    }
//}
