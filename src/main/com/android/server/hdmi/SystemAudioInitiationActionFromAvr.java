package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public class SystemAudioInitiationActionFromAvr extends HdmiCecFeatureAction {
    @VisibleForTesting
    static final int MAX_RETRY_COUNT = 5;
    public int mSendRequestActiveSourceRetryCount;
    public int mSendSetSystemAudioModeRetryCount;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public SystemAudioInitiationActionFromAvr(HdmiCecLocalDevice hdmiCecLocalDevice) {
        super(hdmiCecLocalDevice);
        this.mSendRequestActiveSourceRetryCount = 0;
        this.mSendSetSystemAudioModeRetryCount = 0;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        if (audioSystem().getActiveSource().physicalAddress == 65535) {
            this.mState = 1;
            addTimer(1, 2000);
            sendRequestActiveSource();
        } else {
            this.mState = 2;
            queryTvSystemAudioModeSupport();
        }
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getOpcode() == 130 && this.mState == 1) {
            this.mActionTimer.clearTimerMessage();
            audioSystem().handleActiveSource(hdmiCecMessage);
            this.mState = 2;
            queryTvSystemAudioModeSupport();
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == i && i2 == 1) {
            handleActiveSourceTimeout();
        }
    }

    public void sendRequestActiveSource() {
        sendCommand(HdmiCecMessageBuilder.buildRequestActiveSource(getSourceAddress()), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SystemAudioInitiationActionFromAvr$$ExternalSyntheticLambda1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public final void onSendCompleted(int i) {
                SystemAudioInitiationActionFromAvr.this.lambda$sendRequestActiveSource$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendRequestActiveSource$0(int i) {
        if (i != 0) {
            int i2 = this.mSendRequestActiveSourceRetryCount;
            if (i2 < 5) {
                this.mSendRequestActiveSourceRetryCount = i2 + 1;
                sendRequestActiveSource();
                return;
            }
            audioSystem().checkSupportAndSetSystemAudioMode(false);
            finish();
        }
    }

    public void sendSetSystemAudioMode(final boolean z, final int i) {
        sendCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getSourceAddress(), i, z), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SystemAudioInitiationActionFromAvr$$ExternalSyntheticLambda2
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public final void onSendCompleted(int i2) {
                SystemAudioInitiationActionFromAvr.this.lambda$sendSetSystemAudioMode$1(z, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSetSystemAudioMode$1(boolean z, int i, int i2) {
        if (i2 != 0) {
            int i3 = this.mSendSetSystemAudioModeRetryCount;
            if (i3 < 5) {
                this.mSendSetSystemAudioModeRetryCount = i3 + 1;
                sendSetSystemAudioMode(z, i);
                return;
            }
            audioSystem().checkSupportAndSetSystemAudioMode(false);
            finish();
        }
    }

    public final void handleActiveSourceTimeout() {
        HdmiLogger.debug("Cannot get active source.", new Object[0]);
        if (audioSystem().mService.isPlaybackDevice()) {
            audioSystem().mService.setAndBroadcastActiveSourceFromOneDeviceType(15, getSourcePath(), "SystemAudioInitiationActionFromAvr#handleActiveSourceTimeout()");
            this.mState = 2;
            queryTvSystemAudioModeSupport();
        } else {
            audioSystem().checkSupportAndSetSystemAudioMode(false);
        }
        finish();
    }

    public final void queryTvSystemAudioModeSupport() {
        audioSystem().queryTvSystemAudioModeSupport(new HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback() { // from class: com.android.server.hdmi.SystemAudioInitiationActionFromAvr$$ExternalSyntheticLambda0
            @Override // com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback
            public final void onResult(boolean z) {
                SystemAudioInitiationActionFromAvr.this.lambda$queryTvSystemAudioModeSupport$2(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$queryTvSystemAudioModeSupport$2(boolean z) {
        if (z) {
            if (audioSystem().checkSupportAndSetSystemAudioMode(true)) {
                sendSetSystemAudioMode(true, 15);
            }
            finish();
            return;
        }
        audioSystem().checkSupportAndSetSystemAudioMode(false);
        finish();
    }
}
