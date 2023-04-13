package com.android.server.appop;

import android.os.Trace;
import android.util.ArraySet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class AppOpsCheckingServiceTracingDecorator implements AppOpsCheckingServiceInterface {
    public final AppOpsCheckingServiceInterface mService;

    public AppOpsCheckingServiceTracingDecorator(AppOpsCheckingServiceInterface appOpsCheckingServiceInterface) {
        this.mService = appOpsCheckingServiceInterface;
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void writeState() {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#writeState");
        try {
            this.mService.writeState();
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void readState() {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#readState");
        try {
            this.mService.readState();
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void shutdown() {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#shutdown");
        try {
            this.mService.shutdown();
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void systemReady() {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#systemReady");
        try {
            this.mService.systemReady();
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseIntArray getNonDefaultUidModes(int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getNonDefaultUidModes");
        try {
            return this.mService.getNonDefaultUidModes(i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseIntArray getNonDefaultPackageModes(String str, int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getNonDefaultPackageModes");
        try {
            return this.mService.getNonDefaultPackageModes(str, i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public int getUidMode(int i, int i2) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getUidMode");
        try {
            return this.mService.getUidMode(i, i2);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public boolean setUidMode(int i, int i2, int i3) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#setUidMode");
        try {
            return this.mService.setUidMode(i, i2, i3);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public int getPackageMode(String str, int i, int i2) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getPackageMode");
        try {
            return this.mService.getPackageMode(str, i, i2);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void setPackageMode(String str, int i, int i2, int i3) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#setPackageMode");
        try {
            this.mService.setPackageMode(str, i, i2, i3);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public boolean removePackage(String str, int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#removePackage");
        try {
            return this.mService.removePackage(str, i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void removeUid(int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#removeUid");
        try {
            this.mService.removeUid(i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void clearAllModes() {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#clearAllModes");
        try {
            this.mService.clearAllModes();
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void startWatchingOpModeChanged(OnOpModeChangedListener onOpModeChangedListener, int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#startWatchingOpModeChanged");
        try {
            this.mService.startWatchingOpModeChanged(onOpModeChangedListener, i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void startWatchingPackageModeChanged(OnOpModeChangedListener onOpModeChangedListener, String str) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#startWatchingPackageModeChanged");
        try {
            this.mService.startWatchingPackageModeChanged(onOpModeChangedListener, str);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void removeListener(OnOpModeChangedListener onOpModeChangedListener) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#removeListener");
        try {
            this.mService.removeListener(onOpModeChangedListener);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public ArraySet<OnOpModeChangedListener> getOpModeChangedListeners(int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getOpModeChangedListeners");
        try {
            return this.mService.getOpModeChangedListeners(i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public ArraySet<OnOpModeChangedListener> getPackageModeChangedListeners(String str) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#getPackageModeChangedListeners");
        try {
            return this.mService.getPackageModeChangedListeners(str);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyWatchersOfChange(int i, int i2) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#notifyWatchersOfChange");
        try {
            this.mService.notifyWatchersOfChange(i, i2);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyOpChanged(OnOpModeChangedListener onOpModeChangedListener, int i, int i2, String str) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#notifyOpChanged");
        try {
            this.mService.notifyOpChanged(onOpModeChangedListener, i, i2, str);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public void notifyOpChangedForAllPkgsInUid(int i, int i2, boolean z, OnOpModeChangedListener onOpModeChangedListener) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#notifyOpChangedForAllPkgsInUid");
        try {
            this.mService.notifyOpChangedForAllPkgsInUid(i, i2, z, onOpModeChangedListener);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseBooleanArray evalForegroundUidOps(int i, SparseBooleanArray sparseBooleanArray) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#evalForegroundUidOps");
        try {
            return this.mService.evalForegroundUidOps(i, sparseBooleanArray);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public SparseBooleanArray evalForegroundPackageOps(String str, SparseBooleanArray sparseBooleanArray, int i) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#evalForegroundPackageOps");
        try {
            return this.mService.evalForegroundPackageOps(str, sparseBooleanArray, i);
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    @Override // com.android.server.appop.AppOpsCheckingServiceInterface
    public boolean dumpListeners(int i, int i2, String str, PrintWriter printWriter) {
        Trace.traceBegin(524288L, "TaggedTracingAppOpsCheckingServiceInterfaceImpl#dumpListeners");
        try {
            return this.mService.dumpListeners(i, i2, str, printWriter);
        } finally {
            Trace.traceEnd(524288L);
        }
    }
}
