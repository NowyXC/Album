package com.nowy.baselib.utils.pay.wxpay;

import android.app.Activity;

import com.nowy.baselib.utils.TextUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2018/1/16.
 */

public class WxPayUtil {
    private Activity mAty;
    private String mAppid;
    private String mMchid;
    private String mPrepayid;
    private String mNonce;
    private String mSign;
    private String mTimesTamp;

    public WxPayUtil(Activity aty, String appid, String mch_id, String prepay_id, String nonce_str, String sign, String timestamp) {
        this.mAty=aty;
        this.mAppid=appid;
        this.mMchid=mch_id;
        this.mPrepayid=prepay_id;
        this.mNonce=nonce_str;
        this.mSign=sign;
        this.mTimesTamp=timestamp;
    }

    public void pay() {
        if (mAty==null)return;

        IWXAPI wxapi = WXAPIFactory.createWXAPI(mAty, null);
        if (!TextUtil.isEmpty(mAppid)){
            wxapi.registerApp(mAppid);
        }

        PayReq payReq = new PayReq();
        payReq.appId=mAppid;
        payReq.partnerId=mMchid;
        payReq.prepayId = mPrepayid;
        payReq.packageValue = "Sign=WXPay";
        payReq.nonceStr = mNonce;
        payReq.timeStamp = mTimesTamp;//时间过期
        payReq.sign=mSign;
        wxapi.sendReq(payReq);

    }
}

