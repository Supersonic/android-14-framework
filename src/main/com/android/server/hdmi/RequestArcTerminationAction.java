package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public final class RequestArcTerminationAction extends RequestArcAction {
    public RequestArcTerminationAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        super(hdmiCecLocalDevice, i);
    }

    public RequestArcTerminationAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, i, iHdmiControlCallback);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mState = 1;
        addTimer(1, 2000);
        sendCommand(HdmiCecMessageBuilder.buildRequestArcTermination(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.RequestArcTerminationAction.1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 0) {
                    RequestArcTerminationAction.this.disableArcTransmission();
                    RequestArcTerminationAction.this.finishWithCallback(3);
                }
            }
        });
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && HdmiUtils.checkCommandSource(hdmiCecMessage, this.mAvrAddress, "RequestArcTerminationAction")) {
            int opcode = hdmiCecMessage.getOpcode();
            if (opcode != 0) {
                if (opcode != 197) {
                    return false;
                }
                finishWithCallback(0);
                return false;
            } else if ((hdmiCecMessage.getParams()[0] & 255) == 196) {
                disableArcTransmission();
                finishWithCallback(3);
                return true;
            }
        }
        return false;
    }
}
