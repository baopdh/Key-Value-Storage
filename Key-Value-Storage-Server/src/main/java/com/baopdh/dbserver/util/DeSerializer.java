/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.util;

import org.apache.thrift.TSerializer;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;

/**
 *
 * @author cpu60019
 */
public class DeSerializer {
    private static final TSerializer serializer = new TSerializer();
    private static final TDeserializer deserializer = new TDeserializer();
    
    private DeSerializer() {}
    
    public static byte[] serialize(Object obj) {
        if (obj instanceof String) {
            return ((String)obj).getBytes();
        }

        if (obj instanceof Integer) {
            return ByteBuffer.allocate(4).putInt((int) obj).array();
        }

        if (obj instanceof TBase) {
            try {
                return serializer.serialize((TBase)obj);
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    
    public static void deserialize(Object obj, byte[] binary) {
        if (binary == null) {
            return;
        }

        if (obj instanceof String) {
            obj = new String(binary);
        } else if (obj instanceof Integer) {
            ByteBuffer.wrap(binary).getInt();
        } else if (obj instanceof TBase) {
            try {
                deserializer.deserialize((TBase) obj, binary);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }
}
