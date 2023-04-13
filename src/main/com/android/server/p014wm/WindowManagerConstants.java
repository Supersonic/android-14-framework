package com.android.server.p014wm;

import android.provider.DeviceConfig;
import android.provider.DeviceConfigInterface;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.WindowManagerConstants */
/* loaded from: classes2.dex */
public final class WindowManagerConstants {
    public final DeviceConfigInterface mDeviceConfig;
    public final WindowManagerGlobalLock mGlobalLock;
    public final DeviceConfig.OnPropertiesChangedListener mListenerAndroid;
    public final DeviceConfig.OnPropertiesChangedListener mListenerWindowManager;
    public boolean mSystemGestureExcludedByPreQStickyImmersive;
    public int mSystemGestureExclusionLimitDp;
    public long mSystemGestureExclusionLogDebounceTimeoutMillis;
    public final Runnable mUpdateSystemGestureExclusionCallback;

    public WindowManagerConstants(final WindowManagerService windowManagerService, DeviceConfigInterface deviceConfigInterface) {
        this(windowManagerService.mGlobalLock, new Runnable() { // from class: com.android.server.wm.WindowManagerConstants$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerConstants.lambda$new$0(WindowManagerService.this);
            }
        }, deviceConfigInterface);
    }

    public static /* synthetic */ void lambda$new$0(WindowManagerService windowManagerService) {
        windowManagerService.mRoot.forAllDisplays(new Consumer() { // from class: com.android.server.wm.WindowManagerConstants$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DisplayContent) obj).updateSystemGestureExclusionLimit();
            }
        });
    }

    @VisibleForTesting
    public WindowManagerConstants(WindowManagerGlobalLock windowManagerGlobalLock, Runnable runnable, DeviceConfigInterface deviceConfigInterface) {
        Objects.requireNonNull(windowManagerGlobalLock);
        this.mGlobalLock = windowManagerGlobalLock;
        Objects.requireNonNull(runnable);
        this.mUpdateSystemGestureExclusionCallback = runnable;
        this.mDeviceConfig = deviceConfigInterface;
        this.mListenerAndroid = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.wm.WindowManagerConstants$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                WindowManagerConstants.this.onAndroidPropertiesChanged(properties);
            }
        };
        this.mListenerWindowManager = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.wm.WindowManagerConstants$$ExternalSyntheticLambda1
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                WindowManagerConstants.this.onWindowPropertiesChanged(properties);
            }
        };
    }

    public void start(Executor executor) {
        this.mDeviceConfig.addOnPropertiesChangedListener(PackageManagerShellCommandDataLoader.PACKAGE, executor, this.mListenerAndroid);
        this.mDeviceConfig.addOnPropertiesChangedListener("window_manager", executor, this.mListenerWindowManager);
        updateSystemGestureExclusionLogDebounceMillis();
        updateSystemGestureExclusionLimitDp();
        updateSystemGestureExcludedByPreQStickyImmersive();
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0053 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void onAndroidPropertiesChanged(DeviceConfig.Properties properties) {
        char c;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                boolean z = false;
                for (String str : properties.getKeyset()) {
                    if (str == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    int hashCode = str.hashCode();
                    if (hashCode != -1271675449) {
                        if (hashCode == 316878247 && str.equals("system_gesture_exclusion_limit_dp")) {
                            c = 0;
                            if (c != 0) {
                                updateSystemGestureExclusionLimitDp();
                            } else if (c == 1) {
                                updateSystemGestureExcludedByPreQStickyImmersive();
                            }
                            z = true;
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                        z = true;
                    } else {
                        if (str.equals("system_gestures_excluded_by_pre_q_sticky_immersive")) {
                            c = 1;
                            if (c != 0) {
                            }
                            z = true;
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                        z = true;
                    }
                }
                if (z) {
                    this.mUpdateSystemGestureExclusionCallback.run();
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x003a A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x0039 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void onWindowPropertiesChanged(DeviceConfig.Properties properties) {
        char c;
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                for (String str : properties.getKeyset()) {
                    if (str == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    if (str.hashCode() == -125834358 && str.equals("system_gesture_exclusion_log_debounce_millis")) {
                        c = 0;
                        if (c != 0) {
                            updateSystemGestureExclusionLogDebounceMillis();
                        }
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void updateSystemGestureExclusionLogDebounceMillis() {
        this.mSystemGestureExclusionLogDebounceTimeoutMillis = this.mDeviceConfig.getLong("window_manager", "system_gesture_exclusion_log_debounce_millis", 0L);
    }

    public final void updateSystemGestureExclusionLimitDp() {
        this.mSystemGestureExclusionLimitDp = Math.max(200, this.mDeviceConfig.getInt(PackageManagerShellCommandDataLoader.PACKAGE, "system_gesture_exclusion_limit_dp", 0));
    }

    public final void updateSystemGestureExcludedByPreQStickyImmersive() {
        this.mSystemGestureExcludedByPreQStickyImmersive = this.mDeviceConfig.getBoolean(PackageManagerShellCommandDataLoader.PACKAGE, "system_gestures_excluded_by_pre_q_sticky_immersive", false);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("WINDOW MANAGER CONSTANTS (dumpsys window constants):");
        printWriter.print("  ");
        printWriter.print("system_gesture_exclusion_log_debounce_millis");
        printWriter.print("=");
        printWriter.println(this.mSystemGestureExclusionLogDebounceTimeoutMillis);
        printWriter.print("  ");
        printWriter.print("system_gesture_exclusion_limit_dp");
        printWriter.print("=");
        printWriter.println(this.mSystemGestureExclusionLimitDp);
        printWriter.print("  ");
        printWriter.print("system_gestures_excluded_by_pre_q_sticky_immersive");
        printWriter.print("=");
        printWriter.println(this.mSystemGestureExcludedByPreQStickyImmersive);
        printWriter.println();
    }
}
