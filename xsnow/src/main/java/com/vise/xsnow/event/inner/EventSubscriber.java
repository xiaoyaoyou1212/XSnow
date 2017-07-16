package com.vise.xsnow.event.inner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: 事件订阅者
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:05
 */
class EventSubscriber extends EventBase {
    private final Object target;
    private final Method method;
    private final ThreadMode thread;
    private Disposable disposable;

    public EventSubscriber(Object target, Method method, ThreadMode thread) {
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
        subscribeEvent(this.method.getParameterTypes()[0]);
    }

    public final Class getParameter() {
        return this.method.getParameterTypes()[0];
    }

    /**
     * 订阅事件
     * @param aClass
     */
    private void subscribeEvent(Class aClass) {
        disposable = toFlowable(aClass).subscribeOn(Schedulers.io()).observeOn(ThreadMode.getScheduler
                (thread)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object event) throws Exception {
                try {
                    handleEvent(event);
                    dellSticky(event);
                } catch (InvocationTargetException e) {
                    throwRuntimeException("Could not dispatch event: " + event.getClass() + " to subscriber " + EventSubscriber.this, e);
                }
            }
        });
    }

    public final Disposable getDisposable() {
        return disposable;
    }

    /**
     * 分发事件
     * @param event
     * @throws InvocationTargetException
     */
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

    private void throwRuntimeException(String msg, InvocationTargetException e) {
        throwRuntimeException(msg, e.getCause());
    }

    private void throwRuntimeException(String msg, Throwable e) {
        Throwable cause = e.getCause();
        if (cause != null) {
            throw new RuntimeException(msg + ": " + cause.getMessage(), cause);
        } else {
            throw new RuntimeException(msg + ": " + e.getMessage(), e);
        }
    }
}
