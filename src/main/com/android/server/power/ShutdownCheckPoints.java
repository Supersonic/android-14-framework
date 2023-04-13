package com.android.server.power;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.os.Process;
import android.os.RemoteException;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
/* loaded from: classes2.dex */
public final class ShutdownCheckPoints {
    public final ArrayList<CheckPoint> mCheckPoints;
    public final Injector mInjector;
    public static final ShutdownCheckPoints INSTANCE = new ShutdownCheckPoints();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public interface Injector {
        IActivityManager activityManager();

        long currentTimeMillis();

        int maxCheckPoints();

        int maxDumpFiles();
    }

    public ShutdownCheckPoints() {
        this(new Injector() { // from class: com.android.server.power.ShutdownCheckPoints.1
            @Override // com.android.server.power.ShutdownCheckPoints.Injector
            public int maxCheckPoints() {
                return 100;
            }

            @Override // com.android.server.power.ShutdownCheckPoints.Injector
            public int maxDumpFiles() {
                return 20;
            }

            @Override // com.android.server.power.ShutdownCheckPoints.Injector
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }

            @Override // com.android.server.power.ShutdownCheckPoints.Injector
            public IActivityManager activityManager() {
                return ActivityManager.getService();
            }
        });
    }

    @VisibleForTesting
    public ShutdownCheckPoints(Injector injector) {
        this.mCheckPoints = new ArrayList<>();
        this.mInjector = injector;
    }

    public static void recordCheckPoint(String str) {
        INSTANCE.recordCheckPointInternal(str);
    }

    public static void recordCheckPoint(int i, String str) {
        INSTANCE.recordCheckPointInternal(i, str);
    }

    public static void recordCheckPoint(String str, String str2, String str3) {
        INSTANCE.recordCheckPointInternal(str, str2, str3);
    }

    public static Thread newDumpThread(File file) {
        return INSTANCE.newDumpThreadInternal(file);
    }

    @VisibleForTesting
    public void recordCheckPointInternal(String str) {
        recordCheckPointInternal(new SystemServerCheckPoint(this.mInjector, str));
        Slog.v("ShutdownCheckPoints", "System server shutdown checkpoint recorded");
    }

    @VisibleForTesting
    public void recordCheckPointInternal(int i, String str) {
        CheckPoint binderCheckPoint;
        if (i == Process.myPid()) {
            binderCheckPoint = new SystemServerCheckPoint(this.mInjector, str);
        } else {
            binderCheckPoint = new BinderCheckPoint(this.mInjector, i, str);
        }
        recordCheckPointInternal(binderCheckPoint);
        Slog.v("ShutdownCheckPoints", "Binder shutdown checkpoint recorded with pid=" + i);
    }

    @VisibleForTesting
    public void recordCheckPointInternal(String str, String str2, String str3) {
        CheckPoint intentCheckPoint;
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(str2)) {
            intentCheckPoint = new SystemServerCheckPoint(this.mInjector, str3);
        } else {
            intentCheckPoint = new IntentCheckPoint(this.mInjector, str, str2, str3);
        }
        recordCheckPointInternal(intentCheckPoint);
        Slog.v("ShutdownCheckPoints", String.format("Shutdown intent checkpoint recorded intent=%s from package=%s", str, str2));
    }

    public final void recordCheckPointInternal(CheckPoint checkPoint) {
        synchronized (this.mCheckPoints) {
            this.mCheckPoints.add(checkPoint);
            if (this.mCheckPoints.size() > this.mInjector.maxCheckPoints()) {
                this.mCheckPoints.remove(0);
            }
        }
    }

    @VisibleForTesting
    public void dumpInternal(PrintWriter printWriter) {
        ArrayList<CheckPoint> arrayList;
        synchronized (this.mCheckPoints) {
            arrayList = new ArrayList(this.mCheckPoints);
        }
        for (CheckPoint checkPoint : arrayList) {
            checkPoint.dump(printWriter);
            printWriter.println();
        }
    }

    @VisibleForTesting
    public Thread newDumpThreadInternal(File file) {
        return new FileDumperThread(this, file, this.mInjector.maxDumpFiles());
    }

    /* loaded from: classes2.dex */
    public static abstract class CheckPoint {
        public final String mReason;
        public final long mTimestamp;

        public abstract void dumpDetails(PrintWriter printWriter);

        public abstract String getOrigin();

        public CheckPoint(Injector injector, String str) {
            this.mTimestamp = injector.currentTimeMillis();
            this.mReason = str;
        }

        public final void dump(PrintWriter printWriter) {
            printWriter.print("Shutdown request from ");
            printWriter.print(getOrigin());
            if (this.mReason != null) {
                printWriter.print(" for reason ");
                printWriter.print(this.mReason);
            }
            printWriter.print(" at ");
            printWriter.print(ShutdownCheckPoints.DATE_FORMAT.format(new Date(this.mTimestamp)));
            printWriter.println(" (epoch=" + this.mTimestamp + ")");
            dumpDetails(printWriter);
        }
    }

    /* loaded from: classes2.dex */
    public static class SystemServerCheckPoint extends CheckPoint {
        public final StackTraceElement[] mStackTraceElements;

        @Override // com.android.server.power.ShutdownCheckPoints.CheckPoint
        public String getOrigin() {
            return "SYSTEM";
        }

        public SystemServerCheckPoint(Injector injector, String str) {
            super(injector, str);
            this.mStackTraceElements = Thread.currentThread().getStackTrace();
        }

        @Override // com.android.server.power.ShutdownCheckPoints.CheckPoint
        public void dumpDetails(PrintWriter printWriter) {
            String methodName = getMethodName();
            if (methodName == null) {
                methodName = "Failed to get method name";
            }
            printWriter.println(methodName);
            printStackTrace(printWriter);
        }

        public String getMethodName() {
            int findCallSiteIndex = findCallSiteIndex();
            StackTraceElement[] stackTraceElementArr = this.mStackTraceElements;
            if (findCallSiteIndex < stackTraceElementArr.length) {
                StackTraceElement stackTraceElement = stackTraceElementArr[findCallSiteIndex];
                return String.format("%s.%s", stackTraceElement.getClassName(), stackTraceElement.getMethodName());
            }
            return null;
        }

        public void printStackTrace(PrintWriter printWriter) {
            int findCallSiteIndex = findCallSiteIndex();
            while (true) {
                findCallSiteIndex++;
                if (findCallSiteIndex >= this.mStackTraceElements.length) {
                    return;
                }
                printWriter.print(" at ");
                printWriter.println(this.mStackTraceElements[findCallSiteIndex]);
            }
        }

        public final int findCallSiteIndex() {
            String canonicalName = ShutdownCheckPoints.class.getCanonicalName();
            int i = 0;
            while (true) {
                StackTraceElement[] stackTraceElementArr = this.mStackTraceElements;
                if (i >= stackTraceElementArr.length || stackTraceElementArr[i].getClassName().equals(canonicalName)) {
                    break;
                }
                i++;
            }
            while (true) {
                StackTraceElement[] stackTraceElementArr2 = this.mStackTraceElements;
                if (i >= stackTraceElementArr2.length || !stackTraceElementArr2[i].getClassName().equals(canonicalName)) {
                    break;
                }
                i++;
            }
            return i;
        }
    }

    /* loaded from: classes2.dex */
    public static class BinderCheckPoint extends SystemServerCheckPoint {
        public final IActivityManager mActivityManager;
        public final int mCallerProcessId;

        @Override // com.android.server.power.ShutdownCheckPoints.SystemServerCheckPoint, com.android.server.power.ShutdownCheckPoints.CheckPoint
        public String getOrigin() {
            return "BINDER";
        }

        public BinderCheckPoint(Injector injector, int i, String str) {
            super(injector, str);
            this.mCallerProcessId = i;
            this.mActivityManager = injector.activityManager();
        }

        @Override // com.android.server.power.ShutdownCheckPoints.SystemServerCheckPoint, com.android.server.power.ShutdownCheckPoints.CheckPoint
        public void dumpDetails(PrintWriter printWriter) {
            String methodName = getMethodName();
            if (methodName == null) {
                methodName = "Failed to get method name";
            }
            printWriter.println(methodName);
            String processName = getProcessName();
            printWriter.print("From process ");
            if (processName == null) {
                processName = "?";
            }
            printWriter.print(processName);
            printWriter.println(" (pid=" + this.mCallerProcessId + ")");
        }

        public String getProcessName() {
            try {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : this.mActivityManager.getRunningAppProcesses()) {
                    if (runningAppProcessInfo.pid == this.mCallerProcessId) {
                        return runningAppProcessInfo.processName;
                    }
                }
                return null;
            } catch (RemoteException e) {
                Slog.e("ShutdownCheckPoints", "Failed to get running app processes from ActivityManager", e);
                return null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class IntentCheckPoint extends CheckPoint {
        public final String mIntentName;
        public final String mPackageName;

        @Override // com.android.server.power.ShutdownCheckPoints.CheckPoint
        public String getOrigin() {
            return "INTENT";
        }

        public IntentCheckPoint(Injector injector, String str, String str2, String str3) {
            super(injector, str3);
            this.mIntentName = str;
            this.mPackageName = str2;
        }

        @Override // com.android.server.power.ShutdownCheckPoints.CheckPoint
        public void dumpDetails(PrintWriter printWriter) {
            printWriter.print("Intent: ");
            printWriter.println(this.mIntentName);
            printWriter.print("Package: ");
            printWriter.println(this.mPackageName);
        }
    }

    /* loaded from: classes2.dex */
    public static final class FileDumperThread extends Thread {
        public final File mBaseFile;
        public final int mFileCountLimit;
        public final ShutdownCheckPoints mInstance;

        public FileDumperThread(ShutdownCheckPoints shutdownCheckPoints, File file, int i) {
            this.mInstance = shutdownCheckPoints;
            this.mBaseFile = file;
            this.mFileCountLimit = i;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            this.mBaseFile.getParentFile().mkdirs();
            File[] listCheckPointsFiles = listCheckPointsFiles();
            int length = (listCheckPointsFiles.length - this.mFileCountLimit) + 1;
            for (int i = 0; i < length; i++) {
                listCheckPointsFiles[i].delete();
            }
            writeCheckpoints(new File(String.format("%s-%d", this.mBaseFile.getAbsolutePath(), Long.valueOf(System.currentTimeMillis()))));
        }

        public final File[] listCheckPointsFiles() {
            final String str = this.mBaseFile.getName() + PackageManagerShellCommandDataLoader.STDIN_PATH;
            File[] listFiles = this.mBaseFile.getParentFile().listFiles(new FilenameFilter() { // from class: com.android.server.power.ShutdownCheckPoints.FileDumperThread.1
                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str2) {
                    if (str2.startsWith(str)) {
                        try {
                            Long.valueOf(str2.substring(str.length()));
                            return true;
                        } catch (NumberFormatException unused) {
                            return false;
                        }
                    }
                    return false;
                }
            });
            if (listFiles == null) {
                return ShutdownCheckPoints.EMPTY_FILE_ARRAY;
            }
            Arrays.sort(listFiles);
            return listFiles;
        }

        public final void writeCheckpoints(File file) {
            FileOutputStream fileOutputStream;
            AtomicFile atomicFile = new AtomicFile(this.mBaseFile);
            try {
                fileOutputStream = atomicFile.startWrite();
            } catch (IOException e) {
                e = e;
                fileOutputStream = null;
            }
            try {
                PrintWriter printWriter = new PrintWriter(fileOutputStream);
                this.mInstance.dumpInternal(printWriter);
                printWriter.flush();
                atomicFile.finishWrite(fileOutputStream);
            } catch (IOException e2) {
                e = e2;
                Log.e("ShutdownCheckPoints", "Failed to write shutdown checkpoints", e);
                if (fileOutputStream != null) {
                    atomicFile.failWrite(fileOutputStream);
                }
                this.mBaseFile.renameTo(file);
            }
            this.mBaseFile.renameTo(file);
        }
    }
}
