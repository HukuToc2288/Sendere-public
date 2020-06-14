import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;

public class Starter {
    public static void main(String[] args) {
        new Main().main(args);
        //main2(args);
    }

    static int i =0;

    public static void main2(String[] args) {
        while (true){
            i++;
            new Thread(() -> {
                if(i%100==0)
                    System.out.println("Thread "+i);
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                }
            }).start();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
            }
        }
    }
}

