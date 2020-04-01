package com.baopdh.dbserver.keygen;

public abstract class KeyGenerate<K> {
    public enum TYPE {
        INT, STRING
    }

    protected String dbName;

    public KeyGenerate(String dbName) {
        this.dbName = dbName;
    }

    public abstract boolean initialize();
    public abstract K getNext();
    public abstract void release();
}
