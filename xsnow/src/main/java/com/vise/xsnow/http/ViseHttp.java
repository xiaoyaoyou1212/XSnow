package com.vise.xsnow.http;

import android.content.Context;

import com.vise.xsnow.http.callback.UCallback;
import com.vise.xsnow.http.config.HttpGlobalConfig;
import com.vise.xsnow.http.core.ApiCache;
import com.vise.xsnow.http.core.ApiManager;
import com.vise.xsnow.http.request.BaseHttpRequest;
import com.vise.xsnow.http.request.DeleteRequest;
import com.vise.xsnow.http.request.DownloadRequest;
import com.vise.xsnow.http.request.GetRequest;
import com.vise.xsnow.http.request.HeadRequest;
import com.vise.xsnow.http.request.OptionsRequest;
import com.vise.xsnow.http.request.PatchRequest;
import com.vise.xsnow.http.request.PostRequest;
import com.vise.xsnow.http.request.PutRequest;
import com.vise.xsnow.http.request.RetrofitRequest;
import com.vise.xsnow.http.request.UploadRequest;

import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @Description: 网络请求入口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 15:07
 */
public class ViseHttp {
    private static Context context;
    private static OkHttpClient.Builder okHttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static ApiCache.Builder apiCacheBuilder;
    private static OkHttpClient okHttpClient;

    private static final HttpGlobalConfig NET_GLOBAL_CONFIG = HttpGlobalConfig.getInstance();

    public static HttpGlobalConfig CONFIG() {
        return NET_GLOBAL_CONFIG;
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
            throw new IllegalStateException("Please call ViseHttp.init(this) in Application to initialize!");
        }
        return context;
    }

    public static OkHttpClient.Builder getOkHttpBuilder() {
        if (okHttpBuilder == null) {
            throw new IllegalStateException("Please call ViseHttp.init(this) in Application to initialize!");
        }
        return okHttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        if (retrofitBuilder == null) {
            throw new IllegalStateException("Please call ViseHttp.init(this) in Application to initialize!");
        }
        return retrofitBuilder;
    }

    public static ApiCache.Builder getApiCacheBuilder() {
        if (apiCacheBuilder == null) {
            throw new IllegalStateException("Please call ViseHttp.init(this) in Application to initialize!");
        }
        return apiCacheBuilder;
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = getOkHttpBuilder().build();
        }
        return okHttpClient;
    }

    public static ApiCache getApiCache() {
        return getApiCacheBuilder().build();
    }

    /**
     * 通用请求，可传入自定义请求
     *
     * @param request
     * @return
     */
    public static BaseHttpRequest BASE(BaseHttpRequest request) {
        if (request != null) {
            return request;
        } else {
            return new GetRequest("");
        }
    }

    /**
     * 可传入自定义Retrofit接口服务的请求类型
     *
     * @return
     */
    public static <T> RetrofitRequest RETROFIT() {
        return new RetrofitRequest();
    }

    /**
     * GET请求
     *
     * @param suffixUrl
     * @return
     */
    public static GetRequest GET(String suffixUrl) {
        return new GetRequest(suffixUrl);
    }

    /**
     * POST请求
     *
     * @param suffixUrl
     * @return
     */
    public static PostRequest POST(String suffixUrl) {
        return new PostRequest(suffixUrl);
    }

    /**
     * HEAD请求
     *
     * @param suffixUrl
     * @return
     */
    public static HeadRequest HEAD(String suffixUrl) {
        return new HeadRequest(suffixUrl);
    }

    /**
     * PUT请求
     *
     * @param suffixUrl
     * @return
     */
    public static PutRequest PUT(String suffixUrl) {
        return new PutRequest(suffixUrl);
    }

    /**
     * PATCH请求
     *
     * @param suffixUrl
     * @return
     */
    public static PatchRequest PATCH(String suffixUrl) {
        return new PatchRequest(suffixUrl);
    }

    /**
     * OPTIONS请求
     *
     * @param suffixUrl
     * @return
     */
    public static OptionsRequest OPTIONS(String suffixUrl) {
        return new OptionsRequest(suffixUrl);
    }

    /**
     * DELETE请求
     *
     * @param suffixUrl
     * @return
     */
    public static DeleteRequest DELETE(String suffixUrl) {
        return new DeleteRequest(suffixUrl);
    }

    /**
     * 上传
     *
     * @param suffixUrl
     * @return
     */
    public static UploadRequest UPLOAD(String suffixUrl) {
        return new UploadRequest(suffixUrl);
    }

    /**
     * 上传（包含上传进度回调）
     *
     * @param suffixUrl
     * @return
     */
    public static UploadRequest UPLOAD(String suffixUrl, UCallback uCallback) {
        return new UploadRequest(suffixUrl, uCallback);
    }

    /**
     * 下载（回调DownProgress）
     *
     * @param suffixUrl
     * @return
     */
    public static DownloadRequest DOWNLOAD(String suffixUrl) {
        return new DownloadRequest(suffixUrl);
    }

    /**
     * 添加请求订阅者
     *
     * @param tag
     * @param disposable
     */
    public static void addDisposable(Object tag, Disposable disposable) {
        ApiManager.get().add(tag, disposable);
    }

    /**
     * 根据Tag取消请求
     */
    public static void cancelTag(Object tag) {
        ApiManager.get().cancel(tag);
    }

    /**
     * 取消所有请求请求
     */
    public static void cancelAll() {
        ApiManager.get().cancelAll();
    }

    /**
     * 清除对应Key的缓存
     *
     * @param key
     */
    public static void removeCache(String key) {
        getApiCache().remove(key);
    }

    /**
     * 清楚所有缓存并关闭缓存
     *
     * @return
     */
    public static Disposable clearCache() {
        return getApiCache().clear();
    }

}
