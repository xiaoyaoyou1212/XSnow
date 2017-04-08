package com.vise.xsnow.net.interceptor;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.vise.log.ViseLog;
import com.vise.xsnow.common.GSONUtil;
import com.vise.xsnow.net.api.ViseApi;
import com.vise.xsnow.net.mode.ApiCode;
import com.vise.xsnow.net.mode.ApiResult;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
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
public class HttpResponseInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = process(chain);
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null && mediaType.subtype().equals("json")) {
            return true;
        }
        return false;
    }

    private Response process(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        String bodyString = buffer.clone().readString(charset);
        ViseLog.i("<-- HTTP Interceptor:" + bodyString + " host:" + request.url().toString());
        boolean isText = isText(contentType);
        if (!isText) {
            return response;
        }

        if (!TextUtils.isEmpty(bodyString)) {
            ApiResult apiResult = GSONUtil.gson().fromJson(bodyString, ApiResult.class);
            if (apiResult != null) {
                switch (apiResult.getCode()) {
                    case ApiCode.Response.ACCESS_TOKEN_EXPIRED: //AccessToken错误或已过期
                        return processAccessTokenError(chain, request);
                    case ApiCode.Response.REFRESH_TOKEN_EXPIRED://RefreshToken错误或已过期
                        return processRefreshTokenError(chain, request);
                    case ApiCode.Response.OTHER_PHONE_LOGINED://帐号在其它手机已登录
//                        notifyLoginExit(apiResult.getMsg());
                        break;
                    case ApiCode.Response.SIGN_ERROR://签名错误
                        return processSignError(chain, request);
                    case ApiCode.Response.TIMESTAMP_ERROR://timestamp过期
                        Type type = new TypeToken<ApiResult<Long>>() {}.getType();
                        ApiResult<Long> timestamp = GSONUtil.gson().fromJson(bodyString, type);
                        ViseLog.e("<-- TIMESTAMP_ERROR:" + timestamp.getData());
//                        TokenManager.getInstance().setTimestamp(timestamp.getData());
                        return processTimestampError(chain, request);
                    default:
                        break;
                }
            }
        }
        return response;
    }

    /**
     * 处理签名
     *
     * @param body
     * @param path
     * @return
     */
    private String processSign(FormBody body, String path) {
        /*HetParamsMerge het = new HetParamsMerge();
        het.setPath(path);
        boolean isSign = false;
        for (int i = 0; i < body.size(); i++) {
            String name = body.encodedName(i);
            String value = body.encodedValue(i);
            if (name.equals(ComParamContact.Common.SIGN)) {
                isSign = true;
                continue;
            }
            if (name.equals(ComParamContact.Common.ACCESSTOKEN)) {
                value = TokenManager.getInstance().getAuthModel().getAccessToken();
            } else if (name.equals(ComParamContact.Common.REFRESH_TOKEN)) {
                value = TokenManager.getInstance().getAuthModel().getRefreshToken();
            } else if (name != null && name.equals(ComParamContact.Common.TIMESTAMP)) {
                value = String.valueOf(TokenManager.getInstance().getTimestamp());
            }
            het.add(name, value);
        }
        if (isSign) {
            return het.sign();
        }*/
        return null;
    }

    /**
     * 处理签名错误
     *
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    private Response processSignError(Chain chain, Request request) throws IOException {
        String method = request.method();
        // create a new request and modify it accordingly using the new token
        FormBody oldBody = (FormBody) request.body();
        if (oldBody == null) {
            if (request.method().equalsIgnoreCase("GET")) {
                oldBody = getRequestParams(request.url().query());
            } else {
                return chain.proceed(request);
            }
        }
        String path = request.url().pathSegments().get(0);
        String sign = processSign(oldBody, path);
        FormBody.Builder newBody = new FormBody.Builder();
        for (int i = 0; i < oldBody.size(); i++) {
            String name = oldBody.encodedName(i);
            String value = oldBody.encodedValue(i);
            if (!TextUtils.isEmpty(name)) {
                /*if (name.equals(ComParamContact.Common.SIGN)) {
                    if (!TextUtils.isEmpty(sign)) {
                        value = sign;
                    }
                }*/
            }
            newBody.add(name, value);
        }

        Request newRequest;
        if (method.equalsIgnoreCase("GET")) {
            String url = packageParams(newBody.build());
            ViseLog.i("<-- SignError NewUrl:" + url);
            HttpUrl newHttpUrl = request.url().newBuilder().query(url).build();
            newRequest = request.newBuilder().url(newHttpUrl).get().build();
        } else {
            newRequest = request.newBuilder().post(newBody.build()).build();
        }
        return chain.proceed(newRequest);
    }

    /**
     * 处理刷新Token错误
     *
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    private Response processRefreshTokenError(Chain chain, Request request) throws IOException {
//        return processError(chain, request, ComParamContact.Common.REFRESH_TOKEN, TokenManager.getInstance().getAuthModel().getRefreshToken());
        return null;
    }

    /**
     * 处理AccessToke错误
     *
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    private Response processAccessTokenError(Chain chain, Request request) throws IOException {
//        return processError(chain, request, ComParamContact.Common.ACCESSTOKEN, TokenManager.getInstance().getAuthModel().getAccessToken());
        return null;
    }

    /**
     * 处理时间戳过期错误
     *
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    private Response processTimestampError(Chain chain, Request request) throws IOException {
//        return processError(chain, request, ComParamContact.Common.TIMESTAMP, TokenManager.getInstance().getTimestamp() + "");
        return null;
    }

    /**
     * 处理网络请求出现的业务错误
     *
     * @param chain
     * @param request
     * @param key
     * @param data
     * @return
     * @throws IOException
     */
    private Response processError(Chain chain, Request request, String key, String data) throws IOException {
        // create a new request and modify it accordingly using the new token
        String method = request.method();
        FormBody oldBody = (FormBody) request.body();
        if (oldBody == null) {
            if (method.equalsIgnoreCase("GET")) {
                oldBody = getRequestParams(request.url().query());
            } else {
                return chain.proceed(request);
            }
        }
        FormBody.Builder newBody = new FormBody.Builder();
        for (int i = 0; i < oldBody.size(); i++) {
            String name = oldBody.encodedName(i);
            String value = oldBody.encodedValue(i);
            if (!TextUtils.isEmpty(name)) {
                if (name.equals(key)) {
                    value = data;
                }
                /*if (name.equals(ComParamContact.Common.SIGN)) {
                    String path = request.url().pathSegments().get(0);
                    String ret = processSign(oldBody, path);
                    if (!TextUtils.isEmpty(ret)) {
                        value = ret;
                    }
                }*/
            }
            newBody.add(name, value);
        }

        Request newRequest;
        if (method.equalsIgnoreCase("GET")) {
            String url = packageParams(newBody.build());
            ViseLog.i("<-- Business Error NewUrl:" + url);
            HttpUrl newHttpUrl = request.url().newBuilder().query(url).build();
            newRequest = request.newBuilder().url(newHttpUrl).get().build();
        } else {
            newRequest = request.newBuilder().post(newBody.build()).build();
        }
        return chain.proceed(newRequest);
    }

    /**
     * 处理Token
     *
     * @param chain
     * @param request
     * @return
     * @throws IOException
     */
    private Response processHetException(Chain chain, Request request) throws IOException {
        // create a new request and modify it accordingly using the new token
        FormBody oldBody = (FormBody) request.body();
        FormBody.Builder newBody = new FormBody.Builder();
        for (int i = 0; i < oldBody.size(); i++) {
            String name = oldBody.encodedName(i);
            String value = oldBody.encodedValue(i);
            /*if (!TextUtils.isEmpty(name)) {
                if (name.equals(ComParamContact.Common.ACCESSTOKEN)) {
                    value = TokenManager.getInstance().getAuthModel().getAccessToken();
                } else if (name.equals(ComParamContact.Common.REFRESH_TOKEN)) {
                    value = TokenManager.getInstance().getAuthModel().getRefreshToken();
                } else if (name != null && name.equals(ComParamContact.Common.TIMESTAMP)) {
                    value = String.valueOf(TokenManager.getInstance().getTimestamp());
                }
            }*/
            newBody.add(name, value);
        }

        Request newRequest = request.newBuilder().post(newBody.build()).build();
        return chain.proceed(newRequest);
    }

    /**
     * 将GET请求的参数封装成FormBody
     *
     * @param params
     * @return
     */
    private FormBody getRequestParams(String params) {
        if (params == null) {
            return null;
        }
        String[] strArr = params.split("&");
        if (strArr == null) {
            return null;
        }

        TreeMap<String, String> map = new TreeMap<>();
        FormBody.Builder fBuilder = new FormBody.Builder();
        for (String s : strArr) {
            String[] sArr = s.split("=");
            if (sArr.length < 2)
                continue;
            map.put(sArr[0], sArr[1]);
            fBuilder.add(sArr[0], sArr[1]);
        }
        FormBody formBody = fBuilder.build();
        return formBody;
    }

    /**
     * 封装参数
     *
     * @param oldBody
     * @return
     */
    private String packageParams(FormBody oldBody) {
        List<String> namesAndValues = new ArrayList<>();
        for (int i = 0; i < oldBody.size(); i++) {
            String name = oldBody.encodedName(i);
            String value = oldBody.encodedValue(i);
            if (!TextUtils.isEmpty(name)) {
                namesAndValues.add(name);
                namesAndValues.add(value);
            }
        }
        StringBuilder sb = new StringBuilder();
        namesAndValuesToQueryString(sb, namesAndValues);
        return sb.toString();
    }

    /**
     * 合并GET参数
     *
     * @param out
     * @param namesAndValues
     */
    private void namesAndValuesToQueryString(StringBuilder out, List<String> namesAndValues) {
        for (int i = 0, size = namesAndValues.size(); i < size; i += 2) {
            String name = namesAndValues.get(i);
            String value = namesAndValues.get(i + 1);
            if (i > 0) out.append('&');
            out.append(name);
            if (value != null) {
                out.append('=');
                out.append(value);
            }
        }
    }
}
