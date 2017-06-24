package com.vise.xsnow.http.subscriber;

import com.vise.xsnow.http.callback.ACallback;

/**
 * @Description: 包含下载进度回调的订阅者
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/6/7 23:45.
 */
public class DownCallbackSubscriber<T> extends ApiCallbackSubscriber<T> {
    public DownCallbackSubscriber(ACallback<T> callBack) {
        super(callBack);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        callBack.onSuccess(super.data);
    }
}
