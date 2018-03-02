package com.nowy.baselib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.MainThread;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Nowy on 2016/4/8.
 */
public class GlideUtil {
    public static int BG_DEF = android.R.color.white;
    public static int USER_EEF = android.R.color.white;
    public static int BG_DEF_1VS1 =android.R.color.white ;
    public static int BG_BEF_4VS3 = android.R.color.white;

    /**
     * 加载bitmap，如果是GIF则显示第一帧
     */
    public static String LOAD_BITMAP="GLIDEUTILS_GLIDE_LOAD_BITMAP";
    /**
     * 加载gif动画
     */
    public static String LOAD_GIF="GLIDEUTILS_GLIDE_LOAD_GIF";


    /**
     * 加载用户头像
     * @param iv
     * @param url
     */
    public static void displayUser(ImageView iv,String url){
        DrawableRequestBuilder builder = builder(iv,url,false,0,0,USER_EEF).dontAnimate();
        display(iv,builder);
    }

    /**
     * 加载1:1的图片
     * @param iv
     * @param url
     */
    public static void display1Vs1(ImageView iv,String url){
        DrawableRequestBuilder builder = builder(iv,url,false,0,0,BG_DEF_1VS1);
        display(iv,builder);
    }

    /**
     * 加载4:3的图片(宽高)
     * @param iv
     * @param url
     */
    public static void display4Vs3(ImageView iv,String url){
        DrawableRequestBuilder builder = builder(iv,url,false,0,0,BG_BEF_4VS3);
        display(iv,builder);
    }





    public static void display(ImageView iv, String url){
        DrawableRequestBuilder builder = builder(iv,url,false,0,0,BG_DEF);
        display(iv,builder);
    }

    public static void displayWithRes(ImageView iv, File file, int res){
        DrawableRequestBuilder<File>  builder =  builder(iv,file,true,0,0,res).dontAnimate();
        display(iv,builder);
    }

    public static void displayOriginal(ImageView iv, String url){
        DrawableRequestBuilder builder = builder(iv,url,true,Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL,BG_DEF);
        display(iv,builder);
    }


    /**
     * Glide请求图片，会受到Fragment 生命周期控制。
     * @param fragment
     * @param path
     * @param imageView
     * @param placeid
     * @param errorid
     * @param bitmapOrgif  加载普通图片 或者GIF图片 ，GIF图片设置bitmap显示第一帧
     */
    public void LoadFragmentBitmap(android.app.Fragment fragment,String path,ImageView imageView,int placeid,int errorid,String bitmapOrgif){
        if(bitmapOrgif==null||bitmapOrgif.equals(LOAD_BITMAP)){
            Glide.with(fragment).load(path).placeholder(placeid).error(errorid).crossFade().into(imageView);
        }else if(bitmapOrgif.equals(LOAD_GIF)){
            Glide.with(fragment).load(path).asGif().crossFade().into(imageView);
        }
    }
    /**
     * Glide请求图片，会受到support.v4.app.Fragment生命周期控制。
     * @param fragment
     * @param path
     * @param imageView
     * @param placeid
     * @param errorid
     * @param bitmapOrgif  加载普通图片 或者GIF图片 ，GIF图片设置bitmap显示第一帧
     */
    public void LoadSupportv4FragmentBitmap(android.support.v4.app.Fragment fragment,String path,ImageView imageView,int placeid,int errorid,String bitmapOrgif){
        if(bitmapOrgif==null||bitmapOrgif.equals(LOAD_BITMAP)){
            Glide.with(fragment).load(path).placeholder(placeid).error(errorid).crossFade().into(imageView);
        }else if(bitmapOrgif.equals(LOAD_GIF)){
            Glide.with(fragment).load(path).asGif().crossFade().into(imageView);
        }
    }
    //---------------------圆形图片-------------------
    /**
     * 加载设置圆形图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param context
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public void LoadContextCircleBitmap(Context context, String path, final ImageView imageView){
        Glide.with(context).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }
    /**
     * Glide请求图片设置圆形，会受到android.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public static void LoadfragmentCircleBitmap(android.app.Fragment fragment, String path, final ImageView imageView){
        Glide.with(fragment).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }
    /**
     * Glide请求图片设置圆形，会受到android.support.v4.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public static void LoadSupportv4FragmentCircleBitmap(android.support.v4.app.Fragment fragment, String path, final ImageView imageView){
        Glide.with(fragment).load(path).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }
    //-----------------------圆角图片----------------------
    /**
     * 加载设置圆角图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param context
     * @param path
     * @param imageView
     * @param roundradius 圆角大小（>0）
     */
    @SuppressWarnings("unchecked")
    public void LoadContextRoundBitmap(Context context,String path,ImageView imageView,int roundradius){
        if(roundradius<0){
            Glide.with(context).load(path).bitmapTransform(new GlideRoundTransform(context)).into(imageView);
        }else{
            Glide.with(context).load(path).bitmapTransform(new GlideRoundTransform(context,roundradius)).into(imageView);
        }
    }
    /**
     * Glide请求图片设置圆角，会受到android.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     * @param roundradius
     */
    @SuppressWarnings("unchecked")
    public void LoadfragmentRoundBitmap(android.app.Fragment fragment,String path,ImageView imageView,int roundradius){
        if(roundradius<0){
            Glide.with(fragment).load(path).bitmapTransform(new GlideRoundTransform(fragment.getActivity())).into(imageView);
        }else{
            Glide.with(fragment).load(path).bitmapTransform(new GlideRoundTransform(fragment.getActivity(),roundradius)).into(imageView);
        }
    }
    /**
     * Glide请求图片设置圆角，会受到android.support.v4.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     * @param roundradius
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentRoundBitmap(android.support.v4.app.Fragment fragment,String path,ImageView imageView,int roundradius){
        if(roundradius<0){
            Glide.with(fragment).load(path).bitmapTransform(new GlideRoundTransform(fragment.getActivity())).into(imageView);
        }else{
            Glide.with(fragment).load(path).bitmapTransform(new GlideRoundTransform(fragment.getActivity(),roundradius)).into(imageView);
        }
    }
    //-------------------------------------------------
    /**
     * Glide 加载模糊图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param context
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public void LoadContextBlurBitmap(Context context,String path,ImageView imageView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Glide.with(context).load(path).bitmapTransform(new BlurTransformation(context)).into(imageView);
        }else{
            Glide.with(context).load(path).into(imageView);
        }
    }
    /**
     * Glide 加载模糊图片 会受到Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public void LoadFragmentBlurBitmap(android.app.Fragment fragment,String path,ImageView imageView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Glide.with(fragment).load(path).bitmapTransform(new BlurTransformation(fragment.getActivity())).into(imageView);
        }else{
            Glide.with(fragment).load(path).into(imageView);
        }

    }
    /**
     * Glide 加载模糊图片 会受到support.v4.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentBlurBitmap(Fragment fragment,String path,ImageView imageView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Glide.with(fragment).load(path).bitmapTransform(new BlurTransformation(fragment.getActivity())).into(imageView);
        }else{
            Glide.with(fragment).load(path).into(imageView);
        }
    }
    //---------------------------------------------------------
    /**
     * 旋转图片
     *使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param context
     * @param path
     * @param imageView
     * @param rotateRotationAngle 旋转角度
     */
    @SuppressWarnings("unchecked")
    public void LoadContextRotateBitmap(Context context,String path,ImageView imageView,Float rotateRotationAngle){
        Glide.with(context).load(path).bitmapTransform(new RotateTransformation(context, rotateRotationAngle)).into(imageView);
    }
    /**
     * Glide 加载旋转图片 会受到Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     * @param rotateRotationAngle
     */
    @SuppressWarnings("unchecked")
    public void LoadFragmentRotateBitmap(android.app.Fragment fragment,String path,ImageView imageView,Float rotateRotationAngle){
        Glide.with(fragment).load(path).bitmapTransform(new RotateTransformation(fragment.getActivity(), rotateRotationAngle)).into(imageView);
    }
    /**
     * Glide 加载旋转图片 会受到support.v4.app.Fragment生命周期控制
     * @param fragment
     * @param path
     * @param imageView
     * @param rotateRotationAngle
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentRotateBitmap(android.support.v4.app.Fragment fragment,String path,ImageView imageView,Float rotateRotationAngle){
        Glide.with(fragment).load(path).bitmapTransform(new RotateTransformation(fragment.getActivity(), rotateRotationAngle)).into(imageView);
    }


    public static <T> void display(final ImageView iv, T url, boolean isFit, int width, int height, int resImg){
        DrawableRequestBuilder<T> builder = builder(iv, url, isFit,  width,  height, resImg).crossFade();
        builder.into(new MyImageViewTarget(iv){
            @Override
            protected void setResource(GlideDrawable resource) {
                super.setResource(resource);
                if(view != null)
                    view.setImageDrawable(resource);
            }
        });//修改tag
    }


    public static <T> void display(ImageView iv,DrawableRequestBuilder<T> builder){
        builder.into(new MyImageViewTarget(iv){
            @Override
            protected void setResource(GlideDrawable resource) {
                super.setResource(resource);
                if(view != null)
                    view.setImageDrawable(resource);
            }
        });//修改tag
    }

    private static <T> DrawableRequestBuilder<T> builder(final ImageView iv, T url, boolean isFit,
                                                         int width, int height, int resImg){
        DrawableRequestBuilder<T> builder = Glide.with(iv.getContext()).load(url)
                .placeholder(resImg)
                .error(resImg);
        if(width + height > 0)
            builder.override(width, height);
        if(isFit){
            builder.fitCenter();
        }else{
            builder.centerCrop();
        }
        return builder;
    }


    /**
     * 清除缓存
     * @param context
     */
    @MainThread
    public static void clearCache(Context context){
        clearMemory(context);
        clearDiskCache(context);
    }


    /**
     * 清理内存缓存
     * @param context
     */
    public static void clearMemory(Context context){
        Glide.get(context).clearMemory();
    }

    /**
     * 清理磁盘缓存
     * @param context
     */
    public static void clearDiskCache(Context context){
        Glide.get(context).clearDiskCache();
    }



    /**
     * 重新设置tag对应的ID，反正view的setTag覆盖
     */
    public static class MyImageViewTarget extends ImageViewTarget<GlideDrawable> {
        public MyImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setImageDrawable(resource);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }


        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }


    /**
     * 圆形
     */
    public static class GlideCircleTransform extends BitmapTransformation {

        private Paint mBorderPaint;
        private float mBorderWidth;

        public GlideCircleTransform(Context context) {
            super(context);
        }

        public GlideCircleTransform(Context context, float borderWidth, int borderColor) {
            super(context);
            mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;

            mBorderPaint = new Paint();
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }


        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            if (mBorderPaint != null) {
                float borderRadius = r - mBorderWidth / 2;
                canvas.drawCircle(r, r, borderRadius, mBorderPaint);
            }
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }



    //----------------------旋转---------------------------
    /**
     *旋转
     */
    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }
    //--------------------------------------------------

    //-----------------------------图片模糊----------------------------------
    /**
     *图片模糊
     * API 17以上使用
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public class BlurTransformation extends BitmapTransformation {

        private RenderScript rs;

        public BlurTransformation(Context context) {
            super( context );

            rs = RenderScript.create( context );
        }


        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap blurredBitmap = toTransform.copy( Bitmap.Config.ARGB_8888, true );

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(
                    rs,
                    blurredBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SHARED
            );
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = null;

            script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(10);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            toTransform.recycle();

            return blurredBitmap;
        }

        @Override
        public String getId() {
            return "blur";
        }
    }
    //-------------------图片转换圆角图片------------------------------
    /**
     *图片转换圆角图片
     */
    public  class GlideRoundTransform extends BitmapTransformation {

        private  float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        /**
         * 自定义圆角大小
         * @param context
         * @param dp
         */
        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private  Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }



    /**
     * GIF
     */
    public static  class MyGifTarget extends ImageViewTarget<GifDrawable> {
        public MyGifTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(GifDrawable resource) {
            view.setImageDrawable(resource);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }




    /**
     * GIF
     */
    public static class MyGifImageViewTarget extends GlideDrawableImageViewTarget {

        public MyGifImageViewTarget(ImageView view) {
            super(view);
        }
        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id,request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }
    @SuppressWarnings("unchecked")
    public static <Z> Target<Z> buildTarget(ImageView view, Class<Z> clazz) {
        if (GlideDrawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new MyGifImageViewTarget(view);
        } else if (Bitmap.class.equals(clazz)) {
            return (Target<Z>) new BitmapImageViewTarget(view);
        } else if (Drawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new DrawableImageViewTarget(view);
        } else {
            throw new IllegalArgumentException("Unhandled class: " + clazz
                    + ", try .as*(Class).transcode(ResourceTranscoder)");
        }
    }
}
