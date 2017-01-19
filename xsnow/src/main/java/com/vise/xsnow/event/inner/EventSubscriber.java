package com.vise.xsnow.event.inner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @Description: 事件订阅者
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:05
 */
public class EventSubscriber extends EventHelper {
    private final Object target;
    private final Method method;
    private final EventThread thread;
    private Subscription subscription;

    public EventSubscriber(Object target, Method method, EventThread thread) {
        if (target == null) {
            throw new NullPointerException("SubscriberEvent target cannot be null.");
        }
        if (method == null) {
            throw new NullPointerException("SubscriberEvent method cannot be null.");
        }
        if (thread == null) {
            throw new NullPointerException("SubscriberEvent thread cannot be null.");
        }
        this.target = target;
        this.method = method;
        this.thread = thread;
        this.method.setAccessible(true);
        initObservable(this.method.getParameterTypes()[0]);
    }

    public final Class getParameter() {
        return this.method.getParameterTypes()[0];
    }

    private final void initObservable(Class aClass) {
        subscription = toObservable(aClass).onBackpressureBuffer().subscribeOn(Schedulers.io()).observeOn(EventThread.getScheduler
                (thread)).subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                try {
                    handleEvent(event);
                    dellSticky(event);
                } catch (InvocationTargetException e) {
                    throwRuntimeException("Could not dispatch event: " + event.getClass() + " to subscriber " + EventSubscriber.this, e);
                }
            }
        });
    }

    public final Subscription getSubscription() {
        return subscription;
    }

    public final void handleEvent(Object event) throws InvocationTargetException {
        try {
            method.invoke(target, event);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventSubscriber other = (EventSubscriber) obj;
        return method.equals(other.method) && target == other.target;
    }

    public final void throwRuntimeException(String msg, InvocationTargetException e) {
        throwRuntimeException(msg, e.getCause());
    }

    public final void throwRuntimeException(String msg, Throwable e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            throw new RuntimeException(msg + ": " + cause.getMessage(), cause);
        } else {
            throw new RuntimeException(msg + ": " + e.getMessage(), e);
        }
    }
}
