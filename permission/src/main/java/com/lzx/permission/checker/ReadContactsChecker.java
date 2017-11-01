package com.lzx.permission.checker;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by Lzx on 2017/10/27.
 */

public class ReadContactsChecker implements IChecker {
    @Override
    public boolean check(Context context, String permission) {
        return checkReadContactsPermission(context);
    }

    private static boolean checkReadContactsPermission(Context context) {
        if (context == null) {
            return false;
        }

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cursor == null) {
                return false;
            }
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
            }
        }
        return count != 0;
    }
}
