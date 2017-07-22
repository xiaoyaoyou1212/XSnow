package com.vise.snowdemo.api;

import com.vise.snowdemo.mode.AuthorModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @Description: 自定义Retrofit接口服务
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/7/22 23:36.
 */
public interface AuthorService {
    @GET("getAuthor")
    Observable<AuthorModel> getAuthor();
}
