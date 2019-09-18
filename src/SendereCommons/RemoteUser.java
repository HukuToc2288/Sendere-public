package SendereCommons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Handler;

public abstract class RemoteUser {

    //It's almost 16MiB
    //18.09.2019
    public static final int BUFFER_LENGTH = 1024*1024*16-1;

    private String nickname;
    private long hash;
    private SocketChannel socket;
    private int port;
    private boolean identified = false;
/*    private InputStream in;
    private OutputStream out;*/
    private boolean stopReceiving = false;
    Selector selector;

    public RemoteUser(String nickname, long hash, SocketChannel socket) throws IOException {
        identify(nickname, hash);
        commonInitialization(socket);
    }

    public RemoteUser(SocketChannel socket) throws IOException {
        commonInitialization(socket);
    }

    private void commonInitialization(SocketChannel socket) throws IOException {
        this.socket = socket;
        selector = SelectorProvider.provider().openSelector();
        doReceiving();
    }

    private void doReceiving() {
        Thread receiverThread = new Thread(() -> {
            while (!stopReceiving){

                int read = 0;
                byte[] packetLength = new byte[4];
                int length = packetLength.length;
                ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
                try {

                    // wait for events
                    this.selector.select();

                    //work on selected keys
                    Iterator keys = this.selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = (SelectionKey) keys.next();

                        // this is necessary to prevent the same key from coming up
                        // again the next time around.
                        keys.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isReadable()){
                            while (read<length)
                                read+=socket.read(lengthBuffer);
                            length = ((packetLength[0] + (packetLength[0]>=0 ? 0 : 256))<<16) + ((packetLength[1] + (packetLength[1]>=0 ? 0 : 256))<<8) + packetLength[2] + (packetLength[2]>=0 ? 0 : 256);
                            //47 is '/' symbol's code
                            //18.09.2019
                            if(packetLength[3]!=47)
                                continue;
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(length);
                            read = 0;
                            while (read<length)
                            read+= channel.read(buffer);
                            onReceive(buffer.array(), length);
                        }
                    }
                } catch (SocketException e) {
                    destroy();
                    onDisconnect();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        });
        receiverThread.start();
    }

    private void read(SelectionKey key) throws IOException {


    }

    protected abstract void onDisconnect();

    public boolean sendMessage(byte[] data, int length){
        byte[] byteLength = new byte[]{(byte) ((length&0x00FF0000)>>16), (byte) ((length&0x0000FF00)>>8), (byte) (length&0x000000FF), 47};
        try {
            ByteBuffer buffer = ByteBuffer.allocate(length+4);
            buffer.put(byteLength);
            buffer.put(data);
            socket.write(buffer);
            return true;
        } catch (SocketException e) {
            destroy();
            onDisconnect();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return false;
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
        return socket.socket().getInetAddress().getHostAddress();
    }
}
