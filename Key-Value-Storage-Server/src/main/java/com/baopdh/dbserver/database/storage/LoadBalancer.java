/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.database.storage;

import com.baopdh.dbserver.util.DeSerializer;
import org.apache.commons.codec.digest.MurmurHash3;

/**
 *
 * @author cpu60019
 */
public class LoadBalancer<K> {
    private int size;

    public LoadBalancer(int size) {
        this.size = size;
    }

    public int getDBIndex(K key) {
        byte[] bytes = DeSerializer.serialize(key);

        int res = 0;
        if (bytes != null)
            res = MurmurHash3.hash32x86(bytes);
        if (res < 0)
            res = -res;

        return res % this.size;
    }
}
