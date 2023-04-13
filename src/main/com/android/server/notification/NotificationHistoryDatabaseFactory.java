package com.android.server.notification;

import android.content.Context;
import android.os.Handler;
import java.io.File;
/* loaded from: classes2.dex */
public class NotificationHistoryDatabaseFactory {
    public static NotificationHistoryDatabase sTestingNotificationHistoryDb;

    public static NotificationHistoryDatabase create(Context context, Handler handler, File file) {
        NotificationHistoryDatabase notificationHistoryDatabase = sTestingNotificationHistoryDb;
        return notificationHistoryDatabase != null ? notificationHistoryDatabase : new NotificationHistoryDatabase(handler, file);
    }
}
