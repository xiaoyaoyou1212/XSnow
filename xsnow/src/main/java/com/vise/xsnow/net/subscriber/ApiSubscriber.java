package com.vise.xsnow.net.subscriber;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.vise.utils.assist.Network;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.net.mode.ApiCode;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * @Description: API统一订阅者，采用弱引用管理上下文，防止内存泄漏
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-03 14:07
 */
public abstract class ApiSubscriber<T> extends Subscriber<T> {
    public WeakReference<Context> contextWeakReference;

    public ApiSubscriber(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiCode.Request.UNKNOWN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!Network.isConnected(contextWeakReference.get())) {
            onError(new ApiException(new NetworkErrorException(), ApiCode.Request.NETWORK_ERROR));
        }
    }

    public abstract void onError(ApiException e);
}
