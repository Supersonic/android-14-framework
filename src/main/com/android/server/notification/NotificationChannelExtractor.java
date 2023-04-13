package com.android.server.notification;

import android.content.Context;
/* loaded from: classes2.dex */
public class NotificationChannelExtractor implements NotificationSignalExtractor {
    public RankingConfig mConfig;
    public Context mContext;

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
        this.mContext = context;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        RankingConfig rankingConfig;
        if (notificationRecord == null || notificationRecord.getNotification() == null || (rankingConfig = this.mConfig) == null) {
            return null;
        }
        notificationRecord.updateNotificationChannel(rankingConfig.getConversationNotificationChannel(notificationRecord.getSbn().getPackageName(), notificationRecord.getSbn().getUid(), notificationRecord.getChannel().getId(), notificationRecord.getSbn().getShortcutId(), true, false));
        return null;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
        this.mConfig = rankingConfig;
    }
}
