package com.android.internal.p028os;
/* renamed from: com.android.internal.os.KernelAllocationStats */
/* loaded from: classes4.dex */
public final class KernelAllocationStats {
    public static native ProcessDmabuf[] getDmabufAllocations();

    public static native ProcessGpuMem[] getGpuAllocations();

    private KernelAllocationStats() {
    }

    /* renamed from: com.android.internal.os.KernelAllocationStats$ProcessDmabuf */
    /* loaded from: classes4.dex */
    public static final class ProcessDmabuf {
        public final int oomScore;
        public final String processName;
        public final int retainedBuffersCount;
        public final int retainedSizeKb;
        public final int surfaceFlingerCount;
        public final int surfaceFlingerSizeKb;
        public final int uid;

        ProcessDmabuf(int uid, String processName, int oomScore, int retainedSizeKb, int retainedBuffersCount, int surfaceFlingerSizeKb, int surfaceFlingerCount) {
            this.uid = uid;
            this.processName = processName;
            this.oomScore = oomScore;
            this.retainedSizeKb = retainedSizeKb;
            this.retainedBuffersCount = retainedBuffersCount;
            this.surfaceFlingerSizeKb = surfaceFlingerSizeKb;
            this.surfaceFlingerCount = surfaceFlingerCount;
        }
    }

    /* renamed from: com.android.internal.os.KernelAllocationStats$ProcessGpuMem */
    /* loaded from: classes4.dex */
    public static final class ProcessGpuMem {
        public final int gpuMemoryKb;
        public final int pid;

        ProcessGpuMem(int pid, int gpuMemoryKb) {
            this.pid = pid;
            this.gpuMemoryKb = gpuMemoryKb;
        }
    }
}
