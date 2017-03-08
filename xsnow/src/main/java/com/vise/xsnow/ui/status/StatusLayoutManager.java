package com.vise.xsnow.ui.status;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.view.ViewStub;

/**
 * @Description: 各种状态视图显示管理，可配置网络异常显示视图、无数据显示视图等
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-03-08 15:54
 */
public class StatusLayoutManager {
    private final Context mContext;
    private final ViewStub mEmptyView;
    private final int mEmptyRetryViewId;
    private final ViewStub mNetworkErrorView;
    private final int mNetworkErrorRetryViewId;
    private final ViewStub mOtherErrorView;
    private final int mOtherErrorRetryViewId;
    private final int mLoadingLayoutResId;
    private final int mContentLayoutResId;
    private final int mRetryViewId;
    private final StatusLayout mStatusLayout;
    private final OnStatusViewListener mStatusViewListener;
    private final OnRetryListener mRetryListener;

    public StatusLayoutManager(Builder builder) {
        this.mContext = builder.mContext;
        this.mEmptyView = builder.mEmptyView;
        this.mEmptyRetryViewId = builder.mEmptyRetryViewId;
        this.mNetworkErrorView = builder.mNetworkErrorView;
        this.mNetworkErrorRetryViewId = builder.mNetworkErrorRetryViewId;
        this.mOtherErrorView = builder.mOtherErrorView;
        this.mOtherErrorRetryViewId = builder.mOtherErrorRetryViewId;
        this.mLoadingLayoutResId = builder.mLoadingLayoutResId;
        this.mContentLayoutResId = builder.mContentLayoutResId;
        this.mRetryViewId = builder.mRetryViewId;
        this.mStatusViewListener = builder.mStatusViewListener;
        this.mRetryListener = builder.mRetryListener;

        mStatusLayout = new StatusLayout(mContext);
        mStatusLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mStatusLayout.setStatusLayoutManager(this);
    }


    /**
     * 显示加载视图
     */
    public void showLoadingView() {
        mStatusLayout.showLoadingView();
    }

    /**
     * 显示内容视图
     */
    public void showContentView() {
        mStatusLayout.showContentView();
    }

    /**
     * 显示空视图
     */
    public void showEmptyView() {
        mStatusLayout.showEmptyView();
    }

    /**
     * 显示网络异常
     */
    public void showNetworkErrorView() {
        mStatusLayout.showNetworkErrorView();
    }

    /**
     * 显示其他异常
     */
    public void showOtherErrorView() {
        mStatusLayout.showOtherErrorView();
    }

    public Context getContext() {
        return mContext;
    }

    public ViewStub getEmptyView() {
        return mEmptyView;
    }

    public int getEmptyRetryViewId() {
        return mEmptyRetryViewId;
    }

    public ViewStub getNetworkErrorView() {
        return mNetworkErrorView;
    }

    public int getNetworkErrorRetryViewId() {
        return mNetworkErrorRetryViewId;
    }

    public ViewStub getOtherErrorView() {
        return mOtherErrorView;
    }

    public int getOtherErrorRetryViewId() {
        return mOtherErrorRetryViewId;
    }

    public int getLoadingLayoutResId() {
        return mLoadingLayoutResId;
    }

    public int getContentLayoutResId() {
        return mContentLayoutResId;
    }

    public int getRetryViewId() {
        return mRetryViewId;
    }

    public StatusLayout getStatusLayout() {
        return mStatusLayout;
    }

    public OnStatusViewListener getStatusViewListener() {
        return mStatusViewListener;
    }

    public OnRetryListener getRetryListener() {
        return mRetryListener;
    }

    public static final class Builder {
        private Context mContext;
        private ViewStub mEmptyView;
        private int mEmptyRetryViewId;
        private ViewStub mNetworkErrorView;
        private int mNetworkErrorRetryViewId;
        private ViewStub mOtherErrorView;
        private int mOtherErrorRetryViewId;
        private int mLoadingLayoutResId;
        private int mContentLayoutResId;
        private int mRetryViewId;
        private OnStatusViewListener mStatusViewListener;
        private OnRetryListener mRetryListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder emptyView(@LayoutRes int mEmptyViewId) {
            mEmptyView = new ViewStub(mContext);
            mEmptyView.setLayoutResource(mEmptyViewId);
            return this;
        }

        public Builder emptyRetryViewId(int mEmptyRetryViewId) {
            this.mEmptyRetryViewId = mEmptyRetryViewId;
            return this;
        }

        public Builder networkErrorView(@LayoutRes int mNetworkErrorId) {
            mNetworkErrorView = new ViewStub(mContext);
            mNetworkErrorView.setLayoutResource(mNetworkErrorId);
            return this;
        }

        public Builder networkErrorRetryViewId(int mNetworkErrorRetryViewId) {
            this.mNetworkErrorRetryViewId = mNetworkErrorRetryViewId;
            return this;
        }

        public Builder otherErrorView(@LayoutRes int mOtherErrorViewId) {
            mOtherErrorView = new ViewStub(mContext);
            mOtherErrorView.setLayoutResource(mOtherErrorViewId);
            return this;
        }

        public Builder otherErrorRetryViewId(int mOtherErrorRetryViewId) {
            this.mOtherErrorRetryViewId = mOtherErrorRetryViewId;
            return this;
        }

        public Builder loadingView(@LayoutRes int mLoadingLayoutResId) {
            this.mLoadingLayoutResId = mLoadingLayoutResId;
            return this;
        }

        public Builder contentView(@LayoutRes int mContentLayoutResId) {
            this.mContentLayoutResId = mContentLayoutResId;
            return this;
        }

        public Builder retryViewId(int mRetryViewId) {
            this.mRetryViewId = mRetryViewId;
            return this;
        }

        public Builder onStatusViewListener(OnStatusViewListener mStatusViewListener) {
            this.mStatusViewListener = mStatusViewListener;
            return this;
        }

        public Builder onRetryListener(OnRetryListener mRetryListener) {
            this.mRetryListener = mRetryListener;
            return this;
        }

        public StatusLayoutManager build() {
            return new StatusLayoutManager(this);
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }
}
