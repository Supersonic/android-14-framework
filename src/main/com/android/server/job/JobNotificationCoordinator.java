package com.android.server.job;

import android.app.Notification;
import android.content.pm.UserPackage;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseSetArray;
import com.android.server.LocalServices;
import com.android.server.notification.NotificationManagerInternal;
/* loaded from: classes.dex */
public class JobNotificationCoordinator {
    public final ArrayMap<UserPackage, SparseSetArray<JobServiceContext>> mCurrentAssociations = new ArrayMap<>();
    public final ArrayMap<JobServiceContext, NotificationDetails> mNotificationDetails = new ArrayMap<>();
    public final NotificationManagerInternal mNotificationManagerInternal = (NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class);

    /* loaded from: classes.dex */
    public static final class NotificationDetails {
        public final int appPid;
        public final int appUid;
        public final int jobEndNotificationPolicy;
        public final int notificationId;
        public final UserPackage userPackage;

        public NotificationDetails(UserPackage userPackage, int i, int i2, int i3, int i4) {
            this.userPackage = userPackage;
            this.notificationId = i3;
            this.appPid = i;
            this.appUid = i2;
            this.jobEndNotificationPolicy = i4;
        }
    }

    public void enqueueNotification(JobServiceContext jobServiceContext, String str, int i, int i2, int i3, Notification notification, int i4) {
        validateNotification(str, i2, notification, i4);
        NotificationDetails notificationDetails = this.mNotificationDetails.get(jobServiceContext);
        if (notificationDetails != null && notificationDetails.notificationId != i3) {
            removeNotificationAssociation(jobServiceContext, 0);
        }
        int userId = UserHandle.getUserId(i2);
        this.mNotificationManagerInternal.enqueueNotification(str, str, i2, i, null, i3, notification, userId);
        UserPackage of = UserPackage.of(userId, str);
        NotificationDetails notificationDetails2 = new NotificationDetails(of, i, i2, i3, i4);
        SparseSetArray<JobServiceContext> sparseSetArray = this.mCurrentAssociations.get(of);
        if (sparseSetArray == null) {
            sparseSetArray = new SparseSetArray<>();
            this.mCurrentAssociations.put(of, sparseSetArray);
        }
        sparseSetArray.add(i3, jobServiceContext);
        this.mNotificationDetails.put(jobServiceContext, notificationDetails2);
    }

    public void removeNotificationAssociation(JobServiceContext jobServiceContext, int i) {
        NotificationDetails remove = this.mNotificationDetails.remove(jobServiceContext);
        if (remove == null) {
            return;
        }
        SparseSetArray<JobServiceContext> sparseSetArray = this.mCurrentAssociations.get(remove.userPackage);
        if (sparseSetArray == null || !sparseSetArray.remove(remove.notificationId, jobServiceContext)) {
            Slog.wtf("JobNotificationCoordinator", "Association data structures not in sync");
            return;
        }
        ArraySet arraySet = sparseSetArray.get(remove.notificationId);
        if (arraySet == null || arraySet.isEmpty()) {
            if (remove.jobEndNotificationPolicy == 1 || i == 13) {
                String str = remove.userPackage.packageName;
                NotificationManagerInternal notificationManagerInternal = this.mNotificationManagerInternal;
                int i2 = remove.appUid;
                notificationManagerInternal.cancelNotification(str, str, i2, remove.appPid, null, remove.notificationId, UserHandle.getUserId(i2));
            }
        }
    }

    public final void validateNotification(String str, int i, Notification notification, int i2) {
        if (notification == null) {
            throw new NullPointerException("notification");
        }
        if (notification.getSmallIcon() == null) {
            throw new IllegalArgumentException("small icon required");
        }
        if (this.mNotificationManagerInternal.getNotificationChannel(str, i, notification.getChannelId()) == null) {
            throw new IllegalArgumentException("invalid notification channel");
        }
        if (i2 != 0 && i2 != 1) {
            throw new IllegalArgumentException("invalid job end notification policy");
        }
    }
}
