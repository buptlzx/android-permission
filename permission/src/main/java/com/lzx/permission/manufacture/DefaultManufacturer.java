package com.lzx.permission.manufacture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by Lzx on 2017/11/3.
 */

public class DefaultManufacturer implements IManufacturer {

    @Override
    public Intent getPermissionSettingIntent(Context context) {
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        return intent;
    }

    @Override
    public boolean needCheckByChecker() {
        return false;
    }
}
