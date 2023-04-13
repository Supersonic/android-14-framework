package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.ScanCallback */
/* loaded from: classes2.dex */
public interface ScanCallback {
    void onAnalogSifStandardReported(int i);

    void onAtsc3PlpInfosReported(Atsc3PlpInfo[] atsc3PlpInfoArr);

    void onDvbsStandardReported(int i);

    void onDvbtStandardReported(int i);

    @Deprecated
    void onFrequenciesReported(int[] iArr);

    void onGroupIdsReported(int[] iArr);

    void onHierarchyReported(int i);

    void onInputStreamIdsReported(int[] iArr);

    void onLocked();

    void onPlpIdsReported(int[] iArr);

    void onProgress(int i);

    void onScanStopped();

    void onSignalTypeReported(int i);

    void onSymbolRatesReported(int[] iArr);

    default void onUnlocked() {
    }

    default void onFrequenciesLongReported(long[] frequencies) {
        int[] intFrequencies = new int[frequencies.length];
        for (int i = 0; i < frequencies.length; i++) {
            intFrequencies[i] = (int) frequencies[i];
        }
        onFrequenciesReported(intFrequencies);
    }

    default void onModulationReported(int modulation) {
    }

    default void onPriorityReported(boolean isHighPriority) {
    }

    default void onDvbcAnnexReported(int dvbcAnnex) {
    }

    default void onDvbtCellIdsReported(int[] dvbtCellIds) {
    }
}
