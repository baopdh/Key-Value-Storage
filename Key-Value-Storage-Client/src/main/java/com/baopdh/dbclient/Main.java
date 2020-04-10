package com.baopdh.dbclient;

import com.baopdh.dbclient.thrift.gen.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

class Action {
    enum TYPE {
        PUT, GET, DELETE
    }

    int id;
    TYPE type;
    User user;

    Action(int id, String name, String email, String phone, TYPE type) {
        this.id = id;
        this.type = type;
        this.user = new User(name, email, phone);
    }
}


public class Main {
    private static final String PHONE_NUMBER = "0123456789";

    public static void main(String[] args) {
        Runnable r = () -> {
            KVStoreClient client = new KVStoreClient();
            if (!client.open()) {
                System.out.println("open client failed ");
                return;
            }

            while (true) {
//            for (int i = 0; i < 10; ++i) {
                int key = new Random().nextInt(85262) + 1;
                try {
                    User u = client.get(key);
//                    System.out.println("Get " + key + " " + (u != null ? u : ""));
//                    Thread.sleep(500);
                } catch (Exception e) {
                    System.err.println("Main :" + e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        Thread t2 = new Thread(r);
        t.start();
        t2.start();
    }

    public static void main1(String[] args) {
        Runnable r = () -> {
            KVStoreClient client = new KVStoreClient();
            if (!client.open()) {
                System.out.println("open client failed ");
                return;
            }

            int i = 1000;
            while (true) {
                String name = RandomStringUtils.randomAlphabetic(i);
                String email = RandomStringUtils.randomAlphabetic(i);
                User user = new User(name, email, PHONE_NUMBER);
                int key = client.getKey();
                client.put(key, user);
                if (++i > 1025) {
                    i = 1000;
                }
            }
        };

        Thread t = new Thread(r);
        Thread t2 = new Thread(r);
        t.start();
        t2.start();
    }

    private static final Action[] actions = new Action[]{
            new Action(1, "a", "a", "a", Action.TYPE.PUT),
            new Action(2, "b", "b", "b", Action.TYPE.PUT),
            new Action(3, "c", "c", "c", Action.TYPE.PUT),
            new Action(1, "c", "c", "c", Action.TYPE.GET),
            new Action(2, "d", "d", "d", Action.TYPE.GET),
            new Action(2, "d", "d", "d", Action.TYPE.GET),
            new Action(2, "d", "d", "d", Action.TYPE.GET),
            new Action(1, "d", "d", "d", Action.TYPE.DELETE),
            new Action(1, "d", "d", "d", Action.TYPE.GET),
    };

    private static void run(int i) {
        KVStoreClient client = new KVStoreClient();
        if (!client.open()) {
            System.out.println("open client failed " + i);
            return;
        }

        switch (actions[i].type) {
            case PUT:
                int key = client.getKey();
                System.out.println("Client " + i + " put " + client.put(key, actions[i].user));
                break;
            case GET:
                try {
                    User u = client.get(actions[i].id);
                    System.out.println("Client " + i + " get " + actions[i].id + " " + (u != null ? u : ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case DELETE:
                System.out.println("Client " + i + " delete " + actions[i].id + " " + client.delete(actions[i].id));
                break;
        }

        client.close();
    }

//    public static void main(String[] args) {
        // put
//        for (int i = 0; i < 3; ++i) {
//            final int ind = i;
//            Runnable r = () -> {
//                run(ind);
//            };
//            new Thread(r).start();
//        }
//
//        // wait for put
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            System.out.println(e.getMessage());
//        }
//
//        // get and delete
//        for (int i = 3; i < actions.length; ++i) {
//            final int ind = i;
//            Runnable r = () -> {
//                run(ind);
//            };
//            new Thread(r).start();
//        }
//    }
}
