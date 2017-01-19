package com.vise.xsnow.event;

import com.vise.xsnow.event.inner.EventThread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 接收事件注解，必须在接收事件地方定制该注解
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-29 19:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscribe {
    EventThread thread() default EventThread.MAIN_THREAD;
}
