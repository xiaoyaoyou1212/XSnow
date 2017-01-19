package com.vise.xsnow.download.core;

import com.vise.log.ViseLog;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRange;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @Description: 下载类型，包含下载URL、下载文件大小及最后修改时间
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:54.
 */
public abstract class DownType {
    public String mUrl;
    public long mFileLength;
    public String mLastModify;
    public DownHelper mDownloadHelper;

    public abstract void prepareDownload() throws IOException, ParseException;

    public abstract Observable<DownProgress> startDownload() throws IOException;

    /**
     * 普通下载方式
     */
    static class NormalDownload extends DownType {

        @Override
        public void prepareDownload() throws IOException, ParseException {
            mDownloadHelper.prepareNormalDownload(mUrl, mFileLength, mLastModify);
        }

        private Observable<DownProgress> normalSave(final Response<ResponseBody> response) {
            return Observable.create(new Observable.OnSubscribe<DownProgress>() {
                @Override
                public void call(Subscriber<? super DownProgress> subscriber) {
                    mDownloadHelper.saveNormalFile(subscriber, mUrl, response);
                }
            });
        }

        @Override
        public Observable<DownProgress> startDownload() {
            ViseLog.i("Normal download start!!");
            return mDownloadHelper.getDownloadApi().downloadFile(null, mUrl)
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<Response<ResponseBody>, Observable<DownProgress>>() {
                        @Override
                        public Observable<DownProgress> call(final Response<ResponseBody> response) {
                            return normalSave(response);
                        }
                    }).onBackpressureLatest().retry(new Func2<Integer, Throwable, Boolean>() {
                        @Override
                        public Boolean call(Integer integer, Throwable throwable) {
                            return mDownloadHelper.retry(integer, throwable);
                        }
                    });
        }
    }

    /**
     * 支持断点下载方式
     */
    static class ContinueDownload extends DownType {
        /**
         * 分段下载的任务
         *
         * @param i 下载编号
         * @return Observable
         */
        private Observable<DownProgress> rangeDownloadTask(final int i) {
            return Observable.create(new Observable.OnSubscribe<DownRange>() {
                @Override
                public void call(Subscriber<? super DownRange> subscriber) {
                    try {
                        DownRange range = mDownloadHelper.readDownloadRange(mUrl, i);
                        if (range.getStart() <= range.getEnd()) {
                            subscriber.onNext(range);
                        }
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).flatMap(new Func1<DownRange, Observable<DownProgress>>() {
                @Override
                public Observable<DownProgress> call(final DownRange downRange) {
                    String range = "bytes=" + downRange.getStart() + "-" + downRange.getEnd();
                    return mDownloadHelper.getDownloadApi().downloadFile(range, mUrl)
                            .flatMap(new Func1<Response<ResponseBody>, Observable<DownProgress>>() {
                                @Override
                                public Observable<DownProgress> call(Response<ResponseBody> response) {
                                    return rangeSave(downRange.getStart(), downRange.getEnd(), i, response.body());
                                }
                            });
                }
            }).onBackpressureLatest().retry(new Func2<Integer, Throwable, Boolean>() {
                @Override
                public Boolean call(Integer integer, Throwable throwable) {
                    return mDownloadHelper.retry(integer, throwable);
                }
            });
        }


        /**
         * 保存断点下载的文件,以及下载进度
         *
         * @param start    从start开始
         * @param end      到end结束
         * @param i        下载编号
         * @param response 响应值
         * @return Observable
         */
        private Observable<DownProgress> rangeSave(final long start, final long end, final int i,
                                                   final ResponseBody response) {
            return Observable.create(new Observable.OnSubscribe<DownProgress>() {
                @Override
                public void call(Subscriber<? super DownProgress> subscriber) {
                    mDownloadHelper.saveRangeFile(subscriber, i, start, end, mUrl, response);
                }
            });
        }

        @Override
        public void prepareDownload() throws IOException, ParseException {
            ViseLog.i("Continue download start!!");
        }

        @Override
        public Observable<DownProgress> startDownload() throws IOException {
            List<Observable<DownProgress>> tasks = new ArrayList<>();
            for (int i = 0; i < mDownloadHelper.getMaxThreads(); i++) {
                tasks.add(rangeDownloadTask(i));
            }
            return Observable.mergeDelayError(tasks);
        }
    }

    /**
     * 支持多线程下载方式
     */
    static class MultiThreadDownload extends ContinueDownload {

        @Override
        public void prepareDownload() throws IOException, ParseException {
            mDownloadHelper.prepareMultiThreadDownload(mUrl, mFileLength, mLastModify);
        }

        @Override
        public Observable<DownProgress> startDownload() throws IOException {
            ViseLog.i("Multi Thread download start!!");
            return super.startDownload();
        }
    }

    /**
     * 文件已下载方式
     */
    static class AlreadyDownloaded extends DownType {

        @Override
        public void prepareDownload() throws IOException, ParseException {
            ViseLog.i("File Already downloaded!!");
        }

        @Override
        public Observable<DownProgress> startDownload() throws IOException {
            return Observable.just(new DownProgress(mFileLength, mFileLength));
        }
    }

    /**
     * 不支持断点下载方式
     */
    static class RequestRangeNotSatisfiable extends DownType {

        @Override
        public void prepareDownload() throws IOException, ParseException {

        }

        @Override
        public Observable<DownProgress> startDownload() throws IOException {
            return mDownloadHelper.requestHeaderWithIfRangeByGet(mUrl)
                    .flatMap(new Func1<DownType, Observable<DownProgress>>() {
                        @Override
                        public Observable<DownProgress> call(DownType type) {
                            try {
                                type.prepareDownload();
                                return type.startDownload();
                            } catch (IOException | ParseException e) {
                                return Observable.error(e);
                            }
                        }
                    });
        }
    }
}
