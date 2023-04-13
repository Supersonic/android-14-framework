package com.android.server.notification;

import android.content.Context;
/* loaded from: classes2.dex */
public class CriticalNotificationExtractor implements NotificationSignalExtractor {
    public boolean mSupportsCriticalNotifications = false;

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
        this.mSupportsCriticalNotifications = supportsCriticalNotifications(context);
    }

    public final boolean supportsCriticalNotifications(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.type.automotive", 0);
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        if (this.mSupportsCriticalNotifications && notificationRecord != null && notificationRecord.getNotification() != null) {
            if (notificationRecord.isCategory("car_emergency")) {
                notificationRecord.setCriticality(0);
            } else if (notificationRecord.isCategory("car_warning")) {
                notificationRecord.setCriticality(1);
            } else {
                notificationRecord.setCriticality(2);
            }
        }
        return null;
    }
}
