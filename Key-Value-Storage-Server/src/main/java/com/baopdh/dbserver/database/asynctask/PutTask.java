/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;

import java.io.Serializable;

/**
 *
 * @author cpu60019
 */
public class PutTask<K extends Serializable, V extends Serializable> extends AsyncTask<K, V> {
    public PutTask(K key, V value, Storage<K, V> storage) {
        super(key, value, storage);
    }

    @Override
    public void run() {
        this.storage.put(key, value);
    }
}
