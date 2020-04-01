package com.baopdh.dbserver.util;

import com.baopdh.dbserver.database.asynctask.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

class AppendingObjectOutputStream extends ObjectOutputStream {
    public AppendingObjectOutputStream(OutputStream outputStream) throws IOException {
        super(outputStream);
    }

    @Override
    protected void writeStreamHeader() throws IOException {}
}

public class TransactionLog {
    private String dbName;
    private ObjectOutputStream out;

    public  TransactionLog(String dbName) {
        this.dbName = dbName;
    }

    public boolean start() {
        String dirUrl = FileUtil.getDBUrl(this.dbName) + "logs";

        if (!FileUtil.makeDirIfNotExist(dirUrl))
            return false;

        boolean append = FileUtil.isExist(dirUrl + Constant.FILE_URL_DELIMITER + "transaction.log");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dirUrl
                    + Constant.FILE_URL_DELIMITER
                    + "transaction.log", append);
            if (append) {
                this.out = new AppendingObjectOutputStream(fileOutputStream);
            } else {
                this.out = new ObjectOutputStream(fileOutputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean end() {
        try {
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean commit(AsyncTask task) {
        try {
            this.out.writeObject(task);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
