/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import kyotocabinet.*;

/**
 *
 * @author cpu60019
 */
public class KCDB implements IStorage {
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

    public byte[] get(byte[] key) {
        return db.get(key);
    }

    public boolean put(byte[] key, byte[] value) {
        return db.set(key, value);
    }

    public boolean remove(byte[] key) {
        return db.remove(key);
    }
}
