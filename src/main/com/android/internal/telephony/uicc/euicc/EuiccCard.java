package com.android.internal.telephony.uicc.euicc;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class EuiccCard extends UiccCard {
    private volatile String mEid;
    private RegistrantList mEidReadyRegistrants;

    public EuiccCard(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, Object obj, IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        super(context, commandsInterface, iccCardStatus, i, obj, multipleEnabledProfilesMode);
        if (TextUtils.isEmpty(iccCardStatus.eid)) {
            loge("no eid given in constructor for phone " + i);
            loadEidAndNotifyRegistrants();
            return;
        }
        this.mEid = iccCardStatus.eid;
        this.mCardId = iccCardStatus.eid;
    }

    @Override // com.android.internal.telephony.uicc.UiccCard
    public void updateSupportedMepMode(IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        this.mSupportedMepMode = multipleEnabledProfilesMode;
        for (UiccPort uiccPort : this.mUiccPorts.values()) {
            if (uiccPort instanceof EuiccPort) {
                ((EuiccPort) uiccPort).updateSupportedMepMode(multipleEnabledProfilesMode);
            } else {
                loge("eUICC card has non-euicc port object:" + uiccPort.toString());
            }
        }
    }

    @Override // com.android.internal.telephony.uicc.UiccCard
    public void update(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i) {
        synchronized (this.mLock) {
            if (!TextUtils.isEmpty(iccCardStatus.eid)) {
                this.mEid = iccCardStatus.eid;
            }
            super.update(context, commandsInterface, iccCardStatus, i);
        }
    }

    @Override // com.android.internal.telephony.uicc.UiccCard
    protected void updateCardId(String str) {
        if (TextUtils.isEmpty(this.mEid)) {
            super.updateCardId(str);
        } else {
            this.mCardId = this.mEid;
        }
    }

    public void registerForEidReady(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        if (this.mEid != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
            return;
        }
        if (this.mEidReadyRegistrants == null) {
            this.mEidReadyRegistrants = new RegistrantList();
        }
        this.mEidReadyRegistrants.add(registrant);
    }

    public void unregisterForEidReady(Handler handler) {
        RegistrantList registrantList = this.mEidReadyRegistrants;
        if (registrantList != null) {
            registrantList.remove(handler);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    protected void loadEidAndNotifyRegistrants() {
        Handler handler = new Handler();
        ((EuiccPort) this.mUiccPorts.get(0)).getEid(new AsyncResultCallback<String>() { // from class: com.android.internal.telephony.uicc.euicc.EuiccCard.1
            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onResult(String str) {
                EuiccCard.this.mEid = str;
                ((UiccCard) EuiccCard.this).mCardId = str;
                if (TextUtils.isEmpty(str)) {
                    EuiccCard.logd("eid is loaded but empty ");
                }
                if (EuiccCard.this.mEidReadyRegistrants != null) {
                    EuiccCard.this.mEidReadyRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
                }
            }

            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onException(Throwable th) {
                if (EuiccCard.this.mEidReadyRegistrants != null) {
                    EuiccCard.this.mEidReadyRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
                }
                EuiccCard.this.mEid = PhoneConfigurationManager.SSSS;
                ((UiccCard) EuiccCard.this).mCardId = PhoneConfigurationManager.SSSS;
                Rlog.e("EuiccCard", "Failed loading eid", th);
            }
        }, handler);
    }

    public String getEid() {
        return this.mEid;
    }

    private static void loge(String str) {
        Rlog.e("EuiccCard", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("EuiccCard", str);
    }

    @Override // com.android.internal.telephony.uicc.UiccCard
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("EuiccCard:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mEid=" + this.mEid);
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
