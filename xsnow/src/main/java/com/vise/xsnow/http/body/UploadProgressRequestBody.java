package com.vise.xsnow.http.body;

import com.vise.log.ViseLog;
import com.vise.xsnow.http.callback.UCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @Description: 上传进度请求实体类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-04-08 14:48
 */
public class UploadProgressRequestBody extends RequestBody {

    private RequestBody requestBody;
    private UCallback callback;
    private long lastTime;
    private long currentTime;

    public UploadProgressRequestBody(RequestBody requestBody, UCallback callback) {
        this.requestBody = requestBody;
        this.callback = callback;
        if (requestBody == null || callback == null) {
            throw new NullPointerException("this requestBody and callback must not null.");
        }
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private final class CountingSink extends ForwardingSink {
        //当前字节长度
        private long currentLength = 0L;
        //总字节长度，避免多次调用contentLength()方法
        private long totalLength = 0L;

        public CountingSink(Sink sink) {
            super(sink);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            //增加当前写入的字节数
            currentLength += byteCount;
            //获得contentLength的值，后续不再调用
            if (totalLength == 0) {
                totalLength = contentLength();
            }
            currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 100 || lastTime == 0 || currentLength == totalLength) {
                lastTime = currentTime;
                Observable.just(currentLength).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        ViseLog.i("upload progress currentLength:" + currentLength + ",totalLength:" + totalLength);
                        callback.onProgress(currentLength, totalLength, (100.0f * currentLength) / totalLength);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onFail(-1, throwable.getMessage());
                    }
                });
            }
        }
    }

}
