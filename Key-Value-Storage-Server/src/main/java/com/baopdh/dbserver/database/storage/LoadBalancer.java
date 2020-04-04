/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import org.apache.commons.codec.digest.MurmurHash3;

/**
 *
 * @author cpu60019
 */
public class LoadBalancer {
    private int size;

    public LoadBalancer(int size) {
        this.size = size;
    }

    public int getDBIndex(byte[] key) {
        int res = 0;

        if (key != null)
            res = MurmurHash3.hash32x86(key);

        if (res < 0)
            res = -res;

        return res % this.size;
    }
}
