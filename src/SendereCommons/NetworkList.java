package SendereCommons;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;

public class NetworkList {

    private static SimpleNetworkInterface[] networkList;

    public static void updateList() {
        ArrayList<NetworkInterface> nets = null;
        try {
            nets = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            //If there are absolutely no nets (even loopback)
            nets = new ArrayList<>();
        }
        ArrayList<SimpleNetworkInterface> networkInterfaceArray = new ArrayList<SimpleNetworkInterface>();
        for (int i = 0; i<nets.size(); i++){
            ArrayList<InetAddress> inetAddresses = Collections.list(nets.get(i).getInetAddresses());
            if(inetAddresses.size()==0 || !hasIp4address(inetAddresses))
                continue;
            networkInterfaceArray.add(new SimpleNetworkInterface(nets.get(i)));
        }
        networkList = networkInterfaceArray.toArray(new SimpleNetworkInterface[0]);
    }

    private static boolean hasIp4address(ArrayList<InetAddress> inetAddresses) {
        for (InetAddress inetAddress : inetAddresses) {
            if (inetAddress.getHostAddress().contains(".") && !inetAddress.getHostAddress().equals("127.0.0.1") && !inetAddress.getHostAddress().equals("/0.0.0.0"))
                return true;
        }
        return false;
    }

    public static SimpleNetworkInterface[] getNetworkList(){
        return networkList;
    }
}