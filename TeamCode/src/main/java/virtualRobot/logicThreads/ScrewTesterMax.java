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
                double threshold = 2;
                double curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
                double sign = Math.signum(curr);
                double power = 0.2;
                robot.addToTelemetry("Wall Values: ", curr + " " + sign);
                Log.d("WallTrace", curr + " " + sign);
                while (Math.abs(curr) > threshold) {
                    robot.getLeftRotate().setPower(power*sign);
                    robot.getRightRotate().setPower(-power*sign);
                    curr = robot.getSonarLeft().getValue() - robot.getSonarRight().getValue();
                    sign = Math.signum(curr);
                    robot.addToTelemetry("Wall Values: ", curr + " " + sign);
                    Log.d("WallTrace", curr + " " + sign);
                }
                Log.d("WallTrace", "Ended Orient");
                robot.getLeftRotate().setPower(0);
                robot.getLeftRotate().setPower(0);
                while (robot.getLineSensor().getValue() < SallyJoeBot.BWTHRESHOLD) {
                    Log.d("WallTrace", "Forward");
                    robot.getLeftRotate().setPower(1.0);
                    robot.getRightRotate().setPower(1.0);
                }
                Log.d("WallTrace", "Ended Forward");
                return Thread.currentThread().isInterrupted();
            }
        });
    }
}
