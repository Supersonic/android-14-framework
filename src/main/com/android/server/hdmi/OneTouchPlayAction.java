package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.net.INetd;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public final class OneTouchPlayAction extends HdmiCecFeatureAction {
    @VisibleForTesting
    static final int STATE_WAITING_FOR_REPORT_POWER_STATUS = 1;
    public final boolean mIsCec20;
    public int mPowerStatusCounter;
    public HdmiCecLocalDeviceSource mSource;
    public final int mTargetAddress;

    public static OneTouchPlayAction create(HdmiCecLocalDeviceSource hdmiCecLocalDeviceSource, int i, IHdmiControlCallback iHdmiControlCallback) {
        if (hdmiCecLocalDeviceSource == null || iHdmiControlCallback == null) {
            Slog.e("OneTouchPlayAction", "Wrong arguments");
            return null;
        }
        return new OneTouchPlayAction(hdmiCecLocalDeviceSource, i, iHdmiControlCallback);
    }

    public OneTouchPlayAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback) {
        this(hdmiCecLocalDevice, i, iHdmiControlCallback, hdmiCecLocalDevice.getDeviceInfo().getCecVersion() >= 6 && getTargetCecVersion(hdmiCecLocalDevice, i) >= 6);
    }

    @VisibleForTesting
    public OneTouchPlayAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback, boolean z) {
        super(hdmiCecLocalDevice, iHdmiControlCallback);
        this.mPowerStatusCounter = 0;
        this.mTargetAddress = i;
        this.mIsCec20 = z;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mSource = source();
        sendCommand(HdmiCecMessageBuilder.buildTextViewOn(getSourceAddress(), this.mTargetAddress));
        boolean z = this.mIsCec20 && getTargetDevicePowerStatus(this.mSource, this.mTargetAddress, -1) == 0;
        setAndBroadcastActiveSource();
        if (shouldTurnOnConnectedAudioSystem()) {
            sendCommand(HdmiCecMessageBuilder.buildSystemAudioModeRequest(getSourceAddress(), 5, getSourcePath(), true));
        }
        if (!this.mIsCec20) {
            queryDevicePowerStatus();
        } else {
            int targetDevicePowerStatus = getTargetDevicePowerStatus(this.mSource, this.mTargetAddress, -1);
            if (targetDevicePowerStatus == -1) {
                queryDevicePowerStatus();
            } else if (targetDevicePowerStatus == 0) {
                if (!z) {
                    maySendActiveSource();
                }
                finishWithCallback(0);
                return true;
            }
        }
        this.mState = 1;
        addTimer(1, 2000);
        return true;
    }

    public final void setAndBroadcastActiveSource() {
        this.mSource.mService.setAndBroadcastActiveSourceFromOneDeviceType(this.mTargetAddress, getSourcePath(), "OneTouchPlayAction#broadcastActiveSource()");
        if (this.mSource.mService.audioSystem() != null) {
            this.mSource = this.mSource.mService.audioSystem();
        }
        this.mSource.setRoutingPort(0);
        this.mSource.setLocalActivePort(0);
    }

    public final void maySendActiveSource() {
        this.mSource.maySendActiveSource(this.mTargetAddress);
    }

    public final void queryDevicePowerStatus() {
        sendCommand(HdmiCecMessageBuilder.buildGiveDevicePowerStatus(getSourceAddress(), this.mTargetAddress));
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && this.mTargetAddress == hdmiCecMessage.getSource() && hdmiCecMessage.getOpcode() == 144) {
            if (hdmiCecMessage.getParams()[0] == 0) {
                maySendActiveSource();
                finishWithCallback(0);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        if (this.mState == i && i == 1) {
            int i2 = this.mPowerStatusCounter;
            this.mPowerStatusCounter = i2 + 1;
            if (i2 < 10) {
                queryDevicePowerStatus();
                addTimer(this.mState, 2000);
                return;
            }
            finishWithCallback(1);
        }
    }

    public final boolean shouldTurnOnConnectedAudioSystem() {
        HdmiControlService hdmiControlService = this.mSource.mService;
        if (hdmiControlService.isAudioSystemDevice()) {
            return false;
        }
        String stringValue = hdmiControlService.getHdmiCecConfig().getStringValue("power_control_mode");
        return stringValue.equals("to_tv_and_audio_system") || stringValue.equals(INetd.IF_FLAG_BROADCAST);
    }

    public static int getTargetCecVersion(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        HdmiDeviceInfo cecDeviceInfo = hdmiCecLocalDevice.mService.getHdmiCecNetwork().getCecDeviceInfo(i);
        if (cecDeviceInfo != null) {
            return cecDeviceInfo.getCecVersion();
        }
        return 5;
    }

    public static int getTargetDevicePowerStatus(HdmiCecLocalDevice hdmiCecLocalDevice, int i, int i2) {
        HdmiDeviceInfo cecDeviceInfo = hdmiCecLocalDevice.mService.getHdmiCecNetwork().getCecDeviceInfo(i);
        return cecDeviceInfo != null ? cecDeviceInfo.getDevicePowerStatus() : i2;
    }
}
