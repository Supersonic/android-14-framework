package com.android.internal.telephony;

import android.hardware.radio.V1_0.CdmaCallWaiting;
import android.hardware.radio.V1_0.CdmaDisplayInfoRecord;
import android.hardware.radio.V1_0.CdmaInformationRecord;
import android.hardware.radio.V1_0.CdmaInformationRecords;
import android.hardware.radio.V1_0.CdmaLineControlInfoRecord;
import android.hardware.radio.V1_0.CdmaNumberInfoRecord;
import android.hardware.radio.V1_0.CdmaRedirectingNumberInfoRecord;
import android.hardware.radio.V1_0.CdmaSignalInfoRecord;
import android.hardware.radio.V1_0.CdmaSmsMessage;
import android.hardware.radio.V1_0.CdmaT53AudioControlInfoRecord;
import android.hardware.radio.V1_0.CdmaT53ClirInfoRecord;
import android.hardware.radio.V1_0.CellInfo;
import android.hardware.radio.V1_0.CfData;
import android.hardware.radio.V1_0.LceDataInfo;
import android.hardware.radio.V1_0.PcoDataInfo;
import android.hardware.radio.V1_0.SetupDataCallResult;
import android.hardware.radio.V1_0.SignalStrength;
import android.hardware.radio.V1_0.SimRefreshResult;
import android.hardware.radio.V1_0.SsInfoData;
import android.hardware.radio.V1_0.StkCcUnsolSsResult;
import android.hardware.radio.V1_0.SuppSvcNotification;
import android.hardware.radio.V1_1.KeepaliveStatus;
import android.hardware.radio.V1_1.NetworkScanResult;
import android.hardware.radio.V1_2.LinkCapacityEstimate;
import android.hardware.radio.V1_4.EmergencyNumber;
import android.hardware.radio.V1_4.PhysicalChannelConfig;
import android.hardware.radio.V1_5.BarringInfo;
import android.hardware.radio.V1_5.CellIdentity;
import android.hardware.radio.V1_6.IRadioIndication;
import android.hardware.radio.V1_6.PhonebookRecordInfo;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.RemoteException;
import android.telephony.AnomalyReporter;
import android.telephony.PcoData;
import android.telephony.PhysicalChannelConfig;
import android.telephony.ServiceState;
import android.text.TextUtils;
import com.android.internal.telephony.cdma.CdmaCallWaitingNotification;
import com.android.internal.telephony.cdma.CdmaInformationRecords;
import com.android.internal.telephony.gsm.SmsMessage;
import com.android.internal.telephony.gsm.SsData;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import com.android.internal.telephony.uicc.IccRefreshResponse;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.ReceivedPhonebookRecords;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class RadioIndication extends IRadioIndication.Stub {
    RIL mRil;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RadioIndication(RIL ril) {
        this.mRil = ril;
    }

    public void radioStateChanged(int i, int i2) {
        this.mRil.processIndication(0, i);
        int convertHalRadioState = RILUtils.convertHalRadioState(i2);
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogMore(1000, "radioStateChanged: " + convertHalRadioState);
        }
        this.mRil.setRadioState(convertHalRadioState, false);
    }

    public void callStateChanged(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1001);
        }
        this.mRil.mCallStateRegistrants.notifyRegistrants();
    }

    public void networkStateChanged(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1002);
        }
        this.mRil.mNetworkStateRegistrants.notifyRegistrants();
    }

    public void newSms(int i, ArrayList<Byte> arrayList) {
        this.mRil.processIndication(0, i);
        byte[] arrayListToPrimitiveArray = RILUtils.arrayListToPrimitiveArray(arrayList);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1003);
        }
        SmsMessage createFromPdu = SmsMessage.createFromPdu(arrayListToPrimitiveArray);
        Registrant registrant = this.mRil.mGsmSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, createFromPdu == null ? null : new android.telephony.SmsMessage(createFromPdu), (Throwable) null));
        }
    }

    public void newSmsStatusReport(int i, ArrayList<Byte> arrayList) {
        this.mRil.processIndication(0, i);
        byte[] arrayListToPrimitiveArray = RILUtils.arrayListToPrimitiveArray(arrayList);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1004);
        }
        Registrant registrant = this.mRil.mSmsStatusRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, arrayListToPrimitiveArray, (Throwable) null));
        }
    }

    public void newSmsOnSim(int i, int i2) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1005);
        }
        Registrant registrant = this.mRil.mSmsOnSimRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void onUssd(int i, int i2, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogMore(1006, PhoneConfigurationManager.SSSS + i2);
        }
        String[] strArr = {PhoneConfigurationManager.SSSS + i2, str};
        Registrant registrant = this.mRil.mUSSDRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, strArr, (Throwable) null));
        }
    }

    public void nitzTimeReceived(int i, String str, long j) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(CallFailCause.CDMA_NOT_EMERGENCY, str);
        }
        Object[] objArr = {str, Long.valueOf(j)};
        if (TelephonyProperties.ignore_nitz().orElse(Boolean.FALSE).booleanValue()) {
            if (this.mRil.isLogOrTrace()) {
                this.mRil.riljLog("ignoring UNSOL_NITZ_TIME_RECEIVED");
                return;
            }
            return;
        }
        Registrant registrant = this.mRil.mNITZTimeRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, objArr, (Throwable) null));
        }
        this.mRil.mLastNITZTimeInfo = objArr;
    }

    public void currentSignalStrength(int i, SignalStrength signalStrength) {
        this.mRil.processIndication(0, i);
        android.telephony.SignalStrength fixupSignalStrength10 = this.mRil.fixupSignalStrength10(RILUtils.convertHalSignalStrength(signalStrength));
        if (this.mRil.isLogvOrTrace()) {
            this.mRil.unsljLogvRet(CallFailCause.CDMA_ACCESS_BLOCKED, fixupSignalStrength10);
        }
        Registrant registrant = this.mRil.mSignalStrengthRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, fixupSignalStrength10, (Throwable) null));
        }
    }

    public void currentLinkCapacityEstimate(int i, LinkCapacityEstimate linkCapacityEstimate) {
        this.mRil.processIndication(0, i);
        List<android.telephony.LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(linkCapacityEstimate);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1045, convertHalLceData);
        }
        RegistrantList registrantList = this.mRil.mLceInfoRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, convertHalLceData, (Throwable) null));
        }
    }

    public void currentLinkCapacityEstimate_1_6(int i, android.hardware.radio.V1_6.LinkCapacityEstimate linkCapacityEstimate) {
        this.mRil.processIndication(0, i);
        List<android.telephony.LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(linkCapacityEstimate);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1045, convertHalLceData);
        }
        RegistrantList registrantList = this.mRil.mLceInfoRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, convertHalLceData, (Throwable) null));
        }
    }

    public void currentSignalStrength_1_2(int i, android.hardware.radio.V1_2.SignalStrength signalStrength) {
        this.mRil.processIndication(0, i);
        android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
        if (this.mRil.isLogvOrTrace()) {
            this.mRil.unsljLogvRet(CallFailCause.CDMA_ACCESS_BLOCKED, convertHalSignalStrength);
        }
        Registrant registrant = this.mRil.mSignalStrengthRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, convertHalSignalStrength, (Throwable) null));
        }
    }

    public void currentSignalStrength_1_4(int i, android.hardware.radio.V1_4.SignalStrength signalStrength) {
        this.mRil.processIndication(0, i);
        android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
        if (this.mRil.isLogvOrTrace()) {
            this.mRil.unsljLogvRet(CallFailCause.CDMA_ACCESS_BLOCKED, convertHalSignalStrength);
        }
        Registrant registrant = this.mRil.mSignalStrengthRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, convertHalSignalStrength, (Throwable) null));
        }
    }

    public void currentSignalStrength_1_6(int i, android.hardware.radio.V1_6.SignalStrength signalStrength) {
        this.mRil.processIndication(0, i);
        android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
        if (this.mRil.isLogvOrTrace()) {
            this.mRil.unsljLogvRet(CallFailCause.CDMA_ACCESS_BLOCKED, convertHalSignalStrength);
        }
        Registrant registrant = this.mRil.mSignalStrengthRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, convertHalSignalStrength, (Throwable) null));
        }
    }

    public void currentPhysicalChannelConfigs_1_4(int i, ArrayList<PhysicalChannelConfig> arrayList) {
        this.mRil.processIndication(0, i);
        physicalChannelConfigsIndication(arrayList);
    }

    public void currentPhysicalChannelConfigs_1_6(int i, ArrayList<android.hardware.radio.V1_6.PhysicalChannelConfig> arrayList) {
        this.mRil.processIndication(0, i);
        physicalChannelConfigsIndication(arrayList);
    }

    public void currentPhysicalChannelConfigs(int i, ArrayList<android.hardware.radio.V1_2.PhysicalChannelConfig> arrayList) {
        this.mRil.processIndication(0, i);
        physicalChannelConfigsIndication(arrayList);
    }

    public void currentEmergencyNumberList(int i, ArrayList<EmergencyNumber> arrayList) {
        this.mRil.processIndication(0, i);
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        Iterator<EmergencyNumber> it = arrayList.iterator();
        while (it.hasNext()) {
            EmergencyNumber next = it.next();
            arrayList2.add(new android.telephony.emergency.EmergencyNumber(next.number, MccTable.countryCodeForMcc(next.mcc), next.mnc, next.categories, next.urns, next.sources, 0));
        }
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1102, arrayList2);
        }
        this.mRil.cacheEmergencyNumberListIndication(arrayList2);
        this.mRil.mEmergencyNumberListRegistrants.notifyRegistrants(new AsyncResult((Object) null, arrayList2, (Throwable) null));
    }

    public void dataCallListChanged(int i, ArrayList<SetupDataCallResult> arrayList) {
        responseDataCallListChanged(i, arrayList);
    }

    public void dataCallListChanged_1_4(int i, ArrayList<android.hardware.radio.V1_4.SetupDataCallResult> arrayList) {
        responseDataCallListChanged(i, arrayList);
    }

    public void dataCallListChanged_1_5(int i, ArrayList<android.hardware.radio.V1_5.SetupDataCallResult> arrayList) {
        responseDataCallListChanged(i, arrayList);
    }

    public void dataCallListChanged_1_6(int i, ArrayList<android.hardware.radio.V1_6.SetupDataCallResult> arrayList) {
        responseDataCallListChanged(i, arrayList);
    }

    public void unthrottleApn(int i, String str) throws RemoteException {
        responseApnUnthrottled(i, str);
    }

    public void suppSvcNotify(int i, SuppSvcNotification suppSvcNotification) {
        this.mRil.processIndication(0, i);
        SuppServiceNotification suppServiceNotification = new SuppServiceNotification();
        suppServiceNotification.notificationType = suppSvcNotification.isMT ? 1 : 0;
        suppServiceNotification.code = suppSvcNotification.code;
        suppServiceNotification.index = suppSvcNotification.index;
        suppServiceNotification.type = suppSvcNotification.type;
        suppServiceNotification.number = suppSvcNotification.number;
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1011, suppServiceNotification);
        }
        Registrant registrant = this.mRil.mSsnRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, suppServiceNotification, (Throwable) null));
        }
    }

    public void stkSessionEnd(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1012);
        }
        Registrant registrant = this.mRil.mCatSessionEndRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    public void stkProactiveCommand(int i, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1013);
        }
        Registrant registrant = this.mRil.mCatProCmdRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, str, (Throwable) null));
        }
    }

    public void stkEventNotify(int i, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1014);
        }
        Registrant registrant = this.mRil.mCatEventRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, str, (Throwable) null));
        }
    }

    public void stkCallSetup(int i, long j) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1015, Long.valueOf(j));
        }
        Registrant registrant = this.mRil.mCatCallSetUpRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Long.valueOf(j), (Throwable) null));
        }
    }

    public void simSmsStorageFull(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1016);
        }
        Registrant registrant = this.mRil.mIccSmsFullRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }

    public void simRefresh(int i, SimRefreshResult simRefreshResult) {
        this.mRil.processIndication(0, i);
        IccRefreshResponse iccRefreshResponse = new IccRefreshResponse();
        iccRefreshResponse.refreshResult = simRefreshResult.type;
        iccRefreshResponse.efId = simRefreshResult.efId;
        iccRefreshResponse.aid = simRefreshResult.aid;
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1017, iccRefreshResponse);
        }
        this.mRil.mIccRefreshRegistrants.notifyRegistrants(new AsyncResult((Object) null, iccRefreshResponse, (Throwable) null));
    }

    public void callRing(int i, boolean z, CdmaSignalInfoRecord cdmaSignalInfoRecord) {
        char[] cArr;
        this.mRil.processIndication(0, i);
        if (z) {
            cArr = null;
        } else {
            cArr = new char[]{cdmaSignalInfoRecord.isPresent ? (char) 1 : (char) 0, (char) cdmaSignalInfoRecord.signalType, (char) cdmaSignalInfoRecord.alertPitch, (char) cdmaSignalInfoRecord.signal};
            this.mRil.writeMetricsCallRing(cArr);
        }
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1018, cArr);
        }
        Registrant registrant = this.mRil.mRingRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, cArr, (Throwable) null));
        }
    }

    public void simStatusChanged(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1019);
        }
        this.mRil.mIccStatusChangedRegistrants.notifyRegistrants();
    }

    public void cdmaNewSms(int i, CdmaSmsMessage cdmaSmsMessage) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1020);
        }
        android.telephony.SmsMessage smsMessage = new android.telephony.SmsMessage(RILUtils.convertHalCdmaSmsMessage(cdmaSmsMessage));
        Registrant registrant = this.mRil.mCdmaSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, smsMessage, (Throwable) null));
        }
    }

    public void newBroadcastSms(int i, ArrayList<Byte> arrayList) {
        this.mRil.processIndication(0, i);
        byte[] arrayListToPrimitiveArray = RILUtils.arrayListToPrimitiveArray(arrayList);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1021, IccUtils.bytesToHexString(arrayListToPrimitiveArray));
        }
        Registrant registrant = this.mRil.mGsmBroadcastSmsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, arrayListToPrimitiveArray, (Throwable) null));
        }
    }

    public void cdmaRuimSmsStorageFull(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1022);
        }
        Registrant registrant = this.mRil.mIccSmsFullRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }

    public void restrictedStateChanged(int i, int i2) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1023, Integer.valueOf(i2));
        }
        Registrant registrant = this.mRil.mRestrictedStateRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void enterEmergencyCallbackMode(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1024);
        }
        Registrant registrant = this.mRil.mEmergencyCallbackModeRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }

    public void cdmaCallWaiting(int i, CdmaCallWaiting cdmaCallWaiting) {
        this.mRil.processIndication(0, i);
        CdmaCallWaitingNotification cdmaCallWaitingNotification = new CdmaCallWaitingNotification();
        cdmaCallWaitingNotification.number = cdmaCallWaiting.number;
        int presentationFromCLIP = CdmaCallWaitingNotification.presentationFromCLIP(cdmaCallWaiting.numberPresentation);
        cdmaCallWaitingNotification.numberPresentation = presentationFromCLIP;
        cdmaCallWaitingNotification.name = cdmaCallWaiting.name;
        cdmaCallWaitingNotification.namePresentation = presentationFromCLIP;
        CdmaSignalInfoRecord cdmaSignalInfoRecord = cdmaCallWaiting.signalInfoRecord;
        cdmaCallWaitingNotification.isPresent = cdmaSignalInfoRecord.isPresent ? 1 : 0;
        cdmaCallWaitingNotification.signalType = cdmaSignalInfoRecord.signalType;
        cdmaCallWaitingNotification.alertPitch = cdmaSignalInfoRecord.alertPitch;
        cdmaCallWaitingNotification.signal = cdmaSignalInfoRecord.signal;
        cdmaCallWaitingNotification.numberType = cdmaCallWaiting.numberType;
        cdmaCallWaitingNotification.numberPlan = cdmaCallWaiting.numberPlan;
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1025, cdmaCallWaitingNotification);
        }
        this.mRil.mCallWaitingInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaCallWaitingNotification, (Throwable) null));
    }

    public void cdmaOtaProvisionStatus(int i, int i2) {
        this.mRil.processIndication(0, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1026, iArr);
        }
        this.mRil.mOtaProvisionRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void cdmaInfoRec(int i, CdmaInformationRecords cdmaInformationRecords) {
        com.android.internal.telephony.cdma.CdmaInformationRecords cdmaInformationRecords2;
        this.mRil.processIndication(0, i);
        int size = cdmaInformationRecords.infoRec.size();
        for (int i2 = 0; i2 < size; i2++) {
            CdmaInformationRecord cdmaInformationRecord = (CdmaInformationRecord) cdmaInformationRecords.infoRec.get(i2);
            int i3 = cdmaInformationRecord.name;
            switch (i3) {
                case 0:
                case 7:
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaDisplayInfoRec(i3, ((CdmaDisplayInfoRecord) cdmaInformationRecord.display.get(0)).alphaBuf));
                    break;
                case 1:
                case 2:
                case 3:
                    CdmaNumberInfoRecord cdmaNumberInfoRecord = (CdmaNumberInfoRecord) cdmaInformationRecord.number.get(0);
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaNumberInfoRec(i3, cdmaNumberInfoRecord.number, cdmaNumberInfoRecord.numberType, cdmaNumberInfoRecord.numberPlan, cdmaNumberInfoRecord.pi, cdmaNumberInfoRecord.si));
                    break;
                case 4:
                    CdmaSignalInfoRecord cdmaSignalInfoRecord = (CdmaSignalInfoRecord) cdmaInformationRecord.signal.get(0);
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaSignalInfoRec(cdmaSignalInfoRecord.isPresent ? 1 : 0, cdmaSignalInfoRecord.signalType, cdmaSignalInfoRecord.alertPitch, cdmaSignalInfoRecord.signal));
                    break;
                case 5:
                    CdmaRedirectingNumberInfoRecord cdmaRedirectingNumberInfoRecord = (CdmaRedirectingNumberInfoRecord) cdmaInformationRecord.redir.get(0);
                    CdmaNumberInfoRecord cdmaNumberInfoRecord2 = cdmaRedirectingNumberInfoRecord.redirectingNumber;
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaRedirectingNumberInfoRec(cdmaNumberInfoRecord2.number, cdmaNumberInfoRecord2.numberType, cdmaNumberInfoRecord2.numberPlan, cdmaNumberInfoRecord2.pi, cdmaNumberInfoRecord2.si, cdmaRedirectingNumberInfoRecord.redirectingReason));
                    break;
                case 6:
                    CdmaLineControlInfoRecord cdmaLineControlInfoRecord = (CdmaLineControlInfoRecord) cdmaInformationRecord.lineCtrl.get(0);
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaLineControlInfoRec(cdmaLineControlInfoRecord.lineCtrlPolarityIncluded, cdmaLineControlInfoRecord.lineCtrlToggle, cdmaLineControlInfoRecord.lineCtrlReverse, cdmaLineControlInfoRecord.lineCtrlPowerDenial));
                    break;
                case 8:
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaT53ClirInfoRec(((CdmaT53ClirInfoRecord) cdmaInformationRecord.clir.get(0)).cause));
                    break;
                case 9:
                default:
                    throw new RuntimeException("RIL_UNSOL_CDMA_INFO_REC: unsupported record. Got " + com.android.internal.telephony.cdma.CdmaInformationRecords.idToString(i3) + " ");
                case 10:
                    CdmaT53AudioControlInfoRecord cdmaT53AudioControlInfoRecord = (CdmaT53AudioControlInfoRecord) cdmaInformationRecord.audioCtrl.get(0);
                    cdmaInformationRecords2 = new com.android.internal.telephony.cdma.CdmaInformationRecords(new CdmaInformationRecords.CdmaT53AudioControlInfoRec(cdmaT53AudioControlInfoRecord.upLink, cdmaT53AudioControlInfoRecord.downLink));
                    break;
            }
            if (this.mRil.isLogOrTrace()) {
                this.mRil.unsljLogRet(1027, cdmaInformationRecords2);
            }
            this.mRil.notifyRegistrantsCdmaInfoRec(cdmaInformationRecords2);
        }
    }

    public void indicateRingbackTone(int i, boolean z) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1029, Boolean.valueOf(z));
        }
        this.mRil.mRingbackToneRegistrants.notifyRegistrants(new AsyncResult((Object) null, Boolean.valueOf(z), (Throwable) null));
    }

    public void resendIncallMute(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1030);
        }
        this.mRil.mResendIncallMuteRegistrants.notifyRegistrants();
    }

    public void cdmaSubscriptionSourceChanged(int i, int i2) {
        this.mRil.processIndication(0, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1031, iArr);
        }
        this.mRil.mCdmaSubscriptionChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void cdmaPrlChanged(int i, int i2) {
        this.mRil.processIndication(0, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1032, iArr);
        }
        this.mRil.mCdmaPrlChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void exitEmergencyCallbackMode(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1033);
        }
        this.mRil.mExitEmergencyCallbackModeRegistrants.notifyRegistrants();
    }

    public void rilConnected(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1034);
        }
        this.mRil.setRadioPower(false, null);
        RIL ril = this.mRil;
        ril.setCdmaSubscriptionSource(ril.mCdmaSubscription, null);
        this.mRil.notifyRegistrantsRilConnectionChanged(15);
    }

    public void voiceRadioTechChanged(int i, int i2) {
        this.mRil.processIndication(0, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1035, iArr);
        }
        this.mRil.mVoiceRadioTechChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void cellInfoList(int i, ArrayList<CellInfo> arrayList) {
        this.mRil.processIndication(0, i);
        responseCellInfoList(arrayList);
    }

    public void cellInfoList_1_2(int i, ArrayList<android.hardware.radio.V1_2.CellInfo> arrayList) {
        this.mRil.processIndication(0, i);
        responseCellInfoList(arrayList);
    }

    public void cellInfoList_1_4(int i, ArrayList<android.hardware.radio.V1_4.CellInfo> arrayList) {
        this.mRil.processIndication(0, i);
        responseCellInfoList(arrayList);
    }

    public void cellInfoList_1_5(int i, ArrayList<android.hardware.radio.V1_5.CellInfo> arrayList) {
        this.mRil.processIndication(0, i);
        responseCellInfoList(arrayList);
    }

    public void cellInfoList_1_6(int i, ArrayList<android.hardware.radio.V1_6.CellInfo> arrayList) {
        this.mRil.processIndication(0, i);
        responseCellInfoList(arrayList);
    }

    private void responseCellInfoList(ArrayList<? extends Object> arrayList) {
        ArrayList<android.telephony.CellInfo> convertHalCellInfoList = RILUtils.convertHalCellInfoList(arrayList);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1036, convertHalCellInfoList);
        }
        this.mRil.mRilCellInfoListRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalCellInfoList, (Throwable) null));
    }

    public void uiccApplicationsEnablementChanged(int i, boolean z) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1103, Boolean.valueOf(z));
        }
        this.mRil.mUiccApplicationsEnablementRegistrants.notifyResult(Boolean.valueOf(z));
    }

    public void networkScanResult(int i, NetworkScanResult networkScanResult) {
        responseNetworkScan(i, networkScanResult);
    }

    public void networkScanResult_1_2(int i, android.hardware.radio.V1_2.NetworkScanResult networkScanResult) {
        responseNetworkScan_1_2(i, networkScanResult);
    }

    public void networkScanResult_1_4(int i, android.hardware.radio.V1_4.NetworkScanResult networkScanResult) {
        responseNetworkScan_1_4(i, networkScanResult);
    }

    public void networkScanResult_1_5(int i, android.hardware.radio.V1_5.NetworkScanResult networkScanResult) {
        responseNetworkScan_1_5(i, networkScanResult);
    }

    public void networkScanResult_1_6(int i, android.hardware.radio.V1_6.NetworkScanResult networkScanResult) {
        responseNetworkScan_1_6(i, networkScanResult);
    }

    public void imsNetworkStateChanged(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1037);
        }
        this.mRil.mImsNetworkStateChangedRegistrants.notifyRegistrants();
    }

    public void subscriptionStatusChanged(int i, boolean z) {
        this.mRil.processIndication(0, i);
        int[] iArr = {z ? 1 : 0};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1038, iArr);
        }
        this.mRil.mSubscriptionStatusRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void srvccStateNotify(int i, int i2) {
        this.mRil.processIndication(0, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1039, iArr);
        }
        this.mRil.writeMetricsSrvcc(i2);
        this.mRil.mSrvccStateRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void hardwareConfigChanged(int i, ArrayList<android.hardware.radio.V1_0.HardwareConfig> arrayList) {
        this.mRil.processIndication(0, i);
        ArrayList<HardwareConfig> convertHalHardwareConfigList = RILUtils.convertHalHardwareConfigList(arrayList);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1040, convertHalHardwareConfigList);
        }
        this.mRil.mHardwareConfigChangeRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalHardwareConfigList, (Throwable) null));
    }

    public void radioCapabilityIndication(int i, android.hardware.radio.V1_0.RadioCapability radioCapability) {
        this.mRil.processIndication(0, i);
        RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1042, convertHalRadioCapability);
        }
        this.mRil.mPhoneRadioCapabilityChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalRadioCapability, (Throwable) null));
    }

    public void onSupplementaryServiceIndication(int i, StkCcUnsolSsResult stkCcUnsolSsResult) {
        int i2 = 0;
        this.mRil.processIndication(0, i);
        SsData ssData = new SsData();
        ssData.serviceType = ssData.ServiceTypeFromRILInt(stkCcUnsolSsResult.serviceType);
        ssData.requestType = ssData.RequestTypeFromRILInt(stkCcUnsolSsResult.requestType);
        ssData.teleserviceType = ssData.TeleserviceTypeFromRILInt(stkCcUnsolSsResult.teleserviceType);
        ssData.serviceClass = stkCcUnsolSsResult.serviceClass;
        ssData.result = stkCcUnsolSsResult.result;
        if (ssData.serviceType.isTypeCF() && ssData.requestType.isTypeInterrogation()) {
            CfData cfData = (CfData) stkCcUnsolSsResult.cfData.get(0);
            int size = cfData.cfInfo.size();
            ssData.cfInfo = new CallForwardInfo[size];
            while (i2 < size) {
                android.hardware.radio.V1_0.CallForwardInfo callForwardInfo = (android.hardware.radio.V1_0.CallForwardInfo) cfData.cfInfo.get(i2);
                ssData.cfInfo[i2] = new CallForwardInfo();
                CallForwardInfo callForwardInfo2 = ssData.cfInfo[i2];
                callForwardInfo2.status = callForwardInfo.status;
                callForwardInfo2.reason = callForwardInfo.reason;
                callForwardInfo2.serviceClass = callForwardInfo.serviceClass;
                callForwardInfo2.toa = callForwardInfo.toa;
                callForwardInfo2.number = callForwardInfo.number;
                callForwardInfo2.timeSeconds = callForwardInfo.timeSeconds;
                RIL ril = this.mRil;
                ril.riljLog("[SS Data] CF Info " + i2 + " : " + ssData.cfInfo[i2]);
                i2++;
            }
        } else {
            SsInfoData ssInfoData = (SsInfoData) stkCcUnsolSsResult.ssInfo.get(0);
            int size2 = ssInfoData.ssInfo.size();
            ssData.ssInfo = new int[size2];
            while (i2 < size2) {
                ssData.ssInfo[i2] = ((Integer) ssInfoData.ssInfo.get(i2)).intValue();
                RIL ril2 = this.mRil;
                ril2.riljLog("[SS Data] SS Info " + i2 + " : " + ssData.ssInfo[i2]);
                i2++;
            }
        }
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1043, ssData);
        }
        Registrant registrant = this.mRil.mSsRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, ssData, (Throwable) null));
        }
    }

    public void stkCallControlAlphaNotify(int i, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1044, str);
        }
        Registrant registrant = this.mRil.mCatCcAlphaRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, str, (Throwable) null));
        }
    }

    public void lceData(int i, LceDataInfo lceDataInfo) {
        this.mRil.processIndication(0, i);
        List<android.telephony.LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(lceDataInfo);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1045, convertHalLceData);
        }
        RegistrantList registrantList = this.mRil.mLceInfoRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, convertHalLceData, (Throwable) null));
        }
    }

    public void pcoData(int i, PcoDataInfo pcoDataInfo) {
        this.mRil.processIndication(0, i);
        PcoData pcoData = new PcoData(pcoDataInfo.cid, pcoDataInfo.bearerProto, pcoDataInfo.pcoId, RILUtils.arrayListToPrimitiveArray(pcoDataInfo.contents));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1046, pcoData);
        }
        this.mRil.mPcoDataRegistrants.notifyRegistrants(new AsyncResult((Object) null, pcoData, (Throwable) null));
    }

    public void modemReset(int i, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1047, str);
        }
        this.mRil.writeMetricsModemRestartEvent(str);
        this.mRil.mModemResetRegistrants.notifyRegistrants(new AsyncResult((Object) null, str, (Throwable) null));
    }

    public void carrierInfoForImsiEncryption(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1048, null);
        }
        this.mRil.mCarrierInfoForImsiEncryptionRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
    }

    public void keepaliveStatus(int i, KeepaliveStatus keepaliveStatus) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogRet(1050, "handle=" + keepaliveStatus.sessionHandle + " code=" + keepaliveStatus.code);
        }
        this.mRil.mNattKeepaliveStatusRegistrants.notifyRegistrants(new AsyncResult((Object) null, new com.android.internal.telephony.data.KeepaliveStatus(keepaliveStatus.sessionHandle, keepaliveStatus.code), (Throwable) null));
    }

    public void simPhonebookChanged(int i) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1053);
        }
        this.mRil.mSimPhonebookChangedRegistrants.notifyRegistrants();
    }

    public void simPhonebookRecordsReceived(int i, byte b, ArrayList<PhonebookRecordInfo> arrayList) {
        this.mRil.processIndication(0, i);
        ArrayList arrayList2 = new ArrayList();
        Iterator<PhonebookRecordInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(RILUtils.convertHalPhonebookRecordInfo(it.next()));
        }
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogRet(1054, "status = " + ((int) b) + " received " + arrayList.size() + " records");
        }
        this.mRil.mSimPhonebookRecordsReceivedRegistrants.notifyRegistrants(new AsyncResult((Object) null, new ReceivedPhonebookRecords(b, arrayList2), (Throwable) null));
    }

    public void registrationFailed(int i, CellIdentity cellIdentity, String str, int i2, int i3, int i4) {
        this.mRil.processIndication(0, i);
        android.telephony.CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(cellIdentity);
        if (convertHalCellIdentity == null || TextUtils.isEmpty(str) || (i2 & 3) == 0 || (i2 & (-4)) != 0 || i3 < 0 || i4 < 0 || (i3 == Integer.MAX_VALUE && i4 == Integer.MAX_VALUE)) {
            reportAnomaly(UUID.fromString("f16e5703-6105-4341-9eb3-e68189156eb4"), "Invalid registrationFailed indication");
            this.mRil.riljLoge("Invalid registrationFailed indication");
            return;
        }
        this.mRil.mRegistrationFailedRegistrant.notifyRegistrant(new AsyncResult((Object) null, new RegistrationFailedEvent(convertHalCellIdentity, str, i2, i3, i4), (Throwable) null));
    }

    public void barringInfoChanged(int i, CellIdentity cellIdentity, ArrayList<BarringInfo> arrayList) {
        this.mRil.processIndication(0, i);
        if (cellIdentity == null || arrayList == null) {
            reportAnomaly(UUID.fromString("645b16bb-c930-4c1c-9c5d-568696542e05"), "Invalid barringInfoChanged indication");
            this.mRil.riljLoge("Invalid barringInfoChanged indication");
            return;
        }
        this.mRil.mBarringInfoChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, new android.telephony.BarringInfo(RILUtils.convertHalCellIdentity(cellIdentity), RILUtils.convertHalBarringInfoList(arrayList)), (Throwable) null));
    }

    private void setFrequencyRangeOrChannelNumber(PhysicalChannelConfig.Builder builder, android.hardware.radio.V1_4.PhysicalChannelConfig physicalChannelConfig) {
        byte discriminator = physicalChannelConfig.rfInfo.getDiscriminator();
        if (discriminator == 0) {
            builder.setFrequencyRange(physicalChannelConfig.rfInfo.range());
        } else if (discriminator == 1) {
            builder.setDownlinkChannelNumber(physicalChannelConfig.rfInfo.channelNumber());
        } else {
            RIL ril = this.mRil;
            ril.riljLoge("Unsupported frequency type " + ((int) physicalChannelConfig.rfInfo.getDiscriminator()));
        }
    }

    private void physicalChannelConfigsIndication(List<? extends Object> list) {
        ArrayList arrayList = new ArrayList(list.size());
        try {
            for (Object obj : list) {
                if (obj instanceof android.hardware.radio.V1_2.PhysicalChannelConfig) {
                    android.hardware.radio.V1_2.PhysicalChannelConfig physicalChannelConfig = (android.hardware.radio.V1_2.PhysicalChannelConfig) obj;
                    arrayList.add(new PhysicalChannelConfig.Builder().setCellConnectionStatus(RILUtils.convertHalCellConnectionStatus(physicalChannelConfig.status)).setCellBandwidthDownlinkKhz(physicalChannelConfig.cellBandwidthDownlink).build());
                } else if (obj instanceof android.hardware.radio.V1_4.PhysicalChannelConfig) {
                    android.hardware.radio.V1_4.PhysicalChannelConfig physicalChannelConfig2 = (android.hardware.radio.V1_4.PhysicalChannelConfig) obj;
                    PhysicalChannelConfig.Builder builder = new PhysicalChannelConfig.Builder();
                    setFrequencyRangeOrChannelNumber(builder, physicalChannelConfig2);
                    arrayList.add(builder.setCellConnectionStatus(RILUtils.convertHalCellConnectionStatus(physicalChannelConfig2.base.status)).setCellBandwidthDownlinkKhz(physicalChannelConfig2.base.cellBandwidthDownlink).setNetworkType(ServiceState.rilRadioTechnologyToNetworkType(physicalChannelConfig2.rat)).setPhysicalCellId(physicalChannelConfig2.physicalCellId).setContextIds(physicalChannelConfig2.contextIds.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.RadioIndication$$ExternalSyntheticLambda0
                        @Override // java.util.function.ToIntFunction
                        public final int applyAsInt(Object obj2) {
                            int intValue;
                            intValue = ((Integer) obj2).intValue();
                            return intValue;
                        }
                    }).toArray()).build());
                } else if (obj instanceof android.hardware.radio.V1_6.PhysicalChannelConfig) {
                    android.hardware.radio.V1_6.PhysicalChannelConfig physicalChannelConfig3 = (android.hardware.radio.V1_6.PhysicalChannelConfig) obj;
                    PhysicalChannelConfig.Builder builder2 = new PhysicalChannelConfig.Builder();
                    byte discriminator = physicalChannelConfig3.band.getDiscriminator();
                    if (discriminator == 0) {
                        builder2.setBand(physicalChannelConfig3.band.geranBand());
                    } else if (discriminator == 1) {
                        builder2.setBand(physicalChannelConfig3.band.utranBand());
                    } else if (discriminator == 2) {
                        builder2.setBand(physicalChannelConfig3.band.eutranBand());
                    } else if (discriminator == 3) {
                        builder2.setBand(physicalChannelConfig3.band.ngranBand());
                    } else {
                        RIL ril = this.mRil;
                        ril.riljLoge("Unsupported band " + ((int) physicalChannelConfig3.band.getDiscriminator()));
                    }
                    arrayList.add(builder2.setCellConnectionStatus(RILUtils.convertHalCellConnectionStatus(physicalChannelConfig3.status)).setDownlinkChannelNumber(physicalChannelConfig3.downlinkChannelNumber).setUplinkChannelNumber(physicalChannelConfig3.uplinkChannelNumber).setCellBandwidthDownlinkKhz(physicalChannelConfig3.cellBandwidthDownlinkKhz).setCellBandwidthUplinkKhz(physicalChannelConfig3.cellBandwidthUplinkKhz).setNetworkType(ServiceState.rilRadioTechnologyToNetworkType(physicalChannelConfig3.rat)).setPhysicalCellId(physicalChannelConfig3.physicalCellId).setContextIds(physicalChannelConfig3.contextIds.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.RadioIndication$$ExternalSyntheticLambda1
                        @Override // java.util.function.ToIntFunction
                        public final int applyAsInt(Object obj2) {
                            int intValue;
                            intValue = ((Integer) obj2).intValue();
                            return intValue;
                        }
                    }).toArray()).build());
                } else {
                    RIL ril2 = this.mRil;
                    ril2.riljLoge("Unsupported PhysicalChannelConfig " + obj);
                }
            }
            if (this.mRil.isLogOrTrace()) {
                this.mRil.unsljLogRet(1101, arrayList);
            }
            this.mRil.mPhysicalChannelConfigurationRegistrants.notifyRegistrants(new AsyncResult((Object) null, arrayList, (Throwable) null));
        } catch (IllegalArgumentException e) {
            reportAnomaly(UUID.fromString("918f0970-9aa9-4bcd-a28e-e49a83fe77d5"), "RIL reported invalid PCC (HIDL)");
            RIL ril3 = this.mRil;
            ril3.riljLoge("Invalid PhysicalChannelConfig " + e);
        }
    }

    private void responseNetworkScan(int i, NetworkScanResult networkScanResult) {
        this.mRil.processIndication(0, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(new ArrayList(networkScanResult.networkInfos)));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    private void responseNetworkScan_1_2(int i, android.hardware.radio.V1_2.NetworkScanResult networkScanResult) {
        this.mRil.processIndication(0, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(new ArrayList(networkScanResult.networkInfos)));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    private void responseNetworkScan_1_4(int i, android.hardware.radio.V1_4.NetworkScanResult networkScanResult) {
        this.mRil.processIndication(0, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(new ArrayList(networkScanResult.networkInfos)));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    private void responseNetworkScan_1_5(int i, android.hardware.radio.V1_5.NetworkScanResult networkScanResult) {
        this.mRil.processIndication(0, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(new ArrayList(networkScanResult.networkInfos)));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    private void responseNetworkScan_1_6(int i, android.hardware.radio.V1_6.NetworkScanResult networkScanResult) {
        this.mRil.processIndication(0, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(new ArrayList(networkScanResult.networkInfos)));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    private void responseDataCallListChanged(int i, List<?> list) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1010, list);
        }
        this.mRil.mDataCallListChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, RILUtils.convertHalDataCallResultList((List<? extends Object>) list), (Throwable) null));
    }

    private void responseApnUnthrottled(int i, String str) {
        this.mRil.processIndication(0, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1052, str);
        }
        this.mRil.mApnUnthrottledRegistrants.notifyRegistrants(new AsyncResult((Object) null, str, (Throwable) null));
    }

    private void reportAnomaly(UUID uuid, String str) {
        Integer num = this.mRil.mPhoneId;
        Phone phone = num == null ? null : PhoneFactory.getPhone(num.intValue());
        AnomalyReporter.reportAnomaly(uuid, str, phone == null ? -1 : phone.getCarrierId());
    }
}
