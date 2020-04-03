/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import kyotocabinet.*;
import com.baopdh.dbserver.util.DeSerializer;
import org.apache.thrift.TBase;

/**
 *
 * @author cpu60019
 */
public class KCDB<K, V extends TBase<?,?>> implements IStorage<K, V> {
    private final String dbName;
    private final DB db = new DB();

    public KCDB(String dbName) {
        this.dbName = dbName;
    }
    
    public boolean open() {
        return this.db.open(this.dbName, DB.OWRITER | DB.OCREATE | DB.OREADER);
    }
    
    public boolean close() {
        return this.db.close();
    }
    
    @Override
    public V get(K key) { // Can't use this interface
        return null;
    }

    public byte[] get(byte[] key) {
        return db.get(key);
    }
    
    @Override
    public boolean put(K key, V value) {
        return db.set(DeSerializer.serialize(key), DeSerializer.serialize(value));
    }

    public boolean put(byte[] key, V value) {
        return db.set(key, DeSerializer.serialize(value));
    }
    
    @Override
    public boolean remove(K key) {
        return db.remove(DeSerializer.serialize(key));
    }

    public boolean remove(byte[] key) {
        return db.remove(key);
    }
}
