package com.vise.netexpand.common;

import com.vise.netexpand.mode.ApiResult;
import com.vise.netexpand.mode.ResponseCode;

/**
 * @Description: 响应处理帮助类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/17 23:59.
 */
public class ResponseHelper {

    public static boolean isSuccess(ApiResult apiResult) {
        if (apiResult == null) {
            return false;
        }
        return apiResult.getCode() == ResponseCode.HTTP_SUCCESS || ignoreSomeIssue(apiResult.getCode());
    }

    private static boolean ignoreSomeIssue(int code) {
        switch (code) {
            case ResponseCode.TIMESTAMP_ERROR://时间戳过期
            case ResponseCode.ACCESS_TOKEN_EXPIRED://AccessToken错误或已过期
            case ResponseCode.REFRESH_TOKEN_EXPIRED://RefreshToken错误或已过期
            case ResponseCode.OTHER_PHONE_LOGIN://帐号在其它手机已登录
            case ResponseCode.NO_ACCESS_TOKEN://缺少授权信息,没有AccessToken
            case ResponseCode.SIGN_ERROR://签名错误
                return true;
            default:
                return false;
        }
    }
}
