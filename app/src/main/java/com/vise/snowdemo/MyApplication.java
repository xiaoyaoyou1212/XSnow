package com.vise.snowdemo;

import android.app.Application;

import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;
import com.vise.snowdemo.db.DbHelper;
import com.vise.snowdemo.loader.FrescoLoader;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.interceptor.HttpLogInterceptor;
import com.vise.xsnow.loader.LoaderManager;

/**
 * @Description: 自定义Application，主要负责一些初始化操作
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:19.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initLog();
        initNet();
        DbHelper.getInstance().init(this);
        LoaderManager.setLoader(new FrescoLoader());//外部定制图片加载库Fresco
        LoaderManager.getLoader().init(this);
    }

    private void initLog() {
        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(false);//是否排版显示
        ViseLog.plant(new LogcatTree());//添加打印日志信息到Logcat的树
    }

    private void initNet() {
        ViseHttp.init(this);
        ViseHttp.CONFIG()
                //配置请求主机地址
                .baseUrl("http://192.168.1.105/")
                .setCookie(true)
                //配置日志拦截器
                .interceptor(new HttpLogInterceptor()
                        .setLevel(HttpLogInterceptor.Level.BODY));

    }

}
