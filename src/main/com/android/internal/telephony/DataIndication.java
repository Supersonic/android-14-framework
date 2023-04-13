package com.android.internal.telephony;

import android.hardware.radio.data.DataProfileInfo;
import android.hardware.radio.data.IRadioDataIndication;
import android.hardware.radio.data.KeepaliveStatus;
import android.hardware.radio.data.PcoDataInfo;
import android.hardware.radio.data.SetupDataCallResult;
import android.hardware.radio.data.SlicingConfig;
import android.os.AsyncResult;
import android.os.RemoteException;
import android.telephony.PcoData;
import android.telephony.data.DataProfile;
/* loaded from: classes.dex */
public class DataIndication extends IRadioDataIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public DataIndication(RIL ril) {
        this.mRil = ril;
    }

    public void dataCallListChanged(int i, SetupDataCallResult[] setupDataCallResultArr) {
        this.mRil.processIndication(1, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1010, setupDataCallResultArr);
        }
        this.mRil.mDataCallListChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, RILUtils.convertHalDataCallResultList(setupDataCallResultArr), (Throwable) null));
    }

    public void keepaliveStatus(int i, KeepaliveStatus keepaliveStatus) {
        this.mRil.processIndication(1, i);
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogRet(1050, "handle=" + keepaliveStatus.sessionHandle + " code=" + keepaliveStatus.code);
        }
        this.mRil.mNattKeepaliveStatusRegistrants.notifyRegistrants(new AsyncResult((Object) null, new com.android.internal.telephony.data.KeepaliveStatus(keepaliveStatus.sessionHandle, keepaliveStatus.code), (Throwable) null));
    }

    public void pcoData(int i, PcoDataInfo pcoDataInfo) {
        this.mRil.processIndication(1, i);
        PcoData pcoData = new PcoData(pcoDataInfo.cid, pcoDataInfo.bearerProto, pcoDataInfo.pcoId, pcoDataInfo.contents);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1046, pcoData);
        }
        this.mRil.mPcoDataRegistrants.notifyRegistrants(new AsyncResult((Object) null, pcoData, (Throwable) null));
    }

    public void unthrottleApn(int i, DataProfileInfo dataProfileInfo) throws RemoteException {
        this.mRil.processIndication(1, i);
        DataProfile convertToDataProfile = RILUtils.convertToDataProfile(dataProfileInfo);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1052, convertToDataProfile);
        }
        this.mRil.mApnUnthrottledRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertToDataProfile, (Throwable) null));
    }

    public void slicingConfigChanged(int i, SlicingConfig slicingConfig) throws RemoteException {
        this.mRil.processIndication(1, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1055, slicingConfig);
        }
        this.mRil.mSlicingConfigChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, RILUtils.convertHalSlicingConfig(slicingConfig), (Throwable) null));
    }
}
