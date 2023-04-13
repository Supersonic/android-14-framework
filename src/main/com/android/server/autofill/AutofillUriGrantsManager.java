package com.android.server.autofill;

import android.app.IUriGrantsManager;
import android.app.UriGrantsManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.p014wm.ActivityTaskManagerInternal;
/* loaded from: classes.dex */
public final class AutofillUriGrantsManager {
    public static final String TAG = "AutofillUriGrantsManager";
    public final int mSourceUid;
    public final int mSourceUserId;
    public final ActivityTaskManagerInternal mActivityTaskMgrInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
    public final IUriGrantsManager mUgm = UriGrantsManager.getService();

    public AutofillUriGrantsManager(int i) {
        this.mSourceUid = i;
        this.mSourceUserId = UserHandle.getUserId(i);
    }

    public void grantUriPermissions(ComponentName componentName, IBinder iBinder, int i, ClipData clipData) {
        String packageName = componentName.getPackageName();
        IBinder uriPermissionOwnerForActivity = this.mActivityTaskMgrInternal.getUriPermissionOwnerForActivity(iBinder);
        if (uriPermissionOwnerForActivity == null) {
            String str = TAG;
            Slog.w(str, "Can't grant URI permissions, because the target activity token is invalid: clip=" + clipData + ", targetActivity=" + componentName + ", targetUserId=" + i + ", targetActivityToken=" + Integer.toHexString(iBinder.hashCode()));
            return;
        }
        for (int i2 = 0; i2 < clipData.getItemCount(); i2++) {
            Uri uri = clipData.getItemAt(i2).getUri();
            if (uri != null && "content".equals(uri.getScheme())) {
                grantUriPermissions(uri, packageName, i, uriPermissionOwnerForActivity);
            }
        }
    }

    public final void grantUriPermissions(Uri uri, String str, int i, IBinder iBinder) {
        String str2;
        String str3;
        String str4;
        String str5;
        int userIdFromUri = ContentProvider.getUserIdFromUri(uri, this.mSourceUserId);
        if (Helper.sVerbose) {
            String str6 = TAG;
            Slog.v(str6, "Granting URI permissions: uri=" + uri + ", sourceUid=" + this.mSourceUid + ", sourceUserId=" + userIdFromUri + ", targetPkg=" + str + ", targetUserId=" + i + ", permissionOwner=" + Integer.toHexString(iBinder.hashCode()));
        }
        Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                str4 = ", permissionOwner=";
                str5 = ", sourceUid=";
                str2 = ", sourceUserId=";
                str3 = ", targetPkg=";
            } catch (RemoteException e) {
                e = e;
                str2 = ", sourceUserId=";
                str3 = ", targetPkg=";
                str4 = ", permissionOwner=";
                str5 = ", sourceUid=";
            }
            try {
                this.mUgm.grantUriPermissionFromOwner(iBinder, this.mSourceUid, str, uriWithoutUserId, 1, userIdFromUri, i);
            } catch (RemoteException e2) {
                e = e2;
                String str7 = TAG;
                Slog.e(str7, "Granting URI permissions failed: uri=" + uri + str5 + this.mSourceUid + str2 + userIdFromUri + str3 + str + ", targetUserId=" + i + str4 + Integer.toHexString(iBinder.hashCode()), e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
