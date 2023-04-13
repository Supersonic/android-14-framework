package com.android.server.hdmi;

import com.android.internal.util.FrameworkStatsLog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class CecMessageBuffer {
    public List<HdmiCecMessage> mBuffer = new ArrayList();
    public HdmiControlService mHdmiControlService;

    public CecMessageBuffer(HdmiControlService hdmiControlService) {
        this.mHdmiControlService = hdmiControlService;
    }

    public boolean bufferMessage(HdmiCecMessage hdmiCecMessage) {
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode == 4 || opcode == 13) {
            bufferImageOrTextViewOn(hdmiCecMessage);
            return true;
        } else if (opcode == 112) {
            bufferSystemAudioModeRequest(hdmiCecMessage);
            return true;
        } else if (opcode == 128) {
            bufferRoutingChange(hdmiCecMessage);
            return true;
        } else if (opcode == 130) {
            bufferActiveSource(hdmiCecMessage);
            return true;
        } else if (opcode != 134) {
            return false;
        } else {
            bufferSetStreamPath(hdmiCecMessage);
            return true;
        }
    }

    public void processMessages() {
        for (final HdmiCecMessage hdmiCecMessage : this.mBuffer) {
            this.mHdmiControlService.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.CecMessageBuffer.1
                @Override // java.lang.Runnable
                public void run() {
                    CecMessageBuffer.this.mHdmiControlService.handleCecCommand(hdmiCecMessage);
                }
            });
        }
        this.mBuffer.clear();
    }

    public final void bufferActiveSource(HdmiCecMessage hdmiCecMessage) {
        if (replaceMessageIfBuffered(hdmiCecMessage, 130)) {
            return;
        }
        this.mBuffer.add(hdmiCecMessage);
    }

    public final void bufferImageOrTextViewOn(HdmiCecMessage hdmiCecMessage) {
        if (replaceMessageIfBuffered(hdmiCecMessage, 4) || replaceMessageIfBuffered(hdmiCecMessage, 13)) {
            return;
        }
        this.mBuffer.add(hdmiCecMessage);
    }

    public final void bufferSystemAudioModeRequest(HdmiCecMessage hdmiCecMessage) {
        if (replaceMessageIfBuffered(hdmiCecMessage, 112)) {
            return;
        }
        this.mBuffer.add(hdmiCecMessage);
    }

    public final void bufferRoutingChange(HdmiCecMessage hdmiCecMessage) {
        if (replaceMessageIfBuffered(hdmiCecMessage, 128)) {
            return;
        }
        this.mBuffer.add(hdmiCecMessage);
    }

    public final void bufferSetStreamPath(HdmiCecMessage hdmiCecMessage) {
        if (replaceMessageIfBuffered(hdmiCecMessage, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_TIME_ZONE)) {
            return;
        }
        this.mBuffer.add(hdmiCecMessage);
    }

    public List<HdmiCecMessage> getBuffer() {
        return new ArrayList(this.mBuffer);
    }

    public final boolean replaceMessageIfBuffered(HdmiCecMessage hdmiCecMessage, int i) {
        for (int i2 = 0; i2 < this.mBuffer.size(); i2++) {
            if (this.mBuffer.get(i2).getOpcode() == i) {
                this.mBuffer.set(i2, hdmiCecMessage);
                return true;
            }
        }
        return false;
    }
}
