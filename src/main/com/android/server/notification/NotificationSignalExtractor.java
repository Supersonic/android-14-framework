package com.android.server.notification;

import android.content.Context;
/* loaded from: classes2.dex */
public interface NotificationSignalExtractor {
    void initialize(Context context, NotificationUsageStats notificationUsageStats);

    RankingReconsideration process(NotificationRecord notificationRecord);

    void setConfig(RankingConfig rankingConfig);

    void setZenHelper(ZenModeHelper zenModeHelper);
}
