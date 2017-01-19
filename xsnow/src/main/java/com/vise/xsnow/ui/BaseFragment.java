package com.vise.xsnow.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vise.xsnow.event.BusFactory;

/**
 * @Description: Fragment基类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 14:52
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected Resources mResources;
    protected LayoutInflater mInflater;
    private boolean isOnResumeRegisterBus = false;
    private boolean isOnStartRegisterBus = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        this.mResources = mContext.getResources();
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        bindEvent();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnResumeRegisterBus) {
            BusFactory.getBus().register(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnStartRegisterBus) {
            BusFactory.getBus().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isOnResumeRegisterBus) {
            BusFactory.getBus().unregister(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOnStartRegisterBus) {
            BusFactory.getBus().unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected boolean isOnResumeRegisterBus() {
        return isOnResumeRegisterBus;
    }

    protected BaseFragment setOnResumeRegisterBus(boolean onResumeRegisterBus) {
        isOnResumeRegisterBus = onResumeRegisterBus;
        return this;
    }

    protected boolean isOnStartRegisterBus() {
        return isOnStartRegisterBus;
    }

    protected BaseFragment setOnStartRegisterBus(boolean onStartRegisterBus) {
        isOnStartRegisterBus = onStartRegisterBus;
        return this;
    }

    /**
     * 布局的LayoutID
     *
     * @return
     */
    protected abstract int getLayoutID();

    /**
     * 初始化子View
     */
    protected abstract void initView(View contentView);

    /**
     * 绑定事件
     */
    protected abstract void bindEvent();

    /**
     * 初始化数据
     */
    protected abstract void initData();
}
