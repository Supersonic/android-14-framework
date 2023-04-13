package com.android.server.p011pm.pkg.component;
/* renamed from: com.android.server.pm.pkg.component.ComponentMutateUtils */
/* loaded from: classes2.dex */
public class ComponentMutateUtils {
    public static void setMaxAspectRatio(ParsedActivity parsedActivity, int i, float f) {
        ((ParsedActivityImpl) parsedActivity).setMaxAspectRatio(i, f);
    }

    public static void setMinAspectRatio(ParsedActivity parsedActivity, int i, float f) {
        ((ParsedActivityImpl) parsedActivity).setMinAspectRatio(i, f);
    }

    public static void setSupportsSizeChanges(ParsedActivity parsedActivity, boolean z) {
        ((ParsedActivityImpl) parsedActivity).setSupportsSizeChanges(z);
    }

    public static void setResizeMode(ParsedActivity parsedActivity, int i) {
        ((ParsedActivityImpl) parsedActivity).setResizeMode(i);
    }

    public static void setExactFlags(ParsedComponent parsedComponent, int i) {
        ((ParsedComponentImpl) parsedComponent).setFlags(i);
    }

    public static void setEnabled(ParsedMainComponent parsedMainComponent, boolean z) {
        ((ParsedMainComponentImpl) parsedMainComponent).setEnabled(z);
    }

    public static void setPackageName(ParsedComponent parsedComponent, String str) {
        ((ParsedComponentImpl) parsedComponent).setPackageName(str);
    }

    public static void setDirectBootAware(ParsedMainComponent parsedMainComponent, boolean z) {
        ((ParsedMainComponentImpl) parsedMainComponent).setDirectBootAware(z);
    }

    public static void setExported(ParsedMainComponent parsedMainComponent, boolean z) {
        ((ParsedMainComponentImpl) parsedMainComponent).setExported(z);
    }

    public static void setAuthority(ParsedProvider parsedProvider, String str) {
        ((ParsedProviderImpl) parsedProvider).setAuthority(str);
    }

    public static void setSyncable(ParsedProvider parsedProvider, boolean z) {
        ((ParsedProviderImpl) parsedProvider).setSyncable(z);
    }

    public static void setProtectionLevel(ParsedPermission parsedPermission, int i) {
        ((ParsedPermissionImpl) parsedPermission).setProtectionLevel(i);
    }

    public static void setParsedPermissionGroup(ParsedPermission parsedPermission, ParsedPermissionGroup parsedPermissionGroup) {
        ((ParsedPermissionImpl) parsedPermission).setParsedPermissionGroup(parsedPermissionGroup);
    }

    public static void setPriority(ParsedPermissionGroup parsedPermissionGroup, int i) {
        ((ParsedPermissionGroupImpl) parsedPermissionGroup).setPriority(i);
    }

    public static void addStateFrom(ParsedProcess parsedProcess, ParsedProcess parsedProcess2) {
        ((ParsedProcessImpl) parsedProcess).addStateFrom(parsedProcess2);
    }
}
