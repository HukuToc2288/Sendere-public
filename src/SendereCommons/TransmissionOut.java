package SendereCommons;

import java.io.File;

public abstract class TransmissionOut {

    public final RemoteUser user;
    public final String rooDirectory;
    public final String filename;
    public final int number;
    public final boolean isDirectory;
    private final Object pauseLock = new Object();
    protected boolean stop;

    public TransmissionOut(RemoteUser user, boolean isDirectory, int id, String path){
        this.user = user;
        File file = new File(path);
        this.rooDirectory = file.getParent();
        filename = file.getName();
        this.isDirectory = isDirectory;
        number = id;
    }

    public abstract void start();

    public abstract void onIntermediateSuccess();

    public abstract void onFail();

    public abstract void onSuccess();

    public void waitForResponse(){
        synchronized (pauseLock) {
            try {
                pauseLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        synchronized (pauseLock) {
            pauseLock.notifyAll(); // Unblocks thread
        }
    }

    public void terminate(){
        stop = true;
        resume();
    }
}
