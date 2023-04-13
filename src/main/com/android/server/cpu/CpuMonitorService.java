package com.android.server.cpu;

import android.content.Context;
import android.os.Binder;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.DumpUtils;
import com.android.server.SystemService;
import com.android.server.utils.PriorityDump;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class CpuMonitorService extends SystemService {
    public static final boolean DEBUG = Log.isLoggable(CpuMonitorService.class.getSimpleName(), 3);
    public static final String TAG = "CpuMonitorService";
    public final Context mContext;
    @GuardedBy({"mLock"})
    public final ArrayMap<Object, Object> mCpuAvailabilityCallbackInfoByCallbacks;
    public final CpuMonitorInternal mLocalService;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public long mMonitoringIntervalMilliseconds;

    public CpuMonitorService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mCpuAvailabilityCallbackInfoByCallbacks = new ArrayMap<>();
        this.mMonitoringIntervalMilliseconds = 5000L;
        this.mLocalService = new CpuMonitorInternal() { // from class: com.android.server.cpu.CpuMonitorService.1
        };
        this.mContext = context;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishLocalService(CpuMonitorInternal.class, this.mLocalService);
        publishBinderService("cpu_monitor", new CpuMonitorBinder(), false, 1);
    }

    public final void doDump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.printf("*%s*\n", new Object[]{CpuMonitorService.class.getSimpleName()});
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            indentingPrintWriter.printf("CPU monitoring interval: %d ms\n", new Object[]{Long.valueOf(this.mMonitoringIntervalMilliseconds)});
            if (!this.mCpuAvailabilityCallbackInfoByCallbacks.isEmpty()) {
                indentingPrintWriter.println("CPU availability change callbacks:");
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < this.mCpuAvailabilityCallbackInfoByCallbacks.size(); i++) {
                    indentingPrintWriter.printf("%s: %s\n", new Object[]{this.mCpuAvailabilityCallbackInfoByCallbacks.keyAt(i), this.mCpuAvailabilityCallbackInfoByCallbacks.valueAt(i)});
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
        indentingPrintWriter.decreaseIndent();
    }

    /* loaded from: classes.dex */
    public final class CpuMonitorBinder extends Binder {
        public final PriorityDump.PriorityDumper mPriorityDumper;

        public CpuMonitorBinder() {
            this.mPriorityDumper = new PriorityDump.PriorityDumper() { // from class: com.android.server.cpu.CpuMonitorService.CpuMonitorBinder.1
                @Override // com.android.server.utils.PriorityDump.PriorityDumper
                public void dumpCritical(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
                    if (!DumpUtils.checkDumpAndUsageStatsPermission(CpuMonitorService.this.mContext, CpuMonitorService.TAG, printWriter) || z) {
                        return;
                    }
                    IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
                    try {
                        CpuMonitorService.this.doDump(indentingPrintWriter);
                        indentingPrintWriter.close();
                    } catch (Throwable th) {
                        try {
                            indentingPrintWriter.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
            };
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            PriorityDump.dump(this.mPriorityDumper, fileDescriptor, printWriter, strArr);
        }
    }
}
