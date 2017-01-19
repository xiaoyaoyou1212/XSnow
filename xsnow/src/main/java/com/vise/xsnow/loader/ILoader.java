package com.vise.xsnow.loader;

import android.content.Context;
import android.widget.ImageView;

import com.vise.xsnow.common.ViseConfig;

import java.io.File;

/**
 * @Description: 图片加载接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-12-19 15:04
 */
public interface ILoader {
    void init(Context context);

    void loadNet(ImageView target, String url, Options options);

    void loadResource(ImageView target, int resId, Options options);

    void loadAssets(ImageView target, String assetName, Options options);

    void loadFile(ImageView target, File file, Options options);

    void clearMemoryCache(Context context);

    void clearDiskCache(Context context);

    class Options {

        public static final int RES_NONE = -1;
        public int loadingResId = RES_NONE;//加载中的资源id
        public int loadErrorResId = RES_NONE;//加载失败的资源id

        public static Options defaultOptions() {
            return new Options(ViseConfig.IL_LOADING_RES, ViseConfig.IL_ERROR_RES);
        }

        public Options(int loadingResId, int loadErrorResId) {
            this.loadingResId = loadingResId;
            this.loadErrorResId = loadErrorResId;
        }
    }
}
