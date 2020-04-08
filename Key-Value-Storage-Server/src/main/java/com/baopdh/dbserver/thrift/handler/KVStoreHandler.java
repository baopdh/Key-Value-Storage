package com.baopdh.dbserver.thrift.handler;

import com.baopdh.dbserver.DatabaseAccessLayer;
import com.baopdh.dbserver.keygen.KeyGenerate;
import com.baopdh.dbserver.profiler.ApiList;
import com.baopdh.dbserver.thrift.gen.*;
import org.apache.thrift.TException;

public class KVStoreHandler implements KVStoreService.Iface {
    private final DatabaseAccessLayer<Integer, User> databaseAccessLayer;
    private final ApiList apiList = ApiList.getInstance();

    public KVStoreHandler(String dbName) {
        databaseAccessLayer =
                new DatabaseAccessLayer<>(dbName, KeyGenerate.TYPE.INT, User.class);
        databaseAccessLayer.start();
    }

    @Override
    public void ping() {
        System.out.println("Ping");
    }

    @Override
    public User get(int id) {
        apiList.addPendingRequest(ApiList.API.GET);
        long start = System.currentTimeMillis();
        try {
            return databaseAccessLayer.get(id);
        } finally {
            apiList.saveNewRequest(ApiList.API.GET, System.currentTimeMillis() - start);
        }
    }

    @Override
    public boolean remove(int id) {
        apiList.addPendingRequest(ApiList.API.DELETE);
        long start = System.currentTimeMillis();
        try {
            return databaseAccessLayer.remove(id);
        } finally {
            apiList.saveNewRequest(ApiList.API.DELETE, System.currentTimeMillis() - start);
        }
    }

    @Override
    public boolean put(int id, User user) {
        apiList.addPendingRequest(ApiList.API.PUT);
        long start = System.currentTimeMillis();
        try {
            return databaseAccessLayer.put(id, user);
        } finally {
            apiList.saveNewRequest(ApiList.API.PUT, System.currentTimeMillis() - start);
        }
    }

    @Override
    public int getKey() {
        return databaseAccessLayer.getKey();
    }
}
