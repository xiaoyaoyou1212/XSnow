package com.vise.xsnow.net.body;

import com.vise.xsnow.net.callback.DCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;

/**
 * @Description: 下载进度响应实体类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/15 10:46.
 */
public class DownloadProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final DCallback callback;
    private BufferedSource bufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody, DCallback callback) {
        this.responseBody = responseBody;
        this.callback = callback;
        if (responseBody == null || callback == null) {
            throw new NullPointerException("this responseBody and callback must not null.");
        }
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                callback.onProgress(totalBytesRead, responseBody.contentLength(),
                        totalBytesRead / responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }

            @Override
            public Timeout timeout() {
                callback.onFail(-1, "Download TimeOut!");
                return super.timeout();
            }
        };
    }
}
