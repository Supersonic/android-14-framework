package com.android.ims.rcs.uce.eab;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.eab.EabProvider;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class EabUtil {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "EabUtil";

    public static String getContactFromEab(Context context, String contact) {
        StringBuilder result = new StringBuilder();
        try {
            Cursor cursor = context.getContentResolver().query(EabProvider.CONTACT_URI, new String[]{EabProvider.ContactColumns.PHONE_NUMBER, EabProvider.ContactColumns.RAW_CONTACT_ID, EabProvider.ContactColumns.CONTACT_ID, EabProvider.ContactColumns.DATA_ID}, "phone_number=?", new String[]{contact}, null);
            if (cursor != null && cursor.moveToFirst()) {
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.PHONE_NUMBER)));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.RAW_CONTACT_ID)));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.CONTACT_ID)));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.DATA_ID)));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "getEabContactId exception " + e);
        }
        Log.d(LOG_TAG, "getContactFromEab() result: " + ((Object) result));
        return result.toString();
    }

    public static String getCapabilityFromEab(Context context, String contact) {
        StringBuilder result = new StringBuilder();
        try {
            Cursor cursor = context.getContentResolver().query(EabProvider.ALL_DATA_URI, new String[]{EabProvider.ContactColumns.PHONE_NUMBER, "_id", EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP, "_id", EabProvider.OptionsColumns.REQUEST_TIMESTAMP}, "phone_number=?", new String[]{contact}, null);
            if (cursor != null && cursor.moveToFirst()) {
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.PHONE_NUMBER)));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex("_id")));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP)));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex("_id")));
                result.append(",");
                result.append(cursor.getString(cursor.getColumnIndex(EabProvider.OptionsColumns.REQUEST_TIMESTAMP)));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "getCapability exception " + e);
        }
        Log.d(LOG_TAG, "getCapabilityFromEab() result: " + ((Object) result));
        return result.toString();
    }

    public static int removeContactFromEab(int subId, String contacts, Context context) {
        List<String> contactList;
        if (TextUtils.isEmpty(contacts) || (contactList = (List) Arrays.stream(contacts.split(",")).collect(Collectors.toList())) == null || contactList.isEmpty()) {
            return -1;
        }
        int count = 0;
        for (String contact : contactList) {
            int contactId = getEabContactId(contact, context);
            if (contactId != -1) {
                int commonId = getEabCommonId(contactId, context);
                count += removeContactCapabilities(contactId, commonId, context);
            }
        }
        return count;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0053, code lost:
        if (r2 == null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0056, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int getEabContactId(String contactNumber, Context context) {
        int contactId = -1;
        Cursor cursor = null;
        String formattedNumber = EabControllerImpl.formatNumber(context, contactNumber);
        try {
            try {
                cursor = context.getContentResolver().query(EabProvider.CONTACT_URI, new String[]{"_id"}, "phone_number=?", new String[]{formattedNumber}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    contactId = cursor.getInt(cursor.getColumnIndex("_id"));
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "getEabContactId exception " + e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0057, code lost:
        if (r2 == null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x005a, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int getEabCommonId(int contactId, Context context) {
        int commonId = -1;
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(EabProvider.COMMON_URI, new String[]{"_id"}, "eab_contact_id=?", new String[]{String.valueOf(contactId)}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    commonId = cursor.getInt(cursor.getColumnIndex("_id"));
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "getEabCommonId exception " + e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static int removeContactCapabilities(int contactId, int commonId, Context context) {
        int count = context.getContentResolver().delete(EabProvider.PRESENCE_URI, "eab_common_id=?", new String[]{String.valueOf(commonId)});
        context.getContentResolver().delete(EabProvider.OPTIONS_URI, "eab_common_id=?", new String[]{String.valueOf(commonId)});
        context.getContentResolver().delete(EabProvider.COMMON_URI, "eab_contact_id=?", new String[]{String.valueOf(contactId)});
        context.getContentResolver().delete(EabProvider.CONTACT_URI, "_id=?", new String[]{String.valueOf(contactId)});
        return count;
    }
}
