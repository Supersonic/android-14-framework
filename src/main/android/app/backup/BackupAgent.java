package android.app.backup;

import android.app.IBackupAgent;
import android.app.QueuedWork;
import android.app.backup.BackupRestoreEventLogger;
import android.app.backup.FullBackup;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.p001pm.ApplicationInfo;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.infra.AndroidFuture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public abstract class BackupAgent extends ContextWrapper {
    private static final boolean DEBUG = false;
    private static final int DEFAULT_BACKUP_DESTINATION = 0;
    public static final int FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED = 1;
    public static final int FLAG_DEVICE_TO_DEVICE_TRANSFER = 2;
    public static final int FLAG_FAKE_CLIENT_SIDE_ENCRYPTION_ENABLED = Integer.MIN_VALUE;
    public static final int RESULT_ERROR = -1;
    public static final int RESULT_SUCCESS = 0;
    private static final String TAG = "BackupAgent";
    public static final int TYPE_DIRECTORY = 2;
    public static final int TYPE_EOF = 0;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_SYMLINK = 3;
    private volatile int mBackupDestination;
    private final IBinder mBinder;
    Handler mHandler;
    private volatile BackupRestoreEventLogger mLogger;
    private UserHandle mUser;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackupTransportFlags {
    }

    public abstract void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) throws IOException;

    public abstract void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException;

    Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        return this.mHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class SharedPrefsSynchronizer implements Runnable {
        public final CountDownLatch mLatch = new CountDownLatch(1);

        SharedPrefsSynchronizer() {
        }

        @Override // java.lang.Runnable
        public void run() {
            QueuedWork.waitToFinish();
            this.mLatch.countDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void waitForSharedPrefs() {
        Handler h = getHandler();
        SharedPrefsSynchronizer s = new SharedPrefsSynchronizer();
        h.postAtFrontOfQueue(s);
        try {
            s.mLatch.await();
        } catch (InterruptedException e) {
        }
    }

    public BackupRestoreEventLogger getBackupRestoreEventLogger() {
        return this.mLogger;
    }

    public BackupAgent() {
        super(null);
        this.mHandler = null;
        this.mLogger = null;
        this.mBackupDestination = 0;
        this.mBinder = new BackupServiceBinder().asBinder();
    }

    public void onCreate() {
    }

    public void onCreate(UserHandle user) {
        this.mUser = user;
        onCreate();
    }

    @Deprecated
    public void onCreate(UserHandle user, int backupDestination) {
        this.mBackupDestination = backupDestination;
        onCreate(user);
    }

    public void onCreate(UserHandle user, int backupDestination, int operationType) {
        this.mBackupDestination = backupDestination;
        this.mLogger = new BackupRestoreEventLogger(operationType);
        onCreate(user, backupDestination);
    }

    public void onDestroy() {
    }

    public void onRestore(BackupDataInput data, long appVersionCode, ParcelFileDescriptor newState) throws IOException {
        onRestore(data, (int) appVersionCode, newState);
    }

    public void onRestore(BackupDataInput data, long appVersionCode, ParcelFileDescriptor newState, Set<String> excludedKeys) throws IOException {
        onRestore(data, appVersionCode, newState);
    }

    public void onFullBackup(FullBackupDataOutput data) throws IOException {
        String str;
        FullBackup.BackupScheme backupScheme = FullBackup.getBackupScheme(this, this.mBackupDestination);
        if (!backupScheme.isFullBackupEnabled(data.getTransportFlags())) {
            return;
        }
        try {
            IncludeExcludeRules includeExcludeRules = getIncludeExcludeRules(backupScheme);
            Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> manifestIncludeMap = includeExcludeRules.getIncludeMap();
            Set<FullBackup.BackupScheme.PathWithRequiredFlags> manifestExcludeSet = includeExcludeRules.getExcludeSet();
            String packageName = getPackageName();
            ApplicationInfo appInfo = getApplicationInfo();
            Context ceContext = createCredentialProtectedStorageContext();
            String rootDir = ceContext.getDataDir().getCanonicalPath();
            String filesDir = ceContext.getFilesDir().getCanonicalPath();
            String databaseDir = ceContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            String sharedPrefsDir = ceContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            Context deContext = createDeviceProtectedStorageContext();
            String deviceRootDir = deContext.getDataDir().getCanonicalPath();
            String deviceFilesDir = deContext.getFilesDir().getCanonicalPath();
            String deviceDatabaseDir = deContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            String deviceSharedPrefsDir = deContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            String deviceRootDir2 = appInfo.nativeLibraryDir;
            if (deviceRootDir2 != null) {
                str = new File(appInfo.nativeLibraryDir).getCanonicalPath();
            } else {
                str = null;
            }
            String libDir = str;
            ArraySet<String> traversalExcludeSet = new ArraySet<>();
            traversalExcludeSet.add(filesDir);
            traversalExcludeSet.add(databaseDir);
            traversalExcludeSet.add(sharedPrefsDir);
            traversalExcludeSet.add(deviceFilesDir);
            traversalExcludeSet.add(deviceDatabaseDir);
            traversalExcludeSet.add(deviceSharedPrefsDir);
            if (libDir != null) {
                traversalExcludeSet.add(libDir);
            }
            Set<String> extraExcludedDirs = getExtraExcludeDirsIfAny(ceContext);
            Set<String> extraExcludedDeviceDirs = getExtraExcludeDirsIfAny(deContext);
            traversalExcludeSet.addAll(extraExcludedDirs);
            traversalExcludeSet.addAll(extraExcludedDeviceDirs);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, "r", manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(rootDir);
            traversalExcludeSet.addAll(extraExcludedDirs);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_ROOT_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(deviceRootDir);
            traversalExcludeSet.addAll(extraExcludedDeviceDirs);
            traversalExcludeSet.remove(filesDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.FILES_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(filesDir);
            traversalExcludeSet.remove(deviceFilesDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_FILES_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(deviceFilesDir);
            traversalExcludeSet.remove(databaseDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DATABASE_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(databaseDir);
            traversalExcludeSet.remove(deviceDatabaseDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_DATABASE_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(deviceDatabaseDir);
            traversalExcludeSet.remove(sharedPrefsDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.SHAREDPREFS_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(sharedPrefsDir);
            traversalExcludeSet.remove(deviceSharedPrefsDir);
            applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
            traversalExcludeSet.add(deviceSharedPrefsDir);
            if (Process.myUid() != 1000) {
                File efLocation = getExternalFilesDir(null);
                if (efLocation != null) {
                    applyXmlFiltersAndDoFullBackupForDomain(packageName, FullBackup.MANAGED_EXTERNAL_TREE_TOKEN, manifestIncludeMap, manifestExcludeSet, traversalExcludeSet, data);
                }
            }
        } catch (IOException | XmlPullParserException e) {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.m105v("BackupXmlParserLogging", "Exception trying to parse fullBackupContent xml file! Aborting full backup.", e);
            }
        }
    }

    private Set<String> getExtraExcludeDirsIfAny(Context context) throws IOException {
        Set<String> excludedDirs = new HashSet<>();
        excludedDirs.add(context.getCacheDir().getCanonicalPath());
        excludedDirs.add(context.getCodeCacheDir().getCanonicalPath());
        excludedDirs.add(context.getNoBackupFilesDir().getCanonicalPath());
        return Collections.unmodifiableSet(excludedDirs);
    }

    public IncludeExcludeRules getIncludeExcludeRules(FullBackup.BackupScheme backupScheme) throws IOException, XmlPullParserException {
        Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> manifestIncludeMap = backupScheme.maybeParseAndGetCanonicalIncludePaths();
        ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> manifestExcludeSet = backupScheme.maybeParseAndGetCanonicalExcludePaths();
        return new IncludeExcludeRules(manifestIncludeMap, manifestExcludeSet);
    }

    public void onQuotaExceeded(long backupDataBytes, long quotaBytes) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getBackupUserId() {
        UserHandle userHandle = this.mUser;
        return userHandle == null ? super.getUserId() : userHandle.getIdentifier();
    }

    private void applyXmlFiltersAndDoFullBackupForDomain(String packageName, String domainToken, Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> includeMap, Set<FullBackup.BackupScheme.PathWithRequiredFlags> filterSet, ArraySet<String> traversalExcludeSet, FullBackupDataOutput data) throws IOException {
        if (includeMap == null || includeMap.size() == 0) {
            fullBackupFileTree(packageName, domainToken, FullBackup.getBackupScheme(this, this.mBackupDestination).tokenToDirectoryPath(domainToken), filterSet, traversalExcludeSet, data);
        } else if (includeMap.get(domainToken) != null) {
            for (FullBackup.BackupScheme.PathWithRequiredFlags includeFile : includeMap.get(domainToken)) {
                if (areIncludeRequiredTransportFlagsSatisfied(includeFile.getRequiredFlags(), data.getTransportFlags())) {
                    fullBackupFileTree(packageName, domainToken, includeFile.getPath(), filterSet, traversalExcludeSet, data);
                }
            }
        }
    }

    private boolean areIncludeRequiredTransportFlagsSatisfied(int includeFlags, int transportFlags) {
        return (transportFlags & includeFlags) == includeFlags;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0119  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0123  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void fullBackupFile(File file, FullBackupDataOutput output) {
        String rootDir;
        String filesDir;
        String nbFilesDir;
        String dbDir;
        String spDir;
        String cacheDir;
        String codeCacheDir;
        String deviceRootDir;
        String deviceFilesDir;
        String deviceNbFilesDir;
        String deviceDbDir;
        String deviceSpDir;
        String deviceCacheDir;
        String deviceCodeCacheDir;
        String libDir;
        String efDir;
        String filePath;
        String domain;
        String rootpath;
        ApplicationInfo appInfo = getApplicationInfo();
        try {
            Context ceContext = createCredentialProtectedStorageContext();
            rootDir = ceContext.getDataDir().getCanonicalPath();
            filesDir = ceContext.getFilesDir().getCanonicalPath();
            nbFilesDir = ceContext.getNoBackupFilesDir().getCanonicalPath();
            dbDir = ceContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            spDir = ceContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            cacheDir = ceContext.getCacheDir().getCanonicalPath();
            codeCacheDir = ceContext.getCodeCacheDir().getCanonicalPath();
            Context deContext = createDeviceProtectedStorageContext();
            deviceRootDir = deContext.getDataDir().getCanonicalPath();
            deviceFilesDir = deContext.getFilesDir().getCanonicalPath();
            deviceNbFilesDir = deContext.getNoBackupFilesDir().getCanonicalPath();
            deviceDbDir = deContext.getDatabasePath("foo").getParentFile().getCanonicalPath();
            deviceSpDir = deContext.getSharedPreferencesPath("foo").getParentFile().getCanonicalPath();
            deviceCacheDir = deContext.getCacheDir().getCanonicalPath();
            deviceCodeCacheDir = deContext.getCodeCacheDir().getCanonicalPath();
            try {
                String efDir2 = appInfo.nativeLibraryDir;
                if (efDir2 == null) {
                    libDir = null;
                } else {
                    libDir = new File(appInfo.nativeLibraryDir).getCanonicalPath();
                }
            } catch (IOException e) {
            }
        } catch (IOException e2) {
        }
        try {
            if (Process.myUid() != 1000) {
                try {
                    File efLocation = getExternalFilesDir(null);
                    if (efLocation != null) {
                        String efDir3 = efLocation.getCanonicalPath();
                        efDir = efDir3;
                        filePath = file.getCanonicalPath();
                        if (!filePath.startsWith(cacheDir) && !filePath.startsWith(codeCacheDir) && !filePath.startsWith(nbFilesDir) && !filePath.startsWith(deviceCacheDir) && !filePath.startsWith(deviceCodeCacheDir) && !filePath.startsWith(deviceNbFilesDir) && !filePath.startsWith(libDir)) {
                            if (!filePath.startsWith(dbDir)) {
                                domain = FullBackup.DATABASE_TREE_TOKEN;
                                rootpath = dbDir;
                            } else if (filePath.startsWith(spDir)) {
                                domain = FullBackup.SHAREDPREFS_TREE_TOKEN;
                                rootpath = spDir;
                            } else if (filePath.startsWith(filesDir)) {
                                domain = FullBackup.FILES_TREE_TOKEN;
                                rootpath = filesDir;
                            } else if (filePath.startsWith(rootDir)) {
                                domain = "r";
                                rootpath = rootDir;
                            } else if (filePath.startsWith(deviceDbDir)) {
                                domain = FullBackup.DEVICE_DATABASE_TREE_TOKEN;
                                rootpath = deviceDbDir;
                            } else if (filePath.startsWith(deviceSpDir)) {
                                domain = FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN;
                                rootpath = deviceSpDir;
                            } else if (filePath.startsWith(deviceFilesDir)) {
                                domain = FullBackup.DEVICE_FILES_TREE_TOKEN;
                                rootpath = deviceFilesDir;
                            } else if (filePath.startsWith(deviceRootDir)) {
                                domain = FullBackup.DEVICE_ROOT_TREE_TOKEN;
                                rootpath = deviceRootDir;
                            } else if (efDir == null || !filePath.startsWith(efDir)) {
                                Log.m104w(TAG, "File " + filePath + " is in an unsupported location; skipping");
                                return;
                            } else {
                                domain = FullBackup.MANAGED_EXTERNAL_TREE_TOKEN;
                                rootpath = efDir;
                            }
                            FullBackup.backupToTar(getPackageName(), domain, null, rootpath, filePath, output);
                            return;
                        }
                        Log.m104w(TAG, "lib, cache, code_cache, and no_backup files are not backed up");
                        return;
                    }
                } catch (IOException e3) {
                    Log.m104w(TAG, "Unable to obtain canonical paths");
                    return;
                }
            }
            filePath = file.getCanonicalPath();
            if (!filePath.startsWith(cacheDir)) {
                if (!filePath.startsWith(dbDir)) {
                }
                FullBackup.backupToTar(getPackageName(), domain, null, rootpath, filePath, output);
                return;
            }
            Log.m104w(TAG, "lib, cache, code_cache, and no_backup files are not backed up");
            return;
        } catch (IOException e4) {
            Log.m104w(TAG, "Unable to obtain canonical paths");
            return;
        }
        efDir = null;
    }

    protected final void fullBackupFileTree(String packageName, String domain, String startingPath, Set<FullBackup.BackupScheme.PathWithRequiredFlags> manifestExcludes, ArraySet<String> systemExcludes, FullBackupDataOutput output) {
        File file;
        File file2;
        StructStat stat;
        File[] contents;
        BackupAgent backupAgent = this;
        String domainPath = FullBackup.getBackupScheme(backupAgent, backupAgent.mBackupDestination).tokenToDirectoryPath(domain);
        if (domainPath == null) {
            return;
        }
        File rootFile = new File(startingPath);
        if (rootFile.exists()) {
            LinkedList<File> scanQueue = new LinkedList<>();
            scanQueue.add(rootFile);
            while (scanQueue.size() > 0) {
                File file3 = scanQueue.remove(0);
                try {
                    stat = Os.lstat(file3.getPath());
                } catch (ErrnoException e) {
                    e = e;
                    file2 = file3;
                } catch (IOException e2) {
                    file = file3;
                }
                if (!OsConstants.S_ISREG(stat.st_mode)) {
                    try {
                    } catch (ErrnoException e3) {
                        e = e3;
                        file2 = file3;
                        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                            Log.m106v("BackupXmlParserLogging", "Error scanning file " + file2 + " : " + e);
                        }
                        backupAgent = this;
                    } catch (IOException e4) {
                        file = file3;
                        if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                            Log.m106v("BackupXmlParserLogging", "Error canonicalizing path of " + file);
                        }
                        backupAgent = this;
                    }
                    if (!OsConstants.S_ISDIR(stat.st_mode)) {
                    }
                }
                String filePath = file3.getCanonicalPath();
                if (manifestExcludes == null || !backupAgent.manifestExcludesContainFilePath(manifestExcludes, filePath)) {
                    if (systemExcludes == null || !systemExcludes.contains(filePath)) {
                        if (OsConstants.S_ISDIR(stat.st_mode) && (contents = file3.listFiles()) != null) {
                            for (File entry : contents) {
                                scanQueue.add(0, entry);
                            }
                        }
                        FullBackup.backupToTar(packageName, domain, null, domainPath, filePath, output);
                        backupAgent = this;
                    }
                }
            }
        }
    }

    private boolean manifestExcludesContainFilePath(Set<FullBackup.BackupScheme.PathWithRequiredFlags> manifestExcludes, String filePath) {
        for (FullBackup.BackupScheme.PathWithRequiredFlags exclude : manifestExcludes) {
            String excludePath = exclude.getPath();
            if (excludePath != null && excludePath.equals(filePath)) {
                return true;
            }
        }
        return false;
    }

    public void onRestoreFile(ParcelFileDescriptor data, long size, File destination, int type, long mode, long mtime) throws IOException {
        boolean accept = isFileEligibleForRestore(destination);
        FullBackup.restoreFile(data, size, type, mode, mtime, accept ? destination : null);
    }

    private boolean isFileEligibleForRestore(File destination) throws IOException {
        FullBackup.BackupScheme bs = FullBackup.getBackupScheme(this, this.mBackupDestination);
        if (!bs.isFullRestoreEnabled()) {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.m106v("BackupXmlParserLogging", "onRestoreFile \"" + destination.getCanonicalPath() + "\" : fullBackupContent not enabled for " + getPackageName());
            }
            return false;
        }
        String destinationCanonicalPath = destination.getCanonicalPath();
        try {
            Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> includes = bs.maybeParseAndGetCanonicalIncludePaths();
            ArraySet<FullBackup.BackupScheme.PathWithRequiredFlags> excludes = bs.maybeParseAndGetCanonicalExcludePaths();
            if (excludes != null && BackupUtils.isFileSpecifiedInPathList(destination, excludes)) {
                if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                    Log.m106v("BackupXmlParserLogging", "onRestoreFile: \"" + destinationCanonicalPath + "\": listed in excludes; skipping.");
                }
                return false;
            } else if (includes != null && !includes.isEmpty()) {
                boolean explicitlyIncluded = false;
                for (Set<FullBackup.BackupScheme.PathWithRequiredFlags> domainIncludes : includes.values()) {
                    explicitlyIncluded |= BackupUtils.isFileSpecifiedInPathList(destination, domainIncludes);
                    if (explicitlyIncluded) {
                        break;
                    }
                }
                if (!explicitlyIncluded) {
                    if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                        Log.m106v("BackupXmlParserLogging", "onRestoreFile: Trying to restore \"" + destinationCanonicalPath + "\" but it isn't specified in the included files; skipping.");
                    }
                    return false;
                }
                return true;
            } else {
                return true;
            }
        } catch (XmlPullParserException e) {
            if (Log.isLoggable("BackupXmlParserLogging", 2)) {
                Log.m105v("BackupXmlParserLogging", "onRestoreFile \"" + destinationCanonicalPath + "\" : Exception trying to parse fullBackupContent xml file! Aborting onRestoreFile.", e);
            }
            return false;
        }
    }

    protected void onRestoreFile(ParcelFileDescriptor data, long size, int type, String domain, String path, long mode, long mtime) throws IOException {
        long mode2;
        String basePath = FullBackup.getBackupScheme(this, this.mBackupDestination).tokenToDirectoryPath(domain);
        if (!domain.equals(FullBackup.MANAGED_EXTERNAL_TREE_TOKEN)) {
            mode2 = mode;
        } else {
            mode2 = -1;
        }
        if (basePath != null) {
            File outFile = new File(basePath, path);
            String outPath = outFile.getCanonicalPath();
            if (outPath.startsWith(basePath + File.separatorChar)) {
                onRestoreFile(data, size, outFile, type, mode2, mtime);
                return;
            }
        }
        FullBackup.restoreFile(data, size, type, mode2, mtime, null);
    }

    public void onRestoreFinished() {
    }

    public final void clearBackupRestoreEventLogger() {
        if (this.mLogger != null) {
            this.mLogger.clearData();
        }
    }

    public final IBinder onBind() {
        return this.mBinder;
    }

    public void attach(Context context) {
        attachBaseContext(context);
    }

    /* loaded from: classes.dex */
    private class BackupServiceBinder extends IBackupAgent.Stub {
        private static final String TAG = "BackupServiceBinder";

        private BackupServiceBinder() {
        }

        /* JADX WARN: Removed duplicated region for block: B:40:0x00cf  */
        @Override // android.app.IBackupAgent
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void doBackup(ParcelFileDescriptor oldState, ParcelFileDescriptor data, ParcelFileDescriptor newState, long quotaBytes, IBackupCallback callbackBinder, int transportFlags) throws RemoteException {
            IOException iOException;
            BackupDataOutput output = new BackupDataOutput(data.getFileDescriptor(), quotaBytes, transportFlags);
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                } catch (Throwable ex) {
                    iOException = ex;
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.operationComplete(-1L);
                    } catch (RemoteException e) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(oldState);
                        IoUtils.closeQuietly(data);
                        IoUtils.closeQuietly(newState);
                    }
                    throw iOException;
                }
                try {
                    BackupAgent.this.onBackup(oldState, output, newState);
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.operationComplete(0L);
                    } catch (RemoteException e2) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(oldState);
                        IoUtils.closeQuietly(data);
                        IoUtils.closeQuietly(newState);
                    }
                } catch (IOException e3) {
                    ex = e3;
                    Log.m111d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", ex);
                    throw new RuntimeException(ex);
                } catch (RuntimeException e4) {
                    ex = e4;
                    Log.m111d(TAG, "onBackup (" + BackupAgent.this.getClass().getName() + ") threw", ex);
                    throw ex;
                } catch (Throwable th) {
                    th = th;
                    iOException = th;
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    callbackBinder.operationComplete(-1L);
                    if (Binder.getCallingPid() != Process.myPid()) {
                    }
                    throw iOException;
                }
            } catch (IOException e5) {
                ex = e5;
            } catch (RuntimeException e6) {
                ex = e6;
            } catch (Throwable th2) {
                th = th2;
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestore(ParcelFileDescriptor data, long appVersionCode, ParcelFileDescriptor newState, int token, IBackupManager callbackBinder) throws RemoteException {
            doRestoreInternal(data, appVersionCode, newState, token, callbackBinder, null);
        }

        @Override // android.app.IBackupAgent
        public void doRestoreWithExcludedKeys(ParcelFileDescriptor data, long appVersionCode, ParcelFileDescriptor newState, int token, IBackupManager callbackBinder, List<String> excludedKeys) throws RemoteException {
            doRestoreInternal(data, appVersionCode, newState, token, callbackBinder, excludedKeys);
        }

        /* JADX WARN: Removed duplicated region for block: B:46:0x00e8  */
        /* JADX WARN: Type inference failed for: r4v0, types: [java.util.Collection] */
        /* JADX WARN: Type inference failed for: r4v1 */
        /* JADX WARN: Type inference failed for: r4v6 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private void doRestoreInternal(ParcelFileDescriptor data, long appVersionCode, ParcelFileDescriptor newState, int token, IBackupManager callbackBinder, List<String> excludedKeys) throws RemoteException {
            IOException iOException;
            long j;
            String str;
            String str2;
            BackupAgent backupAgent;
            Set<String> hashSet;
            ?? r4 = excludedKeys;
            BackupAgent.this.waitForSharedPrefs();
            BackupDataInput input = new BackupDataInput(data.getFileDescriptor());
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    try {
                        try {
                            backupAgent = BackupAgent.this;
                            if (r4 != 0) {
                                try {
                                    hashSet = new HashSet<>((Collection<? extends String>) r4);
                                } catch (Throwable th) {
                                    iOException = th;
                                    j = 0;
                                    BackupAgent.this.reloadSharedPreferences();
                                    Binder.restoreCallingIdentity(ident);
                                    try {
                                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, j);
                                    } catch (RemoteException e) {
                                    }
                                    if (Binder.getCallingPid() != Process.myPid()) {
                                    }
                                    throw iOException;
                                }
                            } else {
                                try {
                                    hashSet = Collections.emptySet();
                                } catch (IOException e2) {
                                    ex = e2;
                                    str2 = ") threw";
                                    str = str2;
                                    Log.m111d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + str, ex);
                                    throw new RuntimeException(ex);
                                }
                            }
                            str2 = ") threw";
                        } catch (Throwable th2) {
                            ex = th2;
                            r4 = 0;
                            iOException = ex;
                            j = r4;
                            BackupAgent.this.reloadSharedPreferences();
                            Binder.restoreCallingIdentity(ident);
                            callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, j);
                            if (Binder.getCallingPid() != Process.myPid()) {
                                IoUtils.closeQuietly(data);
                                IoUtils.closeQuietly(newState);
                            }
                            throw iOException;
                        }
                    } catch (RuntimeException e3) {
                        ex = e3;
                        str2 = ") threw";
                    }
                } catch (IOException e4) {
                    ex = e4;
                    str = ") threw";
                }
                try {
                    backupAgent.onRestore(input, appVersionCode, newState, hashSet);
                    BackupAgent.this.reloadSharedPreferences();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                    } catch (RemoteException e5) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(data);
                        IoUtils.closeQuietly(newState);
                    }
                } catch (IOException e6) {
                    ex = e6;
                    str = str2;
                    Log.m111d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + str, ex);
                    throw new RuntimeException(ex);
                } catch (RuntimeException e7) {
                    ex = e7;
                    Log.m111d(TAG, "onRestore (" + BackupAgent.this.getClass().getName() + str2, ex);
                    throw ex;
                }
            } catch (Throwable th3) {
                ex = th3;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:47:0x00fa  */
        @Override // android.app.IBackupAgent
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void doFullBackup(ParcelFileDescriptor data, long quotaBytes, int token, IBackupManager callbackBinder, int transportFlags) {
            BackupAgent.this.waitForSharedPrefs();
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    try {
                        BackupAgent.this.onFullBackup(new FullBackupDataOutput(data, quotaBytes, transportFlags));
                        BackupAgent.this.waitForSharedPrefs();
                        try {
                            FileOutputStream out = new FileOutputStream(data.getFileDescriptor());
                            byte[] buf = new byte[4];
                            out.write(buf);
                        } catch (IOException e) {
                            Log.m110e(TAG, "Unable to finalize backup stream!");
                        }
                        Binder.restoreCallingIdentity(ident);
                        try {
                            callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                        } catch (RemoteException e2) {
                        }
                        if (Binder.getCallingPid() != Process.myPid()) {
                            IoUtils.closeQuietly(data);
                        }
                    } catch (IOException e3) {
                        ex = e3;
                        Log.m111d(TAG, "onFullBackup (" + BackupAgent.this.getClass().getName() + ") threw", ex);
                        throw new RuntimeException(ex);
                    } catch (RuntimeException e4) {
                        ex = e4;
                        Log.m111d(TAG, "onFullBackup (" + BackupAgent.this.getClass().getName() + ") threw", ex);
                        throw ex;
                    }
                } catch (Throwable th) {
                    th = th;
                    Throwable th2 = th;
                    BackupAgent.this.waitForSharedPrefs();
                    try {
                        FileOutputStream out2 = new FileOutputStream(data.getFileDescriptor());
                        byte[] buf2 = new byte[4];
                        out2.write(buf2);
                    } catch (IOException e5) {
                        Log.m110e(TAG, "Unable to finalize backup stream!");
                    }
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                    } catch (RemoteException e6) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(data);
                    }
                    throw th2;
                }
            } catch (IOException e7) {
                ex = e7;
            } catch (RuntimeException e8) {
                ex = e8;
            } catch (Throwable th3) {
                th = th3;
                Throwable th22 = th;
                BackupAgent.this.waitForSharedPrefs();
                FileOutputStream out22 = new FileOutputStream(data.getFileDescriptor());
                byte[] buf22 = new byte[4];
                out22.write(buf22);
                Binder.restoreCallingIdentity(ident);
                callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                if (Binder.getCallingPid() != Process.myPid()) {
                }
                throw th22;
            }
        }

        @Override // android.app.IBackupAgent
        public void doMeasureFullBackup(long quotaBytes, int token, IBackupManager callbackBinder, int transportFlags) {
            FullBackupDataOutput measureOutput = new FullBackupDataOutput(quotaBytes, transportFlags);
            BackupAgent.this.waitForSharedPrefs();
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onFullBackup(measureOutput);
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, measureOutput.getSize());
                    } catch (RemoteException e) {
                    }
                } catch (IOException ex) {
                    Log.m111d(TAG, "onFullBackup[M] (" + BackupAgent.this.getClass().getName() + ") threw", ex);
                    throw new RuntimeException(ex);
                } catch (RuntimeException ex2) {
                    Log.m111d(TAG, "onFullBackup[M] (" + BackupAgent.this.getClass().getName() + ") threw", ex2);
                    throw ex2;
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                try {
                    callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, measureOutput.getSize());
                } catch (RemoteException e2) {
                }
                throw th;
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestoreFile(ParcelFileDescriptor data, long size, int type, String domain, String path, long mode, long mtime, int token, IBackupManager callbackBinder) throws RemoteException {
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onRestoreFile(data, size, type, domain, path, mode, mtime);
                    BackupAgent.this.waitForSharedPrefs();
                    BackupAgent.this.reloadSharedPreferences();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                    } catch (RemoteException e) {
                    }
                    if (Binder.getCallingPid() != Process.myPid()) {
                        IoUtils.closeQuietly(data);
                    }
                } catch (IOException e2) {
                    Log.m111d(TAG, "onRestoreFile (" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw new RuntimeException(e2);
                }
            } catch (Throwable th) {
                BackupAgent.this.waitForSharedPrefs();
                BackupAgent.this.reloadSharedPreferences();
                Binder.restoreCallingIdentity(ident);
                try {
                    callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                } catch (RemoteException e3) {
                }
                if (Binder.getCallingPid() != Process.myPid()) {
                    IoUtils.closeQuietly(data);
                }
                throw th;
            }
        }

        @Override // android.app.IBackupAgent
        public void doRestoreFinished(int token, IBackupManager callbackBinder) {
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onRestoreFinished();
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                    } catch (RemoteException e) {
                    }
                } catch (Throwable th) {
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.opCompleteForUser(BackupAgent.this.getBackupUserId(), token, 0L);
                    } catch (RemoteException e2) {
                    }
                    throw th;
                }
            } catch (Exception e3) {
                Log.m111d(TAG, "onRestoreFinished (" + BackupAgent.this.getClass().getName() + ") threw", e3);
                throw e3;
            }
        }

        @Override // android.app.IBackupAgent
        public void fail(String message) {
            BackupAgent.this.getHandler().post(new FailRunnable(message));
        }

        @Override // android.app.IBackupAgent
        public void doQuotaExceeded(long backupDataBytes, long quotaBytes, IBackupCallback callbackBinder) {
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.onQuotaExceeded(backupDataBytes, quotaBytes);
                    BackupAgent.this.waitForSharedPrefs();
                    Binder.restoreCallingIdentity(ident);
                    try {
                        callbackBinder.operationComplete(0L);
                    } catch (RemoteException e) {
                    }
                } catch (Exception e2) {
                    Log.m111d(TAG, "onQuotaExceeded(" + BackupAgent.this.getClass().getName() + ") threw", e2);
                    throw e2;
                }
            } catch (Throwable th) {
                BackupAgent.this.waitForSharedPrefs();
                Binder.restoreCallingIdentity(ident);
                try {
                    callbackBinder.operationComplete(-1L);
                } catch (RemoteException e3) {
                }
                throw th;
            }
        }

        @Override // android.app.IBackupAgent
        public void getLoggerResults(AndroidFuture<List<BackupRestoreEventLogger.DataTypeResult>> in) {
            if (BackupAgent.this.mLogger != null) {
                in.complete(BackupAgent.this.mLogger.getLoggingResults());
            } else {
                in.complete(Collections.emptyList());
            }
        }

        @Override // android.app.IBackupAgent
        public void clearBackupRestoreEventLogger() {
            long ident = Binder.clearCallingIdentity();
            try {
                try {
                    BackupAgent.this.clearBackupRestoreEventLogger();
                } catch (Exception e) {
                    Log.m111d(TAG, "clearBackupRestoreEventLogger (" + BackupAgent.this.getClass().getName() + ") threw", e);
                    throw e;
                }
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* loaded from: classes.dex */
    static class FailRunnable implements Runnable {
        private String mMessage;

        FailRunnable(String message) {
            this.mMessage = message;
        }

        @Override // java.lang.Runnable
        public void run() {
            throw new IllegalStateException(this.mMessage);
        }
    }

    /* loaded from: classes.dex */
    public static class IncludeExcludeRules {
        private final Set<FullBackup.BackupScheme.PathWithRequiredFlags> mManifestExcludeSet;
        private final Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> mManifestIncludeMap;

        public IncludeExcludeRules(Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> manifestIncludeMap, Set<FullBackup.BackupScheme.PathWithRequiredFlags> manifestExcludeSet) {
            this.mManifestIncludeMap = manifestIncludeMap;
            this.mManifestExcludeSet = manifestExcludeSet;
        }

        public static IncludeExcludeRules emptyRules() {
            return new IncludeExcludeRules(Collections.emptyMap(), new ArraySet());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Map<String, Set<FullBackup.BackupScheme.PathWithRequiredFlags>> getIncludeMap() {
            return this.mManifestIncludeMap;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Set<FullBackup.BackupScheme.PathWithRequiredFlags> getExcludeSet() {
            return this.mManifestExcludeSet;
        }

        public int hashCode() {
            return Objects.hash(this.mManifestIncludeMap, this.mManifestExcludeSet);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            IncludeExcludeRules that = (IncludeExcludeRules) object;
            if (Objects.equals(this.mManifestIncludeMap, that.mManifestIncludeMap) && Objects.equals(this.mManifestExcludeSet, that.mManifestExcludeSet)) {
                return true;
            }
            return false;
        }
    }
}
