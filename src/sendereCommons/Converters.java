package sendereCommons;

public class Converters {

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(final byte[] b) {
        return bytesToLong(b,0);
    }

    public static long bytesToLong(final byte[] b, int offset) {
        long result = 0;
        for (int i = offset; i < 8+offset; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}
