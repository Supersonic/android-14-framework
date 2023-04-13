package com.android.server.notification;

import android.content.Context;
/* loaded from: classes2.dex */
public class PriorityExtractor implements NotificationSignalExtractor {
    public RankingConfig mConfig;

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        if (notificationRecord == null || notificationRecord.getNotification() == null || this.mConfig == null) {
            return null;
        }
        notificationRecord.setPackagePriority(notificationRecord.getChannel().canBypassDnd() ? 2 : 0);
        return null;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
        this.mConfig = rankingConfig;
    }
}
