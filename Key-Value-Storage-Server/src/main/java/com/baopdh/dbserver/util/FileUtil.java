package com.baopdh.dbserver.util;

import java.io.File;

public class FileUtil {
    private FileUtil() {}

    public static boolean makeDirIfNotExist(String dirUrl) {
        File directory = new File(dirUrl);
        if (!directory.exists()){
            return directory.mkdirs();
        }

        return true;
    }

    public static boolean isExist(String fileUrl) {
        File file = new File(fileUrl);
        return file.exists();
    }

    public static String getDBUrl(String dbName) {
        String dir = System.getProperty("user.dir")
                + Constant.FILE_URL_DELIMITER
                + "database"
                + Constant.FILE_URL_DELIMITER
                + dbName
                + Constant.FILE_URL_DELIMITER;

        return dir;
    }
}
