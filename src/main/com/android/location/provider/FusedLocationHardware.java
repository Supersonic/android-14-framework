package com.android.location.provider;

import android.annotation.SystemApi;
import android.os.Looper;
@SystemApi
@Deprecated
/* loaded from: classes.dex */
public final class FusedLocationHardware {
    private FusedLocationHardware() {
    }

    public void registerSink(FusedLocationHardwareSink sink, Looper looper) {
    }

    public void unregisterSink(FusedLocationHardwareSink sink) {
    }

    public int getSupportedBatchSize() {
        return 0;
    }

    public void startBatching(int id, GmsFusedBatchOptions batchOptions) {
    }

    public void stopBatching(int id) {
    }

    public void updateBatchingOptions(int id, GmsFusedBatchOptions batchOptions) {
    }

    public void requestBatchOfLocations(int batchSizeRequest) {
    }

    public void flushBatchedLocations() {
    }

    public boolean supportsDiagnosticDataInjection() {
        return false;
    }

    public void injectDiagnosticData(String data) {
    }

    public boolean supportsDeviceContextInjection() {
        return false;
    }

    public void injectDeviceContext(int deviceEnabledContext) {
    }

    public int getVersion() {
        return 1;
    }
}
