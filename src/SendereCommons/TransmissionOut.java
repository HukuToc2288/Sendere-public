package SendereCommons;

public abstract class TransmissionOut {

    public final RemoteUser user;
    public final String rootFile;
    public final int number;
    private String currentRelativePath;
    private final Object pauseLock = new Object();
    protected boolean stop;

    public TransmissionOut(RemoteUser user, int id, String rootFile){
        this.user = user;
        this.rootFile = rootFile;
        number = id;
    }

    public abstract void start();

    public abstract void onIntermediateSuccess();

    public abstract void onFail();

    public abstract void onSuccess();

    public void setCurrentRelativePath(String currentRelativePath) {
        this.currentRelativePath = currentRelativePath;
    }

    public String getCurrentRelativePath() {
        return currentRelativePath;
    }

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
