package com.vise.xsnow.ui.status;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @Description: 各种状态视图显示布局
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-03-08 15:51
 */
public class StatusLayout extends FrameLayout {

    //布局管理器
    private StatusLayoutManager mStatusLayoutManager;
    //存放布局集合
    private SparseArray<View> mLayoutSparseArray = new SparseArray();

    public StatusLayout(Context context) {
        super(context);
    }

    public StatusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setStatusLayoutManager(StatusLayoutManager statusLayoutManager) {
        mStatusLayoutManager = statusLayoutManager;
        addAllLayoutToLayout();
    }

    private void addAllLayoutToLayout() {
        if (mStatusLayoutManager.getContentLayoutResId() != 0) {
            addLayoutResId(mStatusLayoutManager.getContentLayoutResId(), StatusType.CONTENT.getType());
        }
        if (mStatusLayoutManager.getLoadingLayoutResId() != 0) {
            addLayoutResId(mStatusLayoutManager.getLoadingLayoutResId(), StatusType.LOADING.getType());
        }

        if (mStatusLayoutManager.getEmptyView() != null) addView(mStatusLayoutManager.getEmptyView());
        if (mStatusLayoutManager.getOtherErrorView() != null) addView(mStatusLayoutManager.getOtherErrorView());
        if (mStatusLayoutManager.getNetworkErrorView() != null) addView(mStatusLayoutManager.getNetworkErrorView());
    }

    private void addLayoutResId(@LayoutRes int layoutResId, int id) {
        View resView = LayoutInflater.from(mStatusLayoutManager.getContext()).inflate(layoutResId, null);
        mLayoutSparseArray.put(id, resView);
        addView(resView);
    }

    /**
     * 显示加载视图
     */
    public void showLoadingView() {
        if (mLayoutSparseArray.get(StatusType.LOADING.getType()) != null) showHideViewById(StatusType.LOADING.getType());
    }

    /**
     * 显示内容视图
     */
    public void showContentView() {
        if (mLayoutSparseArray.get(StatusType.CONTENT.getType()) != null) showHideViewById(StatusType.CONTENT.getType());
    }

    /**
     * 显示空视图
     */
    public void showEmptyView() {
        if (inflateLayout(StatusType.EMPTY.getType())) showHideViewById(StatusType.EMPTY.getType());
    }

    /**
     * 显示网络异常
     */
    public void showNetworkErrorView() {
        if (inflateLayout(StatusType.NETWORK_ERROR.getType())) showHideViewById(StatusType.NETWORK_ERROR.getType());
    }

    /**
     * 显示其他异常
     */
    public void showOtherErrorView() {
        if (inflateLayout(StatusType.OTHER_ERROR.getType())) showHideViewById(StatusType.OTHER_ERROR.getType());
    }

    /**
     * 重试加载
     */
    public void retryLoad(View view, int id) {
        int retryViewId = id;
        if (mStatusLayoutManager.getRetryViewId() != 0) {
            retryViewId = mStatusLayoutManager.getRetryViewId();
        }
        View retryView = view.findViewById(retryViewId);
        if (retryView == null || mStatusLayoutManager.getRetryListener() == null) return;
        retryView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatusLayoutManager.getRetryListener().onRetry();
            }
        });
    }

    private void showHideViewById(int id) {
        for (int i = 0; i < mLayoutSparseArray.size(); i++) {
            int key = mLayoutSparseArray.keyAt(i);
            View valueView = mLayoutSparseArray.valueAt(i);
            if (key == id) {
                valueView.setVisibility(View.VISIBLE);
                if (mStatusLayoutManager.getStatusViewListener() != null)
                    mStatusLayoutManager.getStatusViewListener().onShowView(valueView, key);
            } else {
                if (valueView.getVisibility() != View.GONE) {
                    valueView.setVisibility(View.GONE);
                    if (mStatusLayoutManager.getStatusViewListener() != null)
                        mStatusLayoutManager.getStatusViewListener().onHideView(valueView, key);
                }
            }
        }
    }

    private boolean inflateLayout(int id) {
        boolean isShow = true;
        if (mLayoutSparseArray.get(id) != null) return isShow;
        if (id == StatusType.NETWORK_ERROR.getType() && mStatusLayoutManager.getNetworkErrorView() != null) {
            View view = mStatusLayoutManager.getNetworkErrorView().inflate();
            retryLoad(view, mStatusLayoutManager.getNetworkErrorRetryViewId());
            mLayoutSparseArray.put(id, view);
            isShow = true;
        } else if(id == StatusType.EMPTY.getType() && mStatusLayoutManager.getEmptyView() != null) {
            View view = mStatusLayoutManager.getEmptyView().inflate();
            retryLoad(view, mStatusLayoutManager.getEmptyRetryViewId());
            mLayoutSparseArray.put(id, view);
            isShow = true;
        } else if(id == StatusType.OTHER_ERROR.getType() && mStatusLayoutManager.getOtherErrorView() != null) {
            View view = mStatusLayoutManager.getOtherErrorView().inflate();
            retryLoad(view, mStatusLayoutManager.getOtherErrorRetryViewId());
            mLayoutSparseArray.put(id, view);
            isShow = true;
        } else {
            isShow = false;
        }
        return isShow;
    }

}
