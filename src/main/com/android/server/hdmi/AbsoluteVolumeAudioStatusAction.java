package com.android.server.hdmi;
/* loaded from: classes.dex */
public final class AbsoluteVolumeAudioStatusAction extends HdmiCecFeatureAction {
    public int mInitialAudioStatusRetriesLeft;
    public AudioStatus mLastAudioStatus;
    public final int mTargetAddress;

    public AbsoluteVolumeAudioStatusAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i) {
        super(hdmiCecLocalDevice);
        this.mInitialAudioStatusRetriesLeft = 2;
        this.mTargetAddress = i;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        this.mState = 1;
        sendGiveAudioStatus();
        return true;
    }

    public void updateVolume(int i) {
        this.mLastAudioStatus = new AudioStatus(i, this.mLastAudioStatus.getMute());
    }

    public final void sendGiveAudioStatus() {
        addTimer(this.mState, 2000);
        sendCommand(HdmiCecMessageBuilder.buildGiveAudioStatus(getSourceAddress(), this.mTargetAddress));
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        if (hdmiCecMessage.getOpcode() != 122) {
            return false;
        }
        return handleReportAudioStatus(hdmiCecMessage);
    }

    public final boolean handleReportAudioStatus(HdmiCecMessage hdmiCecMessage) {
        if (this.mTargetAddress != hdmiCecMessage.getSource() || hdmiCecMessage.getParams().length == 0) {
            return false;
        }
        AudioStatus audioStatus = new AudioStatus(HdmiUtils.getAudioStatusVolume(hdmiCecMessage), HdmiUtils.isAudioStatusMute(hdmiCecMessage));
        int i = this.mState;
        if (i == 1) {
            localDevice().getService().enableAbsoluteVolumeControl(audioStatus);
            this.mState = 2;
        } else if (i == 2) {
            if (audioStatus.getVolume() != this.mLastAudioStatus.getVolume()) {
                localDevice().getService().notifyAvcVolumeChange(audioStatus.getVolume());
            }
            if (audioStatus.getMute() != this.mLastAudioStatus.getMute()) {
                localDevice().getService().notifyAvcMuteChange(audioStatus.getMute());
            }
        }
        this.mLastAudioStatus = audioStatus;
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2;
        if (this.mState == i && (i2 = this.mInitialAudioStatusRetriesLeft) > 0) {
            this.mInitialAudioStatusRetriesLeft = i2 - 1;
            sendGiveAudioStatus();
        }
    }
}
