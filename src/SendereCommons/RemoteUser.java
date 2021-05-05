package SendereCommons;

import com.google.protobuf.Any;
import lombok.Data;
import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
    @Getter
    private int authenticationStage = 0;
    private SimpleNetworkInterface networkInterface;
    private BlockingQueue<SendingQueueElement> sendingQueue = new ArrayBlockingQueue<SendingQueueElement>(10);
    private PublicKey rsaPublicKey;
    private long secret = 0;
    private byte encryptedSecret[];


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
        doSending();
        doAlive();
    }

    private void doSending() {
//        final Thread sendingThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        SendingQueueElement element = sendingQueue.take();
//                        int length = element.getLength();
//                        outputStream.write(new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)});
//                        outputStream.write(element.flags);
//                        if (length == 0)
//                            continue;
//                        for (byte[] dataPiece : element.getDatas())
//                            outputStream.write(dataPiece);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        onDisconnectInternal();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        sendingThread.start();
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
                    byte[] packetLength = new byte[4];
                    byte flags;
                    int length;
                    try {
//                        while (inputStream.available() < 4 && !disconnected) {
////                            if (System.currentTimeMillis() > lastAliveTime + 5000)
////                                RemoteUser.this.onDisconnectInternal();
//                            System.out.println("waiting...");
//                            Thread.sleep(2000);
//                        }
                        int headerRead = 0;
                        while (headerRead<4 && !disconnected){
                            headerRead+=inputStream.read(packetLength,headerRead,4-headerRead);
                        }
                        length = ((packetLength[0] + (packetLength[0] >= 0 ? 0 : 256)) << 16) + ((packetLength[1] + (packetLength[1] >= 0 ? 0 : 256)) << 8) + packetLength[2] + (packetLength[2] >= 0 ? 0 : 256);
                        //47 is '/' symbol's code
                        //18.09.2019
//                    if(packetLength[3]!=47)
//                        continue;
                       // flags = (byte) inputStream.read();
                        read = 0;
                        byte[] data = new byte[length];
                        while (read < length && !disconnected)
                            read += inputStream.read(data, read, length - read);
                        lastAliveTime = System.currentTimeMillis();
                        RemoteUser.this.onReceive(packetLength[3], data);

                    } catch (SocketException e) {
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

//        if (disconnected)
//            return false;
//        try {
//            // TODO: 11.04.2021 send flags: stub
//            sendingQueue.put(new SendingQueueElement(0, (byte) 0, null));
//            return true;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        sendMessage((byte) 0);
        return false;
    }

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
        //byte[] byteLength = new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)};
//        try {
//            //Don't pay attention on "Synchronization on a non-final field" warning
//            //output stream never changing even if javac can't understand it
//            //19.09.2019
//            sendingQueue.put(new SendingQueueElement(length, flags, data));
//            return true;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        synchronized (outputStream) {
            try {
                outputStream.write(new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)});
                outputStream.write(flags);
                if (length == 0)
                    return true;
                for (byte[] dataPiece : data)
                    outputStream.write(dataPiece);
            } catch (IOException e) {
                e.printStackTrace();
                onDisconnectInternal();
            }
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

    @Data
    private static class SendingQueueElement {
        private int length;
        private byte flags;
        private byte[][] datas;

        public SendingQueueElement(int length, byte flags, byte[][] datas) {
            this.length = length;
            this.flags = flags;
            this.datas = datas;
        }
    }

    public boolean doAuthenticationStage1(long secret, byte[] encryptedSecret, byte[] publicKeyBytes){
        try {
            rsaPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            this.secret = secret;
            long decryptedSecret = Sendere.decrypt(encryptedSecret, rsaPublicKey);
            if (decryptedSecret == secret){
                this.encryptedSecret = encryptedSecret;
                authenticationStage=1;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}