package com.nowy.albumlib.view.callback;

import android.content.Context;
import android.view.View;

import com.nowy.albumlib.R;
import com.nowy.baselib.views.loadsir.callback.Callback;


public class LoadingCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.callback_loading;
    }

    @Override
    public boolean getSuccessVisible() {
        return super.getSuccessVisible();
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }
}
