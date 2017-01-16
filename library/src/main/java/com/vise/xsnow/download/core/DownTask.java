package com.vise.xsnow.download.core;

import com.vise.log.ViseLog;
import com.vise.xsnow.download.ViseDownload;
import com.vise.xsnow.download.mode.DownEvent;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.download.mode.DownStatus;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-16 18:27
 */
public class DownTask {
    private boolean canceled = false;
    private ViseDownload viseDownload;
    private String url;
    private String saveName;
    private String savePath;
    private DownProgress downProgress;
    private Subscription subscription;

    public boolean isCanceled() {
        return canceled;
    }

    public DownTask setCanceled(boolean canceled) {
        this.canceled = canceled;
        return this;
    }

    public ViseDownload getViseDownload() {
        return viseDownload;
    }

    public DownTask setViseDownload(ViseDownload viseDownload) {
        this.viseDownload = viseDownload;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DownTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSaveName() {
        return saveName;
    }

    public DownTask setSaveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public DownTask setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public DownProgress getDownProgress() {
        return downProgress;
    }

    public DownTask setDownProgress(DownProgress downProgress) {
        this.downProgress = downProgress;
        return this;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public DownTask setSubscription(Subscription subscription) {
        this.subscription = subscription;
        return this;
    }

    /*public void start(final Map<String, DownTask> nowDownloadMap,
                      final AtomicInteger count, final DownDbManager helper,
                      final Map<String, Subject<DownEvent, DownEvent>> subjectPool) {
        nowDownloadMap.put(url, this);
        count.incrementAndGet();
        final DownEventFactory eventFactory = DownEventFactory.getSingleton();
        subscription = viseDownload.download(url, saveName, savePath)
                .subscribeOn(Schedulers.io())
                .onBackpressureLatest()
                .subscribe(new Subscriber<DownProgress>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        helper.updateRecord(url, DownStatus.STARTED.getStatus());
                    }

                    @Override
                    public void onCompleted() {
                        subjectPool.get(url).onNext(eventFactory.factory(url, DownStatus.COMPLETED.getStatus(), downProgress));

                        helper.updateRecord(url, DownStatus.COMPLETED.getStatus());
                        count.decrementAndGet();
                        nowDownloadMap.remove(url);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ViseLog.w("error:" + e);
                        subjectPool.get(url).onNext(eventFactory.factory(url, DownStatus.FAILED.getStatus(), downProgress, e));

                        helper.updateRecord(url, DownStatus.FAILED.getStatus());
                        count.decrementAndGet();
                        nowDownloadMap.remove(url);
                    }

                    @Override
                    public void onNext(DownProgress progress) {
                        subjectPool.get(url).onNext(eventFactory.factory(url, DownStatus.STARTED.getStatus(), progress));
                        helper.updateRecord(url, progress);
                        downProgress = progress;
                    }
                });
    }*/

    public static class Builder {
        ViseDownload viseDownload;
        String url;
        String saveName;
        String savePath;

        public Builder setViseDownload(ViseDownload viseDownload) {
            this.viseDownload = viseDownload;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setSaveName(String saveName) {
            this.saveName = saveName;
            return this;
        }

        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public DownTask build() {
            DownTask task = new DownTask();
            task.viseDownload = viseDownload;
            task.url = url;
            task.saveName = saveName;
            task.savePath = savePath;
            return task;
        }
    }
}
