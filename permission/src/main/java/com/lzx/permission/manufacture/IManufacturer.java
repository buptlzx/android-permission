package com.lzx.permission.manufacture;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Lzx on 2017/11/3.
 */

public interface IManufacturer {

    Intent getPermissionSettingIntent(Context context);
    boolean needCheckByChecker();

}
