package com.vise.xsnow.http.request;

import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.core.ApiManager;
import com.vise.xsnow.http.mode.CacheResult;
import com.vise.xsnow.http.subscriber.ApiCallbackSubscriber;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * @Description: Patch请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 20:29.
 */
public class PatchRequest extends BaseRequest<PatchRequest> {
    @Override
    protected <T> Observable<T> execute(Type type) {
        return apiService.patch(suffixUrl, params).compose(this.<T>norTransformer(type));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return this.<T>execute(type).compose(ViseHttp.getInstance().getApiCache().<T>transformer(cacheMode, type));
    }

    @Override
    protected <T> void execute(ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber(callback);
        if (super.tag != null) {
            ApiManager.get().add(super.tag, disposableObserver);
        }
        if (isLocalCache) {
            this.cacheExecute(getSubType(callback)).subscribe(disposableObserver);
        } else {
            this.execute(getType(callback)).subscribe(disposableObserver);
        }
    }
}
