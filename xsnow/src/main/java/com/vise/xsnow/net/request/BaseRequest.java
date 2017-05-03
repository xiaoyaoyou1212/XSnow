package com.vise.xsnow.net.request;

import com.vise.log.ViseLog;
import com.vise.utils.assist.SSLUtil;
import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.api.ApiService;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.config.NetConfig;
import com.vise.xsnow.net.convert.GsonConverterFactory;
import com.vise.xsnow.net.core.ApiCache;
import com.vise.xsnow.net.core.ApiCookie;
import com.vise.xsnow.net.func.ApiDataFunc;
import com.vise.xsnow.net.func.ApiErrFunc;
import com.vise.xsnow.net.func.ApiFunc;
import com.vise.xsnow.net.mode.ApiHost;
import com.vise.xsnow.net.mode.ApiResult;
import com.vise.xsnow.net.mode.CacheMode;
import com.vise.xsnow.net.mode.HttpHeaders;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:05
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected NetConfig netConfig;
    protected ApiService apiService;
    protected OkHttpClient okHttpClient;
    protected Retrofit retrofit;
    protected ApiCache apiCache;

    protected String baseUrl;//基础域名
    protected String suffixUrl;//链接后缀
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeout;//连接超时时间
    protected int retryCount;//重试次数
    protected boolean isLocalCache;//是否使用本地缓存
    protected CacheMode cacheMode;//本地缓存类型
    protected String cacheKey;//本地缓存Key
    protected long cacheTime;//本地缓存时间
    protected Map<String, String> params = new LinkedHashMap<>();//请求参数
    protected HttpHeaders headers = new HttpHeaders();//请求头
    protected ApiCallback apiCallback;//请求回调
    protected Class clazz;//响应实体类

    public <T> Observable<T> request(Class<T> clazz) {
        generateGlobalConfig();
        generateLocalConfig();
        return execute(clazz);
    }

    public <T> Subscription request(ApiCallback<T> apiCallback) {
        generateGlobalConfig();
        generateLocalConfig();
        return execute(apiCallback);
    }

    protected abstract <T> Observable<T> execute(Class<T> clazz);

    protected abstract <T> Subscription execute(ApiCallback<T> apiCallback);

    protected <T> Observable.Transformer<ResponseBody, T> norTransformer(final Class<T> clazz) {
        return new Observable.Transformer<ResponseBody, T>() {
            @Override
            public Observable<T> call(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers
                                .mainThread()).map(new ApiFunc<T>(clazz)).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }

    protected <T> Observable.Transformer<ApiResult<T>, T> apiTransformer() {
        return new Observable.Transformer<ApiResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<ApiResult<T>> apiResultObservable) {
                return apiResultObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread()).map(new ApiDataFunc<T>()).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }

    /**
     * 生成局部配置
     */
    private void generateLocalConfig() {

    }

    /**
     * 生成全局配置
     */
    private void generateGlobalConfig() {
        netConfig = ViseNet.getInstance().Config();

        if (netConfig.getBaseUrl() == null) {
            netConfig.baseUrl(ApiHost.getHost());
        }
        ViseNet.getRetrofitBuilder().baseUrl(netConfig.getBaseUrl());

        if (netConfig.getConverterFactory() == null) {
            netConfig.converterFactory(GsonConverterFactory.create());
        }
        ViseNet.getRetrofitBuilder().addConverterFactory(netConfig.getConverterFactory());

        if (netConfig.getCallAdapterFactory() == null) {
            netConfig.callAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        ViseNet.getRetrofitBuilder().addCallAdapterFactory(netConfig.getCallAdapterFactory());

        if (netConfig.getCallFactory() != null) {
            ViseNet.getRetrofitBuilder().callFactory(netConfig.getCallFactory());
        }

        if (netConfig.getHostnameVerifier() == null) {
            netConfig.hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier(netConfig.getBaseUrl()));
        }
        ViseNet.getOkHttpBuilder().hostnameVerifier(netConfig.getHostnameVerifier());

        if (netConfig.getSslSocketFactory() == null) {
            netConfig.SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
        }
        ViseNet.getOkHttpBuilder().sslSocketFactory(netConfig.getSslSocketFactory());

        if (netConfig.getConnectionPool() == null) {
            netConfig.connectionPool(new ConnectionPool(ViseConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    ViseConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        ViseNet.getOkHttpBuilder().connectionPool(netConfig.getConnectionPool());

        if (netConfig.isCookie() && netConfig.getApiCookie() == null) {
            netConfig.cookieManager(new ApiCookie(ViseNet.getContext()));
        }
        if (netConfig.isCookie()) {
            ViseNet.getOkHttpBuilder().cookieJar(netConfig.getApiCookie());
        }

        if (netConfig.getHttpCacheDirectory() == null) {
            netConfig.setHttpCacheDirectory(new File(ViseNet.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR));
        }
        if (netConfig.isCache()) {
            try {
                if (netConfig.getCache() == null) {
                    netConfig.setCache(new Cache(netConfig.getHttpCacheDirectory(), ViseConfig.CACHE_MAX_SIZE));
                }
                netConfig.cacheOnline(netConfig.getCache());
                netConfig.cacheOffline(netConfig.getCache());
            } catch (Exception e) {
                ViseLog.e("Could not create http cache" + e);
            }
        }
        if (netConfig.getCache() != null) {
            ViseNet.getOkHttpBuilder().cache(netConfig.getCache());
        }

        okHttpClient = ViseNet.getOkHttpBuilder().build();
        ViseNet.getRetrofitBuilder().client(okHttpClient);
        retrofit = ViseNet.getRetrofitBuilder().build();
        apiCache = ViseNet.getApiCacheBuilder().build();
        apiService = retrofit.create(ApiService.class);
    }
}
