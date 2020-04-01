package com.baopdh.dbclient;

import com.baopdh.dbclient.thrift.gen.KVStoreService;
import com.baopdh.dbclient.thrift.gen.User;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class KVStoreClient {
    private TTransport transport;
    private KVStoreService.Client client;

    public boolean open() {
        try {
            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            client = new KVStoreService.Client(protocol);
        } catch (TException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void ping() {
        try {
            client.ping();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        try {
            return client.remove(id);
        } catch (TException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int put(User user) {
        try {
            return client.put(user);
        } catch (TException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public User get(int id) {
        try {
            return client.get(id);
        } catch (TException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        if (transport != null)
            transport.close();
    }
}
