package com.vise.xsnow.download.core;

import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.net.mode.ApiHost;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: 下载Retrofit
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-17 15:20
 */
public class DownRetrofit {

    private DownRetrofit() {
    }

    public static Retrofit getInstance() {
        return SingletonFactory.INSTANCE;
    }

    private static class SingletonFactory {
        private static final Retrofit INSTANCE = create();

        private static Retrofit create() {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.readTimeout(ViseConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            builder.connectTimeout(ViseConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            return new Retrofit.Builder().baseUrl(ApiHost.getHost())
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
    }
}
