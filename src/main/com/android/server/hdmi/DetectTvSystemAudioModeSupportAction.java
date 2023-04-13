package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public class DetectTvSystemAudioModeSupportAction extends HdmiCecFeatureAction {
    public HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback mCallback;
    public int mSendSetSystemAudioModeRetryCount;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public DetectTvSystemAudioModeSupportAction(HdmiCecLocalDevice hdmiCecLocalDevice, HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback tvSystemAudioModeSupportedCallback) {
        super(hdmiCecLocalDevice);
        this.mSendSetSystemAudioModeRetryCount = 0;
        this.mCallback = tvSystemAudioModeSupportedCallback;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mState = 1;
        addTimer(1, 2000);
        sendSetSystemAudioMode();
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getOpcode() == 0 && this.mState == 1 && HdmiUtils.getAbortFeatureOpcode(hdmiCecMessage) == 114) {
            if (HdmiUtils.getAbortReason(hdmiCecMessage) == 1) {
                this.mActionTimer.clearTimerMessage();
                this.mState = 2;
                addTimer(2, 300);
            } else {
                finishAction(false);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 != i) {
            return;
        }
        if (i2 == 1) {
            finishAction(true);
        } else if (i2 != 2) {
        } else {
            int i3 = this.mSendSetSystemAudioModeRetryCount + 1;
            this.mSendSetSystemAudioModeRetryCount = i3;
            if (i3 < 5) {
                this.mState = 1;
                addTimer(1, 2000);
                sendSetSystemAudioMode();
                return;
            }
            finishAction(false);
        }
    }

    public void sendSetSystemAudioMode() {
        sendCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getSourceAddress(), 0, true), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.DetectTvSystemAudioModeSupportAction$$ExternalSyntheticLambda0
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public final void onSendCompleted(int i) {
                DetectTvSystemAudioModeSupportAction.this.lambda$sendSetSystemAudioMode$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSetSystemAudioMode$0(int i) {
        if (i != 0) {
            finishAction(false);
        }
    }

    public final void finishAction(boolean z) {
        this.mCallback.onResult(z);
        audioSystem().setTvSystemAudioModeSupport(z);
        finish();
    }
}
