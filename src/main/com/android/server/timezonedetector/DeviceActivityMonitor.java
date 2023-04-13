package com.android.server.timezonedetector;
/* loaded from: classes2.dex */
public interface DeviceActivityMonitor extends Dumpable {

    /* loaded from: classes2.dex */
    public interface Listener {
        void onFlightComplete();
    }

    void addListener(Listener listener);
}
