package com.vise.snowdemo.adapter;

import android.content.Context;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vise.snowdemo.R;
import com.vise.xsnow.loader.ILoader;
import com.vise.xsnow.loader.LoaderManager;
import com.vise.xsnow.ui.adapter.helper.HelperAdapter;
import com.vise.xsnow.ui.adapter.helper.HelperViewHolder;

import java.util.List;

/**
 * @Description: 图片加载适配器
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:22.
 */
public class ImageAdapter extends HelperAdapter<String> {
    public ImageAdapter(Context context, List data) {
        super(context, data, R.layout.item_image_loader);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, String s) {
        SimpleDraweeView icon = viewHolder.getView(R.id.item_image_loader_icon);
//        LoaderFactory.getLoader().loadAssets(icon, "github_head_portrait.jpg", null);
//        LoaderFactory.getLoader().loadResource(icon, R.mipmap.github_head_portrait, null);
        LoaderManager.getLoader().loadNet(icon, s, new ILoader.Options(R.mipmap.github_head_portrait, R.mipmap.github_head_portrait));
    }
}
