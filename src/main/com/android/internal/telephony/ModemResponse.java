package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.modem.ActivityStatsInfo;
import android.hardware.radio.modem.IRadioModemResponse;
import android.hardware.radio.modem.ImeiInfo;
import android.os.SystemClock;
import android.telephony.ActivityStatsTechSpecificInfo;
import android.telephony.AnomalyReporter;
import android.telephony.ModemActivityInfo;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class ModemResponse extends IRadioModemResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public ModemResponse(RIL ril) {
        this.mRil = ril;
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void enableModemResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void getBasebandVersionResponse(RadioResponseInfo radioResponseInfo, String str) {
        RadioResponse.responseString(3, this.mRil, radioResponseInfo, str);
    }

    public void getDeviceIdentityResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4) {
        RadioResponse.responseStrings(3, this.mRil, radioResponseInfo, str, str2, str3, str4);
    }

    public void getImeiResponse(RadioResponseInfo radioResponseInfo, ImeiInfo imeiInfo) {
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, imeiInfo);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, imeiInfo);
        }
    }

    public void getHardwareConfigResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.modem.HardwareConfig[] hardwareConfigArr) {
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            ArrayList<HardwareConfig> convertHalHardwareConfigList = RILUtils.convertHalHardwareConfigList(hardwareConfigArr);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalHardwareConfigList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalHardwareConfigList);
        }
    }

    public void getModemActivityInfoResponse(RadioResponseInfo radioResponseInfo, ActivityStatsInfo activityStatsInfo) {
        ModemActivityInfo modemActivityInfo;
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                int i = activityStatsInfo.sleepModeTimeMs;
                int i2 = activityStatsInfo.idleModeTimeMs;
                int length = activityStatsInfo.techSpecificInfo.length;
                ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = new ActivityStatsTechSpecificInfo[length];
                for (int i3 = 0; i3 < length; i3++) {
                    android.hardware.radio.modem.ActivityStatsTechSpecificInfo activityStatsTechSpecificInfo = activityStatsInfo.techSpecificInfo[i3];
                    int i4 = activityStatsTechSpecificInfo.rat;
                    int i5 = activityStatsTechSpecificInfo.frequencyRange;
                    int[] iArr = new int[ModemActivityInfo.getNumTxPowerLevels()];
                    int i6 = activityStatsInfo.techSpecificInfo[i3].rxModeTimeMs;
                    for (int i7 = 0; i7 < ModemActivityInfo.getNumTxPowerLevels(); i7++) {
                        iArr[i7] = activityStatsInfo.techSpecificInfo[i3].txmModetimeMs[i7];
                    }
                    activityStatsTechSpecificInfoArr[i3] = new ActivityStatsTechSpecificInfo(i4, i5, iArr, i6);
                }
                modemActivityInfo = new ModemActivityInfo(SystemClock.elapsedRealtime(), i, i2, activityStatsTechSpecificInfoArr);
            } else {
                modemActivityInfo = new ModemActivityInfo(SystemClock.elapsedRealtime(), 0, 0, new ActivityStatsTechSpecificInfo[]{new ActivityStatsTechSpecificInfo(0, 0, new int[ModemActivityInfo.getNumTxPowerLevels()], 0)});
                radioResponseInfo.error = 0;
            }
            RadioResponse.sendMessageResponse(processResponse.mResult, modemActivityInfo);
            this.mRil.processResponseDone(processResponse, radioResponseInfo, modemActivityInfo);
        }
    }

    public void getModemStackStatusResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void getRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.modem.RadioCapability radioCapability) {
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
            int i = radioResponseInfo.error;
            if (i == 6 || i == 2) {
                convertHalRadioCapability = this.mRil.makeStaticRadioCapability();
                radioResponseInfo.error = 0;
            }
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalRadioCapability);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalRadioCapability);
        }
    }

    public void nvReadItemResponse(RadioResponseInfo radioResponseInfo, String str) {
        RadioResponse.responseString(3, this.mRil, radioResponseInfo, str);
    }

    public void nvResetConfigResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void nvWriteCdmaPrlResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void nvWriteItemResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void requestShutdownResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void sendDeviceStateResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
    }

    public void setRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.modem.RadioCapability radioCapability) {
        RILRequest processResponse = this.mRil.processResponse(3, radioResponseInfo);
        if (processResponse != null) {
            RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalRadioCapability);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalRadioCapability);
        }
    }

    public void setRadioPowerResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(3, this.mRil, radioResponseInfo);
        RIL ril = this.mRil;
        int i = radioResponseInfo.error;
        ril.mLastRadioPowerResult = i;
        if (i == 70) {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_RF_HARDWARE_ISSUE_UUID), "RF HW damaged");
        } else if (i == 71) {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_NO_RF_CALIBRATION_UUID), "No RF calibration data");
        } else if (i == 1 || i == 0) {
        } else {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_BUGREPORT_UUID), "Radio power failure");
        }
    }
}
