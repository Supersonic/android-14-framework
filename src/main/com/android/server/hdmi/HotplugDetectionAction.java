package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.util.Slog;
import com.android.server.hdmi.HdmiControlService;
import java.util.BitSet;
import java.util.List;
/* loaded from: classes.dex */
public final class HotplugDetectionAction extends HdmiCecFeatureAction {
    public int mAvrStatusCount;
    public final boolean mIsTvDevice;
    public int mTimeoutCount;

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean processCommand(HdmiCecMessage hdmiCecMessage) {
        return false;
    }

    public HotplugDetectionAction(HdmiCecLocalDevice hdmiCecLocalDevice) {
        super(hdmiCecLocalDevice);
        this.mTimeoutCount = 0;
        this.mAvrStatusCount = 0;
        this.mIsTvDevice = localDevice().mService.isTvDevice();
    }

    public final int getPollingInterval() {
        return this.mIsTvDevice ? 5000 : 60000;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public boolean start() {
        Slog.v("HotPlugDetectionAction", "Hot-plug detection started.");
        this.mState = 1;
        this.mTimeoutCount = 0;
        addTimer(1, getPollingInterval());
        return true;
    }

    @Override // com.android.server.hdmi.HdmiCecFeatureAction
    public void handleTimerEvent(int i) {
        int i2 = this.mState;
        if (i2 == i && i2 == 1) {
            if (this.mIsTvDevice) {
                int i3 = (this.mTimeoutCount + 1) % 3;
                this.mTimeoutCount = i3;
                if (i3 == 0) {
                    pollAllDevices();
                } else if (m52tv().isSystemAudioActivated()) {
                    pollAudioSystem();
                }
                addTimer(this.mState, 5000);
                return;
            }
            pollAllDevices();
            addTimer(this.mState, 60000);
        }
    }

    public void pollAllDevicesNow() {
        this.mActionTimer.clearTimerMessage();
        this.mTimeoutCount = 0;
        this.mState = 1;
        pollAllDevices();
        addTimer(this.mState, getPollingInterval());
    }

    public final void pollAllDevices() {
        Slog.v("HotPlugDetectionAction", "Poll all devices.");
        pollDevices(new HdmiControlService.DevicePollingCallback() { // from class: com.android.server.hdmi.HotplugDetectionAction.1
            @Override // com.android.server.hdmi.HdmiControlService.DevicePollingCallback
            public void onPollingFinished(List<Integer> list) {
                HotplugDetectionAction.this.checkHotplug(list, false);
            }
        }, 65537, 1);
    }

    public final void pollAudioSystem() {
        Slog.v("HotPlugDetectionAction", "Poll audio system.");
        pollDevices(new HdmiControlService.DevicePollingCallback() { // from class: com.android.server.hdmi.HotplugDetectionAction.2
            @Override // com.android.server.hdmi.HdmiControlService.DevicePollingCallback
            public void onPollingFinished(List<Integer> list) {
                HotplugDetectionAction.this.checkHotplug(list, true);
            }
        }, 65538, 1);
    }

    public final void checkHotplug(List<Integer> list, boolean z) {
        HdmiDeviceInfo avrDeviceInfo;
        List<HdmiDeviceInfo> deviceInfoList = localDevice().mService.getHdmiCecNetwork().getDeviceInfoList(false);
        BitSet infoListToBitSet = infoListToBitSet(deviceInfoList, z, false);
        BitSet addressListToBitSet = addressListToBitSet(list);
        BitSet complement = complement(infoListToBitSet, addressListToBitSet);
        int i = -1;
        while (true) {
            i = complement.nextSetBit(i + 1);
            if (i == -1) {
                break;
            }
            if (this.mIsTvDevice && i == 5 && (avrDeviceInfo = m52tv().getAvrDeviceInfo()) != null && m52tv().isConnected(avrDeviceInfo.getPortId())) {
                this.mAvrStatusCount++;
                Slog.w("HotPlugDetectionAction", "Ack not returned from AVR. count: " + this.mAvrStatusCount);
                if (this.mAvrStatusCount < 3) {
                }
            }
            Slog.v("HotPlugDetectionAction", "Remove device by hot-plug detection:" + i);
            removeDevice(i);
        }
        if (!complement.get(5)) {
            this.mAvrStatusCount = 0;
        }
        BitSet complement2 = complement(addressListToBitSet, infoListToBitSet(deviceInfoList, z, true));
        int i2 = -1;
        while (true) {
            i2 = complement2.nextSetBit(i2 + 1);
            if (i2 == -1) {
                return;
            }
            Slog.v("HotPlugDetectionAction", "Add device by hot-plug detection:" + i2);
            addDevice(i2);
        }
    }

    public static BitSet infoListToBitSet(List<HdmiDeviceInfo> list, boolean z, boolean z2) {
        BitSet bitSet = new BitSet(15);
        for (HdmiDeviceInfo hdmiDeviceInfo : list) {
            boolean z3 = false;
            boolean z4 = !z || hdmiDeviceInfo.getDeviceType() == 5;
            z3 = (z2 && hdmiDeviceInfo.getPhysicalAddress() == 65535) ? true : true;
            if (z4 && z3) {
                bitSet.set(hdmiDeviceInfo.getLogicalAddress());
            }
        }
        return bitSet;
    }

    public static BitSet addressListToBitSet(List<Integer> list) {
        BitSet bitSet = new BitSet(15);
        for (Integer num : list) {
            bitSet.set(num.intValue());
        }
        return bitSet;
    }

    public static BitSet complement(BitSet bitSet, BitSet bitSet2) {
        BitSet bitSet3 = (BitSet) bitSet.clone();
        bitSet3.andNot(bitSet2);
        return bitSet3;
    }

    public final void addDevice(int i) {
        sendCommand(HdmiCecMessageBuilder.buildGivePhysicalAddress(getSourceAddress(), i));
    }

    public final void removeDevice(int i) {
        if (this.mIsTvDevice) {
            mayChangeRoutingPath(i);
            mayCancelOneTouchRecord(i);
            mayDisableSystemAudioAndARC(i);
        }
        mayCancelDeviceSelect(i);
        localDevice().mService.getHdmiCecNetwork().removeCecDevice(localDevice(), i);
    }

    public final void mayChangeRoutingPath(int i) {
        HdmiDeviceInfo cecDeviceInfo = localDevice().mService.getHdmiCecNetwork().getCecDeviceInfo(i);
        if (cecDeviceInfo != null) {
            m52tv().handleRemoveActiveRoutingPath(cecDeviceInfo.getPhysicalAddress());
        }
    }

    public final void mayCancelDeviceSelect(int i) {
        for (DeviceSelectActionFromTv deviceSelectActionFromTv : getActions(DeviceSelectActionFromTv.class)) {
            if (deviceSelectActionFromTv.getTargetAddress() == i) {
                removeAction(DeviceSelectActionFromTv.class);
            }
        }
        for (DeviceSelectActionFromPlayback deviceSelectActionFromPlayback : getActions(DeviceSelectActionFromPlayback.class)) {
            if (deviceSelectActionFromPlayback.getTargetAddress() == i) {
                removeAction(DeviceSelectActionFromTv.class);
            }
        }
    }

    public final void mayCancelOneTouchRecord(int i) {
        for (OneTouchRecordAction oneTouchRecordAction : getActions(OneTouchRecordAction.class)) {
            if (oneTouchRecordAction.getRecorderAddress() == i) {
                removeAction(oneTouchRecordAction);
            }
        }
    }

    public final void mayDisableSystemAudioAndARC(int i) {
        if (HdmiUtils.isEligibleAddressForDevice(5, i)) {
            m52tv().setSystemAudioMode(false);
            if (m52tv().isArcEstablished()) {
                m52tv().enableAudioReturnChannel(false);
                addAndStartAction(new RequestArcTerminationAction(localDevice(), i));
            }
        }
    }
}
