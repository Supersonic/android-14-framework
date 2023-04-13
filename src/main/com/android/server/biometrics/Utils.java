package com.android.server.biometrics;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.PromptInfo;
import android.os.Binder;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Utils {
    public static int authenticatorStatusToBiometricConstant(int i) {
        switch (i) {
            case 1:
                return 0;
            case 2:
            case 4:
                return 12;
            case 3:
            case 6:
            case 8:
            default:
                return 1;
            case 5:
                return 15;
            case 7:
                return 11;
            case 9:
                return 14;
            case 10:
                return 7;
            case 11:
                return 9;
            case 12:
                return 18;
        }
    }

    public static boolean containsFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    public static int getPublicBiometricStrength(@BiometricManager.Authenticators.Types int i) {
        return i & 255;
    }

    public static boolean isConfirmationSupported(int i) {
        return i == 4 || i == 8;
    }

    public static boolean isCredentialRequested(@BiometricManager.Authenticators.Types int i) {
        return (i & 32768) != 0;
    }

    public static int removeBiometricBits(@BiometricManager.Authenticators.Types int i) {
        return i & (-32768);
    }

    public static boolean isDebugEnabled(Context context, int i) {
        if (i == -10000) {
            return false;
        }
        return (Build.IS_ENG || Build.IS_USERDEBUG) && Settings.Secure.getIntForUser(context.getContentResolver(), "biometric_debug_enabled", 0, i) != 0;
    }

    public static boolean isVirtualEnabled(Context context) {
        return Build.isDebuggable() && Settings.Secure.getIntForUser(context.getContentResolver(), "biometric_virtual_enabled", 0, -2) == 1;
    }

    public static List<String> filterAvailableHalInstances(Context context, List<String> list) {
        if (list.size() <= 1) {
            return list;
        }
        int indexOf = list.indexOf("virtual");
        if (isVirtualEnabled(context) && indexOf != -1) {
            return List.of(list.get(indexOf));
        }
        ArrayList arrayList = new ArrayList(list);
        if (indexOf != -1) {
            arrayList.remove(indexOf);
        }
        return arrayList;
    }

    public static void combineAuthenticatorBundles(PromptInfo promptInfo) {
        int i;
        boolean isDeviceCredentialAllowed = promptInfo.isDeviceCredentialAllowed();
        promptInfo.setDeviceCredentialAllowed(false);
        if (promptInfo.getAuthenticators() != 0) {
            i = promptInfo.getAuthenticators();
        } else {
            i = isDeviceCredentialAllowed ? 33023 : 255;
        }
        promptInfo.setAuthenticators(i);
    }

    public static boolean isCredentialRequested(PromptInfo promptInfo) {
        return isCredentialRequested(promptInfo.getAuthenticators());
    }

    public static int getPublicBiometricStrength(PromptInfo promptInfo) {
        return getPublicBiometricStrength(promptInfo.getAuthenticators());
    }

    public static boolean isBiometricRequested(@BiometricManager.Authenticators.Types int i) {
        return getPublicBiometricStrength(i) != 0;
    }

    public static boolean isBiometricRequested(PromptInfo promptInfo) {
        return getPublicBiometricStrength(promptInfo) != 0;
    }

    public static boolean isAtLeastStrength(@BiometricManager.Authenticators.Types int i, @BiometricManager.Authenticators.Types int i2) {
        int i3 = i & 32767;
        if (((~i2) & i3) != 0) {
            return false;
        }
        for (int i4 = 1; i4 <= i2; i4 = (i4 << 1) | 1) {
            if (i4 == i3) {
                return true;
            }
        }
        Slog.e("BiometricService", "Unknown sensorStrength: " + i3 + ", requestedStrength: " + i2);
        return false;
    }

    public static boolean isValidAuthenticatorConfig(PromptInfo promptInfo) {
        return isValidAuthenticatorConfig(promptInfo.getAuthenticators());
    }

    public static boolean isValidAuthenticatorConfig(int i) {
        if (i == 0) {
            return true;
        }
        if (((-65536) & i) != 0) {
            Slog.e("BiometricService", "Non-biometric, non-credential bits found. Authenticators: " + i);
            return false;
        }
        int i2 = i & 32767;
        if ((i2 == 0 && isCredentialRequested(i)) || i2 == 15 || i2 == 255) {
            return true;
        }
        Slog.e("BiometricService", "Unsupported biometric flags. Authenticators: " + i);
        return false;
    }

    public static int biometricConstantsToBiometricManager(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 7 || i == 9) {
                    return 0;
                }
                if (i != 18) {
                    if (i == 11) {
                        return 11;
                    }
                    if (i == 12) {
                        return 12;
                    }
                    if (i == 14) {
                        return 11;
                    }
                    if (i == 15) {
                        return 15;
                    }
                    Slog.e("BiometricService", "Unhandled result code: " + i);
                }
            }
            return 1;
        }
        return 0;
    }

    public static int getAuthenticationTypeForResult(int i) {
        if (i == 1 || i == 4) {
            return 2;
        }
        if (i == 7) {
            return 1;
        }
        throw new IllegalArgumentException("Unsupported dismissal reason: " + i);
    }

    public static boolean listContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public static void checkPermissionOrShell(Context context, String str) {
        if (Binder.getCallingUid() == 2000) {
            return;
        }
        checkPermission(context, str);
    }

    public static void checkPermission(Context context, String str) {
        context.enforceCallingOrSelfPermission(str, "Must have " + str + " permission.");
    }

    public static boolean isCurrentUserOrProfile(Context context, int i) {
        UserManager userManager = UserManager.get(context);
        if (userManager == null) {
            Slog.e("BiometricUtils", "Unable to get UserManager");
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (int i2 : userManager.getEnabledProfileIds(ActivityManager.getCurrentUser())) {
                if (i2 == i) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                }
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static boolean isStrongBiometric(int i) {
        try {
            return isAtLeastStrength(IBiometricService.Stub.asInterface(ServiceManager.getService("biometric")).getCurrentStrength(i), 15);
        } catch (RemoteException e) {
            Slog.e("BiometricUtils", "RemoteException", e);
            return false;
        }
    }

    @BiometricManager.Authenticators.Types
    public static int getCurrentStrength(int i) {
        try {
            return IBiometricService.Stub.asInterface(ServiceManager.getService("biometric")).getCurrentStrength(i);
        } catch (RemoteException e) {
            Slog.e("BiometricUtils", "RemoteException", e);
            return 0;
        }
    }

    public static boolean isKeyguard(Context context, String str) {
        boolean hasInternalPermission = hasInternalPermission(context);
        ComponentName unflattenFromString = ComponentName.unflattenFromString(context.getResources().getString(17039954));
        String packageName = unflattenFromString != null ? unflattenFromString.getPackageName() : null;
        return hasInternalPermission && packageName != null && packageName.equals(str);
    }

    public static boolean isSystem(Context context, String str) {
        return hasInternalPermission(context) && PackageManagerShellCommandDataLoader.PACKAGE.equals(str);
    }

    public static boolean isSettings(Context context, String str) {
        return hasInternalPermission(context) && "com.android.settings".equals(str);
    }

    public static boolean hasInternalPermission(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.USE_BIOMETRIC_INTERNAL") == 0;
    }

    public static String getClientName(BaseClientMonitor baseClientMonitor) {
        return baseClientMonitor != null ? baseClientMonitor.getClass().getSimpleName() : "null";
    }

    public static boolean isUserEncryptedOrLockdown(LockPatternUtils lockPatternUtils, int i) {
        int strongAuthForUser = lockPatternUtils.getStrongAuthForUser(i);
        boolean containsFlag = containsFlag(strongAuthForUser, 1);
        boolean z = containsFlag(strongAuthForUser, 2) || containsFlag(strongAuthForUser, 32);
        Slog.d("BiometricUtils", "isEncrypted: " + containsFlag + " isLockdown: " + z);
        return containsFlag || z;
    }

    public static boolean isForeground(int i, int i2) {
        List runningAppProcesses;
        try {
            runningAppProcesses = ActivityManager.getService().getRunningAppProcesses();
        } catch (RemoteException unused) {
            Slog.w("BiometricUtils", "am.getRunningAppProcesses() failed");
        }
        if (runningAppProcesses == null) {
            Slog.e("BiometricUtils", "No running app processes found, defaulting to true");
            return true;
        }
        for (int i3 = 0; i3 < runningAppProcesses.size(); i3++) {
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = (ActivityManager.RunningAppProcessInfo) runningAppProcesses.get(i3);
            if (runningAppProcessInfo.pid == i2 && runningAppProcessInfo.uid == i && runningAppProcessInfo.importance <= 125) {
                return true;
            }
        }
        return false;
    }

    public static int authenticatorStrengthToPropertyStrength(@BiometricManager.Authenticators.Types int i) {
        if (i != 15) {
            if (i != 255) {
                if (i == 4095) {
                    return 0;
                }
                throw new IllegalArgumentException("Unknown strength: " + i);
            }
            return 1;
        }
        return 2;
    }

    @BiometricManager.Authenticators.Types
    public static int propertyStrengthToAuthenticatorStrength(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return 15;
                }
                throw new IllegalArgumentException("Unknown strength: " + i);
            }
            return 255;
        }
        return 4095;
    }

    public static boolean isBackground(String str) {
        Slog.v("BiometricUtils", "Checking if the authenticating is in background, clientPackage:" + str);
        List<ActivityManager.RunningTaskInfo> tasks = ActivityTaskManager.getInstance().getTasks(Integer.MAX_VALUE);
        if (tasks == null || tasks.isEmpty()) {
            Slog.d("BiometricUtils", "No running tasks reported");
            return true;
        }
        for (ActivityManager.RunningTaskInfo runningTaskInfo : tasks) {
            ComponentName componentName = runningTaskInfo.topActivity;
            if (componentName != null) {
                String packageName = componentName.getPackageName();
                if (packageName.contentEquals(str) && runningTaskInfo.isVisible()) {
                    return false;
                }
                Slog.i("BiometricUtils", "Running task, top: " + packageName + ", isVisible: " + runningTaskInfo.isVisible());
            }
        }
        return true;
    }
}
