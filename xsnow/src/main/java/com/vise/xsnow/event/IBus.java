package com.vise.xsnow.event;

/**
 * @Description: 事件总线接口
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2016-12-19 15:06
 */
public interface IBus {
    void register(Object object);

    void unregister(Object object);

    void post(IEvent event);

    void postSticky(IEvent event);
}
