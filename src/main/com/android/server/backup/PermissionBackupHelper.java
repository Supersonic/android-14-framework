package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.permission.PermissionManagerInternal;
import android.util.Slog;
import com.android.server.LocalServices;
/* loaded from: classes5.dex */
public class PermissionBackupHelper extends BlobBackupHelper {
    private static final boolean DEBUG = false;
    private static final String KEY_PERMISSIONS = "permissions";
    private static final int STATE_VERSION = 1;
    private static final String TAG = "PermissionBackup";
    private final PermissionManagerInternal mPermissionManager;
    private final int mUserId;

    public PermissionBackupHelper(int userId) {
        super(1, KEY_PERMISSIONS);
        this.mUserId = userId;
        this.mPermissionManager = (PermissionManagerInternal) LocalServices.getService(PermissionManagerInternal.class);
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String key) {
        char c;
        try {
            switch (key.hashCode()) {
                case 1133704324:
                    if (key.equals(KEY_PERMISSIONS)) {
                        c = 0;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return this.mPermissionManager.backupRuntimePermissions(this.mUserId);
                default:
                    Slog.m90w(TAG, "Unexpected backup key " + key);
                    return null;
            }
        } catch (Exception e) {
            Slog.m95e(TAG, "Unable to store payload " + key, e);
            return null;
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String key, byte[] payload) {
        char c;
        try {
            switch (key.hashCode()) {
                case 1133704324:
                    if (key.equals(KEY_PERMISSIONS)) {
                        c = 0;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    this.mPermissionManager.restoreRuntimePermissions(payload, this.mUserId);
                    return;
                default:
                    Slog.m90w(TAG, "Unexpected restore key " + key);
                    return;
            }
        } catch (Exception e) {
            Slog.m95e(TAG, "Unable to restore key " + key, e);
        }
    }
}
