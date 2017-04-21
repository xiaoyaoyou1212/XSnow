package com.vise.snowdemo.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.utils.view.DialogUtil;
import com.vise.xsnow.download.ViseDownload;
import com.vise.xsnow.download.mode.DownEvent;
import com.vise.xsnow.download.mode.DownProgress;
import com.vise.xsnow.net.callback.ApiCallback;
import com.vise.xsnow.net.exception.ApiException;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.Permission;
import com.vise.xsnow.permission.PermissionManager;
import com.vise.xsnow.permission.RxPermissions;
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
        mNormal_download = F(R.id.normal_download);
        mNormal_download_progress = F(R.id.normal_download_progress);
        mNormal_download_desc = F(R.id.normal_download_desc);
        mService_download = F(R.id.service_download);
        mService_download_progress = F(R.id.service_download_progress);
        mService_download_desc = F(R.id.service_download_desc);
    }

    @Override
    protected void bindEvent() {
        C(mNormal_download);
        C(mService_download);
    }

    @Override
    protected void initData() {
        PermissionManager.instance().with(this).request(new OnPermissionCallback() {
            @Override
            public void onRequestAllow(String permissionName) {
                DialogUtil.showTips(mContext, "权限控制", "已经授权！\n" + permissionName);
            }

            @Override
            public void onRequestRefuse(String permissionName) {
                DialogUtil.showTips(mContext, "权限控制", "拒绝授权，提示请求许可理由！\n" + permissionName);
            }

            @Override
            public void onRequestNoAsk(String permissionName) {
                DialogUtil.showTips(mContext, "权限控制", "拒绝授权，不在提醒！\n" + permissionName);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        viseDownload = ViseDownload.getInstance()
                .maxThread(3)
                .context(mContext);
    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()) {
            case R.id.normal_download:
                normalDownload();
                break;
            case R.id.service_download:
                serverDownload();
                break;
        }
    }

    private void serverDownload() {
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

    private void normalDownload() {
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
}
