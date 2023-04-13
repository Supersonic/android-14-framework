package com.android.internal.telephony.domainselection;

import android.telephony.DomainSelectionService;
import android.telephony.ims.ImsReasonInfo;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.domainselection.DomainSelectionConnection;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes.dex */
public class NormalCallDomainSelectionConnection extends DomainSelectionConnection {
    private DomainSelectionConnection.DomainSelectionConnectionCallback mCallback;

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onRequestEmergencyNetworkScan(int[] iArr, int i) {
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWwanSelected() {
    }

    public NormalCallDomainSelectionConnection(Phone phone, DomainSelectionController domainSelectionController) {
        super(phone, 1, false, domainSelectionController);
        this.mTag = "NormalCallDomainSelectionConnection";
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWlanSelected() {
        getCompletableFuture().complete(2);
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onSelectionTerminated(int i) {
        DomainSelectionConnection.DomainSelectionConnectionCallback domainSelectionConnectionCallback = this.mCallback;
        if (domainSelectionConnectionCallback != null) {
            domainSelectionConnectionCallback.onSelectionTerminated(i);
        }
    }

    public CompletableFuture<Integer> createNormalConnection(DomainSelectionService.SelectionAttributes selectionAttributes, DomainSelectionConnection.DomainSelectionConnectionCallback domainSelectionConnectionCallback) {
        this.mCallback = domainSelectionConnectionCallback;
        selectDomain(selectionAttributes);
        return getCompletableFuture();
    }

    public static DomainSelectionService.SelectionAttributes getSelectionAttributes(int i, int i2, String str, String str2, boolean z, int i3, ImsReasonInfo imsReasonInfo) {
        DomainSelectionService.SelectionAttributes.Builder videoCall = new DomainSelectionService.SelectionAttributes.Builder(i, i2, 1).setEmergency(false).setCallId(str).setNumber(str2).setCsDisconnectCause(i3).setVideoCall(z);
        if (imsReasonInfo != null) {
            videoCall.setPsDisconnectCause(imsReasonInfo);
        }
        return videoCall.build();
    }

    public static boolean isWpsCall(String str) {
        return str != null && (str.startsWith("*272") || str.startsWith("*31#*272") || str.startsWith("#31#*272"));
    }
}
