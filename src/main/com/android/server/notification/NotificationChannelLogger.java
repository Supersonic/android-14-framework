package com.android.server.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes2.dex */
public interface NotificationChannelLogger {
    static int getImportance(boolean z) {
        return z ? 0 : 3;
    }

    void logAppEvent(NotificationChannelEvent notificationChannelEvent, int i, String str);

    void logNotificationChannel(NotificationChannelEvent notificationChannelEvent, NotificationChannel notificationChannel, int i, String str, int i2, int i3);

    void logNotificationChannelGroup(NotificationChannelEvent notificationChannelEvent, NotificationChannelGroup notificationChannelGroup, int i, String str, boolean z);

    default void logNotificationChannelCreated(NotificationChannel notificationChannel, int i, String str) {
        logNotificationChannel(NotificationChannelEvent.getCreated(notificationChannel), notificationChannel, i, str, 0, getLoggingImportance(notificationChannel));
    }

    default void logNotificationChannelDeleted(NotificationChannel notificationChannel, int i, String str) {
        logNotificationChannel(NotificationChannelEvent.getDeleted(notificationChannel), notificationChannel, i, str, getLoggingImportance(notificationChannel), 0);
    }

    default void logNotificationChannelModified(NotificationChannel notificationChannel, int i, String str, int i2, boolean z) {
        logNotificationChannel(NotificationChannelEvent.getUpdated(z), notificationChannel, i, str, i2, getLoggingImportance(notificationChannel));
    }

    default void logNotificationChannelGroup(NotificationChannelGroup notificationChannelGroup, int i, String str, boolean z, boolean z2) {
        logNotificationChannelGroup(NotificationChannelEvent.getGroupUpdated(z), notificationChannelGroup, i, str, z2);
    }

    default void logNotificationChannelGroupDeleted(NotificationChannelGroup notificationChannelGroup, int i, String str) {
        logNotificationChannelGroup(NotificationChannelEvent.NOTIFICATION_CHANNEL_GROUP_DELETED, notificationChannelGroup, i, str, false);
    }

    default void logAppNotificationsAllowed(int i, String str, boolean z) {
        logAppEvent(NotificationChannelEvent.getBlocked(z), i, str);
    }

    /* loaded from: classes2.dex */
    public enum NotificationChannelEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_CHANNEL_CREATED(219),
        NOTIFICATION_CHANNEL_UPDATED(220),
        NOTIFICATION_CHANNEL_UPDATED_BY_USER(221),
        NOTIFICATION_CHANNEL_DELETED(222),
        NOTIFICATION_CHANNEL_GROUP_CREATED(FrameworkStatsLog.EXCLUSION_RECT_STATE_CHANGED),
        NOTIFICATION_CHANNEL_GROUP_UPDATED(224),
        NOTIFICATION_CHANNEL_GROUP_DELETED(226),
        NOTIFICATION_CHANNEL_CONVERSATION_CREATED(272),
        NOTIFICATION_CHANNEL_CONVERSATION_DELETED(274),
        APP_NOTIFICATIONS_BLOCKED(FrameworkStatsLog.BEDTIME_MODE_STATE_CHANGED),
        APP_NOTIFICATIONS_UNBLOCKED(558);
        
        private final int mId;

        NotificationChannelEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        public static NotificationChannelEvent getUpdated(boolean z) {
            if (z) {
                return NOTIFICATION_CHANNEL_UPDATED_BY_USER;
            }
            return NOTIFICATION_CHANNEL_UPDATED;
        }

        public static NotificationChannelEvent getCreated(NotificationChannel notificationChannel) {
            if (notificationChannel.getConversationId() != null) {
                return NOTIFICATION_CHANNEL_CONVERSATION_CREATED;
            }
            return NOTIFICATION_CHANNEL_CREATED;
        }

        public static NotificationChannelEvent getDeleted(NotificationChannel notificationChannel) {
            if (notificationChannel.getConversationId() != null) {
                return NOTIFICATION_CHANNEL_CONVERSATION_DELETED;
            }
            return NOTIFICATION_CHANNEL_DELETED;
        }

        public static NotificationChannelEvent getGroupUpdated(boolean z) {
            if (z) {
                return NOTIFICATION_CHANNEL_GROUP_CREATED;
            }
            return NOTIFICATION_CHANNEL_GROUP_DELETED;
        }

        public static NotificationChannelEvent getBlocked(boolean z) {
            return z ? APP_NOTIFICATIONS_UNBLOCKED : APP_NOTIFICATIONS_BLOCKED;
        }
    }

    static int getIdHash(NotificationChannel notificationChannel) {
        return SmallHash.hash(notificationChannel.getId());
    }

    static int getConversationIdHash(NotificationChannel notificationChannel) {
        return SmallHash.hash(notificationChannel.getConversationId());
    }

    static int getIdHash(NotificationChannelGroup notificationChannelGroup) {
        return SmallHash.hash(notificationChannelGroup.getId());
    }

    static int getLoggingImportance(NotificationChannel notificationChannel) {
        return getLoggingImportance(notificationChannel, notificationChannel.getImportance());
    }

    static int getLoggingImportance(NotificationChannel notificationChannel, int i) {
        if (notificationChannel.getConversationId() == null || i < 4 || !notificationChannel.isImportantConversation()) {
            return i;
        }
        return 5;
    }

    static int getImportance(NotificationChannelGroup notificationChannelGroup) {
        return getImportance(notificationChannelGroup.isBlocked());
    }
}
