package com.vise.xsnow.common;

import android.content.Context;

import com.vise.log.ViseLog;
import com.vise.log.inner.DefaultTree;
import com.vise.xsnow.BuildConfig;

/**
 * @Description: 全局初始化配置
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 14:50
 */
public class ViseContext {
    private static ViseContext instance;
    private Context context;

    private ViseContext(Context context) {
        this.context = context;
    }

    public static ViseContext getInstance(Context context) {
        if (instance == null) {
            synchronized (ViseContext.class) {
                if (instance == null) {
                    instance = new ViseContext(context);
                }
            }
        }
        return instance;
    }

    public void init() {
        initLog();
    }

    private void initLog() {
        if (BuildConfig.DEBUG) {
            ViseLog.getLogConfig().configAllowLog(true).configShowBorders(true);
            ViseLog.plant(new DefaultTree());
        }
    }
}
