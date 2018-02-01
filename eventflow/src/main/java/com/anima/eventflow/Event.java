package com.anima.eventflow;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2018/1/31.
 */

public abstract class Event {
    private Observable observable;

    protected abstract Object run();

    public Observable exec() {
        observable = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                Object result = run();
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(observable.toString(), result);
                subscriber.onNext(dataMap);
                subscriber.onCompleted();
            }
        });
        return observable;
    }

    @Override
    public String toString() {
        return observable.toString();
    }
}
