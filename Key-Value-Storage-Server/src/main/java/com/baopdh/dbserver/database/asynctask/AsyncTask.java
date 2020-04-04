package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;

public abstract class AsyncTask<K> implements Runnable {
    protected K key;
    protected byte[] keyBytes;
    protected byte[] valueBytes;
    protected Storage storage;

    public AsyncTask(K key, byte[] keyBytes, byte[] valueBytes, Storage storage) {
        this.key = key;
        this.keyBytes = keyBytes;
        this.valueBytes = valueBytes;
        this.storage = storage;
    }

    public K getKey() {
        return this.key;
    }
}
