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
import com.baopdh.dbserver.thrift.gen.TASK;
import com.baopdh.dbserver.thrift.gen.Task;
import com.baopdh.dbserver.thrift.gen.User;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.logger.TransactionLog;
import com.baopdh.dbserver.util.DeSerializer;
import org.apache.thrift.TBase;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cpu60019
 */
public class DatabaseAccessLayer<K, V extends TBase<?,?>> implements IService<K, V> {
    private Cache<K, V> cache;
    private Database<K, V> database;
    private String dbName;
    private KeyGenerate<?> keyGenerate;
    private int retryTime, retryDelay;
    private TransactionLog transactionLog;

    public DatabaseAccessLayer(String dbName, KeyGenerate.TYPE keyType, Class<V> resultType) {
        this.retryTime = ConfigGetter.getInt("db.retry.time", 3);
        this.retryDelay = ConfigGetter.getInt("db.retry.delay", 1);
        this.dbName = dbName;
        this.transactionLog = new TransactionLog(dbName);
        this.database = new Database<K, V>(dbName, true, transactionLog, resultType);
        this.cache = new Cache<>();
        this.initKeyGen(keyType);
    }

    private void initKeyGen(KeyGenerate.TYPE keyType) {
        switch (keyType) {
            case INT:
                this.keyGenerate = new IntegerKeyGenerate(dbName);
                break;
            case STRING:
                // init string key generate here
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
        for (int i = 0; i < this.retryTime; ++i) {
            if (database.put(key, value)) {
                this.transactionLog.commit(
                        new Task(ByteBuffer.wrap(DeSerializer.serialize(key)), ByteBuffer.wrap(DeSerializer.serialize(value)), TASK.PUT));
                return cache.put(key, value);
            }

            try {
                TimeUnit.SECONDS.sleep(this.retryDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        this.transactionLog.commit(
                new Task(ByteBuffer.wrap(DeSerializer.serialize(key)), ByteBuffer.wrap(DeSerializer.serialize(value)), TASK.WARNING));
        return false;
    }
    
    @Override
    public boolean remove(K key) {
        for (int i = 0; i < this.retryTime; ++i) {
            if (database.remove(key)) {
                this.transactionLog.commit(new Task(ByteBuffer.wrap(DeSerializer.serialize(key)), null, TASK.DELETE));
                return cache.remove(key);
            }

            try {
                TimeUnit.SECONDS.sleep(this.retryDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        this.transactionLog.commit(new Task(ByteBuffer.wrap(DeSerializer.serialize(key)), null, TASK.WARNING));
        return false;
    }
}