package celuk.groot.remote;

public class Updater {
    
    protected boolean continueUpdating = false;
    protected Thread updaterThread = null;
    protected int updateInterval = 2000;
    
    public Updater() {
    }

    protected void update() {
    }

    public void startUpdating(int interval) {
        this.updateInterval = interval;
        if (updaterThread == null) {
            this.continueUpdating = true;
            updaterThread = new Thread(() -> {
                while (this.continueUpdating) {
                    try {
                        update();           
                        Thread.sleep(updateInterval);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }
            });
            updaterThread.setDaemon(true);
            updaterThread.start();
        }
    }

    public void stopUpdating() {
        continueUpdating = false;
        updaterThread.interrupt();
        updaterThread = null;
    }
}
