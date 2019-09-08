package SendereCommons;

public abstract class TransmissionIn {


    public final RemoteUser user;
    public final String rootDir;
    public final int number;
    private String currentRelativePath;

    public TransmissionIn(RemoteUser user, int id, String rootDir){
        this.user = user;
        this.rootDir = rootDir;
        number = id;
    }

    public TransmissionIn(RemoteUser user, int id){
        this.user = user;
        this.rootDir = Settings.receivedDir;
        number = id;
    }

    /**
     * Calls when need to create directory
     * @param relativePath file path relative to root transmission's directory
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean createDirectory(String relativePath);

    /**
     * Calls when need to create file
     * @param relativePath file path relative to root transmission's directory
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean createFile(String relativePath);

    /**
     * Calls when Sendere received raw data that should be written to opened file
     * @param data bytes to writing
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean writeToFile(byte[] data);

    /**
     * Calls when need to close file that was opened early by {@code createFile}
     * @see #createFile(String)
     */
    public abstract boolean closeFile();

    /**
     * Calls when remote user notify about total files size that need to be send by this transmission
     * Note that actual progress not updating by Sendere, so it should be calculated by external stuff
     * Sorry for my bad english, I'm just B2
     * @param size total transaction size in bytes
     */
    public abstract void onUpdateTransmissionSize(long size);

    public abstract void onDone();

    public void setCurrentRelativePath(String currentRelativePath) {
        this.currentRelativePath = currentRelativePath;
    }

    public String getCurrentRelativePath() {
        return currentRelativePath;
    }
}
