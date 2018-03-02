package com.nowy.albumlib.view.recyclerView.divider;

import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.nowy.baselib.utils.AppUtil;

/**
 * Created by Nowy on 2018/2/28.
 */

public class DividerUtil {
    @NonNull
    public static Divider getDivider(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int size = AppUtil.dip2px(6);
            return new Api21ItemDivider(color, size, size);
        } else {
            int size = AppUtil.dip2px(2);
            return new Api20ItemDivider(color, size, size);
        }
    }



}
