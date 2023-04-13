package com.android.internal.telephony;

import android.os.Handler;
import android.os.Message;
import android.telephony.AnomalyReporter;
import android.telephony.ServiceState;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Pair;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;
import javax.sip.InvalidArgumentException;
/* loaded from: classes.dex */
public class DisplayInfoController extends Handler {
    private static final Set<Pair<Integer, Integer>> VALID_DISPLAY_INFO_SET = Set.of(Pair.create(13, 1), Pair.create(13, 2), Pair.create(13, 3), Pair.create(13, 5), Pair.create(20, 5));
    private final String mLogTag;
    private final NetworkTypeController mNetworkTypeController;
    private final Phone mPhone;
    private ServiceState mServiceState;
    private final LocalLog mLocalLog = new LocalLog(128);
    private final RegistrantList mTelephonyDisplayInfoChangedRegistrants = new RegistrantList();
    private TelephonyDisplayInfo mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);

    public DisplayInfoController(Phone phone) {
        this.mPhone = phone;
        this.mLogTag = "DIC-" + phone.getPhoneId();
        NetworkTypeController networkTypeController = new NetworkTypeController(phone, this);
        this.mNetworkTypeController = networkTypeController;
        networkTypeController.sendMessage(0);
        this.mServiceState = phone.getServiceStateTracker().getServiceState();
        post(new Runnable() { // from class: com.android.internal.telephony.DisplayInfoController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayInfoController.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mPhone.getServiceStateTracker().registerForServiceStateChanged(this, 1, null);
        updateTelephonyDisplayInfo();
    }

    public TelephonyDisplayInfo getTelephonyDisplayInfo() {
        return this.mTelephonyDisplayInfo;
    }

    public void updateTelephonyDisplayInfo() {
        TelephonyDisplayInfo telephonyDisplayInfo = new TelephonyDisplayInfo(this.mNetworkTypeController.getDataNetworkType(), this.mNetworkTypeController.getOverrideNetworkType(), this.mServiceState.getRoaming());
        if (telephonyDisplayInfo.equals(this.mTelephonyDisplayInfo)) {
            return;
        }
        logl("TelephonyDisplayInfo changed from " + this.mTelephonyDisplayInfo + " to " + telephonyDisplayInfo);
        validateDisplayInfo(telephonyDisplayInfo);
        this.mTelephonyDisplayInfo = telephonyDisplayInfo;
        this.mTelephonyDisplayInfoChangedRegistrants.notifyRegistrants();
        this.mPhone.notifyDisplayInfoChanged(this.mTelephonyDisplayInfo);
    }

    private void validateDisplayInfo(TelephonyDisplayInfo telephonyDisplayInfo) {
        try {
            if (telephonyDisplayInfo.getNetworkType() == 19) {
                throw new InvalidArgumentException("LTE_CA is not a valid network type.");
            }
            if (telephonyDisplayInfo.getNetworkType() < 0 && telephonyDisplayInfo.getNetworkType() > 20) {
                throw new InvalidArgumentException("Invalid network type " + telephonyDisplayInfo.getNetworkType());
            }
            if (telephonyDisplayInfo.getOverrideNetworkType() != 0 && !VALID_DISPLAY_INFO_SET.contains(Pair.create(Integer.valueOf(telephonyDisplayInfo.getNetworkType()), Integer.valueOf(telephonyDisplayInfo.getOverrideNetworkType())))) {
                throw new InvalidArgumentException("Invalid network type override " + TelephonyDisplayInfo.overrideNetworkTypeToString(telephonyDisplayInfo.getOverrideNetworkType()) + " for " + TelephonyManager.getNetworkTypeName(telephonyDisplayInfo.getNetworkType()));
            }
        } catch (InvalidArgumentException e) {
            logel(e.getMessage());
            AnomalyReporter.reportAnomaly(UUID.fromString("3aa92a2c-94ed-46a0-a744-d6b1dfec2a56"), e.getMessage(), this.mPhone.getCarrierId());
        }
    }

    public void registerForTelephonyDisplayInfoChanged(Handler handler, int i, Object obj) {
        this.mTelephonyDisplayInfoChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForTelephonyDisplayInfoChanged(Handler handler) {
        this.mTelephonyDisplayInfoChangedRegistrants.remove(handler);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        if (message.what != 1) {
            return;
        }
        this.mServiceState = this.mPhone.getServiceStateTracker().getServiceState();
        log("ServiceState updated, isRoaming=" + this.mServiceState.getRoaming());
        updateTelephonyDisplayInfo();
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    private void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    private void logel(String str) {
        loge(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("DisplayInfoController:");
        androidUtilIndentingPrintWriter.println(" mPhone=" + this.mPhone.getPhoneName());
        androidUtilIndentingPrintWriter.println(" mTelephonyDisplayInfo=" + this.mTelephonyDisplayInfo.toString());
        androidUtilIndentingPrintWriter.flush();
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println(" ***************************************");
        this.mNetworkTypeController.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.flush();
    }
}
