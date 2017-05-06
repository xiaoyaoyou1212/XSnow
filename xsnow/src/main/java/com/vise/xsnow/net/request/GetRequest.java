package com.vise.xsnow.net.request;

import com.vise.utils.assist.ClassUtil;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

import rx.Observable;
import rx.Subscription;

/**
 * @Description: Get请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-28 16:05
 */
public class GetRequest extends BaseRequest<GetRequest> {

    @Override
    protected <T> Observable<T> execute(Class<T> clazz) {
        return apiService.get(suffixUrl, params).compose(this.norTransformer(clazz));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Class<T> clazz) {
        return execute(clazz).compose(apiCache.transformer(cacheMode, clazz));
    }

    @Override
    protected <T> Subscription execute(ApiCallback<T> apiCallback) {
        if (isLocalCache) {
            return this.cacheExecute(ClassUtil.getTClass(apiCallback))
                    .subscribe(new ApiCallbackSubscriber(ViseNet.getContext(), apiCallback));
        }
        return this.execute(ClassUtil.getTClass(apiCallback))
                .subscribe(new ApiCallbackSubscriber(ViseNet.getContext(), apiCallback));
    }
}
