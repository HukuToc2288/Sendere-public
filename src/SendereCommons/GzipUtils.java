package SendereCommons;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

    static volatile int cores = 0;

    static {
        if (cores == 0)
            cores = Runtime.getRuntime().availableProcessors();
    }

    public static byte[][] doMulticoreGZip(byte[] buffer, int length) {
        int blockSize = (length / cores);
        int lastBlockSize = blockSize+length % cores;
        byte[][] datas = new byte[cores][];
        Thread[] threads = new Thread[cores];
        for (int i = 0; i < datas.length; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ByteArrayOutputStream obj = new ByteArrayOutputStream();
                        MyGZIPOutputStream myGZIPOutputStream = new MyGZIPOutputStream(obj);
                        //myGZIPOutputStream.setLevel(Deflater.BEST_COMPRESSION);
                        BufferedOutputStream gzip = new BufferedOutputStream(myGZIPOutputStream);
                        gzip.write(buffer, finalI * blockSize, finalI+1 == datas.length ? lastBlockSize : blockSize);
                        gzip.close();
                        datas[finalI] = obj.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            threads[i] = thread;
        }
        while (atLeastOneWorking(threads)) ;
        return datas;
    }

    static class MyGZIPOutputStream extends GZIPOutputStream {
        public MyGZIPOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        public void setLevel(int level) {
            def.setLevel(level);
        }
    }

    private static boolean atLeastOneWorking(Thread[] threads) {
        for (Thread thread : threads)
            if (thread.isAlive())
                return true;
        return false;
    }

    public static byte[] unzip(byte[] gdata, int off, int len) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(gdata,off, len);
        BufferedInputStream gzipInputStream = new BufferedInputStream(new GZIPInputStream(byteArrayInputStream));
        byte[] data = gzipInputStream.readAllBytes();
        gzipInputStream.close();
        return data;
    }
}
