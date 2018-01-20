package com.vise.snowdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vise.snowdemo.R;
import com.vise.xsnow.loader.ILoader;
import com.vise.xsnow.loader.LoaderManager;

import java.util.List;

/**
 * @Description: 图片加载适配器
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:22.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> dataList;

    public ImageAdapter(Context context, List<String> data) {
        this.context = context;
        this.dataList = data;
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return dataList != null ? dataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image_loader, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.item_image_loader_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LoaderManager.getLoader().loadNet(viewHolder.imageView, dataList.get(position), new ILoader.Options(R.mipmap.github_head_portrait, R.mipmap.github_head_portrait));
        return convertView;
    }

    public static class ViewHolder {
        public SimpleDraweeView imageView;
    }
}
