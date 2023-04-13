package com.android.internal.p028os;
/* renamed from: com.android.internal.os.KernelCpuBpfTracking */
/* loaded from: classes4.dex */
public final class KernelCpuBpfTracking {
    private static boolean sTracking = false;
    private static long[] sFreqs = null;
    private static int[] sFreqsClusters = null;

    private static native int[] getFreqsClustersInternal();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native long[] getFreqsInternal();

    public static native boolean isSupported();

    private static native boolean startTrackingInternal();

    private KernelCpuBpfTracking() {
    }

    public static boolean startTracking() {
        if (!sTracking) {
            sTracking = startTrackingInternal();
        }
        return sTracking;
    }

    public static long[] getFreqs() {
        if (sFreqs == null) {
            long[] freqs = getFreqsInternal();
            if (freqs == null) {
                return new long[0];
            }
            sFreqs = freqs;
        }
        return sFreqs;
    }

    public static int[] getFreqsClusters() {
        if (sFreqsClusters == null) {
            int[] freqsClusters = getFreqsClustersInternal();
            if (freqsClusters == null) {
                return new int[0];
            }
            sFreqsClusters = freqsClusters;
        }
        return sFreqsClusters;
    }

    public static int getClusters() {
        int[] freqClusters = getFreqsClusters();
        if (freqClusters.length > 0) {
            return freqClusters[freqClusters.length - 1] + 1;
        }
        return 0;
    }
}
