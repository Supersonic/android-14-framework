package com.android.server.notification;

import android.app.NotificationChannel;
import android.app.Person;
import android.os.Bundle;
import android.p005os.IInstalld;
import android.service.notification.NotificationListenerService;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.FrameworkStatsLog;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface NotificationRecordLogger {
    void log(UiEventLogger.UiEventEnum uiEventEnum);

    void log(UiEventLogger.UiEventEnum uiEventEnum, NotificationRecord notificationRecord);

    void logNotificationAdjusted(NotificationRecord notificationRecord, int i, int i2, InstanceId instanceId);

    void maybeLogNotificationPosted(NotificationRecord notificationRecord, NotificationRecord notificationRecord2, int i, int i2, InstanceId instanceId);

    default void logNotificationCancelled(NotificationRecord notificationRecord, @NotificationListenerService.NotificationCancelReason int i, int i2) {
        log(NotificationCancelledEvent.fromCancelReason(i, i2), notificationRecord);
    }

    default void logNotificationVisibility(NotificationRecord notificationRecord, boolean z) {
        log(NotificationEvent.fromVisibility(z), notificationRecord);
    }

    /* loaded from: classes2.dex */
    public enum NotificationReportedEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_POSTED(FrameworkStatsLog.f379x24956b9a),
        NOTIFICATION_UPDATED(FrameworkStatsLog.f380x2165d62a),
        NOTIFICATION_ADJUSTED(908);
        
        private final int mId;

        NotificationReportedEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        public static NotificationReportedEvent fromRecordPair(NotificationRecordPair notificationRecordPair) {
            return notificationRecordPair.old != null ? NOTIFICATION_UPDATED : NOTIFICATION_POSTED;
        }
    }

    /* loaded from: classes2.dex */
    public enum NotificationCancelledEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        NOTIFICATION_CANCEL_CLICK(FrameworkStatsLog.f376xd07885aa),
        NOTIFICATION_CANCEL_USER_OTHER(FrameworkStatsLog.f383xde3a78eb),
        NOTIFICATION_CANCEL_USER_CANCEL_ALL(FrameworkStatsLog.f382x8c80549a),
        NOTIFICATION_CANCEL_ERROR(FrameworkStatsLog.f381xaad26533),
        NOTIFICATION_CANCEL_PACKAGE_CHANGED(168),
        NOTIFICATION_CANCEL_USER_STOPPED(169),
        NOTIFICATION_CANCEL_PACKAGE_BANNED(170),
        NOTIFICATION_CANCEL_APP_CANCEL(FrameworkStatsLog.f386xd1d8c3fe),
        NOTIFICATION_CANCEL_APP_CANCEL_ALL(FrameworkStatsLog.f384xd8079e8d),
        NOTIFICATION_CANCEL_LISTENER_CANCEL(173),
        NOTIFICATION_CANCEL_LISTENER_CANCEL_ALL(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_EMPTY_STATE_QUIET_MODE),
        NOTIFICATION_CANCEL_GROUP_SUMMARY_CANCELED(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_LAUNCH_OTHER_APP),
        NOTIFICATION_CANCEL_GROUP_OPTIMIZATION(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_PICK_RESULT),
        NOTIFICATION_CANCEL_PACKAGE_SUSPENDED(177),
        NOTIFICATION_CANCEL_PROFILE_TURNED_OFF(178),
        NOTIFICATION_CANCEL_UNAUTOBUNDLED(FrameworkStatsLog.f373xa546c0ca),
        NOTIFICATION_CANCEL_CHANNEL_BANNED(FrameworkStatsLog.f369xce0f313f),
        NOTIFICATION_CANCEL_SNOOZED(181),
        NOTIFICATION_CANCEL_TIMEOUT(FrameworkStatsLog.f371x936fafd5),
        NOTIFICATION_CANCEL_CHANNEL_REMOVED(1261),
        NOTIFICATION_CANCEL_CLEAR_DATA(1262),
        NOTIFICATION_CANCEL_USER_PEEK(FrameworkStatsLog.f389x36bdcca6),
        NOTIFICATION_CANCEL_USER_AOD(FrameworkStatsLog.f391x3c5de2c3),
        NOTIFICATION_CANCEL_USER_BUBBLE(1228),
        NOTIFICATION_CANCEL_USER_LOCKSCREEN(FrameworkStatsLog.f390xde8506f2),
        NOTIFICATION_CANCEL_USER_SHADE(FrameworkStatsLog.f392xcd34d435),
        NOTIFICATION_CANCEL_ASSISTANT(906);
        
        private final int mId;

        NotificationCancelledEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        public static NotificationCancelledEvent fromCancelReason(@NotificationListenerService.NotificationCancelReason int i, int i2) {
            if (i2 == -1) {
                if (NotificationManagerService.DBG) {
                    throw new IllegalArgumentException("Unexpected surface " + i2);
                }
                return INVALID;
            } else if (i2 == 0) {
                if (1 > i || i > 21) {
                    if (i == 22) {
                        return NOTIFICATION_CANCEL_ASSISTANT;
                    }
                    if (NotificationManagerService.DBG) {
                        throw new IllegalArgumentException("Unexpected cancel reason " + i);
                    }
                    return INVALID;
                }
                return values()[i];
            } else if (i != 2) {
                if (NotificationManagerService.DBG) {
                    throw new IllegalArgumentException("Unexpected cancel with surface " + i);
                }
                return INVALID;
            } else if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 3) {
                        if (i2 != 4) {
                            if (i2 == 5) {
                                return NOTIFICATION_CANCEL_USER_LOCKSCREEN;
                            }
                            if (NotificationManagerService.DBG) {
                                throw new IllegalArgumentException("Unexpected surface for user-dismiss " + i);
                            }
                            return INVALID;
                        }
                        return NOTIFICATION_CANCEL_USER_BUBBLE;
                    }
                    return NOTIFICATION_CANCEL_USER_SHADE;
                }
                return NOTIFICATION_CANCEL_USER_AOD;
            } else {
                return NOTIFICATION_CANCEL_USER_PEEK;
            }
        }
    }

    /* loaded from: classes2.dex */
    public enum NotificationEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_OPEN(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__PLATFORM_PROVISIONING_PARAM),
        NOTIFICATION_CLOSE(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_USB_DATA_SIGNALING),
        NOTIFICATION_SNOOZED(FrameworkStatsLog.f88x54490b7),
        NOTIFICATION_NOT_POSTED_SNOOZED(FrameworkStatsLog.f107x9a09c896),
        NOTIFICATION_CLICKED(320),
        NOTIFICATION_ACTION_CLICKED(321),
        NOTIFICATION_DETAIL_OPEN_SYSTEM(FrameworkStatsLog.TIF_TUNE_CHANGED),
        NOTIFICATION_DETAIL_CLOSE_SYSTEM(FrameworkStatsLog.AUTO_ROTATE_REPORTED),
        NOTIFICATION_DETAIL_OPEN_USER(329),
        NOTIFICATION_DETAIL_CLOSE_USER(330),
        NOTIFICATION_DIRECT_REPLIED(331),
        NOTIFICATION_SMART_REPLIED(332),
        NOTIFICATION_SMART_REPLY_VISIBLE(FrameworkStatsLog.DEVICE_ROTATED),
        NOTIFICATION_ACTION_CLICKED_0(450),
        NOTIFICATION_ACTION_CLICKED_1(FrameworkStatsLog.CDM_ASSOCIATION_ACTION),
        NOTIFICATION_ACTION_CLICKED_2(FrameworkStatsLog.MAGNIFICATION_TRIPLE_TAP_AND_HOLD_ACTIVATED_SESSION_REPORTED),
        NOTIFICATION_CONTEXTUAL_ACTION_CLICKED_0(FrameworkStatsLog.MAGNIFICATION_FOLLOW_TYPING_FOCUS_ACTIVATED_SESSION_REPORTED),
        NOTIFICATION_CONTEXTUAL_ACTION_CLICKED_1(454),
        NOTIFICATION_CONTEXTUAL_ACTION_CLICKED_2(455),
        NOTIFICATION_ASSIST_ACTION_CLICKED_0(456),
        NOTIFICATION_ASSIST_ACTION_CLICKED_1(457),
        NOTIFICATION_ASSIST_ACTION_CLICKED_2(458);
        
        private final int mId;

        NotificationEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        public static NotificationEvent fromVisibility(boolean z) {
            return z ? NOTIFICATION_OPEN : NOTIFICATION_CLOSE;
        }

        public static NotificationEvent fromExpanded(boolean z, boolean z2) {
            return z2 ? z ? NOTIFICATION_DETAIL_OPEN_USER : NOTIFICATION_DETAIL_CLOSE_USER : z ? NOTIFICATION_DETAIL_OPEN_SYSTEM : NOTIFICATION_DETAIL_CLOSE_SYSTEM;
        }

        public static NotificationEvent fromAction(int i, boolean z, boolean z2) {
            if (i < 0 || i > 2) {
                return NOTIFICATION_ACTION_CLICKED;
            }
            if (z) {
                return values()[NOTIFICATION_ASSIST_ACTION_CLICKED_0.ordinal() + i];
            }
            if (z2) {
                return values()[NOTIFICATION_CONTEXTUAL_ACTION_CLICKED_0.ordinal() + i];
            }
            return values()[NOTIFICATION_ACTION_CLICKED_0.ordinal() + i];
        }
    }

    /* loaded from: classes2.dex */
    public enum NotificationPanelEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_PANEL_OPEN(FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_TOP_SLEEPING),
        NOTIFICATION_PANEL_CLOSE(326);
        
        private final int mId;

        NotificationPanelEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    /* loaded from: classes2.dex */
    public static class NotificationRecordPair {
        public final NotificationRecord old;

        /* renamed from: r */
        public final NotificationRecord f1151r;

        public NotificationRecordPair(NotificationRecord notificationRecord, NotificationRecord notificationRecord2) {
            this.f1151r = notificationRecord;
            this.old = notificationRecord2;
        }

        public boolean shouldLogReported(int i) {
            NotificationRecord notificationRecord = this.f1151r;
            if (notificationRecord == null) {
                return false;
            }
            if (this.old == null || i > 0) {
                return true;
            }
            return (Objects.equals(notificationRecord.getSbn().getChannelIdLogTag(), this.old.getSbn().getChannelIdLogTag()) && Objects.equals(this.f1151r.getSbn().getGroupLogTag(), this.old.getSbn().getGroupLogTag()) && this.f1151r.getSbn().getNotification().isGroupSummary() == this.old.getSbn().getNotification().isGroupSummary() && Objects.equals(this.f1151r.getSbn().getNotification().category, this.old.getSbn().getNotification().category) && this.f1151r.getImportance() == this.old.getImportance() && NotificationRecordLogger.getLoggingImportance(this.f1151r) == NotificationRecordLogger.getLoggingImportance(this.old) && this.f1151r.rankingScoreMatches(this.old.getRankingScore())) ? false : true;
        }

        public int getStyle() {
            return getStyle(this.f1151r.getSbn().getNotification().extras);
        }

        public final int getStyle(Bundle bundle) {
            String string;
            if (bundle == null || (string = bundle.getString("android.template")) == null || string.isEmpty()) {
                return 0;
            }
            return string.hashCode();
        }

        public int getNumPeople() {
            return getNumPeople(this.f1151r.getSbn().getNotification().extras);
        }

        public final int getNumPeople(Bundle bundle) {
            ArrayList parcelableArrayList;
            if (bundle == null || (parcelableArrayList = bundle.getParcelableArrayList("android.people.list", Person.class)) == null || parcelableArrayList.isEmpty()) {
                return 0;
            }
            return parcelableArrayList.size();
        }

        public int getAssistantHash() {
            String adjustmentIssuer = this.f1151r.getAdjustmentIssuer();
            if (adjustmentIssuer == null) {
                return 0;
            }
            return adjustmentIssuer.hashCode();
        }

        public int getInstanceId() {
            if (this.f1151r.getSbn().getInstanceId() == null) {
                return 0;
            }
            return this.f1151r.getSbn().getInstanceId().getId();
        }

        public int getNotificationIdHash() {
            return SmallHash.hash(this.f1151r.getSbn().getId() ^ Objects.hashCode(this.f1151r.getSbn().getTag()));
        }

        public int getChannelIdHash() {
            return SmallHash.hash(this.f1151r.getSbn().getNotification().getChannelId());
        }

        public int getGroupIdHash() {
            return SmallHash.hash(this.f1151r.getSbn().getGroup());
        }
    }

    static int getLoggingImportance(NotificationRecord notificationRecord) {
        int importance = notificationRecord.getImportance();
        NotificationChannel channel = notificationRecord.getChannel();
        return channel == null ? importance : NotificationChannelLogger.getLoggingImportance(channel, importance);
    }

    static boolean isForegroundService(NotificationRecord notificationRecord) {
        return (notificationRecord.getSbn() == null || notificationRecord.getSbn().getNotification() == null || (notificationRecord.getSbn().getNotification().flags & 64) == 0) ? false : true;
    }

    static boolean isNonDismissible(NotificationRecord notificationRecord) {
        return (notificationRecord.getSbn() == null || notificationRecord.getSbn().getNotification() == null || (notificationRecord.getNotification().flags & IInstalld.FLAG_FORCE) == 0) ? false : true;
    }
}
