package com.vise.netexpand.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.netexpand.mode.ApiResult;
import com.vise.netexpand.temp.DefaultResponseState;
import com.vise.netexpand.temp.IResponseState;
import com.vise.netexpand.temp.Utils;
import com.vise.xsnow.common.GsonUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @Description: Http响应拦截
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-08 15:15
 */
public abstract class HttpResponseInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private IResponseState responseState;

    public HttpResponseInterceptor() {
        this(new DefaultResponseState());
    }

    public HttpResponseInterceptor(IResponseState responseState) {
        this.responseState = responseState;
        Utils.checkIllegalArgument(responseState, "this responseState is null.");
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return process(chain);
    }

    private Response process(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return response;
        }
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        if (charset == null) {
            return response;
        }
        String bodyString = buffer.clone().readString(charset);
        ViseLog.i("<-- HTTP Interceptor:" + bodyString + " host:" + request.url().toString());
        boolean isText = isText(contentType);
        if (!isText) {
            return response;
        }

        if (!TextUtils.isEmpty(bodyString)) {
            ApiResult apiResult = GsonUtil.gson().fromJson(bodyString, ApiResult.class);
            if (apiResult != null) {
                if (apiResult.getCode() == responseState.accessTokenExpired()) {//AccessToken错误或已过期
                    return processAccessTokenExpired(chain, request);
                } else if (apiResult.getCode() == responseState.refreshTokenExpired()) {//RefreshToken错误或已过期
                    return processRefreshTokenExpired(chain, request);
                } else if (apiResult.getCode() == responseState.otherPhoneLogin()) {//帐号在其它手机已登录
                    return processOtherPhoneLogin(chain, request);
                } else if (apiResult.getCode() == responseState.signError()) {//签名错误
                    return processSignError(chain, request);
                } else if (apiResult.getCode() == responseState.timestampError()) {//timestamp过期
                    return processTimestampError(chain, request);
                } else if (apiResult.getCode() == responseState.noAccessToken()) {//缺少授权信息
                    return processNoAccessToken(chain, request);
                } else {
                    if (responseState.otherError() != null && responseState.otherError().size() > 0) {
                        for (int errorCode : responseState.otherError()) {
                            if (apiResult.getCode() == errorCode) {
                                return processOtherError(errorCode, chain, request);
                            }
                        }
                    }
                }
            }
        }
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        return mediaType.subtype() != null && mediaType.subtype().equals("json");
    }

    /**
     * AccessToken错误或已过期
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processAccessTokenExpired(Chain chain, Request request);

    /**
     * RefreshToken错误或已过期
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processRefreshTokenExpired(Chain chain, Request request);

    /**
     * 帐号在其它手机已登录
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processOtherPhoneLogin(Chain chain, Request request);

    /**
     * 签名错误
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processSignError(Chain chain, Request request);

    /**
     * timestamp过期
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processTimestampError(Chain chain, Request request);

    /**
     * 缺少授权信息
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processNoAccessToken(Chain chain, Request request);

    /**
     * 其他异常处理
     *
     * @param chain
     * @param request
     * @return
     */
    abstract Response processOtherError(int errorCode, Chain chain, Request request);

}
