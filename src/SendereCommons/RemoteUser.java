package SendereCommons;

import java.util.ArrayList;
import java.util.HashSet;

public class RemoteUser {
    public final String nickname;
    public final long hash;
    private HashSet<String> addresses;
    private int port;

    public RemoteUser(String nickname, long hash, String address, int port){
        this.nickname = nickname;
        this.hash = hash;
        addresses = new HashSet<String>();
        addAddress(address);
        setPort(port);
    }

    public HashSet<String> getAddresses() {
        return addresses;
    }

    public void addAddress(String address){
        addresses.add(address);
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
}
