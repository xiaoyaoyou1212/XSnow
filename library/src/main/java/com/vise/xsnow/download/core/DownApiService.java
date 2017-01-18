package com.vise.xsnow.download.core;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @Description: 下载API服务
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 22:14.
 */
public interface DownApiService {
    @GET
    @Streaming
    Observable<Response<ResponseBody>> downloadFile(@Header("Range") String range, @Url String url);

    @HEAD
    Observable<Response<Void>> getHttpHeader(@Header("Range") String range, @Url String url);

    @HEAD
    Observable<Response<Void>> getHttpHeaderWithIfRange(@Header("Range") final String range,
                                                        @Header("If-Range") final String lastModify,
                                                        @Url String url);

    @GET
    Observable<Response<Void>> requestWithIfRange(@Header("Range") final String range,
                                                  @Header("If-Range") final String lastModify,
                                                  @Url String url);
}
