package com.android.internal.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.p008os.Build;
import android.p008os.CarrierAssociatedAppEntry;
import android.p008os.SystemConfigManager;
import android.p008os.UserHandle;
import android.permission.LegacyPermissionManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public final class CarrierAppUtils {
    private static final boolean DEBUG = false;
    private static final String TAG = "CarrierAppUtils";

    private CarrierAppUtils() {
    }

    public static synchronized void disableCarrierAppsUntilPrivileged(String callingPackage, TelephonyManager telephonyManager, int userId, Context context) {
        synchronized (CarrierAppUtils.class) {
            SystemConfigManager config = (SystemConfigManager) context.getSystemService(SystemConfigManager.class);
            Set<String> systemCarrierAppsDisabledUntilUsed = config.getDisabledUntilUsedPreinstalledCarrierApps();
            Map<String, List<CarrierAssociatedAppEntry>> systemCarrierAssociatedAppsDisabledUntilUsed = config.getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries();
            ContentResolver contentResolver = getContentResolverForUser(context, userId);
            disableCarrierAppsUntilPrivileged(callingPackage, telephonyManager, contentResolver, userId, systemCarrierAppsDisabledUntilUsed, systemCarrierAssociatedAppsDisabledUntilUsed, context);
        }
    }

    public static synchronized void disableCarrierAppsUntilPrivileged(String callingPackage, int userId, Context context) {
        synchronized (CarrierAppUtils.class) {
            SystemConfigManager config = (SystemConfigManager) context.getSystemService(SystemConfigManager.class);
            Set<String> systemCarrierAppsDisabledUntilUsed = config.getDisabledUntilUsedPreinstalledCarrierApps();
            Map<String, List<CarrierAssociatedAppEntry>> systemCarrierAssociatedAppsDisabledUntilUsed = config.getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries();
            ContentResolver contentResolver = getContentResolverForUser(context, userId);
            disableCarrierAppsUntilPrivileged(callingPackage, null, contentResolver, userId, systemCarrierAppsDisabledUntilUsed, systemCarrierAssociatedAppsDisabledUntilUsed, context);
        }
    }

    private static ContentResolver getContentResolverForUser(Context context, int userId) {
        Context userContext = context.createContextAsUser(UserHandle.m145of(userId), 0);
        return userContext.getContentResolver();
    }

    private static boolean isUpdatedSystemApp(ApplicationInfo ai) {
        return (ai.flags & 128) != 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:55:0x00fc, code lost:
        if ((r0.flags & 8388608) == 0) goto L90;
     */
    /* JADX WARN: Removed duplicated region for block: B:117:0x02a4  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x02a6  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x00e9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:184:0x008e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0200  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void disableCarrierAppsUntilPrivileged(String callingPackage, TelephonyManager telephonyManager, ContentResolver contentResolver, int userId, Set<String> systemCarrierAppsDisabledUntilUsed, Map<String, List<CarrierAssociatedAppEntry>> systemCarrierAssociatedAppsDisabledUntilUsed, Context context) {
        boolean hasPrivileges;
        List<AssociatedAppInfo> associatedAppList;
        LegacyPermissionManager permissionManager;
        Map<String, List<AssociatedAppInfo>> associatedApps;
        int carrierAppsHandledSdk;
        String str;
        int carrierAppsHandledSdk2;
        String str2;
        ApplicationInfo ai;
        int carrierAppsHandledSdk3;
        boolean allowDisable;
        char c;
        String str3 = callingPackage;
        TelephonyManager telephonyManager2 = telephonyManager;
        ContentResolver contentResolver2 = contentResolver;
        PackageManager packageManager = context.getPackageManager();
        LegacyPermissionManager permissionManager2 = (LegacyPermissionManager) context.getSystemService(Context.LEGACY_PERMISSION_SERVICE);
        List<ApplicationInfo> candidates = getDefaultCarrierAppCandidatesHelper(userId, systemCarrierAppsDisabledUntilUsed, context);
        if (candidates != null && !candidates.isEmpty()) {
            Map<String, List<AssociatedAppInfo>> associatedApps2 = getDefaultCarrierAssociatedAppsHelper(userId, systemCarrierAssociatedAppsDisabledUntilUsed, context);
            List<String> enabledCarrierPackages = new ArrayList<>();
            int userId2 = contentResolver.getUserId();
            String str4 = Settings.Secure.CARRIER_APPS_HANDLED;
            int carrierAppsHandledSdk4 = Settings.Secure.getIntForUser(contentResolver2, Settings.Secure.CARRIER_APPS_HANDLED, 0, userId2);
            boolean hasRunEver = carrierAppsHandledSdk4 != 0;
            boolean hasRunForSdk = carrierAppsHandledSdk4 == Build.VERSION.SDK_INT;
            try {
                Iterator<ApplicationInfo> it = candidates.iterator();
                while (it.hasNext()) {
                    try {
                        ApplicationInfo ai2 = it.next();
                        List<ApplicationInfo> candidates2 = candidates;
                        try {
                            String packageName = ai2.packageName;
                            try {
                                if (telephonyManager2 != null) {
                                    try {
                                        if (telephonyManager2.checkCarrierPrivilegesForPackageAnyPhone(packageName) == 1) {
                                            hasPrivileges = true;
                                            packageManager.setSystemAppState(packageName, 0);
                                            associatedAppList = associatedApps2.get(packageName);
                                            if (associatedAppList == null) {
                                                try {
                                                    for (AssociatedAppInfo associatedApp : associatedAppList) {
                                                        Map<String, List<AssociatedAppInfo>> associatedApps3 = associatedApps2;
                                                        LegacyPermissionManager permissionManager3 = permissionManager2;
                                                        try {
                                                            packageManager.setSystemAppState(associatedApp.appInfo.packageName, 0);
                                                            associatedApps2 = associatedApps3;
                                                            permissionManager2 = permissionManager3;
                                                        } catch (PackageManager.NameNotFoundException e) {
                                                            e = e;
                                                            Log.m103w(TAG, "Could not reach PackageManager", e);
                                                        }
                                                    }
                                                    permissionManager = permissionManager2;
                                                    associatedApps = associatedApps2;
                                                } catch (PackageManager.NameNotFoundException e2) {
                                                    e = e2;
                                                }
                                            } else {
                                                permissionManager = permissionManager2;
                                                associatedApps = associatedApps2;
                                            }
                                            int enabledSetting = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationEnabledSetting(packageName);
                                            PackageManager packageManager2 = packageManager;
                                            if (hasPrivileges) {
                                                carrierAppsHandledSdk = carrierAppsHandledSdk4;
                                                str = str4;
                                                try {
                                                    if (!isUpdatedSystemApp(ai2) && enabledSetting == 0 && (ai2.flags & 8388608) != 0) {
                                                        Log.m108i(TAG, "Update state (" + packageName + "): DISABLED_UNTIL_USED for user " + userId);
                                                        context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().setSystemAppState(packageName, 3);
                                                    }
                                                    if (associatedAppList != null) {
                                                        for (AssociatedAppInfo associatedApp2 : associatedAppList) {
                                                            if (hasRunEver) {
                                                                if (hasRunForSdk || associatedApp2.addedInSdk == -1) {
                                                                    ai = ai2;
                                                                    carrierAppsHandledSdk3 = carrierAppsHandledSdk;
                                                                } else {
                                                                    carrierAppsHandledSdk3 = carrierAppsHandledSdk;
                                                                    if (associatedApp2.addedInSdk > carrierAppsHandledSdk3) {
                                                                        ai = ai2;
                                                                        if (associatedApp2.addedInSdk <= Build.VERSION.SDK_INT) {
                                                                        }
                                                                    } else {
                                                                        ai = ai2;
                                                                    }
                                                                }
                                                                allowDisable = false;
                                                                int associatedAppEnabledSetting = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationEnabledSetting(associatedApp2.appInfo.packageName);
                                                                boolean associatedAppInstalled = (associatedApp2.appInfo.flags & 8388608) == 0;
                                                                if (!allowDisable && associatedAppEnabledSetting == 0 && associatedAppInstalled) {
                                                                    Log.m108i(TAG, "Update associated state (" + associatedApp2.appInfo.packageName + "): DISABLED_UNTIL_USED for user " + userId);
                                                                    c = 3;
                                                                    context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().setSystemAppState(associatedApp2.appInfo.packageName, 3);
                                                                } else {
                                                                    c = 3;
                                                                }
                                                                carrierAppsHandledSdk = carrierAppsHandledSdk3;
                                                                ai2 = ai;
                                                            } else {
                                                                ai = ai2;
                                                                carrierAppsHandledSdk3 = carrierAppsHandledSdk;
                                                            }
                                                            allowDisable = true;
                                                            int associatedAppEnabledSetting2 = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationEnabledSetting(associatedApp2.appInfo.packageName);
                                                            if ((associatedApp2.appInfo.flags & 8388608) == 0) {
                                                            }
                                                            if (!allowDisable) {
                                                            }
                                                            c = 3;
                                                            carrierAppsHandledSdk = carrierAppsHandledSdk3;
                                                            ai2 = ai;
                                                        }
                                                    }
                                                } catch (PackageManager.NameNotFoundException e3) {
                                                    e = e3;
                                                    Log.m103w(TAG, "Could not reach PackageManager", e);
                                                }
                                            } else {
                                                try {
                                                    String str5 = "): ENABLED for user ";
                                                    if ((isUpdatedSystemApp(ai2) || enabledSetting != 0) && enabledSetting != 4) {
                                                        try {
                                                        } catch (PackageManager.NameNotFoundException e4) {
                                                            e = e4;
                                                            Log.m103w(TAG, "Could not reach PackageManager", e);
                                                        }
                                                    }
                                                    Log.m108i(TAG, "Update state (" + packageName + "): ENABLED for user " + userId);
                                                    context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().setSystemAppState(packageName, 2);
                                                    context.createPackageContextAsUser(str3, 0, UserHandle.m145of(userId)).getPackageManager().setApplicationEnabledSetting(packageName, 1, 1);
                                                    if (associatedAppList != null) {
                                                        Iterator<AssociatedAppInfo> it2 = associatedAppList.iterator();
                                                        while (it2.hasNext()) {
                                                            AssociatedAppInfo associatedApp3 = it2.next();
                                                            Iterator<AssociatedAppInfo> it3 = it2;
                                                            String str6 = str4;
                                                            int associatedAppEnabledSetting3 = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationEnabledSetting(associatedApp3.appInfo.packageName);
                                                            boolean associatedAppInstalled2 = (associatedApp3.appInfo.flags & 8388608) != 0;
                                                            try {
                                                                if (associatedAppEnabledSetting3 != 0) {
                                                                    carrierAppsHandledSdk2 = carrierAppsHandledSdk4;
                                                                    if (associatedAppEnabledSetting3 != 4 && associatedAppInstalled2) {
                                                                        str2 = str5;
                                                                        it2 = it3;
                                                                        str4 = str6;
                                                                        carrierAppsHandledSdk4 = carrierAppsHandledSdk2;
                                                                        str5 = str2;
                                                                    }
                                                                } else {
                                                                    carrierAppsHandledSdk2 = carrierAppsHandledSdk4;
                                                                }
                                                                Log.m108i(TAG, "Update associated state (" + associatedApp3.appInfo.packageName + str5 + userId);
                                                                str2 = str5;
                                                                context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().setSystemAppState(associatedApp3.appInfo.packageName, 2);
                                                                context.createPackageContextAsUser(str3, 0, UserHandle.m145of(userId)).getPackageManager().setApplicationEnabledSetting(associatedApp3.appInfo.packageName, 1, 1);
                                                                it2 = it3;
                                                                str4 = str6;
                                                                carrierAppsHandledSdk4 = carrierAppsHandledSdk2;
                                                                str5 = str2;
                                                            } catch (PackageManager.NameNotFoundException e5) {
                                                                e = e5;
                                                                Log.m103w(TAG, "Could not reach PackageManager", e);
                                                            }
                                                        }
                                                        carrierAppsHandledSdk = carrierAppsHandledSdk4;
                                                        str = str4;
                                                    } else {
                                                        carrierAppsHandledSdk = carrierAppsHandledSdk4;
                                                        str = str4;
                                                    }
                                                    enabledCarrierPackages.add(ai2.packageName);
                                                } catch (PackageManager.NameNotFoundException e6) {
                                                    e = e6;
                                                }
                                            }
                                            carrierAppsHandledSdk4 = carrierAppsHandledSdk;
                                            str3 = callingPackage;
                                            telephonyManager2 = telephonyManager;
                                            contentResolver2 = contentResolver;
                                            candidates = candidates2;
                                            packageManager = packageManager2;
                                            associatedApps2 = associatedApps;
                                            permissionManager2 = permissionManager;
                                            str4 = str;
                                        }
                                    } catch (PackageManager.NameNotFoundException e7) {
                                        e = e7;
                                        Log.m103w(TAG, "Could not reach PackageManager", e);
                                    }
                                }
                                int enabledSetting2 = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationEnabledSetting(packageName);
                                PackageManager packageManager22 = packageManager;
                                if (hasPrivileges) {
                                }
                                carrierAppsHandledSdk4 = carrierAppsHandledSdk;
                                str3 = callingPackage;
                                telephonyManager2 = telephonyManager;
                                contentResolver2 = contentResolver;
                                candidates = candidates2;
                                packageManager = packageManager22;
                                associatedApps2 = associatedApps;
                                permissionManager2 = permissionManager;
                                str4 = str;
                            } catch (PackageManager.NameNotFoundException e8) {
                                e = e8;
                            }
                            hasPrivileges = false;
                            packageManager.setSystemAppState(packageName, 0);
                            associatedAppList = associatedApps2.get(packageName);
                            if (associatedAppList == null) {
                            }
                        } catch (PackageManager.NameNotFoundException e9) {
                            e = e9;
                        }
                    } catch (PackageManager.NameNotFoundException e10) {
                        e = e10;
                    }
                }
                LegacyPermissionManager permissionManager4 = permissionManager2;
                String str7 = str4;
                if (!hasRunEver || !hasRunForSdk) {
                    try {
                    } catch (PackageManager.NameNotFoundException e11) {
                        e = e11;
                        Log.m103w(TAG, "Could not reach PackageManager", e);
                    }
                    try {
                        Settings.Secure.putIntForUser(contentResolver, str7, Build.VERSION.SDK_INT, contentResolver.getUserId());
                    } catch (PackageManager.NameNotFoundException e12) {
                        e = e12;
                        Log.m103w(TAG, "Could not reach PackageManager", e);
                    }
                }
                if (enabledCarrierPackages.isEmpty()) {
                    return;
                }
                String[] packageNames = new String[enabledCarrierPackages.size()];
                enabledCarrierPackages.toArray(packageNames);
                try {
                    permissionManager4.grantDefaultPermissionsToEnabledCarrierApps(packageNames, UserHandle.m145of(userId), TelephonyUtils.DIRECT_EXECUTOR, new Consumer() { // from class: com.android.internal.telephony.CarrierAppUtils$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            CarrierAppUtils.lambda$disableCarrierAppsUntilPrivileged$0((Boolean) obj);
                        }
                    });
                } catch (PackageManager.NameNotFoundException e13) {
                    e = e13;
                    Log.m103w(TAG, "Could not reach PackageManager", e);
                }
            } catch (PackageManager.NameNotFoundException e14) {
                e = e14;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$disableCarrierAppsUntilPrivileged$0(Boolean isSuccess) {
    }

    public static List<ApplicationInfo> getDefaultCarrierApps(TelephonyManager telephonyManager, int userId, Context context) {
        List<ApplicationInfo> candidates = getDefaultCarrierAppCandidates(userId, context);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        for (int i = candidates.size() - 1; i >= 0; i--) {
            ApplicationInfo ai = candidates.get(i);
            String packageName = ai.packageName;
            boolean hasPrivileges = telephonyManager.checkCarrierPrivilegesForPackageAnyPhone(packageName) == 1;
            if (!hasPrivileges) {
                candidates.remove(i);
            }
        }
        return candidates;
    }

    public static List<ApplicationInfo> getDefaultCarrierAppCandidates(int userId, Context context) {
        Set<String> systemCarrierAppsDisabledUntilUsed = ((SystemConfigManager) context.getSystemService(SystemConfigManager.class)).getDisabledUntilUsedPreinstalledCarrierApps();
        return getDefaultCarrierAppCandidatesHelper(userId, systemCarrierAppsDisabledUntilUsed, context);
    }

    private static List<ApplicationInfo> getDefaultCarrierAppCandidatesHelper(int userId, Set<String> systemCarrierAppsDisabledUntilUsed, Context context) {
        if (systemCarrierAppsDisabledUntilUsed == null || systemCarrierAppsDisabledUntilUsed.isEmpty()) {
            return null;
        }
        List<ApplicationInfo> apps = new ArrayList<>(systemCarrierAppsDisabledUntilUsed.size());
        for (String packageName : systemCarrierAppsDisabledUntilUsed) {
            ApplicationInfo ai = getApplicationInfoIfSystemApp(userId, packageName, context);
            if (ai != null) {
                apps.add(ai);
            }
        }
        return apps;
    }

    private static Map<String, List<AssociatedAppInfo>> getDefaultCarrierAssociatedAppsHelper(int userId, Map<String, List<CarrierAssociatedAppEntry>> systemCarrierAssociatedAppsDisabledUntilUsed, Context context) {
        int size = systemCarrierAssociatedAppsDisabledUntilUsed.size();
        Map<String, List<AssociatedAppInfo>> associatedApps = new ArrayMap<>(size);
        for (Map.Entry<String, List<CarrierAssociatedAppEntry>> entry : systemCarrierAssociatedAppsDisabledUntilUsed.entrySet()) {
            String carrierAppPackage = entry.getKey();
            List<CarrierAssociatedAppEntry> associatedAppPackages = entry.getValue();
            for (int j = 0; j < associatedAppPackages.size(); j++) {
                CarrierAssociatedAppEntry associatedApp = associatedAppPackages.get(j);
                ApplicationInfo ai = getApplicationInfoIfSystemApp(userId, associatedApp.packageName, context);
                if (ai != null && !isUpdatedSystemApp(ai)) {
                    List<AssociatedAppInfo> appList = associatedApps.get(carrierAppPackage);
                    if (appList == null) {
                        appList = new ArrayList<>();
                        associatedApps.put(carrierAppPackage, appList);
                    }
                    appList.add(new AssociatedAppInfo(ai, associatedApp.addedInSdk));
                }
            }
        }
        return associatedApps;
    }

    private static ApplicationInfo getApplicationInfoIfSystemApp(int userId, String packageName, Context context) {
        try {
            ApplicationInfo ai = context.createContextAsUser(UserHandle.m145of(userId), 0).getPackageManager().getApplicationInfo(packageName, 537952256);
            if (ai != null) {
                return ai;
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m103w(TAG, "Could not reach PackageManager", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class AssociatedAppInfo {
        public final int addedInSdk;
        public final ApplicationInfo appInfo;

        AssociatedAppInfo(ApplicationInfo appInfo, int addedInSdk) {
            this.appInfo = appInfo;
            this.addedInSdk = addedInSdk;
        }
    }
}
