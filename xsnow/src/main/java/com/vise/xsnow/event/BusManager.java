package com.vise.xsnow.event;

/**
 * @Description: 事件管理，可定制事件发送方式，可替换成EventBus。
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:06
 */
public class BusManager {
    private static IBus innerBus;
    private static IBus externalBus;

    public static void setBus(IBus bus) {
        if (externalBus == null && bus != null) {
            externalBus = bus;
        }
    }

    public static IBus getBus() {
        if (innerBus == null) {
            synchronized (BusManager.class) {
                if (innerBus == null) {
                    if (externalBus != null) {
                        innerBus = externalBus;
                    } else {
                        innerBus = new RxBusImpl();
                    }
                }
            }
        }
        return innerBus;
    }
}
