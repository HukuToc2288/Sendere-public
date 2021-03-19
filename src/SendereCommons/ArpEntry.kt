package SendereCommons

import java.util.*

public class ArpEntry{
    val address: ByteArray
    val status: Int
    var portForNextPing: Int = 0

    constructor(address: ByteArray, status: Int, startPort: Int) {
        this.status = status
        this.address = address
        portForNextPing = startPort
    }

    constructor(address: ByteArray, startPort: Int){
        this.address = address
        status = 0
        portForNextPing = startPort
    }

    public fun incrementPingPort(){
        portForNextPing++
    }

    override fun equals(other: Any?): Boolean {
        return address.contentEquals((other as ArpEntry).address)
    }

    override fun hashCode(): Int {
        return address.contentHashCode()
    }
}
