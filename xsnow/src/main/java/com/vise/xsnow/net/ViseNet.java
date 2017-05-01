package com.vise.xsnow.net;

import android.content.Context;

import com.vise.xsnow.net.config.NetConfig;
import com.vise.xsnow.net.core.ApiCache;
import com.vise.xsnow.net.request.GetRequest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * @Description: 网络请求入口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 15:07
 */
public class ViseNet {
    private static ViseNet sViseNet;
    private static Context sContext;
    private static OkHttpClient.Builder sOkHttpBuilder;
    private static Retrofit.Builder sRetrofitBuilder;
    private static ApiCache.Builder sApiCacheBuilder;

    private final NetConfig NET_CONFIG = NetConfig.getInstance();

    private ViseNet() {
    }

    public static ViseNet getInstance() {
        if (sViseNet == null) {
            synchronized (ViseNet.class) {
                if (sViseNet == null) {
                    sViseNet = new ViseNet();
                }
            }
        }
        return sViseNet;
    }

    public static void init(Context context) {
        if (sContext == null && context != null) {
            sContext = context.getApplicationContext();
            sOkHttpBuilder = new OkHttpClient.Builder();
            sRetrofitBuilder = new Retrofit.Builder();
            sApiCacheBuilder = new ApiCache.Builder(sContext);
        }
    }

    public static Context getContext() {
        if (sContext == null) {
            throw new IllegalStateException("Please call ViseNet.init(this) in Application to initialize!");
        }
        return sContext;
    }

    public static OkHttpClient.Builder getOkHttpBuilder() {
        return sOkHttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return sRetrofitBuilder;
    }

    public static ApiCache.Builder getApiCacheBuilder() {
        return sApiCacheBuilder;
    }

    public NetConfig Config() {
        return NET_CONFIG;
    }

    public static GetRequest get() {
        return new GetRequest();
    }
}
