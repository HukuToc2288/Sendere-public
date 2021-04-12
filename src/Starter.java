import SendereCommons.OSUtils;
import SendereCommons.OSUtilsKt;
import SendereCommons.RSATest;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Starter {
    public static void main2(String[] args) {
        new Main().main(args);
    }

    static int i = 0;

    static int cnt = 0;
    static String packet = "Ьпръ " + cnt;
    static final Object pauseThreadLock = new Object();
    static final Object changeMessageLock = new Object();

    public static void main(String[] args) throws IOException, InterruptedException {
        //new Main().main(args);
        final Lock lock = new ReentrantLock();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (pauseThreadLock) {
                            pauseThreadLock.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                    for (int j = 0; j < 10; j++) {
                        System.out.print(packet);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println();
                    packet = null;
                    synchronized (changeMessageLock) {
                        changeMessageLock.notify();
                    }
                }
            }
        });
        thread.start();
        Scanner scanner = new Scanner(System.in);
        while (true) {

            synchronized (changeMessageLock) {
                packet = "Ьпръ " + ++cnt;
            }
            synchronized (pauseThreadLock) {
                pauseThreadLock.notify();
            }
            synchronized (changeMessageLock){
                changeMessageLock.wait();
            }
        }
    }
}

