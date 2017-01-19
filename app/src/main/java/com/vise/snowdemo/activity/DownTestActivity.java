package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vise.snowdemo.R;
import com.vise.xsnow.ui.BaseActivity;

/**
 * @Description:
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

            }
        });
        mService_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void initData() {

    }
}
