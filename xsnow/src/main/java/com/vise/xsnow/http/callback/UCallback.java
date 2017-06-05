package com.vise.xsnow.http.callback;

/**
 * @Description: 上传进度回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/15 10:58.
 */
public interface UCallback {
    void onProgress(long currentLength, long totalLength, float percent);
    void onFail(int errCode, String errMsg);
}
