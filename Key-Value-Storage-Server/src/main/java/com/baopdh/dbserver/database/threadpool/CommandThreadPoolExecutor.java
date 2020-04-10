/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.threadpool;

import com.baopdh.dbserver.database.asynctask.AsyncTask;
import com.baopdh.dbserver.database.taskmap.TaskMap;
import org.apache.thrift.TBase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cpu60019
 */
public class CommandThreadPoolExecutor<K, V extends TBase<?,?>> extends ThreadPoolExecutor {
    public CommandThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (t != null) { // handle failed execution here
            System.out.println("Worker failed " + ((AsyncTask<K>) r).getKey());
        }
    }
}
