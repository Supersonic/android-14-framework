package com.android.server.backup;

import android.accounts.AccountManagerInternal;
import android.app.backup.BlobBackupHelper;
import android.util.Slog;
import com.android.server.LocalServices;
/* loaded from: classes5.dex */
public class AccountManagerBackupHelper extends BlobBackupHelper {
    private static final boolean DEBUG = false;
    private static final String KEY_ACCOUNT_ACCESS_GRANTS = "account_access_grants";
    private static final int STATE_VERSION = 1;
    private static final String TAG = "AccountsBackup";
    private final int mUserId;

    public AccountManagerBackupHelper(int userId) {
        super(1, KEY_ACCOUNT_ACCESS_GRANTS);
        this.mUserId = userId;
    }

    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String key) {
        char c;
        AccountManagerInternal am = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
        try {
            switch (key.hashCode()) {
                case 1544100736:
                    if (key.equals(KEY_ACCOUNT_ACCESS_GRANTS)) {
                        c = 0;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
        } catch (Exception e) {
            Slog.m95e(TAG, "Unable to store payload " + key, e);
        }
        switch (c) {
            case 0:
                return am.backupAccountAccessPermissions(this.mUserId);
            default:
                Slog.m90w(TAG, "Unexpected backup key " + key);
                return new byte[0];
        }
    }

    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String key, byte[] payload) {
        char c;
        AccountManagerInternal am = (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
        try {
            switch (key.hashCode()) {
                case 1544100736:
                    if (key.equals(KEY_ACCOUNT_ACCESS_GRANTS)) {
                        c = 0;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    am.restoreAccountAccessPermissions(payload, this.mUserId);
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
