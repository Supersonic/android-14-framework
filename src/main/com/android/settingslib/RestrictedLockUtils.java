package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.Objects;
/* loaded from: classes2.dex */
public class RestrictedLockUtils {
    @RequiresApi(23)
    public static EnforcedAdmin getProfileOrDeviceOwner(Context context, UserHandle userHandle) {
        return getProfileOrDeviceOwner(context, null, userHandle);
    }

    @RequiresApi(23)
    public static EnforcedAdmin getProfileOrDeviceOwner(Context context, String str, UserHandle userHandle) {
        DevicePolicyManager devicePolicyManager;
        ComponentName deviceOwnerComponentOnAnyUser;
        if (userHandle == null || (devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy")) == null) {
            return null;
        }
        try {
            ComponentName profileOwner = ((DevicePolicyManager) context.createPackageContextAsUser(context.getPackageName(), 0, userHandle).getSystemService(DevicePolicyManager.class)).getProfileOwner();
            if (profileOwner != null) {
                return new EnforcedAdmin(profileOwner, str, userHandle);
            }
            if (!Objects.equals(devicePolicyManager.getDeviceOwnerUser(), userHandle) || (deviceOwnerComponentOnAnyUser = devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == null) {
                return null;
            }
            return new EnforcedAdmin(deviceOwnerComponentOnAnyUser, str, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /* loaded from: classes2.dex */
    public static class EnforcedAdmin {
        public static final EnforcedAdmin MULTIPLE_ENFORCED_ADMIN = new EnforcedAdmin();
        @Nullable
        public ComponentName component;
        @Nullable
        public String enforcedRestriction;
        @Nullable
        public UserHandle user;

        public EnforcedAdmin(ComponentName componentName, String str, UserHandle userHandle) {
            this.component = componentName;
            this.enforcedRestriction = str;
            this.user = userHandle;
        }

        public EnforcedAdmin() {
            this.component = null;
            this.enforcedRestriction = null;
            this.user = null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            EnforcedAdmin enforcedAdmin = (EnforcedAdmin) obj;
            return Objects.equals(this.user, enforcedAdmin.user) && Objects.equals(this.component, enforcedAdmin.component) && Objects.equals(this.enforcedRestriction, enforcedAdmin.enforcedRestriction);
        }

        public int hashCode() {
            return Objects.hash(this.component, this.enforcedRestriction, this.user);
        }

        public String toString() {
            return "EnforcedAdmin{component=" + this.component + ", enforcedRestriction='" + this.enforcedRestriction + ", user=" + this.user + '}';
        }
    }
}
