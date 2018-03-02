package com.nowy.baselib.views.scroll;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.nowy.baselib.R;
import com.nowy.baselib.utils.AppUtil;


/**
 * Created by Nowy on 2016/12/2.
 * 可设置最大高度的scrollView
 */

public class MaxHeightScrollView extends ScrollView {
    private int mMaxHeight = 0;//最大高度
    private static final float MAX_HEIGHT_DEF = 250.0f;//默认最大高度
    private ScrollListener mScrollListener;
    private boolean mIsScrolledToBottom = false;//是否滑动到底部
    public MaxHeightScrollView(Context context) {
        super(context);
        init(context,null,0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attr, int defStyle){
        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.maxHeightScrollView,defStyle,0);
        mMaxHeight = a.getDimensionPixelOffset(R.styleable.maxHeightScrollView_maxHeight,
                AppUtil.dip2px(getContext(),MAX_HEIGHT_DEF));
        a.recycle();


    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.mScrollListener = scrollListener;
    }

    public void setMaxHeight(int max){
        this.mMaxHeight = max;
        measure(0,0);
    }


    public void  setHeight(int height){
        ViewGroup.LayoutParams lp = getLayoutParams();
        if(lp == null) generateDefaultLayoutParams();
        lp.height = height > mMaxHeight ? mMaxHeight : height;
        setLayoutParams(lp);
    }

    public void setHalfScreenHeight(Activity activity){
        if(activity != null){
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics d = new DisplayMetrics();
            display.getMetrics(d);
            setMaxHeight(d.heightPixels / 2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mMaxHeight > 0)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if(scrollY > 0 ){
            if(null != mScrollListener && mIsScrolledToBottom != clampedY){
                mScrollListener.onScrollBottomListener(clampedY);
            }
            mIsScrolledToBottom = clampedY;
        }else {

        }
    }

    public interface ScrollListener{
        void onScrollBottomListener(boolean clampedY);
    }
}
