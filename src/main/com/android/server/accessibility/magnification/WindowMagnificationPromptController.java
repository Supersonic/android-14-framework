package com.android.server.accessibility.magnification;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
/* loaded from: classes.dex */
public class WindowMagnificationPromptController {
    @VisibleForTesting
    static final String ACTION_DISMISS = "com.android.server.accessibility.magnification.action.DISMISS";
    @VisibleForTesting
    static final String ACTION_TURN_ON_IN_SETTINGS = "com.android.server.accessibility.magnification.action.TURN_ON_IN_SETTINGS";
    public static final Uri MAGNIFICATION_WINDOW_MODE_PROMPT_URI = Settings.Secure.getUriFor("accessibility_show_window_magnification_prompt");
    public final ContentObserver mContentObserver;
    public final Context mContext;
    public boolean mNeedToShowNotification;
    @VisibleForTesting
    BroadcastReceiver mNotificationActionReceiver;
    public final NotificationManager mNotificationManager;
    public final int mUserId;

    public WindowMagnificationPromptController(Context context, int i) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mUserId = i;
        ContentObserver contentObserver = new ContentObserver(null) { // from class: com.android.server.accessibility.magnification.WindowMagnificationPromptController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                WindowMagnificationPromptController.this.onPromptSettingsValueChanged();
            }
        };
        this.mContentObserver = contentObserver;
        context.getContentResolver().registerContentObserver(MAGNIFICATION_WINDOW_MODE_PROMPT_URI, false, contentObserver, i);
        this.mNeedToShowNotification = isWindowMagnificationPromptEnabled();
    }

    @VisibleForTesting
    public void onPromptSettingsValueChanged() {
        boolean isWindowMagnificationPromptEnabled = isWindowMagnificationPromptEnabled();
        if (this.mNeedToShowNotification == isWindowMagnificationPromptEnabled) {
            return;
        }
        this.mNeedToShowNotification = isWindowMagnificationPromptEnabled;
        if (isWindowMagnificationPromptEnabled) {
            return;
        }
        unregisterReceiverIfNeeded();
        this.mNotificationManager.cancel(1004);
    }

    public void showNotificationIfNeeded() {
        if (this.mNeedToShowNotification) {
            Notification.Builder builder = new Notification.Builder(this.mContext, SystemNotificationChannels.ACCESSIBILITY_MAGNIFICATION);
            String string = this.mContext.getString(17041795);
            builder.setSmallIcon(17302299).setContentTitle(this.mContext.getString(17041796)).setContentText(string).setLargeIcon(Icon.createWithResource(this.mContext, 17302306)).setTicker(this.mContext.getString(17041796)).setOnlyAlertOnce(true).setStyle(new Notification.BigTextStyle().bigText(string)).setDeleteIntent(createPendingIntent(ACTION_DISMISS)).setContentIntent(createPendingIntent(ACTION_TURN_ON_IN_SETTINGS)).setActions(buildTurnOnAction());
            this.mNotificationManager.notify(1004, builder.build());
            registerReceiverIfNeeded();
        }
    }

    @VisibleForTesting
    public void onDestroy() {
        dismissNotification();
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    public final boolean isWindowMagnificationPromptEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_show_window_magnification_prompt", 0, this.mUserId) == 1;
    }

    public final Notification.Action buildTurnOnAction() {
        return new Notification.Action.Builder((Icon) null, this.mContext.getString(17041662), createPendingIntent(ACTION_TURN_ON_IN_SETTINGS)).build();
    }

    public final PendingIntent createPendingIntent(String str) {
        Intent intent = new Intent(str);
        intent.setPackage(this.mContext.getPackageName());
        return PendingIntent.getBroadcast(this.mContext, 0, intent, 67108864);
    }

    public final void registerReceiverIfNeeded() {
        if (this.mNotificationActionReceiver != null) {
            return;
        }
        this.mNotificationActionReceiver = new NotificationActionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DISMISS);
        intentFilter.addAction(ACTION_TURN_ON_IN_SETTINGS);
        this.mContext.registerReceiver(this.mNotificationActionReceiver, intentFilter, "android.permission.MANAGE_ACCESSIBILITY", null, 2);
    }

    public final void launchMagnificationSettings() {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS");
        intent.addFlags(268468224);
        intent.putExtra("android.intent.extra.COMPONENT_NAME", AccessibilityShortcutController.MAGNIFICATION_COMPONENT_NAME.flattenToShortString());
        intent.addFlags(268435456);
        this.mContext.startActivityAsUser(intent, ActivityOptions.makeBasic().setLaunchDisplayId(this.mContext.getDisplayId()).toBundle(), UserHandle.of(this.mUserId));
        ((StatusBarManager) this.mContext.getSystemService(StatusBarManager.class)).collapsePanels();
    }

    public final void dismissNotification() {
        unregisterReceiverIfNeeded();
        this.mNotificationManager.cancel(1004);
    }

    public final void unregisterReceiverIfNeeded() {
        BroadcastReceiver broadcastReceiver = this.mNotificationActionReceiver;
        if (broadcastReceiver == null) {
            return;
        }
        this.mContext.unregisterReceiver(broadcastReceiver);
        this.mNotificationActionReceiver = null;
    }

    /* loaded from: classes.dex */
    public class NotificationActionReceiver extends BroadcastReceiver {
        public NotificationActionReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            WindowMagnificationPromptController.this.mNeedToShowNotification = false;
            Settings.Secure.putIntForUser(WindowMagnificationPromptController.this.mContext.getContentResolver(), "accessibility_show_window_magnification_prompt", 0, WindowMagnificationPromptController.this.mUserId);
            if (WindowMagnificationPromptController.ACTION_TURN_ON_IN_SETTINGS.equals(action)) {
                WindowMagnificationPromptController.this.launchMagnificationSettings();
                WindowMagnificationPromptController.this.dismissNotification();
            } else if (WindowMagnificationPromptController.ACTION_DISMISS.equals(action)) {
                WindowMagnificationPromptController.this.dismissNotification();
            }
        }
    }
}
