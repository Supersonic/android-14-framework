package com.android.server.hdmi;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public final class SystemAudioAutoInitiationAction extends HdmiCecFeatureAction {
    @VisibleForTesting
    static final int RETRIES_ON_TIMEOUT = 1;
    public final int mAvrAddress;
    public int mRetriesOnTimeOut;

    public SystemAudioAutoInitiationAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        super(hdmiCecLocalDevice);
        this.mRetriesOnTimeOut = 1;
        this.mAvrAddress = i;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mState = 1;
        addTimer(1, 2000);
        sendGiveSystemAudioModeStatus();
        return true;
    }

    public final void sendGiveSystemAudioModeStatus() {
        sendCommand(HdmiCecMessageBuilder.buildGiveSystemAudioModeStatus(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SystemAudioAutoInitiationAction.1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 0) {
                    SystemAudioAutoInitiationAction.this.handleSystemAudioModeStatusTimeout();
                }
            }
        });
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && this.mAvrAddress == hdmiCecMessage.getSource() && hdmiCecMessage.getOpcode() == 126) {
            handleSystemAudioModeStatusMessage(HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage));
            return true;
        }
        return false;
    }

    public final void handleSystemAudioModeStatusMessage(boolean z) {
        if (!canChangeSystemAudio()) {
            HdmiLogger.debug("Cannot change system audio mode in auto initiation action.", new Object[0]);
            finish();
            return;
        }
        boolean isSystemAudioControlFeatureEnabled = m52tv().isSystemAudioControlFeatureEnabled();
        if (z != isSystemAudioControlFeatureEnabled) {
            addAndStartAction(new SystemAudioActionFromTv(m52tv(), this.mAvrAddress, isSystemAudioControlFeatureEnabled, null));
        } else {
            m52tv().setSystemAudioMode(isSystemAudioControlFeatureEnabled);
        }
        finish();
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == i && i2 == 1) {
            int i3 = this.mRetriesOnTimeOut;
            if (i3 > 0) {
                this.mRetriesOnTimeOut = i3 - 1;
                addTimer(i2, 2000);
                sendGiveSystemAudioModeStatus();
                return;
            }
            handleSystemAudioModeStatusTimeout();
        }
    }

    public final void handleSystemAudioModeStatusTimeout() {
        if (!canChangeSystemAudio()) {
            HdmiLogger.debug("Cannot change system audio mode in auto initiation action.", new Object[0]);
            finish();
            return;
        }
        addAndStartAction(new SystemAudioActionFromTv(m52tv(), this.mAvrAddress, m52tv().isSystemAudioControlFeatureEnabled(), null));
        finish();
    }

    public final boolean canChangeSystemAudio() {
        return (m52tv().hasAction(SystemAudioActionFromTv.class) || m52tv().hasAction(SystemAudioActionFromAvr.class)) ? false : true;
    }
}
