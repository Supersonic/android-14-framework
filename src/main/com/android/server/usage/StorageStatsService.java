package com.android.server.usage;

import android.app.AppOpsManager;
import android.app.usage.ExternalStorageStats;
import android.app.usage.IStorageStatsManager;
import android.app.usage.StorageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelableException;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.CrateInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.p005os.IInstalld;
import android.p005os.storage.CrateMetadata;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DataUnit;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseLongArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.IoThread;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.storage.CacheQuotaStrategy;
import com.android.server.usage.StorageStatsManagerLocal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class StorageStatsService extends IStorageStatsManager.Stub {
    public static final long DEFAULT_QUOTA = DataUnit.MEBIBYTES.toBytes(64);
    public final AppOpsManager mAppOps;
    public final ArrayMap<String, SparseLongArray> mCacheQuotas;
    public final Context mContext;
    public final HandlerC1726H mHandler;
    public final Installer mInstaller;
    public final PackageManager mPackage;
    public final StorageManager mStorage;
    public final UserManager mUser;
    public final CopyOnWriteArrayList<Pair<String, StorageStatsManagerLocal.StorageStatsAugmenter>> mStorageStatsAugmenters = new CopyOnWriteArrayList<>();
    @GuardedBy({"mLock"})
    public int mStorageThresholdPercentHigh = 20;
    public final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public StorageStatsService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [android.os.IBinder, com.android.server.usage.StorageStatsService] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? storageStatsService = new StorageStatsService(getContext());
            this.mService = storageStatsService;
            publishBinderService("storagestats", storageStatsService);
        }
    }

    public StorageStatsService(Context context) {
        Context context2 = (Context) Preconditions.checkNotNull(context);
        this.mContext = context2;
        this.mAppOps = (AppOpsManager) Preconditions.checkNotNull((AppOpsManager) context.getSystemService(AppOpsManager.class));
        this.mUser = (UserManager) Preconditions.checkNotNull((UserManager) context.getSystemService(UserManager.class));
        this.mPackage = (PackageManager) Preconditions.checkNotNull(context.getPackageManager());
        StorageManager storageManager = (StorageManager) Preconditions.checkNotNull((StorageManager) context.getSystemService(StorageManager.class));
        this.mStorage = storageManager;
        this.mCacheQuotas = new ArrayMap<>();
        Installer installer = new Installer(context);
        this.mInstaller = installer;
        installer.onStart();
        invalidateMounts();
        HandlerC1726H handlerC1726H = new HandlerC1726H(IoThread.get().getLooper());
        this.mHandler = handlerC1726H;
        handlerC1726H.sendEmptyMessage(101);
        storageManager.registerListener(new StorageEventListener() { // from class: com.android.server.usage.StorageStatsService.1
            public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
                int i3 = volumeInfo.type;
                if ((i3 == 0 || i3 == 1 || i3 == 2) && i2 == 2) {
                    StorageStatsService.this.invalidateMounts();
                }
            }
        });
        LocalManagerRegistry.addManager(StorageStatsManagerLocal.class, new LocalService());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addDataScheme("package");
        context2.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.usage.StorageStatsService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context3, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_FULLY_REMOVED".equals(action)) {
                    StorageStatsService.this.mHandler.removeMessages(103);
                    StorageStatsService.this.mHandler.sendEmptyMessage(103);
                }
            }
        }, intentFilter);
        updateConfig();
        DeviceConfig.addOnPropertiesChangedListener("storage_native_boot", context2.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.usage.StorageStatsService$$ExternalSyntheticLambda3
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                StorageStatsService.this.lambda$new$0(properties);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        updateConfig();
    }

    public final void updateConfig() {
        synchronized (this.mLock) {
            this.mStorageThresholdPercentHigh = DeviceConfig.getInt("storage_native_boot", "storage_threshold_percent_high", 20);
        }
    }

    public final void invalidateMounts() {
        try {
            this.mInstaller.invalidateMounts();
        } catch (Installer.InstallerException e) {
            Slog.wtf("StorageStatsService", "Failed to invalidate mounts", e);
        }
    }

    public final void enforceStatsPermission(int i, String str) {
        String checkStatsPermission = checkStatsPermission(i, str, true);
        if (checkStatsPermission != null) {
            throw new SecurityException(checkStatsPermission);
        }
    }

    public final String checkStatsPermission(int i, String str, boolean z) {
        int checkOp;
        if (z) {
            checkOp = this.mAppOps.noteOp(43, i, str);
        } else {
            checkOp = this.mAppOps.checkOp(43, i, str);
        }
        if (checkOp != 0) {
            if (checkOp == 3) {
                if (this.mContext.checkCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS") == 0) {
                    return null;
                }
                return "Caller does not have android.permission.PACKAGE_USAGE_STATS; callingPackage=" + str + ", callingUid=" + i;
            }
            return "Package " + str + " from UID " + i + " blocked by mode " + checkOp;
        }
        return null;
    }

    public boolean isQuotaSupported(String str, String str2) {
        try {
            return this.mInstaller.isQuotaSupported(str);
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public boolean isReservedSupported(String str, String str2) {
        if (str == StorageManager.UUID_PRIVATE_INTERNAL) {
            return SystemProperties.getBoolean("vold.has_reserved", false) || Build.IS_ARC;
        }
        return false;
    }

    public long getTotalBytes(String str, String str2) {
        if (str == StorageManager.UUID_PRIVATE_INTERNAL) {
            return FileUtils.roundStorageSize(this.mStorage.getPrimaryStorageSize());
        }
        VolumeInfo findVolumeByUuid = this.mStorage.findVolumeByUuid(str);
        if (findVolumeByUuid == null) {
            throw new ParcelableException(new IOException("Failed to find storage device for UUID " + str));
        }
        return FileUtils.roundStorageSize(findVolumeByUuid.disk.size);
    }

    public long getFreeBytes(String str, String str2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                File findPathForUuid = this.mStorage.findPathForUuid(str);
                if (isQuotaSupported(str, PackageManagerShellCommandDataLoader.PACKAGE)) {
                    return findPathForUuid.getUsableSpace() + Math.max(0L, getCacheBytes(str, PackageManagerShellCommandDataLoader.PACKAGE) - this.mStorage.getStorageCacheBytes(findPathForUuid, 0));
                }
                return findPathForUuid.getUsableSpace();
            } catch (FileNotFoundException e) {
                throw new ParcelableException(e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public long getCacheBytes(String str, String str2) {
        enforceStatsPermission(Binder.getCallingUid(), str2);
        long j = 0;
        for (UserInfo userInfo : this.mUser.getUsers()) {
            j += queryStatsForUser(str, userInfo.id, null).cacheBytes;
        }
        return j;
    }

    public long getCacheQuotaBytes(String str, int i, String str2) {
        enforceStatsPermission(Binder.getCallingUid(), str2);
        if (this.mCacheQuotas.containsKey(str)) {
            return this.mCacheQuotas.get(str).get(i, DEFAULT_QUOTA);
        }
        return DEFAULT_QUOTA;
    }

    public StorageStats queryStatsForPackage(String str, final String str2, int i, String str3) {
        final boolean z;
        if (i != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        try {
            ApplicationInfo applicationInfoAsUser = this.mPackage.getApplicationInfoAsUser(str2, IInstalld.FLAG_FORCE, i);
            if (Binder.getCallingUid() == applicationInfoAsUser.uid) {
                z = checkStatsPermission(Binder.getCallingUid(), str3, false) == null;
            } else {
                enforceStatsPermission(Binder.getCallingUid(), str3);
                z = true;
            }
            if (ArrayUtils.defeatNullable(this.mPackage.getPackagesForUid(applicationInfoAsUser.uid)).length == 1) {
                return queryStatsForUid(str, applicationInfoAsUser.uid, str3);
            }
            int userId = UserHandle.getUserId(applicationInfoAsUser.uid);
            String[] strArr = {str2};
            long[] jArr = new long[1];
            String[] strArr2 = new String[0];
            if (!applicationInfoAsUser.isSystemApp() || applicationInfoAsUser.isUpdatedSystemApp()) {
                strArr2 = (String[]) ArrayUtils.appendElement(String.class, strArr2, applicationInfoAsUser.getCodePath());
            }
            String[] strArr3 = strArr2;
            final PackageStats packageStats = new PackageStats("StorageStatsService");
            try {
                this.mInstaller.getAppSize(str, strArr, i, 0, userId, jArr, strArr3, packageStats);
                if (str == StorageManager.UUID_PRIVATE_INTERNAL) {
                    final UserHandle of = UserHandle.of(i);
                    forEachStorageStatsAugmenter(new Consumer() { // from class: com.android.server.usage.StorageStatsService$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((StorageStatsManagerLocal.StorageStatsAugmenter) obj).augmentStatsForPackageForUser(packageStats, str2, of, z);
                        }
                    }, "queryStatsForPackage");
                }
                return translate(packageStats);
            } catch (Installer.InstallerException e) {
                throw new ParcelableException(new IOException(e.getMessage()));
            }
        } catch (PackageManager.NameNotFoundException e2) {
            throw new ParcelableException(e2);
        }
    }

    public StorageStats queryStatsForUid(String str, final int i, String str2) {
        final PackageStats packageStats;
        int userId = UserHandle.getUserId(i);
        int appId = UserHandle.getAppId(i);
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        boolean z = true;
        if (Binder.getCallingUid() == i) {
            if (checkStatsPermission(Binder.getCallingUid(), str2, false) != null) {
                z = false;
            }
        } else {
            enforceStatsPermission(Binder.getCallingUid(), str2);
        }
        final boolean z2 = z;
        String[] defeatNullable = ArrayUtils.defeatNullable(this.mPackage.getPackagesForUid(i));
        long[] jArr = new long[defeatNullable.length];
        String[] strArr = new String[0];
        for (String str3 : defeatNullable) {
            try {
                ApplicationInfo applicationInfoAsUser = this.mPackage.getApplicationInfoAsUser(str3, IInstalld.FLAG_FORCE, userId);
                if (!applicationInfoAsUser.isSystemApp() || applicationInfoAsUser.isUpdatedSystemApp()) {
                    strArr = (String[]) ArrayUtils.appendElement(String.class, strArr, applicationInfoAsUser.getCodePath());
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new ParcelableException(e);
            }
        }
        PackageStats packageStats2 = new PackageStats("StorageStatsService");
        try {
            String[] strArr2 = strArr;
            this.mInstaller.getAppSize(str, defeatNullable, userId, getDefaultFlags(), appId, jArr, strArr2, packageStats2);
            if (SystemProperties.getBoolean("fw.verify_storage", false)) {
                PackageStats packageStats3 = new PackageStats("StorageStatsService");
                this.mInstaller.getAppSize(str, defeatNullable, userId, 0, appId, jArr, strArr2, packageStats3);
                packageStats = packageStats2;
                checkEquals("UID " + i, packageStats3, packageStats);
            } else {
                packageStats = packageStats2;
            }
            if (str == StorageManager.UUID_PRIVATE_INTERNAL) {
                forEachStorageStatsAugmenter(new Consumer() { // from class: com.android.server.usage.StorageStatsService$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((StorageStatsManagerLocal.StorageStatsAugmenter) obj).augmentStatsForUid(packageStats, i, z2);
                    }
                }, "queryStatsForUid");
            }
            return translate(packageStats);
        } catch (Installer.InstallerException e2) {
            throw new ParcelableException(new IOException(e2.getMessage()));
        }
    }

    public StorageStats queryStatsForUser(String str, int i, String str2) {
        if (i != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        enforceStatsPermission(Binder.getCallingUid(), str2);
        int[] appIds = getAppIds(i);
        final PackageStats packageStats = new PackageStats("StorageStatsService");
        try {
            this.mInstaller.getUserSize(str, i, getDefaultFlags(), appIds, packageStats);
            if (SystemProperties.getBoolean("fw.verify_storage", false)) {
                PackageStats packageStats2 = new PackageStats("StorageStatsService");
                this.mInstaller.getUserSize(str, i, 0, appIds, packageStats2);
                checkEquals("User " + i, packageStats2, packageStats);
            }
            if (str == StorageManager.UUID_PRIVATE_INTERNAL) {
                final UserHandle of = UserHandle.of(i);
                forEachStorageStatsAugmenter(new Consumer() { // from class: com.android.server.usage.StorageStatsService$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((StorageStatsManagerLocal.StorageStatsAugmenter) obj).augmentStatsForUser(packageStats, of);
                    }
                }, "queryStatsForUser");
            }
            return translate(packageStats);
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public ExternalStorageStats queryExternalStatsForUser(String str, int i, String str2) {
        if (i != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        enforceStatsPermission(Binder.getCallingUid(), str2);
        int[] appIds = getAppIds(i);
        try {
            long[] externalSize = this.mInstaller.getExternalSize(str, i, getDefaultFlags(), appIds);
            if (SystemProperties.getBoolean("fw.verify_storage", false)) {
                long[] externalSize2 = this.mInstaller.getExternalSize(str, i, 0, appIds);
                checkEquals("External " + i, externalSize2, externalSize);
            }
            ExternalStorageStats externalStorageStats = new ExternalStorageStats();
            externalStorageStats.totalBytes = externalSize[0];
            externalStorageStats.audioBytes = externalSize[1];
            externalStorageStats.videoBytes = externalSize[2];
            externalStorageStats.imageBytes = externalSize[3];
            externalStorageStats.appBytes = externalSize[4];
            externalStorageStats.obbBytes = externalSize[5];
            return externalStorageStats;
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public final int[] getAppIds(int i) {
        int[] iArr = null;
        for (ApplicationInfo applicationInfo : this.mPackage.getInstalledApplicationsAsUser(IInstalld.FLAG_FORCE, i)) {
            int appId = UserHandle.getAppId(applicationInfo.uid);
            if (!ArrayUtils.contains(iArr, appId)) {
                iArr = ArrayUtils.appendInt(iArr, appId);
            }
        }
        return iArr;
    }

    public static int getDefaultFlags() {
        if (SystemProperties.getBoolean("fw.disable_quota", false)) {
            return 0;
        }
        return IInstalld.FLAG_USE_QUOTA;
    }

    public static void checkEquals(String str, long[] jArr, long[] jArr2) {
        for (int i = 0; i < jArr.length; i++) {
            checkEquals(str + "[" + i + "]", jArr[i], jArr2[i]);
        }
    }

    public static void checkEquals(String str, PackageStats packageStats, PackageStats packageStats2) {
        checkEquals(str + " codeSize", packageStats.codeSize, packageStats2.codeSize);
        checkEquals(str + " dataSize", packageStats.dataSize, packageStats2.dataSize);
        checkEquals(str + " cacheSize", packageStats.cacheSize, packageStats2.cacheSize);
        checkEquals(str + " externalCodeSize", packageStats.externalCodeSize, packageStats2.externalCodeSize);
        checkEquals(str + " externalDataSize", packageStats.externalDataSize, packageStats2.externalDataSize);
        checkEquals(str + " externalCacheSize", packageStats.externalCacheSize, packageStats2.externalCacheSize);
    }

    public static void checkEquals(String str, long j, long j2) {
        if (j != j2) {
            Slog.e("StorageStatsService", str + " expected " + j + " actual " + j2);
        }
    }

    public static StorageStats translate(PackageStats packageStats) {
        StorageStats storageStats = new StorageStats();
        storageStats.codeBytes = packageStats.codeSize + packageStats.externalCodeSize;
        storageStats.dataBytes = packageStats.dataSize + packageStats.externalDataSize;
        long j = packageStats.cacheSize;
        long j2 = packageStats.externalCacheSize;
        storageStats.cacheBytes = j + j2;
        storageStats.externalCacheBytes = j2;
        return storageStats;
    }

    /* renamed from: com.android.server.usage.StorageStatsService$H */
    /* loaded from: classes2.dex */
    public class HandlerC1726H extends Handler {
        public long mPreviousBytes;
        public final StatFs mStats;
        public long mTotalBytes;

        public HandlerC1726H(Looper looper) {
            super(looper);
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            this.mStats = statFs;
            this.mPreviousBytes = statFs.getAvailableBytes();
            this.mTotalBytes = statFs.getTotalBytes();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            long j;
            if (StorageStatsService.isCacheQuotaCalculationsEnabled(StorageStatsService.this.mContext.getContentResolver())) {
                switch (message.what) {
                    case 100:
                        this.mStats.restat(Environment.getDataDirectory().getAbsolutePath());
                        long abs = Math.abs(this.mPreviousBytes - this.mStats.getAvailableBytes());
                        synchronized (StorageStatsService.this.mLock) {
                            if (this.mStats.getAvailableBytes() > (this.mTotalBytes * StorageStatsService.this.mStorageThresholdPercentHigh) / 100) {
                                j = (this.mTotalBytes * 5) / 100;
                            } else {
                                j = (this.mTotalBytes * 2) / 100;
                            }
                        }
                        if (abs > j) {
                            this.mPreviousBytes = this.mStats.getAvailableBytes();
                            recalculateQuotas(getInitializedStrategy());
                            StorageStatsService.this.notifySignificantDelta();
                        }
                        sendEmptyMessageDelayed(100, 30000L);
                        return;
                    case 101:
                        CacheQuotaStrategy initializedStrategy = getInitializedStrategy();
                        this.mPreviousBytes = -1L;
                        try {
                            this.mPreviousBytes = initializedStrategy.setupQuotasFromFile();
                        } catch (IOException e) {
                            Slog.e("StorageStatsService", "An error occurred while reading the cache quota file.", e);
                        } catch (IllegalStateException e2) {
                            Slog.e("StorageStatsService", "Cache quota XML file is malformed?", e2);
                        }
                        if (this.mPreviousBytes < 0) {
                            this.mStats.restat(Environment.getDataDirectory().getAbsolutePath());
                            this.mPreviousBytes = this.mStats.getAvailableBytes();
                            recalculateQuotas(initializedStrategy);
                        }
                        sendEmptyMessageDelayed(100, 30000L);
                        sendEmptyMessageDelayed(102, 36000000L);
                        return;
                    case 102:
                        recalculateQuotas(getInitializedStrategy());
                        sendEmptyMessageDelayed(102, 36000000L);
                        return;
                    case 103:
                        recalculateQuotas(getInitializedStrategy());
                        return;
                    default:
                        return;
                }
            }
        }

        public final void recalculateQuotas(CacheQuotaStrategy cacheQuotaStrategy) {
            cacheQuotaStrategy.recalculateQuotas();
        }

        public final CacheQuotaStrategy getInitializedStrategy() {
            return new CacheQuotaStrategy(StorageStatsService.this.mContext, (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class), StorageStatsService.this.mInstaller, StorageStatsService.this.mCacheQuotas);
        }
    }

    @VisibleForTesting
    public static boolean isCacheQuotaCalculationsEnabled(ContentResolver contentResolver) {
        return Settings.Global.getInt(contentResolver, "enable_cache_quota_calculation", 1) != 0;
    }

    public void notifySignificantDelta() {
        this.mContext.getContentResolver().notifyChange(Uri.parse("content://com.android.externalstorage.documents/"), (ContentObserver) null, false);
    }

    public static void checkCratesEnable() {
        if (!SystemProperties.getBoolean("fw.storage_crates", false)) {
            throw new IllegalStateException("Storage Crate feature is disabled.");
        }
    }

    public final void enforceCratesPermission(int i, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_CRATES", str);
    }

    public static List<CrateInfo> convertCrateInfoFrom(CrateMetadata[] crateMetadataArr) {
        CrateInfo copyFrom;
        if (ArrayUtils.isEmpty(crateMetadataArr)) {
            return Collections.EMPTY_LIST;
        }
        ArrayList arrayList = new ArrayList();
        for (CrateMetadata crateMetadata : crateMetadataArr) {
            if (crateMetadata != null && !TextUtils.isEmpty(crateMetadata.f16id) && !TextUtils.isEmpty(crateMetadata.packageName) && (copyFrom = CrateInfo.copyFrom(crateMetadata.uid, crateMetadata.packageName, crateMetadata.f16id)) != null) {
                arrayList.add(copyFrom);
            }
        }
        return arrayList;
    }

    public final ParceledListSlice<CrateInfo> getAppCrates(String str, String[] strArr, int i) {
        try {
            return new ParceledListSlice<>(convertCrateInfoFrom(this.mInstaller.getAppCrates(str, strArr, i)));
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public ParceledListSlice<CrateInfo> queryCratesForPackage(String str, String str2, int i, String str3) {
        checkCratesEnable();
        if (i != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        try {
            if (Binder.getCallingUid() != this.mPackage.getApplicationInfoAsUser(str2, IInstalld.FLAG_FORCE, i).uid) {
                enforceCratesPermission(Binder.getCallingUid(), str3);
            }
            return getAppCrates(str, new String[]{str2}, i);
        } catch (PackageManager.NameNotFoundException e) {
            throw new ParcelableException(e);
        }
    }

    public ParceledListSlice<CrateInfo> queryCratesForUid(String str, int i, String str2) {
        String[] defeatNullable;
        checkCratesEnable();
        int userId = UserHandle.getUserId(i);
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        if (Binder.getCallingUid() != i) {
            enforceCratesPermission(Binder.getCallingUid(), str2);
        }
        String[] strArr = new String[0];
        for (String str3 : ArrayUtils.defeatNullable(this.mPackage.getPackagesForUid(i))) {
            if (!TextUtils.isEmpty(str3)) {
                try {
                    if (this.mPackage.getApplicationInfoAsUser(str3, IInstalld.FLAG_FORCE, userId) != null) {
                        strArr = (String[]) ArrayUtils.appendElement(String.class, strArr, str3);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new ParcelableException(e);
                }
            }
        }
        return getAppCrates(str, strArr, userId);
    }

    public ParceledListSlice<CrateInfo> queryCratesForUser(String str, int i, String str2) {
        checkCratesEnable();
        if (i != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "StorageStatsService");
        }
        enforceCratesPermission(Binder.getCallingUid(), str2);
        try {
            return new ParceledListSlice<>(convertCrateInfoFrom(this.mInstaller.getUserCrates(str, i)));
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public void forEachStorageStatsAugmenter(Consumer<StorageStatsManagerLocal.StorageStatsAugmenter> consumer, String str) {
        int size = this.mStorageStatsAugmenters.size();
        for (int i = 0; i < size; i++) {
            Pair<String, StorageStatsManagerLocal.StorageStatsAugmenter> pair = this.mStorageStatsAugmenters.get(i);
            StorageStatsManagerLocal.StorageStatsAugmenter storageStatsAugmenter = (StorageStatsManagerLocal.StorageStatsAugmenter) pair.second;
            Trace.traceBegin(524288L, str + XmlUtils.STRING_ARRAY_SEPARATOR + ((String) pair.first));
            try {
                consumer.accept(storageStatsAugmenter);
                Trace.traceEnd(524288L);
            } catch (Throwable th) {
                Trace.traceEnd(524288L);
                throw th;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class LocalService implements StorageStatsManagerLocal {
        public LocalService() {
        }

        @Override // com.android.server.usage.StorageStatsManagerLocal
        public void registerStorageStatsAugmenter(StorageStatsManagerLocal.StorageStatsAugmenter storageStatsAugmenter, String str) {
            StorageStatsService.this.mStorageStatsAugmenters.add(Pair.create(str, storageStatsAugmenter));
        }
    }
}
