package com.android.server.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.notification.NotificationChannelLogger;
/* loaded from: classes2.dex */
public class NotificationChannelLoggerImpl implements NotificationChannelLogger {
    public UiEventLogger mUiEventLogger = new UiEventLoggerImpl();

    @Override // com.android.server.notification.NotificationChannelLogger
    public void logNotificationChannel(NotificationChannelLogger.NotificationChannelEvent notificationChannelEvent, NotificationChannel notificationChannel, int i, String str, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.NOTIFICATION_CHANNEL_MODIFIED, notificationChannelEvent.getId(), i, str, NotificationChannelLogger.getIdHash(notificationChannel), i2, i3, notificationChannel.isConversation(), NotificationChannelLogger.getConversationIdHash(notificationChannel), notificationChannel.isDemoted(), notificationChannel.isImportantConversation());
    }

    @Override // com.android.server.notification.NotificationChannelLogger
    public void logNotificationChannelGroup(NotificationChannelLogger.NotificationChannelEvent notificationChannelEvent, NotificationChannelGroup notificationChannelGroup, int i, String str, boolean z) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.NOTIFICATION_CHANNEL_MODIFIED, notificationChannelEvent.getId(), i, str, NotificationChannelLogger.getIdHash(notificationChannelGroup), NotificationChannelLogger.getImportance(z), NotificationChannelLogger.getImportance(notificationChannelGroup), false, 0, false, false);
    }

    @Override // com.android.server.notification.NotificationChannelLogger
    public void logAppEvent(NotificationChannelLogger.NotificationChannelEvent notificationChannelEvent, int i, String str) {
        this.mUiEventLogger.log(notificationChannelEvent, i, str);
    }
}
