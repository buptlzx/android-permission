package com.lzx.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;

import com.lzx.permission.interfaces.IDialog;
import com.lzx.permission.interfaces.IOperation;
import com.lzx.permission.interfaces.IPermissionRequestListener;
import com.lzx.permission.manufacture.ManufacturerManager;

/**
 * Created by Lzx on 2017/10/11.
 */

public class PermissionFragment extends Fragment {

    static final int i = 4;
    private static final String TAG = "PermissionFragment" + i;
    SparseArrayCompat<Request> permissionRequestMap = new SparseArrayCompat<>();
    SparseArrayCompat<Request> settingRequestMap = new SparseArrayCompat<>();

    void requestPermissions(final IPermissionRequestListener requestListener,
                            final IDialog rationDialog, IDialog neverAskDialog, final String... permissions) {
        Log.d(TAG, "requestPermissions: ");
        final Request request = new Request(permissions, requestListener, rationDialog, neverAskDialog);

        String[] ungrant = PermissionsHelper.getUngrantPermissions(getActivity(), request.getPermissions());
        if (ungrant.length == 0) {
            request.onPermissionsGrant();
            return;
        }

        if (Build.VERSION.SDK_INT < 23 || ManufacturerManager.inst().needCheckByChecker()) {
            showDialogForNeverAsk(request, ungrant);
            return;
        }

        if (PermissionsHelper.shouldShowRationale(getActivity(), permissions)) {
            showRational(request, ungrant);
        } else {
            doRequestPermissions(request, ungrant);
        }
    }

    private void showRational(final Request request, final String[] ungrant) {
        IOperation operation = new IOperation() {

            @Override
            public void execute() {
                doRequestPermissions(request, ungrant);
            }

            @Override
            public void cancel() {
                request.onPermissionDenied();
            }
        };
        request.showDialogForReason(getActivity(), operation, ungrant);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void doRequestPermissions(Request request, String[] ungrant) {
        int requestCode = request.genPermissionRequestCode();
        Log.d(TAG, "doRequestPermissions: requestCode = " + requestCode );
        permissionRequestMap.put(requestCode, request);
        requestPermissions(ungrant, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Request request = permissionRequestMap.get(requestCode);
        permissionRequestMap.remove(requestCode);
        if (request != null) {
            afterPermissionRequest(permissions, grantResults, request);
        }
    }

    private void afterPermissionRequest(String[] permissions, int[] grantResults, final Request request) {
        boolean grant = PermissionsHelper.verifyPermissions(getActivity(), permissions, grantResults);
        if (grant) {
            request.onPermissionsGrant();
            return;
        }

        boolean showRationaleNextTime = PermissionsHelper.shouldShowRationale(getActivity(), permissions);
        if (showRationaleNextTime) {
            request.onPermissionDenied();
        } else {
            if (!isIntentAvaliable(getActivity(), getAppSettingIntent(getActivity()))) {
                Log.d(TAG, "afterPermissionRequest: AppSettingIntent is not avaliable.");
                request.onPermissionDenied();
                return;
            }

            String[] ungrant = PermissionsHelper.getUngrantPermissions(getActivity(), request.getPermissions());
            showDialogForNeverAsk(request, ungrant);
        }
    }

    private void showDialogForNeverAsk(final Request request, String[] ungrant) {
        IOperation operation = new IOperation() {
            @Override
            public void execute() {
                gotoSettings(request);
            }

            @Override
            public void cancel() {
                request.onPermissionDenied();
            }
        };
        request.showDialogForNeverAsk(getActivity(), operation, ungrant);
    }


    private void gotoSettings(Request request) {
        try {
            int requestCode = request.getSettingRequestCode();
            Intent intent = getAppSettingIntent(getActivity());
            startActivityForResult(intent, requestCode);
            Log.d(TAG, "gotoSettings: requestCode" + requestCode);

            settingRequestMap.put(requestCode, request);
        } catch (Exception e) {
            Log.e(TAG, "gotoSettings error");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Request request = settingRequestMap.get(requestCode);
        settingRequestMap.remove(requestCode);

        if (request != null) {
            if (PermissionsHelper.hasPermissions(getActivity(), request.getPermissions())) {
                request.onPermissionsGrant();
            }
        }
    }

    private static class Request {

        private String[] permissions;
        private IPermissionRequestListener requestListener;
        private IDialog rationalDialog;
        private IDialog neverAskDialog;

        public Request(String[] permissions, IPermissionRequestListener request, IDialog rationalDialog, IDialog neverAskDialog) {
            this.permissions = permissions;
            this.requestListener = request;
            this.rationalDialog = rationalDialog;
            this.neverAskDialog = neverAskDialog;
        }

        public void onPermissionsGrant() {
            requestListener.onPermissionsGrant(permissions);
        }

        public void onPermissionDenied() {
            requestListener.onPermissionDenied(permissions);
        }

        public void showDialogForReason(Activity activity, IOperation operation, String[] ungrant) {
            rationalDialog.showDialog(activity, operation, permissions, ungrant);
        }

        public void showDialogForNeverAsk(Activity activity, IOperation operation, String[] ungrant) {
            neverAskDialog.showDialog(activity, operation, permissions, ungrant);
        }

        public String[] getPermissions() {
            return permissions;
        }

        public int genPermissionRequestCode() {
            int requestCode = HashCodeHelpers.hashCodeGeneric(permissions);
            requestCode = requestCode & 0xffff;
            return requestCode;
        }

        public int getSettingRequestCode() {
            int requestCode = HashCodeHelpers.hashCodeGeneric(permissions);
            requestCode = (requestCode & 0xffff0000) >>> 16;
            return requestCode;
        }

    }

    private static Intent getAppSettingIntent(Context context) {
        return ManufacturerManager.inst().getPermissionSettingIntent(context);
    }

    private static boolean isIntentAvaliable(Context context, Intent intent) {
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo == null || resolveInfo.activityInfo == null || resolveInfo.activityInfo.name.toLowerCase().contains("resolver")) {
            return false;
        }
        return true;
    }
}
