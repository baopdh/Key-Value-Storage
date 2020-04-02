/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver;

import com.baopdh.dbserver.keygen.KeyGenerate;
import com.baopdh.dbserver.thrift.gen.KVStoreService;
import com.baopdh.dbserver.thrift.gen.User;
import com.baopdh.dbserver.thrift.handler.KVStoreHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author cpu60019
 */
public class Main {
    public static KVStoreHandler kvStoreHandler;

    public static KVStoreService.Processor<?> processor;

    public static void main(String[] args) {
        kvStoreHandler = new KVStoreHandler();
        processor = new KVStoreService.Processor<>(kvStoreHandler);

        try {
            TServerTransport serverTransport = new TServerSocket(9090);
//            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
             TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        DatabaseAccessLayer<Integer, User> databaseAccessLayer =
//                new DatabaseAccessLayer<>("Test", KeyGenerate.TYPE.INT, User.class);
//
//        databaseAccessLayer.start();
//
//        final String put = "put";
//        final String get = "get";
//        final String delete = "delete";
//
//        int[] a = new int[]{1,2,3,2,4,4,6,6,8,4,1,1};
//        String[] action = new String[]{put, put, put, get, put, put, put, get, put, get, delete, get};
//
//        for (int i = 0; i < 3; ++i) {
//            switch (action[i]) {
//                case put:
//                    System.out.println("put " + a[i] + " " + databaseAccessLayer.put(new User()));
//                    break;
//                case get:
//                    System.out.println("get " + a[i] + " " + databaseAccessLayer.get(a[i]));
//                    break;
//                case delete:
//                    System.out.println("delete " + a[i] + " " + databaseAccessLayer.remove(a[i]));
//                    break;
//            }
//            System.out.println("---------------------------");
//        }
//
//        //wait 1s
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//        }
//
//        for (int i = 3; i < action.length; ++i) {
//            final int ind = i;
//            Runnable runnable = () -> {
//                switch (action[ind]) {
//                    case put:
//                        System.out.println("put " + a[ind] + " " + databaseAccessLayer.put(new User()));
//                        break;
//                    case get:
//                        System.out.println("get " + a[ind] + " " + databaseAccessLayer.get(a[ind]));
//                        break;
//                    case delete:
//                        System.out.println("delete " + a[ind] + " " + databaseAccessLayer.remove(a[ind]));
//                        break;
//                }
//            };
//            new Thread(runnable).start();
//        }
////        databaseAccessLayer.stop();
//    }
}
