package android.accounts;

import android.p008os.RemoteCallback;
/* loaded from: classes.dex */
public abstract class AccountManagerInternal {

    /* loaded from: classes.dex */
    public interface OnAppPermissionChangeListener {
        void onAppPermissionChanged(Account account, int i);
    }

    public abstract void addOnAppPermissionChangeListener(OnAppPermissionChangeListener onAppPermissionChangeListener);

    public abstract byte[] backupAccountAccessPermissions(int i);

    public abstract boolean hasAccountAccess(Account account, int i);

    public abstract void requestAccountAccess(Account account, String str, int i, RemoteCallback remoteCallback);

    public abstract void restoreAccountAccessPermissions(byte[] bArr, int i);
}
