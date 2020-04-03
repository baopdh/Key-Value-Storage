/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.asynctask;

import com.baopdh.dbserver.database.storage.Storage;
import com.baopdh.dbserver.util.TransactionLog;
import org.apache.thrift.TBase;

import java.io.Serializable;

/**
 *
 * @author cpu60019
 */
public class DeleteTask<K, V extends Serializable & TBase<?,?>> extends AsyncTask<K, V> {
    public DeleteTask(K key, V value, Storage<K, V> storage, TransactionLog transactionLog) {
        super(key, value, storage, transactionLog);
    }

    @Override
    public void run() {
        this.storage.remove(this.key);
    }
}
