package com.vise.xsnow.http.strategy;

import com.vise.log.ViseLog;
import com.vise.xsnow.common.GsonUtil;
import com.vise.xsnow.http.core.ApiCache;
import com.vise.xsnow.http.mode.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: 缓存策略
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 14:28.
 */
abstract class CacheStrategy<T> implements ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> loadCache(final ApiCache apiCache, final String key, final Type type) {
        return apiCache.<T>get(key).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s != null;
            }
        }).map(new Function<String, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(String s) throws Exception {
                T t = GsonUtil.gson().fromJson(s, type);
                ViseLog.i("loadCache result=" + t);
                return new CacheResult<>(true, t);
            }
        });
    }

    <T> Observable<CacheResult<T>> loadRemote(final ApiCache apiCache, final String key, Observable<T> source) {
        return source.map(new Function<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(T t) throws Exception {
                ViseLog.i("loadRemote result=" + t);
                apiCache.put(key, t).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean status) throws Exception {
                        ViseLog.i("save status => " + status);
                    }
                });
                return new CacheResult<>(false, t);
            }
        });
    }
}
