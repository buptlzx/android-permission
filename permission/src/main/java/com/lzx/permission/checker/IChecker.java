package com.lzx.permission.checker;

import android.content.Context;

/**
 * Created by Lzx on 2017/10/27.
 */

public interface IChecker {

    boolean check(Context context, String permission);

}
