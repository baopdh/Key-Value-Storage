package com.baopdh.dbserver.util;

import java.util.concurrent.Semaphore;

public class MultipleReadWriteLog {
    private int countRead, countWrite;
    private Semaphore turn = new Semaphore(1);
    private Semaphore mutex = new Semaphore(1);
    private Semaphore readCountMutex = new Semaphore(1);
    private Semaphore writeCountMutex = new Semaphore(1);

    public void lockRead() {
        turn.acquireUninterruptibly();
        readCountMutex.acquireUninterruptibly();
        if (countRead == 0)
            mutex.acquireUninterruptibly();
        ++countRead;
        readCountMutex.release();
        turn.release();
    }

    public void releaseRead() {
        readCountMutex.acquireUninterruptibly();
        --countRead;
        if (countRead == 0)
            mutex.release();
        readCountMutex.release();
    }

    public void lockWrite() {
        turn.acquireUninterruptibly();
        writeCountMutex.acquireUninterruptibly();
        if (countWrite == 0)
            mutex.acquireUninterruptibly();
        ++countWrite;
        writeCountMutex.release();
        turn.release();
    }

    public void releaseWrite() {
        writeCountMutex.acquireUninterruptibly();
        --countWrite;
        if (countWrite == 0)
            mutex.release();
        writeCountMutex.release();
    }
}
