package SendereCommons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Handler;

public abstract class RemoteUser {

    public static final int BUFFER_LENGTH = 1024*33;

    private String nickname;
    private long hash;
    private Socket socket;
    private int port;
    private boolean identified = false;
    private InputStream in;
    private OutputStream out;
    private boolean stopReceiving = false;

    public RemoteUser(String nickname, long hash, Socket socket) throws IOException {
        identify(nickname, hash);
        commonInitialization(socket);
    }

    public RemoteUser(Socket socket) throws IOException {
        commonInitialization(socket);
    }

    private void commonInitialization(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        doReceiving();
    }

    private void doReceiving() {
        Thread receiverThread = new Thread(() -> {
            while (!stopReceiving){
                byte[] buffer = new byte[BUFFER_LENGTH];
                int read;
                byte[] packetLength = new byte[2];
                try {
                    while (in.available()<3);
                    read = in.read(packetLength);
                    if(read<0)
                        throw new SocketException();
                    int length = (packetLength[0]<<8) + packetLength[1];
                    buffer[0] = (byte) in.read();
                    if(buffer[0]!="/".getBytes()[0])
                        continue;
                    while (in.available()<length);
                    read = in.read(buffer );
                    if(read<0)
                        throw new SocketException();
                    onReceive(buffer.clone(), length);
                } catch (SocketException e) {
                    destroy();
                    onDisconnect();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                //Okay
                //10.09.2019
                e.printStackTrace();
            }
        });
        receiverThread.start();
    }

    protected abstract void onDisconnect();

    public boolean sendMessage(byte[] data, int length){
        byte[] byteLength = new byte[]{(byte) ((length&0x0000FF00)>>8), (byte) (length&0x000000FF)};
        try {
            out.write(byteLength,0,byteLength.length);
            out.write(47);
            out.write(data, 0, length);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort() {
        return port;
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

    public void identify(String nickname, long hash){
        this.nickname = nickname;
        this.hash = hash;
        identified = true;
    }

    public boolean isIdentified(){
        return identified;
    }

    public abstract void onReceive(byte[] buffer, int length);

    public void destroy(){
        stopReceiving = true;
    }

    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }
}
