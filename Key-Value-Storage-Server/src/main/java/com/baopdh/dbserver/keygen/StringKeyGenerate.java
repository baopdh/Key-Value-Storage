package com.baopdh.dbserver.keygen;

import com.baopdh.dbserver.util.Constant;
import com.baopdh.dbserver.util.FileUtil;

import java.io.*;

public class StringKeyGenerate extends KeyGenerate<String>{
    private static final String KEY_FILE_NAME = "key.auto";

    private int currentKey;
    private RandomAccessFile fileWriter;

    public StringKeyGenerate(String dbName) {
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

    public synchronized String getNext() {
        ++currentKey;
        try {
            this.fileWriter.seek(0);
            this.fileWriter.writeInt(currentKey);
        } catch (IOException e) {
            e.printStackTrace();
            return "NULL";
        }

        return String.valueOf(currentKey);
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
            RandomAccessFile fileReader = new RandomAccessFile(fileUrl, "rw");
            try {
                this.currentKey = fileReader.readInt();
            } catch (EOFException e) {
                this.currentKey = 0;
                result = true;
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
            e.printStackTrace();
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
