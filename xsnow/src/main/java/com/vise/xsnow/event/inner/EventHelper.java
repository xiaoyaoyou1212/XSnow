package com.vise.xsnow.event.inner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * @Description: 事件帮助类
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:08
 */
public class EventHelper {
    protected final static Map<Class<?>, Object> STICKY_EVENT_MAP;
    protected final static SerializedSubject<Object, Object> SUBJECT;

    static {
        SUBJECT = new SerializedSubject<>(PublishSubject.create());
        STICKY_EVENT_MAP = new HashMap<>();
    }

    protected EventHelper() {
    }

    protected static <T> Observable<T> toObservable(Class<T> eventType) {
        return SUBJECT.ofType(eventType);
    }

    protected static synchronized void dellSticky(Object event) {
        if (!STICKY_EVENT_MAP.isEmpty()) {
            List<Class> classes = new ArrayList<>();
            for (Map.Entry<Class<?>, Object> objectEntry : STICKY_EVENT_MAP.entrySet()) {
                if (objectEntry.getKey() == event.getClass()) {
                    classes.add(event.getClass());
                }
            }
            stickyEventMapRemove(classes);
        }
    }

    protected static void stickyEventMapRemove(List<Class> classes) {
        for (Class aClass : classes) STICKY_EVENT_MAP.remove(aClass);
    }
}
