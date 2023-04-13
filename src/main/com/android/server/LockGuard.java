package com.android.server;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.util.jobs.XmlUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class LockGuard {
    public static Object[] sKnownFixed = new Object[9];
    public static ArrayMap<Object, LockInfo> sKnown = new ArrayMap<>(0, true);

    /* loaded from: classes.dex */
    public static class LockInfo {
        public ArraySet<Object> children;
        public boolean doWtf;
        public String label;

        public LockInfo() {
            this.children = new ArraySet<>(0, true);
        }
    }

    public static LockInfo findOrCreateLockInfo(Object obj) {
        LockInfo lockInfo = sKnown.get(obj);
        if (lockInfo == null) {
            LockInfo lockInfo2 = new LockInfo();
            lockInfo2.label = "0x" + Integer.toHexString(System.identityHashCode(obj)) + " [" + new Throwable().getStackTrace()[2].toString() + "]";
            sKnown.put(obj, lockInfo2);
            return lockInfo2;
        }
        return lockInfo;
    }

    public static void guard(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            Object obj = sKnownFixed[i2];
            if (obj != null && Thread.holdsLock(obj)) {
                Object obj2 = sKnownFixed[i];
                doLog(obj2, "Calling thread " + Thread.currentThread().getName() + " is holding " + lockToString(i2) + " while trying to acquire " + lockToString(i));
            }
        }
    }

    public static void doLog(Object obj, String str) {
        if (obj != null && findOrCreateLockInfo(obj).doWtf) {
            final RuntimeException runtimeException = new RuntimeException(str);
            new Thread(new Runnable() { // from class: com.android.server.LockGuard$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Slog.wtf("LockGuard", runtimeException);
                }
            }).start();
            return;
        }
        Slog.w("LockGuard", str, new Throwable());
    }

    public static Object installLock(Object obj, int i) {
        return installLock(obj, i, false);
    }

    public static Object installLock(Object obj, int i, boolean z) {
        sKnownFixed[i] = obj;
        LockInfo findOrCreateLockInfo = findOrCreateLockInfo(obj);
        findOrCreateLockInfo.doWtf = z;
        findOrCreateLockInfo.label = "Lock-" + lockToString(i);
        return obj;
    }

    public static Object installNewLock(int i) {
        return installNewLock(i, false);
    }

    public static Object installNewLock(int i, boolean z) {
        Object obj = new Object();
        installLock(obj, i, z);
        return obj;
    }

    public static String lockToString(Object obj) {
        LockInfo lockInfo = sKnown.get(obj);
        if (lockInfo != null && !TextUtils.isEmpty(lockInfo.label)) {
            return lockInfo.label;
        }
        return "0x" + Integer.toHexString(System.identityHashCode(obj));
    }

    public static String lockToString(int i) {
        switch (i) {
            case 0:
                return "APP_OPS";
            case 1:
                return "POWER";
            case 2:
                return "USER";
            case 3:
                return "PACKAGES";
            case 4:
                return "STORAGE";
            case 5:
                return "WINDOW";
            case 6:
                return "PROCESS";
            case 7:
                return "ACTIVITY";
            case 8:
                return "DPMS";
            default:
                return Integer.toString(i);
        }
    }

    public static void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (int i = 0; i < sKnown.size(); i++) {
            Object keyAt = sKnown.keyAt(i);
            LockInfo valueAt = sKnown.valueAt(i);
            printWriter.println("Lock " + lockToString(keyAt) + XmlUtils.STRING_ARRAY_SEPARATOR);
            for (int i2 = 0; i2 < valueAt.children.size(); i2++) {
                printWriter.println("  Child " + lockToString(valueAt.children.valueAt(i2)));
            }
            printWriter.println();
        }
    }
}
