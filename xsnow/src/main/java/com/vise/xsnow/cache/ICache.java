package com.vise.xsnow.cache;

/**
 * @Description: 缓存接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-12-19 15:03
 */
public interface ICache {
    void put(String key, Object value);

    Object get(String key);

    boolean contains(String key);

    void remove(String key);

    void clear();
}
