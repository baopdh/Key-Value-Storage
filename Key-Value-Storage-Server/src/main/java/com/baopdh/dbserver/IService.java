/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver;

/**
 *
 * @author cpu60019
 */
public interface IService<K, V> {
    public V get(K key);
    public boolean put(K key, V value);
    public boolean remove(K key);
}
