package SendereCommons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public abstract class RemoteUser {

    private String nickname;
    private long hash;
    private Socket socket;
    private int port;
    private boolean identified = false;
    private InputStream in;
    private OutputStream out;
    private boolean disconnected = false;
    private long lastAliveTime;

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
                if(!identified)
                    onDisconnectInternal();
            }
        },5000);
    }

    public int getPort(){
        return socket.getPort();
    }

    private void commonInitialization(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        lastAliveTime = System.currentTimeMillis();
        doReceiving();
        doAlive();
    }

    private void doAlive() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(Headers.IM_ALIVE.getBytes(), Headers.IM_ALIVE.getBytes().length);
            }
        },0, 2000);
    }

    private void doReceiving() {
        Thread receiverThread = new Thread(() -> {
            while (!disconnected){
                int read;
                byte[] packetLength = new byte[4];
                int length;
                try {
                    while (in.available()<4&&!disconnected){
                            Thread.sleep(500);
                    };
                    in.read(packetLength);
                    length = ((packetLength[0] + (packetLength[0]>=0 ? 0 : 256))<<16) + ((packetLength[1] + (packetLength[1]>=0 ? 0 : 256))<<8) + packetLength[2] + (packetLength[2]>=0 ? 0 : 256);
                    //47 is '/' symbol's code
                    //18.09.2019
                    if(packetLength[3]!=47)
                        continue;
                    read = 0;
                    byte[] buffer = new byte[length];
                    while (read<length&&!disconnected)
                        read+=in.read(buffer, read, length-read);
                    lastAliveTime = System.currentTimeMillis();
                    if(!Arrays.equals(buffer, Headers.IM_ALIVE.getBytes())) {
                        onReceive(buffer, length);
                    }
                    if(System.currentTimeMillis() > lastAliveTime+5000)
                        onDisconnectInternal();
                } catch (SocketException | InterruptedException e) {
                    onDisconnectInternal();
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



    private void onDisconnectInternal(){
        if(disconnected)
            return;
        disconnected = true;
        if(isIdentified())
            onDisconnect();
    }

    protected abstract void onDisconnect();

    boolean sendMessage(byte[] data, int length){
        if (disconnected)
            return false;
        byte[] byteLength = new byte[]{(byte) ((length&0x00FF0000)>>16), (byte) ((length&0x0000FF00)>>8), (byte) (length&0x000000FF), 47};
        try {
            //Don't pay attention on "Synchronization on a non-final field" warning
            //output stream never changing even if javac can't understand it
            //19.09.2019
            synchronized (out) {
                out.write(byteLength);
                out.write(data, 0, length);
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

    void identify(String nickname, long hash){
        this.nickname = nickname;
        this.hash = hash;
        identified = true;
    }

    private boolean isIdentified(){
        return identified;
    }

    public abstract void onReceive(byte[] buffer, int length);

    public String getAddress() {
        return socket.getInetAddress().getHostAddress();
    }
}