package com.vise.xsnow.http.request;

import com.vise.log.ViseLog;
import com.vise.utils.assist.SSLUtil;
import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.api.ApiService;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.callback.UCallback;
import com.vise.xsnow.http.config.HttpGlobalConfig;
import com.vise.xsnow.http.core.ApiCookie;
import com.vise.xsnow.http.func.ApiFunc;
import com.vise.xsnow.http.func.ApiRetryFunc;
import com.vise.xsnow.http.interceptor.HeadersInterceptor;
import com.vise.xsnow.http.interceptor.UploadProgressInterceptor;
import com.vise.xsnow.http.mode.ApiHost;
import com.vise.xsnow.http.mode.CacheMode;
import com.vise.xsnow.http.mode.CacheResult;
import com.vise.xsnow.http.mode.HttpHeaders;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @Description: 请求基类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:05
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected HttpGlobalConfig httpGlobalConfig;//全局配置
    protected ApiService apiService;//通用接口服务
    protected Retrofit retrofit;//Retrofit对象
    protected List<Interceptor> interceptors = new ArrayList<>();//局部请求的拦截器
    protected List<Interceptor> networkInterceptors = new ArrayList<>();//局部请求的网络拦截器
    protected Map<String, String> params = new LinkedHashMap<>();//请求参数
    protected HttpHeaders headers = new HttpHeaders();//请求头
    protected int retryDelayMillis;//请求失败重试间隔时间
    protected int retryCount;//重试次数
    protected String baseUrl;//基础域名
    protected String suffixUrl = "";//链接后缀
    protected Object tag;//请求标签
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存
    protected boolean isLocalCache;//是否使用本地缓存
    protected CacheMode cacheMode;//本地缓存类型
    protected String cacheKey;//本地缓存Key
    protected long cacheTime;//本地缓存时间
    protected UCallback uploadCallback;//上传进度回调

    /**
     * 添加请求参数
     *
     * @param paramKey
     * @param paramValue
     * @return
     */
    public R addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.params.put(paramKey, paramValue);
        }
        return (R) this;
    }

    /**
     * 添加请求参数
     *
     * @param params
     * @return
     */
    public R addParams(Map<String, String> params) {
        if (params != null) {
            this.params.putAll(params);
        }
        return (R) this;
    }

    /**
     * 移除请求参数
     *
     * @param paramKey
     * @return
     */
    public R removeParam(String paramKey) {
        if (paramKey != null) {
            this.params.remove(paramKey);
        }
        return (R) this;
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public R params(Map<String, String> params) {
        if (params != null) {
            this.params = params;
        }
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headerKey
     * @param headerValue
     * @return
     */
    public R addHeader(String headerKey, String headerValue) {
        this.headers.put(headerKey, headerValue);
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headers
     * @return
     */
    public R addHeaders(Map<String, String> headers) {
        this.headers.put(headers);
        return (R) this;
    }

    /**
     * 移除请求头
     *
     * @param headerKey
     * @return
     */
    public R removeHeader(String headerKey) {
        this.headers.remove(headerKey);
        return (R) this;
    }

    /**
     * 设置请求头
     *
     * @param headers
     * @return
     */
    public R headers(HttpHeaders headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return (R) this;
    }

    /**
     * 设置请求失败重试间隔时间（毫秒）
     *
     * @param retryDelayMillis
     * @return
     */
    public R retryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return (R) this;
    }

    /**
     * 设置请求失败重试次数
     *
     * @param retryCount
     * @return
     */
    public R retryCount(int retryCount) {
        this.retryCount = retryCount;
        return (R) this;
    }

    /**
     * 设置基础域名，当前请求会替换全局域名
     *
     * @param baseUrl
     * @return
     */
    public R baseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl;
        }
        return (R) this;
    }

    /**
     * 设置请求链接后缀
     *
     * @param suffixUrl
     * @return
     */
    public R suffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
        return (R) this;
    }

    /**
     * 设置请求标签
     *
     * @param tag
     * @return
     */
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param connectTimeOut
     * @return
     */
    public R connectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return (R) this;
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param readTimeOut
     * @return
     */
    public R readTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param writeTimeOut
     * @return
     */
    public R writeTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    /**
     * 设置是否进行HTTP缓存
     *
     * @param isHttpCache
     * @return
     */
    public R setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return (R) this;
    }

    /**
     * 设置是否进行本地缓存
     *
     * @param isLocalCache
     * @return
     */
    public R setLocalCache(boolean isLocalCache) {
        this.isLocalCache = isLocalCache;
        return (R) this;
    }

    /**
     * 设置本地缓存类型
     *
     * @param cacheMode
     * @return
     */
    public R cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (R) this;
    }

    /**
     * 设置本地缓存Key
     *
     * @param cacheKey
     * @return
     */
    public R cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (R) this;
    }

    /**
     * 设置本地缓存时间(毫秒)，默认永久
     *
     * @param cacheTime
     * @return
     */
    public R cacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return (R) this;
    }

    /**
     * 局部设置拦截器
     *
     * @param interceptor
     * @return
     */
    public R interceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
        return (R) this;
    }

    /**
     * 局部设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public R networkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            networkInterceptors.add(interceptor);
        }
        return (R) this;
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

    public <T> Observable<T> request(Type type) {
        generateGlobalConfig();
        generateLocalConfig();
        return execute(type);
    }

    public <T> Observable<CacheResult<T>> cacheRequest(Type type) {
        generateGlobalConfig();
        generateLocalConfig();
        return cacheExecute(type);
    }

    public <T> void request(ACallback<T> callback) {
        generateGlobalConfig();
        generateLocalConfig();
        execute(callback);
    }

    protected abstract <T> Observable<T> execute(Type type);

    protected abstract <T> Observable<CacheResult<T>> cacheExecute(Type type);

    protected abstract <T> void execute(ACallback<T> callback);

    protected <T> ObservableTransformer<ResponseBody, T> norTransformer(final Type type) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiFunc<T>(type))
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }

    /**
     * 生成局部配置
     */
    protected void generateLocalConfig() {
        OkHttpClient.Builder newBuilder = ViseHttp.getInstance().getOkHttpClient().newBuilder();

        if (httpGlobalConfig.getGlobalParams() != null) {
            params.putAll(httpGlobalConfig.getGlobalParams());
        }

        if (httpGlobalConfig.getGlobalHeaders() != null) {
            headers.put(httpGlobalConfig.getGlobalHeaders());
        }

        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                newBuilder.addInterceptor(interceptor);
            }
        }

        if (!networkInterceptors.isEmpty()) {
            for (Interceptor interceptor : networkInterceptors) {
                newBuilder.addNetworkInterceptor(interceptor);
            }
        }

        if (headers.headersMap.size() > 0) {
            newBuilder.addInterceptor(new HeadersInterceptor(headers.headersMap));
        }

        if (uploadCallback != null) {
            newBuilder.addNetworkInterceptor(new UploadProgressInterceptor(uploadCallback));
        }

        if (retryCount <= 0) {
            retryCount = httpGlobalConfig.getRetryCount();
        }

        if (retryDelayMillis <= 0) {
            retryDelayMillis = httpGlobalConfig.getRetryDelayMillis();
        }

        if (readTimeOut > 0) {
            newBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        }

        if (writeTimeOut > 0) {
            newBuilder.readTimeout(writeTimeOut, TimeUnit.SECONDS);
        }

        if (connectTimeOut > 0) {
            newBuilder.readTimeout(connectTimeOut, TimeUnit.SECONDS);
        }

        if (isHttpCache) {
            try {
                if (httpGlobalConfig.getHttpCache() == null) {
                    httpGlobalConfig.httpCache(new Cache(httpGlobalConfig.getHttpCacheDirectory(), ViseConfig.CACHE_MAX_SIZE));
                }
                httpGlobalConfig.cacheOnline(httpGlobalConfig.getHttpCache());
                httpGlobalConfig.cacheOffline(httpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                ViseLog.e("Could not create http cache" + e);
            }
            newBuilder.cache(httpGlobalConfig.getHttpCache());
        }

        if (isLocalCache) {
            if (cacheKey != null) {
                ViseHttp.getApiCacheBuilder().cacheKey(cacheKey);
            } else {
                ViseHttp.getApiCacheBuilder().cacheKey(ApiHost.getHost());
            }
            if (cacheTime > 0) {
                ViseHttp.getApiCacheBuilder().cacheTime(cacheTime);
            } else {
                ViseHttp.getApiCacheBuilder().cacheTime(ViseConfig.CACHE_NEVER_EXPIRE);
            }
        }

        if (baseUrl != null) {
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            if (isLocalCache && cacheKey == null) {
                ViseHttp.getApiCacheBuilder().cacheKey(baseUrl);
            }
            if (httpGlobalConfig.getConverterFactory() != null) {
                newRetrofitBuilder.addConverterFactory(httpGlobalConfig.getConverterFactory());
            }
            if (httpGlobalConfig.getCallAdapterFactory() != null) {
                newRetrofitBuilder.addCallAdapterFactory(httpGlobalConfig.getCallAdapterFactory());
            }
            if (httpGlobalConfig.getCallFactory() != null) {
                newRetrofitBuilder.callFactory(httpGlobalConfig.getCallFactory());
            }
            newBuilder.hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier(baseUrl));
            newRetrofitBuilder.client(newBuilder.build());
            retrofit = newRetrofitBuilder.build();
        } else {
            ViseHttp.getRetrofitBuilder().client(newBuilder.build());
            retrofit = ViseHttp.getRetrofitBuilder().build();
        }

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 生成全局配置
     */
    protected void generateGlobalConfig() {
        httpGlobalConfig = ViseHttp.CONFIG();

        if (httpGlobalConfig.getBaseUrl() == null) {
            httpGlobalConfig.baseUrl(ApiHost.getHost());
        }
        ViseHttp.getRetrofitBuilder().baseUrl(httpGlobalConfig.getBaseUrl());

        if (httpGlobalConfig.getConverterFactory() != null) {
            ViseHttp.getRetrofitBuilder().addConverterFactory(httpGlobalConfig.getConverterFactory());
        }

        if (httpGlobalConfig.getCallAdapterFactory() == null) {
            httpGlobalConfig.callAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        ViseHttp.getRetrofitBuilder().addCallAdapterFactory(httpGlobalConfig.getCallAdapterFactory());

        if (httpGlobalConfig.getCallFactory() != null) {
            ViseHttp.getRetrofitBuilder().callFactory(httpGlobalConfig.getCallFactory());
        }

        if (httpGlobalConfig.getHostnameVerifier() == null) {
            httpGlobalConfig.hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier(httpGlobalConfig.getBaseUrl()));
        }
        ViseHttp.getOkHttpBuilder().hostnameVerifier(httpGlobalConfig.getHostnameVerifier());

        if (httpGlobalConfig.getSslSocketFactory() == null) {
            httpGlobalConfig.SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
        }
        ViseHttp.getOkHttpBuilder().sslSocketFactory(httpGlobalConfig.getSslSocketFactory());

        if (httpGlobalConfig.getConnectionPool() == null) {
            httpGlobalConfig.connectionPool(new ConnectionPool(ViseConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    ViseConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        ViseHttp.getOkHttpBuilder().connectionPool(httpGlobalConfig.getConnectionPool());

        if (httpGlobalConfig.isCookie() && httpGlobalConfig.getApiCookie() == null) {
            httpGlobalConfig.apiCookie(new ApiCookie(ViseHttp.getContext()));
        }
        if (httpGlobalConfig.isCookie()) {
            ViseHttp.getOkHttpBuilder().cookieJar(httpGlobalConfig.getApiCookie());
        }

        if (httpGlobalConfig.getHttpCacheDirectory() == null) {
            httpGlobalConfig.setHttpCacheDirectory(new File(ViseHttp.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR));
        }
        if (httpGlobalConfig.isHttpCache()) {
            try {
                if (httpGlobalConfig.getHttpCache() == null) {
                    httpGlobalConfig.httpCache(new Cache(httpGlobalConfig.getHttpCacheDirectory(), ViseConfig.CACHE_MAX_SIZE));
                }
                httpGlobalConfig.cacheOnline(httpGlobalConfig.getHttpCache());
                httpGlobalConfig.cacheOffline(httpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                ViseLog.e("Could not create http cache" + e);
            }
        }
        if (httpGlobalConfig.getHttpCache() != null) {
            ViseHttp.getOkHttpBuilder().cache(httpGlobalConfig.getHttpCache());
        }
    }

    /**
     * 获取第一级type
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 获取次一级type(如果有)
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }
}
