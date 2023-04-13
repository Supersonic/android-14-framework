package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.service.carrier.CarrierIdentifier;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.metrics.CarrierIdMatchStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class CarrierResolver extends Handler {
    private static final Uri CONTENT_URL_PREFER_APN;
    private static final String LOG_TAG;
    private static final boolean VDBG;
    private final BroadcastReceiver mCarrierIdTestReceiver;
    private Integer mCarrierListVersion;
    private String mCarrierName;
    private final ContentObserver mContentObserver;
    private Context mContext;
    private IccRecords mIccRecords;
    private Phone mPhone;
    private final PhoneStateListener mPhoneStateListener;
    private String mPreferApn;
    private String mSpecificCarrierName;
    private final TelephonyManager mTelephonyMgr;
    private String mTestOverrideApn;
    private String mTestOverrideCarrierPriviledgeRule;
    private List<CarrierMatchingRule> mCarrierMatchingRulesOnMccMnc = new ArrayList();
    private int mCarrierId = -1;
    private int mSpecificCarrierId = -1;
    private int mMnoCarrierId = -1;
    private String mSpn = PhoneConfigurationManager.SSSS;
    private final LocalLog mCarrierIdLocalLog = new LocalLog(16);

    static {
        String simpleName = CarrierResolver.class.getSimpleName();
        LOG_TAG = simpleName;
        VDBG = Rlog.isLoggable(simpleName, 2);
        CONTENT_URL_PREFER_APN = Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "preferapn");
    }

    public CarrierResolver(Phone phone) {
        ContentObserver contentObserver = new ContentObserver(this) { // from class: com.android.internal.telephony.CarrierResolver.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                if (Telephony.Carriers.CONTENT_URI.equals(uri)) {
                    CarrierResolver.logd("onChange URI: " + uri);
                    CarrierResolver.this.sendEmptyMessage(3);
                } else if (Telephony.CarrierId.All.CONTENT_URI.equals(uri)) {
                    CarrierResolver.logd("onChange URI: " + uri);
                    CarrierResolver.this.sendEmptyMessage(4);
                }
            }
        };
        this.mContentObserver = contentObserver;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.CarrierResolver.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int phoneId = CarrierResolver.this.mPhone.getPhoneId();
                int intExtra = intent.getIntExtra("override_carrier_id", -1);
                int intExtra2 = intent.getIntExtra("override_specific_carrier_id", intExtra);
                int intExtra3 = intent.getIntExtra("override_mno_carrier_id", intExtra);
                String stringExtra = intent.getStringExtra("override_carrier_name");
                String stringExtra2 = intent.getStringExtra("override_specific_carrier_name");
                int intExtra4 = intent.getIntExtra("sub_id", SubscriptionManager.getDefaultSubscriptionId());
                if (intExtra <= 0) {
                    CarrierResolver.logd("Override carrier id must be greater than 0.", phoneId);
                } else if (intExtra4 != CarrierResolver.this.mPhone.getSubId()) {
                    CarrierResolver.logd("Override carrier id failed. The sub id doesn't same as phone's sub id.", phoneId);
                } else {
                    CarrierResolver.logd("Override carrier id to: " + intExtra, phoneId);
                    CarrierResolver.logd("Override specific carrier id to: " + intExtra2, phoneId);
                    CarrierResolver.logd("Override mno carrier id to: " + intExtra3, phoneId);
                    CarrierResolver.logd("Override carrier name to: " + stringExtra, phoneId);
                    CarrierResolver.logd("Override specific carrier name to: " + stringExtra2, phoneId);
                    CarrierResolver.this.updateCarrierIdAndName(intExtra, stringExtra != null ? stringExtra : PhoneConfigurationManager.SSSS, intExtra2, stringExtra2 != null ? stringExtra : PhoneConfigurationManager.SSSS, intExtra3, false);
                }
            }
        };
        this.mCarrierIdTestReceiver = broadcastReceiver;
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.android.internal.telephony.CarrierResolver.3
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str) {
            }
        };
        logd("Creating CarrierResolver[" + phone.getPhoneId() + "]");
        Context context = phone.getContext();
        this.mContext = context;
        this.mPhone = phone;
        this.mTelephonyMgr = TelephonyManager.from(context);
        this.mContext.getContentResolver().registerContentObserver(CONTENT_URL_PREFER_APN, false, contentObserver);
        this.mContext.getContentResolver().registerContentObserver(Telephony.CarrierId.All.CONTENT_URI, false, contentObserver);
        UiccController.getInstance().registerForIccChanged(this, 2, null);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.internal.telephony.ACTION_TEST_OVERRIDE_CARRIER_ID");
            this.mContext.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void resolveSubscriptionCarrierId(String str) {
        logd("[resolveSubscriptionCarrierId] simState: " + str);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2044189691:
                if (str.equals("LOADED")) {
                    c = 0;
                    break;
                }
                break;
            case -1830845986:
                if (str.equals("CARD_IO_ERROR")) {
                    c = 1;
                    break;
                }
                break;
            case 1924388665:
                if (str.equals("ABSENT")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                handleSimLoaded(false);
                return;
            case 1:
            case 2:
                handleSimAbsent();
                return;
            default:
                return;
        }
    }

    private void handleSimLoaded(boolean z) {
        IccRecords iccRecords = this.mIccRecords;
        if (iccRecords != null) {
            this.mSpn = iccRecords.getServiceProviderName() == null ? PhoneConfigurationManager.SSSS : this.mIccRecords.getServiceProviderName();
        } else {
            loge("mIccRecords is null on SIM_LOAD_EVENT, could not get SPN");
        }
        this.mPreferApn = getPreferApn();
        loadCarrierMatchingRulesOnMccMnc(false, z);
    }

    private void handleSimAbsent() {
        this.mCarrierMatchingRulesOnMccMnc.clear();
        this.mSpn = null;
        this.mPreferApn = null;
        updateCarrierIdAndName(-1, null, -1, null, -1, false);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        logd("handleMessage: " + message.what);
        int i = message.what;
        boolean z = false;
        if (i == 1) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult != null) {
                Object obj = asyncResult.userObj;
                if ((obj instanceof Boolean) && ((Boolean) obj).booleanValue()) {
                    z = true;
                }
            }
            handleSimLoaded(z);
        } else if (i == 2) {
            IccRecords iccRecords = UiccController.getInstance().getIccRecords(this.mPhone.getPhoneId(), 1);
            IccRecords iccRecords2 = this.mIccRecords;
            if (iccRecords2 != iccRecords) {
                if (iccRecords2 != null) {
                    logd("Removing stale icc objects.");
                    this.mIccRecords.unregisterForRecordsOverride(this);
                    this.mIccRecords = null;
                }
                if (iccRecords != null) {
                    logd("new Icc object");
                    iccRecords.registerForRecordsOverride(this, 1, Boolean.TRUE);
                    this.mIccRecords = iccRecords;
                }
            }
        } else if (i != 3) {
            if (i == 4) {
                this.mCarrierListVersion = null;
                loadCarrierMatchingRulesOnMccMnc(true, false);
                return;
            }
            loge("invalid msg: " + message.what);
        } else {
            String preferApn = getPreferApn();
            if (equals(this.mPreferApn, preferApn, true)) {
                return;
            }
            logd("[updatePreferApn] from:" + this.mPreferApn + " to:" + preferApn);
            this.mPreferApn = preferApn;
            matchSubscriptionCarrier(true, false);
        }
    }

    private void loadCarrierMatchingRulesOnMccMnc(boolean z, boolean z2) {
        try {
            String simOperatorNumericForPhone = this.mTelephonyMgr.getSimOperatorNumericForPhone(this.mPhone.getPhoneId());
            Cursor query = this.mContext.getContentResolver().query(Telephony.CarrierId.All.CONTENT_URI, null, "mccmnc=?", new String[]{simOperatorNumericForPhone}, null);
            if (query != null) {
                if (VDBG) {
                    logd("[loadCarrierMatchingRules]- " + query.getCount() + " Records(s) in DB mccmnc: " + simOperatorNumericForPhone);
                }
                this.mCarrierMatchingRulesOnMccMnc.clear();
                while (query.moveToNext()) {
                    this.mCarrierMatchingRulesOnMccMnc.add(makeCarrierMatchingRule(query));
                }
                matchSubscriptionCarrier(z, z2);
                CarrierIdMatchStats.sendCarrierIdTableVersion(getCarrierListVersion());
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            loge("[loadCarrierMatchingRules]- ex: " + e);
        }
    }

    private String getCarrierNameFromId(int i) {
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            Uri uri = Telephony.CarrierId.All.CONTENT_URI;
            Cursor query = contentResolver.query(uri, null, "carrier_id=?", new String[]{i + PhoneConfigurationManager.SSSS}, null);
            if (query != null) {
                if (VDBG) {
                    logd("[getCarrierNameFromId]- " + query.getCount() + " Records(s) in DB cid: " + i);
                }
                if (query.moveToNext()) {
                    String string = query.getString(query.getColumnIndex("carrier_name"));
                    query.close();
                    return string;
                }
            }
            if (query != null) {
                query.close();
                return null;
            }
            return null;
        } catch (Exception e) {
            loge("[getCarrierNameFromId]- ex: " + e);
            return null;
        }
    }

    private static List<CarrierMatchingRule> getCarrierMatchingRulesFromMccMnc(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        try {
            Cursor query = context.getContentResolver().query(Telephony.CarrierId.All.CONTENT_URI, null, "mccmnc=?", new String[]{str}, null);
            if (query != null) {
                if (VDBG) {
                    logd("[loadCarrierMatchingRules]- " + query.getCount() + " Records(s) in DB mccmnc: " + str);
                }
                arrayList.clear();
                while (query.moveToNext()) {
                    arrayList.add(makeCarrierMatchingRule(query));
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            loge("[loadCarrierMatchingRules]- ex: " + e);
        }
        return arrayList;
    }

    private String getPreferApn() {
        if (!TextUtils.isEmpty(this.mTestOverrideApn)) {
            logd("[getPreferApn]- " + this.mTestOverrideApn + " test override");
            return this.mTestOverrideApn;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = Telephony.Carriers.CONTENT_URI;
        Cursor query = contentResolver.query(Uri.withAppendedPath(uri, "preferapn/subId/" + this.mPhone.getSubId()), new String[]{"apn"}, null, null, null);
        if (query != null) {
            try {
                try {
                    if (VDBG) {
                        logd("[getPreferApn]- " + query.getCount() + " Records(s) in DB");
                    }
                    if (query.moveToNext()) {
                        String string = query.getString(query.getColumnIndexOrThrow("apn"));
                        logd("[getPreferApn]- " + string);
                        return string;
                    }
                } catch (Exception e) {
                    loge("[getPreferApn]- exception: " + e);
                }
            } finally {
                query.close();
            }
        }
        if (query == null) {
            return null;
        }
        return null;
    }

    private boolean isPreferApnUserEdited(String str) {
        Cursor query;
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            Uri uri = Telephony.Carriers.CONTENT_URI;
            query = contentResolver.query(Uri.withAppendedPath(uri, "preferapn/subId/" + this.mPhone.getSubId()), new String[]{"edited"}, "apn=?", new String[]{str}, null);
        } catch (Exception e) {
            loge("[isPreferApnUserEdited]- exception: " + e);
        }
        if (query == null || !query.moveToFirst()) {
            if (query != null) {
                query.close();
            }
            return false;
        }
        boolean z = query.getInt(query.getColumnIndexOrThrow("edited")) == 1;
        query.close();
        return z;
    }

    public void setTestOverrideApn(String str) {
        logd("[setTestOverrideApn]: " + str);
        this.mTestOverrideApn = str;
    }

    public void setTestOverrideCarrierPriviledgeRule(String str) {
        logd("[setTestOverrideCarrierPriviledgeRule]: " + str);
        this.mTestOverrideCarrierPriviledgeRule = str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCarrierIdAndName(int i, String str, int i2, String str2, int i3, boolean z) {
        boolean z2;
        boolean z3 = true;
        boolean z4 = false;
        if (i2 != this.mSpecificCarrierId) {
            logd("[updateSpecificCarrierId] from:" + this.mSpecificCarrierId + " to:" + i2);
            this.mSpecificCarrierId = i2;
            z2 = true;
        } else {
            z2 = false;
        }
        if (str2 != this.mSpecificCarrierName) {
            logd("[updateSpecificCarrierName] from:" + this.mSpecificCarrierName + " to:" + str2);
            this.mSpecificCarrierName = str2;
            z2 = true;
        }
        if (z2) {
            this.mCarrierIdLocalLog.log("[updateSpecificCarrierIdAndName] cid:" + this.mSpecificCarrierId + " name:" + this.mSpecificCarrierName);
            Intent intent = new Intent("android.telephony.action.SUBSCRIPTION_SPECIFIC_CARRIER_IDENTITY_CHANGED");
            intent.putExtra("android.telephony.extra.SPECIFIC_CARRIER_ID", this.mSpecificCarrierId);
            intent.putExtra("android.telephony.extra.SPECIFIC_CARRIER_NAME", this.mSpecificCarrierName);
            intent.putExtra("android.telephony.extra.SUBSCRIPTION_ID", this.mPhone.getSubId());
            this.mContext.sendBroadcast(intent);
            ContentValues contentValues = new ContentValues();
            contentValues.put("specific_carrier_id", Integer.valueOf(this.mSpecificCarrierId));
            contentValues.put("specific_carrier_id_name", this.mSpecificCarrierName);
            this.mContext.getContentResolver().update(Telephony.CarrierId.getSpecificCarrierIdUriForSubscriptionId(this.mPhone.getSubId()), contentValues, null, null);
        }
        if (!equals(str, this.mCarrierName, true)) {
            logd("[updateCarrierName] from:" + this.mCarrierName + " to:" + str);
            this.mCarrierName = str;
            z4 = true;
        }
        if (i != this.mCarrierId) {
            logd("[updateCarrierId] from:" + this.mCarrierId + " to:" + i);
            this.mCarrierId = i;
            z4 = true;
        }
        if (i3 != this.mMnoCarrierId) {
            logd("[updateMnoCarrierId] from:" + this.mMnoCarrierId + " to:" + i3);
            this.mMnoCarrierId = i3;
        } else {
            z3 = z4;
        }
        if (z3) {
            this.mCarrierIdLocalLog.log("[updateCarrierIdAndName] cid:" + this.mCarrierId + " name:" + this.mCarrierName + " mnoCid:" + this.mMnoCarrierId);
            Intent intent2 = new Intent("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED");
            intent2.putExtra("android.telephony.extra.CARRIER_ID", this.mCarrierId);
            intent2.putExtra("android.telephony.extra.CARRIER_NAME", this.mCarrierName);
            intent2.putExtra("android.telephony.extra.SUBSCRIPTION_ID", this.mPhone.getSubId());
            this.mContext.sendBroadcast(intent2);
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("carrier_id", Integer.valueOf(this.mCarrierId));
            contentValues2.put("carrier_name", this.mCarrierName);
            this.mContext.getContentResolver().update(Telephony.CarrierId.getUriForSubscriptionId(this.mPhone.getSubId()), contentValues2, null, null);
        }
        if (!SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId()) || z) {
            return;
        }
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionManagerService.getInstance().setCarrierId(this.mPhone.getSubId(), this.mCarrierId);
        } else {
            SubscriptionController.getInstance().setCarrierId(this.mCarrierId, this.mPhone.getSubId());
        }
    }

    private static CarrierMatchingRule makeCarrierMatchingRule(Cursor cursor) {
        String string = cursor.getString(cursor.getColumnIndexOrThrow("privilege_access_rule"));
        return new CarrierMatchingRule(cursor.getString(cursor.getColumnIndexOrThrow("mccmnc")), cursor.getString(cursor.getColumnIndexOrThrow("imsi_prefix_xpattern")), cursor.getString(cursor.getColumnIndexOrThrow("iccid_prefix")), cursor.getString(cursor.getColumnIndexOrThrow("gid1")), cursor.getString(cursor.getColumnIndexOrThrow("gid2")), cursor.getString(cursor.getColumnIndexOrThrow("plmn")), cursor.getString(cursor.getColumnIndexOrThrow("spn")), cursor.getString(cursor.getColumnIndexOrThrow("apn")), TextUtils.isEmpty(string) ? null : new ArrayList(Arrays.asList(string)), cursor.getInt(cursor.getColumnIndexOrThrow("carrier_id")), cursor.getString(cursor.getColumnIndexOrThrow("carrier_name")), cursor.getInt(cursor.getColumnIndexOrThrow("parent_carrier_id")));
    }

    /* loaded from: classes.dex */
    public static class CarrierMatchingRule {
        public final String apn;
        public final String gid1;
        public final String gid2;
        public final String iccidPrefix;
        public final String imsiPrefixPattern;
        private int mCid;
        private String mName;
        private int mParentCid;
        private int mScore;
        public final String mccMnc;
        public final String plmn;
        public final List<String> privilegeAccessRule;
        public final String spn;

        @VisibleForTesting
        public CarrierMatchingRule(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, List<String> list, int i, String str9, int i2) {
            this.mScore = 0;
            this.mccMnc = str;
            this.imsiPrefixPattern = str2;
            this.iccidPrefix = str3;
            this.gid1 = str4;
            this.gid2 = str5;
            this.plmn = str6;
            this.spn = str7;
            this.apn = str8;
            this.privilegeAccessRule = list;
            this.mCid = i;
            this.mName = str9;
            this.mParentCid = i2;
        }

        private CarrierMatchingRule(CarrierMatchingRule carrierMatchingRule) {
            this.mScore = 0;
            this.mccMnc = carrierMatchingRule.mccMnc;
            this.imsiPrefixPattern = carrierMatchingRule.imsiPrefixPattern;
            this.iccidPrefix = carrierMatchingRule.iccidPrefix;
            this.gid1 = carrierMatchingRule.gid1;
            this.gid2 = carrierMatchingRule.gid2;
            this.plmn = carrierMatchingRule.plmn;
            this.spn = carrierMatchingRule.spn;
            this.apn = carrierMatchingRule.apn;
            this.privilegeAccessRule = carrierMatchingRule.privilegeAccessRule;
            this.mCid = carrierMatchingRule.mCid;
            this.mName = carrierMatchingRule.mName;
            this.mParentCid = carrierMatchingRule.mParentCid;
        }

        public void match(CarrierMatchingRule carrierMatchingRule) {
            this.mScore = 0;
            String str = this.mccMnc;
            if (str != null) {
                if (!CarrierResolver.equals(carrierMatchingRule.mccMnc, str, false)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += CallFailCause.RADIO_UPLINK_FAILURE;
            }
            String str2 = this.imsiPrefixPattern;
            if (str2 != null) {
                if (!imsiPrefixMatch(carrierMatchingRule.imsiPrefixPattern, str2)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 128;
            }
            String str3 = this.iccidPrefix;
            if (str3 != null) {
                if (!iccidPrefixMatch(carrierMatchingRule.iccidPrefix, str3)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 64;
            }
            String str4 = this.gid1;
            if (str4 != null) {
                if (!gidMatch(carrierMatchingRule.gid1, str4)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 32;
            }
            String str5 = this.gid2;
            if (str5 != null) {
                if (!gidMatch(carrierMatchingRule.gid2, str5)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 16;
            }
            String str6 = this.plmn;
            if (str6 != null) {
                if (!CarrierResolver.equals(carrierMatchingRule.plmn, str6, true)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 8;
            }
            String str7 = this.spn;
            if (str7 != null) {
                if (!CarrierResolver.equals(carrierMatchingRule.spn, str7, true)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 2;
            }
            List<String> list = this.privilegeAccessRule;
            if (list != null && !list.isEmpty()) {
                if (!carrierPrivilegeRulesMatch(carrierMatchingRule.privilegeAccessRule, this.privilegeAccessRule)) {
                    this.mScore = -1;
                    return;
                }
                this.mScore += 4;
            }
            String str8 = this.apn;
            if (str8 != null) {
                if (!CarrierResolver.equals(carrierMatchingRule.apn, str8, true)) {
                    this.mScore = -1;
                } else {
                    this.mScore++;
                }
            }
        }

        private boolean imsiPrefixMatch(String str, String str2) {
            if (TextUtils.isEmpty(str2)) {
                return true;
            }
            if (!TextUtils.isEmpty(str) && str.length() >= str2.length()) {
                for (int i = 0; i < str2.length(); i++) {
                    if (str2.charAt(i) != 'x' && str2.charAt(i) != 'X' && str2.charAt(i) != str.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        private boolean iccidPrefixMatch(String str, String str2) {
            if (str == null || str2 == null) {
                return false;
            }
            return str.startsWith(str2);
        }

        private boolean gidMatch(String str, String str2) {
            if (str != null) {
                Locale locale = Locale.ROOT;
                if (str.toLowerCase(locale).startsWith(str2.toLowerCase(locale))) {
                    return true;
                }
            }
            return false;
        }

        private boolean carrierPrivilegeRulesMatch(List<String> list, List<String> list2) {
            if (list == null || list.isEmpty()) {
                return false;
            }
            for (String str : list2) {
                for (String str2 : list) {
                    if (!TextUtils.isEmpty(str) && str.equalsIgnoreCase(str2)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String toString() {
            return "[CarrierMatchingRule] - mccmnc: " + this.mccMnc + " gid1: " + this.gid1 + " gid2: " + this.gid2 + " plmn: " + this.plmn + " imsi_prefix: " + this.imsiPrefixPattern + " iccid_prefix" + this.iccidPrefix + " spn: " + this.spn + " privilege_access_rule: " + this.privilegeAccessRule + " apn: " + this.apn + " name: " + this.mName + " cid: " + this.mCid + " score: " + this.mScore;
        }
    }

    private CarrierMatchingRule getSubscriptionMatchingRule() {
        ArrayList certsFromCarrierPrivilegeAccessRules;
        String simOperatorNumericForPhone = this.mTelephonyMgr.getSimOperatorNumericForPhone(this.mPhone.getPhoneId());
        String iccSerialNumber = this.mPhone.getIccSerialNumber();
        String groupIdLevel1 = this.mPhone.getGroupIdLevel1();
        String groupIdLevel2 = this.mPhone.getGroupIdLevel2();
        String subscriberId = this.mPhone.getSubscriberId();
        String plmn = this.mPhone.getPlmn();
        String str = this.mSpn;
        String str2 = this.mPreferApn;
        if (!TextUtils.isEmpty(this.mTestOverrideCarrierPriviledgeRule)) {
            certsFromCarrierPrivilegeAccessRules = new ArrayList(Arrays.asList(this.mTestOverrideCarrierPriviledgeRule));
        } else {
            certsFromCarrierPrivilegeAccessRules = this.mTelephonyMgr.createForSubscriptionId(this.mPhone.getSubId()).getCertsFromCarrierPrivilegeAccessRules();
        }
        if (VDBG) {
            StringBuilder sb = new StringBuilder();
            sb.append("[matchSubscriptionCarrier] mnnmnc:");
            sb.append(simOperatorNumericForPhone);
            sb.append(" gid1: ");
            sb.append(groupIdLevel1);
            sb.append(" gid2: ");
            sb.append(groupIdLevel2);
            sb.append(" imsi: ");
            String str3 = LOG_TAG;
            sb.append(Rlog.pii(str3, subscriberId));
            sb.append(" iccid: ");
            sb.append(Rlog.pii(str3, iccSerialNumber));
            sb.append(" plmn: ");
            sb.append(plmn);
            sb.append(" spn: ");
            sb.append(str);
            sb.append(" apn: ");
            sb.append(str2);
            sb.append(" accessRules: ");
            sb.append(certsFromCarrierPrivilegeAccessRules != null ? certsFromCarrierPrivilegeAccessRules : null);
            logd(sb.toString());
        }
        return new CarrierMatchingRule(simOperatorNumericForPhone, subscriberId, iccSerialNumber, groupIdLevel1, groupIdLevel2, plmn, str, str2, certsFromCarrierPrivilegeAccessRules, -1, null, -1);
    }

    private void updateCarrierConfig() {
        IccCard iccCard = this.mPhone.getIccCard();
        IccCardConstants.State state = IccCardConstants.State.UNKNOWN;
        if (iccCard != null) {
            state = iccCard.getState();
        }
        ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).updateConfigForPhoneId(this.mPhone.getPhoneId(), UiccController.getIccStateIntentString(state));
    }

    private void matchSubscriptionCarrier(boolean z, boolean z2) {
        String str;
        if (!SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId())) {
            logd("[matchSubscriptionCarrier]skip before sim records loaded");
            return;
        }
        CarrierMatchingRule subscriptionMatchingRule = getSubscriptionMatchingRule();
        String str2 = null;
        int i = -1;
        CarrierMatchingRule carrierMatchingRule = null;
        CarrierMatchingRule carrierMatchingRule2 = null;
        CarrierMatchingRule carrierMatchingRule3 = null;
        for (CarrierMatchingRule carrierMatchingRule4 : this.mCarrierMatchingRulesOnMccMnc) {
            carrierMatchingRule4.match(subscriptionMatchingRule);
            if (carrierMatchingRule4.mScore > i) {
                i = carrierMatchingRule4.mScore;
                carrierMatchingRule = carrierMatchingRule4;
                carrierMatchingRule2 = carrierMatchingRule;
            } else if (i > -1 && carrierMatchingRule4.mScore == i) {
                if (carrierMatchingRule4.mParentCid == carrierMatchingRule.mCid) {
                    carrierMatchingRule = carrierMatchingRule4;
                } else if (carrierMatchingRule.mParentCid == carrierMatchingRule4.mCid) {
                    carrierMatchingRule2 = carrierMatchingRule4;
                }
            }
            if (carrierMatchingRule4.mScore == 256) {
                carrierMatchingRule3 = carrierMatchingRule4;
            }
        }
        if (i == -1) {
            logd("[matchSubscriptionCarrier - no match] cid: -1 name: " + ((Object) null));
            updateCarrierIdAndName(-1, null, -1, null, -1, z2);
        } else {
            if (carrierMatchingRule == carrierMatchingRule2 && carrierMatchingRule.mParentCid != -1) {
                carrierMatchingRule2 = new CarrierMatchingRule(carrierMatchingRule);
                carrierMatchingRule2.mCid = carrierMatchingRule2.mParentCid;
                carrierMatchingRule2.mName = getCarrierNameFromId(carrierMatchingRule2.mCid);
            }
            logd("[matchSubscriptionCarrier] specific cid: " + carrierMatchingRule.mCid + " specific name: " + carrierMatchingRule.mName + " cid: " + carrierMatchingRule2.mCid + " name: " + carrierMatchingRule2.mName);
            updateCarrierIdAndName(carrierMatchingRule2.mCid, carrierMatchingRule2.mName, carrierMatchingRule.mCid, carrierMatchingRule.mName, carrierMatchingRule3 == null ? carrierMatchingRule.mCid : carrierMatchingRule3.mCid, z2);
            if (z) {
                logd("[matchSubscriptionCarrier] - Calling updateCarrierConfig()");
                updateCarrierConfig();
            }
        }
        int i2 = i & 32;
        String str3 = (i2 != 0 || TextUtils.isEmpty(subscriptionMatchingRule.gid1)) ? null : subscriptionMatchingRule.gid1;
        String str4 = ((i == -1 || i2 == 0) && !TextUtils.isEmpty(subscriptionMatchingRule.mccMnc)) ? subscriptionMatchingRule.mccMnc : null;
        String str5 = subscriptionMatchingRule.apn;
        if (str5 != null && !isPreferApnUserEdited(str5)) {
            str2 = subscriptionMatchingRule.apn;
        }
        String str6 = str2;
        String str7 = subscriptionMatchingRule.iccidPrefix;
        String substring = (str7 == null || str7.length() < 7) ? subscriptionMatchingRule.iccidPrefix : subscriptionMatchingRule.iccidPrefix.substring(0, 7);
        String str8 = subscriptionMatchingRule.imsiPrefixPattern;
        if (str8 != null && str8.length() >= 8) {
            str = subscriptionMatchingRule.imsiPrefixPattern.substring(0, 8);
        } else {
            str = subscriptionMatchingRule.imsiPrefixPattern;
        }
        TelephonyMetrics.getInstance().writeCarrierIdMatchingEvent(this.mPhone.getPhoneId(), getCarrierListVersion(), this.mCarrierId, str4, str3, new CarrierMatchingRule(subscriptionMatchingRule.mccMnc, str, substring, subscriptionMatchingRule.gid1, subscriptionMatchingRule.gid2, subscriptionMatchingRule.plmn, subscriptionMatchingRule.spn, str6, subscriptionMatchingRule.privilegeAccessRule, -1, null, -1));
        if (str4 == null && str3 == null) {
            return;
        }
        CarrierIdMatchStats.onCarrierIdMismatch(this.mCarrierId, str4, str3, subscriptionMatchingRule.spn, TextUtils.isEmpty(subscriptionMatchingRule.spn) ? subscriptionMatchingRule.plmn : PhoneConfigurationManager.SSSS);
    }

    public int getCarrierListVersion() {
        if (this.mCarrierListVersion == null) {
            Cursor query = this.mContext.getContentResolver().query(Uri.withAppendedPath(Telephony.CarrierId.All.CONTENT_URI, "get_version"), null, null, null);
            try {
                query.moveToFirst();
                this.mCarrierListVersion = Integer.valueOf(query.getInt(0));
                query.close();
            } catch (Throwable th) {
                if (query != null) {
                    try {
                        query.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        return this.mCarrierListVersion.intValue();
    }

    public int getCarrierId() {
        return this.mCarrierId;
    }

    public int getSpecificCarrierId() {
        return this.mSpecificCarrierId;
    }

    public String getCarrierName() {
        return this.mCarrierName;
    }

    public String getSpecificCarrierName() {
        return this.mSpecificCarrierName;
    }

    public int getMnoCarrierId() {
        return this.mMnoCarrierId;
    }

    public static int getCarrierIdFromIdentifier(Context context, CarrierIdentifier carrierIdentifier) {
        String str = carrierIdentifier.getMcc() + carrierIdentifier.getMnc();
        String gid1 = carrierIdentifier.getGid1();
        String gid2 = carrierIdentifier.getGid2();
        String imsi = carrierIdentifier.getImsi();
        String spn = carrierIdentifier.getSpn();
        if (VDBG) {
            logd("[getCarrierIdFromIdentifier] mnnmnc:" + str + " gid1: " + gid1 + " gid2: " + gid2 + " imsi: " + Rlog.pii(LOG_TAG, imsi) + " spn: " + spn);
        }
        CarrierMatchingRule carrierMatchingRule = new CarrierMatchingRule(str, imsi, null, gid1, gid2, null, spn, null, null, -1, null, -1);
        int i = -1;
        int i2 = -1;
        for (CarrierMatchingRule carrierMatchingRule2 : getCarrierMatchingRulesFromMccMnc(context, carrierMatchingRule.mccMnc)) {
            carrierMatchingRule2.match(carrierMatchingRule);
            if (carrierMatchingRule2.mScore > i2) {
                i2 = carrierMatchingRule2.mScore;
                i = carrierMatchingRule2.mCid;
            }
        }
        return i;
    }

    public static List<Integer> getCarrierIdsFromApnQuery(Context context, String str, String str2, String str3) {
        String str4 = "mccmnc=" + str;
        if ("spn".equals(str2) && str3 != null) {
            str4 = str4 + " AND spn='" + str3 + "'";
        } else if ("imsi".equals(str2) && str3 != null) {
            str4 = str4 + " AND imsi_prefix_xpattern='" + str3 + "'";
        } else if ("gid1".equals(str2) && str3 != null) {
            str4 = str4 + " AND gid1='" + str3 + "'";
        } else if ("gid2".equals(str2) && str3 != null) {
            str4 = str4 + " AND gid2='" + str3 + "'";
        } else {
            logd("mvno case empty or other invalid values");
        }
        ArrayList arrayList = new ArrayList();
        try {
            Cursor query = context.getContentResolver().query(Telephony.CarrierId.All.CONTENT_URI, null, str4, null, null);
            if (query != null) {
                if (VDBG) {
                    logd("[getCarrierIdsFromApnQuery]- " + query.getCount() + " Records(s) in DB");
                }
                while (query.moveToNext()) {
                    int i = query.getInt(query.getColumnIndex("carrier_id"));
                    if (!arrayList.contains(Integer.valueOf(i))) {
                        arrayList.add(Integer.valueOf(i));
                    }
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            loge("[getCarrierIdsFromApnQuery]- ex: " + e);
        }
        logd(str4 + " " + arrayList);
        return arrayList;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x004c, code lost:
        r3.close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getCarrierIdFromMccMnc(Context context, String str) {
        try {
            Cursor cursorForMccMnc = getCursorForMccMnc(context, str);
            if (cursorForMccMnc != null && cursorForMccMnc.moveToNext()) {
                if (VDBG) {
                    logd("[getCarrierIdFromMccMnc]- " + cursorForMccMnc.getCount() + " Records(s) in DB mccmnc: " + str);
                }
                int i = cursorForMccMnc.getInt(cursorForMccMnc.getColumnIndex("carrier_id"));
                cursorForMccMnc.close();
                return i;
            }
            return -1;
        } catch (Exception e) {
            loge("[getCarrierIdFromMccMnc]- ex: " + e);
            return -1;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x004c, code lost:
        r3.close();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String getCarrierNameFromMccMnc(Context context, String str) {
        try {
            Cursor cursorForMccMnc = getCursorForMccMnc(context, str);
            if (cursorForMccMnc != null && cursorForMccMnc.moveToNext()) {
                if (VDBG) {
                    logd("[getCarrierNameFromMccMnc]- " + cursorForMccMnc.getCount() + " Records(s) in DB mccmnc: " + str);
                }
                String string = cursorForMccMnc.getString(cursorForMccMnc.getColumnIndex("carrier_name"));
                cursorForMccMnc.close();
                return string;
            }
            return null;
        } catch (Exception e) {
            loge("[getCarrierNameFromMccMnc]- ex: " + e);
            return null;
        }
    }

    private static Cursor getCursorForMccMnc(Context context, String str) {
        try {
            return context.getContentResolver().query(Telephony.CarrierId.All.CONTENT_URI, null, "mccmnc=? AND gid1 is NULL AND gid2 is NULL AND imsi_prefix_xpattern is NULL AND spn is NULL AND iccid_prefix is NULL AND plmn is NULL AND privilege_access_rule is NULL AND apn is NULL", new String[]{str}, null);
        } catch (Exception e) {
            loge("[getCursorForMccMnc]- ex: " + e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean equals(String str, String str2, boolean z) {
        if (str == null && str2 == null) {
            return true;
        }
        if (str == null || str2 == null) {
            return false;
        }
        return z ? str.equalsIgnoreCase(str2) : str.equals(str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d(LOG_TAG, str);
    }

    private static void loge(String str) {
        Rlog.e(LOG_TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str, int i) {
        Rlog.d(LOG_TAG + "[" + i + "]", str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("mCarrierResolverLocalLogs:");
        indentingPrintWriter.increaseIndent();
        this.mCarrierIdLocalLog.dump(fileDescriptor, printWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("mCarrierId: " + this.mCarrierId);
        indentingPrintWriter.println("mSpecificCarrierId: " + this.mSpecificCarrierId);
        indentingPrintWriter.println("mMnoCarrierId: " + this.mMnoCarrierId);
        indentingPrintWriter.println("mCarrierName: " + this.mCarrierName);
        indentingPrintWriter.println("mSpecificCarrierName: " + this.mSpecificCarrierName);
        indentingPrintWriter.println("carrier_list_version: " + getCarrierListVersion());
        indentingPrintWriter.println("mCarrierMatchingRules on mccmnc: " + this.mTelephonyMgr.getSimOperatorNumericForPhone(this.mPhone.getPhoneId()));
        indentingPrintWriter.increaseIndent();
        for (CarrierMatchingRule carrierMatchingRule : this.mCarrierMatchingRulesOnMccMnc) {
            indentingPrintWriter.println(carrierMatchingRule.toString());
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("mSpn: " + this.mSpn);
        indentingPrintWriter.println("mPreferApn: " + this.mPreferApn);
        indentingPrintWriter.flush();
    }
}
