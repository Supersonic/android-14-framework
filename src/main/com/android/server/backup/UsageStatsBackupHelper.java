package com.android.server.backup;

import android.app.backup.BlobBackupHelper;
import android.app.usage.UsageStatsManagerInternal;
import com.android.server.LocalServices;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public class UsageStatsBackupHelper extends BlobBackupHelper {
    public final int mUserId;

    public UsageStatsBackupHelper(int i) {
        super(1, new String[]{"usage_stats"});
        this.mUserId = i;
    }

    public byte[] getBackupPayload(String str) {
        if ("usage_stats".equals(str)) {
            UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeInt(0);
                dataOutputStream.write(usageStatsManagerInternal.getBackupPayload(this.mUserId, str));
            } catch (IOException unused) {
                byteArrayOutputStream.reset();
            }
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    public void applyRestoredPayload(String str, byte[] bArr) {
        if ("usage_stats".equals(str)) {
            UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
            try {
                dataInputStream.readInt();
                int length = bArr.length - 4;
                byte[] bArr2 = new byte[length];
                dataInputStream.read(bArr2, 0, length);
                usageStatsManagerInternal.applyRestoredPayload(this.mUserId, str, bArr2);
            } catch (IOException unused) {
            }
        }
    }
}
