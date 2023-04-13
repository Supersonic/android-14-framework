package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.util.Slog;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public final class DevicePowerStatusAction extends HdmiCecFeatureAction {
    public int mRetriesOnTimeout;
    public final int mTargetAddress;

    public static DevicePowerStatusAction create(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback) {
        if (hdmiCecLocalDevice == null || iHdmiControlCallback == null) {
            Slog.e("DevicePowerStatusAction", "Wrong arguments");
            return null;
        }
        return new DevicePowerStatusAction(hdmiCecLocalDevice, i, iHdmiControlCallback);
    }

    public DevicePowerStatusAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, iHdmiControlCallback);
        this.mRetriesOnTimeout = 1;
        this.mTargetAddress = i;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        HdmiDeviceInfo cecDeviceInfo;
        int devicePowerStatus;
        HdmiControlService hdmiControlService = localDevice().mService;
        if (hdmiControlService.getCecVersion() >= 6 && (cecDeviceInfo = hdmiControlService.getHdmiCecNetwork().getCecDeviceInfo(this.mTargetAddress)) != null && cecDeviceInfo.getCecVersion() >= 6 && (devicePowerStatus = cecDeviceInfo.getDevicePowerStatus()) != -1) {
            finishWithCallback(devicePowerStatus);
            return true;
        }
        queryDevicePowerStatus();
        this.mState = 1;
        addTimer(1, 2000);
        return true;
    }

    public final void queryDevicePowerStatus() {
        sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), this.mTargetAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.DevicePowerStatusAction$$ExternalSyntheticLambda0
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public final void onSendCompleted(int i) {
                DevicePowerStatusAction.this.lambda$queryDevicePowerStatus$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$queryDevicePowerStatus$0(int i) {
        if (i == 1) {
            finishWithCallback(-1);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && this.mTargetAddress == hdmiCecMessage.getSource() && hdmiCecMessage.getOpcode() == 144) {
            finishWithCallback(hdmiCecMessage.getParams()[0]);
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        if (this.mState == i && i == 1) {
            int i2 = this.mRetriesOnTimeout;
            if (i2 > 0) {
                this.mRetriesOnTimeout = i2 - 1;
                start();
                return;
            }
            finishWithCallback(-1);
        }
    }
}
