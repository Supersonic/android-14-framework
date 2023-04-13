package com.android.server.inputmethod;

import android.app.UriGrantsManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.IInputContentUriToken;
import com.android.server.LocalServices;
import com.android.server.uri.UriGrantsManagerInternal;
/* loaded from: classes.dex */
public final class InputContentUriTokenHandler extends IInputContentUriToken.Stub {
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public IBinder mPermissionOwnerToken = null;
    public final int mSourceUid;
    public final int mSourceUserId;
    public final String mTargetPackage;
    public final int mTargetUserId;
    public final Uri mUri;

    public InputContentUriTokenHandler(Uri uri, int i, String str, int i2, int i3) {
        this.mUri = uri;
        this.mSourceUid = i;
        this.mTargetPackage = str;
        this.mSourceUserId = i2;
        this.mTargetUserId = i3;
    }

    public void take() {
        synchronized (this.mLock) {
            if (this.mPermissionOwnerToken != null) {
                return;
            }
            IBinder newUriPermissionOwner = ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).newUriPermissionOwner("InputContentUriTokenHandler");
            this.mPermissionOwnerToken = newUriPermissionOwner;
            doTakeLocked(newUriPermissionOwner);
        }
    }

    public final void doTakeLocked(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                UriGrantsManager.getService().grantUriPermissionFromOwner(iBinder, this.mSourceUid, this.mTargetPackage, this.mUri, 1, this.mSourceUserId, this.mTargetUserId);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void release() {
        synchronized (this.mLock) {
            if (this.mPermissionOwnerToken == null) {
                return;
            }
            ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).revokeUriPermissionFromOwner(this.mPermissionOwnerToken, this.mUri, 1, this.mSourceUserId);
            this.mPermissionOwnerToken = null;
        }
    }

    public void finalize() throws Throwable {
        try {
            release();
        } finally {
            super/*java.lang.Object*/.finalize();
        }
    }
}
