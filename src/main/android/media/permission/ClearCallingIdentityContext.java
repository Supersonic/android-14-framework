package android.media.permission;

import android.p008os.Binder;
/* loaded from: classes2.dex */
public class ClearCallingIdentityContext implements SafeCloseable {
    private final long mRestoreKey = Binder.clearCallingIdentity();

    public static SafeCloseable create() {
        return new ClearCallingIdentityContext();
    }

    private ClearCallingIdentityContext() {
    }

    @Override // android.media.permission.SafeCloseable, java.lang.AutoCloseable
    public void close() {
        Binder.restoreCallingIdentity(this.mRestoreKey);
    }
}
