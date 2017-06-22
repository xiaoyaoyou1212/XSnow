package com.vise.xsnow.http.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 设置请求标签拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 21:26.
 */
public class TagInterceptor implements Interceptor {

    private Object tag;

    public TagInterceptor(Object tag) {
        this.tag = tag;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.tag(tag);
        return chain.proceed(builder.build());
    }
}
