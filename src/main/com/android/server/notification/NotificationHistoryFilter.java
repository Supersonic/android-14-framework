package com.android.server.notification;

import android.app.NotificationHistory;
import android.text.TextUtils;
/* loaded from: classes2.dex */
public final class NotificationHistoryFilter {
    public String mChannel;
    public int mNotificationCount;
    public String mPackage;

    public NotificationHistoryFilter() {
    }

    public String getPackage() {
        return this.mPackage;
    }

    public String getChannel() {
        return this.mChannel;
    }

    public boolean isFiltering() {
        return (getPackage() == null && getChannel() == null && this.mNotificationCount >= Integer.MAX_VALUE) ? false : true;
    }

    public boolean matchesPackageAndChannelFilter(NotificationHistory.HistoricalNotification historicalNotification) {
        if (TextUtils.isEmpty(getPackage())) {
            return true;
        }
        if (getPackage().equals(historicalNotification.getPackage())) {
            return TextUtils.isEmpty(getChannel()) || getChannel().equals(historicalNotification.getChannelId());
        }
        return false;
    }

    public boolean matchesCountFilter(NotificationHistory notificationHistory) {
        return notificationHistory.getHistoryCount() < this.mNotificationCount;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        public String mPackage = null;
        public String mChannel = null;
        public int mNotificationCount = Integer.MAX_VALUE;

        public NotificationHistoryFilter build() {
            NotificationHistoryFilter notificationHistoryFilter = new NotificationHistoryFilter();
            notificationHistoryFilter.mPackage = this.mPackage;
            notificationHistoryFilter.mChannel = this.mChannel;
            notificationHistoryFilter.mNotificationCount = this.mNotificationCount;
            return notificationHistoryFilter;
        }
    }
}
