package com.android.internal.util;

import android.app.Notification;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Looper;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.SparseArray;
import java.util.Collection;
import java.util.Objects;
/* loaded from: classes3.dex */
public class NotificationMessagingUtil {
    private static final String DEFAULT_SMS_APP_SETTING = "sms_default_application";
    private final Context mContext;
    private SparseArray<String> mDefaultSmsApp = new SparseArray<>();
    private final ContentObserver mSmsContentObserver;

    public NotificationMessagingUtil(Context context) {
        ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.internal.util.NotificationMessagingUtil.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Collection<Uri> uris, int flags, int userId) {
                if (uris.contains(Settings.Secure.getUriFor("sms_default_application"))) {
                    NotificationMessagingUtil.this.cacheDefaultSmsApp(userId);
                }
            }
        };
        this.mSmsContentObserver = contentObserver;
        this.mContext = context;
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("sms_default_application"), false, contentObserver);
    }

    public boolean isImportantMessaging(StatusBarNotification sbn, int importance) {
        if (importance < 2) {
            return false;
        }
        return hasMessagingStyle(sbn) || (isCategoryMessage(sbn) && isDefaultMessagingApp(sbn));
    }

    public boolean isMessaging(StatusBarNotification sbn) {
        return hasMessagingStyle(sbn) || isDefaultMessagingApp(sbn) || isCategoryMessage(sbn);
    }

    private boolean isDefaultMessagingApp(StatusBarNotification sbn) {
        int userId = sbn.getUserId();
        if (userId == -10000 || userId == -1) {
            return false;
        }
        if (this.mDefaultSmsApp.get(userId) == null) {
            cacheDefaultSmsApp(userId);
        }
        return Objects.equals(this.mDefaultSmsApp.get(userId), sbn.getPackageName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cacheDefaultSmsApp(int userId) {
        this.mDefaultSmsApp.put(userId, Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "sms_default_application", userId));
    }

    private boolean hasMessagingStyle(StatusBarNotification sbn) {
        return sbn.getNotification().isStyle(Notification.MessagingStyle.class);
    }

    private boolean isCategoryMessage(StatusBarNotification sbn) {
        return Notification.CATEGORY_MESSAGE.equals(sbn.getNotification().category);
    }
}
