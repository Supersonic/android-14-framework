package com.android.server.companion;

import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.companion.AssociationInfo;
import android.content.Context;
import android.os.Binder;
import android.os.UserHandle;
import android.util.Slog;
import java.util.function.Consumer;
@SuppressLint({"LongLogTag"})
/* loaded from: classes.dex */
public final class RolesUtils {
    public static boolean isRoleHolder(Context context, int i, String str, String str2) {
        return ((RoleManager) context.getSystemService(RoleManager.class)).getRoleHoldersAsUser(str2, UserHandle.of(i)).contains(str);
    }

    public static void addRoleHolderForAssociation(Context context, AssociationInfo associationInfo, Consumer<Boolean> consumer) {
        String deviceProfile = associationInfo.getDeviceProfile();
        if (deviceProfile == null) {
            return;
        }
        ((RoleManager) context.getSystemService(RoleManager.class)).addRoleHolderAsUser(deviceProfile, associationInfo.getPackageName(), 1, UserHandle.of(associationInfo.getUserId()), context.getMainExecutor(), consumer);
    }

    public static void removeRoleHolderForAssociation(Context context, AssociationInfo associationInfo) {
        final String deviceProfile = associationInfo.getDeviceProfile();
        if (deviceProfile == null) {
            return;
        }
        RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
        final String packageName = associationInfo.getPackageName();
        final int userId = associationInfo.getUserId();
        UserHandle of = UserHandle.of(userId);
        Slog.i("CDM_CompanionDeviceManagerService", "Removing CDM role holder, role=" + deviceProfile + ", package=u" + userId + "\\" + packageName);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            roleManager.removeRoleHolderAsUser(deviceProfile, packageName, 1, of, context.getMainExecutor(), new Consumer() { // from class: com.android.server.companion.RolesUtils$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RolesUtils.lambda$removeRoleHolderForAssociation$0(userId, packageName, deviceProfile, (Boolean) obj);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$removeRoleHolderForAssociation$0(int i, String str, String str2, Boolean bool) {
        if (bool.booleanValue()) {
            return;
        }
        Slog.e("CDM_CompanionDeviceManagerService", "Failed to remove u" + i + "\\" + str + " from the list of " + str2 + " holders.");
    }
}
