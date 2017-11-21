package com.lzx.permission.manufacture;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lzx on 2017/11/3.
 */

public class ManufacturerManager implements IManufacturer {

    private static final Map<String, IManufacturer> manufacturers;
    static {
        manufacturers = new HashMap<>();
//        manufacturers.put("VIVO", new VIVO());
        manufacturers.put("MEIZU", new MEIZU());
    }

    public static IManufacturer inst() {
        return Single.single;
    }
    private static final class Single {
        private static final IManufacturer single = new ManufacturerManager();
    }

    IManufacturer manufacturer;

    @Override
    public Intent getPermissionSettingIntent(Context context) {
        return getManufacturer().getPermissionSettingIntent(context);
    }

    @Override
    public boolean needCheckByChecker() {
        return getManufacturer().needCheckByChecker();
    }

    private IManufacturer getManufacturer() {
        if (manufacturer == null) {
            manufacturer = new DefaultManufacturer();

            String manufacturerName = Build.MANUFACTURER;
            if (!TextUtils.isEmpty(manufacturerName)) {
                manufacturerName = manufacturerName.toUpperCase();
                if (manufacturers.containsKey(manufacturerName)) {
                    manufacturer = manufacturers.get(manufacturerName);
                }
            }
        }
        return manufacturer;
    }
}
