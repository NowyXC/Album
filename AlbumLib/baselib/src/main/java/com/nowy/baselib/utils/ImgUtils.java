package com.nowy.baselib.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Nowy on 2017/3/24.
 */

public class ImgUtils {
    public static File getCachePath(Context context){
        return context.getCacheDir();
    }


    public static File getFilePath(Context context){
        return context.getFilesDir();
    }

    public static String saveBmp2Cache(Context context, String relativeDir, String fileName, Bitmap bmp){
        return saveBitmap2Cache(context,relativeDir,fileName,bmp,100);
    }


    public static String saveBmp2FileDir(Context context, String relativeDir, String fileName, Bitmap bmp){
        return saveBitmap2File(context,relativeDir,fileName,bmp,100);
    }

    public static String saveBitmap2Cache(Context context, String relativePath, String fileName, Bitmap bm, int quality) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath + "/";
        }
        File file = null;
        FileOutputStream out = null;
        try {
            File dir = createCacheDir(context,relativePath);
            file = createFile(dir , fileName);
            out = new FileOutputStream(file.getPath());
            bm.compress(Bitmap.CompressFormat.PNG, quality, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            closeIO(out);
        }
    }



    public static String saveBitmap2File(Context context, String relativePath, String fileName, Bitmap bm, int quality) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath + "/";
        }
        File file = null;
        FileOutputStream out = null;
        try {
            File dir = createFileDir(context,relativePath);
            file = createFile(dir , fileName);
            out = new FileOutputStream(file.getPath());
            bm.compress(Bitmap.CompressFormat.PNG, quality, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            closeIO(out);
        }
    }







    public static File createCacheDir(Context context, String dirRelativePath) {
        File dir = new File(getCachePath(context), dirRelativePath);
        dir.mkdirs();
        return dir;
    }

    public static File createFileDir(Context context, String dirRelativePath) {
        File dir = new File(getFilePath(context), dirRelativePath);
        dir.mkdirs();
        return dir;
    }


    public static File createFile(File dir, String fileRelativePath) throws IOException {
        File file = new File(dir,fileRelativePath);
        file.createNewFile();
        return file;
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {

            }
        }
    }
}
