package com.android.server.notification;

import android.util.Slog;
import java.util.Comparator;
/* loaded from: classes2.dex */
public class GlobalSortKeyComparator implements Comparator<NotificationRecord> {
    @Override // java.util.Comparator
    public int compare(NotificationRecord notificationRecord, NotificationRecord notificationRecord2) {
        if (notificationRecord.getGlobalSortKey() == null) {
            Slog.wtf("GlobalSortComp", "Missing left global sort key: " + notificationRecord);
            return 1;
        } else if (notificationRecord2.getGlobalSortKey() == null) {
            Slog.wtf("GlobalSortComp", "Missing right global sort key: " + notificationRecord2);
            return -1;
        } else {
            return notificationRecord.getGlobalSortKey().compareTo(notificationRecord2.getGlobalSortKey());
        }
    }
}
