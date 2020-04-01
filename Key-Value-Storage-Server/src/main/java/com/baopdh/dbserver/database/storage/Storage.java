/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.database.IDatabase;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.util.Constant;
import com.baopdh.dbserver.util.FileUtil;

/**
 *
 * @author cpu60019
 */
public class Storage<K, V> implements IDatabase<K, V> {
    private static final String FILE_FORMAT = "node%d.kch";
    private static final int DEFAULT_NUM_CLUSTER = 3;

    private String dbName;
    private LoadBalancer<K> loadBalancer;
    private IDatabase<K, V>[] dbIstance;

    public Storage(String dbName) {
        this.dbName = dbName;
        this.init();
    }

    private void init() {
        int size = ConfigGetter.getInt("db.numcluster", DEFAULT_NUM_CLUSTER);

        this.dbIstance = new KCDB[size];
        this.loadBalancer = new LoadBalancer<>(size);
    }

    public boolean open() {
        String currentDir = FileUtil.getDBUrl(this.dbName);

        if (!FileUtil.makeDirIfNotExist(currentDir)) {
            System.out.println("Make database directory failed");
            return false;
        }

        for (int i = 0; i < this.dbIstance.length; ++i) {
            String nodeName = currentDir + String.format(FILE_FORMAT, i);
            this.dbIstance[i] = new KCDB<K, V>(nodeName);
            if (!this.dbIstance[i].open()) {
                System.out.println("Open db fail: " + nodeName);
                this.close();
                return false;
            }
        }

        return true;
    }

    public boolean close() {
        for (int i = 0; i < this.dbIstance.length; ++i) {
            if (this.dbIstance[i] != null)
                this.dbIstance[i].close();
        }

        return true;
    }

    @Override
    public V get(K key) {
        return this.dbIstance[loadBalancer.getDBIndex(key)].get(key);
    }
    
    @Override
    public boolean put(K key, V value) {
        return this.dbIstance[loadBalancer.getDBIndex(key)].put(key, value);
    }
    
    @Override
    public boolean remove(K key) {
        return this.dbIstance[loadBalancer.getDBIndex(key)].remove(key);
    }
}
