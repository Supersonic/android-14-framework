package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.hdmi.HdmiCecLocalDevice;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
public final class NewDeviceAction extends HdmiCecFeatureAction {
    public final int mDeviceLogicalAddress;
    public final int mDevicePhysicalAddress;
    public final int mDeviceType;
    public String mDisplayName;
    public int mTimeoutRetry;
    public int mVendorId;

    public NewDeviceAction(HdmiCecLocalDevice hdmiCecLocalDevice, int i, int i2, int i3) {
        super(hdmiCecLocalDevice);
        this.mDeviceLogicalAddress = i;
        this.mDevicePhysicalAddress = i2;
        this.mDeviceType = i3;
        this.mVendorId = 16777215;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        requestOsdName(true);
        return true;
    }

    public final void requestOsdName(boolean z) {
        if (z) {
            this.mTimeoutRetry = 0;
        }
        this.mState = 1;
        if (mayProcessCommandIfCached(this.mDeviceLogicalAddress, 71)) {
            return;
        }
        sendCommand(HdmiCecMessageBuilder.buildGiveOsdNameCommand(getSourceAddress(), this.mDeviceLogicalAddress));
        addTimer(this.mState, 2000);
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        int opcode = hdmiCecMessage.getOpcode();
        int source = hdmiCecMessage.getSource();
        byte[] params = hdmiCecMessage.getParams();
        if (this.mDeviceLogicalAddress != source) {
            return false;
        }
        int i = this.mState;
        if (i == 1) {
            if (opcode == 71) {
                try {
                    this.mDisplayName = new String(params, "US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    Slog.e("NewDeviceAction", "Failed to get OSD name: " + e.getMessage());
                }
                requestVendorId(true);
                return true;
            } else if (opcode == 0 && (params[0] & 255) == 70) {
                requestVendorId(true);
                return true;
            }
        } else if (i == 2) {
            if (opcode == 135) {
                this.mVendorId = HdmiUtils.threeBytesToInt(params);
                addDeviceInfo();
                finish();
                return true;
            } else if (opcode == 0 && (params[0] & 255) == 140) {
                addDeviceInfo();
                finish();
                return true;
            }
        }
        return false;
    }

    public final boolean mayProcessCommandIfCached(int i, int i2) {
        HdmiCecMessage message = getCecMessageCache().getMessage(i, i2);
        if (message != null) {
            return processCommand(message);
        }
        return false;
    }

    public final void requestVendorId(boolean z) {
        if (z) {
            this.mTimeoutRetry = 0;
        }
        this.mState = 2;
        if (mayProcessCommandIfCached(this.mDeviceLogicalAddress, FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED)) {
            return;
        }
        sendCommand(HdmiCecMessageBuilder.buildGiveDeviceVendorIdCommand(getSourceAddress(), this.mDeviceLogicalAddress));
        addTimer(this.mState, 2000);
    }

    public final void addDeviceInfo() {
        if (!localDevice().mService.getHdmiCecNetwork().isInDeviceList(this.mDeviceLogicalAddress, this.mDevicePhysicalAddress)) {
            Slog.w("NewDeviceAction", String.format("Device not found (%02x, %04x)", Integer.valueOf(this.mDeviceLogicalAddress), Integer.valueOf(this.mDevicePhysicalAddress)));
            return;
        }
        if (this.mDisplayName == null) {
            this.mDisplayName = HdmiUtils.getDefaultDeviceName(this.mDeviceLogicalAddress);
        }
        HdmiDeviceInfo build = HdmiDeviceInfo.cecDeviceBuilder().setLogicalAddress(this.mDeviceLogicalAddress).setPhysicalAddress(this.mDevicePhysicalAddress).setPortId(m52tv().getPortId(this.mDevicePhysicalAddress)).setDeviceType(this.mDeviceType).setVendorId(this.mVendorId).setDisplayName(this.mDisplayName).build();
        localDevice().mService.getHdmiCecNetwork().addCecDevice(build);
        m52tv().processDelayedMessages(this.mDeviceLogicalAddress);
        if (HdmiUtils.isEligibleAddressForDevice(5, this.mDeviceLogicalAddress)) {
            m52tv().onNewAvrAdded(build);
        }
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == 0 || i2 != i) {
            return;
        }
        if (i == 1) {
            int i3 = this.mTimeoutRetry + 1;
            this.mTimeoutRetry = i3;
            if (i3 < 5) {
                requestOsdName(false);
            } else {
                requestVendorId(true);
            }
        } else if (i == 2) {
            int i4 = this.mTimeoutRetry + 1;
            this.mTimeoutRetry = i4;
            if (i4 < 5) {
                requestVendorId(false);
                return;
            }
            addDeviceInfo();
            finish();
        }
    }

    public boolean isActionOf(HdmiCecLocalDevice.ActiveSource activeSource) {
        return this.mDeviceLogicalAddress == activeSource.logicalAddress && this.mDevicePhysicalAddress == activeSource.physicalAddress;
    }
}
