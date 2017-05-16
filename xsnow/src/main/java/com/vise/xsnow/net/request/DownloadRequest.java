package com.vise.xsnow.net.request;

import android.content.Context;

import com.vise.utils.assist.ClassUtil;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.callback.DCallback;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

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
    protected <T> Observable<T> execute(Class<T> clazz) {
        return apiService.downFile(suffixUrl, params).compose(this.norTransformer(clazz));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz) {
        return null;
    }

    @Override
    protected <T> Subscription execute(Context context, ACallback<T> callback) {
        return this.execute(ClassUtil.getTClass(callback))
                .subscribe(new ApiCallbackSubscriber(context, callback));
    }
}
