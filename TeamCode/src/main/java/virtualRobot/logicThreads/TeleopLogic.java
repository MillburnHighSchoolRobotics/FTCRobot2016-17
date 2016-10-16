package virtualRobot.logicThreads;

import android.util.Log;

import virtualRobot.JoystickController;
import virtualRobot.LogicThread;
import virtualRobot.PIDController;
import virtualRobot.SallyJoeBot;
import virtualRobot.TeleopRobot;
import virtualRobot.commands.Command;
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

    @Override
    public void loadCommands() {
        final double BUTTON_PUSHER_STATIONARY = (PushLeftButton.BUTTON_PUSHER_LEFT + PushRightButton.BUTTON_PUSHER_RIGHT) / 2;

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
                if (controller1.isDown(JoystickController.BUTTON_LT)) {
                    Log.d("thingDown?", "Left Trigger");
                    //in the case of mecanum wheels, translating and strafing
                    double movementAngle = MathUtils.truncate(Math.toDegrees(controller1.getValue(JoystickController.THETA_1)),2);
                    double power = controller1.getValue(JoystickController.Y_2);
                    double scale = 0;
                    movementAngle = movementAngle < 0 ? movementAngle + 360 : movementAngle;
                    Log.d("translateJoy", movementAngle + " " + power);
                    if (movementAngle >= 0 && movementAngle <= 90) { //quadrant 1
                        scale = MathUtils.sinDegrees(movementAngle-45) / MathUtils.cosDegrees(movementAngle-45);
                        robot.getLFMotor().setPower(power * POWER_MATRIX[0][0]);
                        robot.getRFMotor().setPower(power * POWER_MATRIX[0][1] * scale);
                        robot.getLBMotor().setPower(power * POWER_MATRIX[0][2] * scale);
                        robot.getRBMotor().setPower(power * POWER_MATRIX[0][3]);
                    } else if (movementAngle  > 90 && movementAngle <= 180 ) { //quadrant 2
                        power *= -1;
                        scale = MathUtils.sinDegrees(movementAngle - 135) / MathUtils.cosDegrees(movementAngle - 135);
                        robot.getLFMotor().setPower(power * POWER_MATRIX[2][0] * scale);
                        robot.getRFMotor().setPower(power * POWER_MATRIX[2][1]);
                        robot.getLBMotor().setPower(power * POWER_MATRIX[2][2]);
                        robot.getRBMotor().setPower(power * POWER_MATRIX[2][3] * scale );
                    } else if (movementAngle > 180 && movementAngle <= 270) { //quadrant 3
                        scale = MathUtils.sinDegrees(movementAngle-225) / MathUtils.cosDegrees(movementAngle-225);
                        robot.getLFMotor().setPower(power * POWER_MATRIX[4][0]);
                        robot.getRFMotor().setPower(power * POWER_MATRIX[4][1] * scale );
                        robot.getLBMotor().setPower(power * POWER_MATRIX[4][2] * scale );
                        robot.getRBMotor().setPower(power * POWER_MATRIX[4][3]);
                        Log.d("aaa", robot.getLFMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRBMotor().getPower());
                    } else if (movementAngle > 270 && movementAngle < 360) { //quadrant 4
                        power *= -1;
                        scale = MathUtils.sinDegrees(movementAngle - 315) / MathUtils.cosDegrees(movementAngle-315);

                        robot.getLFMotor().setPower(power * POWER_MATRIX[6][0] * scale);
                        robot.getRFMotor().setPower(power * POWER_MATRIX[6][1]);
                        robot.getLBMotor().setPower(power * POWER_MATRIX[6][2]);
                        robot.getRBMotor().setPower(power * POWER_MATRIX[6][3] * scale);
                    }
                } else {
                    double leftPower = controller1.getValue(JoystickController.Y_1);
                    double rightPower = controller1.getValue(JoystickController.Y_2);
                    Log.d("tankJoy",leftPower + " " + rightPower);
                    robot.getLeftRotate().setPower(leftPower);
                    robot.getRightRotate().setPower(rightPower);
                }

                //Beacon Code
                if (controller1.isPressed(JoystickController.BUTTON_X)) {
                    PIDController allign = new PIDController(0, 0, 0, SallyJoeBot.BWTHRESHOLD);
                    double adjustedPower;
                    while (robot.getUltrasonicSensor().getValue() < 5) {
                        adjustedPower = allign.getPIDOutput(robot.getLineSensor().getValue());
                        robot.getLeftRotate().setPower(adjustedPower);
                        robot.getRightRotate().setPower(-adjustedPower);
                    }
                    robot.getLeftRotate().setPower(0);
                    robot.getRightRotate().setPower(0);
                }

                if (controller1.isDpadLeft()) {
                    robot.getButtonServo().setPosition(PushLeftButton.BUTTON_PUSHER_LEFT);
                } else if (controller1.isDpadRight()) {
                    robot.getButtonServo().setPosition(PushRightButton.BUTTON_PUSHER_RIGHT);
                } else {
                    robot.getButtonServo().setPosition(BUTTON_PUSHER_STATIONARY);
                }

                //reaper forward and backward
                if (controller1.isDown(JoystickController.BUTTON_RB)) {
                    robot.getReaperMotor().setPower(1);
                } else if (controller1.isDown(JoystickController.BUTTON_LB)) {
                    robot.getReaperMotor().setPower(-1);
                } else {
                    robot.getReaperMotor().setPower(0);
                }

                //lifting cap ball
                if (controller1.isDpadDown()) {
                    robot.getCapServo().setPosition(servoValOpen);
                } else if (controller1.isDpadUp()) {
                    robot.getCapServo().setPosition(servoValClosed);
                }
                    Log.d("TeleOp Motors", robot.getLFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getRBMotor().getPower());
                    try {
                        Thread.currentThread().sleep(30);
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                    }
            }
                Log.d("teleOpThread", "wasInterrupted");
                return isInterrupted;
            }
        });
    }
}
