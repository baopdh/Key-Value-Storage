package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;

import java.io.Serializable;

public abstract class AsyncTask<K extends Serializable, V extends Serializable> implements Runnable, Serializable {
    protected K key;
    protected V value;
    protected transient Storage<K, V> storage;

    public AsyncTask(K key, V value, Storage<K, V> storage) {
        this.key = key;
        this.value = value;
        this.storage = storage;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}
