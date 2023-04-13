package com.android.server.devicepolicy;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.utils.Slogf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public final class PersonalAppsSuspensionHelper {
    public final Context mContext;
    public final PackageManager mPackageManager;

    public static PersonalAppsSuspensionHelper forUser(Context context, int i) {
        return new PersonalAppsSuspensionHelper(context.createContextAsUser(UserHandle.of(i), 0));
    }

    public PersonalAppsSuspensionHelper(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
    }

    public String[] getPersonalAppsForSuspension() {
        List<PackageInfo> installedPackages = this.mPackageManager.getInstalledPackages(786432);
        ArraySet arraySet = new ArraySet();
        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((!applicationInfo.isSystemApp() && !applicationInfo.isUpdatedSystemApp()) || hasLauncherIntent(packageInfo.packageName)) {
                arraySet.add(packageInfo.packageName);
            }
        }
        arraySet.removeAll(getCriticalPackages());
        arraySet.removeAll(getSystemLauncherPackages());
        arraySet.removeAll(getAccessibilityServices());
        arraySet.removeAll(getInputMethodPackages());
        arraySet.remove(Telephony.Sms.getDefaultSmsPackage(this.mContext));
        arraySet.remove(getSettingsPackageName());
        for (String str : this.mPackageManager.getUnsuspendablePackages((String[]) arraySet.toArray(new String[0]))) {
            arraySet.remove(str);
        }
        if (Log.isLoggable("DevicePolicyManager", 4)) {
            Slogf.m20i("DevicePolicyManager", "Packages subject to suspension: %s", String.join(",", arraySet));
        }
        return (String[]) arraySet.toArray(new String[0]);
    }

    public final List<String> getSystemLauncherPackages() {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        for (ResolveInfo resolveInfo : this.mPackageManager.queryIntentActivities(intent, 786432)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo == null || TextUtils.isEmpty(activityInfo.packageName)) {
                Slogf.wtf("DevicePolicyManager", "Could not find package name for launcher app %s", resolveInfo);
            } else {
                String str = resolveInfo.activityInfo.packageName;
                try {
                    ApplicationInfo applicationInfo = this.mPackageManager.getApplicationInfo(str, 786432);
                    if (applicationInfo.isSystemApp() || applicationInfo.isUpdatedSystemApp()) {
                        arrayList.add(str);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    Slogf.m24e("DevicePolicyManager", "Could not find application info for launcher app: %s", str);
                }
            }
        }
        return arrayList;
    }

    public final List<String> getAccessibilityServices() {
        IBinder service = ServiceManager.getService("accessibility");
        IAccessibilityManager asInterface = service == null ? null : IAccessibilityManager.Stub.asInterface(service);
        Context context = this.mContext;
        AccessibilityManager accessibilityManager = new AccessibilityManager(context, asInterface, context.getUserId());
        try {
            List<AccessibilityServiceInfo> enabledAccessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(-1);
            accessibilityManager.removeClient();
            ArrayList arrayList = new ArrayList();
            for (AccessibilityServiceInfo accessibilityServiceInfo : enabledAccessibilityServiceList) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(accessibilityServiceInfo.getId());
                if (unflattenFromString != null) {
                    arrayList.add(unflattenFromString.getPackageName());
                }
            }
            return arrayList;
        } catch (Throwable th) {
            accessibilityManager.removeClient();
            throw th;
        }
    }

    public final List<String> getInputMethodPackages() {
        List<InputMethodInfo> enabledInputMethodListAsUser = InputMethodManagerInternal.get().getEnabledInputMethodListAsUser(this.mContext.getUserId());
        ArrayList arrayList = new ArrayList();
        for (InputMethodInfo inputMethodInfo : enabledInputMethodListAsUser) {
            arrayList.add(inputMethodInfo.getPackageName());
        }
        return arrayList;
    }

    public final String getSettingsPackageName() {
        Intent intent = new Intent("android.settings.SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        ResolveInfo resolveActivity = this.mPackageManager.resolveActivity(intent, 786432);
        if (resolveActivity != null) {
            return resolveActivity.activityInfo.packageName;
        }
        return null;
    }

    public final List<String> getCriticalPackages() {
        return Arrays.asList(this.mContext.getResources().getStringArray(17236114));
    }

    public final boolean hasLauncherIntent(String str) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 786432);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("PersonalAppsSuspensionHelper");
        indentingPrintWriter.increaseIndent();
        DevicePolicyManagerService.dumpApps(indentingPrintWriter, "critical packages", getCriticalPackages());
        DevicePolicyManagerService.dumpApps(indentingPrintWriter, "launcher packages", getSystemLauncherPackages());
        DevicePolicyManagerService.dumpApps(indentingPrintWriter, "accessibility services", getAccessibilityServices());
        DevicePolicyManagerService.dumpApps(indentingPrintWriter, "input method packages", getInputMethodPackages());
        indentingPrintWriter.printf("SMS package: %s\n", new Object[]{Telephony.Sms.getDefaultSmsPackage(this.mContext)});
        indentingPrintWriter.printf("Settings package: %s\n", new Object[]{getSettingsPackageName()});
        DevicePolicyManagerService.dumpApps(indentingPrintWriter, "Packages subject to suspension", getPersonalAppsForSuspension());
        indentingPrintWriter.decreaseIndent();
    }
}
