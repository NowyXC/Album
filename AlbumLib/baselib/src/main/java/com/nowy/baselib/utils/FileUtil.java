package com.nowy.baselib.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import com.nowy.baselib.app.BaseApp;
import com.nowy.baselib.utils.encryption.MD5Utils;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文件操作类
 */
public class FileUtil {

    public static final String TAG = FileUtil.class.getSimpleName();



    //SD卡根路径
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();



    //是否剪切
    public static boolean CUT_FLAG = false;
    //是否复制
    public static boolean COPY_FLAG = false;
    //是否删除
    public static boolean DELETE_FLAG = false;


    /** Regular expression for safe filenames: no spaces or metacharacters */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");


    /**
     * 是否有SDCard
     *
     * @return
     */
    public static boolean haveSDCard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    /**
     * SD卡是否可以移除
     * @return
     */
    public static boolean SDCardRemovable(){
        return Environment.isExternalStorageRemovable();
    }


    /**
     * 获取SD卡根目录路径
     * @return
     */
    public static String getSDCardPath() {
        return SD_CARD_PATH;
    }


    /**
     * 获取APP根目录路径
     * @param context
     * @return context.getFilesDir().getParent() + File.separator
     */
    public static String getAppPath(Context context) {
        return context.getFilesDir().getParent() + File.separator;
    }




    /**
     * 获取项目根路径
     * SD卡存在：/storage/sdcard0/Android/data/<package包名>/
     * sd卡不存在: /data/data/<package包名>/
     * 如果获取的是手机内部目录的话
     * @param context
     * @param applicationDir
     * @return
     */
    public static String getRootPath(Context context, String applicationDir) {
        if (haveSDCard()) {
            String sdcardPath = getSDCardPath();
            if(!TextUtil.isEmpty(applicationDir)){
                sdcardPath = sdcardPath + File.separator + applicationDir + File.separator;
            }
            Logger.t(TAG).d( "have sdcard! sdcard path: " + sdcardPath);
            return sdcardPath;
        } else {
            String dirPath = getAppPath(context);
            if(!TextUtil.isEmpty(applicationDir)){
                dirPath = dirPath + applicationDir + File.separator;
            }
            Logger.t(TAG).d( "have no sdcard! dir path: " + dirPath);
            return dirPath;
        }
    }


    /**
     * 创建文件夹(默认首先在SdCard中创建文件夹，如SdCard不存在, 则在手机中创建文件夹)
     * @param path
     * @return String
     */
    public static String getRootFilePath(String path) {
        String file;
        //SdCard已存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = Environment.getExternalStorageDirectory().getPath() + path;
            Logger.t(TAG).i("AppFileMgr-->>getRootFilePath-->>file", "创建文件夹路径为" + file);
            File files = new File(file);
            if (files == null || !files.exists()) {
                files.mkdir();
            }
            return file;
        }else {//SdCard卡不存在
            file = Environment.getRootDirectory().getPath() + path;
            Logger.t(TAG).i("AppFileMgr-->>getRootFilePath-->>file:","创建文件夹的路径为" + file);
            File files = new File(file);
            if (files == null || !files.exists()) {
                files.mkdir();
            }
            return file;
        }
    }


    /**
     * 获取磁盘的缓存目录,SD卡可用优先用SD卡的缓存目录
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if ((haveSDCard()|| !SDCardRemovable())
                && getExternalCacheDir(context) != null) {//SD卡可用就用外部缓存
            cachePath = getExternalCacheDir(context).getPath();
        } else {
            cachePath = getCacheDir(context).getPath();
        }
        Logger.t(TAG).i("cachePath:"+cachePath);
        return cachePath;
    }



    /**
     * 获取内部的项目缓存目录
     * /data/data/<package包名>/cache
     * @param context
     * @return
     */
    public static File getCacheDir(Context context){
        Logger.t(TAG).i("内部缓存目录:"+context.getCacheDir().getPath());
        return context.getCacheDir();
    }


    /**
     * 获取sd的项目缓存目录
     * /storage/sdcard0/Android/data/<package包名>/cache
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return context.getExternalCacheDir();
        }
        return null;
    }


    /**
     * Environment中的dir type类型
     * {@link android.os.Environment#DIRECTORY_MUSIC},//音乐存放的标准目录
     * {@link android.os.Environment#DIRECTORY_PODCASTS},//系统广播存放的标准目录
     * {@link android.os.Environment#DIRECTORY_RINGTONES},//系统铃声存放的标准目录
     * {@link android.os.Environment#DIRECTORY_ALARMS},   // 系统提醒铃声存放的标准目录。
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},//系统通知铃声存放的标准目录
     * {@link android.os.Environment#DIRECTORY_PICTURES}, //图片存放的标准目录
     * {@link android.os.Environment#DIRECTORY_DCIM },     //相机拍摄照片和视频的标准目录
     * {@link android.os.Environment#DIRECTORY_MOVIES},   //电影存放的标准目录
     * {@link android.os.Environment#DIRECTORY_DOCUMENTS},//文档
     * {@link android.os.Environment#DIRECTORY_DOWNLOADS}.//下载的标准目录
     * @return
     */


    /**
     * 获取磁盘的文件缓存目录,SD卡可用优先用SD卡的文件缓存目录
     * @param context
     * @return
     */
    public static String getDiskFilesDir(Context context,String type) {
        String filesPath;
        if ((haveSDCard()|| !SDCardRemovable())
                && getExternalFilesDir(context,type) != null) {//SD卡可用就用外部缓存
            filesPath = getExternalFilesDir(context,type).getPath();
        } else {
            filesPath = getFilesDir(context).getPath();
        }
        Logger.t(TAG).i("cachePath:"+filesPath);
        return filesPath;
    }



    /**
     * 获取项目的内部缓存目录路径
     * @param context
     * @return
     */
    public static File getFilesDir(Context context){
        Logger.t(TAG).i("内部缓存目录:"+context.getFilesDir().getPath());
        return context.getFilesDir();
    }


    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception 　　
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Logger.t(TAG).e("文件不存在!");
        }
        return size;
    }


    /**
     * 获取SDCard的项目外部缓存目录
     * 需要权限
     * < uses-permission android:name ="android.permission.WRITE_EXTERNAL_STORAGE" />
     * < uses-permission android:name ="android.permissions.WRITE_EXTERNAL_STORAGE" />
     * @param context
     * @param type 文件目录类型
     * {@link android.os.Environment#DIRECTORY_MUSIC},//音乐
     * {@link android.os.Environment#DIRECTORY_PODCASTS},//音频
     * {@link android.os.Environment#DIRECTORY_RINGTONES},//铃声
     * {@link android.os.Environment#DIRECTORY_ALARMS},   // 闹铃
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},//通知铃声
     * {@link android.os.Environment#DIRECTORY_PICTURES}, //图片
     * {@link android.os.Environment#DIRECTORY_DCIM },     //相机拍摄照片和视频的标准目录
     * {@link android.os.Environment#DIRECTORY_MOVIES},   //视频
     * {@link android.os.Environment#DIRECTORY_DOCUMENTS},//文档
     * {@link android.os.Environment#DIRECTORY_DOWNLOADS}.//下载路径
     * @return
     */
    public static File getExternalFilesDir(Context context,String type){
        Logger.t(TAG).i("内部缓存目录:"+context.getExternalFilesDir(type).getPath());
        File dir = context.getExternalFilesDir(type);
        if(dir == null){
            Logger.t(TAG).e("Directory不存在");
            return null;
        }
        if(!dir.mkdirs()) {
            Logger.t(TAG).e("Directory创建失败");
        }
        return dir;
    }


    /**
     * 获取外部音乐的路径
     * @param context
     * @return
     */
    public static File getExternalMusicDir(Context context){
        return getExternalFilesDir(context,Environment.DIRECTORY_MUSIC);
    }

    /**
     * 获取外部图片存放路径
     * @param context
     * @return
     */
    public static File getExternalPictureDir(Context context){
        return getExternalFilesDir(context,Environment.DIRECTORY_PICTURES);
    }


    /**
     * Environment.getExternalStoragePublicDirectory为保存公共资源
     * 在删除应用之后都不需要连带删除
     */
    /**
     * 获取拍照的外部图片存放路径
     * @return
     */
    public static File getStorageDCIMDir() {
        return getExternalStoragePublicDir(Environment.DIRECTORY_DCIM);
    }


    /**
     * 获取拍照的外部图片存放路径
     * @return
     */
    public static File getStoragePicturesDir() {
        return getExternalStoragePublicDir(Environment.DIRECTORY_PICTURES);
    }



    /**
     * 获取外部文档存放路径
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static File getStorageDocDir() {
        return getExternalStoragePublicDir(Environment.DIRECTORY_DOCUMENTS);
    }


    /**
     * 获取外部下载路径
     * @return
     */
    public static File getStorageDownloadsDir() {
        return getExternalStoragePublicDir(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * 共享文件
     * 例如:Environment.DIRECTORY_DCIM 公共相册、视频资源
     *
     * @param type 这个方法接收一个参数，表明目录所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量，比如DIRECTORY_DCIM等。
     * @return
     */
    public static File getExternalStoragePublicDir(String type){
        File dir = Environment.getExternalStoragePublicDirectory(type);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                Logger.t(TAG).e("内部缓存目录创建失败！");
            }
        }
        Logger.t(TAG).i("内部缓存目录:"+dir.getPath());
        return dir;
    }





    /**
     * 在SD卡上创建文件
     * @param fileRelativePath 相对路径
     * @throws IOException
     */
    public static File createSDFile(String fileRelativePath) throws IOException {
        File file = new File(SD_CARD_PATH + fileRelativePath);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @param dirRelativePath 相对路径
     * @param dirRelativePath
     */
    public static File createSDDir(String dirRelativePath) {
        File dir = new File(SD_CARD_PATH + dirRelativePath);
        dir.mkdirs();
        return dir;
    }


    /**
     * 获取一个文件对象，如果不存在，则自动创建
     * @param parentDir
     * @param dirPath
     * @return
     */
    public static File getDirAutoCreated(String parentDir,String dirPath) {
        File dirFile = new File(parentDir,dirPath);
        if (dirFile.isFile()) {
            Logger.t(TAG).e( "[getDirAutoCreated]file[" + dirPath + "] is file!");
            return dirFile;
        }
        if (!dirFile.exists()) {
            if(!dirFile.mkdirs()){
                Logger.t(TAG).e( "[getDirAutoCreated]file[" + dirPath + "] create error !");
            }
        }
        return dirFile;
    }



    /**
     * 获取一个文件对象，如果不存在，则自动创建
     *
     * @param filePath 绝对路径
     * @return
     */
    public static File getFileAutoCreated(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            Logger.t(TAG).e("[getFileAutoCreated]file[" + filePath + "] is directory!");
            return file;
        }
        if (file.exists()) {
            return file;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.t(TAG).e(e,e.getMessage());
        }
        return file;
    }

    /**
     * 获取一个目录对象，如果不存在，则自动创建
     *
     * @param dirPath
     * @return
     */
    public static File getDirAutoCreated(String dirPath) {
        File dirFile = new File(dirPath);
        if (dirFile.isFile()) {
            Logger.t(TAG).e( "[getDirAutoCreated]file[" + dirPath + "] is file!");
            return dirFile;
        }
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }




    /**
     * 判断SD卡上的文件夹是否存在
     * @param relativePath 相对路径
     */
    public static boolean isFileExistInSDCard(String relativePath) {
        File file = new File(SD_CARD_PATH + relativePath);
        return file.exists();
    }


    /**
     * 文件夹是否存在
     * @param filePath 绝对路径
     */
    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }




    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param fileUri
     */
    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    /**
     * 通过本地多媒体库获取文件路径
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * 先质量压缩到90%，再把bitmap保存到sd卡上
     *
     * @param relativePath
     * @param fileName
     * @param bm
     * @return
     * @author com.tiantian
     */
    public static int saveBitmap2SD(String relativePath, String fileName, Bitmap bm) {
        return saveBitmap2SD(relativePath, fileName, bm, 90);
    }

    /**
     * 先质量压缩到指定百分比（0% ~ 90%），再把bitmap保存到sd卡上
     *
     * @param relativePath 相对路径
     * @param fileName 文件名(eg:img.jpg)
     * @param bm
     * @param quality
     * @return
     */
    public static int saveBitmap2SD(String relativePath, String fileName, Bitmap bm, int quality) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath + "/";
        }
        File file = null;
        FileOutputStream out = null;
        try {
            createSDDir(relativePath);
            file = createSDFile(relativePath + fileName);
            out = new FileOutputStream(file.getPath());
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeIO(out);
        }
    }

    /**
     * 先质量压缩到指定百分比（0% ~ 90%），再把bitmap保存到sd卡上
     *
     * @param filePath 绝对路径
     * @param bm
     * @param quality
     * @return
     */
    public static int saveBitmap2SDAbsolute(String filePath, Bitmap bm, int quality) {
        File file = null;
        FileOutputStream out = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file.getPath());
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeIO(out);
        }
    }


    /**
     * 压缩图片直到容量小于200kb，并保存到sdcard
     *
     * @param relativePath
     * @param fileName
     * @param bm
     * @return
     */
    public static int saveBitmap2SDWithCapacity(String relativePath, String fileName, Bitmap bm) {
        return saveBitmap2SDWithCapacity(relativePath, fileName, bm, 200);
    }

    /**
     * 压缩图片直到容量小于指定值(kb)，并保存到sdcard
     *
     * @param relativePath 相对路径
     * @param fileName 文件名(eg:img.jpg)
     * @param bm
     * @param capacity
     * @return
     */
    public static int saveBitmap2SDWithCapacity(String relativePath, String fileName, Bitmap bm, int capacity) {
        if (!relativePath.endsWith("/")) {
            relativePath = relativePath + "/";
        }
        File file = null;
        FileOutputStream out = null;
        ByteArrayInputStream bais = null;
        try {
            createSDDir(relativePath);
            file = createSDFile(relativePath + fileName);
            out = new FileOutputStream(file.getPath());
//            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            bais = compressImage(bm, capacity);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bais.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeIO(out, bais);
        }

    }

    /**
     * 压缩图片直到容量小于指定值(kb)
     *
     * @param image
     * @param capacity
     * @return
     */
    public static ByteArrayInputStream compressImage(Bitmap image, int capacity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > capacity) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            if (options < 10) {
                break;
            }
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        baos.reset();
        return bais;
    }


    /**
     * 把uri转为File对象
     *
     * @param context
     * @param uri
     * @return
     */
    public static File uri2File(Context context, Uri uri) {
        // 而managedquery在api 11 被弃用，所以要转为使用CursorLoader,并使用loadInBackground来返回
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
            cursor = loader.loadInBackground();
            if (null == cursor) {
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.t(TAG).e(ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static File uri2FileInteral(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        File file = uri2File(context, uri);
        return null == file ? new File(uri.getPath()) : file;
    }







    /**
     * 打开文件
     * @param context
     * @param path
     */
    public static void openFile(Context context, String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMimeType(path);
        //参数二type见下面
        intent.setDataAndType(Uri.fromFile(new File(path)), type);
        context.startActivity(intent);
    }


    /**
     * 获取文件行数
     *
     * @param file 文件
     * @return 文件行数
     */
    public static int getFileLines(File file) {
        int count = 1;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int readChars;
            while ((readChars = is.read(buffer, 0, 1024)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (buffer[i] == '\n') ++count;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
        }
        return count;
    }



    /**
     * 批量删除文件
     * @param files
     */
    public static void deleteFile(File... files) {
        if (!TextUtil.isEmpty(files)) {
            for (File file : files) {
                try {
                    file.delete();
                } catch (RuntimeException ex) {
                    Logger.t(TAG).e(ex,ex.getMessage());
                }
            }
        }
    }

    /**
     * 删除文件夹,包括自身
     *
     * @param file
     */
    public static void deleteFolder(File file) {
        if (file.exists() && file.isDirectory()) {//判断是文件还是目录
            if (file.listFiles().length > 0) {//若目录下没有文件则直接删除
                //若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = file.listFiles();
                int i = file.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        deleteFolder(delFile[j]);//递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();//删除文件  
                }
            }
            file.delete();
        }
    }

    /**
     * 删除文件夹,包括自身
     *
     * @param filePath
     */
    public static void deleteFolder(String filePath) {
        File file = new File(filePath);
        deleteFolder(file);
    }


    /**
     * 从文件中读取Byte数组
     * Add by Hubert
     *
     * @param file
     * @return byte[]
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        // 获取文件大小
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // 文件太大，无法读取
            throw new IOException("File is to large " + file.getName());
        }

        // 创建一个数据来保存文件数据
        byte[] bytes = new byte[(int) length];

        // 读取数据到byte数组中
        int offset = 0;

        int numRead = 0;

        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // 确保所有数据均被读取
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();

        return bytes;
    }


    /**
     * InputStream转byte[]
     * @param in
     * @return
     * @throws Exception
     */
    public static byte[] getBytesFromInputStream(InputStream in) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count;
        while ((count = in.read(data, 0, 4096)) != -1)
            outStream.write(data, 0, count);
        return outStream.toByteArray();
    }


    /**
     * 把流写入文件中
     * @param file
     * @param ins
     * @return
     * @throws IOException
     */
    public static File getFileFromInputStream(File file, InputStream ins) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return file;
    }


    /**
     * 流转化为字符串
     * @param is
     * @return
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
            Logger.t(TAG).e( ex,ex.getMessage());
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }





    /**
     * 根据地址获取InputStream
     * @param urlStr
     * @throws IOException
     * @return InputStream
     */
    public InputStream getInputStreamByStringURL(String urlStr){
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            inputStream = urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Logger.t(TAG).e("AppFileMgr-->>getInputStreamByStringURL:","根据地址获取InputStream失败！" + e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
            Logger.t(TAG).e("AppFileMgr-->>getInputStreamByStringURL:","根据地址获取InputStream失败！" + e.getMessage());
        }
        return inputStream;
    }




    /**
     * 文件转化为Object
     * @param fileName 文件全部路径
     * @return Object null 读取失败
     * 实现序列化的对象也可以写入本地
     * implements Serializable
     */
    public static Object file2Object(String fileName) {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 把Object输出到文件
     * @param obj 要保存的对象，可以是List<String>这种列表对象
     * @param outputFile 输出文件的绝对路径
     */
    public static void object2File(Object obj, String outputFile) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(outputFile));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }



    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        StringBuilder resultSb = null;
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            Logger.t(TAG).e( e,e.getMessage());
        }
        return inputStream2String(is);
    }

    public static String readFile(File file) {
        return readFile(file.getPath());
    }


    /**
     * 将文本写入到指定目录的文件中，如果文件不存在则自动创建
     * @param file
     * @param content
     * @return
     */
    public static int writeFile(File file, String content) {
        return writeFile(file.getPath(), content,false);
    }


    /**
     * 将流写入到指定目录的文件中，如果文件不存在则自动创建
     * @param file
     * @param stream
     * @return 写入成功返回true
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream,false);
    }



    /**
     * 写文本到指定目录的文件中（不存在自动创建文件）
     * @param path
     * @param content
     * @param append 如果是true,内容拼接到文件后面
     * @return
     */
    public static int writeFile(String path, String content, boolean append) {
        if(TextUtil.isEmpty(content)) return -1;

        FileWriter fileWriter = null;
        try {
            getFileAutoCreated(path);
            fileWriter = new FileWriter(path, append);
            fileWriter.write(content);
            return 0;
        } catch (Exception e) {
            Logger.t(TAG).e( e,e.getMessage());
        } finally {
            closeIO(fileWriter);
        }
        return -1;
    }

    /**
     * write file
     * 将输入流写入指定路径的文件中
     * @param file the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            getFileAutoCreated(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(o,stream);
        }
    }


    /**
     * 获取手机内部可用空间大小
     * @return
     */
    static public long getAvailableInternalMemorySize() {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();//获取当前可用的存储空间
        return availableBlocks * blockSize;
    }
    /**
     * 获取手机内部空间大小
     * @return
     */
    static public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    /**
     * 获取手机外部可用空间大小
     * @return
     */
    static public long getAvailableExternalMemorySize() {
        if (haveSDCard()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }else {
            return -1L;
        }
    }




    /**
     * 获取单个文件的大小or获取目录包括包含的文件的总大小
     * @param directory 文件获取目录
     * @return
     */
    public static long getDirectorySize(File directory){
        if(!directory.exists()) return 0;
        if(directory.isDirectory()){
            long directorySize=0;
            for(File file:directory.listFiles()){
                directorySize+=getDirectorySize(file);
            }
            return directorySize;
        }else{
            return directory.length();
        }
    }


    /**
     * 目录大小格式化(1024 -> 1k)
     * @param directorySize
     * @return
     */
    public static String formatFileSize(long directorySize) {
        return Formatter.formatFileSize(BaseApp.getInstance(),directorySize);
    }



    /**
     * 获取指定路径下的文件列表
     * @param strPath
     * @return
     */
    public static List<String> getFileList(String strPath) {
        List<String> fileList = new ArrayList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null)
            return null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFileList(files[i].getAbsolutePath());
            } else {
                fileList.add(files[i].getPath());
            }
        }
        return fileList;
    }




    /**
     * 更新本地文件
     * @param context
     * @param path
     */
    public static void notifyFileSystemChanged(Context context,String path) {
        if (path == null)
            return;
        final File f = new File(path);
        if (Build.VERSION.SDK_INT >= 19 /*Build.VERSION_CODES.KITKAT*/) { //添加此判断，判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{path};

            if(paths == null)
                 paths = new String[]{Environment.getExternalStorageDirectory().toString()};

            MediaScannerConnection.scanFile(context, paths,  null,null);
        } else {
            final Intent intent;
            if (f.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
//                Log.v(LOG_TAG, "directory changed, send broadcast:" + intent.toString());
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(path)));
                Logger.t(TAG).e("file changed, send uri : " + intent.getData());
                Logger.t(TAG).e("file changed, send broadcast:" + intent.toString());
            }
            context.sendBroadcast(intent);
        }
    }




    /**
     * 扫描目录（扫描后可以及时在图库中看到）
     *
     * @param context
     * @param path
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static void scanFile(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
    }





    /***
     * 单文件剪切/目录文件剪切功能实现
     * 单文件剪切操作（1）：
     * 					  File src = new File("F://work//s2sh.jpg");  剪切文件路径
     * 		   			  File desc = new File("F://AAA//");          存放目录路径
     *  				  falg = CutFile( src, desc, true , true);    返回文件剪切成功与失败状态(测试通过)
     * 单文件剪切操作（2）：
     * 					  File src = new File("F://work//s2sh.jpg");  剪切文件路径
     * 					  File src = new File("F://AAA//s2sh.jpg");   存放后全路径
     * 					  falg = CutFile( src, desc, true , true);    返回文件剪切成功与失败状态(测试通过)
     * 文件目录剪切操作(1):
     * 					  File src = new File("F://testB");    	           源文件所在目录
     *                    File desc = new File("F://AAA//testB");    文件剪切到目录全路径
     *                    falg = CutFile( src, desc, true , true);   返回文件剪切成功与失败状态(测试通过)
     * @param src  源文件夹
     * @param desc 目标文夹
     * @param boolCover 如(源/目)文件目录同名
     * @param boolCut 如是否是剪切操作，
     * @throws Exception 异常处理
     * @return falg = true 文件剪切成功。falg = false 文件剪切失败。
     */
    public static boolean cutFile(File src, File desc, boolean boolCover, boolean boolCut){
        try {
            //1:单文件剪切操作
            if(src.isFile())
            {
                if(!desc.isFile() || boolCover)
                    //创建新文件
                    desc.createNewFile();
                //进行复制操作
                CUT_FLAG = copyFile(src, desc);
                //是否是剪切操作
                if(boolCut){ 	src.delete();	}
            }
            //2：多文件剪切操作
            else if(src.isDirectory()) {
                desc.mkdirs();
                File[] list = src.listFiles();
                //循环向目标目录写如内容
                for(int i = 0; i < list.length; i++){
                    String fileName = list[i].getAbsolutePath().substring(src.getAbsolutePath().length(), list[i].getAbsolutePath().length());
                    File descFile = new File(desc.getAbsolutePath()+ fileName);
                    cutFile(list[i],descFile, boolCover, boolCut);
                }
                //是否是剪切操作
                if(boolCut)	{  	src.delete();	}
            }
        } catch (Exception e) {
            CUT_FLAG = false;
            e.printStackTrace();
            Logger.t(TAG).e("AppFileMgr-->>cutFile:","文件剪切操作出现异常！" + e.getMessage());
        }
        return CUT_FLAG;
    }

    /***
     * 单文件或多文件目录复制操作
     * 单文件复制形式1：
     * 					File src = new File("F://work//s2sh.jpg");  源文件全路径
     *					File desc = new File("F://AAA//");          需要复制文件路径
     *					falg = CopeFile(src, desc);					返回复制成功与失败状态(测试通过)
     * 单文件复制形式2：
     * 					File src = new File("F://work//s2sh.jpg");  源文件全路径
     *					File desc = new File("F://AAA//s2sh.jpg");  需要复制文件路径
     *					falg = CopeFile(src, desc);					返回复制成功与失败状态(测试通过)
     * 目录复制形式1：
     * 					File src = new File("F://test");     		源文件目录路径
     *  				File desc = new File("F://AAA//test");		复制目录下全路径
     *					falg = CopeFile(src, desc);                 返回复制成功与失败状态(测试通过)
     * @param src  源文件的全路径
     * @param desc 复制文件路径
     * @throws Exception 异常处理
     * @return falg = true 复制操作成功。falg = false 复制操作失败。
     */
    public static boolean copyFile(File src, File desc){
        //创建字节流对象(输入,输出)
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        //创建文件输入流,输入流对象
        FileInputStream srcInputStream  = null;
        FileOutputStream descOutputStream= null;
        //记录同文件复制数量操作
        int count = 0;
        //是否存在相同文件
        boolean boolCover = false;
        //单文件复制操作实现
        if(src.isFile()){
            try {
                //获取需要复制下目录列表文件数组
                File[] list = desc.listFiles();
                //获取复制文件名
                String srcname = src.toString().substring(src.toString().lastIndexOf("\\")+1, src.toString().length()).trim();
                if(null != list)
                {
                    if(list.length > 0)
                    {
                        //循环判断复制目录下是否和源文名相同
                        for(int i = 0; i < list.length; i++)
                        {
                            //获取复制目录下文件名
                            String descname = list[i].toString().substring(list[i].toString().lastIndexOf("\\")+1, list[i].toString().length()).trim();
                            //判定复制文件名和目录文件名相同，记录重复数为1
                            if(srcname.equals(descname)){
                                count = count + 1;
                                boolCover = true;
                            }
                            if(descname.indexOf("复件") != -1 && descname.indexOf(srcname.substring(srcname.indexOf(")")+1, srcname.length())) != -1){
                                count = count + 1;
                            }
                        }
                    }
                }
                //存在重复文件信息
                if(boolCover)
                {
                    if(count == 1)
                    {
                        if(desc.toString().indexOf(".") != -1)
                        {
                            //向磁盘中写入： 复件 + 复制文件名称
                            descOutputStream = new FileOutputStream(desc.toString() + "\\复件 " );
                        }else
                        {
                            //向磁盘中写入： 复件 + 复制文件名称
                            descOutputStream = new FileOutputStream(desc.toString() + "\\复件 " + srcname);
                        }
                    }else{
                        if(desc.toString().indexOf(".") != -1)
                        {
                            //向磁盘中写入： 复件(记录数)+ 复制文件名称
                            descOutputStream = new FileOutputStream(desc.toString() + "\\复件 ("+count+") ");
                        }else
                        {
                            //向磁盘中写入： 复件(记录数)+ 复制文件名称
                            descOutputStream = new FileOutputStream(desc.toString() + "\\复件 ("+count+") " + srcname);
                        }
                    }
                }else{
                    if(desc.toString().indexOf(".") != -1)
                    {
                        descOutputStream = new FileOutputStream(desc.toString() + "\\" );
                    }else
                    {
                        descOutputStream = new FileOutputStream(desc.toString() + "\\" + srcname);
                    }
                }
                byte[] buf = new byte[1];
                srcInputStream = new FileInputStream(src);
                bis = new BufferedInputStream(srcInputStream);
                bos = new BufferedOutputStream(descOutputStream);
                while(bis.read(buf) != -1){
                    bos.write(buf);
                    bos.flush();
                }
                COPY_FLAG = true;
            } catch (Exception e) {
                COPY_FLAG = false;
                e.printStackTrace();
                Logger.t(TAG).e("AppFileMgr-->>copyFile:","文件复制操作出现异常！" + e.getMessage());
            }finally{
                try {
                    if(bis != null){
                        bis.close();
                    }
                    if(bos != null){
                        bos.close();
                    }
                } catch (IOException e) {
                    COPY_FLAG = false;
                    e.printStackTrace();
                    Logger.t(TAG).e("AppFileMgr-->>copyFile:", "文件复制操作，数据流关闭出现异常！" + e.getMessage());
                }
            }
        }else if(src.isDirectory()){
            //创建目录
            desc.mkdir();
            File[] list = src.listFiles();
            //循环向目标目录写如内容
            for(int i = 0; i < list.length; i++){
                String fileName = list[i].getAbsolutePath().substring(src.getAbsolutePath().length(), list[i].getAbsolutePath().length());
                File descFile = new File(desc.getAbsolutePath()+ fileName);
                copyFile(list[i], descFile);
            }
        }
        return COPY_FLAG;
    }



    /***
     * 用于对文件或文件夹进行删除操作
     * 1：删除文件     FileHelper.DeleteFile(new File("F:\\AAA\\A.txt"))   测试通过
     * 2：删除目录     FileHelper.DeleteFile(new File("F:\\AAA\\work"))    测试通过
     * @param file  删除文件对象
     * @return  delete_falg为true删除文件/目录成功,为false删除文件/目录失败。
     */
    public static boolean deleteFile(File file){
        try {
            if(file.isFile())
            {
                file.delete();
                DELETE_FLAG  = true;
            }
            else if(file.isDirectory())
            {
                File[] list = file.listFiles();
                for(int i=0;i<list.length;i++){
                    deleteFile(list[i]);
                }
                file.delete();
            }
        } catch (Exception e) {
            DELETE_FLAG = false;
            e.printStackTrace();
            Logger.t(TAG).i("AppFileMgr-->>deleteFile", "文件删除出现异常！" + e.getMessage());
        }
        return DELETE_FLAG;
    }



    /***
     * 用于对文件进行重命名操作
     * 1：重命名：FileHelper.RenameFile(new File("F:\\AAA\\A.txt"),"AA")  测试通过
     * @param file  重命名文件对象
     * @param name  命名文件名称
     * @return  rename_falg为true重命名成功,为false重命名失败。
     */
    public static boolean renameFile(File file, String name){
        String path = file.getParent();
        if(!path.endsWith(File.separator)){
            path += File.separator;
        }
        Logger.t(TAG).i("AppFileMgr-->>renameFile:", "文件重命名操作成功！");
        return file.renameTo(new File(path + name));
    }



    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static String getFileMD5(File file) {
        return MD5Utils.getFileMD5(file);
    }







    /**
     * 获取文件名称
     * @param path
     * @return String
     */
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        Logger.t(TAG).i("AppFileMgr-->>getFileName-->>path:", path);
        return path.substring(index + 1);
    }



    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param file 文件
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(File file) {
        if (file == null) return null;
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * 获取全路径中的不带拓展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带拓展名的文件名
     */
    public static String getFileNameNoExtension(String filePath) {
        if (TextUtil.isEmpty(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }


    /**
     * android用的是linux文件系统ext，ext2，ext3，ext4
     * 也支持fat，fat16，fat32文件系统
     */

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    public static String getFileExtension(File file) {
        if (file == null) return null;
        return getFileExtension(file.getPath());
    }


    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     * 检验文件名是否合法（是否存在特殊字符或者空格）
     * @param file  The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe.  Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }


    /**
     * 检验Ext4文件名的字符
     * @param c
     * @return
     */
    private static boolean isValidExtFilenameChar(char c) {
        switch (c) {
            case '\0':
            case '/':
                return false;
            default:
                return true;
        }
    }

    /**
     * Check if given filename is valid for an ext4 filesystem.
     * 检测文件名是否符合Ext4文件系统格式
     */
    public static boolean isValidExtFilename(String name) {
        return (name != null) && name.equals(buildValidExtFilename(name));
    }

    /**
     * Mutate the given filename to make it valid for an ext4 filesystem,
     * replacing any invalid characters with "_".
     * 把文件名转化成符合Ext4文件系统格式,特殊字符用下划线代替
     */
    public static String buildValidExtFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidExtFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        trimFilename(res, 255);
        return res.toString();
    }


    /**
     * 检验FAT文件名的字符
     * @param c
     * @return
     */
    private static boolean isValidFatFilenameChar(char c) {
        if ((0x00 <= c && c <= 0x1f)) {
            return false;
        }
        switch (c) {
            case '"':
            case '*':
            case '/':
            case ':':
            case '<':
            case '>':
            case '?':
            case '\\':
            case '|':
            case 0x7F:
                return false;
            default:
                return true;
        }
    }

    /**
     * Check if given filename is valid for a FAT filesystem.
     * 检测文件名是否符合FAT文件系统格式
     */
    public static boolean isValidFatFilename(String name) {
        return (name != null) && name.equals(buildValidFatFilename(name));
    }

    /**
     * Mutate the given filename to make it valid for a FAT filesystem,
     * replacing any invalid characters with "_".
     * 把文件名转化成符合FAT文件系统格式,特殊字符用下划线代替
     */
    public static String buildValidFatFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidFatFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        // Even though vfat allows 255 UCS-2 chars, we might eventually write to
        // ext4 through a FUSE layer, so use that limit.
        trimFilename(res, 255);
        return res.toString();
    }


    /**
     * 修正文件名
     * @param res
     * @param maxBytes
     */
    private static void trimFilename(StringBuilder res, int maxBytes) {
        byte[] raw;
            try {
                raw = res.toString().getBytes("UTF-8");
                if (raw.length > maxBytes) {
                    maxBytes -= 3;
                    while (raw.length > maxBytes) {
                        res.deleteCharAt(res.length() / 2);
                        raw = res.toString().getBytes("UTF-8");
                    }
                    res.insert(res.length() / 2, "...");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

    }




    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    public static String getFileExtension(String filePath) {
        if (TextUtil.isEmpty(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }


    /**
     * 获取文件类型
     * @param uri
     * @return
     */
    public static String getMimeType(String uri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }



    /**
     * 简单获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码
     */
    public static String getFileCharsetSimple(File file) {
        int p = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            p = (is.read() << 8) + is.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is);
        }
        switch (p) {
            case 0xefbb:
                return "UTF-8";
            case 0xfffe:
                return "Unicode";
            case 0xfeff:
                return "UTF-16BE";
            default:
                return "GBK";
        }
    }



    /**
     * 从assets中读取文本
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            Logger.t(TAG).e( e,e.getMessage());
        }
        return inputStream2String(is);

    }




    /**
     * 清空某个目录下的所有文件和文件夹
     * @param directory
     */
    public static void clearDirectory(File directory){
        if(directory.exists()&&directory.isDirectory()){
            for(File file:directory.listFiles()){
                if(file.exists()&&file.isFile()) {
                    file.delete();
                }else if(file.exists()&&file.isDirectory()){
                    clearDirectory(file);
                    file.delete();
                }
            }
        }
    }



    /**
     * recyle bitmaps
     *
     * @param bitmaps
     */
    public static void recycleBitmap(Bitmap... bitmaps) {
        if (TextUtil.isEmpty(bitmaps)) {
            return;
        }

        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
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
                Logger.t(TAG).e( "close IO ERROR...", e);
            }
        }
    }


}