package com.nowy.baselib.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 */
public class ABViewUtil {

    /**
     * 閫傜敤浜嶢dapter涓畝鍖朧iewHolder鐩稿叧浠ｇ爜
     *
     * @param convertView
     * @param id
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T obtainView(View convertView, int id) {
        SparseArray<View> holder = (SparseArray<View>) convertView.getTag();
        if (holder == null) {
            holder = new SparseArray<View>();
            convertView.setTag(holder);
        }
        View childView = holder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            holder.put(id, childView);
        }
        return (T) childView;
    }


    /**
     * view璁剧疆background drawable
     *
     * @param view
     * @param drawable
     */
    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }


    /**
     * 鑾峰彇鎺т欢鐨勯珮搴︼紝濡傛灉鑾峰彇鐨勯珮搴︿负0锛屽垯閲嶆柊璁＄畻灏哄鍚庡啀杩斿洖楂樺害
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
//        int height = view.getMeasuredHeight();
//        if(0 < height){
//            return height;
//        }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 鑾峰彇鎺т欢鐨勫搴︼紝濡傛灉鑾峰彇鐨勫搴︿负0锛屽垯閲嶆柊璁＄畻灏哄鍚庡啀杩斿洖瀹藉害
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(View view) {
//        int width = view.getMeasuredWidth();
//        if(0 < width){
//            return width;
//        }
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 娴嬮噺鎺т欢鐨勫昂瀵�
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }



    /**
     * 浣跨敤ColorFilter鏉ユ敼鍙樹寒搴�
     *
     * @param imageview
     * @param brightness
     */
    public static void changeBrightness(ImageView imageview, float brightness) {
        imageview.setColorFilter(getBrightnessMatrixColorFilter(brightness));
    }
    public static void changeBrightness(Drawable drawable, float brightness) {
        drawable.setColorFilter(getBrightnessMatrixColorFilter(brightness));
    }

    private static ColorMatrixColorFilter getBrightnessMatrixColorFilter(float brightness) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(
                new float[]{
                        1, 0, 0, 0, brightness,
                        0, 1, 0, 0, brightness,
                        0, 0, 1, 0, brightness,
                        0, 0, 0, 1, 0
                });
        return new ColorMatrixColorFilter(matrix);
    }



    /**
     * 中划线
     * @param textView
     */
    public static void middleStrike(TextView textView){
        textView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); //中划线
        textView.getPaint().setAntiAlias(true);//抗锯齿
    }

    /**
     * 下划线
     * @param textView
     */
    public static void underline(TextView textView){
        textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG); //中划线
        textView.getPaint().setAntiAlias(true);//抗锯齿
    }


    /**
     * 取消划线
     * @param tv
     */
    public static void cancelStrike(TextView tv){
        tv.getPaint().setFlags(0);  // 取消设置的的划线
    }
}