package com.nowy.baselib.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import com.nowy.baselib.R;
import com.orhanobut.logger.Logger;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nowy on 2018/2/1.
 * 前景色遮罩层管理器
 * 采用FrameLayout主要是为了兼容低版本，
 * FrameLayout的Foreground(前景色)在低版本中就有
 * View的需要api 23
 */

public class ForegroundMaskManager {
//    private Reference<FrameLayout> mLayoutRef;
    private List<Reference<FrameLayout>> mLayoutRefList = new ArrayList<>();
    //setAlpha(from=0,to=255)
    private static final int ALPHA_TRANSPARENT = 0;//全透明
    private static final int ALPHA_TRANSLUCENT = 127;//半透明
    private static final long ANIM_DURATION = 300;//动画时间


    private boolean mAnimRunning = false;
    private OnMaskAlphaListener mListener;

    @DrawableRes
    private int mDrawableRes;


    private ForegroundMaskManager(@DrawableRes int mDrawableRes,FrameLayout... layoutArr) {
        if(layoutArr!= null&&layoutArr.length > 0){
            for(FrameLayout layout : layoutArr)
                mLayoutRefList.add(new WeakReference<>(layout));
        }
        this.mDrawableRes = mDrawableRes;
    }



    private void setForeground(Reference<FrameLayout> ref,@DrawableRes int drawableRes){
        if(ref.get() != null){
            Drawable drawable = ContextCompat.getDrawable(ref.get().getContext(),drawableRes);
            if(drawable != null){
                drawable.mutate().setAlpha(ALPHA_TRANSPARENT);
                ref.get().setForeground(drawable);
//                ref.get().getForeground().mutate().setAlpha();
            }
        }
    }


    private void setForegroundInList(@DrawableRes int drawableRes){
        if(mLayoutRefList.size() > 0 ){
            for(Reference<FrameLayout> ref : mLayoutRefList){
                setForeground(ref,drawableRes);
            }
        }
    }


    private Drawable getForegroundByPosition(int position){
        if(position >= 0 && mLayoutRefList.size() > position){
            if(mLayoutRefList.get(position) == null) return null;
            if(mLayoutRefList.get(position).get() == null) return null;
            return mLayoutRefList.get(position).get().getForeground();
        }
        return null;
    }



    public static ForegroundMaskManager initMaskManager(FrameLayout... layout){
        return initMaskManager(R.drawable.mask_layer_dark,layout);
    }

    //drawable的argb通道中，alpha通道不要设置
    public static ForegroundMaskManager initMaskManager(@DrawableRes int drawableRes,FrameLayout... layout){
        ForegroundMaskManager manager = new ForegroundMaskManager(drawableRes,layout);
        manager.setForegroundInList(drawableRes);
        return manager;
    }


    public void showMask(){

        setAlpha(true,ALPHA_TRANSPARENT,ALPHA_TRANSLUCENT);
    }

    public void hideMask(){
        setAlpha(false,ALPHA_TRANSLUCENT,ALPHA_TRANSPARENT);
    }


    public boolean isAnimRunning(){
        return mAnimRunning;
    }




    public void clear(){
        if(mLayoutRefList != null && mLayoutRefList.size() > 0 ){
            for(Reference<FrameLayout> ref : mLayoutRefList){
                if(ref.get() != null && ref.get().getForeground() != null){
                    ref.get().getForeground().mutate().setAlpha(0);
                }
            }
        }

        if(mLayoutRefList != null)
            mLayoutRefList.clear();
    }

    private void setAlpha(final boolean isShow, final int from , final int to){
        ValueAnimator animator = ValueAnimator.ofInt(from,to);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(ANIM_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimRunning = true;
                if(mLayoutRefList != null && mLayoutRefList.size() > 0 ){
                    for(Reference<FrameLayout> ref : mLayoutRefList){
                        if(ref.get() != null && ref.get().getForeground() != null){
                            ref.get().getForeground().mutate().setAlpha((Integer) animation.getAnimatedValue());
                        }
                    }
                }
                if(mListener != null){
                    Logger.t("setAlpha").e("getAnimatedValue "+animation.getAnimatedValue());
                    float value =  (((Integer)animation.getAnimatedValue())*1.0f)/Math.abs(from - to);
                    mListener.onMaskAlpha(isShow,value);
                }

            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimRunning = false;
                super.onAnimationEnd(animation);
            }
        });

        animator.start();
    }

    public void setOnMaskAlphaListener(OnMaskAlphaListener listener) {
        this.mListener = listener;
    }

    public interface OnMaskAlphaListener{
        void onMaskAlpha(boolean isShow,float fraction);
    }


}
