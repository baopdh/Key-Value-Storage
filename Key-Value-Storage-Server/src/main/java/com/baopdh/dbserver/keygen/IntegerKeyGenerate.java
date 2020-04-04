package com.baopdh.dbserver.keygen;

import com.baopdh.dbserver.util.Constant;
import com.baopdh.dbserver.util.FileUtil;

import java.io.*;
import java.util.concurrent.Semaphore;

public class IntegerKeyGenerate extends KeyGenerate<Integer>{
    private static final String KEY_FILE_NAME = "key.auto";

    private int currentKey;
    private RandomAccessFile fileWriter;

    public IntegerKeyGenerate(String dbName) {
        super(dbName);
    }

    public boolean initialize() {
        String dirUrl = FileUtil.getDBUrl(this.dbName) + "intkey" + Constant.FILE_URL_DELIMITER;

        if (!FileUtil.makeDirIfNotExist(dirUrl)) {
            System.out.println("Make directory failed: " + dirUrl);
            return false;
        }

        if (!this.readCurrentKey(dirUrl + KEY_FILE_NAME)) {
            System.out.println("Get current key failed");
            return false;
        }

        if (!this.openFileForWrite(dirUrl + KEY_FILE_NAME)) {
            System.out.println("Open key file failed");
            return false;
        }

        return true;
    }

    public synchronized Integer getNext() {
        ++currentKey;
        try {
            this.fileWriter.seek(0);
            this.fileWriter.write(currentKey);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return currentKey;
    }

    public void release() {
        try {
            this.fileWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean readCurrentKey(String fileUrl) {
        boolean result = true;
        try {
            InputStream input = new FileInputStream(fileUrl);
            DataInputStream fileReader = new DataInputStream(input);
            try {
                this.currentKey = fileReader.read();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            } finally {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        } catch (FileNotFoundException e) {
            this.currentKey = 0;
            result = true;
        }

        return result;
    }

    private boolean openFileForWrite(String fileUrl) {
        try {
            this.fileWriter = new RandomAccessFile(fileUrl, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
