package com.lzx.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;

import com.lzx.permission.interfaces.IDialog;
import com.lzx.permission.interfaces.IOperation;
import com.lzx.permission.interfaces.IPermissionRequestListener;

/**
 * Created by Lzx on 2017/10/11.
 */

public class PermissionsRequest {

    private static final String TAG = "PermissionsRequest";

    PermissionFragment fragment;

    PermissionsRequest(Activity activity) {
        this.fragment = getPermissionFragment(activity);
    }

    void request(IPermissionRequestListener listener, IDialog rational, IDialog neverAsk, String... permissions) {
        fragment.requestPermissions(listener, rational, neverAsk, permissions);
    }

    private PermissionFragment getPermissionFragment(Activity activity) {
        FragmentManager fM = activity.getFragmentManager();
        PermissionFragment fragment = (PermissionFragment) fM.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new PermissionFragment();
            fM.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
            fM.executePendingTransactions();
        }
        return fragment;
    }

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {

        Activity activity;

        IDialog rationalDialog = new RationalDialog();

        IDialog neverAskDialog = new NeverAskDialog();

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder rationalDialog(IDialog dialog) {
            this.rationalDialog = dialog;
            return this;
        }

        public Builder neverAskDialog(IDialog dialog) {
            this.neverAskDialog = dialog;
            return this;
        }

        public void request(IPermissionRequestListener requestListener, String... permissions) {
            PermissionsRequest request = new PermissionsRequest(activity);
            request.request(requestListener, rationalDialog, neverAskDialog, permissions);
        }
    }

    public static class ExecutelDialog implements IDialog {
        @Override
        public void showDialog(Activity activity, IOperation operation, String... permissions) {
            operation.execute();
        }
    }

    public static class CancelDialog implements IDialog {

        @Override
        public void showDialog(Activity activity, IOperation operation, String... permissions) {
            operation.cancel();
        }
    }

    public static class RationalDialog implements IDialog {

        @Override
        public void showDialog(Activity activity, final IOperation operation, String... ungrantPermissions) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permissions_dialog_title)
                    .setMessage(activity.getString(R.string.permissions_dialog_info_message,
                            PermissionsHelper.getPermissionsInfo(activity, ungrantPermissions)))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.execute();
                            onExecute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.cancel();
                            onCancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            operation.cancel();
                            RationalDialog.this.onCancel();
                        }
                    })
                    .show();
            onShow();
        }

        public void onShow() {}

        public void onExecute() {}

        public void onCancel() {}
    }

    public static class NeverAskDialog implements IDialog {

        @Override
        public void showDialog(Activity activity, final IOperation operation, String... ungrantPermissions) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permissions_dialog_title)
                    .setMessage(activity.getString(R.string.permissions_dialog_neverask_message,
                            PermissionsHelper.getPermissionsInfo(activity, ungrantPermissions)))
                    .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.execute();
                            onExecute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.cancel();
                            onCancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            operation.cancel();
                            NeverAskDialog.this.onCancel();
                        }
                    })
                    .show();
            onShow();
        }

        public void onShow() {}

        public void onExecute() {}

        public void onCancel() {}
    }
}


