package com.vise.xsnow.http.interceptor;

import android.support.annotation.NonNull;

import com.vise.xsnow.http.body.UploadProgressRequestBody;
import com.vise.xsnow.http.callback.UCallback;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 上传进度拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-08 15:10
 */
public class UploadProgressInterceptor implements Interceptor {

    private UCallback callback;

    public UploadProgressInterceptor(UCallback callback) {
        this.callback = callback;
        if (callback == null) {
            throw new NullPointerException("this callback must not null.");
        }
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        new UploadProgressRequestBody(originalRequest.body(), callback))
                .build();
        return chain.proceed(progressRequest);
    }
}
