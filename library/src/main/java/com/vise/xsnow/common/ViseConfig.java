package com.vise.xsnow.common;

import com.vise.xsnow.loader.ILoader;

/**
 * @Description: 全局常量配置
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:23
 */
public class ViseConfig {
    public static final int IL_LOADING_RES = ILoader.Options.RES_NONE;//默认加载中的资源id
    public static final int IL_ERROR_RES = ILoader.Options.RES_NONE;//默认加载失败的资源id

    public static final String CACHE_SP_NAME = "sp_cache";//默认SharedPreferences缓存目录
    public static final String CACHE_DISK_DIR = "disk_cache";//默认磁盘缓存目录
    public static final String CACHE_HTTP_DIR = "http_cache";//默认HTTP缓存目录

    public static final int MAX_AGE_ONLINE = 60;//默认最大在线缓存时间（秒）
    public static final int MAX_AGE_OFFLINE = 24 * 60 * 60;//默认最大离线缓存时间（秒）

    public static String API_HOST = "https://api.github.com/";//默认API主机地址

    public static final String COOKIE_PREFS = "Cookies_Prefs";//默认Cookie缓存目录

    public static final int DEFAULT_TIMEOUT = 60;//默认超时时间
    public static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;//默认空闲连接数
    public static final long DEFAULT_KEEP_ALIVE_DURATION = 8;//默认心跳间隔时长
    public static final long CACHE_MAX_SIZE = 10 * 1024 * 1024;//默认最大缓存大小
}
