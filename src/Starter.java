import SendereCommons.OSUtils;
import SendereCommons.OSUtilsKt;
import SendereCommons.RSATest;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Starter {

    static final int THREAD_COUNT = 2;

    static ExecutorService zlibExecutorService = Executors.newFixedThreadPool(3);

    public static void main2(String[] args) {
        new Main().main(args);
    }


    static int cnt = 0;
    static String packet = "Ьпръ " + cnt;
    static final Object pauseThreadLock = new Object();
    static final Object changeMessageLock = new Object();

    public static void main(String[] args) throws IOException, InterruptedException {
        new Main().main(args);
//        File file = new File("/home/huku/Downloads/terraria_english_v1_4_1_2_42620.sh");
//        FileInputStream fis = new FileInputStream(file);
//        byte[] data = new byte[(int) file.length()];
//        fis.read(data);
//        fis.close();
//        long time = System.currentTimeMillis();
//        byte[] deflated = singleThreadDeflate(data);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Single thread: Compressed file size: " + deflated.length * 100.0 / data.length + "%, speed: " + data.length / 1024 / 1024.0 / (time / 1000.0) + "MB/s");
//
//        time = System.currentTimeMillis();
//        deflated = deflate(data);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Multiple thread: Compressed file size: " + deflated.length * 100.0 / data.length + "%, speed: " + data.length / 1024 / 1024.0 / (time / 1000.0) + "MB/s");
//        zlibExecutorService.shutdown();
    }

    static final int BUFFER_SIZE = 1024 * 256;

    public static byte[] deflate(final byte[] data) throws IOException {
        final Deflater[] deflaters = new Deflater[THREAD_COUNT];
        for (int i = 0; i < deflaters.length; i++) {
            final Deflater deflater = new Deflater(9, false);
            deflater.setStrategy(Deflater.DEFAULT_STRATEGY);
            deflaters[i] = deflater;
        }
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        List<Callable<Boolean>> taskList = new ArrayList<>();
        for (int j = 0; j < data.length; j += BUFFER_SIZE * THREAD_COUNT) {
            final int finalJ = j;
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int finalI = i;
                taskList.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        int blockSize = Math.min(BUFFER_SIZE, data.length - (finalJ + finalI * BUFFER_SIZE));
                        final byte[] buffer = new byte[BUFFER_SIZE];
                        deflaters[finalI].setInput(data, finalJ + finalI * BUFFER_SIZE, blockSize);
                        int count = deflaters[finalI].deflate(buffer);
                        deflaters[finalI].reset();
                        if (count <= blockSize)
                            outputStream.write(buffer, 0, count);
                        else
                            outputStream.write(data, finalJ + finalI * BUFFER_SIZE, blockSize);
                        return true;
                    }
                });
            }
            try {
                zlibExecutorService.invokeAll(taskList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskList.clear();
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }

    public static byte[] singleThreadDeflate(final byte[] data) throws IOException {
        final Deflater deflater = new Deflater(9, false);
        deflater.setStrategy(Deflater.DEFAULT_STRATEGY);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        final byte[] buffer = new byte[BUFFER_SIZE];
        for (int j = 0; j < data.length; j += BUFFER_SIZE) {

            int blockSize = Math.min(BUFFER_SIZE, data.length - j);
            deflater.setInput(data, j, blockSize);
            int count = deflater.deflate(buffer);
            deflater.reset();
            if (count <= blockSize)
                outputStream.write(buffer, 0, count);
            else
                outputStream.write(data, j, blockSize);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        return output;
    }
}

