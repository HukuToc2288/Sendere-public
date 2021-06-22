package sendereCommons;

public class InRequest {
    public final RemoteUser who;
    public final boolean isDirectory;
    public final long transmissionId;
    public final String filename;

    public InRequest(RemoteUser who, boolean isDirectory, long transmissionId, String filename){
        this.who = who;
        this.isDirectory = isDirectory;
        this.transmissionId = transmissionId;
        this.filename = filename;
    }
}
