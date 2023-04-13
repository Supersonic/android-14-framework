package com.android.server.p011pm.dex;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.dex.ArtManager;
import android.content.pm.dex.ArtManagerInternal;
import android.content.pm.dex.DexMetadataHelper;
import android.content.pm.dex.IArtManager;
import android.content.pm.dex.ISnapshotRuntimeProfileCallback;
import android.content.pm.dex.PackageOptimizationInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.system.Os;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.art.ArtManagerLocal;
import com.android.server.p011pm.DexOptHelper;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.PackageManagerServiceCompilerMapping;
import com.android.server.p011pm.PackageManagerServiceUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import dalvik.system.DexFile;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.dex.ArtManagerService */
/* loaded from: classes2.dex */
public class ArtManagerService extends IArtManager.Stub {
    public static final boolean DEBUG = Log.isLoggable("ArtManagerService", 3);
    public final Context mContext;
    public final Handler mHandler = new Handler(BackgroundThread.getHandler().getLooper());
    public final Installer mInstaller;
    public IPackageManager mPackageManager;

    static {
        verifyTronLoggingConstants();
    }

    public ArtManagerService(Context context, Installer installer, Object obj) {
        this.mContext = context;
        this.mInstaller = installer;
        LocalServices.addService(ArtManagerInternal.class, new ArtManagerInternalImpl());
    }

    public final IPackageManager getPackageManager() {
        if (this.mPackageManager == null) {
            this.mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }
        return this.mPackageManager;
    }

    public final boolean checkAndroidPermissions(int i, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_RUNTIME_PROFILES", "ArtManagerService");
        int noteOp = ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).noteOp(43, i, str);
        if (noteOp != 0) {
            if (noteOp != 3) {
                return false;
            }
            this.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", "ArtManagerService");
        }
        return true;
    }

    public final boolean checkShellPermissions(int i, String str, int i2) {
        PackageInfo packageInfo;
        if (i2 != 2000) {
            return false;
        }
        if (RoSystemProperties.DEBUGGABLE) {
            return true;
        }
        if (i == 1) {
            return false;
        }
        try {
            packageInfo = getPackageManager().getPackageInfo(str, 0L, 0);
        } catch (RemoteException unused) {
            packageInfo = null;
        }
        return packageInfo != null && (packageInfo.applicationInfo.flags & 2) == 2;
    }

    public void snapshotRuntimeProfile(int i, String str, String str2, ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback, String str3) {
        int callingUid = Binder.getCallingUid();
        if (!checkShellPermissions(i, str, callingUid) && !checkAndroidPermissions(callingUid, str3)) {
            try {
                iSnapshotRuntimeProfileCallback.onError(2);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        Objects.requireNonNull(iSnapshotRuntimeProfileCallback);
        boolean z = i == 1;
        if (!z) {
            Preconditions.checkStringNotEmpty(str2);
            Preconditions.checkStringNotEmpty(str);
        }
        if (!isRuntimeProfilingEnabled(i, str3)) {
            throw new IllegalStateException("Runtime profiling is not enabled for " + i);
        }
        if (DEBUG) {
            Slog.d("ArtManagerService", "Requested snapshot for " + str + XmlUtils.STRING_ARRAY_SEPARATOR + str2);
        }
        if (z) {
            snapshotBootImageProfile(iSnapshotRuntimeProfileCallback);
        } else {
            snapshotAppProfile(str, str2, iSnapshotRuntimeProfileCallback);
        }
    }

    public final void snapshotAppProfile(String str, String str2, ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback) {
        PackageInfo packageInfo;
        String str3 = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(str, 0L, 0);
        } catch (RemoteException unused) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            postError(iSnapshotRuntimeProfileCallback, str, 0);
            return;
        }
        boolean equals = packageInfo.applicationInfo.getBaseCodePath().equals(str2);
        String[] splitCodePaths = packageInfo.applicationInfo.getSplitCodePaths();
        if (!equals && splitCodePaths != null) {
            int length = splitCodePaths.length - 1;
            while (true) {
                if (length < 0) {
                    break;
                } else if (splitCodePaths[length].equals(str2)) {
                    str3 = packageInfo.applicationInfo.splitNames[length];
                    equals = true;
                    break;
                } else {
                    length--;
                }
            }
        }
        if (!equals) {
            postError(iSnapshotRuntimeProfileCallback, str, 1);
        } else if (DexOptHelper.useArtService()) {
            try {
                try {
                    PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = PackageManagerServiceUtils.getPackageManagerLocal().withFilteredSnapshot();
                    try {
                        ParcelFileDescriptor snapshotAppProfile = DexOptHelper.getArtManagerLocal().snapshotAppProfile(withFilteredSnapshot, str, str3);
                        if (withFilteredSnapshot != null) {
                            withFilteredSnapshot.close();
                        }
                        postSuccess(str, snapshotAppProfile, iSnapshotRuntimeProfileCallback);
                    } catch (Throwable th) {
                        if (withFilteredSnapshot != null) {
                            try {
                                withFilteredSnapshot.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (IllegalStateException | ArtManagerLocal.SnapshotProfileException unused2) {
                    postError(iSnapshotRuntimeProfileCallback, str, 2);
                }
            } catch (IllegalArgumentException unused3) {
                postError(iSnapshotRuntimeProfileCallback, str, 0);
            }
        } else {
            int appId = UserHandle.getAppId(packageInfo.applicationInfo.uid);
            if (appId < 0) {
                postError(iSnapshotRuntimeProfileCallback, str, 2);
                Slog.wtf("ArtManagerService", "AppId is -1 for package: " + str);
                return;
            }
            try {
                createProfileSnapshot(str, ArtManager.getProfileName(str3), str2, appId, iSnapshotRuntimeProfileCallback);
                destroyProfileSnapshot(str, ArtManager.getProfileName(str3));
            } catch (Installer.LegacyDexoptDisabledException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void createProfileSnapshot(String str, String str2, String str3, int i, ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback) throws Installer.LegacyDexoptDisabledException {
        try {
            if (!this.mInstaller.createProfileSnapshot(i, str, str2, str3)) {
                postError(iSnapshotRuntimeProfileCallback, str, 2);
                return;
            }
            File profileSnapshotFileForName = ArtManager.getProfileSnapshotFileForName(str, str2);
            try {
                ParcelFileDescriptor open = ParcelFileDescriptor.open(profileSnapshotFileForName, 268435456);
                if (open != null && open.getFileDescriptor().valid()) {
                    postSuccess(str, open, iSnapshotRuntimeProfileCallback);
                }
                postError(iSnapshotRuntimeProfileCallback, str, 2);
            } catch (FileNotFoundException e) {
                Slog.w("ArtManagerService", "Could not open snapshot profile for " + str + XmlUtils.STRING_ARRAY_SEPARATOR + profileSnapshotFileForName, e);
                postError(iSnapshotRuntimeProfileCallback, str, 2);
            }
        } catch (Installer.InstallerException unused) {
            postError(iSnapshotRuntimeProfileCallback, str, 2);
        }
    }

    public final void destroyProfileSnapshot(String str, String str2) throws Installer.LegacyDexoptDisabledException {
        if (DEBUG) {
            Slog.d("ArtManagerService", "Destroying profile snapshot for" + str + XmlUtils.STRING_ARRAY_SEPARATOR + str2);
        }
        try {
            this.mInstaller.destroyProfileSnapshot(str, str2);
        } catch (Installer.InstallerException e) {
            Slog.e("ArtManagerService", "Failed to destroy profile snapshot for " + str + XmlUtils.STRING_ARRAY_SEPARATOR + str2, e);
        }
    }

    public boolean isRuntimeProfilingEnabled(int i, String str) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 2000 || checkAndroidPermissions(callingUid, str)) {
            if (i != 0) {
                if (i == 1) {
                    return (Build.IS_USERDEBUG || Build.IS_ENG) && SystemProperties.getBoolean("persist.device_config.runtime_native_boot.profilebootclasspath", SystemProperties.getBoolean("dalvik.vm.profilebootclasspath", false));
                }
                throw new IllegalArgumentException("Invalid profile type:" + i);
            }
            return true;
        }
        return false;
    }

    public final void snapshotBootImageProfile(ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback) {
        if (DexOptHelper.useArtService()) {
            try {
                PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = PackageManagerServiceUtils.getPackageManagerLocal().withFilteredSnapshot();
                ParcelFileDescriptor snapshotBootImageProfile = DexOptHelper.getArtManagerLocal().snapshotBootImageProfile(withFilteredSnapshot);
                if (withFilteredSnapshot != null) {
                    withFilteredSnapshot.close();
                }
                postSuccess(PackageManagerShellCommandDataLoader.PACKAGE, snapshotBootImageProfile, iSnapshotRuntimeProfileCallback);
                return;
            } catch (IllegalStateException | ArtManagerLocal.SnapshotProfileException unused) {
                postError(iSnapshotRuntimeProfileCallback, PackageManagerShellCommandDataLoader.PACKAGE, 2);
                return;
            }
        }
        String join = String.join(XmlUtils.STRING_ARRAY_SEPARATOR, Os.getenv("BOOTCLASSPATH"), Os.getenv("SYSTEMSERVERCLASSPATH"));
        String str = Os.getenv("STANDALONE_SYSTEMSERVER_JARS");
        if (str != null) {
            join = String.join(XmlUtils.STRING_ARRAY_SEPARATOR, join, str);
        }
        try {
            createProfileSnapshot(PackageManagerShellCommandDataLoader.PACKAGE, "android.prof", join, -1, iSnapshotRuntimeProfileCallback);
            destroyProfileSnapshot(PackageManagerShellCommandDataLoader.PACKAGE, "android.prof");
        } catch (Installer.LegacyDexoptDisabledException e) {
            throw new RuntimeException(e);
        }
    }

    public final void postError(final ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback, final String str, final int i) {
        if (DEBUG) {
            Slog.d("ArtManagerService", "Failed to snapshot profile for " + str + " with error: " + i);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.dex.ArtManagerService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ArtManagerService.lambda$postError$0(iSnapshotRuntimeProfileCallback, i, str);
            }
        });
    }

    public static /* synthetic */ void lambda$postError$0(ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback, int i, String str) {
        try {
            iSnapshotRuntimeProfileCallback.onError(i);
        } catch (RemoteException | RuntimeException e) {
            Slog.w("ArtManagerService", "Failed to callback after profile snapshot for " + str, e);
        }
    }

    public final void postSuccess(final String str, final ParcelFileDescriptor parcelFileDescriptor, final ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback) {
        if (DEBUG) {
            Slog.d("ArtManagerService", "Successfully snapshot profile for " + str);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.dex.ArtManagerService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ArtManagerService.lambda$postSuccess$1(parcelFileDescriptor, iSnapshotRuntimeProfileCallback, str);
            }
        });
    }

    public static /* synthetic */ void lambda$postSuccess$1(ParcelFileDescriptor parcelFileDescriptor, ISnapshotRuntimeProfileCallback iSnapshotRuntimeProfileCallback, String str) {
        try {
            try {
                if (parcelFileDescriptor.getFileDescriptor().valid()) {
                    iSnapshotRuntimeProfileCallback.onSuccess(parcelFileDescriptor);
                } else {
                    Slog.wtf("ArtManagerService", "The snapshot FD became invalid before posting the result for " + str);
                    iSnapshotRuntimeProfileCallback.onError(2);
                }
            } catch (RemoteException | RuntimeException e) {
                Slog.w("ArtManagerService", "Failed to call onSuccess after profile snapshot for " + str, e);
            }
        } finally {
            IoUtils.closeQuietly(parcelFileDescriptor);
        }
    }

    public void prepareAppProfiles(AndroidPackage androidPackage, int i, boolean z) throws Installer.LegacyDexoptDisabledException {
        File findDexMetadataForFile;
        int appId = UserHandle.getAppId(androidPackage.getUid());
        if (i < 0) {
            Slog.wtf("ArtManagerService", "Invalid user id: " + i);
        } else if (appId < 0) {
            Slog.wtf("ArtManagerService", "Invalid app id: " + appId);
        } else {
            try {
                ArrayMap<String, String> packageProfileNames = getPackageProfileNames(androidPackage);
                for (int size = packageProfileNames.size() - 1; size >= 0; size--) {
                    String keyAt = packageProfileNames.keyAt(size);
                    String valueAt = packageProfileNames.valueAt(size);
                    String str = null;
                    if (z && (findDexMetadataForFile = DexMetadataHelper.findDexMetadataForFile(new File(keyAt))) != null) {
                        str = findDexMetadataForFile.getAbsolutePath();
                    }
                    String str2 = str;
                    synchronized (this.mInstaller) {
                        if (!this.mInstaller.prepareAppProfile(androidPackage.getPackageName(), i, appId, valueAt, keyAt, str2)) {
                            Slog.e("ArtManagerService", "Failed to prepare profile for " + androidPackage.getPackageName() + XmlUtils.STRING_ARRAY_SEPARATOR + keyAt);
                        }
                    }
                }
            } catch (Installer.InstallerException e) {
                Slog.e("ArtManagerService", "Failed to prepare profile for " + androidPackage.getPackageName(), e);
            }
        }
    }

    public void prepareAppProfiles(AndroidPackage androidPackage, int[] iArr, boolean z) throws Installer.LegacyDexoptDisabledException {
        for (int i : iArr) {
            prepareAppProfiles(androidPackage, i, z);
        }
    }

    public void clearAppProfiles(AndroidPackage androidPackage) throws Installer.LegacyDexoptDisabledException {
        try {
            ArrayMap<String, String> packageProfileNames = getPackageProfileNames(androidPackage);
            for (int size = packageProfileNames.size() - 1; size >= 0; size--) {
                this.mInstaller.clearAppProfiles(androidPackage.getPackageName(), packageProfileNames.valueAt(size));
            }
        } catch (Installer.InstallerException e) {
            Slog.w("ArtManagerService", String.valueOf(e));
        }
    }

    public void dumpProfiles(AndroidPackage androidPackage, boolean z) throws Installer.LegacyDexoptDisabledException {
        int sharedAppGid = UserHandle.getSharedAppGid(androidPackage.getUid());
        try {
            ArrayMap<String, String> packageProfileNames = getPackageProfileNames(androidPackage);
            for (int size = packageProfileNames.size() - 1; size >= 0; size--) {
                this.mInstaller.dumpProfiles(sharedAppGid, androidPackage.getPackageName(), packageProfileNames.valueAt(size), packageProfileNames.keyAt(size), z);
            }
        } catch (Installer.InstallerException e) {
            Slog.w("ArtManagerService", "Failed to dump profiles", e);
        }
    }

    public boolean compileLayouts(PackageState packageState, AndroidPackage androidPackage) {
        try {
            String packageName = androidPackage.getPackageName();
            String path = androidPackage.getSplits().get(0).getPath();
            File dataDir = PackageInfoUtils.getDataDir(androidPackage, UserHandle.myUserId());
            String str = dataDir.getAbsolutePath() + "/code_cache/compiled_view.dex";
            if (!packageState.isPrivileged() && !androidPackage.isUseEmbeddedDex() && !androidPackage.isDefaultToDeviceProtectedStorage()) {
                Log.i("PackageManager", "Compiling layouts in " + packageName + " (" + path + ") to " + str);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                boolean compileLayouts = this.mInstaller.compileLayouts(path, packageName, str, androidPackage.getUid());
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return compileLayouts;
            }
            return false;
        } catch (Throwable th) {
            Log.e("PackageManager", "Failed to compile layouts", th);
            return false;
        }
    }

    public final ArrayMap<String, String> getPackageProfileNames(AndroidPackage androidPackage) {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        if (androidPackage.isDeclaredHavingCode()) {
            arrayMap.put(androidPackage.getBaseApkPath(), ArtManager.getProfileName((String) null));
        }
        String[] splitCodePaths = androidPackage.getSplitCodePaths();
        int[] splitFlags = androidPackage.getSplitFlags();
        String[] splitNames = androidPackage.getSplitNames();
        if (!ArrayUtils.isEmpty(splitCodePaths)) {
            for (int i = 0; i < splitCodePaths.length; i++) {
                if ((splitFlags[i] & 4) != 0) {
                    arrayMap.put(splitCodePaths[i], ArtManager.getProfileName(splitNames[i]));
                }
            }
        }
        return arrayMap;
    }

    public static int getCompilationReasonTronValue(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1968171580:
                if (str.equals("bg-dexopt")) {
                    c = 0;
                    break;
                }
                break;
            case -1836520088:
                if (str.equals("install-fast-dm")) {
                    c = 1;
                    break;
                }
                break;
            case -1425983632:
                if (str.equals("ab-ota")) {
                    c = 2;
                    break;
                }
                break;
            case -1291894341:
                if (str.equals("prebuilt")) {
                    c = 3;
                    break;
                }
                break;
            case -1205769507:
                if (str.equals("boot-after-mainline-update")) {
                    c = 4;
                    break;
                }
                break;
            case -1125526357:
                if (str.equals("install-bulk-secondary-dm")) {
                    c = 5;
                    break;
                }
                break;
            case -903566235:
                if (str.equals("shared")) {
                    c = 6;
                    break;
                }
                break;
            case -587828592:
                if (str.equals("boot-after-ota")) {
                    c = 7;
                    break;
                }
                break;
            case -525717262:
                if (str.equals("install-bulk-dm")) {
                    c = '\b';
                    break;
                }
                break;
            case -207505425:
                if (str.equals("first-boot")) {
                    c = '\t';
                    break;
                }
                break;
            case 3614689:
                if (str.equals("vdex")) {
                    c = '\n';
                    break;
                }
                break;
            case 17118443:
                if (str.equals("install-bulk-secondary")) {
                    c = 11;
                    break;
                }
                break;
            case 24665195:
                if (str.equals("inactive")) {
                    c = '\f';
                    break;
                }
                break;
            case 96784904:
                if (str.equals("error")) {
                    c = '\r';
                    break;
                }
                break;
            case 884802606:
                if (str.equals("cmdline")) {
                    c = 14;
                    break;
                }
                break;
            case 900392443:
                if (str.equals("install-dm")) {
                    c = 15;
                    break;
                }
                break;
            case 1558537393:
                if (str.equals("install-bulk-secondary-downgraded")) {
                    c = 16;
                    break;
                }
                break;
            case 1756645502:
                if (str.equals("install-bulk-downgraded-dm")) {
                    c = 17;
                    break;
                }
                break;
            case 1791051557:
                if (str.equals("install-bulk-secondary-downgraded-dm")) {
                    c = 18;
                    break;
                }
                break;
            case 1956259839:
                if (str.equals("post-boot")) {
                    c = 19;
                    break;
                }
                break;
            case 1957569947:
                if (str.equals("install")) {
                    c = 20;
                    break;
                }
                break;
            case 1988662788:
                if (str.equals("install-bulk")) {
                    c = 21;
                    break;
                }
                break;
            case 1988762958:
                if (str.equals("install-fast")) {
                    c = 22;
                    break;
                }
                break;
            case 2005174776:
                if (str.equals("install-bulk-downgraded")) {
                    c = 23;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 5;
            case 1:
                return 15;
            case 2:
                return 6;
            case 3:
                return 23;
            case 4:
                return 25;
            case 5:
                return 17;
            case 6:
                return 8;
            case 7:
                return 20;
            case '\b':
                return 16;
            case '\t':
                return 2;
            case '\n':
                return 24;
            case 11:
                return 12;
            case '\f':
                return 7;
            case '\r':
                return 0;
            case 14:
                return 22;
            case 15:
                return 9;
            case 16:
                return 14;
            case 17:
                return 18;
            case 18:
                return 19;
            case 19:
                return 21;
            case 20:
                return 4;
            case 21:
                return 11;
            case 22:
                return 10;
            case 23:
                return 13;
            default:
                return 1;
        }
    }

    public static int getCompilationFilterTronValue(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2111392495:
                if (str.equals("speed-profile-iorap")) {
                    c = 0;
                    break;
                }
                break;
            case -1957514039:
                if (str.equals("assume-verified")) {
                    c = 1;
                    break;
                }
                break;
            case -1803365233:
                if (str.equals("everything-profile")) {
                    c = 2;
                    break;
                }
                break;
            case -1707970841:
                if (str.equals("verify-iorap")) {
                    c = 3;
                    break;
                }
                break;
            case -1704485649:
                if (str.equals("extract-iorap")) {
                    c = 4;
                    break;
                }
                break;
            case -1305289599:
                if (str.equals("extract")) {
                    c = 5;
                    break;
                }
                break;
            case -1129892317:
                if (str.equals("speed-profile")) {
                    c = 6;
                    break;
                }
                break;
            case -1079751646:
                if (str.equals("run-from-apk-fallback-iorap")) {
                    c = 7;
                    break;
                }
                break;
            case -902315795:
                if (str.equals("run-from-vdex-fallback")) {
                    c = '\b';
                    break;
                }
                break;
            case -819951495:
                if (str.equals("verify")) {
                    c = '\t';
                    break;
                }
                break;
            case -701043824:
                if (str.equals("space-profile-iorap")) {
                    c = '\n';
                    break;
                }
                break;
            case -44924837:
                if (str.equals("run-from-vdex-fallback-iorap")) {
                    c = 11;
                    break;
                }
                break;
            case 50732855:
                if (str.equals("assume-verified-iorap")) {
                    c = '\f';
                    break;
                }
                break;
            case 96784904:
                if (str.equals("error")) {
                    c = '\r';
                    break;
                }
                break;
            case 109637894:
                if (str.equals("space")) {
                    c = 14;
                    break;
                }
                break;
            case 109641799:
                if (str.equals("speed")) {
                    c = 15;
                    break;
                }
                break;
            case 256996201:
                if (str.equals("run-from-apk-iorap")) {
                    c = 16;
                    break;
                }
                break;
            case 348518370:
                if (str.equals("space-profile")) {
                    c = 17;
                    break;
                }
                break;
            case 401590963:
                if (str.equals("everything")) {
                    c = 18;
                    break;
                }
                break;
            case 590454177:
                if (str.equals("everything-iorap")) {
                    c = 19;
                    break;
                }
                break;
            case 658336598:
                if (str.equals("quicken")) {
                    c = 20;
                    break;
                }
                break;
            case 863294077:
                if (str.equals("everything-profile-iorap")) {
                    c = 21;
                    break;
                }
                break;
            case 922064507:
                if (str.equals("run-from-apk")) {
                    c = 22;
                    break;
                }
                break;
            case 979981365:
                if (str.equals("speed-iorap")) {
                    c = 23;
                    break;
                }
                break;
            case 1316714932:
                if (str.equals("space-iorap")) {
                    c = 24;
                    break;
                }
                break;
            case 1482618884:
                if (str.equals("quicken-iorap")) {
                    c = 25;
                    break;
                }
                break;
            case 1906552308:
                if (str.equals("run-from-apk-fallback")) {
                    c = 26;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 21;
            case 1:
                return 2;
            case 2:
                return 10;
            case 3:
                return 17;
            case 4:
                return 16;
            case 5:
                return 3;
            case 6:
                return 8;
            case 7:
                return 26;
            case '\b':
                return 14;
            case '\t':
                return 4;
            case '\n':
                return 19;
            case 11:
                return 27;
            case '\f':
                return 15;
            case '\r':
                return 0;
            case 14:
                return 7;
            case 15:
                return 9;
            case 16:
                return 25;
            case 17:
                return 6;
            case 18:
                return 11;
            case 19:
                return 24;
            case 20:
                return 5;
            case 21:
                return 23;
            case 22:
                return 12;
            case 23:
                return 22;
            case 24:
                return 20;
            case 25:
                return 18;
            case 26:
                return 13;
            default:
                return 1;
        }
    }

    public static void verifyTronLoggingConstants() {
        String str;
        int i = 0;
        while (true) {
            String[] strArr = PackageManagerServiceCompilerMapping.REASON_STRINGS;
            if (i >= strArr.length) {
                return;
            }
            str = strArr[i];
            int compilationReasonTronValue = getCompilationReasonTronValue(str);
            if (compilationReasonTronValue == 0 || compilationReasonTronValue == 1) {
                break;
            }
            i++;
        }
        throw new IllegalArgumentException("Compilation reason not configured for TRON logging: " + str);
    }

    /* renamed from: com.android.server.pm.dex.ArtManagerService$ArtManagerInternalImpl */
    /* loaded from: classes2.dex */
    public class ArtManagerInternalImpl extends ArtManagerInternal {
        public ArtManagerInternalImpl() {
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0070  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public PackageOptimizationInfo getPackageOptimizationInfo(ApplicationInfo applicationInfo, String str, String str2) {
            String str3;
            String str4 = "error";
            if (applicationInfo.packageName.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
                return PackageOptimizationInfo.createWithNoInfo();
            }
            try {
                DexFile.OptimizationInfo dexFileOptimizationInfo = DexFile.getDexFileOptimizationInfo(applicationInfo.getBaseCodePath(), VMRuntime.getInstructionSet(str));
                String status = dexFileOptimizationInfo.getStatus();
                str3 = dexFileOptimizationInfo.getReason();
                str4 = status;
            } catch (FileNotFoundException e) {
                Slog.e("ArtManagerInternalImpl", "Could not get optimizations status for " + applicationInfo.getBaseCodePath(), e);
                str3 = "error";
                if (checkIorapCompiledTrace(applicationInfo.packageName, str2, applicationInfo.longVersionCode)) {
                }
                return new PackageOptimizationInfo(ArtManagerService.getCompilationFilterTronValue(str4), ArtManagerService.getCompilationReasonTronValue(str3));
            } catch (IllegalArgumentException e2) {
                Slog.wtf("ArtManagerInternalImpl", "Requested optimization status for " + applicationInfo.getBaseCodePath() + " due to an invalid abi " + str, e2);
                str3 = "error";
                if (checkIorapCompiledTrace(applicationInfo.packageName, str2, applicationInfo.longVersionCode)) {
                }
                return new PackageOptimizationInfo(ArtManagerService.getCompilationFilterTronValue(str4), ArtManagerService.getCompilationReasonTronValue(str3));
            }
            if (checkIorapCompiledTrace(applicationInfo.packageName, str2, applicationInfo.longVersionCode)) {
                str4 = str4 + "-iorap";
            }
            return new PackageOptimizationInfo(ArtManagerService.getCompilationFilterTronValue(str4), ArtManagerService.getCompilationReasonTronValue(str3));
        }

        public final boolean checkIorapCompiledTrace(String str, String str2, long j) {
            Path path = Paths.get("/data/misc/iorapd", str, Long.toString(j), str2, "compiled_traces", "compiled_trace.pb");
            try {
                boolean exists = Files.exists(path, new LinkOption[0]);
                if (ArtManagerService.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(path.toString());
                    sb.append(exists ? " exists" : " doesn't exist");
                    Log.d("ArtManagerInternalImpl", sb.toString());
                }
                if (exists) {
                    long size = Files.size(path);
                    if (ArtManagerService.DEBUG) {
                        Log.d("ArtManagerInternalImpl", path.toString() + " size is " + Long.toString(size));
                    }
                    return size > 0;
                }
                return exists;
            } catch (IOException e) {
                Log.d("ArtManagerInternalImpl", e.getMessage());
                return false;
            }
        }
    }
}
