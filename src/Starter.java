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
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            threads.add(thread);
            System.out.println(i);
        }
        //new Main().main(args);
    }
}

