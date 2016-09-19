package virtualRobot;

import java.util.ArrayList;

/**
 * Created by shant on 1/5/2016.
 */
public abstract class GodThread implements Runnable {
    private Thread innerThread;
    private boolean isInnerThreadRunning;
    protected ArrayList<Thread> children;

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
}
