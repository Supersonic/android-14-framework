package com.android.server.p006am;

import android.app.IInstrumentationWatcher;
import android.content.ComponentName;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import java.util.ArrayList;
/* renamed from: com.android.server.am.InstrumentationReporter */
/* loaded from: classes.dex */
public class InstrumentationReporter {
    public final Object mLock = new Object();
    public ArrayList<Report> mPendingReports;
    public Thread mThread;

    /* renamed from: com.android.server.am.InstrumentationReporter$MyThread */
    /* loaded from: classes.dex */
    public final class MyThread extends Thread {
        public MyThread() {
            super("InstrumentationReporter");
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x001b, code lost:
            r1 = 0;
         */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x0020, code lost:
            if (r1 >= r4.size()) goto L28;
         */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x0022, code lost:
            r2 = r4.get(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x002a, code lost:
            if (r2.mType != 0) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:18:0x002c, code lost:
            r2.mWatcher.instrumentationStatus(r2.mName, r2.mResultCode, r2.mResults);
         */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x0038, code lost:
            r2.mWatcher.instrumentationFinished(r2.mName, r2.mResultCode, r2.mResults);
         */
        /* JADX WARN: Code restructure failed: missing block: B:21:0x0044, code lost:
            android.util.Slog.i("ActivityManager", "Failure reporting to instrumentation watcher: comp=" + r2.mName + " results=" + r2.mResults);
         */
        /* JADX WARN: Code restructure failed: missing block: B:24:0x006b, code lost:
            r8.this$0.mLock.wait(10000);
         */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            boolean z;
            Process.setThreadPriority(0);
            while (true) {
                z = false;
                while (true) {
                    synchronized (InstrumentationReporter.this.mLock) {
                        InstrumentationReporter instrumentationReporter = InstrumentationReporter.this;
                        ArrayList<Report> arrayList = instrumentationReporter.mPendingReports;
                        instrumentationReporter.mPendingReports = null;
                        if (arrayList != null && !arrayList.isEmpty()) {
                            break;
                        }
                        InstrumentationReporter.this.mThread = null;
                        return;
                    }
                    z = true;
                }
            }
            z = true;
            int i = i + 1;
        }
    }

    /* renamed from: com.android.server.am.InstrumentationReporter$Report */
    /* loaded from: classes.dex */
    public final class Report {
        public final ComponentName mName;
        public final int mResultCode;
        public final Bundle mResults;
        public final int mType;
        public final IInstrumentationWatcher mWatcher;

        public Report(int i, IInstrumentationWatcher iInstrumentationWatcher, ComponentName componentName, int i2, Bundle bundle) {
            this.mType = i;
            this.mWatcher = iInstrumentationWatcher;
            this.mName = componentName;
            this.mResultCode = i2;
            this.mResults = bundle;
            Binder.allowBlocking(iInstrumentationWatcher.asBinder());
        }
    }

    public void reportStatus(IInstrumentationWatcher iInstrumentationWatcher, ComponentName componentName, int i, Bundle bundle) {
        report(new Report(0, iInstrumentationWatcher, componentName, i, bundle));
    }

    public void reportFinished(IInstrumentationWatcher iInstrumentationWatcher, ComponentName componentName, int i, Bundle bundle) {
        report(new Report(1, iInstrumentationWatcher, componentName, i, bundle));
    }

    public final void report(Report report) {
        synchronized (this.mLock) {
            if (this.mThread == null) {
                MyThread myThread = new MyThread();
                this.mThread = myThread;
                myThread.start();
            }
            if (this.mPendingReports == null) {
                this.mPendingReports = new ArrayList<>();
            }
            this.mPendingReports.add(report);
            this.mLock.notifyAll();
        }
    }
}
