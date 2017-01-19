package com.vise.xsnow.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseViewHolder {
    /**
     * <p>保存小控件的集合</p>
     */
    private SparseArray<View> mViews = new SparseArray<View>();
    /**
     * <p>保存布局view的集合</p>
     */
    private SparseArray<View> mConvertViews = new SparseArray<View>();
    /**
     * <p>当前下标</p>
     */
    private int mPosition;
    /**
     * <p>单个布局view</p>
     */
    private View mConvertView;
    /**
     * <p>layoutId</p>
     */
    protected int mLayoutId;
    /**
     * <p>上下文</p>
     */
    protected Context mContext;

    public BaseViewHolder(Context context, int position, ViewGroup parent, int layoutId) {
        mConvertView = mConvertViews.get(layoutId);
        mPosition = position;
        mContext = context;
        mLayoutId = layoutId;
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            mConvertViews.put(layoutId, mConvertView);
            mConvertView.setTag(this);
        }
    }

    public BaseViewHolder() {
    }

    /**
     * 获取BaseViewHolder实例
     *
     * @param context  上下文
     * @param position 位置
     * @param parent   viewGroup
     * @param layoutId 对应的布局Id
     * @return BaseViewHolder实例
     */
    public <T extends BaseViewHolder> T get(Context context, int position, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return (T) new BaseViewHolder(context, position, parent, layoutId);
        } else {
            BaseViewHolder mHolder = (BaseViewHolder) convertView.getTag();
            if (mHolder.mLayoutId != layoutId) {
                return (T) new BaseViewHolder(context, position, parent, layoutId);
            }
            mHolder.setPosition(position);
            return (T) mHolder;
        }
    }

    /**
     * 获取viewId对应的控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    /**
     * 当没有指定到时候，返回队列中的第一个
     *
     * @return
     */
    public View getConvertView() {
        return mConvertViews.valueAt(0);
    }

    /**
     * 返回队列中指定layoutId对应的view
     *
     * @param layoutId
     * @return
     */
    public View getConvertView(int layoutId) {
        return mConvertViews.get(layoutId);
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getLayoutId() {
        return mLayoutId;
    }
}
