package com.vise.xsnow.http.request;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.core.ApiManager;
import com.vise.xsnow.http.func.ApiRetryFunc;
import com.vise.xsnow.http.mode.CacheResult;
import com.vise.xsnow.http.mode.DownProgress;
import com.vise.xsnow.http.subscriber.DownCallbackSubscriber;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Description: 下载请求
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/14 21:50.
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {

    private String dirName = ViseConfig.DEFAULT_DOWNLOAD_DIR;
    private String fileName = ViseConfig.DEFAULT_DOWNLOAD_FILE_NAME;

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
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new Function<ResponseBody, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(final ResponseBody responseBody) throws Exception {
                        return Flowable.create(new FlowableOnSubscribe<DownProgress>() {
                            @Override
                            public void subscribe(FlowableEmitter<DownProgress> subscriber) throws Exception {
                                File dir = getDiskCacheDir(ViseHttp.getContext(), dirName);
                                if (!dir.exists()) {
                                    dir.mkdir();
                                }
                                File file = new File(dir.getPath() + File.separator + fileName);
                                saveFile(subscriber, file, responseBody);
                            }
                        }, BackpressureStrategy.LATEST);
                    }
                })
                .sample(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return null;
    }

    @Override
    protected <T> void execute(ACallback<T> callback) {
        DisposableObserver disposableObserver = new DownCallbackSubscriber(callback);
        if (super.tag != null) {
            ApiManager.get().add(super.tag, disposableObserver);
        }
        this.execute(getType(callback)).subscribe(disposableObserver);
    }

    private void saveFile(FlowableEmitter<? super DownProgress> sub, File saveFile, ResponseBody resp) {
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
                sub.onComplete();
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
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + dirName);
    }
}
