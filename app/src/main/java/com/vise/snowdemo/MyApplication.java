package com.vise.snowdemo;

import com.vise.snowdemo.db.DbHelper;
import com.vise.xsnow.ViseApplication;
import com.vise.xsnow.loader.LoaderFactory;

/**
 * @Description: 自定义Application，主要负责一些初始化操作，
 * 如果需要日志打印需要继承自ViseApplication，也可以自己根据
 * 需要进行日志初始化，不需要继承ViseApplication。
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
