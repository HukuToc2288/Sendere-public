package SendereCommons;

import java.io.IOException;
import java.net.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Sendere {

    //port 1337 lol
    //20 ports must be enough...
    //21.08.2019
    public static final int START_PORT = 1337;
    public static final int END_PORT = 1356;

    /**
     * Size in bytes of oncoming and incoming messages that should be used instead of
     * hardcoded value.
     */
    public static final int PACKET_LENGTH = 1024 * 33;

    private int mainPort;
    public final long HASH;
    private ServerSocket serverSocket;
    private Thread receiverThread;
    private boolean allowReceiving = true;
    private boolean userReady = true;
    private int newUserId = 0;

    private HashMap<Long, RemoteUser> remoteUsers;
    private HashMap<Integer, TransmissionIn> transmissionsIn = new HashMap<Integer, TransmissionIn>();
    private HashMap<Integer, TransmissionOut> transmissionsOut = new HashMap<Integer, TransmissionOut>();

    private LinkedList<InRequest> inRequests = new LinkedList<InRequest>();
    private InRequest currentInRequest;

    private void onReceive(RemoteUser sender, byte[] buffer, int length) {
        String[] receivedMessage = new String(Arrays.copyOf(buffer, length)).split("\n");
        if (receivedMessage[0].equals(Headers.PING) && receivedMessage[1].equals(Headers.TRUE)) {
            if (receivedMessage[2].equals(Settings.nickname) && Long.parseLong(receivedMessage[3]) == HASH && sender.getPort() == getMainPort())
                return;
            sender.identify(receivedMessage[2], Long.parseLong(receivedMessage[3]));
            remoteUsers.put(Long.parseLong(receivedMessage[3]), sender);
            onRemoteUserConnected(sender);
            if (Settings.visibility == 1) {
                String pongMessage = Headers.PONG + "\n" + Settings.nickname + "\n" + HASH;
                sendMessage(sender, pongMessage);
            }
        } else if (receivedMessage[0].equals(Headers.PONG)) {
            if (receivedMessage[1].equals(Settings.nickname) && Long.parseLong(receivedMessage[2]) == HASH && sender.getPort() == getMainPort())
                return;
            RemoteUser existingUser = remoteUsers.get(sender.getHash());
            if (existingUser != null) {
                onRemoteUserUpdated(existingUser);
            } else {
                sender.identify(receivedMessage[1], Long.parseLong(receivedMessage[2]));
                remoteUsers.put(Long.parseLong(receivedMessage[2]), sender);
                onRemoteUserFound(sender);
            }
        } else if (receivedMessage[0].equals(Headers.TEXT)) {
            onTextMessageReceived(sender, receivedMessage[1]);
        } else if ((receivedMessage[0].equals(Headers.SEND_REQUEST))) {
            InRequest request = new InRequest(sender, receivedMessage[1].equals(Headers.TRUE), Integer.parseInt(receivedMessage[2]), receivedMessage[3]);
            if (userReady) {
                userReady = false;
                currentInRequest = request;
                onSendRequest(request);
            } else {
                inRequests.add(request);
            }
        } else if (receivedMessage[0].equals(Headers.SEND_RESPONSE)) {
            TransmissionOut transmission = transmissionsOut.get(Integer.parseInt(receivedMessage[2]));
            if (transmission != null) {
                if (receivedMessage[1].equals(Headers.TRUE)) {
                    onSendResponse(true, transmission);
                    Thread transmissionThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            transmission.start();
                        }
                    });
                    transmissionThread.start();
                } else {
                    onSendResponse(false, transmission);
                    transmissionsOut.remove(Integer.parseInt(receivedMessage[2]));
                }
            }
        } else if (receivedMessage[0].equals(Headers.MKFILE)) {
            TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
            if (transmission != null) {
                transmission.createFile(receivedMessage[2]);
            }
        } else if (receivedMessage[0].equals(Headers.MKDIR)) {
            TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
            if (transmission != null) {
                transmission.createDirectory(receivedMessage[2]);
            }
        } else if (receivedMessage[0].equals(Headers.RAW_DATA)) {
            TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
            if (transmission != null) {
                //This line allows to write packet data into file and send feedback about operation success
                //28.08.2019
                transmission.writeToFile(Arrays.copyOfRange(buffer, receivedMessage[0].getBytes().length + receivedMessage[1].getBytes().length + "\n\n".length(), length));
            }
        } else if (receivedMessage[0].equals(Headers.CLOSE_FILE)) {
            TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
            if (transmission != null) {
                transmission.closeFile();
            }
        } else if (receivedMessage[0].equals(Headers.SEND_COMPLETE)) {
            TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
            if (transmission != null) {
                transmission.onDone();
                transmissionsIn.remove(transmission.number);
            }
        }
    }

    public Sendere() {
        for (int i = START_PORT; i <= END_PORT; i++) {
            try {
                serverSocket = new ServerSocket(i);
                mainPort = i;
                break;
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        if (serverSocket == null) {
            throw new RuntimeException("There are no free ports in range " + START_PORT + "-" + END_PORT + ". Sendere need at least one of them to run.");
        }
        HASH = System.currentTimeMillis();
        startReceiving();
    }

    /**
     * Calls when updated some remote user info (e.g. add new IP address)
     *
     * @param user remote user whose info updated
     */
    public abstract void onRemoteUserUpdated(RemoteUser user);

    /**
     * Calls when new remote user connected to local network and declared itself
     *
     * @param user connected user
     */
    public abstract void onRemoteUserConnected(RemoteUser user);

    /**
     * Calls when already connected user was found by Sendere
     *
     * @param user found user
     */
    public abstract void onRemoteUserFound(RemoteUser user);

    /**
     * Calls when some text data received
     *
     * @param who     remote user who send message
     * @param message message content
     */
    public abstract void onTextMessageReceived(RemoteUser who, String message);

    /**
     * Calls when remote user trying to send file or directory to the client
     *
     * @param request class which contains request data
     */
    public abstract void onSendRequest(InRequest request);

    public abstract void onSendResponse(boolean allow, TransmissionOut transmission);

    public abstract void onUserDisconnected(RemoteUser remoteUser);

    public void startReceiving() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (allowReceiving) {
                    try {
                        RemoteUser unidentifiedUser = new RemoteUser(serverSocket.accept()) {
                            @Override
                            protected void onDisconnect() {
                                remoteUsers.remove(getHash());
                                onUserDisconnected(this);
                            }

                            @Override
                            public void onReceive(byte[] buffer, int length) {
                                Sendere.this.onReceive(this, buffer, length);
                            }
                        };
                    } catch (IOException e) {
                        /*e.printStackTrace();*/
                    }
                }
            }
        });
        thread.start();
    }

    public void updateRemoteUsersList() {
        ExecutorService service = Executors.newFixedThreadPool(256);
        ArrayList<byte[]> addressesToPing = new ArrayList<>();
        remoteUsers = new HashMap<>();
        for (SimpleNetworkInterface networkInterface : NetworkList.getNetworkList()) {
            int prefixLength = networkInterface.subnetPreffixLength;
            if (prefixLength<24)
                onInternalError(1,prefixLength+"");
            if (prefixLength == 32)
                continue;
            if (networkInterface.stringAddress.equals("null"))
                return;
            byte[] mask = new byte[]{0, 0, 0, 0};
            for (int i = 0; i < 4; i++) {
                if (prefixLength >= 8) {
                    mask[i] = (byte) 255;
                    prefixLength -= 8;
                } else {
                    mask[i] = (byte) (255 << (8 - prefixLength));
                    break;
                }
            }
            int[] startAddress = new int[4];
            byte[] endAddress = new byte[4];
            for (int i = 0; i < 4; i++) {
                startAddress[i] = (byte) (networkInterface.byteAddress[i] & mask[i]);
                endAddress[i] = (byte) (networkInterface.byteAddress[i] | ~mask[i]);
            }
            //System.out.println("start scanning "+networkInterface.stringAddress);
            boolean[] was255 = {false, false, false, false};
            boolean lastPing = false;
            while (true) {
                int[] tempAddress = startAddress.clone();
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //System.out.println("checking "+InetAddress.getByAddress(tempAddress).getHostAddress());
                            byte[] tempByteAddress = intToByteArray(tempAddress);

                            if (InetAddress.getByAddress(tempByteAddress).isReachable(2000)) {
                                boolean add = true;
                                for (byte[] bytes : addressesToPing) {
                                    if (Arrays.equals(bytes, tempByteAddress)) {
                                        add = false;
                                        break;
                                    }
                                }
                                if (add /*&& !Arrays.equals(networkInterface.byteAddress, tempByteAddress)*/)
                                    addressesToPing.add(tempByteAddress);
                            }
                        } catch (IOException e) {
                            //
                        }
                    }
                });
                startAddress[3]++;
                if (startAddress[3] == 256) {
                    startAddress[2]++;
                    startAddress[3] = 0;
                    if (startAddress[2] == 256) {
                        startAddress[1]++;
                        startAddress[2] = 0;
                        if (startAddress[1] == 256) {
                            startAddress[0]++;
                            startAddress[1] = 0;
                            if (startAddress[0] == 256) {
                                //WTF???
                                //21.08.2019
                            }
                        }
                    }
                }
                if (Arrays.equals(intToByteArray(tempAddress), endAddress))
                    break;

            }
        }
        service.shutdown();
        while (!service.isTerminated()) ;
        service = Executors.newFixedThreadPool(256);
        String message = Headers.PING + "\n" + (Settings.visibility == 1 ? (Headers.TRUE + "\n" + Settings.nickname + "\n" + HASH) : Headers.FALSE);
        for (byte[] address : addressesToPing) {
            for (int i = START_PORT; i <= END_PORT; i++) {
                int finalI = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket remoteSocket = new Socket(InetAddress.getByAddress(address), finalI);
                            RemoteUser unidentifiedUser = new RemoteUser(remoteSocket) {
                                @Override
                                protected void onDisconnect() {
                                    remoteUsers.remove(this.getHash());
                                    onUserDisconnected(this);
                                }

                                @Override
                                public void onReceive(byte[] buffer, int length) {
                                    Sendere.this.onReceive(this, buffer, length);
                                }
                            };
                            sendMessage(unidentifiedUser, message);
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                    }
                });
            }
        }
        service.shutdown();
        while (!service.isTerminated()) ;
    }

    protected abstract void onInternalError(int code, String message);

    private byte[] intToByteArray(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

/*    public void addOrUpdateRemoteUser(String nickname, long hash, Socket socket) {
        boolean isNewUser = true;
        for (RemoteUser user : remoteUsers) {
            if (user.hash == hash && user.nickname.equals(nickname)) {
                user.addAddress(address);
                isNewUser = false;
                break;
            }
        }
        if (isNewUser)
            remoteUsers.add(new RemoteUser(nickname, hash, address, port));
    }*/

    public boolean sendMessage(RemoteUser remoteUser, String message) {
        return sendMessage(remoteUser, message.getBytes(), message.getBytes().length);
    }

    public boolean sendMessage(RemoteUser remoteUser, byte[] data, int length) {
        return remoteUser.sendMessage(data, length);
    }

    public int getMainPort() {
        return mainPort;
    }

    public HashMap<Long, RemoteUser> getRemoteUsers() {
        return remoteUsers;
    }

/*    public RemoteUser findRemoteUserByAddress(String address) {
        for (RemoteUser user : remoteUsers) {
            for (String s : user.getAddresses()) {
                if (s.equals(address))
                    return user;
            }
        }
        return null;
    }*/

    public void processSendRequest(boolean allow, TransmissionIn transmission) {
        if (allow)
            transmissionsIn.put(transmission.number, transmission);
        sendMessage(transmission.user, Headers.SEND_RESPONSE + "\n" + (allow ? Headers.TRUE : Headers.FALSE) + "\n" + transmission.number);
        if (!inRequests.isEmpty()) {
            currentInRequest = inRequests.removeLast();
            onSendRequest(currentInRequest);
        } else {
            userReady = true;
        }
    }

    public boolean createRemoteDirectory(String relativePath, TransmissionOut transmission) {
        return sendMessage(transmission.user, Headers.MKDIR + "\n" + transmission.number + "\n" + relativePath);
    }

    public boolean createRemoteFile(String relativePath, TransmissionOut transmission) {
        return sendMessage(transmission.user, Headers.MKFILE + "\n" + transmission.number + "\n" + relativePath);
    }

    public void addTransmissionOut(TransmissionOut transmission) {
        transmissionsOut.put(transmission.number, transmission);
    }

    public void sendTransmissionRequest(TransmissionOut transmission) {
        sendMessage(transmission.user, Headers.SEND_REQUEST + "\n" + (transmission.isDirectory ? Headers.TRUE : Headers.FALSE) + "\n" + transmission.number + "\n" + transmission.filename);
    }
}