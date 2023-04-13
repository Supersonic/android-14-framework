package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.gsm.SimTlv;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class SIMRecords extends IccRecords {
    protected static final int EVENT_GET_FDN_DONE = 48;
    protected static final int EVENT_GET_OPL_DONE = 16;
    protected static final int EVENT_GET_PNN_DONE = 15;
    protected static final int EVENT_GET_PSISMSC_DONE = 47;
    protected static final int EVENT_GET_SMSS_RECORD_DONE = 46;
    protected static final int EVENT_GET_SST_DONE = 17;
    protected static final String LOG_TAG = "SIMRecords";
    private static final boolean VDBG = Rlog.isLoggable(LOG_TAG, 2);
    private int mCallForwardingStatus;
    private byte[] mCphsInfo;
    boolean mCspPlmnEnabled;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfCPHS_MWI;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfCff;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfCfis;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfLi;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfMWIS;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mEfPl;
    private GetSpnFsmState mSpnState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    UsimServiceTable mUsimServiceTable;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    VoiceMailConstants mVmConfig;

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(implicitMember = "values()[Lcom/android/internal/telephony/uicc/SIMRecords$GetSpnFsmState;")
    /* loaded from: classes.dex */
    public enum GetSpnFsmState {
        IDLE,
        INIT,
        READ_SPN_3GPP,
        READ_SPN_CPHS,
        READ_SPN_SHORT_CPHS
    }

    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    public String toString() {
        return "SimRecords: " + super.toString() + " mVmConfig" + this.mVmConfig + " callForwardingEnabled=" + this.mCallForwardingStatus + " spnState=" + this.mSpnState + " mCphsInfo=" + IccUtils.bytesToHexString(this.mCphsInfo) + " mCspPlmnEnabled=" + this.mCspPlmnEnabled + " efMWIS=" + IccUtils.bytesToHexString(this.mEfMWIS) + " efCPHS_MWI=" + IccUtils.bytesToHexString(this.mEfCPHS_MWI) + " mEfCff=" + IccUtils.bytesToHexString(this.mEfCff) + " mEfCfis=" + IccUtils.bytesToHexString(this.mEfCfis) + " getOperatorNumeric=" + getOperatorNumeric() + " mPsiSmsc=" + this.mPsiSmsc + " TPMR=" + getSmssTpmrValue();
    }

    public SIMRecords(UiccCardApplication uiccCardApplication, Context context, CommandsInterface commandsInterface) {
        super(uiccCardApplication, context, commandsInterface);
        this.mCphsInfo = null;
        this.mCspPlmnEnabled = true;
        this.mEfMWIS = null;
        this.mEfCPHS_MWI = null;
        this.mEfCff = null;
        this.mEfCfis = null;
        this.mEfLi = null;
        this.mEfPl = null;
        this.mAdnCache = new AdnRecordCache(this.mFh);
        this.mVmConfig = new VoiceMailConstants();
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mRecordsToLoad = 0;
        this.mCi.setOnSmsOnSim(this, 21, null);
        resetRecords();
        log("SIMRecords X ctor this=" + this);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dispose() {
        log("Disposing SIMRecords this=" + this);
        this.mCi.unSetOnSmsOnSim(this);
        resetRecords();
        super.dispose();
    }

    protected void finalize() {
        log("finalized");
    }

    protected void resetRecords() {
        this.mImsi = null;
        this.mMsisdn = null;
        this.mVoiceMailNum = null;
        this.mMncLength = -1;
        log("setting0 mMncLength" + this.mMncLength);
        this.mIccId = null;
        this.mFullIccId = null;
        this.mCarrierNameDisplayCondition = 0;
        this.mEfMWIS = null;
        this.mEfCPHS_MWI = null;
        this.mSpdi = null;
        this.mPnnHomeName = null;
        this.mPnns = null;
        this.mOpl = null;
        this.mGid1 = null;
        this.mGid2 = null;
        this.mPlmnActRecords = null;
        this.mOplmnActRecords = null;
        this.mHplmnActRecords = null;
        this.mFplmns = null;
        this.mEhplmns = null;
        this.mAdnCache.reset();
        log("SIMRecords: onRadioOffOrNotAvailable set 'gsm.sim.operator.numeric' to operator=null");
        log("update icc_operator_numeric=" + ((Object) null));
        this.mTelephonyManager.setSimOperatorNumericForPhone(this.mParentApp.getPhoneId(), PhoneConfigurationManager.SSSS);
        this.mTelephonyManager.setSimOperatorNameForPhone(this.mParentApp.getPhoneId(), PhoneConfigurationManager.SSSS);
        this.mTelephonyManager.setSimCountryIsoForPhone(this.mParentApp.getPhoneId(), PhoneConfigurationManager.SSSS);
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mLoaded.set(false);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getMsisdnNumber() {
        return this.mMsisdn;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public UsimServiceTable getUsimServiceTable() {
        return this.mUsimServiceTable;
    }

    public String getSimServiceTable() {
        UsimServiceTable usimServiceTable = this.mUsimServiceTable;
        if (usimServiceTable != null) {
            return IccUtils.bytesToHexString(usimServiceTable.getUSIMServiceTable());
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int getExtFromEf(int i) {
        return i != 28475 ? (i == 28480 && this.mParentApp.getType() == IccCardApplicationStatus.AppType.APPTYPE_USIM) ? IccConstants.EF_EXT5 : IccConstants.EF_EXT1 : IccConstants.EF_EXT2;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setMsisdnNumber(String str, String str2, Message message) {
        if (this.mDestroyed.get()) {
            return;
        }
        this.mNewMsisdn = str2;
        this.mNewMsisdnTag = str;
        log("Set MSISDN: " + this.mNewMsisdnTag + " " + Rlog.pii(LOG_TAG, this.mNewMsisdn));
        new AdnRecordLoader(this.mFh).updateEF(new AdnRecord(this.mNewMsisdnTag, this.mNewMsisdn), IccConstants.EF_MSISDN, getExtFromEf(IccConstants.EF_MSISDN), 1, null, obtainMessage(30, message));
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public String getMsisdnAlphaTag() {
        return this.mMsisdnTag;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getVoiceMailNumber() {
        return this.mVoiceMailNum;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMailNumber(String str, String str2, Message message) {
        if (this.mDestroyed.get()) {
            return;
        }
        if (this.mIsVoiceMailFixed) {
            AsyncResult.forMessage(message).exception = new IccVmFixedException("Voicemail number is fixed by operator");
            message.sendToTarget();
            return;
        }
        this.mNewVoiceMailNum = str2;
        this.mNewVoiceMailTag = str;
        AdnRecord adnRecord = new AdnRecord(this.mNewVoiceMailTag, this.mNewVoiceMailNum);
        int i = this.mMailboxIndex;
        if (i != 0 && i != 255) {
            new AdnRecordLoader(this.mFh).updateEF(adnRecord, IccConstants.EF_MBDN, IccConstants.EF_EXT6, this.mMailboxIndex, null, obtainMessage(20, 1, 0, message));
        } else if (isCphsMailboxEnabled()) {
            new AdnRecordLoader(this.mFh).updateEF(adnRecord, IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, null, obtainMessage(25, 1, 0, message));
        } else {
            AsyncResult.forMessage(message).exception = new IccVmNotSupportedException("Update SIM voice mailbox error");
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public String getVoiceMailAlphaTag() {
        return this.mVoiceMailTag;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMessageWaiting(int i, int i2) {
        if (!this.mDestroyed.get() && i == 1) {
            try {
                byte[] bArr = this.mEfMWIS;
                if (bArr != null) {
                    bArr[0] = (byte) ((bArr[0] & 254) | (i2 == 0 ? 0 : 1));
                    if (i2 < 0) {
                        bArr[1] = 0;
                    } else {
                        bArr[1] = (byte) i2;
                    }
                    this.mFh.updateEFLinearFixed(IccConstants.EF_MWIS, 1, bArr, null, obtainMessage(14, IccConstants.EF_MWIS, 0));
                }
                byte[] bArr2 = this.mEfCPHS_MWI;
                if (bArr2 != null) {
                    bArr2[0] = (byte) ((i2 == 0 ? 5 : 10) | (bArr2[0] & 240));
                    this.mFh.updateEFTransparent(IccConstants.EF_VOICE_MAIL_INDICATOR_CPHS, bArr2, obtainMessage(14, Integer.valueOf((int) IccConstants.EF_VOICE_MAIL_INDICATOR_CPHS)));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                logw("Error saving voice mail state to SIM. Probably malformed SIM record", e);
            }
        }
    }

    private boolean validEfCfis(byte[] bArr) {
        if (bArr != null) {
            byte b = bArr[0];
            if (b < 1 || b > 4) {
                logw("MSP byte: " + ((int) bArr[0]) + " is not between 1 and 4", null);
            }
            for (byte b2 : bArr) {
                if (b2 != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public int getVoiceMessageCount() {
        byte[] bArr = this.mEfMWIS;
        if (bArr != null) {
            boolean z = (bArr[0] & 1) != 0;
            int i = bArr[1] & 255;
            if (!z || (i != 0 && i != 255)) {
                r1 = i;
            }
            log(" VoiceMessageCount from SIM MWIS = " + r1);
            return r1;
        }
        byte[] bArr2 = this.mEfCPHS_MWI;
        if (bArr2 != null) {
            int i2 = bArr2[0] & 15;
            r1 = i2 != 10 ? i2 == 5 ? 0 : -2 : -1;
            log(" VoiceMessageCount from SIM CPHS = " + r1);
            return r1;
        }
        return -2;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public int getVoiceCallForwardingFlag() {
        return this.mCallForwardingStatus;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setVoiceCallForwardingFlag(int i, boolean z, String str) {
        if (!this.mDestroyed.get() && i == 1) {
            this.mCallForwardingStatus = z ? 1 : 0;
            this.mRecordsEventsRegistrants.notifyResult(1);
            try {
                if (validEfCfis(this.mEfCfis)) {
                    if (z) {
                        byte[] bArr = this.mEfCfis;
                        bArr[1] = (byte) (bArr[1] | 1);
                    } else {
                        byte[] bArr2 = this.mEfCfis;
                        bArr2[1] = (byte) (bArr2[1] & 254);
                    }
                    log("setVoiceCallForwardingFlag: enable=" + z + " mEfCfis=" + IccUtils.bytesToHexString(this.mEfCfis));
                    if (z && !TextUtils.isEmpty(str)) {
                        logv("EF_CFIS: updating cf number, " + Rlog.pii(LOG_TAG, str));
                        byte[] numberToCalledPartyBCD = PhoneNumberUtils.numberToCalledPartyBCD(str, 1);
                        System.arraycopy(numberToCalledPartyBCD, 0, this.mEfCfis, 3, numberToCalledPartyBCD.length);
                        byte[] bArr3 = this.mEfCfis;
                        bArr3[2] = (byte) numberToCalledPartyBCD.length;
                        bArr3[14] = -1;
                        bArr3[15] = -1;
                    }
                    this.mFh.updateEFLinearFixed(IccConstants.EF_CFIS, 1, this.mEfCfis, null, obtainMessage(14, Integer.valueOf((int) IccConstants.EF_CFIS)));
                } else {
                    log("setVoiceCallForwardingFlag: ignoring enable=" + z + " invalid mEfCfis=" + IccUtils.bytesToHexString(this.mEfCfis));
                }
                byte[] bArr4 = this.mEfCff;
                if (bArr4 != null) {
                    if (z) {
                        bArr4[0] = (byte) ((bArr4[0] & 240) | 10);
                    } else {
                        bArr4[0] = (byte) ((bArr4[0] & 240) | 5);
                    }
                    this.mFh.updateEFTransparent(IccConstants.EF_CFF_CPHS, bArr4, obtainMessage(14, Integer.valueOf((int) IccConstants.EF_CFF_CPHS)));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                logw("Error saving call forwarding flag to SIM. Probably malformed SIM record", e);
            }
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onRefresh(boolean z, int[] iArr) {
        if (z) {
            fetchSimRecords();
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getOperatorNumeric() {
        String imsi = getIMSI();
        if (imsi == null) {
            log("getOperatorNumeric: IMSI == null");
            return null;
        }
        int i = this.mMncLength;
        if (i == -1 || i == 0) {
            log("getSIMOperatorNumeric: bad mncLength");
            return null;
        }
        int length = imsi.length();
        int i2 = this.mMncLength;
        if (length >= i2 + 3) {
            return imsi.substring(0, i2 + 3);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x0214 A[Catch: all -> 0x09f1, RuntimeException -> 0x09f4, TryCatch #5 {RuntimeException -> 0x09f4, all -> 0x09f1, blocks: (B:16:0x006e, B:18:0x0076, B:19:0x008e, B:21:0x0094, B:23:0x0097, B:25:0x00a1, B:26:0x00b9, B:28:0x00c1, B:29:0x00d9, B:31:0x00e3, B:55:0x01e2, B:59:0x01f1, B:61:0x0210, B:63:0x0214, B:65:0x0218, B:60:0x01fa, B:78:0x024f, B:82:0x025e, B:83:0x0268, B:84:0x0280, B:88:0x028f, B:89:0x02c7, B:90:0x02df, B:94:0x02ee, B:96:0x030e, B:97:0x032a, B:98:0x0342, B:102:0x0351, B:104:0x0371, B:105:0x038d, B:106:0x03a5, B:108:0x03b1, B:109:0x03cb, B:110:0x03e9, B:112:0x03f5, B:113:0x040f, B:114:0x042d, B:116:0x0435, B:117:0x044d, B:118:0x046e, B:120:0x047a, B:121:0x047e, B:130:0x04c6, B:133:0x04d0, B:148:0x0543, B:150:0x054f, B:151:0x0553, B:194:0x06e0, B:197:0x06ea, B:198:0x06f3, B:201:0x0700, B:202:0x071e, B:205:0x0727, B:206:0x072f, B:209:0x0738, B:215:0x074f, B:218:0x075c, B:219:0x0760, B:221:0x076a, B:223:0x0772, B:224:0x0778, B:225:0x07a5, B:230:0x07bb, B:248:0x0811, B:250:0x0835, B:251:0x084d, B:252:0x0851, B:254:0x0875, B:255:0x088d, B:257:0x0892, B:258:0x0899, B:261:0x08a1, B:263:0x08a9, B:267:0x08bc, B:269:0x08ca, B:270:0x08df, B:274:0x08f9, B:276:0x0909, B:278:0x090d, B:279:0x0922, B:280:0x0930, B:282:0x093c, B:285:0x095d, B:286:0x0963, B:288:0x096a, B:289:0x0980, B:290:0x0990, B:293:0x099e, B:294:0x09c8, B:296:0x09d0, B:297:0x09e8, B:226:0x07a7, B:228:0x07af, B:232:0x07bf, B:234:0x07db, B:235:0x07e1, B:237:0x07e4, B:238:0x07ea, B:242:0x07f4, B:243:0x0809), top: B:317:0x003f }] */
    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleMessage(Message message) {
        Message message2;
        AdnRecord adnRecord;
        AdnRecord adnRecord2;
        String[] strArr;
        AsyncResult asyncResult;
        AsyncResult asyncResult2;
        if (this.mDestroyed.get()) {
            loge("Received message " + message + "[" + message.what + "]  while being destroyed. Ignoring.");
            return;
        }
        boolean z = false;
        try {
            try {
                message2 = null;
            } catch (RuntimeException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            switch (message.what) {
                case 3:
                    AsyncResult asyncResult3 = (AsyncResult) message.obj;
                    if (asyncResult3.exception != null) {
                        loge("Exception querying IMSI, Exception:" + asyncResult3.exception);
                    } else {
                        setImsi((String) asyncResult3.result);
                    }
                    z = true;
                    break;
                case 4:
                    AsyncResult asyncResult4 = (AsyncResult) message.obj;
                    byte[] bArr = (byte[]) asyncResult4.result;
                    if (asyncResult4.exception == null) {
                        this.mIccId = IccUtils.bcdToString(bArr, 0, bArr.length);
                        this.mFullIccId = IccUtils.bchToString(bArr, 0, bArr.length);
                        log("iccid: " + SubscriptionInfo.givePrintableIccid(this.mFullIccId));
                    }
                    z = true;
                    break;
                case 5:
                    AsyncResult asyncResult5 = (AsyncResult) message.obj;
                    byte[] bArr2 = (byte[]) asyncResult5.result;
                    if (asyncResult5.exception == null) {
                        log("EF_MBI: " + IccUtils.bytesToHexString(bArr2));
                        int i = bArr2[0] & 255;
                        this.mMailboxIndex = i;
                        if (i != 0 && i != 255) {
                            log("Got valid mailbox number for MBDN");
                            z = true;
                        }
                    }
                    this.mRecordsToLoad++;
                    if (z) {
                        new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MBDN, IccConstants.EF_EXT6, this.mMailboxIndex, obtainMessage(6));
                    } else {
                        new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, obtainMessage(11));
                    }
                    z = true;
                    break;
                case 6:
                case 11:
                    this.mVoiceMailNum = null;
                    this.mVoiceMailTag = null;
                    AsyncResult asyncResult6 = (AsyncResult) message.obj;
                    if (asyncResult6.exception != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Invalid or missing EF");
                        sb.append(message.what == 11 ? "[MAILBOX]" : "[MBDN]");
                        log(sb.toString());
                        if (message.what == 6) {
                            this.mRecordsToLoad++;
                            new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, obtainMessage(11));
                        }
                    } else {
                        AdnRecord adnRecord3 = (AdnRecord) asyncResult6.result;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("VM: ");
                        sb2.append(adnRecord3);
                        sb2.append(message.what == 11 ? " EF[MAILBOX]" : " EF[MBDN]");
                        log(sb2.toString());
                        if (adnRecord3.isEmpty() && message.what == 6) {
                            this.mRecordsToLoad++;
                            new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, obtainMessage(11));
                        } else {
                            this.mVoiceMailNum = adnRecord3.getNumber();
                            this.mVoiceMailTag = adnRecord3.getAlphaTag();
                        }
                    }
                    z = true;
                    break;
                case 7:
                    AsyncResult asyncResult7 = (AsyncResult) message.obj;
                    byte[] bArr3 = (byte[]) asyncResult7.result;
                    log("EF_MWIS : " + IccUtils.bytesToHexString(bArr3));
                    if (asyncResult7.exception != null) {
                        log("EVENT_GET_MWIS_DONE exception = " + asyncResult7.exception);
                    } else if ((bArr3[0] & 255) == 255) {
                        log("SIMRecords: Uninitialized record MWIS");
                    } else {
                        this.mEfMWIS = bArr3;
                    }
                    z = true;
                    break;
                case 8:
                    AsyncResult asyncResult8 = (AsyncResult) message.obj;
                    byte[] bArr4 = (byte[]) asyncResult8.result;
                    log("EF_CPHS_MWI: " + IccUtils.bytesToHexString(bArr4));
                    if (asyncResult8.exception != null) {
                        log("EVENT_GET_VOICE_MAIL_INDICATOR_CPHS_DONE exception = " + asyncResult8.exception);
                    } else {
                        this.mEfCPHS_MWI = bArr4;
                    }
                    z = true;
                    break;
                case 9:
                    this.mMncLength = 0;
                    if (!this.mCarrierTestOverride.isInTestMode()) {
                        AsyncResult asyncResult9 = (AsyncResult) message.obj;
                        byte[] bArr5 = (byte[]) asyncResult9.result;
                        if (asyncResult9.exception == null) {
                            log("EF_AD: " + IccUtils.bytesToHexString(bArr5));
                            if (bArr5.length < 3) {
                                log("Corrupt AD data on SIM");
                            } else if (bArr5.length == 3) {
                                log("MNC length not present in EF_AD");
                            } else {
                                int i2 = bArr5[3] & 15;
                                if (i2 != 2 && i2 != 3) {
                                    log("Received invalid or unset MNC Length=" + i2);
                                }
                                this.mMncLength = i2;
                            }
                        }
                    }
                    updateOperatorPlmn();
                    z = true;
                    break;
                case 10:
                    AsyncResult asyncResult10 = (AsyncResult) message.obj;
                    if (asyncResult10.exception != null) {
                        log("Invalid or missing EF[MSISDN]");
                    } else {
                        AdnRecord adnRecord4 = (AdnRecord) asyncResult10.result;
                        this.mMsisdn = adnRecord4.getNumber();
                        this.mMsisdnTag = adnRecord4.getAlphaTag();
                        log("MSISDN: " + Rlog.pii(LOG_TAG, this.mMsisdn));
                    }
                    z = true;
                    break;
                case 12:
                    getSpnFsm(false, (AsyncResult) message.obj);
                    z = true;
                    break;
                case 13:
                    AsyncResult asyncResult11 = (AsyncResult) message.obj;
                    byte[] bArr6 = (byte[]) asyncResult11.result;
                    if (asyncResult11.exception == null) {
                        parseEfSpdi(bArr6);
                    }
                    z = true;
                    break;
                case 14:
                    Throwable th2 = ((AsyncResult) message.obj).exception;
                    if (th2 != null) {
                        logw("update failed. ", th2);
                        break;
                    }
                    break;
                case 15:
                    AsyncResult asyncResult12 = (AsyncResult) message.obj;
                    if (asyncResult12.exception == null) {
                        parseEfPnn((ArrayList) asyncResult12.result);
                    }
                    z = true;
                    break;
                case 16:
                    AsyncResult asyncResult13 = (AsyncResult) message.obj;
                    if (asyncResult13.exception == null) {
                        parseEfOpl((ArrayList) asyncResult13.result);
                    }
                    z = true;
                    break;
                case 17:
                    AsyncResult asyncResult14 = (AsyncResult) message.obj;
                    byte[] bArr7 = (byte[]) asyncResult14.result;
                    if (asyncResult14.exception == null) {
                        this.mUsimServiceTable = new UsimServiceTable(bArr7);
                        log("SST: " + this.mUsimServiceTable);
                    }
                    z = true;
                    break;
                case 18:
                    AsyncResult asyncResult15 = (AsyncResult) message.obj;
                    if (asyncResult15.exception == null) {
                        handleSmses((ArrayList) asyncResult15.result);
                    }
                    z = true;
                    break;
                case 19:
                    log("marked read: sms " + message.arg1);
                    break;
                case 20:
                    AsyncResult asyncResult16 = (AsyncResult) message.obj;
                    log("EVENT_SET_MBDN_DONE ex:" + asyncResult16.exception);
                    if (asyncResult16.exception == null) {
                        Object obj = asyncResult16.result;
                        if (obj != null && (adnRecord = (AdnRecord) obj) != null) {
                            this.mNewVoiceMailTag = adnRecord.mAlphaTag;
                        }
                        this.mVoiceMailNum = this.mNewVoiceMailNum;
                        this.mVoiceMailTag = this.mNewVoiceMailTag;
                    }
                    if (isCphsMailboxEnabled()) {
                        AdnRecord adnRecord5 = new AdnRecord(this.mVoiceMailTag, this.mVoiceMailNum);
                        Object obj2 = asyncResult16.userObj;
                        Message message3 = (Message) obj2;
                        if (asyncResult16.exception != null || obj2 == null) {
                            message2 = message3;
                        } else {
                            AsyncResult.forMessage((Message) obj2).exception = null;
                            ((Message) asyncResult16.userObj).sendToTarget();
                            log("Callback with MBDN successful.");
                        }
                        new AdnRecordLoader(this.mFh).updateEF(adnRecord5, IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, null, obtainMessage(25, 1, 0, message2));
                        break;
                    } else if (asyncResult16.userObj != null) {
                        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
                        if (asyncResult16.exception != null && carrierConfigManager != null) {
                            PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(SubscriptionManager.getSubscriptionId(this.mParentApp.getPhoneId()));
                            if (configForSubId != null && configForSubId.getBoolean("editable_voicemail_number_bool")) {
                                AsyncResult.forMessage((Message) asyncResult16.userObj).exception = new IccVmNotSupportedException("Update SIM voice mailbox error");
                            } else {
                                AsyncResult.forMessage((Message) asyncResult16.userObj).exception = asyncResult16.exception;
                            }
                        } else {
                            AsyncResult.forMessage((Message) asyncResult16.userObj).exception = asyncResult16.exception;
                        }
                        ((Message) asyncResult16.userObj).sendToTarget();
                        break;
                    }
                    break;
                case 21:
                    AsyncResult asyncResult17 = (AsyncResult) message.obj;
                    Integer num = (Integer) asyncResult17.result;
                    if (asyncResult17.exception == null && num != null) {
                        log("READ EF_SMS RECORD index=" + num);
                        this.mFh.loadEFLinearFixed(IccConstants.EF_SMS, num.intValue(), obtainMessage(22));
                        break;
                    }
                    loge("Error on SMS_ON_SIM with exp " + asyncResult17.exception + " index " + num);
                case 22:
                    AsyncResult asyncResult18 = (AsyncResult) message.obj;
                    if (asyncResult18.exception == null) {
                        handleSms((byte[]) asyncResult18.result);
                        break;
                    } else {
                        loge("Error on GET_SMS with exp " + asyncResult18.exception);
                        break;
                    }
                case 23:
                case 27:
                case 28:
                case 29:
                case 31:
                case 35:
                case 44:
                case 45:
                default:
                    super.handleMessage(message);
                    break;
                case 24:
                    AsyncResult asyncResult19 = (AsyncResult) message.obj;
                    byte[] bArr8 = (byte[]) asyncResult19.result;
                    if (asyncResult19.exception != null) {
                        this.mEfCff = null;
                    } else {
                        log("EF_CFF_CPHS: " + IccUtils.bytesToHexString(bArr8));
                        this.mEfCff = bArr8;
                    }
                    z = true;
                    break;
                case 25:
                    AsyncResult asyncResult20 = (AsyncResult) message.obj;
                    if (asyncResult20.exception == null) {
                        Object obj3 = asyncResult20.result;
                        if (obj3 != null && (adnRecord2 = (AdnRecord) obj3) != null) {
                            this.mNewVoiceMailTag = adnRecord2.mAlphaTag;
                        }
                        this.mVoiceMailNum = this.mNewVoiceMailNum;
                        this.mVoiceMailTag = this.mNewVoiceMailTag;
                    } else {
                        log("Set CPHS MailBox with exception: " + asyncResult20.exception);
                    }
                    if (asyncResult20.userObj != null) {
                        log("Callback with CPHS MB successful.");
                        AsyncResult.forMessage((Message) asyncResult20.userObj).exception = asyncResult20.exception;
                        ((Message) asyncResult20.userObj).sendToTarget();
                        break;
                    }
                    break;
                case 26:
                    AsyncResult asyncResult21 = (AsyncResult) message.obj;
                    if (asyncResult21.exception == null) {
                        this.mCphsInfo = (byte[]) asyncResult21.result;
                        log("iCPHS: " + IccUtils.bytesToHexString(this.mCphsInfo));
                    }
                    z = true;
                    break;
                case 30:
                    AsyncResult asyncResult22 = (AsyncResult) message.obj;
                    if (asyncResult22.exception == null) {
                        this.mMsisdn = this.mNewMsisdn;
                        this.mMsisdnTag = this.mNewMsisdnTag;
                        log("Success to update EF[MSISDN]");
                    }
                    Object obj4 = asyncResult22.userObj;
                    if (obj4 != null) {
                        AsyncResult.forMessage((Message) obj4).exception = asyncResult22.exception;
                        ((Message) asyncResult22.userObj).sendToTarget();
                        break;
                    }
                    break;
                case 32:
                    AsyncResult asyncResult23 = (AsyncResult) message.obj;
                    byte[] bArr9 = (byte[]) asyncResult23.result;
                    if (asyncResult23.exception != null) {
                        this.mEfCfis = null;
                    } else {
                        log("EF_CFIS: " + IccUtils.bytesToHexString(bArr9));
                        this.mEfCfis = bArr9;
                    }
                    z = true;
                    break;
                case 33:
                    AsyncResult asyncResult24 = (AsyncResult) message.obj;
                    if (asyncResult24.exception != null) {
                        loge("Exception in fetching EF_CSP data " + asyncResult24.exception);
                    } else {
                        byte[] bArr10 = (byte[]) asyncResult24.result;
                        log("EF_CSP: " + IccUtils.bytesToHexString(bArr10));
                        handleEfCspData(bArr10);
                    }
                    z = true;
                    break;
                case 34:
                    AsyncResult asyncResult25 = (AsyncResult) message.obj;
                    byte[] bArr11 = (byte[]) asyncResult25.result;
                    if (asyncResult25.exception != null) {
                        loge("Exception in get GID1 " + asyncResult25.exception);
                        this.mGid1 = null;
                    } else {
                        this.mGid1 = IccUtils.bytesToHexString(bArr11);
                        log("GID1: " + this.mGid1);
                    }
                    z = true;
                    break;
                case 36:
                    AsyncResult asyncResult26 = (AsyncResult) message.obj;
                    byte[] bArr12 = (byte[]) asyncResult26.result;
                    if (asyncResult26.exception != null) {
                        loge("Exception in get GID2 " + asyncResult26.exception);
                        this.mGid2 = null;
                    } else {
                        this.mGid2 = IccUtils.bytesToHexString(bArr12);
                        log("GID2: " + this.mGid2);
                    }
                    z = true;
                    break;
                case 37:
                    AsyncResult asyncResult27 = (AsyncResult) message.obj;
                    byte[] bArr13 = (byte[]) asyncResult27.result;
                    if (asyncResult27.exception == null && bArr13 != null) {
                        log("Received a PlmnActRecord, raw=" + IccUtils.bytesToHexString(bArr13));
                        this.mPlmnActRecords = PlmnActRecord.getRecords(bArr13);
                        if (VDBG) {
                            log("PlmnActRecords=" + Arrays.toString(this.mPlmnActRecords));
                        }
                        z = true;
                        break;
                    }
                    loge("Failed getting User PLMN with Access Tech Records: " + asyncResult27.exception);
                    z = true;
                case 38:
                    AsyncResult asyncResult28 = (AsyncResult) message.obj;
                    byte[] bArr14 = (byte[]) asyncResult28.result;
                    if (asyncResult28.exception == null && bArr14 != null) {
                        log("Received a PlmnActRecord, raw=" + IccUtils.bytesToHexString(bArr14));
                        this.mOplmnActRecords = PlmnActRecord.getRecords(bArr14);
                        if (VDBG) {
                            log("OplmnActRecord[]=" + Arrays.toString(this.mOplmnActRecords));
                        }
                        z = true;
                        break;
                    }
                    loge("Failed getting Operator PLMN with Access Tech Records: " + asyncResult28.exception);
                    z = true;
                case 39:
                    AsyncResult asyncResult29 = (AsyncResult) message.obj;
                    byte[] bArr15 = (byte[]) asyncResult29.result;
                    if (asyncResult29.exception == null && bArr15 != null) {
                        log("Received a PlmnActRecord, raw=" + IccUtils.bytesToHexString(bArr15));
                        this.mHplmnActRecords = PlmnActRecord.getRecords(bArr15);
                        log("HplmnActRecord[]=" + Arrays.toString(this.mHplmnActRecords));
                        z = true;
                        break;
                    }
                    loge("Failed getting Home PLMN with Access Tech Records: " + asyncResult29.exception);
                    z = true;
                case 40:
                    AsyncResult asyncResult30 = (AsyncResult) message.obj;
                    byte[] bArr16 = (byte[]) asyncResult30.result;
                    if (asyncResult30.exception == null && bArr16 != null) {
                        this.mEhplmns = parseBcdPlmnList(bArr16, "Equivalent Home");
                        z = true;
                        break;
                    }
                    loge("Failed getting Equivalent Home PLMNs: " + asyncResult30.exception);
                    z = true;
                case 41:
                    AsyncResult asyncResult31 = (AsyncResult) message.obj;
                    byte[] bArr17 = (byte[]) asyncResult31.result;
                    if (asyncResult31.exception == null && bArr17 != null) {
                        this.mFplmns = parseBcdPlmnList(bArr17, "Forbidden");
                        if (message.arg1 == 1238273) {
                            if (VDBG) {
                                logv("getForbiddenPlmns(): send async response");
                            }
                            Message message4 = (Message) retrievePendingTransaction(Integer.valueOf(message.arg2)).first;
                            if (message4 != null) {
                                Throwable th3 = asyncResult31.exception;
                                if (th3 == null && bArr17 != null && (strArr = this.mFplmns) != null) {
                                    AsyncResult.forMessage(message4, Arrays.copyOf(strArr, strArr.length), (Throwable) null);
                                } else {
                                    AsyncResult.forMessage(message4, (Object) null, th3);
                                }
                                message4.sendToTarget();
                                break;
                            } else {
                                loge("Failed to retrieve a response message for FPLMN");
                                break;
                            }
                        }
                        z = true;
                        break;
                    }
                    loge("Failed getting Forbidden PLMNs: " + asyncResult31.exception);
                    if (message.arg1 == 1238273) {
                    }
                    z = true;
                case 42:
                    AsyncResult asyncResult32 = (AsyncResult) message.obj;
                    if (asyncResult32.exception != null) {
                        Message message5 = (Message) asyncResult32.userObj;
                        AsyncResult.forMessage(message5).exception = asyncResult32.exception;
                        message5.sendToTarget();
                        break;
                    } else {
                        Pair<Message, Object> retrievePendingTransaction = retrievePendingTransaction(Integer.valueOf(message.arg2));
                        Message message6 = (Message) retrievePendingTransaction.first;
                        List list = (List) retrievePendingTransaction.second;
                        int intValue = ((Integer) asyncResult32.result).intValue();
                        if (intValue >= 0 && intValue % 3 == 0) {
                            int i3 = intValue / 3;
                            this.mFh.updateEFTransparent(IccConstants.EF_FPLMN, IccUtils.encodeFplmns(list, intValue), obtainMessage(43, message.arg1, storePendingTransaction(message6, list.size() <= i3 ? list : list.subList(0, i3))));
                            break;
                        }
                        loge("Failed to retrieve a correct fplmn size: " + intValue);
                        AsyncResult.forMessage(message6, -1, (Throwable) null);
                        message6.sendToTarget();
                    }
                    break;
                case 43:
                    if (((AsyncResult) message.obj).exception != null) {
                        loge("Failed setting Forbidden PLMNs: " + asyncResult.exception);
                        break;
                    } else {
                        Pair<Message, Object> retrievePendingTransaction2 = retrievePendingTransaction(Integer.valueOf(message.arg2));
                        Message message7 = (Message) retrievePendingTransaction2.first;
                        String[] strArr2 = (String[]) ((List) retrievePendingTransaction2.second).toArray(new String[0]);
                        this.mFplmns = strArr2;
                        if (message.arg1 == 1238273) {
                            AsyncResult.forMessage(message7, Integer.valueOf(strArr2.length), (Throwable) null);
                            message7.sendToTarget();
                        }
                        log("Successfully setted fplmns " + asyncResult.result);
                        break;
                    }
                case 46:
                    AsyncResult asyncResult33 = (AsyncResult) message.obj;
                    if (asyncResult33.exception != null) {
                        loge("Failed to read USIM EF_SMSS field error=" + asyncResult33.exception);
                    } else {
                        this.mSmssValues = (byte[]) asyncResult33.result;
                        if (VDBG) {
                            log("SIMRecords - EF_SMSS TPMR value = " + getSmssTpmrValue());
                        }
                    }
                    z = true;
                    break;
                case 47:
                    AsyncResult asyncResult34 = (AsyncResult) message.obj;
                    if (asyncResult34.exception != null) {
                        loge("Failed to read USIM EF_PSISMSC field error=" + asyncResult34.exception);
                    } else {
                        byte[] bArr18 = (byte[]) asyncResult34.result;
                        if (bArr18 != null && bArr18.length > 0) {
                            this.mPsiSmsc = parseEfPsiSmsc(bArr18);
                            if (VDBG) {
                                log("SIMRecords - EF_PSISMSC value = " + this.mPsiSmsc);
                            }
                        }
                    }
                    z = true;
                    break;
                case 48:
                    if (((AsyncResult) message.obj).exception != null) {
                        loge("Failed to read USIM EF_FDN field error=" + asyncResult2.exception);
                        break;
                    } else {
                        log("EF_FDN read successfully");
                        break;
                    }
            }
            if (!z) {
                return;
            }
        } catch (RuntimeException e2) {
            e = e2;
            z = true;
            logw("Exception parsing SIM record", e);
            if (!z) {
                return;
            }
            onRecordLoaded();
        } catch (Throwable th4) {
            th = th4;
            z = true;
            if (z) {
                onRecordLoaded();
            }
            throw th;
        }
        onRecordLoaded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfPlLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_PL";
        }

        private EfPlLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            SIMRecords sIMRecords = SIMRecords.this;
            sIMRecords.mEfPl = (byte[]) asyncResult.result;
            sIMRecords.log("EF_PL=" + IccUtils.bytesToHexString(SIMRecords.this.mEfPl));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfUsimLiLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_LI";
        }

        private EfUsimLiLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            SIMRecords sIMRecords = SIMRecords.this;
            sIMRecords.mEfLi = (byte[]) asyncResult.result;
            sIMRecords.log("EF_LI=" + IccUtils.bytesToHexString(SIMRecords.this.mEfLi));
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void handleFileUpdate(int i) {
        if (i != 28435) {
            if (i == 28437) {
                this.mRecordsToLoad++;
                log("[CSP] SIM Refresh for EF_CSP_CPHS");
                this.mFh.loadEFTransparent(IccConstants.EF_CSP_CPHS, obtainMessage(33));
                return;
            } else if (i == 28439) {
                this.mRecordsToLoad++;
                new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MAILBOX_CPHS, IccConstants.EF_EXT1, 1, obtainMessage(11));
                return;
            } else if (i == 28475) {
                log("SIM Refresh called for EF_FDN");
                this.mParentApp.queryFdn();
                this.mAdnCache.reset();
                return;
            } else if (i == 28480) {
                this.mRecordsToLoad++;
                log("SIM Refresh called for EF_MSISDN");
                new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MSISDN, getExtFromEf(IccConstants.EF_MSISDN), 1, obtainMessage(10));
                return;
            } else if (i == 28615) {
                this.mRecordsToLoad++;
                new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MBDN, IccConstants.EF_EXT6, this.mMailboxIndex, obtainMessage(6));
                return;
            } else if (i != 28619) {
                this.mLoaded.set(false);
                this.mAdnCache.reset();
                fetchSimRecords();
                return;
            }
        }
        log("SIM Refresh called for EF_CFIS or EF_CFF_CPHS");
        loadCallForwardingRecords();
    }

    private void dispatchGsmMessage(SmsMessage smsMessage) {
        this.mNewSmsRegistrants.notifyResult(smsMessage);
    }

    private void handleSms(byte[] bArr) {
        log("handleSms status : " + ((int) bArr[0]));
        if ((bArr[0] & 7) == 3) {
            int length = bArr.length - 1;
            byte[] bArr2 = new byte[length];
            System.arraycopy(bArr, 1, bArr2, 0, length);
            dispatchGsmMessage(SmsMessage.createFromPdu(bArr2, "3gpp"));
        }
    }

    private void handleSmses(ArrayList<byte[]> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            byte[] bArr = arrayList.get(i);
            log("handleSmses status " + i + ": " + ((int) bArr[0]));
            if ((bArr[0] & 7) == 3) {
                int length = bArr.length - 1;
                byte[] bArr2 = new byte[length];
                System.arraycopy(bArr, 1, bArr2, 0, length);
                dispatchGsmMessage(SmsMessage.createFromPdu(bArr2, "3gpp"));
                bArr[0] = 1;
            }
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void onRecordLoaded() {
        this.mRecordsToLoad--;
        log("onRecordLoaded " + this.mRecordsToLoad + " requested: " + this.mRecordsRequested);
        if (getRecordsLoaded()) {
            onAllRecordsLoaded();
        } else if (getLockedRecordsLoaded() || getNetworkLockedRecordsLoaded()) {
            onLockedAllRecordsLoaded();
        } else if (this.mRecordsToLoad < 0) {
            loge("recordsToLoad <0, programmer error suspected");
            this.mRecordsToLoad = 0;
        }
    }

    private void setVoiceCallForwardingFlagFromSimRecords() {
        if (validEfCfis(this.mEfCfis)) {
            this.mCallForwardingStatus = this.mEfCfis[1] & 1;
            log("EF_CFIS: callForwardingEnabled=" + this.mCallForwardingStatus);
            return;
        }
        byte[] bArr = this.mEfCff;
        if (bArr != null) {
            this.mCallForwardingStatus = (bArr[0] & 15) != 10 ? 0 : 1;
            log("EF_CFF: callForwardingEnabled=" + this.mCallForwardingStatus);
            return;
        }
        this.mCallForwardingStatus = -1;
        log("EF_CFIS and EF_CFF not valid. callForwardingEnabled=" + this.mCallForwardingStatus);
    }

    private void setSimLanguageFromEF() {
        if (Resources.getSystem().getBoolean(17891868)) {
            setSimLanguage(this.mEfLi, this.mEfPl);
        } else {
            log("Not using EF LI/EF PL");
        }
    }

    private void onLockedAllRecordsLoaded() {
        setSimLanguageFromEF();
        setVoiceCallForwardingFlagFromSimRecords();
        int i = this.mLockedRecordsReqReason;
        if (i == 1) {
            this.mLockedRecordsLoadedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        } else if (i == 2) {
            this.mNetworkLockedRecordsLoadedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        } else {
            loge("onLockedAllRecordsLoaded: unexpected mLockedRecordsReqReason " + this.mLockedRecordsReqReason);
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void onAllRecordsLoaded() {
        String str;
        log("record load complete");
        setSimLanguageFromEF();
        setVoiceCallForwardingFlagFromSimRecords();
        String operatorNumeric = getOperatorNumeric();
        if (!TextUtils.isEmpty(operatorNumeric)) {
            log("onAllRecordsLoaded set 'gsm.sim.operator.numeric' to operator='" + operatorNumeric + "'");
            this.mTelephonyManager.setSimOperatorNumericForPhone(this.mParentApp.getPhoneId(), operatorNumeric);
        } else {
            log("onAllRecordsLoaded empty 'gsm.sim.operator.numeric' skipping");
        }
        String imsi = getIMSI();
        if (!TextUtils.isEmpty(imsi) && imsi.length() >= 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("onAllRecordsLoaded set mcc imsi");
            if (VDBG) {
                str = "=" + imsi;
            } else {
                str = PhoneConfigurationManager.SSSS;
            }
            sb.append(str);
            log(sb.toString());
            this.mTelephonyManager.setSimCountryIsoForPhone(this.mParentApp.getPhoneId(), MccTable.countryCodeForMcc(imsi.substring(0, 3)));
        } else {
            log("onAllRecordsLoaded empty imsi skipping setting mcc");
        }
        setVoiceMailByCountry(operatorNumeric);
        this.mLoaded.set(true);
        this.mRecordsLoadedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
    }

    private void setVoiceMailByCountry(String str) {
        if (!this.mDestroyed.get() && this.mVmConfig.containsCarrier(str)) {
            this.mIsVoiceMailFixed = true;
            this.mVoiceMailNum = this.mVmConfig.getVoiceMailNumber(str);
            this.mVoiceMailTag = this.mVmConfig.getVoiceMailTag(str);
        }
    }

    public void getForbiddenPlmns(Message message) {
        this.mFh.loadEFTransparent(IccConstants.EF_FPLMN, obtainMessage(41, 1238273, storePendingTransaction(message)));
    }

    public void setForbiddenPlmns(Message message, List<String> list) {
        this.mFh.getEFTransparentRecordSize(IccConstants.EF_FPLMN, obtainMessage(42, 1238273, storePendingTransaction(message, list)));
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onReady() {
        fetchSimRecords();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onLocked() {
        log("only fetch EF_LI, EF_PL and EF_ICCID in locked state");
        super.onLocked();
        loadEfLiAndEfPl();
        this.mFh.loadEFTransparent(IccConstants.EF_ICCID, obtainMessage(4));
        this.mRecordsToLoad++;
    }

    private void loadEfLiAndEfPl() {
        if (this.mParentApp.getType() == IccCardApplicationStatus.AppType.APPTYPE_USIM) {
            this.mFh.loadEFTransparent(IccConstants.EF_LI, obtainMessage(100, new EfUsimLiLoaded()));
            this.mRecordsToLoad++;
            this.mFh.loadEFTransparent(IccConstants.EF_PL, obtainMessage(100, new EfPlLoaded()));
            this.mRecordsToLoad++;
        }
    }

    private void loadCallForwardingRecords() {
        this.mRecordsRequested = true;
        this.mFh.loadEFLinearFixed(IccConstants.EF_CFIS, 1, obtainMessage(32));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_CFF_CPHS, obtainMessage(24));
        this.mRecordsToLoad++;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void fetchSimRecords() {
        this.mRecordsRequested = true;
        log("fetchSimRecords " + this.mRecordsToLoad);
        this.mCi.getIMSIForApp(this.mParentApp.getAid(), obtainMessage(3));
        this.mRecordsToLoad = this.mRecordsToLoad + 1;
        this.mFh.loadEFTransparent(IccConstants.EF_ICCID, obtainMessage(4));
        this.mRecordsToLoad++;
        new AdnRecordLoader(this.mFh).loadFromEF(IccConstants.EF_MSISDN, getExtFromEf(IccConstants.EF_MSISDN), 1, obtainMessage(10));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixed(IccConstants.EF_MBI, 1, obtainMessage(5));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_AD, obtainMessage(9));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixed(IccConstants.EF_MWIS, 1, obtainMessage(7));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_VOICE_MAIL_INDICATOR_CPHS, obtainMessage(8));
        this.mRecordsToLoad++;
        loadCallForwardingRecords();
        getSpnFsm(true, null);
        this.mFh.loadEFTransparent(IccConstants.EF_SPDI, obtainMessage(13));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_PNN, obtainMessage(15));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_OPL, obtainMessage(16));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_SST, obtainMessage(17));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_INFO_CPHS, obtainMessage(26));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_CSP_CPHS, obtainMessage(33));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_GID1, obtainMessage(34));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_GID2, obtainMessage(36));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_PLMN_W_ACT, obtainMessage(37));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_OPLMN_W_ACT, obtainMessage(38));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_HPLMN_W_ACT, obtainMessage(39));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_EHPLMN, obtainMessage(40));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_FPLMN, obtainMessage(41, 1238272, -1));
        this.mRecordsToLoad++;
        loadEfLiAndEfPl();
        this.mFh.getEFLinearRecordSize(IccConstants.EF_SMS, obtainMessage(28));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixed(IccConstants.EF_PSISMSC, 1, obtainMessage(47));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_SMSS, obtainMessage(46));
        this.mRecordsToLoad++;
        log("fetchSimRecords " + this.mRecordsToLoad + " requested: " + this.mRecordsRequested);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public int getCarrierNameDisplayCondition() {
        return this.mCarrierNameDisplayCondition;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void getSpnFsm(boolean z, AsyncResult asyncResult) {
        GetSpnFsmState getSpnFsmState;
        if (z) {
            GetSpnFsmState getSpnFsmState2 = this.mSpnState;
            if (getSpnFsmState2 == GetSpnFsmState.READ_SPN_3GPP || getSpnFsmState2 == GetSpnFsmState.READ_SPN_CPHS || getSpnFsmState2 == GetSpnFsmState.READ_SPN_SHORT_CPHS || getSpnFsmState2 == (getSpnFsmState = GetSpnFsmState.INIT)) {
                this.mSpnState = GetSpnFsmState.INIT;
                return;
            }
            this.mSpnState = getSpnFsmState;
        }
        int i = C03291.f23xdc363d50[this.mSpnState.ordinal()];
        if (i == 1) {
            setServiceProviderName(null);
            this.mFh.loadEFTransparent(IccConstants.EF_SPN, obtainMessage(12));
            this.mRecordsToLoad++;
            this.mSpnState = GetSpnFsmState.READ_SPN_3GPP;
        } else if (i == 2) {
            if (asyncResult != null && asyncResult.exception == null) {
                byte[] bArr = (byte[]) asyncResult.result;
                this.mCarrierNameDisplayCondition = IccRecords.convertSpnDisplayConditionToBitmask(bArr[0] & 255);
                setServiceProviderName(IccUtils.adnStringFieldToString(bArr, 1, bArr.length - 1));
                String serviceProviderName = getServiceProviderName();
                if (serviceProviderName == null || serviceProviderName.length() == 0) {
                    this.mSpnState = GetSpnFsmState.READ_SPN_CPHS;
                } else {
                    log("Load EF_SPN: " + serviceProviderName + " carrierNameDisplayCondition: " + this.mCarrierNameDisplayCondition);
                    this.mTelephonyManager.setSimOperatorNameForPhone(this.mParentApp.getPhoneId(), serviceProviderName);
                    this.mSpnState = GetSpnFsmState.IDLE;
                }
            } else {
                this.mSpnState = GetSpnFsmState.READ_SPN_CPHS;
            }
            if (this.mSpnState == GetSpnFsmState.READ_SPN_CPHS) {
                this.mFh.loadEFTransparent(IccConstants.EF_SPN_CPHS, obtainMessage(12));
                this.mRecordsToLoad++;
                this.mCarrierNameDisplayCondition = 0;
            }
        } else if (i != 3) {
            if (i == 4) {
                if (asyncResult != null && asyncResult.exception == null) {
                    byte[] bArr2 = (byte[]) asyncResult.result;
                    setServiceProviderName(IccUtils.adnStringFieldToString(bArr2, 0, bArr2.length));
                    String serviceProviderName2 = getServiceProviderName();
                    if (serviceProviderName2 == null || serviceProviderName2.length() == 0) {
                        log("No SPN loaded in either CHPS or 3GPP");
                    } else {
                        this.mCarrierNameDisplayCondition = 0;
                        log("Load EF_SPN_SHORT_CPHS: " + serviceProviderName2);
                        this.mTelephonyManager.setSimOperatorNameForPhone(this.mParentApp.getPhoneId(), serviceProviderName2);
                    }
                } else {
                    setServiceProviderName(null);
                    log("No SPN loaded in either CHPS or 3GPP");
                }
                this.mSpnState = GetSpnFsmState.IDLE;
                return;
            }
            this.mSpnState = GetSpnFsmState.IDLE;
        } else {
            if (asyncResult != null && asyncResult.exception == null) {
                byte[] bArr3 = (byte[]) asyncResult.result;
                setServiceProviderName(IccUtils.adnStringFieldToString(bArr3, 0, bArr3.length));
                String serviceProviderName3 = getServiceProviderName();
                if (serviceProviderName3 == null || serviceProviderName3.length() == 0) {
                    this.mSpnState = GetSpnFsmState.READ_SPN_SHORT_CPHS;
                } else {
                    this.mCarrierNameDisplayCondition = 0;
                    log("Load EF_SPN_CPHS: " + serviceProviderName3);
                    this.mTelephonyManager.setSimOperatorNameForPhone(this.mParentApp.getPhoneId(), serviceProviderName3);
                    this.mSpnState = GetSpnFsmState.IDLE;
                }
            } else {
                this.mSpnState = GetSpnFsmState.READ_SPN_SHORT_CPHS;
            }
            if (this.mSpnState == GetSpnFsmState.READ_SPN_SHORT_CPHS) {
                this.mFh.loadEFTransparent(IccConstants.EF_SPN_SHORT_CPHS, obtainMessage(12));
                this.mRecordsToLoad++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.uicc.SIMRecords$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C03291 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$uicc$SIMRecords$GetSpnFsmState */
        static final /* synthetic */ int[] f23xdc363d50;

        static {
            int[] iArr = new int[GetSpnFsmState.values().length];
            f23xdc363d50 = iArr;
            try {
                iArr[GetSpnFsmState.INIT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f23xdc363d50[GetSpnFsmState.READ_SPN_3GPP.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f23xdc363d50[GetSpnFsmState.READ_SPN_CPHS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f23xdc363d50[GetSpnFsmState.READ_SPN_SHORT_CPHS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private void parseEfSpdi(byte[] bArr) {
        byte[] bArr2;
        SimTlv simTlv = new SimTlv(bArr, 0, bArr.length);
        while (true) {
            if (!simTlv.isValidObject()) {
                bArr2 = null;
                break;
            }
            if (simTlv.getTag() == 163) {
                simTlv = new SimTlv(simTlv.getData(), 0, simTlv.getData().length);
            }
            if (simTlv.getTag() != 128) {
                simTlv.nextObject();
            } else {
                bArr2 = simTlv.getData();
                break;
            }
        }
        if (bArr2 == null) {
            return;
        }
        ArrayList arrayList = new ArrayList(bArr2.length / 3);
        for (int i = 0; i + 2 < bArr2.length; i += 3) {
            String bcdPlmnToString = IccUtils.bcdPlmnToString(bArr2, i);
            if (!TextUtils.isEmpty(bcdPlmnToString)) {
                arrayList.add(bcdPlmnToString);
            }
        }
        log("parseEfSpdi: " + arrayList);
        this.mSpdi = (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private void parseEfPnn(ArrayList<byte[]> arrayList) {
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        ArrayList arrayList2 = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            byte[] bArr = arrayList.get(i);
            SimTlv simTlv = new SimTlv(bArr, 0, bArr.length);
            String str = null;
            String str2 = null;
            while (simTlv.isValidObject()) {
                int tag = simTlv.getTag();
                if (tag == 67) {
                    str = IccUtils.networkNameToString(simTlv.getData(), 0, simTlv.getData().length);
                } else if (tag == 69) {
                    str2 = IccUtils.networkNameToString(simTlv.getData(), 0, simTlv.getData().length);
                }
                simTlv.nextObject();
            }
            arrayList2.add(new IccRecords.PlmnNetworkName(str, str2));
        }
        log("parseEfPnn: " + arrayList2);
        IccRecords.PlmnNetworkName[] plmnNetworkNameArr = (IccRecords.PlmnNetworkName[]) arrayList2.toArray(new IccRecords.PlmnNetworkName[0]);
        this.mPnns = plmnNetworkNameArr;
        if (plmnNetworkNameArr.length > 0) {
            this.mPnnHomeName = plmnNetworkNameArr[0].getName();
        }
    }

    private void parseEfOpl(ArrayList<byte[]> arrayList) {
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        ArrayList arrayList2 = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            byte[] bArr = arrayList.get(i);
            if (bArr.length != 8) {
                loge("Invalid length for OPL record " + IccUtils.bytesToHexString(bArr));
            } else {
                String bcdPlmnToString = IccUtils.bcdPlmnToString(bArr, 0);
                if (bcdPlmnToString.length() < 5) {
                    loge("Invalid length for decoded PLMN " + bcdPlmnToString);
                } else {
                    arrayList2.add(new IccRecords.OperatorPlmnInfo(bcdPlmnToString, IccUtils.bytesToInt(bArr, 3, 2), IccUtils.bytesToInt(bArr, 5, 2), IccUtils.bytesToInt(bArr, 7, 1)));
                }
            }
        }
        log("parseEfOpl: " + arrayList2);
        this.mOpl = (IccRecords.OperatorPlmnInfo[]) arrayList2.toArray(new IccRecords.OperatorPlmnInfo[0]);
    }

    private String[] parseBcdPlmnList(byte[] bArr, String str) {
        log("Received " + str + " PLMNs, raw=" + IccUtils.bytesToHexString(bArr));
        if (bArr.length == 0 || bArr.length % 3 != 0) {
            loge("Received invalid " + str + " PLMN list");
            return null;
        }
        int length = bArr.length / 3;
        String[] strArr = new String[length];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            String bcdPlmnToString = IccUtils.bcdPlmnToString(bArr, i2 * 3);
            strArr[i] = bcdPlmnToString;
            if (!TextUtils.isEmpty(bcdPlmnToString)) {
                i++;
            }
        }
        String[] strArr2 = (String[]) Arrays.copyOf(strArr, i);
        if (VDBG) {
            logv(str + " PLMNs: " + Arrays.toString(strArr2));
        }
        return strArr2;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isCphsMailboxEnabled() {
        byte[] bArr = this.mCphsInfo;
        return bArr != null && (bArr[1] & 48) == 48;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void log(String str) {
        if (this.mParentApp != null) {
            Rlog.d(LOG_TAG, "[SIMRecords-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.d(LOG_TAG, "[SIMRecords] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void loge(String str) {
        if (this.mParentApp != null) {
            Rlog.e(LOG_TAG, "[SIMRecords-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.e(LOG_TAG, "[SIMRecords] " + str);
    }

    protected void logw(String str, Throwable th) {
        if (this.mParentApp != null) {
            Rlog.w(LOG_TAG, "[SIMRecords-" + this.mParentApp.getPhoneId() + "] " + str, th);
            return;
        }
        Rlog.w(LOG_TAG, "[SIMRecords] " + str, th);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void logv(String str) {
        if (this.mParentApp != null) {
            Rlog.v(LOG_TAG, "[SIMRecords-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.v(LOG_TAG, "[SIMRecords] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public boolean isCspPlmnEnabled() {
        return this.mCspPlmnEnabled;
    }

    private void handleEfCspData(byte[] bArr) {
        int length = bArr.length / 2;
        this.mCspPlmnEnabled = true;
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            if (bArr[i2] == -64) {
                StringBuilder sb = new StringBuilder();
                sb.append("[CSP] found ValueAddedServicesGroup, value ");
                int i3 = i2 + 1;
                sb.append((int) bArr[i3]);
                log(sb.toString());
                if ((bArr[i3] & 128) == 128) {
                    this.mCspPlmnEnabled = true;
                    return;
                }
                this.mCspPlmnEnabled = false;
                log("[CSP] Set Automatic Network Selection");
                this.mNetworkSelectionModeAutomaticRegistrants.notifyRegistrants();
                return;
            }
        }
        log("[CSP] Value Added Service Group (0xC0), not found!");
    }

    public void loadFdnRecords() {
        UiccCardApplication uiccCardApplication = this.mParentApp;
        if (uiccCardApplication != null && uiccCardApplication.getIccFdnEnabled() && this.mParentApp.getIccFdnAvailable()) {
            log("Loading FdnRecords");
            this.mAdnCache.requestLoadAllAdnLike(IccConstants.EF_FDN, getExtFromEf(IccConstants.EF_FDN), obtainMessage(48));
        }
    }

    @VisibleForTesting
    public void setMailboxIndex(int i) {
        this.mMailboxIndex = i;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SIMRecords: " + this);
        printWriter.println(" extends:");
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println(" mVmConfig=" + this.mVmConfig);
        printWriter.println(" mCallForwardingStatus=" + this.mCallForwardingStatus);
        printWriter.println(" mSpnState=" + this.mSpnState);
        printWriter.println(" mCphsInfo=" + IccUtils.bytesToHexString(this.mCphsInfo));
        printWriter.println(" mCspPlmnEnabled=" + this.mCspPlmnEnabled);
        printWriter.println(" mEfMWIS[]=" + Arrays.toString(this.mEfMWIS));
        printWriter.println(" mEfCPHS_MWI[]=" + Arrays.toString(this.mEfCPHS_MWI));
        printWriter.println(" mEfCff[]=" + Arrays.toString(this.mEfCff));
        printWriter.println(" mEfCfis[]=" + Arrays.toString(this.mEfCfis));
        printWriter.println(" mCarrierNameDisplayCondition=" + this.mCarrierNameDisplayCondition);
        printWriter.println(" mSpdi[]=" + Arrays.toString(this.mSpdi));
        printWriter.println(" mUsimServiceTable=" + this.mUsimServiceTable);
        printWriter.println(" mGid1=" + this.mGid1);
        if (this.mCarrierTestOverride.isInTestMode()) {
            printWriter.println(" mFakeGid1=" + this.mCarrierTestOverride.getFakeGid1());
        }
        printWriter.println(" mGid2=" + this.mGid2);
        if (this.mCarrierTestOverride.isInTestMode()) {
            printWriter.println(" mFakeGid2=" + this.mCarrierTestOverride.getFakeGid2());
        }
        printWriter.println(" mPnnHomeName=" + this.mPnnHomeName);
        if (this.mCarrierTestOverride.isInTestMode()) {
            printWriter.println(" mFakePnnHomeName=" + this.mCarrierTestOverride.getFakePnnHomeName());
        }
        printWriter.println(" mPlmnActRecords[]=" + Arrays.toString(this.mPlmnActRecords));
        printWriter.println(" mOplmnActRecords[]=" + Arrays.toString(this.mOplmnActRecords));
        printWriter.println(" mHplmnActRecords[]=" + Arrays.toString(this.mHplmnActRecords));
        printWriter.println(" mFplmns[]=" + Arrays.toString(this.mFplmns));
        printWriter.println(" mEhplmns[]=" + Arrays.toString(this.mEhplmns));
        printWriter.println(" mPsismsc=" + this.mPsiSmsc);
        printWriter.println(" TPMR=" + getSmssTpmrValue());
        printWriter.flush();
    }
}
