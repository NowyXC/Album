package com.nowy.albumlib.view.callback;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nowy.albumlib.R;
import com.nowy.baselib.utils.IntentUtil;
import com.nowy.baselib.views.loadsir.callback.Callback;

public class ErrorCallback extends Callback implements View.OnClickListener{
    TextView mTvError;
    TextView mTvHint;
    Button mBtnTryAg;
    Button mBtnSetting;
    @Override
    protected int onCreateView() {
        return R.layout.callback_error;
    }

    @Override
    protected boolean onReloadEvent(final Context context, View view) {
        if(view.getId() == R.id.error_BtnTryAg){//重试
            if(getOnReloadListener() != null){
                getOnReloadListener().onReload(view);
            }
        }else if(view.getId() == R.id.error_BtnSetting){//网络设置
            IntentUtil.toWIFISettingAty(context);
        }
        return true;
    }

    @Override
    protected void onViewCreate(Context context, View view) {
        super.onViewCreate(context, view);
        mTvError = (TextView) view.findViewById(R.id.error_Tv);
        mTvHint = (TextView) view.findViewById(R.id.error_TvHint);
        mBtnTryAg = (Button) view.findViewById(R.id.error_BtnTryAg);
        mBtnSetting = (Button) view.findViewById(R.id.error_BtnSetting);

        mBtnTryAg.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);

    }



    @Override
    public void update(Object data) {
        super.update(data);
        mTvError.setText(String.valueOf(data));
    }

    @Override
    public void onClick(View v) {
        onReloadEvent(v.getContext(),v);
    }
}
