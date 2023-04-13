package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.net.INetd;
import android.sysprop.HdmiProperties;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.hdmi.HdmiCecLocalDevice;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class HdmiCecLocalDeviceSource extends HdmiCecLocalDevice {
    public boolean mIsSwitchDevice;
    @GuardedBy({"mLock"})
    public int mLocalActivePort;
    @GuardedBy({"mLock"})
    public boolean mRoutingControlFeatureEnabled;
    @GuardedBy({"mLock"})
    public int mRoutingPort;

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int getRcProfile() {
        return 1;
    }

    public void handleRoutingChangeAndInformation(int i, HdmiCecMessage hdmiCecMessage) {
    }

    public void onActiveSourceLost() {
    }

    public void switchInputOnReceivingNewActivePath(int i) {
    }

    public void updateDevicePowerStatus(int i, int i2) {
    }

    public HdmiCecLocalDeviceSource(HdmiControlService hdmiControlService, int i) {
        super(hdmiControlService, i);
        this.mIsSwitchDevice = ((Boolean) HdmiProperties.is_switch().orElse(Boolean.FALSE)).booleanValue();
        this.mRoutingPort = 0;
        this.mLocalActivePort = 0;
    }

    public void queryDisplayStatus(IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        List actions = getActions(DevicePowerStatusAction.class);
        if (!actions.isEmpty()) {
            Slog.i("HdmiCecLocalDeviceSource", "queryDisplayStatus already in progress");
            ((DevicePowerStatusAction) actions.get(0)).addCallback(iHdmiControlCallback);
            return;
        }
        DevicePowerStatusAction create = DevicePowerStatusAction.create(this, 0, iHdmiControlCallback);
        if (create == null) {
            Slog.w("HdmiCecLocalDeviceSource", "Cannot initiate queryDisplayStatus");
            invokeCallback(iHdmiControlCallback, -1);
            return;
        }
        addAndStartAction(create);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void onHotplug(int i, boolean z) {
        assertRunOnServiceThread();
        if (this.mService.getPortInfo(i).getType() == 1) {
            this.mCecMessageCache.flushAll();
        }
        if (z) {
            this.mService.wakeUp();
        }
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void sendStandby(int i) {
        assertRunOnServiceThread();
        String stringValue = this.mService.getHdmiCecConfig().getStringValue("power_control_mode");
        if (stringValue.equals(INetd.IF_FLAG_BROADCAST)) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 15));
            return;
        }
        this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 0));
        if (stringValue.equals("to_tv_and_audio_system")) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(getDeviceInfo().getLogicalAddress(), 5));
        }
    }

    public void oneTouchPlay(IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        List actions = getActions(OneTouchPlayAction.class);
        if (!actions.isEmpty()) {
            Slog.i("HdmiCecLocalDeviceSource", "oneTouchPlay already in progress");
            ((OneTouchPlayAction) actions.get(0)).addCallback(iHdmiControlCallback);
            return;
        }
        OneTouchPlayAction create = OneTouchPlayAction.create(this, 0, iHdmiControlCallback);
        if (create == null) {
            Slog.w("HdmiCecLocalDeviceSource", "Cannot initiate oneTouchPlay");
            invokeCallback(iHdmiControlCallback, 5);
            return;
        }
        addAndStartAction(create);
    }

    public void toggleAndFollowTvPower() {
        assertRunOnServiceThread();
        if (this.mService.getPowerManager().isInteractive()) {
            this.mService.pauseActiveMediaSessions();
        } else {
            this.mService.wakeUp();
        }
        this.mService.queryDisplayStatus(new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceSource.1
            public void onComplete(int i) {
                if (i == -1) {
                    Slog.i("HdmiCecLocalDeviceSource", "TV power toggle: TV power status unknown");
                    HdmiCecLocalDeviceSource.this.sendUserControlPressedAndReleased(0, 107);
                } else if (i == 0 || i == 2) {
                    Slog.i("HdmiCecLocalDeviceSource", "TV power toggle: turning off TV");
                    HdmiCecLocalDeviceSource.this.sendStandby(0);
                    HdmiCecLocalDeviceSource.this.mService.standby();
                } else if (i == 1 || i == 3) {
                    Slog.i("HdmiCecLocalDeviceSource", "TV power toggle: turning on TV");
                    HdmiCecLocalDeviceSource.this.oneTouchPlay(new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiCecLocalDeviceSource.1.1
                        public void onComplete(int i2) {
                            if (i2 != 0) {
                                Slog.w("HdmiCecLocalDeviceSource", "Failed to complete One Touch Play. result=" + i2);
                                HdmiCecLocalDeviceSource.this.sendUserControlPressedAndReleased(0, 107);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void setActiveSource(int i, int i2, String str) {
        boolean isActiveSource = isActiveSource();
        super.setActiveSource(i, i2, str);
        if (!isActiveSource || isActiveSource()) {
            return;
        }
        onActiveSourceLost();
    }

    public void setActiveSource(int i, String str) {
        assertRunOnServiceThread();
        setActiveSource(HdmiCecLocalDevice.ActiveSource.m51of(-1, i), str);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleActiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int source = hdmiCecMessage.getSource();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        HdmiCecLocalDevice.ActiveSource m51of = HdmiCecLocalDevice.ActiveSource.m51of(source, twoBytesToInt);
        if (!getActiveSource().equals(m51of)) {
            setActiveSource(m51of, "HdmiCecLocalDeviceSource#handleActiveSource()");
        }
        updateDevicePowerStatus(source, 0);
        if (isRoutingControlFeatureEnabled()) {
            switchInputOnReceivingNewActivePath(twoBytesToInt);
            return -1;
        }
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRequestActiveSource(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        maySendActiveSource(hdmiCecMessage.getSource());
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleSetStreamPath(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        if (twoBytesToInt == this.mService.getPhysicalAddress() && this.mService.isPlaybackDevice()) {
            setAndBroadcastActiveSource(hdmiCecMessage, twoBytesToInt, "HdmiCecLocalDeviceSource#handleSetStreamPath()");
        } else if (twoBytesToInt != this.mService.getPhysicalAddress() || !isActiveSource()) {
            setActiveSource(twoBytesToInt, "HdmiCecLocalDeviceSource#handleSetStreamPath()");
        }
        switchInputOnReceivingNewActivePath(twoBytesToInt);
        return -1;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRoutingChange(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams(), 2);
        if (twoBytesToInt != this.mService.getPhysicalAddress() || !isActiveSource()) {
            setActiveSource(twoBytesToInt, "HdmiCecLocalDeviceSource#handleRoutingChange()");
        }
        if (isRoutingControlFeatureEnabled()) {
            handleRoutingChangeAndInformation(twoBytesToInt, hdmiCecMessage);
            return -1;
        }
        return 4;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public int handleRoutingInformation(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int twoBytesToInt = HdmiUtils.twoBytesToInt(hdmiCecMessage.getParams());
        if (twoBytesToInt != this.mService.getPhysicalAddress() || !isActiveSource()) {
            setActiveSource(twoBytesToInt, "HdmiCecLocalDeviceSource#handleRoutingInformation()");
        }
        if (isRoutingControlFeatureEnabled()) {
            handleRoutingChangeAndInformation(twoBytesToInt, hdmiCecMessage);
            return -1;
        }
        return 4;
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public void disableDevice(boolean z, HdmiCecLocalDevice.PendingActionClearedCallback pendingActionClearedCallback) {
        removeAction(OneTouchPlayAction.class);
        removeAction(DevicePowerStatusAction.class);
        removeAction(AbsoluteVolumeAudioStatusAction.class);
        super.disableDevice(z, pendingActionClearedCallback);
    }

    @Override // com.android.server.hdmi.HdmiCecLocalDevice
    public List<Integer> getRcFeatures() {
        ArrayList arrayList = new ArrayList();
        HdmiCecConfig hdmiCecConfig = this.mService.getHdmiCecConfig();
        if (hdmiCecConfig.getIntValue("rc_profile_source_handles_root_menu") == 1) {
            arrayList.add(4);
        }
        if (hdmiCecConfig.getIntValue("rc_profile_source_handles_setup_menu") == 1) {
            arrayList.add(3);
        }
        if (hdmiCecConfig.getIntValue("rc_profile_source_handles_contents_menu") == 1) {
            arrayList.add(2);
        }
        if (hdmiCecConfig.getIntValue("rc_profile_source_handles_top_menu") == 1) {
            arrayList.add(1);
        }
        if (hdmiCecConfig.getIntValue("rc_profile_source_handles_media_context_sensitive_menu") == 1) {
            arrayList.add(0);
        }
        return arrayList;
    }

    public void setAndBroadcastActiveSource(HdmiCecMessage hdmiCecMessage, int i, String str) {
        this.mService.setAndBroadcastActiveSource(i, getDeviceInfo().getDeviceType(), hdmiCecMessage.getSource(), str);
    }

    public boolean isActiveSource() {
        if (getDeviceInfo() == null) {
            return false;
        }
        return getActiveSource().equals(getDeviceInfo().getLogicalAddress(), getDeviceInfo().getPhysicalAddress());
    }

    public void wakeUpIfActiveSource() {
        if (isActiveSource()) {
            this.mService.wakeUp();
        }
    }

    public void maySendActiveSource(int i) {
        if (isActiveSource()) {
            addAndStartAction(new ActiveSourceAction(this, i));
        }
    }

    @VisibleForTesting
    public void setRoutingPort(int i) {
        synchronized (this.mLock) {
            this.mRoutingPort = i;
        }
    }

    public int getRoutingPort() {
        int i;
        synchronized (this.mLock) {
            i = this.mRoutingPort;
        }
        return i;
    }

    public int getLocalActivePort() {
        int i;
        synchronized (this.mLock) {
            i = this.mLocalActivePort;
        }
        return i;
    }

    public void setLocalActivePort(int i) {
        synchronized (this.mLock) {
            this.mLocalActivePort = i;
        }
    }

    public boolean isRoutingControlFeatureEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mRoutingControlFeatureEnabled;
        }
        return z;
    }
}
