package com.android.internal.telephony;

import android.hardware.radio.voice.CdmaCallWaiting;
import android.hardware.radio.voice.CdmaInformationRecord;
import android.hardware.radio.voice.CdmaLineControlInfoRecord;
import android.hardware.radio.voice.CdmaNumberInfoRecord;
import android.hardware.radio.voice.CdmaRedirectingNumberInfoRecord;
import android.hardware.radio.voice.CdmaSignalInfoRecord;
import android.hardware.radio.voice.CdmaT53AudioControlInfoRecord;
import android.hardware.radio.voice.CfData;
import android.hardware.radio.voice.EmergencyNumber;
import android.hardware.radio.voice.IRadioVoiceIndication;
import android.hardware.radio.voice.SsInfoData;
import android.hardware.radio.voice.StkCcUnsolSsResult;
import android.os.AsyncResult;
import com.android.internal.telephony.cdma.CdmaCallWaitingNotification;
import com.android.internal.telephony.cdma.CdmaInformationRecords;
import com.android.internal.telephony.gsm.SsData;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class VoiceIndication extends IRadioVoiceIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public VoiceIndication(RIL ril) {
        this.mRil = ril;
    }

    public void callRing(int i, boolean z, CdmaSignalInfoRecord cdmaSignalInfoRecord) {
        char[] cArr;
        this.mRil.processIndication(6, i);
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

    public void callStateChanged(int i) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1001);
        }
        this.mRil.mCallStateRegistrants.notifyRegistrants();
    }

    public void cdmaCallWaiting(int i, CdmaCallWaiting cdmaCallWaiting) {
        this.mRil.processIndication(6, i);
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

    public void cdmaInfoRec(int i, CdmaInformationRecord[] cdmaInformationRecordArr) {
        CdmaInformationRecords cdmaInformationRecords;
        this.mRil.processIndication(6, i);
        for (CdmaInformationRecord cdmaInformationRecord : cdmaInformationRecordArr) {
            int i2 = cdmaInformationRecord.name;
            switch (i2) {
                case 0:
                case 7:
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaDisplayInfoRec(i2, cdmaInformationRecord.display[0].alphaBuf));
                    break;
                case 1:
                case 2:
                case 3:
                    CdmaNumberInfoRecord cdmaNumberInfoRecord = cdmaInformationRecord.number[0];
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaNumberInfoRec(i2, cdmaNumberInfoRecord.number, cdmaNumberInfoRecord.numberType, cdmaNumberInfoRecord.numberPlan, cdmaNumberInfoRecord.pi, cdmaNumberInfoRecord.si));
                    break;
                case 4:
                    CdmaSignalInfoRecord cdmaSignalInfoRecord = cdmaInformationRecord.signal[0];
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaSignalInfoRec(cdmaSignalInfoRecord.isPresent ? 1 : 0, cdmaSignalInfoRecord.signalType, cdmaSignalInfoRecord.alertPitch, cdmaSignalInfoRecord.signal));
                    break;
                case 5:
                    CdmaRedirectingNumberInfoRecord cdmaRedirectingNumberInfoRecord = cdmaInformationRecord.redir[0];
                    CdmaNumberInfoRecord cdmaNumberInfoRecord2 = cdmaRedirectingNumberInfoRecord.redirectingNumber;
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaRedirectingNumberInfoRec(cdmaNumberInfoRecord2.number, cdmaNumberInfoRecord2.numberType, cdmaNumberInfoRecord2.numberPlan, cdmaNumberInfoRecord2.pi, cdmaNumberInfoRecord2.si, cdmaRedirectingNumberInfoRecord.redirectingReason));
                    break;
                case 6:
                    CdmaLineControlInfoRecord cdmaLineControlInfoRecord = cdmaInformationRecord.lineCtrl[0];
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaLineControlInfoRec(cdmaLineControlInfoRecord.lineCtrlPolarityIncluded, cdmaLineControlInfoRecord.lineCtrlToggle, cdmaLineControlInfoRecord.lineCtrlReverse, cdmaLineControlInfoRecord.lineCtrlPowerDenial));
                    break;
                case 8:
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaT53ClirInfoRec(cdmaInformationRecord.clir[0].cause));
                    break;
                case 9:
                default:
                    throw new RuntimeException("RIL_UNSOL_CDMA_INFO_REC: unsupported record. Got " + CdmaInformationRecords.idToString(i2) + " ");
                case 10:
                    CdmaT53AudioControlInfoRecord cdmaT53AudioControlInfoRecord = cdmaInformationRecord.audioCtrl[0];
                    cdmaInformationRecords = new CdmaInformationRecords(new CdmaInformationRecords.CdmaT53AudioControlInfoRec(cdmaT53AudioControlInfoRecord.upLink, cdmaT53AudioControlInfoRecord.downLink));
                    break;
            }
            if (this.mRil.isLogOrTrace()) {
                this.mRil.unsljLogRet(1027, cdmaInformationRecords);
            }
            this.mRil.notifyRegistrantsCdmaInfoRec(cdmaInformationRecords);
        }
    }

    public void cdmaOtaProvisionStatus(int i, int i2) {
        this.mRil.processIndication(6, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1026, iArr);
        }
        this.mRil.mOtaProvisionRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void currentEmergencyNumberList(int i, EmergencyNumber[] emergencyNumberArr) {
        this.mRil.processIndication(6, i);
        ArrayList arrayList = new ArrayList(emergencyNumberArr.length);
        for (EmergencyNumber emergencyNumber : emergencyNumberArr) {
            arrayList.add(new android.telephony.emergency.EmergencyNumber(emergencyNumber.number, MccTable.countryCodeForMcc(emergencyNumber.mcc), emergencyNumber.mnc, emergencyNumber.categories, RILUtils.primitiveArrayToArrayList(emergencyNumber.urns), emergencyNumber.sources, 0));
        }
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1102, arrayList);
        }
        this.mRil.cacheEmergencyNumberListIndication(arrayList);
        this.mRil.mEmergencyNumberListRegistrants.notifyRegistrants(new AsyncResult((Object) null, arrayList, (Throwable) null));
    }

    public void enterEmergencyCallbackMode(int i) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1024);
        }
        Registrant registrant = this.mRil.mEmergencyCallbackModeRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant();
        }
    }

    public void exitEmergencyCallbackMode(int i) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1033);
        }
        this.mRil.mExitEmergencyCallbackModeRegistrants.notifyRegistrants();
    }

    public void indicateRingbackTone(int i, boolean z) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1029, Boolean.valueOf(z));
        }
        this.mRil.mRingbackToneRegistrants.notifyRegistrants(new AsyncResult((Object) null, Boolean.valueOf(z), (Throwable) null));
    }

    public void onSupplementaryServiceIndication(int i, StkCcUnsolSsResult stkCcUnsolSsResult) {
        this.mRil.processIndication(6, i);
        SsData ssData = new SsData();
        ssData.serviceType = ssData.ServiceTypeFromRILInt(stkCcUnsolSsResult.serviceType);
        ssData.requestType = ssData.RequestTypeFromRILInt(stkCcUnsolSsResult.requestType);
        ssData.teleserviceType = ssData.TeleserviceTypeFromRILInt(stkCcUnsolSsResult.teleserviceType);
        ssData.serviceClass = stkCcUnsolSsResult.serviceClass;
        ssData.result = stkCcUnsolSsResult.result;
        int i2 = 0;
        if (ssData.serviceType.isTypeCF() && ssData.requestType.isTypeInterrogation()) {
            CfData cfData = stkCcUnsolSsResult.cfData[0];
            int length = cfData.cfInfo.length;
            ssData.cfInfo = new CallForwardInfo[length];
            while (i2 < length) {
                android.hardware.radio.voice.CallForwardInfo callForwardInfo = cfData.cfInfo[i2];
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
            SsInfoData ssInfoData = stkCcUnsolSsResult.ssInfo[0];
            int length2 = ssInfoData.ssInfo.length;
            ssData.ssInfo = new int[length2];
            while (i2 < length2) {
                ssData.ssInfo[i2] = ssInfoData.ssInfo[i2];
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

    public void onUssd(int i, int i2, String str) {
        this.mRil.processIndication(6, i);
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

    public void resendIncallMute(int i) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1030);
        }
        this.mRil.mResendIncallMuteRegistrants.notifyRegistrants();
    }

    public void srvccStateNotify(int i, int i2) {
        this.mRil.processIndication(6, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1039, iArr);
        }
        this.mRil.writeMetricsSrvcc(i2);
        this.mRil.mSrvccStateRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void stkCallControlAlphaNotify(int i, String str) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1044, str);
        }
        Registrant registrant = this.mRil.mCatCcAlphaRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, str, (Throwable) null));
        }
    }

    public void stkCallSetup(int i, long j) {
        this.mRil.processIndication(6, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1015, Long.valueOf(j));
        }
        Registrant registrant = this.mRil.mCatCallSetUpRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Long.valueOf(j), (Throwable) null));
        }
    }
}
