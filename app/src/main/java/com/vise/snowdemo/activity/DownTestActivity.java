package com.vise.snowdemo.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.utils.view.DialogUtil;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.callback.ACallback;
import com.vise.xsnow.net.callback.DCallback;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;
import com.vise.xsnow.ui.BaseActivity;

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
    }

    private void normalDownload() {
        ViseNet.DOWNLOAD(new DCallback() {
            @Override
            public void onProgress(long currentLength, long totalLength) {
                ViseLog.i("down progress currentLength:" + currentLength + ",totalLength:" + totalLength);
                mNormal_download_progress.setProgress((int) (currentLength * 100 / totalLength));
                mNormal_download_desc.setText((float) currentLength * 100 / totalLength + "%");
            }

            @Override
            public void onComplete() {
                ViseLog.i("down complete!");
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.i("down errorCode:" + errCode + ",errorMsg:" + errMsg);
            }}).baseUrl("http://dldir1.qq.com/")
                .suffixUrl("weixin/android/weixin6330android920.apk")
                .request(mContext, new ACallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                ViseLog.i("down request success:" + data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                ViseLog.i("down errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }
}
