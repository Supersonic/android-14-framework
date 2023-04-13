package com.android.internal.p028os;
/* renamed from: com.android.internal.os.KernelCpuTotalBpfMapReader */
/* loaded from: classes4.dex */
public final class KernelCpuTotalBpfMapReader {
    private static native long[] readInternal();

    private KernelCpuTotalBpfMapReader() {
    }

    public static long[] read() {
        if (!KernelCpuBpfTracking.startTracking()) {
            return null;
        }
        return readInternal();
    }
}
