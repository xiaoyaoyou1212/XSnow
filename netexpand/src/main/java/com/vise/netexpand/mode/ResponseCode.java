package com.vise.netexpand.mode;

/**
 * @Description: Response响应码（根据服务器提供文档进行定义）
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-12-30 18:11
 */
public class ResponseCode {
    //HTTP请求成功状态码
    public static final int HTTP_SUCCESS = 0;
    //AccessToken错误或已过期
    public static final int ACCESS_TOKEN_EXPIRED = 10001;
    //RefreshToken错误或已过期
    public static final int REFRESH_TOKEN_EXPIRED = 10002;
    //帐号在其它手机已登录
    public static final int OTHER_PHONE_LOGIN = 10003;
    //时间戳过期
    public static final int TIMESTAMP_ERROR = 10004;
    //缺少授权信息,没有AccessToken
    public static final int NO_ACCESS_TOKEN = 10005;
    //签名错误
    public static final int SIGN_ERROR = 10006;
}
