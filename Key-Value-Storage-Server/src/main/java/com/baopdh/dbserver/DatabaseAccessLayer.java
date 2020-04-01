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

import java.io.Serializable;

/**
 *
 * @author cpu60019
 */
public class DatabaseAccessLayer<K extends Serializable, V extends Serializable> implements IService<K, V> {
    private Cache<K, V> cache = new Cache<K, V>();
    private Database<K, V> database;
    private String dbName;
    private KeyGenerate keyGenerate;

    public DatabaseAccessLayer(String dbName, KeyGenerate.TYPE keyType) {
        this.database = new Database<K, V>(dbName, true);
        this.dbName = dbName;
        this.initKeyGen(keyType);
    }

    private void initKeyGen(KeyGenerate.TYPE keyType) {
        switch (keyType) {
            case INT:
                this.keyGenerate = new IntegerKeyGenerate(dbName);
                break;
        }
        if (!this.keyGenerate.initialize())
            System.out.println("Init keygen failed");
    }

    public boolean start() {
        return database.open();
    }

    public boolean stop() {
        keyGenerate.release();
        return database.close();
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null)
            return value;

        return database.get(key);
    }

    @SuppressWarnings("unchecked")
    public K put(V value) {
        Object key = this.keyGenerate.getNext();

        if (this.put((K) key, value))
            return (K) key;

        return null;
    }

    @Override
    public boolean put(K key, V value) {
        if (database.put(key, value)) {
            return cache.put(key, value);
        }

        return false;
    }
    
    @Override
    public boolean remove(K key) {
        if (database.remove(key)) {
            return cache.remove(key);
        }

        return false;
    }
}