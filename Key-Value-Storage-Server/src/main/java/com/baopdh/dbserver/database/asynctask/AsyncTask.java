package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;
import com.baopdh.dbserver.thrift.gen.TASK;
import com.baopdh.dbserver.thrift.gen.Task;
import com.baopdh.dbserver.thrift.gen.User;
import com.baopdh.dbserver.util.TransactionLog;
import org.apache.thrift.TBase;

import java.io.Serializable;

public abstract class AsyncTask<K, V extends Serializable & TBase<?,?>> implements Runnable {
    protected K key;
    protected V value;
    protected Storage<K, V> storage;
    protected TransactionLog transactionLog;

    public AsyncTask(K key, V value, Storage<K, V> storage, TransactionLog transactionLog) {
        this.key = key;
        this.value = value;
        this.storage = storage;
        this.transactionLog = transactionLog;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public void logWarning() {
        if (this.transactionLog != null)
            this.transactionLog.commit(new Task((int)this.key, (User)this.value, TASK.WARNING));
    }
}
