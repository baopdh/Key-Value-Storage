/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver.cache;

import com.baopdh.dbserver.IService;
import com.baopdh.dbserver.util.ConfigGetter;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author cpu60019
 */
public class LRUCache<K, V> implements IService<K, V> {
    private class Node {
        K key;
        V value;
        Node next = null;
        Node prev = null;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Map<K, Node> hMap = new HashMap<>();
    private Node head = null; // least recently used
    private Node tail = null; // most recently used
    private int size;
    
    private final ReadWriteLock lock = new ReentrantReadWriteLock(); // semaphore
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    
    public LRUCache(int size) {
        if (size <= 0) // constraint size >= 1
            size = 1000;
        this.size = size;
    }
    
    private void addNode(Node node) {
        // add new node to map
        this.hMap.put(node.key, node);

        // add new node to tail
        if (this.tail == null) {
            this.head = node;
            this.tail = node;
        } else {
            node.prev = this.tail;
            this.tail.next = node;
            this.tail = node;
        }
        //-----------------------------

        --this.size;
    }
    
    private void deleteNode(Node node) {
        // delete node from map
        this.hMap.remove(node.key);

        // detach node from list
        if (node.prev != null)
            node.prev.next = node.next;
        else { // if node is head
            this.head = node.next;
            if (this.head == null)
                this.tail = null;
        }
        
        if (node.next != null)
            node.next.prev = node.prev;
        else { // if node is tail
            this.tail = node.prev;
            if (this.tail == null)
                this.head = null;
        }
        //-----------------------------
        
        ++this.size;
    }
    
    private synchronized void moveToTail(Node node) {
        // detach node from list
        if (node.next != null)
            node.next.prev = node.prev;
        else // if node is tail no need to move
            return;
        
        if (node.prev != null)
            node.prev.next = node.next;
        else // if node is head
            this.head = node.next;
        //-----------------------------
        
        this.tail.next = node;
        node.prev = this.tail;
        node.next = null;
        this.tail = node;
    }
    
    @Override
    public boolean put(K key, V value) {
        try {
            writeLock.lock();
            
            Node node = this.hMap.getOrDefault(key, null);

            if (node == null) { // if key not exist
                node = new Node(key, value);
                // remove least recently used item if no more capacity
                if (this.size == 0) {
                    this.deleteNode(this.head);
                }
                //----------------------------------------------------
                this.addNode(node);
            } else { // else
                node.value = value;
                this.moveToTail(node);
            }

            return true;
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public V get(K key) {
        try {
            readLock.lock();

            Node node = this.hMap.getOrDefault(key, null);

            if (node == null)
                return null;

            this.moveToTail(node);

            return node.value;
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public boolean remove(K key) {
        try {
            writeLock.lock();
            
            Node node = this.hMap.getOrDefault(key, null);
        
            if (node == null)
                return true;

            this.deleteNode(node);

            return true;
        } finally {
            writeLock.unlock();
        }
    }
}