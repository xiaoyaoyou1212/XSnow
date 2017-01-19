package com.vise.xsnow.net.mode;

import java.io.Serializable;

/**
 * @Description: 设置缓存后的数据返回结果，主要增加是否是缓存数据的区分
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 14:25.
 */
public class CacheResult<T> implements Serializable {
    private boolean isCache;
    private T cacheData;

    public CacheResult(boolean isCache, T cacheData) {
        this.isCache = isCache;
        this.cacheData = cacheData;
    }

    public boolean isCache() {
        return isCache;
    }

    public CacheResult setCache(boolean cache) {
        isCache = cache;
        return this;
    }

    public T getCacheData() {
        return cacheData;
    }

    public CacheResult setCacheData(T cacheData) {
        this.cacheData = cacheData;
        return this;
    }

    @Override
    public String toString() {
        return "CacheResult{" +
                "isCache=" + isCache +
                ", cacheData=" + cacheData +
                '}';
    }
}
