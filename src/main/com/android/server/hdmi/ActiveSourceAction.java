package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
/* loaded from: classes.dex */
public class ActiveSourceAction extends HdmiCecFeatureAction {
    public final int mDestination;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public ActiveSourceAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        super(hdmiCecLocalDevice);
        this.mDestination = i;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mState = 1;
        int sourceAddress = getSourceAddress();
        int sourcePath = getSourcePath();
        sendCommand(HdmiCecMessageBuilder.buildActiveSource(sourceAddress, sourcePath));
        if (source().getType() == 4) {
            sendCommand(HdmiCecMessageBuilder.buildReportMenuStatus(sourceAddress, this.mDestination, 0));
        }
        source().setActiveSource(sourceAddress, sourcePath, "ActiveSourceAction");
        this.mState = 2;
        finish();
        return true;
    }
}
