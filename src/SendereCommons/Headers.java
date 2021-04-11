package SendereCommons;

public class Headers {

    // Start for headers supported in all Sendere versions

    public static final byte PING = 17;
    public static final byte PONG = 18;
    public static final byte TEXT = 19;
    public static final byte MKDIR = 20;
    public static final byte MKFILE = 21;
    public static final byte IM_ALIVE = 22;
    public static final byte RAW_DATA = 23;
    public static final byte GZIP_DATA = 24;
    public static final byte CLOSE_FILE = 25;
    public static final byte SEND_REQUEST = 26;
    public static final byte SEND_RESPONSE = 27;
    public static final byte SEND_COMPLETE = 28;
    public static final byte SPEED_MEASURE = 29;

   // public static final Header DEVICE_DISCOVERY = new Header("SENDERE: k7o 8 ce7u?");

    // End for headers supported in all Sendere versions
}
