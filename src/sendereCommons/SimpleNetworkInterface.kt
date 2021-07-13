package sendereCommons

import lombok.Data
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.Runnable
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

@Data
class SimpleNetworkInterface(private val networkInterface: NetworkInterface) {
    val stringAddress: String
    val byteAddress: ByteArray
    val subnetPrefixLength: Int
    val macAddress: ByteArray
    val systemName: String
    val displayName: String

    init {
        systemName = networkInterface.name
        displayName = networkInterface.displayName
        macAddress = try {
            networkInterface.hardwareAddress
        } catch (e: Exception){
            byteArrayOf(0)
        }
        var tempStringAddress = "null"
        var tempByteAddress = byteArrayOf(0)
        var tempPrefixLength = 0
        val addresses = networkInterface.interfaceAddresses
        for (address in addresses) {
            if (address.address.hostAddress.contains(".")) {
                tempStringAddress = address.address.hostAddress
                tempByteAddress = address.address.address
                tempPrefixLength = address.networkPrefixLength.toInt()
            }
        }
        subnetPrefixLength = tempPrefixLength
        stringAddress = tempStringAddress
        byteAddress = tempByteAddress
    }
}