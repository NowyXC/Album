package com.nowy.baselib.utils;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 动画工具类
 *
 */
public class AnimationUtil {

    //动画持续时间
    public final static int ANIMATION_IN_TIME=500;
    public final static int ANIMATION_OUT_TIME=500;

    public static Animation createInAnimation(Context context, int fromYDelta){
        AnimationSet set=new AnimationSet(context,null);
        set.setFillAfter(true);

        TranslateAnimation animation=new TranslateAnimation(0,0,fromYDelta,0);
        animation.setDuration(ANIMATION_IN_TIME);
        set.addAnimation(animation);

        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(ANIMATION_IN_TIME);
        set.addAnimation(alphaAnimation);


        return set;
    }

    public static Animation createOutAnimation(Context context,int toYDelta){
        AnimationSet set=new AnimationSet(context,null);
        set.setFillAfter(true);

        TranslateAnimation animation=new TranslateAnimation(0,0,0,toYDelta);
        animation.setDuration(ANIMATION_OUT_TIME);
        set.addAnimation(animation);

        AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        set.addAnimation(alphaAnimation);


        return set;
    }

    public static Animation createInAnimation_Alpha(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        return alphaAnimation;
    }


    public static Animation createOutAnimation_Alpha(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(ANIMATION_OUT_TIME);
        return alphaAnimation;
    }


    /**
     * 给视图添加点击效果,让背景变深
     * */
    public static void addTouchDrak(View view, boolean isClick) {
        view.setOnTouchListener(VIEW_TOUCH_DARK);

        if (!isClick) {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    /**
     * 给视图添加点击效果,让背景变暗
     * */
    public static void addTouchLight(View view, boolean isClick) {
        view.setOnTouchListener(VIEW_TOUCH_LIGHT);

        if (!isClick) {
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    /**
     * 让控件点击时，颜色变深
     * */
    public static final View.OnTouchListener VIEW_TOUCH_DARK = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1,
                0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
        public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

        @SuppressWarnings("deprecation")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(
                            new ColorMatrixColorFilter(BT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setColorFilter(new ColorMatrixColorFilter(
                            BT_NOT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(
                            new ColorMatrixColorFilter(BT_NOT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            }
            return false;
        }
    };

    /**
     * 让控件点击时，颜色变暗
     * */
    public static final View.OnTouchListener VIEW_TOUCH_LIGHT = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 50, 0, 1,
                0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
        public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

        @SuppressWarnings("deprecation")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setDrawingCacheEnabled(true);

                    iv.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(
                            new ColorMatrixColorFilter(BT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setColorFilter(new ColorMatrixColorFilter(
                            BT_NOT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(
                            new ColorMatrixColorFilter(BT_NOT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            }
            return false;
        }
    };
}