package com.baopdh.dbserver.profiler;

import java.util.concurrent.Semaphore;

public class ApiStat {
    private final String name;
    private int totalReq = 0;       // Total requests to server
    private int lastTotalReq = 0;
    private long totalTimeProc = 0; // Total time processing this api
    private long lastTotalTimeProc = 0;
    private int pendingReq = 0;     // Number of pending requests
    private long lastTimeProc = 0;   // Last time processing this api
    private double requestRate = 0;  // = (totalReqT2 - totalReqT1)/(totalProcT2 - totalProcT1)
    private double processRate = 0;  // = totalReqT2 / totalProcT2

    private final Semaphore mutex = new Semaphore(1);

    public ApiStat(String name) {
        this.name = name;
    }

    public int getTotalReq() {
        return totalReq;
    }

    public int getPendingReq() {
        return pendingReq;
    }

    public long getTotalTimeProc() {
        return totalTimeProc;
    }

    public long getLastTimeProc() {
        return lastTimeProc;
    }

    public double getRequestRate() {
        return requestRate;
    }

    public double getProcessRate() {
        return processRate;
    }

    public String getName() {
        return name;
    }

    public void addPendingRequest() {
        mutex.acquireUninterruptibly();
        ++this.pendingReq;
        mutex.release();
    }

    public void saveNewRequest(long l) {
        l *= 1000;
        mutex.acquireUninterruptibly();
        ++this.totalReq;
        --this.pendingReq;
        this.totalTimeProc += l;
        this.lastTimeProc = l;
        this.processRate = this.totalReq * 1000000.0 / this.totalTimeProc;
        mutex.release();
    }
}
