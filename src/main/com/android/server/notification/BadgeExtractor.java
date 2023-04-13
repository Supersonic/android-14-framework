package com.android.server.notification;

import android.app.Notification;
import android.content.Context;
/* loaded from: classes2.dex */
public class BadgeExtractor implements NotificationSignalExtractor {
    public RankingConfig mConfig;

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        RankingConfig rankingConfig;
        if (notificationRecord == null || notificationRecord.getNotification() == null || (rankingConfig = this.mConfig) == null) {
            return null;
        }
        boolean badgingEnabled = rankingConfig.badgingEnabled(notificationRecord.getSbn().getUser());
        boolean canShowBadge = this.mConfig.canShowBadge(notificationRecord.getSbn().getPackageName(), notificationRecord.getSbn().getUid());
        if (!badgingEnabled || !canShowBadge) {
            notificationRecord.setShowBadge(false);
        } else if (notificationRecord.getChannel() != null) {
            notificationRecord.setShowBadge(notificationRecord.getChannel().canShowBadge() && canShowBadge);
        } else {
            notificationRecord.setShowBadge(canShowBadge);
        }
        if (notificationRecord.isIntercepted() && (notificationRecord.getSuppressedVisualEffects() & 64) != 0) {
            notificationRecord.setShowBadge(false);
        }
        Notification.BubbleMetadata bubbleMetadata = notificationRecord.getNotification().getBubbleMetadata();
        if (bubbleMetadata != null && bubbleMetadata.isNotificationSuppressed()) {
            notificationRecord.setShowBadge(false);
        }
        if (this.mConfig.isMediaNotificationFilteringEnabled() && notificationRecord.getNotification().isMediaNotification()) {
            notificationRecord.setShowBadge(false);
        }
        return null;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
        this.mConfig = rankingConfig;
    }
}
