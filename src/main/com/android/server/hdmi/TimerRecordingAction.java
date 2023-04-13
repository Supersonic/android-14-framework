package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.util.Slog;
import com.android.server.hdmi.HdmiControlService;
import java.util.Arrays;
/* loaded from: classes.dex */
public class TimerRecordingAction extends HdmiCecFeatureAction {
    public final byte[] mRecordSource;
    public final int mRecorderAddress;
    public final int mSourceType;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public /* bridge */ /* synthetic */ void addCallback(IHdmiControlCallback iHdmiControlCallback) {
        super.addCallback(iHdmiControlCallback);
    }

    public TimerRecordingAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, int i2, byte[] bArr) {
        super(hdmiCecLocalDevice);
        this.mRecorderAddress = i;
        this.mSourceType = i2;
        this.mRecordSource = bArr;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        sendTimerMessage();
        return true;
    }

    public final void sendTimerMessage() {
        HdmiCecMessage buildSetDigitalTimer;
        int i = this.mSourceType;
        if (i == 1) {
            buildSetDigitalTimer = HdmiCecMessageBuilder.buildSetDigitalTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
        } else if (i == 2) {
            buildSetDigitalTimer = HdmiCecMessageBuilder.buildSetAnalogueTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
        } else if (i == 3) {
            buildSetDigitalTimer = HdmiCecMessageBuilder.buildSetExternalTimer(getSourceAddress(), this.mRecorderAddress, this.mRecordSource);
        } else {
            m52tv().announceTimerRecordingResult(this.mRecorderAddress, 2);
            finish();
            return;
        }
        sendCommand(buildSetDigitalTimer, new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.TimerRecordingAction.1
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i2) {
                if (i2 != 0) {
                    TimerRecordingAction.this.m52tv().announceTimerRecordingResult(TimerRecordingAction.this.mRecorderAddress, 1);
                    TimerRecordingAction.this.finish();
                    return;
                }
                TimerRecordingAction timerRecordingAction = TimerRecordingAction.this;
                timerRecordingAction.mState = 1;
                timerRecordingAction.addTimer(1, 120000);
            }
        });
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (this.mState == 1 && hdmiCecMessage.getSource() == this.mRecorderAddress) {
            int opcode = hdmiCecMessage.getOpcode();
            if (opcode != 0) {
                if (opcode != 53) {
                    return false;
                }
                return handleTimerStatus(hdmiCecMessage);
            }
            return handleFeatureAbort(hdmiCecMessage);
        }
        return false;
    }

    public final boolean handleTimerStatus(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        if (params.length == 1 || params.length == 3) {
            m52tv().announceTimerRecordingResult(this.mRecorderAddress, bytesToInt(params));
            Slog.i("TimerRecordingAction", "Received [Timer Status Data]:" + Arrays.toString(params));
        } else {
            Slog.w("TimerRecordingAction", "Invalid [Timer Status Data]:" + Arrays.toString(params));
        }
        finish();
        return true;
    }

    public final boolean handleFeatureAbort(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        int i = params[0] & 255;
        if (i == 52 || i == 151 || i == 162) {
            Slog.i("TimerRecordingAction", "[Feature Abort] for " + i + " reason:" + (params[1] & 255));
            m52tv().announceTimerRecordingResult(this.mRecorderAddress, 1);
            finish();
            return true;
        }
        return false;
    }

    public static int bytesToInt(byte[] bArr) {
        if (bArr.length > 4) {
            throw new IllegalArgumentException("Invalid data size:" + Arrays.toString(bArr));
        }
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            i |= (bArr[i2] & 255) << ((3 - i2) * 8);
        }
        return i;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        if (this.mState != i) {
            Slog.w("TimerRecordingAction", "Timeout in invalid state:[Expected:" + this.mState + ", Actual:" + i + "]");
            return;
        }
        m52tv().announceTimerRecordingResult(this.mRecorderAddress, 1);
        finish();
    }
}
