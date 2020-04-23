package com.baopdh.dbserver.database.taskmap;

import com.baopdh.thrift.gen.TASK;


public class PendingTask<V> {
    private TASK type;
    private V value;

    public PendingTask(TASK type, V value) {
        this.type = type;
        this.value = value;
    }

    public void updateTask(TASK type, V value) {
        this.type = type;
        this.value = value;
    }

    public TASK getType() {
        return this.type;
    }

    public V getValue() {
        return this.value;
    }
}
