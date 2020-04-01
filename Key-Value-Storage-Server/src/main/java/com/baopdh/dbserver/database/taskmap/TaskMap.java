/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.taskmap;

import com.baopdh.dbserver.database.Database;
import com.baopdh.dbserver.util.Hasher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author cpu60019
 */
public class TaskMap<K, V> {
    private final Map<K, PendingTask<V>> tasks = new HashMap<K, PendingTask<V>>();
    private Semaphore[] mutex;

    public TaskMap(Semaphore[] mutex) {
        this.mutex = mutex;
    }

    public PendingTask<V> find(K key) {
        return this.tasks.get(key);
    }

    public void tryRemove(K key) {
        // if this thread satisfy remove condition but not execute remove yet
        // in the meantime a task with the same key update
        // os switch back and remove so use this lock
        int index = Database.getMutexIndex(key);
        mutex[index].acquireUninterruptibly();
        // -----------------------------------

        PendingTask<V> pTask = this.find(key);
        if (pTask == null) // if it has been removed, return (case: same key, task1 running, task2 come in)
            return;
        if (pTask.downCountAndGet() == 0) // if no task's come in, remove
            this.tasks.remove(key);

        mutex[index].release();
    }

    public void findAndUpdate(K key, V value, TASK type) {
        PendingTask<V> pTask = this.find(key);

        if (pTask != null) {
            pTask.updateTask(type, value);
            // put in case it has been removed after a running task with the same key
            this.tasks.put(key, pTask);
        } else {
            this.tasks.put(key, new PendingTask<V>(type, value));
        }
    }

    public void rollBack(K key) {
        PendingTask<V> pTask = this.find(key);

        if (pTask.rollback() == 0) {
            this.tasks.remove(key);
        }
    }
}
