package com.lzx.permission.checker;

import android.content.Context;
import android.support.v4.content.PermissionChecker;

/**
 * Created by Lzx on 2017/10/27.
 */

public class DefaultChecker implements IChecker {
    @Override
    public boolean check(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
    }
}
