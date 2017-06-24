package com.vise.snowdemo.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.vise.snowdemo.R;
import com.vise.utils.view.DialogUtil;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.callback.UCallback;
import com.vise.xsnow.http.mode.DownProgress;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;
import com.vise.xsnow.ui.BaseActivity;

import java.io.File;

/**
 * @Description: 上传下载展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:06.
 */
public class UploadDownActivity extends BaseActivity {

    private Button mDownload_btn;
    private ProgressBar mDownload_progress;
    private TextView mDownload_progress_desc;
    private Button mUpload_btn;
    private ProgressBar mUpload_progress;
    private TextView mUpload_progress_desc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_test);
    }

    @Override
    protected void initView() {
        mDownload_btn = F(R.id.download_btn);
        mDownload_progress = F(R.id.download_progress);
        mDownload_progress_desc = F(R.id.download_progress_desc);
        mUpload_btn = F(R.id.upload_btn);
        mUpload_progress = F(R.id.upload_progress);
        mUpload_progress_desc = F(R.id.upload_progress_desc);
    }

    @Override
    protected void bindEvent() {
        C(mDownload_btn);
        C(mUpload_btn);
    }

    @Override
    protected void initData() {
        PermissionManager.instance().with(this).request(new OnPermissionCallback() {
            @Override
            public void onRequestAllow(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_allow) + "\n" + permissionName);
            }

            @Override
            public void onRequestRefuse(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_refuse) + "\n" + permissionName);
            }

            @Override
            public void onRequestNoAsk(String permissionName) {
                DialogUtil.showTips(mContext, getString(R.string.permission_control),
                        getString(R.string.permission_noAsk) + "\n" + permissionName);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()) {
            case R.id.download_btn:
                download();
                break;
            case R.id.upload_btn:
                upload();
                break;
        }
    }

    private void upload() {
        mUpload_btn.setClickable(false);
        ViseHttp.UPLOAD(new UCallback() {
            @Override
            public void onProgress(long currentLength, long totalLength, float percent) {
                if (percent == 100) {
                    mUpload_btn.setClickable(true);
                }
                mUpload_progress.setProgress((int) percent);
                mUpload_progress_desc.setText(percent + "%");
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                mUpload_btn.setClickable(true);
                ViseLog.i("upload errorCode:" + errCode + ",errorMsg:" + errMsg);
            }}).addParam("strategyId", "41")
                .addParam("title", "初秋美白养成计划")
                .addParam("tagIds", "95,96,208")
                .addParam("content", "夏天晒黑了？初秋正是美白的好时机，快快行动起来。")
                .addParam("status", "1")
                .addFile("androidPicFile", getUploadFile(mContext, "test.jpg"))
                .baseUrl("https://200.200.200.50/")
                .suffixUrl("v1/web/cms/skinStrategy/addOrUpdateSkinStrategy")
                .request(new ACallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                ViseLog.i("upload success:" + data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                mUpload_btn.setClickable(true);
                ViseLog.i("upload errorCode:" + errCode + ",errorMsg:" + errMsg);
            }
        });
    }

    private void download() {
        mDownload_btn.setClickable(false);
        String saveName = "weixin.apk";
        ViseHttp.DOWNLOAD()
                .baseUrl("http://dldir1.qq.com/")
                .suffixUrl("weixin/android/weixin6330android920.apk")
                .setFileName(saveName)
                .request(new ACallback<DownProgress>() {
                    @Override
                    public void onSuccess(DownProgress downProgress) {
                        if (downProgress == null) {
                            return;
                        }
                        ViseLog.i("down progress currentLength:" + downProgress.getDownloadSize() + ",totalLength:" + downProgress.getTotalSize());
                        mDownload_progress.setProgress((int) (downProgress.getDownloadSize() * 100 / downProgress.getTotalSize()));
                        mDownload_progress_desc.setText(downProgress.getPercent());
                        if (downProgress.isDownComplete()) {
                            mDownload_btn.setClickable(true);
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        ViseLog.i("down errorCode:" + errCode + ",errorMsg:" + errMsg);
                        mDownload_btn.setClickable(true);
                    }
                });
    }

    private File getUploadFile(Context context, String fileName) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + fileName);
    }
}
