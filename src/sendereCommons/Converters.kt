package sendereCommons

object Converters {
    fun longToBytes(l: Long): ByteArray {
        var l = l
        val result = ByteArray(8)
        for (i in 7 downTo 0) {
            result[i] = (l and 0xFF).toByte()
            l = l shr 8
        }
        return result
    }

    @JvmOverloads
    fun bytesToLong(b: ByteArray, offset: Int = 0): Long {
        var result: Long = 0
        for (i in offset until 8 + offset) {
            result = result shl 8
            result = result or (b[i].toLong() and 0xFF)
        }
        return result
    }
}