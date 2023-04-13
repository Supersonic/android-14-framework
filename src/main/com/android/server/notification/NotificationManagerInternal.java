package com.android.server.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import java.util.Set;
/* loaded from: classes2.dex */
public interface NotificationManagerInternal {
    boolean areNotificationsEnabledForPackage(String str, int i);

    void cancelNotification(String str, String str2, int i, int i2, String str3, int i3, int i4);

    void cleanupHistoryFiles();

    void enqueueNotification(String str, String str2, int i, int i2, String str3, int i3, Notification notification, int i4);

    NotificationChannel getNotificationChannel(String str, int i, String str2);

    NotificationChannelGroup getNotificationChannelGroup(String str, int i, String str2);

    int getNumNotificationChannelsForPackage(String str, int i, boolean z);

    void onConversationRemoved(String str, int i, Set<String> set);

    void removeForegroundServiceFlagFromNotification(String str, int i, int i2);

    void sendReviewPermissionsNotification();
}
