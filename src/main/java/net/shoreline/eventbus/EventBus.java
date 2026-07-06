package net.shoreline.eventbus;

import net.shoreline.eventbus.annotation.EventListener;
import net.shoreline.eventbus.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBus
{
    public static final EventBus INSTANCE = new EventBus();

    private final Map<Class<? extends Event>, List<Subscription>> event2InvokerMap = new ConcurrentHashMap<>();

    private EventBus()
    {
    }

    public void dispatch(Event event)
    {
        List<Subscription> subs = event2InvokerMap.get(event.getClass());
        if (subs == null || subs.isEmpty())
        {
            return;
        }
        Subscription[] snapshot;
        synchronized (subs)
        {
            snapshot = subs.toArray(new Subscription[0]);
        }
        for (Subscription s : snapshot)
        {
            if (event.isCancelable() && event.isCanceled() && !s.receiveCanceled)
            {
                continue;
            }
            try
            {
                s.method.invoke(s.subscriber, event);
            }
            catch (InvocationTargetException e)
            {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                if (cause instanceof RuntimeException re)
                {
                    throw re;
                }
                if (cause instanceof Error err)
                {
                    throw err;
                }
                throw new RuntimeException(cause);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public Object subscribe(Object subscriber)
    {
        if (subscriber == null)
        {
            return null;
        }
        Class<?> cls = subscriber.getClass();
        while (cls != null && cls != Object.class)
        {
            for (Method m : cls.getDeclaredMethods())
            {
                EventListener ann = m.getAnnotation(EventListener.class);
                if (ann == null)
                {
                    continue;
                }
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1)
                {
                    continue;
                }
                if (!Event.class.isAssignableFrom(params[0]))
                {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Class<? extends Event> eventCls = (Class<? extends Event>) params[0];
                try
                {
                    m.setAccessible(true);
                }
                catch (Throwable ignored)
                {
                }
                Subscription sub = new Subscription(subscriber, m, ann.priority(), ann.receiveCanceled());
                List<Subscription> list = event2InvokerMap.computeIfAbsent(eventCls, k -> Collections.synchronizedList(new ArrayList<>()));
                synchronized (list)
                {
                    list.add(sub);
                    list.sort(Comparator.comparingInt((Subscription s) -> s.priority).reversed());
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    public Object unsubscribe(Object subscriber)
    {
        if (subscriber == null)
        {
            return null;
        }
        for (List<Subscription> list : event2InvokerMap.values())
        {
            synchronized (list)
            {
                list.removeIf(s -> s.subscriber == subscriber);
            }
        }
        return null;
    }

    private static final class Subscription
    {
        final Object subscriber;
        final Method method;
        final int priority;
        final boolean receiveCanceled;

        Subscription(Object subscriber, Method method, int priority, boolean receiveCanceled)
        {
            this.subscriber = subscriber;
            this.method = method;
            this.priority = priority;
            this.receiveCanceled = receiveCanceled;
        }
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    public final static class InvokerNode
    {
        private final InvokerNode next;
        private final Invoker invoker;
        private final Object subscriber;
        private final Integer priority;

        private InvokerNode(Object invoker, Object subscriber, Object priority)
        {
            this.next = null;
            this.invoker = (Invoker) invoker;
            this.subscriber = subscriber;
            this.priority = (Integer) priority;
        }
    }

    @FunctionalInterface
    public interface Invoker
    {
        void invoke(Object event);
    }
}