package com.nowy.baselib.utils;

import android.content.Context;
import android.os.PowerManager;

import com.orhanobut.logger.Logger;

/**
 * 唤醒和释放手机
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:tiantian.china.2@gmail.com
 * Date: 13-7-31
 * Time: 上午10:24
 */
public abstract class WakeLockerUtil {
    private static final String TAG = WakeLockerUtil.class.getSimpleName();
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
//                PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                PowerManager.ON_AFTER_RELEASE, "TAG");
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wakeLock.acquire();
    }

    public static void release() {

        synchronized (WakeLockerUtil.class) {
            // sanity check for null as this is a public method
            if (wakeLock != null) {
                try {
                    wakeLock.release();
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                    Logger.t(TAG).e( "ignoring this exception, probably wakeLock was already released, ", th);
                }
            }
            wakeLock = null;
        }

    }
}
