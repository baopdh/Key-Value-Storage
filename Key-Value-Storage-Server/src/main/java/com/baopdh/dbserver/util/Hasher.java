package com.baopdh.dbserver.util;


import org.apache.commons.codec.digest.MurmurHash3;

public class Hasher {
    public static  int hashToInt(Object obj) {
        if (obj instanceof Integer) {
            return MurmurHash3.hash32((Integer) obj);
        }

        return 0;
    }
}
