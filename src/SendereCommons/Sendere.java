package SendereCommons;

import java.io.IOException;
import java.net.*;
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
    public static final int PACKET_LENGTH = 1023;

    private int mainPort;
    public final long HASH;
    private DatagramSocket mainSocket;
    private Thread receiverThread;
    private boolean allowReceiving = true;
    private boolean userReady = true;

    private static HashSet<OnPacketReceiveListener> listeners;
    private ArrayList<RemoteUser> remoteUsers;
    private HashMap<Integer, TransmissionIn> transmissionsIn = new HashMap<Integer, TransmissionIn>();
    private HashMap<Integer, TransmissionOut> transmissionsOut = new HashMap<Integer, TransmissionOut>();

    private LinkedList<InRequest> inRequests = new LinkedList<>();
    private InRequest currentInRequest;

    OnPacketReceiveListener listener = new OnPacketReceiveListener() {
        @Override
        public void onReceive(DatagramPacket packet) {
            String[] receivedMessage = new String(Arrays.copyOf(packet.getData(), packet.getLength())).split("\n");
            if (receivedMessage[0].equals(Headers.PING)&&receivedMessage[1].equals(Headers.TRUE)){
                if(receivedMessage[2].equals(Settings.nickname)&&Long.parseLong(receivedMessage[3])==HASH)
                    return;
                boolean isNewUser = true;
                for (RemoteUser user: remoteUsers){
                    if (user.hash==Long.parseLong(receivedMessage[3]) && user.nickname.equals(receivedMessage[2])){
                        user.addAddress(packet.getAddress().getHostAddress());
                        onRemoteUserUpdated(user);
                        isNewUser = false;
                        break;
                    }
                }
                if (isNewUser){
                    RemoteUser user = new RemoteUser(receivedMessage[2], Long.parseLong(receivedMessage[3]),packet.getAddress().getHostAddress(),packet.getPort());
                    remoteUsers.add(user);
                    onRemoteUserConnected(user);
                }
                if(Settings.visibility == 1) {
                    String sendMessage = Headers.PONG + "\n" + Settings.nickname + "\n" + HASH;
                    sendPacket(new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, packet.getAddress(), packet.getPort()));
                }
            }else if(receivedMessage[0].equals(Headers.PONG)){
                if(receivedMessage[1].equals(Settings.nickname)&&Long.parseLong(receivedMessage[2])==HASH)
                    return;
                boolean isNewUser = true;
                for (RemoteUser user: remoteUsers){
                    if (user.hash==Long.parseLong(receivedMessage[2]) && user.nickname.equals(receivedMessage[1])){
                        user.addAddress(packet.getAddress().getHostAddress());
                        onRemoteUserUpdated(user);
                        isNewUser = false;
                        break;
                    }
                }
                if (isNewUser){
                    RemoteUser user = new RemoteUser(receivedMessage[1], Long.parseLong(receivedMessage[2]),packet.getAddress().getHostAddress(),packet.getPort());
                    remoteUsers.add(user);
                    onRemoteUserFound(user);
                }
            }else if (receivedMessage[0].equals(Headers.TEXT)){
                RemoteUser user = findRemoteUserByAddress(packet.getAddress().getHostAddress());
                if(user != null)
                    onTextMessageReceived(user,receivedMessage[1]);
            } else if ((receivedMessage[0].equals(Headers.SEND_REQUEST))){
                RemoteUser user = findRemoteUserByAddress(packet.getAddress().getHostAddress());
                if(user == null)
                    return;
                InRequest request = new InRequest(user, receivedMessage[1].equals(Headers.TRUE), Integer.parseInt(receivedMessage[2]), receivedMessage[3]);
                if(userReady){
                    userReady = false;
                    currentInRequest = request;
                    onSendRequest(request);
                }else{
                    inRequests.add(request);
                }
            } else if(receivedMessage[0].equals(Headers.MKFILE)){
                TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null)
                    sendMessage(Headers.SEND_FEEDBACK + "\n" + transmission.number +"\n" + (transmission.createFile(receivedMessage[2]) ? Headers.TRUE : Headers.FALSE), packet.getAddress(), packet.getPort());
            } else if(receivedMessage[0].equals(Headers.MKDIR)){
                TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null)
                    sendMessage(Headers.SEND_FEEDBACK + "\n" + transmission.number +"\n" + (transmission.createDirectory(receivedMessage[2]) ? Headers.TRUE : Headers.FALSE), packet.getAddress(), packet.getPort());
            } else if (receivedMessage[0].equals(Headers.RAW_DATA)) {
                TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null){
                    //This line allows to write packet data into file and send feedback about operation success
                    //28.08.2019
                    sendMessage(Headers.SEND_FEEDBACK + "\n" + transmission.number +"\n" + (transmission.writeToFile(Arrays.copyOfRange(packet.getData(), receivedMessage[0].getBytes().length+receivedMessage[1].getBytes().length+"\n\n".length(), packet.getLength())) ? Headers.TRUE : Headers.FALSE), packet.getAddress(), packet.getPort());
                }
            } else if(receivedMessage[0].equals(Headers.CLOSE_FILE)){
                TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null)
                    sendMessage(Headers.SEND_FEEDBACK + "\n" + transmission.number +"\n" + (transmission.closeFile() ? Headers.TRUE : Headers.FALSE), packet.getAddress(), packet.getPort());
            } else if(receivedMessage[0].equals(Headers.SEND_COMPLETE)){
                TransmissionIn transmission = transmissionsIn.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null)
                    sendMessage(Headers.SEND_FEEDBACK + (transmission.closeFile() ? Headers.TRUE : Headers.FALSE), packet.getAddress(), packet.getPort());
            } else if(receivedMessage[0].equals(Headers.SEND_FEEDBACK)){
                TransmissionOut transmission = transmissionsOut.get(Integer.parseInt(receivedMessage[1]));
                if(transmission!=null){
                    if(receivedMessage[2].equals(Headers.TRUE))
                        transmission.onIntermediateSuccess();
                    else
                        transmission.onFail();
                }
            }
        }
    };

    public Sendere() {
        for (int i = START_PORT; i <= END_PORT; i++) {
            try {
                mainSocket = new DatagramSocket(i);
                mainPort = i;
                break;
            } catch (SocketException e) {
                //e.printStackTrace();
            }
        }
        if (mainSocket == null){
            throw new RuntimeException("There are no free ports in range "+START_PORT+"-"+END_PORT+". Sendere need at least one of them to run.");
        }
        HASH = System.currentTimeMillis();
        listeners = new HashSet<>();
       // mainSocket.setSoTimeout(1000);
        addOnPacketReceiveListener(listener);
        startReceiving();
    }

    /**
     * Calls when updated some remote user info (e.g. add new IP address)
     * @param user remote user whose info updated
     */
    public abstract void onRemoteUserUpdated(RemoteUser user);

    /**
     * Calls when new remote user connected to local network and declared itself
     * @param user connected user
     */
    public abstract void onRemoteUserConnected(RemoteUser user);

    /**
     * Calls when already connected user was found by Sendere
     * @param user found user
     */
    public abstract void onRemoteUserFound(RemoteUser user);

    /**
     * Calls when some text data received
     * @param who remote user who send message
     * @param message message content
     */
    public abstract void onTextMessageReceived(RemoteUser who, String message);

    /**
     * Calls when remote user trying to send file or directory to the client
     * @param request class which contains request data
     */
    public abstract void onSendRequest(InRequest request);

    public void startReceiving(){
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (allowReceiving){
                    DatagramPacket packet = new DatagramPacket(new byte[PACKET_LENGTH],PACKET_LENGTH);
                    try {
                        mainSocket.receive(packet);
                        onPacketReceive(packet);
                    } catch (IOException e) {
                        /*e.printStackTrace();*/
                    }
                }
            }
        });
        receiverThread.start();
    }

    public void updateRemoteUsersList(){
        ExecutorService service = Executors.newCachedThreadPool();
        ArrayList<byte[]> addressesToPing = new ArrayList<>();
        remoteUsers = new ArrayList<>();
        for (SimpleNetworkInterface networkInterface: NetworkList.getNetworkList()){
            int prefixLength = networkInterface.subnetPreffixLength;
            if (prefixLength==32)
                continue;
            if(networkInterface.stringAddress.equals("null"))
                return;
            byte[] mask = new byte[]{0,0,0,0};
            for (int i=0; i<4; i++){
                if(prefixLength>=8) {
                    mask[i] = (byte) 255;
                    prefixLength-=8;
                } else {
                    mask[i] = (byte) (255 << (8 - prefixLength));
                    break;
                }
            }
            byte[] startAddress = new byte[4];
            byte[] endAddress = new byte[4];
            for (int i=0; i<4; i++){
                startAddress[i] = (byte)(networkInterface.byteAddress[i]&mask[i]);
                endAddress[i] = (byte)(networkInterface.byteAddress[i]|~mask[i]);
            }
            //System.out.println("start scanning "+networkInterface.stringAddress);
            boolean[] was255 = {false, false, false, false};
            boolean lastPing = false;
            while (true){
                byte[] tempAddress = startAddress.clone();
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //System.out.println("checking "+InetAddress.getByAddress(tempAddress).getHostAddress());
                            if (InetAddress.getByAddress(tempAddress).isReachable(2000)) {
                                boolean add = true;
                                for (byte[] bytes: addressesToPing) {
                                    if (Arrays.equals(bytes, tempAddress)) {
                                        add = false;
                                        break;
                                    }
                                }
                                if(add && !Arrays.equals(networkInterface.byteAddress, tempAddress))
                                    addressesToPing.add(tempAddress);
                            }
                        } catch (IOException e) {
                            //
                        }
                    }
                });
                startAddress[3]++;
                if(startAddress[3]==-1){
                    if(was255[3]) {
                        startAddress[2]++;
                        startAddress[3] = 0;
                    }
                    was255[3]=!was255[3];
                    if(startAddress[2]==-1){
                        if(was255[2]) {
                            startAddress[1]++;
                            startAddress[2] = 0;
                        }
                        was255[2]=!was255[2];
                        if(startAddress[1]==-1){
                            if(was255[1]) {
                                startAddress[0]++;
                                startAddress[1] = 0;
                            }
                            was255[1]=!was255[1];
                            if(startAddress[3]==0){
                                //WTF???
                                //21.08.2019
                            }
                        }
                    }
                }
                if (lastPing)
                    break;
                if(Arrays.equals(startAddress, endAddress))
                    lastPing=true;

            }
        }
        service.shutdown();
        while (!service.isTerminated());
        for (byte[] address: addressesToPing){
            for (int i = START_PORT; i <= END_PORT; i++) {
                try {
                    String message = Headers.PING + "\n" + (Settings.visibility == 1 ? (Headers.TRUE + "\n" + Settings.nickname + "\n" + HASH) :  Headers.FALSE);
                    sendPacket(new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByAddress(address), i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addOrUpdateRemoteUser(String nickname, long hash, String address, int port){
        boolean isNewUser = true;
        for (RemoteUser user: remoteUsers){
            if (user.hash==hash && user.nickname.equals(nickname)){
                user.addAddress(address);
                isNewUser = false;
                break;
            }
        }
        if (isNewUser)
            remoteUsers.add(new RemoteUser(nickname, hash, address, port));
    }

    public boolean sendMessage(String message, RemoteUser remoteUser) {
        try {
            return sendMessage(message, InetAddress.getByName(remoteUser.getAddresses().iterator().next()), remoteUser.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendMessage(String message, InetAddress address, int port) {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
            return sendPacket(packet);

    }

    public boolean sendPacket(DatagramPacket packet) {
        try {
            mainSocket.send(packet);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendRaw(byte[] data, int length, RemoteUser remoteUser) {
        try {
            return sendPacket(new DatagramPacket(data, length, InetAddress.getByName(remoteUser.getAddresses().iterator().next()), remoteUser.getPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOnPacketReceiveListener(OnPacketReceiveListener listener){
        listeners.add(listener);
    }

    public void removeOnPacketReceiveListener(OnPacketReceiveListener listener){
        listeners.remove(listener);
    }

    public void onPacketReceive(DatagramPacket packet){
        for (OnPacketReceiveListener listener: listeners){
            listener.onReceive(packet);
        }
    };

    public DatagramSocket getMainSocket() {
        return mainSocket;
    }

    public int getMainPort() {
        return mainPort;
    }

    public ArrayList<RemoteUser> getRemoteUsers() {
        return remoteUsers;
    }

    public RemoteUser findRemoteUserByAddress(String address){
        for (RemoteUser user: remoteUsers){
            for (String s: user.getAddresses()){
                if (s.equals(address))
                    return user;
            }
        }
        return null;
    }

    public void processSendRequest(boolean allow, TransmissionIn transmission){
        if(allow)
            transmissionsIn.put(transmission.number, transmission);
        sendMessage(Headers.SEND_RESPONSE+"\n"+ (allow ? Headers.TRUE : Headers.FALSE), currentInRequest.who);
        if (!inRequests.isEmpty()){
            currentInRequest = inRequests.removeLast();
            onSendRequest(currentInRequest);
        }else {
            userReady = true;
        }
    }

    public boolean createRemoteDirectory(String relativePath, TransmissionOut transmission) {
        return sendMessage(Headers.MKDIR+"\n"+transmission.number+"\n"+relativePath, transmission.user);
    }

    public boolean createRemoteFile(String relativePath, TransmissionOut transmission) {
        return sendMessage(Headers.MKFILE+"\n"+transmission.number+"\n"+relativePath, transmission.user);
    }

    public void startTransmissionOut(TransmissionOut transmission) {
        transmissionsOut.put(transmission.number, transmission);
        transmission.start();
    }
}

