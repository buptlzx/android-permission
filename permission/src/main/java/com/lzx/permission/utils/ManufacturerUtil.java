package com.lzx.permission.utils;

import android.os.Build;
import android.text.TextUtils;

/**
 * Created by Lzx on 2017/10/27.
 */

public class ManufacturerUtil {

    private static boolean isMeizu() {
        String manufacture = Build.MANUFACTURER;

        if (TextUtils.isEmpty(manufacture)) {
            return false;
        }

        if (TextUtils.equals("MEIZU", manufacture.trim().toUpperCase())) {
            return true;
        }
        return false;
    }


    public static boolean needCheckByChecker() {
        if (isMeizu()) {
            return true;
        }

        return false;
    }
}
