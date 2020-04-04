/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baopdh.dbserver;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author cpu60019
 */
public class Config {
    private static final String FILE_CONFIG = "/resources/config.properties";
    private final Properties properties = new Properties();
    private static Config instance = null;

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
            instance.readConfig();
        }
        
        return instance;
    }
 
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    private void readConfig() {
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
