package com.lzx.permission.checker;

import android.Manifest;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lzx on 2017/10/27.
 */

public class PermissionCheckerManager implements IChecker {

    public static PermissionCheckerManager inst() {
        return Single.single;
    }
    private final static class Single {
        private final static PermissionCheckerManager single = new PermissionCheckerManager();
    }
    private Map<String, IChecker> checkerMap;
    private IChecker defaultChecker;

    PermissionCheckerManager() {
        init();
    }

    public void init() {
        checkerMap = new HashMap<>();
        checkerMap.put(Manifest.permission.CAMERA, new CameraChecker());
        checkerMap.put(Manifest.permission.RECORD_AUDIO, new AudioRecorderChecker());
        checkerMap.put(Manifest.permission.READ_CONTACTS, new ReadContactsChecker());

        defaultChecker = new DefaultChecker();
    }

    @Override
    public boolean check(Context context, String permission) {
        boolean result = false;
        if (checkerMap.containsKey(permission)) {
            result = checkerMap.get(permission).check(context, permission);
        } else {
            result = defaultChecker.check(context, permission);
        }
        return result;
    }

    public boolean check(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!check(context, permission)) {
                return false;
            }
        }
        return true;
    }
}
