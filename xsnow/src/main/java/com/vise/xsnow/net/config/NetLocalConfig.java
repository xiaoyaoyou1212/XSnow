package com.vise.xsnow.net.config;

import com.vise.xsnow.net.mode.CacheMode;
import com.vise.xsnow.net.mode.HttpHeaders;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @Description: 单个请求配置
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-05-04 15:42
 */
public class NetLocalConfig {
    private Map<String, String> params = new LinkedHashMap<>();//请求参数
    private HttpHeaders headers = new HttpHeaders();//请求头
    private int retryDelayMillis;//请求失败重试间隔时间
    private int retryCount;//重试次数
    private String baseUrl;//基础域名
    private String suffixUrl;//链接后缀
    private long readTimeOut;//读取超时时间
    private long writeTimeOut;//写入超时时间
    private long connectTimeOut;//连接超时时间
    private boolean isHttpCache;//是否使用Http缓存
    private boolean isLocalCache;//是否使用本地缓存
    private CacheMode cacheMode;//本地缓存类型
    private String cacheKey;//本地缓存Key
    private long cacheTime;//本地缓存时间

    /**
     * 设置自定义OkHttpClient
     *
     * @param client
     * @return
     */
    public NetLocalConfig client(OkHttpClient client) {
        return this;
    }

    /**
     * 添加请求参数
     * @param paramsKey
     * @param paramsValue
     * @return
     */
    public NetLocalConfig addParams(String paramsKey, String paramsValue) {
        this.params.put(paramsKey, paramsValue);
        return this;
    }

    /**
     * 移除请求参数
     * @param paramsKey
     * @return
     */
    public NetLocalConfig removeParams(String paramsKey) {
        this.params.remove(paramsKey);
        return this;
    }

    /**
     * 设置请求参数
     * @param params
     * @return
     */
    public NetLocalConfig params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    /**
     * 设置请求头
     * @param headers
     * @return
     */
    public NetLocalConfig headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 设置请求失败重试间隔时间（毫秒）
     * @param retryDelayMillis
     * @return
     */
    public NetLocalConfig retryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    /**
     * 设置请求失败重试次数
     * @param retryCount
     * @return
     */
    public NetLocalConfig retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 设置基础域名，当前请求会替换全局域名
     * @param baseUrl
     * @return
     */
    public NetLocalConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * 设置请求链接后缀
     * @param suffixUrl
     * @return
     */
    public NetLocalConfig suffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
        return this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param connectTimeOut
     * @return
     */
    public NetLocalConfig connectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return this;
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param readTimeOut
     * @return
     */
    public NetLocalConfig readTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param writeTimeOut
     * @return
     */
    public NetLocalConfig writeTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    /**
     * 设置是否进行HTTP缓存
     * @param isHttpCache
     * @return
     */
    public NetLocalConfig isHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return this;
    }

    /**
     * 设置是否进行本地缓存
     * @param isLocalCache
     * @return
     */
    public NetLocalConfig isLocalCache(boolean isLocalCache) {
        this.isLocalCache = isLocalCache;
        return this;
    }

    /**
     * 设置本地缓存类型
     * @param cacheMode
     * @return
     */
    public NetLocalConfig cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    /**
     * 设置本地缓存Key
     *
     * @param cacheKey
     * @return
     */
    public NetLocalConfig cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    /**
     * 设置本地缓存时间，默认永久
     *
     * @param cacheTime
     * @return
     */
    public NetLocalConfig cacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public int getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSuffixUrl() {
        return suffixUrl;
    }

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public long getWriteTimeOut() {
        return writeTimeOut;
    }

    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    public boolean isHttpCache() {
        return isHttpCache;
    }

    public boolean isLocalCache() {
        return isLocalCache;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    private <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }
}
