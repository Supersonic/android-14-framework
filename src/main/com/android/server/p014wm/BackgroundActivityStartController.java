package com.android.server.p014wm;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.BackgroundStartPrivileges;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.Slog;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p006am.PendingIntentRecord;
/* renamed from: com.android.server.wm.BackgroundActivityStartController */
/* loaded from: classes2.dex */
public class BackgroundActivityStartController {
    public final ActivityTaskManagerService mService;
    public final ActivityTaskSupervisor mSupervisor;

    public static String balCodeToString(int i) {
        switch (i) {
            case 0:
                return "BAL_BLOCK";
            case 1:
                return "BAL_ALLOW_DEFAULT";
            case 2:
                return "BAL_ALLOW_ALLOWLISTED_UID";
            case 3:
                return "BAL_ALLOW_ALLOWLISTED_COMPONENT";
            case 4:
                return "BAL_ALLOW_VISIBLE_WINDOW";
            case 5:
                return "BAL_ALLOW_PENDING_INTENT";
            case 6:
                return "BAL_ALLOW_PERMISSION";
            case 7:
                return "BAL_ALLOW_SAW_PERMISSION";
            case 8:
                return "BAL_ALLOW_GRACE_PERIOD";
            case 9:
                return "BAL_ALLOW_FOREGROUND";
            case 10:
                return "BAL_ALLOW_SDK_SANDBOX";
            default:
                throw new IllegalArgumentException("Unexpected value: " + i);
        }
    }

    public BackgroundActivityStartController(ActivityTaskManagerService activityTaskManagerService, ActivityTaskSupervisor activityTaskSupervisor) {
        this.mService = activityTaskManagerService;
        this.mSupervisor = activityTaskSupervisor;
    }

    public final boolean isHomeApp(int i, String str) {
        if (this.mService.mHomeProcess != null) {
            return i == this.mService.mHomeProcess.mUid;
        } else if (str == null) {
            return false;
        } else {
            ComponentName defaultHomeActivity = this.mService.getPackageManagerInternalLocked().getDefaultHomeActivity(UserHandle.getUserId(i));
            return defaultHomeActivity != null && str.equals(defaultHomeActivity.getPackageName());
        }
    }

    public boolean shouldAbortBackgroundActivityStart(int i, int i2, String str, int i3, int i4, WindowProcessController windowProcessController, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges, Intent intent, ActivityOptions activityOptions) {
        return checkBackgroundActivityStart(i, i2, str, i3, i4, windowProcessController, pendingIntentRecord, backgroundStartPrivileges, intent, activityOptions) == 0;
    }

    public int checkBackgroundActivityStart(int i, int i2, String str, int i3, int i4, WindowProcessController windowProcessController, PendingIntentRecord pendingIntentRecord, BackgroundStartPrivileges backgroundStartPrivileges, Intent intent, ActivityOptions activityOptions) {
        boolean z;
        boolean z2;
        int i5;
        boolean z3;
        WindowProcessController windowProcessController2;
        int i6;
        int appId = UserHandle.getAppId(i);
        boolean z4 = pendingIntentRecord == null || activityOptions == null || activityOptions.getPendingIntentCreatorBackgroundActivityStartMode() != 2;
        if (z4) {
            if (i == 0 || appId == 1000 || appId == 1027) {
                return logStartAllowedAndReturnCode(2, false, i, i3, intent, "Important callingUid");
            }
            if (isHomeApp(i, str)) {
                return logStartAllowedAndReturnCode(3, false, i, i3, intent, "Home app");
            }
            WindowState currentInputMethodWindow = this.mService.mRootWindowContainer.getCurrentInputMethodWindow();
            if (currentInputMethodWindow != null && appId == currentInputMethodWindow.mOwnerUid) {
                return logStartAllowedAndReturnCode(3, false, i, i3, intent, "Active ime");
            }
        }
        int balAppSwitchesState = this.mService.getBalAppSwitchesState();
        int uidState = this.mService.mActiveUids.getUidState(i);
        boolean hasActiveVisibleWindow = this.mService.hasActiveVisibleWindow(i);
        boolean z5 = hasActiveVisibleWindow || uidState == 2 || uidState == 3;
        boolean z6 = uidState <= 1;
        boolean z7 = (((balAppSwitchesState == 2 || balAppSwitchesState == 1) || this.mService.mActiveUids.hasNonAppVisibleWindow(i)) && hasActiveVisibleWindow) || z6;
        if (z4 && z7) {
            return logStartAllowedAndReturnCode(4, false, i, i3, intent, "callingUidHasAnyVisibleWindow = " + i + ", isCallingUidPersistentSystemProcess = " + z6);
        }
        int uidState2 = i == i3 ? uidState : this.mService.mActiveUids.getUidState(i3);
        boolean hasActiveVisibleWindow2 = i == i3 ? hasActiveVisibleWindow : this.mService.hasActiveVisibleWindow(i3);
        boolean z8 = i == i3 ? z5 : hasActiveVisibleWindow2 || uidState2 == 2;
        int appId2 = UserHandle.getAppId(i3);
        if (i == i3) {
            z = z8;
            z2 = z6;
        } else {
            z = z8;
            z2 = appId2 == 1000 || uidState2 <= 1;
        }
        if (Process.isSdkSandboxUid(i3)) {
            i5 = uidState2;
            if (this.mService.hasActiveVisibleWindow(Process.getAppUidForSdkSandboxUid(UserHandle.getAppId(i3)))) {
                return logStartAllowedAndReturnCode(10, false, i, i3, intent, "uid in SDK sandbox has visible (non-toast) window");
            }
        } else {
            i5 = uidState2;
        }
        BackgroundStartPrivileges backgroundStartPrivilegesAllowedByCaller = PendingIntentRecord.getBackgroundStartPrivilegesAllowedByCaller(activityOptions, i3);
        if (!backgroundStartPrivilegesAllowedByCaller.allowsBackgroundActivityStarts() || i3 == i) {
            z3 = z6;
        } else {
            if (PendingIntentRecord.isPendingIntentBalAllowedByPermission(activityOptions)) {
                z3 = z6;
                if (ActivityManager.checkComponentPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i3, -1, true) == 0) {
                    return logStartAllowedAndReturnCode(5, false, i, i3, intent, "realCallingUid has BAL permission. realCallingUid: " + i3);
                }
            } else {
                z3 = z6;
            }
            if (hasActiveVisibleWindow2) {
                return logStartAllowedAndReturnCode(5, false, i, i3, intent, "realCallingUid has visible (non-toast) window. realCallingUid: " + i3);
            } else if (z2 && backgroundStartPrivileges.allowsBackgroundActivityStarts()) {
                return logStartAllowedAndReturnCode(5, false, i, i3, intent, "realCallingUid is persistent system process AND intent sender allowed (allowBackgroundActivityStart = true). realCallingUid: " + i3);
            } else if (this.mService.isAssociatedCompanionApp(UserHandle.getUserId(i3), i3)) {
                return logStartAllowedAndReturnCode(5, false, i, i3, intent, "realCallingUid is a companion app. realCallingUid: " + i3);
            }
        }
        if (z4) {
            if (ActivityTaskManagerService.checkPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i2, i) == 0) {
                return logStartAllowedAndReturnCode(6, true, i, i3, intent, "START_ACTIVITIES_FROM_BACKGROUND permission granted");
            }
            if (this.mSupervisor.mRecentTasks.isCallerRecents(i)) {
                return logStartAllowedAndReturnCode(3, true, i, i3, intent, "Recents Component");
            }
            if (this.mService.isDeviceOwner(i)) {
                return logStartAllowedAndReturnCode(3, true, i, i3, intent, "Device Owner");
            }
            if (this.mService.isAssociatedCompanionApp(UserHandle.getUserId(i), i)) {
                return logStartAllowedAndReturnCode(3, true, i, i3, intent, "Companion App");
            }
            if (this.mService.hasSystemAlertWindowPermission(i, i2, str)) {
                Slog.w("ActivityTaskManager", "Background activity start for " + str + " allowed because SYSTEM_ALERT_WINDOW permission is granted.");
                return logStartAllowedAndReturnCode(7, true, i, i3, intent, "SYSTEM_ALERT_WINDOW permission is granted");
            } else if (isSystemExemptFlagEnabled() && this.mService.getAppOpsManager().checkOpNoThrow(130, i, str) == 0) {
                return logStartAllowedAndReturnCode(6, true, i, i3, intent, "OP_SYSTEM_EXEMPT_FROM_ACTIVITY_BG_START_RESTRICTION appop is granted");
            }
        }
        if (windowProcessController == null && backgroundStartPrivilegesAllowedByCaller.allowsBackgroundActivityStarts()) {
            windowProcessController2 = this.mService.getProcessController(i4, i3);
            i6 = i3;
        } else {
            windowProcessController2 = windowProcessController;
            i6 = i;
        }
        if (windowProcessController2 != null && z4) {
            int areBackgroundActivityStartsAllowed = windowProcessController2.areBackgroundActivityStartsAllowed(balAppSwitchesState);
            if (areBackgroundActivityStartsAllowed != 0) {
                return logStartAllowedAndReturnCode(areBackgroundActivityStartsAllowed, true, i, i3, intent, "callerApp process (pid = " + windowProcessController2.getPid() + ", uid = " + i6 + ") is allowed");
            }
            ArraySet<WindowProcessController> processes = this.mService.mProcessMap.getProcesses(i6);
            if (processes != null) {
                for (int size = processes.size() - 1; size >= 0; size--) {
                    WindowProcessController valueAt = processes.valueAt(size);
                    int areBackgroundActivityStartsAllowed2 = valueAt.areBackgroundActivityStartsAllowed(balAppSwitchesState);
                    if (valueAt != windowProcessController2 && areBackgroundActivityStartsAllowed2 != 0) {
                        return logStartAllowedAndReturnCode(areBackgroundActivityStartsAllowed2, true, i, i3, intent, "process" + valueAt.getPid() + " from uid " + i6 + " is allowed");
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Background activity start [callingPackage: ");
        sb.append(str);
        sb.append("; callingUid: ");
        sb.append(i);
        sb.append("; appSwitchState: ");
        sb.append(balAppSwitchesState);
        sb.append("; isCallingUidForeground: ");
        sb.append(z5);
        sb.append("; callingUidHasAnyVisibleWindow: ");
        sb.append(hasActiveVisibleWindow);
        sb.append("; callingUidProcState: ");
        sb.append(DebugUtils.valueToString(ActivityManager.class, "PROCESS_STATE_", uidState));
        sb.append("; isCallingUidPersistentSystemProcess: ");
        sb.append(z3);
        sb.append("; realCallingUid: ");
        sb.append(i3);
        sb.append("; isRealCallingUidForeground: ");
        sb.append(z);
        sb.append("; realCallingUidHasAnyVisibleWindow: ");
        sb.append(hasActiveVisibleWindow2);
        sb.append("; realCallingUidProcState: ");
        int i7 = i5;
        sb.append(DebugUtils.valueToString(ActivityManager.class, "PROCESS_STATE_", i7));
        sb.append("; isRealCallingUidPersistentSystemProcess: ");
        sb.append(z2);
        sb.append("; originatingPendingIntent: ");
        sb.append(pendingIntentRecord);
        sb.append("; backgroundStartPrivileges: ");
        sb.append(backgroundStartPrivileges);
        sb.append("; intent: ");
        sb.append(intent);
        sb.append("; callerApp: ");
        sb.append(windowProcessController2);
        sb.append("; inVisibleTask: ");
        sb.append(windowProcessController2 != null && windowProcessController2.hasActivityInVisibleTask());
        sb.append("]");
        Slog.w("ActivityTaskManager", sb.toString());
        if (this.mService.isActivityStartsLoggingEnabled()) {
            this.mSupervisor.getActivityMetricsLogger().logAbortedBgActivityStart(intent, windowProcessController2, i, str, uidState, hasActiveVisibleWindow, i3, i7, hasActiveVisibleWindow2, pendingIntentRecord != null);
            return 0;
        }
        return 0;
    }

    public static int logStartAllowedAndReturnCode(int i, boolean z, int i2, int i3, Intent intent, int i4, String str) {
        return logStartAllowedAndReturnCode(i, z, i2, i3, intent, "");
    }

    public static int logStartAllowedAndReturnCode(int i, boolean z, int i2, int i3, Intent intent, String str) {
        statsLogBalAllowed(i, i2, i3, intent);
        return i;
    }

    public static boolean isSystemExemptFlagEnabled() {
        return DeviceConfig.getBoolean("window_manager", "system_exempt_from_activity_bg_start_restriction_enabled", true);
    }

    public static void statsLogBalAllowed(int i, int i2, int i3, Intent intent) {
        if (i == 5 && (i2 == 1000 || i3 == 1000)) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.BAL_ALLOWED, intent != null ? intent.getComponent().flattenToShortString() : "", i, i2, i3);
        }
        if (i == 6 || i == 9 || i == 7) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.BAL_ALLOWED, "", i, i2, i3);
        }
    }
}
