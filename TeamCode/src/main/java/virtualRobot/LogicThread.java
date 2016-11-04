package virtualRobot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import virtualRobot.commands.Command;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.commands.addData;

/**
 * Created by shant on 10/8/2015.
 * Every LogicThread class when it is extends this abstract class will create to a queue of commands.
 * This is done in the abstract loadCommands method.
 * When that logic thread is instantiated and a thread is made and started using it, the run() commands is excecuted
 * This goes through the aforementioned queue of commands, executing (calling changeRobotState()) each one
 */
public abstract class LogicThread<T extends AutonomousRobot> implements Runnable {
    protected List<Command> commands; //contains our queue of commands
    protected List<Thread> children; //contains a list of threads created under this logic Thread using spawn new thread
    protected T robot;

    protected double startTime, elapsedTime;
    public HashMap<LogicThread<AutonomousRobot>, Object[]> data = new HashMap<LogicThread<AutonomousRobot>, Object[]>(); //if a logic thread wants to store data
    @Override
    public void run(){

        loadCommands();
        startTime = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted() && (commands.size() != 0)) {
            boolean isInterrupted = false;
            Command c = commands.remove(0);
            if (c instanceof Rotate) {
                if (((Rotate)c).getName() != null) robot.addToProgress(((Rotate)c).getName());
            }
            if (c instanceof Translate) {
                if (((Translate)c).getName() != null) robot.addToProgress(((Translate)c).getName());
            }
            if (c instanceof addData) {
                data.put(((addData)c).getLogicThread(), ((addData)c).getMyData());
            }
            try {
                isInterrupted = c.changeRobotState();
            }
            catch (InterruptedException e) {
                isInterrupted = true;
            }
            if (c instanceof SpawnNewThread) { //If a new thread is spawned well in the logic Thread, we have to pay special attention to it, because we want to be able to automatically kill it if this logicThread every dies.
                List<Thread> threadList = ((SpawnNewThread) c).getThreads();

                for (Thread t : threadList) {
                    children.add(t);
                }
            }

            elapsedTime = System.currentTimeMillis() - startTime;


            if (isInterrupted)
                break;
        }


        for (Thread x: children) //Kill any threads made using spawn new thread
            if (x.isAlive())
                x.interrupt();

    }

    public LogicThread() {
        robot = (T) Command.ROBOT;
        commands = new ArrayList<Command>();
        children = new ArrayList<Thread>();
    }

    public abstract void loadCommands();
}
