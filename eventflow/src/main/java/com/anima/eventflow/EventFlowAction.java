package com.anima.eventflow;

import android.content.DialogInterface;

/**
 * Created by Administrator on 2018/1/31.
 */

public interface EventFlowAction extends DialogInterface.OnCancelListener {

    EventFlowAction showMessage(String message);

    EventFlowAction nest(EventFlow.NestFlatMapCallback callback);

    EventFlowAction sequence(Event event);

    EventFlowAction merge(Event event);

    void subscribe(EventResult result);

    void subscribe(EventResultList resultList);

    void cancelSubscription();

}
