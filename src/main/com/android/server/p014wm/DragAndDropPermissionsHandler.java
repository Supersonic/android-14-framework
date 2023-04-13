package com.android.server.p014wm;

import android.app.UriGrantsManager;
import android.content.ClipData;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.view.IDragAndDropPermissions;
import com.android.server.LocalServices;
import com.android.server.uri.UriGrantsManagerInternal;
import java.util.ArrayList;
/* renamed from: com.android.server.wm.DragAndDropPermissionsHandler */
/* loaded from: classes2.dex */
public class DragAndDropPermissionsHandler extends IDragAndDropPermissions.Stub {
    public IBinder mActivityToken;
    public final WindowManagerGlobalLock mGlobalLock;
    public final int mMode;
    public IBinder mPermissionOwnerToken;
    public final int mSourceUid;
    public final int mSourceUserId;
    public final String mTargetPackage;
    public final int mTargetUserId;
    public final ArrayList<Uri> mUris;

    public DragAndDropPermissionsHandler(WindowManagerGlobalLock windowManagerGlobalLock, ClipData clipData, int i, String str, int i2, int i3, int i4) {
        ArrayList<Uri> arrayList = new ArrayList<>();
        this.mUris = arrayList;
        this.mActivityToken = null;
        this.mPermissionOwnerToken = null;
        this.mGlobalLock = windowManagerGlobalLock;
        this.mSourceUid = i;
        this.mTargetPackage = str;
        this.mMode = i2;
        this.mSourceUserId = i3;
        this.mTargetUserId = i4;
        clipData.collectUris(arrayList);
    }

    public void take(IBinder iBinder) throws RemoteException {
        if (this.mActivityToken == null && this.mPermissionOwnerToken == null) {
            this.mActivityToken = iBinder;
            doTake(getUriPermissionOwnerForActivity(iBinder));
        }
    }

    public final void doTake(IBinder iBinder) throws RemoteException {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        for (int i = 0; i < this.mUris.size(); i++) {
            try {
                UriGrantsManager.getService().grantUriPermissionFromOwner(iBinder, this.mSourceUid, this.mTargetPackage, this.mUris.get(i), this.mMode, this.mSourceUserId, this.mTargetUserId);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void takeTransient() throws RemoteException {
        if (this.mActivityToken == null && this.mPermissionOwnerToken == null) {
            IBinder newUriPermissionOwner = ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).newUriPermissionOwner("drop");
            this.mPermissionOwnerToken = newUriPermissionOwner;
            doTake(newUriPermissionOwner);
        }
    }

    public void release() throws RemoteException {
        IBinder uriPermissionOwnerForActivity;
        IBinder iBinder = this.mActivityToken;
        if (iBinder == null && this.mPermissionOwnerToken == null) {
            return;
        }
        if (iBinder != null) {
            try {
                uriPermissionOwnerForActivity = getUriPermissionOwnerForActivity(iBinder);
            } catch (Exception unused) {
                return;
            } finally {
                this.mActivityToken = null;
            }
        } else {
            uriPermissionOwnerForActivity = this.mPermissionOwnerToken;
            this.mPermissionOwnerToken = null;
        }
        UriGrantsManagerInternal uriGrantsManagerInternal = (UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class);
        for (int i = 0; i < this.mUris.size(); i++) {
            uriGrantsManagerInternal.revokeUriPermissionFromOwner(uriPermissionOwnerForActivity, this.mUris.get(i), this.mMode, this.mSourceUserId);
        }
    }

    public final IBinder getUriPermissionOwnerForActivity(IBinder iBinder) {
        Binder externalToken;
        ActivityTaskManagerService.enforceNotIsolatedCaller("getUriPermissionOwnerForActivity");
        synchronized (this.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord isInRootTaskLocked = ActivityRecord.isInRootTaskLocked(iBinder);
                if (isInRootTaskLocked == null) {
                    throw new IllegalArgumentException("Activity does not exist; token=" + iBinder);
                }
                externalToken = isInRootTaskLocked.getUriPermissionsLocked().getExternalToken();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return externalToken;
    }

    public void finalize() throws Throwable {
        if (this.mActivityToken != null || this.mPermissionOwnerToken == null) {
            return;
        }
        release();
    }
}
