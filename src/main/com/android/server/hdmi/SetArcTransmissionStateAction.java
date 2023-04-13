package com.android.server.hdmi;

import android.util.Slog;
import com.android.server.hdmi.HdmiControlService;
import com.android.server.hdmi.RequestSadAction;
import java.util.List;
/* loaded from: classes.dex */
public final class SetArcTransmissionStateAction extends HdmiCecFeatureAction {
    public final int mAvrAddress;
    public final boolean mEnabled;

    public SetArcTransmissionStateAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, boolean z) {
        super(hdmiCecLocalDevice);
        HdmiUtils.verifyAddressType(getSourceAddress(), 0);
        HdmiUtils.verifyAddressType(i, 5);
        this.mAvrAddress = i;
        this.mEnabled = z;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        if (this.mEnabled) {
            addAndStartAction(new RequestSadAction(localDevice(), 5, new RequestSadAction.RequestSadCallback() { // from class: com.android.server.hdmi.SetArcTransmissionStateAction.1
                @Override // com.android.server.hdmi.RequestSadAction.RequestSadCallback
                public void onRequestSadDone(List<byte[]> list) {
                    Slog.i("SetArcTransmissionStateAction", "Enabling ARC");
                    SetArcTransmissionStateAction.this.m52tv().enableArc(list);
                    SetArcTransmissionStateAction setArcTransmissionStateAction = SetArcTransmissionStateAction.this;
                    setArcTransmissionStateAction.mState = 1;
                    setArcTransmissionStateAction.addTimer(1, 2000);
                    SetArcTransmissionStateAction.this.sendReportArcInitiated();
                }
            }));
            return true;
        }
        disableArc();
        finish();
        return true;
    }

    public final void sendReportArcInitiated() {
        sendCommand(HdmiCecMessageBuilder.buildReportArcInitiated(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SetArcTransmissionStateAction.2
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 1) {
                    return;
                }
                SetArcTransmissionStateAction.this.disableArc();
                HdmiLogger.debug("Failed to send <Report Arc Initiated>.", new Object[0]);
                SetArcTransmissionStateAction.this.finish();
            }
        });
    }

    public final void disableArc() {
        Slog.i("SetArcTransmissionStateAction", "Disabling ARC");
        m52tv().disableArc();
        sendCommand(HdmiCecMessageBuilder.buildReportArcTerminated(getSourceAddress(), this.mAvrAddress));
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && hdmiCecMessage.getOpcode() == 0 && (hdmiCecMessage.getParams()[0] & 255) == 193) {
            HdmiLogger.debug("Feature aborted for <Report Arc Initiated>", new Object[0]);
            disableArc();
            finish();
            return true;
        }
        return false;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == i && i2 == 1) {
            finish();
        }
    }
}
