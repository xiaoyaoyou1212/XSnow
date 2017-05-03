package com.vise.xsnow.net.config;

import com.vise.xsnow.cache.DiskCache;
import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.core.ApiCookie;
import com.vise.xsnow.net.interceptor.GzipRequestInterceptor;
import com.vise.xsnow.net.interceptor.HeadersInterceptor;
import com.vise.xsnow.net.interceptor.OfflineCacheInterceptor;
import com.vise.xsnow.net.interceptor.OnlineCacheInterceptor;

import java.io.File;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;

/**
 * @Description: 请求全局配置
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 17:17
 */
public class NetConfig {
    private CallAdapter.Factory callAdapterFactory;//Call适配器工厂
    private Converter.Factory converterFactory;//转换工厂
    private okhttp3.Call.Factory callFactory;//Call工厂
    private SSLSocketFactory sslSocketFactory;//SSL工厂
    private HostnameVerifier hostnameVerifier;//主机域名验证
    private ConnectionPool connectionPool;//连接池
    private File httpCacheDirectory;//Http缓存路径
    private Boolean isHttpCache;//是否使用Http缓存
    private ApiCookie apiCookie;//Cookie配置
    private Boolean isCookie;//是否使用Cookie
    private String baseUrl;//基础域名
    private Cache httpCache;//Http缓存对象

    private static NetConfig sNetConfig;
    private NetConfig() {
    }

    public static NetConfig getInstance() {
        if (sNetConfig == null) {
            synchronized (NetConfig.class) {
                if (sNetConfig == null) {
                    sNetConfig = new NetConfig();
                }
            }
        }
        return sNetConfig;
    }

    /**
     * 设置自定义OkHttpClient
     *
     * @param client
     * @return
     */
    public NetConfig client(OkHttpClient client) {
        ViseNet.getRetrofitBuilder().client(checkNotNull(client, "client == null"));
        return this;
    }

    /**
     * 设置Call的工厂
     *
     * @param factory
     * @return
     */
    public NetConfig callFactory(okhttp3.Call.Factory factory) {
        this.callFactory = checkNotNull(factory, "factory == null");
        return this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public NetConfig connectTimeout(int timeout) {
        return connectTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public NetConfig readTimeout(int timeout) {
        return readTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public NetConfig writeTimeout(int timeout) {
        return writeTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置是否添加Cookie
     *
     * @param isCookie
     * @return
     */
    public NetConfig cookie(boolean isCookie) {
        this.isCookie = isCookie;
        return this;
    }

    /**
     * 设置是否添加缓存
     *
     * @param isHttpCache
     * @return
     */
    public NetConfig cache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return this;
    }

    /**
     * 设置代理
     *
     * @param proxy
     * @return
     */
    public NetConfig proxy(Proxy proxy) {
        ViseNet.getOkHttpBuilder().proxy(checkNotNull(proxy, "proxy == null"));
        return this;
    }

    /**
     * 设置连接池
     *
     * @param connectionPool
     * @return
     */
    public NetConfig connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = checkNotNull(connectionPool, "connectionPool == null");
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public NetConfig connectTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            ViseNet.getOkHttpBuilder().connectTimeout(timeout, unit);
        } else {
            ViseNet.getOkHttpBuilder().connectTimeout(ViseConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置写入超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public NetConfig writeTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            ViseNet.getOkHttpBuilder().writeTimeout(timeout, unit);
        } else {
            ViseNet.getOkHttpBuilder().writeTimeout(ViseConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public NetConfig readTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            ViseNet.getOkHttpBuilder().readTimeout(timeout, unit);
        } else {
            ViseNet.getOkHttpBuilder().readTimeout(ViseConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置请求BaseURL
     *
     * @param baseUrl
     * @return
     */
    public NetConfig baseUrl(String baseUrl) {
        this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
        return this;
    }

    /**
     * 设置转换工厂
     *
     * @param factory
     * @return
     */
    public NetConfig converterFactory(Converter.Factory factory) {
        this.converterFactory = factory;
        return this;
    }

    /**
     * 设置CallAdapter工厂
     *
     * @param factory
     * @return
     */
    public NetConfig callAdapterFactory(CallAdapter.Factory factory) {
        this.callAdapterFactory = factory;
        return this;
    }

    /**
     * 设置请求头部
     *
     * @param headers
     * @return
     */
    public NetConfig headers(Map<String, String> headers) {
        ViseNet.getOkHttpBuilder().addInterceptor(new HeadersInterceptor(headers));
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param parameters
     * @return
     */
    public NetConfig parameters(Map<String, String> parameters) {
        ViseNet.getOkHttpBuilder().addInterceptor(new HeadersInterceptor(parameters));
        return this;
    }

    /**
     * 设置拦截器
     *
     * @param interceptor
     * @return
     */
    public NetConfig interceptor(Interceptor interceptor) {
        ViseNet.getOkHttpBuilder().addInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public NetConfig networkInterceptor(Interceptor interceptor) {
        ViseNet.getOkHttpBuilder().addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 设置Cookie管理
     *
     * @param cookie
     * @return
     */
    public NetConfig cookieManager(ApiCookie cookie) {
        this.apiCookie = checkNotNull(cookie, "cookieManager == null");
        return this;
    }

    /**
     * 设置SSL工厂
     *
     * @param sslSocketFactory
     * @return
     */
    public NetConfig SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * 设置主机验证机制
     *
     * @param hostnameVerifier
     * @return
     */
    public NetConfig hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
     *
     * @return
     */
    public NetConfig postGzipInterceptor() {
        interceptor(new GzipRequestInterceptor());
        return this;
    }

    /**
     * 设置缓存Key，主要针对网路请求结果进行缓存
     *
     * @param cacheKey
     * @return
     */
    public NetConfig cacheKey(String cacheKey) {
        ViseNet.getApiCacheBuilder().cacheKey(checkNotNull(cacheKey, "cacheKey == null"));
        return this;
    }

    /**
     * 设置缓存时间，默认永久，主要针对网路请求结果进行缓存
     *
     * @param cacheTime
     * @return
     */
    public NetConfig cacheTime(long cacheTime) {
        ViseNet.getApiCacheBuilder().cacheTime(Math.max(DiskCache.CACHE_NEVER_EXPIRE, cacheTime));
        return this;
    }

    /**
     * 设置在线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @return
     */
    public NetConfig cacheOnline(Cache httpCache) {
        networkInterceptor(new OnlineCacheInterceptor());
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置在线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @param cacheControlValue
     * @return
     */
    public NetConfig cacheOnline(Cache httpCache, final int cacheControlValue) {
        networkInterceptor(new OnlineCacheInterceptor(cacheControlValue));
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置离线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @return
     */
    public NetConfig cacheOffline(Cache httpCache) {
        networkInterceptor(new OfflineCacheInterceptor(ViseNet.getContext()));
        interceptor(new OfflineCacheInterceptor(ViseNet.getContext()));
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置离线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @param cacheControlValue
     * @return
     */
    public NetConfig cacheOffline(Cache httpCache, final int cacheControlValue) {
        networkInterceptor(new OfflineCacheInterceptor(ViseNet.getContext(), cacheControlValue));
        interceptor(new OfflineCacheInterceptor(ViseNet.getContext(), cacheControlValue));
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置缓存路径
     * @param httpCacheDirectory
     * @return
     */
    public NetConfig setHttpCacheDirectory(File httpCacheDirectory) {
        this.httpCacheDirectory = httpCacheDirectory;
        return this;
    }

    /**
     * 设置缓存
     * @param httpCache
     * @return
     */
    public NetConfig setCache(Cache httpCache) {
        this.httpCache = httpCache;
        return this;
    }

    public File getHttpCacheDirectory() {
        return httpCacheDirectory;
    }

    public CallAdapter.Factory getCallAdapterFactory() {
        return callAdapterFactory;
    }

    public Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    public Call.Factory getCallFactory() {
        return callFactory;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public ApiCookie getApiCookie() {
        return apiCookie;
    }

    public Boolean isCookie() {
        return isCookie;
    }

    public Boolean isCache() {
        return isHttpCache;
    }

    public Cache getCache() {
        return httpCache;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }
}
