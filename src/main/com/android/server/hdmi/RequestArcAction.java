package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
/* loaded from: classes.dex */
public abstract class RequestArcAction extends HdmiCecFeatureAction {
    public final int mAvrAddress;

    public RequestArcAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, iHdmiControlCallback);
        HdmiUtils.verifyAddressType(getSourceAddress(), 0);
        HdmiUtils.verifyAddressType(i, 5);
        this.mAvrAddress = i;
    }

    public RequestArcAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        this(hdmiCecLocalDevice, i, null);
    }

    public final void disableArcTransmission() {
        addAndStartAction(new SetArcTransmissionStateAction(localDevice(), this.mAvrAddress, false));
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public final void handleTimerEvent(int i) {
        if (this.mState == i && i == 1) {
            HdmiLogger.debug("[T] RequestArcAction.", new Object[0]);
            disableArcTransmission();
            finishWithCallback(1);
        }
    }
}
