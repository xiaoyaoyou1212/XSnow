package com.vise.xsnow.http.request;

/**
 * @Description: 传入自定义Retrofit接口的请求类型
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/7/22 15:11.
 */
public class RetrofitRequest extends BaseRequest<RetrofitRequest> {

    public RetrofitRequest() {

    }

    public <T> T create(Class<T> cls) {
        generateGlobalConfig();
        generateLocalConfig();
        return retrofit.create(cls);
    }

}
