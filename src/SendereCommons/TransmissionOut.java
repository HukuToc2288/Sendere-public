package SendereCommons;

import lombok.Data;

import java.io.File;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
public abstract class TransmissionOut {

    public final RemoteUser user;
    public final String rooDirectory;
    public final String filename;
    public final long id;
    public final boolean isDirectory;
    private final Object pauseLock = new Object();
    protected boolean stop;

    public TransmissionOut(RemoteUser user, boolean isDirectory, String path){
        this.user = user;
        File file = new File(path);
        this.rooDirectory = file.getParent();
        filename = file.getName();
        this.isDirectory = isDirectory;
        this.id = Settings.getNickname().hashCode()^user.getNickname().hashCode()^ThreadLocalRandom.current().nextLong();
    }

    public abstract void start();

    public abstract void onFail();

    public abstract void onSuccess();

    public void terminate(){
        stop = true;
    }
}
