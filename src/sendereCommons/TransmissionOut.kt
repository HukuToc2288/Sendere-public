package sendereCommons

import sendereCommons.Settings.nickname
import lombok.Data
import java.io.File
import java.util.concurrent.ThreadLocalRandom

@Data
abstract class TransmissionOut(val user: RemoteUser, isDirectory: Boolean, path: String?) {
    @JvmField
    val rooDirectory: String
    @JvmField
    val filename: String
    @JvmField
    val id: Long
    val isDirectory: Boolean
    private val pauseLock = Any()
    @JvmField
    protected var stop = false
    abstract fun start()
    abstract fun onFail()
    abstract fun onSuccess()
    fun stop() {
        stop = true
    }

    init {
        val file = File(path)
        rooDirectory = file.parent
        filename = file.name
        this.isDirectory = isDirectory
        id = (nickname.hashCode() xor user.nickname.hashCode() xor ThreadLocalRandom.current().nextLong().toInt()).toLong()
    }
}