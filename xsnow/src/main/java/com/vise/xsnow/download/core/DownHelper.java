package com.vise.xsnow.download.core;

import android.text.TextUtils;

import com.vise.log.ViseLog;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRange;

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
 * @Description: 下载帮助类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:55.
 */
public class DownHelper {
    private static final String TEST_RANGE_SUPPORT = "bytes=0-";

    private int MAX_RETRY_COUNT = 3;

    private DownApiService mDownloadApi;
    private FileHelper mFileHelper;
    private DownTypeFactory mFactory;

    private Map<String, String[]> mDownloadRecord;

    public DownHelper() {
        mDownloadRecord = new HashMap<>();
        mFileHelper = new FileHelper();
        mDownloadApi = DownRetrofit.getInstance().create(DownApiService.class);
        mFactory = new DownTypeFactory(this);
    }

    /**
     * 设置自定义retrofit
     * @param retrofit
     */
    public void setRetrofit(Retrofit retrofit) {
        mDownloadApi = retrofit.create(DownApiService.class);
    }

    /**
     * 设置默认保存路径
     * @param defaultSavePath
     */
    public void setDefaultSavePath(String defaultSavePath) {
        mFileHelper.setDefaultSavePath(defaultSavePath);
    }

    /**
     * 设置最大重试次数
     * @param MAX_RETRY_COUNT
     */
    public void setMaxRetryCount(int MAX_RETRY_COUNT) {
        this.MAX_RETRY_COUNT = MAX_RETRY_COUNT;
    }

    /**
     * 获取文件保存路径
     * @param savePath
     * @return
     */
    public String[] getFileSavePaths(String savePath) {
        return mFileHelper.getRealDirectoryPaths(savePath);
    }

    /**
     * 获取文件保存路径
     * @param saveName
     * @param savePath
     * @return
     */
    public String[] getRealFilePaths(String saveName, String savePath) {
        return mFileHelper.getRealFilePaths(saveName, savePath);
    }

    /**
     * 获取服务操作接口
     * @return
     */
    public DownApiService getDownloadApi() {
        return mDownloadApi;
    }

    /**
     * 获取最大线程数
     * @return
     */
    public int getMaxThreads() {
        return mFileHelper.getMaxThreads();
    }

    /**
     * 设置最大线程数
     * @param MAX_THREADS
     */
    public void setMaxThreads(int MAX_THREADS) {
        mFileHelper.setMaxThreads(MAX_THREADS);
    }

    /**
     * 准备普通下载
     * @param url
     * @param fileLength
     * @param lastModify
     * @throws IOException
     * @throws ParseException
     */
    public void prepareNormalDownload(String url, long fileLength, String lastModify) throws IOException, ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getFile(url), fileLength, lastModify);
    }

    /**
     * 保存普通下载文件
     * @param sub
     * @param url
     * @param resp
     */
    public void saveNormalFile(Subscriber<? super DownProgress> sub, String url, Response<ResponseBody> resp) {
        mFileHelper.saveFile(sub, getFile(url), resp);
    }

    /**
     * 读取下载范围
     * @param url
     * @param i
     * @return
     * @throws IOException
     */
    public DownRange readDownloadRange(String url, int i) throws IOException {
        return mFileHelper.readDownloadRange(getTempFile(url), i);
    }

    /**
     * 准备多线程下载
     * @param url
     * @param fileLength
     * @param lastModify
     * @throws IOException
     * @throws ParseException
     */
    public void prepareMultiThreadDownload(String url, long fileLength, String lastModify) throws IOException, ParseException {
        mFileHelper.prepareDownload(getLastModifyFile(url), getTempFile(url), getFile(url), fileLength, lastModify);
    }

    /**
     * 保存某个下载范围的临时文件，再进行拼接
     * @param subscriber
     * @param i
     * @param start
     * @param end
     * @param url
     * @param response
     */
    public void saveRangeFile(Subscriber<? super DownProgress> subscriber, int i, long start, long end, String url, ResponseBody response) {
        mFileHelper.saveFile(subscriber, i, start, end, getTempFile(url), getFile(url), response);
    }

    /**
     * 下载重试
     * @param integer
     * @param throwable
     * @return
     */
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

    /**
     * 下载事件分发
     * @param url
     * @param saveName
     * @param savePath
     * @return
     */
    public Observable<DownProgress> downloadDispatcher(final String url, final String saveName, final String savePath) {
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

    /**
     * 通过get方式请求是否支持断点下载
     * @param url
     * @return
     * @throws IOException
     */
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

    /**
     * 添加下载记录
     * @param url
     * @param saveName
     * @param savePath
     * @throws IOException
     */
    private void addDownloadRecord(String url, String saveName, String savePath) throws IOException {
        mFileHelper.createDirectories(savePath);
        mDownloadRecord.put(url, getRealFilePaths(saveName, savePath));
    }

    /**
     * 下载记录是否存在
     * @param url
     * @return
     */
    private boolean isRecordExists(String url) {
        return mDownloadRecord.get(url) != null;
    }

    /**
     * 删除下载记录
     * @param url
     */
    private void deleteDownloadRecord(String url) {
        mDownloadRecord.remove(url);
    }

    /**
     * 获取最后修改时间
     * @param url
     * @return
     * @throws IOException
     */
    private String getLastModify(String url) throws IOException {
        return mFileHelper.getLastModify(getLastModifyFile(url));
    }

    /**
     * 下载未完成文件
     * @param url
     * @return
     * @throws IOException
     */
    private boolean downloadNotComplete(String url) throws IOException {
        return mFileHelper.downloadNotComplete(getTempFile(url));
    }

    /**
     * 下载未完成文件
     * @param url
     * @param contentLength
     * @return
     */
    private boolean downloadNotComplete(String url, long contentLength) {
        return getFile(url).length() != contentLength;
    }

    /**
     * 是否需要重新下载
     * @param url
     * @param contentLength
     * @return
     * @throws IOException
     */
    private boolean needReDownload(String url, long contentLength) throws IOException {
        return tempFileNotExists(url) || tempFileDamaged(url, contentLength);
    }

    /**
     * 下载文件是否已存在
     * @param url
     * @return
     */
    private boolean downloadFileExists(String url) {
        return getFile(url).exists();
    }

    /**
     * 临时文件是否已损坏
     * @param url
     * @param fileLength
     * @return
     * @throws IOException
     */
    private boolean tempFileDamaged(String url, long fileLength) throws IOException {
        return mFileHelper.tempFileDamaged(getTempFile(url), fileLength);
    }

    /**
     * 临时文件不存在
     * @param url
     * @return
     */
    private boolean tempFileNotExists(String url) {
        return !getTempFile(url).exists();
    }

    /**
     * 获取文件
     * @param url
     * @return
     */
    private File getFile(String url) {
        return new File(mDownloadRecord.get(url)[0]);
    }

    /**
     * 获取临时文件
     * @param url
     * @return
     */
    private File getTempFile(String url) {
        return new File(mDownloadRecord.get(url)[1]);
    }

    /**
     * 获取最后下载文件
     * @param url
     * @return
     */
    private File getLastModifyFile(String url) {
        return new File(mDownloadRecord.get(url)[2]);
    }

    /**
     * 获取下载类型
     * @param url
     * @return
     */
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

    /**
     * 获取文件不存在下载类型被观察者
     * @param url
     * @return
     */
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

    /**
     * 获取文件已存在下载类型被观察者
     * @param url
     * @return
     * @throws IOException
     */
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

    /**
     * 获取服务器文件已改变下载类型
     * @param resp
     * @param url
     * @return
     */
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

    /**
     * 获取服务器文件未改变下载类型
     * @param resp
     * @param url
     * @return
     */
    private DownType getWhenServerFileNotChange(Response<Void> resp, String url) {
        if (FileHelper.Utils.notSupportRange(resp)) {
            return getWhenNotSupportRange(resp, url);
        } else {
            return getWhenSupportRange(resp, url);
        }
    }

    /**
     * 获取支持断点下载类型
     * @param resp
     * @param url
     * @return
     */
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

    /**
     * 获取不支持断点下载类型
     * @param resp
     * @param url
     * @return
     */
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
