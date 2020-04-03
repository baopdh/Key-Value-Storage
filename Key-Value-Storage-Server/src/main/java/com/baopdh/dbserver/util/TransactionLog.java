package com.baopdh.dbserver.util;

import com.baopdh.dbserver.thrift.gen.Task;

import java.io.*;
import java.time.LocalDate;

public class TransactionLog {
    private static final int MAX_FILE_SIZE = 1000000;
    private static final String ID_FILE = "id.auto";

    private final int FILE_SIZE_LIMIT = ConfigGetter.getInt("database.log.file.max-size", MAX_FILE_SIZE);
    private String dbName;
    private String directory;
    private long curFileSize = 0;
    private int curFileId = 0;
    private FileOutputStream out;

    public  TransactionLog(String dbName) {
        this.dbName = dbName;
    }

    public boolean start() {
        if (!this.makeDirectory(this.getFolderName()))
            return false;

        if (!this.getCurrentFileId(this.directory + Constant.FILE_URL_DELIMITER + ID_FILE))
            return false;

        return this.openLogFile();
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

    public synchronized boolean commit(Task task) {
        byte[] arr = DeSerializer.serialize(task);

        if (arr.length + curFileSize > FILE_SIZE_LIMIT) {
            this.end();
            this.increaseFileId(this.directory + Constant.FILE_URL_DELIMITER + ID_FILE);
            this.openLogFile();
        }

        try {
            curFileSize += arr.length;
            this.out.write(DeSerializer.serialize(arr.length));
            this.out.write(arr);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean makeDirectory(String folder) {
        this.directory = FileUtil.getDBUrl(this.dbName) + "logs" + Constant.FILE_URL_DELIMITER + folder;
        return FileUtil.makeDirIfNotExist(this.directory);
    }

    private void increaseFileId(String fileUrl) {
        ++this.curFileId;

        try (RandomAccessFile file = new RandomAccessFile(fileUrl, "rw")) {
            file.seek(0);
            file.write(this.curFileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean openLogFile() {
        try {
            File logFile = new File(this.directory + Constant.FILE_URL_DELIMITER
                    + String.format("transaction%d.log", this.curFileId));
            this.curFileSize = logFile.length();
            out = new FileOutputStream(logFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getFolderName() {
        return LocalDate.now().toString();
    }

    private boolean getCurrentFileId(String fileUrl) {
        boolean result = true;
        try {
            InputStream input = new FileInputStream(fileUrl);
            DataInputStream fileReader = new DataInputStream(input);
            try {
                this.curFileId = fileReader.read();
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
            this.curFileId = 0;
            result = true;
        }

        return result;
    }
}

// This class is used for appending serialized object to an existing file, but no need anymore, just leave it here
//class AppendingObjectOutputStream extends ObjectOutputStream {
//    public AppendingObjectOutputStream(OutputStream outputStream) throws IOException {
//        super(outputStream);
//    }
//
//    @Override
//    protected void writeStreamHeader() throws IOException {}
//}