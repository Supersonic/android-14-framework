package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.locales.LocaleManagerInternal;
/* loaded from: classes.dex */
public class AppSpecificLocalesBackupHelper extends BlobBackupHelper {
    public final LocaleManagerInternal mLocaleManagerInternal;
    public final int mUserId;

    public AppSpecificLocalesBackupHelper(int i) {
        super(1, new String[]{"app_locales"});
        this.mUserId = i;
        this.mLocaleManagerInternal = (LocaleManagerInternal) LocalServices.getService(LocaleManagerInternal.class);
    }

    public byte[] getBackupPayload(String str) {
        if ("app_locales".equals(str)) {
            try {
                return this.mLocaleManagerInternal.getBackupPayload(this.mUserId);
            } catch (Exception e) {
                Slog.e("AppLocalesBackupHelper", "Couldn't communicate with locale manager", e);
                return null;
            }
        }
        Slog.w("AppLocalesBackupHelper", "Unexpected backup key " + str);
        return null;
    }

    public void applyRestoredPayload(String str, byte[] bArr) {
        if ("app_locales".equals(str)) {
            try {
                this.mLocaleManagerInternal.stageAndApplyRestoredPayload(bArr, this.mUserId);
                return;
            } catch (Exception e) {
                Slog.e("AppLocalesBackupHelper", "Couldn't communicate with locale manager", e);
                return;
            }
        }
        Slog.w("AppLocalesBackupHelper", "Unexpected restore key " + str);
    }
}
