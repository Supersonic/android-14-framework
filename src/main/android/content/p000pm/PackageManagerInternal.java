package android.content.p000pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.IPackageLoadingProgressCallback;
import android.content.pm.SigningDetails;
import android.content.pm.overlay.OverlayPaths;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageList;
import com.android.server.p011pm.PackageSetting;
import com.android.server.p011pm.dex.DynamicCodeLogger;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.SharedUserApi;
import com.android.server.p011pm.pkg.mutate.PackageStateMutator;
import com.android.server.p011pm.snapshot.PackageDataSnapshot;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* renamed from: android.content.pm.PackageManagerInternal */
/* loaded from: classes.dex */
public abstract class PackageManagerInternal {

    /* renamed from: android.content.pm.PackageManagerInternal$ExternalSourcesPolicy */
    /* loaded from: classes.dex */
    public interface ExternalSourcesPolicy {
        int getPackageTrustedToInstallApps(String str, int i);
    }

    /* renamed from: android.content.pm.PackageManagerInternal$PackageListObserver */
    /* loaded from: classes.dex */
    public interface PackageListObserver {
        default void onPackageAdded(String str, int i) {
        }

        default void onPackageChanged(String str, int i) {
        }

        default void onPackageRemoved(String str, int i) {
        }
    }

    public abstract void addIsolatedUid(int i, int i2);

    public abstract boolean canAccessComponent(int i, ComponentName componentName, int i2);

    public abstract boolean canAccessInstantApps(int i, int i2);

    public abstract boolean canQueryPackage(int i, String str);

    public abstract int checkUidSignaturesForAllUsers(int i, int i2);

    public abstract void clearBlockUninstallForUser(int i);

    public abstract PackageStateMutator.Result commitPackageStateMutation(PackageStateMutator.InitialState initialState, Consumer<PackageStateMutator> consumer);

    public abstract long deleteOatArtifactsOfPackage(String str);

    public abstract boolean filterAppAccess(int i, int i2);

    public abstract boolean filterAppAccess(AndroidPackage androidPackage, int i, int i2);

    public abstract boolean filterAppAccess(String str, int i, int i2, boolean z);

    public abstract void finishPackageInstall(int i, boolean z);

    public abstract void flushPackageRestrictions(int i);

    public abstract void forEachInstalledPackage(Consumer<AndroidPackage> consumer, int i);

    public abstract void forEachPackage(Consumer<AndroidPackage> consumer);

    public abstract void forEachPackageSetting(Consumer<PackageSetting> consumer);

    public abstract void forEachPackageState(Consumer<PackageStateInternal> consumer);

    public abstract void freeAllAppCacheAboveQuota(String str) throws IOException;

    public abstract void freeStorage(String str, long j, int i) throws IOException;

    public abstract ActivityInfo getActivityInfo(ComponentName componentName, long j, int i, int i2);

    public abstract AndroidPackage getAndroidPackage(String str);

    public abstract List<String> getApksInApex(String str);

    public abstract int getApplicationEnabledState(String str, int i);

    public abstract ApplicationInfo getApplicationInfo(String str, long j, int i, int i2);

    public abstract int getComponentEnabledSetting(ComponentName componentName, int i, int i2);

    public abstract ComponentName getDefaultHomeActivity(int i);

    public abstract ArraySet<String> getDisabledComponents(String str, int i);

    public abstract PackageStateInternal getDisabledSystemPackage(String str);

    public abstract String getDisabledSystemPackageName(String str);

    public abstract int getDistractingPackageRestrictions(String str, int i);

    public abstract DynamicCodeLogger getDynamicCodeLogger();

    public abstract ArraySet<String> getEnabledComponents(String str, int i);

    public abstract ComponentName getHomeActivitiesAsUser(List<ResolveInfo> list, int i);

    public abstract IncrementalStatesInfo getIncrementalStatesInfo(String str, int i, int i2);

    public abstract List<ApplicationInfo> getInstalledApplications(long j, int i, int i2);

    public abstract String getInstantAppPackageName(int i);

    public abstract String[] getKnownPackageNames(int i, int i2);

    public abstract String getNameForUid(int i);

    public abstract AndroidPackage getPackage(int i);

    public abstract AndroidPackage getPackage(String str);

    public abstract PackageInfo getPackageInfo(String str, long j, int i, int i2);

    public abstract PackageList getPackageList(PackageListObserver packageListObserver);

    public abstract PackageStateInternal getPackageStateInternal(String str);

    public abstract ArrayMap<String, ? extends PackageStateInternal> getPackageStates();

    public abstract int getPackageTargetSdkVersion(String str);

    public abstract int getPackageUid(String str, long j, int i);

    public abstract List<AndroidPackage> getPackagesForAppId(int i);

    public abstract int[] getPermissionGids(String str, int i);

    public abstract ArrayMap<String, ProcessInfo> getProcessesForUid(int i);

    public abstract String getSetupWizardPackageName();

    public abstract SharedUserApi getSharedUserApi(int i);

    public abstract ArraySet<PackageStateInternal> getSharedUserPackages(int i);

    public abstract String[] getSharedUserPackagesForPackage(String str, int i);

    public abstract SuspendDialogInfo getSuspendedDialogInfo(String str, String str2, int i);

    public abstract Bundle getSuspendedPackageLauncherExtras(String str, int i);

    public abstract String getSuspendingPackage(String str, int i);

    public abstract ComponentName getSystemUiServiceComponent();

    public abstract List<String> getTargetPackageNames(int i);

    public abstract int getUidTargetSdkVersion(int i);

    public abstract int[] getVisibilityAllowList(String str, int i);

    public abstract void grantImplicitAccess(int i, Intent intent, int i2, int i3, boolean z);

    public abstract void grantImplicitAccess(int i, Intent intent, int i2, int i3, boolean z, boolean z2);

    public abstract boolean hasInstantApplicationMetadata(String str, int i);

    public abstract boolean hasSignatureCapability(int i, int i2, @SigningDetails.CertCapabilities int i3);

    public abstract boolean isApexPackage(String str);

    public abstract boolean isCallerInstallerOfRecord(AndroidPackage androidPackage, int i);

    public abstract boolean isDataRestoreSafe(Signature signature, String str);

    public abstract boolean isDataRestoreSafe(byte[] bArr, String str);

    public abstract boolean isInstantApp(String str, int i);

    public abstract boolean isInstantAppInstallerComponent(ComponentName componentName);

    public abstract boolean isPackageDataProtected(int i, String str);

    public abstract boolean isPackageEphemeral(int i, String str);

    public abstract boolean isPackageFrozen(String str, int i, int i2);

    public abstract boolean isPackagePersistent(String str);

    public abstract boolean isPackageStateProtected(String str, int i);

    public abstract boolean isPackageSuspended(String str, int i);

    public abstract boolean isPermissionUpgradeNeeded(int i);

    public abstract boolean isPermissionsReviewRequired(String str, int i);

    public abstract boolean isPlatformSigned(String str);

    public abstract boolean isResolveActivityComponent(ComponentInfo componentInfo);

    public abstract boolean isSameApp(String str, int i, int i2);

    public abstract boolean isSuspendingAnyPackages(String str, int i);

    public abstract boolean isSystemPackage(String str);

    public abstract boolean isUidPrivileged(int i);

    @Deprecated
    public abstract void legacyDumpProfiles(String str, boolean z) throws Installer.LegacyDexoptDisabledException;

    @Deprecated
    public abstract void legacyForceDexOpt(String str) throws Installer.LegacyDexoptDisabledException;

    @Deprecated
    public abstract void legacyReconcileSecondaryDexFiles(String str) throws Installer.LegacyDexoptDisabledException;

    public abstract void migrateLegacyObbData();

    public abstract void notifyPackageUse(String str, int i);

    public abstract void onPackageProcessKilledForUninstall(String str);

    public abstract void pruneInstantApps();

    public abstract List<ResolveInfo> queryIntentActivities(Intent intent, String str, long j, int i, int i2);

    public abstract List<ResolveInfo> queryIntentReceivers(Intent intent, String str, long j, int i, int i2, boolean z);

    public abstract List<ResolveInfo> queryIntentServices(Intent intent, long j, int i, int i2);

    public abstract void reconcileAppsData(int i, int i2, boolean z);

    public abstract boolean registerInstalledLoadingProgressCallback(String str, InstalledLoadingProgressCallback installedLoadingProgressCallback, int i);

    public abstract void removeAllDistractingPackageRestrictions(int i);

    public abstract void removeAllNonSystemPackageSuspensions(int i);

    public abstract void removeDistractingPackageRestrictions(String str, int i);

    public abstract void removeIsolatedUid(int i);

    public abstract String removeLegacyDefaultBrowserPackageName(int i);

    public abstract void removeNonSystemPackageSuspensions(String str, int i);

    public abstract void removePackageListObserver(PackageListObserver packageListObserver);

    public abstract void requestChecksums(String str, boolean z, int i, int i2, List list, IOnChecksumsReadyListener iOnChecksumsReadyListener, int i3, Executor executor, Handler handler);

    public abstract void requestInstantAppResolutionPhaseTwo(AuxiliaryResolveInfo auxiliaryResolveInfo, Intent intent, String str, String str2, String str3, boolean z, Bundle bundle, int i);

    public abstract ProviderInfo resolveContentProvider(String str, long j, int i, int i2);

    public abstract ResolveInfo resolveIntentExported(Intent intent, String str, long j, long j2, int i, boolean z, int i2, int i3);

    public abstract ResolveInfo resolveService(Intent intent, String str, long j, int i, int i2);

    public abstract void setDeviceAndProfileOwnerPackages(int i, String str, SparseArray<String> sparseArray);

    public abstract void setEnableRollbackCode(int i, int i2);

    public abstract void setEnabledOverlayPackages(int i, ArrayMap<String, OverlayPaths> arrayMap, Set<String> set, Set<String> set2);

    public abstract void setExternalSourcesPolicy(ExternalSourcesPolicy externalSourcesPolicy);

    public abstract void setIntegrityVerificationResult(int i, int i2);

    public abstract void setKeepUninstalledPackages(List<String> list);

    public abstract void setOwnerProtectedPackages(int i, List<String> list);

    public abstract void setPackageStoppedState(String str, boolean z, int i);

    public abstract String[] setPackagesSuspendedByAdmin(int i, String[] strArr, boolean z);

    public abstract void setPackagesSuspendedForQuietMode(int i, boolean z);

    public abstract void setVisibilityLogging(String str, boolean z);

    public abstract void shutdown();

    public abstract PackageDataSnapshot snapshot();

    public abstract void uninstallApex(String str, long j, int i, IntentSender intentSender, int i2);

    public abstract void unsuspendForSuspendingPackage(String str, int i);

    public abstract void updateRuntimePermissionsFingerprint(int i);

    public abstract boolean wasPackageEverLaunched(String str, int i);

    public abstract void writePermissionSettings(int[] iArr, boolean z);

    public abstract void writeSettings(boolean z);

    public PackageList getPackageList() {
        return getPackageList(null);
    }

    public boolean filterAppAccess(String str, int i, int i2) {
        return filterAppAccess(str, i, i2, true);
    }

    /* renamed from: android.content.pm.PackageManagerInternal$InstalledLoadingProgressCallback */
    /* loaded from: classes.dex */
    public static abstract class InstalledLoadingProgressCallback {
        public final LoadingProgressCallbackBinder mBinder = new LoadingProgressCallbackBinder();
        public final Executor mExecutor;

        public abstract void onLoadingProgressChanged(float f);

        public InstalledLoadingProgressCallback(Handler handler) {
            this.mExecutor = new HandlerExecutor(handler == null ? new Handler(Looper.getMainLooper()) : handler);
        }

        public final IBinder getBinder() {
            return this.mBinder;
        }

        /* renamed from: android.content.pm.PackageManagerInternal$InstalledLoadingProgressCallback$LoadingProgressCallbackBinder */
        /* loaded from: classes.dex */
        public class LoadingProgressCallbackBinder extends IPackageLoadingProgressCallback.Stub {
            public LoadingProgressCallbackBinder() {
            }

            public void onPackageLoadingProgressChanged(float f) {
                InstalledLoadingProgressCallback.this.mExecutor.execute(PooledLambda.obtainRunnable(new BiConsumer() { // from class: android.content.pm.PackageManagerInternal$InstalledLoadingProgressCallback$LoadingProgressCallbackBinder$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((PackageManagerInternal.InstalledLoadingProgressCallback) obj).onLoadingProgressChanged(((Float) obj2).floatValue());
                    }
                }, InstalledLoadingProgressCallback.this, Float.valueOf(f)).recycleOnUse());
            }
        }
    }
}
