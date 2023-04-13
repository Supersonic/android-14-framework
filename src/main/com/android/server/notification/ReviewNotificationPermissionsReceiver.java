package com.android.server.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
/* loaded from: classes2.dex */
public class ReviewNotificationPermissionsReceiver extends BroadcastReceiver {
    public static final boolean DEBUG = Log.isLoggable("ReviewNotifPermissions", 3);

    public static IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REVIEW_NOTIF_ACTION_REMIND");
        intentFilter.addAction("REVIEW_NOTIF_ACTION_DISMISS");
        intentFilter.addAction("REVIEW_NOTIF_ACTION_CANCELED");
        return intentFilter;
    }

    @VisibleForTesting
    public void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.cancel("NotificationService", 71);
        } else {
            Slog.w("ReviewNotifPermissions", "could not cancel notification: NotificationManager not found");
        }
    }

    @VisibleForTesting
    public void rescheduleNotification(Context context) {
        ReviewNotificationPermissionsJobService.scheduleJob(context, 604800000L);
        if (DEBUG) {
            Slog.d("ReviewNotifPermissions", "Scheduled review permissions notification for on or after: " + LocalDateTime.now(ZoneId.systemDefault()).plus(604800000L, (TemporalUnit) ChronoUnit.MILLIS));
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("REVIEW_NOTIF_ACTION_REMIND")) {
            rescheduleNotification(context);
            Settings.Global.putInt(context.getContentResolver(), "review_permissions_notification_state", 1);
            cancelNotification(context);
        } else if (action.equals("REVIEW_NOTIF_ACTION_DISMISS")) {
            Settings.Global.putInt(context.getContentResolver(), "review_permissions_notification_state", 2);
            cancelNotification(context);
        } else if (action.equals("REVIEW_NOTIF_ACTION_CANCELED")) {
            int i = Settings.Global.getInt(context.getContentResolver(), "review_permissions_notification_state", -1);
            if (i == 0) {
                rescheduleNotification(context);
                Settings.Global.putInt(context.getContentResolver(), "review_permissions_notification_state", 1);
            } else if (i == 3) {
                Settings.Global.putInt(context.getContentResolver(), "review_permissions_notification_state", 1);
            }
        }
    }
}
