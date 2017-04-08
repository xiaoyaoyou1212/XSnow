package com.vise.xsnow.net.callback;

/**
 * @Description: 上传进度回调接口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-08 15:03
 */
public interface UploadProgressCallback {
    void onProgress(long currentLength, long totalLength, float percent);
}
