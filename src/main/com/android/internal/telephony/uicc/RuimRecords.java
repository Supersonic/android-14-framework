package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.BitwiseInputStream;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes.dex */
public class RuimRecords extends IccRecords {
    boolean mCsimSpnDisplayCondition;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private byte[] mEFli;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private byte[] mEFpl;
    private String mHomeNetworkId;
    private String mHomeSystemId;
    private String mMdn;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mMin;
    private String mMin2Min1;
    private String mMyMobileNumber;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mNai;
    private boolean mOtaCommited;
    private String mPrlVersion;

    private static boolean isPrintableAscii(char c) {
        return (' ' <= c && c <= '~') || c == '\r' || c == '\n';
    }

    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    public String toString() {
        return "RuimRecords: " + super.toString() + " m_ota_commited" + this.mOtaCommited + " mMyMobileNumber=xxxx mMin2Min1=" + this.mMin2Min1 + " mPrlVersion=" + this.mPrlVersion + " mEFpl=" + IccUtils.bytesToHexString(this.mEFpl) + " mEFli=" + IccUtils.bytesToHexString(this.mEFli) + " mCsimSpnDisplayCondition=" + this.mCsimSpnDisplayCondition + " mMdn=" + this.mMdn + " mMin=" + this.mMin + " mHomeSystemId=" + this.mHomeSystemId + " mHomeNetworkId=" + this.mHomeNetworkId;
    }

    public RuimRecords(UiccCardApplication uiccCardApplication, Context context, CommandsInterface commandsInterface) {
        super(uiccCardApplication, context, commandsInterface);
        this.mOtaCommited = false;
        this.mEFpl = null;
        this.mEFli = null;
        this.mCsimSpnDisplayCondition = false;
        this.mAdnCache = new AdnRecordCache(this.mFh);
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mRecordsToLoad = 0;
        resetRecords();
        log("RuimRecords X ctor this=" + this);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dispose() {
        log("Disposing RuimRecords " + this);
        resetRecords();
        super.dispose();
    }

    protected void finalize() {
        log("RuimRecords finalized");
    }

    protected void resetRecords() {
        this.mMncLength = -1;
        log("setting0 mMncLength" + this.mMncLength);
        this.mIccId = null;
        this.mFullIccId = null;
        this.mAdnCache.reset();
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mLoaded.set(false);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getMdnNumber() {
        return this.mMyMobileNumber;
    }

    public String getCdmaMin() {
        return this.mMin2Min1;
    }

    public String getPrlVersion() {
        return this.mPrlVersion;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public String getNAI() {
        return this.mNai;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMailNumber(String str, String str2, Message message) {
        AsyncResult.forMessage(message).exception = new IccException("setVoiceMailNumber not implemented");
        message.sendToTarget();
        loge("method setVoiceMailNumber is not implemented");
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onRefresh(boolean z, int[] iArr) {
        if (z) {
            fetchRuimRecords();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getRUIMOperatorNumeric() {
        String imsi = getIMSI();
        if (imsi == null) {
            return null;
        }
        int i = this.mMncLength;
        if (i != -1 && i != 0) {
            return imsi.substring(0, i + 3);
        }
        return imsi.substring(0, MccTable.smallestDigitsMccForMnc(Integer.parseInt(imsi.substring(0, 3))) + 3);
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
            RuimRecords.this.mEFpl = (byte[]) asyncResult.result;
            RuimRecords ruimRecords = RuimRecords.this;
            ruimRecords.log("EF_PL=" + IccUtils.bytesToHexString(RuimRecords.this.mEFpl));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimLiLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_LI";
        }

        private EfCsimLiLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            RuimRecords.this.mEFli = (byte[]) asyncResult.result;
            for (int i = 0; i < RuimRecords.this.mEFli.length; i += 2) {
                int i2 = i + 1;
                switch (RuimRecords.this.mEFli[i2]) {
                    case 1:
                        RuimRecords.this.mEFli[i] = 101;
                        RuimRecords.this.mEFli[i2] = 110;
                        break;
                    case 2:
                        RuimRecords.this.mEFli[i] = 102;
                        RuimRecords.this.mEFli[i2] = 114;
                        break;
                    case 3:
                        RuimRecords.this.mEFli[i] = 101;
                        RuimRecords.this.mEFli[i2] = 115;
                        break;
                    case 4:
                        RuimRecords.this.mEFli[i] = 106;
                        RuimRecords.this.mEFli[i2] = 97;
                        break;
                    case 5:
                        RuimRecords.this.mEFli[i] = 107;
                        RuimRecords.this.mEFli[i2] = 111;
                        break;
                    case 6:
                        RuimRecords.this.mEFli[i] = 122;
                        RuimRecords.this.mEFli[i2] = 104;
                        break;
                    case 7:
                        RuimRecords.this.mEFli[i] = 104;
                        RuimRecords.this.mEFli[i2] = 101;
                        break;
                    default:
                        RuimRecords.this.mEFli[i] = 32;
                        RuimRecords.this.mEFli[i2] = 32;
                        break;
                }
            }
            RuimRecords ruimRecords = RuimRecords.this;
            ruimRecords.log("EF_LI=" + IccUtils.bytesToHexString(RuimRecords.this.mEFli));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimSpnLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_SPN";
        }

        private EfCsimSpnLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            byte[] bArr = (byte[]) asyncResult.result;
            RuimRecords.this.log("CSIM_SPN=" + IccUtils.bytesToHexString(bArr));
            RuimRecords.this.mCsimSpnDisplayCondition = (bArr[0] & 1) != 0;
            byte b = bArr[1];
            byte b2 = bArr[2];
            byte[] bArr2 = new byte[32];
            System.arraycopy(bArr, 3, bArr2, 0, bArr.length - 3 < 32 ? bArr.length - 3 : 32);
            int i = 0;
            while (i < 32 && (bArr2[i] & 255) != 255) {
                i++;
            }
            if (i == 0) {
                RuimRecords.this.setServiceProviderName(PhoneConfigurationManager.SSSS);
                return;
            }
            try {
            } catch (Exception e) {
                RuimRecords.this.log("spn decode error: " + e);
            }
            if (b != 0) {
                if (b != 2) {
                    if (b != 3) {
                        if (b == 4) {
                            RuimRecords.this.setServiceProviderName(new String(bArr2, 0, i, "utf-16"));
                        } else if (b != 8) {
                            if (b != 9) {
                                RuimRecords.this.log("SPN encoding not supported");
                            }
                        }
                    }
                    RuimRecords.this.setServiceProviderName(GsmAlphabet.gsm7BitPackedToString(bArr2, 0, (i * 8) / 7));
                } else {
                    String str = new String(bArr2, 0, i, "US-ASCII");
                    if (RuimRecords.isPrintableAsciiOnly(str)) {
                        RuimRecords.this.setServiceProviderName(str);
                    } else {
                        RuimRecords.this.log("Some corruption in SPN decoding = " + str);
                        RuimRecords.this.log("Using ENCODING_GSM_7BIT_ALPHABET scheme...");
                        RuimRecords.this.setServiceProviderName(GsmAlphabet.gsm7BitPackedToString(bArr2, 0, (i * 8) / 7));
                    }
                }
                RuimRecords.this.log("spn=" + RuimRecords.this.getServiceProviderName());
                RuimRecords.this.log("spnCondition=" + RuimRecords.this.mCsimSpnDisplayCondition);
                RuimRecords ruimRecords = RuimRecords.this;
                ruimRecords.mTelephonyManager.setSimOperatorNameForPhone(ruimRecords.mParentApp.getPhoneId(), RuimRecords.this.getServiceProviderName());
            }
            RuimRecords.this.setServiceProviderName(new String(bArr2, 0, i, "ISO-8859-1"));
            RuimRecords.this.log("spn=" + RuimRecords.this.getServiceProviderName());
            RuimRecords.this.log("spnCondition=" + RuimRecords.this.mCsimSpnDisplayCondition);
            RuimRecords ruimRecords2 = RuimRecords.this;
            ruimRecords2.mTelephonyManager.setSimOperatorNameForPhone(ruimRecords2.mParentApp.getPhoneId(), RuimRecords.this.getServiceProviderName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPrintableAsciiOnly(CharSequence charSequence) {
        int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!isPrintableAscii(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimMdnLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_MDN";
        }

        private EfCsimMdnLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            byte[] bArr = (byte[]) asyncResult.result;
            RuimRecords ruimRecords = RuimRecords.this;
            ruimRecords.log("CSIM_MDN=" + IccUtils.bytesToHexString(bArr));
            RuimRecords.this.mMdn = IccUtils.cdmaBcdToString(bArr, 1, bArr[0] & 15);
            RuimRecords ruimRecords2 = RuimRecords.this;
            ruimRecords2.log("CSIM MDN=" + RuimRecords.this.mMdn);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class EfCsimImsimLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_IMSIM";
        }

        public EfCsimImsimLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            byte[] bArr = (byte[]) asyncResult.result;
            if (bArr == null || bArr.length < 10) {
                RuimRecords.this.loge("Invalid IMSI from EF_CSIM_IMSIM");
                return;
            }
            RuimRecords ruimRecords = RuimRecords.this;
            ruimRecords.log("data=" + Rlog.pii("RuimRecords", IccUtils.bytesToHexString(bArr)));
            if ((bArr[7] & 128) == 128) {
                String decodeImsi = decodeImsi(bArr);
                if (TextUtils.isEmpty(RuimRecords.this.mImsi)) {
                    RuimRecords ruimRecords2 = RuimRecords.this;
                    ruimRecords2.mImsi = decodeImsi;
                    ruimRecords2.log("IMSI=" + Rlog.pii("RuimRecords", RuimRecords.this.mImsi));
                }
                RuimRecords.this.mMin = decodeImsi.substring(5, 15);
                RuimRecords ruimRecords3 = RuimRecords.this;
                ruimRecords3.log("min present=" + Rlog.pii("RuimRecords", RuimRecords.this.mMin));
                return;
            }
            RuimRecords.this.log("min not present");
        }

        private int decodeImsiDigits(int i, int i2) {
            int i3 = 1;
            for (int i4 = 0; i4 < i2; i4++) {
                i += i3;
                if ((i / i3) % 10 == 0) {
                    i -= i3 * 10;
                }
                i3 *= 10;
            }
            return i;
        }

        @VisibleForTesting
        public String decodeImsi(byte[] bArr) {
            int decodeImsiDigits = decodeImsiDigits(((bArr[9] & 3) << 8) | (bArr[8] & 255), 3);
            int decodeImsiDigits2 = decodeImsiDigits(bArr[6] & Byte.MAX_VALUE, 2);
            int i = ((bArr[2] & 3) << 8) + (bArr[1] & 255);
            byte b = bArr[4];
            int i2 = (((bArr[5] & 255) << 8) | (b & 255)) >> 6;
            int i3 = (b >> 2) & 15;
            if (i3 > 9) {
                i3 = 0;
            }
            int decodeImsiDigits3 = decodeImsiDigits(i, 3);
            int decodeImsiDigits4 = decodeImsiDigits(i2, 3);
            int decodeImsiDigits5 = decodeImsiDigits((bArr[3] & 255) | ((b & 3) << 8), 3);
            StringBuilder sb = new StringBuilder();
            Locale locale = Locale.US;
            sb.append(String.format(locale, "%03d", Integer.valueOf(decodeImsiDigits)));
            sb.append(String.format(locale, "%02d", Integer.valueOf(decodeImsiDigits2)));
            sb.append(String.format(locale, "%03d", Integer.valueOf(decodeImsiDigits3)));
            sb.append(String.format(locale, "%03d", Integer.valueOf(decodeImsiDigits4)));
            sb.append(String.format(locale, "%d", Integer.valueOf(i3)));
            sb.append(String.format(locale, "%03d", Integer.valueOf(decodeImsiDigits5)));
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimCdmaHomeLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_CDMAHOME";
        }

        private EfCsimCdmaHomeLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            ArrayList arrayList = (ArrayList) asyncResult.result;
            RuimRecords ruimRecords = RuimRecords.this;
            ruimRecords.log("CSIM_CDMAHOME data size=" + arrayList.size());
            if (arrayList.isEmpty()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                byte[] bArr = (byte[]) it.next();
                if (bArr.length == 5) {
                    int i = ((bArr[1] & 255) << 8) | (bArr[0] & 255);
                    int i2 = bArr[2] & 255;
                    sb.append(i);
                    sb.append(',');
                    sb2.append(i2 | ((bArr[3] & 255) << 8));
                    sb2.append(',');
                }
            }
            sb.setLength(sb.length() - 1);
            sb2.setLength(sb2.length() - 1);
            RuimRecords.this.mHomeSystemId = sb.toString();
            RuimRecords.this.mHomeNetworkId = sb2.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimEprlLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_EPRL";
        }

        private EfCsimEprlLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            RuimRecords.this.onGetCSimEprlDone(asyncResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void onGetCSimEprlDone(AsyncResult asyncResult) {
        byte[] bArr = (byte[]) asyncResult.result;
        log("CSIM_EPRL=" + IccUtils.bytesToHexString(bArr));
        if (bArr.length > 3) {
            this.mPrlVersion = Integer.toString((bArr[3] & 255) | ((bArr[2] & 255) << 8));
        }
        log("CSIM PRL version=" + this.mPrlVersion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfCsimMipUppLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_CSIM_MIPUPP";
        }

        private EfCsimMipUppLoaded() {
        }

        boolean checkLengthLegal(int i, int i2) {
            if (i < i2) {
                Log.e("RuimRecords", "CSIM MIPUPP format error, length = " + i + "expected length at least =" + i2);
                return false;
            }
            return true;
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            byte[] bArr = (byte[]) asyncResult.result;
            if (bArr.length < 1) {
                Log.e("RuimRecords", "MIPUPP read error");
                return;
            }
            BitwiseInputStream bitwiseInputStream = new BitwiseInputStream(bArr);
            try {
                int read = bitwiseInputStream.read(8) << 3;
                if (checkLengthLegal(read, 1)) {
                    int i = read - 1;
                    if (bitwiseInputStream.read(1) == 1) {
                        if (!checkLengthLegal(i, 11)) {
                            return;
                        }
                        bitwiseInputStream.skip(11);
                        i -= 11;
                    }
                    if (checkLengthLegal(i, 4)) {
                        int read2 = bitwiseInputStream.read(4);
                        int i2 = i - 4;
                        for (int i3 = 0; i3 < read2 && checkLengthLegal(i2, 4); i3++) {
                            int read3 = bitwiseInputStream.read(4);
                            int i4 = i2 - 4;
                            if (!checkLengthLegal(i4, 8)) {
                                return;
                            }
                            int read4 = bitwiseInputStream.read(8);
                            int i5 = i4 - 8;
                            if (read3 == 0) {
                                if (checkLengthLegal(i5, read4 << 3)) {
                                    char[] cArr = new char[read4];
                                    for (int i6 = 0; i6 < read4; i6++) {
                                        cArr[i6] = (char) (bitwiseInputStream.read(8) & 255);
                                    }
                                    RuimRecords.this.mNai = new String(cArr);
                                    if (Log.isLoggable("RuimRecords", 2)) {
                                        Log.v("RuimRecords", "MIPUPP Nai = " + RuimRecords.this.mNai);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            int i7 = read4 << 3;
                            int i8 = i7 + CallFailCause.RECOVERY_ON_TIMER_EXPIRY;
                            if (!checkLengthLegal(i5, i8)) {
                                return;
                            }
                            bitwiseInputStream.skip(i7 + 101);
                            int i9 = i5 - i8;
                            if (bitwiseInputStream.read(1) == 1) {
                                if (!checkLengthLegal(i9, 32)) {
                                    return;
                                }
                                bitwiseInputStream.skip(32);
                                i9 -= 32;
                            }
                            if (!checkLengthLegal(i9, 5)) {
                                return;
                            }
                            bitwiseInputStream.skip(4);
                            i2 = (i9 - 4) - 1;
                            if (bitwiseInputStream.read(1) == 1) {
                                if (!checkLengthLegal(i2, 32)) {
                                    return;
                                }
                                bitwiseInputStream.skip(32);
                                i2 -= 32;
                            }
                        }
                    }
                }
            } catch (Exception unused) {
                Log.e("RuimRecords", "MIPUPP read Exception error!");
            }
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    public void handleMessage(Message message) {
        if (this.mDestroyed.get()) {
            loge("Received message " + message + "[" + message.what + "] while being destroyed. Ignoring.");
            return;
        }
        boolean z = false;
        try {
            try {
                int i = message.what;
                if (i == 4) {
                    log("Event EVENT_GET_DEVICE_IDENTITY_DONE Received");
                } else if (i == 5) {
                    try {
                        AsyncResult asyncResult = (AsyncResult) message.obj;
                        byte[] bArr = (byte[]) asyncResult.result;
                        if (asyncResult.exception == null) {
                            this.mIccId = IccUtils.bcdToString(bArr, 0, bArr.length);
                            this.mFullIccId = IccUtils.bchToString(bArr, 0, bArr.length);
                            log("iccid: " + SubscriptionInfo.givePrintableIccid(this.mFullIccId));
                        }
                        z = true;
                    } catch (RuntimeException e) {
                        e = e;
                        z = true;
                        Rlog.w("RuimRecords", "Exception parsing RUIM record", e);
                        if (!z) {
                            return;
                        }
                        onRecordLoaded();
                    } catch (Throwable th) {
                        th = th;
                        z = true;
                        if (z) {
                            onRecordLoaded();
                        }
                        throw th;
                    }
                } else if (i == 10) {
                    AsyncResult asyncResult2 = (AsyncResult) message.obj;
                    String[] strArr = (String[]) asyncResult2.result;
                    if (asyncResult2.exception == null) {
                        this.mMyMobileNumber = strArr[0];
                        this.mMin2Min1 = strArr[3];
                        this.mPrlVersion = strArr[4];
                        log("MDN: " + this.mMyMobileNumber + " MIN: " + this.mMin2Min1);
                    }
                } else if (i == 14) {
                    Throwable th2 = ((AsyncResult) message.obj).exception;
                    if (th2 != null) {
                        Rlog.i("RuimRecords", "RuimRecords update failed", th2);
                    }
                } else {
                    if (i != 21 && i != 22) {
                        switch (i) {
                            case 17:
                                log("Event EVENT_GET_SST_DONE Received");
                                break;
                            case 18:
                            case 19:
                                break;
                            default:
                                super.handleMessage(message);
                                break;
                        }
                    }
                    Rlog.w("RuimRecords", "Event not supported: " + message.what);
                }
                if (!z) {
                    return;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (RuntimeException e2) {
            e = e2;
        }
        onRecordLoaded();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static String[] getAssetLanguages(Context context) {
        String[] locales = context.getAssets().getLocales();
        String[] strArr = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            String str = locales[i];
            int indexOf = str.indexOf(45);
            if (indexOf < 0) {
                strArr[i] = str;
            } else {
                strArr[i] = str.substring(0, indexOf);
            }
        }
        return strArr;
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

    private void onLockedAllRecordsLoaded() {
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
        log("record load complete");
        if (Resources.getSystem().getBoolean(17891868)) {
            setSimLanguage(this.mEFli, this.mEFpl);
        }
        this.mLoaded.set(true);
        this.mRecordsLoadedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        if (TextUtils.isEmpty(this.mMdn)) {
            return;
        }
        int subscriptionId = SubscriptionManager.getSubscriptionId(this.mParentApp.getUiccProfile().getPhoneId());
        if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            SubscriptionManager.from(this.mContext).setDisplayNumber(this.mMdn, subscriptionId);
        } else {
            log("Cannot call setDisplayNumber: invalid subId");
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onReady() {
        fetchRuimRecords();
        this.mCi.getCDMASubscription(obtainMessage(10));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onLocked() {
        log("only fetch EF_ICCID in locked state");
        super.onLocked();
        this.mFh.loadEFTransparent(IccConstants.EF_ICCID, obtainMessage(5));
        this.mRecordsToLoad++;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void fetchRuimRecords() {
        this.mRecordsRequested = true;
        log("fetchRuimRecords " + this.mRecordsToLoad);
        this.mFh.loadEFTransparent(IccConstants.EF_ICCID, obtainMessage(5));
        this.mRecordsToLoad = this.mRecordsToLoad + 1;
        this.mFh.loadEFTransparent(IccConstants.EF_PL, obtainMessage(100, new EfPlLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(28474, obtainMessage(100, new EfCsimLiLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(28481, obtainMessage(100, new EfCsimSpnLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixed(IccConstants.EF_CSIM_MDN, 1, obtainMessage(100, new EfCsimMdnLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_CSIM_IMSIM, obtainMessage(100, new EfCsimImsimLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_CSIM_CDMAHOME, obtainMessage(100, new EfCsimCdmaHomeLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_CSIM_EPRL, 4, obtainMessage(100, new EfCsimEprlLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_CSIM_MIPUPP, obtainMessage(100, new EfCsimMipUppLoaded()));
        this.mRecordsToLoad++;
        this.mFh.getEFLinearRecordSize(IccConstants.EF_SMS, obtainMessage(28));
        this.mRecordsToLoad++;
        log("fetchRuimRecords " + this.mRecordsToLoad + " requested: " + this.mRecordsRequested);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public boolean isProvisioned() {
        if (TelephonyProperties.test_csim().orElse(Boolean.FALSE).booleanValue()) {
            return true;
        }
        UiccCardApplication uiccCardApplication = this.mParentApp;
        if (uiccCardApplication == null) {
            return false;
        }
        return (uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_CSIM && (this.mMdn == null || this.mMin == null)) ? false : true;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMessageWaiting(int i, int i2) {
        log("RuimRecords:setVoiceMessageWaiting - NOP for CDMA");
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public int getVoiceMessageCount() {
        log("RuimRecords:getVoiceMessageCount - NOP for CDMA");
        return 0;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void handleFileUpdate(int i) {
        this.mLoaded.set(false);
        this.mAdnCache.reset();
        fetchRuimRecords();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getMdn() {
        return this.mMdn;
    }

    public String getMin() {
        return this.mMin;
    }

    public String getSid() {
        return this.mHomeSystemId;
    }

    public String getNid() {
        return this.mHomeNetworkId;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean getCsimSpnDisplayCondition() {
        return this.mCsimSpnDisplayCondition;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void log(String str) {
        if (this.mParentApp != null) {
            Rlog.d("RuimRecords", "[RuimRecords-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.d("RuimRecords", "[RuimRecords] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void loge(String str) {
        if (this.mParentApp != null) {
            Rlog.e("RuimRecords", "[RuimRecords-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.e("RuimRecords", "[RuimRecords] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("RuimRecords: " + this);
        printWriter.println(" extends:");
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println(" mOtaCommited=" + this.mOtaCommited);
        printWriter.println(" mMyMobileNumber=" + this.mMyMobileNumber);
        printWriter.println(" mMin2Min1=" + this.mMin2Min1);
        printWriter.println(" mPrlVersion=" + this.mPrlVersion);
        printWriter.println(" mEFpl[]=" + Arrays.toString(this.mEFpl));
        printWriter.println(" mEFli[]=" + Arrays.toString(this.mEFli));
        printWriter.println(" mCsimSpnDisplayCondition=" + this.mCsimSpnDisplayCondition);
        printWriter.println(" mMdn=" + this.mMdn);
        printWriter.println(" mMin=" + this.mMin);
        printWriter.println(" mHomeSystemId=" + this.mHomeSystemId);
        printWriter.println(" mHomeNetworkId=" + this.mHomeNetworkId);
        printWriter.flush();
    }
}
