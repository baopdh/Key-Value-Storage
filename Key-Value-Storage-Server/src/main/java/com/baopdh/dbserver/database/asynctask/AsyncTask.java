package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;
import com.baopdh.dbserver.util.TransactionLog;
import org.apache.thrift.TBase;

import java.io.Serializable;

public abstract class AsyncTask<K extends Serializable, V extends Serializable & TBase<?,?>> implements Runnable, Serializable {
    protected K key;
    protected V value;
    protected transient Storage<K, V> storage;
    protected transient TransactionLog transactionLog;

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
            this.transactionLog.commit(this);
    }
}
