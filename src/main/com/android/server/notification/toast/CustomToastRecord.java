package com.android.server.notification.toast;

import android.app.ITransientNotification;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.notification.NotificationManagerService;
/* loaded from: classes2.dex */
public class CustomToastRecord extends ToastRecord {
    public final ITransientNotification callback;

    @Override // com.android.server.notification.toast.ToastRecord
    public boolean isAppRendered() {
        return true;
    }

    @Override // com.android.server.notification.toast.ToastRecord
    public boolean keepProcessAlive() {
        return true;
    }

    public CustomToastRecord(NotificationManagerService notificationManagerService, int i, int i2, String str, boolean z, IBinder iBinder, ITransientNotification iTransientNotification, int i3, Binder binder, int i4) {
        super(notificationManagerService, i, i2, str, z, iBinder, i3, binder, i4);
        this.callback = (ITransientNotification) Preconditions.checkNotNull(iTransientNotification);
    }

    @Override // com.android.server.notification.toast.ToastRecord
    public boolean show() {
        if (NotificationManagerService.DBG) {
            Slog.d("NotificationService", "Show pkg=" + this.pkg + " callback=" + this.callback);
        }
        try {
            this.callback.show(this.windowToken);
            return true;
        } catch (RemoteException unused) {
            Slog.w("NotificationService", "Object died trying to show custom toast " + this.token + " in package " + this.pkg);
            this.mNotificationManager.keepProcessAliveForToastIfNeeded(this.pid);
            return false;
        }
    }

    @Override // com.android.server.notification.toast.ToastRecord
    public void hide() {
        try {
            this.callback.hide();
        } catch (RemoteException unused) {
            Slog.w("NotificationService", "Object died trying to hide custom toast " + this.token + " in package " + this.pkg);
        }
    }

    public String toString() {
        return "CustomToastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.pid + XmlUtils.STRING_ARRAY_SEPARATOR + this.pkg + "/" + UserHandle.formatUid(this.uid) + " isSystemToast=" + this.isSystemToast + " token=" + this.token + " callback=" + this.callback + " duration=" + getDuration() + "}";
    }
}
