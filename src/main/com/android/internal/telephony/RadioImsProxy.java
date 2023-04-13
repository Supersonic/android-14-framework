package com.android.internal.telephony;

import android.hardware.radio.ims.IRadioIms;
import android.hardware.radio.ims.ImsCall;
import android.hardware.radio.ims.ImsRegistration;
import android.hardware.radio.ims.SrvccCall;
import android.os.RemoteException;
import android.telephony.Rlog;
/* loaded from: classes.dex */
public class RadioImsProxy extends RadioServiceProxy {
    private volatile IRadioIms mImsProxy = null;

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void responseAcknowledgement() throws RemoteException {
    }

    public HalVersion setAidl(HalVersion halVersion, IRadioIms iRadioIms) {
        try {
            halVersion = RIL.getServiceHalVersion(iRadioIms.getInterfaceVersion());
        } catch (RemoteException e) {
            Rlog.e("RadioImsProxy", "setAidl: " + e);
        }
        this.mHalVersion = halVersion;
        this.mImsProxy = iRadioIms;
        this.mIsAidl = true;
        Rlog.d("RadioImsProxy", "AIDL initialized mHalVersion=" + this.mHalVersion);
        return this.mHalVersion;
    }

    public IRadioIms getAidl() {
        return this.mImsProxy;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void clear() {
        super.clear();
        this.mImsProxy = null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public boolean isEmpty() {
        return this.mRadioProxy == null && this.mImsProxy == null;
    }

    public void setSrvccCallInfo(int i, SrvccCall[] srvccCallArr) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.setSrvccCallInfo(i, srvccCallArr);
        }
    }

    public void updateImsRegistrationInfo(int i, ImsRegistration imsRegistration) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.updateImsRegistrationInfo(i, imsRegistration);
        }
    }

    public void startImsTraffic(int i, int i2, int i3, int i4, int i5) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.startImsTraffic(i, i2, i3, i4, i5);
        }
    }

    public void stopImsTraffic(int i, int i2) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.stopImsTraffic(i, i2);
        }
    }

    public void triggerEpsFallback(int i, int i2) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.triggerEpsFallback(i, i2);
        }
    }

    public void sendAnbrQuery(int i, int i2, int i3, int i4) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.sendAnbrQuery(i, i2, i3, i4);
        }
    }

    public void updateImsCallStatus(int i, ImsCall[] imsCallArr) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mImsProxy.updateImsCallStatus(i, imsCallArr);
        }
    }
}
