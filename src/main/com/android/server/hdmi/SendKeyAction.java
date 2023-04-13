package com.android.server.hdmi;

import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.hdmi.HdmiControlService;
/* loaded from: classes.dex */
public final class SendKeyAction extends HdmiCecFeatureAction {
    public int mLastKeycode;
    public long mLastSendKeyTime;
    public final int mTargetAddress;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        return false;
    }

    public SendKeyAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, int i2) {
        super(hdmiCecLocalDevice);
        this.mTargetAddress = i;
        this.mLastKeycode = i2;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        sendKeyDown(this.mLastKeycode);
        this.mLastSendKeyTime = getCurrentTime();
        if (!HdmiCecKeycode.isRepeatableKey(this.mLastKeycode)) {
            sendKeyUp();
            finish();
            return true;
        }
        this.mState = 1;
        addTimer(1, FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        return true;
    }

    public final long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void processKeyEvent(int i, boolean z) {
        int i2 = this.mState;
        if (i2 != 1 && i2 != 2) {
            Slog.w("SendKeyAction", "Not in a valid state");
        } else if (z) {
            if (i != this.mLastKeycode) {
                sendKeyDown(i);
                this.mLastSendKeyTime = getCurrentTime();
                if (!HdmiCecKeycode.isRepeatableKey(i)) {
                    sendKeyUp();
                    finish();
                    return;
                }
            } else if (getCurrentTime() - this.mLastSendKeyTime >= 300) {
                sendKeyDown(i);
                this.mLastSendKeyTime = getCurrentTime();
            }
            this.mActionTimer.clearTimerMessage();
            addTimer(this.mState, 1000);
            this.mLastKeycode = i;
        } else if (i == this.mLastKeycode) {
            sendKeyUp();
            finish();
        }
    }

    public final void sendKeyDown(int i) {
        byte[] androidKeyToCecKey = HdmiCecKeycode.androidKeyToCecKey(i);
        if (androidKeyToCecKey == null) {
            return;
        }
        if (this.mTargetAddress == 5 && localDevice().getDeviceInfo().getLogicalAddress() != 0) {
            sendCommand(HdmiCecMessageBuilder.buildUserControlPressed(getSourceAddress(), this.mTargetAddress, androidKeyToCecKey), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SendKeyAction.1
                @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                public void onSendCompleted(int i2) {
                    if (i2 != 0) {
                        HdmiLogger.debug("AVR did not respond to <User Control Pressed>", new Object[0]);
                        SendKeyAction.this.localDevice().mService.setSystemAudioActivated(false);
                    }
                }
            });
        } else {
            sendCommand(HdmiCecMessageBuilder.buildUserControlPressed(getSourceAddress(), this.mTargetAddress, androidKeyToCecKey));
        }
    }

    public final void sendKeyUp() {
        if (HdmiCecKeycode.isVolumeKeycode(this.mLastKeycode) && localDevice().getService().isAbsoluteVolumeControlEnabled()) {
            sendCommand(HdmiCecMessageBuilder.buildUserControlReleased(getSourceAddress(), this.mTargetAddress), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SendKeyAction$$ExternalSyntheticLambda0
                @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                public final void onSendCompleted(int i) {
                    SendKeyAction.this.lambda$sendKeyUp$0(i);
                }
            });
        } else {
            sendCommand(HdmiCecMessageBuilder.buildUserControlReleased(getSourceAddress(), this.mTargetAddress));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendKeyUp$0(int i) {
        sendCommand(HdmiCecMessageBuilder.buildGiveAudioStatus(getSourceAddress(), localDevice().findAudioReceiverAddress()));
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 != 1) {
            if (i2 == 2) {
                sendKeyUp();
                finish();
                return;
            }
            Slog.w("SendKeyAction", "Not in a valid state");
            return;
        }
        this.mActionTimer.clearTimerMessage();
        this.mState = 2;
        sendKeyDown(this.mLastKeycode);
        this.mLastSendKeyTime = getCurrentTime();
        addTimer(this.mState, 1000);
    }
}
