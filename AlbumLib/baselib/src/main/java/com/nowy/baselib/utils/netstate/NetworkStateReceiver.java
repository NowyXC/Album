package com.nowy.baselib.utils.netstate;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.nowy.baselib.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class NetworkStateReceiver extends BroadcastReceiver {
	private static Boolean networkAvailable = false;
	private static NetWorkUtil.NetType netType;
	private static List<NetChangeObserver> netChangeObserverArrayList = Collections.synchronizedList(new LinkedList<NetChangeObserver>());
	public final static String ACTION_ANDROID_NET_CHANGE_NOTIFY = BuildConfig.APPLICATION_ID+".net.conn.CONNECTIVITY_CHANGE";
	private static BroadcastReceiver receiver;

	/**
	 *
	 * @Title: getReceiver
	 * @说 明:获得广播实例
	 * @参 数: @return
	 * @return BroadcastReceiver 返回类型
	 * @throws
	 */
	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new NetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		receiver = NetworkStateReceiver.this;
		if (intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)
				|| intent.getAction().equalsIgnoreCase(ACTION_ANDROID_NET_CHANGE_NOTIFY)) {
			if (!NetWorkUtil.isNetworkAvailable(context)) {
				networkAvailable = false;
			} else {
				netType = NetWorkUtil.getAPNType(context);
				networkAvailable = true;
			}
			notifyObserver();
		}
	}

	/**
	 * 注册网络状态广播
	 *
	 * @param mContext
	 */
	public static void registerNetworkStateReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_ANDROID_NET_CHANGE_NOTIFY);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
	}

	/**
	 * 检查网络状态
	 *
	 * @param mContext
	 */
	public static void checkNetworkState(Context mContext) {
		Intent intent = new Intent();
		intent.setAction(ACTION_ANDROID_NET_CHANGE_NOTIFY);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 注销网络状态广播
	 *
	 * @param mContext
	 */
	public static void unRegisterNetworkStateReceiver(Context mContext) {
		if (receiver != null) {
			try {
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 *
	 * @return
	 */
	public static Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public static NetWorkUtil.NetType getAPNType() {
		return netType;
	}

	private void notifyObserver() {

		for (int i = 0; i < netChangeObserverArrayList.size(); i++) {
			NetChangeObserver observer = netChangeObserverArrayList.get(i);
			if (observer != null) {
				if (isNetworkAvailable()) {
					observer.onConnect(netType);
				} else {
					observer.onDisConnect();
				}
			}
		}

	}

	public static int size(){
		return netChangeObserverArrayList == null ? 0 : netChangeObserverArrayList.size();
	}


	/**
	 * 注册网络连接观察者
	 * @param observer
	 *
	 */
	public static void registerObserver(NetChangeObserver observer) {
		if (netChangeObserverArrayList == null) {
			netChangeObserverArrayList = new ArrayList<>();
		}
		netChangeObserverArrayList.add(observer);
	}

	/**
	 * 注销网络连接观察者
	 * 
	 * @param observer
	 *            observerKey
	 */
	public static void removeRegisterObserver(NetChangeObserver observer) {
		if (netChangeObserverArrayList != null) {
			netChangeObserverArrayList.remove(observer);
		}
	}

}