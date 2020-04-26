/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver;

import com.baopdh.dbserver.keygen.KeyGenerate;
import com.baopdh.dbserver.profiler.ProfilerServer;
import com.baopdh.dbserver.thrift.handler.KVStoreHandler;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.thrift.gen.KVStoreService;
import com.baopdh.thrift.gen.KVStoreService.Iface;
import com.baopdh.thrift.gen.User;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.concurrent.TimeUnit;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

/**
 *
 * @author cpu60019
 */
public class Main {
    public static KVStoreHandler kvStoreHandler;
    public static KVStoreService.Processor<?> processor;

    public static void main(String[] args) {
        // start http profiler server
        ProfilerServer profilerServer = new ProfilerServer();
        System.out.println("Starting profiler server");
        if (!profilerServer.start())
            System.out.println("Profiler server failed to start");
        else
            System.out.println("Profiler server is running");

        // start thrift database server
        try {
            kvStoreHandler = new KVStoreHandler(ConfigGetter.get("db.name", "Storage"));
            processor = new KVStoreService.Processor<>(kvStoreHandler);
            
//            TServerTransport serverTransport = new TServerSocket(9090);
            TNonblockingServerTransport serverTransport =
                    new TNonblockingServerSocket(ConfigGetter.getInt("db.port", 9090));

//             TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the database server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error staIface rting database server: " + e.getMessage());
        }
    }

    public static void main1(String[] args) {
        DatabaseAccessLayer<Integer, User> databaseAccessLayer =
                new DatabaseAccessLayer<>("Test1", KeyGenerate.TYPE.INT, User.class);

        databaseAccessLayer.start();

        final String put = "put";
        final String get = "get";
        final String delete = "delete";

        int[] a = new int[]{1,2,3,2,4,4,6,6,8,4,1,1};
        String[] action = new String[]{put, put, put, get, put, put, put, get, put, get, delete, get};
//        String[] action = new String[]{get, get, get, get, get, get, get, get, get, get, get, get};
        for (int i = 0; i < 3; ++i) {
            switch (action[i]) {
                case put:
                    int key = databaseAccessLayer.getKey();
                    System.out.println("put " + a[i] + " " + databaseAccessLayer.put(key, new User()));
                    break;
                case get:
                    System.out.println("get " + a[i] + " " + databaseAccessLayer.get(a[i]));
                    break;
                case delete:
                    System.out.println("delete " + a[i] + " " + databaseAccessLayer.remove(a[i]));
                    break;
            }
            System.out.println("---------------------------");
        }

        //wait 1 second
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        for (int i = 3; i < action.length; ++i) {
            final int ind = i;
            Runnable runnable = () -> {
                switch (action[ind]) {
                    case put:
                        int key = databaseAccessLayer.getKey();
                        System.out.println("put " + a[ind] + " " + databaseAccessLayer.put(key, new User()));
                        break;
                    case get:
                        System.out.println("get " + a[ind] + " " + databaseAccessLayer.get(a[ind]));
                        break;
                    case delete:
                        System.out.println("delete " + a[ind] + " " + databaseAccessLayer.remove(a[ind]));
                        break;
                }
            };
            new Thread(runnable).start();
        }
//        databaseAccessLayer.stop();
    }
}
