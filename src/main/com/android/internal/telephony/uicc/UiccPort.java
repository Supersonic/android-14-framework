package com.android.internal.telephony.uicc;

import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.SubscriptionInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.IccLogicalChannelRequest;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class UiccPort {
    protected static final boolean DBG = true;
    protected static final String LOG_TAG = "UiccPort";
    protected String mCardId;
    private CommandsInterface mCi;
    private Context mContext;
    private String mIccid;
    protected final Object mLock;
    @GuardedBy({"mOpenChannelRecords"})
    private final List<OpenLogicalChannelRecord> mOpenChannelRecords = new ArrayList();
    private final int mPhoneId;
    private int mPhysicalSlotIndex;
    private int mPortIdx;
    private UiccProfile mUiccProfile;

    public UiccPort(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, Object obj, UiccCard uiccCard) {
        log("Creating");
        this.mPhoneId = i;
        this.mLock = obj;
        update(context, commandsInterface, iccCardStatus, uiccCard);
    }

    public void update(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, UiccCard uiccCard) {
        synchronized (this.mLock) {
            this.mContext = context;
            this.mCi = commandsInterface;
            this.mIccid = iccCardStatus.iccid;
            IccSlotPortMapping iccSlotPortMapping = iccCardStatus.mSlotPortMapping;
            this.mPortIdx = iccSlotPortMapping.mPortIndex;
            this.mPhysicalSlotIndex = iccSlotPortMapping.mPhysicalSlotIndex;
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile == null) {
                this.mUiccProfile = TelephonyComponentFactory.getInstance().inject(UiccProfile.class.getName()).makeUiccProfile(this.mContext, this.mCi, iccCardStatus, this.mPhoneId, uiccCard, this.mLock);
            } else {
                uiccProfile.update(context, commandsInterface, iccCardStatus);
            }
        }
    }

    public void dispose() {
        synchronized (this.mLock) {
            log("Disposing Port");
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                uiccProfile.dispose();
            }
            this.mUiccProfile = null;
        }
    }

    protected void finalize() {
        log("UiccPort finalized");
    }

    @Deprecated
    public boolean isApplicationOnIcc(IccCardApplicationStatus.AppType appType) {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.isApplicationOnIcc(appType);
            }
            return false;
        }
    }

    @Deprecated
    public IccCardStatus.PinState getUniversalPinState() {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.getUniversalPinState();
            }
            return IccCardStatus.PinState.PINSTATE_UNKNOWN;
        }
    }

    @Deprecated
    public UiccCardApplication getApplication(int i) {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.getApplication(i);
            }
            return null;
        }
    }

    @Deprecated
    public UiccCardApplication getApplicationIndex(int i) {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.getApplicationIndex(i);
            }
            return null;
        }
    }

    @Deprecated
    public UiccCardApplication getApplicationByType(int i) {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.getApplicationByType(i);
            }
            return null;
        }
    }

    @Deprecated
    public boolean resetAppWithAid(String str, boolean z) {
        synchronized (this.mLock) {
            UiccProfile uiccProfile = this.mUiccProfile;
            if (uiccProfile != null) {
                return uiccProfile.resetAppWithAid(str, z);
            }
            return false;
        }
    }

    @Deprecated
    public void iccOpenLogicalChannel(String str, int i, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.iccOpenLogicalChannel(str, i, message);
        } else {
            loge("iccOpenLogicalChannel Failed!");
        }
    }

    @Deprecated
    public void iccCloseLogicalChannel(int i, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.iccCloseLogicalChannel(i, false, message);
        } else {
            loge("iccCloseLogicalChannel Failed!");
        }
    }

    @Deprecated
    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, String str, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.iccTransmitApduLogicalChannel(i, i2, i3, i4, i5, i6, str, false, message);
        } else {
            loge("iccTransmitApduLogicalChannel Failed!");
        }
    }

    @Deprecated
    public void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, String str, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.iccTransmitApduBasicChannel(i, i2, i3, i4, i5, str, message);
        } else {
            loge("iccTransmitApduBasicChannel Failed!");
        }
    }

    @Deprecated
    public void iccExchangeSimIO(int i, int i2, int i3, int i4, int i5, String str, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.iccExchangeSimIO(i, i2, i3, i4, i5, str, message);
        } else {
            loge("iccExchangeSimIO Failed!");
        }
    }

    @Deprecated
    public void sendEnvelopeWithStatus(String str, Message message) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.sendEnvelopeWithStatus(str, message);
        } else {
            loge("sendEnvelopeWithStatus Failed!");
        }
    }

    @Deprecated
    public int getNumApplications() {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            return uiccProfile.getNumApplications();
        }
        return 0;
    }

    public int getPhoneId() {
        return this.mPhoneId;
    }

    public int getPortIdx() {
        return this.mPortIdx;
    }

    public UiccProfile getUiccProfile() {
        return this.mUiccProfile;
    }

    @Deprecated
    public boolean setOperatorBrandOverride(String str) {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            return uiccProfile.setOperatorBrandOverride(str);
        }
        return false;
    }

    @Deprecated
    public String getOperatorBrandOverride() {
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            return uiccProfile.getOperatorBrandOverride();
        }
        return null;
    }

    public String getIccId() {
        String str = this.mIccid;
        if (str != null) {
            return str;
        }
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            return uiccProfile.getIccId();
        }
        return null;
    }

    private void log(String str) {
        Rlog.d(LOG_TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e(LOG_TAG, str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("UiccPort:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mPortIdx=" + this.mPortIdx);
        androidUtilIndentingPrintWriter.println("mCi=" + this.mCi);
        androidUtilIndentingPrintWriter.println("mIccid=" + SubscriptionInfo.givePrintableIccid(this.mIccid));
        androidUtilIndentingPrintWriter.println("mPhoneId=" + this.mPhoneId);
        androidUtilIndentingPrintWriter.println("mPhysicalSlotIndex=" + this.mPhysicalSlotIndex);
        synchronized (this.mOpenChannelRecords) {
            androidUtilIndentingPrintWriter.println("mOpenChannelRecords=" + this.mOpenChannelRecords);
        }
        androidUtilIndentingPrintWriter.println("mUiccProfile");
        UiccProfile uiccProfile = this.mUiccProfile;
        if (uiccProfile != null) {
            uiccProfile.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        }
    }

    public void onLogicalChannelOpened(IccLogicalChannelRequest iccLogicalChannelRequest) {
        OpenLogicalChannelRecord openLogicalChannelRecord = new OpenLogicalChannelRecord(iccLogicalChannelRequest);
        try {
            iccLogicalChannelRequest.binder.linkToDeath(openLogicalChannelRecord, 0);
            addOpenLogicalChannelRecord(openLogicalChannelRecord);
            log("onLogicalChannelOpened: monitoring client " + openLogicalChannelRecord);
        } catch (RemoteException | NullPointerException unused) {
            loge("IccOpenLogicChannel client has died, clean up manually");
            openLogicalChannelRecord.binderDied();
        }
    }

    public void onLogicalChannelClosed(int i) {
        IccLogicalChannelRequest iccLogicalChannelRequest;
        OpenLogicalChannelRecord openLogicalChannelRecord = getOpenLogicalChannelRecord(i);
        if (openLogicalChannelRecord == null || (iccLogicalChannelRequest = openLogicalChannelRecord.mRequest) == null || iccLogicalChannelRequest.binder == null) {
            return;
        }
        log("onLogicalChannelClosed: stop monitoring client " + openLogicalChannelRecord);
        openLogicalChannelRecord.mRequest.binder.unlinkToDeath(openLogicalChannelRecord, 0);
        removeOpenLogicalChannelRecord(openLogicalChannelRecord);
        openLogicalChannelRecord.mRequest.binder = null;
    }

    @VisibleForTesting
    public OpenLogicalChannelRecord getOpenLogicalChannelRecord(int i) {
        synchronized (this.mOpenChannelRecords) {
            for (OpenLogicalChannelRecord openLogicalChannelRecord : this.mOpenChannelRecords) {
                IccLogicalChannelRequest iccLogicalChannelRequest = openLogicalChannelRecord.mRequest;
                if (iccLogicalChannelRequest != null && iccLogicalChannelRequest.channel == i) {
                    return openLogicalChannelRecord;
                }
            }
            return null;
        }
    }

    private void addOpenLogicalChannelRecord(OpenLogicalChannelRecord openLogicalChannelRecord) {
        synchronized (this.mOpenChannelRecords) {
            this.mOpenChannelRecords.add(openLogicalChannelRecord);
        }
    }

    private void removeOpenLogicalChannelRecord(OpenLogicalChannelRecord openLogicalChannelRecord) {
        synchronized (this.mOpenChannelRecords) {
            this.mOpenChannelRecords.remove(openLogicalChannelRecord);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class OpenLogicalChannelRecord implements IBinder.DeathRecipient {
        IccLogicalChannelRequest mRequest;

        OpenLogicalChannelRecord(IccLogicalChannelRequest iccLogicalChannelRequest) {
            this.mRequest = iccLogicalChannelRequest;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            UiccPort uiccPort = UiccPort.this;
            uiccPort.loge("IccOpenLogicalChannelRecord: client died, close channel in record " + this);
            UiccPort.this.iccCloseLogicalChannel(this.mRequest.channel, null);
            UiccPort.this.onLogicalChannelClosed(this.mRequest.channel);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("OpenLogicalChannelRecord {");
            sb.append(" mRequest=" + this.mRequest);
            sb.append("}");
            return sb.toString();
        }
    }
}
