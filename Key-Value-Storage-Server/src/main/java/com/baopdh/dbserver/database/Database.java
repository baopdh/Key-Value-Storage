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
import com.baopdh.dbserver.database.threadpool.CommandThreadPoolExecutor;
import com.baopdh.dbserver.logger.TransactionLog;
import com.baopdh.dbserver.thrift.gen.TASK;
import com.baopdh.dbserver.util.*;
import org.apache.thrift.TBase;

import java.util.concurrent.*;

/**
 *
 * @author cpu60019
 */
public class Database<K, V extends TBase<?,?>> implements IDatabase<K, V> {
    public static final int MUTEX_SIZE = 128; //lock concurrent taskMap operations with the same key
    private static final int BLOCKING_QUEUE_SIZE = 10000;

    private Storage storage;
    private CommandThreadPoolExecutor<K, V> threadPoolExecutor;
    private TaskMap<K, V> taskMap;

    private final Semaphore[] mutex = new Semaphore[MUTEX_SIZE];
    private final MultipleReadWriteLog multipleReadWriteLog = new MultipleReadWriteLog();

    private TransactionLog transactionLog;
    private Class<V> resultType;

    public Database(String dbName, boolean isPrestartThreads, TransactionLog transactionLog, Class<V> resultType) {
        this.storage = new Storage(dbName);

        for (int i = 0; i < MUTEX_SIZE; ++i) {
            mutex[i] = new Semaphore(1);
        }

        this.taskMap = new TaskMap<>(this.mutex);

        this.initThreadPool(isPrestartThreads);
        this.threadPoolExecutor.setTaskMap(this.taskMap);

        this.transactionLog = transactionLog;

        this.resultType = resultType;
    }

    private void initThreadPool(boolean isPrestartThreads) {
        BlockingQueue<Runnable> blockingQueue =
                new ArrayBlockingQueue<>(ConfigGetter.getInt("db.blockingqueue.size", BLOCKING_QUEUE_SIZE));
        this.threadPoolExecutor = new CommandThreadPoolExecutor<K, V>(1, 1,
                ConfigGetter.getInt("db.pool.keepalive", 5000),
                TimeUnit.MILLISECONDS,
                blockingQueue);

        if (isPrestartThreads)
            this.threadPoolExecutor.prestartAllCoreThreads();
    }

    public static int getMutexIndex(Object key) {
        int index = Hasher.hashToInt(key) % MUTEX_SIZE;
        return index >= 0 ? index : -index;
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
        // multiple writes at the same time
        multipleReadWriteLog.lockRead();
        // --------------------------------
        try {
            // find in task map
            PendingTask<V> pTask = this.taskMap.find(key);
            if (pTask != null) {
                if (pTask.getType() == TASK.PUT) // if present
                    return pTask.getValue();
                if (pTask.getType() == TASK.DELETE) { // if key deleted
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
            multipleReadWriteLog.releaseRead();
        }
    }
    
    @Override
    public boolean put(K key, V value) {
        // multiple writes at the same time
        multipleReadWriteLog.lockWrite();
        // -----------------------------------

        // if many tasks with the same key enter at the same time,
        // this lock will prevent find operation return null many times
        // and ensure safe concurrent modifications
        int index = getMutexIndex(key);
        mutex[index].acquireUninterruptibly();
        // -----------------------------------
        try {
            taskMap.findAndUpdate(key, value, TASK.PUT);

            AsyncTask<K> task = new PutTask<>(key, DeSerializer.serialize(key), DeSerializer.serialize(value), this.storage);
            try {
                threadPoolExecutor.execute(task);
            } catch (RejectedExecutionException e) {
                taskMap.rollBack(key); // roll back
                return false;
            }

            return true;
        } finally {
            mutex[index].release();
            multipleReadWriteLog.releaseWrite();
        }
    }
    
    @Override
    public boolean remove(K key) {
        // likewise as put
        multipleReadWriteLog.lockWrite();

        int index = getMutexIndex(key);
        mutex[index].acquireUninterruptibly();
        // -----------------------------------
        try {
            taskMap.findAndUpdate(key, null, TASK.DELETE);

            AsyncTask<K> task = new DeleteTask<>(key, DeSerializer.serialize(key), null, this.storage);
            try {
                threadPoolExecutor.execute(task);
            } catch (RejectedExecutionException e) {
                taskMap.rollBack(key); // roll back
                return false;
            }

            return true;
        } finally {
            mutex[index].release();
            multipleReadWriteLog.releaseWrite();
        }
    }
}
