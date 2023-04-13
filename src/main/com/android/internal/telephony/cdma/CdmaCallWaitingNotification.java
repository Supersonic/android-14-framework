package com.android.internal.telephony.cdma;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class CdmaCallWaitingNotification {
    @UnsupportedAppUsage
    public String number = null;
    public int numberPresentation = 0;
    public String name = null;
    public int namePresentation = 0;
    public int numberType = 0;
    public int numberPlan = 0;
    public int isPresent = 0;
    public int signalType = 0;
    public int alertPitch = 0;
    public int signal = 0;

    public String toString() {
        return super.toString() + "Call Waiting Notification   number: " + Rlog.pii("CdmaCallWaitingNotification", this.number) + " numberPresentation: " + this.numberPresentation + " name: " + this.name + " namePresentation: " + this.namePresentation + " numberType: " + this.numberType + " numberPlan: " + this.numberPlan + " isPresent: " + this.isPresent + " signalType: " + this.signalType + " alertPitch: " + this.alertPitch + " signal: " + this.signal;
    }

    public static int presentationFromCLIP(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    Rlog.d("CdmaCallWaitingNotification", "Unexpected presentation " + i);
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }
}
