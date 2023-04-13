package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.people.PeopleServiceInternal;
/* loaded from: classes.dex */
public class PeopleBackupHelper extends BlobBackupHelper {
    public static final String TAG = PeopleBackupHelper.class.getSimpleName();
    public final int mUserId;

    public PeopleBackupHelper(int i) {
        super(1, new String[]{"people_conversation_infos"});
        this.mUserId = i;
    }

    public byte[] getBackupPayload(String str) {
        if (!"people_conversation_infos".equals(str)) {
            String str2 = TAG;
            Slog.w(str2, "Unexpected backup key " + str);
            return new byte[0];
        }
        return ((PeopleServiceInternal) LocalServices.getService(PeopleServiceInternal.class)).getBackupPayload(this.mUserId);
    }

    public void applyRestoredPayload(String str, byte[] bArr) {
        if (!"people_conversation_infos".equals(str)) {
            String str2 = TAG;
            Slog.w(str2, "Unexpected restore key " + str);
            return;
        }
        ((PeopleServiceInternal) LocalServices.getService(PeopleServiceInternal.class)).restore(this.mUserId, bArr);
    }
}
