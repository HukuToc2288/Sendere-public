package SendereCommons;

import lombok.Data;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

@Data
public class SimpleNetworkInterface {

    public final String stringAddress;
    public final byte[] byteAddress;
    public final int subnetPreffixLength;
    public final byte[] macAddress;
    public final String systemName;
    public final String displayName;
    private final NetworkInterface networkInterface;

    public SimpleNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        systemName = networkInterface.getName();
        displayName = networkInterface.getDisplayName();
        byte[] tempMacAddress;
        try {
            tempMacAddress = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            tempMacAddress =  new byte[]{0};
        }
        macAddress = tempMacAddress;
        String tempStringAddress = "null";
        byte[] tempByteAddress = new byte[]{0};
        byte[] tempMask = new byte[]{0};
        int tempPrefixLength = 0;
        List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
        for (InterfaceAddress address: addresses) {
            if (address.getAddress().getHostAddress().contains(".")) {
                tempStringAddress = address.getAddress().getHostAddress();
                tempByteAddress = address.getAddress().getAddress();
                tempPrefixLength = address.getNetworkPrefixLength();
            }
        }
        subnetPreffixLength = tempPrefixLength;
        stringAddress = tempStringAddress;
        byteAddress = tempByteAddress;
    }
}
