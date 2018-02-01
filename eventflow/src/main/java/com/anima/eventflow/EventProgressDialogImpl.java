package com.anima.eventflow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jianjianhong on 2017/11/21.
 */

public class EventProgressDialogImpl implements EventProgressDialog {

    private final static String TAG = "EventProgressDialog";

    private Context context;
    private ProgressDialog progressDialog;


    public EventProgressDialogImpl(Context context, int style, EventFlowAction eventFlow){
        this.context = context;
        initProgressDialog(style, eventFlow);
    }

    private void initProgressDialog(final int style, final EventFlowAction eventFlow) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(style);
                //progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(eventFlow);
            }
        });
    }

    @Override
    public void showProgress(final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    @Override
    public void hideProgress() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        });
    }

    @Override
    public void updateProgress(final String message, final int progress) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
                progressDialog.setProgress(progress);
            }
        });
    }
}
