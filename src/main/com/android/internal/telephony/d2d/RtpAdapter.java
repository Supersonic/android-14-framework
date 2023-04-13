package com.android.internal.telephony.d2d;

import android.telephony.ims.RtpHeaderExtension;
import android.telephony.ims.RtpHeaderExtensionType;
import java.util.Set;
/* loaded from: classes.dex */
public interface RtpAdapter {

    /* loaded from: classes.dex */
    public interface Callback {
        void onRtpHeaderExtensionsReceived(Set<RtpHeaderExtension> set);
    }

    Set<RtpHeaderExtensionType> getAcceptedRtpHeaderExtensions();

    void sendRtpHeaderExtensions(Set<RtpHeaderExtension> set);
}
