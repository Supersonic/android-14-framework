package com.android.server.devicepolicy;

import android.app.admin.Authority;
import android.app.admin.DeviceAdminAuthority;
import android.app.admin.DpcAuthority;
import android.app.admin.RoleAuthority;
import android.app.admin.UnknownAuthority;
import android.content.ComponentName;
import android.os.UserHandle;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.role.RoleManagerLocal;
import com.android.server.LocalManagerRegistry;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class EnforcingAdmin {
    public final ActiveAdmin mActiveAdmin;
    public Set<String> mAuthorities;
    public final ComponentName mComponentName;
    public final boolean mIsRoleAuthority;
    public final String mPackageName;
    public final int mUserId;

    public static EnforcingAdmin createEnforcingAdmin(String str, int i, ActiveAdmin activeAdmin) {
        Objects.requireNonNull(str);
        return new EnforcingAdmin(str, i, activeAdmin);
    }

    public static EnforcingAdmin createEnterpriseEnforcingAdmin(ComponentName componentName, int i) {
        Objects.requireNonNull(componentName);
        return new EnforcingAdmin(componentName.getPackageName(), componentName, Set.of("enterprise"), i, null);
    }

    public static EnforcingAdmin createEnterpriseEnforcingAdmin(ComponentName componentName, int i, ActiveAdmin activeAdmin) {
        Objects.requireNonNull(componentName);
        return new EnforcingAdmin(componentName.getPackageName(), componentName, Set.of("enterprise"), i, activeAdmin);
    }

    public static EnforcingAdmin createDeviceAdminEnforcingAdmin(ComponentName componentName, int i, ActiveAdmin activeAdmin) {
        Objects.requireNonNull(componentName);
        return new EnforcingAdmin(componentName.getPackageName(), componentName, Set.of("device_admin"), i, activeAdmin);
    }

    public static String getRoleAuthorityOf(String str) {
        return "role:" + str;
    }

    public static Authority getParcelableAuthority(String str) {
        if (str == null || str.isEmpty()) {
            return UnknownAuthority.UNKNOWN_AUTHORITY;
        }
        if ("enterprise".equals(str)) {
            return DpcAuthority.DPC_AUTHORITY;
        }
        if ("device_admin".equals(str)) {
            return DeviceAdminAuthority.DEVICE_ADMIN_AUTHORITY;
        }
        if (str.startsWith("role:")) {
            return new RoleAuthority(Set.of(str.substring(5)));
        }
        return UnknownAuthority.UNKNOWN_AUTHORITY;
    }

    public EnforcingAdmin(String str, ComponentName componentName, Set<String> set, int i, ActiveAdmin activeAdmin) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(set);
        this.mIsRoleAuthority = false;
        this.mPackageName = str;
        this.mComponentName = componentName;
        this.mAuthorities = new HashSet(set);
        this.mUserId = i;
        this.mActiveAdmin = activeAdmin;
    }

    public EnforcingAdmin(String str, int i, ActiveAdmin activeAdmin) {
        Objects.requireNonNull(str);
        this.mIsRoleAuthority = true;
        this.mPackageName = str;
        this.mUserId = i;
        this.mComponentName = null;
        this.mAuthorities = null;
        this.mActiveAdmin = activeAdmin;
    }

    public static Set<String> getRoleAuthoritiesOrDefault(String str, int i) {
        Set<String> roles = getRoles(str, i);
        HashSet hashSet = new HashSet();
        Iterator<String> it = roles.iterator();
        while (it.hasNext()) {
            hashSet.add("role:" + it.next());
        }
        return hashSet.isEmpty() ? Set.of("default") : hashSet;
    }

    public static Set<String> getRoles(String str, int i) {
        HashSet hashSet = new HashSet();
        Map rolesAndHolders = ((RoleManagerLocal) LocalManagerRegistry.getManager(RoleManagerLocal.class)).getRolesAndHolders(i);
        for (String str2 : rolesAndHolders.keySet()) {
            if (((Set) rolesAndHolders.get(str2)).contains(str)) {
                hashSet.add(str2);
            }
        }
        return hashSet;
    }

    public final Set<String> getAuthorities() {
        if (this.mAuthorities == null) {
            this.mAuthorities = getRoleAuthoritiesOrDefault(this.mPackageName, this.mUserId);
        }
        return this.mAuthorities;
    }

    public boolean hasAuthority(String str) {
        return getAuthorities().contains(str);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public ActiveAdmin getActiveAdmin() {
        return this.mActiveAdmin;
    }

    public android.app.admin.EnforcingAdmin getParcelableAdmin() {
        UnknownAuthority unknownAuthority;
        if (this.mIsRoleAuthority) {
            Set<String> roles = getRoles(this.mPackageName, this.mUserId);
            if (roles.isEmpty()) {
                unknownAuthority = UnknownAuthority.UNKNOWN_AUTHORITY;
            } else {
                unknownAuthority = new RoleAuthority(roles);
            }
        } else if (this.mAuthorities.contains("enterprise")) {
            unknownAuthority = DpcAuthority.DPC_AUTHORITY;
        } else if (this.mAuthorities.contains("device_admin")) {
            unknownAuthority = DeviceAdminAuthority.DEVICE_ADMIN_AUTHORITY;
        } else {
            unknownAuthority = UnknownAuthority.UNKNOWN_AUTHORITY;
        }
        return new android.app.admin.EnforcingAdmin(this.mPackageName, unknownAuthority, UserHandle.of(this.mUserId));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || EnforcingAdmin.class != obj.getClass()) {
            return false;
        }
        EnforcingAdmin enforcingAdmin = (EnforcingAdmin) obj;
        return Objects.equals(this.mPackageName, enforcingAdmin.mPackageName) && Objects.equals(this.mComponentName, enforcingAdmin.mComponentName) && Objects.equals(Boolean.valueOf(this.mIsRoleAuthority), Boolean.valueOf(enforcingAdmin.mIsRoleAuthority)) && hasMatchingAuthorities(this, enforcingAdmin);
    }

    public static boolean hasMatchingAuthorities(EnforcingAdmin enforcingAdmin, EnforcingAdmin enforcingAdmin2) {
        if (enforcingAdmin.mIsRoleAuthority && enforcingAdmin2.mIsRoleAuthority) {
            return true;
        }
        return enforcingAdmin.getAuthorities().equals(enforcingAdmin2.getAuthorities());
    }

    public int hashCode() {
        if (this.mIsRoleAuthority) {
            return Objects.hash(this.mPackageName, Integer.valueOf(this.mUserId));
        }
        Object[] objArr = new Object[3];
        Object obj = this.mComponentName;
        if (obj == null) {
            obj = this.mPackageName;
        }
        objArr[0] = obj;
        objArr[1] = Integer.valueOf(this.mUserId);
        objArr[2] = getAuthorities();
        return Objects.hash(objArr);
    }

    public void saveToXml(TypedXmlSerializer typedXmlSerializer) throws IOException {
        typedXmlSerializer.attribute((String) null, "package-name", this.mPackageName);
        typedXmlSerializer.attributeBoolean((String) null, "is-role", this.mIsRoleAuthority);
        typedXmlSerializer.attributeInt((String) null, "user-id", this.mUserId);
        if (this.mIsRoleAuthority) {
            return;
        }
        ComponentName componentName = this.mComponentName;
        if (componentName != null) {
            typedXmlSerializer.attribute((String) null, "class-name", componentName.getClassName());
        }
        typedXmlSerializer.attribute((String) null, "authorities", String.join(";", getAuthorities()));
    }

    public static EnforcingAdmin readFromXml(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "package-name");
        boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "is-role");
        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "authorities");
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "user-id");
        if (attributeBoolean) {
            return new EnforcingAdmin(attributeValue, attributeInt, null);
        }
        String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "class-name");
        return new EnforcingAdmin(attributeValue, attributeValue3 != null ? new ComponentName(attributeValue, attributeValue3) : null, Set.of((Object[]) attributeValue2.split(";")), attributeInt, null);
    }

    public String toString() {
        return "EnforcingAdmin { mPackageName= " + this.mPackageName + ", mComponentName= " + this.mComponentName + ", mAuthorities= " + this.mAuthorities + ", mUserId= " + this.mUserId + ", mIsRoleAuthority= " + this.mIsRoleAuthority + " }";
    }
}
