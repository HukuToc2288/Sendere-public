package SendereCommons;

import jdk.jfr.Enabled;

import java.util.Arrays;
import java.util.Objects;

public class Header {
    private byte[] content;
    private int minVersion = 0;
    private int maxVersion = Integer.MAX_VALUE;

    public Header(String content){
        this.content = content.getBytes();
    }

    public Header(String content, int minVersion){
        this.content = content.getBytes();
        this.minVersion = minVersion;
    }

    public Header(String content, int minVersion, int maxVersion){
        this.content = content.getBytes();
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    boolean isSupported(int version){
        return version>=minVersion && version <= maxVersion;
    }

    public Header(byte[] content){
        this.content = content;
    }

    public static boolean equals(Header header, String s) {
        return equals(header, s.getBytes());
    }

    public static boolean equals(Header header, byte[] b) {
        return Arrays.equals(b, header.content);
    }

    public boolean equals(Header header) {
        if (this == header) return true;
        return Arrays.equals(content, header.content);
    }

    public boolean equals(String string) {
        return Arrays.equals(content, string.getBytes());
    }

    public boolean equals(byte[] bytes) {
        return Arrays.equals(content, bytes);
    }


    //To avoid any issues compare Header only with String, byte[] or another Header or use provided static equals() methods
    // 29.06.2020
    @Override
    @Deprecated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        return Arrays.equals(content, header.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }

    @Override
    public String toString() {
        return new String(content);
    }

    public int length(){
        return content.length;
    }

    public byte[] getBytes() {
        return content;
    }
}
