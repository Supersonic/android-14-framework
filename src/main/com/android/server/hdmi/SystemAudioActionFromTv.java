package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
/* loaded from: classes.dex */
public final class SystemAudioActionFromTv extends SystemAudioAction {
    public SystemAudioActionFromTv(HdmiCecLocalDevice hdmiCecLocalDevice, int i, boolean z, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, i, z, iHdmiControlCallback);
        HdmiUtils.verifyAddressType(getSourceAddress(), 0);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        removeSystemAudioActionInProgress();
        sendSystemAudioModeRequest();
        return true;
    }
}
