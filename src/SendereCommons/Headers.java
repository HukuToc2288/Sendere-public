package SendereCommons;

public class Headers {

    // Start for headers supported in all Sendere versions

    public static final Header PING = new Header("PING");
    public static final Header PONG = new Header("PONG");
    public static final Header TEXT = new Header("TEXT");
    public static final Header TRUE = new Header("TRUE");
    public static final Header FALSE = new Header("FALS");
    public static final Header MKDIR = new Header("MKDR");
    public static final Header MKFILE = new Header("MKFL");
    public static final Header IM_ALIVE = new Header("IALV");
    public static final Header RAW_DATA = new Header("RAWD");
    public static final Header GZIP_DATA = new Header("RAWZ");
    public static final Header CLOSE_FILE = new Header("CLSF");
    public static final Header SEND_REQUEST = new Header("SDRT");
    public static final Header SEND_RESPONSE = new Header("SDRE");
    public static final Header SEND_COMPLETE = new Header("SDCT");
    public static final Header SPEED_MEASURE = new Header("SPDM");

    // End for headers supported in all Sendere versions
}
