package com.nowy.baselib.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;

import com.nowy.baselib.app.BaseApp;
import com.nowy.baselib.utils.permission.XPermissionUtils;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Nowy on 2017/12/20.
 * 方法归纳：
 * # 判断当前应用程序处于前台还是后台（需要权限）
 * # 获取当前应用程序的版本号
 * # 获取当前应用程序的版本名
 * # 是否第一次运行app
 * # 回到home界面
 * # 获取应用第一次安装日期
 * # 获取应用更新日期
 * # 获取应用大小
 * # 获取应用apk文件地址
 * # 获取应用的安装市场
 * # 获取应用签名
 * # 获取应用targetSdk
 * # 获取应用uid
 * # 是否是系统应用
 * # 服务是否在运行
 * # 停止服务
 * # 结束进程
 * # 运行脚本
 * # 获得root权限
 * # 获取状态栏高度＋标题栏高度
 * # 获取状态栏高度
 * # 设置根布局参数,是否fitsSystemWindows
 * # 获取cache目录的大小(默认除以1024)
 * # 清除缓存
 * # 应用是否安装
 * # 通过隐式意图调用系统安装程序安装APK(注意7.0的兼容问题)
 * # 卸载应用
 * # 复制文本到手机中
 * # 创建一个唯一的快捷方式（需要权限）
 * # 删除快捷方式
 * # dp <=> px 和sp<=> px
 */

public class AppUtil {
    private static final String TAG = AppUtil.class.getSimpleName();
    private static final String SP_FIRST_LOG = "sp_first";//记录是否首次启动



    /**
     * 判断当前应用程序处于前台还是后台
     * 需要添加权限: <uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean isApplicationBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                Logger.t(TAG).d("isBackground: " + true);
                return true;
            }
        }
        Logger.t(TAG).d("isBackground: " + false);
        return false;
    }



    /**
     * 获取当前应用程序的版本号
     *
     * @return
     * @author wangjie
     */
    public static int getAppVersionCode() {
        int version = 1;
        try {
            Context context = BaseApp.getInstance();
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            Logger.t(TAG).i(TAG, "该应用的版本号: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.t(TAG).i(TAG, "getAppVersion", e);
        }

        return version;
    }


    /**
     * 获取应用图标
     * @param context
     * @param packageName
     * @return
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Drawable appIcon = null;
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            appIcon = applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }





    /**
     * 获取当前应用程序的版本名
     *
     * @return
     * @author wangjie
     */
    public static String getAppVersionName() {
        String versionName = "1.0";
        try {
            Context context = BaseApp.getInstance();
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.t(TAG).e(TAG, "getAppVersion", e);
        }

        return versionName;
    }


    /**
     * @return boolean 返回类型
     * @throws
     * @Title: isFirstRun
     * @说 明:判断程序是否第一次运行
     * @参 数: @param context
     * @参 数: @return
     */
    public static boolean isFirstRun(Context context) {
        boolean isFirstRun = false;
        SharedPreferences sp = context.getSharedPreferences(SP_FIRST_LOG, Context.MODE_PRIVATE);

        int versionCode = sp.getInt("versionCode", 0);
        int newAppVersion = getAppVersionCode();
        if (versionCode != newAppVersion) {
            sp.edit().putInt("version", newAppVersion).apply();
            isFirstRun = true;
        }
        return isFirstRun;
    }


    /**
     * 回到home，后台运行
     * @param context
     */
    public static void goHome(Context context) {
        Logger.t(TAG).d("返回键回到HOME，程序后台运行...");
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }



    /**
     * 获取应用第一次安装日期
     * @param context
     * @param packageName
     * @return
     */
    public static long getAppFirstInstallTime(Context context, String packageName) {
        long lastUpdateTime = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            lastUpdateTime = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return lastUpdateTime;
    }

    /**
     * 获取应用更新日期
     * @param context
     * @param packageName
     * @return
     */
    public static long getAppLastUpdateTime(Context context, String packageName) {
        long lastUpdateTime = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            lastUpdateTime = packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return lastUpdateTime;
    }



    /**
     * 获取应用大小
     * @param context
     * @param packageName
     * @return
     */
    public static long getAppSize(Context context, String packageName) {
        long appSize = 0;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            appSize = new File(applicationInfo.sourceDir).length();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appSize;
    }



    /**
     * 获取应用apk文件地址
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppApk(Context context, String packageName) {
        String sourceDir = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            sourceDir = applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sourceDir;
    }

    /**
     * 获取应用的安装市场
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppInstaller(Context context, String packageName) {
        return context.getPackageManager().getInstallerPackageName(packageName);
    }


    /**
     * 获取应用签名
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppSign(Context context, String packageName) {
        try {
            PackageInfo pis = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return hexdigest(pis.signatures[0].toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(AppUtil.class.getName() + "the " + packageName + "'s application not found");
        }
    }


    public static String hexdigest(byte[] paramArrayOfByte) {
        final char[] hexDigits = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            for (int i = 0, j = 0; ; i++, j++) {
                if (i >= 16) {
                    return new String(arrayOfChar);
                }
                int k = arrayOfByte[i];
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                arrayOfChar[++j] = hexDigits[(k & 0xF)];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 获取应用targetSdk
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppTargetSdkVersion(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取应用uid
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppUid(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }



    /**
     * 获取状态栏高度＋标题栏高度以外部分高度
     *
     * @param activity
     * @return
     */
    public static int getContentViewHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        return outRect.height();
    }



    /**
     * 是否是系统应用
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isSystemApp(Context context, String packageName) {
        boolean isSys = false;
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            if (applicationInfo != null && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                isSys = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            isSys = false;
        }
        return isSys;
    }


    /**
     * 服务是否在运行
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止服务
     * @param context
     * @param className
     * @return
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * 结束进程
     * @param context
     * @param pid
     * @param processName
     */
    @SuppressLint("MissingPermission")
    public static void killProcesses(Context context, int pid, String processName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName;
        if(XPermissionUtils.hasPermission(context, Manifest.permission.KILL_BACKGROUND_PROCESSES)){
            try {
                if (!processName.contains(":")) {
                    packageName = processName;
                } else {
                    packageName = processName.split(":")[0];
                }
                activityManager.killBackgroundProcesses(packageName);
                Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(activityManager, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    /**
     * 运行脚本
     * @param script
     * @return
     */
    public static String runScript(String script) {
        String sRet;
        try {
            final Process m_process = Runtime.getRuntime().exec(script);
            final StringBuilder sbread = new StringBuilder();
            Thread tout = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(m_process.getInputStream()),
                            8192);
                    String ls_1;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sbread.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tout.start();

            final StringBuilder sberr = new StringBuilder();
            Thread terr = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(m_process.getErrorStream()),
                            8192);
                    String ls_1;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sberr.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            terr.start();

            m_process.waitFor();
            while (tout.isAlive()) {
                Thread.sleep(50);
            }
            if (terr.isAlive())
                terr.interrupt();
            String stdout = sbread.toString();
            String stderr = sberr.toString();
            sRet = stdout + stderr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sRet;
    }


    /**
     * 获得root权限
     * @param context
     * @return
     */
    public static boolean getRootPermission(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + packageCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取状态栏高度＋标题栏高度
     *
     * @param activity
     * @return
     */
    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return x;
    }


    /**
     * 设置根布局参数 是否需要fitsSystemWindows
     */
    public static void setRootView(Activity activity, boolean fitsSystemWindows) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(fitsSystemWindows);
        rootView.setClipToPadding(fitsSystemWindows);
    }


    /**
     * 获取cache目录的大小
     * @param context
     * @return
     */
    public static String getCacheSize(Context context) {
        long directorySize = FileUtil.getDirectorySize(context.getCacheDir());
        Logger.t(TAG).e( "cacheSize=" + directorySize);
        double kiloByte = directorySize / 1024.;
        if (kiloByte < 1) {
            return "没有缓存";
        }
        return FileUtil.formatFileSize(directorySize);
    }

    /**
     * 清除缓存
     * @param context
     */
    public static void clearCache(Context context) {
        FileUtil.clearDirectory(context.getCacheDir());
    }


    /**
     * 应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        boolean installed = false;
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        List<ApplicationInfo> installedApplications = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo in : installedApplications) {
            if (packageName.equals(in.packageName)) {
                installed = true;
                break;
            } else {
                installed = false;
            }
        }
        return installed;
    }


    /**
     * 安装apk
     * @param context
     * @param authority FileProvider的全称。Android 7.0需要的URI权限请求问题
     *  BuildConfig.APPLICATION_ID+".FileProvider"
     * @param apkPath
     * @param isRoot 是否root
     */
    public static void install(Context context,String authority,String apkPath,boolean isRoot){
        if(isRoot){
            installRoot(context,authority,apkPath);
        }else{
            install(context,authority,apkPath);
        }
    }




    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public static void install(Context context,String authority,String apkPath) {
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, authority, file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    //通过Root方式安装
    private static void installRoot(final Context context, final String authority, final String apkPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShellUtil.CommandResult result = ShellUtil.execCommand("pm install -r "+apkPath,true);
                if (result.result == 0) {
                    T.s("安装成功");
                } else {
                    install(context,authority,apkPath);
                }
            }
        }).start();
    }



    /**
     * 卸载应用
     * @param context
     * @param packageName
     * @return
     */
    public static boolean uninstallApk(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }


    /**
     * 复制文本到手机中
     *
     * @param context
     * @param content
     */
    public static void copyToSystem(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(content);
    }



    /**
     * 创建一个唯一的快捷方式
     * <p>
     * <-创建快捷方式权限
     * <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
     * <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
     *
     * @param context
     * @param appNameId
     * @param ic_launcherId
     * @param launcherActivity
     */
    public static void createOnlyShortcut(Context context, int appNameId, int ic_launcherId, Class launcherActivity) {
        SharedPrefsUtil .init(context, "shortcut", Context.MODE_PRIVATE);
        SharedPrefsUtil  abPrefsUtil = SharedPrefsUtil .getPrefsUtil("shortcut");
        if (abPrefsUtil.getBoolean("isFirst", true)) { //是第一次启动应用
            deleteShortCut(context, appNameId); //防止应用被清空数据的判断标识位为true，可桌面已有图标的情况
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appNameId));
            shortcut.putExtra("duplicate", false);//设置是否重复创建
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(context, launcherActivity);//设置第一个页面
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, ic_launcherId);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            context.sendBroadcast(shortcut);
            abPrefsUtil.putBoolean("isFirst", false).commit();
        }
    }

    /**
     * 删除快捷方式
     */
    public static void deleteShortCut(Context activity, int appNameId) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(appNameId));
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        activity.sendBroadcast(shortcut);
    }



    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = BaseApp.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
