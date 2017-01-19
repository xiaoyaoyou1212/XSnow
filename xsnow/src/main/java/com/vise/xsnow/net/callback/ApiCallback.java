package com.vise.xsnow.net.callback;

import com.vise.xsnow.net.exception.ApiException;

/**
 * @Description: API操作回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-05 09:39
 */
public abstract class ApiCallback<T> {
    public abstract void onStart();

    public abstract void onError(ApiException e);

    public abstract void onCompleted();

    public abstract void onNext(T t);
}
