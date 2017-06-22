package com.vise.xsnow.http.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.xsnow.common.ViseConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Description: 在线缓存拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 22:23.
 */
public class OnlineCacheInterceptor implements Interceptor {
    private String cacheControlValue;

    public OnlineCacheInterceptor() {
        this(ViseConfig.MAX_AGE_ONLINE);
    }

    public OnlineCacheInterceptor(int cacheControlValue) {
        this.cacheControlValue = String.format("max-age=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") || cacheControl
                .contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            ViseLog.d(originalResponse.headers());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();

        } else {
            return originalResponse;
        }
    }
}
