/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver;

import com.baopdh.dbserver.cache.Cache;
import com.baopdh.dbserver.database.Database;
import com.baopdh.dbserver.keygen.IntegerKeyGenerate;
import com.baopdh.dbserver.keygen.KeyGenerate;
import com.baopdh.dbserver.keygen.StringKeyGenerate;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.logger.TransactionLog;
import com.baopdh.dbserver.util.DeSerializer;
import com.baopdh.thrift.gen.Operation;
import com.baopdh.thrift.gen.TASK;
import org.apache.thrift.TBase;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cpu60019
 */
public class DatabaseAccessLayer<K, V extends TBase<?,?>> implements IService<K, V> {
    private final Cache<K, V> cache;
    private final Database<K, V> database;
    private final String dbName;
    private KeyGenerate<?> keyGenerate;
    private final int retryTime, retryDelay;
    private final TransactionLog transactionLog;

    private final Semaphore mutex = new Semaphore(1); // use to ensure write order in many layers

    public DatabaseAccessLayer(String dbName, KeyGenerate.TYPE keyType, Class<V> resultType) {
        this.retryTime = ConfigGetter.getInt("db.retry.time", 3);
        this.retryDelay = ConfigGetter.getInt("db.retry.delay", 1);
        this.dbName = dbName;
        this.transactionLog = new TransactionLog(dbName);
        this.database = new Database<K, V>(dbName, true, resultType);
        this.cache = new Cache<>();
        this.initKeyGen(keyType);
    }

    private void initKeyGen(KeyGenerate.TYPE keyType) {
        switch (keyType) {
            case INT:
                this.keyGenerate = new IntegerKeyGenerate(dbName);
                break;
            case STRING:
                this.keyGenerate = new StringKeyGenerate(dbName);
                break;
        }

        if (this.keyGenerate == null || !this.keyGenerate.initialize())
            System.out.println("Init keygen failed");
    }

    public boolean start() {
        return database.open() && this.transactionLog.start();
    }

    public boolean stop() {
        keyGenerate.release();
        return database.close() && this.transactionLog.end();
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null)
            return value;

        return database.get(key);
    }

    @SuppressWarnings("unchecked")
    public K getKey() {
        return (K) this.keyGenerate.getNext();
    }

    @Override
    public boolean put(K key, V value) {
        for (int i = 0; i < this.retryTime; ++i) { // do retryTime times
            mutex.acquireUninterruptibly();
            try {
                if (database.put(key, value)) { // if action success, put cache, return
                    this.transactionLog.commit(
                            new Operation(ByteBuffer.wrap(DeSerializer.serialize(key)), ByteBuffer.wrap(DeSerializer.serialize(value)), TASK.PUT));
                    return cache.put(key, value);
                } else { // else sleep if has more retry times, otherwise commit a warning
                    if (i < this.retryTime - 1) {
                        try {
                            TimeUnit.SECONDS.sleep(this.retryDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.transactionLog.commit(
                                new Operation(ByteBuffer.wrap(DeSerializer.serialize(key)), ByteBuffer.wrap(DeSerializer.serialize(value)), TASK.WARNING));
                        System.out.println("Denied task " + key);
                    }
                }
            } finally {
                mutex.release();
            }
        }

        return false;
    }
    
    @Override
    public boolean remove(K key) {
        for (int i = 0; i < this.retryTime; ++i) { // do retryTime times
            mutex.acquireUninterruptibly();
            try {
                if (database.remove(key)) { // if action success, put cache, return
                    this.transactionLog.commit(new Operation(ByteBuffer.wrap(DeSerializer.serialize(key)), null, TASK.DELETE));
                    return cache.remove(key);
                } else { // else sleep if has more retry times, otherwise commit a warning
                    if (i < this.retryTime - 1) {
                        try {
                            TimeUnit.SECONDS.sleep(this.retryDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.transactionLog.commit(new Operation(ByteBuffer.wrap(DeSerializer.serialize(key)), null, TASK.WARNING));
                        System.out.println("Denied task " + key);
                    }
                }
            } finally {
                mutex.release();
            }
        }

        return false;
    }
}