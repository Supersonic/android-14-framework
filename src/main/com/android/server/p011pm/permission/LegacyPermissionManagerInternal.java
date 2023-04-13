package com.android.server.p011pm.permission;
/* renamed from: com.android.server.pm.permission.LegacyPermissionManagerInternal */
/* loaded from: classes2.dex */
public interface LegacyPermissionManagerInternal {

    /* renamed from: com.android.server.pm.permission.LegacyPermissionManagerInternal$PackagesProvider */
    /* loaded from: classes2.dex */
    public interface PackagesProvider {
        String[] getPackages(int i);
    }

    /* renamed from: com.android.server.pm.permission.LegacyPermissionManagerInternal$SyncAdapterPackagesProvider */
    /* loaded from: classes2.dex */
    public interface SyncAdapterPackagesProvider {
        String[] getPackages(String str, int i);
    }

    int checkSoundTriggerRecordAudioPermissionForDataDelivery(int i, String str, String str2, String str3);

    void grantDefaultPermissions(int i);

    void grantDefaultPermissionsToDefaultSimCallManager(String str, int i);

    void grantDefaultPermissionsToDefaultUseOpenWifiApp(String str, int i);

    void resetRuntimePermissions();

    void scheduleReadDefaultPermissionExceptions();

    void setDialerAppPackagesProvider(PackagesProvider packagesProvider);

    void setLocationExtraPackagesProvider(PackagesProvider packagesProvider);

    void setLocationPackagesProvider(PackagesProvider packagesProvider);

    void setSimCallManagerPackagesProvider(PackagesProvider packagesProvider);

    void setSmsAppPackagesProvider(PackagesProvider packagesProvider);

    void setSyncAdapterPackagesProvider(SyncAdapterPackagesProvider syncAdapterPackagesProvider);

    void setUseOpenWifiAppPackagesProvider(PackagesProvider packagesProvider);

    void setVoiceInteractionPackagesProvider(PackagesProvider packagesProvider);
}
