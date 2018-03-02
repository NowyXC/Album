package com.nowy.baselib.activity;

import android.support.annotation.NonNull;

import com.nowy.baselib.utils.T;
import com.nowy.baselib.utils.netstate.NetChangeObserver;
import com.nowy.baselib.utils.netstate.NetWorkUtil;
import com.nowy.baselib.utils.permission.XPermissionUtils;
import com.orhanobut.logger.Logger;


/**
 * Created by Nowy on 2017/12/20.
 * 权限请求的基础类，关联了XPermissionUtils的权限请求回调
 * 网络状态监听
 */

public class BasePermissionAty extends BaseLoadDialogAty implements NetChangeObserver {
    public static final String TAG = BasePermissionAty.class.getSimpleName();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        XPermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onConnect(NetWorkUtil.NetType type) {
        Logger.t(TAG).i("当前网络为："+NetWorkUtil.getNetStr(type));
    }

    @Override
    public void onDisConnect() {
        T.s("暂无网络");
    }


}
