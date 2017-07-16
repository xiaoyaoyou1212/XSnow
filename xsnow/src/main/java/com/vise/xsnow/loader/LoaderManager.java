package com.vise.xsnow.loader;

/**
 * @Description: 图片加载管理，可定制图片加载框架
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:16
 */
public class LoaderManager {
    private static ILoader innerLoader;
    private static ILoader externalLoader;

    public static void setLoader(ILoader loader) {
        if (externalLoader == null && loader != null) {
            externalLoader = loader;
        }
    }

    public static ILoader getLoader() {
        if (innerLoader == null) {
            synchronized (LoaderManager.class) {
                if (innerLoader == null) {
                    if (externalLoader != null) {
                        innerLoader = externalLoader;
                    } else {
                        innerLoader = new GlideLoader();
                    }
                }
            }
        }
        return innerLoader;
    }
}
