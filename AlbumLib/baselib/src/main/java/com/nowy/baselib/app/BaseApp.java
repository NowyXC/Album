package com.nowy.baselib.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.nowy.baselib.listener.SimpleActivityLifecycleCallbacks;
import com.nowy.baselib.manager.AppManager;
import com.nowy.baselib.utils.SharedPrefsUtil;
import com.nowy.baselib.utils.netstate.NetChangeObserver;
import com.nowy.baselib.utils.netstate.NetWorkUtil;
import com.nowy.baselib.utils.netstate.NetworkStateReceiver;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by Nowy on 2017/12/20.
 */

public class BaseApp extends Application {
    public static final String TAG = BaseApp.class.getSimpleName();
    public static BaseApp instance;
    public static boolean DEBUG = true;//控制日志的输出
    public static final String ENCRYPT_PREFS = "encrypt_prefs" ;//sp文件名
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLogger();
        initPrefs();
        registerActivityLifecycle();
        initNetStateListener(this);
    }



    /**
     * 绑定监听activity的生命周期
     * AppManager管理activity
     * NetworkStateReceiver管理网络监听
     */
    private void registerActivityLifecycle(){
        ActivityLifecycleCallbacks lifecycleCallbacks = new SimpleActivityLifecycleCallbacks(){
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                super.onActivityCreated(activity, savedInstanceState);
                AppManager.getAppManager().addActivity(activity);
                if(activity instanceof NetChangeObserver)
                    NetworkStateReceiver.registerObserver((NetChangeObserver) activity);

                Logger.t(TAG).i("lifecycleCallbacks "
                        +"\n当前创建的activity："+activity
                        +"\n当前堆栈的大小:"+AppManager.getAppManager().size()
                        +"\n当前网络监听集合的大小:"+NetworkStateReceiver.size());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                super.onActivityDestroyed(activity);
                AppManager.getAppManager().removeActivity(activity);
                if(activity instanceof NetChangeObserver)
                    NetworkStateReceiver.removeRegisterObserver((NetChangeObserver) activity);

                Logger.t(TAG).i("lifecycleCallbacks "
                        +"\n当前移除的activity："+activity
                        +"\n当前堆栈大小:"+AppManager.getAppManager().size()
                        +"\n当前网络监听集合的大小:"+NetworkStateReceiver.size());
            }

        };


        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }


    /**
     * 初始化SharedPreferences,默认创建ENCRYPT_PREFS实例
     *  按key（名字）获取其他SharedPrefsUtil实例
     */
    private void initPrefs(){
        SharedPrefsUtil.init(this,ENCRYPT_PREFS,MODE_PRIVATE);
    }


    /**
     * 日志初始化
     */
    private void initLogger(){
        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return DEBUG;
            }
        });
    }


    /**
     * 初始化网络状态监听
     * Android 7.0之后静态注册的广播可能无法接收到网络状态变化
     * 放在application进行动态注册
     * @param context
     */
    private void initNetStateListener(Context context){
        NetWorkUtil.init(context);
    }





    /**
     * 退出app
     */
    public static void exit(){
        NetWorkUtil.unRegister(getInstance());
        AppManager.getAppManager().AppExit();
    }

    public static BaseApp getInstance() {
        return instance;
    }
}
