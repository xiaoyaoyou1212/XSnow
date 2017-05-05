package com.vise.xsnow.net;

import android.content.Context;

import com.vise.xsnow.net.config.NetGlobalConfig;
import com.vise.xsnow.net.core.ApiCache;
import com.vise.xsnow.net.request.GetRequest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @Description: 网络请求入口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 15:07
 */
public class ViseNet {
    private static ViseNet viseNet;
    private static Context context;
    private static OkHttpClient.Builder okHttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static ApiCache.Builder apiCacheBuilder;

    private final NetGlobalConfig NET_GLOBAL_CONFIG = NetGlobalConfig.getInstance();

    private ViseNet() {
    }

    public static ViseNet getInstance() {
        if (viseNet == null) {
            synchronized (ViseNet.class) {
                if (viseNet == null) {
                    viseNet = new ViseNet();
                }
            }
        }
        return viseNet;
    }

    public static void init(Context appContext) {
        if (context == null && appContext != null) {
            context = appContext.getApplicationContext();
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(context);
        }
    }

    public static Context getContext() {
        if (context == null) {
            throw new IllegalStateException("Please call ViseNet.init(this) in Application to initialize!");
        }
        return context;
    }

    public static OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

    public static ApiCache.Builder getApiCacheBuilder() {
        return apiCacheBuilder;
    }

    public NetGlobalConfig config() {
        return NET_GLOBAL_CONFIG;
    }

    public static GetRequest get() {
        return new GetRequest();
    }
}
