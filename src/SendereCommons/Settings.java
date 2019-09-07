package SendereCommons;

public class Settings {
    /**
     * It is, actually, your nickname, that will be displayed to other Sendere users
     */
    public static String nickname = "Sendere User";

    /**
     * This parameter determines should other people see this device in Sendere interface
     * Invisible device also shouldn't answer on any messages and send extra information
     * about itself in PING requests.
     * 0 - device completely invisible
     * 1 - device visible to all Sendere users
     * 2 - device visible for friends only (not implemented yet)
     */
    public static int visibility = 1;

    /**
     * Directory where received files will be placed
     * Path has to be in UNIX style
     */
    public static String receivedDir = ".";
}
