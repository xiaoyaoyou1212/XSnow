package com.vise.xsnow.net.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.vise.xsnow.net.mode.ApiCode;
import com.vise.xsnow.net.mode.ApiResult;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @Description: API异常统一管理
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-30 17:59
 */
public class ApiException extends Exception {

    private final int code;
    private String message;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDisplayMessage() {
        return message + "(code:" + code + ")";
    }

    public static boolean isSuccess(ApiResult apiResult) {
        if (apiResult == null) {
            return false;
        }
        if (apiResult.getCode() == ApiCode.Response.HTTP_SUCCESS || ignoreSomeIssue(apiResult.getCode())) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean ignoreSomeIssue(int code) {
        switch (code) {
            case ApiCode.Response.TIMESTAMP_ERROR://时间戳过期
            case ApiCode.Response.ACCESS_TOKEN_EXPIRED://AccessToken错误或已过期
            case ApiCode.Response.REFRESH_TOKEN_EXPIRED://RefreshToken错误或已过期
            case ApiCode.Response.OTHER_PHONE_LOGINED: //帐号在其它手机已登录
            case ApiCode.Response.SIGN_ERROR://签名错误
                return true;
            default:
                return false;
        }
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ApiCode.Request.HTTP_ERROR);
            switch (httpException.code()) {
                case ApiCode.Http.UNAUTHORIZED:
                case ApiCode.Http.FORBIDDEN:
                case ApiCode.Http.NOT_FOUND:
                case ApiCode.Http.REQUEST_TIMEOUT:
                case ApiCode.Http.GATEWAY_TIMEOUT:
                case ApiCode.Http.INTERNAL_SERVER_ERROR:
                case ApiCode.Http.BAD_GATEWAY:
                case ApiCode.Http.SERVICE_UNAVAILABLE:
                default:
                    ex.message = "NETWORK_ERROR";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ApiException(e, ApiCode.Request.PARSE_ERROR);
            ex.message = "PARSE_ERROR";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ApiCode.Request.NETWORK_ERROR);
            ex.message = "NETWORK_ERROR";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ApiCode.Request.SSL_ERROR);
            ex.message = "SSL_ERROR";
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ApiCode.Request.TIMEOUT_ERROR);
            ex.message = "TIMEOUT_ERROR";
            return ex;
        } else {
            ex = new ApiException(e, ApiCode.Request.UNKNOWN);
            ex.message = "UNKNOWN";
            return ex;
        }
    }

}
