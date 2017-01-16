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
 * @Description:
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
        mDataBaseHelper.repairErrorFlag();
        if (intent != null) {
            MAX_DOWNLOAD_NUMBER = intent.getIntExtra(INTENT_KEY, 5);
        }
        return super.onStartCommand(intent, flags, startId);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mDownloadQueueThread = new Thread(new DownOperateDispatchRunnable());
        mDownloadQueueThread.start();
        return mBinder;
    }

    public Subject<DownEvent, DownEvent> getSubject(ViseDownload rxDownload, String url) {
        Subject<DownEvent, DownEvent> subject = createAndGet(url);
        if (!mDataBaseHelper.recordNotExists(url)) {
            DownRecord record = mDataBaseHelper.readSingleRecord(url);
            File file = rxDownload.getRealFiles(record.getSaveName(), record.getSavePath())[0];
            if (file.exists()) {
                subject.onNext(mEventFactory.factory(url, record.getStatus(), record.getDownProgress()));
            }
        }
        return subject;
    }

    public Subject<DownEvent, DownEvent> createAndGet(String url) {
        if (mSubjectPool.get(url) == null) {
            Subject<DownEvent, DownEvent> subject = new SerializedSubject<>(BehaviorSubject.create
                    (mEventFactory.factory(url, DownStatus.NORMAL.getStatus(), null)));
            mSubjectPool.put(url, subject);
        }
        return mSubjectPool.get(url);
    }

    public void addDownOperate(DownTask mission) throws IOException {
        String url = mission.getUrl();
        if (mWaitingForDownloadLookUpMap.get(url) != null || mNowDownloading.get(url) != null) {
            throw new IOException("This download mission is exists.");
        } else {
            if (mDataBaseHelper.recordNotExists(url)) {
                mDataBaseHelper.insertRecord(mission);
                createAndGet(url).onNext(mEventFactory.factory(url, DownStatus.WAITING.getStatus(), null));
            } else {
                mDataBaseHelper.updateRecord(url, DownStatus.WAITING.getStatus());
                createAndGet(url).onNext(mEventFactory.factory(url, DownStatus.WAITING.getStatus(),
                        mDataBaseHelper.readProgress(url)));
            }
            mWaitingForDownload.offer(mission);
            mWaitingForDownloadLookUpMap.put(url, mission);
        }
    }

    public void pauseDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.PAUSED.getStatus());
        mDataBaseHelper.updateRecord(url, DownStatus.PAUSED.getStatus());
    }

    public void cancelDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.CANCELED.getStatus());
        mDataBaseHelper.updateRecord(url, DownStatus.CANCELED.getStatus());
    }

    public void deleteDownload(String url) {
        suspendDownloadAndSendEvent(url, DownStatus.DELETED.getStatus());
        mDataBaseHelper.deleteRecord(url);
    }

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

    private class DownOperateDispatchRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                DownTask mission = mWaitingForDownload.peek();
                if (null != mission) {
                    String url = mission.getUrl();
                    if (mission.isCanceled()) {
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                        continue;
                    }
                    if (mNowDownloading.get(url) != null) {
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                        continue;
                    }
                    if (mCount.get() < MAX_DOWNLOAD_NUMBER) {
//                        mission.start(mNowDownloading, mCount, mDataBaseHelper, mSubjectPool);
                        mWaitingForDownload.remove();
                        mWaitingForDownloadLookUpMap.remove(url);
                    }
                }
            }
        }
    }

    public class DownloadBinder extends Binder {
        public DownService getService() {
            return DownService.this;
        }
    }
}
