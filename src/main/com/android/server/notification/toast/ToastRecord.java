package com.android.server.notification.toast;

import android.os.Binder;
import android.os.IBinder;
import com.android.server.notification.NotificationManagerService;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public abstract class ToastRecord {
    public final int displayId;
    public final boolean isSystemToast;
    public int mDuration;
    public final NotificationManagerService mNotificationManager;
    public final int pid;
    public final String pkg;
    public final IBinder token;
    public final int uid;
    public final Binder windowToken;

    public abstract void hide();

    public abstract boolean isAppRendered();

    public boolean keepProcessAlive() {
        return false;
    }

    public abstract boolean show();

    public ToastRecord(NotificationManagerService notificationManagerService, int i, int i2, String str, boolean z, IBinder iBinder, int i3, Binder binder, int i4) {
        this.mNotificationManager = notificationManagerService;
        this.uid = i;
        this.pid = i2;
        this.pkg = str;
        this.isSystemToast = z;
        this.token = iBinder;
        this.windowToken = binder;
        this.displayId = i4;
        this.mDuration = i3;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void update(int i) {
        this.mDuration = i;
    }

    public void dump(PrintWriter printWriter, String str, NotificationManagerService.DumpFilter dumpFilter) {
        if (dumpFilter == null || dumpFilter.matches(this.pkg)) {
            printWriter.println(str + this);
        }
    }
}
