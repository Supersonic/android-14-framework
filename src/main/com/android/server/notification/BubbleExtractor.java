package com.android.server.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.p005os.IInstalld;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes2.dex */
public class BubbleExtractor implements NotificationSignalExtractor {
    public ActivityManager mActivityManager;
    public RankingConfig mConfig;
    public Context mContext;
    public ShortcutHelper mShortcutHelper;
    public boolean mSupportsBubble;

    public final void logBubbleError(String str, String str2) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setZenHelper(ZenModeHelper zenModeHelper) {
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void initialize(Context context, NotificationUsageStats notificationUsageStats) {
        this.mContext = context;
        this.mActivityManager = (ActivityManager) context.getSystemService("activity");
        this.mSupportsBubble = Resources.getSystem().getBoolean(17891825);
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public RankingReconsideration process(NotificationRecord notificationRecord) {
        if (notificationRecord == null || notificationRecord.getNotification() == null || this.mConfig == null || this.mShortcutHelper == null) {
            return null;
        }
        boolean z = false;
        boolean z2 = canPresentAsBubble(notificationRecord) && !this.mActivityManager.isLowRamDevice() && notificationRecord.isConversation() && notificationRecord.getShortcutInfo() != null && (notificationRecord.getNotification().flags & 64) == 0;
        boolean bubblesEnabled = this.mConfig.bubblesEnabled(notificationRecord.getUser());
        int bubblePreference = this.mConfig.getBubblePreference(notificationRecord.getSbn().getPackageName(), notificationRecord.getSbn().getUid());
        NotificationChannel channel = notificationRecord.getChannel();
        if (!bubblesEnabled || bubblePreference == 0 || !z2) {
            notificationRecord.setAllowBubble(false);
            if (!z2) {
                notificationRecord.getNotification().setBubbleMetadata(null);
            }
        } else if (channel == null) {
            notificationRecord.setAllowBubble(true);
        } else if (bubblePreference == 1) {
            notificationRecord.setAllowBubble(channel.getAllowBubbles() != 0);
        } else if (bubblePreference == 2) {
            notificationRecord.setAllowBubble(channel.canBubble());
        }
        if (notificationRecord.canBubble() && !notificationRecord.isFlagBubbleRemoved()) {
            z = true;
        }
        if (z) {
            notificationRecord.getNotification().flags |= IInstalld.FLAG_USE_QUOTA;
        } else {
            notificationRecord.getNotification().flags &= -4097;
        }
        return null;
    }

    @Override // com.android.server.notification.NotificationSignalExtractor
    public void setConfig(RankingConfig rankingConfig) {
        this.mConfig = rankingConfig;
    }

    public void setShortcutHelper(ShortcutHelper shortcutHelper) {
        this.mShortcutHelper = shortcutHelper;
    }

    @VisibleForTesting
    public void setActivityManager(ActivityManager activityManager) {
        this.mActivityManager = activityManager;
    }

    @VisibleForTesting
    public boolean canPresentAsBubble(NotificationRecord notificationRecord) {
        boolean z;
        if (this.mSupportsBubble) {
            Notification.BubbleMetadata bubbleMetadata = notificationRecord.getNotification().getBubbleMetadata();
            String packageName = notificationRecord.getSbn().getPackageName();
            if (bubbleMetadata == null) {
                return false;
            }
            String shortcutId = bubbleMetadata.getShortcutId();
            String id = notificationRecord.getShortcutInfo() != null ? notificationRecord.getShortcutInfo().getId() : null;
            if (id != null && shortcutId != null) {
                z = shortcutId.equals(id);
            } else {
                z = (shortcutId == null || this.mShortcutHelper.getValidShortcutInfo(shortcutId, packageName, notificationRecord.getUser()) == null) ? false : true;
            }
            if (bubbleMetadata.getIntent() != null || z) {
                if (z) {
                    return true;
                }
                return canLaunchInTaskView(this.mContext, bubbleMetadata.getIntent(), packageName);
            }
            String key = notificationRecord.getKey();
            logBubbleError(key, "couldn't find valid shortcut for bubble with shortcutId: " + shortcutId);
            return false;
        }
        return false;
    }

    @VisibleForTesting
    public boolean canLaunchInTaskView(Context context, PendingIntent pendingIntent, String str) {
        if (pendingIntent == null) {
            Slog.w("BubbleExtractor", "Unable to create bubble -- no intent");
            return false;
        }
        Intent intent = pendingIntent.getIntent();
        ActivityInfo resolveActivityInfo = intent != null ? intent.resolveActivityInfo(context.getPackageManager(), 0) : null;
        if (resolveActivityInfo == null) {
            FrameworkStatsLog.write(173, str, 1);
            Slog.w("BubbleExtractor", "Unable to send as bubble -- couldn't find activity info for intent: " + intent);
            return false;
        } else if (ActivityInfo.isResizeableMode(resolveActivityInfo.resizeMode)) {
            return true;
        } else {
            FrameworkStatsLog.write(173, str, 2);
            Slog.w("BubbleExtractor", "Unable to send as bubble -- activity is not resizable for intent: " + intent);
            return false;
        }
    }
}
