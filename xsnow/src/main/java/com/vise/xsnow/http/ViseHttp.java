package com.vise.xsnow.http;

import android.content.Context;

import com.vise.xsnow.http.callback.UCallback;
import com.vise.xsnow.http.config.HttpGlobalConfig;
import com.vise.xsnow.http.core.ApiCache;
import com.vise.xsnow.http.core.ApiManager;
import com.vise.xsnow.http.request.BaseRequest;
import com.vise.xsnow.http.request.DeleteRequest;
import com.vise.xsnow.http.request.DownloadRequest;
import com.vise.xsnow.http.request.GetRequest;
import com.vise.xsnow.http.request.HeadRequest;
import com.vise.xsnow.http.request.OptionsRequest;
import com.vise.xsnow.http.request.PatchRequest;
import com.vise.xsnow.http.request.PostRequest;
import com.vise.xsnow.http.request.PutRequest;
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
    private static ViseHttp instance;
    private static Context context;
    private static OkHttpClient.Builder okHttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static ApiCache.Builder apiCacheBuilder;
    private OkHttpClient okHttpClient;
    private ApiCache apiCache;

    private static final HttpGlobalConfig NET_GLOBAL_CONFIG = HttpGlobalConfig.getInstance();

    public static HttpGlobalConfig CONFIG() {
        return NET_GLOBAL_CONFIG;
    }

    private ViseHttp() {
    }

    public static ViseHttp getInstance() {
        if (instance == null) {
            synchronized (ViseHttp.class) {
                if (instance == null) {
                    instance = new ViseHttp();
                }
            }
        }
        return instance;
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

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = getOkHttpBuilder().build();
        }
        return okHttpClient;
    }

    public ApiCache getApiCache() {
        if (apiCache == null || apiCache.isClosed()) {
            apiCache = getApiCacheBuilder().build();
        }
        return apiCache;
    }

    /**
     * 通用请求，可传入自定义请求
     * @param request
     * @return
     */
    public static BaseRequest BASE(BaseRequest request) {
        if (request != null) {
            return request;
        } else {
            return new GetRequest();
        }
    }

    /**
     * GET请求
     * @return
     */
    public static GetRequest GET() {
        return new GetRequest();
    }

    /**
     * POST请求
     * @return
     */
    public static PostRequest POST() {
        return new PostRequest();
    }

    /**
     * HEAD请求
     * @return
     */
    public static HeadRequest HEAD() {
        return new HeadRequest();
    }

    /**
     * PUT请求
     * @return
     */
    public static PutRequest PUT() {
        return new PutRequest();
    }

    /**
     * PATCH请求
     * @return
     */
    public static PatchRequest PATCH() {
        return new PatchRequest();
    }

    /**
     * OPTIONS请求
     * @return
     */
    public static OptionsRequest OPTIONS() {
        return new OptionsRequest();
    }

    /**
     * DELETE请求
     * @return
     */
    public static DeleteRequest DELETE() {
        return new DeleteRequest();
    }

    /**
     * 上传
     * @return
     */
    public static UploadRequest UPLOAD() {
        return new UploadRequest();
    }

    /**
     * 上传（包含上传进度）
     * @param callback 上传进度回调
     * @return
     */
    public static UploadRequest UPLOAD(UCallback callback) {
        return new UploadRequest(callback);
    }

    /**
     * 下载（回调DownProgress）
     * @return
     */
    public static DownloadRequest DOWNLOAD() {
        return new DownloadRequest();
    }

    /**
     * 添加请求订阅者
     * @param tag
     * @param disposable
     */
    public void addDisposable(Object tag, Disposable disposable) {
        ApiManager.get().add(tag, disposable);
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        ApiManager.get().cancel(tag);
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        ApiManager.get().cancelAll();
    }

    /**
     * 清除对应Key的缓存
     * @param key
     */
    public void removeCache(String key) {
        getApiCache().remove(key);
    }

    /**
     * 清楚所有缓存并关闭缓存
     * @return
     */
    public Disposable clearCache() {
        return getApiCache().clear();
    }

}
