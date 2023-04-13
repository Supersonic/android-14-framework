package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public final class DeviceSelectActionFromPlayback extends HdmiCecFeatureAction {
    @VisibleForTesting
    static final int STATE_WAIT_FOR_ACTIVE_SOURCE_MESSAGE_AFTER_ROUTING_CHANGE = 4;
    @VisibleForTesting
    private static final int STATE_WAIT_FOR_ACTIVE_SOURCE_MESSAGE_AFTER_SET_STREAM_PATH = 5;
    @VisibleForTesting
    static final int STATE_WAIT_FOR_DEVICE_POWER_ON = 3;
    @VisibleForTesting
    static final int STATE_WAIT_FOR_REPORT_POWER_STATUS = 1;
    public final HdmiCecMessage mGivePowerStatus;
    public final boolean mIsCec20;
    public int mPowerStatusCounter;
    public final HdmiDeviceInfo mTarget;

    public DeviceSelectActionFromPlayback(HdmiCecLocalDevicePlayback hdmiCecLocalDevicePlayback, HdmiDeviceInfo hdmiDeviceInfo, IHdmiControlCallback iHdmiControlCallback) {
        this(hdmiCecLocalDevicePlayback, hdmiDeviceInfo, iHdmiControlCallback, hdmiCecLocalDevicePlayback.getDeviceInfo().getCecVersion() >= 6 && hdmiDeviceInfo.getCecVersion() >= 6);
    }

    @VisibleForTesting
    public DeviceSelectActionFromPlayback(HdmiCecLocalDevicePlayback hdmiCecLocalDevicePlayback, HdmiDeviceInfo hdmiDeviceInfo, IHdmiControlCallback iHdmiControlCallback, boolean z) {
        super(hdmiCecLocalDevicePlayback, iHdmiControlCallback);
        this.mPowerStatusCounter = 0;
        this.mTarget = hdmiDeviceInfo;
        this.mGivePowerStatus = HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), getTargetAddress());
        this.mIsCec20 = z;
    }

    public int getTargetAddress() {
        return this.mTarget.getLogicalAddress();
    }

    public final int getTargetPath() {
        return this.mTarget.getPhysicalAddress();
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        sendRoutingChange();
        if (!this.mIsCec20) {
            queryDevicePowerStatus();
        } else {
            HdmiDeviceInfo cecDeviceInfo = localDevice().mService.getHdmiCecNetwork().getCecDeviceInfo(getTargetAddress());
            int devicePowerStatus = cecDeviceInfo != null ? cecDeviceInfo.getDevicePowerStatus() : -1;
            if (devicePowerStatus == -1) {
                queryDevicePowerStatus();
            } else if (devicePowerStatus == 0) {
                this.mState = 4;
                addTimer(4, 2000);
                return true;
            }
        }
        this.mState = 1;
        addTimer(1, 2000);
        return true;
    }

    public final void queryDevicePowerStatus() {
        sendCommand(this.mGivePowerStatus, new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.DeviceSelectActionFromPlayback.1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 0) {
                    DeviceSelectActionFromPlayback.this.finishWithCallback(7);
                }
            }
        });
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getSource() != getTargetAddress()) {
            return false;
        }
        int opcode = hdmiCecMessage.getOpcode();
        byte[] params = hdmiCecMessage.getParams();
        if (opcode == 130) {
            finishWithCallback(0);
            return true;
        } else if (this.mState == 1 && opcode == 144) {
            return handleReportPowerStatus(params[0]);
        } else {
            return false;
        }
    }

    public final boolean handleReportPowerStatus(int i) {
        if (i == 0) {
            selectDevice();
            return true;
        } else if (i == 1) {
            if (this.mPowerStatusCounter == 0) {
                sendRoutingChange();
                this.mState = 3;
                addTimer(3, 5000);
            } else {
                selectDevice();
            }
            return true;
        } else if (i == 2) {
            if (this.mPowerStatusCounter < 2) {
                this.mState = 3;
                addTimer(3, 5000);
            } else {
                selectDevice();
            }
            return true;
        } else if (i != 3) {
            return false;
        } else {
            if (this.mPowerStatusCounter < 4) {
                this.mState = 2;
                addTimer(2, 5000);
            } else {
                selectDevice();
            }
            return true;
        }
    }

    public final void selectDevice() {
        sendRoutingChange();
        this.mState = 4;
        addTimer(4, 2000);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 != i) {
            Slog.w("DeviceSelectActionFromPlayback", "Timer in a wrong state. Ignored.");
        } else if (i2 == 1) {
            selectDevice();
            addTimer(this.mState, 2000);
        } else if (i2 == 3) {
            this.mPowerStatusCounter++;
            queryDevicePowerStatus();
            this.mState = 1;
            addTimer(1, 2000);
        } else if (i2 != 4) {
            if (i2 != 5) {
                return;
            }
            finishWithCallback(1);
        } else {
            sendSetStreamPath();
            this.mState = 5;
            addTimer(5, 2000);
        }
    }

    public final void sendRoutingChange() {
        sendCommand(HdmiCecMessageBuilder.buildRoutingChange(getSourceAddress(), playback().getActiveSource().physicalAddress, getTargetPath()));
    }

    public final void sendSetStreamPath() {
        sendCommand(HdmiCecMessageBuilder.buildSetStreamPath(getSourceAddress(), getTargetPath()));
    }
}
