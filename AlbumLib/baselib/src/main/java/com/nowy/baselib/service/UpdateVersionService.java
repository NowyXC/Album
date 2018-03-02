package com.nowy.baselib.service;

/**
 * 启动下载的后台服务，通过广播接收下载完成的通知
 */

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.nowy.baselib.R;
import com.nowy.baselib.app.BaseApp;
import com.nowy.baselib.utils.AppUtil;
import com.nowy.baselib.utils.ShellUtil;
import com.nowy.baselib.utils.TextUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

public class UpdateVersionService extends Service {
    public static final String TAG = UpdateVersionService.class.getSimpleName();
    public static final String BUNDLE_URL = "url";
    public static final String BUNDLE_DESC = "desc";
    public static final String BUNDLE_FILENAME = "fileName";
    public static final String BUNDLE_AUTHORITY = "authority";
    public static final String DOWNLOAD_APK_NAME = "mdd.apk";
    private LongSparseArray<String> mApkPaths;
    public static final int NOTIFICATION_DOWNLOAD_ID = 1453;
    private String url;
    /** 安卓系统下载类 **/
    DownloadManager manager;
    /** 接收下载完的广播 **/
    DownloadCompleteReceiver receiver;
    private String fileName;
    private String desc;
    private String authority;


    public static void start(Context context,String downUrl,String authority) {
        String appName = context.getString(R.string.app_name);
        String desc = appName + "更新";
        start(context,downUrl,authority,desc,DOWNLOAD_APK_NAME);
    }


    /**
     * 开启下载服务，自定义更新的通知栏的app名称,和本地保存的文件名
     * @param context
     * @param downUrl 下载链接
     * @param authority 本地保存的FileProvider，7.0需要配置
     * @param desc 通知栏下载时显示的提示
     * @param fileName 本地保存的文件名(带后缀eg: simple.apk)
     */
    public static void start(Context context,String downUrl,
                             String authority,
                             String desc,String fileName) {
        Intent starter = new Intent(context, UpdateVersionService.class);
        starter.putExtra(BUNDLE_URL,downUrl);
        starter.putExtra(BUNDLE_AUTHORITY,authority);
        starter.putExtra(BUNDLE_DESC,desc);
        starter.putExtra(BUNDLE_FILENAME,fileName);
        context.startService(starter);
    }



    /** 初始化下载器 **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(url));
        Logger.t(TAG).e("转换过的："+Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);

//        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_APK_NAME);
        // 设置下载路径和文件名
        //这是DownloadManager的限制
        if(TextUtil.isEmpty(fileName)){
            fileName = DOWNLOAD_APK_NAME;
        }
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        down.setDestinationUri(Uri.fromFile(file));

        if(TextUtils.isEmpty(desc)){
            String appName = getBaseContext().getString(R.string.app_name);
            desc = appName + "更新";
        }

        down.setDescription(desc);
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        // 将下载请求放入队列
        long downloadId = manager.enqueue(down);
        mApkPaths.put(downloadId,file.getAbsolutePath());
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApkPaths = new LongSparseArray<>();
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(BUNDLE_URL);
        fileName = intent.getStringExtra(BUNDLE_FILENAME);
        desc = intent.getStringExtra(BUNDLE_DESC);
        authority = intent.getStringExtra(BUNDLE_AUTHORITY);
        Logger.t(TAG).e("未转换:"+url);
        if(!TextUtils.isEmpty(url)){
            // 调用下载
            initDownManager();
        }
        //意外杀死后，不在重启服务
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //停止服务并关闭广播
                UpdateVersionService.this.stopSelf();

//                AppUtil.install(context,manager.getUriForDownloadedFile(downId));

//                //自动安装apk
//                installAPK(context,manager.getUriForDownloadedFile(downId));


                String apkPath = mApkPaths.get(downId);


                if (!apkPath.isEmpty()){
                    ShellUtil.setPermission(apkPath);//提升读写权限,否则可能出现解析异常
                    AppUtil.install(context,authority,apkPath,ShellUtil.checkRootPermission());
                }else {
                    Logger.t(TAG).e("apkPath is null");
                }

                BaseApp.exit();
//                install(context,(File)msg.obj);

            }
        }


//
//        /**
//         * 安装apk文件
//         */
//        private void installAPK(Context context, Uri apk) {
//            if(apk!=null){
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);//动作
//                intent.addCategory(Intent.CATEGORY_DEFAULT);//类型
//                intent.setDataAndType(apk, "application/vnd.android.package-archive");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                context.startActivity(intent);
//            }
//        }
//    }
//    /**
//     * 通过隐式意图调用系统安装程序安装APK
//     */
//    public static void install(Context context,File apkfile) {
//        File file = apkfile;
////                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
////                , "myApp.apk");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//
//            Uri apkUri = FileProvider.getUriForFile(context, com.mdd.baselib.BuildConfig.APPLICATION_ID+".FileProvider", file);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        }else{
//            intent.setDataAndType(Uri.fromFile(file),
//                    "application/vnd.android.package-archive");
//        }
//        context.startActivity(intent);
    }
}
