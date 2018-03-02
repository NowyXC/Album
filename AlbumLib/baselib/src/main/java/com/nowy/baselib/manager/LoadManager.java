package com.nowy.baselib.manager;

import android.app.Activity;
import android.os.Handler;

import com.nowy.baselib.views.dialog.LoadDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nowy on 2018/1/12.
 * 维护activity里面的加载对话框效果
 */

public class LoadManager  {
    private AtomicInteger mAtomic = new AtomicInteger(0);
    private WeakReference<Activity> mAtyRef ;
    private Handler mHandler ;
    private long mDelayedMillis = 1000L;//加载框延时时间

    /**
     * 绑定当前界面
     * @param activity
     */
    public LoadManager(Activity activity) {
        this.mAtyRef = new WeakReference<>(activity);
        this.mHandler = new Handler(activity.getMainLooper());
    }


    public void setDelayedMillis(long mDelayedMillis) {
        this.mDelayedMillis = mDelayedMillis;
    }

    /**
     * 显示对话框(延时)
     */
    public void showLoadDialog(){
        if(mAtomic.getAndIncrement() == 0){//先返回，再加一
            if(mAtyRef.get() != null){
                showLoadDialogDelayed(mDelayedMillis);
            }
        }
    }


    /**
     * 立即显示对话框（只有在没有其他延时的加载框时会显示）
     */
    public void showLoadDialogNow(){
        if(mAtomic.getAndIncrement() == 0){//先返回，再加一
            if(mAtyRef.get() != null){
                showLoadDialogDelayed(0);
            }
        }
    }



    /**
     * 显示对话框
     * @param delayedMillis 延时显示时间
     */
    private void showLoadDialogDelayed(long delayedMillis){
        if(mAtyRef.get() != null){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if (mAtyRef!=null){
                        if(mAtyRef.get() != null && mAtomic.get() > 0){
                            LoadDialog.show(mAtyRef.get());
//                        }
                    }
                }
            },delayedMillis);
        }
    }


    /**
     * 销毁所有对话框计数
     */
    public void resetDialog(){
        if(mAtyRef.get() != null)
            LoadDialog.dismiss(mAtyRef.get());
        mAtomic.set(0);
        this.mHandler.removeCallbacksAndMessages(null);
    }




    /**
     * 关闭对话框
     */
    public void dismissDialog(){
        if(mAtomic.get() > 0 //显示对话框
                && mAtomic.decrementAndGet() <= 0){//先-1，再返回
            if(mAtyRef.get() != null)
                LoadDialog.dismiss(mAtyRef.get());
        }
    }


    /**
     * 与当前界面界面，并销毁引用
     */
    public void onDestroy(){
        this.mAtomic.set(0);
        if(mAtyRef.get() != null)
            LoadDialog.dismiss(mAtyRef.get());
        this.mHandler.removeCallbacksAndMessages(null);
        this.mAtyRef.clear();
    }


}
