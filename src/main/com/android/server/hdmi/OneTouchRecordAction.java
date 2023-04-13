package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.util.Slog;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public class OneTouchRecordAction extends HdmiCecFeatureAction {
    public final byte[] mRecordSource;
    public final int mRecorderAddress;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public OneTouchRecordAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, byte[] bArr) {
        super(hdmiCecLocalDevice);
        this.mRecorderAddress = i;
        this.mRecordSource = bArr;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        sendRecordOn();
        return true;
    }

    public final void sendRecordOn() {
        sendCommand(HdmiCecMessageBuilder.buildRecordOn(getSourceAddress(), this.mRecorderAddress, this.mRecordSource), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.OneTouchRecordAction.1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 0) {
                    OneTouchRecordAction.this.m52tv().announceOneTouchRecordResult(OneTouchRecordAction.this.mRecorderAddress, 49);
                    OneTouchRecordAction.this.finish();
                }
            }
        });
        this.mState = 1;
        addTimer(1, 120000);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && this.mRecorderAddress == hdmiCecMessage.getSource() && hdmiCecMessage.getOpcode() == 10) {
            return handleRecordStatus(hdmiCecMessage);
        }
        return false;
    }

    public final boolean handleRecordStatus(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getSource() != this.mRecorderAddress) {
            return false;
        }
        byte b = hdmiCecMessage.getParams()[0];
        m52tv().announceOneTouchRecordResult(this.mRecorderAddress, b);
        Slog.i("OneTouchRecordAction", "Got record status:" + ((int) b) + " from " + hdmiCecMessage.getSource());
        if (b == 1 || b == 2 || b == 3 || b == 4) {
            this.mState = 2;
            this.mActionTimer.clearTimerMessage();
        } else {
            finish();
        }
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        if (this.mState != i) {
            Slog.w("OneTouchRecordAction", "Timeout in invalid state:[Expected:" + this.mState + ", Actual:" + i + "]");
            return;
        }
        m52tv().announceOneTouchRecordResult(this.mRecorderAddress, 49);
        finish();
    }

    public int getRecorderAddress() {
        return this.mRecorderAddress;
    }
}
