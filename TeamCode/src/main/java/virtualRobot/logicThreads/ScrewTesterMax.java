package virtualRobot.logicThreads;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.PIDController;
import virtualRobot.SallyJoeBot;
import virtualRobot.commands.Command;

/**
 * Created by ethachu19 on 10/27/2016.
 */

public class ScrewTesterMax extends LogicThread<AutonomousRobot> {
    private static final double SCALE = .7;
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

        commands.add(new Command() {
            @Override
            public boolean changeRobotState() throws InterruptedException
            {
//                double threshold = 1;
//                double curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
//                double sign = Math.signum(curr);
//                double power = 0.15;
//                robot.addToTelemetry("Wall Values: ", curr + " " + sign);
//                Log.d("WallTrace", curr + " " + sign);
//                while (Math.abs(curr) > threshold) {
//                    robot.getLeftRotate().setPower(power*sign);
//                    robot.getRightRotate().setPower(-power*sign);
//                    curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
//                    sign = Math.signum(curr);
//                    robot.addToTelemetry("Wall Values: ", curr + " " + sign);
//                    Log.d("WallTrace", curr + " " + sign);
//                }
//                Log.d("WallTrace", "Ended Orient");
                robot.stopMotors();
                double tp = 0.2;
                double kp = 3.1;
                double kp2 = 5.1;
                double kd = 3.1;
                double target = 4.5;
                PIDController close = new PIDController(0.07,0,0,0,13);
                PIDController allign = new PIDController(0.13,0,0,0,0);
                double currLeft, currRight, errClose, errAllign;
                while (robot.getLineSensor().getValue() > SallyJoeBot.BWTHRESHOLD) {
                    currLeft = robot.getSonarLeft().getValue();
                    currRight = robot.getSonarRight().getValue();

                    errClose = close.getPIDOutput(currLeft);
                    errAllign = allign.getPIDOutput(currLeft-currRight);
                    robot.getRightRotate().setPower(tp - errClose - errAllign);
                    robot.getLeftRotate().setPower(tp + errClose + errAllign);
                    Log.d("WallTrace", "Forward " + currLeft + " " + currRight + "/n Errors: " + errClose + " " + errAllign);
//                    robot.getLeftRotate().setPower(0.15);
//                    robot.getRightRotate().setPower(0.15);
                }
                Log.d("WallTrace", "Ended Forward");
                robot.stopMotors();
                return Thread.currentThread().isInterrupted();
            }
        });
    }
}
