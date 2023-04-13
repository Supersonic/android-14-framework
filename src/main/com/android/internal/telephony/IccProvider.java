package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.internal.telephony.uicc.IccConstants;
import com.android.telephony.Rlog;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class IccProvider extends ContentProvider {
    protected static final int ADN = 1;
    protected static final int ADN_ALL = 7;
    protected static final int ADN_SUB = 2;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static final boolean DBG = true;
    protected static final int FDN = 3;
    protected static final int FDN_SUB = 4;
    protected static final int SDN = 5;
    protected static final int SDN_SUB = 6;
    @VisibleForTesting
    public static final String STR_NEW_ANRS = "newAnrs";
    @VisibleForTesting
    public static final String STR_NEW_EMAILS = "newEmails";
    @VisibleForTesting
    public static final String STR_NEW_NUMBER = "newNumber";
    @VisibleForTesting
    public static final String STR_NEW_TAG = "newTag";
    @VisibleForTesting
    public static final String STR_PIN2 = "pin2";
    @VisibleForTesting
    public static final String STR_TAG = "tag";
    private static final UriMatcher URL_MATCHER;
    private SubscriptionManager mSubscriptionManager;
    @VisibleForTesting
    public static final String STR_NUMBER = "number";
    @VisibleForTesting
    public static final String STR_EMAILS = "emails";
    @VisibleForTesting
    public static final String STR_ANRS = "anrs";
    @UnsupportedAppUsage
    private static final String[] ADDRESS_BOOK_COLUMN_NAMES = {"name", STR_NUMBER, STR_EMAILS, STR_ANRS, "_id"};

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        URL_MATCHER = uriMatcher;
        uriMatcher.addURI("icc", "adn", 1);
        uriMatcher.addURI("icc", "adn/subId/#", 2);
        uriMatcher.addURI("icc", "fdn", 3);
        uriMatcher.addURI("icc", "fdn/subId/#", 4);
        uriMatcher.addURI("icc", "sdn", 5);
        uriMatcher.addURI("icc", "sdn/subId/#", 6);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mSubscriptionManager = SubscriptionManager.from(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        log("query");
        switch (URL_MATCHER.match(uri)) {
            case 1:
                return loadFromEf(28474, SubscriptionManager.getDefaultSubscriptionId());
            case 2:
                return loadFromEf(28474, getRequestSubId(uri));
            case 3:
                return loadFromEf(IccConstants.EF_FDN, SubscriptionManager.getDefaultSubscriptionId());
            case 4:
                return loadFromEf(IccConstants.EF_FDN, getRequestSubId(uri));
            case 5:
                return loadFromEf(IccConstants.EF_SDN, SubscriptionManager.getDefaultSubscriptionId());
            case 6:
                return loadFromEf(IccConstants.EF_SDN, getRequestSubId(uri));
            case 7:
                return loadAllSimContacts(28474);
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    private Cursor loadAllSimContacts(int i) {
        Cursor[] cursorArr;
        List activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (activeSubscriptionInfoList == null || activeSubscriptionInfoList.size() == 0) {
            cursorArr = new Cursor[0];
        } else {
            int size = activeSubscriptionInfoList.size();
            cursorArr = new Cursor[size];
            for (int i2 = 0; i2 < size; i2++) {
                int subscriptionId = ((SubscriptionInfo) activeSubscriptionInfoList.get(i2)).getSubscriptionId();
                cursorArr[i2] = loadFromEf(i, subscriptionId);
                Rlog.i("IccProvider", "ADN Records loaded for Subscription ::" + subscriptionId);
            }
        }
        return new MergeCursor(cursorArr);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        switch (URL_MATCHER.match(uri)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return "vnd.android.cursor.dir/sim-contact";
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0093 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0094  */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Uri insert(Uri uri, ContentValues contentValues) {
        int defaultSubscriptionId;
        String str;
        ContentValues contentValues2;
        String asString;
        log("insert");
        int match = URL_MATCHER.match(uri);
        int i = 28474;
        if (match == 1) {
            defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        } else if (match == 2) {
            defaultSubscriptionId = getRequestSubId(uri);
        } else {
            if (match == 3) {
                defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
                asString = contentValues.getAsString(STR_PIN2);
            } else if (match == 4) {
                defaultSubscriptionId = getRequestSubId(uri);
                asString = contentValues.getAsString(STR_PIN2);
            } else {
                throw new UnsupportedOperationException("Cannot insert into URL: " + uri);
            }
            str = asString;
            i = 28475;
            String asString2 = contentValues.getAsString(STR_TAG);
            String asString3 = contentValues.getAsString(STR_NUMBER);
            String asString4 = contentValues.getAsString(STR_EMAILS);
            String asString5 = contentValues.getAsString(STR_ANRS);
            contentValues2 = new ContentValues();
            contentValues2.put(STR_NEW_TAG, asString2);
            contentValues2.put(STR_NEW_NUMBER, asString3);
            contentValues2.put(STR_NEW_EMAILS, asString4);
            contentValues2.put(STR_NEW_ANRS, asString5);
            if (updateIccRecordInEf(i, contentValues2, str, defaultSubscriptionId)) {
                return null;
            }
            StringBuilder sb = new StringBuilder("content://icc/");
            if (match == 1) {
                sb.append("adn/");
            } else if (match == 2) {
                sb.append("adn/subId/");
            } else if (match == 3) {
                sb.append("fdn/");
            } else if (match == 4) {
                sb.append("fdn/subId/");
            }
            sb.append(0);
            Uri parse = Uri.parse(sb.toString());
            getContext().getContentResolver().notifyChange(uri, null);
            return parse;
        }
        str = null;
        String asString22 = contentValues.getAsString(STR_TAG);
        String asString32 = contentValues.getAsString(STR_NUMBER);
        String asString42 = contentValues.getAsString(STR_EMAILS);
        String asString52 = contentValues.getAsString(STR_ANRS);
        contentValues2 = new ContentValues();
        contentValues2.put(STR_NEW_TAG, asString22);
        contentValues2.put(STR_NEW_NUMBER, asString32);
        contentValues2.put(STR_NEW_EMAILS, asString42);
        contentValues2.put(STR_NEW_ANRS, asString52);
        if (updateIccRecordInEf(i, contentValues2, str, defaultSubscriptionId)) {
        }
    }

    private String normalizeValue(String str) {
        int length = str.length();
        if (length == 0) {
            log("len of input String is 0");
            return str;
        } else if (str.charAt(0) == '\'') {
            int i = length - 1;
            return str.charAt(i) == '\'' ? str.substring(1, i) : str;
        } else {
            return str;
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int defaultSubscriptionId;
        int match = URL_MATCHER.match(uri);
        int i = 28474;
        if (match == 1) {
            defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        } else if (match != 2) {
            i = IccConstants.EF_FDN;
            if (match == 3) {
                defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
            } else if (match == 4) {
                defaultSubscriptionId = getRequestSubId(uri);
            } else {
                throw new UnsupportedOperationException("Cannot insert into URL: " + uri);
            }
        } else {
            defaultSubscriptionId = getRequestSubId(uri);
        }
        log("delete");
        String[] split = str.split(" AND ");
        int length = split.length;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        while (true) {
            int i2 = length - 1;
            if (i2 < 0) {
                break;
            }
            String str7 = split[i2];
            String[] strArr2 = split;
            log("parsing '" + str7 + "'");
            String[] split2 = str7.split("=", 2);
            if (split2.length != 2) {
                Rlog.e("IccProvider", "resolve: bad whereClause parameter: " + str7);
            } else {
                String trim = split2[0].trim();
                String trim2 = split2[1].trim();
                if (STR_TAG.equals(trim)) {
                    str2 = normalizeValue(trim2);
                } else if (STR_NUMBER.equals(trim)) {
                    str3 = normalizeValue(trim2);
                } else if (STR_EMAILS.equals(trim)) {
                    str4 = normalizeValue(trim2);
                } else if (STR_ANRS.equals(trim)) {
                    str5 = normalizeValue(trim2);
                } else if (STR_PIN2.equals(trim)) {
                    str6 = normalizeValue(trim2);
                }
            }
            split = strArr2;
            length = i2;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(STR_TAG, str2);
        contentValues.put(STR_NUMBER, str3);
        contentValues.put(STR_EMAILS, str4);
        contentValues.put(STR_ANRS, str5);
        if (i == 3 && TextUtils.isEmpty(str6)) {
            return 0;
        }
        log("delete mvalues= " + contentValues);
        if (updateIccRecordInEf(i, contentValues, str6, defaultSubscriptionId)) {
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0059 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005b  */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int defaultSubscriptionId;
        String str2;
        String asString;
        log("update");
        int match = URL_MATCHER.match(uri);
        int i = 28474;
        if (match == 1) {
            defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        } else if (match == 2) {
            defaultSubscriptionId = getRequestSubId(uri);
        } else {
            if (match == 3) {
                defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
                asString = contentValues.getAsString(STR_PIN2);
            } else if (match == 4) {
                defaultSubscriptionId = getRequestSubId(uri);
                asString = contentValues.getAsString(STR_PIN2);
            } else {
                throw new UnsupportedOperationException("Cannot insert into URL: " + uri);
            }
            str2 = asString;
            i = 28475;
            if (updateIccRecordInEf(i, contentValues, str2, defaultSubscriptionId)) {
                return 0;
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        str2 = null;
        if (updateIccRecordInEf(i, contentValues, str2, defaultSubscriptionId)) {
        }
    }

    private MatrixCursor loadFromEf(int i, int i2) {
        log("loadFromEf: efType=0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT) + ", subscription=" + i2);
        List<AdnRecord> list = null;
        try {
            IIccPhoneBook asInterface = IIccPhoneBook.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getIccPhoneBookServiceRegisterer().get());
            if (asInterface != null) {
                list = asInterface.getAdnRecordsInEfForSubscriber(i2, i);
            }
        } catch (RemoteException unused) {
        } catch (SecurityException e) {
            log(e.toString());
        }
        if (list != null) {
            int size = list.size();
            MatrixCursor matrixCursor = new MatrixCursor(ADDRESS_BOOK_COLUMN_NAMES, size);
            log("adnRecords.size=" + size);
            for (int i3 = 0; i3 < size; i3++) {
                loadRecord(list.get(i3), matrixCursor, i3);
            }
            return matrixCursor;
        }
        Rlog.w("IccProvider", "Cannot load ADN records");
        return new MatrixCursor(ADDRESS_BOOK_COLUMN_NAMES);
    }

    private boolean updateIccRecordInEf(int i, ContentValues contentValues, String str, int i2) {
        log("updateIccRecordInEf: efType=" + i + ", values: [ " + contentValues + "  ], subId:" + i2);
        boolean z = false;
        try {
            IIccPhoneBook asInterface = IIccPhoneBook.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getIccPhoneBookServiceRegisterer().get());
            if (asInterface != null) {
                z = asInterface.updateAdnRecordsInEfBySearchForSubscriber(i2, i, contentValues, str);
            }
        } catch (RemoteException unused) {
        } catch (SecurityException e) {
            log(e.toString());
        }
        log("updateIccRecordInEf: " + z);
        return z;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void loadRecord(AdnRecord adnRecord, MatrixCursor matrixCursor, int i) {
        if (adnRecord.isEmpty()) {
            return;
        }
        Object[] objArr = new Object[5];
        String alphaTag = adnRecord.getAlphaTag();
        String number = adnRecord.getNumber();
        log("loadRecord: " + alphaTag + ", " + Rlog.pii("IccProvider", number));
        objArr[0] = alphaTag;
        objArr[1] = number;
        String[] emails = adnRecord.getEmails();
        if (emails != null) {
            StringBuilder sb = new StringBuilder();
            for (String str : emails) {
                log("Adding email:" + Rlog.pii("IccProvider", str));
                sb.append(str);
                sb.append(",");
            }
            objArr[2] = sb.toString();
        }
        String[] additionalNumbers = adnRecord.getAdditionalNumbers();
        if (additionalNumbers != null) {
            StringBuilder sb2 = new StringBuilder();
            for (String str2 : additionalNumbers) {
                log("Adding anr:" + str2);
                sb2.append(str2);
                sb2.append(":");
            }
            objArr[3] = sb2.toString();
        }
        objArr[4] = Integer.valueOf(i);
        matrixCursor.addRow(objArr);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void log(String str) {
        Rlog.d("IccProvider", "[IccProvider] " + str);
    }

    private int getRequestSubId(Uri uri) {
        log("getRequestSubId url: " + uri);
        try {
            return Integer.parseInt(uri.getLastPathSegment());
        } catch (NumberFormatException unused) {
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }
}
