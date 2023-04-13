package com.android.server.notification;

import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.notification.NotificationRecordLogger;
/* loaded from: classes2.dex */
public class NotificationRecordLoggerImpl implements NotificationRecordLogger {
    public UiEventLogger mUiEventLogger = new UiEventLoggerImpl();

    @Override // com.android.server.notification.NotificationRecordLogger
    public void maybeLogNotificationPosted(NotificationRecord notificationRecord, NotificationRecord notificationRecord2, int i, int i2, InstanceId instanceId) {
        NotificationRecordLogger.NotificationRecordPair notificationRecordPair = new NotificationRecordLogger.NotificationRecordPair(notificationRecord, notificationRecord2);
        if (notificationRecordPair.shouldLogReported(i2)) {
            writeNotificationReportedAtom(notificationRecordPair, NotificationRecordLogger.NotificationReportedEvent.fromRecordPair(notificationRecordPair), i, i2, instanceId);
        }
    }

    @Override // com.android.server.notification.NotificationRecordLogger
    public void logNotificationAdjusted(NotificationRecord notificationRecord, int i, int i2, InstanceId instanceId) {
        writeNotificationReportedAtom(new NotificationRecordLogger.NotificationRecordPair(notificationRecord, null), NotificationRecordLogger.NotificationReportedEvent.NOTIFICATION_ADJUSTED, i, i2, instanceId);
    }

    public final void writeNotificationReportedAtom(NotificationRecordLogger.NotificationRecordPair notificationRecordPair, NotificationRecordLogger.NotificationReportedEvent notificationReportedEvent, int i, int i2, InstanceId instanceId) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.NOTIFICATION_REPORTED, notificationReportedEvent.getId(), notificationRecordPair.f1151r.getUid(), notificationRecordPair.f1151r.getSbn().getPackageName(), notificationRecordPair.getInstanceId(), notificationRecordPair.getNotificationIdHash(), notificationRecordPair.getChannelIdHash(), notificationRecordPair.getGroupIdHash(), instanceId == null ? 0 : instanceId.getId(), notificationRecordPair.f1151r.getSbn().getNotification().isGroupSummary(), notificationRecordPair.f1151r.getSbn().getNotification().category, notificationRecordPair.getStyle(), notificationRecordPair.getNumPeople(), i, NotificationRecordLogger.getLoggingImportance(notificationRecordPair.f1151r), i2, notificationRecordPair.f1151r.getImportanceExplanationCode(), notificationRecordPair.f1151r.getInitialImportance(), notificationRecordPair.f1151r.getInitialImportanceExplanationCode(), notificationRecordPair.f1151r.getAssistantImportance(), notificationRecordPair.getAssistantHash(), notificationRecordPair.f1151r.getRankingScore(), notificationRecordPair.f1151r.getSbn().isOngoing(), NotificationRecordLogger.isForegroundService(notificationRecordPair.f1151r), notificationRecordPair.f1151r.getSbn().getNotification().getTimeoutAfter(), NotificationRecordLogger.isNonDismissible(notificationRecordPair.f1151r));
    }

    @Override // com.android.server.notification.NotificationRecordLogger
    public void log(UiEventLogger.UiEventEnum uiEventEnum, NotificationRecord notificationRecord) {
        if (notificationRecord == null) {
            return;
        }
        this.mUiEventLogger.logWithInstanceId(uiEventEnum, notificationRecord.getUid(), notificationRecord.getSbn().getPackageName(), notificationRecord.getSbn().getInstanceId());
    }

    @Override // com.android.server.notification.NotificationRecordLogger
    public void log(UiEventLogger.UiEventEnum uiEventEnum) {
        this.mUiEventLogger.log(uiEventEnum);
    }
}
