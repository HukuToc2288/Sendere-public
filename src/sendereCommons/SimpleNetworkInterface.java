package sendereCommons;

import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Data
public class SimpleNetworkInterface {

    public final String stringAddress;
    public final byte[] byteAddress;
    public final int subnetPrefixLength;
    public final byte[] macAddress;
    public final String systemName;
    public final String displayName;
    private final NetworkInterface networkInterface;

    private Thread senderThread;
    private Thread receiverThread;

    private BlockingQueue<SenderQueueElement> senderQueue;

    private Object senderLock = new Object();
    private Object receiverLock = new Object();

    private RemoteUser currentActiveUser = null;


    public SimpleNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        systemName = networkInterface.getName();
        displayName = networkInterface.getDisplayName();
        byte[] tempMacAddress;
        try {
            tempMacAddress = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            tempMacAddress = new byte[]{0};
        }
        macAddress = tempMacAddress;
        String tempStringAddress = "null";
        byte[] tempByteAddress = new byte[]{0};
        int tempPrefixLength = 0;
        List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
        for (InterfaceAddress address : addresses) {
            if (address.getAddress().getHostAddress().contains(".")) {
                tempStringAddress = address.getAddress().getHostAddress();
                tempByteAddress = address.getAddress().getAddress();
                tempPrefixLength = address.getNetworkPrefixLength();
            }
        }
        subnetPrefixLength = tempPrefixLength;
        stringAddress = tempStringAddress;
        byteAddress = tempByteAddress;

        senderQueue = new ArrayBlockingQueue<>(10);

        senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    try {
                        SenderQueueElement element = senderQueue.take();
                        if (element.getUser().isDisconnected())
                            continue;
                        OutputStream outputStream = element.getUser().getOutputStream();
                        byte[][] dataToSend = element.getData();
                        int length = 0;
                        for (byte[] dataPiece : dataToSend) {
                            length += dataPiece.length;
                        }
                        byte[] byteLength = new byte[]{(byte) ((length & 0x00FF0000) >> 16), (byte) ((length & 0x0000FF00) >> 8), (byte) (length & 0x000000FF)};
                        outputStream.write(byteLength);
                        outputStream.write(element.getFlags());
                        for (byte[] dataPiece : dataToSend) {
                            outputStream.write(dataPiece);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
            }
        });
        senderThread.start();
    }

    public void sendPacket(RemoteUser userToSend, byte flags, byte[][] dataToSend) {
        try {
            senderQueue.put(new SenderQueueElement(userToSend, flags, dataToSend));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class SenderQueueElement {
        private RemoteUser user;
        private byte flags;
        private byte[][] data;

        public SenderQueueElement(RemoteUser userToSend, byte flags, byte[][] dataToSend) {
            user = userToSend;
            this.flags = flags;
            data = dataToSend;
        }
    }
}
