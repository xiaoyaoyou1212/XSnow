package com.vise.xsnow.net.request;

import android.content.Context;

import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.callback.DCallback;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscription;

/**
 * @Description: 下载请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 21:50.
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {

    public DownloadRequest() {
    }

    public DownloadRequest(DCallback callback) {
        this.downCallback = callback;
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        return apiService.downFile(suffixUrl, params).compose(this.<T>norTransformer(type));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return null;
    }

    @Override
    protected <T> Subscription execute(Context context, ACallback<T> callback) {
        return this.execute(getType(callback))
                .subscribe(new ApiCallbackSubscriber(context, callback));
    }
}
