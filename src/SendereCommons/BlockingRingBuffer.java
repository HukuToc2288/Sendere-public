package SendereCommons;

import lombok.Data;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class BlockingRingBuffer {
    private byte[] buffer;
    private int readPosition = 0;
    private int writePosition = 0;
    private int freeSpace;
    private final Lock lock = new ReentrantLock();

    public BlockingRingBuffer(int capacity){
        buffer = new byte[capacity];
        freeSpace = capacity;
    }

    public void write(byte data){
        while (freeSpace<1){
            lock.lock();
        }
        lock.lock();
        buffer[writePosition++]=data;
        freeSpace--;
        if (writePosition>=buffer.length)
            writePosition=0;
        lock.unlock();
    }

    public byte read(){
        byte data;
        while (freeSpace>=buffer.length){
            lock.lock();
        }
        data=buffer[readPosition++];
        freeSpace++;
        if (readPosition>=buffer.length)
            readPosition=0;
        lock.unlock();
        return data;
    }
//
//    public void write(byte[] data, int offset, int length){
//        while (freeSpace<length){
//            lock.lock();
//        }
//        lock.lock();
//        System.arraycopy();
//    }
}
