package com.android.internal.telephony;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import java.security.PublicKey;
import java.util.Date;
/* loaded from: classes.dex */
public class CarrierInfoManager {
    private long mLastAccessResetCarrierKey = 0;

    /* JADX WARN: Code restructure failed: missing block: B:94:0x01eb, code lost:
        if (r11 == null) goto L92;
     */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x01f2: MOVE  (r9 I:??[OBJECT, ARRAY]) = (r11 I:??[OBJECT, ARRAY]), block:B:98:0x01f2 */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01f5  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0171 A[Catch: Exception -> 0x00c6, IllegalArgumentException -> 0x00c9, all -> 0x01f1, TRY_LEAVE, TryCatch #0 {all -> 0x01f1, blocks: (B:8:0x0067, B:11:0x006e, B:13:0x0074, B:14:0x0088, B:16:0x008e, B:23:0x00a4, B:19:0x009a, B:26:0x00ac, B:27:0x00af, B:31:0x00be, B:30:0x00ba, B:39:0x00ce, B:41:0x00e4, B:45:0x00ef, B:47:0x00fb, B:52:0x0108, B:56:0x0113, B:58:0x0119, B:62:0x0124, B:64:0x0130, B:69:0x0154, B:74:0x016b, B:76:0x0171, B:80:0x018b, B:72:0x0160, B:88:0x01be, B:93:0x01d7), top: B:104:0x0035 }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x018b A[Catch: Exception -> 0x00c6, IllegalArgumentException -> 0x00c9, all -> 0x01f1, TRY_ENTER, TRY_LEAVE, TryCatch #0 {all -> 0x01f1, blocks: (B:8:0x0067, B:11:0x006e, B:13:0x0074, B:14:0x0088, B:16:0x008e, B:23:0x00a4, B:19:0x009a, B:26:0x00ac, B:27:0x00af, B:31:0x00be, B:30:0x00ba, B:39:0x00ce, B:41:0x00e4, B:45:0x00ef, B:47:0x00fb, B:52:0x0108, B:56:0x0113, B:58:0x0119, B:62:0x0124, B:64:0x0130, B:69:0x0154, B:74:0x016b, B:76:0x0171, B:80:0x018b, B:72:0x0160, B:88:0x01be, B:93:0x01d7), top: B:104:0x0035 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static ImsiEncryptionInfo getCarrierInfoForImsiEncryption(int i, Context context, String str, int i2, boolean z, int i3) {
        Cursor cursor;
        Cursor cursor2;
        String str2;
        String str3;
        String str4;
        Cursor cursor3 = null;
        if (TextUtils.isEmpty(str)) {
            Log.e("CarrierInfoManager", "Invalid networkOperator: " + str);
            return null;
        }
        String substring = str.substring(0, 3);
        String substring2 = str.substring(3);
        Log.i("CarrierInfoManager", "using values for mcc, mnc: " + substring + "," + substring2);
        try {
            try {
                cursor = context.getContentResolver().query(Telephony.CarrierColumns.CONTENT_URI, new String[]{"public_key", "expiration_time", "key_identifier", "carrier_id"}, "mcc=? and mnc=? and key_type=?", new String[]{substring, substring2, String.valueOf(i)}, null);
                int i4 = -1;
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            if (cursor.getCount() > 1) {
                                Log.e("CarrierInfoManager", "More than 1 row found for the keyType: " + i);
                                while (cursor.moveToNext()) {
                                    String string = cursor.getString(3);
                                    int parseInt = TextUtils.isEmpty(string) ? -1 : Integer.parseInt(string);
                                    if (parseInt != -1 && parseInt == i2) {
                                        ImsiEncryptionInfo imsiEncryptionInfo = getImsiEncryptionInfo(cursor, substring, substring2, i, parseInt);
                                        cursor.close();
                                        return imsiEncryptionInfo;
                                    }
                                }
                                cursor.moveToFirst();
                            }
                            String string2 = cursor.getString(3);
                            if (!TextUtils.isEmpty(string2)) {
                                i4 = Integer.parseInt(string2);
                            }
                            ImsiEncryptionInfo imsiEncryptionInfo2 = getImsiEncryptionInfo(cursor, substring, substring2, i, i4);
                            cursor.close();
                            return imsiEncryptionInfo2;
                        }
                    } catch (IllegalArgumentException e) {
                        e = e;
                        Log.e("CarrierInfoManager", "Bad arguments:" + e);
                    } catch (Exception e2) {
                        e = e2;
                        Log.e("CarrierInfoManager", "Query failed:" + e);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return null;
                    }
                }
                Log.d("CarrierInfoManager", "No rows found for keyType: " + i);
                if (!z) {
                    Log.d("CarrierInfoManager", "Skipping fallback logic");
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                }
                CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
                if (carrierConfigManager == null) {
                    Log.d("CarrierInfoManager", "Could not get CarrierConfigManager for backup key");
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } else if (i3 == -1) {
                    Log.d("CarrierInfoManager", "Could not get carrier config with invalid subId");
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } else {
                    PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(i3);
                    if (configForSubId == null) {
                        Log.d("CarrierInfoManager", "Could not get carrier config bundle for backup key");
                        if (cursor != null) {
                            cursor.close();
                        }
                        return null;
                    }
                    int i5 = configForSubId.getInt("imsi_key_availability_int");
                    if (!CarrierKeyDownloadManager.isKeyEnabled(i, i5)) {
                        Log.d("CarrierInfoManager", "Backup key does not have matching keyType. keyType=" + i + " keyAvailability=" + i5);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return null;
                    }
                    if (i == 1) {
                        str2 = configForSubId.getString("imsi_carrier_public_key_epdg_string");
                        str4 = "backup_key_from_carrier_config_epdg";
                    } else if (i != 2) {
                        str2 = null;
                        str3 = null;
                        if (TextUtils.isEmpty(str2)) {
                            Pair<PublicKey, Long> keyInformation = CarrierKeyDownloadManager.getKeyInformation(str2.getBytes());
                            ImsiEncryptionInfo imsiEncryptionInfo3 = new ImsiEncryptionInfo(substring, substring2, i, str3, (PublicKey) keyInformation.first, new Date(((Long) keyInformation.second).longValue()), i2);
                            if (cursor != null) {
                                cursor.close();
                            }
                            return imsiEncryptionInfo3;
                        }
                        Log.d("CarrierInfoManager", "Could not get carrier config key string for backup key. keyType=" + i);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return null;
                    } else {
                        str2 = configForSubId.getString("imsi_carrier_public_key_wlan_string");
                        str4 = "backup_key_from_carrier_config_wlan";
                    }
                    str3 = str4;
                    if (TextUtils.isEmpty(str2)) {
                    }
                }
            } catch (Throwable th) {
                th = th;
                cursor3 = cursor2;
                if (cursor3 != null) {
                    cursor3.close();
                }
                throw th;
            }
        } catch (IllegalArgumentException e3) {
            e = e3;
            cursor = null;
        } catch (Exception e4) {
            e = e4;
            cursor = null;
        } catch (Throwable th2) {
            th = th2;
            if (cursor3 != null) {
            }
            throw th;
        }
    }

    private static ImsiEncryptionInfo getImsiEncryptionInfo(Cursor cursor, String str, String str2, int i, int i2) {
        try {
            return new ImsiEncryptionInfo(str, str2, i, cursor.getString(2), cursor.getBlob(0), new Date(cursor.getLong(1)), i2);
        } catch (Exception e) {
            Log.e("CarrierInfoManager", "Exception = " + e.getMessage());
            return null;
        }
    }

    public static void updateOrInsertCarrierKey(ImsiEncryptionInfo imsiEncryptionInfo, Context context, int i) {
        byte[] encoded = imsiEncryptionInfo.getPublicKey().getEncoded();
        ContentResolver contentResolver = context.getContentResolver();
        TelephonyMetrics telephonyMetrics = TelephonyMetrics.getInstance();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mcc", imsiEncryptionInfo.getMcc());
        contentValues.put("mnc", imsiEncryptionInfo.getMnc());
        contentValues.put("carrier_id", Integer.valueOf(imsiEncryptionInfo.getCarrierId()));
        contentValues.put("key_type", Integer.valueOf(imsiEncryptionInfo.getKeyType()));
        contentValues.put("key_identifier", imsiEncryptionInfo.getKeyIdentifier());
        contentValues.put("public_key", encoded);
        contentValues.put("expiration_time", Long.valueOf(imsiEncryptionInfo.getExpirationTime().getTime()));
        boolean z = false;
        try {
            try {
                Log.i("CarrierInfoManager", "Inserting imsiEncryptionInfo into db");
                contentResolver.insert(Telephony.CarrierColumns.CONTENT_URI, contentValues);
            } catch (SQLiteConstraintException unused) {
                Log.i("CarrierInfoManager", "Insert failed, updating imsiEncryptionInfo into db");
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("public_key", encoded);
                contentValues2.put("expiration_time", Long.valueOf(imsiEncryptionInfo.getExpirationTime().getTime()));
                contentValues2.put("key_identifier", imsiEncryptionInfo.getKeyIdentifier());
                try {
                    if (contentResolver.update(Telephony.CarrierColumns.CONTENT_URI, contentValues2, "mcc=? and mnc=? and key_type=? and carrier_id=?", new String[]{imsiEncryptionInfo.getMcc(), imsiEncryptionInfo.getMnc(), String.valueOf(imsiEncryptionInfo.getKeyType()), String.valueOf(imsiEncryptionInfo.getCarrierId())}) == 0) {
                        Log.d("CarrierInfoManager", "Error updating values:" + imsiEncryptionInfo);
                    } else {
                        z = true;
                    }
                } catch (Exception e) {
                    Log.d("CarrierInfoManager", "Error updating values:" + imsiEncryptionInfo + e);
                }
                telephonyMetrics.writeCarrierKeyEvent(i, imsiEncryptionInfo.getKeyType(), z);
            } catch (Exception e2) {
                Log.d("CarrierInfoManager", "Error inserting/updating values:" + imsiEncryptionInfo + e2);
                telephonyMetrics.writeCarrierKeyEvent(i, imsiEncryptionInfo.getKeyType(), z);
            }
        } finally {
            telephonyMetrics.writeCarrierKeyEvent(i, imsiEncryptionInfo.getKeyType(), true);
        }
    }

    public static void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo, Context context, int i) {
        Log.i("CarrierInfoManager", "inserting carrier key: " + imsiEncryptionInfo);
        updateOrInsertCarrierKey(imsiEncryptionInfo, context, i);
    }

    public void resetCarrierKeysForImsiEncryption(Context context, int i) {
        Log.i("CarrierInfoManager", "resetting carrier key");
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastAccessResetCarrierKey < 43200000) {
            Log.i("CarrierInfoManager", "resetCarrierKeysForImsiEncryption: Access rate exceeded");
            return;
        }
        this.mLastAccessResetCarrierKey = currentTimeMillis;
        int subscriptionId = SubscriptionManager.getSubscriptionId(i);
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            Log.e("CarrierInfoManager", "Could not reset carrier keys, subscription for mPhoneId=" + i);
            return;
        }
        deleteCarrierInfoForImsiEncryption(context, subscriptionId, ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionId).getSimCarrierId());
        Intent intent = new Intent("com.android.internal.telephony.ACTION_CARRIER_CERTIFICATE_DOWNLOAD");
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public static void deleteCarrierInfoForImsiEncryption(Context context, int i, int i2) {
        Log.i("CarrierInfoManager", "deleting carrier key from db for subId=" + i);
        String simOperator = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getSimOperator();
        if (!TextUtils.isEmpty(simOperator)) {
            String substring = simOperator.substring(0, 3);
            String substring2 = simOperator.substring(3);
            String valueOf = String.valueOf(i2);
            try {
                int delete = context.getContentResolver().delete(Telephony.CarrierColumns.CONTENT_URI, "mcc=? and mnc=? and carrier_id=?", new String[]{substring, substring2, valueOf});
                Log.i("CarrierInfoManager", "Deleting the number of entries = " + delete + "   for carrierId = " + valueOf);
                return;
            } catch (Exception e) {
                Log.e("CarrierInfoManager", "Delete failed" + e);
                return;
            }
        }
        Log.e("CarrierInfoManager", "Invalid networkOperator: " + simOperator);
    }

    public static void deleteAllCarrierKeysForImsiEncryption(Context context) {
        Log.i("CarrierInfoManager", "deleting ALL carrier keys from db");
        try {
            context.getContentResolver().delete(Telephony.CarrierColumns.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e("CarrierInfoManager", "Delete failed" + e);
        }
    }
}
