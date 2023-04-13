package com.android.ims.rcs.uce.eab;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.ims.rcs.uce.eab.EabProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class EabContactSyncController {
    private static final String LAST_UPDATED_TIME_KEY = "eab_last_updated_time";
    private static final int NOT_INIT_LAST_UPDATED_TIME = -1;
    private final String TAG = getClass().getSimpleName();

    public List<Uri> syncContactToEabProvider(Context context) {
        Log.d(this.TAG, "syncContactToEabProvider");
        List<Uri> refreshContacts = null;
        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = null;
        long lastUpdatedTimeStamp = getLastUpdatedTime(context);
        if (lastUpdatedTimeStamp != -1) {
            selection.append("contact_last_updated_timestamp>?");
            selectionArgs = new String[]{String.valueOf(lastUpdatedTimeStamp)};
        }
        handleContactDeletedCase(context, lastUpdatedTimeStamp);
        Cursor updatedContact = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selection.toString(), selectionArgs, null);
        if (updatedContact != null) {
            Log.d(this.TAG, "Contact changed count: " + updatedContact.getCount());
            if (updatedContact.getCount() == 0) {
                return new ArrayList();
            }
            handlePhoneNumberDeletedCase(context, updatedContact);
            refreshContacts = handlePhoneNumberInsertedCase(context, updatedContact);
            if (updatedContact.getCount() > 0) {
                long maxTimestamp = findMaxTimestamp(updatedContact);
                if (maxTimestamp != Long.MIN_VALUE) {
                    setLastUpdatedTime(context, maxTimestamp);
                }
            }
            updatedContact.close();
        } else {
            Log.e(this.TAG, "Cursor is null.");
        }
        return refreshContacts;
    }

    private void handleContactDeletedCase(Context context, long timeStamp) {
        String selection = "";
        if (timeStamp != -1) {
            selection = "contact_deleted_timestamp>" + timeStamp;
        }
        Cursor cursor = context.getContentResolver().query(ContactsContract.DeletedContacts.CONTENT_URI, new String[]{EabProvider.ContactColumns.CONTACT_ID, "contact_deleted_timestamp"}, selection, null, null);
        if (cursor == null) {
            Log.d(this.TAG, "handleContactDeletedCase() cursor is null.");
            return;
        }
        Log.d(this.TAG, "(Case 1) The count of contact that need to be deleted: " + cursor.getCount());
        StringBuilder deleteClause = new StringBuilder();
        while (cursor.moveToNext()) {
            if (deleteClause.length() > 0) {
                deleteClause.append(" OR ");
            }
            String contactId = cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.CONTACT_ID));
            deleteClause.append("contact_id=" + contactId);
        }
        if (deleteClause.toString().length() > 0) {
            int number = context.getContentResolver().delete(EabProvider.CONTACT_URI, deleteClause.toString(), null);
            Log.d(this.TAG, "(Case 1) Deleted contact count=" + number);
        }
    }

    private void handlePhoneNumberDeletedCase(Context context, Cursor cursor) {
        Map<String, List<String>> phoneNumberMap = new HashMap<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String mimeType = cursor.getString(cursor.getColumnIndex("mimetype"));
            if (mimeType.equals("vnd.android.cursor.item/phone_v2")) {
                String rawContactId = cursor.getString(cursor.getColumnIndex(EabProvider.ContactColumns.RAW_CONTACT_ID));
                String number = formatNumber(context, cursor.getString(cursor.getColumnIndex("data1")));
                if (phoneNumberMap.containsKey(rawContactId)) {
                    phoneNumberMap.get(rawContactId).add(number);
                } else {
                    List<String> phoneNumberList = new ArrayList<>();
                    phoneNumberList.add(number);
                    phoneNumberMap.put(rawContactId, phoneNumberList);
                }
            }
        }
        StringBuilder deleteClause = new StringBuilder();
        List<String> deleteClauseArgs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : phoneNumberMap.entrySet()) {
            String rawContactId2 = entry.getKey();
            List<String> phoneNumberList2 = entry.getValue();
            if (deleteClause.length() > 0) {
                deleteClause.append(" OR ");
            }
            deleteClause.append("(raw_contact_id=? ");
            deleteClauseArgs.add(rawContactId2);
            if (phoneNumberList2.size() > 0) {
                String argsList = (String) phoneNumberList2.stream().map(new Function() { // from class: com.android.ims.rcs.uce.eab.EabContactSyncController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return EabContactSyncController.lambda$handlePhoneNumberDeletedCase$0((String) obj);
                    }
                }).collect(Collectors.joining(", "));
                deleteClause.append(" AND phone_number NOT IN (" + argsList + "))");
                deleteClauseArgs.addAll(phoneNumberList2);
            } else {
                deleteClause.append(")");
            }
        }
        Log.d(this.TAG, "(Case 2, 3) handlePhoneNumberDeletedCase number count= " + context.getContentResolver().delete(EabProvider.CONTACT_URI, deleteClause.toString(), (String[]) deleteClauseArgs.toArray(new String[0])));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$handlePhoneNumberDeletedCase$0(String s) {
        return "?";
    }

    private List<Uri> handlePhoneNumberInsertedCase(Context context, Cursor contactCursor) {
        List<Uri> refreshContacts = new ArrayList<>();
        List<ContentValues> allContactData = new ArrayList<>();
        int i = -1;
        contactCursor.moveToPosition(-1);
        Cursor eabContact = context.getContentResolver().query(EabProvider.CONTACT_URI, null, "data_id IS NOT NULL", null, EabProvider.ContactColumns.DATA_ID);
        while (contactCursor.moveToNext()) {
            String contactId = contactCursor.getString(contactCursor.getColumnIndex(EabProvider.ContactColumns.CONTACT_ID));
            String rawContactId = contactCursor.getString(contactCursor.getColumnIndex(EabProvider.ContactColumns.RAW_CONTACT_ID));
            String dataId = contactCursor.getString(contactCursor.getColumnIndex("_id"));
            String mimeType = contactCursor.getString(contactCursor.getColumnIndex("mimetype"));
            if (mimeType.equals("vnd.android.cursor.item/phone_v2")) {
                String number = formatNumber(context, contactCursor.getString(contactCursor.getColumnIndex("data1")));
                int index = searchDataIdIndex(eabContact, Integer.parseInt(dataId));
                if (index == i) {
                    Log.d(this.TAG, "Data id does not exist. Insert phone number into EAB db.");
                    refreshContacts.add(Uri.parse(number));
                    ContentValues data = new ContentValues();
                    data.put(EabProvider.ContactColumns.CONTACT_ID, contactId);
                    data.put(EabProvider.ContactColumns.DATA_ID, dataId);
                    data.put(EabProvider.ContactColumns.RAW_CONTACT_ID, rawContactId);
                    data.put(EabProvider.ContactColumns.PHONE_NUMBER, number);
                    allContactData.add(data);
                }
                i = -1;
            }
        }
        int result = context.getContentResolver().bulkInsert(EabProvider.CONTACT_URI, (ContentValues[]) allContactData.toArray(new ContentValues[0]));
        Log.d(this.TAG, "(Case 3, 4) Phone number insert count: " + result);
        return refreshContacts;
    }

    private int searchDataIdIndex(Cursor cursor, int targetDataId) {
        int start = 0;
        int end = cursor.getCount() - 1;
        while (start <= end) {
            int position = (start + end) >>> 1;
            cursor.moveToPosition(position);
            int dataId = cursor.getInt(cursor.getColumnIndex(EabProvider.ContactColumns.DATA_ID));
            if (dataId > targetDataId) {
                end = position - 1;
            } else if (dataId < targetDataId) {
                start = position + 1;
            } else {
                return position;
            }
        }
        return -1;
    }

    private long findMaxTimestamp(Cursor cursor) {
        long maxTimestamp = Long.MIN_VALUE;
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            long lastUpdatedTimeStamp = cursor.getLong(cursor.getColumnIndex("contact_last_updated_timestamp"));
            Log.d(this.TAG, lastUpdatedTimeStamp + " " + maxTimestamp);
            if (lastUpdatedTimeStamp > maxTimestamp) {
                maxTimestamp = lastUpdatedTimeStamp;
            }
        }
        return maxTimestamp;
    }

    private void setLastUpdatedTime(Context context, long timestamp) {
        Log.d(this.TAG, "setLastUpdatedTime: " + timestamp);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(LAST_UPDATED_TIME_KEY, timestamp).apply();
    }

    private long getLastUpdatedTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(LAST_UPDATED_TIME_KEY, -1L);
    }

    private String formatNumber(Context context, String number) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        String simCountryIso = manager.getSimCountryIso();
        if (simCountryIso != null) {
            String simCountryIso2 = simCountryIso.toUpperCase();
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phoneNumber = util.parse(number, simCountryIso2);
                return util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            } catch (NumberParseException e) {
                Log.w(this.TAG, "formatNumber: could not format " + number + ", error: " + e);
            }
        }
        return number;
    }
}
