package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.database.IDatabase;

public interface IStorage<K, V> extends IDatabase<K, V> {
    public byte[] get(byte[] key);
    public boolean put(byte[] key, V value);
    public boolean remove(byte[] key);
}
