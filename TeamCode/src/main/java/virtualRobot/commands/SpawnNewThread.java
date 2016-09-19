package virtualRobot.commands;

import java.util.ArrayList;
import java.util.List;

import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;

/**
 * Created by DOSullivan on 10/28/15.
 */
public class SpawnNewThread implements Command {
	private List<LogicThread> logic;
    private List<Thread> t;
    
    private ExitCondition exitCondition;
    
    public SpawnNewThread() {
    	logic = new ArrayList<LogicThread>();
    	t = new ArrayList<Thread>();
    	
    	exitCondition = new ExitCondition() {
    		public boolean isConditionMet() {
    			return false;
    		}
    	};
    }
    
    public SpawnNewThread(List<LogicThread> l) {
    	this();
    	logic = l;
    }

    @Override
    public boolean changeRobotState() {
        int i = 0;
        boolean isInterrupted = false;
        
        while (!exitCondition.isConditionMet() && i < logic.size()) {
        	Thread temp = new Thread(logic.get(i++));
        	temp.start();
        	t.add(temp);
        	
        	if (Thread.currentThread().isInterrupted()) {
        		isInterrupted = true;
        		break;
        	}
        }
        
        return isInterrupted;
    }
    
    public List<Thread> getThreads() {
        return t;
    }
    
    public void addLogicThread(LogicThread l) {
    	logic.add(l);
    }

}
