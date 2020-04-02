package com.baopdh.dbserver.thrift.handler;

import com.baopdh.dbserver.DatabaseAccessLayer;
import com.baopdh.dbserver.keygen.KeyGenerate;
import com.baopdh.dbserver.thrift.gen.*;
import org.apache.thrift.TException;

public class KVStoreHandler implements KVStoreService.Iface {
    private DatabaseAccessLayer<Integer, User> databaseAccessLayer;

    public KVStoreHandler() {
        databaseAccessLayer =
                new DatabaseAccessLayer<>("Test", KeyGenerate.TYPE.INT, User.class);
        databaseAccessLayer.start();
    }

    @Override
    public void ping() throws TException {
        System.out.println("Ping");
    }

    @Override
    public User get(int id) throws TException {
        return databaseAccessLayer.get(id);
    }

    @Override
    public boolean remove(int id) throws TException {
        return databaseAccessLayer.remove(id);
    }

    @Override
    public int put(User user) throws TException {
        return databaseAccessLayer.put(user);
    }
}
