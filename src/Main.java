import SendereCommons.*;
import SendereCommons.protopackets.CloseFilePacket;
import SendereCommons.protopackets.RawDataPacket;
import SendereCommons.protopackets.RemoteErrorPacket;
import SendereCommons.protopackets.TransmissionControlPacket;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.Deflater;

public class Main {

    private Sendere sendere;
    private Scanner scanner;
    private boolean question = false;
    private boolean authenticationQuestion = false;
    private boolean allow = false;
    private InRequest tempInRequest;
    private HashSet<String> blacklistIPs = new HashSet<String>();

    public void main(String[] args) {
        try {
            Settings.setNickname(System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            //Keep default
        }
        println("Сбор даннных о сетевых интерфесах...");
        NetworkList.updateList();
        if (NetworkList.getNetworkList().length == 0) {
            println("Ваше устройство не педключено ни к одной сети");
            System.exit(0);
        }
        println("Инициализация сервиса...");
        sendere = new Sendere() {

            @Override
            protected void onAuthenticationRequestReceived(long secret) {

            }

            @Override
            protected void onRemoteErrorReceived(RemoteUser user, RemoteErrorPacket.ErrorType errorType, String extraMessage) {
                String errorDescription = null;
                switch (errorType) {
                    case NOT_PROTOBUF:
                        errorDescription = "Пакет не в формате protobuf";
                        break;
                    case INVALID_FORMAT:
                        errorDescription = "Формат пакета на удалённом уст-ве отличается от передаваемого: " + extraMessage;
                        break;
                    case UNRECOGNIZED_PACKET:
                        errorDescription = "Удалённое уст-во не может обрабатывать данный тип пакета: " + extraMessage;
                        break;
                    case CHAT_NOT_ALLOWED:
                        errorDescription = "Удалённый пользователь запрещает приём текстовых сообщений";
                        break;
                    default:
                        errorDescription = "Неизвестная ошибка: " + extraMessage;
                        break;
                }
                System.err.printf("Ошибка от пользоватля %s [%d]: %s\r\n", user, sendere.getRemoteUsers().indexOf(user), errorDescription);
            }

            @Override
            public void onRemoteUserUpdated(RemoteUser user) {

            }

            @Override
            public void onRemoteUserConnected(RemoteUser user) {
                println("Подключился пользователь " + user.getNickname() + " [" + sendere.getRemoteUsers().indexOf(user) + "]");
            }

            @Override
            public void onRemoteUserFound(RemoteUser user) {
                println("Обнаружен пользователь " + user.getNickname() + " [" + sendere.getRemoteUsers().indexOf(user) + "]");
            }

            @Override
            public void onTextMessageReceived(RemoteUser who, String message) {
                println("Сообщение от пользователя " + who.getNickname() + " [" + sendere.getRemoteUsers().indexOf(who) + "] : " + message);
            }

            @Override
            public void onSendRequest(InRequest request) {
                println(String.format("Пользователь %s [%d] хочет передать вам %s %s. Принять?", request.who, sendere.getRemoteUsers().indexOf(request.who), request.isDirectory ? "директорию" : "файл", request.filename));
                tempInRequest = request;
                question = true;
            }

            @Override
            public void onSendResponse(boolean allow, TransmissionOut transmission) {
                if (allow) {
                    println("Передача " + transmission.id + " начата");
                } else {
                    println("Передача " + transmission.id + " отклонена");
                }
            }

            @Override
            public void onUserDisconnected(RemoteUser remoteUser) {
                println("Пользователь " + remoteUser.getNickname() + " [" + sendere.getRemoteUsers().indexOf(remoteUser) + "] отключился");
            }

            @Override
            protected void onInternalError(int code, String message) {
                if (code == 1) {
                    println("Поиск в данной сети может занять большое время. Рекомендуем ввести IP-адрес вручную (" + message + ")");
                }
            }
        };
        println("Sendere запущен на порту " + sendere.getMainPort());
        println("Поиск устройств в сети...");
        sendere.updateRemoteUsersList();
        println("");
        scanner = new Scanner(System.in);
        Thread consoleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    println("Введите команду. Для справки введите \"?\"");
                    String line = readLine().trim();
                    if (question) {
                        if (line.equals("yes")) {
                            question = false;
                            TransmissionIn transmission = new TransmissionIn(tempInRequest.who, tempInRequest.transmissionId) {

                                private FileOutputStream writer;
                                long startTime = System.currentTimeMillis();
                                long totalBytesReceived = 0;

                                @Override
                                public boolean createDirectory(String relativePath) {
                                    try {
                                        int i = 2;
                                        String tempPath = rootDir + "/" + relativePath;
                                        while (new File(tempPath).exists()) {
                                            tempPath = rootDir + "/" + relativePath + i++;
                                        }
                                        return new File(tempPath).mkdir();
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                public boolean createFile(String relativePath) {
                                    try {
                                        int i = 2;
                                        String tempPath = rootDir + "/" + relativePath;
                                        while (new File(tempPath).exists()) {
                                            tempPath = rootDir + "/" + relativePath + i++;
                                        }
                                        File file = new File(tempPath);
                                        if (!file.createNewFile())
                                            return false;
                                        writer = new FileOutputStream(file);
                                        return true;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                public boolean writeToFile(byte[] data, int off, int len) {
                                    try {
                                        writer.write(data, off, len);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return false;
                                    }
                                    totalBytesReceived += len - off;
                                    return true;
                                }

                                @Override
                                public boolean closeFile() {
                                    try {
                                        writer.close();
                                        return true;
                                    } catch (IOException e) {
                                        return false;
                                    }
                                }

                                @Override
                                public void onUpdateTransmissionSize(long size) {

                                }

                                @Override
                                public void onDone() {
                                    int totalTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
                                    println(String.format("Приём %1$s успешно завершён", id));
                                    println(String.format("Средняя скорость приёма %.2f МБ/с при средней скорости сети %.2f МБ/с", ((double) totalBytesReceived) / 1024 / 1024 / totalTime, ((double) realData) / 1024 / 1024 / totalTime));
                                }
                            };

                            sendere.processSendRequest(true, transmission);
                            println("Приём начат с идентификатором " + transmission.id);
                        } else if (line.equals("no")) {
                            question = false;
                            sendere.processSendRequest(false, TransmissionIn.createDummyTransmission(tempInRequest.who, tempInRequest.transmissionId));
                            println("Приём отклонён");
                        } else {
                            println("Ответьте yes или no");
                        }
                        continue;
                    }
                    if (line.equals("who")) {
                        if (sendere.getRemoteUsers().size() == 0) {
                            println("Сейчас в лоакльной сети никого. Если это не так, убедитесь, что устройства находятся в одной локальной сети");
                            println("");
                            continue;
                        }
                        println("Пользователи в сети:");
                        println("");
                        RemoteUserList users = sendere.getRemoteUsers();
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i) == null)
                                continue;
                            println("Пользователь " + i + ":");
                            println("Хэш-сумма:" + users.get(i).getHash());
                            println("Никнейм: " + users.get(i).getNickname());
                            println("Локальнй адрес: " + users.get(i).getAddress());
                            println("");
                        }
                    } else if (line.startsWith("tell ") && line.split(" ").length >= 3) {
                        String[] split = line.split(" ", 3);
                        RemoteUser tempUser;
                        try {
                            tempUser = sendere.getRemoteUsers().get(Integer.parseInt(split[1]));
                            if (tempUser == null)
                                throw new Exception();
                        } catch (Exception e) {
                            println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка");
                            continue;
                        }
                        sendere.sendTextMessage(tempUser, split[2]);
                        println("Сообщение отправлено");
                        if (!Settings.isAllowChat())
                            println("Обратите внимание, что ваши настройки запрещают приём текстовых сообщений, а значит вы не сможете получить ответ");
                    } else if (line.startsWith("speed ") && line.split(" ").length == 2) {
                        String[] split = line.split(" ", 3);
                        RemoteUser tempUser;
                        try {
                            tempUser = sendere.getRemoteUsers().get(Integer.parseInt(split[1]));
                            if (tempUser == null)
                                throw new Exception();
                        } catch (Exception e) {
                            println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка");
                            continue;
                        }
                        println("Замеряем скорость с пользователем " + sendere.getRemoteUsers().get(Integer.parseInt(split[1])) + " [" + split[1] + "]...");
                        int testDuration = 10;
                        long endTime = System.currentTimeMillis() + testDuration * 1000;
                        int packetsSend = 0;
                        while (System.currentTimeMillis() < endTime) {
                            sendere.sendMessage(tempUser, (byte)4, RawDataPacket.newBuilder().setTransmissionId(0)
                                    .setData(ByteString.copyFrom(new byte[1048572])).build());
                            packetsSend++;
                        }
                        println(String.format("Средняя скорость соединения с пользователем %.2f МБ/с", (float) packetsSend / testDuration));
                    } else if (line.startsWith("send") && line.split(" ").length >= 3) {
                        String[] split = line.split(" ", 3);
                        RemoteUser tempUser;
                        try {
                            tempUser = sendere.getRemoteUsers().get(Integer.parseInt(split[1]));
                            if (tempUser == null)
                                throw new Exception();
                        } catch (Exception e) {
                            println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка");
                            continue;
                        }

                        if (!new File(split[2]).exists()) {
                            println(String.format("Файла %1$s не существует", split[2]));
                            continue;
                        }

                        //Надо переделать расчёт номера передачи
                        TransmissionOut transmission = new TransmissionOut(tempUser, new File(split[2]).isDirectory(), split[2]) {

                            private Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION,true);

                            @Override
                            public void start() {
                                deflater.setStrategy(Deflater.HUFFMAN_ONLY);
                                recursiveSend(filename);
                                sendere.sendMessage(user, (byte) 0, TransmissionControlPacket.newBuilder()
                                        .setSignal(TransmissionControlPacket.Signal.SENDING_COMPLETE)
                                        .setTransmissionId(this.getId())
                                        .build());
                                onSuccess();
                            }

                            @Override
                            public void onFail() {
                                terminate();
                                println("Передача с номером " + id + " не удалась");
                            }

                            @Override
                            public void onSuccess() {
                                terminate();
                                println("Передача с номером " + id + " успешно завершена");
                            }

                            private void recursiveSend(String currentRelativePath) {
                                File file = new File(rooDirectory + "/" + currentRelativePath);
                                if (!file.exists())
                                    return;
                                if (file.isDirectory()) {
                                    if (!sendere.createRemoteDirectory(currentRelativePath, this))
                                        stop = true;
                                    if (stop)
                                        return;
                                    for (String name : file.list()) {
                                        recursiveSend(currentRelativePath + "/" + name);
                                    }
                                } else {
                                    try {
                                        sendere.createRemoteFile(currentRelativePath, this);
                                        FileInputStream in = new FileInputStream(file);
                                        byte[] data = new byte[1024 * 1024/8];
                                        int dataLength;
                                        byte flags = 0;
                                        while ((dataLength = in.read(data)) != -1) {
                                            // TODO: 16.04.2021 add GZip support, huku
                                            if (Settings.isAllowGzip()) {
                                                flags+=1;
                                            }
                                            sendere.sendMessage(user, flags, RawDataPacket.newBuilder()
                                                    .setTransmissionId(this.getId())
                                                    .setData(ByteString.copyFrom(data, 0, dataLength))
                                                    .build());
                                            if (stop)
                                                return;
                                        }
                                        sendere.sendMessage(user, (byte) 0, CloseFilePacket.newBuilder().setTransmissionId(this.getId()).build());
                                        //sendere.sendMessage(user, Headers.CLOSE_FILE, String.valueOf(id));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        stop = true;
                                    }
                                }
                            }
                        };
                        sendere.addTransmissionOut(transmission);
                        sendere.sendTransmissionRequest(transmission);
                    } else {
                        println("Команда не распознана");
                        println("");
                    }
                }

            }
        });
        consoleThread.start();

    }


    public void println(String msg) {
        System.out.println(msg);
    }

    public String readLine() {
        return scanner.nextLine();
    }
}
