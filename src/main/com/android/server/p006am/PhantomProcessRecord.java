package com.android.server.p006am;

import android.os.Handler;
import android.os.Process;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.PhantomProcessRecord */
/* loaded from: classes.dex */
public final class PhantomProcessRecord {
    public long mCurrentCputime;
    public long mLastCputime;
    public final Object mLock;
    public final Consumer<PhantomProcessRecord> mOnKillListener;
    public final int mPid;
    public final FileDescriptor mPidFd;
    public final int mPpid;
    public final String mProcessName;
    public final ActivityManagerService mService;
    public String mStringName;
    public final int mUid;
    public int mUpdateSeq;
    public boolean mZombie;
    public static final long[] LONG_OUT = new long[1];
    public static final int[] LONG_FORMAT = {8202};
    public Runnable mProcKillTimer = new Runnable() { // from class: com.android.server.am.PhantomProcessRecord.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (PhantomProcessRecord.this.mLock) {
                Slog.w("ActivityManager", "Process " + toString() + " is still alive after " + PhantomProcessRecord.this.mService.mConstants.mProcessKillTimeoutMs + "ms");
                PhantomProcessRecord phantomProcessRecord = PhantomProcessRecord.this;
                phantomProcessRecord.mZombie = true;
                phantomProcessRecord.onProcDied(false);
            }
        }
    };
    public boolean mKilled = false;
    public int mAdj = -1000;
    public final long mKnownSince = SystemClock.elapsedRealtime();
    public final Handler mKillHandler = ProcessList.sKillHandler;

    public PhantomProcessRecord(String str, int i, int i2, int i3, ActivityManagerService activityManagerService, Consumer<PhantomProcessRecord> consumer) throws IllegalStateException {
        this.mProcessName = str;
        this.mUid = i;
        this.mPid = i2;
        this.mPpid = i3;
        this.mService = activityManagerService;
        this.mLock = activityManagerService.mPhantomProcessList.mLock;
        this.mOnKillListener = consumer;
        if (Process.supportsPidFd()) {
            StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
            try {
                try {
                    FileDescriptor openPidFd = Process.openPidFd(i2, 0);
                    this.mPidFd = openPidFd;
                    if (openPidFd != null) {
                        return;
                    }
                    throw new IllegalStateException();
                } catch (IOException e) {
                    Slog.w("ActivityManager", "Unable to open process " + i2 + ", it might be gone");
                    IllegalStateException illegalStateException = new IllegalStateException();
                    illegalStateException.initCause(e);
                    throw illegalStateException;
                }
            } finally {
                StrictMode.setThreadPolicy(allowThreadDiskReads);
            }
        }
        this.mPidFd = null;
    }

    @GuardedBy({"mLock"})
    public void killLocked(String str, boolean z) {
        if (this.mKilled) {
            return;
        }
        Trace.traceBegin(64L, "kill");
        if (z || this.mUid == this.mService.mCurOomAdjUid) {
            ActivityManagerService activityManagerService = this.mService;
            activityManagerService.reportUidInfoMessageLocked("ActivityManager", "Killing " + toString() + ": " + str, this.mUid);
        }
        if (this.mPid > 0) {
            EventLog.writeEvent(30023, Integer.valueOf(UserHandle.getUserId(this.mUid)), Integer.valueOf(this.mPid), this.mProcessName, Integer.valueOf(this.mAdj), str);
            if (!Process.supportsPidFd()) {
                onProcDied(false);
            } else {
                this.mKillHandler.postDelayed(this.mProcKillTimer, this, this.mService.mConstants.mProcessKillTimeoutMs);
            }
            Process.killProcessQuiet(this.mPid);
            ProcessList.killProcessGroup(this.mUid, this.mPid);
        }
        this.mKilled = true;
        Trace.traceEnd(64L);
    }

    @GuardedBy({"mLock"})
    public void updateAdjLocked() {
        String str = "/proc/" + this.mPid + "/oom_score_adj";
        int[] iArr = LONG_FORMAT;
        long[] jArr = LONG_OUT;
        if (Process.readProcFile(str, iArr, null, jArr, null)) {
            this.mAdj = (int) jArr[0];
        }
    }

    @GuardedBy({"mLock"})
    public void onProcDied(boolean z) {
        if (z) {
            Slog.i("ActivityManager", "Process " + toString() + " died");
        }
        this.mKillHandler.removeCallbacks(this.mProcKillTimer, this);
        Consumer<PhantomProcessRecord> consumer = this.mOnKillListener;
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    public String toString() {
        String str = this.mStringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("PhantomProcessRecord {");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.mPid);
        sb.append(':');
        sb.append(this.mPpid);
        sb.append(':');
        sb.append(this.mProcessName);
        sb.append('/');
        int i = this.mUid;
        if (i < 10000) {
            sb.append(i);
        } else {
            sb.append('u');
            sb.append(UserHandle.getUserId(this.mUid));
            int appId = UserHandle.getAppId(this.mUid);
            if (appId >= 10000) {
                sb.append('a');
                sb.append(appId - 10000);
            } else {
                sb.append('s');
                sb.append(appId);
            }
            if (appId >= 99000 && appId <= 99999) {
                sb.append('i');
                sb.append(appId - 99000);
            }
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.mStringName = sb2;
        return sb2;
    }

    public void dump(PrintWriter printWriter, String str) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        printWriter.print(str);
        printWriter.print("user #");
        printWriter.print(UserHandle.getUserId(this.mUid));
        printWriter.print(" uid=");
        printWriter.print(this.mUid);
        printWriter.print(" pid=");
        printWriter.print(this.mPid);
        printWriter.print(" ppid=");
        printWriter.print(this.mPpid);
        printWriter.print(" knownSince=");
        TimeUtils.formatDuration(this.mKnownSince, elapsedRealtime, printWriter);
        printWriter.print(" killed=");
        printWriter.println(this.mKilled);
        printWriter.print(str);
        printWriter.print("lastCpuTime=");
        printWriter.print(this.mLastCputime);
        if (this.mLastCputime > 0) {
            printWriter.print(" timeUsed=");
            TimeUtils.formatDuration(this.mCurrentCputime - this.mLastCputime, printWriter);
        }
        printWriter.print(" oom adj=");
        printWriter.print(this.mAdj);
        printWriter.print(" seq=");
        printWriter.println(this.mUpdateSeq);
    }

    public boolean equals(String str, int i, int i2) {
        return this.mUid == i && this.mPid == i2 && TextUtils.equals(this.mProcessName, str);
    }
}
