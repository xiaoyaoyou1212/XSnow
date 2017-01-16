package com.vise.xsnow.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vise.xsnow.download.core.DownHelper;
import com.vise.xsnow.download.core.DownService;
import com.vise.xsnow.download.core.DownTask;
import com.vise.xsnow.download.db.DownDbManager;
import com.vise.xsnow.download.mode.DownEvent;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownRecord;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:03.
 */
public class ViseDownload {
    private static DownService mDownService;
    private static boolean bound = false;

    private DownHelper mDownloadHelper;

    private Context mContext;

    private boolean mAutoInstall;
    private int MAX_DOWNLOAD_NUMBER = 5;

    private ViseDownload() {
    }

    public static ViseDownload getInstance() {
        return new ViseDownload();
    }

    /**
     * 普通下载时不需要context, 使用Service下载时需要context;
     *
     * @param context context
     * @return ViseDownload
     */
    public ViseDownload context(Context context) {
        this.mContext = context;
        mDownloadHelper = new DownHelper(mContext);
        return this;
    }

    public ViseDownload defaultSavePath(String savePath) {
        mDownloadHelper.setDefaultSavePath(savePath);
        return this;
    }

    public ViseDownload retrofit(Retrofit retrofit) {
        mDownloadHelper.setRetrofit(retrofit);
        return this;
    }

    public ViseDownload maxThread(int max) {
        mDownloadHelper.setMaxThreads(max);
        return this;
    }

    public ViseDownload maxRetryCount(int max) {
        mDownloadHelper.setMaxRetryCount(max);
        return this;
    }

    public ViseDownload maxDownloadNumber(int max) {
        this.MAX_DOWNLOAD_NUMBER = max;
        return this;
    }

    public ViseDownload autoInstall(boolean flag) {
        this.mAutoInstall = flag;
        return this;
    }

    /**
     * get Files.
     * File[] {DownloadFile, TempFile, LastModifyFile}
     *
     * @param saveName saveName
     * @param savePath savePath
     * @return Files
     */
    public File[] getRealFiles(String saveName, String savePath) {
        String[] filePaths = mDownloadHelper.getRealFilePaths(saveName, savePath);
        return new File[]{new File(filePaths[0]), new File(filePaths[1]), new File(filePaths[2])};
    }

    /**
     * Receive the download address for the url download event and download status.
     * 接收下载地址为url的下载事件和下载状态.
     * <p>
     * Note that only receive the download address for the URL.
     * 注意只接收下载地址为url的事件和状态.
     *
     * @param url download url
     * @return Observable<DownProgress>
     */
    public Observable<DownEvent> receiveDownProgress(final String url) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            subscriber.onNext(null);
                        }
                    });
                } else {
                    subscriber.onNext(null);
                }
            }
        }).flatMap(new Func1<Object, Observable<DownEvent>>() {
            @Override
            public Observable<DownEvent> call(Object o) {
                return mDownService.getSubject(ViseDownload.this, url).asObservable().onBackpressureLatest();
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Read all the download record from the database
     * 从数据库中读取所有的下载记录
     *
     * @return Observable<List<DownloadRecord>>
     */
    public Observable<List<DownRecord>> getTotalDownloadRecords() {
        if (mContext == null) {
            return Observable.error(new Throwable("Context is NULL! You should call " +
                    "#ViseDownload.context(Context context)# first!"));
        }
        DownDbManager dataBaseHelper = DownDbManager.getSingleton(mContext);
        return dataBaseHelper.readAllRecords();
    }

    /**
     * Read single download record with url.
     * 从数据库中读取下载地址为url的下载记录
     *
     * @param url download url
     * @return Observable<DownProgress>
     */
    public Observable<DownRecord> getDownloadRecord(String url) {
        if (mContext == null) {
            return Observable.error(new Throwable("Context is NULL! You should call " +
                    "#ViseDownload.context(Context context)# first!"));
        }
        DownDbManager dataBaseHelper = DownDbManager.getSingleton(mContext);
        return dataBaseHelper.readRecord(url);
    }

    /**
     * Suspended download address for the url download task in Service.
     * 暂停Service中下载地址为url的下载任务.
     * <p>
     * Book the download records in the tag database are paused.
     * 同时标记数据库中的下载记录为暂停状态.
     *
     * @param url download url
     */
    public Observable<?> pauseServiceDownload(final String url) {
        return Observable.just(null).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            mDownService.pauseDownload(url);
                        }
                    });
                } else {
                    mDownService.pauseDownload(url);
                }
            }
        });
    }

    /**
     * 取消Service中下载地址为url的下载任务.
     * <p>
     * 同时标记数据库中的下载记录为取消状态.
     * 不会删除已经下载的文件.
     *
     * @param url download url
     */
    public Observable<?> cancelServiceDownload(final String url) {
        return Observable.just(null).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            mDownService.cancelDownload(url);
                        }
                    });
                } else {
                    mDownService.cancelDownload(url);
                }
            }
        });
    }

    /**
     * 删除Service中下载地址为url的下载任务.
     * <p>
     * 同时从数据库中删除该下载记录.
     * 不会删除已经下载的文件.
     *
     * @param url download url
     */
    public Observable<?> deleteServiceDownload(final String url) {
        return Observable.just(null).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            mDownService.deleteDownload(url);
                        }
                    });
                } else {
                    mDownService.deleteDownload(url);
                }
            }
        });
    }

    /**
     * Using Service to download. Just download, can't receive download status.
     * 使用Service下载. 仅仅开始下载, 不会接收下载进度.
     * <p>
     * Un subscribe will not pause download.
     * 取消订阅不会停止下载.
     * <p>
     * If you want receive download status, see {@link #receiveDownProgress(String)}
     * <p>
     * If you want pause download, see {@link #pauseServiceDownload(String)}
     * <p>
     * Also save the download records in the database, if you want get record from database,
     * see  {@link #getDownloadRecord(String)}
     *
     * @param url      download file Url
     * @param saveName download file SaveName
     * @param savePath download file SavePath. If NULL, using default save path {@code /storage/emulated/0/Download/}
     * @return Observable<DownProgress>
     */
    public Observable<Object> serviceDownload(@NonNull final String url, @NonNull final String saveName,
                                              @Nullable final String savePath) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                if (!bound) {
                    startBindServiceAndDo(new ServiceConnectedCallback() {
                        @Override
                        public void call() {
                            try {
                                addDownloadTask(url, saveName, savePath);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }
                        }
                    });
                } else {
                    try {
                        addDownloadTask(url, saveName, savePath);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    /**
     * Normal download.
     * <p>
     * Un subscribe will  pause download.
     * <p>
     * Do not save the download records in the database.
     *
     * @param url      download file Url
     * @param saveName download file SaveName
     * @param savePath download file SavePath. If NULL, using default save path {@code /storage/emulated/0/Download/}
     * @return Observable<DownProgress>
     */
    public Observable<DownProgress> download(@NonNull final String url, @NonNull final String saveName,
                                             @Nullable final String savePath) {
        return mDownloadHelper.downloadDispatcher(url, saveName, savePath, mContext, mAutoInstall);
    }

    /**
     * Normal download version of the Transformer.
     * <p>
     * Provide RxJava Compose operator use.
     *
     * @param url      download file Url
     * @param saveName download file SaveName
     * @param savePath download file SavePath. If NULL, using default save path {@code /storage/emulated/0/Download/}
     * @param <T>      T
     * @return Transformer
     */
    public <T> Observable.Transformer<T, DownProgress> transform(@NonNull final String url,
                                                                 @NonNull final String saveName,
                                                                 @Nullable final String savePath) {
        return new Observable.Transformer<T, DownProgress>() {
            @Override
            public Observable<DownProgress> call(Observable<T> observable) {
                return observable.flatMap(new Func1<T, Observable<DownProgress>>() {
                    @Override
                    public Observable<DownProgress> call(T t) {
                        return download(url, saveName, savePath);
                    }
                });
            }
        };
    }

    /**
     * Service download without status version of the Transformer.
     * <p>
     * Provide RxJava Compose operator use.
     *
     * @param url      download file Url
     * @param saveName download file SaveName
     * @param savePath download file SavePath. If NULL, using default save path {@code /storage/emulated/0/Download/}
     * @param <T>      T
     * @return Transformer
     */
    public <T> Observable.Transformer<T, Object> transformService(@NonNull final String url,
                                                                  @NonNull final String saveName,
                                                                  @Nullable final String savePath) {
        return new Observable.Transformer<T, Object>() {
            @Override
            public Observable<Object> call(Observable<T> observable) {
                return observable.flatMap(new Func1<T, Observable<Object>>() {
                    @Override
                    public Observable<Object> call(T t) {
                        return serviceDownload(url, saveName, savePath);
                    }
                });
            }
        };
    }

    private void addDownloadTask(@NonNull String url, @NonNull String saveName,
                                 @Nullable String savePath) throws IOException {
        mDownService.addDownOperate(
                new DownTask.Builder()
                        .setViseDownload(ViseDownload.this)
                        .setUrl(url)
                        .setSaveName(saveName)
                        .setSavePath(savePath)
                        .build());
    }

    private void startBindServiceAndDo(final ServiceConnectedCallback callback) {
        if (mContext == null) {
            throw new RuntimeException("Context is NULL! You should call " +
                    "#ViseDownload.context(Context context)# first!");
        }
        Intent intent = new Intent(mContext, DownService.class);
        intent.putExtra(DownService.INTENT_KEY, MAX_DOWNLOAD_NUMBER);
        mContext.startService(intent);
        mContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                DownService.DownloadBinder downloadBinder = (DownService.DownloadBinder) binder;
                mDownService = downloadBinder.getService();
                mContext.unbindService(this);
                bound = true;
                callback.call();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //注意!!这个方法只会在系统杀掉Service时才会调用!!
                bound = false;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private interface ServiceConnectedCallback {
        void call();
    }
}
