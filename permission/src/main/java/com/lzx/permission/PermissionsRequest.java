package com.lzx.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

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

        IDialog rationalDialog = new ExecutelDialog();

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

        public void request(final IPermissionRequestListener requestListener, final String... permissions) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    PermissionsRequest request = new PermissionsRequest(activity);
                    request.request(requestListener, rationalDialog, neverAskDialog, permissions);
                }
            });
        }
    }

    public static class ExecutelDialog implements IDialog {
        @Override
        public void showDialog(Activity activity, IOperation operation, String[] permissions, String[] ungrantPermissions) {
            operation.execute();
        }
    }

    public static class CancelDialog implements IDialog {

        @Override
        public void showDialog(Activity activity, IOperation operation, String[] permissions, String[] ungrantPermissions) {
            operation.cancel();
        }
    }

    public static abstract class AbsDialog implements IDialog {

        protected abstract String getPermissionMessage(Activity activity, String... permissions);
        protected abstract String getPermissionTitle(Activity activity, String... permissions);
        protected String getPositiveText(Activity activity) {
            return activity.getString(R.string.ok);
        }
        protected String getNegativeText(Activity activity) {
            return activity.getString(R.string.cancel);
        }

        @Override
        public final void showDialog(Activity activity, final IOperation operation, String[] permissions, String[] ungrantPermissions) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(getPermissionTitle(activity, permissions))
                    .setMessage(getPermissionMessage(activity, permissions))
                    .setPositiveButton(getPositiveText(activity), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.execute();
                            onExecute();
                        }
                    })
                    .setNegativeButton(getNegativeText(activity), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            operation.cancel();
                            onCancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            AbsDialog.this.onCancel();
                        }
                    }).show();
            onShow();
        }

        public void onShow() {}
        public void onExecute() {}
        public void onCancel() {}
    }

    public static class RationalDialog extends AbsDialog {

        @Override
        protected String getPermissionTitle(Activity activity, String... permissions) {
            return activity.getString(R.string.permissions_dialog_title);
        }

        @Override
        protected String getPermissionMessage(Activity activity, String... permissions) {
            return activity.getString(R.string.permissions_dialog_info_message, PermissionsHelper.getPermissionsInfo(activity, permissions));
        }
    }

    public static class NeverAskDialog extends AbsDialog {

        @Override
        protected String getPermissionTitle(Activity activity, String... permissions) {
            return activity.getString(R.string.permissions_dialog_title);
        }

        @Override
        protected String getPermissionMessage(Activity activity, String... permissions) {
            return activity.getString(R.string.permissions_dialog_neverask_message, PermissionsHelper.getPermissionsInfo(activity, permissions));
        }

        @Override
        protected String getPositiveText(Activity activity) {
            return activity.getString(R.string.setting);
        }
    }
}


