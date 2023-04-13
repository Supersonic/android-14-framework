package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.util.SparseIntArray;
import com.android.server.hdmi.HdmiControlService;
import java.util.List;
/* loaded from: classes.dex */
public class PowerStatusMonitorAction extends HdmiCecFeatureAction {
    public final SparseIntArray mPowerStatus;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public PowerStatusMonitorAction(HdmiCecLocalDevice hdmiCecLocalDevice) {
        super(hdmiCecLocalDevice);
        this.mPowerStatus = new SparseIntArray();
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        queryPowerStatus();
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && hdmiCecMessage.getOpcode() == 144) {
            return handleReportPowerStatus(hdmiCecMessage);
        }
        return false;
    }

    public final boolean handleReportPowerStatus(HdmiCecMessage hdmiCecMessage) {
        int source = hdmiCecMessage.getSource();
        if (this.mPowerStatus.get(source, -2) == -2) {
            return false;
        }
        updatePowerStatus(source, hdmiCecMessage.getParams()[0] & 255, true);
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == 1) {
            handleTimeout();
        } else if (i2 != 2) {
        } else {
            queryPowerStatus();
        }
    }

    public final void handleTimeout() {
        for (int i = 0; i < this.mPowerStatus.size(); i++) {
            updatePowerStatus(this.mPowerStatus.keyAt(i), -1, false);
        }
        this.mPowerStatus.clear();
        this.mState = 2;
    }

    public final void resetPowerStatus(List<HdmiDeviceInfo> list) {
        this.mPowerStatus.clear();
        for (HdmiDeviceInfo hdmiDeviceInfo : list) {
            if (localDevice().mService.getCecVersion() < 6 || hdmiDeviceInfo.getCecVersion() < 6) {
                this.mPowerStatus.append(hdmiDeviceInfo.getLogicalAddress(), hdmiDeviceInfo.getDevicePowerStatus());
            }
        }
    }

    public final void queryPowerStatus() {
        List<HdmiDeviceInfo> deviceInfoList = localDevice().mService.getHdmiCecNetwork().getDeviceInfoList(false);
        resetPowerStatus(deviceInfoList);
        for (HdmiDeviceInfo hdmiDeviceInfo : deviceInfoList) {
            if (localDevice().mService.getCecVersion() < 6 || hdmiDeviceInfo.getCecVersion() < 6) {
                final int logicalAddress = hdmiDeviceInfo.getLogicalAddress();
                sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), logicalAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.PowerStatusMonitorAction.1
                    @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                    public void onSendCompleted(int i) {
                        if (i != 0) {
                            PowerStatusMonitorAction.this.updatePowerStatus(logicalAddress, -1, true);
                        }
                    }
                });
            }
        }
        this.mState = 1;
        addTimer(2, 60000);
        addTimer(1, 5000);
    }

    public final void updatePowerStatus(int i, int i2, boolean z) {
        localDevice().mService.getHdmiCecNetwork().updateDevicePowerStatus(i, i2);
        if (z) {
            this.mPowerStatus.delete(i);
        }
    }
}
