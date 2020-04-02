/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.util.DeSerializer;
import com.baopdh.dbserver.util.FileUtil;
import org.apache.thrift.TBase;

/**
 *
 * @author cpu60019
 */
public class Storage<K, V extends TBase<?,?>> {
    private static final String FILE_FORMAT = "node%d.kch";
    private static final int DEFAULT_NUM_CLUSTER = 3;

    private String dbName;
    private LoadBalancer<K> loadBalancer;
    private IStorage<K, V>[] dbInstance;

    public Storage(String dbName) {
        this.dbName = dbName;
        this.init();
    }

    private void init() {
        int size = ConfigGetter.getInt("db.numcluster", DEFAULT_NUM_CLUSTER);

        this.dbInstance = new KCDB[size];
        this.loadBalancer = new LoadBalancer<>(size);
    }

    public boolean open() {
        String currentDir = FileUtil.getDBUrl(this.dbName);

        if (!FileUtil.makeDirIfNotExist(currentDir)) {
            System.out.println("Make database directory failed");
            return false;
        }

        for (int i = 0; i < this.dbInstance.length; ++i) {
            String nodeName = currentDir + String.format(FILE_FORMAT, i);
            this.dbInstance[i] = new KCDB<K, V>(nodeName);
            if (!this.dbInstance[i].open()) {
                System.out.println("Open db fail: " + nodeName);
                this.close();
                return false;
            }
        }

        return true;
    }

    public boolean close() {
        for (IStorage<K, V> instance: this.dbInstance) {
            if (instance != null)
                instance.close();
        }

        return true;
    }

    public byte[] get(K key) {
        byte[] bytes = DeSerializer.serialize(key);
        return this.dbInstance[loadBalancer.getDBIndex(bytes)].get(bytes);
    }

    public boolean put(K key, V value) {
        byte[] bytes = DeSerializer.serialize(key);
        return this.dbInstance[loadBalancer.getDBIndex(bytes)].put(bytes, value);
    }

    public boolean remove(K key) {
        byte[] bytes = DeSerializer.serialize(key);
        return this.dbInstance[loadBalancer.getDBIndex(bytes)].remove(bytes);
    }
}
