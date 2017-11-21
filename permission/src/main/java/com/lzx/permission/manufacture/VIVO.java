package com.lzx.permission.manufacture;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lzx on 2017/11/3.
 */

public class VIVO implements IManufacturer {

    private final String MAIN_CLS = "com.iqoo.secure.MainActivity";
    private final String PKG = "com.iqoo.secure";

    @Override
    public Intent getPermissionSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("package", context.getPackageName());
        intent.setComponent(new ComponentName(PKG, MAIN_CLS));
        return intent;
    }

    @Override
    public boolean needCheckByChecker() {
        return false;
    }
}
