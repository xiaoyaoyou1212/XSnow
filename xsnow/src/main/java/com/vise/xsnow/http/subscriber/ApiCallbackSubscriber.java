package com.vise.xsnow.http.subscriber;

import android.content.Context;

import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.exception.ApiException;

import io.reactivex.disposables.Disposable;

/**
 * @Description: 包含回调的订阅者，如果订阅这个，上层在不使用订阅者的情况下可获得回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-05 09:35
 */
public class ApiCallbackSubscriber<T> extends ApiSubscriber<T> {

    protected ACallback<T> callBack;
    protected Disposable disposable;

    public ApiCallbackSubscriber(Context context, ACallback<T> callBack) {
        super(context);
        if (callBack == null) {
            throw new NullPointerException("this callback is null!");
        }
        this.callBack = callBack;
    }

    @Override
    public void onError(ApiException e) {
        if (disposable != null) {
            disposable.dispose();
        }
        if (e == null) {
            callBack.onFail(-1, "This ApiException is Null.");
            return;
        }
        callBack.onFail(e.getCode(), e.getMessage());
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
        callBack.onSuccess(t);
    }

    @Override
    public void onComplete() {

    }
}
