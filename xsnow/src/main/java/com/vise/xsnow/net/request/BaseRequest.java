package com.vise.xsnow.net.request;

import com.vise.xsnow.net.config.NetConfig;
import com.vise.xsnow.net.core.ApiCache;

import java.io.File;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:05
 */
public class BaseRequest {
    protected NetConfig netConfig;
    protected OkHttpClient okHttpClient;
    protected Retrofit retrofit;
    protected ApiCache apiCache;
    protected File httpCacheDirectory;
}
