package com.vise.xsnow.net.interceptor;

import com.vise.xsnow.net.body.UploadProgressRequestBody;
import com.vise.xsnow.net.callback.UploadProgressCallback;

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

    private UploadProgressCallback uploadProgressCallback;

    public UploadProgressInterceptor(UploadProgressCallback uploadProgressCallback) {
        this.uploadProgressCallback = uploadProgressCallback;
        if (uploadProgressCallback == null) {
            throw new NullPointerException("this uploadProgressCallback must not null.");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        new UploadProgressRequestBody(originalRequest.body(), uploadProgressCallback))
                .build();
        return chain.proceed(progressRequest);
    }
}
