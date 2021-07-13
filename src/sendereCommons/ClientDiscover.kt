package sendereCommons

import sendereCommons.protopackets.DiscoveryPacket
import com.google.protobuf.ByteString
import java.io.IOException
import java.net.*
import java.util.*
import kotlin.collections.ArrayList

abstract class ClientDiscover(networkInterfaces: List<NetworkInterface>, private val port: Int) {

    private val multicastGroupAddress = "224.0.0.1"
    private lateinit var broadcastTimer: Timer
    private val broadcastSockets: ArrayList<DatagramSocket> = ArrayList()
    private var discoverySocket: MulticastSocket? = null

    init {
        for (networkInterface in networkInterfaces) {
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                if (inetAddress.isLinkLocalAddress || inetAddress.isLoopbackAddress)
                    continue
                broadcastSockets.add(DatagramSocket(0, inetAddress))
            }
        }
    }

    public fun start(){
        startDiscovery()
        startBroadcast()
    }

    private fun startDiscovery() {
        for (discoveryPort in Sendere.START_PORT..Sendere.END_PORT) {
            try {
                discoverySocket = MulticastSocket(discoveryPort)
                discoverySocket!!.joinGroup(InetAddress.getByName(multicastGroupAddress))
            } catch (e: Exception) {
            }
        }
        requireNotNull(discoverySocket)
        val discoveryBufferPacket = DatagramPacket(ByteArray(1024), 1024)
        Thread {
            while (true) {
                try {
                    discoverySocket!!.receive(discoveryBufferPacket)
                    val discoveryPacket = DiscoveryPacket.parseFrom(discoveryBufferPacket.data.copyOfRange(0,discoveryBufferPacket.length))
                    if (discoveryPacket.suid != Sendere.SUID)
                        onDiscoveryReceived(discoveryPacket.suid, InetAddress.getByAddress(discoveryPacket.address.toByteArray()), discoveryPacket.port)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun startBroadcast() {
        val discoveryPacketBuilder: DiscoveryPacket.Builder = DiscoveryPacket.newBuilder()
                .setSuid(Sendere.SUID)
                .setPort(port)
        broadcastTimer = Timer()
        broadcastTimer.schedule(object : TimerTask() {
            override fun run() {
                repeat(3) {

                    for (broadcastSocket in broadcastSockets) {
                        val discoveryMessage: ByteArray = discoveryPacketBuilder
                                .setAddress(ByteString.copyFrom(broadcastSocket.localAddress.address))
                                .build()
                                .toByteArray()
                        for (sendPort in Sendere.START_PORT..Sendere.END_PORT) {
                            val discoveryPacket = DatagramPacket(discoveryMessage, 0, discoveryMessage.size,
                                    InetAddress.getByName(multicastGroupAddress), sendPort)
                            try {
                                broadcastSocket.send(discoveryPacket)
                            } catch (e: IOException){
                                //e.printStackTrace()
                            }
                        }
                    }
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        stopBroadcasting()
                        return
                    }
                }
            }
        }, 0, 10000)
    }

    fun stopBroadcasting() {
        broadcastTimer.cancel()
    }

    abstract fun onDiscoveryReceived(remoteSUID: Long, address: InetAddress, port: Int)
}