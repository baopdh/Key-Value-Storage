package com.baopdh.dbserver.profiler;

import java.util.Iterator;

public class ApiList implements Iterable<ApiStat>{
    public enum API {
        PUT(0), GET(1), DELETE(2);
        public static final int length = API.values().length;

        private final int code;
        API(int code) {
            this.code = code;
        }
        public int getCode() {
            return this.code;
        }
    }

    ApiStat[] list = new ApiStat[API.length];

    private static ApiList instance;

    private ApiList() {
        list[API.PUT.getCode()] = new ApiStat("put(key, value)");
        list[API.GET.getCode()] = new ApiStat("get(key)");
        list[API.DELETE.getCode()] = new ApiStat("delete(key)");
    }

    public static ApiList getInstance() {
        if (instance == null) {
            instance = new ApiList();
        }

        return instance;
    }

    @Override
    public Iterator<ApiStat> iterator() {
        return new Iterator<ApiStat>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < API.length;
            }

            @Override
            public ApiStat next() {
                return list[i++];
            }
        };
    }
}
