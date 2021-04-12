package SendereCommons;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public abstract class RemoteUser {

    private String nickname;
    private long hash;
    private SocketChannel socketChannel;
    private int port;
    private boolean identified = false;
    @Getter
    private boolean disconnected = false;
    private long lastAliveTime;
    private SimpleNetworkInterface networkInterface;

    private final Object lock = new Object();

    public RemoteUser(String nickname, long hash, SocketChannel socketChannel) throws IOException {
        identify(nickname, hash);
        commonInitialization(socketChannel);
    }

    public RemoteUser(SocketChannel socketChannel) throws IOException {
        commonInitialization(socketChannel);
        Timer identifyTimer = new Timer();
        identifyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!identified)
                    onDisconnectInternal();
            }
        }, 5000);
    }

    public int getPort() {
        return socketChannel.socket().getPort();
    }

    private void commonInitialization(SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        lastAliveTime = System.currentTimeMillis();
    }


    private void onDisconnectInternal() {
        if (disconnected)
            return;
        disconnected = true;
        if (isIdentified())
            onDisconnect();
    }

    protected abstract void onDisconnect();

    @Override
    public String toString() {
        return nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public long getHash() {
        return hash;
    }

    void identify(String nickname, long hash) {
        this.nickname = nickname;
        this.hash = hash;
        identified = true;
    }

    private boolean isIdentified() {
        return identified;
    }

    public abstract void onReceive(byte flags, byte[] data);

    /**
     * @param header
     * @param data
     * @param length
     * @return
     * @deprecated go use partial send
     */
    @Deprecated
    boolean sendMessage(byte header, byte[] data, int length) {
        return sendMessage((byte) 0, new byte[]{header}, data);
    }

    public boolean sendMessage(byte flags, byte[]... data) {
        if (disconnected)
            return false;
        int length = 0;
        for (byte[] dataPiece : data) {
            length += dataPiece.length;
        }
        byte[] byteLength = new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)};
        try {
            //Don't pay attention on "Synchronization on a non-final field" warning
            //output stream never changing even if javac can't understand it
            //19.09.2019

                socketChannel.write(ByteBuffer.wrap(byteLength));
                socketChannel.write(ByteBuffer.wrap(new byte[]{flags}));
                for (byte[] dataPiece : data) {
                    socketChannel.write(ByteBuffer.wrap(dataPiece));
                }
            return true;
        } catch (SocketException e) {
            //e.printStackTrace();
            onDisconnectInternal();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteUser that = (RemoteUser) o;
        return hash == that.hash && port == that.port && identified == that.identified && Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, hash);
    }
}