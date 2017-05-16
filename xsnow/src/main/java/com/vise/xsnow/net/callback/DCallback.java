package com.vise.xsnow.net.callback;

/**
 * @Description: 下载进度回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/15 10:55.
 */
public interface DCallback {
    void onProgress(long currentLength, long totalLength);
    void onComplete();
    void onFail(int errCode, String errMsg);
}
