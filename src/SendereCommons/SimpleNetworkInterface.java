package SendereCommons;

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

    }
}
