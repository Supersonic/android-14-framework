package com.android.server.companion.virtual;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.UserHandle;
import android.util.Slog;
/* loaded from: classes.dex */
public class PermissionUtils {
    public static boolean validateCallingPackageName(Context context, String str) {
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int packageUidAsUser = context.getPackageManager().getPackageUidAsUser(str, UserHandle.getUserId(callingUid));
            if (packageUidAsUser == callingUid) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            }
            Slog.e("VDM.PermissionUtils", "validatePackageName: App with package name " + str + " is UID " + packageUidAsUser + " but caller is " + callingUid);
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.e("VDM.PermissionUtils", "validatePackageName: App with package name " + str + " does not exist");
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
