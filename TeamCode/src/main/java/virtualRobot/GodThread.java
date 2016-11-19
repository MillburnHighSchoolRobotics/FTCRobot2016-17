package virtualRobot;

import java.util.ArrayList;

import static virtualRobot.GodThread.ColorType.*;
import static virtualRobot.GodThread.LineType.*;

/**
 * Created by shant on 1/5/2016.
 * This will do a couple things:
 * 1. First it starts innerThread, which will run all of the logic threads located in the instantiaion of godThrad
 * 2. At the same time it checks to make sure innerThread is still running. If It detects that it has been interreptuted it kills innerThread
 * and all of it's children (the logicThreads that are still running in the innerThread)
 * 3. GodThread also has the delegateMonitor Method. All Logic Threads that are created in the innerThread
 * should have delegateMonitor called with them passed in. It will automatically stop ALL logic Threads (it kills innerThread) in innerThread if
 * there is a monitor and it goes off, and will at the very minimum  also make sure that that logicThread completeley finishes
 * executing before the next one in the innerThread is started
 */
public abstract class GodThread implements Runnable {
    private Thread innerThread;
    private boolean isInnerThreadRunning;
    protected ArrayList<Thread> children;
    protected VuforiaLocalizerImplSubclass vuforia;

    private class InnerThread implements Runnable {
        public void run() {
            try {
                realRun();
            } catch (InterruptedException e) {
                return;
            } finally {
                isInnerThreadRunning = false;
            }
        }
    }

    public GodThread() {
        innerThread = new Thread(new InnerThread());
        children = new ArrayList<Thread>();
    }

    public void run() {
        innerThread.start();
        isInnerThreadRunning = true;
        try {
            while (isInnerThreadRunning) {
                approve();
                Thread.sleep(5);
            }
        } catch (InterruptedException e) {
            killInnerThread();
        } finally {
            killActiveThreads();
        }
    }

    private void approve() {
        synchronized (this) {
            notifyAll();

        }
    }

    public void requestApproval() throws InterruptedException {
        synchronized (this) {
            wait();
        }
    }

    private void killActiveThreads() {
        for (Thread thread : children) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
    }

    protected void delegateMonitor(Thread logic, MonitorThread[] monitors) throws InterruptedException {
        while (logic.isAlive()) {
            boolean isNormal = true;
            for (MonitorThread m : monitors) {
                if (m.getStatus() != MonitorThread.NORMAL) {
                    isNormal = false;
                    break;
                }
            }

            if (!isNormal) {
                killInnerThread();
            }

            requestApproval();
        }
    }

    protected void killInnerThread() {
        innerThread.interrupt();
    }

    public abstract void realRun() throws InterruptedException;

    public void setVuforia(VuforiaLocalizerImplSubclass vuforia) {
        this.vuforia = vuforia;
    }

    public enum Line { //For use in autonomous to determine which line we're shooting for
        BLUE_FIRST_LINE(BLUE, FIRST),
        BLUE_SECOND_LINE(BLUE, SECOND),
        RED_FIRST_LINE(RED, FIRST),
        RED_SECOND_LINE(RED, SECOND);
        private  ColorType color;
        private LineType line;
        private Line(ColorType c, LineType l) {
            color = c;
            line = l;
        }
        public ColorType getColor() {
            return color;
        }
        public LineType getLine() {
            return line;
        }
    }
    public enum ColorType {
        RED,
        BLUE
    }
     public enum LineType {
        FIRST,
        SECOND
    }
}
