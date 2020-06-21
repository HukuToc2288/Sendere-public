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
    public static String receivingDir = System.getProperty("user.home");

    /**
     * Show that user allows to receive text messages
     * If set to {@code true}, when packet with {@link Headers#TEXT} header will be received,
     * {@link Sendere#onTextMessageReceived} implementation will be invoked
     * Otherwise, nothing happens
     * Also when user sends text message when this parameter set to false, he should be
     * warned, that he won't be able to receive answer
     */
    public static boolean allowChat = true;

    /**
     * Show that user allows to receive files and directories
     * If set to {@code true}, {@link Sendere#onSendRequest} will be invoked when user ready to answer it
     * Otherwise, send request will be automatically processed as denied
     * @see Sendere#processSendRequest
     */
    public static boolean allowReceiving = true;
}
