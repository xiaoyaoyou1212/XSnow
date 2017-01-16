package com.vise.xsnow.download.core;

import android.content.Context;
import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRange;
import com.vise.xsnow.net.api.ApiService;
import com.vise.xsnow.net.api.ViseApi;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:55.
 */
public class DownHelper {
    private static final String TEST_RANGE_SUPPORT = "bytes=0-";

    private int MAX_RETRY_COUNT = 3;

    private ApiService mDownloadApi;
    private FileHelper mFileHelper;
    private DownTypeFactory mFactory;

    private Map<String, String[]> mDownloadRecord;

    public DownHelper(Context context) {
        mDownloadRecord = new HashMap<>();
        mFileHelper = new FileHelper();
        mDownloadApi = new ViseApi.Builder(context).build().create(ApiService.class);
        mFactory = new DownTypeFactory(this);
    }

    public void setRetrofit(Retrofit retrofit) {
        mDownloadApi = retrofit.create(ApiService.class);
    }

    public void setDefaultSavePath(String defaultSavePath) {
        mFileHelper.setDefaultSavePath(defaultSavePath);
    }

    public void setMaxRetryCount(int MAX_RETRY_COUNT) {
        this.MAX_RETRY_COUNT = MAX_RETRY_COUNT;
    }

    public String[] getFileSavePaths(String savePath) {
        return mFileHelper.getRealDirectoryPaths(savePath);
    }

    public String[] getRealFilePaths(String saveName, String savePath) {
        return mFileHelper.getRealFilePaths(saveName, savePath);
    }

    public ApiService getDownloadApi() {
        return mDownloadApi;
    }

    public Boolean retry(Integer integer, Throwable throwable) {
        if (throwable instanceof ProtocolException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(Thread.currentThread().getName() +
                        " we got an error in the underlying protocol, such as a TCP error, retry to connect " +
                        integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof UnknownHostException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(Thread.currentThread().getName() +
                        " no network, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof HttpException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(Thread.currentThread().getName() +
                        " had non-2XX http error, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof SocketTimeoutException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(Thread.currentThread().getName() +
                        " socket time out,retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof ConnectException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(TextUtils.concat(Thread.currentThread().getName(), " ", throwable.getMessage(),
                        ". retry to connect ", String.valueOf(integer), " times").toString());
                return true;
            }
            return false;
        } else if (throwable instanceof SocketException) {
            if (integer < MAX_RETRY_COUNT + 1) {
                ViseLog.w(Thread.currentThread().getName() +
                        " a network or conversion error happened, retry to connect " + integer + " times");
                return true;
            }
            return false;
        } else if (throwable instanceof CompositeException) {
            ViseLog.w(throwable.getMessage());
            return false;
        } else {
            ViseLog.w(throwable);
            return false;
        }
    }

    public int getMaxThreads() {
        return mFileHelper.getMaxThreads();
    }

    public void setMaxThreads(int MAX_THREADS) {
        mFileHelper.setMaxThreads(MAX_THREADS);
    }

    public void prepareNormalDownload(String url, long fileLength, String lastModify) throws IOException,
            ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getFile(url), fileLength, lastModify);
    }

    public void saveNormalFile(Subscriber<? super DownProgress> sub, String url, Response<ResponseBody> resp) {
        mFileHelper.saveFile(sub, getFile(url), resp);
    }

    public DownRange readDownloadRange(String url, int i) throws IOException {
        return mFileHelper.readDownloadRange(getTempFile(url), i);
    }

    public void prepareMultiThreadDownload(String url, long fileLength, String lastModify) throws IOException,
            ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getTempFile(url), getFile(url),
                fileLength, lastModify);
    }

    public void saveRangeFile(Subscriber<? super DownProgress> subscriber, int i, long start, long end,
                              String url, ResponseBody response) {
        mFileHelper.saveFile(subscriber, i, start, end, getTempFile(url), getFile(url), response);
    }

    public Observable<DownProgress> downloadDispatcher(final String url, final String saveName,
                                                       final String savePath, final Context context,
                                                       final boolean autoInstall) {
        if (isRecordExists(url)) {
            return Observable.error(new Throwable("This url download task already exists, so do nothing."));
        }
        try {
            addDownloadRecord(url, saveName, savePath);
        } catch (IOException e) {
            return Observable.error(e);
        }
        return getDownType(url)
                .flatMap(new Func1<DownType, Observable<DownProgress>>() {
                    @Override
                    public Observable<DownProgress> call(DownType type) {
                        try {
                            type.prepareDownload();
                            return type.startDownload();
                        } catch (IOException e) {
                            return Observable.error(e);
                        } catch (ParseException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (autoInstall) {
                            if (context == null) {
                                throw new IllegalStateException("Context is NULL! You should call " +
                                        "#RxDownload.context(Context context)# first!");
                            }
                            FileHelper.Utils.installApk(context, new File(getRealFilePaths(saveName, savePath)[0]));
                        }
                        deleteDownloadRecord(url);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof CompositeException) {
                            ViseLog.w(throwable.getMessage());
                        } else {
                            ViseLog.w(throwable);
                        }
                        deleteDownloadRecord(url);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        deleteDownloadRecord(url);
                    }
                });
    }

    public Observable<DownType> requestHeaderWithIfRangeByGet(final String url) throws IOException {
        return getDownloadApi()
                .requestWithIfRange(TEST_RANGE_SUPPORT, getLastModify(url), url)
                .map(new Func1<Response<Void>, DownType>() {
                    @Override
                    public DownType call(Response<Void> resp) {
                        if (FileHelper.Utils.serverFileNotChange(resp)) {
                            return getWhenServerFileNotChange(resp, url);
                        } else if (FileHelper.Utils.serverFileChanged(resp)) {
                            return getWhenServerFileChanged(resp, url);
                        } else {
                            throw new RuntimeException("unknown error");
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return retry(integer, throwable);
                    }
                });
    }

    private void addDownloadRecord(String url, String saveName, String savePath) throws IOException {
        mFileHelper.createDirectories(savePath);
        mDownloadRecord.put(url, getRealFilePaths(saveName, savePath));
    }

    private boolean isRecordExists(String url) {
        return mDownloadRecord.get(url) != null;
    }

    private void deleteDownloadRecord(String url) {
        mDownloadRecord.remove(url);
    }

    private String getLastModify(String url) throws IOException {
        return mFileHelper.getLastModify(getLastModifyFile(url));
    }

    private boolean downloadNotComplete(String url) throws IOException {
        return mFileHelper.downloadNotComplete(getTempFile(url));
    }

    private boolean downloadNotComplete(String url, long contentLength) {
        return getFile(url).length() != contentLength;
    }

    private boolean needReDownload(String url, long contentLength) throws IOException {
        return tempFileNotExists(url) || tempFileDamaged(url, contentLength);
    }

    private boolean downloadFileExists(String url) {
        return getFile(url).exists();
    }

    private boolean tempFileDamaged(String url, long fileLength) throws IOException {
        return mFileHelper.tempFileDamaged(getTempFile(url), fileLength);
    }

    private boolean tempFileNotExists(String url) {
        return !getTempFile(url).exists();
    }

    private File getFile(String url) {
        return new File(mDownloadRecord.get(url)[0]);
    }

    private File getTempFile(String url) {
        return new File(mDownloadRecord.get(url)[1]);
    }

    private File getLastModifyFile(String url) {
        return new File(mDownloadRecord.get(url)[2]);
    }

    private Observable<DownType> getDownType(String url) {
        if (downloadFileExists(url)) {
            try {
                return getWhenFileExists(url);
            } catch (IOException e) {
                return getWhenFileNotExists(url);
            }
        } else {
            return getWhenFileNotExists(url);
        }
    }

    private Observable<DownType> getWhenFileNotExists(final String url) {
        return getDownloadApi()
                .getHttpHeader(TEST_RANGE_SUPPORT, url)
                .map(new Func1<Response<Void>, DownType>() {
                    @Override
                    public DownType call(Response<Void> response) {
                        if (FileHelper.Utils.notSupportRange(response)) {
                            return mFactory.url(url)
                                    .fileLength(FileHelper.Utils.contentLength(response))
                                    .lastModify(FileHelper.Utils.lastModify(response))
                                    .buildNormalDownload();
                        } else {
                            return mFactory.url(url)
                                    .lastModify(FileHelper.Utils.lastModify(response))
                                    .fileLength(FileHelper.Utils.contentLength(response))
                                    .buildMultiDownload();
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return retry(integer, throwable);
                    }
                });
    }

    private Observable<DownType> getWhenFileExists(final String url) throws IOException {
        return getDownloadApi()
                .getHttpHeaderWithIfRange(TEST_RANGE_SUPPORT, getLastModify(url), url)
                .map(new Func1<Response<Void>, DownType>() {
                    @Override
                    public DownType call(Response<Void> resp) {
                        if (FileHelper.Utils.serverFileNotChange(resp)) {
                            return getWhenServerFileNotChange(resp, url);
                        } else if (FileHelper.Utils.serverFileChanged(resp)) {
                            return getWhenServerFileChanged(resp, url);
                        } else if (FileHelper.Utils.requestRangeNotSatisfiable(resp)) {
                            return mFactory.url(url)
                                    .fileLength(FileHelper.Utils.contentLength(resp))
                                    .lastModify(FileHelper.Utils.lastModify(resp))
                                    .buildRequestRangeNotSatisfiable();
                        } else {
                            throw new RuntimeException("unknown error");
                        }
                    }
                }).retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return retry(integer, throwable);
                    }
                });
    }

    private DownType getWhenServerFileChanged(Response<Void> resp, String url) {
        if (FileHelper.Utils.notSupportRange(resp)) {
            return mFactory.url(url)
                    .fileLength(FileHelper.Utils.contentLength(resp))
                    .lastModify(FileHelper.Utils.lastModify(resp))
                    .buildNormalDownload();
        } else {
            return mFactory.url(url)
                    .fileLength(FileHelper.Utils.contentLength(resp))
                    .lastModify(FileHelper.Utils.lastModify(resp))
                    .buildMultiDownload();
        }
    }

    private DownType getWhenServerFileNotChange(Response<Void> resp, String url) {
        if (FileHelper.Utils.notSupportRange(resp)) {
            return getWhenNotSupportRange(resp, url);
        } else {
            return getWhenSupportRange(resp, url);
        }
    }

    private DownType getWhenSupportRange(Response<Void> resp, String url) {
        long contentLength = FileHelper.Utils.contentLength(resp);
        try {
            if (needReDownload(url, contentLength)) {
                return mFactory.url(url)
                        .fileLength(contentLength)
                        .lastModify(FileHelper.Utils.lastModify(resp))
                        .buildMultiDownload();
            }
            if (downloadNotComplete(url)) {
                return mFactory.url(url)
                        .fileLength(contentLength)
                        .lastModify(FileHelper.Utils.lastModify(resp))
                        .buildContinueDownload();
            }
        } catch (IOException e) {
            ViseLog.w("download record file may be damaged,so we will re download");
            return mFactory.url(url)
                    .fileLength(contentLength)
                    .lastModify(FileHelper.Utils.lastModify(resp))
                    .buildMultiDownload();
        }
        return mFactory.fileLength(contentLength).buildAlreadyDownload();
    }

    private DownType getWhenNotSupportRange(Response<Void> resp, String url) {
        long contentLength = FileHelper.Utils.contentLength(resp);
        if (downloadNotComplete(url, contentLength)) {
            return mFactory.url(url)
                    .fileLength(contentLength)
                    .lastModify(FileHelper.Utils.lastModify(resp))
                    .buildNormalDownload();
        } else {
            return mFactory.fileLength(contentLength).buildAlreadyDownload();
        }
    }
}
