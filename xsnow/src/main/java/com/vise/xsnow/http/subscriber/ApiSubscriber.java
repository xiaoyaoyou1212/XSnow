package com.vise.xsnow.http.subscriber;

import android.content.Context;

import com.vise.xsnow.http.exception.ApiException;
import com.vise.xsnow.http.mode.ApiCode;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;

/**
 * @Description: API统一订阅者，采用弱引用管理上下文，防止内存泄漏
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-03 14:07
 */
abstract class ApiSubscriber<T> implements Observer<T> {
    WeakReference<Context> contextWeakReference;

    ApiSubscriber(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiCode.Request.UNKNOWN));
        }
    }

    protected abstract void onError(ApiException e);
}
