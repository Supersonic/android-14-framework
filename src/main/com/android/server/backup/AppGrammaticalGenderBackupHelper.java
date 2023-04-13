package com.android.server.backup;

import android.app.backup.BackupDataOutput;
import android.app.backup.BlobBackupHelper;
import android.os.ParcelFileDescriptor;
import com.android.server.LocalServices;
import com.android.server.grammaticalinflection.GrammaticalInflectionManagerInternal;
/* loaded from: classes.dex */
public class AppGrammaticalGenderBackupHelper extends BlobBackupHelper {
    public final GrammaticalInflectionManagerInternal mGrammarInflectionManagerInternal;
    public final int mUserId;

    public AppGrammaticalGenderBackupHelper(int i) {
        super(1, new String[]{"app_gender"});
        this.mUserId = i;
        this.mGrammarInflectionManagerInternal = (GrammaticalInflectionManagerInternal) LocalServices.getService(GrammaticalInflectionManagerInternal.class);
    }

    public void performBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) {
        if ((backupDataOutput.getTransportFlags() & 1) == 0) {
            return;
        }
        super.performBackup(parcelFileDescriptor, backupDataOutput, parcelFileDescriptor2);
    }

    public byte[] getBackupPayload(String str) {
        GrammaticalInflectionManagerInternal grammaticalInflectionManagerInternal;
        if (!"app_gender".equals(str) || (grammaticalInflectionManagerInternal = this.mGrammarInflectionManagerInternal) == null) {
            return null;
        }
        return grammaticalInflectionManagerInternal.getBackupPayload(this.mUserId);
    }

    public void applyRestoredPayload(String str, byte[] bArr) {
        GrammaticalInflectionManagerInternal grammaticalInflectionManagerInternal;
        if (!"app_gender".equals(str) || (grammaticalInflectionManagerInternal = this.mGrammarInflectionManagerInternal) == null) {
            return;
        }
        grammaticalInflectionManagerInternal.stageAndApplyRestoredPayload(bArr, this.mUserId);
    }
}
