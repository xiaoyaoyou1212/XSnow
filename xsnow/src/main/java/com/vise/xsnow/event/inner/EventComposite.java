package com.vise.xsnow.event.inner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @Description: 事件复合
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:19
 */
public class EventComposite extends EventBase {
    private CompositeDisposable compositeDisposable;
    private Object object;
    private Set<EventSubscriber> subscriberEvents;

    public final CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public final void setCompositeDisposable(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    public final Object getObject() {
        return object;
    }

    public final void setObject(Class<?> object) {
        this.object = object;
    }

    public final Set<EventSubscriber> getSubscriberEvents() {
        return subscriberEvents;
    }

    public final void setSubscriberEvents(Set<EventSubscriber> subscriberEvents) {
        this.subscriberEvents = subscriberEvents;
    }

    public EventComposite(CompositeDisposable compositeDisposable, Object object, Set<EventSubscriber> subscriberEvents) {
        this.compositeDisposable = compositeDisposable;
        this.object = object;
        this.subscriberEvents = subscriberEvents;
    }

    /**
     * 发送粘性事件
     * @param objectMap
     */
    public final void subscriberSticky(Map<Class<?>, Object> objectMap) {
        List<Class> classes = new ArrayList<>();
        for (Map.Entry<Class<?>, Object> classObjectEntry : objectMap.entrySet()) {
            for (EventSubscriber subscriberEvent : subscriberEvents) {
                if (classObjectEntry.getKey() == subscriberEvent.getParameter()) {
                    try {
                        classes.add(classObjectEntry.getKey());
                        subscriberEvent.handleEvent(classObjectEntry.getValue());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stickyEventMapRemove(classes);
    }
}
