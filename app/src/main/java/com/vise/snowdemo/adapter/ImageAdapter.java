package com.vise.snowdemo.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.vise.snowdemo.R;
import com.vise.xsnow.loader.ILoader;
import com.vise.xsnow.loader.LoaderFactory;
import com.vise.xsnow.ui.adapter.helper.HelperAdapter;
import com.vise.xsnow.ui.adapter.helper.HelperViewHolder;

import java.util.List;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:22.
 */
public class ImageAdapter extends HelperAdapter<String> {
    public ImageAdapter(Context context, List data) {
        super(context, data, R.layout.item_image_loader);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, String s) {
        ImageView icon = viewHolder.getView(R.id.item_image_loader_icon);
        LoaderFactory.getLoader().loadNet(icon, s, new ILoader.Options(R.mipmap.ic_launcher, R.mipmap.ic_launcher));
    }
}
