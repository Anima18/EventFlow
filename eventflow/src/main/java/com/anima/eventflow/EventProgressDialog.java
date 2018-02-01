package com.anima.eventflow;

/**
 * Created by jianjianhong on 2017/11/21.
 */

public interface EventProgressDialog {

    void showProgress(String message);

    void hideProgress();

    void updateProgress(String message, int progress);
}
