package sendereCommons

import sendereCommons.Settings.receivingDir
import lombok.Data
import java.lang.Runnable
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

@Data
abstract class TransmissionIn @JvmOverloads constructor(val user: RemoteUser, val id: Long, val rootDir: String = receivingDir) {
    @JvmField
    var realData: Long = 0
    var currentRelativePath: String? = null
    private val transmissionThread: Thread
    private val transmissionQueue: BlockingQueue<Runnable> = ArrayBlockingQueue(1)

    /**
     * Calls when sender requested to create directory
     * @param relativePath file path relative to root transmission's directory
     * @return `true` on success, if returned `false` operation should be terminated
     */
    abstract fun createDirectory(relativePath: String?): Boolean

    /**
     * Calls when sender requested to create file
     * @param relativePath file path relative to root transmission's directory
     * @return `true` on success, if returned `false` operation should be terminated
     */
    abstract fun createFile(relativePath: String?): Boolean

    /**
     * Calls when Sendere received raw data that should be written to opened file
     * @param data bytes to writing
     * @param len count of bytes from array that should be written
     * @return `true` on success, if returned `false` operation should be terminated
     */
    abstract fun writeToFile(data: ByteArray?, off: Int, len: Int): Boolean

    /**
     * Calls when need to close file that was opened early by [.createFile]
     * @see .createFile
     */
    abstract fun closeFile(): Boolean

    /**
     * Calls when remote user notify about total size of files will be send by this transmission
     * Note that actual progress not updating by Sendere, so it should be calculated by external stuff
     * @param size total transaction size in bytes
     */
    abstract fun onUpdateTransmissionSize(size: Long)
    abstract fun onDone()
    fun writeToFile(data: ByteArray) {
        writeToFile(data, 0, data.size)
    }

    fun postToTransmissionThread(runnable: Runnable) {
        try {
            transmissionQueue.put(runnable)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Creates transmission with all methods empty
         * This method using when we don't actually want to receive something, but have to create transmission to deny it
         * and there is no need to implement any methods
         * @param user user who want to send files
         * @param id unique id by which transmission can be determined
         * @return transmission with empty methods
         */
        @JvmStatic
        fun createDummyTransmission(user: RemoteUser, id: Long): TransmissionIn {
            return object : TransmissionIn(user, id) {
                override fun createDirectory(relativePath: String?): Boolean {
                    return false
                }

                override fun createFile(relativePath: String?): Boolean {
                    return false
                }

                override fun writeToFile(data: ByteArray?, off: Int, len: Int): Boolean {
                    return false
                }

                override fun closeFile(): Boolean {
                    return false
                }

                override fun onUpdateTransmissionSize(size: Long) {}
                override fun onDone() {}
            }
        }
    }

    init {
        transmissionThread = Thread(Runnable {
            try {
                while (true) {
                    transmissionQueue.take().run()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return@Runnable
            }
        })
        transmissionThread.start()
    }
}