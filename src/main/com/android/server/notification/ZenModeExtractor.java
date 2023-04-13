package com.android.server.notification;

import android.content.Context;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes2.dex */
public class ZenModeExtractor implements NotificationSignalExtractor {
    public static final boolean DBG = Log.isLoggable("ZenModeExtractor", 3);
    public ZenModeHelper mZenModeHelper;

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
        if (DBG) {
            Slog.d("ZenModeExtractor", "Initializing  " + getClass().getSimpleName() + ".");
        }
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        if (notificationRecord == null || notificationRecord.getNotification() == null) {
            if (DBG) {
                Slog.d("ZenModeExtractor", "skipping empty notification");
            }
            return null;
        }
        ZenModeHelper zenModeHelper = this.mZenModeHelper;
        if (zenModeHelper == null) {
            if (DBG) {
                Slog.d("ZenModeExtractor", "skipping - no zen info available");
            }
            return null;
        }
        notificationRecord.setIntercepted(zenModeHelper.shouldIntercept(notificationRecord));
        if (notificationRecord.isIntercepted()) {
            notificationRecord.setSuppressedVisualEffects(this.mZenModeHelper.getConsolidatedNotificationPolicy().suppressedVisualEffects);
        } else {
            notificationRecord.setSuppressedVisualEffects(0);
        }
        return null;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
        this.mZenModeHelper = zenModeHelper;
    }
}
