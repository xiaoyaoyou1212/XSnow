package com.vise.xsnow.http.strategy;

import com.vise.xsnow.http.core.ApiCache;
import com.vise.xsnow.http.mode.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;

/**
 * @Description: 缓存策略--优先缓存
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 14:31.
 */
public class FirstCacheStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(apiCache, cacheKey, type);
        cache.onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(apiCache, cacheKey, source);
        return Observable.concat(cache, remote).firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
            @Override
            public Boolean call(CacheResult<T> tResultData) {
                return tResultData != null && tResultData.getCacheData() != null;
            }
        });
    }
}
