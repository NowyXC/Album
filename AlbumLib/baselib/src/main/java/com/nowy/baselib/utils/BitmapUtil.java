package com.nowy.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Nowy on 2017/12/21.
 * 图片工具类
 * # 获取bitmap宽高数组
 * # 旋转bmp
 * # 判断图片旋转情况
 * # 转换View到Bitmap(截屏)
 * # 根据uri获取图片
 * # 图片uri转path
 * # 根据path获取Bmp
 * # 根据路径获得bmp并压缩返回bitmap用于显示
 * # 合并bmp
 * # 将多个Bitmap合并成一个图片
 * # 保存bmp到本地
 * # 获取屏幕大小的drawable实例
 * # 计算缩放比例inSampleSize
 */

public class BitmapUtil {
    public static final String TAG = BitmapUtil.class.getSimpleName();

    /**
     * 获取bitmap宽高数组
     * @return 0：宽 1：高
     */
    public static int[] getBitmapWidthAndHeight(String file){
        int [] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file,options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        options.inJustDecodeBounds = false;
        return size;
    }


    /**
     *
     * 从Assets中读取图片
     * @param filepath 相对路径
     * @return Bitmap
     */
    public static Bitmap getImageFromAssetsFile(String filepath, Context context) {
        Bitmap image = null;
        InputStream is = null;
        AssetManager am = context.getResources().getAssets();
        try {
            is = am.open(filepath);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return image;
    }


    /**
     *
     * 从Assets中读取图片
     * @param filepath 相对路径
     * @return InputStream
     */
    public static InputStream getImageFromAssetsFileInputStream(String filepath, Context context) {
        InputStream is = null;
        AssetManager am = context.getResources().getAssets();
        try {
            is = am.open(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * 旋转bmp
     * @param degree ： 图片被系统旋转的角度
     * @param bitmap ： bmp
     * @return 纠向后的图片
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return bm;
    }

    /**
     * 放大图片
     * @param bmp
     * @param big
     * @return
     */
    public static Bitmap bigImage(Bitmap bmp, float big) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(big, big);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }

    /**
     * 判断图片旋转情况
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            final ExifInterface exifInterface = new ExifInterface(path);
            final int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 转换View到Bitmap(截屏)
     * @param v
     * @return
     */
    public static Bitmap convertViewToBitmap(View v) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        return bitmap;
    }


    /**
     * 根据uri获取图片
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            // 读取uri所在的图片
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    /**
     * 图片uri转path
     * @param uri
     * @param activity
     * @return
     */
    public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        try {
            if (value.startsWith("/external")) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
                // Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    final String picUri = cursor.getString(column_index);
                    //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                    if(Integer.parseInt(Build.VERSION.SDK) < 14)
                    {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    return picUri;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return value;
    }


    /**
     * 根据path获取Bmp
     * @param filePath
     * @return
     */
    public static Bitmap getPathToBitmap(String filePath) {
        Bitmap bm = BitmapFactory.decodeFile(filePath);

        // 处理某些手机拍照角度旋转的问题
        final int degree = readPictureDegree(filePath);
        if (degree != 0) {// 旋转照片角度
            bm = rotateBitmap(degree,bm);
        }
        return bm;
    }


    /**
     * 获取bmp的大小
     * @param bitmap
     * @return
     */
    public static long getBitmapSize(Bitmap bitmap){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }



    /**
     * 根据路径获得bmp并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = computeSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        Bitmap bm = BitmapFactory.decodeFile(filePath, options);

        // 处理某些手机拍照角度旋转的问题
        final int degree = readPictureDegree(filePath);
        if (degree != 0) {// 旋转照片角度
            bm = rotateBitmap(degree,bm);
        }
        return bm;
    }



    /**
     * 合并bmp
     * @param first 第一张图片
     * @param second 第二张图片
     * @param fromPoint 第二张图片的开始位置的坐标
     * @return
     */
    public static Bitmap mixtureBitmap(Bitmap first, Bitmap second,PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }


    /**
     * 将多个Bitmap合并成一个图片。
     *
     * @param columns 将多个图合成多少列
     * @param bitmaps
     *            ... 要合成的图片
     * @return
     */
    public static Bitmap combineBitmaps(int columns, Bitmap... bitmaps) {
        if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
            throw new IllegalArgumentException(
                    "Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
        }
        int maxWidthPerImage = 20;
        int maxHeightPerImage = 20;
        for (Bitmap b : bitmaps) {
            maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage
                    : b.getWidth();
            maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage
                    : b.getHeight();
        }
        Logger.t(TAG).d("maxWidthPerImage=>" + maxWidthPerImage
                + ";maxHeightPerImage=>" + maxHeightPerImage);
        int rows = 0;
        if (columns >= bitmaps.length) {
            rows = 1;
            columns = bitmaps.length;
        } else {
            rows = bitmaps.length % columns == 0 ? bitmaps.length / columns
                    : bitmaps.length / columns + 1;
        }
        Bitmap newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows
                * maxHeightPerImage, Bitmap.Config.ARGB_8888);
        Logger.t(TAG).d("newBitmap=>" + newBitmap.getWidth() + ","
                + newBitmap.getHeight());
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                int index = x * columns + y;
                if (index >= bitmaps.length)
                    break;
                Logger.t(TAG).d("y=>" + y + " * maxWidthPerImage=>"
                        + maxWidthPerImage + " = " + (y * maxWidthPerImage));
                Logger.t(TAG).d("x=>" + x + " * maxHeightPerImage=>"
                        + maxHeightPerImage + " = " + (x * maxHeightPerImage));
                newBitmap = mixtureBitmap(newBitmap, bitmaps[index],
                        new PointF(y * maxWidthPerImage, x * maxHeightPerImage));
            }
        }
        return newBitmap;
    }


    /**
     * 保存bmp到本地
     * @param imgPath 保存地址
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static boolean saveBitmapJPG(String imgPath, Bitmap bitmap) throws IOException {
        File imageFile = new File(imgPath);
        File tmpFile = new File(imageFile.getAbsolutePath() ,"save_"+System.currentTimeMillis()+ ".tmp");
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile),  32 * 1024); // 32 Kb
        boolean savedSuccessfully = false;

        try {
            savedSuccessfully = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } finally {
            os.close();
            if(savedSuccessfully && !tmpFile.renameTo(imageFile)) {
                savedSuccessfully = false;
            }

            if(!savedSuccessfully) {
                tmpFile.delete();
            }
        }

        bitmap.recycle();
        return savedSuccessfully;
    }


    /**
     * 获取屏幕大小的drawable实例
     * @param imgPath
     * @param mContext
     * @return
     */
     public static Drawable getScaleDraw(Context mContext,String imgPath) {

        Bitmap bitmap = null;
        try {
            Log.d(TAG, "[getScaleDraw]imgPath is " + imgPath.toString());
            File imageFile = new File(imgPath);
            if (!imageFile.exists()) {
                Log.d(TAG, "[getScaleDraw]file not  exists");
                return null;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgPath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1,
                    DeviceUtil.getDeviceHeight(mContext) * DeviceUtil.getDeviceWidth(mContext));
            // Log.d(TAG,"inSampleSize===>"+opts.inSampleSize);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imgPath, opts);

        } catch (OutOfMemoryError err) {
            Log.d(TAG, "[getScaleDraw] out of memory");

        }
        if (bitmap == null) {
            return null;
        }
        Drawable resizeDrawable = new BitmapDrawable(mContext.getResources(),
                bitmap);
        return resizeDrawable;
    }


    /**
     * 计算缩放比例inSampleSize
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;

    }



    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }

    }
}
