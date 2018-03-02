package com.nowy.albumlib.album.pop;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.nowy.baselib.utils.AppUtil;
import com.nowy.baselib.utils.DeviceUtil;

/**
 * Created by Nowy on 2017/7/24.
 */

public class PopUtil {

    /**
     * 显示pop在屏幕下方
     * @param activity
     * @param resLayout
     * @param view
     */
    public static void ShowPopAtBottom(final Activity activity, @LayoutRes int resLayout,
                                       View view, boolean isMatchWidth,boolean hasShadow){
        PopupWindow mPopupWindow = isMatchWidth ?
                createPop_MatchW(activity,resLayout,view,hasShadow) : createPop_Wrap(activity,resLayout,view,hasShadow);

        mPopupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,0,
                AppUtil.dip2px(view.getContext(),6.0f));
    }



    public static PopupWindow createPop_Bottom(final Activity activity, @LayoutRes int resLayout,
                                             View view,boolean hasShadow){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);


        setupPop(activity,hasShadow,mPopupWindow);
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM ,0, 0);

        return mPopupWindow;
    }


    /**
     * 显示Pop在控件下方
     * @param activity
     * @param resLayout
     * @param view
     * @param isMatchWidth 是否填充父容器宽度，true:填充（只填充0.9宽度）
     */
    public static PopupWindow ShowPopAsDropDown(final Activity activity, @LayoutRes int resLayout,
                                         View view, boolean isMatchWidth,boolean hasShadow){
        PopupWindow mPopupWindow = isMatchWidth ?
                createPop_MatchW(activity,resLayout,view,hasShadow) : createPop_Wrap(activity,resLayout,view,hasShadow);

        mPopupWindow.showAsDropDown(view);

        return mPopupWindow;
    }


    /**
     * 构建填充宽度的pop
     * @param activity
     * @param resLayout
     * @param view
     * @return
     */
    public static PopupWindow createPop_MatchW(final Activity activity,
                                               @LayoutRes int resLayout, View view,boolean hasShadow){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindow.setWidth((int)(ABAppUtil.getDeviceWidth(view.getContext())*0.9));
        setupPop(activity,hasShadow,mPopupWindow);
        
        return mPopupWindow;
    }


    public static PopupWindow createPop_Match(final Activity activity,
                                              @LayoutRes int resLayout,boolean hasShadow, View view){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
//        mPopupWindow.setWidth((int)(ABAppUtil.getDeviceWidth(view.getContext())*0.9));
        setupPop(activity,hasShadow,mPopupWindow);

        return mPopupWindow;
    }


    public static PopupWindow createPop_Wrap(final Activity activity, @LayoutRes int resLayout,
                                             View view, boolean hasShadow){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        setupPop(activity,hasShadow,mPopupWindow);
        return mPopupWindow;
    }

    public static PopupWindow createPop_center(final Activity activity, @LayoutRes int resLayout,
                                               View view, boolean hasShadow){
        View popView =  LayoutInflater.from(view.getContext())
                .inflate(resLayout,null);
        PopupWindow mPopupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setWidth((int)(DeviceUtil.getDeviceWidth(view.getContext())*0.9));
        setupPop(activity,hasShadow,mPopupWindow);
        return mPopupWindow;
    }

    /**
     * 设置Pop的部分通用参数
     * @param activity
     * @param popupWindow
     */
    private static void setupPop(final Activity activity, boolean hasShadow, PopupWindow popupWindow){
        // 设置外部可点击
        popupWindow.setOutsideTouchable(true);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        if(hasShadow){//是否需要屏幕变暗
            //变暗
            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = 0.7f;
            activity.getWindow().setAttributes(lp);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {//回复到正常颜色
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1f;
                    activity.getWindow().setAttributes(lp);
                }
            });
        }


    }
}
