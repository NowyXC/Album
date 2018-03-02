package com.nowy.albumlib.app;

import android.support.v7.app.AppCompatDelegate;

import com.nowy.albumlib.view.callback.EmptyCallback;
import com.nowy.albumlib.view.callback.ErrorCallback;
import com.nowy.albumlib.view.callback.LoadingCallback;
import com.nowy.baselib.app.BaseApp;
import com.nowy.baselib.views.loadsir.core.LoadSir;

/**
 * Created by Nowy on 2018/3/2.
 */

public class NApp extends BaseApp {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
//                return new RefreshHeaderView (context);
////                        .setSpinnerStyle(SpinnerStyle.FixedBehind);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//            }
//        });
        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
//            }
//        });
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initLoadSir();
    }

    /**
     * 全局配置UI加载各种状态页
     */
    private void initLoadSir(){
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())//添加各种状态页
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
//                .addCallback(new TimeoutCallback())
//                .setDefaultCallback(LoadingCallback.class)//设置默认状态页
                .commit();
    }
}
