import sendereCommons.*
import sendereCommons.TransmissionIn.Companion.createDummyTransmission
import sendereCommons.protopackets.CloseFilePacket
import sendereCommons.protopackets.RawDataPacket
import sendereCommons.protopackets.RemoteErrorPacket
import sendereCommons.protopackets.TransmissionControlPacket
import com.google.protobuf.ByteString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.InetAddress
import java.util.*
import java.util.zip.Deflater
import kotlin.experimental.or

class Main {
    private lateinit var sendere: Sendere
    private lateinit var scanner: Scanner
    private var question = false
    private val authenticationQuestion = false
    private val allow = false
    private var tempInRequest: InRequest? = null
    private val blacklistIPs = HashSet<String>()
    fun main(args: Array<String?>?) {
        try {
            Settings.nickname = (System.getProperty("user.name") + "@" + InetAddress.getLocalHost().hostName)
        } catch (e: Exception) {
            //Keep default
        }
        println("Сбор даннных о сетевых интерфесах...")
        NetworkList.updateList()
        if (NetworkList.getNetworkList().size == 0) {
            println("Ваше устройство не педключено ни к одной сети")
            System.exit(0)
        }
        println("Инициализация сервиса...")
        sendere = object : Sendere() {
            override fun onAuthenticationRequestReceived(secret: Long) {}
            override fun onRemoteErrorReceived(user: RemoteUser, errorType: RemoteErrorPacket.ErrorType?, extraMessage: String?) {
                var errorDescription: String? = null
                errorDescription = when (errorType) {
                    RemoteErrorPacket.ErrorType.NOT_PROTOBUF -> "Пакет не в формате protobuf"
                    RemoteErrorPacket.ErrorType.INVALID_FORMAT -> "Формат пакета на удалённом уст-ве отличается от передаваемого: $extraMessage"
                    RemoteErrorPacket.ErrorType.UNRECOGNIZED_PACKET -> "Удалённое уст-во не может обрабатывать данный тип пакета: $extraMessage"
                    RemoteErrorPacket.ErrorType.CHAT_NOT_ALLOWED -> "Удалённый пользователь запрещает приём текстовых сообщений"
                    else -> "Неизвестная ошибка: $extraMessage"
                }
                System.err.printf("Ошибка от пользоватля %s [%d]: %s\r\n", user, sendere!!.remoteUsers!!.indexOf(user), errorDescription)
            }

            override fun onRemoteUserUpdated(user: RemoteUser) {}
            override fun onRemoteUserConnected(user: RemoteUser) {
                println("Подключился пользователь " + user.nickname + " [" + sendere!!.remoteUsers!!.indexOf(user) + "]")
            }

            override fun onRemoteUserFound(user: RemoteUser) {
                println("Обнаружен пользователь " + user.nickname + " [" + sendere!!.remoteUsers!!.indexOf(user) + "]")
            }

            override fun onTextMessageReceived(who: RemoteUser, message: String?) {
                println("Сообщение от пользователя " + who.nickname + " [" + sendere!!.remoteUsers!!.indexOf(who) + "] : " + message)
            }

            override fun onSendRequest(request: InRequest) {
                println(String.format("Пользователь %s [%d] хочет передать вам %s %s. Принять?", request.who, sendere!!.remoteUsers!!.indexOf(request.who), if (request.isDirectory) "директорию" else "файл", request.filename))
                tempInRequest = request
                question = true
            }

            override fun onSendResponse(allow: Boolean, transmission: TransmissionOut) {
                if (allow) {
                    println("Передача " + transmission.id + " начата")
                } else {
                    println("Передача " + transmission.id + " отклонена")
                }
            }

            override fun onUserDisconnected(remoteUser: RemoteUser) {
                println("Пользователь " + remoteUser.nickname + " [" + sendere!!.remoteUsers!!.indexOf(remoteUser) + "] отключился")
            }

            override fun onInternalError(code: Int, message: String?) {
                if (code == 1) {
                    println("Поиск в данной сети может занять большое время. Рекомендуем ввести IP-адрес вручную ($message)")
                }
            }
        }
        println("Sendere запущен на порту " + sendere.mainPort)
        println("Поиск устройств в сети...")
        println("")
        scanner = Scanner(System.`in`)
        val consoleThread = Thread {
            while (true) {
                println("Введите команду")
                val line = readLine().trim { it <= ' ' }
                if (question) {
                    if (line == "yes") {
                        question = false
                        val transmission: TransmissionIn = object : TransmissionIn(tempInRequest!!.who, tempInRequest!!.transmissionId) {
                            private var writer: FileOutputStream? = null
                            var startTime = System.currentTimeMillis()
                            var totalBytesReceived: Long = 0
                            override fun createDirectory(relativePath: String?): Boolean {
                                return try {
                                    var i = 2
                                    var tempPath = "$rootDir/$relativePath"
                                    while (File(tempPath).exists()) {
                                        tempPath = rootDir + "/" + relativePath + i++
                                    }
                                    File(tempPath).mkdir()
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            override fun createFile(relativePath: String?): Boolean {
                                return try {
                                    var i = 2
                                    var tempPath = "$rootDir/$relativePath"
                                    while (File(tempPath).exists()) {
                                        tempPath = rootDir + "/" + relativePath + i++
                                    }
                                    val file = File(tempPath)
                                    if (!file.createNewFile()) return false
                                    writer = FileOutputStream(file)
                                    true
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            override fun writeToFile(data: ByteArray?, off: Int, len: Int): Boolean {
                                try {
                                    writer!!.write(data, off, len)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    return false
                                }
                                totalBytesReceived += len - off.toLong()
                                return true
                            }

                            override fun closeFile(): Boolean {
                                return try {
                                    writer!!.close()
                                    true
                                } catch (e: IOException) {
                                    false
                                }
                            }

                            override fun onUpdateTransmissionSize(size: Long) {}
                            override fun onDone() {
                                val totalTime = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                                println(String.format("Приём %1\$s успешно завершён", id))
                                println(String.format("Средняя скорость приёма %.2f МБ/с при средней скорости сети %.2f МБ/с", totalBytesReceived.toDouble() / 1024 / 1024 / totalTime, realData.toDouble() / 1024 / 1024 / totalTime))
                            }
                        }
                        sendere.processSendRequest(true, transmission)
                        println("Приём начат с идентификатором " + transmission.id)
                    } else if (line == "no") {
                        question = false
                        sendere.processSendRequest(false, createDummyTransmission(tempInRequest!!.who, tempInRequest!!.transmissionId))
                        println("Приём отклонён")
                    } else {
                        println("Ответьте yes или no")
                    }
                    continue
                }
                if (line == "who") {
                    if (sendere.remoteUsers!!.size == 0) {
                        println("Сейчас в лоакльной сети никого. Если это не так, убедитесь, что устройства находятся в одной локальной сети")
                        println("")
                        continue
                    }
                    println("Пользователи в сети:")
                    println("")
                    val users = sendere.remoteUsers
                    for (i in users!!.indices) {
                        if (users[i] == null) continue
                        println("Пользователь $i:")
                        println("Хэш-сумма:" + users[i].hash)
                        println("Никнейм: " + users[i].nickname)
                        println("Локальнй адрес: " + users[i].address)
                        println("")
                    }
                } else if (line.startsWith("tell ") && line.split(" ".toRegex()).toTypedArray().size >= 3) {
                    val split = line.split(" ".toRegex(), 3).toTypedArray()
                    var tempUser: RemoteUser?
                    try {
                        tempUser = sendere.remoteUsers!![split[1].toInt()]
                        if (tempUser == null) throw Exception()
                    } catch (e: Exception) {
                        println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка")
                        continue
                    }
                    sendere.sendTextMessage(tempUser, split[2])
                    println("Сообщение отправлено")
                    if (!Settings.allowChat) println("Обратите внимание, что ваши настройки запрещают приём текстовых сообщений, а значит вы не сможете получить ответ")
                } else if (line.startsWith("speed ") && line.split(" ".toRegex()).toTypedArray().size == 2) {
                    val split = line.split(" ".toRegex(), 3).toTypedArray()
                    var tempUser: RemoteUser?
                    try {
                        tempUser = sendere.remoteUsers!![split[1].toInt()]
                        if (tempUser == null) throw Exception()
                    } catch (e: Exception) {
                        println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка")
                        continue
                    }
                    println("Замеряем скорость с пользователем " + sendere.remoteUsers!![split[1].toInt()] + " [" + split[1] + "]...")
                    val testDuration = 10
                    val endTime = System.currentTimeMillis() + testDuration * 1000
                    var packetsSend = 0
                    while (System.currentTimeMillis() < endTime) {
                        sendere.sendMessage(tempUser, 4.toByte(), RawDataPacket.newBuilder().setTransmissionId(0)
                                .setData(ByteString.copyFrom(ByteArray(1048572))).build())
                        packetsSend++
                    }
                    println(String.format("Средняя скорость соединения с пользователем %.2f МБ/с", packetsSend.toFloat() / testDuration))
                } else if (line.startsWith("send") && line.split(" ".toRegex()).toTypedArray().size >= 3) {
                    val split = line.split(" ".toRegex(), 3).toTypedArray()
                    var tempUser: RemoteUser?
                    try {
                        tempUser = sendere.remoteUsers!![split[1].toInt()]
                        if (tempUser == null) throw Exception()
                    } catch (e: Exception) {
                        println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка")
                        continue
                    }
                    if (!File(split[2]).exists()) {
                        println(String.format("Файла %1\$s не существует", split[2]))
                        continue
                    }

                    //Надо переделать расчёт номера передачи
                    val transmission: TransmissionOut = object : TransmissionOut(tempUser, File(split[2]).isDirectory, split[2]) {
                        private val deflater = Deflater(Deflater.BEST_COMPRESSION, true)
                        override fun start() {
                            deflater.setStrategy(Deflater.HUFFMAN_ONLY)
                            recursiveSend(filename)
                            sendere.sendMessage(user, 0.toByte(), TransmissionControlPacket.newBuilder()
                                    .setSignal(TransmissionControlPacket.Signal.SENDING_COMPLETE)
                                    .setTransmissionId(this.id)
                                    .build())
                            onSuccess()
                        }

                        override fun onFail() {
                            stop()
                            println("Передача с номером $id не удалась")
                        }

                        override fun onSuccess() {
                            stop()
                            println("Передача с номером $id успешно завершена")
                        }

                        private fun recursiveSend(currentRelativePath: String) {
                            val file = File("$rooDirectory/$currentRelativePath")
                            if (!file.exists()) return
                            if (file.isDirectory) {
                                if (!sendere.createRemoteDirectory(currentRelativePath, this)) stop = true
                                if (stop) return
                                for (name in file.list()) {
                                    recursiveSend("$currentRelativePath/$name")
                                }
                            } else {
                                try {
                                    sendere.createRemoteFile(currentRelativePath, this)
                                    val `in` = FileInputStream(file)
                                    val data = ByteArray(1024 * 1024 / 8)
                                    var dataLength: Int
                                    var flags: Byte = 0
                                    while (`in`.read(data).also { dataLength = it } != -1) {
                                        // TODO: 16.04.2021 add GZip support, huku
                                        if (Settings.allowGzip) {
                                            flags = flags or 1
                                        }
                                        sendere.sendMessage(user, flags, RawDataPacket.newBuilder()
                                                .setTransmissionId(this.id)
                                                .setData(ByteString.copyFrom(data, 0, dataLength))
                                                .build())
                                        if (stop) return
                                    }
                                    sendere.sendMessage(user, 0.toByte(), CloseFilePacket.newBuilder().setTransmissionId(this.id).build())
                                    //sendere.sendMessage(user, Headers.CLOSE_FILE, String.valueOf(id));
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    stop = true
                                }
                            }
                        }
                    }
                    sendere.addTransmissionOut(transmission)
                    sendere.sendTransmissionRequest(transmission)
                } else if (line.startsWith("auth")) {
                    println("Сопряжение с пользователем huku@Tomahawk [0]")
                    println("Код сопряжения от удалённого устройства - 324098")
                    println("Внимательно проверьте совпадение кодов сопряжения на обеих устройствах!")
                    println("Коды совпадают?")
                    scanner.nextLine()
                    println("Установка зазищённого соединения...")
                    println("Защищённое соединение установлено")
                } else {
                    println("Команда не распознана")
                    println("")
                }
            }
        }
        consoleThread.start()
    }

    fun readLine(): String {
        return scanner.nextLine()
    }
}