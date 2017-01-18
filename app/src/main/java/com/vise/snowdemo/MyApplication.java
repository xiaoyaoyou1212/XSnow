package com.vise.snowdemo;

import com.vise.snowdemo.db.DbHelper;
import com.vise.xsnow.ViseApplication;
import com.vise.xsnow.loader.LoaderFactory;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:19.
 */
public class MyApplication extends ViseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        DbHelper.getInstance().init(this);
        LoaderFactory.getLoader().init(this);
    }
}
