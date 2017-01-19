package com.vise.xsnow.ui.adapter.helper;

import android.content.Context;

import com.vise.xsnow.ui.adapter.BaseAdapter;
import com.vise.xsnow.ui.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class HelperAdapter<T> extends BaseAdapter<T> implements DataHelper<T> {
    public HelperAdapter(Context context, List data, int... layoutIds) {
        super(context, data, layoutIds);
    }

    public HelperAdapter(Context context, List data) {
        super(context, data);
    }

    public HelperAdapter(Context context, int... layoutIds) {
        super(context, layoutIds);
    }

    public HelperAdapter(Context context) {
        super(context);
    }

    @Override
    public <H extends BaseViewHolder> void convert(H viewHolder, int position, T t) {
        HelperViewHolder holder = (HelperViewHolder) viewHolder;
        HelpConvert(holder, position, t);
    }

    /*实现具体控件的获取和赋值等业务*/
    public abstract void HelpConvert(HelperViewHolder viewHolder, int position, T t);

    @Override
    public boolean isEnabled(int position) {
        return mList != null && position < mList.size();
    }

    @Override
    public void addItemToHead(T data) {
        add(0, data);
    }

    @Override
    public void addItemToLast(T data) {
        add(mList.size(), data);
    }

    @Override
    public void addItemsToHead(List<T> dataList) {
        addAll(0, dataList);
    }

    @Override
    public void addItemsToLast(List<T> dataList) {
        addAll(mList != null ? mList.size() : 0, dataList);
    }

    @Override
    public void addAll(int startPosition, List<T> dataList) {
        if (mList != null) mList.addAll(startPosition, dataList);
        notifyDataSetChanged();
    }

    @Override
    public void add(int startPosition, T data) {
        if (mList != null) mList.add(startPosition, data);
        notifyDataSetChanged();
    }

    @Override
    public T getData(int index) {
        return (getCount() == 0 || mList == null) ? null : mList.get(index);
    }

    @Override
    public void alterObj(T oldData, T newData) {
        alterObj(mList != null ? mList.indexOf(oldData) : 0, newData);
    }

    @Override
    public void alterObj(int index, T data) {
        if (mList != null) mList.set(index, data);
        notifyDataSetChanged();
    }

    @Override
    public void remove(T data) {
        if (mList != null) mList.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public void removeToIndex(int index) {
        if (mList != null) mList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public void replaceAll(List<T> dataList) {
        if (mList != null) mList.clear();
        addAll(0, dataList);
    }

    @Override
    public void clear() {
        if (mList != null) mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean contains(T data) {
        return mList != null && mList.contains(data);
    }

    @Override
    public void setListAll(List<T> dataList) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.clear();
        mList.addAll(dataList);
        notifyDataSetChanged();
    }
}
