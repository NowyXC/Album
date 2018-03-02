package com.nowy.baselib.utils.pay.alipay;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.nowy.baselib.utils.T;
import com.nowy.baselib.utils.TextUtil;

/**
 * Created by YJ on 2018/1/16. 支付宝支付工具类
 */

public class AliPayUtil {
    private  Activity mAty;
    private String mOrderInfo;

    public AliPayUtil(Activity aty, String orderInfo){
        this.mAty=aty;
        this.mOrderInfo=orderInfo;
    }

    public void pay() {
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Runnable payRunnable=new Runnable() {
        @Override
        public void run() {
            if (mAty==null)return;
            if (TextUtil.isEmpty(mOrderInfo))return;
            PayTask alipay = new PayTask(mAty);
            final String result = alipay.pay(mOrderInfo,true);

            mAty.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtil.isEmpty(result)){
                        PayResult payResult = new PayResult(result);

                        String resultInfo = payResult.getResult();
                        String resultStatus = payResult.getResultStatus();

                        T.s("resultInfo:"+resultInfo+".."+"resultStatus:"+resultStatus);

                        if (TextUtils.equals(resultStatus, "9000")) {//支付成功

                        } else {//其他情况

                        }

                    }
                }
            });


        }
    };
}
