package com.lzx.permission.interfaces;

import android.app.Activity;

/**
 * Created by Lzx on 2017/10/12.
 */
public interface IDialog {
    void showDialog(Activity activity, IOperation operation, String... ungrantPermissions);
}
