package virtualRobot.logicThreads;

import android.util.Log;

import virtualRobot.JoystickController;
import virtualRobot.LogicThread;
import virtualRobot.TeleopRobot;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushLeftButton;
import virtualRobot.logicThreads.NoSensorAutonomouses.PushRightButton;
import virtualRobot.utils.MathUtils;

/**
 * _____ ______   ___  ___  ________
 * |\   _ \  _   \|\  \|\  \|\   ____\             .-""-.
 * \ \  \\\__\ \  \ \  \\\  \ \  \___|_           /[] _ _\
 * \ \  \\|__| \  \ \   __  \ \_____  \         _|_o_LII|_
 * \ \  \    \ \  \ \  \ \  \|____|\  \       / | ==== | \
 * \ \__\    \ \__\ \__\ \__\____\_\  \      |_| ==== |_|
 * \|__|     \|__|\|__|\|__|\_________\      ||" ||  ||
 * \|_________|      ||LI  o ||
 * ||'----'||
 * /__|    |__\
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * Created by DOSullivan on 9/14/2016.
 * Teleop! For Robot!
 */
public class TeleopLogic extends LogicThread<TeleopRobot> {

    private static final int POWER_MATRIX[][] = { //for each of the directions
            {1, 1, 1, 1},
            {1, 0, 0, 1},
            {1, -1, -1, 1},
            {0, -1, -1, 0},
            {-1, -1, -1, -1},
            {-1, 0, 0, -1},
            {-1, 1, 1, -1},
            {0, 1, 1, 0}
    };

    private static final double servoValOpen = 1.0, servoValClosed = 0.25;
    public static final double BUTTON_PUSHER_STATIONARY = (PushLeftButton.BUTTON_PUSHER_LEFT + PushRightButton.BUTTON_PUSHER_RIGHT) / 2;
    public final static double CAP_LEFT_OPEN = 1;
    public final static double CAP_LEFT_CLOSED = 0;
    public final static double CAP_RIGHT_OPEN = 1;
    public final static double CAP_RIGHT_CLOSED = 0;

    @Override
    public void loadCommands() {

        commands.add(new Command() {
            @Override
            public boolean changeRobotState() {
                boolean isInterrupted = false;
                JoystickController controller1 = robot.getJoystickController1();
                JoystickController controller2 = robot.getJoystickController2();
                while (!isInterrupted) {
                    controller1.logicalRefresh();
                    controller2.logicalRefresh();

                //Movement Code
                    if (!MathUtils.equals(controller1.getValue(JoystickController.R_1), 0) ) {
                        Log.d("joystickInput", "Left Stick Stationary");
                        double angle = Math.toDegrees(controller1.getValue(JoystickController.THETA_1));
                        double power = controller1.getValue(JoystickController.R_1);
                        angle = angle < 0 ? angle + 360 : angle;
                        Log.d("teleOpMovement", "Turn: " + angle + " " + power);
                        if (angle >= 45 && angle < 135) {
                            robot.getLeftRotate().setPower(power);
                            robot.getRightRotate().setPower(power);
                        } else if (angle < 45 || angle >= 315) {
                            robot.getLeftRotate().setPower(power);
                            robot.getRightRotate().setPower(-power);
                        } else if (angle >= 225 && angle < 315) {
                            robot.getLeftRotate().setPower(-power);
                            robot.getRightRotate().setPower(-power);
                        } else if (angle >= 135 && angle < 225) {
                            robot.getLeftRotate().setPower(-power);
                            robot.getRightRotate().setPower(power);
                        }
                    } else {
                        Log.d("joystickInput", "Left Stick Moved");
                        //in the case of mecanum wheels, translating and strafing
                        double movementAngle = MathUtils.truncate(Math.toDegrees(controller1.getValue(JoystickController.THETA_2)),2);
                        double power = controller1.getValue(JoystickController.R_2);
                        double scale = 0;
                        double LF = 0, RF = 0, LB = 1, RB = 1;
                        movementAngle = movementAngle < 0 ? movementAngle + 360 : movementAngle;
                        Log.d("teleOpMovement", "Translate: " + movementAngle + " " + power);
                        if (movementAngle >= 0 && movementAngle <= 90) { //quadrant 1
                            scale = MathUtils.sinDegrees(movementAngle-45) / MathUtils.cosDegrees(movementAngle-45);
                            LF = power * POWER_MATRIX[0][0];
                            RF = power * POWER_MATRIX[0][1] * scale;
                            LB = power * POWER_MATRIX[0][2] * scale;
                            RB = power * POWER_MATRIX[0][3];
                        } else if (movementAngle  > 90 && movementAngle <= 180 ) { //quadrant 2
                            power *= -1;
                            scale = MathUtils.sinDegrees(movementAngle - 135) / MathUtils.cosDegrees(movementAngle - 135);
                            LF = (power * POWER_MATRIX[2][0] * scale);
                            RF = (power * POWER_MATRIX[2][1]);
                            LB = (power * POWER_MATRIX[2][2]);
                            RB = (power * POWER_MATRIX[2][3] * scale );
                        } else if (movementAngle > 180 && movementAngle <= 270) { //quadrant 3
                            scale = MathUtils.sinDegrees(movementAngle-225) / MathUtils.cosDegrees(movementAngle-225);
                            LF = (power * POWER_MATRIX[4][0]);
                            RF = (power * POWER_MATRIX[4][1] * scale );
                            LB = (power * POWER_MATRIX[4][2] * scale );
                            RB = (power * POWER_MATRIX[4][3]);
                            Log.d("aaa", robot.getLFMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRBMotor().getPower());
                        } else if (movementAngle > 270 && movementAngle < 360) { //quadrant 4
                            power *= -1;
                            scale = MathUtils.sinDegrees(movementAngle - 315) / MathUtils.cosDegrees(movementAngle-315);
                            LF = (power * POWER_MATRIX[6][0] * scale);
                            RF = (power * POWER_MATRIX[6][1]);
                            LB = (power * POWER_MATRIX[6][2]);
                            RB = (power * POWER_MATRIX[6][3] * scale);
                        }
//                        robot.getLeftRotate().setRatioAndPower(LF,LB);
//                        robot.getRightRotate().setRatioAndPower(RF,RB);
                        robot.getLFMotor().setPower(LF);
                        robot.getLBMotor().setPower(LB);
                        robot.getRFMotor().setPower(RF);
                        robot.getRBMotor().setPower(RB);
                        robot.getLeftRotate().zeroEncoders();
                        robot.getRightRotate().zeroEncoders();
                    }

                    robot.getTelemetry().put("Speed Ratio: ", robot.getLeftRotate().getSpeedRatio() + " " + robot.getRightRotate().getSpeedRatio());

                    //Beacon Code
                    if (controller2.isPressed(JoystickController.BUTTON_X)) {
//                        PIDController allign = new PIDController(0.5,0.5,0.5, SallyJoeBot.BWTHRESHOLD);
//                        double adjustedPower;
//                        double basePower = 0.5;
//                        while (robot.getUltrasonicSensor().getValue() < 2) {
//                            adjustedPower = allign.getPIDOutput(robot.getLineSensor().getValue());
//                            robot.getLFMotor().setPower(basePower + adjustedPower);
//                            robot.getLBMotor().setPower(-basePower + adjustedPower);
//                            robot.getRFMotor().setPower(-basePower - adjustedPower);
//                            robot.getRBMotor().setPower(basePower - adjustedPower);
//                        }
                        robot.stopMotors();
                    }

                    //button pusher
                    if (controller2.isDown(JoystickController.BUTTON_LT)) {
                        robot.getButtonServo().setPosition(PushLeftButton.BUTTON_PUSHER_LEFT);
                    } else if (controller2.isDown(JoystickController.BUTTON_RT)) {
                        robot.getButtonServo().setPosition(PushRightButton.BUTTON_PUSHER_RIGHT);
                    } else {
                        robot.getButtonServo().setPosition(BUTTON_PUSHER_STATIONARY);
                    }

                    //reaper forward and backward
//                    if (controller1.isDown(JoystickController.BUTTON_RB)) {
//                        robot.getReaperMotor().setPower(1);
//                    } else if (controller1.isDown(JoystickController.BUTTON_LB)) {
//                        robot.getReaperMotor().setPower(-1);
//                    } else {
////                        robot.getReaperMotor().setPosition(robot.getReaperEncoder(), 500);
//                        robot.getReaperMotor().setPower(0);
//                    }

                    if(controller1.isDpadLeft()) {
                        robot.getLFMotor().setPower(1.0);
                    }
                    if(controller1.isDpadRight()) {
                        robot.getRFMotor().setPower(1.0);
                    }
                    if(controller1.isDpadUp()) {
                        robot.getLBMotor().setPower(1.0);
                    }
                    if(controller1.isDpadDown()) {
                        robot.getRBMotor().setPower(1.0);
                    }

                    if(controller2.isDpadUp()) {
                        robot.getLiftLeftMotor().setPower(1.0);
                        robot.getLiftRightMotor().setPower(1.0);
                    } else if (controller2.isDpadDown()) {
                        robot.getLiftLeftMotor().setPower(-1.0);
                        robot.getLiftRightMotor().setPower(-1.0);
                    } else {
                        robot.getLiftLeftMotor().setPower(0);
                        robot.getLiftRightMotor().setPower(0);
                    }

                    //lifting cap ball
                    /*if (controller2.isPressed(JoystickController.BUTTON_A)) {
                        if(MathUtils.equals(robot.getCapLeftServo().getPosition(),CAP_LEFT_OPEN))
                            robot.getCapLeftServo().setPosition(CAP_LEFT_CLOSED);
                        else
                            robot.getCapLeftServo().setPosition(CAP_LEFT_OPEN);
                    } else if (controller2.isPressed(JoystickController.BUTTON_B)) {
                        if(MathUtils.equals(robot.getCapRightServo().getPosition(),CAP_RIGHT_OPEN))
                            robot.getCapRightServo().setPosition(CAP_RIGHT_CLOSED);
                        else
                            robot.getCapRightServo().setPosition(CAP_RIGHT_OPEN);
                    }*/

//                    if (controller2.getValue(JoystickController.Y_1) > 0.05) {
//                        double currLeft = robot.getSonarLeft().getValue();
//                        double currRight = robot.getSonarRight().getValue();
//                        double tp = 0.2;
//
//                        double errClose = (8 - currLeft)*0.008;
//                        double errAllign = (currLeft - currRight)*0.012;
//
//                        robot.getLBMotor().setPower(tp - errClose - errAllign);
//                        robot.getLFMotor().setPower(tp - errClose - errAllign);
//                        robot.getRFMotor().setPower(tp + errClose + errAllign);
//                        robot.getRBMotor().setPower(tp + errClose + errAllign);
//                    } else if (controller2.getValue(JoystickController.Y_1) < -0.05) {
//                        double currLeft = robot.getSonarRight().getValue();
//                        double currRight = robot.getSonarLeft().getValue();
//                        double tp = 0.2;
//
//                        double errClose = (8 - currLeft)*0.008;
//                        double errAllign = (currLeft - currRight)*0.012;
//
//                        robot.getLBMotor().setPower((tp - errClose - errAllign)*-1);
//                        robot.getLFMotor().setPower((tp - errClose - errAllign)*-1);
//                        robot.getRFMotor().setPower((tp + errClose + errAllign)*-1);
//                        robot.getRBMotor().setPower((tp + errClose + errAllign)*-1);
//                    }

                    try {
                        Thread.currentThread().sleep(30);
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                    }
            }
                Log.d("FTCThreads", "teleOp was Interrupted");
                return isInterrupted;
            }
        });
    }
}
