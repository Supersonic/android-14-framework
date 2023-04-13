package com.android.server.p011pm;

import android.app.compat.ChangeIdStateCache;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackagePartitions;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.SuspendDialogInfo;
import android.content.pm.UserInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.content.pm.overlay.OverlayPaths;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.PatternMatcher;
import android.os.PersistableBundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.EventLogTags;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.permission.persistence.RuntimePermissionsPersistence;
import com.android.permission.persistence.RuntimePermissionsState;
import com.android.server.LocalServices;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.ResilientAtomicFile;
import com.android.server.p011pm.Settings;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.permission.LegacyPermissionDataProvider;
import com.android.server.p011pm.permission.LegacyPermissionSettings;
import com.android.server.p011pm.permission.LegacyPermissionState;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageUserState;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import com.android.server.p011pm.pkg.SuspendParams;
import com.android.server.p011pm.pkg.component.ParsedComponent;
import com.android.server.p011pm.pkg.component.ParsedIntentInfo;
import com.android.server.p011pm.pkg.component.ParsedPermission;
import com.android.server.p011pm.pkg.component.ParsedProcess;
import com.android.server.p011pm.resolution.ComponentResolver;
import com.android.server.p011pm.snapshot.PackageDataSnapshot;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import com.android.server.utils.Slogf;
import com.android.server.utils.Snappable;
import com.android.server.utils.SnapshotCache;
import com.android.server.utils.TimingsTraceAndSlog;
import com.android.server.utils.Watchable;
import com.android.server.utils.WatchableImpl;
import com.android.server.utils.Watched;
import com.android.server.utils.WatchedArrayList;
import com.android.server.utils.WatchedArrayMap;
import com.android.server.utils.WatchedArraySet;
import com.android.server.utils.WatchedSparseArray;
import com.android.server.utils.WatchedSparseIntArray;
import com.android.server.utils.Watcher;
import dalvik.annotation.optimization.NeverCompile;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* renamed from: com.android.server.pm.Settings */
/* loaded from: classes2.dex */
public final class Settings implements Watchable, Snappable, ResilientAtomicFile.ReadEventLogger {
    public static final Object[] FLAG_DUMP_SPEC;
    public static int PRE_M_APP_INFO_FLAG_CANT_SAVE_STATE = 268435456;
    public static int PRE_M_APP_INFO_FLAG_HIDDEN = 134217728;
    public static int PRE_M_APP_INFO_FLAG_PRIVILEGED = 1073741824;
    public static final Object[] PRIVATE_FLAG_DUMP_SPEC;
    @Watched(manual = true)
    public final AppIdSettingMap mAppIds;
    public final File mBackupStoppedPackagesFilename;
    @Watched
    public final WatchedSparseArray<ArraySet<String>> mBlockUninstallPackages;
    @Watched
    public final WatchedSparseArray<CrossProfileIntentResolver> mCrossProfileIntentResolvers;
    public final SnapshotCache<WatchedSparseArray<CrossProfileIntentResolver>> mCrossProfileIntentResolversSnapshot;
    @Watched
    public final WatchedSparseArray<String> mDefaultBrowserApp;
    @Watched
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final WatchedArrayMap<String, PackageSetting> mDisabledSysPackages;
    @Watched(manual = true)
    public final DomainVerificationManagerInternal mDomainVerificationManager;
    public final Handler mHandler;
    @Watched
    public final WatchedArraySet<String> mInstallerPackages;
    public final SnapshotCache<WatchedArraySet<String>> mInstallerPackagesSnapshot;
    @Watched
    public final WatchedArrayMap<String, KernelPackageState> mKernelMapping;
    public final File mKernelMappingFilename;
    public final SnapshotCache<WatchedArrayMap<String, KernelPackageState>> mKernelMappingSnapshot;
    public final KeySetManagerService mKeySetManagerService;
    @Watched
    public final WatchedArrayMap<Long, Integer> mKeySetRefs;
    public final SnapshotCache<WatchedArrayMap<Long, Integer>> mKeySetRefsSnapshot;
    public final PackageManagerTracedLock mLock;
    @Watched
    public final WatchedSparseIntArray mNextAppLinkGeneration;
    public final Watcher mObserver;
    public final File mPackageListFilename;
    public final Object mPackageRestrictionsLock;
    @Watched
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    final WatchedArrayMap<String, PackageSetting> mPackages;
    public final SnapshotCache<WatchedArrayMap<String, PackageSetting>> mPackagesSnapshot;
    @Watched
    public final WatchedArrayList<Signature> mPastSignatures;
    public final SnapshotCache<WatchedArrayList<Signature>> mPastSignaturesSnapshot;
    @GuardedBy({"mPackageRestrictionsLock"})
    public final SparseIntArray mPendingAsyncPackageRestrictionsWrites;
    @Watched
    public final WatchedArrayList<PackageSetting> mPendingPackages;
    public final SnapshotCache<WatchedArrayList<PackageSetting>> mPendingPackagesSnapshot;
    @Watched(manual = true)
    public final LegacyPermissionDataProvider mPermissionDataProvider;
    @Watched(manual = true)
    public final LegacyPermissionSettings mPermissions;
    @Watched
    public final WatchedSparseArray<PersistentPreferredIntentResolver> mPersistentPreferredActivities;
    public final SnapshotCache<WatchedSparseArray<PersistentPreferredIntentResolver>> mPersistentPreferredActivitiesSnapshot;
    @Watched
    public final WatchedSparseArray<PreferredIntentResolver> mPreferredActivities;
    public final SnapshotCache<WatchedSparseArray<PreferredIntentResolver>> mPreferredActivitiesSnapshot;
    public final File mPreviousSettingsFilename;
    public final StringBuilder mReadMessages;
    @Watched
    public final WatchedArrayMap<String, String> mRenamedPackages;
    @Watched(manual = true)
    public final RuntimePermissionPersistence mRuntimePermissionsPersistence;
    public final File mSettingsFilename;
    public final File mSettingsReserveCopyFilename;
    @Watched
    public final WatchedArrayMap<String, SharedUserSetting> mSharedUsers;
    public final SnapshotCache<Settings> mSnapshot;
    public final File mStoppedPackagesFilename;
    public final File mSystemDir;
    @Watched(manual = true)
    public VerifierDeviceIdentity mVerifierDeviceIdentity;
    @Watched
    public final WatchedArrayMap<String, VersionInfo> mVersion;
    public final WatchableImpl mWatchable;

    @Override // com.android.server.utils.Watchable
    public void registerObserver(Watcher watcher) {
        this.mWatchable.registerObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public void unregisterObserver(Watcher watcher) {
        this.mWatchable.unregisterObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public boolean isRegisteredObserver(Watcher watcher) {
        return this.mWatchable.isRegisteredObserver(watcher);
    }

    @Override // com.android.server.utils.Watchable
    public void dispatchChange(Watchable watchable) {
        this.mWatchable.dispatchChange(watchable);
    }

    public void onChanged() {
        dispatchChange(this);
    }

    /* renamed from: com.android.server.pm.Settings$KernelPackageState */
    /* loaded from: classes2.dex */
    public static final class KernelPackageState {
        public int appId;
        public int[] excludedUserIds;

        public KernelPackageState() {
        }
    }

    /* renamed from: com.android.server.pm.Settings$VersionInfo */
    /* loaded from: classes2.dex */
    public static class VersionInfo {
        public String buildFingerprint;
        public int databaseVersion;
        public String fingerprint;
        public int sdkVersion;

        public void forceCurrent() {
            this.sdkVersion = Build.VERSION.SDK_INT;
            this.databaseVersion = 3;
            this.buildFingerprint = Build.FINGERPRINT;
            this.fingerprint = PackagePartitions.FINGERPRINT;
        }
    }

    public final SnapshotCache<Settings> makeCache() {
        return new SnapshotCache<Settings>(this, this) { // from class: com.android.server.pm.Settings.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.server.utils.SnapshotCache
            public Settings createSnapshot() {
                Settings settings = new Settings();
                settings.mWatchable.seal();
                return settings;
            }
        };
    }

    public final void registerObservers() {
        this.mPackages.registerObserver(this.mObserver);
        this.mInstallerPackages.registerObserver(this.mObserver);
        this.mKernelMapping.registerObserver(this.mObserver);
        this.mDisabledSysPackages.registerObserver(this.mObserver);
        this.mBlockUninstallPackages.registerObserver(this.mObserver);
        this.mVersion.registerObserver(this.mObserver);
        this.mPreferredActivities.registerObserver(this.mObserver);
        this.mPersistentPreferredActivities.registerObserver(this.mObserver);
        this.mCrossProfileIntentResolvers.registerObserver(this.mObserver);
        this.mSharedUsers.registerObserver(this.mObserver);
        this.mAppIds.registerObserver(this.mObserver);
        this.mRenamedPackages.registerObserver(this.mObserver);
        this.mNextAppLinkGeneration.registerObserver(this.mObserver);
        this.mDefaultBrowserApp.registerObserver(this.mObserver);
        this.mPendingPackages.registerObserver(this.mObserver);
        this.mPastSignatures.registerObserver(this.mObserver);
        this.mKeySetRefs.registerObserver(this.mObserver);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Settings(Map<String, PackageSetting> map) {
        this.mWatchable = new WatchableImpl();
        this.mPackageRestrictionsLock = new Object();
        this.mPendingAsyncPackageRestrictionsWrites = new SparseIntArray();
        this.mDisabledSysPackages = new WatchedArrayMap<>();
        this.mBlockUninstallPackages = new WatchedSparseArray<>();
        this.mVersion = new WatchedArrayMap<>();
        this.mSharedUsers = new WatchedArrayMap<>();
        this.mRenamedPackages = new WatchedArrayMap<>();
        this.mDefaultBrowserApp = new WatchedSparseArray<>();
        this.mNextAppLinkGeneration = new WatchedSparseIntArray();
        this.mReadMessages = new StringBuilder();
        Watcher watcher = new Watcher() { // from class: com.android.server.pm.Settings.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                Settings.this.dispatchChange(watchable);
            }
        };
        this.mObserver = watcher;
        WatchedArrayMap<String, PackageSetting> watchedArrayMap = new WatchedArrayMap<>();
        this.mPackages = watchedArrayMap;
        this.mPackagesSnapshot = new SnapshotCache.Auto(watchedArrayMap, watchedArrayMap, "Settings.mPackages");
        WatchedArrayMap<String, KernelPackageState> watchedArrayMap2 = new WatchedArrayMap<>();
        this.mKernelMapping = watchedArrayMap2;
        this.mKernelMappingSnapshot = new SnapshotCache.Auto(watchedArrayMap2, watchedArrayMap2, "Settings.mKernelMapping");
        WatchedArraySet<String> watchedArraySet = new WatchedArraySet<>();
        this.mInstallerPackages = watchedArraySet;
        this.mInstallerPackagesSnapshot = new SnapshotCache.Auto(watchedArraySet, watchedArraySet, "Settings.mInstallerPackages");
        WatchedSparseArray<PreferredIntentResolver> watchedSparseArray = new WatchedSparseArray<>();
        this.mPreferredActivities = watchedSparseArray;
        this.mPreferredActivitiesSnapshot = new SnapshotCache.Auto(watchedSparseArray, watchedSparseArray, "Settings.mPreferredActivities");
        WatchedSparseArray<PersistentPreferredIntentResolver> watchedSparseArray2 = new WatchedSparseArray<>();
        this.mPersistentPreferredActivities = watchedSparseArray2;
        this.mPersistentPreferredActivitiesSnapshot = new SnapshotCache.Auto(watchedSparseArray2, watchedSparseArray2, "Settings.mPersistentPreferredActivities");
        WatchedSparseArray<CrossProfileIntentResolver> watchedSparseArray3 = new WatchedSparseArray<>();
        this.mCrossProfileIntentResolvers = watchedSparseArray3;
        this.mCrossProfileIntentResolversSnapshot = new SnapshotCache.Auto(watchedSparseArray3, watchedSparseArray3, "Settings.mCrossProfileIntentResolvers");
        WatchedArrayList<Signature> watchedArrayList = new WatchedArrayList<>();
        this.mPastSignatures = watchedArrayList;
        this.mPastSignaturesSnapshot = new SnapshotCache.Auto(watchedArrayList, watchedArrayList, "Settings.mPastSignatures");
        WatchedArrayMap<Long, Integer> watchedArrayMap3 = new WatchedArrayMap<>();
        this.mKeySetRefs = watchedArrayMap3;
        this.mKeySetRefsSnapshot = new SnapshotCache.Auto(watchedArrayMap3, watchedArrayMap3, "Settings.mKeySetRefs");
        WatchedArrayList<PackageSetting> watchedArrayList2 = new WatchedArrayList<>();
        this.mPendingPackages = watchedArrayList2;
        this.mPendingPackagesSnapshot = new SnapshotCache.Auto(watchedArrayList2, watchedArrayList2, "Settings.mPendingPackages");
        this.mKeySetManagerService = new KeySetManagerService(watchedArrayMap);
        this.mHandler = new Handler(BackgroundThread.getHandler().getLooper());
        this.mLock = new PackageManagerTracedLock();
        watchedArrayMap.putAll(map);
        this.mAppIds = new AppIdSettingMap();
        this.mSystemDir = null;
        this.mPermissions = null;
        this.mRuntimePermissionsPersistence = null;
        this.mPermissionDataProvider = null;
        this.mSettingsFilename = null;
        this.mSettingsReserveCopyFilename = null;
        this.mPreviousSettingsFilename = null;
        this.mPackageListFilename = null;
        this.mStoppedPackagesFilename = null;
        this.mBackupStoppedPackagesFilename = null;
        this.mKernelMappingFilename = null;
        this.mDomainVerificationManager = null;
        registerObservers();
        Watchable.verifyWatchedAttributes(this, watcher);
        this.mSnapshot = makeCache();
    }

    public Settings(File file, RuntimePermissionsPersistence runtimePermissionsPersistence, LegacyPermissionDataProvider legacyPermissionDataProvider, DomainVerificationManagerInternal domainVerificationManagerInternal, Handler handler, PackageManagerTracedLock packageManagerTracedLock) {
        this.mWatchable = new WatchableImpl();
        this.mPackageRestrictionsLock = new Object();
        this.mPendingAsyncPackageRestrictionsWrites = new SparseIntArray();
        this.mDisabledSysPackages = new WatchedArrayMap<>();
        this.mBlockUninstallPackages = new WatchedSparseArray<>();
        this.mVersion = new WatchedArrayMap<>();
        this.mSharedUsers = new WatchedArrayMap<>();
        this.mRenamedPackages = new WatchedArrayMap<>();
        this.mDefaultBrowserApp = new WatchedSparseArray<>();
        this.mNextAppLinkGeneration = new WatchedSparseIntArray();
        this.mReadMessages = new StringBuilder();
        Watcher watcher = new Watcher() { // from class: com.android.server.pm.Settings.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                Settings.this.dispatchChange(watchable);
            }
        };
        this.mObserver = watcher;
        WatchedArrayMap<String, PackageSetting> watchedArrayMap = new WatchedArrayMap<>();
        this.mPackages = watchedArrayMap;
        this.mPackagesSnapshot = new SnapshotCache.Auto(watchedArrayMap, watchedArrayMap, "Settings.mPackages");
        WatchedArrayMap<String, KernelPackageState> watchedArrayMap2 = new WatchedArrayMap<>();
        this.mKernelMapping = watchedArrayMap2;
        this.mKernelMappingSnapshot = new SnapshotCache.Auto(watchedArrayMap2, watchedArrayMap2, "Settings.mKernelMapping");
        WatchedArraySet<String> watchedArraySet = new WatchedArraySet<>();
        this.mInstallerPackages = watchedArraySet;
        this.mInstallerPackagesSnapshot = new SnapshotCache.Auto(watchedArraySet, watchedArraySet, "Settings.mInstallerPackages");
        WatchedSparseArray<PreferredIntentResolver> watchedSparseArray = new WatchedSparseArray<>();
        this.mPreferredActivities = watchedSparseArray;
        this.mPreferredActivitiesSnapshot = new SnapshotCache.Auto(watchedSparseArray, watchedSparseArray, "Settings.mPreferredActivities");
        WatchedSparseArray<PersistentPreferredIntentResolver> watchedSparseArray2 = new WatchedSparseArray<>();
        this.mPersistentPreferredActivities = watchedSparseArray2;
        this.mPersistentPreferredActivitiesSnapshot = new SnapshotCache.Auto(watchedSparseArray2, watchedSparseArray2, "Settings.mPersistentPreferredActivities");
        WatchedSparseArray<CrossProfileIntentResolver> watchedSparseArray3 = new WatchedSparseArray<>();
        this.mCrossProfileIntentResolvers = watchedSparseArray3;
        this.mCrossProfileIntentResolversSnapshot = new SnapshotCache.Auto(watchedSparseArray3, watchedSparseArray3, "Settings.mCrossProfileIntentResolvers");
        WatchedArrayList<Signature> watchedArrayList = new WatchedArrayList<>();
        this.mPastSignatures = watchedArrayList;
        this.mPastSignaturesSnapshot = new SnapshotCache.Auto(watchedArrayList, watchedArrayList, "Settings.mPastSignatures");
        WatchedArrayMap<Long, Integer> watchedArrayMap3 = new WatchedArrayMap<>();
        this.mKeySetRefs = watchedArrayMap3;
        this.mKeySetRefsSnapshot = new SnapshotCache.Auto(watchedArrayMap3, watchedArrayMap3, "Settings.mKeySetRefs");
        WatchedArrayList<PackageSetting> watchedArrayList2 = new WatchedArrayList<>();
        this.mPendingPackages = watchedArrayList2;
        this.mPendingPackagesSnapshot = new SnapshotCache.Auto(watchedArrayList2, watchedArrayList2, "Settings.mPendingPackages");
        this.mKeySetManagerService = new KeySetManagerService(watchedArrayMap);
        this.mHandler = handler;
        this.mLock = packageManagerTracedLock;
        this.mAppIds = new AppIdSettingMap();
        this.mPermissions = new LegacyPermissionSettings(packageManagerTracedLock);
        this.mRuntimePermissionsPersistence = new RuntimePermissionPersistence(runtimePermissionsPersistence, new Consumer<Integer>() { // from class: com.android.server.pm.Settings.3
            @Override // java.util.function.Consumer
            public void accept(Integer num) {
                RuntimePermissionPersistence runtimePermissionPersistence = Settings.this.mRuntimePermissionsPersistence;
                int intValue = num.intValue();
                LegacyPermissionDataProvider legacyPermissionDataProvider2 = Settings.this.mPermissionDataProvider;
                Settings settings = Settings.this;
                runtimePermissionPersistence.writeStateForUser(intValue, legacyPermissionDataProvider2, settings.mPackages, settings.mSharedUsers, settings.mHandler, Settings.this.mLock, false);
            }
        });
        this.mPermissionDataProvider = legacyPermissionDataProvider;
        File file2 = new File(file, "system");
        this.mSystemDir = file2;
        file2.mkdirs();
        FileUtils.setPermissions(file2.toString(), 509, -1, -1);
        this.mSettingsFilename = new File(file2, "packages.xml");
        this.mSettingsReserveCopyFilename = new File(file2, "packages.xml.reservecopy");
        this.mPreviousSettingsFilename = new File(file2, "packages-backup.xml");
        File file3 = new File(file2, "packages.list");
        this.mPackageListFilename = file3;
        FileUtils.setPermissions(file3, FrameworkStatsLog.DISPLAY_HBM_STATE_CHANGED, 1000, 1032);
        File file4 = new File("/config/sdcardfs");
        this.mKernelMappingFilename = file4.exists() ? file4 : null;
        this.mStoppedPackagesFilename = new File(file2, "packages-stopped.xml");
        this.mBackupStoppedPackagesFilename = new File(file2, "packages-stopped-backup.xml");
        this.mDomainVerificationManager = domainVerificationManagerInternal;
        registerObservers();
        Watchable.verifyWatchedAttributes(this, watcher);
        this.mSnapshot = makeCache();
    }

    public Settings(Settings settings) {
        this.mWatchable = new WatchableImpl();
        this.mPackageRestrictionsLock = new Object();
        this.mPendingAsyncPackageRestrictionsWrites = new SparseIntArray();
        WatchedArrayMap<String, PackageSetting> watchedArrayMap = new WatchedArrayMap<>();
        this.mDisabledSysPackages = watchedArrayMap;
        WatchedSparseArray<ArraySet<String>> watchedSparseArray = new WatchedSparseArray<>();
        this.mBlockUninstallPackages = watchedSparseArray;
        WatchedArrayMap<String, VersionInfo> watchedArrayMap2 = new WatchedArrayMap<>();
        this.mVersion = watchedArrayMap2;
        WatchedArrayMap<String, SharedUserSetting> watchedArrayMap3 = new WatchedArrayMap<>();
        this.mSharedUsers = watchedArrayMap3;
        WatchedArrayMap<String, String> watchedArrayMap4 = new WatchedArrayMap<>();
        this.mRenamedPackages = watchedArrayMap4;
        WatchedSparseArray<String> watchedSparseArray2 = new WatchedSparseArray<>();
        this.mDefaultBrowserApp = watchedSparseArray2;
        WatchedSparseIntArray watchedSparseIntArray = new WatchedSparseIntArray();
        this.mNextAppLinkGeneration = watchedSparseIntArray;
        this.mReadMessages = new StringBuilder();
        this.mObserver = new Watcher() { // from class: com.android.server.pm.Settings.1
            @Override // com.android.server.utils.Watcher
            public void onChange(Watchable watchable) {
                Settings.this.dispatchChange(watchable);
            }
        };
        WatchedArrayMap<String, PackageSetting> snapshot = settings.mPackagesSnapshot.snapshot();
        this.mPackages = snapshot;
        this.mPackagesSnapshot = new SnapshotCache.Sealed();
        this.mKernelMapping = settings.mKernelMappingSnapshot.snapshot();
        this.mKernelMappingSnapshot = new SnapshotCache.Sealed();
        this.mInstallerPackages = settings.mInstallerPackagesSnapshot.snapshot();
        this.mInstallerPackagesSnapshot = new SnapshotCache.Sealed();
        this.mKeySetManagerService = new KeySetManagerService(settings.mKeySetManagerService, snapshot);
        this.mHandler = null;
        this.mLock = null;
        this.mRuntimePermissionsPersistence = settings.mRuntimePermissionsPersistence;
        this.mSettingsFilename = null;
        this.mSettingsReserveCopyFilename = null;
        this.mPreviousSettingsFilename = null;
        this.mPackageListFilename = null;
        this.mStoppedPackagesFilename = null;
        this.mBackupStoppedPackagesFilename = null;
        this.mKernelMappingFilename = null;
        this.mDomainVerificationManager = settings.mDomainVerificationManager;
        watchedArrayMap.snapshot(settings.mDisabledSysPackages);
        watchedSparseArray.snapshot(settings.mBlockUninstallPackages);
        watchedArrayMap2.putAll(settings.mVersion);
        this.mVerifierDeviceIdentity = settings.mVerifierDeviceIdentity;
        this.mPreferredActivities = settings.mPreferredActivitiesSnapshot.snapshot();
        this.mPreferredActivitiesSnapshot = new SnapshotCache.Sealed();
        this.mPersistentPreferredActivities = settings.mPersistentPreferredActivitiesSnapshot.snapshot();
        this.mPersistentPreferredActivitiesSnapshot = new SnapshotCache.Sealed();
        this.mCrossProfileIntentResolvers = settings.mCrossProfileIntentResolversSnapshot.snapshot();
        this.mCrossProfileIntentResolversSnapshot = new SnapshotCache.Sealed();
        watchedArrayMap3.snapshot(settings.mSharedUsers);
        this.mAppIds = settings.mAppIds.snapshot();
        this.mPastSignatures = settings.mPastSignaturesSnapshot.snapshot();
        this.mPastSignaturesSnapshot = new SnapshotCache.Sealed();
        this.mKeySetRefs = settings.mKeySetRefsSnapshot.snapshot();
        this.mKeySetRefsSnapshot = new SnapshotCache.Sealed();
        watchedArrayMap4.snapshot(settings.mRenamedPackages);
        watchedSparseIntArray.snapshot(settings.mNextAppLinkGeneration);
        watchedSparseArray2.snapshot(settings.mDefaultBrowserApp);
        this.mPendingPackages = settings.mPendingPackagesSnapshot.snapshot();
        this.mPendingPackagesSnapshot = new SnapshotCache.Sealed();
        this.mSystemDir = null;
        this.mPermissions = settings.mPermissions;
        this.mPermissionDataProvider = settings.mPermissionDataProvider;
        this.mSnapshot = new SnapshotCache.Sealed();
    }

    @Override // com.android.server.utils.Snappable
    public Settings snapshot() {
        return this.mSnapshot.snapshot();
    }

    public final void invalidatePackageCache() {
        PackageManagerService.invalidatePackageInfoCache();
        ChangeIdStateCache.invalidate();
        onChanged();
    }

    public PackageSetting getPackageLPr(String str) {
        return this.mPackages.get(str);
    }

    public WatchedArrayMap<String, PackageSetting> getPackagesLocked() {
        return this.mPackages;
    }

    public WatchedArrayMap<String, PackageSetting> getDisabledSystemPackagesLocked() {
        return this.mDisabledSysPackages;
    }

    public KeySetManagerService getKeySetManagerService() {
        return this.mKeySetManagerService;
    }

    public String getRenamedPackageLPr(String str) {
        return this.mRenamedPackages.get(str);
    }

    public String addRenamedPackageLPw(String str, String str2) {
        return this.mRenamedPackages.put(str, str2);
    }

    public void removeRenamedPackageLPw(String str) {
        this.mRenamedPackages.remove(str);
    }

    public void pruneRenamedPackagesLPw() {
        for (int size = this.mRenamedPackages.size() - 1; size >= 0; size--) {
            if (this.mPackages.get(this.mRenamedPackages.valueAt(size)) == null) {
                this.mRenamedPackages.removeAt(size);
            }
        }
    }

    public SharedUserSetting getSharedUserLPw(String str, int i, int i2, boolean z) throws PackageManagerException {
        SharedUserSetting sharedUserSetting = this.mSharedUsers.get(str);
        if (sharedUserSetting == null && z) {
            sharedUserSetting = new SharedUserSetting(str, i, i2);
            int acquireAndRegisterNewAppId = this.mAppIds.acquireAndRegisterNewAppId(sharedUserSetting);
            sharedUserSetting.mAppId = acquireAndRegisterNewAppId;
            if (acquireAndRegisterNewAppId < 0) {
                throw new PackageManagerException(-4, "Creating shared user " + str + " failed");
            }
            Log.i("PackageManager", "New shared user " + str + ": id=" + sharedUserSetting.mAppId);
            this.mSharedUsers.put(str, sharedUserSetting);
        }
        return sharedUserSetting;
    }

    public Collection<SharedUserSetting> getAllSharedUsersLPw() {
        return this.mSharedUsers.values();
    }

    public boolean disableSystemPackageLPw(String str, boolean z) {
        PackageSetting packageSetting = this.mPackages.get(str);
        boolean z2 = false;
        if (packageSetting == null) {
            Log.w("PackageManager", "Package " + str + " is not an installed package");
            return false;
        }
        if (this.mDisabledSysPackages.get(str) == null && packageSetting.getPkg() != null && packageSetting.isSystem() && !packageSetting.isUpdatedSystemApp()) {
            PackageSetting packageSetting2 = z ? new PackageSetting(packageSetting) : packageSetting;
            z2 = true;
            packageSetting.getPkgState().setUpdatedSystemApp(true);
            this.mDisabledSysPackages.put(str, packageSetting2);
            SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(packageSetting2);
            if (sharedUserSettingLPr != null) {
                sharedUserSettingLPr.mDisabledPackages.add(packageSetting2);
            }
        }
        return z2;
    }

    public PackageSetting enableSystemPackageLPw(String str) {
        PackageSetting packageSetting = this.mDisabledSysPackages.get(str);
        if (packageSetting == null) {
            Log.w("PackageManager", "Package " + str + " is not disabled");
            return null;
        }
        SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(packageSetting);
        if (sharedUserSettingLPr != null) {
            sharedUserSettingLPr.mDisabledPackages.remove(packageSetting);
        }
        packageSetting.getPkgState().setUpdatedSystemApp(false);
        PackageSetting addPackageLPw = addPackageLPw(str, packageSetting.getRealName(), packageSetting.getPath(), packageSetting.getLegacyNativeLibraryPath(), packageSetting.getPrimaryCpuAbiLegacy(), packageSetting.getSecondaryCpuAbiLegacy(), packageSetting.getCpuAbiOverride(), packageSetting.getAppId(), packageSetting.getVersionCode(), packageSetting.getFlags(), packageSetting.getPrivateFlags(), packageSetting.getUsesSdkLibraries(), packageSetting.getUsesSdkLibrariesVersionsMajor(), packageSetting.getUsesStaticLibraries(), packageSetting.getUsesStaticLibrariesVersions(), packageSetting.getMimeGroups(), this.mDomainVerificationManager.generateNewId());
        if (addPackageLPw != null) {
            addPackageLPw.getPkgState().setUpdatedSystemApp(false);
        }
        this.mDisabledSysPackages.remove(str);
        return addPackageLPw;
    }

    public boolean isDisabledSystemPackageLPr(String str) {
        return this.mDisabledSysPackages.containsKey(str);
    }

    public void removeDisabledSystemPackageLPw(String str) {
        SharedUserSetting sharedUserSettingLPr;
        PackageSetting remove = this.mDisabledSysPackages.remove(str);
        if (remove == null || (sharedUserSettingLPr = getSharedUserSettingLPr(remove)) == null) {
            return;
        }
        sharedUserSettingLPr.mDisabledPackages.remove(remove);
        checkAndPruneSharedUserLPw(sharedUserSettingLPr, false);
    }

    public PackageSetting addPackageLPw(String str, String str2, File file, String str3, String str4, String str5, String str6, int i, long j, int i2, int i3, String[] strArr, long[] jArr, String[] strArr2, long[] jArr2, Map<String, Set<String>> map, UUID uuid) {
        PackageSetting packageSetting = this.mPackages.get(str);
        if (packageSetting != null) {
            if (packageSetting.getAppId() == i) {
                return packageSetting;
            }
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate package, keeping first: " + str);
            return null;
        }
        PackageSetting packageSetting2 = new PackageSetting(str, str2, file, str3, str4, str5, str6, j, i2, i3, 0, strArr, jArr, strArr2, jArr2, map, uuid);
        packageSetting2.setAppId(i);
        if (this.mAppIds.registerExistingAppId(i, packageSetting2, str)) {
            this.mPackages.put(str, packageSetting2);
            return packageSetting2;
        }
        return null;
    }

    public SharedUserSetting addSharedUserLPw(String str, int i, int i2, int i3) {
        SharedUserSetting sharedUserSetting = this.mSharedUsers.get(str);
        if (sharedUserSetting != null) {
            if (sharedUserSetting.mAppId == i) {
                return sharedUserSetting;
            }
            PackageManagerService.reportSettingsProblem(6, "Adding duplicate shared user, keeping first: " + str);
            return null;
        }
        SharedUserSetting sharedUserSetting2 = new SharedUserSetting(str, i2, i3);
        sharedUserSetting2.mAppId = i;
        if (this.mAppIds.registerExistingAppId(i, sharedUserSetting2, str)) {
            this.mSharedUsers.put(str, sharedUserSetting2);
            return sharedUserSetting2;
        }
        return null;
    }

    public void pruneSharedUsersLPw() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Map.Entry<String, SharedUserSetting> entry : this.mSharedUsers.entrySet()) {
            SharedUserSetting value = entry.getValue();
            if (value == null) {
                arrayList.add(entry.getKey());
            } else {
                WatchedArraySet<PackageSetting> packageSettings = value.getPackageSettings();
                boolean z = false;
                for (int size = packageSettings.size() - 1; size >= 0; size--) {
                    if (this.mPackages.get(packageSettings.valueAt(size).getPackageName()) == null) {
                        packageSettings.removeAt(size);
                        z = true;
                    }
                }
                WatchedArraySet<PackageSetting> disabledPackageSettings = value.getDisabledPackageSettings();
                for (int size2 = disabledPackageSettings.size() - 1; size2 >= 0; size2--) {
                    if (this.mDisabledSysPackages.get(disabledPackageSettings.valueAt(size2).getPackageName()) == null) {
                        disabledPackageSettings.removeAt(size2);
                        z = true;
                    }
                }
                if (z) {
                    value.onChanged();
                }
                if (packageSettings.isEmpty() && disabledPackageSettings.isEmpty()) {
                    arrayList2.add(value);
                }
            }
        }
        final WatchedArrayMap<String, SharedUserSetting> watchedArrayMap = this.mSharedUsers;
        Objects.requireNonNull(watchedArrayMap);
        arrayList.forEach(new Consumer() { // from class: com.android.server.pm.Settings$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WatchedArrayMap.this.remove((String) obj);
            }
        });
        arrayList2.forEach(new Consumer() { // from class: com.android.server.pm.Settings$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Settings.this.lambda$pruneSharedUsersLPw$0((SharedUserSetting) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$pruneSharedUsersLPw$0(SharedUserSetting sharedUserSetting) {
        checkAndPruneSharedUserLPw(sharedUserSetting, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x00fe, code lost:
        if (r5.preCreated == false) goto L28;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static PackageSetting createNewSetting(String str, PackageSetting packageSetting, PackageSetting packageSetting2, String str2, SharedUserSetting sharedUserSetting, File file, String str3, String str4, String str5, long j, int i, int i2, UserHandle userHandle, boolean z, boolean z2, boolean z3, boolean z4, UserManagerService userManagerService, String[] strArr, long[] jArr, String[] strArr2, long[] jArr2, Set<String> set, UUID uuid) {
        int i3;
        boolean z5;
        if (packageSetting != null) {
            PackageSetting domainSetId = new PackageSetting(packageSetting, str).setPath(file).setLegacyNativeLibraryPath(str3).setPrimaryCpuAbi(str4).setSecondaryCpuAbi(str5).setSignatures(new PackageSignatures()).setLongVersionCode(j).setUsesSdkLibraries(strArr).setUsesSdkLibrariesVersionsMajor(jArr).setUsesStaticLibraries(strArr2).setUsesStaticLibrariesVersions(jArr2).setLastModifiedTime(file.lastModified()).setDomainSetId(uuid);
            domainSetId.setFlags(i).setPrivateFlags(i2);
            return domainSetId;
        }
        int identifier = userHandle != null ? userHandle.getIdentifier() : 0;
        PackageSetting packageSetting3 = new PackageSetting(str, str2, file, str3, str4, str5, null, j, i, i2, 0, strArr, jArr, strArr2, jArr2, createMimeGroups(set), uuid);
        packageSetting3.setLastModifiedTime(file.lastModified());
        if (sharedUserSetting != null) {
            packageSetting3.setSharedUserAppId(sharedUserSetting.mAppId);
        }
        if ((i & 1) == 0) {
            List<UserInfo> allUsers = getAllUsers(userManagerService);
            if (allUsers != null && z) {
                Iterator<UserInfo> it = allUsers.iterator();
                while (it.hasNext()) {
                    UserInfo next = it.next();
                    if (userHandle != null) {
                        i3 = identifier;
                        if (i3 == -1) {
                            if (!isAdbInstallDisallowed(userManagerService, next.id)) {
                            }
                        }
                        if (i3 != next.id) {
                            z5 = false;
                            packageSetting3.setUserState(next.id, 0L, 0, z5, true, true, false, 0, null, z2, z3, null, null, null, 0, 0, null, null, 0L);
                            identifier = i3;
                        }
                    } else {
                        i3 = identifier;
                    }
                    z5 = true;
                    packageSetting3.setUserState(next.id, 0L, 0, z5, true, true, false, 0, null, z2, z3, null, null, null, 0, 0, null, null, 0L);
                    identifier = i3;
                }
            }
        } else {
            int i4 = identifier;
            if (z4) {
                packageSetting3.setStopped(true, i4);
            }
        }
        if (sharedUserSetting != null) {
            packageSetting3.setAppId(sharedUserSetting.mAppId);
            return packageSetting3;
        } else if (packageSetting2 != null) {
            packageSetting3.setSignatures(new PackageSignatures(packageSetting2.getSignatures()));
            packageSetting3.setAppId(packageSetting2.getAppId());
            packageSetting3.getLegacyPermissionState().copyFrom(packageSetting2.getLegacyPermissionState());
            List<UserInfo> allUsers2 = getAllUsers(userManagerService);
            if (allUsers2 != null) {
                for (UserInfo userInfo : allUsers2) {
                    int i5 = userInfo.id;
                    packageSetting3.setDisabledComponentsCopy(packageSetting2.getDisabledComponents(i5), i5);
                    packageSetting3.setEnabledComponentsCopy(packageSetting2.getEnabledComponents(i5), i5);
                }
                return packageSetting3;
            }
            return packageSetting3;
        } else {
            return packageSetting3;
        }
    }

    public static Map<String, Set<String>> createMimeGroups(Set<String> set) {
        if (set == null) {
            return null;
        }
        return new KeySetToValueMap(set, new ArraySet());
    }

    public static void updatePackageSetting(PackageSetting packageSetting, PackageSetting packageSetting2, SharedUserSetting sharedUserSetting, SharedUserSetting sharedUserSetting2, File file, String str, String str2, String str3, int i, int i2, UserManagerService userManagerService, String[] strArr, long[] jArr, String[] strArr2, long[] jArr2, Set<String> set, UUID uuid) throws PackageManagerException {
        List<UserInfo> allUsers;
        String packageName = packageSetting.getPackageName();
        if (sharedUserSetting2 != null) {
            if (!Objects.equals(sharedUserSetting, sharedUserSetting2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Package ");
                sb.append(packageName);
                sb.append(" shared user changed from ");
                sb.append(sharedUserSetting != null ? sharedUserSetting.name : "<nothing>");
                sb.append(" to ");
                sb.append(sharedUserSetting2.name);
                PackageManagerService.reportSettingsProblem(5, sb.toString());
                throw new PackageManagerException(-24, "Updating application package " + packageName + " failed");
            }
            packageSetting.setSharedUserAppId(sharedUserSetting2.mAppId);
        } else {
            packageSetting.setSharedUserAppId(-1);
        }
        if (!packageSetting.getPath().equals(file)) {
            boolean isSystem = packageSetting.isSystem();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Update");
            sb2.append(isSystem ? " system" : "");
            sb2.append(" package ");
            sb2.append(packageName);
            sb2.append(" code path from ");
            sb2.append(packageSetting.getPathString());
            sb2.append(" to ");
            sb2.append(file.toString());
            sb2.append("; Retain data and using new");
            Slog.i("PackageManager", sb2.toString());
            if (!isSystem) {
                if ((i & 1) != 0 && packageSetting2 == null && (allUsers = getAllUsers(userManagerService)) != null) {
                    for (UserInfo userInfo : allUsers) {
                        packageSetting.setInstalled(true, userInfo.id);
                        packageSetting.setUninstallReason(0, userInfo.id);
                    }
                }
                packageSetting.setLegacyNativeLibraryPath(str);
            }
            packageSetting.setPath(file);
        }
        packageSetting.setPrimaryCpuAbi(str2).setSecondaryCpuAbi(str3).updateMimeGroups(set).setDomainSetId(uuid);
        if (strArr != null && jArr != null && strArr.length == jArr.length) {
            packageSetting.setUsesSdkLibraries(strArr).setUsesSdkLibrariesVersionsMajor(jArr);
        } else {
            packageSetting.setUsesSdkLibraries(null).setUsesSdkLibrariesVersionsMajor(null);
        }
        if (strArr2 != null && jArr2 != null && strArr2.length == jArr2.length) {
            packageSetting.setUsesStaticLibraries(strArr2).setUsesStaticLibrariesVersions(jArr2);
        } else {
            packageSetting.setUsesStaticLibraries(null).setUsesStaticLibrariesVersions(null);
        }
        packageSetting.setFlags((packageSetting.getFlags() & (-2)) | (i & 1));
        packageSetting.setPrivateFlags((packageSetting.getPrivateFlags() & 512) != 0 ? i2 | 512 : i2 & (-513));
    }

    public boolean registerAppIdLPw(PackageSetting packageSetting, boolean z) throws PackageManagerException {
        boolean z2;
        if (packageSetting.getAppId() == 0 || z) {
            packageSetting.setAppId(this.mAppIds.acquireAndRegisterNewAppId(packageSetting));
            z2 = true;
        } else {
            z2 = this.mAppIds.registerExistingAppId(packageSetting.getAppId(), packageSetting, packageSetting.getPackageName());
        }
        if (packageSetting.getAppId() >= 0) {
            return z2;
        }
        PackageManagerService.reportSettingsProblem(5, "Package " + packageSetting.getPackageName() + " could not be assigned a valid UID");
        throw new PackageManagerException(-4, "Package " + packageSetting.getPackageName() + " could not be assigned a valid UID");
    }

    public void writeUserRestrictionsLPw(PackageSetting packageSetting, PackageSetting packageSetting2) {
        List<UserInfo> allUsers;
        Object readUserState;
        if (getPackageLPr(packageSetting.getPackageName()) == null || (allUsers = getAllUsers(UserManagerService.getInstance())) == null) {
            return;
        }
        for (UserInfo userInfo : allUsers) {
            if (packageSetting2 == null) {
                readUserState = PackageUserState.DEFAULT;
            } else {
                readUserState = packageSetting2.readUserState(userInfo.id);
            }
            if (!readUserState.equals(packageSetting.readUserState(userInfo.id))) {
                writePackageRestrictionsLPr(userInfo.id);
            }
        }
    }

    public static boolean isAdbInstallDisallowed(UserManagerService userManagerService, int i) {
        return userManagerService.hasUserRestriction("no_debugging_features", i);
    }

    public void insertPackageSettingLPw(PackageSetting packageSetting, AndroidPackage androidPackage) {
        if (packageSetting.getSigningDetails().getSignatures() == null) {
            packageSetting.setSigningDetails(androidPackage.getSigningDetails());
        }
        SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(packageSetting);
        if (sharedUserSettingLPr != null && sharedUserSettingLPr.signatures.mSigningDetails.getSignatures() == null) {
            sharedUserSettingLPr.signatures.mSigningDetails = androidPackage.getSigningDetails();
        }
        addPackageSettingLPw(packageSetting, sharedUserSettingLPr);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void addPackageSettingLPw(PackageSetting packageSetting, SharedUserSetting sharedUserSetting) {
        this.mPackages.put(packageSetting.getPackageName(), packageSetting);
        if (sharedUserSetting != null) {
            SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(packageSetting);
            if (sharedUserSettingLPr != null && sharedUserSettingLPr != sharedUserSetting) {
                PackageManagerService.reportSettingsProblem(6, "Package " + packageSetting.getPackageName() + " was user " + sharedUserSettingLPr + " but is now " + sharedUserSetting + "; I am not changing its files so it will probably fail!");
                sharedUserSettingLPr.removePackage(packageSetting);
            } else if (packageSetting.getAppId() != 0 && packageSetting.getAppId() != sharedUserSetting.mAppId) {
                PackageManagerService.reportSettingsProblem(6, "Package " + packageSetting.getPackageName() + " was app id " + packageSetting.getAppId() + " but is now user " + sharedUserSetting + " with app id " + sharedUserSetting.mAppId + "; I am not changing its files so it will probably fail!");
            }
            sharedUserSetting.addPackage(packageSetting);
            packageSetting.setSharedUserAppId(sharedUserSetting.mAppId);
            packageSetting.setAppId(sharedUserSetting.mAppId);
        }
        SettingBase settingLPr = getSettingLPr(packageSetting.getAppId());
        if (sharedUserSetting == null) {
            if (settingLPr == null || settingLPr == packageSetting) {
                return;
            }
            this.mAppIds.replaceSetting(packageSetting.getAppId(), packageSetting);
        } else if (settingLPr == null || settingLPr == sharedUserSetting) {
        } else {
            this.mAppIds.replaceSetting(packageSetting.getAppId(), sharedUserSetting);
        }
    }

    public boolean checkAndPruneSharedUserLPw(SharedUserSetting sharedUserSetting, boolean z) {
        if ((z || (sharedUserSetting.getPackageStates().isEmpty() && sharedUserSetting.getDisabledPackageStates().isEmpty())) && this.mSharedUsers.remove(sharedUserSetting.name) != null) {
            removeAppIdLPw(sharedUserSetting.mAppId);
            return true;
        }
        return false;
    }

    public int removePackageLPw(String str) {
        PackageSetting remove = this.mPackages.remove(str);
        if (remove != null) {
            removeInstallerPackageStatus(str);
            SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(remove);
            if (sharedUserSettingLPr != null) {
                sharedUserSettingLPr.removePackage(remove);
                if (checkAndPruneSharedUserLPw(sharedUserSettingLPr, false)) {
                    return sharedUserSettingLPr.mAppId;
                }
                return -1;
            }
            removeAppIdLPw(remove.getAppId());
            return remove.getAppId();
        }
        return -1;
    }

    public final void removeInstallerPackageStatus(String str) {
        if (this.mInstallerPackages.contains(str)) {
            for (int i = 0; i < this.mPackages.size(); i++) {
                this.mPackages.valueAt(i).removeInstallerPackage(str);
            }
            this.mInstallerPackages.remove(str);
        }
    }

    public SettingBase getSettingLPr(int i) {
        return this.mAppIds.getSetting(i);
    }

    public void removeAppIdLPw(int i) {
        this.mAppIds.removeSetting(i);
    }

    public void convertSharedUserSettingsLPw(SharedUserSetting sharedUserSetting) {
        PackageSetting valueAt = sharedUserSetting.getPackageSettings().valueAt(0);
        this.mAppIds.replaceSetting(sharedUserSetting.getAppId(), valueAt);
        valueAt.setSharedUserAppId(-1);
        if (!sharedUserSetting.getDisabledPackageSettings().isEmpty()) {
            sharedUserSetting.getDisabledPackageSettings().valueAt(0).setSharedUserAppId(-1);
        }
        this.mSharedUsers.remove(sharedUserSetting.getName());
    }

    public void checkAndConvertSharedUserSettingsLPw(SharedUserSetting sharedUserSetting) {
        AndroidPackageInternal pkg;
        if (sharedUserSetting.isSingleUser() && (pkg = sharedUserSetting.getPackageSettings().valueAt(0).getPkg()) != null && pkg.isLeavingSharedUser() && SharedUidMigration.applyStrategy(2)) {
            convertSharedUserSettingsLPw(sharedUserSetting);
        }
    }

    public PreferredIntentResolver editPreferredActivitiesLPw(int i) {
        PreferredIntentResolver preferredIntentResolver = this.mPreferredActivities.get(i);
        if (preferredIntentResolver == null) {
            PreferredIntentResolver preferredIntentResolver2 = new PreferredIntentResolver();
            this.mPreferredActivities.put(i, preferredIntentResolver2);
            return preferredIntentResolver2;
        }
        return preferredIntentResolver;
    }

    public PersistentPreferredIntentResolver editPersistentPreferredActivitiesLPw(int i) {
        PersistentPreferredIntentResolver persistentPreferredIntentResolver = this.mPersistentPreferredActivities.get(i);
        if (persistentPreferredIntentResolver == null) {
            PersistentPreferredIntentResolver persistentPreferredIntentResolver2 = new PersistentPreferredIntentResolver();
            this.mPersistentPreferredActivities.put(i, persistentPreferredIntentResolver2);
            return persistentPreferredIntentResolver2;
        }
        return persistentPreferredIntentResolver;
    }

    public CrossProfileIntentResolver editCrossProfileIntentResolverLPw(int i) {
        CrossProfileIntentResolver crossProfileIntentResolver = this.mCrossProfileIntentResolvers.get(i);
        if (crossProfileIntentResolver == null) {
            CrossProfileIntentResolver crossProfileIntentResolver2 = new CrossProfileIntentResolver();
            this.mCrossProfileIntentResolvers.put(i, crossProfileIntentResolver2);
            return crossProfileIntentResolver2;
        }
        return crossProfileIntentResolver;
    }

    public String removeDefaultBrowserPackageNameLPw(int i) {
        if (i == -1) {
            return null;
        }
        return this.mDefaultBrowserApp.removeReturnOld(i);
    }

    public final File getUserSystemDirectory(int i) {
        return new File(new File(this.mSystemDir, "users"), Integer.toString(i));
    }

    public final ResilientAtomicFile getUserPackagesStateFile(int i) {
        return new ResilientAtomicFile(new File(getUserSystemDirectory(i), "package-restrictions.xml"), new File(getUserSystemDirectory(i), "package-restrictions-backup.xml"), new File(getUserSystemDirectory(i), "package-restrictions.xml.reservecopy"), FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED, "package restrictions", this);
    }

    public final ResilientAtomicFile getSettingsFile() {
        return new ResilientAtomicFile(this.mSettingsFilename, this.mPreviousSettingsFilename, this.mSettingsReserveCopyFilename, FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED, "package manager settings", this);
    }

    public final File getUserRuntimePermissionsFile(int i) {
        return new File(getUserSystemDirectory(i), "runtime-permissions.xml");
    }

    public void writeAllUsersPackageRestrictionsLPr() {
        writeAllUsersPackageRestrictionsLPr(false);
    }

    public void writeAllUsersPackageRestrictionsLPr(boolean z) {
        List<UserInfo> allUsers = getAllUsers(UserManagerService.getInstance());
        if (allUsers == null) {
            return;
        }
        if (z) {
            synchronized (this.mPackageRestrictionsLock) {
                this.mPendingAsyncPackageRestrictionsWrites.clear();
            }
            this.mHandler.removeMessages(30);
        }
        for (UserInfo userInfo : allUsers) {
            writePackageRestrictionsLPr(userInfo.id, z);
        }
    }

    public void writeAllRuntimePermissionsLPr() {
        for (int i : UserManagerService.getInstance().getUserIds()) {
            this.mRuntimePermissionsPersistence.writeStateForUserAsync(i);
        }
    }

    public boolean isPermissionUpgradeNeeded(int i) {
        return this.mRuntimePermissionsPersistence.isPermissionUpgradeNeeded(i);
    }

    public void updateRuntimePermissionsFingerprint(int i) {
        this.mRuntimePermissionsPersistence.updateRuntimePermissionsFingerprint(i);
    }

    public int getDefaultRuntimePermissionsVersion(int i) {
        return this.mRuntimePermissionsPersistence.getVersion(i);
    }

    public void setDefaultRuntimePermissionsVersion(int i, int i2) {
        this.mRuntimePermissionsPersistence.setVersion(i, i2);
    }

    public void setPermissionControllerVersion(long j) {
        this.mRuntimePermissionsPersistence.setPermissionControllerVersion(j);
    }

    public VersionInfo findOrCreateVersion(String str) {
        VersionInfo versionInfo = this.mVersion.get(str);
        if (versionInfo == null) {
            VersionInfo versionInfo2 = new VersionInfo();
            this.mVersion.put(str, versionInfo2);
            return versionInfo2;
        }
        return versionInfo;
    }

    public VersionInfo getInternalVersion() {
        return this.mVersion.get(StorageManager.UUID_PRIVATE_INTERNAL);
    }

    public VersionInfo getExternalVersion() {
        return this.mVersion.get("primary_physical");
    }

    public void onVolumeForgotten(String str) {
        this.mVersion.remove(str);
    }

    public void readPreferredActivitiesLPw(TypedXmlPullParser typedXmlPullParser, int i) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    PreferredActivity preferredActivity = new PreferredActivity(typedXmlPullParser);
                    if (preferredActivity.mPref.getParseError() == null) {
                        PreferredIntentResolver editPreferredActivitiesLPw = editPreferredActivitiesLPw(i);
                        if (editPreferredActivitiesLPw.shouldAddPreferredActivity(preferredActivity)) {
                            editPreferredActivitiesLPw.addFilter((PackageDataSnapshot) null, (PackageDataSnapshot) preferredActivity);
                        }
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + preferredActivity.mPref.getParseError() + " at " + typedXmlPullParser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final void readPersistentPreferredActivitiesLPw(TypedXmlPullParser typedXmlPullParser, int i) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    editPersistentPreferredActivitiesLPw(i).addFilter((PackageDataSnapshot) null, (PackageDataSnapshot) new PersistentPreferredActivity(typedXmlPullParser));
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <persistent-preferred-activities>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final void readCrossProfileIntentFiltersLPw(TypedXmlPullParser typedXmlPullParser, int i) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (name.equals("item")) {
                    editCrossProfileIntentResolverLPw(i).addFilter((PackageDataSnapshot) null, (PackageDataSnapshot) new CrossProfileIntentFilter(typedXmlPullParser));
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under crossProfile-intent-filters: " + name);
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public void readDefaultAppsLPw(XmlPullParser xmlPullParser, int i) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                String name = xmlPullParser.getName();
                if (name.equals("default-browser")) {
                    this.mDefaultBrowserApp.put(i, xmlPullParser.getAttributeValue(null, "packageName"));
                } else if (!name.equals("default-dialer")) {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under default-apps: " + xmlPullParser.getName());
                    XmlUtils.skipCurrentTag(xmlPullParser);
                }
            }
        }
    }

    public void readBlockUninstallPackagesLPw(TypedXmlPullParser typedXmlPullParser, int i) throws XmlPullParserException, IOException {
        int depth = typedXmlPullParser.getDepth();
        ArraySet<String> arraySet = new ArraySet<>();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("block-uninstall")) {
                    arraySet.add(typedXmlPullParser.getAttributeValue((String) null, "packageName"));
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under block-uninstall-packages: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        if (arraySet.isEmpty()) {
            this.mBlockUninstallPackages.remove(i);
        } else {
            this.mBlockUninstallPackages.put(i, arraySet);
        }
    }

    @Override // com.android.server.p011pm.ResilientAtomicFile.ReadEventLogger
    public void logEvent(int i, String str) {
        StringBuilder sb = this.mReadMessages;
        sb.append(str + "\n");
        PackageManagerService.reportSettingsProblem(i, str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 27, insn: 0x03ac: MOVE  (r2 I:??[OBJECT, ARRAY]) = (r27 I:??[OBJECT, ARRAY]), block:B:168:0x03ab */
    /* JADX WARN: Removed duplicated region for block: B:179:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:236:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r15v14, types: [android.os.PersistableBundle] */
    /* JADX WARN: Type inference failed for: r43v1 */
    /* JADX WARN: Type inference failed for: r43v2 */
    /* JADX WARN: Type inference failed for: r43v3 */
    /* JADX WARN: Type inference failed for: r43v5, types: [android.util.ArrayMap] */
    /* JADX WARN: Type inference failed for: r7v16, types: [android.os.PersistableBundle] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readPackageRestrictionsLPr(int i, ArrayMap<String, Long> arrayMap) {
        Throwable th;
        Object obj;
        ResilientAtomicFile resilientAtomicFile;
        ResilientAtomicFile resilientAtomicFile2;
        Object obj2;
        SuspendDialogInfo openRead;
        TypedXmlPullParser resolvePullParser;
        int next;
        char c;
        boolean z;
        char c2;
        int i2;
        char c3;
        boolean z2;
        char c4;
        SuspendDialogInfo suspendDialogInfo;
        int i3;
        ArrayMap<String, SuspendParams> arrayMap2;
        boolean z3;
        int i4;
        char c5;
        ResilientAtomicFile userPackagesStateFile = getUserPackagesStateFile(i);
        SuspendDialogInfo suspendDialogInfo2 = null;
        try {
            try {
                obj = this.mPackageRestrictionsLock;
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                synchronized (obj) {
                    try {
                        openRead = userPackagesStateFile.openRead();
                        try {
                            if (openRead == null) {
                                try {
                                    for (PackageSetting packageSetting : this.mPackages.values()) {
                                        Object obj3 = obj;
                                        ResilientAtomicFile resilientAtomicFile3 = userPackagesStateFile;
                                        packageSetting.setUserState(i, 0L, 0, true, false, false, false, 0, null, false, false, null, null, null, 0, 0, null, null, 0L);
                                        obj = obj3;
                                        userPackagesStateFile = resilientAtomicFile3;
                                    }
                                    ResilientAtomicFile resilientAtomicFile4 = userPackagesStateFile;
                                    resilientAtomicFile4.close();
                                    return;
                                } catch (Throwable th3) {
                                    th = th3;
                                    resilientAtomicFile2 = userPackagesStateFile;
                                    obj2 = obj;
                                    suspendDialogInfo2 = openRead;
                                    while (true) {
                                        try {
                                            try {
                                                break;
                                            } catch (IOException | XmlPullParserException e) {
                                                e = e;
                                                userPackagesStateFile = resilientAtomicFile2;
                                                userPackagesStateFile.failRead(suspendDialogInfo2, e);
                                                readPackageRestrictionsLPr(i, arrayMap);
                                                if (userPackagesStateFile == null) {
                                                }
                                            }
                                        } catch (Throwable th4) {
                                            th = th4;
                                        }
                                    }
                                    throw th;
                                }
                            } else {
                                resilientAtomicFile2 = userPackagesStateFile;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        resilientAtomicFile2 = userPackagesStateFile;
                        obj2 = obj;
                    }
                }
                try {
                    resolvePullParser = Xml.resolvePullParser(openRead);
                    while (true) {
                        next = resolvePullParser.next();
                        c = 2;
                        z = true;
                        if (next == 2 || next == 1) {
                            break;
                        }
                    }
                    c2 = 5;
                } catch (IOException | XmlPullParserException e2) {
                    e = e2;
                }
            } catch (Throwable th7) {
                th = th7;
                userPackagesStateFile = resilientAtomicFile;
                if (userPackagesStateFile != null) {
                    try {
                        userPackagesStateFile.close();
                    } catch (Throwable th8) {
                        th.addSuppressed(th8);
                    }
                }
                throw th;
            }
        } catch (IOException | XmlPullParserException e3) {
            e = e3;
        }
        if (next != 2) {
            this.mReadMessages.append("No start tag found in package restrictions file\n");
            PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager package restrictions file");
            resilientAtomicFile2.close();
            return;
        }
        int depth = resolvePullParser.getDepth();
        while (true) {
            int next2 = resolvePullParser.next();
            if (next2 != z && (next2 != 3 || resolvePullParser.getDepth() > depth)) {
                if (next2 != 3 && next2 != 4) {
                    String name = resolvePullParser.getName();
                    if (name.equals("pkg")) {
                        String attributeValue = resolvePullParser.getAttributeValue(suspendDialogInfo2, "name");
                        PackageSetting packageSetting2 = this.mPackages.get(attributeValue);
                        if (packageSetting2 == null) {
                            Slog.w("PackageManager", "No package known for package restrictions " + attributeValue);
                            XmlUtils.skipCurrentTag(resolvePullParser);
                        } else {
                            long attributeLong = resolvePullParser.getAttributeLong(suspendDialogInfo2, "ceDataInode", 0L);
                            boolean attributeBoolean = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "inst", z);
                            boolean attributeBoolean2 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "stopped", false);
                            boolean attributeBoolean3 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "nl", false);
                            boolean attributeBoolean4 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "hidden", false);
                            if (!attributeBoolean4) {
                                attributeBoolean4 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "blocked", false);
                            }
                            boolean z4 = attributeBoolean4;
                            int attributeInt = resolvePullParser.getAttributeInt(suspendDialogInfo2, "distraction_flags", 0);
                            boolean attributeBoolean5 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "suspended", false);
                            String attributeValue2 = resolvePullParser.getAttributeValue(suspendDialogInfo2, "suspending-package");
                            String attributeValue3 = resolvePullParser.getAttributeValue(suspendDialogInfo2, "suspend_dialog_message");
                            if (attributeBoolean5 && attributeValue2 == null) {
                                attributeValue2 = PackageManagerShellCommandDataLoader.PACKAGE;
                            }
                            boolean attributeBoolean6 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "blockUninstall", false);
                            boolean attributeBoolean7 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "instant-app", false);
                            boolean attributeBoolean8 = resolvePullParser.getAttributeBoolean(suspendDialogInfo2, "virtual-preload", false);
                            int attributeInt2 = resolvePullParser.getAttributeInt(suspendDialogInfo2, "enabled", 0);
                            String attributeValue4 = resolvePullParser.getAttributeValue(suspendDialogInfo2, "enabledCaller");
                            String attributeValue5 = resolvePullParser.getAttributeValue(suspendDialogInfo2, "harmful-app-warning");
                            int attributeInt3 = resolvePullParser.getAttributeInt(suspendDialogInfo2, "domainVerificationStatus", 0);
                            int attributeInt4 = resolvePullParser.getAttributeInt(suspendDialogInfo2, "install-reason", 0);
                            int attributeInt5 = resolvePullParser.getAttributeInt(suspendDialogInfo2, "uninstall-reason", 0);
                            String attributeValue6 = resolvePullParser.getAttributeValue(suspendDialogInfo2, "splash-screen-theme");
                            long attributeLongHex = resolvePullParser.getAttributeLongHex(suspendDialogInfo2, "first-install-time", 0L);
                            int depth2 = resolvePullParser.getDepth();
                            SuspendDialogInfo suspendDialogInfo3 = suspendDialogInfo2;
                            SuspendDialogInfo suspendDialogInfo4 = suspendDialogInfo3;
                            SuspendDialogInfo suspendDialogInfo5 = suspendDialogInfo4;
                            ArraySet<String> arraySet = suspendDialogInfo5;
                            ArraySet<String> arraySet2 = arraySet;
                            ArrayMap arrayMap3 = arraySet2;
                            ArraySet<String> arraySet3 = arraySet;
                            while (true) {
                                int next3 = resolvePullParser.next();
                                i3 = depth;
                                if (next3 != 1) {
                                    int i5 = 3;
                                    if (next3 == 3) {
                                        if (resolvePullParser.getDepth() > depth2) {
                                            i5 = 3;
                                        }
                                    }
                                    if (next3 != i5 && next3 != 4) {
                                        String name2 = resolvePullParser.getName();
                                        switch (name2.hashCode()) {
                                            case -2027581689:
                                                if (name2.equals("disabled-components")) {
                                                    c5 = 1;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            case -1963032286:
                                                if (name2.equals("enabled-components")) {
                                                    c5 = 0;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            case -1592287551:
                                                if (name2.equals("suspended-app-extras")) {
                                                    c5 = 2;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            case -1422791362:
                                                if (name2.equals("suspended-launcher-extras")) {
                                                    c5 = 3;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            case -858175433:
                                                if (name2.equals("suspend-params")) {
                                                    c5 = 5;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            case 1660896545:
                                                if (name2.equals("suspended-dialog-info")) {
                                                    c5 = 4;
                                                    break;
                                                }
                                                c5 = 65535;
                                                break;
                                            default:
                                                c5 = 65535;
                                                break;
                                        }
                                        if (c5 == 0) {
                                            i4 = depth2;
                                            suspendDialogInfo5 = readComponentsLPr(resolvePullParser);
                                        } else if (c5 == 1) {
                                            i4 = depth2;
                                            arraySet3 = readComponentsLPr(resolvePullParser);
                                        } else if (c5 == 2) {
                                            i4 = depth2;
                                            suspendDialogInfo4 = PersistableBundle.restoreFromXml(resolvePullParser);
                                        } else if (c5 == 3) {
                                            i4 = depth2;
                                            arraySet2 = PersistableBundle.restoreFromXml(resolvePullParser);
                                        } else if (c5 == 4) {
                                            i4 = depth2;
                                            suspendDialogInfo3 = SuspendDialogInfo.restoreFromXml(resolvePullParser);
                                        } else if (c5 == 5) {
                                            i4 = depth2;
                                            String attributeValue7 = resolvePullParser.getAttributeValue((String) null, "suspending-package");
                                            if (attributeValue7 == null) {
                                                Slog.wtf("PackageSettings", "No suspendingPackage found inside tag suspend-params");
                                            } else {
                                                if (arrayMap3 == null) {
                                                    arrayMap3 = new ArrayMap();
                                                }
                                                ArrayMap arrayMap4 = arrayMap3;
                                                arrayMap4.put(attributeValue7, SuspendParams.restoreFromXml(resolvePullParser));
                                                arrayMap3 = arrayMap4;
                                            }
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            i4 = depth2;
                                            sb.append("Unknown tag ");
                                            sb.append(resolvePullParser.getName());
                                            sb.append(" under tag ");
                                            sb.append("pkg");
                                            Slog.wtf("PackageSettings", sb.toString());
                                        }
                                        depth = i3;
                                        depth2 = i4;
                                        arraySet3 = arraySet3;
                                    }
                                    i4 = depth2;
                                    depth = i3;
                                    depth2 = i4;
                                    arraySet3 = arraySet3;
                                }
                            }
                            if (suspendDialogInfo3 == null && !TextUtils.isEmpty(attributeValue3)) {
                                suspendDialogInfo3 = new SuspendDialogInfo.Builder().setMessage(attributeValue3).build();
                            }
                            if (attributeBoolean5 && arrayMap3 == null) {
                                SuspendParams suspendParams = new SuspendParams(suspendDialogInfo3, suspendDialogInfo4, arraySet2);
                                ArrayMap<String, SuspendParams> arrayMap5 = new ArrayMap<>();
                                arrayMap5.put(attributeValue2, suspendParams);
                                arrayMap2 = arrayMap5;
                            } else {
                                arrayMap2 = arrayMap3;
                            }
                            if (attributeBoolean6) {
                                z3 = true;
                                try {
                                    setBlockUninstallLPw(i, attributeValue, true);
                                } catch (IOException | XmlPullParserException e4) {
                                    e = e4;
                                    suspendDialogInfo2 = openRead;
                                    userPackagesStateFile = resilientAtomicFile2;
                                    userPackagesStateFile.failRead(suspendDialogInfo2, e);
                                    readPackageRestrictionsLPr(i, arrayMap);
                                    if (userPackagesStateFile == null) {
                                    }
                                }
                            } else {
                                z3 = true;
                            }
                            if (attributeLongHex == 0) {
                                attributeLongHex = arrayMap.getOrDefault(attributeValue, 0L).longValue();
                            }
                            TypedXmlPullParser typedXmlPullParser = resolvePullParser;
                            i2 = i3;
                            c3 = 5;
                            z2 = z3;
                            c4 = 2;
                            suspendDialogInfo = null;
                            packageSetting2.setUserState(i, attributeLong, attributeInt2, attributeBoolean, attributeBoolean2, attributeBoolean3, z4, attributeInt, arrayMap2, attributeBoolean7, attributeBoolean8, attributeValue4, suspendDialogInfo5, arraySet3, attributeInt4, attributeInt5, attributeValue5, attributeValue6, attributeLongHex);
                            try {
                                this.mDomainVerificationManager.setLegacyUserState(attributeValue, i, attributeInt3);
                                resolvePullParser = typedXmlPullParser;
                            } catch (IOException | XmlPullParserException e5) {
                                e = e5;
                                suspendDialogInfo2 = openRead;
                                userPackagesStateFile = resilientAtomicFile2;
                                userPackagesStateFile.failRead(suspendDialogInfo2, e);
                                readPackageRestrictionsLPr(i, arrayMap);
                                if (userPackagesStateFile == null) {
                                }
                            }
                        }
                    } else {
                        TypedXmlPullParser typedXmlPullParser2 = resolvePullParser;
                        i2 = depth;
                        c3 = c2;
                        z2 = z;
                        c4 = c;
                        suspendDialogInfo = suspendDialogInfo2;
                        if (name.equals("preferred-activities")) {
                            resolvePullParser = typedXmlPullParser2;
                            readPreferredActivitiesLPw(resolvePullParser, i);
                        } else {
                            resolvePullParser = typedXmlPullParser2;
                            if (name.equals("persistent-preferred-activities")) {
                                readPersistentPreferredActivitiesLPw(resolvePullParser, i);
                            } else if (name.equals("crossProfile-intent-filters")) {
                                readCrossProfileIntentFiltersLPw(resolvePullParser, i);
                            } else if (name.equals("default-apps")) {
                                readDefaultAppsLPw(resolvePullParser, i);
                            } else if (name.equals("block-uninstall-packages")) {
                                readBlockUninstallPackagesLPw(resolvePullParser, i);
                            } else {
                                Slog.w("PackageManager", "Unknown element under <stopped-packages>: " + resolvePullParser.getName());
                                XmlUtils.skipCurrentTag(resolvePullParser);
                            }
                        }
                    }
                    depth = i2;
                    c2 = c3;
                    z = z2;
                    suspendDialogInfo2 = suspendDialogInfo;
                    c = c4;
                }
            }
        }
        userPackagesStateFile = resilientAtomicFile2;
        if (userPackagesStateFile == null) {
            userPackagesStateFile.close();
        }
    }

    public void setBlockUninstallLPw(int i, String str, boolean z) {
        ArraySet<String> arraySet = this.mBlockUninstallPackages.get(i);
        if (z) {
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                this.mBlockUninstallPackages.put(i, arraySet);
            }
            arraySet.add(str);
        } else if (arraySet != null) {
            arraySet.remove(str);
            if (arraySet.isEmpty()) {
                this.mBlockUninstallPackages.remove(i);
            }
        }
    }

    public void clearBlockUninstallLPw(int i) {
        this.mBlockUninstallPackages.remove(i);
    }

    public boolean getBlockUninstallLPr(int i, String str) {
        ArraySet<String> arraySet = this.mBlockUninstallPackages.get(i);
        if (arraySet == null) {
            return false;
        }
        return arraySet.contains(str);
    }

    public final ArraySet<String> readComponentsLPr(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        String attributeValue;
        int depth = typedXmlPullParser.getDepth();
        ArraySet<String> arraySet = null;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4 && typedXmlPullParser.getName().equals("item") && (attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name")) != null) {
                if (arraySet == null) {
                    arraySet = new ArraySet<>();
                }
                arraySet.add(attributeValue);
            }
        }
        return arraySet;
    }

    public void writePreferredActivitiesLPr(TypedXmlSerializer typedXmlSerializer, int i, boolean z) throws IllegalArgumentException, IllegalStateException, IOException {
        typedXmlSerializer.startTag((String) null, "preferred-activities");
        PreferredIntentResolver preferredIntentResolver = this.mPreferredActivities.get(i);
        if (preferredIntentResolver != null) {
            for (F f : preferredIntentResolver.filterSet()) {
                typedXmlSerializer.startTag((String) null, "item");
                f.writeToXml(typedXmlSerializer, z);
                typedXmlSerializer.endTag((String) null, "item");
            }
        }
        typedXmlSerializer.endTag((String) null, "preferred-activities");
    }

    public void writePersistentPreferredActivitiesLPr(TypedXmlSerializer typedXmlSerializer, int i) throws IllegalArgumentException, IllegalStateException, IOException {
        typedXmlSerializer.startTag((String) null, "persistent-preferred-activities");
        PersistentPreferredIntentResolver persistentPreferredIntentResolver = this.mPersistentPreferredActivities.get(i);
        if (persistentPreferredIntentResolver != null) {
            for (F f : persistentPreferredIntentResolver.filterSet()) {
                typedXmlSerializer.startTag((String) null, "item");
                f.writeToXml(typedXmlSerializer);
                typedXmlSerializer.endTag((String) null, "item");
            }
        }
        typedXmlSerializer.endTag((String) null, "persistent-preferred-activities");
    }

    public void writeCrossProfileIntentFiltersLPr(TypedXmlSerializer typedXmlSerializer, int i) throws IllegalArgumentException, IllegalStateException, IOException {
        typedXmlSerializer.startTag((String) null, "crossProfile-intent-filters");
        CrossProfileIntentResolver crossProfileIntentResolver = this.mCrossProfileIntentResolvers.get(i);
        if (crossProfileIntentResolver != null) {
            for (F f : crossProfileIntentResolver.filterSet()) {
                typedXmlSerializer.startTag((String) null, "item");
                f.writeToXml(typedXmlSerializer);
                typedXmlSerializer.endTag((String) null, "item");
            }
        }
        typedXmlSerializer.endTag((String) null, "crossProfile-intent-filters");
    }

    public void writeDefaultAppsLPr(XmlSerializer xmlSerializer, int i) throws IllegalArgumentException, IllegalStateException, IOException {
        xmlSerializer.startTag(null, "default-apps");
        String str = this.mDefaultBrowserApp.get(i);
        if (!TextUtils.isEmpty(str)) {
            xmlSerializer.startTag(null, "default-browser");
            xmlSerializer.attribute(null, "packageName", str);
            xmlSerializer.endTag(null, "default-browser");
        }
        xmlSerializer.endTag(null, "default-apps");
    }

    public void writeBlockUninstallPackagesLPr(TypedXmlSerializer typedXmlSerializer, int i) throws IOException {
        ArraySet<String> arraySet = this.mBlockUninstallPackages.get(i);
        if (arraySet != null) {
            typedXmlSerializer.startTag((String) null, "block-uninstall-packages");
            for (int i2 = 0; i2 < arraySet.size(); i2++) {
                typedXmlSerializer.startTag((String) null, "block-uninstall");
                typedXmlSerializer.attribute((String) null, "packageName", arraySet.valueAt(i2));
                typedXmlSerializer.endTag((String) null, "block-uninstall");
            }
            typedXmlSerializer.endTag((String) null, "block-uninstall-packages");
        }
    }

    public void writePackageRestrictionsLPr(int i) {
        writePackageRestrictionsLPr(i, false);
    }

    public void writePackageRestrictionsLPr(final int i, final boolean z) {
        invalidatePackageCache();
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (z) {
            lambda$writePackageRestrictionsLPr$1(i, uptimeMillis, z);
            return;
        }
        synchronized (this.mPackageRestrictionsLock) {
            this.mPendingAsyncPackageRestrictionsWrites.put(i, this.mPendingAsyncPackageRestrictionsWrites.get(i, 0) + 1);
        }
        this.mHandler.obtainMessage(30, new Runnable() { // from class: com.android.server.pm.Settings$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Settings.this.lambda$writePackageRestrictionsLPr$1(i, uptimeMillis, z);
            }
        }).sendToTarget();
    }

    public void writePackageRestrictions(Integer[] numArr) {
        invalidatePackageCache();
        long uptimeMillis = SystemClock.uptimeMillis();
        for (Integer num : numArr) {
            lambda$writePackageRestrictionsLPr$1(num.intValue(), uptimeMillis, true);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:102:0x0246, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x0247, code lost:
        r3 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0030, code lost:
        if (r2 == null) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x0264, code lost:
        android.util.Slog.wtf("PackageManager", "Unable to write package manager package restrictions,  current changes will be lost at reboot", r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:121:0x026b, code lost:
        if (r3 != null) goto L127;
     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x026d, code lost:
        r2.failWrite(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:123:0x0270, code lost:
        if (r2 != null) goto L129;
     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x0272, code lost:
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:125:0x0275, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0032, code lost:
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0035, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0040, code lost:
        r4 = r16.mLock;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0042, code lost:
        monitor-enter(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0043, code lost:
        r8 = android.util.Xml.resolveSerializer(r7);
        r8.startDocument((java.lang.String) null, java.lang.Boolean.TRUE);
        r8.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        r8.startTag((java.lang.String) null, "package-restrictions");
        r9 = r16.mPackages.values().iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0065, code lost:
        if (r9.hasNext() == false) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0067, code lost:
        r10 = r9.next();
        r11 = r10.readUserState(r17);
        r8.startTag((java.lang.String) null, "pkg");
        r8.attribute((java.lang.String) null, "name", r10.getPackageName());
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0088, code lost:
        if (r11.getCeDataInode() == 0) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x008a, code lost:
        r8.attributeLong((java.lang.String) null, "ceDataInode", r11.getCeDataInode());
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0097, code lost:
        if (r11.isInstalled() != false) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0099, code lost:
        r8.attributeBoolean((java.lang.String) null, "inst", false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00a2, code lost:
        if (r11.isStopped() == false) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00a4, code lost:
        r8.attributeBoolean((java.lang.String) null, "stopped", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00ae, code lost:
        if (r11.isNotLaunched() == false) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00b0, code lost:
        r8.attributeBoolean((java.lang.String) null, "nl", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b9, code lost:
        if (r11.isHidden() == false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00bb, code lost:
        r8.attributeBoolean((java.lang.String) null, "hidden", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00c4, code lost:
        if (r11.getDistractionFlags() == 0) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00c6, code lost:
        r8.attributeInt((java.lang.String) null, "distraction_flags", r11.getDistractionFlags());
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00d3, code lost:
        if (r11.isSuspended() == false) goto L54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00d5, code lost:
        r8.attributeBoolean((java.lang.String) null, "suspended", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x00df, code lost:
        if (r11.isInstantApp() == false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00e1, code lost:
        r8.attributeBoolean((java.lang.String) null, "instant-app", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00ea, code lost:
        if (r11.isVirtualPreload() == false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00ec, code lost:
        r8.attributeBoolean((java.lang.String) null, "virtual-preload", true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00f6, code lost:
        if (r11.getEnabledState() == 0) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00f8, code lost:
        r8.attributeInt((java.lang.String) null, "enabled", r11.getEnabledState());
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0105, code lost:
        if (r11.getLastDisableAppCaller() == null) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0107, code lost:
        r8.attribute((java.lang.String) null, "enabledCaller", r11.getLastDisableAppCaller());
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0114, code lost:
        if (r11.getInstallReason() == 0) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0116, code lost:
        r8.attributeInt((java.lang.String) null, "install-reason", r11.getInstallReason());
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x011f, code lost:
        r8.attributeLongHex((java.lang.String) null, "first-install-time", r11.getFirstInstallTimeMillis());
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x012c, code lost:
        if (r11.getUninstallReason() == 0) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x012e, code lost:
        r8.attributeInt((java.lang.String) null, "uninstall-reason", r11.getUninstallReason());
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x013c, code lost:
        if (r11.getHarmfulAppWarning() == null) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x013e, code lost:
        r8.attribute((java.lang.String) null, "harmful-app-warning", r11.getHarmfulAppWarning());
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x014b, code lost:
        if (r11.getSplashScreenTheme() == null) goto L77;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x014d, code lost:
        r8.attribute((java.lang.String) null, "splash-screen-theme", r11.getSplashScreenTheme());
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x015b, code lost:
        if (r11.isSuspended() == false) goto L89;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x015d, code lost:
        r10 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x0166, code lost:
        if (r10 >= r11.getSuspendParams().size()) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0168, code lost:
        r8.startTag((java.lang.String) null, "suspend-params");
        r8.attribute((java.lang.String) null, "suspending-package", r11.getSuspendParams().keyAt(r10));
        r12 = r11.getSuspendParams().valueAt(r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0188, code lost:
        if (r12 == null) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x018a, code lost:
        r12.saveToXml(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x018d, code lost:
        r8.endTag((java.lang.String) null, "suspend-params");
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0196, code lost:
        r10 = r11.getEnabledComponents();
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x019a, code lost:
        if (r10 == null) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01a0, code lost:
        if (r10.size() <= 0) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x01a2, code lost:
        r8.startTag((java.lang.String) null, "enabled-components");
        r12 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x01ac, code lost:
        if (r12 >= r10.size()) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x01ae, code lost:
        r8.startTag((java.lang.String) null, "item");
        r8.attribute((java.lang.String) null, "name", r10.valueAt(r12));
        r8.endTag((java.lang.String) null, "item");
        r12 = r12 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x01c6, code lost:
        r8.endTag((java.lang.String) null, "enabled-components");
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01cb, code lost:
        r10 = r11.getDisabledComponents();
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x01cf, code lost:
        if (r10 == null) goto L112;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x01d5, code lost:
        if (r10.size() <= 0) goto L111;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01d7, code lost:
        r8.startTag((java.lang.String) null, "disabled-components");
        r11 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x01e1, code lost:
        if (r11 >= r10.size()) goto L107;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01e3, code lost:
        r8.startTag((java.lang.String) null, "item");
        r8.attribute((java.lang.String) null, "name", r10.valueAt(r11));
        r8.endTag((java.lang.String) null, "item");
        r11 = r11 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01fb, code lost:
        r8.endTag((java.lang.String) null, "disabled-components");
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0200, code lost:
        r8.endTag((java.lang.String) null, "pkg");
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0208, code lost:
        writePreferredActivitiesLPr(r8, r17, true);
        writePersistentPreferredActivitiesLPr(r8, r17);
        writeCrossProfileIntentFiltersLPr(r8, r17);
        writeDefaultAppsLPr(r8, r17);
        writeBlockUninstallPackagesLPr(r8, r17);
        r8.endTag((java.lang.String) null, "package-restrictions");
        r8.endDocument();
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0220, code lost:
        monitor-exit(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0221, code lost:
        r2.finishWrite(r7);
        com.android.internal.logging.EventLogTags.writeCommitSysConfigFile("package-user-" + r17, android.os.SystemClock.uptimeMillis() - r18);
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x023f, code lost:
        r2.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x0242, code lost:
        return;
     */
    /* renamed from: writePackageRestrictions */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void lambda$writePackageRestrictionsLPr$1(int i, long j, boolean z) {
        ResilientAtomicFile userPackagesStateFile = getUserPackagesStateFile(i);
        FileOutputStream fileOutputStream = null;
        try {
            try {
                synchronized (this.mPackageRestrictionsLock) {
                    if (!z) {
                        try {
                            int i2 = this.mPendingAsyncPackageRestrictionsWrites.get(i, 0) - 1;
                            if (i2 < 0) {
                                Log.i("PackageSettings", "Cancel writing package restrictions for user=" + i);
                            } else {
                                this.mPendingAsyncPackageRestrictionsWrites.put(i, i2);
                            }
                        } catch (Throwable th) {
                            th = th;
                        }
                    }
                    try {
                        FileOutputStream startWrite = userPackagesStateFile.startWrite();
                        try {
                        } catch (Throwable th2) {
                            th = th2;
                            fileOutputStream = startWrite;
                        }
                    } catch (IOException e) {
                        Slog.wtf("PackageManager", "Unable to write package manager package restrictions,  current changes will be lost at reboot", e);
                        if (userPackagesStateFile != null) {
                            userPackagesStateFile.close();
                            return;
                        }
                        return;
                    }
                }
                throw th;
            } catch (Throwable th3) {
                if (userPackagesStateFile != null) {
                    try {
                        userPackagesStateFile.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (IOException e2) {
            e = e2;
        }
    }

    public void readInstallPermissionsLPr(TypedXmlPullParser typedXmlPullParser, LegacyPermissionState legacyPermissionState, List<UserInfo> list) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
                    boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "granted", true);
                    int attributeIntHex = typedXmlPullParser.getAttributeIntHex((String) null, "flags", 0);
                    for (UserInfo userInfo : list) {
                        legacyPermissionState.putPermissionState(new LegacyPermissionState.PermissionState(attributeValue, false, attributeBoolean, attributeIntHex), userInfo.id);
                    }
                } else {
                    Slog.w("PackageManager", "Unknown element under <permissions>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public void readUsesSdkLibLPw(TypedXmlPullParser typedXmlPullParser, PackageSetting packageSetting) throws IOException, XmlPullParserException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "version", -1L);
        if (attributeValue != null && attributeLong >= 0) {
            packageSetting.setUsesSdkLibraries((String[]) ArrayUtils.appendElement(String.class, packageSetting.getUsesSdkLibraries(), attributeValue));
            packageSetting.setUsesSdkLibrariesVersionsMajor(ArrayUtils.appendLong(packageSetting.getUsesSdkLibrariesVersionsMajor(), attributeLong));
        }
        XmlUtils.skipCurrentTag(typedXmlPullParser);
    }

    public void readUsesStaticLibLPw(TypedXmlPullParser typedXmlPullParser, PackageSetting packageSetting) throws IOException, XmlPullParserException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "version", -1L);
        if (attributeValue != null && attributeLong >= 0) {
            packageSetting.setUsesStaticLibraries((String[]) ArrayUtils.appendElement(String.class, packageSetting.getUsesStaticLibraries(), attributeValue));
            packageSetting.setUsesStaticLibrariesVersions(ArrayUtils.appendLong(packageSetting.getUsesStaticLibrariesVersions(), attributeLong));
        }
        XmlUtils.skipCurrentTag(typedXmlPullParser);
    }

    public void writeUsesSdkLibLPw(TypedXmlSerializer typedXmlSerializer, String[] strArr, long[] jArr) throws IOException {
        if (ArrayUtils.isEmpty(strArr) || ArrayUtils.isEmpty(jArr) || strArr.length != jArr.length) {
            return;
        }
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            String str = strArr[i];
            long j = jArr[i];
            typedXmlSerializer.startTag((String) null, "uses-sdk-lib");
            typedXmlSerializer.attribute((String) null, "name", str);
            typedXmlSerializer.attributeLong((String) null, "version", j);
            typedXmlSerializer.endTag((String) null, "uses-sdk-lib");
        }
    }

    public void writeUsesStaticLibLPw(TypedXmlSerializer typedXmlSerializer, String[] strArr, long[] jArr) throws IOException {
        if (ArrayUtils.isEmpty(strArr) || ArrayUtils.isEmpty(jArr) || strArr.length != jArr.length) {
            return;
        }
        int length = strArr.length;
        for (int i = 0; i < length; i++) {
            String str = strArr[i];
            long j = jArr[i];
            typedXmlSerializer.startTag((String) null, "uses-static-lib");
            typedXmlSerializer.attribute((String) null, "name", str);
            typedXmlSerializer.attributeLong((String) null, "version", j);
            typedXmlSerializer.endTag((String) null, "uses-static-lib");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0096 A[Catch: IOException -> 0x0126, XmlPullParserException -> 0x0157, TryCatch #4 {IOException -> 0x0126, XmlPullParserException -> 0x0157, blocks: (B:12:0x004c, B:14:0x0054, B:15:0x006a, B:17:0x0070, B:19:0x007e, B:20:0x0085, B:21:0x0089, B:26:0x0096, B:28:0x00a4, B:29:0x00a8, B:33:0x00b1, B:38:0x00bc, B:40:0x00c9, B:42:0x00d9, B:44:0x00ea, B:46:0x0102, B:45:0x00ee, B:47:0x0106, B:48:0x0122), top: B:61:0x004c }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00a4 A[Catch: IOException -> 0x0126, XmlPullParserException -> 0x0157, TryCatch #4 {IOException -> 0x0126, XmlPullParserException -> 0x0157, blocks: (B:12:0x004c, B:14:0x0054, B:15:0x006a, B:17:0x0070, B:19:0x007e, B:20:0x0085, B:21:0x0089, B:26:0x0096, B:28:0x00a4, B:29:0x00a8, B:33:0x00b1, B:38:0x00bc, B:40:0x00c9, B:42:0x00d9, B:44:0x00ea, B:46:0x0102, B:45:0x00ee, B:47:0x0106, B:48:0x0122), top: B:61:0x004c }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x004c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readStoppedLPw() {
        FileInputStream fileInputStream;
        TypedXmlPullParser resolvePullParser;
        int next;
        if (this.mBackupStoppedPackagesFilename.exists()) {
            try {
                fileInputStream = new FileInputStream(this.mBackupStoppedPackagesFilename);
                try {
                    this.mReadMessages.append("Reading from backup stopped packages file\n");
                    PackageManagerService.reportSettingsProblem(4, "Need to read from backup stopped packages file");
                    if (this.mStoppedPackagesFilename.exists()) {
                        Slog.w("PackageManager", "Cleaning up stopped packages file " + this.mStoppedPackagesFilename);
                        this.mStoppedPackagesFilename.delete();
                    }
                } catch (IOException unused) {
                }
            } catch (IOException unused2) {
            }
            if (fileInputStream == null) {
                try {
                    if (!this.mStoppedPackagesFilename.exists()) {
                        this.mReadMessages.append("No stopped packages file found\n");
                        PackageManagerService.reportSettingsProblem(4, "No stopped packages file file; assuming all started");
                        for (PackageSetting packageSetting : this.mPackages.values()) {
                            packageSetting.setStopped(false, 0);
                            packageSetting.setNotLaunched(false, 0);
                        }
                        return;
                    }
                    fileInputStream = new FileInputStream(this.mStoppedPackagesFilename);
                } catch (IOException e) {
                    this.mReadMessages.append("Error reading: " + e.toString());
                    PackageManagerService.reportSettingsProblem(6, "Error reading settings: " + e);
                    Slog.wtf("PackageManager", "Error reading package manager stopped packages", e);
                    return;
                } catch (XmlPullParserException e2) {
                    this.mReadMessages.append("Error reading: " + e2.toString());
                    PackageManagerService.reportSettingsProblem(6, "Error reading stopped packages: " + e2);
                    Slog.wtf("PackageManager", "Error reading package manager stopped packages", e2);
                    return;
                }
            }
            resolvePullParser = Xml.resolvePullParser(fileInputStream);
            while (true) {
                next = resolvePullParser.next();
                if (next == 2 || next == 1) {
                    break;
                }
            }
            if (next == 2) {
                this.mReadMessages.append("No start tag found in stopped packages file\n");
                PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager stopped packages");
                return;
            }
            int depth = resolvePullParser.getDepth();
            while (true) {
                int next2 = resolvePullParser.next();
                if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                    break;
                } else if (next2 != 3 && next2 != 4) {
                    if (resolvePullParser.getName().equals("pkg")) {
                        String attributeValue = resolvePullParser.getAttributeValue((String) null, "name");
                        PackageSetting packageSetting2 = this.mPackages.get(attributeValue);
                        if (packageSetting2 != null) {
                            packageSetting2.setStopped(true, 0);
                            if ("1".equals(resolvePullParser.getAttributeValue((String) null, "nl"))) {
                                packageSetting2.setNotLaunched(true, 0);
                            }
                        } else {
                            Slog.w("PackageManager", "No package known for stopped package " + attributeValue);
                        }
                        XmlUtils.skipCurrentTag(resolvePullParser);
                    } else {
                        Slog.w("PackageManager", "Unknown element under <stopped-packages>: " + resolvePullParser.getName());
                        XmlUtils.skipCurrentTag(resolvePullParser);
                    }
                }
            }
            fileInputStream.close();
            return;
        }
        fileInputStream = null;
        if (fileInputStream == null) {
        }
        resolvePullParser = Xml.resolvePullParser(fileInputStream);
        while (true) {
            next = resolvePullParser.next();
            if (next == 2) {
                break;
            }
            break;
        }
        if (next == 2) {
        }
    }

    public void writeLPr(Computer computer, boolean z) {
        FileOutputStream startWrite;
        long uptimeMillis = SystemClock.uptimeMillis();
        invalidatePackageCache();
        this.mPastSignatures.clear();
        ResilientAtomicFile settingsFile = getSettingsFile();
        FileOutputStream fileOutputStream = null;
        try {
            try {
                startWrite = settingsFile.startWrite();
            } catch (Throwable th) {
                if (settingsFile != null) {
                    try {
                        settingsFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            resolveSerializer.startTag((String) null, "packages");
            int i = 0;
            while (i < this.mVersion.size()) {
                VersionInfo valueAt = this.mVersion.valueAt(i);
                resolveSerializer.startTag((String) null, "version");
                XmlUtils.writeStringAttribute(resolveSerializer, "volumeUuid", this.mVersion.keyAt(i));
                resolveSerializer.attributeInt((String) null, "sdkVersion", valueAt.sdkVersion);
                resolveSerializer.attributeInt((String) null, "databaseVersion", valueAt.databaseVersion);
                XmlUtils.writeStringAttribute(resolveSerializer, "buildFingerprint", valueAt.buildFingerprint);
                XmlUtils.writeStringAttribute(resolveSerializer, "fingerprint", valueAt.fingerprint);
                resolveSerializer.endTag((String) null, "version");
                i++;
                uptimeMillis = uptimeMillis;
            }
            long j = uptimeMillis;
            if (this.mVerifierDeviceIdentity != null) {
                resolveSerializer.startTag((String) null, "verifier");
                resolveSerializer.attribute((String) null, "device", this.mVerifierDeviceIdentity.toString());
                resolveSerializer.endTag((String) null, "verifier");
            }
            resolveSerializer.startTag((String) null, "permission-trees");
            this.mPermissions.writePermissionTrees(resolveSerializer);
            resolveSerializer.endTag((String) null, "permission-trees");
            resolveSerializer.startTag((String) null, "permissions");
            this.mPermissions.writePermissions(resolveSerializer);
            resolveSerializer.endTag((String) null, "permissions");
            for (PackageSetting packageSetting : this.mPackages.values()) {
                if (packageSetting.getPkg() == null || !packageSetting.getPkg().isApex()) {
                    writePackageLPr(resolveSerializer, packageSetting);
                }
            }
            for (PackageSetting packageSetting2 : this.mDisabledSysPackages.values()) {
                if (packageSetting2.getPkg() == null || !packageSetting2.getPkg().isApex()) {
                    writeDisabledSysPackageLPr(resolveSerializer, packageSetting2);
                }
            }
            for (SharedUserSetting sharedUserSetting : this.mSharedUsers.values()) {
                resolveSerializer.startTag((String) null, "shared-user");
                resolveSerializer.attribute((String) null, "name", sharedUserSetting.name);
                resolveSerializer.attributeInt((String) null, "userId", sharedUserSetting.mAppId);
                sharedUserSetting.signatures.writeXml(resolveSerializer, "sigs", this.mPastSignatures.untrackedStorage());
                resolveSerializer.endTag((String) null, "shared-user");
            }
            if (this.mRenamedPackages.size() > 0) {
                for (Map.Entry<String, String> entry : this.mRenamedPackages.entrySet()) {
                    resolveSerializer.startTag((String) null, "renamed-package");
                    resolveSerializer.attribute((String) null, "new", entry.getKey());
                    resolveSerializer.attribute((String) null, "old", entry.getValue());
                    resolveSerializer.endTag((String) null, "renamed-package");
                }
            }
            this.mDomainVerificationManager.writeSettings(computer, resolveSerializer, false, -1);
            this.mKeySetManagerService.writeKeySetManagerServiceLPr(resolveSerializer);
            resolveSerializer.endTag((String) null, "packages");
            resolveSerializer.endDocument();
            settingsFile.finishWrite(startWrite);
            writeKernelMappingLPr();
            writePackageListLPr();
            writeAllUsersPackageRestrictionsLPr(z);
            writeAllRuntimePermissionsLPr();
            EventLogTags.writeCommitSysConfigFile("package", SystemClock.uptimeMillis() - j);
            settingsFile.close();
        } catch (IOException e2) {
            e = e2;
            fileOutputStream = startWrite;
            Slog.wtf("PackageManager", "Unable to write package manager settings, current changes will be lost at reboot", e);
            if (fileOutputStream != null) {
                settingsFile.failWrite(fileOutputStream);
            }
            if (settingsFile != null) {
                settingsFile.close();
            }
        }
    }

    public final void writeKernelRemoveUserLPr(int i) {
        if (this.mKernelMappingFilename == null) {
            return;
        }
        writeIntToFile(new File(this.mKernelMappingFilename, "remove_userid"), i);
    }

    public void writeKernelMappingLPr() {
        File file = this.mKernelMappingFilename;
        if (file == null) {
            return;
        }
        String[] list = file.list();
        ArraySet arraySet = new ArraySet(list.length);
        for (String str : list) {
            arraySet.add(str);
        }
        for (PackageSetting packageSetting : this.mPackages.values()) {
            arraySet.remove(packageSetting.getPackageName());
            writeKernelMappingLPr(packageSetting);
        }
        for (int i = 0; i < arraySet.size(); i++) {
            String str2 = (String) arraySet.valueAt(i);
            this.mKernelMapping.remove(str2);
            new File(this.mKernelMappingFilename, str2).delete();
        }
    }

    public void writeKernelMappingLPr(PackageSetting packageSetting) {
        if (this.mKernelMappingFilename == null || packageSetting == null || packageSetting.getPackageName() == null) {
            return;
        }
        writeKernelMappingLPr(packageSetting.getPackageName(), packageSetting.getAppId(), packageSetting.getNotInstalledUserIds());
    }

    public void writeKernelMappingLPr(String str, int i, int[] iArr) {
        KernelPackageState kernelPackageState = this.mKernelMapping.get(str);
        boolean z = true;
        int i2 = 0;
        boolean z2 = kernelPackageState == null;
        if (!z2 && Arrays.equals(iArr, kernelPackageState.excludedUserIds)) {
            z = false;
        }
        File file = new File(this.mKernelMappingFilename, str);
        if (z2) {
            file.mkdir();
            kernelPackageState = new KernelPackageState();
            this.mKernelMapping.put(str, kernelPackageState);
        }
        if (kernelPackageState.appId != i) {
            writeIntToFile(new File(file, "appid"), i);
        }
        if (z) {
            for (int i3 = 0; i3 < iArr.length; i3++) {
                int[] iArr2 = kernelPackageState.excludedUserIds;
                if (iArr2 == null || !ArrayUtils.contains(iArr2, iArr[i3])) {
                    writeIntToFile(new File(file, "excluded_userids"), iArr[i3]);
                }
            }
            if (kernelPackageState.excludedUserIds != null) {
                while (true) {
                    int[] iArr3 = kernelPackageState.excludedUserIds;
                    if (i2 >= iArr3.length) {
                        break;
                    }
                    if (!ArrayUtils.contains(iArr, iArr3[i2])) {
                        writeIntToFile(new File(file, "clear_userid"), kernelPackageState.excludedUserIds[i2]);
                    }
                    i2++;
                }
            }
            kernelPackageState.excludedUserIds = iArr;
        }
    }

    public final void writeIntToFile(File file, int i) {
        try {
            FileUtils.bytesToFile(file.getAbsolutePath(), Integer.toString(i).getBytes(StandardCharsets.US_ASCII));
        } catch (IOException unused) {
            Slog.w("PackageSettings", "Couldn't write " + i + " to " + file.getAbsolutePath());
        }
    }

    public void writePackageListLPr() {
        writePackageListLPr(-1);
    }

    public void writePackageListLPr(int i) {
        String fileSelabelLookup = SELinux.fileSelabelLookup(this.mPackageListFilename.getAbsolutePath());
        if (fileSelabelLookup == null) {
            Slog.wtf("PackageSettings", "Failed to get SELinux context for " + this.mPackageListFilename.getAbsolutePath());
        }
        if (!SELinux.setFSCreateContext(fileSelabelLookup)) {
            Slog.wtf("PackageSettings", "Failed to set packages.list SELinux context");
        }
        try {
            writePackageListLPrInternal(i);
        } finally {
            SELinux.setFSCreateContext((String) null);
        }
    }

    public final void writePackageListLPrInternal(int i) {
        BufferedWriter bufferedWriter;
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter2;
        int i2;
        Settings settings = this;
        List<UserInfo> activeUsers = getActiveUsers(UserManagerService.getInstance(), true);
        int size = activeUsers.size();
        int[] iArr = new int[size];
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            iArr[i4] = activeUsers.get(i4).id;
        }
        if (i != -1) {
            iArr = ArrayUtils.appendInt(iArr, i);
        }
        JournaledFile journaledFile = new JournaledFile(settings.mPackageListFilename, new File(settings.mPackageListFilename.getAbsolutePath() + ".tmp"));
        try {
            fileOutputStream = new FileOutputStream(journaledFile.chooseForWrite());
            bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(fileOutputStream, Charset.defaultCharset()));
        } catch (Exception e) {
            e = e;
            bufferedWriter = null;
        }
        try {
            FileUtils.setPermissions(fileOutputStream.getFD(), FrameworkStatsLog.DISPLAY_HBM_STATE_CHANGED, 1000, 1032);
            StringBuilder sb = new StringBuilder();
            for (PackageSetting packageSetting : settings.mPackages.values()) {
                String absolutePath = packageSetting.getPkg() == null ? null : PackageInfoUtils.getDataDir(packageSetting.getPkg(), i3).getAbsolutePath();
                if (packageSetting.getPkg() != null && absolutePath != null) {
                    if (!packageSetting.getPkg().isApex()) {
                        boolean isDebuggable = packageSetting.getPkg().isDebuggable();
                        IntArray intArray = new IntArray();
                        int i5 = i3;
                        for (int length = iArr.length; i5 < length; length = length) {
                            intArray.addAll(settings.mPermissionDataProvider.getGidsForUid(UserHandle.getUid(iArr[i5], packageSetting.getAppId())));
                            i5++;
                            settings = this;
                        }
                        if (absolutePath.indexOf(32) >= 0) {
                            settings = this;
                            i3 = 0;
                        } else {
                            sb.setLength(0);
                            sb.append(packageSetting.getPkg().getPackageName());
                            sb.append(" ");
                            sb.append(packageSetting.getPkg().getUid());
                            sb.append(isDebuggable ? " 1 " : " 0 ");
                            sb.append(absolutePath);
                            sb.append(" ");
                            sb.append(packageSetting.getSeInfo());
                            sb.append(" ");
                            int size2 = intArray.size();
                            if (intArray.size() > 0) {
                                i2 = 0;
                                sb.append(intArray.get(0));
                                for (int i6 = 1; i6 < size2; i6++) {
                                    sb.append(",");
                                    sb.append(intArray.get(i6));
                                }
                            } else {
                                i2 = 0;
                                sb.append("none");
                            }
                            sb.append(" ");
                            String str = "1";
                            sb.append(packageSetting.getPkg().isProfileableByShell() ? "1" : "0");
                            sb.append(" ");
                            sb.append(packageSetting.getPkg().getLongVersionCode());
                            sb.append(" ");
                            if (!packageSetting.getPkg().isProfileable()) {
                                str = "0";
                            }
                            sb.append(str);
                            sb.append(" ");
                            if (packageSetting.isSystem()) {
                                sb.append("@system");
                            } else if (packageSetting.isProduct()) {
                                sb.append("@product");
                            } else if (packageSetting.getInstallSource().mInstallerPackageName != null && !packageSetting.getInstallSource().mInstallerPackageName.isEmpty()) {
                                sb.append(packageSetting.getInstallSource().mInstallerPackageName);
                            } else {
                                sb.append("@null");
                            }
                            sb.append("\n");
                            bufferedWriter2.append((CharSequence) sb);
                            settings = this;
                            i3 = i2;
                        }
                    }
                }
                i2 = i3;
                if (!PackageManagerShellCommandDataLoader.PACKAGE.equals(packageSetting.getPackageName())) {
                    Slog.w("PackageSettings", "Skipping " + packageSetting + " due to missing metadata");
                }
                settings = this;
                i3 = i2;
            }
            bufferedWriter2.flush();
            FileUtils.sync(fileOutputStream);
            bufferedWriter2.close();
            journaledFile.commit();
        } catch (Exception e2) {
            e = e2;
            bufferedWriter = bufferedWriter2;
            Slog.wtf("PackageSettings", "Failed to write packages.list", e);
            IoUtils.closeQuietly(bufferedWriter);
            journaledFile.rollback();
        }
    }

    public void writeDisabledSysPackageLPr(TypedXmlSerializer typedXmlSerializer, PackageSetting packageSetting) throws IOException {
        typedXmlSerializer.startTag((String) null, "updated-package");
        typedXmlSerializer.attribute((String) null, "name", packageSetting.getPackageName());
        if (packageSetting.getRealName() != null) {
            typedXmlSerializer.attribute((String) null, "realName", packageSetting.getRealName());
        }
        typedXmlSerializer.attribute((String) null, "codePath", packageSetting.getPathString());
        typedXmlSerializer.attributeLongHex((String) null, "ft", packageSetting.getLastModifiedTime());
        typedXmlSerializer.attributeLongHex((String) null, "ut", packageSetting.getLastUpdateTime());
        typedXmlSerializer.attributeLong((String) null, "version", packageSetting.getVersionCode());
        if (packageSetting.getLegacyNativeLibraryPath() != null) {
            typedXmlSerializer.attribute((String) null, "nativeLibraryPath", packageSetting.getLegacyNativeLibraryPath());
        }
        if (packageSetting.getPrimaryCpuAbiLegacy() != null) {
            typedXmlSerializer.attribute((String) null, "primaryCpuAbi", packageSetting.getPrimaryCpuAbiLegacy());
        }
        if (packageSetting.getSecondaryCpuAbiLegacy() != null) {
            typedXmlSerializer.attribute((String) null, "secondaryCpuAbi", packageSetting.getSecondaryCpuAbiLegacy());
        }
        if (packageSetting.getCpuAbiOverride() != null) {
            typedXmlSerializer.attribute((String) null, "cpuAbiOverride", packageSetting.getCpuAbiOverride());
        }
        if (!packageSetting.hasSharedUser()) {
            typedXmlSerializer.attributeInt((String) null, "userId", packageSetting.getAppId());
        } else {
            typedXmlSerializer.attributeInt((String) null, "sharedUserId", packageSetting.getAppId());
        }
        typedXmlSerializer.attributeFloat((String) null, "loadingProgress", packageSetting.getLoadingProgress());
        writeUsesSdkLibLPw(typedXmlSerializer, packageSetting.getUsesSdkLibraries(), packageSetting.getUsesSdkLibrariesVersionsMajor());
        writeUsesStaticLibLPw(typedXmlSerializer, packageSetting.getUsesStaticLibraries(), packageSetting.getUsesStaticLibrariesVersions());
        typedXmlSerializer.endTag((String) null, "updated-package");
    }

    public void writePackageLPr(TypedXmlSerializer typedXmlSerializer, PackageSetting packageSetting) throws IOException {
        typedXmlSerializer.startTag((String) null, "package");
        typedXmlSerializer.attribute((String) null, "name", packageSetting.getPackageName());
        if (packageSetting.getRealName() != null) {
            typedXmlSerializer.attribute((String) null, "realName", packageSetting.getRealName());
        }
        typedXmlSerializer.attribute((String) null, "codePath", packageSetting.getPathString());
        if (packageSetting.getLegacyNativeLibraryPath() != null) {
            typedXmlSerializer.attribute((String) null, "nativeLibraryPath", packageSetting.getLegacyNativeLibraryPath());
        }
        if (packageSetting.getPrimaryCpuAbiLegacy() != null) {
            typedXmlSerializer.attribute((String) null, "primaryCpuAbi", packageSetting.getPrimaryCpuAbiLegacy());
        }
        if (packageSetting.getSecondaryCpuAbiLegacy() != null) {
            typedXmlSerializer.attribute((String) null, "secondaryCpuAbi", packageSetting.getSecondaryCpuAbiLegacy());
        }
        if (packageSetting.getCpuAbiOverride() != null) {
            typedXmlSerializer.attribute((String) null, "cpuAbiOverride", packageSetting.getCpuAbiOverride());
        }
        typedXmlSerializer.attributeInt((String) null, "publicFlags", packageSetting.getFlags());
        typedXmlSerializer.attributeInt((String) null, "privateFlags", packageSetting.getPrivateFlags());
        typedXmlSerializer.attributeLongHex((String) null, "ft", packageSetting.getLastModifiedTime());
        typedXmlSerializer.attributeLongHex((String) null, "ut", packageSetting.getLastUpdateTime());
        typedXmlSerializer.attributeLong((String) null, "version", packageSetting.getVersionCode());
        if (!packageSetting.hasSharedUser()) {
            typedXmlSerializer.attributeInt((String) null, "userId", packageSetting.getAppId());
        } else {
            typedXmlSerializer.attributeInt((String) null, "sharedUserId", packageSetting.getAppId());
        }
        InstallSource installSource = packageSetting.getInstallSource();
        String str = installSource.mInstallerPackageName;
        if (str != null) {
            typedXmlSerializer.attribute((String) null, "installer", str);
        }
        int i = installSource.mInstallerPackageUid;
        if (i != -1) {
            typedXmlSerializer.attributeInt((String) null, "installerUid", i);
        }
        String str2 = installSource.mUpdateOwnerPackageName;
        if (str2 != null) {
            typedXmlSerializer.attribute((String) null, "updateOwner", str2);
        }
        String str3 = installSource.mInstallerAttributionTag;
        if (str3 != null) {
            typedXmlSerializer.attribute((String) null, "installerAttributionTag", str3);
        }
        typedXmlSerializer.attributeInt((String) null, "packageSource", installSource.mPackageSource);
        if (installSource.mIsOrphaned) {
            typedXmlSerializer.attributeBoolean((String) null, "isOrphaned", true);
        }
        String str4 = installSource.mInitiatingPackageName;
        if (str4 != null) {
            typedXmlSerializer.attribute((String) null, "installInitiator", str4);
        }
        if (installSource.mIsInitiatingPackageUninstalled) {
            typedXmlSerializer.attributeBoolean((String) null, "installInitiatorUninstalled", true);
        }
        String str5 = installSource.mOriginatingPackageName;
        if (str5 != null) {
            typedXmlSerializer.attribute((String) null, "installOriginator", str5);
        }
        if (packageSetting.getVolumeUuid() != null) {
            typedXmlSerializer.attribute((String) null, "volumeUuid", packageSetting.getVolumeUuid());
        }
        if (packageSetting.getCategoryOverride() != -1) {
            typedXmlSerializer.attributeInt((String) null, "categoryHint", packageSetting.getCategoryOverride());
        }
        if (packageSetting.isUpdateAvailable()) {
            typedXmlSerializer.attributeBoolean((String) null, "updateAvailable", true);
        }
        if (packageSetting.isForceQueryableOverride()) {
            typedXmlSerializer.attributeBoolean((String) null, "forceQueryable", true);
        }
        if (packageSetting.isLoading()) {
            typedXmlSerializer.attributeBoolean((String) null, "isLoading", true);
        }
        typedXmlSerializer.attributeFloat((String) null, "loadingProgress", packageSetting.getLoadingProgress());
        typedXmlSerializer.attribute((String) null, "domainSetId", packageSetting.getDomainSetId().toString());
        writeUsesSdkLibLPw(typedXmlSerializer, packageSetting.getUsesSdkLibraries(), packageSetting.getUsesSdkLibrariesVersionsMajor());
        writeUsesStaticLibLPw(typedXmlSerializer, packageSetting.getUsesStaticLibraries(), packageSetting.getUsesStaticLibrariesVersions());
        packageSetting.getSignatures().writeXml(typedXmlSerializer, "sigs", this.mPastSignatures.untrackedStorage());
        PackageSignatures packageSignatures = installSource.mInitiatingPackageSignatures;
        if (packageSignatures != null) {
            packageSignatures.writeXml(typedXmlSerializer, "install-initiator-sigs", this.mPastSignatures.untrackedStorage());
        }
        writeSigningKeySetLPr(typedXmlSerializer, packageSetting.getKeySetData());
        writeUpgradeKeySetsLPr(typedXmlSerializer, packageSetting.getKeySetData());
        writeKeySetAliasesLPr(typedXmlSerializer, packageSetting.getKeySetData());
        writeMimeGroupLPr(typedXmlSerializer, packageSetting.getMimeGroups());
        typedXmlSerializer.endTag((String) null, "package");
    }

    public void writeSigningKeySetLPr(TypedXmlSerializer typedXmlSerializer, PackageKeySetData packageKeySetData) throws IOException {
        typedXmlSerializer.startTag((String) null, "proper-signing-keyset");
        typedXmlSerializer.attributeLong((String) null, "identifier", packageKeySetData.getProperSigningKeySet());
        typedXmlSerializer.endTag((String) null, "proper-signing-keyset");
    }

    public void writeUpgradeKeySetsLPr(TypedXmlSerializer typedXmlSerializer, PackageKeySetData packageKeySetData) throws IOException {
        long[] upgradeKeySets;
        if (packageKeySetData.isUsingUpgradeKeySets()) {
            for (long j : packageKeySetData.getUpgradeKeySets()) {
                typedXmlSerializer.startTag((String) null, "upgrade-keyset");
                typedXmlSerializer.attributeLong((String) null, "identifier", j);
                typedXmlSerializer.endTag((String) null, "upgrade-keyset");
            }
        }
    }

    public void writeKeySetAliasesLPr(TypedXmlSerializer typedXmlSerializer, PackageKeySetData packageKeySetData) throws IOException {
        for (Map.Entry<String, Long> entry : packageKeySetData.getAliases().entrySet()) {
            typedXmlSerializer.startTag((String) null, "defined-keyset");
            typedXmlSerializer.attribute((String) null, "alias", entry.getKey());
            typedXmlSerializer.attributeLong((String) null, "identifier", entry.getValue().longValue());
            typedXmlSerializer.endTag((String) null, "defined-keyset");
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:119:0x027a  */
    /* JADX WARN: Removed duplicated region for block: B:149:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean readSettingsLPw(Computer computer, List<UserInfo> list, ArrayMap<String, Long> arrayMap) {
        FileInputStream openRead;
        int next;
        ?? r4;
        List<UserInfo> list2 = list;
        this.mPendingPackages.clear();
        this.mPastSignatures.clear();
        this.mKeySetRefs.clear();
        this.mInstallerPackages.clear();
        arrayMap.clear();
        ResilientAtomicFile settingsFile = getSettingsFile();
        int i = 1;
        FileInputStream fileInputStream = null;
        String str = null;
        try {
            try {
                openRead = settingsFile.openRead();
                try {
                } catch (IOException | XmlPullParserException e) {
                    e = e;
                }
            } catch (Throwable th) {
                if (settingsFile != null) {
                    try {
                        settingsFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException | XmlPullParserException e2) {
            e = e2;
        }
        if (openRead == null) {
            findOrCreateVersion(StorageManager.UUID_PRIVATE_INTERNAL).forceCurrent();
            findOrCreateVersion("primary_physical").forceCurrent();
            settingsFile.close();
            return false;
        }
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
        do {
            next = resolvePullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            this.mReadMessages.append("No start tag found in settings file\n");
            PackageManagerService.reportSettingsProblem(5, "No start tag found in package manager settings");
            Slog.wtf("PackageManager", "No start tag found in package manager settings");
            settingsFile.close();
            return false;
        }
        int depth = resolvePullParser.getDepth();
        while (true) {
            int next2 = resolvePullParser.next();
            if (next2 == i || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                break;
            }
            if (next2 == 3) {
                list2 = list;
            } else if (next2 != 4) {
                String name = resolvePullParser.getName();
                if (name.equals("package")) {
                    try {
                        readPackageLPw(resolvePullParser, list2, arrayMap);
                    } catch (IOException | XmlPullParserException e3) {
                        e = e3;
                    }
                } else if (name.equals("permissions")) {
                    this.mPermissions.readPermissions(resolvePullParser);
                } else if (name.equals("permission-trees")) {
                    this.mPermissions.readPermissionTrees(resolvePullParser);
                } else if (name.equals("shared-user")) {
                    readSharedUserLPw(resolvePullParser, list2);
                } else if (!name.equals("preferred-packages")) {
                    if (name.equals("preferred-activities")) {
                        readPreferredActivitiesLPw(resolvePullParser, 0);
                    } else if (name.equals("persistent-preferred-activities")) {
                        readPersistentPreferredActivitiesLPw(resolvePullParser, 0);
                    } else if (name.equals("crossProfile-intent-filters")) {
                        readCrossProfileIntentFiltersLPw(resolvePullParser, 0);
                    } else if (name.equals("default-browser")) {
                        readDefaultAppsLPw(resolvePullParser, 0);
                    } else if (name.equals("updated-package")) {
                        readDisabledSysPackageLPw(resolvePullParser, list2);
                    } else if (name.equals("renamed-package")) {
                        String attributeValue = resolvePullParser.getAttributeValue(str, "new");
                        String attributeValue2 = resolvePullParser.getAttributeValue(str, "old");
                        if (attributeValue != null && attributeValue2 != null) {
                            this.mRenamedPackages.put(attributeValue, attributeValue2);
                        }
                    } else {
                        if (name.equals("last-platform-version")) {
                            VersionInfo findOrCreateVersion = findOrCreateVersion(StorageManager.UUID_PRIVATE_INTERNAL);
                            VersionInfo findOrCreateVersion2 = findOrCreateVersion("primary_physical");
                            findOrCreateVersion.sdkVersion = resolvePullParser.getAttributeInt((String) null, "internal", 0);
                            findOrCreateVersion2.sdkVersion = resolvePullParser.getAttributeInt((String) null, "external", 0);
                            String readStringAttribute = XmlUtils.readStringAttribute(resolvePullParser, "buildFingerprint");
                            findOrCreateVersion2.buildFingerprint = readStringAttribute;
                            findOrCreateVersion.buildFingerprint = readStringAttribute;
                            String readStringAttribute2 = XmlUtils.readStringAttribute(resolvePullParser, "fingerprint");
                            findOrCreateVersion2.fingerprint = readStringAttribute2;
                            findOrCreateVersion.fingerprint = readStringAttribute2;
                        } else if (name.equals("database-version")) {
                            VersionInfo findOrCreateVersion3 = findOrCreateVersion(StorageManager.UUID_PRIVATE_INTERNAL);
                            VersionInfo findOrCreateVersion4 = findOrCreateVersion("primary_physical");
                            findOrCreateVersion3.databaseVersion = resolvePullParser.getAttributeInt((String) null, "internal", 0);
                            findOrCreateVersion4.databaseVersion = resolvePullParser.getAttributeInt((String) null, "external", 0);
                        } else if (name.equals("verifier")) {
                            try {
                                this.mVerifierDeviceIdentity = VerifierDeviceIdentity.parse(resolvePullParser.getAttributeValue((String) null, "device"));
                            } catch (IllegalArgumentException e4) {
                                Slog.w("PackageManager", "Discard invalid verifier device id: " + e4.getMessage());
                            }
                        } else if (!"read-external-storage".equals(name)) {
                            if (name.equals("keyset-settings")) {
                                this.mKeySetManagerService.readKeySetsLPw(resolvePullParser, this.mKeySetRefs.untrackedStorage());
                            } else {
                                if ("version".equals(name)) {
                                    VersionInfo findOrCreateVersion5 = findOrCreateVersion(XmlUtils.readStringAttribute(resolvePullParser, "volumeUuid"));
                                    r4 = null;
                                    findOrCreateVersion5.sdkVersion = resolvePullParser.getAttributeInt((String) null, "sdkVersion");
                                    findOrCreateVersion5.databaseVersion = resolvePullParser.getAttributeInt((String) null, "databaseVersion");
                                    findOrCreateVersion5.buildFingerprint = XmlUtils.readStringAttribute(resolvePullParser, "buildFingerprint");
                                    findOrCreateVersion5.fingerprint = XmlUtils.readStringAttribute(resolvePullParser, "fingerprint");
                                } else {
                                    r4 = null;
                                    if (name.equals("domain-verifications")) {
                                        try {
                                            this.mDomainVerificationManager.readSettings(computer, resolvePullParser);
                                        } catch (IOException | XmlPullParserException e5) {
                                            e = e5;
                                        }
                                    } else if (name.equals("domain-verifications-legacy")) {
                                        this.mDomainVerificationManager.readLegacySettings(resolvePullParser);
                                    } else {
                                        Slog.w("PackageManager", "Unknown element under <packages>: " + resolvePullParser.getName());
                                        XmlUtils.skipCurrentTag(resolvePullParser);
                                    }
                                }
                                list2 = list;
                                str = r4;
                            }
                        }
                        r4 = null;
                        list2 = list;
                        str = r4;
                    }
                }
                r4 = str;
                list2 = list;
                str = r4;
            }
            i = 1;
            e = e5;
            fileInputStream = openRead;
            settingsFile.failRead(fileInputStream, e);
            readSettingsLPw(computer, list, arrayMap);
            if (settingsFile == null) {
                settingsFile.close();
                return true;
            }
            return true;
        }
        openRead.close();
        if (settingsFile == null) {
        }
    }

    public boolean readLPw(Computer computer, List<UserInfo> list) {
        ArrayMap<String, Long> arrayMap = new ArrayMap<>();
        try {
            if (readSettingsLPw(computer, list, arrayMap)) {
                if (!this.mVersion.containsKey(StorageManager.UUID_PRIVATE_INTERNAL)) {
                    Slog.wtf("PackageManager", "No internal VersionInfo found in settings, using current.");
                    findOrCreateVersion(StorageManager.UUID_PRIVATE_INTERNAL).forceCurrent();
                }
                if (!this.mVersion.containsKey("primary_physical")) {
                    Slog.wtf("PackageManager", "No external VersionInfo found in settings, using current.");
                    findOrCreateVersion("primary_physical").forceCurrent();
                }
                int size = this.mPendingPackages.size();
                for (int i = 0; i < size; i++) {
                    PackageSetting packageSetting = this.mPendingPackages.get(i);
                    int sharedUserAppId = packageSetting.getSharedUserAppId();
                    if (sharedUserAppId > 0) {
                        SettingBase settingLPr = getSettingLPr(sharedUserAppId);
                        if (settingLPr instanceof SharedUserSetting) {
                            addPackageSettingLPw(packageSetting, (SharedUserSetting) settingLPr);
                        } else if (settingLPr != null) {
                            String str = "Bad package setting: package " + packageSetting.getPackageName() + " has shared uid " + sharedUserAppId + " that is not a shared uid\n";
                            this.mReadMessages.append(str);
                            PackageManagerService.reportSettingsProblem(6, str);
                        } else {
                            String str2 = "Bad package setting: package " + packageSetting.getPackageName() + " has shared uid " + sharedUserAppId + " that is not defined\n";
                            this.mReadMessages.append(str2);
                            PackageManagerService.reportSettingsProblem(6, str2);
                        }
                    }
                }
                this.mPendingPackages.clear();
                if (this.mBackupStoppedPackagesFilename.exists() || this.mStoppedPackagesFilename.exists()) {
                    readStoppedLPw();
                    this.mBackupStoppedPackagesFilename.delete();
                    this.mStoppedPackagesFilename.delete();
                    writePackageRestrictionsLPr(0, true);
                } else {
                    for (UserInfo userInfo : list) {
                        readPackageRestrictionsLPr(userInfo.id, arrayMap);
                    }
                }
                for (UserInfo userInfo2 : list) {
                    this.mRuntimePermissionsPersistence.readStateForUserSync(userInfo2.id, getInternalVersion(), this.mPackages, this.mSharedUsers, getUserRuntimePermissionsFile(userInfo2.id));
                }
                for (PackageSetting packageSetting2 : this.mDisabledSysPackages.values()) {
                    SettingBase settingLPr2 = getSettingLPr(packageSetting2.getAppId());
                    if (settingLPr2 instanceof SharedUserSetting) {
                        SharedUserSetting sharedUserSetting = (SharedUserSetting) settingLPr2;
                        sharedUserSetting.mDisabledPackages.add(packageSetting2);
                        packageSetting2.setSharedUserAppId(sharedUserSetting.mAppId);
                    }
                }
                StringBuilder sb = this.mReadMessages;
                sb.append("Read completed successfully: ");
                sb.append(this.mPackages.size());
                sb.append(" packages, ");
                sb.append(this.mSharedUsers.size());
                sb.append(" shared uids\n");
                writeKernelMappingLPr();
                return true;
            }
            return false;
        } finally {
            if (!this.mVersion.containsKey(StorageManager.UUID_PRIVATE_INTERNAL)) {
                Slog.wtf("PackageManager", "No internal VersionInfo found in settings, using current.");
                findOrCreateVersion(StorageManager.UUID_PRIVATE_INTERNAL).forceCurrent();
            }
            if (!this.mVersion.containsKey("primary_physical")) {
                Slog.wtf("PackageManager", "No external VersionInfo found in settings, using current.");
                findOrCreateVersion("primary_physical").forceCurrent();
            }
        }
    }

    public void readPermissionStateForUserSyncLPr(int i) {
        this.mRuntimePermissionsPersistence.readStateForUserSync(i, getInternalVersion(), this.mPackages, this.mSharedUsers, getUserRuntimePermissionsFile(i));
    }

    public void applyDefaultPreferredAppsLPw(int i) {
        int i2;
        int next;
        int i3;
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        for (PackageSetting packageSetting : this.mPackages.values()) {
            if ((1 & packageSetting.getFlags()) != 0 && packageSetting.getPkg() != null && !packageSetting.getPkg().getPreferredActivityFilters().isEmpty()) {
                List<Pair<String, ParsedIntentInfo>> preferredActivityFilters = packageSetting.getPkg().getPreferredActivityFilters();
                for (int i4 = 0; i4 < preferredActivityFilters.size(); i4++) {
                    Pair<String, ParsedIntentInfo> pair = preferredActivityFilters.get(i4);
                    applyDefaultPreferredActivityLPw(packageManagerInternal, ((ParsedIntentInfo) pair.second).getIntentFilter(), new ComponentName(packageSetting.getPackageName(), (String) pair.first), i);
                }
            }
        }
        int size = PackageManagerService.SYSTEM_PARTITIONS.size();
        int i5 = 0;
        loop2: while (i5 < size) {
            File file = new File(PackageManagerService.SYSTEM_PARTITIONS.get(i5).getFolder(), "etc/preferred-apps");
            if (file.exists() && file.isDirectory()) {
                if (file.canRead()) {
                    File[] listFiles = file.listFiles();
                    if (ArrayUtils.isEmpty(listFiles)) {
                        continue;
                    } else {
                        int length = listFiles.length;
                        int i6 = 0;
                        while (i6 < length) {
                            File file2 = listFiles[i6];
                            if (!file2.getPath().endsWith(".xml")) {
                                Slog.i("PackageSettings", "Non-xml file " + file2 + " in " + file + " directory, ignoring");
                            } else if (file2.canRead()) {
                                try {
                                    FileInputStream fileInputStream = new FileInputStream(file2);
                                    try {
                                        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                                        while (true) {
                                            next = resolvePullParser.next();
                                            i2 = size;
                                            i3 = 2;
                                            if (next == 2) {
                                                break;
                                            }
                                            if (next == 1) {
                                                i3 = 2;
                                                break;
                                            }
                                            size = i2;
                                        }
                                        if (next != i3) {
                                            try {
                                                Slog.w("PackageSettings", "Preferred apps file " + file2 + " does not have start tag");
                                            } catch (Throwable th) {
                                                th = th;
                                                Throwable th2 = th;
                                                try {
                                                    fileInputStream.close();
                                                } catch (Throwable th3) {
                                                    th2.addSuppressed(th3);
                                                }
                                                throw th2;
                                                break loop2;
                                            }
                                        } else if ("preferred-activities".equals(resolvePullParser.getName())) {
                                            readDefaultPreferredActivitiesLPw(resolvePullParser, i);
                                        } else {
                                            Slog.w("PackageSettings", "Preferred apps file " + file2 + " does not start with 'preferred-activities'");
                                        }
                                        try {
                                            fileInputStream.close();
                                        } catch (IOException e) {
                                            e = e;
                                            Slog.w("PackageSettings", "Error reading apps file " + file2, e);
                                            i6++;
                                            size = i2;
                                        } catch (XmlPullParserException e2) {
                                            e = e2;
                                            Slog.w("PackageSettings", "Error reading apps file " + file2, e);
                                            i6++;
                                            size = i2;
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                        i2 = size;
                                    }
                                } catch (IOException e3) {
                                    e = e3;
                                    i2 = size;
                                } catch (XmlPullParserException e4) {
                                    e = e4;
                                    i2 = size;
                                }
                                i6++;
                                size = i2;
                            } else {
                                Slog.w("PackageSettings", "Preferred apps file " + file2 + " cannot be read");
                            }
                            i2 = size;
                            i6++;
                            size = i2;
                        }
                        continue;
                    }
                } else {
                    Slog.w("PackageSettings", "Directory " + file + " cannot be read");
                }
            }
            i5++;
            size = size;
        }
    }

    public static void removeFilters(PreferredIntentResolver preferredIntentResolver, WatchedIntentFilter watchedIntentFilter, List<PreferredActivity> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            preferredIntentResolver.removeFilter((PreferredIntentResolver) list.get(size));
        }
    }

    public final void applyDefaultPreferredActivityLPw(PackageManagerInternal packageManagerInternal, IntentFilter intentFilter, ComponentName componentName, int i) {
        int i2;
        Intent intent = new Intent();
        int i3 = 0;
        intent.setAction(intentFilter.getAction(0));
        int i4 = 786432;
        for (int i5 = 0; i5 < intentFilter.countCategories(); i5++) {
            String category = intentFilter.getCategory(i5);
            if (category.equals("android.intent.category.DEFAULT")) {
                i4 |= 65536;
            } else {
                intent.addCategory(category);
            }
        }
        int countDataSchemes = intentFilter.countDataSchemes();
        int i6 = 0;
        boolean z = false;
        boolean z2 = true;
        while (i6 < countDataSchemes) {
            String dataScheme = intentFilter.getDataScheme(i6);
            if (dataScheme != null && !dataScheme.isEmpty()) {
                z = true;
            }
            int countDataSchemeSpecificParts = intentFilter.countDataSchemeSpecificParts();
            int i7 = i3;
            boolean z3 = true;
            while (i7 < countDataSchemeSpecificParts) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(dataScheme);
                PatternMatcher dataSchemeSpecificPart = intentFilter.getDataSchemeSpecificPart(i7);
                builder.opaquePart(dataSchemeSpecificPart.getPath());
                Intent intent2 = new Intent(intent);
                intent2.setData(builder.build());
                applyDefaultPreferredActivityLPw(packageManagerInternal, intent2, i4, componentName, dataScheme, dataSchemeSpecificPart, null, null, i);
                i7++;
                dataScheme = dataScheme;
                countDataSchemeSpecificParts = countDataSchemeSpecificParts;
                i6 = i6;
                z3 = false;
            }
            String str = dataScheme;
            int i8 = i6;
            int countDataAuthorities = intentFilter.countDataAuthorities();
            int i9 = 0;
            while (i9 < countDataAuthorities) {
                IntentFilter.AuthorityEntry dataAuthority = intentFilter.getDataAuthority(i9);
                int countDataPaths = intentFilter.countDataPaths();
                boolean z4 = true;
                int i10 = 0;
                while (i10 < countDataPaths) {
                    Uri.Builder builder2 = new Uri.Builder();
                    builder2.scheme(str);
                    if (dataAuthority.getHost() != null) {
                        builder2.authority(dataAuthority.getHost());
                    }
                    PatternMatcher dataPath = intentFilter.getDataPath(i10);
                    builder2.path(dataPath.getPath());
                    Intent intent3 = new Intent(intent);
                    intent3.setData(builder2.build());
                    applyDefaultPreferredActivityLPw(packageManagerInternal, intent3, i4, componentName, str, null, dataAuthority, dataPath, i);
                    i10++;
                    countDataAuthorities = countDataAuthorities;
                    countDataPaths = countDataPaths;
                    i9 = i9;
                    z3 = false;
                    z4 = false;
                }
                int i11 = i9;
                int i12 = countDataAuthorities;
                if (z4) {
                    Uri.Builder builder3 = new Uri.Builder();
                    builder3.scheme(str);
                    if (dataAuthority.getHost() != null) {
                        builder3.authority(dataAuthority.getHost());
                    }
                    Intent intent4 = new Intent(intent);
                    intent4.setData(builder3.build());
                    applyDefaultPreferredActivityLPw(packageManagerInternal, intent4, i4, componentName, str, null, dataAuthority, null, i);
                    z3 = false;
                }
                i9 = i11 + 1;
                countDataAuthorities = i12;
            }
            if (z3) {
                Uri.Builder builder4 = new Uri.Builder();
                builder4.scheme(str);
                Intent intent5 = new Intent(intent);
                intent5.setData(builder4.build());
                applyDefaultPreferredActivityLPw(packageManagerInternal, intent5, i4, componentName, str, null, null, null, i);
            }
            i6 = i8 + 1;
            i3 = 0;
            z2 = false;
        }
        int i13 = 0;
        while (i13 < intentFilter.countDataTypes()) {
            String dataType = intentFilter.getDataType(i13);
            if (z) {
                Uri.Builder builder5 = new Uri.Builder();
                int i14 = 0;
                while (i14 < intentFilter.countDataSchemes()) {
                    String dataScheme2 = intentFilter.getDataScheme(i14);
                    if (dataScheme2 == null || dataScheme2.isEmpty()) {
                        i2 = i14;
                    } else {
                        Intent intent6 = new Intent(intent);
                        builder5.scheme(dataScheme2);
                        intent6.setDataAndType(builder5.build(), dataType);
                        i2 = i14;
                        applyDefaultPreferredActivityLPw(packageManagerInternal, intent6, i4, componentName, dataScheme2, null, null, null, i);
                    }
                    i14 = i2 + 1;
                }
            } else {
                Intent intent7 = new Intent(intent);
                intent7.setType(dataType);
                applyDefaultPreferredActivityLPw(packageManagerInternal, intent7, i4, componentName, null, null, null, null, i);
            }
            i13++;
            z2 = false;
        }
        if (z2) {
            applyDefaultPreferredActivityLPw(packageManagerInternal, intent, i4, componentName, null, null, null, null, i);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:58:0x014d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void applyDefaultPreferredActivityLPw(PackageManagerInternal packageManagerInternal, Intent intent, int i, ComponentName componentName, String str, PatternMatcher patternMatcher, IntentFilter.AuthorityEntry authorityEntry, PatternMatcher patternMatcher2, int i2) {
        ComponentName componentName2;
        Settings settings;
        ArrayList<PreferredActivity> findFilters;
        List<ResolveInfo> queryIntentActivities = packageManagerInternal.queryIntentActivities(intent, intent.getType(), i, Binder.getCallingUid(), i2);
        int size = queryIntentActivities == null ? 0 : queryIntentActivities.size();
        if (size < 1) {
            Slog.w("PackageSettings", "No potential matches found for " + intent + " while setting preferred " + componentName.flattenToShortString());
            return;
        }
        int size2 = queryIntentActivities.size();
        ComponentName[] componentNameArr = new ComponentName[size2];
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        while (true) {
            if (i3 >= size) {
                componentName2 = null;
                break;
            }
            ActivityInfo activityInfo = queryIntentActivities.get(i3).activityInfo;
            int i5 = size;
            componentNameArr[i3] = new ComponentName(activityInfo.packageName, activityInfo.name);
            if ((activityInfo.applicationInfo.flags & 1) == 0) {
                if (queryIntentActivities.get(i3).match >= 0) {
                    componentName2 = componentNameArr[i3];
                    break;
                }
            } else if (componentName.getPackageName().equals(activityInfo.packageName) && componentName.getClassName().equals(activityInfo.name)) {
                i4 = queryIntentActivities.get(i3).match;
                z = true;
            }
            i3++;
            size = i5;
        }
        if (componentName2 != null && i4 > 0) {
            componentName2 = null;
        }
        if (!z || componentName2 != null) {
            if (componentName2 == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("No component ");
                sb.append(componentName.flattenToShortString());
                sb.append(" found setting preferred ");
                sb.append(intent);
                sb.append("; possible matches are ");
                for (int i6 = 0; i6 < size2; i6++) {
                    if (i6 > 0) {
                        sb.append(", ");
                    }
                    sb.append(componentNameArr[i6].flattenToShortString());
                }
                Slog.w("PackageSettings", sb.toString());
                return;
            }
            Slog.i("PackageSettings", "Not setting preferred " + intent + "; found third party match " + componentName2.flattenToShortString());
            return;
        }
        WatchedIntentFilter watchedIntentFilter = new WatchedIntentFilter();
        if (intent.getAction() != null) {
            watchedIntentFilter.addAction(intent.getAction());
        }
        if (intent.getCategories() != null) {
            for (String str2 : intent.getCategories()) {
                watchedIntentFilter.addCategory(str2);
            }
        }
        if ((65536 & i) != 0) {
            watchedIntentFilter.addCategory("android.intent.category.DEFAULT");
        }
        if (str != null) {
            watchedIntentFilter.addDataScheme(str);
        }
        if (patternMatcher != null) {
            watchedIntentFilter.addDataSchemeSpecificPart(patternMatcher.getPath(), patternMatcher.getType());
        }
        if (authorityEntry != null) {
            watchedIntentFilter.addDataAuthority(authorityEntry);
        }
        if (patternMatcher2 != null) {
            watchedIntentFilter.addDataPath(patternMatcher2);
        }
        if (intent.getType() != null) {
            try {
                watchedIntentFilter.addDataType(intent.getType());
                settings = this;
            } catch (IntentFilter.MalformedMimeTypeException unused) {
                Slog.w("PackageSettings", "Malformed mimetype " + intent.getType() + " for " + componentName);
            }
            PreferredIntentResolver editPreferredActivitiesLPw = settings.editPreferredActivitiesLPw(i2);
            findFilters = editPreferredActivitiesLPw.findFilters(watchedIntentFilter);
            if (findFilters != null) {
                removeFilters(editPreferredActivitiesLPw, watchedIntentFilter, findFilters);
            }
            editPreferredActivitiesLPw.addFilter((PackageDataSnapshot) null, (PackageDataSnapshot) new PreferredActivity(watchedIntentFilter, i4, componentNameArr, componentName, true));
        }
        settings = this;
        PreferredIntentResolver editPreferredActivitiesLPw2 = settings.editPreferredActivitiesLPw(i2);
        findFilters = editPreferredActivitiesLPw2.findFilters(watchedIntentFilter);
        if (findFilters != null) {
        }
        editPreferredActivitiesLPw2.addFilter((PackageDataSnapshot) null, (PackageDataSnapshot) new PreferredActivity(watchedIntentFilter, i4, componentNameArr, componentName, true));
    }

    public final void readDefaultPreferredActivitiesLPw(TypedXmlPullParser typedXmlPullParser, int i) throws XmlPullParserException, IOException {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    PreferredActivity preferredActivity = new PreferredActivity(typedXmlPullParser);
                    if (preferredActivity.mPref.getParseError() == null) {
                        applyDefaultPreferredActivityLPw(packageManagerInternal, preferredActivity.getIntentFilter(), preferredActivity.mPref.mComponent, i);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <preferred-activity> " + preferredActivity.mPref.getParseError() + " at " + typedXmlPullParser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <preferred-activities>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public final void readDisabledSysPackageLPw(TypedXmlPullParser typedXmlPullParser, List<UserInfo> list) throws XmlPullParserException, IOException {
        LegacyPermissionState legacyPermissionState;
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "realName");
        String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "codePath");
        String attributeValue4 = typedXmlPullParser.getAttributeValue((String) null, "requiredCpuAbi");
        String attributeValue5 = typedXmlPullParser.getAttributeValue((String) null, "nativeLibraryPath");
        String attributeValue6 = typedXmlPullParser.getAttributeValue((String) null, "primaryCpuAbi");
        String attributeValue7 = typedXmlPullParser.getAttributeValue((String) null, "secondaryCpuAbi");
        String attributeValue8 = typedXmlPullParser.getAttributeValue((String) null, "cpuAbiOverride");
        String str = (attributeValue6 != null || attributeValue4 == null) ? attributeValue6 : attributeValue4;
        long attributeLong = typedXmlPullParser.getAttributeLong((String) null, "version", 0L);
        int i = attributeValue3.contains("/priv-app/") ? 8 : 0;
        PackageSetting packageSetting = new PackageSetting(attributeValue, attributeValue2, new File(attributeValue3), attributeValue5, str, attributeValue7, attributeValue8, attributeLong, 1, i, 0, null, null, null, null, null, DomainVerificationManagerInternal.DISABLED_ID);
        long attributeLongHex = typedXmlPullParser.getAttributeLongHex((String) null, "ft", 0L);
        if (attributeLongHex == 0) {
            attributeLongHex = typedXmlPullParser.getAttributeLong((String) null, "ts", 0L);
        }
        packageSetting.setLastModifiedTime(attributeLongHex);
        packageSetting.setLastUpdateTime(typedXmlPullParser.getAttributeLongHex((String) null, "ut", 0L));
        packageSetting.setAppId(parseAppId(typedXmlPullParser));
        if (packageSetting.getAppId() <= 0) {
            int parseSharedUserAppId = parseSharedUserAppId(typedXmlPullParser);
            packageSetting.setAppId(parseSharedUserAppId);
            packageSetting.setSharedUserAppId(parseSharedUserAppId);
        }
        packageSetting.setLoadingProgress(typedXmlPullParser.getAttributeFloat((String) null, "loadingProgress", 0.0f));
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("perms")) {
                    if (packageSetting.hasSharedUser()) {
                        legacyPermissionState = getSettingLPr(packageSetting.getSharedUserAppId()).getLegacyPermissionState();
                    } else {
                        legacyPermissionState = packageSetting.getLegacyPermissionState();
                    }
                    readInstallPermissionsLPr(typedXmlPullParser, legacyPermissionState, list);
                } else if (typedXmlPullParser.getName().equals("uses-static-lib")) {
                    readUsesStaticLibLPw(typedXmlPullParser, packageSetting);
                } else if (typedXmlPullParser.getName().equals("uses-sdk-lib")) {
                    readUsesSdkLibLPw(typedXmlPullParser, packageSetting);
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <updated-package>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        this.mDisabledSysPackages.put(attributeValue, packageSetting);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:153:0x03a2
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public final void readPackageLPw(com.android.modules.utils.TypedXmlPullParser r72, java.util.List<android.content.pm.UserInfo> r73, android.util.ArrayMap<java.lang.String, java.lang.Long> r74) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 2193
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.p011pm.Settings.readPackageLPw(com.android.modules.utils.TypedXmlPullParser, java.util.List, android.util.ArrayMap):void");
    }

    public static int parseAppId(TypedXmlPullParser typedXmlPullParser) {
        return typedXmlPullParser.getAttributeInt((String) null, "userId", 0);
    }

    public static int parseSharedUserAppId(TypedXmlPullParser typedXmlPullParser) {
        return typedXmlPullParser.getAttributeInt((String) null, "sharedUserId", 0);
    }

    public void addInstallerPackageNames(InstallSource installSource) {
        String str = installSource.mInstallerPackageName;
        if (str != null) {
            this.mInstallerPackages.add(str);
        }
        String str2 = installSource.mInitiatingPackageName;
        if (str2 != null) {
            this.mInstallerPackages.add(str2);
        }
        String str3 = installSource.mOriginatingPackageName;
        if (str3 != null) {
            this.mInstallerPackages.add(str3);
        }
    }

    public final Pair<String, Set<String>> readMimeGroupLPw(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        if (attributeValue == null) {
            XmlUtils.skipCurrentTag(typedXmlPullParser);
            return null;
        }
        ArraySet arraySet = new ArraySet();
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("mime-type")) {
                    String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "value");
                    if (attributeValue2 != null) {
                        arraySet.add(attributeValue2);
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <mime-group>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        return Pair.create(attributeValue, arraySet);
    }

    public final void writeMimeGroupLPr(TypedXmlSerializer typedXmlSerializer, Map<String, Set<String>> map) throws IOException {
        if (map == null) {
            return;
        }
        for (String str : map.keySet()) {
            typedXmlSerializer.startTag((String) null, "mime-group");
            typedXmlSerializer.attribute((String) null, "name", str);
            for (String str2 : map.get(str)) {
                typedXmlSerializer.startTag((String) null, "mime-type");
                typedXmlSerializer.attribute((String) null, "value", str2);
                typedXmlSerializer.endTag((String) null, "mime-type");
            }
            typedXmlSerializer.endTag((String) null, "mime-group");
        }
    }

    public final void readDisabledComponentsLPw(PackageSetting packageSetting, TypedXmlPullParser typedXmlPullParser, int i) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
                    if (attributeValue != null) {
                        packageSetting.addDisabledComponent(attributeValue.intern(), i);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <disabled-components> has no name at " + typedXmlPullParser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <disabled-components>: " + typedXmlPullParser.getName());
                }
                XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }
    }

    public final void readEnabledComponentsLPw(PackageSetting packageSetting, TypedXmlPullParser typedXmlPullParser, int i) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
                    if (attributeValue != null) {
                        packageSetting.addEnabledComponent(attributeValue.intern(), i);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <enabled-components> has no name at " + typedXmlPullParser.getPositionDescription());
                    }
                } else {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element under <enabled-components>: " + typedXmlPullParser.getName());
                }
                XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }
    }

    public final void readSharedUserLPw(TypedXmlPullParser typedXmlPullParser, List<UserInfo> list) throws XmlPullParserException, IOException {
        SharedUserSetting sharedUserSetting = null;
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
        int parseAppId = parseAppId(typedXmlPullParser);
        boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "system", false);
        if (attributeValue == null) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: <shared-user> has no name at " + typedXmlPullParser.getPositionDescription());
        } else if (parseAppId == 0) {
            PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: shared-user " + attributeValue + " has bad appId " + parseAppId + " at " + typedXmlPullParser.getPositionDescription());
        } else {
            sharedUserSetting = addSharedUserLPw(attributeValue.intern(), parseAppId, attributeBoolean ? 1 : 0, 0);
            if (sharedUserSetting == null) {
                PackageManagerService.reportSettingsProblem(6, "Occurred while parsing settings at " + typedXmlPullParser.getPositionDescription());
            }
        }
        if (sharedUserSetting != null) {
            int depth = typedXmlPullParser.getDepth();
            while (true) {
                int next = typedXmlPullParser.next();
                if (next == 1) {
                    return;
                }
                if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                    return;
                }
                if (next != 3 && next != 4) {
                    String name = typedXmlPullParser.getName();
                    if (name.equals("sigs")) {
                        sharedUserSetting.signatures.readXml(typedXmlPullParser, this.mPastSignatures.untrackedStorage());
                    } else if (name.equals("perms")) {
                        readInstallPermissionsLPr(typedXmlPullParser, sharedUserSetting.getLegacyPermissionState(), list);
                    } else {
                        PackageManagerService.reportSettingsProblem(5, "Unknown element under <shared-user>: " + typedXmlPullParser.getName());
                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                    }
                }
            }
        } else {
            XmlUtils.skipCurrentTag(typedXmlPullParser);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00b7 A[Catch: all -> 0x0139, TryCatch #1 {all -> 0x0139, blocks: (B:8:0x0036, B:10:0x003f, B:13:0x0054, B:15:0x005a, B:17:0x0066, B:24:0x0079, B:28:0x0086, B:30:0x008d, B:32:0x0093, B:34:0x0099, B:38:0x00a8, B:43:0x00b2, B:45:0x00b7, B:48:0x00be, B:52:0x00d5), top: B:81:0x0036 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0102 A[Catch: all -> 0x013e, TryCatch #3 {all -> 0x013e, blocks: (B:56:0x010a, B:54:0x00fa, B:75:0x013c, B:55:0x0102, B:58:0x0113), top: B:85:0x00fa }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void createNewUserLI(PackageManagerService packageManagerService, Installer installer, int i, Set<String> set, String[] strArr) {
        PackageManagerTracedLock packageManagerTracedLock;
        boolean z;
        boolean z2;
        int i2;
        int i3;
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog("PackageSettingsTiming", 262144L);
        timingsTraceAndSlog.traceBegin("createNewUser-" + i);
        Installer.Batch batch = new Installer.Batch();
        boolean z3 = set == null;
        PackageManagerTracedLock packageManagerTracedLock2 = this.mLock;
        synchronized (packageManagerTracedLock2) {
            try {
                int size = this.mPackages.size();
                int i4 = 0;
                while (i4 < size) {
                    PackageSetting valueAt = this.mPackages.valueAt(i4);
                    if (valueAt.getPkg() != null) {
                        if (valueAt.isSystem() && !ArrayUtils.contains(strArr, valueAt.getPackageName()) && !valueAt.getPkgState().isHiddenUntilInstalled()) {
                            z = true;
                            z2 = !z && (z3 || set.contains(valueAt.getPackageName()));
                            valueAt.setInstalled(z2, i);
                            valueAt.setStopped((packageManagerService.mShouldStopSystemPackagesByDefault || !valueAt.isSystem() || valueAt.isApex() || packageManagerService.mInitialNonStoppedSystemPackages.contains(valueAt.getPackageName())) ? false : true, i);
                            valueAt.setUninstallReason((z || z2) ? 0 : 1, i);
                            if (!z2) {
                                if (valueAt.getAppId() >= 0) {
                                    i2 = i4;
                                    i3 = size;
                                    packageManagerTracedLock = packageManagerTracedLock2;
                                    try {
                                        batch.createAppData(Installer.buildCreateAppDataArgs(valueAt.getVolumeUuid(), valueAt.getPackageName(), i, 1, valueAt.getAppId(), valueAt.getSeInfo(), valueAt.getPkg().getTargetSdkVersion(), !valueAt.getPkg().getUsesSdkLibraries().isEmpty()));
                                    } catch (Throwable th) {
                                        th = th;
                                        throw th;
                                    }
                                }
                            } else {
                                i2 = i4;
                                i3 = size;
                                packageManagerTracedLock = packageManagerTracedLock2;
                                writeKernelMappingLPr(valueAt);
                            }
                            i4 = i2 + 1;
                            size = i3;
                            packageManagerTracedLock2 = packageManagerTracedLock;
                        }
                        z = false;
                        if (z) {
                        }
                        valueAt.setInstalled(z2, i);
                        valueAt.setStopped((packageManagerService.mShouldStopSystemPackagesByDefault || !valueAt.isSystem() || valueAt.isApex() || packageManagerService.mInitialNonStoppedSystemPackages.contains(valueAt.getPackageName())) ? false : true, i);
                        valueAt.setUninstallReason((z || z2) ? 0 : 1, i);
                        if (!z2) {
                        }
                        i4 = i2 + 1;
                        size = i3;
                        packageManagerTracedLock2 = packageManagerTracedLock;
                    }
                    i2 = i4;
                    i3 = size;
                    packageManagerTracedLock = packageManagerTracedLock2;
                    i4 = i2 + 1;
                    size = i3;
                    packageManagerTracedLock2 = packageManagerTracedLock;
                }
                timingsTraceAndSlog.traceBegin("createAppData");
                try {
                    batch.execute(installer);
                } catch (Installer.InstallerException e) {
                    Slog.w("PackageSettings", "Failed to prepare app data", e);
                }
                timingsTraceAndSlog.traceEnd();
                synchronized (this.mLock) {
                    applyDefaultPreferredAppsLPw(i);
                }
                timingsTraceAndSlog.traceEnd();
            } catch (Throwable th2) {
                th = th2;
                packageManagerTracedLock = packageManagerTracedLock2;
            }
        }
    }

    public void removeUserLPw(int i) {
        for (Map.Entry<String, PackageSetting> entry : this.mPackages.entrySet()) {
            entry.getValue().removeUser(i);
        }
        this.mPreferredActivities.remove(i);
        synchronized (this.mPackageRestrictionsLock) {
            getUserPackagesStateFile(i).delete();
            this.mPendingAsyncPackageRestrictionsWrites.delete(i);
        }
        removeCrossProfileIntentFiltersLPw(i);
        this.mRuntimePermissionsPersistence.onUserRemoved(i);
        this.mDomainVerificationManager.clearUser(i);
        writePackageListLPr();
        writeKernelRemoveUserLPr(i);
    }

    public void removeCrossProfileIntentFiltersLPw(int i) {
        synchronized (this.mCrossProfileIntentResolvers) {
            if (this.mCrossProfileIntentResolvers.get(i) != null) {
                this.mCrossProfileIntentResolvers.remove(i);
                writePackageRestrictionsLPr(i);
            }
            int size = this.mCrossProfileIntentResolvers.size();
            for (int i2 = 0; i2 < size; i2++) {
                int keyAt = this.mCrossProfileIntentResolvers.keyAt(i2);
                CrossProfileIntentResolver crossProfileIntentResolver = this.mCrossProfileIntentResolvers.get(keyAt);
                Iterator it = new ArraySet(crossProfileIntentResolver.filterSet()).iterator();
                boolean z = false;
                while (it.hasNext()) {
                    CrossProfileIntentFilter crossProfileIntentFilter = (CrossProfileIntentFilter) it.next();
                    if (crossProfileIntentFilter.getTargetUserId() == i) {
                        crossProfileIntentResolver.removeFilter((CrossProfileIntentResolver) crossProfileIntentFilter);
                        z = true;
                    }
                }
                if (z) {
                    writePackageRestrictionsLPr(keyAt);
                }
            }
        }
    }

    public VerifierDeviceIdentity getVerifierDeviceIdentityLPw(Computer computer) {
        if (this.mVerifierDeviceIdentity == null) {
            this.mVerifierDeviceIdentity = VerifierDeviceIdentity.generate();
            writeLPr(computer, false);
        }
        return this.mVerifierDeviceIdentity;
    }

    public PackageSetting getDisabledSystemPkgLPr(String str) {
        return this.mDisabledSysPackages.get(str);
    }

    public PackageSetting getDisabledSystemPkgLPr(PackageSetting packageSetting) {
        if (packageSetting == null) {
            return null;
        }
        return getDisabledSystemPkgLPr(packageSetting.getPackageName());
    }

    public int getApplicationEnabledSettingLPr(String str, int i) throws PackageManager.NameNotFoundException {
        PackageSetting packageSetting = this.mPackages.get(str);
        if (packageSetting == null) {
            throw new PackageManager.NameNotFoundException(str);
        }
        return packageSetting.getEnabled(i);
    }

    public int getComponentEnabledSettingLPr(ComponentName componentName, int i) throws PackageManager.NameNotFoundException {
        PackageSetting packageSetting = this.mPackages.get(componentName.getPackageName());
        if (packageSetting == null) {
            throw new PackageManager.NameNotFoundException(componentName.getPackageName());
        }
        return packageSetting.getCurrentEnabledStateLPr(componentName.getClassName(), i);
    }

    public SharedUserSetting getSharedUserSettingLPr(String str) {
        return getSharedUserSettingLPr(this.mPackages.get(str));
    }

    public SharedUserSetting getSharedUserSettingLPr(PackageSetting packageSetting) {
        if (packageSetting == null || !packageSetting.hasSharedUser()) {
            return null;
        }
        return (SharedUserSetting) getSettingLPr(packageSetting.getSharedUserAppId());
    }

    public static List<UserInfo> getAllUsers(UserManagerService userManagerService) {
        return getUsers(userManagerService, false, false);
    }

    public static List<UserInfo> getActiveUsers(UserManagerService userManagerService, boolean z) {
        return getUsers(userManagerService, z, true);
    }

    public static List<UserInfo> getUsers(UserManagerService userManagerService, boolean z, boolean z2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<UserInfo> users = userManagerService.getUsers(true, z, z2);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return users;
        } catch (NullPointerException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return null;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public List<? extends PackageStateInternal> getVolumePackagesLPr(String str) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mPackages.size(); i++) {
            PackageSetting valueAt = this.mPackages.valueAt(i);
            if (Objects.equals(str, valueAt.getVolumeUuid())) {
                arrayList.add(valueAt);
            }
        }
        return arrayList;
    }

    public static void printFlags(PrintWriter printWriter, int i, Object[] objArr) {
        printWriter.print("[ ");
        for (int i2 = 0; i2 < objArr.length; i2 += 2) {
            if ((((Integer) objArr[i2]).intValue() & i) != 0) {
                printWriter.print(objArr[i2 + 1]);
                printWriter.print(" ");
            }
        }
        printWriter.print("]");
    }

    static {
        Integer valueOf = Integer.valueOf((int) IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
        FLAG_DUMP_SPEC = new Object[]{1, "SYSTEM", 2, "DEBUGGABLE", 4, "HAS_CODE", 8, "PERSISTENT", 16, "FACTORY_TEST", 32, "ALLOW_TASK_REPARENTING", 64, "ALLOW_CLEAR_USER_DATA", 128, "UPDATED_SYSTEM_APP", 256, "TEST_ONLY", 16384, "VM_SAFE_MODE", 32768, "ALLOW_BACKUP", 65536, "KILL_AFTER_RESTORE", valueOf, "RESTORE_ANY_VERSION", 262144, "EXTERNAL_STORAGE", 1048576, "LARGE_HEAP"};
        PRIVATE_FLAG_DUMP_SPEC = new Object[]{1024, "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_RESIZEABLE", Integer.valueOf((int) IInstalld.FLAG_USE_QUOTA), "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_RESIZEABLE_VIA_SDK_VERSION", Integer.valueOf((int) IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES), "PRIVATE_FLAG_ACTIVITIES_RESIZE_MODE_UNRESIZEABLE", 134217728, "ALLOW_AUDIO_PLAYBACK_CAPTURE", 536870912, "PRIVATE_FLAG_REQUEST_LEGACY_EXTERNAL_STORAGE", Integer.valueOf((int) IInstalld.FLAG_FORCE), "BACKUP_IN_FOREGROUND", 2, "CANT_SAVE_STATE", 32, "DEFAULT_TO_DEVICE_PROTECTED_STORAGE", 64, "DIRECT_BOOT_AWARE", 16, "HAS_DOMAIN_URLS", 1, "HIDDEN", 128, "EPHEMERAL", 32768, "ISOLATED_SPLIT_LOADING", valueOf, "OEM", 256, "PARTIALLY_DIRECT_BOOT_AWARE", 8, "PRIVILEGED", 512, "REQUIRED_FOR_SYSTEM_USER", 16384, "STATIC_SHARED_LIBRARY", 262144, "VENDOR", 524288, "PRODUCT", 2097152, "SYSTEM_EXT", 65536, "VIRTUAL_PRELOAD", 1073741824, "ODM", Integer.MIN_VALUE, "PRIVATE_FLAG_ALLOW_NATIVE_HEAP_POINTER_TAGGING"};
    }

    public void dumpVersionLPr(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mVersion.size(); i++) {
            String keyAt = this.mVersion.keyAt(i);
            VersionInfo valueAt = this.mVersion.valueAt(i);
            if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, keyAt)) {
                indentingPrintWriter.println("Internal:");
            } else if ("primary_physical".equals(keyAt)) {
                indentingPrintWriter.println("External:");
            } else {
                indentingPrintWriter.println("UUID " + keyAt + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
            }
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.printPair("sdkVersion", Integer.valueOf(valueAt.sdkVersion));
            indentingPrintWriter.printPair("databaseVersion", Integer.valueOf(valueAt.databaseVersion));
            indentingPrintWriter.println();
            indentingPrintWriter.printPair("buildFingerprint", valueAt.buildFingerprint);
            indentingPrintWriter.printPair("fingerprint", valueAt.fingerprint);
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
    }

    @NeverCompile
    public void dumpPackageLPr(PrintWriter printWriter, String str, String str2, ArraySet<String> arraySet, PackageSetting packageSetting, LegacyPermissionState legacyPermissionState, SimpleDateFormat simpleDateFormat, Date date, List<UserInfo> list, boolean z, boolean z2) {
        boolean z3;
        AndroidPackageInternal pkg = packageSetting.getPkg();
        if (str2 != null) {
            printWriter.print(str2);
            printWriter.print(",");
            printWriter.print(packageSetting.getRealName() != null ? packageSetting.getRealName() : packageSetting.getPackageName());
            printWriter.print(",");
            printWriter.print(packageSetting.getAppId());
            printWriter.print(",");
            printWriter.print(packageSetting.getVersionCode());
            printWriter.print(",");
            printWriter.print(packageSetting.getLastUpdateTime());
            printWriter.print(",");
            printWriter.print(packageSetting.getInstallSource().mInstallerPackageName != null ? packageSetting.getInstallSource().mInstallerPackageName : "?");
            printWriter.print(packageSetting.getInstallSource().mInstallerPackageUid);
            printWriter.print(packageSetting.getInstallSource().mUpdateOwnerPackageName != null ? packageSetting.getInstallSource().mUpdateOwnerPackageName : "?");
            printWriter.print(packageSetting.getInstallSource().mInstallerAttributionTag != null ? "(" + packageSetting.getInstallSource().mInstallerAttributionTag + ")" : "");
            printWriter.print(",");
            printWriter.print(packageSetting.getInstallSource().mPackageSource);
            printWriter.println();
            if (pkg != null) {
                printWriter.print(str2);
                printWriter.print(PackageManagerShellCommandDataLoader.STDIN_PATH);
                printWriter.print("splt,");
                printWriter.print("base,");
                printWriter.println(pkg.getBaseRevisionCode());
                int[] splitRevisionCodes = pkg.getSplitRevisionCodes();
                for (int i = 0; i < pkg.getSplitNames().length; i++) {
                    printWriter.print(str2);
                    printWriter.print(PackageManagerShellCommandDataLoader.STDIN_PATH);
                    printWriter.print("splt,");
                    printWriter.print(pkg.getSplitNames()[i]);
                    printWriter.print(",");
                    printWriter.println(splitRevisionCodes[i]);
                }
            }
            for (UserInfo userInfo : list) {
                PackageUserStateInternal userStateOrDefault = packageSetting.getUserStateOrDefault(userInfo.id);
                printWriter.print(str2);
                printWriter.print(PackageManagerShellCommandDataLoader.STDIN_PATH);
                printWriter.print("usr");
                printWriter.print(",");
                printWriter.print(userInfo.id);
                printWriter.print(",");
                printWriter.print(userStateOrDefault.isInstalled() ? "I" : "i");
                printWriter.print(userStateOrDefault.isHidden() ? "B" : "b");
                printWriter.print(userStateOrDefault.isSuspended() ? "SU" : "su");
                printWriter.print(userStateOrDefault.isStopped() ? "S" : "s");
                printWriter.print(userStateOrDefault.isNotLaunched() ? "l" : "L");
                printWriter.print(userStateOrDefault.isInstantApp() ? "IA" : "ia");
                printWriter.print(userStateOrDefault.isVirtualPreload() ? "VPI" : "vpi");
                printWriter.print(userStateOrDefault.getHarmfulAppWarning() != null ? "HA" : "ha");
                printWriter.print(",");
                printWriter.print(userStateOrDefault.getEnabledState());
                String lastDisableAppCaller = userStateOrDefault.getLastDisableAppCaller();
                printWriter.print(",");
                if (lastDisableAppCaller == null) {
                    lastDisableAppCaller = "?";
                }
                printWriter.print(lastDisableAppCaller);
                printWriter.print(",");
                printWriter.print(packageSetting.readUserState(userInfo.id).getFirstInstallTimeMillis());
                printWriter.print(",");
                printWriter.println();
            }
            return;
        }
        printWriter.print(str);
        printWriter.print("Package [");
        printWriter.print(packageSetting.getRealName() != null ? packageSetting.getRealName() : packageSetting.getPackageName());
        printWriter.print("] (");
        printWriter.print(Integer.toHexString(System.identityHashCode(packageSetting)));
        printWriter.println("):");
        if (packageSetting.getRealName() != null) {
            printWriter.print(str);
            printWriter.print("  compat name=");
            printWriter.println(packageSetting.getPackageName());
        }
        printWriter.print(str);
        printWriter.print("  appId=");
        printWriter.println(packageSetting.getAppId());
        SharedUserSetting sharedUserSettingLPr = getSharedUserSettingLPr(packageSetting);
        if (sharedUserSettingLPr != null) {
            printWriter.print(str);
            printWriter.print("  sharedUser=");
            printWriter.println(sharedUserSettingLPr);
        }
        printWriter.print(str);
        printWriter.print("  pkg=");
        printWriter.println(pkg);
        printWriter.print(str);
        printWriter.print("  codePath=");
        printWriter.println(packageSetting.getPathString());
        if (arraySet == null) {
            printWriter.print(str);
            printWriter.print("  resourcePath=");
            printWriter.println(packageSetting.getPathString());
            printWriter.print(str);
            printWriter.print("  legacyNativeLibraryDir=");
            printWriter.println(packageSetting.getLegacyNativeLibraryPath());
            printWriter.print(str);
            printWriter.print("  extractNativeLibs=");
            printWriter.println((packageSetting.getFlags() & 268435456) != 0 ? "true" : "false");
            printWriter.print(str);
            printWriter.print("  primaryCpuAbi=");
            printWriter.println(packageSetting.getPrimaryCpuAbiLegacy());
            printWriter.print(str);
            printWriter.print("  secondaryCpuAbi=");
            printWriter.println(packageSetting.getSecondaryCpuAbiLegacy());
            printWriter.print(str);
            printWriter.print("  cpuAbiOverride=");
            printWriter.println(packageSetting.getCpuAbiOverride());
        }
        printWriter.print(str);
        printWriter.print("  versionCode=");
        printWriter.print(packageSetting.getVersionCode());
        if (pkg != null) {
            printWriter.print(" minSdk=");
            printWriter.print(pkg.getMinSdkVersion());
            printWriter.print(" targetSdk=");
            printWriter.println(pkg.getTargetSdkVersion());
            SparseIntArray minExtensionVersions = pkg.getMinExtensionVersions();
            printWriter.print(str);
            printWriter.print("  minExtensionVersions=[");
            if (minExtensionVersions != null) {
                ArrayList arrayList = new ArrayList();
                int size = minExtensionVersions.size();
                for (int i2 = 0; i2 < size; i2++) {
                    arrayList.add(minExtensionVersions.keyAt(i2) + "=" + minExtensionVersions.valueAt(i2));
                }
                printWriter.print(TextUtils.join(", ", arrayList));
            }
            printWriter.print("]");
        }
        printWriter.println();
        if (pkg != null) {
            printWriter.print(str);
            printWriter.print("  versionName=");
            printWriter.println(pkg.getVersionName());
            printWriter.print(str);
            printWriter.print("  usesNonSdkApi=");
            printWriter.println(pkg.isNonSdkApiRequested());
            printWriter.print(str);
            printWriter.print("  splits=");
            dumpSplitNames(printWriter, pkg);
            printWriter.println();
            int signatureSchemeVersion = pkg.getSigningDetails().getSignatureSchemeVersion();
            printWriter.print(str);
            printWriter.print("  apkSigningVersion=");
            printWriter.println(signatureSchemeVersion);
            printWriter.print(str);
            printWriter.print("  flags=");
            printFlags(printWriter, PackageInfoUtils.appInfoFlags(pkg, packageSetting), FLAG_DUMP_SPEC);
            printWriter.println();
            int appInfoPrivateFlags = PackageInfoUtils.appInfoPrivateFlags(pkg, packageSetting);
            if (appInfoPrivateFlags != 0) {
                printWriter.print(str);
                printWriter.print("  privateFlags=");
                printFlags(printWriter, appInfoPrivateFlags, PRIVATE_FLAG_DUMP_SPEC);
                printWriter.println();
            }
            if (pkg.hasPreserveLegacyExternalStorage()) {
                printWriter.print(str);
                printWriter.print("  hasPreserveLegacyExternalStorage=true");
                printWriter.println();
            }
            printWriter.print(str);
            printWriter.print("  forceQueryable=");
            printWriter.print(packageSetting.getPkg().isForceQueryable());
            if (packageSetting.isForceQueryableOverride()) {
                printWriter.print(" (override=true)");
            }
            printWriter.println();
            if (!packageSetting.getPkg().getQueriesPackages().isEmpty()) {
                printWriter.append((CharSequence) str).append((CharSequence) "  queriesPackages=").println(packageSetting.getPkg().getQueriesPackages());
            }
            if (!packageSetting.getPkg().getQueriesIntents().isEmpty()) {
                printWriter.append((CharSequence) str).append((CharSequence) "  queriesIntents=").println(packageSetting.getPkg().getQueriesIntents());
            }
            File dataDir = PackageInfoUtils.getDataDir(pkg, UserHandle.myUserId());
            printWriter.print(str);
            printWriter.print("  dataDir=");
            printWriter.println(dataDir.getAbsolutePath());
            printWriter.print(str);
            printWriter.print("  supportsScreens=[");
            if (pkg.isSmallScreensSupported()) {
                printWriter.print("small");
                z3 = false;
            } else {
                z3 = true;
            }
            if (pkg.isNormalScreensSupported()) {
                if (!z3) {
                    printWriter.print(", ");
                }
                printWriter.print("medium");
                z3 = false;
            }
            if (pkg.isLargeScreensSupported()) {
                if (!z3) {
                    printWriter.print(", ");
                }
                printWriter.print("large");
                z3 = false;
            }
            if (pkg.isExtraLargeScreensSupported()) {
                if (!z3) {
                    printWriter.print(", ");
                }
                printWriter.print("xlarge");
                z3 = false;
            }
            if (pkg.isResizeable()) {
                if (!z3) {
                    printWriter.print(", ");
                }
                printWriter.print("resizeable");
                z3 = false;
            }
            if (pkg.isAnyDensity()) {
                if (!z3) {
                    printWriter.print(", ");
                }
                printWriter.print("anyDensity");
            }
            printWriter.println("]");
            List<String> libraryNames = pkg.getLibraryNames();
            if (libraryNames != null && libraryNames.size() > 0) {
                printWriter.print(str);
                printWriter.println("  dynamic libraries:");
                for (int i3 = 0; i3 < libraryNames.size(); i3++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(libraryNames.get(i3));
                }
            }
            if (pkg.getStaticSharedLibraryName() != null) {
                printWriter.print(str);
                printWriter.println("  static library:");
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.print("name:");
                printWriter.print(pkg.getStaticSharedLibraryName());
                printWriter.print(" version:");
                printWriter.println(pkg.getStaticSharedLibraryVersion());
            }
            if (pkg.getSdkLibraryName() != null) {
                printWriter.print(str);
                printWriter.println("  SDK library:");
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.print("name:");
                printWriter.print(pkg.getSdkLibraryName());
                printWriter.print(" versionMajor:");
                printWriter.println(pkg.getSdkLibVersionMajor());
            }
            List<String> usesLibraries = pkg.getUsesLibraries();
            if (usesLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesLibraries:");
                for (int i4 = 0; i4 < usesLibraries.size(); i4++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(usesLibraries.get(i4));
                }
            }
            List<String> usesStaticLibraries = pkg.getUsesStaticLibraries();
            long[] usesStaticLibrariesVersions = pkg.getUsesStaticLibrariesVersions();
            if (usesStaticLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesStaticLibraries:");
                for (int i5 = 0; i5 < usesStaticLibraries.size(); i5++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.print(usesStaticLibraries.get(i5));
                    printWriter.print(" version:");
                    printWriter.println(usesStaticLibrariesVersions[i5]);
                }
            }
            List<String> usesSdkLibraries = pkg.getUsesSdkLibraries();
            long[] usesSdkLibrariesVersionsMajor = pkg.getUsesSdkLibrariesVersionsMajor();
            if (usesSdkLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesSdkLibraries:");
                int size2 = usesSdkLibraries.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.print(usesSdkLibraries.get(i6));
                    printWriter.print(" version:");
                    printWriter.println(usesSdkLibrariesVersionsMajor[i6]);
                }
            }
            List<String> usesOptionalLibraries = pkg.getUsesOptionalLibraries();
            if (usesOptionalLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesOptionalLibraries:");
                for (int i7 = 0; i7 < usesOptionalLibraries.size(); i7++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(usesOptionalLibraries.get(i7));
                }
            }
            List<String> usesNativeLibraries = pkg.getUsesNativeLibraries();
            if (usesNativeLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesNativeLibraries:");
                for (int i8 = 0; i8 < usesNativeLibraries.size(); i8++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(usesNativeLibraries.get(i8));
                }
            }
            List<String> usesOptionalNativeLibraries = pkg.getUsesOptionalNativeLibraries();
            if (usesOptionalNativeLibraries.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesOptionalNativeLibraries:");
                for (int i9 = 0; i9 < usesOptionalNativeLibraries.size(); i9++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(usesOptionalNativeLibraries.get(i9));
                }
            }
            List<String> usesLibraryFiles = packageSetting.getPkgState().getUsesLibraryFiles();
            if (usesLibraryFiles.size() > 0) {
                printWriter.print(str);
                printWriter.println("  usesLibraryFiles:");
                for (int i10 = 0; i10 < usesLibraryFiles.size(); i10++) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(usesLibraryFiles.get(i10));
                }
            }
            Map<String, ParsedProcess> processes = pkg.getProcesses();
            if (!processes.isEmpty()) {
                printWriter.print(str);
                printWriter.println("  processes:");
                for (ParsedProcess parsedProcess : processes.values()) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(parsedProcess.getName());
                    if (parsedProcess.getDeniedPermissions() != null) {
                        for (String str3 : parsedProcess.getDeniedPermissions()) {
                            printWriter.print(str);
                            printWriter.print("      deny: ");
                            printWriter.println(str3);
                        }
                    }
                }
            }
        }
        printWriter.print(str);
        printWriter.print("  timeStamp=");
        date.setTime(packageSetting.getLastModifiedTime());
        printWriter.println(simpleDateFormat.format(date));
        printWriter.print(str);
        printWriter.print("  lastUpdateTime=");
        date.setTime(packageSetting.getLastUpdateTime());
        printWriter.println(simpleDateFormat.format(date));
        printWriter.print(str);
        printWriter.print("  installerPackageName=");
        printWriter.println(packageSetting.getInstallSource().mInstallerPackageName);
        printWriter.print(str);
        printWriter.print("  installerPackageUid=");
        printWriter.println(packageSetting.getInstallSource().mInstallerPackageUid);
        printWriter.print(str);
        printWriter.print("  initiatingPackageName=");
        printWriter.println(packageSetting.getInstallSource().mInitiatingPackageName);
        printWriter.print(str);
        printWriter.print("  originatingPackageName=");
        printWriter.println(packageSetting.getInstallSource().mOriginatingPackageName);
        if (packageSetting.getInstallSource().mUpdateOwnerPackageName != null) {
            printWriter.print(str);
            printWriter.print("  updateOwnerPackageName=");
            printWriter.println(packageSetting.getInstallSource().mUpdateOwnerPackageName);
        }
        if (packageSetting.getInstallSource().mInstallerAttributionTag != null) {
            printWriter.print(str);
            printWriter.print("  installerAttributionTag=");
            printWriter.println(packageSetting.getInstallSource().mInstallerAttributionTag);
        }
        printWriter.print(str);
        printWriter.print("  packageSource=");
        printWriter.println(packageSetting.getInstallSource().mPackageSource);
        if (packageSetting.isLoading()) {
            printWriter.print(str);
            printWriter.println("  loadingProgress=" + ((int) (packageSetting.getLoadingProgress() * 100.0f)) + "%");
        }
        if (packageSetting.getVolumeUuid() != null) {
            printWriter.print(str);
            printWriter.print("  volumeUuid=");
            printWriter.println(packageSetting.getVolumeUuid());
        }
        printWriter.print(str);
        printWriter.print("  signatures=");
        printWriter.println(packageSetting.getSignatures());
        printWriter.print(str);
        printWriter.print("  installPermissionsFixed=");
        printWriter.print(packageSetting.isInstallPermissionsFixed());
        printWriter.println();
        printWriter.print(str);
        printWriter.print("  pkgFlags=");
        printFlags(printWriter, packageSetting.getFlags(), FLAG_DUMP_SPEC);
        printWriter.println();
        printWriter.print(str);
        printWriter.print("  privatePkgFlags=");
        printFlags(printWriter, packageSetting.getPrivateFlags(), PRIVATE_FLAG_DUMP_SPEC);
        printWriter.println();
        printWriter.print(str);
        printWriter.print("  apexModuleName=");
        printWriter.println(packageSetting.getApexModuleName());
        if (pkg != null && pkg.getOverlayTarget() != null) {
            printWriter.print(str);
            printWriter.print("  overlayTarget=");
            printWriter.println(pkg.getOverlayTarget());
            printWriter.print(str);
            printWriter.print("  overlayCategory=");
            printWriter.println(pkg.getOverlayCategory());
        }
        if (pkg != null && !pkg.getPermissions().isEmpty()) {
            List<ParsedPermission> permissions = pkg.getPermissions();
            printWriter.print(str);
            printWriter.println("  declared permissions:");
            for (int i11 = 0; i11 < permissions.size(); i11++) {
                ParsedPermission parsedPermission = permissions.get(i11);
                if (arraySet == null || arraySet.contains(parsedPermission.getName())) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.print(parsedPermission.getName());
                    printWriter.print(": prot=");
                    printWriter.print(PermissionInfo.protectionToString(parsedPermission.getProtectionLevel()));
                    if ((parsedPermission.getFlags() & 1) != 0) {
                        printWriter.print(", COSTS_MONEY");
                    }
                    if ((parsedPermission.getFlags() & 2) != 0) {
                        printWriter.print(", HIDDEN");
                    }
                    if ((parsedPermission.getFlags() & 1073741824) != 0) {
                        printWriter.print(", INSTALLED");
                    }
                    printWriter.println();
                }
            }
        }
        if ((arraySet != null || z) && pkg != null && pkg.getRequestedPermissions() != null && pkg.getRequestedPermissions().size() > 0) {
            List<String> requestedPermissions = pkg.getRequestedPermissions();
            printWriter.print(str);
            printWriter.println("  requested permissions:");
            for (int i12 = 0; i12 < requestedPermissions.size(); i12++) {
                String str4 = requestedPermissions.get(i12);
                if (arraySet == null || arraySet.contains(str4)) {
                    printWriter.print(str);
                    printWriter.print("    ");
                    printWriter.println(str4);
                }
            }
        }
        if (!packageSetting.hasSharedUser() || arraySet != null || z) {
            dumpInstallPermissionsLPr(printWriter, str + "  ", arraySet, legacyPermissionState, list);
        }
        if (z2) {
            dumpComponents(printWriter, str + "  ", packageSetting);
        }
        for (UserInfo userInfo2 : list) {
            PackageUserStateInternal userStateOrDefault2 = packageSetting.getUserStateOrDefault(userInfo2.id);
            printWriter.print(str);
            printWriter.print("  User ");
            printWriter.print(userInfo2.id);
            printWriter.print(": ");
            printWriter.print("ceDataInode=");
            printWriter.print(userStateOrDefault2.getCeDataInode());
            printWriter.print(" installed=");
            printWriter.print(userStateOrDefault2.isInstalled());
            printWriter.print(" hidden=");
            printWriter.print(userStateOrDefault2.isHidden());
            printWriter.print(" suspended=");
            printWriter.print(userStateOrDefault2.isSuspended());
            printWriter.print(" distractionFlags=");
            printWriter.print(userStateOrDefault2.getDistractionFlags());
            printWriter.print(" stopped=");
            printWriter.print(userStateOrDefault2.isStopped());
            printWriter.print(" notLaunched=");
            printWriter.print(userStateOrDefault2.isNotLaunched());
            printWriter.print(" enabled=");
            printWriter.print(userStateOrDefault2.getEnabledState());
            printWriter.print(" instant=");
            printWriter.print(userStateOrDefault2.isInstantApp());
            printWriter.print(" virtual=");
            printWriter.println(userStateOrDefault2.isVirtualPreload());
            printWriter.print("      installReason=");
            printWriter.println(userStateOrDefault2.getInstallReason());
            PackageUserStateInternal readUserState = packageSetting.readUserState(userInfo2.id);
            printWriter.print("      firstInstallTime=");
            date.setTime(readUserState.getFirstInstallTimeMillis());
            printWriter.println(simpleDateFormat.format(date));
            printWriter.print("      uninstallReason=");
            printWriter.println(userStateOrDefault2.getUninstallReason());
            if (userStateOrDefault2.isSuspended()) {
                printWriter.print(str);
                printWriter.println("  Suspend params:");
                for (int i13 = 0; i13 < userStateOrDefault2.getSuspendParams().size(); i13++) {
                    printWriter.print(str);
                    printWriter.print("    suspendingPackage=");
                    printWriter.print(userStateOrDefault2.getSuspendParams().keyAt(i13));
                    SuspendParams valueAt = userStateOrDefault2.getSuspendParams().valueAt(i13);
                    if (valueAt != null) {
                        printWriter.print(" dialogInfo=");
                        printWriter.print(valueAt.getDialogInfo());
                    }
                    printWriter.println();
                }
            }
            OverlayPaths overlayPaths = userStateOrDefault2.getOverlayPaths();
            if (overlayPaths != null) {
                if (!overlayPaths.getOverlayPaths().isEmpty()) {
                    printWriter.print(str);
                    printWriter.println("    overlay paths:");
                    for (String str5 : overlayPaths.getOverlayPaths()) {
                        printWriter.print(str);
                        printWriter.print("      ");
                        printWriter.println(str5);
                    }
                }
                if (!overlayPaths.getResourceDirs().isEmpty()) {
                    printWriter.print(str);
                    printWriter.println("    legacy overlay paths:");
                    for (String str6 : overlayPaths.getResourceDirs()) {
                        printWriter.print(str);
                        printWriter.print("      ");
                        printWriter.println(str6);
                    }
                }
            }
            Map<String, OverlayPaths> sharedLibraryOverlayPaths = userStateOrDefault2.getSharedLibraryOverlayPaths();
            if (sharedLibraryOverlayPaths != null) {
                Iterator<Map.Entry<String, OverlayPaths>> it = sharedLibraryOverlayPaths.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, OverlayPaths> next = it.next();
                    OverlayPaths value = next.getValue();
                    if (value != null) {
                        if (!value.getOverlayPaths().isEmpty()) {
                            printWriter.print(str);
                            printWriter.println("    ");
                            printWriter.print(next.getKey());
                            printWriter.println(" overlay paths:");
                            for (String str7 : value.getOverlayPaths()) {
                                printWriter.print(str);
                                printWriter.print("        ");
                                printWriter.println(str7);
                                it = it;
                            }
                        }
                        Iterator<Map.Entry<String, OverlayPaths>> it2 = it;
                        if (!value.getResourceDirs().isEmpty()) {
                            printWriter.print(str);
                            printWriter.println("      ");
                            printWriter.print(next.getKey());
                            printWriter.println(" legacy overlay paths:");
                            for (String str8 : value.getResourceDirs()) {
                                printWriter.print(str);
                                printWriter.print("      ");
                                printWriter.println(str8);
                            }
                        }
                        it = it2;
                    }
                }
            }
            String lastDisableAppCaller2 = userStateOrDefault2.getLastDisableAppCaller();
            if (lastDisableAppCaller2 != null) {
                printWriter.print(str);
                printWriter.print("    lastDisabledCaller: ");
                printWriter.println(lastDisableAppCaller2);
            }
            if (!packageSetting.hasSharedUser()) {
                dumpGidsLPr(printWriter, str + "    ", this.mPermissionDataProvider.getGidsForUid(UserHandle.getUid(userInfo2.id, packageSetting.getAppId())));
                dumpRuntimePermissionsLPr(printWriter, str + "    ", arraySet, legacyPermissionState.getPermissionStates(userInfo2.id), z);
            }
            String harmfulAppWarning = userStateOrDefault2.getHarmfulAppWarning();
            if (harmfulAppWarning != null) {
                printWriter.print(str);
                printWriter.print("      harmfulAppWarning: ");
                printWriter.println(harmfulAppWarning);
            }
            if (arraySet == null) {
                WatchedArraySet<String> disabledComponentsNoCopy = userStateOrDefault2.getDisabledComponentsNoCopy();
                if (disabledComponentsNoCopy != null && disabledComponentsNoCopy.size() > 0) {
                    printWriter.print(str);
                    printWriter.println("    disabledComponents:");
                    for (int i14 = 0; i14 < disabledComponentsNoCopy.size(); i14++) {
                        printWriter.print(str);
                        printWriter.print("      ");
                        printWriter.println(disabledComponentsNoCopy.valueAt(i14));
                    }
                }
                WatchedArraySet<String> enabledComponentsNoCopy = userStateOrDefault2.getEnabledComponentsNoCopy();
                if (enabledComponentsNoCopy != null && enabledComponentsNoCopy.size() > 0) {
                    printWriter.print(str);
                    printWriter.println("    enabledComponents:");
                    for (int i15 = 0; i15 < enabledComponentsNoCopy.size(); i15++) {
                        printWriter.print(str);
                        printWriter.print("      ");
                        printWriter.println(enabledComponentsNoCopy.valueAt(i15));
                    }
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x0111, code lost:
        if (r1 != false) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0117, code lost:
        if (r30.onTitlePrinted() == false) goto L69;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0119, code lost:
        r27.println();
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x011c, code lost:
        r27.println("Renamed packages:");
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0123, code lost:
        r27.print("  ");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dumpPackagesLPr(PrintWriter printWriter, String str, ArraySet<String> arraySet, DumpState dumpState, boolean z) {
        int i;
        boolean z2;
        boolean z3;
        DumpState dumpState2 = dumpState;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        boolean isOptionEnabled = dumpState2.isOptionEnabled(2);
        List<UserInfo> allUsers = getAllUsers(UserManagerService.getInstance());
        Iterator<PackageSetting> it = this.mPackages.values().iterator();
        boolean z4 = false;
        while (true) {
            i = 8;
            if (!it.hasNext()) {
                break;
            }
            PackageSetting next = it.next();
            if (str == null || str.equals(next.getRealName()) || str.equals(next.getPackageName())) {
                if (next.getPkg() == null || !next.getPkg().isApex() || dumpState2.isOptionEnabled(8)) {
                    LegacyPermissionState legacyPermissionState = this.mPermissionDataProvider.getLegacyPermissionState(next.getAppId());
                    if (arraySet == null || legacyPermissionState.hasPermissionState(arraySet)) {
                        if (!z && str != null) {
                            dumpState2.setSharedUser(getSharedUserSettingLPr(next));
                        }
                        if (z || z4) {
                            z3 = z4;
                        } else {
                            if (dumpState.onTitlePrinted()) {
                                printWriter.println();
                            }
                            printWriter.println("Packages:");
                            z3 = true;
                        }
                        dumpPackageLPr(printWriter, "  ", z ? "pkg" : null, arraySet, next, legacyPermissionState, simpleDateFormat, date, allUsers, str != null, isOptionEnabled);
                        dumpState2 = dumpState;
                        z4 = z3;
                        simpleDateFormat = simpleDateFormat;
                    }
                }
            }
        }
        SimpleDateFormat simpleDateFormat2 = simpleDateFormat;
        if (this.mRenamedPackages.size() > 0 && arraySet == null) {
            boolean z5 = false;
            for (Map.Entry<String, String> entry : this.mRenamedPackages.entrySet()) {
                if (str == null || str.equals(entry.getKey()) || str.equals(entry.getValue())) {
                    printWriter.print("ren,");
                    printWriter.print(entry.getKey());
                    printWriter.print(z ? " -> " : ",");
                    printWriter.println(entry.getValue());
                }
            }
        }
        if (this.mDisabledSysPackages.size() <= 0 || arraySet != null) {
            return;
        }
        boolean z6 = false;
        for (PackageSetting packageSetting : this.mDisabledSysPackages.values()) {
            if (str == null || str.equals(packageSetting.getRealName()) || str.equals(packageSetting.getPackageName())) {
                if (packageSetting.getPkg() != null && packageSetting.getPkg().isApex()) {
                    if (!dumpState.isOptionEnabled(i)) {
                    }
                }
                if (z || z6) {
                    z2 = z6;
                } else {
                    if (dumpState.onTitlePrinted()) {
                        printWriter.println();
                    }
                    printWriter.println("Hidden system packages:");
                    z2 = true;
                }
                dumpPackageLPr(printWriter, "  ", z ? "dis" : null, arraySet, packageSetting, this.mPermissionDataProvider.getLegacyPermissionState(packageSetting.getAppId()), simpleDateFormat2, date, allUsers, str != null, isOptionEnabled);
                z6 = z2;
                i = i;
            }
        }
    }

    public void dumpPackagesProto(ProtoOutputStream protoOutputStream) {
        List<UserInfo> allUsers = getAllUsers(UserManagerService.getInstance());
        int size = this.mPackages.size();
        for (int i = 0; i < size; i++) {
            this.mPackages.valueAt(i).dumpDebug(protoOutputStream, 2246267895813L, allUsers, this.mPermissionDataProvider);
        }
    }

    public void dumpPermissions(PrintWriter printWriter, String str, ArraySet<String> arraySet, DumpState dumpState) {
        LegacyPermissionSettings.dumpPermissions(printWriter, str, arraySet, this.mPermissionDataProvider.getLegacyPermissions(), this.mPermissionDataProvider.getAllAppOpPermissionPackages(), true, dumpState);
    }

    public void dumpSharedUsersLPr(PrintWriter printWriter, String str, ArraySet<String> arraySet, DumpState dumpState, boolean z) {
        boolean z2;
        boolean z3 = false;
        for (SharedUserSetting sharedUserSetting : this.mSharedUsers.values()) {
            if (str == null || sharedUserSetting == dumpState.getSharedUser()) {
                LegacyPermissionState legacyPermissionState = this.mPermissionDataProvider.getLegacyPermissionState(sharedUserSetting.mAppId);
                if (arraySet == null || legacyPermissionState.hasPermissionState(arraySet)) {
                    if (!z) {
                        if (z3) {
                            z2 = z3;
                        } else {
                            if (dumpState.onTitlePrinted()) {
                                printWriter.println();
                            }
                            printWriter.println("Shared users:");
                            z2 = true;
                        }
                        printWriter.print("  SharedUser [");
                        printWriter.print(sharedUserSetting.name);
                        printWriter.print("] (");
                        printWriter.print(Integer.toHexString(System.identityHashCode(sharedUserSetting)));
                        printWriter.println("):");
                        printWriter.print("    ");
                        printWriter.print("appId=");
                        printWriter.println(sharedUserSetting.mAppId);
                        printWriter.print("    ");
                        printWriter.println("Packages");
                        ArraySet<? extends PackageStateInternal> packageStates = sharedUserSetting.getPackageStates();
                        int size = packageStates.size();
                        for (int i = 0; i < size; i++) {
                            PackageStateInternal valueAt = packageStates.valueAt(i);
                            if (valueAt != null) {
                                printWriter.print("      ");
                                printWriter.println(valueAt);
                            } else {
                                printWriter.print("      ");
                                printWriter.println("NULL?!");
                            }
                        }
                        if (!dumpState.isOptionEnabled(4)) {
                            List<UserInfo> allUsers = getAllUsers(UserManagerService.getInstance());
                            dumpInstallPermissionsLPr(printWriter, "    ", arraySet, legacyPermissionState, allUsers);
                            for (UserInfo userInfo : allUsers) {
                                int i2 = userInfo.id;
                                int[] gidsForUid = this.mPermissionDataProvider.getGidsForUid(UserHandle.getUid(i2, sharedUserSetting.mAppId));
                                Collection<LegacyPermissionState.PermissionState> permissionStates = legacyPermissionState.getPermissionStates(i2);
                                if (!ArrayUtils.isEmpty(gidsForUid) || !permissionStates.isEmpty()) {
                                    printWriter.print("    ");
                                    printWriter.print("User ");
                                    printWriter.print(i2);
                                    printWriter.println(": ");
                                    dumpGidsLPr(printWriter, "      ", gidsForUid);
                                    dumpRuntimePermissionsLPr(printWriter, "      ", arraySet, permissionStates, str != null);
                                }
                            }
                        }
                        z3 = z2;
                    } else {
                        printWriter.print("suid,");
                        printWriter.print(sharedUserSetting.mAppId);
                        printWriter.print(",");
                        printWriter.println(sharedUserSetting.name);
                    }
                }
            }
        }
    }

    public void dumpSharedUsersProto(ProtoOutputStream protoOutputStream) {
        int size = this.mSharedUsers.size();
        for (int i = 0; i < size; i++) {
            this.mSharedUsers.valueAt(i).dumpDebug(protoOutputStream, 2246267895814L);
        }
    }

    public void dumpReadMessages(PrintWriter printWriter, DumpState dumpState) {
        printWriter.println("Settings parse messages:");
        printWriter.print(this.mReadMessages.toString());
    }

    public static void dumpSplitNames(PrintWriter printWriter, AndroidPackage androidPackage) {
        if (androidPackage == null) {
            printWriter.print("unknown");
            return;
        }
        printWriter.print("[");
        printWriter.print("base");
        if (androidPackage.getBaseRevisionCode() != 0) {
            printWriter.print(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
            printWriter.print(androidPackage.getBaseRevisionCode());
        }
        String[] splitNames = androidPackage.getSplitNames();
        int[] splitRevisionCodes = androidPackage.getSplitRevisionCodes();
        for (int i = 0; i < splitNames.length; i++) {
            printWriter.print(", ");
            printWriter.print(splitNames[i]);
            if (splitRevisionCodes[i] != 0) {
                printWriter.print(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                printWriter.print(splitRevisionCodes[i]);
            }
        }
        printWriter.print("]");
    }

    public void dumpGidsLPr(PrintWriter printWriter, String str, int[] iArr) {
        if (ArrayUtils.isEmpty(iArr)) {
            return;
        }
        printWriter.print(str);
        printWriter.print("gids=");
        printWriter.println(PackageManagerServiceUtils.arrayToString(iArr));
    }

    public void dumpRuntimePermissionsLPr(PrintWriter printWriter, String str, ArraySet<String> arraySet, Collection<LegacyPermissionState.PermissionState> collection, boolean z) {
        boolean z2;
        Iterator<LegacyPermissionState.PermissionState> it = collection.iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().isRuntime()) {
                    z2 = true;
                    break;
                }
            } else {
                z2 = false;
                break;
            }
        }
        if (z2 || z) {
            printWriter.print(str);
            printWriter.println("runtime permissions:");
            for (LegacyPermissionState.PermissionState permissionState : collection) {
                if (permissionState.isRuntime() && (arraySet == null || arraySet.contains(permissionState.getName()))) {
                    printWriter.print(str);
                    printWriter.print("  ");
                    printWriter.print(permissionState.getName());
                    printWriter.print(": granted=");
                    printWriter.print(permissionState.isGranted());
                    printWriter.println(permissionFlagsToString(", flags=", permissionState.getFlags()));
                }
            }
        }
    }

    public static String permissionFlagsToString(String str, int i) {
        StringBuilder sb = null;
        while (i != 0) {
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(str);
                sb.append("[ ");
            }
            int numberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= ~numberOfTrailingZeros;
            sb.append(PackageManager.permissionFlagToString(numberOfTrailingZeros));
            if (i != 0) {
                sb.append('|');
            }
        }
        if (sb != null) {
            sb.append(']');
            return sb.toString();
        }
        return "";
    }

    public void dumpInstallPermissionsLPr(PrintWriter printWriter, String str, ArraySet<String> arraySet, LegacyPermissionState legacyPermissionState, List<UserInfo> list) {
        LegacyPermissionState.PermissionState permissionState;
        ArraySet arraySet2 = new ArraySet();
        for (UserInfo userInfo : list) {
            for (LegacyPermissionState.PermissionState permissionState2 : legacyPermissionState.getPermissionStates(userInfo.id)) {
                if (!permissionState2.isRuntime()) {
                    String name = permissionState2.getName();
                    if (arraySet == null || arraySet.contains(name)) {
                        arraySet2.add(name);
                    }
                }
            }
        }
        Iterator it = arraySet2.iterator();
        boolean z = false;
        while (it.hasNext()) {
            String str2 = (String) it.next();
            LegacyPermissionState.PermissionState permissionState3 = legacyPermissionState.getPermissionState(str2, 0);
            for (UserInfo userInfo2 : list) {
                int i = userInfo2.id;
                if (i == 0) {
                    permissionState = permissionState3;
                } else {
                    permissionState = legacyPermissionState.getPermissionState(str2, i);
                    if (Objects.equals(permissionState, permissionState3)) {
                    }
                }
                boolean z2 = true;
                if (!z) {
                    printWriter.print(str);
                    printWriter.println("install permissions:");
                    z = true;
                }
                printWriter.print(str);
                printWriter.print("  ");
                printWriter.print(str2);
                printWriter.print(": granted=");
                printWriter.print((permissionState == null || !permissionState.isGranted()) ? false : false);
                printWriter.print(permissionFlagsToString(", flags=", permissionState != null ? permissionState.getFlags() : 0));
                if (i == 0) {
                    printWriter.println();
                } else {
                    printWriter.print(", userId=");
                    printWriter.println(i);
                }
            }
        }
    }

    public void dumpComponents(PrintWriter printWriter, String str, PackageSetting packageSetting) {
        dumpComponents(printWriter, str, "activities:", packageSetting.getPkg().getActivities());
        dumpComponents(printWriter, str, "services:", packageSetting.getPkg().getServices());
        dumpComponents(printWriter, str, "receivers:", packageSetting.getPkg().getReceivers());
        dumpComponents(printWriter, str, "providers:", packageSetting.getPkg().getProviders());
        dumpComponents(printWriter, str, "instrumentations:", packageSetting.getPkg().getInstrumentations());
    }

    public void dumpComponents(PrintWriter printWriter, String str, String str2, List<? extends ParsedComponent> list) {
        int size = CollectionUtils.size(list);
        if (size == 0) {
            return;
        }
        printWriter.print(str);
        printWriter.println(str2);
        for (int i = 0; i < size; i++) {
            printWriter.print(str);
            printWriter.print("  ");
            printWriter.println(list.get(i).getComponentName().flattenToShortString());
        }
    }

    public void writePermissionStateForUserLPr(int i, boolean z) {
        if (z) {
            this.mRuntimePermissionsPersistence.writeStateForUser(i, this.mPermissionDataProvider, this.mPackages, this.mSharedUsers, null, this.mLock, true);
        } else {
            this.mRuntimePermissionsPersistence.writeStateForUserAsync(i);
        }
    }

    /* renamed from: com.android.server.pm.Settings$KeySetToValueMap */
    /* loaded from: classes2.dex */
    public static class KeySetToValueMap<K, V> implements Map<K, V> {
        public final Set<K> mKeySet;
        public final V mValue;

        public KeySetToValueMap(Set<K> set, V v) {
            this.mKeySet = set;
            this.mValue = v;
        }

        @Override // java.util.Map
        public int size() {
            return this.mKeySet.size();
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            return this.mKeySet.isEmpty();
        }

        @Override // java.util.Map
        public boolean containsKey(Object obj) {
            return this.mKeySet.contains(obj);
        }

        @Override // java.util.Map
        public boolean containsValue(Object obj) {
            return this.mValue == obj;
        }

        @Override // java.util.Map
        public V get(Object obj) {
            return this.mValue;
        }

        @Override // java.util.Map
        public V put(K k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public V remove(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> map) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            return this.mKeySet;
        }

        @Override // java.util.Map
        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    /* renamed from: com.android.server.pm.Settings$RuntimePermissionPersistence */
    /* loaded from: classes2.dex */
    public static final class RuntimePermissionPersistence {
        public static final Random sRandom = new Random();
        public String mExtendedFingerprint;
        public final Consumer<Integer> mInvokeWriteUserStateAsyncCallback;
        @GuardedBy({"mPersistenceLock"})
        public final RuntimePermissionsPersistence mPersistence;
        public final Object mPersistenceLock = new Object();
        public final Handler mAsyncHandler = new MyHandler();
        public final Handler mPersistenceHandler = new PersistenceHandler();
        public final Object mLock = new Object();
        @GuardedBy({"mLock"})
        public final SparseBooleanArray mWriteScheduled = new SparseBooleanArray();
        @GuardedBy({"mLock"})
        public final SparseLongArray mLastNotWrittenMutationTimesMillis = new SparseLongArray();
        @GuardedBy({"mLock"})
        public AtomicBoolean mIsLegacyPermissionStateStale = new AtomicBoolean(false);
        @GuardedBy({"mLock"})
        public final SparseIntArray mVersions = new SparseIntArray();
        @GuardedBy({"mLock"})
        public final SparseArray<String> mFingerprints = new SparseArray<>();
        @GuardedBy({"mLock"})
        public final SparseBooleanArray mPermissionUpgradeNeeded = new SparseBooleanArray();
        @GuardedBy({"mLock"})
        public final SparseArray<RuntimePermissionsState> mPendingStatesToWrite = new SparseArray<>();

        public RuntimePermissionPersistence(RuntimePermissionsPersistence runtimePermissionsPersistence, Consumer<Integer> consumer) {
            this.mPersistence = runtimePermissionsPersistence;
            this.mInvokeWriteUserStateAsyncCallback = consumer;
        }

        public int getVersion(int i) {
            int i2;
            synchronized (this.mLock) {
                i2 = this.mVersions.get(i, 0);
            }
            return i2;
        }

        public void setVersion(int i, int i2) {
            synchronized (this.mLock) {
                this.mVersions.put(i2, i);
                writeStateForUserAsync(i2);
            }
        }

        public boolean isPermissionUpgradeNeeded(int i) {
            boolean z;
            synchronized (this.mLock) {
                z = this.mPermissionUpgradeNeeded.get(i, true);
            }
            return z;
        }

        public void updateRuntimePermissionsFingerprint(int i) {
            synchronized (this.mLock) {
                String str = this.mExtendedFingerprint;
                if (str == null) {
                    throw new RuntimeException("The version of the permission controller hasn't been set before trying to update the fingerprint.");
                }
                this.mFingerprints.put(i, str);
                this.mPermissionUpgradeNeeded.put(i, false);
                writeStateForUserAsync(i);
            }
        }

        public void setPermissionControllerVersion(long j) {
            synchronized (this.mLock) {
                int size = this.mFingerprints.size();
                this.mExtendedFingerprint = getExtendedFingerprint(j);
                for (int i = 0; i < size; i++) {
                    this.mPermissionUpgradeNeeded.put(this.mFingerprints.keyAt(i), !TextUtils.equals(this.mExtendedFingerprint, this.mFingerprints.valueAt(i)));
                }
            }
        }

        public final String getExtendedFingerprint(long j) {
            return PackagePartitions.FINGERPRINT + "?pc_version=" + j;
        }

        public static long uniformRandom(double d, double d2) {
            return (long) ((sRandom.nextDouble() * (d2 - d)) + d);
        }

        public static long nextWritePermissionDelayMillis() {
            return uniformRandom(-300.0d, 300.0d) + 1000;
        }

        public void writeStateForUserAsync(int i) {
            this.mIsLegacyPermissionStateStale.set(true);
            synchronized (this.mLock) {
                long uptimeMillis = SystemClock.uptimeMillis();
                long nextWritePermissionDelayMillis = nextWritePermissionDelayMillis();
                if (this.mWriteScheduled.get(i)) {
                    this.mAsyncHandler.removeMessages(i);
                    long j = this.mLastNotWrittenMutationTimesMillis.get(i);
                    if (uptimeMillis - j >= 2000) {
                        this.mAsyncHandler.obtainMessage(i).sendToTarget();
                        return;
                    }
                    long min = Math.min(nextWritePermissionDelayMillis, Math.max((j + 2000) - uptimeMillis, 0L));
                    this.mAsyncHandler.sendMessageDelayed(this.mAsyncHandler.obtainMessage(i), min);
                } else {
                    this.mLastNotWrittenMutationTimesMillis.put(i, uptimeMillis);
                    this.mAsyncHandler.sendMessageDelayed(this.mAsyncHandler.obtainMessage(i), nextWritePermissionDelayMillis);
                    this.mWriteScheduled.put(i, true);
                }
            }
        }

        public void writeStateForUser(final int i, final LegacyPermissionDataProvider legacyPermissionDataProvider, final WatchedArrayMap<String, ? extends PackageStateInternal> watchedArrayMap, final WatchedArrayMap<String, SharedUserSetting> watchedArrayMap2, final Handler handler, final Object obj, final boolean z) {
            synchronized (this.mLock) {
                this.mAsyncHandler.removeMessages(i);
                this.mWriteScheduled.delete(i);
            }
            Runnable runnable = new Runnable() { // from class: com.android.server.pm.Settings$RuntimePermissionPersistence$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Settings.RuntimePermissionPersistence.this.lambda$writeStateForUser$0(obj, z, legacyPermissionDataProvider, watchedArrayMap, i, watchedArrayMap2, handler);
                }
            };
            if (handler != null) {
                handler.post(runnable);
            } else {
                runnable.run();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$writeStateForUser$0(Object obj, boolean z, LegacyPermissionDataProvider legacyPermissionDataProvider, WatchedArrayMap watchedArrayMap, int i, WatchedArrayMap watchedArrayMap2, Handler handler) {
            boolean andSet = this.mIsLegacyPermissionStateStale.getAndSet(false);
            ArrayMap arrayMap = new ArrayMap();
            ArrayMap arrayMap2 = new ArrayMap();
            synchronized (obj) {
                if (z || andSet) {
                    legacyPermissionDataProvider.writeLegacyPermissionStateTEMP();
                }
                int size = watchedArrayMap.size();
                for (int i2 = 0; i2 < size; i2++) {
                    String str = (String) watchedArrayMap.keyAt(i2);
                    PackageStateInternal packageStateInternal = (PackageStateInternal) watchedArrayMap.valueAt(i2);
                    if (!packageStateInternal.hasSharedUser()) {
                        List<RuntimePermissionsState.PermissionState> permissionsFromPermissionsState = getPermissionsFromPermissionsState(packageStateInternal.getLegacyPermissionState(), i);
                        if (!permissionsFromPermissionsState.isEmpty() || packageStateInternal.isInstallPermissionsFixed()) {
                            arrayMap.put(str, permissionsFromPermissionsState);
                        }
                    }
                }
                int size2 = watchedArrayMap2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    arrayMap2.put((String) watchedArrayMap2.keyAt(i3), getPermissionsFromPermissionsState(((SharedUserSetting) watchedArrayMap2.valueAt(i3)).getLegacyPermissionState(), i));
                }
            }
            synchronized (this.mLock) {
                this.mPendingStatesToWrite.put(i, new RuntimePermissionsState(this.mVersions.get(i, 0), this.mFingerprints.get(i), arrayMap, arrayMap2));
            }
            if (handler != null) {
                this.mPersistenceHandler.obtainMessage(i).sendToTarget();
            } else {
                writePendingStates();
            }
        }

        public final void writePendingStates() {
            int keyAt;
            RuntimePermissionsState valueAt;
            while (true) {
                synchronized (this.mLock) {
                    if (this.mPendingStatesToWrite.size() == 0) {
                        return;
                    }
                    keyAt = this.mPendingStatesToWrite.keyAt(0);
                    valueAt = this.mPendingStatesToWrite.valueAt(0);
                    this.mPendingStatesToWrite.removeAt(0);
                }
                synchronized (this.mPersistenceLock) {
                    this.mPersistence.writeForUser(valueAt, UserHandle.of(keyAt));
                }
            }
        }

        public final List<RuntimePermissionsState.PermissionState> getPermissionsFromPermissionsState(LegacyPermissionState legacyPermissionState, int i) {
            Collection<LegacyPermissionState.PermissionState> permissionStates = legacyPermissionState.getPermissionStates(i);
            ArrayList arrayList = new ArrayList();
            for (LegacyPermissionState.PermissionState permissionState : permissionStates) {
                arrayList.add(new RuntimePermissionsState.PermissionState(permissionState.getName(), permissionState.isGranted(), permissionState.getFlags()));
            }
            return arrayList;
        }

        public final void onUserRemoved(int i) {
            synchronized (this.mLock) {
                this.mAsyncHandler.removeMessages(i);
                this.mPermissionUpgradeNeeded.delete(i);
                this.mVersions.delete(i);
                this.mFingerprints.remove(i);
            }
        }

        public void readStateForUserSync(int i, VersionInfo versionInfo, WatchedArrayMap<String, PackageSetting> watchedArrayMap, WatchedArrayMap<String, SharedUserSetting> watchedArrayMap2, File file) {
            RuntimePermissionsState readForUser;
            synchronized (this.mPersistenceLock) {
                readForUser = this.mPersistence.readForUser(UserHandle.of(i));
            }
            if (readForUser == null) {
                readLegacyStateForUserSync(i, file, watchedArrayMap, watchedArrayMap2);
                writeStateForUserAsync(i);
                return;
            }
            synchronized (this.mLock) {
                int version = readForUser.getVersion();
                if (version == -1) {
                    version = -1;
                }
                this.mVersions.put(i, version);
                this.mFingerprints.put(i, readForUser.getFingerprint());
                char c = 0;
                boolean z = true;
                boolean z2 = versionInfo.sdkVersion < 30;
                Map packagePermissions = readForUser.getPackagePermissions();
                int size = watchedArrayMap.size();
                int i2 = 0;
                while (i2 < size) {
                    String keyAt = watchedArrayMap.keyAt(i2);
                    PackageSetting valueAt = watchedArrayMap.valueAt(i2);
                    List<RuntimePermissionsState.PermissionState> list = (List) packagePermissions.get(keyAt);
                    if (list != null) {
                        readPermissionsState(list, valueAt.getLegacyPermissionState(), i);
                        valueAt.setInstallPermissionsFixed(z);
                    } else if (!valueAt.hasSharedUser() && !z2) {
                        Object[] objArr = new Object[2];
                        objArr[c] = keyAt;
                        objArr[1] = Integer.valueOf(i);
                        Slogf.m12w("PackageSettings", "Missing permission state for package %s on user %d", objArr);
                        valueAt.getLegacyPermissionState().setMissing(true, i);
                    }
                    i2++;
                    c = 0;
                    z = true;
                }
                Map sharedUserPermissions = readForUser.getSharedUserPermissions();
                int size2 = watchedArrayMap2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    String keyAt2 = watchedArrayMap2.keyAt(i3);
                    SharedUserSetting valueAt2 = watchedArrayMap2.valueAt(i3);
                    List<RuntimePermissionsState.PermissionState> list2 = (List) sharedUserPermissions.get(keyAt2);
                    if (list2 != null) {
                        readPermissionsState(list2, valueAt2.getLegacyPermissionState(), i);
                    } else if (!z2) {
                        Slog.w("PackageSettings", "Missing permission state for shared user: " + keyAt2);
                        valueAt2.getLegacyPermissionState().setMissing(true, i);
                    }
                }
            }
        }

        public final void readPermissionsState(List<RuntimePermissionsState.PermissionState> list, LegacyPermissionState legacyPermissionState, int i) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                RuntimePermissionsState.PermissionState permissionState = list.get(i2);
                legacyPermissionState.putPermissionState(new LegacyPermissionState.PermissionState(permissionState.getName(), true, permissionState.isGranted(), permissionState.getFlags()), i);
            }
        }

        public final void readLegacyStateForUserSync(int i, File file, WatchedArrayMap<String, ? extends PackageStateInternal> watchedArrayMap, WatchedArrayMap<String, SharedUserSetting> watchedArrayMap2) {
            synchronized (this.mLock) {
                if (file.exists()) {
                    try {
                        FileInputStream openRead = new AtomicFile(file).openRead();
                        try {
                            parseLegacyRuntimePermissions(Xml.resolvePullParser(openRead), i, watchedArrayMap, watchedArrayMap2);
                            IoUtils.closeQuietly(openRead);
                        } catch (IOException | XmlPullParserException e) {
                            throw new IllegalStateException("Failed parsing permissions file: " + file, e);
                        }
                    } catch (FileNotFoundException unused) {
                        Slog.i("PackageManager", "No permissions state");
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:57:0x00c5 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:67:0x005c A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void parseLegacyRuntimePermissions(TypedXmlPullParser typedXmlPullParser, int i, WatchedArrayMap<String, ? extends PackageStateInternal> watchedArrayMap, WatchedArrayMap<String, SharedUserSetting> watchedArrayMap2) throws IOException, XmlPullParserException {
            boolean z;
            synchronized (this.mLock) {
                int depth = typedXmlPullParser.getDepth();
                while (true) {
                    int next = typedXmlPullParser.next();
                    if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                        break;
                    } else if (next != 3 && next != 4) {
                        String name = typedXmlPullParser.getName();
                        int hashCode = name.hashCode();
                        if (hashCode == 111052) {
                            if (name.equals("pkg")) {
                                z = true;
                                if (z) {
                                }
                            }
                            z = true;
                            if (z) {
                            }
                        } else if (hashCode != 160289295) {
                            if (hashCode == 485578803 && name.equals("shared-user")) {
                                z = true;
                                if (z) {
                                    this.mVersions.put(i, typedXmlPullParser.getAttributeInt((String) null, "version", -1));
                                    this.mFingerprints.put(i, typedXmlPullParser.getAttributeValue((String) null, "fingerprint"));
                                } else if (z) {
                                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
                                    PackageStateInternal packageStateInternal = watchedArrayMap.get(attributeValue);
                                    if (packageStateInternal == null) {
                                        Slog.w("PackageManager", "Unknown package:" + attributeValue);
                                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                                    } else {
                                        parseLegacyPermissionsLPr(typedXmlPullParser, packageStateInternal.getLegacyPermissionState(), i);
                                    }
                                } else if (z) {
                                    String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "name");
                                    SharedUserSetting sharedUserSetting = watchedArrayMap2.get(attributeValue2);
                                    if (sharedUserSetting == null) {
                                        Slog.w("PackageManager", "Unknown shared user:" + attributeValue2);
                                        XmlUtils.skipCurrentTag(typedXmlPullParser);
                                    } else {
                                        parseLegacyPermissionsLPr(typedXmlPullParser, sharedUserSetting.getLegacyPermissionState(), i);
                                    }
                                }
                            }
                            z = true;
                            if (z) {
                            }
                        } else {
                            if (name.equals("runtime-permissions")) {
                                z = false;
                                if (z) {
                                }
                            }
                            z = true;
                            if (z) {
                            }
                        }
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:36:0x003a A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:38:0x0039 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void parseLegacyPermissionsLPr(TypedXmlPullParser typedXmlPullParser, LegacyPermissionState legacyPermissionState, int i) throws IOException, XmlPullParserException {
            char c;
            synchronized (this.mLock) {
                int depth = typedXmlPullParser.getDepth();
                while (true) {
                    int next = typedXmlPullParser.next();
                    if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                        break;
                    } else if (next != 3 && next != 4) {
                        String name = typedXmlPullParser.getName();
                        if (name.hashCode() == 3242771 && name.equals("item")) {
                            c = 0;
                            if (c != 0) {
                                legacyPermissionState.putPermissionState(new LegacyPermissionState.PermissionState(typedXmlPullParser.getAttributeValue((String) null, "name"), true, typedXmlPullParser.getAttributeBoolean((String) null, "granted", true), typedXmlPullParser.getAttributeIntHex((String) null, "flags", 0)), i);
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    }
                }
            }
        }

        /* renamed from: com.android.server.pm.Settings$RuntimePermissionPersistence$MyHandler */
        /* loaded from: classes2.dex */
        public final class MyHandler extends Handler {
            public MyHandler() {
                super(BackgroundThread.getHandler().getLooper());
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                Runnable runnable = (Runnable) message.obj;
                RuntimePermissionPersistence.this.mInvokeWriteUserStateAsyncCallback.accept(Integer.valueOf(i));
                if (runnable != null) {
                    runnable.run();
                }
            }
        }

        /* renamed from: com.android.server.pm.Settings$RuntimePermissionPersistence$PersistenceHandler */
        /* loaded from: classes2.dex */
        public final class PersistenceHandler extends Handler {
            public PersistenceHandler() {
                super(BackgroundThread.getHandler().getLooper());
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                RuntimePermissionPersistence.this.writePendingStates();
            }
        }
    }

    public PersistentPreferredIntentResolver getPersistentPreferredActivities(int i) {
        return this.mPersistentPreferredActivities.get(i);
    }

    public PreferredIntentResolver getPreferredActivities(int i) {
        return this.mPreferredActivities.get(i);
    }

    public CrossProfileIntentResolver getCrossProfileIntentResolver(int i) {
        return this.mCrossProfileIntentResolvers.get(i);
    }

    public void clearPackagePreferredActivities(String str, SparseBooleanArray sparseBooleanArray, int i) {
        ArrayList arrayList = null;
        boolean z = false;
        for (int i2 = 0; i2 < this.mPreferredActivities.size(); i2++) {
            int keyAt = this.mPreferredActivities.keyAt(i2);
            PreferredIntentResolver valueAt = this.mPreferredActivities.valueAt(i2);
            if (i == -1 || i == keyAt) {
                Iterator<F> filterIterator = valueAt.filterIterator();
                while (filterIterator.hasNext()) {
                    PreferredActivity preferredActivity = (PreferredActivity) filterIterator.next();
                    if (str == null || (preferredActivity.mPref.mComponent.getPackageName().equals(str) && preferredActivity.mPref.mAlways)) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(preferredActivity);
                    }
                }
                if (arrayList != null) {
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        valueAt.removeFilter((PreferredIntentResolver) ((PreferredActivity) arrayList.get(i3)));
                    }
                    z = true;
                    sparseBooleanArray.put(keyAt, true);
                }
            }
        }
        if (z) {
            onChanged();
        }
    }

    public boolean clearPackagePersistentPreferredActivities(String str, int i) {
        ArrayList arrayList = null;
        boolean z = false;
        for (int i2 = 0; i2 < this.mPersistentPreferredActivities.size(); i2++) {
            int keyAt = this.mPersistentPreferredActivities.keyAt(i2);
            PersistentPreferredIntentResolver valueAt = this.mPersistentPreferredActivities.valueAt(i2);
            if (i == keyAt) {
                Iterator<F> filterIterator = valueAt.filterIterator();
                while (filterIterator.hasNext()) {
                    PersistentPreferredActivity persistentPreferredActivity = (PersistentPreferredActivity) filterIterator.next();
                    if (persistentPreferredActivity.mComponent.getPackageName().equals(str)) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(persistentPreferredActivity);
                    }
                }
                if (arrayList != null) {
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        valueAt.removeFilter((PersistentPreferredIntentResolver) ((PersistentPreferredActivity) arrayList.get(i3)));
                    }
                    z = true;
                }
            }
        }
        if (z) {
            onChanged();
        }
        return z;
    }

    public boolean clearPersistentPreferredActivity(IntentFilter intentFilter, int i) {
        boolean z;
        PersistentPreferredIntentResolver persistentPreferredIntentResolver = this.mPersistentPreferredActivities.get(i);
        Iterator<F> filterIterator = persistentPreferredIntentResolver.filterIterator();
        while (true) {
            if (!filterIterator.hasNext()) {
                z = false;
                break;
            }
            PersistentPreferredActivity persistentPreferredActivity = (PersistentPreferredActivity) filterIterator.next();
            if (IntentFilter.filterEquals(persistentPreferredActivity.getIntentFilter(), intentFilter)) {
                persistentPreferredIntentResolver.removeFilter((PersistentPreferredIntentResolver) persistentPreferredActivity);
                z = true;
                break;
            }
        }
        if (z) {
            onChanged();
        }
        return z;
    }

    public ArrayList<Integer> systemReady(ComponentResolver componentResolver) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < this.mPreferredActivities.size(); i++) {
            PreferredIntentResolver valueAt = this.mPreferredActivities.valueAt(i);
            arrayList2.clear();
            for (F f : valueAt.filterSet()) {
                if (!componentResolver.isActivityDefined(f.mPref.mComponent)) {
                    arrayList2.add(f);
                }
            }
            if (arrayList2.size() > 0) {
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    PreferredActivity preferredActivity = (PreferredActivity) arrayList2.get(i2);
                    Slog.w("PackageSettings", "Removing dangling preferred activity: " + preferredActivity.mPref.mComponent);
                    valueAt.removeFilter((PreferredIntentResolver) preferredActivity);
                }
                arrayList.add(Integer.valueOf(this.mPreferredActivities.keyAt(i)));
            }
        }
        onChanged();
        return arrayList;
    }

    public void dumpPreferred(PrintWriter printWriter, DumpState dumpState, String str) {
        for (int i = 0; i < this.mPreferredActivities.size(); i++) {
            PreferredIntentResolver valueAt = this.mPreferredActivities.valueAt(i);
            int keyAt = this.mPreferredActivities.keyAt(i);
            if (valueAt.dump(printWriter, dumpState.getTitlePrinted() ? "\nPreferred Activities User " + keyAt + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR : "Preferred Activities User " + keyAt + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR, "  ", str, true, false)) {
                dumpState.setTitlePrinted(true);
            }
        }
    }

    public boolean isInstallerPackage(String str) {
        return this.mInstallerPackages.contains(str);
    }
}
