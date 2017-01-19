package com.vise.xsnow.net.func;

import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.net.mode.ApiResult;

import rx.functions.Func1;

/**
 * @Description: ApiResult<T>转T
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-30 17:55
 */
public class ApiDataFunc<T> implements Func1<ApiResult<T>, T> {
    public ApiDataFunc() {
    }

    @Override
    public T call(ApiResult<T> response) {
        if (ApiException.isSuccess(response)) {
            return response.getData();
        } else {
            return (T) new ApiException(new Throwable(response.getMsg()), response.getCode());
        }
    }
}
