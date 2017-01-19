package com.vise.xsnow.event.inner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.subscriptions.CompositeSubscription;

/**
 * @Description: 事件复合
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:19
 */
public class EventComposite extends EventHelper {
    private CompositeSubscription compositeSubscription;
    private Object object;
    private Set<EventSubscriber> subscriberEvents;

    public final CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    public final void setCompositeSubscription(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
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

    public EventComposite(CompositeSubscription compositeSubscription, Object object, Set<EventSubscriber> subscriberEvents) {
        this.compositeSubscription = compositeSubscription;
        this.object = object;
        this.subscriberEvents = subscriberEvents;
    }

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
