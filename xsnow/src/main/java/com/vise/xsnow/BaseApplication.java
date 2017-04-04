package com.vise.xsnow;

import android.app.Application;

/**
 * @Description: Application基类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 14:49
 */
public abstract class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initConfigs();
    }

    /**
     * 初始化配置
     */
    protected abstract void initConfigs();
}
