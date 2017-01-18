package com.vise.xsnow.net.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @Description: 提供的系列接口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-30 16:42
 */
public interface ApiService {
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url() String url, @FieldMap Map<String, String> maps);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postForm(@Url() String url, @FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postJson(@Url() String url, @Body RequestBody jsonBody);

    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @Body Object object);

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> maps);

    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @QueryMap Map<String, String> maps);

    @PUT()
    Observable<ResponseBody> put(@Url() String url, @QueryMap Map<String, String> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadImage(@Url() String url,
                                         @Part("image\"; filename=\"image" + ".jpg") RequestBody
                                                 requestBody);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFile(@Url String fileUrl,
                                        @Part("description") RequestBody description, @Part("files") MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @PartMap() Map<String, RequestBody>
            maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String path, @Part() List<MultipartBody.Part> parts);

}
