package android.app.backup;

import android.app.compat.CompatChanges;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.p008os.storage.StorageManager;
import android.p008os.storage.StorageVolume;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class FullBackup {
    public static final String APK_TREE_TOKEN = "a";
    public static final String APPS_PREFIX = "apps/";
    public static final String CACHE_TREE_TOKEN = "c";
    public static final String CONF_TOKEN_INTENT_EXTRA = "conftoken";
    public static final String DATABASE_TREE_TOKEN = "db";
    public static final String DEVICE_CACHE_TREE_TOKEN = "d_c";
    public static final String DEVICE_DATABASE_TREE_TOKEN = "d_db";
    public static final String DEVICE_FILES_TREE_TOKEN = "d_f";
    public static final String DEVICE_NO_BACKUP_TREE_TOKEN = "d_nb";
    public static final String DEVICE_ROOT_TREE_TOKEN = "d_r";
    public static final String DEVICE_SHAREDPREFS_TREE_TOKEN = "d_sp";
    public static final String FILES_TREE_TOKEN = "f";
    private static final String FLAG_DISABLE_IF_NO_ENCRYPTION_CAPABILITIES = "disableIfNoEncryptionCapabilities";
    public static final String FLAG_REQUIRED_CLIENT_SIDE_ENCRYPTION = "clientSideEncryption";
    public static final String FLAG_REQUIRED_DEVICE_TO_DEVICE_TRANSFER = "deviceToDeviceTransfer";
    public static final String FLAG_REQUIRED_FAKE_CLIENT_SIDE_ENCRYPTION = "fakeClientSideEncryption";
    public static final String FULL_BACKUP_INTENT_ACTION = "fullback";
    public static final String FULL_RESTORE_INTENT_ACTION = "fullrest";
    private static final long IGNORE_FULL_BACKUP_CONTENT_IN_D2D = 180523564;
    public static final String KEY_VALUE_DATA_TOKEN = "k";
    public static final String MANAGED_EXTERNAL_TREE_TOKEN = "ef";
    public static final String NO_BACKUP_TREE_TOKEN = "nb";
    public static final String OBB_TREE_TOKEN = "obb";
    public static final String ROOT_TREE_TOKEN = "r";
    public static final String SHAREDPREFS_TREE_TOKEN = "sp";
    public static final String SHARED_PREFIX = "shared/";
    public static final String SHARED_STORAGE_TOKEN = "shared";
    static final String TAG = "FullBackup";
    static final String TAG_XML_PARSER = "BackupXmlParserLogging";
    private static final Map<BackupSchemeId, BackupScheme> kPackageBackupSchemeMap = new ArrayMap();

    /* loaded from: classes.dex */
    @interface ConfigSection {
        public static final String CLOUD_BACKUP = "cloud-backup";
        public static final String DEVICE_TRANSFER = "device-transfer";
    }

    public static native int backupToTar(String str, String str2, String str3, String str4, String str5, FullBackupDataOutput fullBackupDataOutput);

    /* loaded from: classes.dex */
    private static class BackupSchemeId {
        final int mBackupDestination;
        final String mPackageName;

        BackupSchemeId(String packageName, int backupDestination) {
            this.mPackageName = packageName;
            this.mBackupDestination = backupDestination;
        }

        public int hashCode() {
            return Objects.hash(this.mPackageName, Integer.valueOf(this.mBackupDestination));
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            BackupSchemeId that = (BackupSchemeId) object;
            if (Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(Integer.valueOf(this.mBackupDestination), Integer.valueOf(that.mBackupDestination))) {
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized BackupScheme getBackupScheme(Context context, int backupDestination) {
        BackupScheme backupSchemeForPackage;
        synchronized (FullBackup.class) {
            BackupSchemeId backupSchemeId = new BackupSchemeId(context.getPackageName(), backupDestination);
            Map<BackupSchemeId, BackupScheme> map = kPackageBackupSchemeMap;
            backupSchemeForPackage = map.get(backupSchemeId);
            if (backupSchemeForPackage == null) {
                backupSchemeForPackage = new BackupScheme(context, backupDestination);
                map.put(backupSchemeId, backupSchemeForPackage);
            }
        }
        return backupSchemeForPackage;
    }

    public static BackupScheme getBackupSchemeForTest(Context context) {
        BackupScheme testing = new BackupScheme(context, 0);
        testing.mExcludes = new ArraySet<>();
        testing.mIncludes = new ArrayMap();
        return testing;
    }

    public static void restoreFile(ParcelFileDescriptor data, long size, int type, long mode, long mtime, File outFile) throws IOException {
        long j = 0;
        if (type == 2) {
            if (outFile != null) {
                outFile.mkdirs();
            }
        } else {
            FileOutputStream out = null;
            if (outFile != null) {
                try {
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    out = new FileOutputStream(outFile);
                } catch (IOException e) {
                    Log.m109e(TAG, "Unable to create/open file " + outFile.getPath(), e);
                }
            }
            byte[] buffer = new byte[32768];
            FileInputStream in = new FileInputStream(data.getFileDescriptor());
            long size2 = size;
            while (true) {
                if (size2 <= j) {
                    break;
                }
                int toRead = size2 > ((long) buffer.length) ? buffer.length : (int) size2;
                int got = in.read(buffer, 0, toRead);
                if (got <= 0) {
                    Log.m104w(TAG, "Incomplete read: expected " + size2 + " but got " + (size - size2));
                    break;
                }
                if (out != null) {
                    try {
                        out.write(buffer, 0, got);
                    } catch (IOException e2) {
                        Log.m109e(TAG, "Unable to write to file " + outFile.getPath(), e2);
                        out.close();
                        outFile.delete();
                        out = null;
                    }
                }
                size2 -= got;
                j = 0;
            }
            if (out != null) {
                out.close();
            }
        }
        if (mode < 0 || outFile == null) {
            return;
        }
        try {
            Os.chmod(outFile.getPath(), (int) (mode & 448));
        } catch (ErrnoException e3) {
            e3.rethrowAsIOException();
        }
        outFile.setLastModified(mtime);
    }

    /* loaded from: classes.dex */
    public static class BackupScheme {
        private static final String TAG_EXCLUDE = "exclude";
        private static final String TAG_INCLUDE = "include";
        private final File CACHE_DIR;
        private final File DATABASE_DIR;
        private final File DEVICE_CACHE_DIR;
        private final File DEVICE_DATABASE_DIR;
        private final File DEVICE_FILES_DIR;
        private final File DEVICE_NOBACKUP_DIR;
        private final File DEVICE_ROOT_DIR;
        private final File DEVICE_SHAREDPREF_DIR;
        private final File EXTERNAL_DIR;
        private final File FILES_DIR;
        private final File NOBACKUP_DIR;
        private final File ROOT_DIR;
        private final File SHAREDPREF_DIR;
        final int mBackupDestination;
        final int mDataExtractionRules;
        ArraySet<PathWithRequiredFlags> mExcludes;
        final int mFullBackupContent;
        Map<String, Set<PathWithRequiredFlags>> mIncludes;
        private Boolean mIsUsingNewScheme;
        final PackageManager mPackageManager;
        final String mPackageName;
        private Integer mRequiredTransportFlags;
        final StorageManager mStorageManager;
        private StorageVolume[] mVolumes = null;

        /* JADX INFO: Access modifiers changed from: package-private */
        public String tokenToDirectoryPath(String domainToken) {
            try {
                if (domainToken.equals(FullBackup.FILES_TREE_TOKEN)) {
                    return this.FILES_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DATABASE_TREE_TOKEN)) {
                    return this.DATABASE_DIR.getCanonicalPath();
                }
                if (domainToken.equals("r")) {
                    return this.ROOT_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.SHAREDPREFS_TREE_TOKEN)) {
                    return this.SHAREDPREF_DIR.getCanonicalPath();
                }
                if (domainToken.equals("c")) {
                    return this.CACHE_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.NO_BACKUP_TREE_TOKEN)) {
                    return this.NOBACKUP_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_FILES_TREE_TOKEN)) {
                    return this.DEVICE_FILES_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_DATABASE_TREE_TOKEN)) {
                    return this.DEVICE_DATABASE_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_ROOT_TREE_TOKEN)) {
                    return this.DEVICE_ROOT_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN)) {
                    return this.DEVICE_SHAREDPREF_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_CACHE_TREE_TOKEN)) {
                    return this.DEVICE_CACHE_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.DEVICE_NO_BACKUP_TREE_TOKEN)) {
                    return this.DEVICE_NOBACKUP_DIR.getCanonicalPath();
                }
                if (domainToken.equals(FullBackup.MANAGED_EXTERNAL_TREE_TOKEN)) {
                    File file = this.EXTERNAL_DIR;
                    if (file != null) {
                        return file.getCanonicalPath();
                    }
                    return null;
                } else if (!domainToken.startsWith(FullBackup.SHARED_PREFIX)) {
                    Log.m108i(FullBackup.TAG, "Unrecognized domain " + domainToken);
                    return null;
                } else {
                    return sharedDomainToPath(domainToken);
                }
            } catch (Exception e) {
                Log.m108i(FullBackup.TAG, "Error reading directory for domain: " + domainToken);
                return null;
            }
        }

        private String sharedDomainToPath(String domain) throws IOException {
            String volume = domain.substring(FullBackup.SHARED_PREFIX.length());
            StorageVolume[] volumes = getVolumeList();
            int volNum = Integer.parseInt(volume);
            if (volNum < this.mVolumes.length) {
                return volumes[volNum].getPathFile().getCanonicalPath();
            }
            return null;
        }

        private StorageVolume[] getVolumeList() {
            StorageManager storageManager = this.mStorageManager;
            if (storageManager != null) {
                if (this.mVolumes == null) {
                    this.mVolumes = storageManager.getVolumeList();
                }
            } else {
                Log.m110e(FullBackup.TAG, "Unable to access Storage Manager");
            }
            return this.mVolumes;
        }

        /* loaded from: classes.dex */
        public static class PathWithRequiredFlags {
            private final String mPath;
            private final int mRequiredFlags;

            public PathWithRequiredFlags(String path, int requiredFlags) {
                this.mPath = path;
                this.mRequiredFlags = requiredFlags;
            }

            public String getPath() {
                return this.mPath;
            }

            public int getRequiredFlags() {
                return this.mRequiredFlags;
            }
        }

        BackupScheme(Context context, int backupDestination) {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            this.mDataExtractionRules = applicationInfo.dataExtractionRulesRes;
            this.mFullBackupContent = applicationInfo.fullBackupContent;
            this.mBackupDestination = backupDestination;
            this.mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            this.mPackageManager = context.getPackageManager();
            this.mPackageName = context.getPackageName();
            Context ceContext = context.createCredentialProtectedStorageContext();
            this.FILES_DIR = ceContext.getFilesDir();
            this.DATABASE_DIR = ceContext.getDatabasePath("foo").getParentFile();
            this.ROOT_DIR = ceContext.getDataDir();
            this.SHAREDPREF_DIR = ceContext.getSharedPreferencesPath("foo").getParentFile();
            this.CACHE_DIR = ceContext.getCacheDir();
            this.NOBACKUP_DIR = ceContext.getNoBackupFilesDir();
            Context deContext = context.createDeviceProtectedStorageContext();
            this.DEVICE_FILES_DIR = deContext.getFilesDir();
            this.DEVICE_DATABASE_DIR = deContext.getDatabasePath("foo").getParentFile();
            this.DEVICE_ROOT_DIR = deContext.getDataDir();
            this.DEVICE_SHAREDPREF_DIR = deContext.getSharedPreferencesPath("foo").getParentFile();
            this.DEVICE_CACHE_DIR = deContext.getCacheDir();
            this.DEVICE_NOBACKUP_DIR = deContext.getNoBackupFilesDir();
            if (Process.myUid() != 1000) {
                this.EXTERNAL_DIR = context.getExternalFilesDir(null);
            } else {
                this.EXTERNAL_DIR = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isFullBackupEnabled(int transportFlags) {
            try {
                if (isUsingNewScheme()) {
                    int requiredTransportFlags = getRequiredTransportFlags();
                    return (transportFlags & requiredTransportFlags) == requiredTransportFlags;
                }
                return isFullBackupContentEnabled();
            } catch (IOException | XmlPullParserException e) {
                Slog.m90w(FullBackup.TAG, "Failed to interpret the backup scheme: " + e);
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isFullRestoreEnabled() {
            try {
                if (isUsingNewScheme()) {
                    return true;
                }
                return isFullBackupContentEnabled();
            } catch (IOException | XmlPullParserException e) {
                Slog.m90w(FullBackup.TAG, "Failed to interpret the backup scheme: " + e);
                return false;
            }
        }

        boolean isFullBackupContentEnabled() {
            if (this.mFullBackupContent < 0) {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "android:fullBackupContent - \"false\"");
                    return false;
                }
                return false;
            }
            return true;
        }

        public synchronized Map<String, Set<PathWithRequiredFlags>> maybeParseAndGetCanonicalIncludePaths() throws IOException, XmlPullParserException {
            if (this.mIncludes == null) {
                maybeParseBackupSchemeLocked();
            }
            return this.mIncludes;
        }

        public synchronized ArraySet<PathWithRequiredFlags> maybeParseAndGetCanonicalExcludePaths() throws IOException, XmlPullParserException {
            if (this.mExcludes == null) {
                maybeParseBackupSchemeLocked();
            }
            return this.mExcludes;
        }

        public synchronized int getRequiredTransportFlags() throws IOException, XmlPullParserException {
            if (this.mRequiredTransportFlags == null) {
                maybeParseBackupSchemeLocked();
            }
            return this.mRequiredTransportFlags.intValue();
        }

        private synchronized boolean isUsingNewScheme() throws IOException, XmlPullParserException {
            if (this.mIsUsingNewScheme == null) {
                maybeParseBackupSchemeLocked();
            }
            return this.mIsUsingNewScheme.booleanValue();
        }

        private void maybeParseBackupSchemeLocked() throws IOException, XmlPullParserException {
            this.mIncludes = new ArrayMap();
            this.mExcludes = new ArraySet<>();
            this.mRequiredTransportFlags = 0;
            this.mIsUsingNewScheme = false;
            if (this.mFullBackupContent == 0 && this.mDataExtractionRules == 0) {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "android:fullBackupContent - \"true\"");
                    return;
                }
                return;
            }
            if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                Log.m106v(FullBackup.TAG_XML_PARSER, "Found xml scheme: android:fullBackupContent=" + this.mFullBackupContent + "; android:dataExtractionRules=" + this.mDataExtractionRules);
            }
            try {
                parseSchemeForBackupDestination(this.mBackupDestination);
            } catch (PackageManager.NameNotFoundException e) {
                throw new IOException(e);
            }
        }

        private void parseSchemeForBackupDestination(int backupDestination) throws PackageManager.NameNotFoundException, IOException, XmlPullParserException {
            XmlResourceParser parser;
            String configSection = getConfigSectionForBackupDestination(backupDestination);
            if (configSection == null) {
                Slog.m90w(FullBackup.TAG, "Given backup destination isn't supported by backup scheme: " + backupDestination);
                return;
            }
            int i = this.mDataExtractionRules;
            if (i != 0) {
                parser = getParserForResource(i);
                try {
                    boolean isSectionPresent = parseNewBackupSchemeFromXmlLocked(parser, configSection, this.mExcludes, this.mIncludes);
                    if (parser != null) {
                        parser.close();
                    }
                    if (isSectionPresent) {
                        this.mIsUsingNewScheme = true;
                        return;
                    }
                } finally {
                }
            }
            if (backupDestination == 1 && CompatChanges.isChangeEnabled(FullBackup.IGNORE_FULL_BACKUP_CONTENT_IN_D2D)) {
                this.mIsUsingNewScheme = true;
                return;
            }
            int i2 = this.mFullBackupContent;
            if (i2 != 0) {
                parser = getParserForResource(i2);
                try {
                    parseBackupSchemeFromXmlLocked(parser, this.mExcludes, this.mIncludes);
                    if (parser != null) {
                        parser.close();
                    }
                } finally {
                }
            }
        }

        private String getConfigSectionForBackupDestination(int backupDestination) {
            switch (backupDestination) {
                case 0:
                    return ConfigSection.CLOUD_BACKUP;
                case 1:
                    return ConfigSection.DEVICE_TRANSFER;
                default:
                    return null;
            }
        }

        private XmlResourceParser getParserForResource(int resourceId) throws PackageManager.NameNotFoundException {
            return this.mPackageManager.getResourcesForApplication(this.mPackageName).getXml(resourceId);
        }

        public boolean parseNewBackupSchemeFromXmlLocked(XmlPullParser parser, String configSection, Set<PathWithRequiredFlags> excludes, Map<String, Set<PathWithRequiredFlags>> includes) throws IOException, XmlPullParserException {
            verifyTopLevelTag(parser, "data-extraction-rules");
            boolean isSectionPresent = false;
            while (true) {
                int event = parser.next();
                if (event != 1) {
                    if (event == 2 && configSection.equals(parser.getName())) {
                        isSectionPresent = true;
                        parseRequiredTransportFlags(parser, configSection);
                        parseRules(parser, excludes, includes, Optional.of(0), configSection);
                    }
                } else {
                    logParsingResults(excludes, includes);
                    return isSectionPresent;
                }
            }
        }

        private void parseRequiredTransportFlags(XmlPullParser parser, String configSection) {
            if (ConfigSection.CLOUD_BACKUP.equals(configSection)) {
                String encryptionAttribute = parser.getAttributeValue(null, FullBackup.FLAG_DISABLE_IF_NO_ENCRYPTION_CAPABILITIES);
                if ("true".equals(encryptionAttribute)) {
                    this.mRequiredTransportFlags = 1;
                }
            }
        }

        public void parseBackupSchemeFromXmlLocked(XmlPullParser parser, Set<PathWithRequiredFlags> excludes, Map<String, Set<PathWithRequiredFlags>> includes) throws IOException, XmlPullParserException {
            verifyTopLevelTag(parser, "full-backup-content");
            parseRules(parser, excludes, includes, Optional.empty(), "full-backup-content");
            logParsingResults(excludes, includes);
        }

        private void verifyTopLevelTag(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
            int event = parser.getEventType();
            while (event != 2) {
                event = parser.next();
            }
            if (tag.equals(parser.getName())) {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "\n");
                    Log.m106v(FullBackup.TAG_XML_PARSER, "====================================================");
                    Log.m106v(FullBackup.TAG_XML_PARSER, "Found valid " + tag + "; parsing xml resource.");
                    Log.m106v(FullBackup.TAG_XML_PARSER, "====================================================");
                    Log.m106v(FullBackup.TAG_XML_PARSER, "");
                    return;
                }
                return;
            }
            throw new XmlPullParserException("Xml file didn't start with correct tag (" + tag + " ). Found \"" + parser.getName() + "\"");
        }

        private void parseRules(XmlPullParser parser, Set<PathWithRequiredFlags> excludes, Map<String, Set<PathWithRequiredFlags>> includes, Optional<Integer> maybeRequiredFlags, String endingTag) throws IOException, XmlPullParserException {
            while (true) {
                int event = parser.next();
                if (event != 1 && !parser.getName().equals(endingTag)) {
                    switch (event) {
                        case 2:
                            validateInnerTagContents(parser);
                            String domainFromXml = parser.getAttributeValue(null, "domain");
                            File domainDirectory = getDirectoryForCriteriaDomain(domainFromXml);
                            if (domainDirectory == null) {
                                if (!Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                                    break;
                                } else {
                                    Log.m106v(FullBackup.TAG_XML_PARSER, "...parsing \"" + parser.getName() + "\": domain=\"" + domainFromXml + "\" invalid; skipping");
                                    break;
                                }
                            } else {
                                File canonicalFile = extractCanonicalFile(domainDirectory, parser.getAttributeValue(null, "path"));
                                if (canonicalFile != null) {
                                    int requiredFlags = getRequiredFlagsForRule(parser, maybeRequiredFlags);
                                    Set<PathWithRequiredFlags> activeSet = parseCurrentTagForDomain(parser, excludes, includes, domainFromXml);
                                    activeSet.add(new PathWithRequiredFlags(canonicalFile.getCanonicalPath(), requiredFlags));
                                    if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                                        Log.m106v(FullBackup.TAG_XML_PARSER, "...parsed " + canonicalFile.getCanonicalPath() + " for domain \"" + domainFromXml + "\", requiredFlags + \"" + requiredFlags + "\"");
                                    }
                                    if ("database".equals(domainFromXml) && !canonicalFile.isDirectory()) {
                                        String canonicalJournalPath = canonicalFile.getCanonicalPath() + "-journal";
                                        activeSet.add(new PathWithRequiredFlags(canonicalJournalPath, requiredFlags));
                                        if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                                            Log.m106v(FullBackup.TAG_XML_PARSER, "...automatically generated " + canonicalJournalPath + ". Ignore if nonexistent.");
                                        }
                                        String canonicalWalPath = canonicalFile.getCanonicalPath() + "-wal";
                                        activeSet.add(new PathWithRequiredFlags(canonicalWalPath, requiredFlags));
                                        if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                                            Log.m106v(FullBackup.TAG_XML_PARSER, "...automatically generated " + canonicalWalPath + ". Ignore if nonexistent.");
                                        }
                                    }
                                    if ("sharedpref".equals(domainFromXml) && !canonicalFile.isDirectory() && !canonicalFile.getCanonicalPath().endsWith(".xml")) {
                                        String canonicalXmlPath = canonicalFile.getCanonicalPath() + ".xml";
                                        activeSet.add(new PathWithRequiredFlags(canonicalXmlPath, requiredFlags));
                                        if (!Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                                            break;
                                        } else {
                                            Log.m106v(FullBackup.TAG_XML_PARSER, "...automatically generated " + canonicalXmlPath + ". Ignore if nonexistent.");
                                            break;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;
                    }
                }
                return;
            }
        }

        private void logParsingResults(Set<PathWithRequiredFlags> excludes, Map<String, Set<PathWithRequiredFlags>> includes) {
            if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                Log.m106v(FullBackup.TAG_XML_PARSER, "\n");
                Log.m106v(FullBackup.TAG_XML_PARSER, "Xml resource parsing complete.");
                Log.m106v(FullBackup.TAG_XML_PARSER, "Final tally.");
                Log.m106v(FullBackup.TAG_XML_PARSER, "Includes:");
                if (includes.isEmpty()) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "  ...nothing specified (This means the entirety of app data minus excludes)");
                } else {
                    for (Map.Entry<String, Set<PathWithRequiredFlags>> entry : includes.entrySet()) {
                        Log.m106v(FullBackup.TAG_XML_PARSER, "  domain=" + entry.getKey());
                        for (PathWithRequiredFlags includeData : entry.getValue()) {
                            Log.m106v(FullBackup.TAG_XML_PARSER, " path: " + includeData.getPath() + " requiredFlags: " + includeData.getRequiredFlags());
                        }
                    }
                }
                Log.m106v(FullBackup.TAG_XML_PARSER, "Excludes:");
                if (excludes.isEmpty()) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "  ...nothing to exclude.");
                } else {
                    for (PathWithRequiredFlags excludeData : excludes) {
                        Log.m106v(FullBackup.TAG_XML_PARSER, " path: " + excludeData.getPath() + " requiredFlags: " + excludeData.getRequiredFlags());
                    }
                }
                Log.m106v(FullBackup.TAG_XML_PARSER, "  ");
                Log.m106v(FullBackup.TAG_XML_PARSER, "====================================================");
                Log.m106v(FullBackup.TAG_XML_PARSER, "\n");
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private int getRequiredFlagsFromString(String requiredFlags) {
            char c;
            int flags = 0;
            if (requiredFlags == null || requiredFlags.length() == 0) {
                return 0;
            }
            String[] flagsStr = requiredFlags.split("\\|");
            for (String f : flagsStr) {
                switch (f.hashCode()) {
                    case 482744282:
                        if (f.equals(FullBackup.FLAG_REQUIRED_FAKE_CLIENT_SIDE_ENCRYPTION)) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1499007205:
                        if (f.equals(FullBackup.FLAG_REQUIRED_CLIENT_SIDE_ENCRYPTION)) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1935925810:
                        if (f.equals(FullBackup.FLAG_REQUIRED_DEVICE_TO_DEVICE_TRANSFER)) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        flags |= 1;
                        continue;
                    case 1:
                        flags |= 2;
                        continue;
                    case 2:
                        flags |= Integer.MIN_VALUE;
                        break;
                }
                Log.m104w(FullBackup.TAG, "Unrecognized requiredFlag provided, value: \"" + f + "\"");
            }
            return flags;
        }

        private int getRequiredFlagsForRule(XmlPullParser parser, Optional<Integer> maybeRequiredFlags) {
            if (maybeRequiredFlags.isPresent()) {
                return maybeRequiredFlags.get().intValue();
            }
            if (TAG_INCLUDE.equals(parser.getName())) {
                return getRequiredFlagsFromString(parser.getAttributeValue(null, "requireFlags"));
            }
            return 0;
        }

        private Set<PathWithRequiredFlags> parseCurrentTagForDomain(XmlPullParser parser, Set<PathWithRequiredFlags> excludes, Map<String, Set<PathWithRequiredFlags>> includes, String domain) throws XmlPullParserException {
            if (TAG_INCLUDE.equals(parser.getName())) {
                String domainToken = getTokenForXmlDomain(domain);
                Set<PathWithRequiredFlags> includeSet = includes.get(domainToken);
                if (includeSet == null) {
                    Set<PathWithRequiredFlags> includeSet2 = new ArraySet<>();
                    includes.put(domainToken, includeSet2);
                    return includeSet2;
                }
                return includeSet;
            } else if (TAG_EXCLUDE.equals(parser.getName())) {
                return excludes;
            } else {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "Invalid tag found in xml \"" + parser.getName() + "\"; aborting operation.");
                }
                throw new XmlPullParserException("Unrecognised tag in backup criteria xml (" + parser.getName() + NavigationBarInflaterView.KEY_CODE_END);
            }
        }

        private String getTokenForXmlDomain(String xmlDomain) {
            if ("root".equals(xmlDomain)) {
                return "r";
            }
            if ("file".equals(xmlDomain)) {
                return FullBackup.FILES_TREE_TOKEN;
            }
            if ("database".equals(xmlDomain)) {
                return FullBackup.DATABASE_TREE_TOKEN;
            }
            if ("sharedpref".equals(xmlDomain)) {
                return FullBackup.SHAREDPREFS_TREE_TOKEN;
            }
            if ("device_root".equals(xmlDomain)) {
                return FullBackup.DEVICE_ROOT_TREE_TOKEN;
            }
            if ("device_file".equals(xmlDomain)) {
                return FullBackup.DEVICE_FILES_TREE_TOKEN;
            }
            if ("device_database".equals(xmlDomain)) {
                return FullBackup.DEVICE_DATABASE_TREE_TOKEN;
            }
            if ("device_sharedpref".equals(xmlDomain)) {
                return FullBackup.DEVICE_SHAREDPREFS_TREE_TOKEN;
            }
            if ("external".equals(xmlDomain)) {
                return FullBackup.MANAGED_EXTERNAL_TREE_TOKEN;
            }
            return null;
        }

        private File extractCanonicalFile(File domain, String filePathFromXml) {
            if (filePathFromXml == null) {
                filePathFromXml = "";
            }
            if (filePathFromXml.contains("..")) {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "...resolved \"" + domain.getPath() + " " + filePathFromXml + "\", but the \"..\" path is not permitted; skipping.");
                }
                return null;
            } else if (filePathFromXml.contains("//")) {
                if (Log.isLoggable(FullBackup.TAG_XML_PARSER, 2)) {
                    Log.m106v(FullBackup.TAG_XML_PARSER, "...resolved \"" + domain.getPath() + " " + filePathFromXml + "\", which contains the invalid \"//\" sequence; skipping.");
                }
                return null;
            } else {
                return new File(domain, filePathFromXml);
            }
        }

        private File getDirectoryForCriteriaDomain(String domain) {
            if (TextUtils.isEmpty(domain)) {
                return null;
            }
            if ("file".equals(domain)) {
                return this.FILES_DIR;
            }
            if ("database".equals(domain)) {
                return this.DATABASE_DIR;
            }
            if ("root".equals(domain)) {
                return this.ROOT_DIR;
            }
            if ("sharedpref".equals(domain)) {
                return this.SHAREDPREF_DIR;
            }
            if ("device_file".equals(domain)) {
                return this.DEVICE_FILES_DIR;
            }
            if ("device_database".equals(domain)) {
                return this.DEVICE_DATABASE_DIR;
            }
            if ("device_root".equals(domain)) {
                return this.DEVICE_ROOT_DIR;
            }
            if ("device_sharedpref".equals(domain)) {
                return this.DEVICE_SHAREDPREF_DIR;
            }
            if ("external".equals(domain)) {
                return this.EXTERNAL_DIR;
            }
            return null;
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        private void validateInnerTagContents(XmlPullParser parser) throws XmlPullParserException {
            char c;
            if (parser == null) {
                return;
            }
            String name = parser.getName();
            switch (name.hashCode()) {
                case -1321148966:
                    if (name.equals(TAG_EXCLUDE)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1942574248:
                    if (name.equals(TAG_INCLUDE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    if (parser.getAttributeCount() > 3) {
                        throw new XmlPullParserException("At most 3 tag attributes allowed for \"include\" tag (\"domain\" & \"path\" & optional \"requiredFlags\").");
                    }
                    return;
                case 1:
                    if (parser.getAttributeCount() > 2) {
                        throw new XmlPullParserException("At most 2 tag attributes allowed for \"exclude\" tag (\"domain\" & \"path\".");
                    }
                    return;
                default:
                    throw new XmlPullParserException("A valid tag is one of \"<include/>\" or \"<exclude/>. You provided \"" + parser.getName() + "\"");
            }
        }
    }
}
