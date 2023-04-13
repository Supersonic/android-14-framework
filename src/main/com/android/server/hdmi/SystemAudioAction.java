package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import com.android.server.hdmi.HdmiControlService;
import java.util.List;
/* loaded from: classes.dex */
public abstract class SystemAudioAction extends HdmiCecFeatureAction {
    public final int mAvrLogicalAddress;
    public int mSendRetryCount;
    public boolean mTargetAudioStatus;

    public SystemAudioAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, boolean z, IHdmiControlCallback iHdmiControlCallback) {
        super(hdmiCecLocalDevice, iHdmiControlCallback);
        this.mSendRetryCount = 0;
        HdmiUtils.verifyAddressType(i, 5);
        this.mAvrLogicalAddress = i;
        this.mTargetAudioStatus = z;
    }

    public void sendSystemAudioModeRequest() {
        List actions = getActions(RoutingControlAction.class);
        if (!actions.isEmpty()) {
            this.mState = 1;
            ((RoutingControlAction) actions.get(0)).addOnFinishedCallback(this, new Runnable() { // from class: com.android.server.hdmi.SystemAudioAction.1
                @Override // java.lang.Runnable
                public void run() {
                    SystemAudioAction.this.sendSystemAudioModeRequestInternal();
                }
            });
            return;
        }
        sendSystemAudioModeRequestInternal();
    }

    public final void sendSystemAudioModeRequestInternal() {
        sendCommand(HdmiCecMessageBuilder.buildSystemAudioModeRequest(getSourceAddress(), this.mAvrLogicalAddress, getSystemAudioModeRequestParam(), this.mTargetAudioStatus), new HdmiControlService.SendMessageCallback() { // from class: com.android.server.hdmi.SystemAudioAction.2
            @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
            public void onSendCompleted(int i) {
                if (i != 0) {
                    HdmiLogger.debug("Failed to send <System Audio Mode Request>:" + i, new Object[0]);
                    SystemAudioAction.this.setSystemAudioMode(false);
                    SystemAudioAction.this.finishWithCallback(7);
                }
            }
        });
        this.mState = 2;
        addTimer(2, this.mTargetAudioStatus ? 5000 : 2000);
    }

    public final int getSystemAudioModeRequestParam() {
        if (m52tv().getActiveSource().isValid()) {
            return m52tv().getActiveSource().physicalAddress;
        }
        int activePath = m52tv().getActivePath();
        if (activePath != 65535) {
            return activePath;
        }
        return 0;
    }

    public final void handleSendSystemAudioModeRequestTimeout() {
        if (this.mTargetAudioStatus) {
            int i = this.mSendRetryCount;
            this.mSendRetryCount = i + 1;
            if (i < 2) {
                sendSystemAudioModeRequest();
                return;
            }
        }
        HdmiLogger.debug("[T]:wait for <Set System Audio Mode>.", new Object[0]);
        setSystemAudioMode(false);
        finishWithCallback(1);
    }

    public void setSystemAudioMode(boolean z) {
        m52tv().setSystemAudioMode(z);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public final boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getSource() == this.mAvrLogicalAddress && this.mState == 2) {
            if (hdmiCecMessage.getOpcode() == 0 && (hdmiCecMessage.getParams()[0] & 255) == 112) {
                HdmiLogger.debug("Failed to start system audio mode request.", new Object[0]);
                setSystemAudioMode(false);
                finishWithCallback(5);
                return true;
            }
            if (hdmiCecMessage.getOpcode() == 114 && HdmiUtils.checkCommandSource(hdmiCecMessage, this.mAvrLogicalAddress, "SystemAudioAction")) {
                boolean parseCommandParamSystemAudioStatus = HdmiUtils.parseCommandParamSystemAudioStatus(hdmiCecMessage);
                if (parseCommandParamSystemAudioStatus == this.mTargetAudioStatus) {
                    setSystemAudioMode(parseCommandParamSystemAudioStatus);
                    finish();
                    return true;
                }
                HdmiLogger.debug("Unexpected system audio mode request:" + parseCommandParamSystemAudioStatus, new Object[0]);
                finishWithCallback(5);
            }
            return false;
        }
        return false;
    }

    public void removeSystemAudioActionInProgress() {
        removeActionExcept(SystemAudioActionFromTv.class, this);
        removeActionExcept(SystemAudioActionFromAvr.class, this);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public final void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == i && i2 == 2) {
            handleSendSystemAudioModeRequestTimeout();
        }
    }
}
