package com.baopdh.dbserver.database.storage;

public interface IStorage {
    byte[] get(byte[] key);
    boolean put(byte[] key, byte[] value);
    boolean remove(byte[] key);
    boolean open();
    boolean close();
}
