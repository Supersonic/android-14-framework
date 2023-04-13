package com.android.internal.telephony;

import android.hardware.radio.network.BarringInfo;
import android.hardware.radio.network.CellIdentity;
import android.hardware.radio.network.CellInfo;
import android.hardware.radio.network.EmergencyRegResult;
import android.hardware.radio.network.IRadioNetworkIndication;
import android.hardware.radio.network.LinkCapacityEstimate;
import android.hardware.radio.network.NetworkScanResult;
import android.hardware.radio.network.PhysicalChannelConfig;
import android.hardware.radio.network.SignalStrength;
import android.hardware.radio.network.SuppSvcNotification;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.telephony.AnomalyReporter;
import android.telephony.PhysicalChannelConfig;
import android.telephony.ServiceState;
import android.text.TextUtils;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/* loaded from: classes.dex */
public class NetworkIndication extends IRadioNetworkIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public NetworkIndication(RIL ril) {
        this.mRil = ril;
    }

    public void barringInfoChanged(int i, CellIdentity cellIdentity, BarringInfo[] barringInfoArr) {
        this.mRil.processIndication(4, i);
        if (cellIdentity == null || barringInfoArr == null) {
            reportAnomaly(UUID.fromString("645b16bb-c930-4c1c-9c5d-568696542e05"), "Invalid barringInfoChanged indication");
            this.mRil.riljLoge("Invalid barringInfoChanged indication");
            return;
        }
        this.mRil.notifyBarringInfoChanged(new android.telephony.BarringInfo(RILUtils.convertHalCellIdentity(cellIdentity), RILUtils.convertHalBarringInfoList(barringInfoArr)));
    }

    public void cdmaPrlChanged(int i, int i2) {
        this.mRil.processIndication(4, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1032, iArr);
        }
        this.mRil.mCdmaPrlChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void cellInfoList(int i, CellInfo[] cellInfoArr) {
        this.mRil.processIndication(4, i);
        ArrayList<android.telephony.CellInfo> convertHalCellInfoList = RILUtils.convertHalCellInfoList(cellInfoArr);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1036, convertHalCellInfoList);
        }
        this.mRil.mRilCellInfoListRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalCellInfoList, (Throwable) null));
    }

    public void currentLinkCapacityEstimate(int i, LinkCapacityEstimate linkCapacityEstimate) {
        this.mRil.processIndication(4, i);
        List<android.telephony.LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(linkCapacityEstimate);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1045, convertHalLceData);
        }
        RegistrantList registrantList = this.mRil.mLceInfoRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, convertHalLceData, (Throwable) null));
        }
    }

    public void currentPhysicalChannelConfigs(int i, PhysicalChannelConfig[] physicalChannelConfigArr) {
        this.mRil.processIndication(4, i);
        ArrayList arrayList = new ArrayList(physicalChannelConfigArr.length);
        try {
            for (PhysicalChannelConfig physicalChannelConfig : physicalChannelConfigArr) {
                PhysicalChannelConfig.Builder builder = new PhysicalChannelConfig.Builder();
                int tag = physicalChannelConfig.band.getTag();
                if (tag == 1) {
                    builder.setBand(physicalChannelConfig.band.getGeranBand());
                } else if (tag == 2) {
                    builder.setBand(physicalChannelConfig.band.getUtranBand());
                } else if (tag == 3) {
                    builder.setBand(physicalChannelConfig.band.getEutranBand());
                } else if (tag == 4) {
                    builder.setBand(physicalChannelConfig.band.getNgranBand());
                } else {
                    this.mRil.riljLoge("Unsupported band type " + physicalChannelConfig.band.getTag());
                }
                arrayList.add(builder.setCellConnectionStatus(RILUtils.convertHalCellConnectionStatus(physicalChannelConfig.status)).setDownlinkChannelNumber(physicalChannelConfig.downlinkChannelNumber).setUplinkChannelNumber(physicalChannelConfig.uplinkChannelNumber).setCellBandwidthDownlinkKhz(physicalChannelConfig.cellBandwidthDownlinkKhz).setCellBandwidthUplinkKhz(physicalChannelConfig.cellBandwidthUplinkKhz).setNetworkType(ServiceState.rilRadioTechnologyToNetworkType(physicalChannelConfig.rat)).setPhysicalCellId(physicalChannelConfig.physicalCellId).setContextIds(physicalChannelConfig.contextIds).build());
            }
            if (this.mRil.isLogOrTrace()) {
                this.mRil.unsljLogRet(1101, arrayList);
            }
            this.mRil.mPhysicalChannelConfigurationRegistrants.notifyRegistrants(new AsyncResult((Object) null, arrayList, (Throwable) null));
        } catch (IllegalArgumentException e) {
            reportAnomaly(UUID.fromString("918f0970-9aa9-4bcd-a28e-e49a83fe77d5"), "RIL reported invalid PCC (AIDL)");
            this.mRil.riljLoge("Invalid PhysicalChannelConfig " + e);
        }
    }

    public void currentSignalStrength(int i, SignalStrength signalStrength) {
        this.mRil.processIndication(4, i);
        android.telephony.SignalStrength fixupSignalStrength10 = this.mRil.fixupSignalStrength10(RILUtils.convertHalSignalStrength(signalStrength));
        if (this.mRil.isLogvOrTrace()) {
            this.mRil.unsljLogvRet(CallFailCause.CDMA_ACCESS_BLOCKED, fixupSignalStrength10);
        }
        Registrant registrant = this.mRil.mSignalStrengthRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, fixupSignalStrength10, (Throwable) null));
        }
    }

    public void imsNetworkStateChanged(int i) {
        this.mRil.processIndication(4, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1037);
        }
        this.mRil.mImsNetworkStateChangedRegistrants.notifyRegistrants();
    }

    public void networkScanResult(int i, NetworkScanResult networkScanResult) {
        this.mRil.processIndication(4, i);
        NetworkScanResult networkScanResult2 = new NetworkScanResult(networkScanResult.status, networkScanResult.error, RILUtils.convertHalCellInfoList(networkScanResult.networkInfos));
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1049, networkScanResult2);
        }
        this.mRil.mRilNetworkScanResultRegistrants.notifyRegistrants(new AsyncResult((Object) null, networkScanResult2, (Throwable) null));
    }

    public void networkStateChanged(int i) {
        this.mRil.processIndication(4, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1002);
        }
        this.mRil.mNetworkStateRegistrants.notifyRegistrants();
    }

    public void nitzTimeReceived(int i, String str, long j, long j2) {
        this.mRil.processIndication(4, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(CallFailCause.CDMA_NOT_EMERGENCY, str);
        }
        if (j <= 0 || j2 < 0 || j2 >= j) {
            reportAnomaly(UUID.fromString("fc7c56d4-485d-475a-aaff-394203c6cdfc"), "NITZ indication with invalid parameter");
            RIL ril = this.mRil;
            ril.riljLoge("NITZ parameter is invalid, ignoring nitzTimeReceived indication. receivedTimeMs = " + j + ", ageMs = " + j2);
            return;
        }
        Object[] objArr = {str, Long.valueOf(j), Long.valueOf(j2)};
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

    public void registrationFailed(int i, CellIdentity cellIdentity, String str, int i2, int i3, int i4) {
        this.mRil.processIndication(4, i);
        android.telephony.CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(cellIdentity);
        if (convertHalCellIdentity == null || TextUtils.isEmpty(str) || (i2 & 3) == 0 || (i2 & (-4)) != 0 || i3 < 0 || i4 < 0 || (i3 == Integer.MAX_VALUE && i4 == Integer.MAX_VALUE)) {
            reportAnomaly(UUID.fromString("f16e5703-6105-4341-9eb3-e68189156eb5"), "Invalid registrationFailed indication");
            RIL ril = this.mRil;
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid registrationFailed indication (ci is null)=");
            sb.append(convertHalCellIdentity == null);
            sb.append(" (chosenPlmn is empty)=");
            sb.append(TextUtils.isEmpty(str));
            sb.append(" (is CS/PS)=");
            sb.append((i2 & 3) == 0);
            sb.append(" (only CS/PS)=");
            sb.append((i2 & (-4)) != 0);
            sb.append(" (causeCode)=");
            sb.append(i3);
            sb.append(" (additionalCauseCode)=");
            sb.append(i4);
            ril.riljLoge(sb.toString());
            return;
        }
        RegistrationFailedEvent registrationFailedEvent = new RegistrationFailedEvent(convertHalCellIdentity, str, i2, i3, i4);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogMore(1104, registrationFailedEvent.toString());
        }
        this.mRil.mRegistrationFailedRegistrant.notifyRegistrant(new AsyncResult((Object) null, registrationFailedEvent, (Throwable) null));
    }

    public void restrictedStateChanged(int i, int i2) {
        this.mRil.processIndication(4, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogvRet(1023, Integer.valueOf(i2));
        }
        Registrant registrant = this.mRil.mRestrictedStateRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void suppSvcNotify(int i, SuppSvcNotification suppSvcNotification) {
        this.mRil.processIndication(4, i);
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

    public void voiceRadioTechChanged(int i, int i2) {
        this.mRil.processIndication(4, i);
        int[] iArr = {i2};
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1035, iArr);
        }
        this.mRil.mVoiceRadioTechChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void emergencyNetworkScanResult(int i, EmergencyRegResult emergencyRegResult) {
        this.mRil.processIndication(4, i);
        android.telephony.EmergencyRegResult convertHalEmergencyRegResult = RILUtils.convertHalEmergencyRegResult(emergencyRegResult);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1106, convertHalEmergencyRegResult);
        }
        this.mRil.mEmergencyNetworkScanRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalEmergencyRegResult, (Throwable) null));
    }

    private void reportAnomaly(UUID uuid, String str) {
        Integer num = this.mRil.mPhoneId;
        Phone phone = num == null ? null : PhoneFactory.getPhone(num.intValue());
        AnomalyReporter.reportAnomaly(uuid, str, phone == null ? -1 : phone.getCarrierId());
    }
}
