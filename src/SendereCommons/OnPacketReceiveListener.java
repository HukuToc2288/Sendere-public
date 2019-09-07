package SendereCommons;

import java.net.DatagramPacket;

public interface OnPacketReceiveListener {
    void onReceive(DatagramPacket packet);
}
