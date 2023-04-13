package com.android.internal.content;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageInstaller;
import android.content.p001pm.PackageManager;
import android.content.p001pm.dex.DexMetadataHelper;
import android.content.p001pm.parsing.PackageLite;
import android.p008os.Environment;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemProperties;
import android.p008os.storage.IStorageManager;
import android.p008os.storage.StorageManager;
import android.p008os.storage.StorageVolume;
import android.p008os.storage.VolumeInfo;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.C4057R;
import com.android.internal.content.NativeLibraryHelper;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import libcore.io.IoUtils;
/* loaded from: classes4.dex */
public class InstallLocationUtils {
    public static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_EXTERNAL = 2;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int RECOMMEND_FAILED_ALREADY_EXISTS = -4;
    public static final int RECOMMEND_FAILED_INSUFFICIENT_STORAGE = -1;
    public static final int RECOMMEND_FAILED_INVALID_APK = -2;
    public static final int RECOMMEND_FAILED_INVALID_LOCATION = -3;
    public static final int RECOMMEND_FAILED_INVALID_URI = -6;
    public static final int RECOMMEND_INSTALL_EPHEMERAL = 3;
    public static final int RECOMMEND_INSTALL_EXTERNAL = 2;
    public static final int RECOMMEND_INSTALL_INTERNAL = 1;
    public static final int RECOMMEND_MEDIA_UNAVAILABLE = -5;
    private static final String TAG = "PackageHelper";
    private static TestableInterface sDefaultTestableInterface = null;

    /* loaded from: classes4.dex */
    public static abstract class TestableInterface {
        public abstract boolean getAllow3rdPartyOnInternalConfig(Context context);

        public abstract File getDataDirectory();

        public abstract ApplicationInfo getExistingAppInfo(Context context, String str);

        public abstract boolean getForceAllowOnExternalSetting(Context context);

        public abstract StorageManager getStorageManager(Context context);
    }

    public static IStorageManager getStorageManager() throws RemoteException {
        IBinder service = ServiceManager.getService("mount");
        if (service != null) {
            return IStorageManager.Stub.asInterface(service);
        }
        Log.m110e(TAG, "Can't get storagemanager service");
        throw new RemoteException("Could not contact storagemanager service");
    }

    private static synchronized TestableInterface getDefaultTestableInterface() {
        TestableInterface testableInterface;
        synchronized (InstallLocationUtils.class) {
            if (sDefaultTestableInterface == null) {
                sDefaultTestableInterface = new TestableInterface() { // from class: com.android.internal.content.InstallLocationUtils.1
                    @Override // com.android.internal.content.InstallLocationUtils.TestableInterface
                    public StorageManager getStorageManager(Context context) {
                        return (StorageManager) context.getSystemService(StorageManager.class);
                    }

                    @Override // com.android.internal.content.InstallLocationUtils.TestableInterface
                    public boolean getForceAllowOnExternalSetting(Context context) {
                        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.FORCE_ALLOW_ON_EXTERNAL, 0) != 0;
                    }

                    @Override // com.android.internal.content.InstallLocationUtils.TestableInterface
                    public boolean getAllow3rdPartyOnInternalConfig(Context context) {
                        return context.getResources().getBoolean(C4057R.bool.config_allow3rdPartyAppOnInternal);
                    }

                    @Override // com.android.internal.content.InstallLocationUtils.TestableInterface
                    public ApplicationInfo getExistingAppInfo(Context context, String packageName) {
                        try {
                            ApplicationInfo existingInfo = context.getPackageManager().getApplicationInfo(packageName, 4194304);
                            return existingInfo;
                        } catch (PackageManager.NameNotFoundException e) {
                            return null;
                        }
                    }

                    @Override // com.android.internal.content.InstallLocationUtils.TestableInterface
                    public File getDataDirectory() {
                        return Environment.getDataDirectory();
                    }
                };
            }
            testableInterface = sDefaultTestableInterface;
        }
        return testableInterface;
    }

    @Deprecated
    public static String resolveInstallVolume(Context context, String packageName, int installLocation, long sizeBytes, TestableInterface testInterface) throws IOException {
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(-1);
        params.appPackageName = packageName;
        params.installLocation = installLocation;
        params.sizeBytes = sizeBytes;
        return resolveInstallVolume(context, params, testInterface);
    }

    public static String resolveInstallVolume(Context context, PackageInstaller.SessionParams params) throws IOException {
        TestableInterface testableInterface = getDefaultTestableInterface();
        return resolveInstallVolume(context, params.appPackageName, params.installLocation, params.sizeBytes, testableInterface);
    }

    private static boolean checkFitOnVolume(StorageManager storageManager, String volumePath, PackageInstaller.SessionParams params) throws IOException {
        if (volumePath == null) {
            return false;
        }
        int installFlags = translateAllocateFlags(params.installFlags);
        UUID target = storageManager.getUuidForPath(new File(volumePath));
        long availBytes = storageManager.getAllocatableBytes(target, installFlags | 8);
        if (params.sizeBytes <= availBytes) {
            return true;
        }
        long cacheClearable = storageManager.getAllocatableBytes(target, installFlags | 16);
        return params.sizeBytes <= availBytes + cacheClearable;
    }

    public static String resolveInstallVolume(Context context, PackageInstaller.SessionParams params, TestableInterface testInterface) throws IOException {
        StorageManager storageManager = testInterface.getStorageManager(context);
        boolean forceAllowOnExternal = testInterface.getForceAllowOnExternalSetting(context);
        boolean allow3rdPartyOnInternal = testInterface.getAllow3rdPartyOnInternalConfig(context);
        ApplicationInfo existingInfo = testInterface.getExistingAppInfo(context, params.appPackageName);
        ArrayMap<String, String> volumePaths = new ArrayMap<>();
        String internalVolumePath = null;
        for (VolumeInfo vol : storageManager.getVolumes()) {
            if (vol.type == 1 && vol.isMountedWritable()) {
                boolean isInternalStorage = VolumeInfo.ID_PRIVATE_INTERNAL.equals(vol.f330id);
                if (isInternalStorage) {
                    internalVolumePath = vol.path;
                }
                if (!isInternalStorage || allow3rdPartyOnInternal) {
                    volumePaths.put(vol.fsUuid, vol.path);
                }
            }
        }
        if (existingInfo != null && existingInfo.isSystemApp()) {
            if (checkFitOnVolume(storageManager, internalVolumePath, params)) {
                return StorageManager.UUID_PRIVATE_INTERNAL;
            }
            throw new IOException("Not enough space on existing volume " + existingInfo.volumeUuid + " for system app " + params.appPackageName + " upgrade");
        } else if (!forceAllowOnExternal && params.installLocation == 1) {
            if (existingInfo != null && !Objects.equals(existingInfo.volumeUuid, StorageManager.UUID_PRIVATE_INTERNAL)) {
                throw new IOException("Cannot automatically move " + params.appPackageName + " from " + existingInfo.volumeUuid + " to internal storage");
            }
            if (!allow3rdPartyOnInternal) {
                throw new IOException("Not allowed to install non-system apps on internal storage");
            }
            if (checkFitOnVolume(storageManager, internalVolumePath, params)) {
                return StorageManager.UUID_PRIVATE_INTERNAL;
            }
            throw new IOException("Requested internal only, but not enough space");
        } else if (existingInfo != null) {
            String existingVolumePath = null;
            if (Objects.equals(existingInfo.volumeUuid, StorageManager.UUID_PRIVATE_INTERNAL)) {
                existingVolumePath = internalVolumePath;
            } else if (volumePaths.containsKey(existingInfo.volumeUuid)) {
                String existingVolumePath2 = volumePaths.get(existingInfo.volumeUuid);
                existingVolumePath = existingVolumePath2;
            }
            if (checkFitOnVolume(storageManager, existingVolumePath, params)) {
                return existingInfo.volumeUuid;
            }
            throw new IOException("Not enough space on existing volume " + existingInfo.volumeUuid + " for " + params.appPackageName + " upgrade");
        } else {
            String bestCandidate = !volumePaths.isEmpty() ? volumePaths.keyAt(0) : null;
            if (volumePaths.size() == 1) {
                if (checkFitOnVolume(storageManager, volumePaths.valueAt(0), params)) {
                    return bestCandidate;
                }
            } else {
                long bestCandidateAvailBytes = Long.MIN_VALUE;
                for (String vol2 : volumePaths.keySet()) {
                    String volumePath = volumePaths.get(vol2);
                    UUID target = storageManager.getUuidForPath(new File(volumePath));
                    long availBytes = storageManager.getAllocatableBytes(target, translateAllocateFlags(params.installFlags));
                    if (availBytes >= bestCandidateAvailBytes) {
                        bestCandidateAvailBytes = availBytes;
                        bestCandidate = vol2;
                    }
                }
                if (bestCandidateAvailBytes >= params.sizeBytes) {
                    return bestCandidate;
                }
            }
            if (!volumePaths.isEmpty() && 2147483647L == params.sizeBytes && SystemProperties.getBoolean("debug.pm.install_skip_size_check_for_maxint", false)) {
                return bestCandidate;
            }
            throw new IOException("No special requests, but no room on allowed volumes.  allow3rdPartyOnInternal? " + allow3rdPartyOnInternal);
        }
    }

    public static boolean fitsOnInternal(Context context, PackageInstaller.SessionParams params) throws IOException {
        StorageManager storage = (StorageManager) context.getSystemService(StorageManager.class);
        UUID target = storage.getUuidForPath(Environment.getDataDirectory());
        int flags = translateAllocateFlags(params.installFlags);
        long allocateableBytes = storage.getAllocatableBytes(target, flags | 8);
        if (params.sizeBytes <= allocateableBytes) {
            return true;
        }
        long cacheClearable = storage.getAllocatableBytes(target, flags | 16);
        return params.sizeBytes <= allocateableBytes + cacheClearable;
    }

    public static boolean fitsOnExternal(Context context, PackageInstaller.SessionParams params) {
        StorageManager storage = (StorageManager) context.getSystemService(StorageManager.class);
        StorageVolume primary = storage.getPrimaryVolume();
        return params.sizeBytes > 0 && !primary.isEmulated() && Environment.MEDIA_MOUNTED.equals(primary.getState()) && params.sizeBytes <= storage.getStorageBytesUntilLow(primary.getPathFile());
    }

    public static int resolveInstallLocation(Context context, PackageInstaller.SessionParams params) throws IOException {
        int prefer;
        boolean checkBoth;
        ApplicationInfo existingInfo = null;
        try {
            existingInfo = context.getPackageManager().getApplicationInfo(params.appPackageName, 4194304);
        } catch (PackageManager.NameNotFoundException e) {
        }
        boolean ephemeral = false;
        if ((params.installFlags & 2048) != 0) {
            prefer = 1;
            ephemeral = true;
            checkBoth = false;
        } else {
            int prefer2 = params.installFlags;
            if ((prefer2 & 16) != 0) {
                prefer = 1;
                checkBoth = false;
            } else {
                int prefer3 = params.installLocation;
                if (prefer3 == 1) {
                    prefer = 1;
                    checkBoth = false;
                } else {
                    int prefer4 = params.installLocation;
                    if (prefer4 == 2) {
                        prefer = 2;
                        checkBoth = true;
                    } else {
                        int prefer5 = params.installLocation;
                        if (prefer5 == 0) {
                            if (existingInfo != null) {
                                if ((existingInfo.flags & 262144) != 0) {
                                    prefer = 2;
                                } else {
                                    prefer = 1;
                                }
                            } else {
                                prefer = 1;
                            }
                            checkBoth = true;
                        } else {
                            prefer = 1;
                            checkBoth = false;
                        }
                    }
                }
            }
        }
        boolean fitsOnInternal = false;
        if (checkBoth || prefer == 1) {
            fitsOnInternal = fitsOnInternal(context, params);
        }
        boolean fitsOnExternal = false;
        if (checkBoth || prefer == 2) {
            fitsOnExternal = fitsOnExternal(context, params);
        }
        if (prefer == 1) {
            if (fitsOnInternal) {
                return ephemeral ? 3 : 1;
            }
        } else if (prefer == 2 && fitsOnExternal) {
            return 2;
        }
        if (checkBoth) {
            if (fitsOnInternal) {
                return 1;
            }
            return fitsOnExternal ? 2 : -1;
        }
        return -1;
    }

    @Deprecated
    public static long calculateInstalledSize(PackageLite pkg, boolean isForwardLocked, String abiOverride) throws IOException {
        return calculateInstalledSize(pkg, abiOverride);
    }

    public static long calculateInstalledSize(PackageLite pkg, String abiOverride) throws IOException {
        return calculateInstalledSize(pkg, abiOverride, (FileDescriptor) null);
    }

    public static long calculateInstalledSize(PackageLite pkg, String abiOverride, FileDescriptor fd) throws IOException {
        NativeLibraryHelper.Handle handle = null;
        try {
            handle = fd != null ? NativeLibraryHelper.Handle.createFd(pkg, fd) : NativeLibraryHelper.Handle.create(pkg);
            return calculateInstalledSize(pkg, handle, abiOverride);
        } finally {
            IoUtils.closeQuietly(handle);
        }
    }

    @Deprecated
    public static long calculateInstalledSize(PackageLite pkg, boolean isForwardLocked, NativeLibraryHelper.Handle handle, String abiOverride) throws IOException {
        return calculateInstalledSize(pkg, handle, abiOverride);
    }

    public static long calculateInstalledSize(PackageLite pkg, NativeLibraryHelper.Handle handle, String abiOverride) throws IOException {
        long sizeBytes = 0;
        for (String codePath : pkg.getAllApkPaths()) {
            File codeFile = new File(codePath);
            sizeBytes += codeFile.length();
        }
        return sizeBytes + DexMetadataHelper.getPackageDexMetadataSize(pkg) + NativeLibraryHelper.sumNativeBinariesWithOverride(handle, abiOverride);
    }

    public static String replaceEnd(String str, String before, String after) {
        if (!str.endsWith(before)) {
            throw new IllegalArgumentException("Expected " + str + " to end with " + before);
        }
        return str.substring(0, str.length() - before.length()) + after;
    }

    public static int translateAllocateFlags(int installFlags) {
        if ((32768 & installFlags) != 0) {
            return 1;
        }
        return 0;
    }

    public static int installLocationPolicy(int installLocation, int recommendedInstallLocation, int installFlags, boolean installedPkgIsSystem, boolean installedPackageOnExternal) {
        if ((installFlags & 2) == 0) {
            return -4;
        }
        if (installedPkgIsSystem || installLocation == 1) {
            return 1;
        }
        if (installLocation == 2) {
            return recommendedInstallLocation;
        }
        if (!installedPackageOnExternal) {
            return 1;
        }
        return 2;
    }

    public static int getInstallationErrorCode(int loc) {
        if (loc == -3) {
            return -19;
        }
        if (loc == -4) {
            return -1;
        }
        if (loc == -1) {
            return -4;
        }
        if (loc == -2) {
            return -2;
        }
        if (loc == -6) {
            return -3;
        }
        if (loc == -5) {
            return -20;
        }
        return 1;
    }
}
