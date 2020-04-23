/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.taskmap;

import com.baopdh.dbserver.cache.LRUCache;
import com.baopdh.dbserver.database.asynctask.AsyncTask;
import com.baopdh.dbserver.database.threadpool.CommandThreadPoolExecutor;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.thrift.gen.TASK;
import org.apache.thrift.TBase;

import java.util.concurrent.*;

/**
 *
 * @author cpu60019
 */
public class TaskMap<K, V extends TBase<?,?>> extends LRUCache<K, PendingTask<V>> {
    private static final int BLOCKING_QUEUE_SIZE = 10000;

    private CommandThreadPoolExecutor<K, V> threadPoolExecutor;

    public TaskMap(boolean isPrestartThreads) {
        super(BLOCKING_QUEUE_SIZE);
        this.initThreadPool(isPrestartThreads);
    }

    public PendingTask<V> find(K key) {
        return super.get(key);
    }

    public boolean put(K key, V value, TASK type, AsyncTask<K> task) {
        try {
            threadPoolExecutor.execute(task);
            super.put(key, new PendingTask<>(type, value));
        } catch (RejectedExecutionException e) {
            return false;
        }

        return true;
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
}
