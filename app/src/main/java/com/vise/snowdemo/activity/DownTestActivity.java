package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.xsnow.download.ViseDownload;
import com.vise.xsnow.download.mode.DownEvent;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.ui.BaseActivity;

import rx.functions.Action1;

/**
 * @Description: 下载展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:06.
 */
public class DownTestActivity extends BaseActivity {

    private Button mNormal_download;
    private ProgressBar mNormal_download_progress;
    private TextView mNormal_download_desc;
    private Button mService_download;
    private ProgressBar mService_download_progress;
    private TextView mService_download_desc;

    private ViseDownload viseDownload;
    private String saveName = "weixin.apk";
    private String url = "http://dldir1.qq.com/weixin/android/weixin6330android920.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_test);
    }

    @Override
    protected void initView() {
        mNormal_download = (Button) findViewById(R.id.normal_download);
        mNormal_download_progress = (ProgressBar) findViewById(R.id.normal_download_progress);
        mNormal_download_desc = (TextView) findViewById(R.id.normal_download_desc);
        mService_download = (Button) findViewById(R.id.service_download);
        mService_download_progress = (ProgressBar) findViewById(R.id.service_download_progress);
        mService_download_desc = (TextView) findViewById(R.id.service_download_desc);
    }

    @Override
    protected void bindEvent() {
        mNormal_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viseDownload.download(url, saveName, null, new ApiCallback<DownProgress>() {
                    @Override
                    public void onStart() {
                        ViseLog.i("down start");
                    }

                    @Override
                    public void onError(ApiException e) {
                        ViseLog.i("down error" + e);
                    }

                    @Override
                    public void onCompleted() {
                        ViseLog.i("down completed");
                    }

                    @Override
                    public void onNext(DownProgress downProgress) {
                        if (downProgress == null) {
                            return;
                        }
                        mNormal_download_progress.setProgress((int) (downProgress.getDownloadSize() * 100 / downProgress.getTotalSize()));
                        mNormal_download_desc.setText(downProgress.getPercent());
                    }
                });
            }
        });
        mService_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viseDownload.receiveDownProgress(url, new ApiCallback<DownEvent>() {
                    @Override
                    public void onStart() {
                        ViseLog.i("down start");
                    }

                    @Override
                    public void onError(ApiException e) {
                        ViseLog.i("down error" + e);
                    }

                    @Override
                    public void onCompleted() {
                        ViseLog.i("down completed");
                    }

                    @Override
                    public void onNext(DownEvent downEvent) {
                        if (downEvent == null || downEvent.getDownProgress() == null
                                || downEvent.getDownProgress().getDownloadSize() > downEvent.getDownProgress().getTotalSize()) {
                            return;
                        }
                        mService_download_progress.setProgress((int) (downEvent.getDownProgress().getDownloadSize()
                                * 100 / downEvent.getDownProgress().getTotalSize()));
                        mService_download_desc.setText(downEvent.getDownProgress().getPercent());
                    }
                });
                viseDownload.serviceDownload(url, saveName, null).subscribe();
            }
        });
    }

    @Override
    protected void initData() {
        viseDownload = ViseDownload.getInstance()
                .maxThread(3)
                .context(mContext);
    }
}
