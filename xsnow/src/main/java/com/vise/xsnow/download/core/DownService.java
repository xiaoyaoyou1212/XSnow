package com.vise.xsnow.download.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vise.xsnow.download.ViseDownload;
import com.vise.xsnow.download.db.DownDbManager;
import com.vise.xsnow.download.mode.DownEvent;
import com.vise.xsnow.download.mode.DownRecord;
import com.vise.xsnow.download.mode.DownStatus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @Description: 下载后台服务
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-16 18:26
 */
public class DownService extends Service {
    public static final String INTENT_KEY = "vise_download_max_number";
    private DownloadBinder mBinder;
    private DownDbManager mDataBaseHelper;
    private DownEventFactory mEventFactory;

    private volatile Map<String, Subject<DownEvent, DownEvent>> mSubjectPool;
    private volatile AtomicInteger mCount = new AtomicInteger(0);

    private Map<String, DownTask> mNowDownloading;
    private Queue<DownTask> mWaitingForDownload;
    private Map<String, DownTask> mWaitingForDownloadLookUpMap;

    private int MAX_DOWNLOAD_NUMBER = 5;
    private Thread mDownloadQueueThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new DownloadBinder();

        mSubjectPool = new ConcurrentHashMap<>();
        mWaitingForDownload = new LinkedList<>();
        mWaitingForDownloadLookUpMap = new HashMap<>();
        mNowDownloading = new HashMap<>();

        mDataBaseHelper = DownDbManager.getSingleton(this);
        mEventFactory = DownEventFactory.getSingleton();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDataBaseHelper.repairErrorStatus();
        if (intent != null) {
            MAX_DOWNLOAD_NUMBER = intent.getIntExtra(INTENT_KEY, 5);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mDownloadQueueThread = new Thread(new DownTaskDispatchRunnable());
        mDownloadQueueThread.start();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadQueueThread.interrupt();
        for (String each : mNowDownloading.keySet()) {
            pauseDownload(each);
        }
        mDataBaseHelper.closeDataBase();
    }

    /**
     * 获取下载事件主题
     * @param viseDownload
     * @param url
     * @return
     */
    public Subject<DownEvent, DownEvent> getSubject(ViseDownload viseDownload, String url) {
        Subject<DownEvent, DownEvent> subject = createAndGet(url);
        if (!mDataBaseHelper.recordNotExists(url)) {
            DownRecord record = mDataBaseHelper.readSingleRecord(url);
            File file = viseDownload.getRealFiles(record.getSaveName(), record.getSavePath())[0];
            if (file.exists()) {
                subject.onNext(mEventFactory.factory(url, record.getStatus(), record.getDownProgress()));
            }
        }
        return subject;
    }

    /**
     * 创建并获取下载事件主题
     * @param url
     * @return
     */
    public Subject<DownEvent, DownEvent> createAndGet(String url) {
        if (mSubjectPool.get(url) == null) {
            Subject<DownEvent, DownEvent> subject = new SerializedSubject<>(BehaviorSubject.create
                    (mEventFactory.factory(url, DownStatus.NORMAL.getStatus(), null)));
            mSubjectPool.put(url, subject);
        }
        return mSubjectPool.get(url);
    }

    /**
     * 添加下载任务
     * @param task
     * @throws IOException
     */
    public void addDownTask(DownTask task) throws IOException {
        String url = task.getUrl();
        if (mWaitingForDownloadLookUpMap.get(url) != null || mNowDownloading.get(url) != null) {
            throw new IOException("This download task is exists.");
        } else {
            if (mDataBaseHelper.recordNotExists(url)) {
                mDataBaseHelper.insertRecord(task);
                createAndGet(url).onNext(mEventFactory.factory(url, DownStatus.WAITING.getStatus(), null));
            } else {
                mDataBaseHelper.updateRecord(url, DownStatus.WAITING.getStatus());
                createAndGet(url).onNext(mEventFactory.factory(url, DownStatus.WAITING.getStatus(),
                        mDataBaseHelper.readProgress(url)));
            }
            mWaitingForDownload.offer(task);
            mWaitingForDownloadLookUpMap.put(url, task);
        }
    }

    /**
     * 暂停下载任务
     * @param url
     */
    public void pauseDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.PAUSED.getStatus());
        mDataBaseHelper.updateRecord(url, DownStatus.PAUSED.getStatus());
    }

    /**
     * 取消下载任务
     * @param url
     */
    public void cancelDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.CANCELED.getStatus());
        mDataBaseHelper.updateRecord(url, DownStatus.CANCELED.getStatus());
    }

    /**
     * 删除下载任务
     * @param url
     */
    public void deleteDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.DELETED.getStatus());
        mDataBaseHelper.deleteRecord(url);
    }

    /**
     * 挂起下载任务并发送事件
     * @param url
     * @param flag
     */
    private void suspendDownloadAndSendEvent(String url, int flag) {
        if (mWaitingForDownloadLookUpMap.get(url) != null) {
            mWaitingForDownloadLookUpMap.get(url).setCanceled(true);
        }
        if (mNowDownloading.get(url) != null) {
            FileHelper.Utils.unSubscribe(mNowDownloading.get(url).getSubscription());
            createAndGet(url).onNext(mEventFactory.factory(url, flag, mNowDownloading.get(url).getDownProgress()));
            mCount.decrementAndGet();
            mNowDownloading.remove(url);
        } else {
            createAndGet(url).onNext(mEventFactory.factory(url, flag, mDataBaseHelper.readProgress(url)));
        }
    }

    /**
     * 下载任务分发
     */
    private class DownTaskDispatchRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                DownTask task = mWaitingForDownload.peek();
                if (null != task) {
                    String url = task.getUrl();
                    if (task.isCanceled()) {//任务已取消
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                        continue;
                    }
                    if (mNowDownloading.get(url) != null) {//任务正在下载
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                        continue;
                    }
                    if (mCount.get() < MAX_DOWNLOAD_NUMBER) {//小于最大下载数
                        task.start(mNowDownloading, mCount, mDataBaseHelper, mSubjectPool);
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                    }
                }
            }
        }
    }

    /**
     * 绑定服务
     */
    public class DownloadBinder extends Binder {
        public DownService getService() {
            return DownService.this;
        }
    }
}
