package com.android.internal.telephony.uicc;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.gsm.SimTlv;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class IccRecords extends Handler implements IccConstants {
    public static final int CALL_FORWARDING_STATUS_DISABLED = 0;
    public static final int CALL_FORWARDING_STATUS_ENABLED = 1;
    public static final int CALL_FORWARDING_STATUS_UNKNOWN = -1;
    public static final int CARRIER_NAME_DISPLAY_CONDITION_BITMASK_PLMN = 1;
    public static final int CARRIER_NAME_DISPLAY_CONDITION_BITMASK_SPN = 2;
    protected static final boolean DBG = true;
    public static final int DEFAULT_CARRIER_NAME_DISPLAY_CONDITION = 0;
    public static final int DEFAULT_VOICE_MESSAGE_COUNT = -2;
    protected static final int EVENT_APP_DETECTED = 260;
    protected static final int EVENT_APP_LOCKED = 258;
    protected static final int EVENT_APP_NETWORK_LOCKED = 259;
    protected static final int EVENT_APP_READY = 257;
    public static final int EVENT_CFI = 1;
    public static final int EVENT_GET_ICC_RECORD_DONE = 100;
    protected static final int EVENT_GET_SMS_RECORD_SIZE_DONE = 28;
    public static final int EVENT_MWI = 0;
    public static final int EVENT_REFRESH = 31;
    public static final int EVENT_SET_SMSS_RECORD_DONE = 201;
    public static final int EVENT_SPN = 2;
    protected static final int HANDLER_ACTION_BASE = 1238272;
    protected static final int HANDLER_ACTION_NONE = 1238272;
    protected static final int HANDLER_ACTION_SEND_RESPONSE = 1238273;
    public static final int INVALID_CARRIER_NAME_DISPLAY_CONDITION_BITMASK = -1;
    protected static final int LOCKED_RECORDS_REQ_REASON_LOCKED = 1;
    protected static final int LOCKED_RECORDS_REQ_REASON_NETWORK_LOCKED = 2;
    protected static final int LOCKED_RECORDS_REQ_REASON_NONE = 0;
    public static final int PLMN_MAX_LENGTH = 6;
    public static final int PLMN_MIN_LENGTH = 5;
    protected static final int SMSS_INVALID_TPMR = -1;
    protected static final int SYSTEM_EVENT_BASE = 256;
    protected static final int UNINITIALIZED = -1;
    protected static final int UNKNOWN = 0;
    public static final int UNKNOWN_VOICE_MESSAGE_COUNT = -1;
    protected AdnRecordCache mAdnCache;
    protected int mCarrierNameDisplayCondition;
    CarrierTestOverride mCarrierTestOverride;
    protected CommandsInterface mCi;
    protected Context mContext;
    protected String[] mEhplmns;
    protected IccFileHandler mFh;
    protected String[] mFplmns;
    protected String mFullIccId;
    protected String mGid1;
    protected String mGid2;
    protected PlmnActRecord[] mHplmnActRecords;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PROTECTED)
    public String mIccId;
    protected String mImsi;
    protected OperatorPlmnInfo[] mOpl;
    protected PlmnActRecord[] mOplmnActRecords;
    protected UiccCardApplication mParentApp;
    protected PlmnActRecord[] mPlmnActRecords;
    protected String mPnnHomeName;
    protected PlmnNetworkName[] mPnns;
    protected String mPrefLang;
    protected String mPsiSmsc;
    protected int mRecordsToLoad;
    protected byte[] mSmssValues;
    protected String[] mSpdi;
    private String mSpn;
    protected TelephonyManager mTelephonyManager;
    protected static final boolean VDBG = Rlog.isLoggable("IccRecords", 2);
    private static final String[] MCCMNC_CODES_HAVING_3DIGITS_MNC = {"302370", "302720", "310260", "405025", "405026", "405027", "405028", "405029", "405030", "405031", "405032", "405033", "405034", "405035", "405036", "405037", "405038", "405039", "405040", "405041", "405042", "405043", "405044", "405045", "405046", "405047", "405750", "405751", "405752", "405753", "405754", "405755", "405756", "405799", "405800", "405801", "405802", "405803", "405804", "405805", "405806", "405807", "405808", "405809", "405810", "405811", "405812", "405813", "405814", "405815", "405816", "405817", "405818", "405819", "405820", "405821", "405822", "405823", "405824", "405825", "405826", "405827", "405828", "405829", "405830", "405831", "405832", "405833", "405834", "405835", "405836", "405837", "405838", "405839", "405840", "405841", "405842", "405843", "405844", "405845", "405846", "405847", "405848", "405849", "405850", "405851", "405852", "405853", "405854", "405855", "405856", "405857", "405858", "405859", "405860", "405861", "405862", "405863", "405864", "405865", "405866", "405867", "405868", "405869", "405870", "405871", "405872", "405873", "405874", "405875", "405876", "405877", "405878", "405879", "405880", "405881", "405882", "405883", "405884", "405885", "405886", "405908", "405909", "405910", "405911", "405912", "405913", "405914", "405915", "405916", "405917", "405918", "405919", "405920", "405921", "405922", "405923", "405924", "405925", "405926", "405927", "405928", "405929", "405930", "405931", "405932", "502142", "502143", "502145", "502146", "502147", "502148"};
    protected static AtomicInteger sNextRequestId = new AtomicInteger(1);
    protected AtomicBoolean mDestroyed = new AtomicBoolean(false);
    protected AtomicBoolean mLoaded = new AtomicBoolean(false);
    protected RegistrantList mRecordsLoadedRegistrants = new RegistrantList();
    protected RegistrantList mLockedRecordsLoadedRegistrants = new RegistrantList();
    protected RegistrantList mNetworkLockedRecordsLoadedRegistrants = new RegistrantList();
    protected RegistrantList mImsiReadyRegistrants = new RegistrantList();
    protected RegistrantList mRecordsEventsRegistrants = new RegistrantList();
    protected RegistrantList mNewSmsRegistrants = new RegistrantList();
    protected RegistrantList mNetworkSelectionModeAutomaticRegistrants = new RegistrantList();
    protected RegistrantList mSpnUpdatedRegistrants = new RegistrantList();
    protected RegistrantList mRecordsOverrideRegistrants = new RegistrantList();
    protected boolean mRecordsRequested = false;
    protected int mLockedRecordsReqReason = 0;
    protected String mMsisdn = null;
    protected String mMsisdnTag = null;
    protected String mNewMsisdn = null;
    protected String mNewMsisdnTag = null;
    protected String mVoiceMailNum = null;
    protected String mVoiceMailTag = null;
    protected String mNewVoiceMailNum = null;
    protected String mNewVoiceMailTag = null;
    protected boolean mIsVoiceMailFixed = false;
    protected int mMncLength = -1;
    protected int mMailboxIndex = 0;
    protected int mSmsCountOnIcc = 0;
    protected final HashMap<Integer, Pair<Message, Object>> mPendingTransactions = new HashMap<>();

    /* loaded from: classes.dex */
    public interface IccRecordLoaded {
        String getEfName();

        void onRecordLoaded(AsyncResult asyncResult);
    }

    public static int convertSpnDisplayConditionToBitmask(int i) {
        int i2 = (i & 1) != 1 ? 0 : 1;
        return (i & 2) == 0 ? i2 | 2 : i2;
    }

    public IsimRecords getIsimRecords() {
        return null;
    }

    public String getNAI() {
        return null;
    }

    public String getOperatorNumeric() {
        return null;
    }

    public UsimServiceTable getUsimServiceTable() {
        return null;
    }

    public int getVoiceCallForwardingFlag() {
        return -1;
    }

    public abstract int getVoiceMessageCount();

    protected abstract void handleFileUpdate(int i);

    public boolean isCspPlmnEnabled() {
        return false;
    }

    public boolean isProvisioned() {
        return true;
    }

    protected abstract void log(String str);

    protected abstract void loge(String str);

    protected abstract void onAllRecordsLoaded();

    protected abstract void onReady();

    protected abstract void onRecordLoaded();

    public abstract void onRefresh(boolean z, int[] iArr);

    public void setVoiceCallForwardingFlag(int i, boolean z, String str) {
    }

    public abstract void setVoiceMailNumber(String str, String str2, Message message);

    public abstract void setVoiceMessageWaiting(int i, int i2);

    /* loaded from: classes.dex */
    private static class AuthAsyncResponse {
        public IccIoResult authRsp;
        public Throwable exception;

        private AuthAsyncResponse() {
        }
    }

    @Override // android.os.Handler
    public String toString() {
        String str;
        String str2;
        String str3;
        String givePrintableIccid = SubscriptionInfo.givePrintableIccid(this.mFullIccId);
        StringBuilder sb = new StringBuilder();
        sb.append("mDestroyed=");
        sb.append(this.mDestroyed);
        sb.append(" mContext=");
        sb.append(this.mContext);
        sb.append(" mCi=");
        sb.append(this.mCi);
        sb.append(" mFh=");
        sb.append(this.mFh);
        sb.append(" mParentApp=");
        sb.append(this.mParentApp);
        sb.append(" recordsToLoad=");
        sb.append(this.mRecordsToLoad);
        sb.append(" adnCache=");
        sb.append(this.mAdnCache);
        sb.append(" recordsRequested=");
        sb.append(this.mRecordsRequested);
        sb.append(" lockedRecordsReqReason=");
        sb.append(this.mLockedRecordsReqReason);
        sb.append(" iccid=");
        sb.append(givePrintableIccid);
        boolean isInTestMode = this.mCarrierTestOverride.isInTestMode();
        String str4 = PhoneConfigurationManager.SSSS;
        if (isInTestMode) {
            str = "mFakeIccid=" + this.mCarrierTestOverride.getFakeIccid();
        } else {
            str = PhoneConfigurationManager.SSSS;
        }
        sb.append(str);
        sb.append(" msisdnTag=");
        sb.append(this.mMsisdnTag);
        sb.append(" voiceMailNum=");
        boolean z = VDBG;
        sb.append(Rlog.pii(z, this.mVoiceMailNum));
        sb.append(" voiceMailTag=");
        sb.append(this.mVoiceMailTag);
        sb.append(" voiceMailNum=");
        sb.append(Rlog.pii(z, this.mNewVoiceMailNum));
        sb.append(" newVoiceMailTag=");
        sb.append(this.mNewVoiceMailTag);
        sb.append(" isVoiceMailFixed=");
        sb.append(this.mIsVoiceMailFixed);
        sb.append(" mImsi=");
        if (this.mImsi != null) {
            str2 = this.mImsi.substring(0, 6) + Rlog.pii(z, this.mImsi.substring(6));
        } else {
            str2 = "null";
        }
        sb.append(str2);
        if (this.mCarrierTestOverride.isInTestMode()) {
            str3 = " mFakeImsi=" + this.mCarrierTestOverride.getFakeIMSI();
        } else {
            str3 = PhoneConfigurationManager.SSSS;
        }
        sb.append(str3);
        sb.append(" mncLength=");
        sb.append(this.mMncLength);
        sb.append(" mailboxIndex=");
        sb.append(this.mMailboxIndex);
        sb.append(" spn=");
        sb.append(this.mSpn);
        if (this.mCarrierTestOverride.isInTestMode()) {
            str4 = " mFakeSpn=" + this.mCarrierTestOverride.getFakeSpn();
        }
        sb.append(str4);
        return sb.toString();
    }

    public IccRecords(UiccCardApplication uiccCardApplication, Context context, CommandsInterface commandsInterface) {
        this.mContext = context;
        this.mCi = commandsInterface;
        this.mFh = uiccCardApplication.getIccFileHandler();
        this.mParentApp = uiccCardApplication;
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mCarrierTestOverride = new CarrierTestOverride(this.mParentApp.getPhoneId());
        this.mCi.registerForIccRefresh(this, 31, null);
        this.mParentApp.registerForReady(this, 257, null);
        this.mParentApp.registerForDetected(this, 260, null);
        this.mParentApp.registerForLocked(this, 258, null);
        this.mParentApp.registerForNetworkLocked(this, 259, null);
    }

    public void setCarrierTestOverride(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.mCarrierTestOverride.override(str, str2, str3, str4, str5, str6, str7);
        this.mTelephonyManager.setSimOperatorNameForPhone(this.mParentApp.getPhoneId(), str7);
        this.mTelephonyManager.setSimOperatorNumericForPhone(this.mParentApp.getPhoneId(), str);
        this.mRecordsOverrideRegistrants.notifyRegistrants();
    }

    public void dispose() {
        this.mDestroyed.set(true);
        this.mCi.unregisterForIccRefresh(this);
        this.mParentApp.unregisterForReady(this);
        this.mParentApp.unregisterForDetected(this);
        this.mParentApp.unregisterForLocked(this);
        this.mParentApp.unregisterForNetworkLocked(this);
        this.mParentApp = null;
        this.mFh = null;
        this.mCi = null;
        this.mContext = null;
        AdnRecordCache adnRecordCache = this.mAdnCache;
        if (adnRecordCache != null) {
            adnRecordCache.reset();
        }
        this.mLoaded.set(false);
    }

    protected void onDetected() {
        this.mRecordsRequested = false;
        this.mLoaded.set(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onLocked() {
        this.mRecordsRequested = false;
        this.mLoaded.set(false);
    }

    public AdnRecordCache getAdnCache() {
        return this.mAdnCache;
    }

    public int storePendingTransaction(Message message) {
        return storePendingTransaction(message, null);
    }

    public int storePendingTransaction(Message message, Object obj) {
        int andIncrement = sNextRequestId.getAndIncrement();
        Pair<Message, Object> pair = new Pair<>(message, obj);
        synchronized (this.mPendingTransactions) {
            this.mPendingTransactions.put(Integer.valueOf(andIncrement), pair);
        }
        return andIncrement;
    }

    public Pair<Message, Object> retrievePendingTransaction(Integer num) {
        Pair<Message, Object> remove;
        synchronized (this.mPendingTransactions) {
            remove = this.mPendingTransactions.remove(num);
        }
        return remove;
    }

    public String getIccId() {
        String fakeIccid;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakeIccid = this.mCarrierTestOverride.getFakeIccid()) == null) ? this.mIccId : fakeIccid;
    }

    public String getFullIccId() {
        return this.mFullIccId;
    }

    public void registerForRecordsLoaded(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mRecordsLoadedRegistrants.add(registrant);
        if (getRecordsLoaded()) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void unregisterForRecordsLoaded(Handler handler) {
        this.mRecordsLoadedRegistrants.remove(handler);
    }

    public void unregisterForRecordsOverride(Handler handler) {
        this.mRecordsOverrideRegistrants.remove(handler);
    }

    public void registerForRecordsOverride(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mRecordsOverrideRegistrants.add(registrant);
        if (getRecordsLoaded()) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void registerForLockedRecordsLoaded(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mLockedRecordsLoadedRegistrants.add(registrant);
        if (getLockedRecordsLoaded()) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void unregisterForLockedRecordsLoaded(Handler handler) {
        this.mLockedRecordsLoadedRegistrants.remove(handler);
    }

    public void registerForNetworkLockedRecordsLoaded(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mNetworkLockedRecordsLoadedRegistrants.add(registrant);
        if (getNetworkLockedRecordsLoaded()) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void unregisterForNetworkLockedRecordsLoaded(Handler handler) {
        this.mNetworkLockedRecordsLoadedRegistrants.remove(handler);
    }

    public void registerForImsiReady(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mImsiReadyRegistrants.add(registrant);
        if (getIMSI() != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void unregisterForImsiReady(Handler handler) {
        this.mImsiReadyRegistrants.remove(handler);
    }

    public void registerForSpnUpdate(Handler handler, int i, Object obj) {
        if (this.mDestroyed.get()) {
            return;
        }
        Registrant registrant = new Registrant(handler, i, obj);
        this.mSpnUpdatedRegistrants.add(registrant);
        if (TextUtils.isEmpty(this.mSpn)) {
            return;
        }
        registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
    }

    public void unregisterForSpnUpdate(Handler handler) {
        this.mSpnUpdatedRegistrants.remove(handler);
    }

    public void registerForRecordsEvents(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mRecordsEventsRegistrants.add(registrant);
        registrant.notifyResult(0);
        registrant.notifyResult(1);
    }

    public void unregisterForRecordsEvents(Handler handler) {
        this.mRecordsEventsRegistrants.remove(handler);
    }

    public void registerForNewSms(Handler handler, int i, Object obj) {
        this.mNewSmsRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForNewSms(Handler handler) {
        this.mNewSmsRegistrants.remove(handler);
    }

    public void registerForNetworkSelectionModeAutomatic(Handler handler, int i, Object obj) {
        this.mNetworkSelectionModeAutomaticRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForNetworkSelectionModeAutomatic(Handler handler) {
        this.mNetworkSelectionModeAutomaticRegistrants.remove(handler);
    }

    public String getIMSI() {
        String fakeIMSI;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakeIMSI = this.mCarrierTestOverride.getFakeIMSI()) == null) ? this.mImsi : fakeIMSI;
    }

    public void setImsi(String str) {
        String stripTrailingFs = IccUtils.stripTrailingFs(str);
        this.mImsi = stripTrailingFs;
        if (!Objects.equals(stripTrailingFs, str)) {
            loge("Invalid IMSI padding digits received.");
        }
        if (TextUtils.isEmpty(this.mImsi)) {
            this.mImsi = null;
        }
        String str2 = this.mImsi;
        if (str2 != null && !str2.matches("[0-9]+")) {
            loge("Invalid non-numeric IMSI digits received.");
            this.mImsi = null;
        }
        String str3 = this.mImsi;
        if (str3 != null && (str3.length() < 6 || this.mImsi.length() > 15)) {
            loge("invalid IMSI " + this.mImsi);
            this.mImsi = null;
        }
        log("IMSI: mMncLength=" + this.mMncLength);
        String str4 = this.mImsi;
        if (str4 != null && str4.length() >= 6) {
            log("IMSI: " + this.mImsi.substring(0, 6) + Rlog.pii(VDBG, this.mImsi.substring(6)));
        }
        updateOperatorPlmn();
        this.mImsiReadyRegistrants.notifyRegistrants();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateOperatorPlmn() {
        String imsi = getIMSI();
        if (imsi != null) {
            int i = this.mMncLength;
            if ((i == 0 || i == 2) && imsi.length() >= 6) {
                String substring = imsi.substring(0, 6);
                String[] strArr = MCCMNC_CODES_HAVING_3DIGITS_MNC;
                int length = strArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if (strArr[i2].equals(substring)) {
                        this.mMncLength = 3;
                        log("IMSI: setting1 mMncLength=" + this.mMncLength);
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            if (this.mMncLength == 0) {
                try {
                    this.mMncLength = MccTable.smallestDigitsMccForMnc(Integer.parseInt(imsi.substring(0, 3)));
                    log("setting2 mMncLength=" + this.mMncLength);
                } catch (NumberFormatException unused) {
                    loge("Corrupt IMSI! setting3 mMncLength=" + this.mMncLength);
                }
            }
            int i3 = this.mMncLength;
            if (i3 == 0 || i3 == -1 || imsi.length() < this.mMncLength + 3) {
                return;
            }
            log("update mccmnc=" + imsi.substring(0, this.mMncLength + 3));
            MccTable.updateMccMncConfiguration(this.mContext, imsi.substring(0, this.mMncLength + 3));
        }
    }

    public String getMsisdnNumber() {
        return this.mMsisdn;
    }

    public String getGid1() {
        String fakeGid1;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakeGid1 = this.mCarrierTestOverride.getFakeGid1()) == null) ? this.mGid1 : fakeGid1;
    }

    public String getGid2() {
        String fakeGid2;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakeGid2 = this.mCarrierTestOverride.getFakeGid2()) == null) ? this.mGid2 : fakeGid2;
    }

    public String getPnnHomeName() {
        String fakePnnHomeName;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakePnnHomeName = this.mCarrierTestOverride.getFakePnnHomeName()) == null) ? this.mPnnHomeName : fakePnnHomeName;
    }

    public PlmnNetworkName[] getPnns() {
        return this.mPnns;
    }

    public OperatorPlmnInfo[] getOpl() {
        return this.mOpl;
    }

    public void setMsisdnNumber(String str, String str2, Message message) {
        loge("setMsisdn() should not be invoked on base IccRecords");
        AsyncResult.forMessage(message).exception = new IccIoResult(106, 130, (byte[]) null).getException();
        message.sendToTarget();
    }

    public String getMsisdnAlphaTag() {
        return this.mMsisdnTag;
    }

    public String getVoiceMailNumber() {
        return this.mVoiceMailNum;
    }

    public String getServiceProviderName() {
        String fakeSpn;
        return (!this.mCarrierTestOverride.isInTestMode() || (fakeSpn = this.mCarrierTestOverride.getFakeSpn()) == null) ? this.mSpn : fakeSpn;
    }

    public String getServiceProviderNameWithBrandOverride() {
        UiccCardApplication uiccCardApplication = this.mParentApp;
        if (uiccCardApplication != null && uiccCardApplication.getUiccProfile() != null) {
            String operatorBrandOverride = this.mParentApp.getUiccProfile().getOperatorBrandOverride();
            if (!TextUtils.isEmpty(operatorBrandOverride)) {
                return operatorBrandOverride;
            }
        }
        return this.mSpn;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setServiceProviderName(String str) {
        if (TextUtils.equals(this.mSpn, str)) {
            return;
        }
        this.mSpn = str != null ? str.trim() : null;
        this.mSpnUpdatedRegistrants.notifyRegistrants();
    }

    public String getVoiceMailAlphaTag() {
        return this.mVoiceMailTag;
    }

    public boolean getRecordsLoaded() {
        return this.mRecordsToLoad == 0 && this.mRecordsRequested;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getLockedRecordsLoaded() {
        return this.mRecordsToLoad == 0 && this.mLockedRecordsReqReason == 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getNetworkLockedRecordsLoaded() {
        return this.mRecordsToLoad == 0 && this.mLockedRecordsReqReason == 2;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 28) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception != null) {
                onRecordLoaded();
                loge("Exception in EVENT_GET_SMS_RECORD_SIZE_DONE " + asyncResult.exception);
                return;
            }
            int[] iArr = (int[]) asyncResult.result;
            try {
                try {
                    this.mSmsCountOnIcc = iArr[2];
                    log("EVENT_GET_SMS_RECORD_SIZE_DONE Size " + iArr[0] + " total " + iArr[1] + " record " + iArr[2]);
                } finally {
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                this.mSmsCountOnIcc = -1;
                loge("ArrayIndexOutOfBoundsException in EVENT_GET_SMS_RECORD_SIZE_DONE: " + e.toString());
            }
        } else if (i == 31) {
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            log("Card REFRESH occurred: ");
            if (asyncResult2.exception == null) {
                handleRefresh((IccRefreshResponse) asyncResult2.result);
                return;
            }
            loge("Icc refresh Exception: " + asyncResult2.exception);
        } else if (i == 90) {
            AsyncResult asyncResult3 = (AsyncResult) message.obj;
            AuthAsyncResponse authAsyncResponse = (AuthAsyncResponse) asyncResult3.userObj;
            log("EVENT_AKA_AUTHENTICATE_DONE");
            synchronized (authAsyncResponse) {
                Throwable th = asyncResult3.exception;
                if (th != null) {
                    authAsyncResponse.exception = th;
                    loge("Exception ICC SIM AKA: " + asyncResult3.exception);
                } else {
                    Object obj = asyncResult3.result;
                    if (obj == null) {
                        authAsyncResponse.exception = new NullPointerException("Null SIM authentication response");
                        loge("EVENT_AKA_AUTHENTICATE_DONE: null response");
                    } else {
                        try {
                            authAsyncResponse.authRsp = (IccIoResult) obj;
                            if (VDBG) {
                                log("ICC SIM AKA: authRsp = " + authAsyncResponse.authRsp);
                            }
                        } catch (ClassCastException e2) {
                            authAsyncResponse.exception = e2;
                            loge("Failed to parse ICC SIM AKA contents: " + e2);
                        }
                    }
                }
                authAsyncResponse.notifyAll();
            }
        } else if (i == 100) {
            try {
                try {
                    AsyncResult asyncResult4 = (AsyncResult) message.obj;
                    IccRecordLoaded iccRecordLoaded = (IccRecordLoaded) asyncResult4.userObj;
                    log(iccRecordLoaded.getEfName() + " LOADED");
                    if (asyncResult4.exception != null) {
                        loge("Record Load Exception: " + asyncResult4.exception);
                    } else {
                        iccRecordLoaded.onRecordLoaded(asyncResult4);
                    }
                } catch (RuntimeException e3) {
                    loge("Exception parsing SIM record: " + e3);
                }
            } finally {
            }
        } else if (i != 201) {
            switch (i) {
                case 257:
                    this.mLockedRecordsReqReason = 0;
                    onReady();
                    return;
                case 258:
                    this.mLockedRecordsReqReason = 1;
                    onLocked();
                    return;
                case 259:
                    this.mLockedRecordsReqReason = 2;
                    onLocked();
                    return;
                case 260:
                    this.mLockedRecordsReqReason = 0;
                    onDetected();
                    return;
                default:
                    super.handleMessage(message);
                    return;
            }
        } else {
            AsyncResult asyncResult5 = (AsyncResult) message.obj;
            Object obj2 = asyncResult5.userObj;
            SmssRecord smssRecord = obj2 != null ? (SmssRecord) obj2 : null;
            if (asyncResult5.exception == null && smssRecord.getSmssValue() != null) {
                this.mSmssValues = (byte[]) smssRecord.getSmssValue().clone();
            } else {
                loge("SIM EF_SMSS field updating error=" + asyncResult5.exception);
            }
            if (smssRecord != null && smssRecord.getMessage() != null) {
                Message message2 = smssRecord.getMessage();
                AsyncResult.forMessage(message2, asyncResult5.result, asyncResult5.exception);
                message2.sendToTarget();
                return;
            }
            loge("smssRecord or smssRecord.getMessage() object is null");
        }
    }

    public String getSimLanguage() {
        return this.mPrefLang;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSimLanguage(byte[] bArr, byte[] bArr2) {
        String[] locales = this.mContext.getAssets().getLocales();
        try {
            this.mPrefLang = findBestLanguage(bArr, locales);
        } catch (UnsupportedEncodingException unused) {
            log("Unable to parse EF-LI: " + Arrays.toString(bArr));
        }
        if (this.mPrefLang == null) {
            try {
                this.mPrefLang = findBestLanguage(bArr2, locales);
            } catch (UnsupportedEncodingException unused2) {
                log("Unable to parse EF-PL: " + Arrays.toString(bArr));
            }
        }
    }

    protected static String findBestLanguage(byte[] bArr, String[] strArr) throws UnsupportedEncodingException {
        if (bArr != null && strArr != null) {
            for (int i = 0; i + 1 < bArr.length; i += 2) {
                String str = new String(bArr, i, 2, "ISO-8859-1");
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    String str2 = strArr[i2];
                    if (str2 != null && str2.length() >= 2 && strArr[i2].substring(0, 2).equalsIgnoreCase(str)) {
                        return str;
                    }
                }
            }
        }
        return null;
    }

    protected void handleRefresh(IccRefreshResponse iccRefreshResponse) {
        if (iccRefreshResponse == null) {
            log("handleRefresh received without input");
        } else if (TextUtils.isEmpty(iccRefreshResponse.aid) || iccRefreshResponse.aid.equals(this.mParentApp.getAid())) {
            if (iccRefreshResponse.refreshResult == 0) {
                log("handleRefresh with SIM_FILE_UPDATED");
                handleFileUpdate(iccRefreshResponse.efId);
                return;
            }
            log("handleRefresh with unknown operation");
        }
    }

    public int getCarrierNameDisplayCondition() {
        return this.mCarrierNameDisplayCondition;
    }

    public String[] getServiceProviderDisplayInformation() {
        return this.mSpdi;
    }

    public String[] getHomePlmns() {
        String operatorNumeric = getOperatorNumeric();
        String[] ehplmns = getEhplmns();
        String[] serviceProviderDisplayInformation = getServiceProviderDisplayInformation();
        if (ArrayUtils.isEmpty(ehplmns)) {
            ehplmns = new String[]{operatorNumeric};
        }
        if (!ArrayUtils.isEmpty(serviceProviderDisplayInformation)) {
            ehplmns = (String[]) ArrayUtils.concatElements(String.class, new String[][]{ehplmns, serviceProviderDisplayInformation});
        }
        return (String[]) ArrayUtils.appendElement(String.class, ehplmns, operatorNumeric);
    }

    public boolean isLoaded() {
        return this.mLoaded.get();
    }

    public String[] getEhplmns() {
        return this.mEhplmns;
    }

    public String[] getPlmnsFromHplmnActRecord() {
        PlmnActRecord[] plmnActRecordArr = this.mHplmnActRecords;
        if (plmnActRecordArr == null) {
            return null;
        }
        String[] strArr = new String[plmnActRecordArr.length];
        int i = 0;
        while (true) {
            PlmnActRecord[] plmnActRecordArr2 = this.mHplmnActRecords;
            if (i >= plmnActRecordArr2.length) {
                return strArr;
            }
            strArr[i] = plmnActRecordArr2[i].plmn;
            i++;
        }
    }

    public String getIccSimChallengeResponse(int i, String str) {
        if (VDBG) {
            log("getIccSimChallengeResponse:");
        }
        CommandsInterface commandsInterface = this.mCi;
        UiccCardApplication uiccCardApplication = this.mParentApp;
        if (commandsInterface == null || uiccCardApplication == null) {
            loge("getIccSimChallengeResponse: Fail, ci or parentApp is null");
            return null;
        }
        AuthAsyncResponse authAsyncResponse = new AuthAsyncResponse();
        synchronized (authAsyncResponse) {
            commandsInterface.requestIccSimAuthentication(i, str, uiccCardApplication.getAid(), obtainMessage(90, 0, 0, authAsyncResponse));
            long elapsedRealtime = SystemClock.elapsedRealtime();
            do {
                try {
                    long elapsedRealtime2 = (elapsedRealtime + 2500) - SystemClock.elapsedRealtime();
                    if (elapsedRealtime2 > 0) {
                        authAsyncResponse.wait(elapsedRealtime2);
                    }
                } catch (InterruptedException unused) {
                    Rlog.w("IccRecords", "getIccSimChallengeResponse: InterruptedException.");
                }
                if (SystemClock.elapsedRealtime() - elapsedRealtime >= 2500 || authAsyncResponse.authRsp != null) {
                    break;
                }
            } while (authAsyncResponse.exception == null);
            if (SystemClock.elapsedRealtime() - elapsedRealtime >= 2500 && authAsyncResponse.authRsp == null && authAsyncResponse.exception == null) {
                loge("getIccSimChallengeResponse timeout!");
                return null;
            } else if (authAsyncResponse.exception != null) {
                loge("getIccSimChallengeResponse exception: " + authAsyncResponse.exception);
                return null;
            } else if (authAsyncResponse.authRsp == null) {
                loge("getIccSimChallengeResponse: No authentication response");
                return null;
            } else {
                if (VDBG) {
                    log("getIccSimChallengeResponse: return rsp.authRsp");
                }
                byte[] bArr = authAsyncResponse.authRsp.payload;
                if (bArr != null) {
                    return new String(bArr);
                }
                return null;
            }
        }
    }

    public int getSmsCapacityOnIcc() {
        log("getSmsCapacityOnIcc: " + this.mSmsCountOnIcc);
        return this.mSmsCountOnIcc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String parseEfPsiSmsc(byte[] bArr) {
        SimTlv simTlv = new SimTlv(bArr, 0, bArr.length);
        if (simTlv.isValidObject() && simTlv.getData() != null && simTlv.getTag() == 128) {
            return new String(simTlv.getData(), Charset.forName("UTF-8"));
        }
        if (VDBG) {
            log("Can't find EF PSISMSC field in SIM = " + IccUtils.bytesToHexString(bArr));
            return null;
        }
        return null;
    }

    public String getSmscIdentity() {
        return this.mPsiSmsc;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        boolean z;
        printWriter.println("IccRecords: " + this);
        printWriter.println(" mDestroyed=" + this.mDestroyed);
        printWriter.println(" mCi=" + this.mCi);
        printWriter.println(" mFh=" + this.mFh);
        printWriter.println(" mParentApp=" + this.mParentApp);
        printWriter.println(" recordsLoadedRegistrants: size=" + this.mRecordsLoadedRegistrants.size());
        for (int i = 0; i < this.mRecordsLoadedRegistrants.size(); i++) {
            printWriter.println("  recordsLoadedRegistrants[" + i + "]=" + ((Registrant) this.mRecordsLoadedRegistrants.get(i)).getHandler());
        }
        printWriter.println(" mLockedRecordsLoadedRegistrants: size=" + this.mLockedRecordsLoadedRegistrants.size());
        for (int i2 = 0; i2 < this.mLockedRecordsLoadedRegistrants.size(); i2++) {
            printWriter.println("  mLockedRecordsLoadedRegistrants[" + i2 + "]=" + ((Registrant) this.mLockedRecordsLoadedRegistrants.get(i2)).getHandler());
        }
        printWriter.println(" mNetworkLockedRecordsLoadedRegistrants: size=" + this.mNetworkLockedRecordsLoadedRegistrants.size());
        for (int i3 = 0; i3 < this.mNetworkLockedRecordsLoadedRegistrants.size(); i3++) {
            printWriter.println("  mLockedRecordsLoadedRegistrants[" + i3 + "]=" + ((Registrant) this.mNetworkLockedRecordsLoadedRegistrants.get(i3)).getHandler());
        }
        printWriter.println(" mImsiReadyRegistrants: size=" + this.mImsiReadyRegistrants.size());
        for (int i4 = 0; i4 < this.mImsiReadyRegistrants.size(); i4++) {
            printWriter.println("  mImsiReadyRegistrants[" + i4 + "]=" + ((Registrant) this.mImsiReadyRegistrants.get(i4)).getHandler());
        }
        printWriter.println(" mRecordsEventsRegistrants: size=" + this.mRecordsEventsRegistrants.size());
        for (int i5 = 0; i5 < this.mRecordsEventsRegistrants.size(); i5++) {
            printWriter.println("  mRecordsEventsRegistrants[" + i5 + "]=" + ((Registrant) this.mRecordsEventsRegistrants.get(i5)).getHandler());
        }
        printWriter.println(" mNewSmsRegistrants: size=" + this.mNewSmsRegistrants.size());
        for (int i6 = 0; i6 < this.mNewSmsRegistrants.size(); i6++) {
            printWriter.println("  mNewSmsRegistrants[" + i6 + "]=" + ((Registrant) this.mNewSmsRegistrants.get(i6)).getHandler());
        }
        printWriter.println(" mNetworkSelectionModeAutomaticRegistrants: size=" + this.mNetworkSelectionModeAutomaticRegistrants.size());
        for (int i7 = 0; i7 < this.mNetworkSelectionModeAutomaticRegistrants.size(); i7++) {
            printWriter.println("  mNetworkSelectionModeAutomaticRegistrants[" + i7 + "]=" + ((Registrant) this.mNetworkSelectionModeAutomaticRegistrants.get(i7)).getHandler());
        }
        printWriter.println(" mRecordsRequested=" + this.mRecordsRequested);
        printWriter.println(" mLockedRecordsReqReason=" + this.mLockedRecordsReqReason);
        printWriter.println(" mRecordsToLoad=" + this.mRecordsToLoad);
        printWriter.println(" mRdnCache=" + this.mAdnCache);
        printWriter.println(" iccid=" + SubscriptionInfo.givePrintableIccid(this.mFullIccId));
        StringBuilder sb = new StringBuilder();
        sb.append(" mMsisdn=");
        sb.append(Rlog.pii(VDBG, this.mMsisdn));
        printWriter.println(sb.toString());
        printWriter.println(" mMsisdnTag=" + this.mMsisdnTag);
        printWriter.println(" mVoiceMailNum=" + Rlog.pii(z, this.mVoiceMailNum));
        printWriter.println(" mVoiceMailTag=" + this.mVoiceMailTag);
        printWriter.println(" mNewVoiceMailNum=" + Rlog.pii(z, this.mNewVoiceMailNum));
        printWriter.println(" mNewVoiceMailTag=" + this.mNewVoiceMailTag);
        printWriter.println(" mIsVoiceMailFixed=" + this.mIsVoiceMailFixed);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" mImsi=");
        sb2.append(this.mImsi != null ? this.mImsi.substring(0, 6) + Rlog.pii(z, this.mImsi.substring(6)) : "null");
        printWriter.println(sb2.toString());
        if (this.mCarrierTestOverride.isInTestMode()) {
            printWriter.println(" mFakeImsi=" + this.mCarrierTestOverride.getFakeIMSI());
        }
        printWriter.println(" mMncLength=" + this.mMncLength);
        printWriter.println(" mMailboxIndex=" + this.mMailboxIndex);
        printWriter.println(" mSpn=" + this.mSpn);
        if (this.mCarrierTestOverride.isInTestMode()) {
            printWriter.println(" mFakeSpn=" + this.mCarrierTestOverride.getFakeSpn());
        }
        printWriter.flush();
    }

    public static String getNetworkNameForPlmnFromPnnOpl(PlmnNetworkName[] plmnNetworkNameArr, OperatorPlmnInfo[] operatorPlmnInfoArr, String str, int i) {
        PlmnNetworkName plmnNetworkName;
        if (operatorPlmnInfoArr != null && plmnNetworkNameArr != null && str != null && str.length() >= 5 && str.length() <= 6) {
            int length = operatorPlmnInfoArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                int pnnIdx = operatorPlmnInfoArr[i2].getPnnIdx(str, i);
                if (pnnIdx < 0) {
                    i2++;
                } else if (pnnIdx < plmnNetworkNameArr.length && (plmnNetworkName = plmnNetworkNameArr[pnnIdx]) != null) {
                    return plmnNetworkName.getName();
                } else {
                    Rlog.e("IccRecords", "Invalid PNN record for Record" + pnnIdx);
                }
            }
        }
        return null;
    }

    /* loaded from: classes.dex */
    public static final class OperatorPlmnInfo {
        public final int lacTacEnd;
        public final int lacTacStart;
        public final String plmnNumericPattern;
        public final int pnnRecordId;

        public OperatorPlmnInfo(String str, int i, int i2, int i3) {
            this.plmnNumericPattern = str;
            this.lacTacStart = i;
            this.lacTacEnd = i2;
            this.pnnRecordId = i3;
        }

        public int getPnnIdx(String str, int i) {
            int i2;
            if (str != null && str.length() == this.plmnNumericPattern.length()) {
                for (int i3 = 0; i3 < str.length(); i3++) {
                    if (str.charAt(i3) != this.plmnNumericPattern.charAt(i3) && this.plmnNumericPattern.charAt(i3) != 'D') {
                        return -1;
                    }
                }
                int i4 = this.lacTacStart;
                if (i4 == 0 && this.lacTacEnd == 65534) {
                    i2 = this.pnnRecordId;
                } else if (i >= i4 && i <= this.lacTacEnd) {
                    i2 = this.pnnRecordId;
                }
                return i2 - 1;
            }
            return -1;
        }

        public int hashCode() {
            return Objects.hash(this.plmnNumericPattern, Integer.valueOf(this.lacTacStart), Integer.valueOf(this.lacTacEnd), Integer.valueOf(this.pnnRecordId));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof OperatorPlmnInfo) {
                OperatorPlmnInfo operatorPlmnInfo = (OperatorPlmnInfo) obj;
                return TextUtils.equals(this.plmnNumericPattern, operatorPlmnInfo.plmnNumericPattern) && this.lacTacStart == operatorPlmnInfo.lacTacStart && this.lacTacEnd == operatorPlmnInfo.lacTacEnd && this.pnnRecordId == operatorPlmnInfo.pnnRecordId;
            }
            return false;
        }

        public String toString() {
            return "{plmnNumericPattern = " + this.plmnNumericPattern + ", lacTacStart = " + this.lacTacStart + ", lacTacEnd = " + this.lacTacEnd + ", pnnRecordId = " + this.pnnRecordId + "}";
        }
    }

    /* loaded from: classes.dex */
    public static final class PlmnNetworkName {
        public final String fullName;
        public final String shortName;

        public PlmnNetworkName(String str, String str2) {
            this.fullName = str;
            this.shortName = str2;
        }

        public String getName() {
            if (!TextUtils.isEmpty(this.fullName)) {
                return this.fullName;
            }
            return this.shortName;
        }

        public int hashCode() {
            return Objects.hash(this.fullName, this.shortName);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof PlmnNetworkName) {
                PlmnNetworkName plmnNetworkName = (PlmnNetworkName) obj;
                return TextUtils.equals(this.fullName, plmnNetworkName.fullName) && TextUtils.equals(this.shortName, plmnNetworkName.shortName);
            }
            return false;
        }

        public String toString() {
            return "{fullName = " + this.fullName + ", shortName = " + this.shortName + "}";
        }
    }

    public void setSmssTpmrValue(int i, Message message) {
        if (VDBG) {
            log("setSmssTpmrValue()");
        }
        byte[] bArr = this.mSmssValues;
        if (bArr != null && bArr.length > 0 && i >= 0 && i <= 255) {
            byte[] bArr2 = (byte[]) bArr.clone();
            bArr2[0] = (byte) (i & 255);
            this.mFh.updateEFTransparent(IccConstants.EF_SMSS, bArr2, obtainMessage(EVENT_SET_SMSS_RECORD_DONE, createSmssRecord(message, bArr2)));
        } else if (message != null) {
            loge("Failed to set EF_SMSS [TPMR] field to SIM");
            byte[] bArr3 = this.mSmssValues;
            if (bArr3 == null || bArr3.length <= 0) {
                AsyncResult.forMessage(message).exception = new FileNotFoundException("EF_SMSS file not found");
            } else if (i < 0 || i > 255) {
                AsyncResult.forMessage(message).exception = new IllegalArgumentException("TPMR value is not in allowed range");
            }
            message.sendToTarget();
        }
    }

    public int getSmssTpmrValue() {
        byte[] bArr = this.mSmssValues;
        if (bArr != null && bArr.length > 0) {
            return bArr[0] & 255;
        }
        loge("IccRecords - EF_SMSS is null");
        return -1;
    }

    @VisibleForTesting
    public SmssRecord createSmssRecord(Message message, byte[] bArr) {
        return new SmssRecord(message, bArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SmssRecord {
        private Message mMsg;
        private byte[] mSmss;

        SmssRecord(Message message, byte[] bArr) {
            this.mMsg = message;
            this.mSmss = bArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public byte[] getSmssValue() {
            return this.mSmss;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Message getMessage() {
            return this.mMsg;
        }
    }
}
