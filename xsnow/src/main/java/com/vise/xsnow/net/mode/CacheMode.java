package com.vise.xsnow.net.mode;

/**
 * @Description: 缓存模式
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 14:22.
 */
public enum CacheMode {
    /**
     * 先请求网络，请求网络失败后再加载缓存
     */
    FIRST_REMOTE("FirstRemoteStrategy"),

    /**
     * 先加载缓存，缓存没有再去请求网络
     */
    FIRST_CACHE("FirstCacheStrategy"),

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    ONLY_REMOTE("OnlyRemoteStrategy"),

    /**
     * 只读取缓存
     */
    ONLY_CACHE("OnlyCacheStrategy"),

    /**
     * 先使用缓存，不管是否存在，仍然请求网络，会回调两次
     */
    CACHE_AND_REMOTE("CacheAndRemoteStrategy");

    private final String className;

    CacheMode(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
