package SendereCommons;

public class InRequest {
    public final RemoteUser who;
    public final boolean isDirectory;
    public final String transmissionId;
    public final String filename;

    public InRequest(RemoteUser who, boolean isDirectory, String transmissionId, String filename){
        this.who = who;
        this.isDirectory = isDirectory;
        this.transmissionId = transmissionId;
        this.filename = filename;
    }
}
