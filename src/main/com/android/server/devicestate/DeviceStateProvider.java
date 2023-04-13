package com.android.server.devicestate;
/* loaded from: classes.dex */
public interface DeviceStateProvider {

    /* loaded from: classes.dex */
    public interface Listener {
        void onStateChanged(int i);

        void onSupportedDeviceStatesChanged(DeviceState[] deviceStateArr, int i);
    }

    void setListener(Listener listener);
}
