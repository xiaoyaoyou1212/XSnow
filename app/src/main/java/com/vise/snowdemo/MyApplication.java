package com.vise.snowdemo;

import com.vise.log.ViseLog;
import com.vise.log.inner.DefaultTree;
import com.vise.netexpand.convert.GsonConverterFactory;
import com.vise.snowdemo.db.DbHelper;
import com.vise.xsnow.BaseApplication;
import com.vise.xsnow.common.ViseConfig;
import com.vise.xsnow.loader.LoaderFactory;
import com.vise.xsnow.net.ViseNet;
import com.vise.xsnow.net.interceptor.HttpLogInterceptor;

/**
 * @Description: 自定义Application，主要负责一些初始化操作
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:19.
 */
public class MyApplication extends BaseApplication {

    @Override
    protected void initConfigs() {
        initLog();
        initNet();
        DbHelper.getInstance().init(this);
        LoaderFactory.getLoader().init(this);
    }

    private void initLog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(false);//是否排版显示
        ViseLog.plant(new DefaultTree());//添加打印日志信息到Logcat的树
    }

    private void initNet() {
        ViseNet.init(this);
        ViseNet.CONFIG()
                .baseUrl(ViseConfig.API_HOST)
                .retryCount(1)
                .converterFactory(GsonConverterFactory.create())
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY));
    }

}
