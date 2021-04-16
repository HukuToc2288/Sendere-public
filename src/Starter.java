import SendereCommons.OSUtils;
import SendereCommons.OSUtilsKt;
import SendereCommons.RSATest;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Logger;

public class Starter {
    public static void main(String[] args) {
        new Main().main(args);
    }

    static int i = 0;

    public static void main2(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LegacyGUI().init();
            }
        });
//        int[] endAddress = new int[]{172,30,63,255};
//        DatagramSocket udpSocket = new DatagramSocket(0);
//        byte[] pdata = "sendere/alive?".getBytes();
//        for (NetworkInterface networkInterface: Collections.list(NetworkInterface.getNetworkInterfaces())) {
//            udpSocket.send(new DatagramPacket(pdata, pdata.length, Inet6Address.getByAddress("null", new byte[]{}, networkInterface),1337));
//        }
//        for (int i=0; i<4; i++) {
//            long nextPingTime = System.currentTimeMillis()+1000;
//            int[] startAddress = new int[]{172, 30, 0, 1};
//            while (true) {
//                try {
//                    udpSocket.send(new DatagramPacket(pdata, pdata.length, InetAddress.getByAddress(new byte[]{
//                            (byte) startAddress[0], (byte) startAddress[1], (byte) startAddress[2], (byte) startAddress[3]}),
//                            9));
//                } catch (Exception ignored){
//
//                }
//                startAddress[3]++;
//                if (startAddress[3] == 256) {
//                    startAddress[2]++;
//                    startAddress[3] = 0;
//                    if (startAddress[2] == 256) {
//                        startAddress[1]++;
//                        startAddress[2] = 0;
//                        if (startAddress[1] == 256) {
//                            startAddress[0]++;
//                            startAddress[1] = 0;
//                            if (startAddress[0] == 256) {
//                                //WTF???
//                                //21.08.2019
//                            }
//                        }
//                    }
//                }
//                if (Arrays.equals(startAddress, endAddress))
//                    break;
//            }
//            while (nextPingTime>System.currentTimeMillis());
//        }
    }
}

