package com.baopdh.dbserver.database;

import com.baopdh.dbserver.IService;

public interface IDatabase<K, V> extends IService<K, V> {
    boolean open();
    boolean close();
}
