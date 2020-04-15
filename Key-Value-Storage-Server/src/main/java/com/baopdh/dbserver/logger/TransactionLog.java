package com.baopdh.dbserver.logger;

import com.baopdh.dbserver.thrift.gen.Operation;
import com.baopdh.dbserver.util.ConfigGetter;
import com.baopdh.dbserver.util.Constant;
import com.baopdh.dbserver.util.DeSerializer;
import com.baopdh.dbserver.util.FileUtil;

import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.Semaphore;

public class TransactionLog {
    private static final int FILE_SIZE_LIMIT = ConfigGetter.getInt("database.log.file.max-size", 10000000);
    private static final String ID_FILE = "id.auto";

    private String dbName;
    private String directory;
    private long curFileSize = 0;
    private int curFileId = 0;
    private FileOutputStream out;
    private Scheduler scheduler;

    private final Semaphore mutex = new Semaphore(1);

    public  TransactionLog(String dbName) {
        this.dbName = dbName;
        this.scheduler = new Scheduler(24 * 60, 0, 5, 0, Scheduler.UNIT.MINTUE);
    }

    public boolean start() {
        this.startScheduler();

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

    public boolean commit(Operation operation) {
        mutex.acquireUninterruptibly();
        try {
            byte[] arr = DeSerializer.serialize(operation);

            if (arr.length + curFileSize > FILE_SIZE_LIMIT) {
                rollBySize();
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
        } finally {
            mutex.release();
        }

    }

    private boolean rollBySize() {
        return this.end() && this.increaseFileId() && this.openLogFile();
    }

    private boolean rollByDate() {
        mutex.acquireUninterruptibly();
        try {
            this.end();
            this.curFileId = 0;

            if (!this.makeDirectory(getFolderName())) {
                System.out.println("Roll by date failed");
                return false;
            }

            return this.openLogFile();
        } finally {
            mutex.release();
        }
    }

    private void startScheduler() {
        Runnable runnable = TransactionLog.this::rollByDate;
        scheduler.start(runnable);
    }

    private boolean makeDirectory(String folder) {
        this.directory = FileUtil.getDBUrl(this.dbName) + "logs" + Constant.FILE_URL_DELIMITER + folder;
        return FileUtil.makeDirIfNotExist(this.directory);
    }

    private boolean increaseFileId() {
        ++this.curFileId;

        try (RandomAccessFile file = new RandomAccessFile(this.directory + Constant.FILE_URL_DELIMITER + ID_FILE, "rw")) {
            file.seek(0);
            file.write(this.curFileId);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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