package com.nowy.library.mediascanner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Nowy on 2018/2/26.
 * 多媒体扫描工具类
 * 主要用于保存图片到本地后刷新本地多媒体库。
 */

public class MediaScannerUtil {
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    public static void scan(Context context,String path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scanKitkat(context,path);
        } else {
            scanFileAsync(context,path);
        }
    }


    public static void scanKitkat(Context context,String path){
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(path);
    }


    public static void scanKitkat(Context context,String[] pathArr){
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(pathArr);
    }



    public static void scanKitkat(Context context,List<String> pathList){
        MediaScanner mediaScanner = new MediaScanner(context);
        mediaScanner.scan(pathList);
    }


    /**
     * 保存后用广播扫描，Android4.4以上不可用
     * 不会阻塞当前程序进程,效果可能延迟
     */
    private static void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }



    /**
     * 保存后用广播扫描，Android4.4以上不可用
     * 不会阻塞当前程序进程,效果可能延迟
     */
    private static void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }


    /**
     * 保存后用广播扫描，Android4.4以上不可用
     * 不会阻塞当前程序进程,效果可能延迟
     */
    private static void scanSDAsync(Context ctx){
        ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + Environment.getExternalStorageDirectory())));
    }


    /**
     * 文件插入到系统相册中
     * @param context
     * @param fileName eg: xxx.jpg
     * @param filePath
     */
    public static void insertGallery(Context context,String fileName,String filePath){
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),filePath, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件插入到系统相册中
     * @param context
     * @param file
     */
    public static void insertGallery(Context context,File file){
        insertGallery(context, file.getName(),file.getAbsolutePath());
    }

}
