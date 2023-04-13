package com.android.server.notification;

import android.app.NotificationChannel;
import android.os.UserHandle;
/* loaded from: classes2.dex */
public interface RankingConfig {
    boolean badgingEnabled(UserHandle userHandle);

    boolean bubblesEnabled(UserHandle userHandle);

    boolean canShowBadge(String str, int i);

    boolean canShowNotificationsOnLockscreen(int i);

    boolean canShowPrivateNotificationsOnLockScreen(int i);

    int getBubblePreference(String str, int i);

    NotificationChannel getConversationNotificationChannel(String str, int i, String str2, String str3, boolean z, boolean z2);

    boolean isMediaNotificationFilteringEnabled();
}
