/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.util.FileUtil;

/**
 *
 * @author cpu60019
 */
public class Storage implements IStorage {
    private static final String FILE_FORMAT = "node%d.kch";
    private static final int DEFAULT_NUM_CLUSTER = 5;

    private String dbName;
    private LoadBalancer loadBalancer;
    private IStorage[] dbInstance;

    public Storage(String dbName) {
        this.dbName = dbName;
        this.init();
    }

    private void init() {
        int size = ConfigGetter.getInt("db.numcluster", DEFAULT_NUM_CLUSTER);

        this.dbInstance = new KCDB[size];
        this.loadBalancer = new LoadBalancer(size);
    }

    @Override
    public boolean open() {
        String currentDir = FileUtil.getDBUrl(this.dbName);

        if (!FileUtil.makeDirIfNotExist(currentDir)) {
            System.out.println("Make database directory failed");
            return false;
        }

        for (int i = 0; i < this.dbInstance.length; ++i) {
            String nodeName = currentDir + String.format(FILE_FORMAT, i);
            this.dbInstance[i] = new KCDB(nodeName);
            if (!this.dbInstance[i].open()) {
                System.out.println("Open db fail: " + nodeName);
                this.close();
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean close() {
        for (IStorage instance: this.dbInstance) {
            if (instance != null)
                instance.close();
        }

        return true;
    }

    @Override
    public byte[] get(byte[] key) {
        return this.dbInstance[loadBalancer.getDBIndex(key)].get(key);
    }

    @Override
    public boolean put(byte[] key, byte[] value) {
        return this.dbInstance[loadBalancer.getDBIndex(key)].put(key, value);
    }

    @Override
    public boolean remove(byte[] key) {
        return this.dbInstance[loadBalancer.getDBIndex(key)].remove(key);
    }
}
