package com.vise.xsnow.net.strategy;

import com.vise.xsnow.net.core.ApiCache;
import com.vise.xsnow.net.mode.CacheResult;

import rx.Observable;

/**
 * @Description: 缓存策略接口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/12/31 14:21.
 */
public interface ICacheStrategy<T> {
    <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Class<T> clazz);
}
