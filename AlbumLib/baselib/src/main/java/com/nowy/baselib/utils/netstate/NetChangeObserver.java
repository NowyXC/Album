package com.nowy.baselib.utils.netstate;



public interface NetChangeObserver {
    /**
     * 网络连接连接时调用
     */
     void onConnect(NetWorkUtil.NetType type);

    /**
     * 当前没有网络连接
     */
     void onDisConnect();
}
