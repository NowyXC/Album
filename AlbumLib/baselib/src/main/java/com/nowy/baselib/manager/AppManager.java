package com.nowy.baselib.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 主要用于生命周期的管理
 */
public class AppManager {

    private final static String TAG = AppManager.class.getSimpleName();
    /**
     * 栈顶
     */
    public static final int STATUS_TASK_TOP = 1;
    /**
     * 栈中
     */
    public static final int STATUS_IN_TASK = 2;
    /**
     * 栈内不存在
     */
    public static final int STATUS_NO_IN_TASK = 3;

    private static  List<Activity> activityStack = Collections.synchronizedList(new LinkedList<Activity>());
    private static AppManager instance = new AppManager();

    private AppManager() {
    }

    public static List<Activity> getActivityStack() {
        return activityStack;
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = null;
        try {
            Logger.t(TAG).i("当前Activity总数量=" + activityStack.size());
            if (activityStack.size() > 0) {
                activity = activityStack.get(activityStack.size() - 1);
                Logger.t(TAG).i("当前的acitivity=" + activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.size() > 0) {
            Activity activity = activityStack.get(activityStack.size() - 1);
            if (activity != null) {
                removeActivity(activity);
                activity.finish();
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            removeActivity(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    public int size(){
        return activityStack == null ? 0 : activityStack.size();
    }

    public void finishActivityForClearTop(Class<?> cls) {

        boolean isOpt = false;

        for (Activity aty : activityStack) {
            if (aty.getClass().equals(cls)) {
                isOpt = true;
            }
        }

        if (isOpt) {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                Activity activity = activityStack.get(i);
                if (!activity.getClass().equals(cls)) {
                    finishActivity(activity);
                } else {
                    finishActivity(activity);
                    return;
                }
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);

            finishActivity(activity);
        }
    }

    /**
     * @Title: finishOtherActivity
     * @说 明:结束除传如的Activity外其他的Activity
     * @参 数: @param mActivity 不结束的activity
     */
    public void finishOtherActivity(Activity mActivity) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (!activityStack.get(i).getClass().equals(mActivity.getClass())) {
                finishActivity(activityStack.get(i));
            }
        }
    }

    /**
     * 结束除指定类名的Activity
     */
    public void finishOtherActivity(Class<?> cls) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (!activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 从列表中移除插件activity，并没有finish
     *
     * @param pluginActivity
     */
    public void removeActivity(Activity pluginActivity) {
        boolean isRemove = activityStack.remove(pluginActivity);
        Logger.t(TAG).e("removeActivity:" + pluginActivity + " --> " + isRemove);
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * 获取App当前在activity栈的位置状态
     *
     * @param context
     * @param pageName
     * @return
     */
    public static int getAppStatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        //判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName) || list.get(0).baseActivity.getPackageName().equals(pageName)) {
            return STATUS_TASK_TOP;
        } else {
            //判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName) || info.baseActivity.getPackageName().equals(pageName)) {
                    return STATUS_IN_TASK;
                }
            }
            return STATUS_NO_IN_TASK;//栈里找不到，返回3
        }
    }


    /**
     * 获得栈顶activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        if (runningTaskInfo != null)
            return runningTaskInfo.get(0).topActivity.getClassName();
        else
            return "";
    }


    @Override
    public String toString() {
        return "AppManager [activityStack.size()=" + activityStack.size();
    }

}