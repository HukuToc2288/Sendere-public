package SendereCommons;

import com.google.protobuf.Any;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public abstract class RemoteUser {

    private String nickname;
    private long hash;
    private Socket socket;
    private int port;
    private boolean identified = false;
    @Getter
    private InputStream inputStream;
    @Getter
    private OutputStream outputStream;
    @Getter
    private boolean disconnected = false;
    private long lastAliveTime;
    private SimpleNetworkInterface networkInterface;

    public RemoteUser(String nickname, long hash, Socket socket) throws IOException {
        identify(nickname, hash);
        commonInitialization(socket);
    }

    public RemoteUser(Socket socket) throws IOException {
        commonInitialization(socket);
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
        return socket.getPort();
    }

    private void commonInitialization(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        lastAliveTime = System.currentTimeMillis();
        doReceiving();
        doAlive();
    }

    private void doAlive() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendAlive();
            }
        }, 0, 2000);
    }

    private void doReceiving() {
        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!disconnected) {
                    int read;
                    byte[] packetLength = new byte[3];
                    byte flags;
                    int length;
                    try {
                        while (inputStream.available() < 4 && !disconnected) {
                            if (System.currentTimeMillis() > lastAliveTime + 5000)
                                RemoteUser.this.onDisconnectInternal();
                            Thread.sleep(500);
                        }
                        inputStream.read(packetLength);
                        length = ((packetLength[0] + (packetLength[0] >= 0 ? 0 : 256)) << 16) + ((packetLength[1] + (packetLength[1] >= 0 ? 0 : 256)) << 8) + packetLength[2] + (packetLength[2] >= 0 ? 0 : 256);
                        //47 is '/' symbol's code
                        //18.09.2019
//                    if(packetLength[3]!=47)
//                        continue;
                        flags = (byte) inputStream.read();
                        read = 0;
                        byte[] data = new byte[length];
                        while (read < length && !disconnected)
                            read += inputStream.read(data, read, length - read);
                        lastAliveTime = System.currentTimeMillis();
                        RemoteUser.this.onReceive(flags, data);

                    } catch (SocketException | InterruptedException e) {
                        RemoteUser.this.onDisconnectInternal();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    //Okay
                    //10.09.2019
                    e.printStackTrace();
                }
            }
        });
        receiverThread.start();
    }


    private void onDisconnectInternal() {
        if (disconnected)
            return;
        disconnected = true;
        if (isIdentified())
            onDisconnect();
    }

    protected abstract void onDisconnect();

    boolean sendAlive() {
        // Send headless packet to keep connection
        // 11.04.2021 huku
        synchronized (outputStream) {
            if (disconnected)
                return false;
            try {
                synchronized (outputStream) {
                    // TODO: 11.04.2021 send flags: stub
                    outputStream.write(0);
                    outputStream.write(0);
                    outputStream.write(0);
                    outputStream.write(0);
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
    }

    /**
     * @deprecated
     * go use partial send
     *
     * @param header
     * @param data
     * @param length
     * @return
     */
    @Deprecated
    boolean sendMessage(byte header, byte[] data, int length) {
        return sendMessage((byte) 0, new byte[]{header}, data);
    }


    public boolean sendMessage(byte flags, byte[]... data) {
        if (disconnected)
            return false;
        int length = 0;
        for (byte[] dataPiece: data){
            length+=dataPiece.length;
        }
        byte[] byteLength = new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)};
        try {
            //Don't pay attention on "Synchronization on a non-final field" warning
            //output stream never changing even if javac can't understand it
            //19.09.2019
            synchronized (outputStream) {
                outputStream.write(byteLength);
                outputStream.write(flags);
                for (byte[] dataPiece: data){
                    outputStream.write(dataPiece);
                }
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

    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteUser that = (RemoteUser) o;
        return hash == that.hash && port == that.port && identified == that.identified && Objects.equals(nickname, that.nickname) && Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, hash);
    }
}