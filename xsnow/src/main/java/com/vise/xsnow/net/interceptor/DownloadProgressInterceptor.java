package com.vise.xsnow.net.interceptor;

import com.vise.xsnow.net.body.DownloadProgressResponseBody;
import com.vise.xsnow.net.callback.DCallback;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Description: 下载进度拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/15 20:54.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private DCallback callback;

    public DownloadProgressInterceptor(DCallback callback) {
        this.callback = callback;
        if (callback == null) {
            throw new NullPointerException("this callback must not null.");
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), callback))
                .build();
    }
}
