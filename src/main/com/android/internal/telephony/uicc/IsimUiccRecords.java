package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Message;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.gsm.SimTlv;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class IsimUiccRecords extends IccRecords implements IsimRecords {
    public static final String INTENT_ISIM_REFRESH = "com.android.intent.isim_refresh";
    protected static final String LOG_TAG = "IsimUiccRecords";
    private static final boolean VDBG = Rlog.isLoggable(LOG_TAG, 2);
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String auth_rsp;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mIsimDomain;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mIsimImpi;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String[] mIsimImpu;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mIsimIst;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String[] mIsimPcscf;

    @Override // com.android.internal.telephony.uicc.IccRecords
    public int getVoiceMessageCount() {
        return 0;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMailNumber(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void setVoiceMessageWaiting(int i, int i2) {
    }

    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    public String toString() {
        return "IsimUiccRecords: " + super.toString() + PhoneConfigurationManager.SSSS;
    }

    public IsimUiccRecords(UiccCardApplication uiccCardApplication, Context context, CommandsInterface commandsInterface) {
        super(uiccCardApplication, context, commandsInterface);
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mRecordsToLoad = 0;
        resetRecords();
        log("IsimUiccRecords X ctor this=" + this);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dispose() {
        log("Disposing " + this);
        resetRecords();
        super.dispose();
    }

    @Override // com.android.internal.telephony.uicc.IccRecords, android.os.Handler
    public void handleMessage(Message message) {
        if (this.mDestroyed.get()) {
            Rlog.e(LOG_TAG, "Received message " + message + "[" + message.what + "] while being destroyed. Ignoring.");
            return;
        }
        loge("IsimUiccRecords: handleMessage " + message + "[" + message.what + "] ");
        try {
            if (message.what == 31) {
                broadcastRefresh();
                super.handleMessage(message);
            } else {
                super.handleMessage(message);
            }
        } catch (RuntimeException e) {
            Rlog.w(LOG_TAG, "Exception parsing SIM record", e);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void fetchIsimRecords() {
        this.mRecordsRequested = true;
        this.mFh.loadEFTransparent(IccConstants.EF_IMPI, obtainMessage(100, new EfIsimImpiLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_IMPU, obtainMessage(100, new EfIsimImpuLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_DOMAIN, obtainMessage(100, new EfIsimDomainLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_IST, obtainMessage(100, new EfIsimIstLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_PCSCF, obtainMessage(100, new EfIsimPcscfLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFTransparent(IccConstants.EF_SMSS, obtainMessage(100, new EfIsimSmssLoaded()));
        this.mRecordsToLoad++;
        this.mFh.loadEFLinearFixed(IccConstants.EF_PSISMSC, 1, obtainMessage(100, new EfIsimPsiSmscLoaded()));
        this.mRecordsToLoad++;
        log("fetchIsimRecords " + this.mRecordsToLoad + " requested: " + this.mRecordsRequested);
    }

    protected void resetRecords() {
        this.mIsimImpi = null;
        this.mIsimDomain = null;
        this.mIsimImpu = null;
        this.mIsimIst = null;
        this.mIsimPcscf = null;
        this.auth_rsp = null;
        this.mRecordsRequested = false;
        this.mLockedRecordsReqReason = 0;
        this.mLoaded.set(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimImpiLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_IMPI";
        }

        private EfIsimImpiLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            IsimUiccRecords.this.mIsimImpi = IsimUiccRecords.isimTlvToString((byte[]) asyncResult.result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimImpuLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_IMPU";
        }

        private EfIsimImpuLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            ArrayList arrayList = (ArrayList) asyncResult.result;
            IsimUiccRecords isimUiccRecords = IsimUiccRecords.this;
            isimUiccRecords.log("EF_IMPU record count: " + arrayList.size());
            IsimUiccRecords.this.mIsimImpu = new String[arrayList.size()];
            Iterator it = arrayList.iterator();
            int i = 0;
            while (it.hasNext()) {
                IsimUiccRecords.this.mIsimImpu[i] = IsimUiccRecords.isimTlvToString((byte[]) it.next());
                i++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimDomainLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_DOMAIN";
        }

        private EfIsimDomainLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            IsimUiccRecords.this.mIsimDomain = IsimUiccRecords.isimTlvToString((byte[]) asyncResult.result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimIstLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_IST";
        }

        private EfIsimIstLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            IsimUiccRecords.this.mIsimIst = IccUtils.bytesToHexString((byte[]) asyncResult.result);
        }
    }

    @VisibleForTesting
    public EfIsimIstLoaded getIsimIstObject() {
        return new EfIsimIstLoaded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimSmssLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_SMSS";
        }

        private EfIsimSmssLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            IsimUiccRecords.this.mSmssValues = (byte[]) asyncResult.result;
            if (IsimUiccRecords.VDBG) {
                IsimUiccRecords isimUiccRecords = IsimUiccRecords.this;
                isimUiccRecords.log("IsimUiccRecords - EF_SMSS TPMR value = " + IsimUiccRecords.this.getSmssTpmrValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimPcscfLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_PCSCF";
        }

        private EfIsimPcscfLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            ArrayList arrayList = (ArrayList) asyncResult.result;
            IsimUiccRecords isimUiccRecords = IsimUiccRecords.this;
            isimUiccRecords.log("EF_PCSCF record count: " + arrayList.size());
            IsimUiccRecords.this.mIsimPcscf = new String[arrayList.size()];
            Iterator it = arrayList.iterator();
            int i = 0;
            while (it.hasNext()) {
                IsimUiccRecords.this.mIsimPcscf[i] = IsimUiccRecords.isimTlvToString((byte[]) it.next());
                i++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EfIsimPsiSmscLoaded implements IccRecords.IccRecordLoaded {
        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public String getEfName() {
            return "EF_ISIM_PSISMSC";
        }

        private EfIsimPsiSmscLoaded() {
        }

        @Override // com.android.internal.telephony.uicc.IccRecords.IccRecordLoaded
        public void onRecordLoaded(AsyncResult asyncResult) {
            byte[] bArr = (byte[]) asyncResult.result;
            if (bArr == null || bArr.length <= 0) {
                return;
            }
            IsimUiccRecords isimUiccRecords = IsimUiccRecords.this;
            isimUiccRecords.mPsiSmsc = isimUiccRecords.parseEfPsiSmsc(bArr);
            if (IsimUiccRecords.VDBG) {
                IsimUiccRecords isimUiccRecords2 = IsimUiccRecords.this;
                isimUiccRecords2.log("IsimUiccRecords - EF_PSISMSC value = " + IsimUiccRecords.this.mPsiSmsc);
            }
        }
    }

    @VisibleForTesting
    public EfIsimPsiSmscLoaded getPsiSmscObject() {
        return new EfIsimPsiSmscLoaded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static String isimTlvToString(byte[] bArr) {
        SimTlv simTlv = new SimTlv(bArr, 0, bArr.length);
        while (simTlv.getTag() != 128) {
            if (!simTlv.nextObject()) {
                if (VDBG) {
                    Rlog.d(LOG_TAG, "[ISIM] can't find TLV. record = " + IccUtils.bytesToHexString(bArr));
                    return null;
                }
                return null;
            }
        }
        return new String(simTlv.getData(), Charset.forName("UTF-8"));
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
        log("SIM locked; record load complete");
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
        this.mLoaded.set(true);
        this.mRecordsLoadedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void handleFileUpdate(int i) {
        if (i == 28423) {
            this.mFh.loadEFTransparent(IccConstants.EF_IST, obtainMessage(100, new EfIsimIstLoaded()));
            this.mRecordsToLoad++;
            return;
        }
        if (i != 28425) {
            switch (i) {
                case IccConstants.EF_IMPI /* 28418 */:
                    this.mFh.loadEFTransparent(IccConstants.EF_IMPI, obtainMessage(100, new EfIsimImpiLoaded()));
                    this.mRecordsToLoad++;
                    return;
                case IccConstants.EF_DOMAIN /* 28419 */:
                    this.mFh.loadEFTransparent(IccConstants.EF_DOMAIN, obtainMessage(100, new EfIsimDomainLoaded()));
                    this.mRecordsToLoad++;
                    return;
                case IccConstants.EF_IMPU /* 28420 */:
                    this.mFh.loadEFLinearFixedAll(IccConstants.EF_IMPU, obtainMessage(100, new EfIsimImpuLoaded()));
                    this.mRecordsToLoad++;
                    return;
            }
        }
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_PCSCF, obtainMessage(100, new EfIsimPcscfLoaded()));
        this.mRecordsToLoad++;
        this.mLoaded.set(false);
        fetchIsimRecords();
    }

    private void broadcastRefresh() {
        Intent intent = new Intent(INTENT_ISIM_REFRESH);
        log("send ISim REFRESH: com.android.intent.isim_refresh");
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mParentApp.getPhoneId());
        this.mContext.sendBroadcast(intent);
    }

    @Override // com.android.internal.telephony.uicc.IsimRecords
    public String getIsimImpi() {
        return this.mIsimImpi;
    }

    @Override // com.android.internal.telephony.uicc.IsimRecords
    public String getIsimDomain() {
        return this.mIsimDomain;
    }

    @Override // com.android.internal.telephony.uicc.IsimRecords
    public String[] getIsimImpu() {
        String[] strArr = this.mIsimImpu;
        if (strArr != null) {
            return (String[]) strArr.clone();
        }
        return null;
    }

    @Override // com.android.internal.telephony.uicc.IsimRecords
    public String getIsimIst() {
        return this.mIsimIst;
    }

    @Override // com.android.internal.telephony.uicc.IsimRecords
    public String[] getIsimPcscf() {
        String[] strArr = this.mIsimPcscf;
        if (strArr != null) {
            return (String[]) strArr.clone();
        }
        return null;
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onReady() {
        fetchIsimRecords();
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void onRefresh(boolean z, int[] iArr) {
        if (z) {
            fetchIsimRecords();
        }
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void log(String str) {
        if (this.mParentApp != null) {
            Rlog.d(LOG_TAG, "[ISIM-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.d(LOG_TAG, "[ISIM] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    protected void loge(String str) {
        if (this.mParentApp != null) {
            Rlog.e(LOG_TAG, "[ISIM-" + this.mParentApp.getPhoneId() + "] " + str);
            return;
        }
        Rlog.e(LOG_TAG, "[ISIM] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccRecords
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("IsimRecords: " + this);
        printWriter.println(" extends:");
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println(" mIsimServiceTable=" + getIsimServiceTable());
        printWriter.flush();
    }

    private IsimServiceTable getIsimServiceTable() {
        String str = this.mIsimIst;
        if (str != null) {
            return new IsimServiceTable(IccUtils.hexStringToBytes(str));
        }
        return null;
    }
}
