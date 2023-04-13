package android.content.p001pm;

import android.Manifest;
import android.app.AppGlobals;
import android.content.Intent;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.ArraySet;
import android.view.inputmethod.InputMethod;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.content.pm.AppsQueryHelper */
/* loaded from: classes.dex */
public class AppsQueryHelper {
    private List<ApplicationInfo> mAllApps;
    private final IPackageManager mPackageManager;
    public static int GET_NON_LAUNCHABLE_APPS = 1;
    public static int GET_APPS_WITH_INTERACT_ACROSS_USERS_PERM = 2;
    public static int GET_IMES = 4;
    public static int GET_REQUIRED_FOR_SYSTEM_USER = 8;

    public AppsQueryHelper(IPackageManager packageManager) {
        this.mPackageManager = packageManager;
    }

    public AppsQueryHelper() {
        this(AppGlobals.getPackageManager());
    }

    public List<String> queryApps(int flags, boolean systemAppsOnly, UserHandle user) {
        boolean nonLaunchableApps = (flags & GET_NON_LAUNCHABLE_APPS) > 0;
        boolean interactAcrossUsers = (flags & GET_APPS_WITH_INTERACT_ACROSS_USERS_PERM) > 0;
        boolean imes = (flags & GET_IMES) > 0;
        boolean requiredForSystemUser = (flags & GET_REQUIRED_FOR_SYSTEM_USER) > 0;
        if (this.mAllApps == null) {
            this.mAllApps = getAllApps(user.getIdentifier());
        }
        List<String> result = new ArrayList<>();
        if (flags == 0) {
            int allAppsSize = this.mAllApps.size();
            for (int i = 0; i < allAppsSize; i++) {
                ApplicationInfo appInfo = this.mAllApps.get(i);
                if (!systemAppsOnly || appInfo.isSystemApp()) {
                    result.add(appInfo.packageName);
                }
            }
            return result;
        }
        if (nonLaunchableApps) {
            Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = queryIntentActivitiesAsUser(intent, user.getIdentifier());
            ArraySet<String> appsWithLaunchers = new ArraySet<>();
            int resolveInfosSize = resolveInfos.size();
            for (int i2 = 0; i2 < resolveInfosSize; i2++) {
                appsWithLaunchers.add(resolveInfos.get(i2).activityInfo.packageName);
            }
            int allAppsSize2 = this.mAllApps.size();
            for (int i3 = 0; i3 < allAppsSize2; i3++) {
                ApplicationInfo appInfo2 = this.mAllApps.get(i3);
                if (!systemAppsOnly || appInfo2.isSystemApp()) {
                    String packageName = appInfo2.packageName;
                    if (!appsWithLaunchers.contains(packageName)) {
                        result.add(packageName);
                    }
                }
            }
        }
        if (interactAcrossUsers) {
            List<PackageInfo> packagesHoldingPermissions = getPackagesHoldingPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS, user.getIdentifier());
            int packagesHoldingPermissionsSize = packagesHoldingPermissions.size();
            for (int i4 = 0; i4 < packagesHoldingPermissionsSize; i4++) {
                PackageInfo packageInfo = packagesHoldingPermissions.get(i4);
                if ((!systemAppsOnly || packageInfo.applicationInfo.isSystemApp()) && !result.contains(packageInfo.packageName)) {
                    result.add(packageInfo.packageName);
                }
            }
        }
        if (imes) {
            List<ResolveInfo> resolveInfos2 = queryIntentServicesAsUser(new Intent(InputMethod.SERVICE_INTERFACE), user.getIdentifier());
            int resolveInfosSize2 = resolveInfos2.size();
            for (int i5 = 0; i5 < resolveInfosSize2; i5++) {
                ServiceInfo serviceInfo = resolveInfos2.get(i5).serviceInfo;
                if ((!systemAppsOnly || serviceInfo.applicationInfo.isSystemApp()) && !result.contains(serviceInfo.packageName)) {
                    result.add(serviceInfo.packageName);
                }
            }
        }
        if (requiredForSystemUser) {
            int allAppsSize3 = this.mAllApps.size();
            for (int i6 = 0; i6 < allAppsSize3; i6++) {
                ApplicationInfo appInfo3 = this.mAllApps.get(i6);
                if ((!systemAppsOnly || appInfo3.isSystemApp()) && appInfo3.isRequiredForSystemUser()) {
                    result.add(appInfo3.packageName);
                }
            }
        }
        return result;
    }

    protected List<ApplicationInfo> getAllApps(int userId) {
        try {
            return this.mPackageManager.getInstalledApplications(8704L, userId).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected List<ResolveInfo> queryIntentActivitiesAsUser(Intent intent, int userId) {
        try {
            return this.mPackageManager.queryIntentActivities(intent, null, 795136L, userId).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected List<ResolveInfo> queryIntentServicesAsUser(Intent intent, int userId) {
        try {
            return this.mPackageManager.queryIntentServices(intent, null, 819328L, userId).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected List<PackageInfo> getPackagesHoldingPermission(String perm, int userId) {
        try {
            return this.mPackageManager.getPackagesHoldingPermissions(new String[]{perm}, 0L, userId).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
