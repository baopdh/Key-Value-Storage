package com.baopdh.dbserver.util;

import com.baopdh.dbserver.Config;

public class ConfigGetter {
    private ConfigGetter() {}

    public static int getInt(String config, int defaultValue) {
        int res = defaultValue;

        String size = Config.getInstance().getProperty(config);
        if (size != null)
            res = Integer.parseInt(size);

        return res;
    }
}
