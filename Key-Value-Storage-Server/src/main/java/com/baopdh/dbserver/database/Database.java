/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database;

import com.baopdh.dbserver.database.asynctask.AsyncTask;
import com.baopdh.dbserver.database.asynctask.DeleteTask;
import com.baopdh.dbserver.database.asynctask.PutTask;
import com.baopdh.dbserver.database.storage.Storage;
import com.baopdh.dbserver.database.taskmap.PendingTask;
import com.baopdh.dbserver.database.taskmap.TaskMap;
import com.baopdh.dbserver.util.*;
import com.baopdh.thrift.gen.TASK;
import org.apache.thrift.TBase;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author cpu60019
 */
public class Database<K, V extends TBase<?,?>> implements IDatabase<K, V> {
    private final Storage storage;
    private final TaskMap<K, V> taskMap;
    private final Class<V> resultType;

    private final ReadWriteLock lock = new ReentrantReadWriteLock(); // semaphore
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    public Database(String dbName, boolean isPrestartThreads, Class<V> resultType) {
        this.storage = new Storage(dbName);
        this.taskMap = new TaskMap<>(isPrestartThreads);
        this.resultType = resultType;
    }

    @Override
    public boolean open() {
        return this.storage.open();
    }

    @Override
    public boolean close() {
        return this.storage.close();
    }

    @Override
    public V get(K key) {
        readLock.lock();
        try {
            // find in task map
            PendingTask<V> pTask = this.taskMap.find(key);
            if (pTask != null) {
                if (pTask.getType() == TASK.PUT) {// if present
                    return pTask.getValue();
                } else { // if key deleted
                    return null;
                }
            }

            // find in disk
            byte[] value = this.storage.get(DeSerializer.serialize(key));

            if (value == null)
                return null;

            try {
                V result = this.resultType.getConstructor().newInstance();
                DeSerializer.deserialize(result, value);
                return result;
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean put(K key, V value) {
        writeLock.lock();
        try {
            AsyncTask<K> task = new PutTask<>(key, DeSerializer.serialize(key), DeSerializer.serialize(value), this.storage);
            return this.taskMap.put(key, value, TASK.PUT, task);
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public boolean remove(K key) {
        writeLock.lock();
        try {
            AsyncTask<K> task = new DeleteTask<>(key, DeSerializer.serialize(key), null, this.storage);
            return this.taskMap.put(key, null, TASK.DELETE, task);
        } finally {
            writeLock.unlock();
        }
    }
}
