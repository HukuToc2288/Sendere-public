import SendereCommons.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {

    private Sendere sendere;
    private Scanner scanner;
    private boolean question = false;
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
                                        writer.write(data,off, len);
                                        totalBytesReceived+=len-off;
                                        return true;
                                    } catch (IOException e) {
                                        return false;
                                    }
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
                                    int totalTime = (int) ((System.currentTimeMillis()-startTime)/1000);
                                    println(String.format("Приём %1$s успешно завершён", id));
                                    println(String.format("Средняя скорость приёма %.2f МБ/с при средней скорости сети %.2f МБ/с", ((double)totalBytesReceived)/1024/1024/totalTime, ((double)realData)/1024/1024/totalTime));
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
                        sendere.sendMessage(tempUser, Headers.TEXT, split[2]);
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
                            sendere.sendMessage(tempUser, Headers.SPEED_MEASURE, new byte[1048572], 1048572);
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

                            @Override
                            public void start() {
                                recursiveSend(filename);
                                sendere.sendMessage(user, Headers.SEND_COMPLETE, String.valueOf(id));
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
                                    sendere.createRemoteDirectory(currentRelativePath, this);
                                    if (stop)
                                        return;
                                    for (String name : file.list()) {
                                        recursiveSend(currentRelativePath + "/" + name);
                                    }
                                } else {
                                    try {
                                        sendere.createRemoteFile(currentRelativePath, this);
                                        FileInputStream in = new FileInputStream(file);
                                        byte[] data = new byte[1024 * 1024 * (Settings.isAllowGzip() ? 4 : 1)];
                                        int dataLength;
                                        byte[] prefix = (id + "\n").getBytes();
                                        byte[] gzipPrefix = (id + "\n").getBytes();
                                        while ((dataLength = in.read(data)) != -1) {
                                            ByteArrayOutputStream outputStream;
                                            if (Settings.isAllowGzip()){
                                                byte[][] gdatas = GzipUtils.doMulticoreGZip(data, dataLength);
                                                for (int i = 0; i < gdatas.length; i++) {
                                                    outputStream = new ByteArrayOutputStream();
                                                    if (gdatas[i].length < dataLength){
                                                        outputStream.write(gzipPrefix);
                                                        outputStream.write(gdatas[i]);
                                                        sendere.sendMessage(user, Headers.GZIP_DATA, outputStream.toByteArray(), gdatas[i].length+gzipPrefix.length);
                                                    } else {
                                                        //If we don't managed to make compressed block size lower than original
                                                        //20.06.2020 huku
                                                        outputStream = new ByteArrayOutputStream();
                                                        outputStream.write(prefix);
                                                        outputStream.write(data);
                                                        sendere.sendMessage(user, Headers.RAW_DATA,outputStream.toByteArray(), prefix.length + dataLength);
                                                    }
                                                    outputStream.close();
                                                }
                                            }else {
                                                outputStream = new ByteArrayOutputStream();
                                                outputStream.write(prefix);
                                                outputStream.write(data);
                                                sendere.sendMessage(user, Headers.RAW_DATA, outputStream.toByteArray(), prefix.length + dataLength);
                                                outputStream.close();
                                            }
                                            if (stop)
                                                return;
                                        }
                                        sendere.sendMessage(user, Headers.CLOSE_FILE , String.valueOf(id));
                                    } catch (IOException e) {
                                        e.printStackTrace();
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
