package com.android.internal.telephony.domainselection;

import android.telephony.DomainSelectionService;
import android.telephony.EmergencyRegResult;
import android.telephony.ims.ImsReasonInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.domainselection.DomainSelectionConnection;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes.dex */
public class EmergencyCallDomainSelectionConnection extends DomainSelectionConnection {
    private DomainSelectionConnection.DomainSelectionConnectionCallback mCallback;
    private EmergencyStateTracker mEmergencyStateTracker;
    private AccessNetworksManager.AccessNetworksManagerCallback mPreferredTransportCallback;
    private int mPreferredTransportType;

    public EmergencyCallDomainSelectionConnection(Phone phone, DomainSelectionController domainSelectionController) {
        this(phone, domainSelectionController, EmergencyStateTracker.getInstance());
    }

    @VisibleForTesting
    public EmergencyCallDomainSelectionConnection(Phone phone, DomainSelectionController domainSelectionController, EmergencyStateTracker emergencyStateTracker) {
        super(phone, 1, true, domainSelectionController);
        this.mEmergencyStateTracker = null;
        this.mPreferredTransportType = -1;
        this.mPreferredTransportCallback = new AccessNetworksManager.AccessNetworksManagerCallback(new NetworkTypeController$$ExternalSyntheticLambda1()) { // from class: com.android.internal.telephony.domainselection.EmergencyCallDomainSelectionConnection.1
            @Override // com.android.internal.telephony.data.AccessNetworksManager.AccessNetworksManagerCallback
            public void onPreferredTransportChanged(int i) {
            }
        };
        this.mTag = "EmergencyCallDomainSelectionConnection";
        this.mEmergencyStateTracker = emergencyStateTracker;
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWlanSelected(boolean z) {
        this.mEmergencyStateTracker.lambda$onEmergencyTransportChanged$1(1, 2);
        if (z) {
            int preferredTransport = this.mPhone.getAccessNetworksManager().getPreferredTransport(512);
            logi("onWlanSelected curTransportType=" + preferredTransport);
            if (preferredTransport != 2) {
                changePreferredTransport(2);
                return;
            }
        }
        CompletableFuture<Integer> completableFuture = getCompletableFuture();
        if (completableFuture != null) {
            completableFuture.complete(4);
        }
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWwanSelected() {
        this.mEmergencyStateTracker.lambda$onEmergencyTransportChanged$1(1, 1);
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onSelectionTerminated(int i) {
        DomainSelectionConnection.DomainSelectionConnectionCallback domainSelectionConnectionCallback = this.mCallback;
        if (domainSelectionConnectionCallback != null) {
            domainSelectionConnectionCallback.onSelectionTerminated(i);
        }
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onDomainSelected(int i, boolean z) {
        if (i == 2 && z) {
            int preferredTransport = this.mPhone.getAccessNetworksManager().getPreferredTransport(512);
            logi("onDomainSelected curTransportType=" + preferredTransport);
            if (preferredTransport != 1) {
                changePreferredTransport(1);
                return;
            }
        }
        super.onDomainSelected(i, z);
    }

    public CompletableFuture<Integer> createEmergencyConnection(DomainSelectionService.SelectionAttributes selectionAttributes, DomainSelectionConnection.DomainSelectionConnectionCallback domainSelectionConnectionCallback) {
        this.mCallback = domainSelectionConnectionCallback;
        selectDomain(selectionAttributes);
        return getCompletableFuture();
    }

    private void changePreferredTransport(int i) {
        logi("changePreferredTransport " + i);
        initHandler();
        this.mPreferredTransportType = i;
        this.mPhone.getAccessNetworksManager().registerForQualifiedNetworksChanged(this.mHandler, 2);
        this.mPhone.notifyEmergencyDomainSelected(i);
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    protected void onQualifiedNetworksChanged() {
        AccessNetworksManager accessNetworksManager = this.mPhone.getAccessNetworksManager();
        int preferredTransport = accessNetworksManager.getPreferredTransport(512);
        logi("onQualifiedNetworksChanged preferred=" + this.mPreferredTransportType + ", current=" + preferredTransport);
        if (preferredTransport == this.mPreferredTransportType) {
            CompletableFuture<Integer> completableFuture = getCompletableFuture();
            if (completableFuture != null) {
                if (preferredTransport == 2) {
                    completableFuture.complete(4);
                } else {
                    completableFuture.complete(2);
                }
            }
            accessNetworksManager.unregisterForQualifiedNetworksChanged(this.mHandler);
        }
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void cancelSelection() {
        logi("cancelSelection");
        this.mPhone.getAccessNetworksManager().unregisterForQualifiedNetworksChanged(this.mHandler);
        super.cancelSelection();
    }

    public static DomainSelectionService.SelectionAttributes getSelectionAttributes(int i, int i2, boolean z, String str, String str2, int i3, ImsReasonInfo imsReasonInfo, EmergencyRegResult emergencyRegResult) {
        DomainSelectionService.SelectionAttributes.Builder csDisconnectCause = new DomainSelectionService.SelectionAttributes.Builder(i, i2, 1).setEmergency(true).setExitedFromAirplaneMode(z).setCallId(str).setNumber(str2).setCsDisconnectCause(i3);
        if (imsReasonInfo != null) {
            csDisconnectCause.setPsDisconnectCause(imsReasonInfo);
        }
        if (emergencyRegResult != null) {
            csDisconnectCause.setEmergencyRegResult(emergencyRegResult);
        }
        return csDisconnectCause.build();
    }
}
