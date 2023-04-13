package com.android.server.backup;

import android.app.AppGlobals;
import android.app.backup.BlobBackupHelper;
import android.content.p001pm.IPackageManager;
import android.util.Slog;
/* loaded from: classes5.dex */
public class PreferredActivityBackupHelper extends BlobBackupHelper {
    private static final boolean DEBUG = false;
    private static final int STATE_VERSION = 4;
    private static final String TAG = "PreferredBackup";
    private final int mUserId;
    private static final String KEY_PREFERRED = "preferred-activity";
    private static final String KEY_DEFAULT_APPS = "default-apps";
    @Deprecated
    private static final String KEY_INTENT_VERIFICATION = "intent-verification";
    private static final String KEY_DOMAIN_VERIFICATION = "domain-verification";
    private static final String[] KEYS = {KEY_PREFERRED, KEY_DEFAULT_APPS, KEY_INTENT_VERIFICATION, KEY_DOMAIN_VERIFICATION};

    /* loaded from: classes5.dex */
    private @interface Key {
    }

    public PreferredActivityBackupHelper(int userId) {
        super(4, KEYS);
        this.mUserId = userId;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.backup.BlobBackupHelper
    protected byte[] getBackupPayload(String key) {
        char c;
        IPackageManager pm = AppGlobals.getPackageManager();
        try {
            switch (key.hashCode()) {
                case -696985986:
                    if (key.equals(KEY_DEFAULT_APPS)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -549387132:
                    if (key.equals(KEY_DOMAIN_VERIFICATION)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -429170260:
                    if (key.equals(KEY_INTENT_VERIFICATION)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1336142555:
                    if (key.equals(KEY_PREFERRED)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
        } catch (Exception e) {
            Slog.m95e(TAG, "Unable to store payload " + key, e);
        }
        switch (c) {
            case 0:
                return pm.getPreferredActivityBackup(this.mUserId);
            case 1:
                return pm.getDefaultAppsBackup(this.mUserId);
            case 2:
                return null;
            case 3:
                return pm.getDomainVerificationBackup(this.mUserId);
            default:
                Slog.m90w(TAG, "Unexpected backup key " + key);
                return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.app.backup.BlobBackupHelper
    protected void applyRestoredPayload(String key, byte[] payload) {
        char c;
        IPackageManager pm = AppGlobals.getPackageManager();
        try {
            switch (key.hashCode()) {
                case -696985986:
                    if (key.equals(KEY_DEFAULT_APPS)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -549387132:
                    if (key.equals(KEY_DOMAIN_VERIFICATION)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -429170260:
                    if (key.equals(KEY_INTENT_VERIFICATION)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1336142555:
                    if (key.equals(KEY_PREFERRED)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    pm.restorePreferredActivities(payload, this.mUserId);
                    return;
                case 1:
                    pm.restoreDefaultApps(payload, this.mUserId);
                    return;
                case 2:
                    return;
                case 3:
                    pm.restoreDomainVerification(payload, this.mUserId);
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
