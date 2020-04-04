/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;

/**
 *
 * @author cpu60019
 */
public class PutTask<K> extends AsyncTask<K> {
    public PutTask(K key, byte[] keyBytes, byte[] valueBytes, Storage storage) {
        super(key, keyBytes, valueBytes, storage);
    }

    @Override
    public void run() {
        this.storage.put(keyBytes, valueBytes);
    }
}
