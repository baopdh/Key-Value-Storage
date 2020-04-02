package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;
import com.baopdh.dbserver.util.TransactionLog;
import org.apache.thrift.TBase;

import java.io.Serializable;

// This is just for logging purpose, not an actual task
// Because there're only 2 write task (put, delete) so field taskType is not added to save capacity
// Tasks are differentiated base on value is null or not
public class WarningTask<K extends Serializable, V extends Serializable & TBase<?,?>> extends AsyncTask<K, V> {
    public WarningTask(K key, V value) {
        super(key, value, null, null);
    }

    @Override
    public void run() {}
}
