package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
/* loaded from: classes.dex */
public final class SystemAudioActionFromAvr extends SystemAudioAction {
    public SystemAudioActionFromAvr(HdmiCecLocalDevice hdmiCecLocalDevice, int i, boolean z, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, i, z, iHdmiControlCallback);
        HdmiUtils.verifyAddressType(getSourceAddress(), 0);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        removeSystemAudioActionInProgress();
        handleSystemAudioActionFromAvr();
        return true;
    }

    public final void handleSystemAudioActionFromAvr() {
        if (this.mTargetAudioStatus == m52tv().isSystemAudioActivated()) {
            finishWithCallback(0);
        } else if (m52tv().isProhibitMode()) {
            sendCommand(HdmiCecMessageBuilder.buildFeatureAbortCommand(getSourceAddress(), this.mAvrLogicalAddress, 114, 4));
            this.mTargetAudioStatus = false;
            sendSystemAudioModeRequest();
        } else {
            removeAction(SystemAudioAutoInitiationAction.class);
            if (this.mTargetAudioStatus) {
                setSystemAudioMode(true);
                finish();
                return;
            }
            setSystemAudioMode(false);
            finishWithCallback(0);
        }
    }
}
