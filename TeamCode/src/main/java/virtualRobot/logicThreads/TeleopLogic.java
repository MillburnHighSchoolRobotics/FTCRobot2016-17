package virtualRobot.logicThreads;

import virtualRobot.JoystickController;
import virtualRobot.LogicThread;
import virtualRobot.PIDController;
import virtualRobot.TeleopRobot;
import virtualRobot.commands.Command;
import virtualRobot.commands.MoveLift;
import virtualRobot.commands.MoveServo;
import virtualRobot.components.Servo;

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
 *
 *
 * <p/>
 *
 * Created by DOSullivan on 9/14/2016.
 */
public class TeleopLogic extends LogicThread<TeleopRobot> {

	private static final int POWER_MATRIX[][] = { //for each of the directions
            { 1, 1, 1, 1 },
            { 1, 0, 0, 1 },
            { 1, -1, -1, 1 },
            { 0, -1, -1, 0 },
            { -1, -1, -1, -1 },
            { -1, 0, 0, -1 },
            { -1, 1, 1, -1 },
            { 0, 1, 1,   0 }
    };

    private static final int servoValOpen = 0.05, servoValClosed = 0.45;

	@Override
	public void loadCommands () {
		final double BUTTON_PUSHER_STATIONARY = (PushLeftButton.BUTTON_PUSHER_LEFT + PushLeftButton.BUTTON_PUSHER_RIGHT) / 2;

        commands.add(new Command() {
        	@Override
            public boolean changeRobotState() {
	        	JoystickController controller1 = robot.getJoystickController1();
				JoystickController controller2 = robot.getJoystickController2();
	        	controller1.logicalRefresh();
	        	controller2.logicalRefresh();

	        	//Movement Code
	        	if (controller1.isdown(JoystickController.BUTTON_LT)) {
	        		//in the case of mecanum wheels, translating and strafing
	        		double movementAngle = controller1.getValue(JoystickController.THETA_1)
	        		double power = controller1.getValue(JoystickController.Y_2);
	        		double scale = 0;
	        		if (controller. >= 0 && movementAngle <= 90) { //quadrant 1
	                    scale = sinDegrees(45 - movementAngle) / cosDegrees(45 - movementAngle);
	                    robot.getLFMotor().setPower( * POWER_MATRIX[0][0]);
	                    robot.getRFMotor().setPower(maxPower * POWER_MATRIX[0][1] * scale);
	                    robot.getLBMotor().setPower(maxPower * POWER_MATRIX[0][2] * scale);
	                    robot.getRBMotor().setPower(maxPower * POWER_MATRIX[0][3]);
	                } else if (movementAngle > -270 && movementAngle <= -180  ) { //quadrant 2
	                    scale = sinDegrees(135 - movementAngle) / cosDegrees(135 - movementAngle);

	                    robot.getLFMotor().setPower(power * POWER_MATRIX[2][0] * scale);
	                    robot.getRFMotor().setPower(power * POWER_MATRIX[2][1]);
	                    robot.getLBMotor().setPower(power * POWER_MATRIX[2][2]);
	                    robot.getRBMotor().setPower(power * POWER_MATRIX[2][3] * scale );
	                } else if (movementAngle > -180 && movementAngle <= -90) { //quadrant 3
	                    scale = sinDegrees(225 - movementAngle) / cosDegrees(225 - movementAngle);

	                    robot.getLFMotor().setPower(power * POWER_MATRIX[4][0]);
	                    robot.getRFMotor().setPower(power * POWER_MATRIX[4][1] * scale );
	                    robot.getLBMotor().setPower(power * POWER_MATRIX[4][2] * scale );
	                    robot.getRBMotor().setPower(power * POWER_MATRIX[4][3]);
	                } else if (movementAngle > -90 && movementAngle <= 0) { //quadrant 4
	                    scale = sinDegrees(315 - movementAngle) / cosDegrees(315 - movementAngle);

	                    robot.getLFMotor().setPower(power * POWER_MATRIX[6][0] * scale);
	                    robot.getRFMotor().setPower(power * POWER_MATRIX[6][1]);
	                    robot.getLBMotor().setPower(power * POWER_MATRIX[6][2]);
	                    robot.getRBMotor().setPower(power * POWER_MATRIX[6][3] * scale);
	                }
	        	} else {

	        		//to be changed to synced motors
	        		robot.getLFMotor().setPower(joystick1.getValue(JoystickController.Y_1));
	        		robot.getLBMotor().setPower(joystick1.getValue(JoystickController.Y_1));
	            	robot.getRFMotor().setPower(joystick1.getValue(JoystickController.Y_2));
	            	robot.getRBMotor().setPower(joystick1.getValue(JoystickController.Y_2));
	        	}

	            //Beacon Code
	            if (controller1.isPressed(JoystickController.BUTTON_X)) {
	            	int side = 0;
	            	/*while(ultrasonic > wall) {
						robot.getLFMotor().setPower(1);
	        			robot.getLBMotor().setPower(1);
	            		robot.getRFMotor().setPower(1);
	            		robot.getRBMotor().setPower(1);		
	            	} */
	            		/* Camera Code */
	            	if (side == 1) {
	            		while (robot.getFlipperLeftServo().getPosition() < PushLeftButton.BUTTON_PUSHER_LEFT)
	            			robot.getFlipperLeftServo().setPosition(PushLeftButton.BUTTON_PUSHER_LEFT);
	            		while (robot.getFlipperLeftServo().getPosition() > BUTTON_PUSHER_STATIONARY)
	            			robot.getFlipperLeftServo().setPosition(TeleopLogic.BUTTON_PUSHER_STATIONARY);
	            	} else if (side == 2) {
	            		while (robot.getFlipperLeftServo().getPosition() > PushLeftButton.BUTTON_PUSHER_RIGHT)
	            			robot.getFlipperLeftServo().setPosition(PushLeftButton.BUTTON_PUSHER_RIGHT);
	            		while (robot.getFlipperLeftServo().getPosition() < BUTTON_PUSHER_STATIONARY)
	            			robot.getFlipperLeftServo().setPosition(TeleopLogic.BUTTON_PUSHER_STATIONARY);
	            	}
	            }

	            //reaper forward and backward
	            if (controller1.isDown(JoystickController.BUTTON_RB)) {
	            	robot.getReaperMotor().setPower(1);
	            } else (controller1.isDown(JoystickController.BUTTON_LB)) {
	            	robot.getReaperMotor().setPower(-1);
	            }

	            //lifting cap ball
	            if (controller1.isDpadDown()) {
	            	while (robot.getCapLeft().getPosition() > servoValOpen || robot.getCapLeft().getPosition() > servoValOpen)
	            		if (robot.getCapLeft().getPosition() > servoValOpen)
	            			robot.getCapLeft().setPosition(robot.getCapLeft().getValue() - 0.01);
	            		if (robot.getCapRight().getPosition() > servoValOpen)
	            			robot.getCapRight().setPosition(robot.getCapRight().getValue() - 0.01);
	            } else if (controller1.isDpadUp()) {
	            	while (robot.getCapLeft().getPosition() < servoValClosed || robot.getCapLeft().getPosition() < servoValClosed)
	            		if (robot.getCapLeft().getPosition() < servoValClosed)
	            			robot.getCapLeft().setPosition(robot.getCapLeft().getValue() - 0.01);
	            		if (robot.getCapRight().getPosition() < servoValClosed)
	            			robot.getCapRight().setPosition(robot.getCapRight().getValue() - 0.01);
	            }
	        }
        })
    }
}