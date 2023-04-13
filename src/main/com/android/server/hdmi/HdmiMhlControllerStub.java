package com.android.server.hdmi;

import android.hardware.hdmi.HdmiPortInfo;
import android.util.SparseArray;
import com.android.internal.util.IndentingPrintWriter;
/* loaded from: classes.dex */
public final class HdmiMhlControllerStub {
    public static final SparseArray<HdmiMhlLocalDeviceStub> mLocalDevices = new SparseArray<>();
    public static final HdmiPortInfo[] EMPTY_PORT_INFO = new HdmiPortInfo[0];

    public void clearAllLocalDevices() {
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
    }

    public HdmiMhlLocalDeviceStub getLocalDevice(int i) {
        return null;
    }

    public HdmiMhlLocalDeviceStub getLocalDeviceById(int i) {
        return null;
    }

    public boolean isReady() {
        return false;
    }

    public void setOption(int i, int i2) {
    }

    public HdmiMhlControllerStub(HdmiControlService hdmiControlService) {
    }

    public static HdmiMhlControllerStub create(HdmiControlService hdmiControlService) {
        return new HdmiMhlControllerStub(hdmiControlService);
    }

    public HdmiPortInfo[] getPortInfos() {
        return EMPTY_PORT_INFO;
    }
}
