package virtualRobot;

/**
 * Created by shant on 10/14/2015.
 * Most Commands will stop after a custom exit condition is met.
 * Exit conditions implement this interface.
 */
public interface ExitCondition {
    boolean isConditionMet();
}
