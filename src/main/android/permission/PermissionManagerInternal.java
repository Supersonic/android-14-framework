package android.permission;
/* loaded from: classes3.dex */
public interface PermissionManagerInternal {
    byte[] backupRuntimePermissions(int i);

    void restoreDelayedRuntimePermissions(String str, int i);

    void restoreRuntimePermissions(byte[] bArr, int i);
}
