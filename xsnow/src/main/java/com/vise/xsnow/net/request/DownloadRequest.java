package com.vise.xsnow.net.request;

import android.content.Context;

import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.mode.CacheResult;

import rx.Observable;
import rx.Subscription;

/**
 * @Description: 下载请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 21:50.
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {
    @Override
    protected <T> Observable<T> execute(Class<T> clazz) {
        return null;
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz) {
        return null;
    }

    @Override
    protected <T> Subscription execute(Context context, ApiCallback<T> apiCallback) {
        return null;
    }
}
