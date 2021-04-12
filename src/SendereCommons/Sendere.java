package SendereCommons;

import lombok.Setter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Sendere {

    //port 1337 lol
    //20 ports must be enough...
    //21.08.2019
    public static final int START_PORT = 1337;
    public static final int END_PORT = 1356;
    public static final int DISCOVERY_PORT = 1338;

    /*
     Those constants defines the version of Sendere library

     Minor version shows little changes in library which don't affect commands structure
     and makes only internal changes
     It usually can be ignored in version check

     Middle version shows that some commands have been added or changed but Sendere still
     can work some way with clients having another middle version
     Middle version should be checked when sending commands and if remote client cannot handle
     some request user should be notified about it

     Major version shows that significant changes have been made since previous and their significance
     doesn't allow clients with different versions to normally work together
     This version should be checked when discovering clients in network and Sendere shouldn't allow any
     communication between users if it mismatch

     VERSION is a version code in 3-byte format that can be used for sending or something

     29.06.2020 huku
     */
    public static final int MAJOR_VERSION = 0;
    public static final int MIDDLE_VERSION = 0;
    public static final int MINOR_VERSION = 1;
    public static final int VERSION = MAJOR_VERSION << 16 + MIDDLE_VERSION << 8 + MINOR_VERSION;
    /**
     * Size in bytes of oncoming and incoming messages that should be used instead of
     * hardcoded value.
     */
    public static final int PACKET_LENGTH = 1024 * 33;

    private int mainPort;
    private int discoveryPort;
    public final long HASH;
    private ServerSocket serverSocket;
    MulticastSocket discoverySocket;
    private Thread receiverThread;
    private boolean allowReceiving = true;
    private boolean userReady = true;

    private RemoteUserList remoteUsers;
    private HashMap<String, TransmissionIn> transmissionsIn = new HashMap<String, TransmissionIn>();
    private HashMap<String, TransmissionOut> transmissionsOut = new HashMap<String, TransmissionOut>();

    private LinkedList<InRequest> inRequests = new LinkedList<InRequest>();
    private InRequest currentInRequest;

    private HashSet<ArpEntry> addressesToPing = new HashSet<ArpEntry>();

    private void onReceive(RemoteUser sender, byte flags, byte[] data) {
        // empty packet
        if (data.length == 0)
            return;
        // TODO: 11.04.2021 stub
        if (flags != 0)
            return;
        byte header = data[0];
        if (header == Headers.PING) {
            long remoteSUID = Converters.bytesToLong(data, 1);
            int remoteNickLength = data[9];
            String remoteNick = new String(data, 10, remoteNickLength, StandardCharsets.UTF_8);
            // self-ping
            // 11.04.2021 huku
            if (remoteSUID == HASH && remoteNick.equals(Settings.getNickname()))
                return;
            RemoteUser existingUser = remoteUsers.getByHash(remoteSUID);
            if (existingUser != null) {
                onRemoteUserUpdated(existingUser);
            } else {
                sender.identify(remoteNick, remoteSUID);
                remoteUsers.put(sender);
                onRemoteUserConnected(sender);
                if (Settings.getVisibility() == 1) {
                    byte[] byteSUID = Converters.longToBytes(HASH);
                    byte[] byteNickname = Settings.getNickname().getBytes(StandardCharsets.UTF_8);
                    byte[] nicknameLength = new byte[]{(byte) byteNickname.length};
                    sendMessage(sender, (byte) 0, new byte[]{Headers.PONG}, byteSUID, nicknameLength, byteNickname);
                }
            }
        } else if (header == Headers.PONG){
            long remoteSUID = Converters.bytesToLong(data, 1);
            int remoteNickLength = data[9];
            String remoteNick = new String(data, 10, remoteNickLength, StandardCharsets.UTF_8);
            // self-ping
            // 11.04.2021 huku
            if (remoteSUID == HASH && remoteNick.equals(Settings.getNickname()))
                return;
            RemoteUser existingUser = remoteUsers.getByHash(remoteSUID);
            if (existingUser != null) {
                onRemoteUserUpdated(existingUser);
            } else {
                sender.identify(remoteNick, remoteSUID);
                remoteUsers.put(sender);
                onRemoteUserFound(sender);
            }
        } else if (header == Headers.TEXT) {
            if (Settings.isAllowChat())
                onTextMessageReceived(sender, new String(data,1,data.length-1, StandardCharsets.UTF_8));
        }
//        } else if ((header.equals(Headers.SEND_REQUEST))) {
//            if (!Settings.isAllowReceiving()) {
//                // TODO: 21.06.2020 make special message if user don't allows to receive files
//                /*
//                 * Now requests process as it canceled by user, so sender unable to determine
//                 * if request really was canceled by user or user don't allows to receive files at all
//                 * 21.06.2020 huku
//                 */
//                processSendRequest(false, TransmissionIn.createDummyTransmission(sender, receivedMessage[1]));
//            } else {
//                InRequest request = new InRequest(sender, receivedMessage[0].equals(Headers.TRUE.toString()), receivedMessage[1], receivedMessage[2]);
//                if (userReady) {
//                    userReady = false;
//                    currentInRequest = request;
//                    onSendRequest(request);
//                } else {
//                    inRequests.add(request);
//                }
//            }
//        } else if (header.equals(Headers.SEND_RESPONSE)) {
//            final TransmissionOut transmission = transmissionsOut.get(receivedMessage[1]);
//            if (transmission != null) {
//                if (receivedMessage[0].equals(Headers.TRUE.toString())) {
//                    onSendResponse(true, transmission);
//                    Thread transmissionThread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            transmission.start();
//                        }
//                    });
//                    transmissionThread.start();
//                } else {
//                    onSendResponse(false, transmission);
//                    transmissionsOut.remove(receivedMessage[1]);
//                }
//            }
//        } else if (header.equals(Headers.MKFILE)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                transmission.createFile(receivedMessage[1]);
//            }
//        } else if (header.equals(Headers.MKDIR)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                transmission.createDirectory(receivedMessage[1]);
//            }
//        } else if (header.equals(Headers.RAW_DATA)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                //This line allows to write packet data into file and send feedback about operation success
//                //28.08.2019
//                int offset = receivedMessage[0].getBytes().length + "\n".length();
//                transmission.writeToFile(data, offset, length - offset);
//            }
//        } else if (header.equals(Headers.CLOSE_FILE)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                transmission.closeFile();
//            }
//        } else if (header.equals(Headers.SEND_COMPLETE)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                transmission.onDone();
//                transmissionsIn.remove(transmission.id);
//            }
//        } else if (header.equals(Headers.GZIP_DATA)) {
//            TransmissionIn transmission = transmissionsIn.get(receivedMessage[0]);
//            if (transmission != null) {
//                //This line allows to write packet data into file and send feedback about operation success
//                //28.08.2019
//                try {
//                    int offset = receivedMessage[0].getBytes().length + "\n".length();
//                    transmission.realData += length - offset;
//                    transmission.writeToFile(GzipUtils.unzip(data, offset, length - offset));
//                } catch (IOException e) {
//                    //fail
//                    e.printStackTrace();
//                }
//            }
//        }
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
            throw new RuntimeException("There are no free TCP ports in range " + START_PORT + "-" + END_PORT + ". Sendere need at least one of them to run.");
        }

        for (int i = START_PORT; i <= END_PORT; i++) {
            try {
                discoveryPort = i;
                discoverySocket = new MulticastSocket(discoveryPort);
                discoverySocket.joinGroup(InetAddress.getByName("224.0.0.1"));
                //discoverySocket.connect("");
                break;
            } catch (IOException e) {
                //e.printStackTrace();
                discoverySocket = null;
            }
        }
        if (discoverySocket == null) {
            throw new RuntimeException("There are no free UDP ports in range " + START_PORT + "-" + END_PORT + ". Sendere need at least one of them to run.");
        }

        HASH = ThreadLocalRandom.current().nextLong();
        System.out.println("Порт обнаружения " + discoveryPort);
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
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (allowReceiving) {
                    try {
                        RemoteUser unidentifiedUser = new RemoteUser(serverSocket.accept()) {
                            @Override
                            protected void onDisconnect() {
                                onUserDisconnected(this);
                                remoteUsers.removeByHash(getHash());
                            }

                            @Override
                            public void onReceive(byte flags, byte[] data) {
                                Sendere.this.onReceive(this, flags, data);
                            }
                        };
                    } catch (IOException e) {
                        /*e.printStackTrace();*/
                    }
                }
            }
        });
        thread.start();

        final DatagramPacket discoveryPacket = new DatagramPacket(new byte[1024], 1024);
        final MulticastSocket discoverySocket;
        try {
            discoverySocket = new MulticastSocket(DISCOVERY_PORT);
            discoverySocket.joinGroup(InetAddress.getByName("224.0.0.1"));
        } catch (IOException e) {
            onInternalError(2, DISCOVERY_PORT + "");
            e.printStackTrace();
            return;
        }
        Thread udpDiscoveryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (allowReceiving) {
                    try {
                        discoverySocket.receive(discoveryPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    String[] receivedData = new String(discoveryPacket.getData(), 0, discoveryPacket.getLength(), StandardCharsets.UTF_8).split("\n");
                    if (receivedData.length != 3)
                        continue;
                    if (!receivedData[0].equals(Headers.DEVICE_DISCOVERY))
                        continue;
                    try {
                        Socket remoteSocket = new Socket(InetAddress.getByName(receivedData[1]), Integer.parseInt(receivedData[2]));
                        RemoteUser unidentifiedUser = new RemoteUser(remoteSocket) {
                            @Override
                            protected void onDisconnect() {
                                onUserDisconnected(this);
                                remoteUsers.removeByHash(this.getHash());
                            }

                            @Override
                            public void onReceive(byte flags, byte[] data) {
                                Sendere.this.onReceive(this, flags, data);
                            }
                        };
                        byte[] byteSUID = Converters.longToBytes(HASH);
                        byte[] byteNickname = Settings.getNickname().getBytes(StandardCharsets.UTF_8);
                        byte[] nicknameLength = new byte[]{(byte) byteNickname.length};
                        sendMessage(unidentifiedUser, (byte) 0, new byte[]{Headers.PING}, byteSUID, nicknameLength, byteNickname);
                    } catch (IOException e) {
//            if (address[0] == 127)
//                e.printStackTrace();
                    }
                }
            }
        });
        udpDiscoveryThread.start();
    }


    boolean stopNeighbourRead;

    public void updateRemoteUsersList() {
        remoteUsers = new RemoteUserList();
        List<NetworkInterface> interfaces;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            interfaces = new ArrayList<>();
            e.printStackTrace();
        }
        for (NetworkInterface networkInterface : interfaces) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                System.out.println("start scanning " + address.toString());
                DatagramSocket udpSocket = null;
                try {
                    udpSocket = new DatagramSocket(0, address);
                    String localAddressString;
                    if (address instanceof Inet4Address)
                        localAddressString = ((Inet4Address) address).getHostAddress();
                    else if (address instanceof Inet6Address)
                        localAddressString = ((Inet6Address) address).getHostAddress();
                    else
                        continue;
                    byte[] discoveryPacketData = (Headers.DEVICE_DISCOVERY + "\n" + localAddressString + "\n" + mainPort).getBytes(StandardCharsets.UTF_8);
                    for (int i = START_PORT; i <= END_PORT; i++) {
                        DatagramPacket discoveryPacket = new DatagramPacket(discoveryPacketData, 0, discoveryPacketData.length, InetAddress.getByName("224.0.0.1"), i);
                        udpSocket.send(discoveryPacket);
                        System.out.println("discovery send success " + i);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    continue;
                }
            }
        }

        System.out.println("ping done");
        // wait for incomplete arp entries to be completed
        // 2 seconds must be enough
        // 24.02.2021 huku
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }

    private HashSet<ArpEntry> readNeighbourTable() {
        HashSet<ArpEntry> addressList = new HashSet<ArpEntry>();
        Process p = null;
        try {
            System.out.println("Running ip neigh");
            p = Runtime.getRuntime().exec("ip neigh");
            //p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                // TODO: make invalid neighbour records detection more reliable and andd ipv6 support
                // 24.02.2021 huku
                if (!line.contains(":")) {
                    continue;
                }
                String ipString = line.substring(0, line.indexOf(' '));
                if (ipString.contains(".")) {
                    // ipv4 address
                    byte[] ipBytes = new byte[4];
                    String[] ipBytesString = ipString.split("\\.");
                    if (ipBytesString.length != 4)
                        continue;
                    for (int i = 0; i < 4; i++) {
                        ipBytes[i] = (byte) Integer.parseInt(ipBytesString[i]);
                    }
                    addressList.add(new ArpEntry(ipBytes, START_PORT));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressList;
    }

    protected abstract void onInternalError(int code, String message);

    private byte[] intToByteArray(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

/*    public void addOrUpdateRemoteUser(String getNickname(), long hash, Socket socket) {
        boolean isNewUser = true;
        for (RemoteUser user : remoteUsers) {
            if (user.hash == hash && user.getNickname().equals(getNickname())) {
                user.addAddress(address);
                isNewUser = false;
                break;
            }
        }
        if (isNewUser)
            remoteUsers.add(new RemoteUser(getNickname(), hash, address, port));
    }*/

    public boolean sendMessage(RemoteUser remoteUser, byte header, String message) {
        return sendMessage(remoteUser, header, message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);
    }

    public boolean sendMessage(RemoteUser remoteUser, byte header, byte[] data, int length) {
        return remoteUser.sendMessage(header, data, length);
    }

    public boolean sendMessage(RemoteUser remoteUser, byte flags, byte[]... data) {
        return remoteUser.sendMessage(flags, data);
    }

    public int getMainPort() {
        return mainPort;
    }

    public RemoteUserList getRemoteUsers() {
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
        // TODO: 11.04.2021 stub
//        if (allow)
//            transmissionsIn.put(transmission.id, transmission);
//        sendMessage(transmission.user, Headers.SEND_RESPONSE, (allow ? Headers.TRUE : Headers.FALSE) + "\n" + transmission.id);
//        if (!inRequests.isEmpty()) {
//            currentInRequest = inRequests.removeLast();
//            onSendRequest(currentInRequest);
//        } else {
//            userReady = true;
//        }
    }

    public boolean createRemoteDirectory(String relativePath, TransmissionOut transmission) {
        return sendMessage(transmission.user, Headers.MKDIR, transmission.id + "\n" + relativePath);
    }

    public boolean createRemoteFile(String relativePath, TransmissionOut transmission) {
        return sendMessage(transmission.user, Headers.MKFILE, transmission.id + "\n" + relativePath);
    }

    public void addTransmissionOut(TransmissionOut transmission) {
        transmissionsOut.put(transmission.id, transmission);
    }

    public void sendTransmissionRequest(TransmissionOut transmission) {
        // TODO: 11.04.2021 stub
        //sendMessage(transmission.user, Headers.SEND_REQUEST, (transmission.isDirectory ? Headers.TRUE : Headers.FALSE) + "\n" + transmission.id + "\n" + transmission.filename);
    }
}