package com.android.server;

import java.util.List;
/* loaded from: classes5.dex */
public class AppWidgetBackupBridge {
    private static WidgetBackupProvider sAppWidgetService;

    public static void register(WidgetBackupProvider instance) {
        sAppWidgetService = instance;
    }

    public static List<String> getWidgetParticipants(int userId) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            return widgetBackupProvider.getWidgetParticipants(userId);
        }
        return null;
    }

    public static byte[] getWidgetState(String packageName, int userId) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            return widgetBackupProvider.getWidgetState(packageName, userId);
        }
        return null;
    }

    public static void systemRestoreStarting(int userId) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.systemRestoreStarting(userId);
        }
    }

    public static void restoreWidgetState(String packageName, byte[] restoredState, int userId) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.restoreWidgetState(packageName, restoredState, userId);
        }
    }

    public static void systemRestoreFinished(int userId) {
        WidgetBackupProvider widgetBackupProvider = sAppWidgetService;
        if (widgetBackupProvider != null) {
            widgetBackupProvider.systemRestoreFinished(userId);
        }
    }
}
