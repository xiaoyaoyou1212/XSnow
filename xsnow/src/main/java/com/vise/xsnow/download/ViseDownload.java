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
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.net.mode.ApiCode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Description: 下载管理入口
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/16 21:03.
 */
public class ViseDownload {
    private static DownService mDownService;
    private static boolean bound = false;

    private Context mContext;
    private DownHelper mDownloadHelper;
    private int MAX_DOWNLOAD_NUMBER = 5;

    private ViseDownload() {
        mDownloadHelper = new DownHelper();
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
        return this;
    }

    /**
     * 设置默认保存路径
     * @param savePath
     * @return
     */
    public ViseDownload defaultSavePath(String savePath) {
        mDownloadHelper.setDefaultSavePath(savePath);
        return this;
    }

    /**
     * 设置自定义Retrofit
     * @param retrofit
     * @return
     */
    public ViseDownload retrofit(Retrofit retrofit) {
        mDownloadHelper.setRetrofit(retrofit);
        return this;
    }

    /**
     * 设置最大线程数
     * @param max
     * @return
     */
    public ViseDownload maxThread(int max) {
        mDownloadHelper.setMaxThreads(max);
        return this;
    }

    /**
     * 设置最大重试次数
     * @param max
     * @return
     */
    public ViseDownload maxRetryCount(int max) {
        mDownloadHelper.setMaxRetryCount(max);
        return this;
    }

    /**
     * 设置最大下载数
     * @param max
     * @return
     */
    public ViseDownload maxDownloadNumber(int max) {
        this.MAX_DOWNLOAD_NUMBER = max;
        return this;
    }


    /**
     * 获取文件 注：File[] {DownloadFile, TempFile, LastModifyFile}
     * @param saveName saveName
     * @param savePath savePath
     * @return Files
     */
    public File[] getRealFiles(String saveName, String savePath) {
        String[] filePaths = mDownloadHelper.getRealFilePaths(saveName, savePath);
        return new File[]{new File(filePaths[0]), new File(filePaths[1]), new File(filePaths[2])};
    }

    /**
     * 接收下载地址为url的下载事件和下载状态.
     * 注意只接收下载地址为url的事件和状态.
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
        }).compose(this.<DownEvent>norTransformer());
    }

    public Subscription receiveDownProgress(final String url, final ApiCallback<DownEvent> callback) {
        return this.receiveDownProgress(url).subscribe(new Subscriber<DownEvent>() {
            @Override
            public void onCompleted() {
                callback.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(new ApiException(e, ApiCode.Request.UNKNOWN));
            }

            @Override
            public void onNext(DownEvent downEvent) {
                callback.onNext(downEvent);
            }
        });
    }

    /**
     * 从数据库中读取所有的下载记录
     * @return Observable<List<DownloadRecord>>
     */
    public Observable<List<DownRecord>> getTotalDownloadRecords() {
        if (mContext == null) {
            return Observable.error(new Throwable("Context is NULL! You should call " +
                    "#ViseDownload.context(Context context)# first!"));
        }
        DownDbManager dataBaseHelper = DownDbManager.getSingleton(mContext);
        return dataBaseHelper.readAllRecords().compose(this.<List<DownRecord>>norTransformer());
    }

    /**
     * 从数据库中读取下载地址为url的下载记录
     * @param url download url
     * @return Observable<DownProgress>
     */
    public Observable<DownRecord> getDownloadRecord(String url) {
        if (mContext == null) {
            return Observable.error(new Throwable("Context is NULL! You should call " +
                    "#ViseDownload.context(Context context)# first!"));
        }
        DownDbManager dataBaseHelper = DownDbManager.getSingleton(mContext);
        return dataBaseHelper.readRecord(url).compose(this.<DownRecord>norTransformer());
    }

    /**
     * 暂停Service中下载地址为url的下载任务.
     * 同时标记数据库中的下载记录为暂停状态.
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
        }).compose(this.norTransformer());
    }

    /**
     * 取消Service中下载地址为url的下载任务.
     * 同时标记数据库中的下载记录为取消状态.
     * 不会删除已经下载的文件.
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
        }).compose(this.norTransformer());
    }

    /**
     * 删除Service中下载地址为url的下载任务.
     * 同时从数据库中删除该下载记录.
     * 不会删除已经下载的文件.
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
        }).compose(this.norTransformer());
    }

    /**
     * 使用普通方式进行下载
     * @param url
     * @param saveName
     * @param savePath
     * @return
     */
    public Observable<DownProgress> download(@NonNull final String url, @NonNull final String saveName,
                                             @Nullable final String savePath) {
        return mDownloadHelper.downloadDispatcher(url, saveName, savePath).compose(this.<DownProgress>norTransformer());
    }

    public Subscription download(@NonNull final String url, @NonNull final String saveName,
                                 @Nullable final String savePath, @NonNull final ApiCallback<DownProgress> callback) {
        return this.download(url, saveName, savePath).subscribe(new Subscriber<DownProgress>() {
            @Override
            public void onCompleted() {
                callback.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(new ApiException(e, ApiCode.Request.UNKNOWN));
            }

            @Override
            public void onNext(DownProgress downProgress) {
                callback.onNext(downProgress);
            }
        });
    }

    /**
     * 使用Service下载，仅仅开始下载, 不会接收下载进度，取消订阅不会停止下载
     * @param url
     * @param saveName
     * @param savePath
     * @return
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

    private <T> Observable.Transformer<T, T> norTransformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 添加下载任务
     * @param url
     * @param saveName
     * @param savePath
     * @throws IOException
     */
    private void addDownloadTask(@NonNull String url, @NonNull String saveName,
                                 @Nullable String savePath) throws IOException {
        mDownService.addDownTask(
                new DownTask.Builder(ViseDownload.this)
                        .setUrl(url)
                        .setSaveName(saveName)
                        .setSavePath(savePath)
                        .build());
    }

    /**
     * 绑定服务并开启
     * @param callback
     */
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
                bound = false;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private interface ServiceConnectedCallback {
        void call();
    }
}
