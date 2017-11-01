package com.lzx.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


import com.lzx.permission.checker.PermissionCheckerManager;
import com.lzx.permission.utils.ManufacturerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lzx on 2017/10/12.
 */

public class PermissionsHelper {

    private static final Map<String, Integer> sPermissionMap;
    static {
        sPermissionMap = new HashMap<>();
        sPermissionMap.put(Manifest.permission.READ_CALENDAR, R.string.permissions_calendar);
        sPermissionMap.put(Manifest.permission.WRITE_CALENDAR, R.string.permissions_calendar);

        sPermissionMap.put(Manifest.permission.CAMERA, R.string.permissions_camera);

        sPermissionMap.put(Manifest.permission.READ_CONTACTS, R.string.permissions_contract);
        sPermissionMap.put(Manifest.permission.WRITE_CONTACTS, R.string.permissions_contract);
        sPermissionMap.put(Manifest.permission.GET_ACCOUNTS, R.string.permissions_contract);

        sPermissionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.permissions_location);
        sPermissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.permissions_location);

        sPermissionMap.put(Manifest.permission.RECORD_AUDIO, R.string.permissions_record_audio);

        sPermissionMap.put(Manifest.permission.READ_PHONE_STATE, R.string.permissions_read_phone_status);
        sPermissionMap.put(Manifest.permission.CALL_PHONE, R.string.permissions_call);
        sPermissionMap.put(Manifest.permission.READ_CALL_LOG, R.string.permissions_call_log);
        sPermissionMap.put(Manifest.permission.WRITE_CALL_LOG, R.string.permissions_call_log);
        sPermissionMap.put(Manifest.permission.ADD_VOICEMAIL, R.string.permissions_call);
        sPermissionMap.put(Manifest.permission.USE_SIP, R.string.permissions_call);
        sPermissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS, R.string.permissions_call);

        sPermissionMap.put(Manifest.permission.BODY_SENSORS, R.string.permissions_sensors);

        sPermissionMap.put(Manifest.permission.SEND_SMS, R.string.permissions_sms);
        sPermissionMap.put(Manifest.permission.RECEIVE_SMS, R.string.permissions_sms);
        sPermissionMap.put(Manifest.permission.READ_SMS, R.string.permissions_sms);
        sPermissionMap.put(Manifest.permission.RECEIVE_WAP_PUSH, R.string.permissions_sms);
        sPermissionMap.put(Manifest.permission.RECEIVE_MMS, R.string.permissions_sms);

        sPermissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.permissions_external_storage);
        sPermissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permissions_external_storage);
    }

    static String getPermissionsInfo(Context context, String... permissions) {
        List<Integer> resIds = new ArrayList<>();
        for (String permission : permissions) {
            if (sPermissionMap.containsKey(permission)) {
                Integer id = sPermissionMap.get(permission);
                if (!resIds.contains(id)) {
                    resIds.add(id);
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Integer i : resIds) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(context.getString(i));
        }
        return builder.toString();
    }


    public static String[] getUngrantPermissions(Activity activity, String... permissions) {
        List<String> ungrant = new ArrayList<>();
        for (String permission : permissions) {
            if (!hasPermissions(activity, permission)) {
                ungrant.add(permission);
            }
        }
        return ungrant.toArray(new String[ungrant.size()]);
    }

    public static boolean hasPermissions(Activity activity, String... permissions) {
        if (permissions == null)
            return true;

        if (Build.VERSION.SDK_INT >= 23 && !ManufacturerUtil.needCheckByChecker()) {
            return PermissionUtils.hasSelfPermissions(activity, permissions);
        } else {
            return PermissionCheckerManager.inst().check(activity, permissions);
        }

    }

    public static boolean shouldShowRationale(Activity activity, String... permissions) {
        if (permissions == null)
            return false;
        return PermissionUtils.shouldShowRequestPermissionRationale(activity, permissions);
    }

    public static boolean verifyPermissions(Activity activity, String[] permissions, int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        if (ManufacturerUtil.needCheckByChecker()) {
            return hasPermissions(activity, permissions);
        }
        return true;
    }
}
