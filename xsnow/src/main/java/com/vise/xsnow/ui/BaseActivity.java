package com.vise.xsnow.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;

import com.vise.xsnow.event.BusFactory;
import com.vise.xsnow.manager.AppManager;

/**
 * @Description: Activity基类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 14:51
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context mContext;
    private SparseArray<View> mViews;
    private boolean mIsRegisterEvent = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mViews = new SparseArray<>();
        if (mIsRegisterEvent) {
            BusFactory.getBus().register(this);
        }
        AppManager.getInstance().addActivity(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        bindEvent();
        initData();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        bindEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsRegisterEvent) {
            BusFactory.getBus().unregister(this);
        }
        AppManager.getInstance().removeActivity(this);
    }

    @Override
    public void onClick(View view) {
        processClick(view);
    }

    protected <E extends View> E F(int viewId) {
        E view = (E) mViews.get(viewId);
        if (view == null) {
            view = (E) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    protected <E extends View> void C(E view) {
        view.setOnClickListener(this);
    }

    public boolean isRegisterEvent() {
        return mIsRegisterEvent;
    }

    public BaseActivity setRegisterEvent(boolean mIsRegisterEvent) {
        this.mIsRegisterEvent = mIsRegisterEvent;
        return this;
    }

    /**
     * 初始化子View
     */
    protected abstract void initView();

    /**
     * 绑定事件
     */
    protected abstract void bindEvent();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 点击事件处理
     * @param view
     */
    protected abstract void processClick(View view);
}
