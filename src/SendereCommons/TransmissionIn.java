package SendereCommons;

public abstract class TransmissionIn {


    public final RemoteUser user;
    public final String rootDir;
    public final int number;
    public long realData = 0 ;
    private String currentRelativePath;

    public TransmissionIn(RemoteUser user, int id, String rootDir){
        this.user = user;
        this.rootDir = rootDir;
        number = id;
    }

    public TransmissionIn(RemoteUser user, int id){
        this.user = user;
        this.rootDir = Settings.receivingDir;
        number = id;
    }

    /**
     * Creates transmission with all methods empty
     * This method using when we don't actually want to receive something, but have to create transmission to deny it
     * and there is no need to implement any methods
     * @param user user who want to send files
     * @param id unique id by which transmission can be determined
     * @return transmission with empty methods
     */
    public static TransmissionIn createDummyTransmission(RemoteUser user, int id){
        return new TransmissionIn(user, id) {
            @Override
            public boolean createDirectory(String relativePath) {
                return false;
            }

            @Override
            public boolean createFile(String relativePath) {
                return false;
            }

            @Override
            public boolean writeToFile(byte[] data, int off, int len) {
                return false;
            }

            @Override
            public boolean closeFile() {
                return false;
            }

            @Override
            public void onUpdateTransmissionSize(long size) {

            }

            @Override
            public void onDone() {

            }
        };
    }

    /**
     * Calls when sender requested to create directory
     * @param relativePath file path relative to root transmission's directory
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean createDirectory(String relativePath);

    /**
     * Calls when sender requested to create file
     * @param relativePath file path relative to root transmission's directory
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean createFile(String relativePath);

    /**
     * Calls when Sendere received raw data that should be written to opened file
     * @param data bytes to writing
     * @param len count of bytes from array that should be written
     * @return {@code true} on success, if returned {@code false} operation should be terminated
     */
    public abstract boolean writeToFile(byte[] data, int off, int len);

    /**
     * Calls when need to close file that was opened early by {@link #createFile}
     * @see #createFile(String)
     */
    public abstract boolean closeFile();

    /**
     * Calls when remote user notify about total size of files will be send by this transmission
     * Note that actual progress not updating by Sendere, so it should be calculated by external stuff
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

    public void writeToFile(byte[] data){
        writeToFile(data, 0, data.length);
    }
}
