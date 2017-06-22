package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vise.snowdemo.R;
import com.vise.xsnow.ui.BaseActivity;
import com.vise.xsnow.ui.status.OnRetryListener;
import com.vise.xsnow.ui.status.OnStatusViewListener;
import com.vise.xsnow.ui.status.StatusLayoutManager;

/**
 * @Description: 各种状态视图切换展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-03-09 10:25
 */
public class StatusSwitchActivity extends BaseActivity {

    private Button mShow_content_view;
    private Button mShow_loading_view;
    private Button mShow_empty_view;
    private Button mShow_network_error_view;
    private Button mShow_other_error_view;

    private StatusLayoutManager mStatusLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_switch);
    }

    @Override
    protected void initView() {
        LinearLayout mLayoutMain = F(R.id.layout_main);
        mShow_content_view = F(R.id.show_content_view);
        mShow_loading_view = F(R.id.show_loading_view);
        mShow_empty_view = F(R.id.show_empty_view);
        mShow_network_error_view = F(R.id.show_network_error_view);
        mShow_other_error_view = F(R.id.show_other_error_view);

        mStatusLayoutManager = StatusLayoutManager.newBuilder(mContext)
                .contentView(R.layout.status_switch_content_layout)
                .loadingView(R.layout.loading_layout)
                .emptyView(R.layout.empty_layout)
                .networkErrorView(R.layout.network_error_layout)
                .otherErrorView(R.layout.other_error_layout)
                .retryViewId(R.id.reload_view)
                .onStatusViewListener(new OnStatusViewListener() {
                    @Override
                    public void onShowView(View view, int id) {

                    }

                    @Override
                    public void onHideView(View view, int id) {

                    }
                })
                .onRetryListener(new OnRetryListener() {
                    @Override
                    public void onRetry() {
                        mStatusLayoutManager.showLoadingView();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mStatusLayoutManager.showContentView();
                                    }
                                });
                            }
                        }).start();
                    }
                }).build();
        mLayoutMain.addView(mStatusLayoutManager.getStatusLayout());
        mStatusLayoutManager.showLoadingView();
    }

    @Override
    protected void bindEvent() {
        C(mShow_content_view);
        C(mShow_loading_view);
        C(mShow_empty_view);
        C(mShow_network_error_view);
        C(mShow_other_error_view);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void processClick(View view) {
        switch (view.getId()) {
            case R.id.show_content_view:
                mStatusLayoutManager.showContentView();
                break;
            case R.id.show_loading_view:
                mStatusLayoutManager.showLoadingView();
                break;
            case R.id.show_empty_view:
                mStatusLayoutManager.showEmptyView();
                break;
            case R.id.show_network_error_view:
                mStatusLayoutManager.showNetworkErrorView();
                break;
            case R.id.show_other_error_view:
                mStatusLayoutManager.showOtherErrorView();
                break;
        }
    }

}
