package com.vise.snowdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.GridView;

import com.vise.snowdemo.R;
import com.vise.snowdemo.adapter.ImageAdapter;
import com.vise.xsnow.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 图片加载展示
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 22:45.
 */
public class ImageLoaderActivity extends BaseActivity {

    private GridView mImageGrid;
    private List<String> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);
    }

    @Override
    protected void initView() {
        mImageGrid = F(R.id.image_loader_grid);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData() {
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b28220efb5e.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b28221b6398.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b28222885ec.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b2822346a2f.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b2822452175.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b282251c07c.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b282265c815.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b2822713af2.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b2822953fe5.jpg");
        mImageList.add("http://img.juemei.com/album/2016-08-16/57b2822b2c64d.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fa59f52a.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fa835387.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fa911860.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fa9e226e.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5faad1799.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5faba7649.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fac8c5c6.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5fad34f13.jpg");
        mImageList.add("http://img.juemei.com/album/2016-09-04/57cb5faf282a5.jpg");
        ImageAdapter mImageAdapter = new ImageAdapter(mContext, mImageList);
        mImageGrid.setAdapter(mImageAdapter);
    }

    @Override
    protected void processClick(View view) {

    }
}
