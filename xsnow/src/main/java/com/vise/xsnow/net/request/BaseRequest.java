package com.vise.xsnow.net.request;

import com.vise.log.ViseLog;
import com.vise.utils.assist.SSLUtil;
import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.api.ApiService;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.config.NetGlobalConfig;
import com.vise.xsnow.net.config.NetLocalConfig;
import com.vise.xsnow.net.convert.GsonConverterFactory;
import com.vise.xsnow.net.core.ApiCache;
import com.vise.xsnow.net.core.ApiCookie;
import com.vise.xsnow.net.func.ApiDataFunc;
import com.vise.xsnow.net.func.ApiErrFunc;
import com.vise.xsnow.net.func.ApiFunc;
import com.vise.xsnow.net.func.ApiRetryFunc;
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
    protected NetGlobalConfig netGlobalConfig;
    protected NetLocalConfig netLocalConfig;
    protected ApiService apiService;
    protected OkHttpClient okHttpClient;
    protected Retrofit retrofit;
    protected ApiCache apiCache;

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
                                .mainThread()).map(new ApiFunc<T>(clazz)).retryWhen(new ApiRetryFunc(3, 3000));
            }
        };
    }

    protected <T> Observable.Transformer<ApiResult<T>, T> apiTransformer() {
        return new Observable.Transformer<ApiResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<ApiResult<T>> apiResultObservable) {
                return apiResultObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread()).map(new ApiDataFunc<T>()).retryWhen(new ApiRetryFunc(3, 3000));
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
        netGlobalConfig = ViseNet.getInstance().Config();

        if (netGlobalConfig.getBaseUrl() == null) {
            netGlobalConfig.baseUrl(ApiHost.getHost());
        }
        ViseNet.getRetrofitBuilder().baseUrl(netGlobalConfig.getBaseUrl());

        if (netGlobalConfig.getConverterFactory() == null) {
            netGlobalConfig.converterFactory(GsonConverterFactory.create());
        }
        ViseNet.getRetrofitBuilder().addConverterFactory(netGlobalConfig.getConverterFactory());

        if (netGlobalConfig.getCallAdapterFactory() == null) {
            netGlobalConfig.callAdapterFactory(RxJavaCallAdapterFactory.create());
        }
        ViseNet.getRetrofitBuilder().addCallAdapterFactory(netGlobalConfig.getCallAdapterFactory());

        if (netGlobalConfig.getCallFactory() != null) {
            ViseNet.getRetrofitBuilder().callFactory(netGlobalConfig.getCallFactory());
        }

        if (netGlobalConfig.getHostnameVerifier() == null) {
            netGlobalConfig.hostnameVerifier(new SSLUtil.UnSafeHostnameVerifier(netGlobalConfig.getBaseUrl()));
        }
        ViseNet.getOkHttpBuilder().hostnameVerifier(netGlobalConfig.getHostnameVerifier());

        if (netGlobalConfig.getSslSocketFactory() == null) {
            netGlobalConfig.SSLSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
        }
        ViseNet.getOkHttpBuilder().sslSocketFactory(netGlobalConfig.getSslSocketFactory());

        if (netGlobalConfig.getConnectionPool() == null) {
            netGlobalConfig.connectionPool(new ConnectionPool(ViseConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    ViseConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        ViseNet.getOkHttpBuilder().connectionPool(netGlobalConfig.getConnectionPool());

        if (netGlobalConfig.isCookie() && netGlobalConfig.getApiCookie() == null) {
            netGlobalConfig.apiCookie(new ApiCookie(ViseNet.getContext()));
        }
        if (netGlobalConfig.isCookie()) {
            ViseNet.getOkHttpBuilder().cookieJar(netGlobalConfig.getApiCookie());
        }

        if (netGlobalConfig.getHttpCacheDirectory() == null) {
            netGlobalConfig.setHttpCacheDirectory(new File(ViseNet.getContext().getCacheDir(), ViseConfig.CACHE_HTTP_DIR));
        }
        if (netGlobalConfig.isHttpCache()) {
            try {
                if (netGlobalConfig.getHttpCache() == null) {
                    netGlobalConfig.httpCache(new Cache(netGlobalConfig.getHttpCacheDirectory(), ViseConfig.CACHE_MAX_SIZE));
                }
                netGlobalConfig.cacheOnline(netGlobalConfig.getHttpCache());
                netGlobalConfig.cacheOffline(netGlobalConfig.getHttpCache());
            } catch (Exception e) {
                ViseLog.e("Could not create http cache" + e);
            }
        }
        if (netGlobalConfig.getHttpCache() != null) {
            ViseNet.getOkHttpBuilder().cache(netGlobalConfig.getHttpCache());
        }

        okHttpClient = ViseNet.getOkHttpBuilder().build();
        ViseNet.getRetrofitBuilder().client(okHttpClient);
        retrofit = ViseNet.getRetrofitBuilder().build();
        apiCache = ViseNet.getApiCacheBuilder().build();
        apiService = retrofit.create(ApiService.class);
    }
}
