package virtualRobot.logicThreads.TestingAutonomouses;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.Rotate;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class RotateAutoPIDTester extends LogicThread {
    double kP;
    AtomicBoolean isTime;

    public RotateAutoPIDTester(double kP, AtomicBoolean ab) {
        this.kP = kP;
        this.isTime = ab;
    }

    @Override
    public void loadCommands() {
        Rotate r = new Rotate(kP,90,40,isTime);
        r.setExitCondition(new ExitCondition() {
            double lastYaw = -400;
            @Override
            public boolean isConditionMet() {
                double currYaw = robot.getHeadingSensor().getValue();
                boolean isSatisfied = MathUtils.equals(currYaw, lastYaw);
                lastYaw = currYaw;
                return isSatisfied;
            }
        });
        commands.add(r);
    }
}
