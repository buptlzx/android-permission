package com.lzx.permission.interfaces;

/**
 * Created by Lzx on 2017/10/11.
 */

public interface IPermissionRequestListener {

    void onPermissionsGrant(String... permissions);

    void onPermissionDenied(String... permissions);

}
