package com.baopdh.dbserver.database.taskmap;

public class PendingTask<V> {
    private int count;
    private TASK type;
    private V value;
    private TASK preType;
    private V preValue;

    public PendingTask(TASK type, V value) {
        this.count = 1;
        this.type = type;
        this.value = value;
    }

    public void updateTask(TASK type, V value) {
        ++this.count;
        this.preType = this.type;
        this.preValue = this.value;
        this.type = type;
        this.value = value;
    }

    public int rollback() {
        this.type = this.preType;
        this.value = this.preValue;
        return --this.count;
    }

    public int downCountAndGet() {
        return --this.count;
    }

    public TASK getType() {
        return this.type;
    }

    public V getValue() {
        return this.value;
    }
}
