package com.android.server.backup;

import android.app.INotificationManager;
import android.app.backup.BlobBackupHelper;
import android.p008os.ServiceManager;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes5.dex */
public class NotificationBackupHelper extends BlobBackupHelper {
    static final int BLOB_VERSION = 1;
    static final String KEY_NOTIFICATIONS = "notifications";
    private final int mUserId;
    static final String TAG = "NotifBackupHelper";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public NotificationBackupHelper(int userId) {
        super(1, KEY_NOTIFICATIONS);
        this.mUserId = userId;
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String key) {
        if (!KEY_NOTIFICATIONS.equals(key)) {
            return null;
        }
        try {
            INotificationManager nm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
            byte[] newPayload = nm.getBackupPayload(this.mUserId);
            return newPayload;
        } catch (Exception e) {
            Slog.m95e(TAG, "Couldn't communicate with notification manager", e);
            return null;
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String key, byte[] payload) {
        if (DEBUG) {
            Slog.m92v(TAG, "Got restore of " + key);
        }
        if (KEY_NOTIFICATIONS.equals(key)) {
            try {
                INotificationManager nm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
                nm.applyRestore(payload, this.mUserId);
            } catch (Exception e) {
                Slog.m95e(TAG, "Couldn't communicate with notification manager", e);
            }
        }
    }
}
