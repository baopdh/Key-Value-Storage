package com.baopdh.dbserver.profiler;

public class ApiStat {
    private String name;
    private int totalReq = 0;       // Total requests to server
    private int pendingReq = 0;     // Number of pending requests
    private long totalTimeProc = 0; // Total time processing this api
    private int lastTimeProc = 0;   // Last time processing this api
    private float requestRate = 0;  // = (totalReqT2 - totalReqT1)/(totalProcT2 - totalProcT1)
    private float processRate = 0;  // = totalReqT2 / totalProcT2

    public ApiStat(String name) {
        this.name = name;
    }

    public int getTotalReq() {
        return totalReq;
    }

    public void setTotalReq(int totalReq) {
        this.totalReq = totalReq;
    }

    public int getPendingReq() {
        return pendingReq;
    }

    public void setPendingReq(int pendingReq) {
        this.pendingReq = pendingReq;
    }

    public long getTotalTimeProc() {
        return totalTimeProc;
    }

    public void setTotalTimeProc(long totalTimeProc) {
        this.totalTimeProc = totalTimeProc;
    }

    public int getLastTimeProc() {
        return lastTimeProc;
    }

    public void setLastTimeProc(int lastTimeProc) {
        this.lastTimeProc = lastTimeProc;
    }

    public float getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(float requestRate) {
        this.requestRate = requestRate;
    }

    public float getProcessRate() {
        return processRate;
    }

    public void setProcessRate(float processRate) {
        this.processRate = processRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
