package android.p008os;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.compat.Compatibility;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.PackageParser;
import android.p008os.storage.StorageManager;
import android.p008os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
/* renamed from: android.os.Environment */
/* loaded from: classes3.dex */
public class Environment {
    private static final long DEFAULT_SCOPED_STORAGE = 149924527;
    public static String DIRECTORY_ALARMS = null;
    @Deprecated
    public static final String DIRECTORY_ANDROID = "Android";
    public static String DIRECTORY_AUDIOBOOKS = null;
    public static String DIRECTORY_DCIM = null;
    public static String DIRECTORY_DOCUMENTS = null;
    public static String DIRECTORY_DOWNLOADS = null;
    public static String DIRECTORY_MOVIES = null;
    public static String DIRECTORY_MUSIC = null;
    public static String DIRECTORY_NOTIFICATIONS = null;
    public static String DIRECTORY_PICTURES = null;
    public static String DIRECTORY_PODCASTS = null;
    public static String DIRECTORY_RECORDINGS = null;
    public static String DIRECTORY_RINGTONES = null;
    public static String DIRECTORY_SCREENSHOTS = null;
    public static final String DIR_ANDROID = "Android";
    private static final File DIR_ANDROID_DATA;
    private static final String DIR_ANDROID_DATA_PATH;
    private static final File DIR_ANDROID_EXPAND;
    private static final File DIR_ANDROID_STORAGE;
    private static final File DIR_APEX_ROOT;
    private static final String DIR_CACHE = "cache";
    private static final String DIR_DATA = "data";
    private static final File DIR_DOWNLOAD_CACHE;
    private static final String DIR_FILES = "files";
    private static final String DIR_MEDIA = "media";
    private static final File DIR_METADATA;
    private static final String DIR_OBB = "obb";
    private static final File DIR_ODM_ROOT;
    private static final File DIR_OEM_ROOT;
    private static final File DIR_PRODUCT_ROOT;
    private static final File DIR_SYSTEM_EXT_ROOT;
    public static final String DIR_USER_CE = "user";
    public static final String DIR_USER_DE = "user_de";
    private static final File DIR_VENDOR_ROOT;
    private static final String ENV_ANDROID_DATA = "ANDROID_DATA";
    private static final String ENV_ANDROID_EXPAND = "ANDROID_EXPAND";
    private static final String ENV_ANDROID_STORAGE = "ANDROID_STORAGE";
    private static final String ENV_APEX_ROOT = "APEX_ROOT";
    private static final String ENV_DOWNLOAD_CACHE = "DOWNLOAD_CACHE";
    private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final String ENV_ODM_ROOT = "ODM_ROOT";
    private static final String ENV_OEM_ROOT = "OEM_ROOT";
    private static final String ENV_PRODUCT_ROOT = "PRODUCT_ROOT";
    private static final String ENV_SYSTEM_EXT_ROOT = "SYSTEM_EXT_ROOT";
    private static final String ENV_VENDOR_ROOT = "VENDOR_ROOT";
    private static final long FORCE_ENABLE_SCOPED_STORAGE = 132649864;
    public static final int HAS_ALARMS = 8;
    public static final int HAS_ANDROID = 65536;
    public static final int HAS_AUDIOBOOKS = 1024;
    public static final int HAS_DCIM = 256;
    public static final int HAS_DOCUMENTS = 512;
    public static final int HAS_DOWNLOADS = 128;
    public static final int HAS_MOVIES = 64;
    public static final int HAS_MUSIC = 1;
    public static final int HAS_NOTIFICATIONS = 16;
    public static final int HAS_OTHER = 131072;
    public static final int HAS_PICTURES = 32;
    public static final int HAS_PODCASTS = 2;
    public static final int HAS_RECORDINGS = 2048;
    public static final int HAS_RINGTONES = 4;
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_EJECTING = "ejecting";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";
    public static final String[] STANDARD_DIRECTORIES;
    private static final String TAG = "Environment";
    private static UserEnvironment sCurrentUser;
    private static boolean sUserRequired;
    private static final String ENV_ANDROID_ROOT = "ANDROID_ROOT";
    private static final File DIR_ANDROID_ROOT = getDirectory(ENV_ANDROID_ROOT, "/system");

    static {
        String directoryPath = getDirectoryPath(ENV_ANDROID_DATA, "/data");
        DIR_ANDROID_DATA_PATH = directoryPath;
        DIR_ANDROID_DATA = new File(directoryPath);
        DIR_ANDROID_EXPAND = getDirectory(ENV_ANDROID_EXPAND, "/mnt/expand");
        DIR_ANDROID_STORAGE = getDirectory(ENV_ANDROID_STORAGE, "/storage");
        DIR_DOWNLOAD_CACHE = getDirectory(ENV_DOWNLOAD_CACHE, "/cache");
        DIR_METADATA = new File("/metadata");
        DIR_OEM_ROOT = getDirectory(ENV_OEM_ROOT, "/oem");
        DIR_ODM_ROOT = getDirectory(ENV_ODM_ROOT, "/odm");
        DIR_VENDOR_ROOT = getDirectory(ENV_VENDOR_ROOT, "/vendor");
        DIR_PRODUCT_ROOT = getDirectory(ENV_PRODUCT_ROOT, "/product");
        DIR_SYSTEM_EXT_ROOT = getDirectory(ENV_SYSTEM_EXT_ROOT, "/system_ext");
        DIR_APEX_ROOT = getDirectory(ENV_APEX_ROOT, "/apex");
        initForCurrentUser();
        DIRECTORY_MUSIC = "Music";
        DIRECTORY_PODCASTS = "Podcasts";
        DIRECTORY_RINGTONES = "Ringtones";
        DIRECTORY_ALARMS = "Alarms";
        DIRECTORY_NOTIFICATIONS = "Notifications";
        DIRECTORY_PICTURES = "Pictures";
        DIRECTORY_MOVIES = "Movies";
        DIRECTORY_DOWNLOADS = "Download";
        DIRECTORY_DCIM = "DCIM";
        DIRECTORY_DOCUMENTS = "Documents";
        DIRECTORY_SCREENSHOTS = "Screenshots";
        DIRECTORY_AUDIOBOOKS = "Audiobooks";
        DIRECTORY_RECORDINGS = "Recordings";
        STANDARD_DIRECTORIES = new String[]{"Music", "Podcasts", "Ringtones", "Alarms", "Notifications", "Pictures", "Movies", "Download", "DCIM", "Documents", "Audiobooks", "Recordings"};
    }

    public static void initForCurrentUser() {
        int userId = UserHandle.myUserId();
        sCurrentUser = new UserEnvironment(userId);
    }

    /* renamed from: android.os.Environment$UserEnvironment */
    /* loaded from: classes3.dex */
    public static class UserEnvironment {
        private final int mUserId;

        public UserEnvironment(int userId) {
            this.mUserId = userId;
        }

        public File[] getExternalDirs() {
            StorageVolume[] volumes = StorageManager.getVolumeList(this.mUserId, 256);
            File[] files = new File[volumes.length];
            for (int i = 0; i < volumes.length; i++) {
                files[i] = volumes[i].getPathFile();
            }
            return files;
        }

        public File getExternalStorageDirectory() {
            return getExternalDirs()[0];
        }

        public File getExternalStoragePublicDirectory(String type) {
            return buildExternalStoragePublicDirs(type)[0];
        }

        public File[] buildExternalStoragePublicDirs(String type) {
            return Environment.buildPaths(getExternalDirs(), type);
        }

        public File[] buildExternalStorageAndroidDataDirs() {
            return Environment.buildPaths(getExternalDirs(), "Android", "data");
        }

        public File[] buildExternalStorageAndroidObbDirs() {
            return Environment.buildPaths(getExternalDirs(), "Android", "obb");
        }

        public File[] buildExternalStorageAppDataDirs(String packageName) {
            return Environment.buildPaths(getExternalDirs(), "Android", "data", packageName);
        }

        public File[] buildExternalStorageAppMediaDirs(String packageName) {
            return Environment.buildPaths(getExternalDirs(), "Android", Environment.DIR_MEDIA, packageName);
        }

        public File[] buildExternalStorageAppObbDirs(String packageName) {
            return Environment.buildPaths(getExternalDirs(), "Android", "obb", packageName);
        }

        public File[] buildExternalStorageAppFilesDirs(String packageName) {
            return Environment.buildPaths(getExternalDirs(), "Android", "data", packageName, Environment.DIR_FILES);
        }

        public File[] buildExternalStorageAppCacheDirs(String packageName) {
            return Environment.buildPaths(getExternalDirs(), "Android", "data", packageName, Environment.DIR_CACHE);
        }
    }

    public static File getRootDirectory() {
        return DIR_ANDROID_ROOT;
    }

    public static File getStorageDirectory() {
        return DIR_ANDROID_STORAGE;
    }

    @SystemApi
    public static File getOemDirectory() {
        return DIR_OEM_ROOT;
    }

    @SystemApi
    public static File getOdmDirectory() {
        return DIR_ODM_ROOT;
    }

    @SystemApi
    public static File getVendorDirectory() {
        return DIR_VENDOR_ROOT;
    }

    @SystemApi
    public static File getProductDirectory() {
        return DIR_PRODUCT_ROOT;
    }

    @SystemApi
    @Deprecated
    public static File getProductServicesDirectory() {
        return getDirectory("PRODUCT_SERVICES_ROOT", "/product_services");
    }

    @SystemApi
    public static File getSystemExtDirectory() {
        return DIR_SYSTEM_EXT_ROOT;
    }

    public static File getApexDirectory() {
        return DIR_APEX_ROOT;
    }

    @Deprecated
    public static File getUserSystemDirectory(int userId) {
        return new File(new File(getDataSystemDirectory(), "users"), Integer.toString(userId));
    }

    @Deprecated
    public static File getUserConfigDirectory(int userId) {
        return new File(new File(new File(getDataDirectory(), "misc"), "user"), Integer.toString(userId));
    }

    public static File getDataDirectory() {
        return DIR_ANDROID_DATA;
    }

    public static String getDataDirectoryPath() {
        return DIR_ANDROID_DATA_PATH;
    }

    public static File getDataDirectory(String volumeUuid) {
        if (TextUtils.isEmpty(volumeUuid)) {
            return DIR_ANDROID_DATA;
        }
        return new File(PackageParser.MNT_EXPAND + volumeUuid);
    }

    public static String getDataDirectoryPath(String volumeUuid) {
        if (TextUtils.isEmpty(volumeUuid)) {
            return DIR_ANDROID_DATA_PATH;
        }
        return getExpandDirectory().getAbsolutePath() + File.separator + volumeUuid;
    }

    public static File getExpandDirectory() {
        return DIR_ANDROID_EXPAND;
    }

    public static File getDataSystemDirectory() {
        return new File(getDataDirectory(), "system");
    }

    public static File getDataSystemDeDirectory() {
        return buildPath(getDataDirectory(), "system_de");
    }

    public static File getDataSystemCeDirectory() {
        return buildPath(getDataDirectory(), "system_ce");
    }

    public static File getDataSystemCeDirectory(int userId) {
        return buildPath(getDataDirectory(), "system_ce", String.valueOf(userId));
    }

    public static File getDataSystemDeDirectory(int userId) {
        return buildPath(getDataDirectory(), "system_de", String.valueOf(userId));
    }

    public static File getDataMiscDirectory() {
        return new File(getDataDirectory(), "misc");
    }

    public static File getDataMiscCeDirectory() {
        return buildPath(getDataDirectory(), "misc_ce");
    }

    public static File getDataMiscCeDirectory(int userId) {
        return buildPath(getDataDirectory(), "misc_ce", String.valueOf(userId));
    }

    private static File getDataMiscCeDirectory(String volumeUuid, int userId) {
        return buildPath(getDataDirectory(volumeUuid), "misc_ce", String.valueOf(userId));
    }

    public static File getDataMiscCeSharedSdkSandboxDirectory(String volumeUuid, int userId, String packageName) {
        return buildPath(getDataMiscCeDirectory(volumeUuid, userId), "sdksandbox", packageName, "shared");
    }

    public static File getDataMiscDeDirectory(int userId) {
        return buildPath(getDataDirectory(), "misc_de", String.valueOf(userId));
    }

    private static File getDataMiscDeDirectory(String volumeUuid, int userId) {
        return buildPath(getDataDirectory(volumeUuid), "misc_de", String.valueOf(userId));
    }

    public static File getDataMiscDeSharedSdkSandboxDirectory(String volumeUuid, int userId, String packageName) {
        return buildPath(getDataMiscDeDirectory(volumeUuid, userId), "sdksandbox", packageName, "shared");
    }

    private static File getDataProfilesDeDirectory(int userId) {
        return buildPath(getDataDirectory(), "misc", "profiles", "cur", String.valueOf(userId));
    }

    public static File getDataVendorCeDirectory(int userId) {
        return buildPath(getDataDirectory(), "vendor_ce", String.valueOf(userId));
    }

    public static File getDataVendorDeDirectory(int userId) {
        return buildPath(getDataDirectory(), "vendor_de", String.valueOf(userId));
    }

    public static File getDataRefProfilesDePackageDirectory(String packageName) {
        return buildPath(getDataDirectory(), "misc", "profiles", "ref", packageName);
    }

    public static File getDataProfilesDePackageDirectory(int userId, String packageName) {
        return buildPath(getDataProfilesDeDirectory(userId), packageName);
    }

    public static File getDataAppDirectory(String volumeUuid) {
        return new File(getDataDirectory(volumeUuid), "app");
    }

    public static File getDataStagingDirectory(String volumeUuid) {
        return new File(getDataDirectory(volumeUuid), "app-staging");
    }

    public static File getDataUserCeDirectory(String volumeUuid) {
        return new File(getDataDirectory(volumeUuid), "user");
    }

    public static File getDataUserCeDirectory(String volumeUuid, int userId) {
        return new File(getDataUserCeDirectory(volumeUuid), String.valueOf(userId));
    }

    public static File getDataUserCePackageDirectory(String volumeUuid, int userId, String packageName) {
        return new File(getDataUserCeDirectory(volumeUuid, userId), packageName);
    }

    @SystemApi
    public static File getDataCePackageDirectoryForUser(UUID storageUuid, UserHandle user, String packageName) {
        String volumeUuid = StorageManager.convert(storageUuid);
        return getDataUserCePackageDirectory(volumeUuid, user.getIdentifier(), packageName);
    }

    public static File getDataUserDeDirectory(String volumeUuid) {
        return new File(getDataDirectory(volumeUuid), DIR_USER_DE);
    }

    public static File getDataUserDeDirectory(String volumeUuid, int userId) {
        return new File(getDataUserDeDirectory(volumeUuid), String.valueOf(userId));
    }

    public static File getDataUserDePackageDirectory(String volumeUuid, int userId, String packageName) {
        return new File(getDataUserDeDirectory(volumeUuid, userId), packageName);
    }

    @SystemApi
    public static File getDataDePackageDirectoryForUser(UUID storageUuid, UserHandle user, String packageName) {
        String volumeUuid = StorageManager.convert(storageUuid);
        return getDataUserDePackageDirectory(volumeUuid, user.getIdentifier(), packageName);
    }

    public static File getDataPreloadsDirectory() {
        return new File(getDataDirectory(), "preloads");
    }

    public static File getDataPreloadsDemoDirectory() {
        return new File(getDataPreloadsDirectory(), "demo");
    }

    public static File getDataPreloadsAppsDirectory() {
        return new File(getDataPreloadsDirectory(), "apps");
    }

    public static File getDataPreloadsMediaDirectory() {
        return new File(getDataPreloadsDirectory(), DIR_MEDIA);
    }

    public static File getDataPreloadsFileCacheDirectory(String packageName) {
        return new File(getDataPreloadsFileCacheDirectory(), packageName);
    }

    public static File getDataPreloadsFileCacheDirectory() {
        return new File(getDataPreloadsDirectory(), "file_cache");
    }

    public static File getPackageCacheDirectory() {
        return new File(getDataSystemDirectory(), "package_cache");
    }

    @SystemApi
    public static Collection<File> getInternalMediaDirectories() {
        ArrayList<File> res = new ArrayList<>();
        addCanonicalFile(res, new File(getRootDirectory(), DIR_MEDIA));
        addCanonicalFile(res, new File(getOemDirectory(), DIR_MEDIA));
        addCanonicalFile(res, new File(getProductDirectory(), DIR_MEDIA));
        return res;
    }

    private static void addCanonicalFile(List<File> list, File file) {
        try {
            list.add(file.getCanonicalFile());
        } catch (IOException e) {
            Log.m104w(TAG, "Failed to resolve " + file + ": " + e);
            list.add(file);
        }
    }

    public static File getExternalStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getExternalDirs()[0];
    }

    public static File getLegacyExternalStorageDirectory() {
        return new File(System.getenv(ENV_EXTERNAL_STORAGE));
    }

    public static File getLegacyExternalStorageObbDirectory() {
        return buildPath(getLegacyExternalStorageDirectory(), "Android", "obb");
    }

    public static boolean isStandardDirectory(String dir) {
        String[] strArr;
        for (String valid : STANDARD_DIRECTORIES) {
            if (valid.equals(dir)) {
                return true;
            }
        }
        return false;
    }

    public static int classifyExternalStorageDirectory(File dir) {
        File[] listFilesOrEmpty;
        int res = 0;
        for (File f : FileUtils.listFilesOrEmpty(dir)) {
            if (f.isFile() && isInterestingFile(f)) {
                res |= 131072;
            } else if (f.isDirectory() && hasInterestingFiles(f)) {
                String name = f.getName();
                if (DIRECTORY_MUSIC.equals(name)) {
                    res |= 1;
                } else if (DIRECTORY_PODCASTS.equals(name)) {
                    res |= 2;
                } else if (DIRECTORY_RINGTONES.equals(name)) {
                    res |= 4;
                } else if (DIRECTORY_ALARMS.equals(name)) {
                    res |= 8;
                } else if (DIRECTORY_NOTIFICATIONS.equals(name)) {
                    res |= 16;
                } else if (DIRECTORY_PICTURES.equals(name)) {
                    res |= 32;
                } else if (DIRECTORY_MOVIES.equals(name)) {
                    res |= 64;
                } else if (DIRECTORY_DOWNLOADS.equals(name)) {
                    res |= 128;
                } else if (DIRECTORY_DCIM.equals(name)) {
                    res |= 256;
                } else if (DIRECTORY_DOCUMENTS.equals(name)) {
                    res |= 512;
                } else if (DIRECTORY_AUDIOBOOKS.equals(name)) {
                    res |= 1024;
                } else if (DIRECTORY_RECORDINGS.equals(name)) {
                    res |= 2048;
                } else {
                    res = "Android".equals(name) ? res | 65536 : res | 131072;
                }
            }
        }
        return res;
    }

    private static boolean hasInterestingFiles(File dir) {
        File[] listFilesOrEmpty;
        ArrayDeque<File> explore = new ArrayDeque<>();
        explore.add(dir);
        while (true) {
            if (explore.isEmpty()) {
                return false;
            }
            File dir2 = explore.pop();
            for (File f : FileUtils.listFilesOrEmpty(dir2)) {
                if (isInterestingFile(f)) {
                    return true;
                }
                if (f.isDirectory()) {
                    explore.add(f);
                }
            }
        }
    }

    private static boolean isInterestingFile(File file) {
        if (file.isFile()) {
            String name = file.getName().toLowerCase();
            return (name.endsWith(".exe") || name.equals("autorun.inf") || name.equals("launchpad.zip") || name.equals(".nomedia")) ? false : true;
        }
        return false;
    }

    public static File getExternalStoragePublicDirectory(String type) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStoragePublicDirs(type)[0];
    }

    public static File[] buildExternalStorageAndroidDataDirs() {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAndroidDataDirs();
    }

    public static File[] buildExternalStorageAndroidObbDirs() {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAndroidObbDirs();
    }

    public static File[] buildExternalStorageAppDataDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppDataDirs(packageName);
    }

    public static File[] buildExternalStorageAppMediaDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppMediaDirs(packageName);
    }

    public static File[] buildExternalStorageAppObbDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppObbDirs(packageName);
    }

    public static File[] buildExternalStorageAppFilesDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppFilesDirs(packageName);
    }

    public static File[] buildExternalStorageAppCacheDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppCacheDirs(packageName);
    }

    public static File[] buildExternalStoragePublicDirs(String dirType) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStoragePublicDirs(dirType);
    }

    public static File getDownloadCacheDirectory() {
        return DIR_DOWNLOAD_CACHE;
    }

    public static File getMetadataDirectory() {
        return DIR_METADATA;
    }

    public static String getExternalStorageState() {
        File externalDir = sCurrentUser.getExternalDirs()[0];
        return getExternalStorageState(externalDir);
    }

    @Deprecated
    public static String getStorageState(File path) {
        return getExternalStorageState(path);
    }

    public static String getExternalStorageState(File path) {
        StorageVolume volume = StorageManager.getStorageVolume(path, UserHandle.myUserId());
        if (volume != null) {
            return volume.getState();
        }
        return "unknown";
    }

    public static boolean isExternalStorageRemovable() {
        File externalDir = sCurrentUser.getExternalDirs()[0];
        return isExternalStorageRemovable(externalDir);
    }

    public static boolean isExternalStorageRemovable(File path) {
        StorageVolume volume = StorageManager.getStorageVolume(path, UserHandle.myUserId());
        if (volume != null) {
            return volume.isRemovable();
        }
        throw new IllegalArgumentException("Failed to find storage device at " + path);
    }

    public static boolean isExternalStorageEmulated() {
        File externalDir = sCurrentUser.getExternalDirs()[0];
        return isExternalStorageEmulated(externalDir);
    }

    public static boolean isExternalStorageEmulated(File path) {
        StorageVolume volume = StorageManager.getStorageVolume(path, UserHandle.myUserId());
        if (volume != null) {
            return volume.isEmulated();
        }
        throw new IllegalArgumentException("Failed to find storage device at " + path);
    }

    public static boolean isExternalStorageLegacy() {
        File externalDir = sCurrentUser.getExternalDirs()[0];
        return isExternalStorageLegacy(externalDir);
    }

    public static boolean isExternalStorageLegacy(File path) {
        Context context = AppGlobals.getInitialApplication();
        int uid = context.getApplicationInfo().uid;
        if (Process.isIsolated(uid) || Process.isSdkSandboxUid(uid)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.isInstantApp()) {
            return false;
        }
        String packageName = AppGlobals.getInitialPackage();
        try {
            PackageManager.Property noAppStorageProp = packageManager.getProperty(PackageManager.PROPERTY_NO_APP_DATA_STORAGE, packageName);
            if (noAppStorageProp != null) {
                if (noAppStorageProp.getBoolean()) {
                    return false;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        boolean defaultScopedStorage = Compatibility.isChangeEnabled((long) DEFAULT_SCOPED_STORAGE);
        boolean forceEnableScopedStorage = Compatibility.isChangeEnabled((long) FORCE_ENABLE_SCOPED_STORAGE);
        if (isScopedStorageEnforced(defaultScopedStorage, forceEnableScopedStorage)) {
            return false;
        }
        if (isScopedStorageDisabled(defaultScopedStorage, forceEnableScopedStorage)) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        String opPackageName = context.getOpPackageName();
        return appOps.noteOpNoThrow(87, uid, opPackageName) == 0 || appOps.noteOpNoThrow(99, uid, opPackageName) == 0;
    }

    private static boolean isScopedStorageEnforced(boolean defaultScopedStorage, boolean forceEnableScopedStorage) {
        return defaultScopedStorage && forceEnableScopedStorage;
    }

    private static boolean isScopedStorageDisabled(boolean defaultScopedStorage, boolean forceEnableScopedStorage) {
        return (defaultScopedStorage || forceEnableScopedStorage) ? false : true;
    }

    public static boolean isExternalStorageManager() {
        File externalDir = sCurrentUser.getExternalDirs()[0];
        return isExternalStorageManager(externalDir);
    }

    public static boolean isExternalStorageManager(File path) {
        Context context = (Context) Objects.requireNonNull(AppGlobals.getInitialApplication());
        String packageName = (String) Objects.requireNonNull(context.getPackageName());
        int uid = context.getApplicationInfo().uid;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        int opMode = appOps.checkOpNoThrow(92, uid, packageName);
        switch (opMode) {
            case 0:
                return true;
            case 1:
            case 2:
                return false;
            case 3:
                return context.checkPermission(Manifest.C0000permission.MANAGE_EXTERNAL_STORAGE, Process.myPid(), uid) == 0;
            default:
                throw new IllegalStateException("Unknown AppOpsManager mode " + opMode);
        }
    }

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    static String getDirectoryPath(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? defaultPath : path;
    }

    public static void setUserRequired(boolean userRequired) {
        sUserRequired = userRequired;
    }

    private static void throwIfUserRequired() {
        if (sUserRequired) {
            Log.wtf(TAG, "Path requests must specify a user by using UserEnvironment", new Throwable());
        }
    }

    public static File[] buildPaths(File[] base, String... segments) {
        File[] result = new File[base.length];
        for (int i = 0; i < base.length; i++) {
            result[i] = buildPath(base[i], segments);
        }
        return result;
    }

    public static File buildPath(File base, String... segments) {
        File file;
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                file = new File(segment);
            } else {
                file = new File(cur, segment);
            }
            cur = file;
        }
        return cur;
    }

    @Deprecated
    public static File maybeTranslateEmulatedPathToInternal(File path) {
        return StorageManager.maybeTranslateEmulatedPathToInternal(path);
    }
}
