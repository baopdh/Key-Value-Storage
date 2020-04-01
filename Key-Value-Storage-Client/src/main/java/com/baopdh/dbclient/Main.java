package com.baopdh.dbclient;

import com.baopdh.dbclient.thrift.gen.User;

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
                System.out.println("Client " + i + " put " + client.put(actions[i].user));
                break;
            case GET:
                User u = client.get(actions[i].id);
                System.out.println("Client " + i + " get " + actions[i].id + " " + (u != null ? u : ""));
                break;
            case DELETE:
                System.out.println("Client " + i + " delete " + actions[i].id + " " + client.delete(actions[i].id));
                break;
        }

        client.close();
    }

    public static void main(String[] args) {
        // put
        for (int i = 0; i < 3; ++i) {
            final int ind = i;
            Runnable r = () -> {
                run(ind);
            };
            new Thread(r).start();
        }

        // wait for put
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // get and delete
        for (int i = 3; i < actions.length; ++i) {
            final int ind = i;
            Runnable r = () -> {
                run(ind);
            };
            new Thread(r).start();
        }

    }
}
