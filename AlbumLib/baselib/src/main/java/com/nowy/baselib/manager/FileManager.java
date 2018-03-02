package com.nowy.baselib.manager;

import android.content.Context;
import android.os.Build;

import com.nowy.baselib.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * Created by Nowy on 2017/12/25.
 * 文件系统管理
 * 主要用于记录各个目录的地址和管理
 */

public class FileManager {
    public static final String TAG = FileManager.class.getSimpleName();
    public static FileManager mInstance;
    private Context mContext;
//
//    public File mImgCachePath;       //图片缓存目录（默认缓存APP需要的图片）
//    public File mImgSavePath;        //图片保存目录(用户保存的图片,eg:拍照)
//    public File mImgSharePath;       //分享图片保存目录(分享图片的临时保存路径)
//    public File mApkSavePath;        //APK保存目录(下载APK保存目录)
//    public File mFileSavePath;       //文件保存目录(其他文件的保存)
//    public File mLogSavePath;        //日志保存目录(日志保存目录)
//    public File mImgCapTempPath;     //截图保存目录(截图文件保存目录，包括网页快照)
//    public File mImgCachePathDef;    //图片默认缓存路径(第三方图片加载框架缓存目录)
//    public File mTmpFile ;           //文件临时缓存路径（压缩上传时候使用）

    public static String APP_ROOT_PATH ;
    public static String IMG_CACHE_PATH ;
    public static String TMP_PATH ;
    private static String APP_CACHE_PATH;
    private static String APP_CACHE_LOG;
    private static String APP_CACHE_IMAGE;
    private static String APP_CACHE_IMAGE_TMP;
    private static String APP_CACHE_IMAGE_CUT;
    private static String APP_CACHE_TMP;
    private static String APP_FILE_PATH;
    private static String APP_FILE_APK;
    private static String APP_FILE_IMAGE;
    private static String APP_FILE_DB;
    private static String PUBLIC_DCIM;
    private static String PUBLIC_PICTURES;
    private static String PUBLIC_DOC;
    private static String PUBLIC_DOWNLOAD;

    //APP的内部存储路径
    private File mCacheDir;

    private File mLogDir;
    private File mImagesCacheDir;
    private File mImagesTmpDir;
    private File mImagesCutDir;
    private File mTmpDir;

    //APP在SDCard的外部项目存储路径(需要SD卡权限)
    private File mFilesDir;

    private File mApkDir;
    private File mImagesDir;
    private File mDBDir;

    //公共资源路径
    private File mDCIMDir;
    private File mPicturesDir;
    private File mDocDir;
    private File mDownloadsDir;


    private FileManager(Context context) {
        this.mContext = context;
    }

    public static synchronized FileManager getInstance(Context context){
        if(mInstance == null){
            synchronized (FileManager.class){
                if(mInstance == null){
                    mInstance = new FileManager(context);
                    mInstance.init(context);
                }
            }
        }
        return mInstance;
    }


    private void init(Context context){
        APP_ROOT_PATH = FileUtil.getAppPath(context);


        /**
         * 缓存目录 /cache底下
         * 一般存放可以清理的缓存数据，包括日志文件，压缩后的图片资源
         **/
        String cacheDirPath = FileUtil.getDiskCacheDir(context); //当前使用的缓存目录，如果SDCard存在，优先使用SDCard路径
        mCacheDir = new File(cacheDirPath);
        if(!mCacheDir.exists()){
            mCacheDir.mkdirs();
        }
        if(mCacheDir.exists())
            APP_CACHE_PATH = cacheDirPath;


        //日志
        mLogDir = FileUtil.getDirAutoCreated(cacheDirPath,"log/");
        if(mLogDir.exists())
            APP_CACHE_LOG = mLogDir.getPath();

        //图片保存路径
        mImagesCacheDir = FileUtil.getDirAutoCreated(cacheDirPath,"images/");
        if(mImagesCacheDir.exists())
            APP_CACHE_IMAGE = mImagesCacheDir.getPath();


        //图片压缩缓存路径
        mImagesTmpDir = FileUtil.getDirAutoCreated(mImagesCacheDir.getPath(),"capture_temp/");
        if(mImagesTmpDir.exists())
            APP_CACHE_IMAGE_TMP = mImagesTmpDir.getPath();


        //图片裁剪缓存路径
        mImagesCutDir = FileUtil.getDirAutoCreated(mImagesCacheDir.getPath(),"capture_cut/");
        if(mImagesCutDir.exists())
            APP_CACHE_IMAGE_CUT = mImagesCutDir.getPath();


        //缓存一些需要暂时存放在本地的数据文件
        mTmpDir = FileUtil.getDirAutoCreated(cacheDirPath,"tmp/");
        if(mTmpDir.exists())
            APP_CACHE_TMP = mTmpDir.getPath();


        /**
         * 文件缓存目录 /files
         * 一般存放一些文件缓存，例如：拍照图片、下载的apk等
         **/
        String filesDirPath = FileUtil.getDiskFilesDir(context,null);
        mFilesDir = new File(filesDirPath);
        if(!mFilesDir.exists()){
            mFilesDir.mkdirs();
        }
        if(mFilesDir.exists())
            APP_FILE_PATH = filesDirPath;

        //apk缓存路径
        mApkDir = FileUtil.getDirAutoCreated(filesDirPath,"apk/");
        if(mApkDir.exists())
            APP_FILE_APK = mApkDir.getPath();


        //图片保存路径(应用图片)
        mImagesDir = FileUtil.getDirAutoCreated(filesDirPath,"images/");
        if(mImagesDir.exists())
            APP_FILE_IMAGE = mImagesDir.getPath();

        //数据库保存路径
        mDBDir = FileUtil.getDirAutoCreated(filesDirPath,"db/");
        if(mDBDir.exists())
            APP_FILE_DB = mDBDir.getPath();


        /**
         * 公共文件目录
         * 只要保存拍照，文档等可以共享的资源
         **/

        /**
         *  //相机拍摄照片和视频的标准目录
         */
        mDCIMDir = FileUtil.getStorageDCIMDir();

        if(mDCIMDir.exists())
            PUBLIC_DCIM = mDCIMDir.getPath();


        /**
         * 公共图片路径
         */
        mPicturesDir = FileUtil.getStoragePicturesDir();
        if(mPicturesDir.exists())
            PUBLIC_PICTURES = mPicturesDir.getPath();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /**
             * 文档目录
             */
            mDocDir = FileUtil.getStorageDocDir();
            if(mDocDir.exists()){
                PUBLIC_DOC = mDocDir.getPath();
            }
        }

        /**
         * 公共下载路径
         */
        mDownloadsDir = FileUtil.getStorageDownloadsDir();
        if(mDownloadsDir.exists())
            PUBLIC_DOWNLOAD = mDownloadsDir.getPath();

    }


    /**
     * 获取应用缓存的根目录
     * @return
     */
    public File getCacheDir() {
        return mCacheDir;
    }

    /**
     * 应用缓存目录底下 /cache底下
     */

    /**
     * 获取应用缓存的日志目录
     * @return
     */
    public File getLogDir() {
        return mLogDir;
    }

    /**
     * 获取应用缓存的图片缓存目录
     * @return
     */
    public File getImagesCacheDir() {
        return mImagesCacheDir;
    }

    /**
     * 获取应用缓存的图片临时目录
     * @return
     */
    public File getImagesTmpDir() {
        return mImagesTmpDir;
    }

    /**
     * 获取应用缓存的图片裁剪目录
     * @return
     */
    public File getImagesCutDir() {
        return mImagesCutDir;
    }


    /**
     * 获取应用缓存cache的临时目录
     * @return
     */
    public File getTmpDir() {
        return mTmpDir;
    }


    /**
     * 应用file目录底下 /files底下
     */

    /**
     * 获取应用外部存储根目录
     * @return
     */
    public File getFilesDir() {
        return mFilesDir;
    }


    /**
     * 获取应用外部存储apk目录
     * @return
     */
    public File getApkDir() {
        return mApkDir;
    }


    /**
     * 获取应用外部存储图片目录
     * @return
     */
    public File getImagesDir() {
        return mImagesDir;
    }

    /**
     * 获取应用DB保存目录
     * @return
     */
    public File getDBDir() {
        return mDBDir;
    }


    /**
     * 相机拍摄照片和视频的标准目录
     *
     * 拍照后的图片和视频都放在这目录
     * @return
     */
    public File getDCIMDir() {
        return mDCIMDir;
    }


    /**
     * 获取公共图片资源目录
     * @return
     */
    public File getPicturesDir() {
        return mPicturesDir;
    }


    /**
     * API大于等于19才有目录
     * @return
     */
    public File getDocDir() {
        return mDocDir;
    }


    /**
     * 获取公共下载目录
     * @return
     */
    public File getDownloadsDir() {
        return mDownloadsDir;
    }


    public static String getAppRootPath() {
        return APP_ROOT_PATH;
    }

    /**
     * 缓存目录根目录,底下保存各种缓存文件，
     * 清除后不影响主要业务
     * @return
     */
    public static String getCachePath() {
        return APP_CACHE_PATH;
    }

    /**
     * 缓存目录下的日志保存目录
     * @return
     */
    public static String getCacheLogPath() {
        return APP_CACHE_LOG;
    }


    /**
     * 图片缓存根目录，存放各种图片缓存
     * @return
     */
    public static String getCacheImagePath() {
        return APP_CACHE_IMAGE;
    }

    /**
     * 对原图操作后的缓存临时图存放目录，例如：压缩后的待上传图片
     * @return
     */
    public static String getCacheImageTmpPath() {
        return APP_CACHE_IMAGE_TMP;
    }

    /**
     * 照片裁剪的临时缓存文件夹
     * @return
     */
    public static String getCacheImageCutPath() {
        return APP_CACHE_IMAGE_CUT;
    }

    /**
     * 临时文件缓存目录(例如：运行中的临时缓存文件.临时json.tmp之类的)
     * @return
     */
    public static String getCacheTmpPath() {
        return APP_CACHE_TMP;
    }

    /**
     * APP数据目录files
     *
     * @return
     */
    public static String getFilePath() {
        return APP_FILE_PATH;
    }

    /**
     * 保存一些apk文件，可以是更新包，附带APK或者插件包
     * @return
     */
    public static String getFileApkPath() {
        return APP_FILE_APK;
    }


    /**
     * APP内部的图片保存目录，主要保存一些长期图片
     * @return
     */
    public static String getFileImagePath() {
        return APP_FILE_IMAGE;
    }


    /**
     * 保存一些DB数据库文件,此处保存的DB文件是指额外的导入的DB文件
     * eg:assets目录导出的db文件
     * 应用自动创建的还是保存在默认的/data/data/.../databases目录下
     * @return
     */
    public static String getFileDbPath() {
        return APP_FILE_DB;
    }


    /**
     * 相机拍摄照片和视频的标准目录
     *
     * 拍照后的图片和视频都放在这目录
     * @return
     */
    public static String getPublicDcimPath() {
        return PUBLIC_DCIM;
    }

    /**
     * 这个目录在部分手机可能为空，例如：小米m5
     * @return
     */
    public static String getPublicPicturesPath() {
        return PUBLIC_PICTURES;
    }

    /**
     * 这个目录在部分手机可能为空，例如：小米m5,荣耀-al00
     * @return
     */
    public static String getPublicDocPath() {
        return PUBLIC_DOC;
    }

    public static String getPublicDownloadPath() {
        return PUBLIC_DOWNLOAD;
    }




    public void printPath(){
        StringBuffer sb = new StringBuffer("[FileManager] 路径打印：\n");
        sb.append("AppRootPath: ").append(getAppRootPath())
          .append("\nCachePath: ").append(getCachePath())
          .append("\nCacheLogPath: ").append(getCacheLogPath())
          .append("\nCacheImagePath: ").append(getCacheImagePath())
          .append("\nCacheImageTmpPath: ").append(getCacheImageTmpPath())
          .append("\nCacheImageCutPath: ").append(getCacheImageCutPath())
          .append("\nCacheTmpPath: ").append(getCacheTmpPath())


          .append("\nFilePath: ").append(getFilePath())
          .append("\nFileApkPath: ").append(getFileApkPath())
          .append("\nFileImagePath: ").append(getFileImagePath())
          .append("\nFileDbPath: ").append(getFileDbPath())


          .append("\nPublicDcimPath: ").append(getPublicDcimPath())
          .append("\nPublicPicturesPath: ").append(getPublicPicturesPath())
          .append("\nPublicDoc: ").append(getPublicDocPath())
          .append("\nPublicDownloadPath: ").append(getPublicDownloadPath());


        Logger.t(TAG).i(sb.toString());
    }
}
