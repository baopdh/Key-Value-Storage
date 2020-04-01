/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.database.IDatabase;
import kyotocabinet.*;
import com.baopdh.dbserver.util.DeSerializer;

/**
 *
 * @author cpu60019
 */
public class KCDB<K, V> implements IDatabase<K, V> {
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
    @SuppressWarnings("unchecked")
    public V get(K key) {
        byte[] arr = db.get(DeSerializer.serialize(key));

        Object value = null;
        DeSerializer.deserialize(value, arr);

        return (V) value;
    }
    
    @Override
    public boolean put(K key, V value) {
        return db.set(DeSerializer.serialize(key), DeSerializer.serialize(value));
    }
    
    @Override
    public boolean remove(K key) {
        return db.remove(DeSerializer.serialize(key));
    }
}
