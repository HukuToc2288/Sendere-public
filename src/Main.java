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
            Settings.nickname = System.getProperty("user.name");
        }catch (Exception e){
            //Keep default
        }
        println("Сбор даннных о сетевых интерфесах...");
        NetworkList.updateList();
        if(NetworkList.getNetworkList().length==0){
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
                println("Подключился пользователь "+user.getNickname());
            }

            @Override
            public void onRemoteUserFound(RemoteUser user) {
                println("Обнаружен пользователь "+user.getNickname());
            }

            @Override
            public void onTextMessageReceived(RemoteUser who, String message) {
                println("Сообщение от пользователя "+who.getNickname()+": "+message);
            }

            @Override
            public void onSendRequest(InRequest request) {
                println(String.format("Пользователь %1$s хочет передать вам %2$s %3$s. Принять?", request.who, request.isDirectory ? "директорию" : "файл", request.filename));
                tempInRequest = request;
                question = true;
            }

            @Override
            public void onSendResponse(boolean allow, TransmissionOut transmission) {
                if(allow){
                    println("Передача "+transmission.number+" начата");
                }else {
                    println("Передача "+transmission.number+" отклонена");
                }
            }
        };
        println("Sendere запущен на порту "+sendere.getMainPort());
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
                                public boolean writeToFile(byte[] data) {
                                    try {
                                        writer.write(data);
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
                                    println(String.format("Приём %1$d успешно завершён", number));
                                }
                            };

                            sendere.processSendRequest(true, transmission);
                            println("Приём начат с идентификатором " + transmission.number);
                        } else if (line.equals("no")) {
                            question = false;
                            sendere.processSendRequest(false, new TransmissionIn(tempInRequest.who, tempInRequest.transmissionId) {
                                @Override
                                public boolean createDirectory(String relativePath) {
                                    return false;
                                }

                                @Override
                                public boolean createFile(String relativePath) {
                                    return false;
                                }

                                @Override
                                public boolean writeToFile(byte[] data) {
                                    return false;
                                }

                                @Override
                                public boolean closeFile() {
                                    return false;
                                }

                                @Override
                                public void onUpdateTransmissionSize(long size) {

                                }

                                @Override
                                public void onDone() {
                                }
                            });
                            println("Приём отклонён");
                        } else {
                            println("Ответьте yes или no");
                        }
                        continue;
                    }
                    if (line.equals("/who")) {
                        if (sendere.getRemoteUsers().size() == 0) {
                            println("Сейчас в лоакльной сети никого. Если это не так, убедитесь, что устройства находятся в одной локальной сети");
                            println("");
                            continue;
                        }
                        println("Пользователи в сети:");
                        println("");
                        for (int i = 0; i < sendere.getRemoteUsers().size(); i++) {
                            RemoteUser tempUser = sendere.getRemoteUsers().get(i);
                            println("Пользователь " + i + ":");
                            println("Никнейм: " + tempUser.getNickname());
                            println("Локальнй адрес: " + tempUser.getAddress());
                            println("");
                        }
                    } else if (line.startsWith("/tell ") && line.split(" ").length >= 3) {
                        String[] split = line.split(" ", 3);
                        RemoteUser tempUser;
                        try {
                            tempUser = sendere.getRemoteUsers().get(Integer.parseInt(split[1]));
                        } catch (Exception e) {
                            println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка");
                            continue;
                        }
                        sendere.sendMessage(tempUser, Headers.TEXT + "\n" + split[2]);
                    } else if (line.startsWith("/send") && line.split(" ").length >= 3) {
                        String[] split = line.split(" ", 3);
                        RemoteUser tempUser;
                        try {
                            tempUser = sendere.getRemoteUsers().get(Integer.parseInt(split[1]));
                        } catch (Exception e) {
                            println("Пользователь с номером \"" + split[1] + "\" не найден. Введите /who для получения списка");
                            continue;
                        }

                        if(!new File(split[2]).exists()){
                            println(String.format("Файла %1$s не существует", split[2]));
                            continue;
                        }

                        //Надо переделать расчёт номера передачи
                        TransmissionOut transmission = new TransmissionOut(tempUser, new File(split[2]).isDirectory(), (int)(System.currentTimeMillis()), split[2]) {

                            @Override
                            public void start() {
                                recursiveSend(filename);
                                onSuccess();
                            }

                            @Override
                            public void onIntermediateSuccess() {
                                resume();
                                System.out.print("*");
                            }

                            @Override
                            public void onFail() {
                                terminate();
                                println("Передача с номером "+number+" не удалась");
                            }

                            @Override
                            public void onSuccess() {
                                terminate();
                                println("Передача с номером "+number+" успешно завершена");
                            }

                            private void recursiveSend(String currentRelativePath){
                                File file = new File(rooDirectory+ "/" +currentRelativePath);
                                if(!file.exists())
                                    return;
                                if(file.isDirectory()){
                                    sendere.createRemoteDirectory(currentRelativePath, this);
                                    waitForResponse();
                                    if(stop)
                                        return;
                                    for (String name: file.list()){
                                        recursiveSend(currentRelativePath+"/"+name);
                                    }
                                }else {
                                    try {
                                        sendere.createRemoteFile(currentRelativePath, this);
                                        waitForResponse();
                                        FileInputStream in = new FileInputStream(file);
                                        byte data[] = new byte[32*1024];
                                        int dataLength;
                                        byte prefix[] = (Headers.RAW_DATA+"\n"+number+"\n").getBytes();
                                        while ((dataLength = in.read(data))!=-1){
                                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                                            outputStream.write(prefix);
                                            outputStream.write(data);
                                            sendere.sendMessage(user, outputStream.toByteArray(), prefix.length+dataLength);
                                            waitForResponse();
                                            if(stop)
                                                return;
                                        }
                                        sendere.sendMessage(user, Headers.CLOSE_FILE+"\n"+number);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                sendere.sendMessage(user, Headers.SEND_COMPLETE+"\n"+number);
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


    public void println(String msg){
            System.out.println(msg);
        }

    public String readLine(){
        return scanner.nextLine();
    }
}
