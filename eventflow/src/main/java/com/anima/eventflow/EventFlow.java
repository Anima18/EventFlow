package com.anima.eventflow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/31.
 */

public class EventFlow implements EventFlowAction {

    private final static String TAG = "EventFlow";
    private List<Event> eventList = new ArrayList<>();
    private Observable observable;
    private Subscription subscription;
    private Context context;
    private EventProgressDialog progressDialog;

    private EventFlow(Context context, Event event) {
        this.context = context;
        this.progressDialog = new EventProgressDialogImpl(context, ProgressDialog.STYLE_SPINNER, this);
        this.progressDialog.showProgress("正在处理中，请稍后...");
        if(event != null) {
            this.observable = event.exec();
            this.eventList.add(event);
        }else {
            this.observable = Observable.just(new HashMap<>());
        }

    }

    public static EventFlow create(Context context) {
        return create(context, null);
    }

    public static EventFlow create(Context context, Event event) {
        return new EventFlow(context, event);
    }

    @Override
    public EventFlowAction nest(final NestFlatMapCallback callback) {
        this.observable = observable.flatMap(new Func1<Map<String, Object>, Observable<?>>() {
            @Override
            public Observable call(Map<String, Object> data) {
                Event event = callback.flatMap(data.get(eventList.get(eventList.size()-1).toString()));
                Observable observable = event.exec();
                eventList.add(event);
                return observable;
            }
        });
        return this;
    }

    @Override
    public EventFlowAction sequence(Event event) {
        this.observable = this.observable.concatWith(event.exec());
        eventList.add(event);
        return this;
    }

    @Override
    public EventFlowAction merge(Event event) {
        this.observable = this.observable.mergeWith(event.exec());
        eventList.add(event);
        return this;
    }

    @Override
    public void subscribe(final EventResult result) {
        this.subscription = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {
                        progressDialog.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        cancelSubscription();
                    }

                    @Override
                    public void onNext(Map<String, Object> data) {
                        result.onResult(data.get(eventList.get(eventList.size()-1).toString()));
                        cancelSubscription();
                    }
                });
    }

    @Override
    public void subscribe(final EventResultList resultList) {
        final List<Object> dataList = new ArrayList<>();
        for(int i = 0; i < eventList.size(); i++) {
            dataList.add(null);
        }
        this.subscription = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    int responseCount = 0;
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        cancelSubscription();
                    }

                    @Override
                    public void onNext(Map<String, Object> data) {

                        for(int i = 0; i < eventList.size(); i++) {
                            String key = eventList.get(i).toString();
                            if(data.containsKey(key)) {
                                dataList.set(i, data.get(key));
                                responseCount++;
                                break;
                            }
                        }
                        if(responseCount == eventList.size()) {
                            resultList.onResult(dataList);
                            cancelSubscription();
                        }
                    }
                });
    }

    /*@Override
    public void subscribe(final Action1 action) {
        this.subscription = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        //EventProgressDialogImpl.getInstance().hideProgress();
                        progressDialog.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        cancelSubscription();
                    }

                    @Override
                    public void onNext(Object o) {
                        action.call(o);
                        cancelSubscription();
                    }
                });
    }*/

    @Override
    public void cancelSubscription() {
        progressDialog.hideProgress();
        this.subscription.unsubscribe();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.subscription.unsubscribe();
    }

    public interface NestFlatMapCallback{
        Event flatMap(final Object o);
    }

    public Observable getObservable() {
        return observable;
    }
}
