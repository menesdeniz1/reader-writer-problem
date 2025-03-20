import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class ReadWriteLock {
    private final Semaphore readerLock = new Semaphore(1);
    private final Semaphore writerLock = new Semaphore(1);
    private int readerCount = 0;

    public void readLock() throws InterruptedException {
        readerLock.acquire();
        readerCount++;
        if (readerCount == 1) {
            writerLock.acquire();
        }
        readerLock.release();
    }

    public void readUnlock() throws InterruptedException {
        readerLock.acquire();
        readerCount--;
        if (readerCount == 0) {
            writerLock.release();
        }
        readerLock.release();
    }

    public void writeLock() throws InterruptedException {
        writerLock.acquire();
    }

    public void writeUnlock() {
        writerLock.release();
    }
}

class Writer implements Runnable {
    private final ReadWriteLock lock;

    public Writer(ReadWriteLock rw) {
        lock = rw;
    }

    public void run() {
        while (true) {
            try {
                lock.writeLock();
                System.out.println("Thread " + Thread.currentThread().getName() + " is WRITING");
                Thread.sleep(2000);
                System.out.println("Thread " + Thread.currentThread().getName() + " has finished WRITING");
                lock.writeUnlock();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Reader implements Runnable {
    private final ReadWriteLock lock;

    public Reader(ReadWriteLock rw) {
        lock = rw;
    }

    public void run() {
        while (true) {
            try {
                lock.readLock();
                System.out.println("Thread " + Thread.currentThread().getName() + " is READING");
                Thread.sleep(2000);
                System.out.println("Thread " + Thread.currentThread().getName() + " has FINISHED READING");
                lock.readUnlock();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Test {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock rw = new ReadWriteLock();

        for (int i = 0; i < 4; i++) {
            executorService.execute(new Writer(rw));
        }

        for (int i = 0; i < 5; i++) {
            executorService.execute(new Reader(rw));
        }

        executorService.shutdown();
    }
}