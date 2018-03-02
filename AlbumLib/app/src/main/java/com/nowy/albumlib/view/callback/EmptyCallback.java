package com.nowy.albumlib.view.callback;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nowy.albumlib.R;
import com.nowy.baselib.views.loadsir.callback.Callback;

public class EmptyCallback extends Callback {
    TextView mTvEmpty;
    @Override
    protected int onCreateView() {
        return R.layout.callback_empty;
    }


    @Override
    protected boolean onReloadEvent(final Context context, View view) {
        return false;
    }

    @Override
    protected void onViewCreate(Context context, View view) {
        super.onViewCreate(context, view);
        mTvEmpty = (TextView) view.findViewById(R.id.empty_Tv);
    }

    @Override
    public void update(Object data) {
        super.update(data);
        mTvEmpty.setText(String.valueOf(data));
    }

}
