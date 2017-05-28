package com.vise.netexpand.request;

import com.vise.netexpand.func.ApiDataFunc;
import com.vise.netexpand.mode.ApiResult;
import com.vise.xsnow.net.func.ApiRetryFunc;
import com.vise.xsnow.net.request.BaseRequest;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/28 15:46.
 */
public abstract class ApiBaseRequest extends BaseRequest<ApiBaseRequest> {
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
