package com.android.server;
/* loaded from: classes.dex */
public interface PersistentDataBlockManagerInternal {
    void clearTestHarnessModeData();

    void forceOemUnlockEnabled(boolean z);

    int getAllowedUid();

    byte[] getFrpCredentialHandle();

    byte[] getTestHarnessModeData();

    void setFrpCredentialHandle(byte[] bArr);

    void setTestHarnessModeData(byte[] bArr);
}
