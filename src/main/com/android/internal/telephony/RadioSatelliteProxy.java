package com.android.internal.telephony;

import android.hardware.radio.satellite.IRadioSatellite;
import android.os.RemoteException;
import android.telephony.Rlog;
/* loaded from: classes.dex */
public class RadioSatelliteProxy extends RadioServiceProxy {
    private volatile IRadioSatellite mSatelliteProxy = null;

    public HalVersion setAidl(HalVersion halVersion, IRadioSatellite iRadioSatellite) {
        try {
            halVersion = RIL.getServiceHalVersion(iRadioSatellite.getInterfaceVersion());
        } catch (RemoteException e) {
            Rlog.e("RadioSatelliteProxy", "setAidl: " + e);
        }
        this.mHalVersion = halVersion;
        this.mSatelliteProxy = iRadioSatellite;
        this.mIsAidl = true;
        Rlog.d("RadioSatelliteProxy", "AIDL initialized mHalVersion=" + this.mHalVersion);
        return this.mHalVersion;
    }

    public IRadioSatellite getAidl() {
        return this.mSatelliteProxy;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void clear() {
        super.clear();
        this.mSatelliteProxy = null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public boolean isEmpty() {
        return this.mRadioProxy == null && this.mSatelliteProxy == null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void responseAcknowledgement() throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.responseAcknowledgement();
        }
    }

    public void getCapabilities(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getCapabilities(i);
        }
    }

    public void setPower(int i, boolean z) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.setPower(i, z);
        }
    }

    public void getPowerState(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getPowerState(i);
        }
    }

    public void provisionService(int i, String str, String str2, String str3, int[] iArr) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.provisionService(i, str, str2, str3, iArr);
        }
    }

    public void addAllowedSatelliteContacts(int i, String[] strArr) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.addAllowedSatelliteContacts(i, strArr);
        }
    }

    public void removeAllowedSatelliteContacts(int i, String[] strArr) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.removeAllowedSatelliteContacts(i, strArr);
        }
    }

    public void sendMessages(int i, String[] strArr, String str, double d, double d2) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.sendMessages(i, strArr, str, d, d2);
        }
    }

    public void getPendingMessages(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getPendingMessages(i);
        }
    }

    public void getSatelliteMode(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getSatelliteMode(i);
        }
    }

    public void setIndicationFilter(int i, int i2) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.setIndicationFilter(i, i2);
        }
    }

    public void startSendingSatellitePointingInfo(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.startSendingSatellitePointingInfo(i);
        }
    }

    public void stopSendingSatellitePointingInfo(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.stopSendingSatellitePointingInfo(i);
        }
    }

    public void getMaxCharactersPerTextMessage(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getMaxCharactersPerTextMessage(i);
        }
    }

    public void getTimeForNextSatelliteVisibility(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mSatelliteProxy.getTimeForNextSatelliteVisibility(i);
        }
    }
}
