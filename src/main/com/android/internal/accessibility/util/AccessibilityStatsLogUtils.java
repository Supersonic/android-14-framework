package com.android.internal.accessibility.util;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes4.dex */
public final class AccessibilityStatsLogUtils {
    private static final int UNKNOWN_STATUS = 0;
    public static int ACCESSIBILITY_PRIVACY_WARNING_STATUS_SHOWN = 1;
    public static int ACCESSIBILITY_PRIVACY_WARNING_STATUS_CLICKED = 2;
    public static int ACCESSIBILITY_PRIVACY_WARNING_STATUS_SERVICE_DISABLED = 3;

    private AccessibilityStatsLogUtils() {
    }

    public static void logAccessibilityShortcutActivated(Context context, ComponentName componentName, int shortcutType) {
        logAccessibilityShortcutActivatedInternal(componentName, convertToLoggingShortcutType(context, shortcutType), 0);
    }

    public static void logAccessibilityShortcutActivated(Context context, ComponentName componentName, int shortcutType, boolean serviceEnabled) {
        logAccessibilityShortcutActivatedInternal(componentName, convertToLoggingShortcutType(context, shortcutType), convertToLoggingServiceStatus(serviceEnabled));
    }

    private static void logAccessibilityShortcutActivatedInternal(ComponentName componentName, int loggingShortcutType, int loggingServiceStatus) {
        FrameworkStatsLog.write(266, componentName.flattenToString(), loggingShortcutType, loggingServiceStatus);
    }

    public static void logMagnificationTripleTap(boolean enabled) {
        FrameworkStatsLog.write(266, AccessibilityShortcutController.MAGNIFICATION_COMPONENT_NAME.flattenToString(), 3, convertToLoggingServiceStatus(enabled));
    }

    public static void logAccessibilityButtonLongPressStatus(ComponentName componentName) {
        FrameworkStatsLog.write(266, componentName.flattenToString(), 4, 0);
    }

    public static void logMagnificationUsageState(int mode, long duration) {
        FrameworkStatsLog.write(345, convertToLoggingMagnificationMode(mode), duration);
    }

    public static void logMagnificationModeWithImeOn(int mode) {
        FrameworkStatsLog.write(346, convertToLoggingMagnificationMode(mode));
    }

    public static void logMagnificationFollowTypingFocusSession(long duration) {
        FrameworkStatsLog.write(453, duration);
    }

    public static void logMagnificationTripleTapAndHoldSession(long duration) {
        FrameworkStatsLog.write(452, duration);
    }

    public static void logNonA11yToolServiceWarningReported(String packageName, int status, long durationMillis) {
        FrameworkStatsLog.write(384, packageName, status, durationMillis);
    }

    private static boolean isAccessibilityFloatingMenuEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_MODE, -1) == 1;
    }

    private static boolean isAccessibilityGestureEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_MODE, -1) == 2;
    }

    private static int convertToLoggingShortcutType(Context context, int shortcutType) {
        switch (shortcutType) {
            case 0:
                if (isAccessibilityFloatingMenuEnabled(context)) {
                    return 5;
                }
                if (isAccessibilityGestureEnabled(context)) {
                    return 6;
                }
                return 1;
            case 1:
                return 2;
            default:
                return 0;
        }
    }

    private static int convertToLoggingServiceStatus(boolean enabled) {
        return enabled ? 1 : 2;
    }

    private static int convertToLoggingMagnificationMode(int mode) {
        switch (mode) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return 0;
        }
    }
}
