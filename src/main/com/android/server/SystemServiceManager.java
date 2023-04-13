package com.android.server;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Trace;
import android.util.ArraySet;
import android.util.Dumpable;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SystemServerClassLoaderFactory;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.utils.TimingsTraceAndSlog;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public final class SystemServiceManager implements Dumpable {
    public static final String TAG = SystemServiceManager.class.getSimpleName();
    public static volatile int sOtherServicesStartIndex;
    public static File sSystemDir;
    public final Context mContext;
    @GuardedBy({"mTargetUsers"})
    public SystemService.TargetUser mCurrentUser;
    public boolean mRuntimeRestarted;
    public long mRuntimeStartElapsedTime;
    public long mRuntimeStartUptime;
    public boolean mSafeMode;
    public UserManagerInternal mUserManagerInternal;
    public int mCurrentPhase = -1;
    @GuardedBy({"mTargetUsers"})
    public final SparseArray<SystemService.TargetUser> mTargetUsers = new SparseArray<>();
    public List<SystemService> mServices = new ArrayList();
    public Set<String> mServiceClassnames = new ArraySet();
    public final int mNumUserPoolThreads = Math.min(Runtime.getRuntime().availableProcessors(), 3);

    public SystemServiceManager(Context context) {
        this.mContext = context;
    }

    public SystemService startService(String str) {
        return startService(loadClassFromLoader(str, SystemServiceManager.class.getClassLoader()));
    }

    public SystemService startServiceFromJar(String str, String str2) {
        return startService(loadClassFromLoader(str, SystemServerClassLoaderFactory.getOrCreateClassLoader(str2, SystemServiceManager.class.getClassLoader(), isJarInTestApex(str2))));
    }

    public final boolean isJarInTestApex(String str) {
        Path path = Paths.get(str, new String[0]);
        if (path.getNameCount() < 2 || !path.getName(0).toString().equals("apex")) {
            return false;
        }
        try {
            return (this.mContext.getPackageManager().getPackageInfo(ApexManager.getInstance().getActivePackageNameForApexModuleName(path.getName(1).toString()), PackageManager.PackageInfoFlags.of(1073741824L)).applicationInfo.flags & 256) != 0;
        } catch (Exception unused) {
            return false;
        }
    }

    public static Class<SystemService> loadClassFromLoader(String str, ClassLoader classLoader) {
        try {
            return Class.forName(str, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to create service " + str + " from class loader " + classLoader.toString() + ": service class not found, usually indicates that the caller should have called PackageManager.hasSystemFeature() to check whether the feature is available on this device before trying to start the services that implement it. Also ensure that the correct path for the classloader is supplied, if applicable.", e);
        }
    }

    public <T extends SystemService> T startService(Class<T> cls) {
        try {
            String name = cls.getName();
            String str = TAG;
            Slog.i(str, "Starting " + name);
            Trace.traceBegin(524288L, "StartService " + name);
            if (!SystemService.class.isAssignableFrom(cls)) {
                throw new RuntimeException("Failed to create " + name + ": service must extend " + SystemService.class.getName());
            }
            try {
                try {
                    try {
                        T newInstance = cls.getConstructor(Context.class).newInstance(this.mContext);
                        startService(newInstance);
                        return newInstance;
                    } catch (InstantiationException e) {
                        throw new RuntimeException("Failed to create service " + name + ": service could not be instantiated", e);
                    }
                } catch (NoSuchMethodException e2) {
                    throw new RuntimeException("Failed to create service " + name + ": service must have a public constructor with a Context argument", e2);
                }
            } catch (IllegalAccessException e3) {
                throw new RuntimeException("Failed to create service " + name + ": service must have a public constructor with a Context argument", e3);
            } catch (InvocationTargetException e4) {
                throw new RuntimeException("Failed to create service " + name + ": service constructor threw an exception", e4);
            }
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    public void startService(SystemService systemService) {
        String name = systemService.getClass().getName();
        if (this.mServiceClassnames.contains(name)) {
            String str = TAG;
            Slog.i(str, "Not starting an already started service " + name);
            return;
        }
        this.mServiceClassnames.add(name);
        this.mServices.add(systemService);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            systemService.onStart();
            warnIfTooLong(SystemClock.elapsedRealtime() - elapsedRealtime, systemService, "onStart");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to start service " + systemService.getClass().getName() + ": onStart threw an exception", e);
        }
    }

    public void sealStartedServices() {
        this.mServiceClassnames = Collections.emptySet();
        this.mServices = Collections.unmodifiableList(this.mServices);
    }

    public void startBootPhase(TimingsTraceAndSlog timingsTraceAndSlog, int i) {
        if (i <= this.mCurrentPhase) {
            throw new IllegalArgumentException("Next phase must be larger than previous");
        }
        this.mCurrentPhase = i;
        String str = TAG;
        Slog.i(str, "Starting phase " + this.mCurrentPhase);
        try {
            timingsTraceAndSlog.traceBegin("OnBootPhase_" + i);
            int size = this.mServices.size();
            for (int i2 = 0; i2 < size; i2++) {
                SystemService systemService = this.mServices.get(i2);
                long elapsedRealtime = SystemClock.elapsedRealtime();
                timingsTraceAndSlog.traceBegin("OnBootPhase_" + i + "_" + systemService.getClass().getName());
                try {
                    systemService.onBootPhase(this.mCurrentPhase);
                    warnIfTooLong(SystemClock.elapsedRealtime() - elapsedRealtime, systemService, "onBootPhase");
                    timingsTraceAndSlog.traceEnd();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to boot service " + systemService.getClass().getName() + ": onBootPhase threw an exception during phase " + this.mCurrentPhase, e);
                }
            }
            timingsTraceAndSlog.traceEnd();
            if (i == 1000) {
                timingsTraceAndSlog.logDuration("TotalBootTime", SystemClock.uptimeMillis() - this.mRuntimeStartUptime);
                SystemServerInitThreadPool.shutdown();
            }
        } catch (Throwable th) {
            timingsTraceAndSlog.traceEnd();
            throw th;
        }
    }

    public boolean isBootCompleted() {
        return this.mCurrentPhase >= 1000;
    }

    public void updateOtherServicesStartIndex() {
        if (isBootCompleted()) {
            return;
        }
        sOtherServicesStartIndex = this.mServices.size();
    }

    public void preSystemReady() {
        this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
    }

    public final SystemService.TargetUser getTargetUser(int i) {
        SystemService.TargetUser targetUser;
        synchronized (this.mTargetUsers) {
            targetUser = this.mTargetUsers.get(i);
        }
        return targetUser;
    }

    public final SystemService.TargetUser newTargetUser(int i) {
        UserInfo userInfo = this.mUserManagerInternal.getUserInfo(i);
        boolean z = userInfo != null;
        Preconditions.checkState(z, "No UserInfo for " + i);
        return new SystemService.TargetUser(userInfo);
    }

    public void onUserStarting(TimingsTraceAndSlog timingsTraceAndSlog, int i) {
        SystemService.TargetUser newTargetUser = newTargetUser(i);
        synchronized (this.mTargetUsers) {
            if (i == 0) {
                if (this.mTargetUsers.contains(i)) {
                    Slog.e(TAG, "Skipping starting system user twice");
                    return;
                }
            }
            this.mTargetUsers.put(i, newTargetUser);
            EventLog.writeEvent(30082, i);
            onUser(timingsTraceAndSlog, "Start", null, newTargetUser);
        }
    }

    public void onUserUnlocking(int i) {
        EventLog.writeEvent(30084, i);
        onUser("Unlocking", i);
    }

    public void onUserUnlocked(int i) {
        EventLog.writeEvent(30085, i);
        onUser("Unlocked", i);
    }

    public void onUserSwitching(int i, int i2) {
        SystemService.TargetUser targetUser;
        SystemService.TargetUser targetUser2;
        EventLog.writeEvent(30083, Integer.valueOf(i), Integer.valueOf(i2));
        synchronized (this.mTargetUsers) {
            SystemService.TargetUser targetUser3 = this.mCurrentUser;
            if (targetUser3 == null) {
                targetUser = newTargetUser(i);
            } else {
                if (i != targetUser3.getUserIdentifier()) {
                    String str = TAG;
                    Slog.wtf(str, "switchUser(" + i + "," + i2 + "): mCurrentUser is " + this.mCurrentUser + ", it should be " + i);
                }
                targetUser = this.mCurrentUser;
            }
            targetUser2 = getTargetUser(i2);
            this.mCurrentUser = targetUser2;
            boolean z = targetUser2 != null;
            Preconditions.checkState(z, "No TargetUser for " + i2);
        }
        onUser(TimingsTraceAndSlog.newAsyncLog(), "Switch", targetUser, targetUser2);
    }

    public void onUserStopping(int i) {
        EventLog.writeEvent(30086, i);
        onUser("Stop", i);
    }

    public void onUserStopped(int i) {
        EventLog.writeEvent(30087, i);
        onUser("Cleanup", i);
        synchronized (this.mTargetUsers) {
            this.mTargetUsers.remove(i);
        }
    }

    public void onUserCompletedEvent(int i, int i2) {
        SystemService.TargetUser targetUser;
        EventLog.writeEvent(30088, Integer.valueOf(i), Integer.valueOf(i2));
        if (i2 == 0 || (targetUser = getTargetUser(i)) == null) {
            return;
        }
        onUser(TimingsTraceAndSlog.newAsyncLog(), "CompletedEvent", null, targetUser, new SystemService.UserCompletedEventType(i2));
    }

    public final void onUser(String str, int i) {
        SystemService.TargetUser targetUser = getTargetUser(i);
        boolean z = targetUser != null;
        Preconditions.checkState(z, "No TargetUser for " + i);
        onUser(TimingsTraceAndSlog.newAsyncLog(), str, null, targetUser);
    }

    public final void onUser(TimingsTraceAndSlog timingsTraceAndSlog, String str, SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        onUser(timingsTraceAndSlog, str, targetUser, targetUser2, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0223 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0161  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0186  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0193 A[Catch: Exception -> 0x01da, TryCatch #1 {Exception -> 0x01da, blocks: (B:53:0x015d, B:72:0x01de, B:73:0x01f2, B:61:0x018e, B:62:0x0193, B:63:0x01a0, B:64:0x01ac, B:67:0x01c2, B:68:0x01ca, B:69:0x01ce), top: B:93:0x018e }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01a0 A[Catch: Exception -> 0x01da, TryCatch #1 {Exception -> 0x01da, blocks: (B:53:0x015d, B:72:0x01de, B:73:0x01f2, B:61:0x018e, B:62:0x0193, B:63:0x01a0, B:64:0x01ac, B:67:0x01c2, B:68:0x01ca, B:69:0x01ce), top: B:93:0x018e }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x01ac A[Catch: Exception -> 0x01da, TryCatch #1 {Exception -> 0x01da, blocks: (B:53:0x015d, B:72:0x01de, B:73:0x01f2, B:61:0x018e, B:62:0x0193, B:63:0x01a0, B:64:0x01ac, B:67:0x01c2, B:68:0x01ca, B:69:0x01ce), top: B:93:0x018e }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01b8  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01ce A[Catch: Exception -> 0x01da, TryCatch #1 {Exception -> 0x01da, blocks: (B:53:0x015d, B:72:0x01de, B:73:0x01f2, B:61:0x018e, B:62:0x0193, B:63:0x01a0, B:64:0x01ac, B:67:0x01c2, B:68:0x01ca, B:69:0x01ce), top: B:93:0x018e }] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void onUser(TimingsTraceAndSlog timingsTraceAndSlog, String str, SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2, SystemService.UserCompletedEventType userCompletedEventType) {
        String str2;
        SystemService systemService;
        int i;
        int i2;
        ExecutorService executorService;
        char c;
        String str3;
        int userIdentifier = targetUser2.getUserIdentifier();
        timingsTraceAndSlog.traceBegin("ssm." + str + "User-" + userIdentifier);
        String str4 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Calling on");
        sb.append(str);
        sb.append("User ");
        sb.append(userIdentifier);
        if (targetUser != null) {
            str2 = " (from " + targetUser + ")";
        } else {
            str2 = "";
        }
        sb.append(str2);
        Slog.i(str4, sb.toString());
        boolean useThreadPool = useThreadPool(userIdentifier, str);
        ExecutorService newFixedThreadPool = useThreadPool ? Executors.newFixedThreadPool(this.mNumUserPoolThreads) : null;
        int size = this.mServices.size();
        boolean z = false;
        int i3 = 0;
        while (i3 < size) {
            SystemService systemService2 = this.mServices.get(i3);
            String name = systemService2.getClass().getName();
            boolean isUserSupported = systemService2.isUserSupported(targetUser2);
            if (!isUserSupported && targetUser != null) {
                isUserSupported = systemService2.isUserSupported(targetUser);
            }
            if (!isUserSupported) {
                Slog.i(TAG, "Skipping " + str + "User-" + userIdentifier + " on " + name);
                i = i3;
                i2 = size;
                executorService = newFixedThreadPool;
            } else {
                boolean z2 = useThreadPool && useThreadPoolForService(str, i3);
                if (!z2) {
                    timingsTraceAndSlog.traceBegin("ssm.on" + str + "User-" + userIdentifier + "_" + name);
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                try {
                    switch (str.hashCode()) {
                        case -1805606060:
                            if (str.equals("Switch")) {
                                c = 0;
                                switch (c) {
                                    case 0:
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        systemService.onUserSwitching(targetUser, targetUser2);
                                        break;
                                    case 1:
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        if (z2) {
                                            executorService.submit(getOnUserStartingRunnable(timingsTraceAndSlog, systemService, targetUser2));
                                            break;
                                        } else {
                                            systemService.onUserStarting(targetUser2);
                                            break;
                                        }
                                    case 2:
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        systemService.onUserUnlocking(targetUser2);
                                        break;
                                    case 3:
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        systemService.onUserUnlocked(targetUser2);
                                        break;
                                    case 4:
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        systemService.onUserStopping(targetUser2);
                                        break;
                                    case 5:
                                        str3 = name;
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        try {
                                            systemService.onUserStopped(targetUser2);
                                        } catch (Exception e) {
                                            e = e;
                                            name = str3;
                                            logFailure(str, targetUser2, name, e);
                                            if (!z2) {
                                            }
                                            i3 = i + 1;
                                            newFixedThreadPool = executorService;
                                            size = i2;
                                        }
                                        break;
                                    case 6:
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        try {
                                            executorService.submit(getOnUserCompletedEventRunnable(timingsTraceAndSlog, systemService2, name, targetUser2, userCompletedEventType));
                                            systemService = systemService2;
                                        } catch (Exception e2) {
                                            e = e2;
                                            name = name;
                                            systemService = systemService2;
                                            logFailure(str, targetUser2, name, e);
                                            if (!z2) {
                                            }
                                            i3 = i + 1;
                                            newFixedThreadPool = executorService;
                                            size = i2;
                                        }
                                        break;
                                    default:
                                        str3 = name;
                                        systemService = systemService2;
                                        i = i3;
                                        i2 = size;
                                        executorService = newFixedThreadPool;
                                        throw new IllegalArgumentException(str + " what?");
                                        break;
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case -1773539708:
                            if (str.equals("Cleanup")) {
                                c = 5;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case -240492034:
                            if (str.equals("Unlocking")) {
                                c = 2;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case -146305277:
                            if (str.equals("Unlocked")) {
                                c = 3;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case 2587682:
                            if (str.equals("Stop")) {
                                c = 4;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case 80204866:
                            if (str.equals("Start")) {
                                c = 1;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        case 537825071:
                            if (str.equals("CompletedEvent")) {
                                c = 6;
                                switch (c) {
                                }
                            }
                            c = 65535;
                            switch (c) {
                            }
                        default:
                            c = 65535;
                            switch (c) {
                            }
                    }
                } catch (Exception e3) {
                    e = e3;
                    systemService = systemService2;
                    i = i3;
                    i2 = size;
                    executorService = newFixedThreadPool;
                }
                if (!z2) {
                    warnIfTooLong(SystemClock.elapsedRealtime() - elapsedRealtime, systemService, "on" + str + "User-" + userIdentifier);
                    timingsTraceAndSlog.traceEnd();
                }
            }
            i3 = i + 1;
            newFixedThreadPool = executorService;
            size = i2;
        }
        ExecutorService executorService2 = newFixedThreadPool;
        if (useThreadPool) {
            executorService2.shutdown();
            try {
                z = executorService2.awaitTermination(30L, TimeUnit.SECONDS);
            } catch (InterruptedException e4) {
                logFailure(str, targetUser2, "(user lifecycle threadpool was interrupted)", e4);
            }
            if (!z) {
                logFailure(str, targetUser2, "(user lifecycle threadpool was not terminated)", null);
            }
        }
        timingsTraceAndSlog.traceEnd();
    }

    public final boolean useThreadPool(int i, String str) {
        str.hashCode();
        return !str.equals("Start") ? str.equals("CompletedEvent") : (ActivityManager.isLowRamDeviceStatic() || i == 0) ? false : true;
    }

    public final boolean useThreadPoolForService(String str, int i) {
        str.hashCode();
        return !str.equals("Start") ? str.equals("CompletedEvent") : i >= sOtherServicesStartIndex;
    }

    public final Runnable getOnUserStartingRunnable(final TimingsTraceAndSlog timingsTraceAndSlog, final SystemService systemService, final SystemService.TargetUser targetUser) {
        return new Runnable() { // from class: com.android.server.SystemServiceManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SystemServiceManager.this.lambda$getOnUserStartingRunnable$0(timingsTraceAndSlog, systemService, targetUser);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOnUserStartingRunnable$0(TimingsTraceAndSlog timingsTraceAndSlog, SystemService systemService, SystemService.TargetUser targetUser) {
        TimingsTraceAndSlog timingsTraceAndSlog2 = new TimingsTraceAndSlog(timingsTraceAndSlog);
        String name = systemService.getClass().getName();
        int userIdentifier = targetUser.getUserIdentifier();
        timingsTraceAndSlog2.traceBegin("ssm.onStartUser-" + userIdentifier + "_" + name);
        try {
            try {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                systemService.onUserStarting(targetUser);
                long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
                warnIfTooLong(elapsedRealtime2, systemService, "onStartUser-" + userIdentifier);
            } catch (Exception e) {
                logFailure("Start", targetUser, name, e);
            }
        } finally {
            timingsTraceAndSlog2.traceEnd();
        }
    }

    public final Runnable getOnUserCompletedEventRunnable(final TimingsTraceAndSlog timingsTraceAndSlog, final SystemService systemService, final String str, final SystemService.TargetUser targetUser, final SystemService.UserCompletedEventType userCompletedEventType) {
        return new Runnable() { // from class: com.android.server.SystemServiceManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SystemServiceManager.this.lambda$getOnUserCompletedEventRunnable$1(timingsTraceAndSlog, targetUser, userCompletedEventType, str, systemService);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOnUserCompletedEventRunnable$1(TimingsTraceAndSlog timingsTraceAndSlog, SystemService.TargetUser targetUser, SystemService.UserCompletedEventType userCompletedEventType, String str, SystemService systemService) {
        TimingsTraceAndSlog timingsTraceAndSlog2 = new TimingsTraceAndSlog(timingsTraceAndSlog);
        int userIdentifier = targetUser.getUserIdentifier();
        timingsTraceAndSlog2.traceBegin("ssm.onCompletedEventUser-" + userIdentifier + "_" + userCompletedEventType + "_" + str);
        try {
            try {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                systemService.onUserCompletedEvent(targetUser, userCompletedEventType);
                warnIfTooLong(SystemClock.elapsedRealtime() - elapsedRealtime, systemService, "onCompletedEventUser-" + userIdentifier);
            } catch (Exception e) {
                logFailure("CompletedEvent", targetUser, str, e);
                throw e;
            }
        } finally {
            timingsTraceAndSlog2.traceEnd();
        }
    }

    public final void logFailure(String str, SystemService.TargetUser targetUser, String str2, Exception exc) {
        String str3 = TAG;
        Slog.wtf(str3, "SystemService failure: Failure reporting " + str + " of user " + targetUser + " to service " + str2, exc);
    }

    public void setSafeMode(boolean z) {
        this.mSafeMode = z;
    }

    public boolean isSafeMode() {
        return this.mSafeMode;
    }

    public boolean isRuntimeRestarted() {
        return this.mRuntimeRestarted;
    }

    public long getRuntimeStartElapsedTime() {
        return this.mRuntimeStartElapsedTime;
    }

    public long getRuntimeStartUptime() {
        return this.mRuntimeStartUptime;
    }

    public void setStartInfo(boolean z, long j, long j2) {
        this.mRuntimeRestarted = z;
        this.mRuntimeStartElapsedTime = j;
        this.mRuntimeStartUptime = j2;
    }

    public final void warnIfTooLong(long j, SystemService systemService, String str) {
        if (j > 50) {
            String str2 = TAG;
            Slog.w(str2, "Service " + systemService.getClass().getName() + " took " + j + " ms in " + str);
        }
    }

    @Deprecated
    public static File ensureSystemDir() {
        if (sSystemDir == null) {
            File file = new File(Environment.getDataDirectory(), "system");
            sSystemDir = file;
            file.mkdirs();
        }
        return sSystemDir;
    }

    public String getDumpableName() {
        return SystemServiceManager.class.getSimpleName();
    }

    public void dump(PrintWriter printWriter, String[] strArr) {
        int i;
        printWriter.printf("Current phase: %d\n", Integer.valueOf(this.mCurrentPhase));
        synchronized (this.mTargetUsers) {
            if (this.mCurrentUser != null) {
                printWriter.print("Current user: ");
                this.mCurrentUser.dump(printWriter);
                printWriter.println();
            } else {
                printWriter.println("Current user not set!");
            }
            int size = this.mTargetUsers.size();
            if (size > 0) {
                printWriter.printf("%d target users: ", Integer.valueOf(size));
                for (int i2 = 0; i2 < size; i2++) {
                    this.mTargetUsers.valueAt(i2).dump(printWriter);
                    if (i2 != size - 1) {
                        printWriter.print(", ");
                    }
                }
                printWriter.println();
            } else {
                printWriter.println("No target users");
            }
        }
        int size2 = this.mServices.size();
        if (size2 > 0) {
            printWriter.printf("%d started services:\n", Integer.valueOf(size2));
            for (i = 0; i < size2; i++) {
                printWriter.print("  ");
                printWriter.println(this.mServices.get(i).getClass().getCanonicalName());
            }
            return;
        }
        printWriter.println("No started services");
    }
}
