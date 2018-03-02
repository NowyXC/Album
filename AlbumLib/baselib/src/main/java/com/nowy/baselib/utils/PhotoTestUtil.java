package com.nowy.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/2/25.
 */

public class PhotoTestUtil {
    private static Uri imageUri;

    /**
     *
     * 拍照
     */
    public static Uri takePhoto(Activity mActivity,String authority, int requestCode) {

        //指定拍照intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = null;
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            String sdcardState = Environment.getExternalStorageState();
            File outputImage = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                try {
                    outputImage = createImageFile(mActivity);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "内存异常", Toast.LENGTH_SHORT).show();
            } try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            } if (outputImage != null) {
                imageUri = Uri.fromFile(outputImage);
                if(Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                    //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                    imageUri = FileProvider.getUriForFile(mActivity, authority, outputImage);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                mActivity.startActivityForResult(takePictureIntent, requestCode);
            }
        } return imageUri;
    }

    private static File createImageFile(Activity mActivity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;//创建以时间命名的文件名称
        File storageDir = getOwnCacheDirectory(mActivity, "takephoto");//创建保存的路径
        File image = new File(storageDir.getPath(), imageFileName + ".jpg");
        if (!image.exists()) {
            try { //在指定的文件夹中创建文件
                image.createNewFile();
            } catch (Exception e) {

            }
        }

        return image;
    }

    /**
     * 根据目录创建文件夹
     * @param context
     * @param cacheDir
     * @return
             */
    private static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //判断sd卡正常挂载并且拥有权限的时候创建文件
        if ( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }

        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }

        return appCacheDir;
    }


    /**
     * 返回Bitmap对象
     * @return
     */
    public static Bitmap getBitmap(){
        if (imageUri==null)return null;
        Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), getOptions(imageUri.getPath()));
        return bitmap;
    }



    /**
     * 获取压缩图片的options
     *
     * @return
     */
    private static BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 4; //此项参数可以根据需求进行计算
        options.inJustDecodeBounds = false;
        return options;

    }
}
