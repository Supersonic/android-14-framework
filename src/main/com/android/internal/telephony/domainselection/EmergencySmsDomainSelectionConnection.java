package com.android.internal.telephony.domainselection;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
/* loaded from: classes.dex */
public class EmergencySmsDomainSelectionConnection extends SmsDomainSelectionConnection {
    private EmergencyStateTracker mEmergencyStateTracker;
    private final Object mLock;
    private int mPreferredTransportType;

    public EmergencySmsDomainSelectionConnection(Phone phone, DomainSelectionController domainSelectionController) {
        this(phone, domainSelectionController, EmergencyStateTracker.getInstance());
    }

    @VisibleForTesting
    public EmergencySmsDomainSelectionConnection(Phone phone, DomainSelectionController domainSelectionController, EmergencyStateTracker emergencyStateTracker) {
        super(phone, domainSelectionController, true);
        this.mLock = new Object();
        this.mPreferredTransportType = -1;
        this.mTag = "DomainSelectionConnection-EmergencySMS";
        this.mEmergencyStateTracker = emergencyStateTracker;
    }

    @Override // com.android.internal.telephony.domainselection.SmsDomainSelectionConnection, com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWlanSelected() {
        synchronized (this.mLock) {
            if (this.mPreferredTransportType != -1) {
                logi("Domain selection completion is in progress");
                return;
            }
            this.mEmergencyStateTracker.lambda$onEmergencyTransportChanged$1(2, 2);
            if (this.mPhone.getAccessNetworksManager().getPreferredTransport(512) != 2) {
                changePreferredTransport(2);
            } else {
                super.onWlanSelected();
            }
        }
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onWwanSelected() {
        this.mEmergencyStateTracker.lambda$onEmergencyTransportChanged$1(2, 1);
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void onDomainSelected(int i) {
        synchronized (this.mLock) {
            if (this.mPreferredTransportType != -1) {
                logi("Domain selection completion is in progress");
            } else if (i == 2 && this.mPhone.getAccessNetworksManager().getPreferredTransport(512) != 1) {
                changePreferredTransport(1);
            } else {
                super.onDomainSelected(i);
            }
        }
    }

    @Override // com.android.internal.telephony.domainselection.SmsDomainSelectionConnection, com.android.internal.telephony.domainselection.DomainSelectionConnection
    public void finishSelection() {
        AccessNetworksManager accessNetworksManager = this.mPhone.getAccessNetworksManager();
        synchronized (this.mLock) {
            if (this.mPreferredTransportType != -1) {
                this.mPreferredTransportType = -1;
                accessNetworksManager.unregisterForQualifiedNetworksChanged(this.mHandler);
            }
        }
        super.finishSelection();
    }

    @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection
    protected void onQualifiedNetworksChanged() {
        AccessNetworksManager accessNetworksManager = this.mPhone.getAccessNetworksManager();
        int preferredTransport = accessNetworksManager.getPreferredTransport(512);
        synchronized (this.mLock) {
            if (preferredTransport == this.mPreferredTransportType) {
                this.mPreferredTransportType = -1;
                super.onDomainSelected(2);
                accessNetworksManager.unregisterForQualifiedNetworksChanged(this.mHandler);
            }
        }
    }

    private void changePreferredTransport(int i) {
        logi("Change preferred transport: " + i);
        initHandler();
        this.mPreferredTransportType = i;
        this.mPhone.getAccessNetworksManager().registerForQualifiedNetworksChanged(this.mHandler, 2);
        this.mPhone.notifyEmergencyDomainSelected(i);
    }
}
