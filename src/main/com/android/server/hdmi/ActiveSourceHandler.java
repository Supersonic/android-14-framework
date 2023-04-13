package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.hdmi.HdmiCecLocalDevice;
/* loaded from: classes.dex */
public final class ActiveSourceHandler {
    public final IHdmiControlCallback mCallback;
    public final HdmiControlService mService;
    public final HdmiCecLocalDeviceTv mSource;

    public static ActiveSourceHandler create(HdmiCecLocalDeviceTv hdmiCecLocalDeviceTv, IHdmiControlCallback iHdmiControlCallback) {
        if (hdmiCecLocalDeviceTv == null) {
            Slog.e("ActiveSourceHandler", "Wrong arguments");
            return null;
        }
        return new ActiveSourceHandler(hdmiCecLocalDeviceTv, iHdmiControlCallback);
    }

    public ActiveSourceHandler(HdmiCecLocalDeviceTv hdmiCecLocalDeviceTv, IHdmiControlCallback iHdmiControlCallback) {
        this.mSource = hdmiCecLocalDeviceTv;
        this.mService = hdmiCecLocalDeviceTv.getService();
        this.mCallback = iHdmiControlCallback;
    }

    public void process(HdmiCecLocalDevice.ActiveSource activeSource, int i) {
        HdmiCecLocalDeviceTv hdmiCecLocalDeviceTv = this.mSource;
        if (this.mService.getDeviceInfo(activeSource.logicalAddress) == null) {
            hdmiCecLocalDeviceTv.startNewDeviceAction(activeSource, i);
        }
        if (!hdmiCecLocalDeviceTv.isProhibitMode()) {
            HdmiCecLocalDevice.ActiveSource m50of = HdmiCecLocalDevice.ActiveSource.m50of(hdmiCecLocalDeviceTv.getActiveSource());
            hdmiCecLocalDeviceTv.updateActiveSource(activeSource, "ActiveSourceHandler");
            boolean z = this.mCallback == null;
            if (!m50of.equals(activeSource)) {
                hdmiCecLocalDeviceTv.setPrevPortId(hdmiCecLocalDeviceTv.getActivePortId());
            }
            hdmiCecLocalDeviceTv.updateActiveInput(activeSource.physicalAddress, z);
            invokeCallback(0);
            return;
        }
        HdmiCecLocalDevice.ActiveSource activeSource2 = hdmiCecLocalDeviceTv.getActiveSource();
        if (activeSource2.logicalAddress == getSourceAddress()) {
            this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(activeSource2.logicalAddress, activeSource2.physicalAddress));
            hdmiCecLocalDeviceTv.updateActiveSource(activeSource2, "ActiveSourceHandler");
            invokeCallback(0);
            return;
        }
        hdmiCecLocalDeviceTv.startRoutingControl(activeSource.physicalAddress, activeSource2.physicalAddress, this.mCallback);
    }

    public final int getSourceAddress() {
        return this.mSource.getDeviceInfo().getLogicalAddress();
    }

    public final void invokeCallback(int i) {
        IHdmiControlCallback iHdmiControlCallback = this.mCallback;
        if (iHdmiControlCallback == null) {
            return;
        }
        try {
            iHdmiControlCallback.onComplete(i);
        } catch (RemoteException e) {
            Slog.e("ActiveSourceHandler", "Callback failed:" + e);
        }
    }
}
