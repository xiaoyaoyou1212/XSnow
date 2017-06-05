package com.vise.xsnow.net.request;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.func.ApiRetryFunc;
import com.vise.xsnow.net.mode.CacheResult;
import com.vise.xsnow.net.mode.DownProgress;
import com.vise.xsnow.net.subscriber.ApiCallbackSubscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Description: 下载请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 21:50.
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {

    protected String dirName = ViseConfig.DEFAULT_DOWNLOAD_DIR;
    protected String fileName = ViseConfig.DEFAULT_DOWNLOAD_FILE_NAME;

    public DownloadRequest() {
    }

    public DownloadRequest setDirName(String dirName) {
        if (!TextUtils.isEmpty(dirName)) {
            this.dirName = dirName;
        }
        return this;
    }

    public DownloadRequest setFileName(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            this.fileName = fileName;
        }
        return this;
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        return (Observable<T>) apiService
                .downFile(suffixUrl, params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .flatMap(new Func1<ResponseBody, Observable<DownProgress>>() {
                    @Override
                    public Observable<DownProgress> call(final ResponseBody response) {
                        return Observable.create(new Observable.OnSubscribe<DownProgress>() {
                            @Override
                            public void call(Subscriber<? super DownProgress> subscriber) {
                                File dir = getDiskCacheDir(ViseNet.getContext(), dirName);
                                if (!dir.exists()) {
                                    dir.mkdir();
                                }
                                File file = new File(dir.getPath() + File.separator + fileName);
                                saveFile(subscriber, file, response);
                            }
                        });
                    }
                })
                .sample(1, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return null;
    }

    @Override
    protected <T> Subscription execute(Context context, ACallback<T> callback) {
        return this.execute(getType(callback))
                .subscribe(new ApiCallbackSubscriber(context, callback));
    }

    private void saveFile(Subscriber<? super DownProgress> sub, File saveFile, ResponseBody resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[8192];

                DownProgress downProgress = new DownProgress();
                inputStream = resp.byteStream();
                outputStream = new FileOutputStream(saveFile);

                long contentLength = resp.contentLength();
                downProgress.setTotalSize(contentLength);

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    downProgress.setDownloadSize(downloadSize);
                    sub.onNext(downProgress);
                }
                outputStream.flush();
                sub.onCompleted();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (IOException e) {
            sub.onError(e);
        }
    }

    private File getDiskCacheDir(Context context, String dirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + dirName);
    }
}
