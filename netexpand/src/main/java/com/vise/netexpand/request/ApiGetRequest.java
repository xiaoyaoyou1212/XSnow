package com.vise.netexpand.request;

import android.content.Context;

import com.vise.netexpand.func.ApiDataFunc;
import com.vise.netexpand.func.ApiResultFunc;
import com.vise.netexpand.mode.ApiResult;
import com.vise.utils.assist.ClassUtil;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.func.ApiRetryFunc;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.request.BaseRequest;
import com.vise.xsnow.net.request.GetRequest;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/13 14:31.
 */
public class ApiGetRequest extends GetRequest {
    @Override
    protected <T> Observable<T> execute(Class<T> clazz) {
        return apiService.get(suffixUrl, params).map(new ApiResultFunc<T>(clazz)).compose(this.<T>apiTransformer());
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz) {
        return execute(clazz).compose(ViseNet.getInstance().getApiCache().transformer(cacheMode, clazz));
    }

    @Override
    protected <T> Subscription execute(Context context, ACallback<T> callback) {
        if (isLocalCache) {
            return this.cacheExecute(ClassUtil.getTClass(callback))
                    .subscribe(new ApiCallbackSubscriber(context, callback));
        }
        return this.execute(ClassUtil.getTClass(callback))
                .subscribe(new ApiCallbackSubscriber(context, callback));
    }

    protected <T> Observable.Transformer<ApiResult<T>, T> apiTransformer() {
        return new Observable.Transformer<ApiResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<ApiResult<T>> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiDataFunc<T>())
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }
}
