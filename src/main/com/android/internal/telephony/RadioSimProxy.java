package com.android.internal.telephony;

import android.hardware.radio.V1_4.CarrierRestrictionsWithPriority;
import android.hardware.radio.sim.CarrierRestrictions;
import android.hardware.radio.sim.IRadioSim;
import android.hardware.radio.sim.IccIo;
import android.hardware.radio.sim.SelectUiccSub;
import android.os.AsyncResult;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.CarrierRestrictionRules;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.Rlog;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
/* loaded from: classes.dex */
public class RadioSimProxy extends RadioServiceProxy {
    private volatile IRadioSim mSimProxy = null;

    public HalVersion setAidl(HalVersion halVersion, IRadioSim iRadioSim) {
        try {
            halVersion = RIL.getServiceHalVersion(iRadioSim.getInterfaceVersion());
        } catch (RemoteException e) {
            Rlog.e("RadioSimProxy", "setAidl: " + e);
        }
        this.mHalVersion = halVersion;
        this.mSimProxy = iRadioSim;
        this.mIsAidl = true;
        Rlog.d("RadioSimProxy", "AIDL initialized mHalVersion=" + this.mHalVersion);
        return this.mHalVersion;
    }

    public IRadioSim getAidl() {
        return this.mSimProxy;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void clear() {
        super.clear();
        this.mSimProxy = null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public boolean isEmpty() {
        return this.mRadioProxy == null && this.mSimProxy == null;
    }

    public void areUiccApplicationsEnabled(int i) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_5)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.areUiccApplicationsEnabled(i);
        } else {
            this.mRadioProxy.areUiccApplicationsEnabled(i);
        }
    }

    public void changeIccPin2ForApp(int i, String str, String str2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.changeIccPin2ForApp(i, str, str2, str3);
        } else {
            this.mRadioProxy.changeIccPin2ForApp(i, str, str2, str3);
        }
    }

    public void changeIccPinForApp(int i, String str, String str2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.changeIccPinForApp(i, str, str2, str3);
        } else {
            this.mRadioProxy.changeIccPinForApp(i, str, str2, str3);
        }
    }

    public void enableUiccApplications(int i, boolean z) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_5)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.enableUiccApplications(i, z);
        } else {
            this.mRadioProxy.enableUiccApplications(i, z);
        }
    }

    public void getAllowedCarriers(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getAllowedCarriers(i);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_4)) {
            this.mRadioProxy.getAllowedCarriers_1_4(i);
        } else {
            this.mRadioProxy.getAllowedCarriers(i);
        }
    }

    public void getCdmaSubscription(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getCdmaSubscription(i);
        } else {
            this.mRadioProxy.getCDMASubscription(i);
        }
    }

    public void getCdmaSubscriptionSource(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getCdmaSubscriptionSource(i);
        } else {
            this.mRadioProxy.getCdmaSubscriptionSource(i);
        }
    }

    public void getFacilityLockForApp(int i, String str, String str2, int i2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getFacilityLockForApp(i, str, str2, i2, str3);
        } else {
            this.mRadioProxy.getFacilityLockForApp(i, str, str2, i2, str3);
        }
    }

    public void getIccCardStatus(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getIccCardStatus(i);
        } else {
            this.mRadioProxy.getIccCardStatus(i);
        }
    }

    public void getImsiForApp(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getImsiForApp(i, str);
        } else {
            this.mRadioProxy.getImsiForApp(i, str);
        }
    }

    public void getSimPhonebookCapacity(int i) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getSimPhonebookCapacity(i);
        } else {
            this.mRadioProxy.getSimPhonebookCapacity(i);
        }
    }

    public void getSimPhonebookRecords(int i) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.getSimPhonebookRecords(i);
        } else {
            this.mRadioProxy.getSimPhonebookRecords(i);
        }
    }

    public void iccCloseLogicalChannel(int i, int i2, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_2_1)) {
                this.mSimProxy.iccCloseLogicalChannel(i, i2);
                return;
            } else {
                this.mSimProxy.iccCloseLogicalChannel(i, i2);
                return;
            }
        }
        this.mRadioProxy.iccCloseLogicalChannel(i, i2);
    }

    public void iccIoForApp(int i, int i2, int i3, String str, int i4, int i5, int i6, String str2, String str3, String str4) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            IccIo iccIo = new IccIo();
            iccIo.command = i2;
            iccIo.fileId = i3;
            iccIo.path = str;
            iccIo.p1 = i4;
            iccIo.p2 = i5;
            iccIo.p3 = i6;
            iccIo.data = str2;
            iccIo.pin2 = str3;
            iccIo.aid = str4;
            this.mSimProxy.iccIoForApp(i, iccIo);
            return;
        }
        android.hardware.radio.V1_0.IccIo iccIo2 = new android.hardware.radio.V1_0.IccIo();
        iccIo2.command = i2;
        iccIo2.fileId = i3;
        iccIo2.path = str;
        iccIo2.p1 = i4;
        iccIo2.p2 = i5;
        iccIo2.p3 = i6;
        iccIo2.data = str2;
        iccIo2.pin2 = str3;
        iccIo2.aid = str4;
        this.mRadioProxy.iccIOForApp(i, iccIo2);
    }

    public void iccOpenLogicalChannel(int i, String str, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.iccOpenLogicalChannel(i, str, i2);
        } else {
            this.mRadioProxy.iccOpenLogicalChannel(i, str, i2);
        }
    }

    public void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, int i6, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.iccTransmitApduBasicChannel(i, RILUtils.convertToHalSimApduAidl(0, i2, i3, i4, i5, i6, str, false, this.mHalVersion));
        } else {
            this.mRadioProxy.iccTransmitApduBasicChannel(i, RILUtils.convertToHalSimApdu(0, i2, i3, i4, i5, i6, str));
        }
    }

    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, int i7, String str) throws RemoteException {
        iccTransmitApduLogicalChannel(i, i2, i3, i4, i5, i6, i7, str, false);
    }

    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, int i7, String str, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.iccTransmitApduLogicalChannel(i, RILUtils.convertToHalSimApduAidl(i2, i3, i4, i5, i6, i7, str, z, this.mHalVersion));
        } else {
            this.mRadioProxy.iccTransmitApduLogicalChannel(i, RILUtils.convertToHalSimApdu(i2, i3, i4, i5, i6, i7, str));
        }
    }

    public void reportStkServiceIsRunning(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.reportStkServiceIsRunning(i);
        } else {
            this.mRadioProxy.reportStkServiceIsRunning(i);
        }
    }

    public void requestIccSimAuthentication(int i, int i2, String str, String str2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.requestIccSimAuthentication(i, i2, str, str2);
        } else {
            this.mRadioProxy.requestIccSimAuthentication(i, i2, str, str2);
        }
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void responseAcknowledgement() throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.responseAcknowledgement();
        } else {
            this.mRadioProxy.responseAcknowledgement();
        }
    }

    public void sendEnvelope(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.sendEnvelope(i, str);
        } else {
            this.mRadioProxy.sendEnvelope(i, str);
        }
    }

    public void sendEnvelopeWithStatus(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.sendEnvelopeWithStatus(i, str);
        } else {
            this.mRadioProxy.sendEnvelopeWithStatus(i, str);
        }
    }

    public void sendTerminalResponseToSim(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.sendTerminalResponseToSim(i, str);
        } else {
            this.mRadioProxy.sendTerminalResponseToSim(i, str);
        }
    }

    public void setAllowedCarriers(int i, CarrierRestrictionRules carrierRestrictionRules, Message message) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            CarrierRestrictions carrierRestrictions = new CarrierRestrictions();
            carrierRestrictions.allowedCarriers = RILUtils.convertToHalCarrierRestrictionListAidl(carrierRestrictionRules.getAllowedCarriers());
            carrierRestrictions.excludedCarriers = RILUtils.convertToHalCarrierRestrictionListAidl(carrierRestrictionRules.getExcludedCarriers());
            carrierRestrictions.allowedCarriersPrioritized = carrierRestrictionRules.getDefaultCarrierRestriction() == 0;
            this.mSimProxy.setAllowedCarriers(i, carrierRestrictions, RILUtils.convertToHalSimLockMultiSimPolicyAidl(carrierRestrictionRules.getMultiSimPolicy()));
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_4)) {
            CarrierRestrictionsWithPriority carrierRestrictionsWithPriority = new CarrierRestrictionsWithPriority();
            carrierRestrictionsWithPriority.allowedCarriers = RILUtils.convertToHalCarrierRestrictionList(carrierRestrictionRules.getAllowedCarriers());
            carrierRestrictionsWithPriority.excludedCarriers = RILUtils.convertToHalCarrierRestrictionList(carrierRestrictionRules.getExcludedCarriers());
            carrierRestrictionsWithPriority.allowedCarriersPrioritized = carrierRestrictionRules.getDefaultCarrierRestriction() == 0;
            this.mRadioProxy.setAllowedCarriers_1_4(i, carrierRestrictionsWithPriority, RILUtils.convertToHalSimLockMultiSimPolicy(carrierRestrictionRules.getMultiSimPolicy()));
        } else {
            boolean isAllCarriersAllowed = carrierRestrictionRules.isAllCarriersAllowed();
            if ((isAllCarriersAllowed || (carrierRestrictionRules.getExcludedCarriers().isEmpty() && carrierRestrictionRules.getDefaultCarrierRestriction() == 0)) && RILUtils.convertToHalSimLockMultiSimPolicy(carrierRestrictionRules.getMultiSimPolicy()) == 0) {
                android.hardware.radio.V1_0.CarrierRestrictions carrierRestrictions2 = new android.hardware.radio.V1_0.CarrierRestrictions();
                carrierRestrictions2.allowedCarriers = RILUtils.convertToHalCarrierRestrictionList(carrierRestrictionRules.getAllowedCarriers());
                this.mRadioProxy.setAllowedCarriers(i, isAllCarriersAllowed, carrierRestrictions2);
            } else if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
            }
        }
    }

    public void setCarrierInfoForImsiEncryption(int i, ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_1)) {
            return;
        }
        if (isAidl()) {
            android.hardware.radio.sim.ImsiEncryptionInfo imsiEncryptionInfo2 = new android.hardware.radio.sim.ImsiEncryptionInfo();
            imsiEncryptionInfo2.mnc = imsiEncryptionInfo.getMnc();
            imsiEncryptionInfo2.mcc = imsiEncryptionInfo.getMcc();
            imsiEncryptionInfo2.keyIdentifier = imsiEncryptionInfo.getKeyIdentifier();
            if (imsiEncryptionInfo.getExpirationTime() != null) {
                imsiEncryptionInfo2.expirationTime = imsiEncryptionInfo.getExpirationTime().getTime();
            }
            imsiEncryptionInfo2.carrierKey = imsiEncryptionInfo.getPublicKey().getEncoded();
            imsiEncryptionInfo2.keyType = (byte) imsiEncryptionInfo.getKeyType();
            this.mSimProxy.setCarrierInfoForImsiEncryption(i, imsiEncryptionInfo2);
            return;
        }
        int i2 = 0;
        if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            android.hardware.radio.V1_6.ImsiEncryptionInfo imsiEncryptionInfo3 = new android.hardware.radio.V1_6.ImsiEncryptionInfo();
            imsiEncryptionInfo3.base.mnc = imsiEncryptionInfo.getMnc();
            imsiEncryptionInfo3.base.mcc = imsiEncryptionInfo.getMcc();
            imsiEncryptionInfo3.base.keyIdentifier = imsiEncryptionInfo.getKeyIdentifier();
            if (imsiEncryptionInfo.getExpirationTime() != null) {
                imsiEncryptionInfo3.base.expirationTime = imsiEncryptionInfo.getExpirationTime().getTime();
            }
            byte[] encoded = imsiEncryptionInfo.getPublicKey().getEncoded();
            int length = encoded.length;
            while (i2 < length) {
                imsiEncryptionInfo3.base.carrierKey.add(Byte.valueOf(encoded[i2]));
                i2++;
            }
            imsiEncryptionInfo3.keyType = (byte) imsiEncryptionInfo.getKeyType();
            this.mRadioProxy.setCarrierInfoForImsiEncryption_1_6(i, imsiEncryptionInfo3);
            return;
        }
        android.hardware.radio.V1_1.ImsiEncryptionInfo imsiEncryptionInfo4 = new android.hardware.radio.V1_1.ImsiEncryptionInfo();
        imsiEncryptionInfo4.mnc = imsiEncryptionInfo.getMnc();
        imsiEncryptionInfo4.mcc = imsiEncryptionInfo.getMcc();
        imsiEncryptionInfo4.keyIdentifier = imsiEncryptionInfo.getKeyIdentifier();
        if (imsiEncryptionInfo.getExpirationTime() != null) {
            imsiEncryptionInfo4.expirationTime = imsiEncryptionInfo.getExpirationTime().getTime();
        }
        byte[] encoded2 = imsiEncryptionInfo.getPublicKey().getEncoded();
        int length2 = encoded2.length;
        while (i2 < length2) {
            imsiEncryptionInfo4.carrierKey.add(Byte.valueOf(encoded2[i2]));
            i2++;
        }
        this.mRadioProxy.setCarrierInfoForImsiEncryption(i, imsiEncryptionInfo4);
    }

    public void setCdmaSubscriptionSource(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.setCdmaSubscriptionSource(i, i2);
        } else {
            this.mRadioProxy.setCdmaSubscriptionSource(i, i2);
        }
    }

    public void setFacilityLockForApp(int i, String str, boolean z, String str2, int i2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.setFacilityLockForApp(i, str, z, str2, i2, str3);
        } else {
            this.mRadioProxy.setFacilityLockForApp(i, str, z, str2, i2, str3);
        }
    }

    public void setSimCardPower(int i, int i2, Message message) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.setSimCardPower(i, i2);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            this.mRadioProxy.setSimCardPower_1_6(i, i2);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_1)) {
            this.mRadioProxy.setSimCardPower_1_1(i, i2);
        } else if (i2 == 0) {
            this.mRadioProxy.setSimCardPower(i, false);
        } else if (i2 == 1) {
            this.mRadioProxy.setSimCardPower(i, true);
        } else if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    public void setUiccSubscription(int i, int i2, int i3, int i4, int i5) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            SelectUiccSub selectUiccSub = new SelectUiccSub();
            selectUiccSub.slot = i2;
            selectUiccSub.appIndex = i3;
            selectUiccSub.subType = i4;
            selectUiccSub.actStatus = i5;
            this.mSimProxy.setUiccSubscription(i, selectUiccSub);
            return;
        }
        android.hardware.radio.V1_0.SelectUiccSub selectUiccSub2 = new android.hardware.radio.V1_0.SelectUiccSub();
        selectUiccSub2.slot = i2;
        selectUiccSub2.appIndex = i3;
        selectUiccSub2.subType = i4;
        selectUiccSub2.actStatus = i5;
        this.mRadioProxy.setUiccSubscription(i, selectUiccSub2);
    }

    public void supplyIccPin2ForApp(int i, String str, String str2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.supplyIccPin2ForApp(i, str, str2);
        } else {
            this.mRadioProxy.supplyIccPin2ForApp(i, str, str2);
        }
    }

    public void supplyIccPinForApp(int i, String str, String str2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.supplyIccPinForApp(i, str, str2);
        } else {
            this.mRadioProxy.supplyIccPinForApp(i, str, str2);
        }
    }

    public void supplyIccPuk2ForApp(int i, String str, String str2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.supplyIccPuk2ForApp(i, str, str2, str3);
        } else {
            this.mRadioProxy.supplyIccPuk2ForApp(i, str, str2, str3);
        }
    }

    public void supplyIccPukForApp(int i, String str, String str2, String str3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.supplyIccPukForApp(i, str, str2, str3);
        } else {
            this.mRadioProxy.supplyIccPukForApp(i, str, str2, str3);
        }
    }

    public void supplySimDepersonalization(int i, IccCardApplicationStatus.PersoSubState persoSubState, String str) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_5)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.supplySimDepersonalization(i, RILUtils.convertToHalPersoTypeAidl(persoSubState), str);
        } else {
            this.mRadioProxy.supplySimDepersonalization(i, RILUtils.convertToHalPersoType(persoSubState), str);
        }
    }

    public void updateSimPhonebookRecords(int i, SimPhonebookRecord simPhonebookRecord) throws RemoteException {
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_6)) {
            return;
        }
        if (isAidl()) {
            this.mSimProxy.updateSimPhonebookRecords(i, RILUtils.convertToHalPhonebookRecordInfoAidl(simPhonebookRecord));
        } else {
            this.mRadioProxy.updateSimPhonebookRecords(i, RILUtils.convertToHalPhonebookRecordInfo(simPhonebookRecord));
        }
    }
}
