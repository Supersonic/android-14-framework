package com.android.server;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.pm.FeatureInfo;
import android.os.Build;
import android.os.CarrierAssociatedAppEntry;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Process;
import android.os.SystemProperties;
import android.os.VintfRuntimeInfo;
import android.os.incremental.IncrementalManager;
import android.os.storage.StorageManager;
import android.permission.PermissionManager;
import android.sysprop.ApexProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimingsTraceLog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.build.UnboundedSdkLevel;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.permission.PermissionAllowlist;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libcore.io.IoUtils;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SystemConfig {
    public static final ArrayMap<String, ArraySet<String>> EMPTY_PERMISSIONS = new ArrayMap<>();
    public static SystemConfig sInstance;
    public String mModulesInstallerPackageName;
    public String mOverlayConfigSignaturePackage;
    public int[] mGlobalGids = EmptyArray.INT;
    public final SparseArray<ArraySet<String>> mSystemPermissions = new SparseArray<>();
    public final ArrayList<PermissionManager.SplitPermissionInfo> mSplitPermissions = new ArrayList<>();
    public final ArrayMap<String, SharedLibraryEntry> mSharedLibraries = new ArrayMap<>();
    public final ArrayMap<String, FeatureInfo> mAvailableFeatures = new ArrayMap<>();
    public final ArraySet<String> mUnavailableFeatures = new ArraySet<>();
    public final ArrayMap<String, PermissionEntry> mPermissions = new ArrayMap<>();
    public final ArraySet<String> mAllowInPowerSaveExceptIdle = new ArraySet<>();
    public final ArraySet<String> mAllowInPowerSave = new ArraySet<>();
    public final ArraySet<String> mAllowInDataUsageSave = new ArraySet<>();
    public final ArraySet<String> mAllowUnthrottledLocation = new ArraySet<>();
    public final ArrayMap<String, ArraySet<String>> mAllowAdasSettings = new ArrayMap<>();
    public final ArrayMap<String, ArraySet<String>> mAllowIgnoreLocationSettings = new ArrayMap<>();
    public final ArraySet<String> mAllowImplicitBroadcasts = new ArraySet<>();
    public final ArraySet<String> mBgRestrictionExemption = new ArraySet<>();
    public final ArraySet<String> mLinkedApps = new ArraySet<>();
    public final ArraySet<ComponentName> mDefaultVrComponents = new ArraySet<>();
    public final ArraySet<ComponentName> mBackupTransportWhitelist = new ArraySet<>();
    public final ArrayMap<String, ArrayMap<String, Boolean>> mPackageComponentEnabledState = new ArrayMap<>();
    public final ArraySet<String> mHiddenApiPackageWhitelist = new ArraySet<>();
    public final ArraySet<String> mDisabledUntilUsedPreinstalledCarrierApps = new ArraySet<>();
    public final ArrayMap<String, List<CarrierAssociatedAppEntry>> mDisabledUntilUsedPreinstalledCarrierAssociatedApps = new ArrayMap<>();
    public final PermissionAllowlist mPermissionAllowlist = new PermissionAllowlist();
    public final ArrayMap<String, ArraySet<String>> mAllowedAssociations = new ArrayMap<>();
    public final ArraySet<String> mBugreportWhitelistedPackages = new ArraySet<>();
    public final ArraySet<String> mAppDataIsolationWhitelistedApps = new ArraySet<>();
    public ArrayMap<String, Set<String>> mPackageToUserTypeWhitelist = new ArrayMap<>();
    public ArrayMap<String, Set<String>> mPackageToUserTypeBlacklist = new ArrayMap<>();
    public final ArraySet<String> mRollbackWhitelistedPackages = new ArraySet<>();
    public final ArraySet<String> mAutomaticRollbackDenylistedPackages = new ArraySet<>();
    public final ArraySet<String> mWhitelistedStagedInstallers = new ArraySet<>();
    public final ArrayMap<String, String> mAllowedVendorApexes = new ArrayMap<>();
    public final Set<String> mInstallConstraintsAllowlist = new ArraySet();
    public final ArrayMap<String, String> mUpdateOwnersForSystemApps = new ArrayMap<>();
    public final Set<String> mInitialNonStoppedSystemPackages = new ArraySet();
    public Map<String, Map<String, String>> mNamedActors = null;

    public static boolean isAtLeastSdkLevel(String str) {
        try {
            return UnboundedSdkLevel.isAtLeast(str);
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public static boolean isAtMostSdkLevel(String str) {
        try {
            return UnboundedSdkLevel.isAtMost(str);
        } catch (IllegalArgumentException unused) {
            return true;
        }
    }

    /* loaded from: classes.dex */
    public static final class SharedLibraryEntry {
        public final boolean canBeSafelyIgnored;
        public final String[] dependencies;
        public final String filename;
        public final boolean isNative;
        public final String name;
        public final String onBootclasspathBefore;
        public final String onBootclasspathSince;

        @VisibleForTesting
        public SharedLibraryEntry(String str, String str2, String[] strArr, boolean z) {
            this(str, str2, strArr, null, null, z);
        }

        @VisibleForTesting
        public SharedLibraryEntry(String str, String str2, String[] strArr, String str3, String str4) {
            this(str, str2, strArr, str3, str4, false);
        }

        public SharedLibraryEntry(String str, String str2, String[] strArr, String str3, String str4, boolean z) {
            this.name = str;
            this.filename = str2;
            this.dependencies = strArr;
            this.onBootclasspathSince = str3;
            this.onBootclasspathBefore = str4;
            this.isNative = z;
            this.canBeSafelyIgnored = (str3 != null && SystemConfig.isAtLeastSdkLevel(str3)) || !(str4 == null || SystemConfig.isAtLeastSdkLevel(str4));
        }
    }

    /* loaded from: classes.dex */
    public static final class PermissionEntry {
        public int[] gids;
        public final String name;
        public boolean perUser;

        public PermissionEntry(String str, boolean z) {
            this.name = str;
            this.perUser = z;
        }
    }

    public static SystemConfig getInstance() {
        SystemConfig systemConfig;
        if (!isSystemProcess()) {
            Slog.wtf("SystemConfig", "SystemConfig is being accessed by a process other than system_server.");
        }
        synchronized (SystemConfig.class) {
            if (sInstance == null) {
                sInstance = new SystemConfig();
            }
            systemConfig = sInstance;
        }
        return systemConfig;
    }

    public int[] getGlobalGids() {
        return this.mGlobalGids;
    }

    public SparseArray<ArraySet<String>> getSystemPermissions() {
        return this.mSystemPermissions;
    }

    public ArrayList<PermissionManager.SplitPermissionInfo> getSplitPermissions() {
        return this.mSplitPermissions;
    }

    public ArrayMap<String, SharedLibraryEntry> getSharedLibraries() {
        return this.mSharedLibraries;
    }

    public ArrayMap<String, FeatureInfo> getAvailableFeatures() {
        return this.mAvailableFeatures;
    }

    public ArrayMap<String, PermissionEntry> getPermissions() {
        return this.mPermissions;
    }

    public ArraySet<String> getAllowImplicitBroadcasts() {
        return this.mAllowImplicitBroadcasts;
    }

    public ArraySet<String> getAllowInPowerSaveExceptIdle() {
        return this.mAllowInPowerSaveExceptIdle;
    }

    public ArraySet<String> getAllowInPowerSave() {
        return this.mAllowInPowerSave;
    }

    public ArraySet<String> getAllowInDataUsageSave() {
        return this.mAllowInDataUsageSave;
    }

    public ArraySet<String> getAllowUnthrottledLocation() {
        return this.mAllowUnthrottledLocation;
    }

    public ArrayMap<String, ArraySet<String>> getAllowAdasLocationSettings() {
        return this.mAllowAdasSettings;
    }

    public ArrayMap<String, ArraySet<String>> getAllowIgnoreLocationSettings() {
        return this.mAllowIgnoreLocationSettings;
    }

    public ArraySet<String> getBgRestrictionExemption() {
        return this.mBgRestrictionExemption;
    }

    public ArraySet<String> getLinkedApps() {
        return this.mLinkedApps;
    }

    public ArraySet<String> getHiddenApiWhitelistedApps() {
        return this.mHiddenApiPackageWhitelist;
    }

    public ArraySet<ComponentName> getDefaultVrComponents() {
        return this.mDefaultVrComponents;
    }

    public ArraySet<ComponentName> getBackupTransportWhitelist() {
        return this.mBackupTransportWhitelist;
    }

    public ArrayMap<String, Boolean> getComponentsEnabledStates(String str) {
        return this.mPackageComponentEnabledState.get(str);
    }

    public ArraySet<String> getDisabledUntilUsedPreinstalledCarrierApps() {
        return this.mDisabledUntilUsedPreinstalledCarrierApps;
    }

    public ArrayMap<String, List<CarrierAssociatedAppEntry>> getDisabledUntilUsedPreinstalledCarrierAssociatedApps() {
        return this.mDisabledUntilUsedPreinstalledCarrierAssociatedApps;
    }

    public PermissionAllowlist getPermissionAllowlist() {
        return this.mPermissionAllowlist;
    }

    public ArrayMap<String, ArraySet<String>> getAllowedAssociations() {
        return this.mAllowedAssociations;
    }

    public ArraySet<String> getBugreportWhitelistedPackages() {
        return this.mBugreportWhitelistedPackages;
    }

    public Set<String> getRollbackWhitelistedPackages() {
        return this.mRollbackWhitelistedPackages;
    }

    public Set<String> getAutomaticRollbackDenylistedPackages() {
        return this.mAutomaticRollbackDenylistedPackages;
    }

    public Set<String> getWhitelistedStagedInstallers() {
        return this.mWhitelistedStagedInstallers;
    }

    public Map<String, String> getAllowedVendorApexes() {
        return this.mAllowedVendorApexes;
    }

    public Set<String> getInstallConstraintsAllowlist() {
        return this.mInstallConstraintsAllowlist;
    }

    public String getModulesInstallerPackageName() {
        return this.mModulesInstallerPackageName;
    }

    public String getSystemAppUpdateOwnerPackageName(String str) {
        return this.mUpdateOwnersForSystemApps.get(str);
    }

    public ArraySet<String> getAppDataIsolationWhitelistedApps() {
        return this.mAppDataIsolationWhitelistedApps;
    }

    public ArrayMap<String, Set<String>> getAndClearPackageToUserTypeWhitelist() {
        ArrayMap<String, Set<String>> arrayMap = this.mPackageToUserTypeWhitelist;
        this.mPackageToUserTypeWhitelist = new ArrayMap<>(0);
        return arrayMap;
    }

    public ArrayMap<String, Set<String>> getAndClearPackageToUserTypeBlacklist() {
        ArrayMap<String, Set<String>> arrayMap = this.mPackageToUserTypeBlacklist;
        this.mPackageToUserTypeBlacklist = new ArrayMap<>(0);
        return arrayMap;
    }

    public Map<String, Map<String, String>> getNamedActors() {
        Map<String, Map<String, String>> map = this.mNamedActors;
        return map != null ? map : Collections.emptyMap();
    }

    public String getOverlayConfigSignaturePackage() {
        if (TextUtils.isEmpty(this.mOverlayConfigSignaturePackage)) {
            return null;
        }
        return this.mOverlayConfigSignaturePackage;
    }

    public Set<String> getInitialNonStoppedSystemPackages() {
        return this.mInitialNonStoppedSystemPackages;
    }

    @VisibleForTesting
    public SystemConfig(boolean z) {
        if (z) {
            Slog.w("SystemConfig", "Constructing a test SystemConfig");
            readAllPermissions();
            return;
        }
        Slog.w("SystemConfig", "Constructing an empty test SystemConfig");
    }

    public SystemConfig() {
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemConfig", 524288L);
        timingsTraceLog.traceBegin("readAllPermissions");
        try {
            readAllPermissions();
            readPublicNativeLibrariesList();
        } finally {
            timingsTraceLog.traceEnd();
        }
    }

    public final void readAllPermissions() {
        File[] listFilesOrEmpty;
        XmlPullParser newPullParser = Xml.newPullParser();
        readPermissions(newPullParser, Environment.buildPath(Environment.getRootDirectory(), new String[]{"etc", "sysconfig"}), -1);
        readPermissions(newPullParser, Environment.buildPath(Environment.getRootDirectory(), new String[]{"etc", "permissions"}), -1);
        int i = Build.VERSION.DEVICE_INITIAL_SDK_INT <= 27 ? 1183 : 1171;
        readPermissions(newPullParser, Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc", "sysconfig"}), i);
        readPermissions(newPullParser, Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc", "permissions"}), i);
        String str = SystemProperties.get("ro.boot.product.vendor.sku", "");
        if (!str.isEmpty()) {
            String str2 = "sku_" + str;
            readPermissions(newPullParser, Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc", "sysconfig", str2}), i);
            readPermissions(newPullParser, Environment.buildPath(Environment.getVendorDirectory(), new String[]{"etc", "permissions", str2}), i);
        }
        readPermissions(newPullParser, Environment.buildPath(Environment.getOdmDirectory(), new String[]{"etc", "sysconfig"}), i);
        readPermissions(newPullParser, Environment.buildPath(Environment.getOdmDirectory(), new String[]{"etc", "permissions"}), i);
        String str3 = SystemProperties.get("ro.boot.product.hardware.sku", "");
        if (!str3.isEmpty()) {
            String str4 = "sku_" + str3;
            readPermissions(newPullParser, Environment.buildPath(Environment.getOdmDirectory(), new String[]{"etc", "sysconfig", str4}), i);
            readPermissions(newPullParser, Environment.buildPath(Environment.getOdmDirectory(), new String[]{"etc", "permissions", str4}), i);
        }
        readPermissions(newPullParser, Environment.buildPath(Environment.getOemDirectory(), new String[]{"etc", "sysconfig"}), 1185);
        readPermissions(newPullParser, Environment.buildPath(Environment.getOemDirectory(), new String[]{"etc", "permissions"}), 1185);
        int i2 = Build.VERSION.DEVICE_INITIAL_SDK_INT <= 30 ? -1 : 2015;
        readPermissions(newPullParser, Environment.buildPath(Environment.getProductDirectory(), new String[]{"etc", "sysconfig"}), i2);
        readPermissions(newPullParser, Environment.buildPath(Environment.getProductDirectory(), new String[]{"etc", "permissions"}), i2);
        readPermissions(newPullParser, Environment.buildPath(Environment.getSystemExtDirectory(), new String[]{"etc", "sysconfig"}), -1);
        readPermissions(newPullParser, Environment.buildPath(Environment.getSystemExtDirectory(), new String[]{"etc", "permissions"}), -1);
        if (isSystemProcess()) {
            for (File file : FileUtils.listFilesOrEmpty(Environment.getApexDirectory())) {
                if (!file.isFile() && !file.getPath().contains("@")) {
                    readPermissions(newPullParser, Environment.buildPath(file, new String[]{"etc", "permissions"}), 19);
                }
            }
        }
    }

    @VisibleForTesting
    public void readPermissions(XmlPullParser xmlPullParser, File file, int i) {
        File[] listFiles;
        if (!file.exists() || !file.isDirectory()) {
            if (i == -1) {
                Slog.w("SystemConfig", "No directory " + file + ", skipping");
            }
        } else if (file.canRead()) {
            File file2 = null;
            for (File file3 : file.listFiles()) {
                if (file3.isFile()) {
                    if (file3.getPath().endsWith("etc/permissions/platform.xml")) {
                        file2 = file3;
                    } else if (!file3.getPath().endsWith(".xml")) {
                        Slog.i("SystemConfig", "Non-xml file " + file3 + " in " + file + " directory, ignoring");
                    } else if (file3.canRead()) {
                        readPermissionsFromXml(xmlPullParser, file3, i);
                    } else {
                        Slog.w("SystemConfig", "Permissions library file " + file3 + " cannot be read");
                    }
                }
            }
            if (file2 != null) {
                readPermissionsFromXml(xmlPullParser, file2, i);
            }
        } else {
            Slog.w("SystemConfig", "Directory " + file + " cannot be read");
        }
    }

    public final void logNotAllowedInPartition(String str, File file, XmlPullParser xmlPullParser) {
        Slog.w("SystemConfig", "<" + str + "> not allowed in partition of " + file + " at " + xmlPullParser.getPositionDescription());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0037, code lost:
        if (r15 != 2) goto L634;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0044, code lost:
        if (r31.getName().equals("permissions") != false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0050, code lost:
        if (r31.getName().equals("config") == false) goto L620;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x007a, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Unexpected start tag in " + r32 + ": found " + r31.getName() + ", expected 'permissions' or 'config'");
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x007b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x007c, code lost:
        r1 = r0;
        r24 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0081, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0082, code lost:
        r2 = r0;
        r25 = "Got exception parsing permissions.";
        r24 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0089, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x008a, code lost:
        r2 = r0;
        r3 = "Got exception parsing permissions.";
        r24 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0091, code lost:
        if (r33 != (-1)) goto L615;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0093, code lost:
        r15 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0095, code lost:
        r15 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0098, code lost:
        if ((r33 & 2) == 0) goto L614;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009a, code lost:
        r16 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x009d, code lost:
        r16 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a1, code lost:
        if ((r33 & 1) == 0) goto L613;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a3, code lost:
        r17 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00a6, code lost:
        r17 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00aa, code lost:
        if ((r33 & 4) == 0) goto L612;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00ac, code lost:
        r18 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00af, code lost:
        r18 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b3, code lost:
        if ((r33 & 8) == 0) goto L611;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x00b5, code lost:
        r19 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00b8, code lost:
        r19 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x00bc, code lost:
        if ((r33 & 16) == 0) goto L610;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00be, code lost:
        r20 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00c1, code lost:
        r20 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00c5, code lost:
        if ((r33 & 32) == 0) goto L609;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00c7, code lost:
        r21 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00ca, code lost:
        r21 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00ce, code lost:
        if ((r33 & 64) == 0) goto L608;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00d0, code lost:
        r22 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00d3, code lost:
        r22 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00d7, code lost:
        if ((r33 & 128) == 0) goto L607;
     */
    /* JADX WARN: Code restructure failed: missing block: B:581:0x106e, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("No start tag found");
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00d9, code lost:
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:598:0x1093, code lost:
        android.util.Slog.w("SystemConfig", r3, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00db, code lost:
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00de, code lost:
        if ((r33 & 256) == 0) goto L606;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00e0, code lost:
        r10 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00e2, code lost:
        r10 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00e5, code lost:
        if ((r33 & 512) == 0) goto L605;
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00e7, code lost:
        r11 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e9, code lost:
        r11 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00ec, code lost:
        if ((r33 & 1024) == 0) goto L604;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00ee, code lost:
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00f0, code lost:
        r4 = false;
     */
    /* JADX WARN: Not initialized variable reg: 25, insn: 0x1073: MOVE  (r3 I:??[OBJECT, ARRAY]) = (r25 I:??[OBJECT, ARRAY]), block:B:585:0x1072 */
    /* JADX WARN: Removed duplicated region for block: B:343:0x08cd A[Catch: IOException -> 0x106f, XmlPullParserException -> 0x1071, all -> 0x1120, TryCatch #0 {all -> 0x1120, blocks: (B:578:0x1029, B:200:0x02fa, B:202:0x030b, B:203:0x032e, B:205:0x0334, B:206:0x0359, B:208:0x035f, B:209:0x0365, B:211:0x0375, B:216:0x03ca, B:212:0x039a, B:214:0x03a0, B:215:0x03c5, B:218:0x03d1, B:220:0x03d7, B:223:0x0403, B:221:0x03fa, B:222:0x0400, B:225:0x040a, B:227:0x0416, B:229:0x043a, B:232:0x0462, B:234:0x046b, B:233:0x0468, B:236:0x0472, B:238:0x047f, B:241:0x04a9, B:243:0x04ad, B:244:0x04b0, B:245:0x04b7, B:247:0x04bb, B:239:0x04a2, B:246:0x04b8, B:248:0x04c0, B:250:0x04c6, B:252:0x04ee, B:251:0x04e9, B:253:0x04f3, B:255:0x04f9, B:257:0x0521, B:256:0x051c, B:259:0x0528, B:261:0x052e, B:268:0x0584, B:262:0x0551, B:264:0x0559, B:265:0x0560, B:266:0x0580, B:267:0x0581, B:269:0x0589, B:271:0x05a6, B:289:0x0653, B:272:0x05cc, B:274:0x05d2, B:275:0x05f7, B:277:0x05fd, B:278:0x0622, B:280:0x062a, B:282:0x062e, B:283:0x0635, B:285:0x063f, B:288:0x0650, B:286:0x064a, B:290:0x0658, B:291:0x068a, B:292:0x068b, B:293:0x06ae, B:294:0x06af, B:295:0x06b8, B:297:0x06be, B:299:0x06e6, B:298:0x06e1, B:300:0x06eb, B:302:0x06f1, B:304:0x0719, B:303:0x0714, B:306:0x0720, B:308:0x0729, B:309:0x0752, B:311:0x075a, B:312:0x0783, B:314:0x0795, B:315:0x079f, B:317:0x07c2, B:316:0x07bf, B:319:0x07c9, B:321:0x07cf, B:324:0x07fb, B:322:0x07f2, B:323:0x07f8, B:326:0x0802, B:327:0x0807, B:329:0x0811, B:331:0x0832, B:336:0x0857, B:338:0x08b6, B:343:0x08cd, B:345:0x08da, B:347:0x08e7, B:349:0x08f4, B:350:0x0901, B:351:0x090c, B:353:0x0916, B:355:0x091c, B:358:0x0948, B:356:0x093f, B:357:0x0945, B:360:0x094f, B:364:0x095e, B:366:0x096a, B:370:0x0999, B:372:0x09a3, B:373:0x09ad, B:376:0x09de, B:368:0x096f, B:374:0x09b6, B:375:0x09db, B:378:0x09e5, B:380:0x09ee, B:386:0x0a4f, B:381:0x0a13, B:383:0x0a19, B:384:0x0a46, B:385:0x0a4c, B:387:0x0a53, B:390:0x0a5c, B:392:0x0a68, B:397:0x0ac0, B:394:0x0a8d, B:395:0x0ab2, B:396:0x0abd, B:399:0x0ac6, B:401:0x0acc, B:404:0x0af8, B:402:0x0aef, B:403:0x0af5, B:406:0x0aff, B:408:0x0b05, B:411:0x0b31, B:409:0x0b28, B:410:0x0b2e, B:413:0x0b38, B:415:0x0b40, B:418:0x0b6e, B:416:0x0b65, B:417:0x0b6b, B:420:0x0b75, B:422:0x0b81, B:437:0x0bdb, B:423:0x0ba4, B:425:0x0bae, B:428:0x0bb6, B:429:0x0bc1, B:431:0x0bc9, B:435:0x0bd4, B:436:0x0bd8, B:439:0x0be2, B:441:0x0bee, B:456:0x0c48, B:442:0x0c11, B:444:0x0c1b, B:447:0x0c23, B:448:0x0c2e, B:450:0x0c36, B:454:0x0c41, B:455:0x0c45, B:458:0x0c4f, B:460:0x0c55, B:463:0x0c81, B:461:0x0c78, B:462:0x0c7e, B:465:0x0c88, B:467:0x0c8e, B:470:0x0cba, B:468:0x0cb1, B:469:0x0cb7, B:472:0x0cc1, B:474:0x0cc7, B:477:0x0cf3, B:475:0x0cea, B:476:0x0cf0, B:479:0x0cfa, B:481:0x0d00, B:484:0x0d2c, B:482:0x0d23, B:483:0x0d29, B:486:0x0d33, B:488:0x0d39, B:491:0x0d67, B:489:0x0d5e, B:490:0x0d64, B:494:0x0d70, B:499:0x0d96, B:503:0x0dc3, B:501:0x0dbb, B:497:0x0d82, B:502:0x0dbf, B:506:0x0dcd, B:508:0x0def, B:541:0x0ecb, B:510:0x0e15, B:512:0x0e3d, B:518:0x0e49, B:523:0x0e53, B:527:0x0e62, B:529:0x0e74, B:531:0x0e80, B:530:0x0e79, B:532:0x0e8c, B:534:0x0e9d, B:536:0x0ea7, B:538:0x0eb1, B:539:0x0ebe, B:540:0x0ec6, B:544:0x0ed4, B:545:0x0ed9, B:548:0x0ee7, B:550:0x0eed, B:551:0x0f14, B:553:0x0f1d, B:554:0x0f46, B:556:0x0f4c, B:557:0x0f7d, B:559:0x0f8b, B:560:0x0f95, B:562:0x0f9c, B:561:0x0f99, B:565:0x0fa7, B:567:0x0fad, B:568:0x0fd4, B:569:0x0fdd, B:572:0x0fe8, B:574:0x0ff0, B:577:0x1025, B:575:0x0ffd, B:576:0x1022, B:595:0x108a, B:598:0x1093, B:580:0x1063, B:581:0x106e), top: B:636:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:344:0x08d8  */
    /* JADX WARN: Removed duplicated region for block: B:525:0x0e5e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:534:0x0e9d A[Catch: IOException -> 0x106f, XmlPullParserException -> 0x1071, all -> 0x1120, TryCatch #0 {all -> 0x1120, blocks: (B:578:0x1029, B:200:0x02fa, B:202:0x030b, B:203:0x032e, B:205:0x0334, B:206:0x0359, B:208:0x035f, B:209:0x0365, B:211:0x0375, B:216:0x03ca, B:212:0x039a, B:214:0x03a0, B:215:0x03c5, B:218:0x03d1, B:220:0x03d7, B:223:0x0403, B:221:0x03fa, B:222:0x0400, B:225:0x040a, B:227:0x0416, B:229:0x043a, B:232:0x0462, B:234:0x046b, B:233:0x0468, B:236:0x0472, B:238:0x047f, B:241:0x04a9, B:243:0x04ad, B:244:0x04b0, B:245:0x04b7, B:247:0x04bb, B:239:0x04a2, B:246:0x04b8, B:248:0x04c0, B:250:0x04c6, B:252:0x04ee, B:251:0x04e9, B:253:0x04f3, B:255:0x04f9, B:257:0x0521, B:256:0x051c, B:259:0x0528, B:261:0x052e, B:268:0x0584, B:262:0x0551, B:264:0x0559, B:265:0x0560, B:266:0x0580, B:267:0x0581, B:269:0x0589, B:271:0x05a6, B:289:0x0653, B:272:0x05cc, B:274:0x05d2, B:275:0x05f7, B:277:0x05fd, B:278:0x0622, B:280:0x062a, B:282:0x062e, B:283:0x0635, B:285:0x063f, B:288:0x0650, B:286:0x064a, B:290:0x0658, B:291:0x068a, B:292:0x068b, B:293:0x06ae, B:294:0x06af, B:295:0x06b8, B:297:0x06be, B:299:0x06e6, B:298:0x06e1, B:300:0x06eb, B:302:0x06f1, B:304:0x0719, B:303:0x0714, B:306:0x0720, B:308:0x0729, B:309:0x0752, B:311:0x075a, B:312:0x0783, B:314:0x0795, B:315:0x079f, B:317:0x07c2, B:316:0x07bf, B:319:0x07c9, B:321:0x07cf, B:324:0x07fb, B:322:0x07f2, B:323:0x07f8, B:326:0x0802, B:327:0x0807, B:329:0x0811, B:331:0x0832, B:336:0x0857, B:338:0x08b6, B:343:0x08cd, B:345:0x08da, B:347:0x08e7, B:349:0x08f4, B:350:0x0901, B:351:0x090c, B:353:0x0916, B:355:0x091c, B:358:0x0948, B:356:0x093f, B:357:0x0945, B:360:0x094f, B:364:0x095e, B:366:0x096a, B:370:0x0999, B:372:0x09a3, B:373:0x09ad, B:376:0x09de, B:368:0x096f, B:374:0x09b6, B:375:0x09db, B:378:0x09e5, B:380:0x09ee, B:386:0x0a4f, B:381:0x0a13, B:383:0x0a19, B:384:0x0a46, B:385:0x0a4c, B:387:0x0a53, B:390:0x0a5c, B:392:0x0a68, B:397:0x0ac0, B:394:0x0a8d, B:395:0x0ab2, B:396:0x0abd, B:399:0x0ac6, B:401:0x0acc, B:404:0x0af8, B:402:0x0aef, B:403:0x0af5, B:406:0x0aff, B:408:0x0b05, B:411:0x0b31, B:409:0x0b28, B:410:0x0b2e, B:413:0x0b38, B:415:0x0b40, B:418:0x0b6e, B:416:0x0b65, B:417:0x0b6b, B:420:0x0b75, B:422:0x0b81, B:437:0x0bdb, B:423:0x0ba4, B:425:0x0bae, B:428:0x0bb6, B:429:0x0bc1, B:431:0x0bc9, B:435:0x0bd4, B:436:0x0bd8, B:439:0x0be2, B:441:0x0bee, B:456:0x0c48, B:442:0x0c11, B:444:0x0c1b, B:447:0x0c23, B:448:0x0c2e, B:450:0x0c36, B:454:0x0c41, B:455:0x0c45, B:458:0x0c4f, B:460:0x0c55, B:463:0x0c81, B:461:0x0c78, B:462:0x0c7e, B:465:0x0c88, B:467:0x0c8e, B:470:0x0cba, B:468:0x0cb1, B:469:0x0cb7, B:472:0x0cc1, B:474:0x0cc7, B:477:0x0cf3, B:475:0x0cea, B:476:0x0cf0, B:479:0x0cfa, B:481:0x0d00, B:484:0x0d2c, B:482:0x0d23, B:483:0x0d29, B:486:0x0d33, B:488:0x0d39, B:491:0x0d67, B:489:0x0d5e, B:490:0x0d64, B:494:0x0d70, B:499:0x0d96, B:503:0x0dc3, B:501:0x0dbb, B:497:0x0d82, B:502:0x0dbf, B:506:0x0dcd, B:508:0x0def, B:541:0x0ecb, B:510:0x0e15, B:512:0x0e3d, B:518:0x0e49, B:523:0x0e53, B:527:0x0e62, B:529:0x0e74, B:531:0x0e80, B:530:0x0e79, B:532:0x0e8c, B:534:0x0e9d, B:536:0x0ea7, B:538:0x0eb1, B:539:0x0ebe, B:540:0x0ec6, B:544:0x0ed4, B:545:0x0ed9, B:548:0x0ee7, B:550:0x0eed, B:551:0x0f14, B:553:0x0f1d, B:554:0x0f46, B:556:0x0f4c, B:557:0x0f7d, B:559:0x0f8b, B:560:0x0f95, B:562:0x0f9c, B:561:0x0f99, B:565:0x0fa7, B:567:0x0fad, B:568:0x0fd4, B:569:0x0fdd, B:572:0x0fe8, B:574:0x0ff0, B:577:0x1025, B:575:0x0ffd, B:576:0x1022, B:595:0x108a, B:598:0x1093, B:580:0x1063, B:581:0x106e), top: B:636:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0ea7 A[Catch: IOException -> 0x106f, XmlPullParserException -> 0x1071, all -> 0x1120, TryCatch #0 {all -> 0x1120, blocks: (B:578:0x1029, B:200:0x02fa, B:202:0x030b, B:203:0x032e, B:205:0x0334, B:206:0x0359, B:208:0x035f, B:209:0x0365, B:211:0x0375, B:216:0x03ca, B:212:0x039a, B:214:0x03a0, B:215:0x03c5, B:218:0x03d1, B:220:0x03d7, B:223:0x0403, B:221:0x03fa, B:222:0x0400, B:225:0x040a, B:227:0x0416, B:229:0x043a, B:232:0x0462, B:234:0x046b, B:233:0x0468, B:236:0x0472, B:238:0x047f, B:241:0x04a9, B:243:0x04ad, B:244:0x04b0, B:245:0x04b7, B:247:0x04bb, B:239:0x04a2, B:246:0x04b8, B:248:0x04c0, B:250:0x04c6, B:252:0x04ee, B:251:0x04e9, B:253:0x04f3, B:255:0x04f9, B:257:0x0521, B:256:0x051c, B:259:0x0528, B:261:0x052e, B:268:0x0584, B:262:0x0551, B:264:0x0559, B:265:0x0560, B:266:0x0580, B:267:0x0581, B:269:0x0589, B:271:0x05a6, B:289:0x0653, B:272:0x05cc, B:274:0x05d2, B:275:0x05f7, B:277:0x05fd, B:278:0x0622, B:280:0x062a, B:282:0x062e, B:283:0x0635, B:285:0x063f, B:288:0x0650, B:286:0x064a, B:290:0x0658, B:291:0x068a, B:292:0x068b, B:293:0x06ae, B:294:0x06af, B:295:0x06b8, B:297:0x06be, B:299:0x06e6, B:298:0x06e1, B:300:0x06eb, B:302:0x06f1, B:304:0x0719, B:303:0x0714, B:306:0x0720, B:308:0x0729, B:309:0x0752, B:311:0x075a, B:312:0x0783, B:314:0x0795, B:315:0x079f, B:317:0x07c2, B:316:0x07bf, B:319:0x07c9, B:321:0x07cf, B:324:0x07fb, B:322:0x07f2, B:323:0x07f8, B:326:0x0802, B:327:0x0807, B:329:0x0811, B:331:0x0832, B:336:0x0857, B:338:0x08b6, B:343:0x08cd, B:345:0x08da, B:347:0x08e7, B:349:0x08f4, B:350:0x0901, B:351:0x090c, B:353:0x0916, B:355:0x091c, B:358:0x0948, B:356:0x093f, B:357:0x0945, B:360:0x094f, B:364:0x095e, B:366:0x096a, B:370:0x0999, B:372:0x09a3, B:373:0x09ad, B:376:0x09de, B:368:0x096f, B:374:0x09b6, B:375:0x09db, B:378:0x09e5, B:380:0x09ee, B:386:0x0a4f, B:381:0x0a13, B:383:0x0a19, B:384:0x0a46, B:385:0x0a4c, B:387:0x0a53, B:390:0x0a5c, B:392:0x0a68, B:397:0x0ac0, B:394:0x0a8d, B:395:0x0ab2, B:396:0x0abd, B:399:0x0ac6, B:401:0x0acc, B:404:0x0af8, B:402:0x0aef, B:403:0x0af5, B:406:0x0aff, B:408:0x0b05, B:411:0x0b31, B:409:0x0b28, B:410:0x0b2e, B:413:0x0b38, B:415:0x0b40, B:418:0x0b6e, B:416:0x0b65, B:417:0x0b6b, B:420:0x0b75, B:422:0x0b81, B:437:0x0bdb, B:423:0x0ba4, B:425:0x0bae, B:428:0x0bb6, B:429:0x0bc1, B:431:0x0bc9, B:435:0x0bd4, B:436:0x0bd8, B:439:0x0be2, B:441:0x0bee, B:456:0x0c48, B:442:0x0c11, B:444:0x0c1b, B:447:0x0c23, B:448:0x0c2e, B:450:0x0c36, B:454:0x0c41, B:455:0x0c45, B:458:0x0c4f, B:460:0x0c55, B:463:0x0c81, B:461:0x0c78, B:462:0x0c7e, B:465:0x0c88, B:467:0x0c8e, B:470:0x0cba, B:468:0x0cb1, B:469:0x0cb7, B:472:0x0cc1, B:474:0x0cc7, B:477:0x0cf3, B:475:0x0cea, B:476:0x0cf0, B:479:0x0cfa, B:481:0x0d00, B:484:0x0d2c, B:482:0x0d23, B:483:0x0d29, B:486:0x0d33, B:488:0x0d39, B:491:0x0d67, B:489:0x0d5e, B:490:0x0d64, B:494:0x0d70, B:499:0x0d96, B:503:0x0dc3, B:501:0x0dbb, B:497:0x0d82, B:502:0x0dbf, B:506:0x0dcd, B:508:0x0def, B:541:0x0ecb, B:510:0x0e15, B:512:0x0e3d, B:518:0x0e49, B:523:0x0e53, B:527:0x0e62, B:529:0x0e74, B:531:0x0e80, B:530:0x0e79, B:532:0x0e8c, B:534:0x0e9d, B:536:0x0ea7, B:538:0x0eb1, B:539:0x0ebe, B:540:0x0ec6, B:544:0x0ed4, B:545:0x0ed9, B:548:0x0ee7, B:550:0x0eed, B:551:0x0f14, B:553:0x0f1d, B:554:0x0f46, B:556:0x0f4c, B:557:0x0f7d, B:559:0x0f8b, B:560:0x0f95, B:562:0x0f9c, B:561:0x0f99, B:565:0x0fa7, B:567:0x0fad, B:568:0x0fd4, B:569:0x0fdd, B:572:0x0fe8, B:574:0x0ff0, B:577:0x1025, B:575:0x0ffd, B:576:0x1022, B:595:0x108a, B:598:0x1093, B:580:0x1063, B:581:0x106e), top: B:636:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:538:0x0eb1 A[Catch: IOException -> 0x106f, XmlPullParserException -> 0x1071, all -> 0x1120, TryCatch #0 {all -> 0x1120, blocks: (B:578:0x1029, B:200:0x02fa, B:202:0x030b, B:203:0x032e, B:205:0x0334, B:206:0x0359, B:208:0x035f, B:209:0x0365, B:211:0x0375, B:216:0x03ca, B:212:0x039a, B:214:0x03a0, B:215:0x03c5, B:218:0x03d1, B:220:0x03d7, B:223:0x0403, B:221:0x03fa, B:222:0x0400, B:225:0x040a, B:227:0x0416, B:229:0x043a, B:232:0x0462, B:234:0x046b, B:233:0x0468, B:236:0x0472, B:238:0x047f, B:241:0x04a9, B:243:0x04ad, B:244:0x04b0, B:245:0x04b7, B:247:0x04bb, B:239:0x04a2, B:246:0x04b8, B:248:0x04c0, B:250:0x04c6, B:252:0x04ee, B:251:0x04e9, B:253:0x04f3, B:255:0x04f9, B:257:0x0521, B:256:0x051c, B:259:0x0528, B:261:0x052e, B:268:0x0584, B:262:0x0551, B:264:0x0559, B:265:0x0560, B:266:0x0580, B:267:0x0581, B:269:0x0589, B:271:0x05a6, B:289:0x0653, B:272:0x05cc, B:274:0x05d2, B:275:0x05f7, B:277:0x05fd, B:278:0x0622, B:280:0x062a, B:282:0x062e, B:283:0x0635, B:285:0x063f, B:288:0x0650, B:286:0x064a, B:290:0x0658, B:291:0x068a, B:292:0x068b, B:293:0x06ae, B:294:0x06af, B:295:0x06b8, B:297:0x06be, B:299:0x06e6, B:298:0x06e1, B:300:0x06eb, B:302:0x06f1, B:304:0x0719, B:303:0x0714, B:306:0x0720, B:308:0x0729, B:309:0x0752, B:311:0x075a, B:312:0x0783, B:314:0x0795, B:315:0x079f, B:317:0x07c2, B:316:0x07bf, B:319:0x07c9, B:321:0x07cf, B:324:0x07fb, B:322:0x07f2, B:323:0x07f8, B:326:0x0802, B:327:0x0807, B:329:0x0811, B:331:0x0832, B:336:0x0857, B:338:0x08b6, B:343:0x08cd, B:345:0x08da, B:347:0x08e7, B:349:0x08f4, B:350:0x0901, B:351:0x090c, B:353:0x0916, B:355:0x091c, B:358:0x0948, B:356:0x093f, B:357:0x0945, B:360:0x094f, B:364:0x095e, B:366:0x096a, B:370:0x0999, B:372:0x09a3, B:373:0x09ad, B:376:0x09de, B:368:0x096f, B:374:0x09b6, B:375:0x09db, B:378:0x09e5, B:380:0x09ee, B:386:0x0a4f, B:381:0x0a13, B:383:0x0a19, B:384:0x0a46, B:385:0x0a4c, B:387:0x0a53, B:390:0x0a5c, B:392:0x0a68, B:397:0x0ac0, B:394:0x0a8d, B:395:0x0ab2, B:396:0x0abd, B:399:0x0ac6, B:401:0x0acc, B:404:0x0af8, B:402:0x0aef, B:403:0x0af5, B:406:0x0aff, B:408:0x0b05, B:411:0x0b31, B:409:0x0b28, B:410:0x0b2e, B:413:0x0b38, B:415:0x0b40, B:418:0x0b6e, B:416:0x0b65, B:417:0x0b6b, B:420:0x0b75, B:422:0x0b81, B:437:0x0bdb, B:423:0x0ba4, B:425:0x0bae, B:428:0x0bb6, B:429:0x0bc1, B:431:0x0bc9, B:435:0x0bd4, B:436:0x0bd8, B:439:0x0be2, B:441:0x0bee, B:456:0x0c48, B:442:0x0c11, B:444:0x0c1b, B:447:0x0c23, B:448:0x0c2e, B:450:0x0c36, B:454:0x0c41, B:455:0x0c45, B:458:0x0c4f, B:460:0x0c55, B:463:0x0c81, B:461:0x0c78, B:462:0x0c7e, B:465:0x0c88, B:467:0x0c8e, B:470:0x0cba, B:468:0x0cb1, B:469:0x0cb7, B:472:0x0cc1, B:474:0x0cc7, B:477:0x0cf3, B:475:0x0cea, B:476:0x0cf0, B:479:0x0cfa, B:481:0x0d00, B:484:0x0d2c, B:482:0x0d23, B:483:0x0d29, B:486:0x0d33, B:488:0x0d39, B:491:0x0d67, B:489:0x0d5e, B:490:0x0d64, B:494:0x0d70, B:499:0x0d96, B:503:0x0dc3, B:501:0x0dbb, B:497:0x0d82, B:502:0x0dbf, B:506:0x0dcd, B:508:0x0def, B:541:0x0ecb, B:510:0x0e15, B:512:0x0e3d, B:518:0x0e49, B:523:0x0e53, B:527:0x0e62, B:529:0x0e74, B:531:0x0e80, B:530:0x0e79, B:532:0x0e8c, B:534:0x0e9d, B:536:0x0ea7, B:538:0x0eb1, B:539:0x0ebe, B:540:0x0ec6, B:544:0x0ed4, B:545:0x0ed9, B:548:0x0ee7, B:550:0x0eed, B:551:0x0f14, B:553:0x0f1d, B:554:0x0f46, B:556:0x0f4c, B:557:0x0f7d, B:559:0x0f8b, B:560:0x0f95, B:562:0x0f9c, B:561:0x0f99, B:565:0x0fa7, B:567:0x0fad, B:568:0x0fd4, B:569:0x0fdd, B:572:0x0fe8, B:574:0x0ff0, B:577:0x1025, B:575:0x0ffd, B:576:0x1022, B:595:0x108a, B:598:0x1093, B:580:0x1063, B:581:0x106e), top: B:636:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:602:0x109f  */
    /* JADX WARN: Removed duplicated region for block: B:603:0x10ab  */
    /* JADX WARN: Removed duplicated region for block: B:606:0x10b2  */
    /* JADX WARN: Removed duplicated region for block: B:609:0x10bd  */
    /* JADX WARN: Removed duplicated region for block: B:610:0x10c3  */
    /* JADX WARN: Removed duplicated region for block: B:613:0x10ce  */
    /* JADX WARN: Removed duplicated region for block: B:616:0x10de  */
    /* JADX WARN: Removed duplicated region for block: B:619:0x10ec  */
    /* JADX WARN: Removed duplicated region for block: B:628:0x1115 A[LOOP:2: B:626:0x110f->B:628:0x1115, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readPermissionsFromXml(XmlPullParser xmlPullParser, File file, int i) {
        String str;
        FileReader fileReader;
        String str2;
        XmlPullParserException xmlPullParserException;
        int i2;
        int version;
        Iterator<String> it;
        int i3;
        String str3;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        boolean z9;
        boolean z10;
        boolean z11;
        boolean z12;
        char c;
        boolean z13;
        boolean z14;
        boolean z15;
        boolean exists;
        int parseInt;
        boolean z16;
        String str4 = "Got exception parsing permissions.";
        try {
            FileReader fileReader2 = new FileReader(file);
            Slog.i("SystemConfig", "Reading permissions from " + file);
            boolean isLowRamDeviceStatic = ActivityManager.isLowRamDeviceStatic();
            try {
                try {
                    try {
                        xmlPullParser.setInput(fileReader2);
                        while (true) {
                            try {
                                int next = xmlPullParser.next();
                                i3 = 1;
                                if (next == 2 || next == 1) {
                                    try {
                                        break;
                                    } catch (IOException e) {
                                        e = e;
                                        IOException iOException = e;
                                        Slog.w("SystemConfig", str, iOException);
                                        IoUtils.closeQuietly(fileReader);
                                        if (StorageManager.isFileEncrypted()) {
                                        }
                                        if (StorageManager.hasAdoptable()) {
                                        }
                                        if (!ActivityManager.isLowRamDeviceStatic()) {
                                        }
                                        version = IncrementalManager.getVersion();
                                        if (version > 0) {
                                        }
                                        addFeature("android.software.app_enumeration", i2);
                                        if (Build.VERSION.DEVICE_INITIAL_SDK_INT >= 29) {
                                        }
                                        enableIpSecTunnelMigrationOnVsrUAndAbove();
                                        if (isErofsSupported()) {
                                        }
                                        it = this.mUnavailableFeatures.iterator();
                                        while (it.hasNext()) {
                                        }
                                        return;
                                    } catch (XmlPullParserException e2) {
                                        xmlPullParserException = e2;
                                        str2 = str3;
                                    }
                                }
                            } catch (XmlPullParserException e3) {
                                fileReader = fileReader2;
                                xmlPullParserException = e3;
                                str2 = str4;
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        Throwable th2 = th;
                        IoUtils.closeQuietly(r24);
                        throw th2;
                    }
                } catch (XmlPullParserException e4) {
                    str2 = "Got exception parsing permissions.";
                    fileReader = fileReader2;
                    xmlPullParserException = e4;
                }
            } catch (IOException e5) {
                e = e5;
                str = str4;
                fileReader = fileReader2;
            } catch (Throwable th3) {
                th = th3;
                FileReader fileReader3 = fileReader2;
                Throwable th22 = th;
                IoUtils.closeQuietly(fileReader3);
                throw th22;
            }
            if (StorageManager.isFileEncrypted()) {
                i2 = 0;
            } else {
                i2 = 0;
                addFeature("android.software.file_based_encryption", 0);
                addFeature("android.software.securely_removes_users", 0);
            }
            if (StorageManager.hasAdoptable()) {
                addFeature("android.software.adoptable_storage", i2);
            }
            if (!ActivityManager.isLowRamDeviceStatic()) {
                addFeature("android.hardware.ram.low", i2);
            } else {
                addFeature("android.hardware.ram.normal", i2);
            }
            version = IncrementalManager.getVersion();
            if (version > 0) {
                addFeature("android.software.incremental_delivery", version);
            }
            addFeature("android.software.app_enumeration", i2);
            if (Build.VERSION.DEVICE_INITIAL_SDK_INT >= 29) {
                addFeature("android.software.ipsec_tunnels", i2);
            }
            enableIpSecTunnelMigrationOnVsrUAndAbove();
            if (isErofsSupported()) {
                if (isKernelVersionAtLeast(5, 10)) {
                    addFeature("android.software.erofs", i2);
                } else if (isKernelVersionAtLeast(4, 19)) {
                    addFeature("android.software.erofs_legacy", i2);
                }
            }
            it = this.mUnavailableFeatures.iterator();
            while (it.hasNext()) {
                removeFeature(it.next());
            }
            return;
            while (true) {
                XmlUtils.nextElement(xmlPullParser);
                if (xmlPullParser.getEventType() != i3) {
                    String name = xmlPullParser.getName();
                    if (name == null) {
                        XmlUtils.skipCurrentTag(xmlPullParser);
                    } else {
                        switch (name.hashCode()) {
                            case -2040330235:
                                if (name.equals("allow-unthrottled-location")) {
                                    c = 11;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1882490007:
                                if (name.equals("allow-in-power-save")) {
                                    c = '\t';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1582324217:
                                if (name.equals("allow-adas-location-settings")) {
                                    c = '\f';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1554938271:
                                if (name.equals("named-actor")) {
                                    c = 29;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1461465444:
                                if (name.equals("component-override")) {
                                    c = 18;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1390350881:
                                if (name.equals("install-constraints-allowed")) {
                                    c = '#';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1005864890:
                                if (name.equals("disabled-until-used-preinstalled-carrier-app")) {
                                    c = 21;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -980620291:
                                if (name.equals("allow-association")) {
                                    c = 25;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -979207434:
                                if (name.equals("feature")) {
                                    c = 6;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -972849788:
                                if (name.equals("automatic-rollback-denylisted-app")) {
                                    c = ' ';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -828905863:
                                if (name.equals("unavailable-feature")) {
                                    c = 7;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -642819164:
                                if (name.equals("allow-in-power-save-except-idle")) {
                                    c = '\b';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -634266752:
                                if (name.equals("bg-restriction-exemption")) {
                                    c = 16;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -560717308:
                                if (name.equals("allow-ignore-location-settings")) {
                                    c = '\r';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -517618225:
                                if (name.equals("permission")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -150068154:
                                if (name.equals("install-in-user-type")) {
                                    c = 28;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 98629247:
                                if (name.equals("group")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 166208699:
                                if (name.equals("library")) {
                                    c = 5;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 180165796:
                                if (name.equals("hidden-api-whitelisted-app")) {
                                    c = 24;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 347247519:
                                if (name.equals("backup-transport-whitelisted-service")) {
                                    c = 19;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 414198242:
                                if (name.equals("allowed-vendor-apex")) {
                                    c = '\"';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 783200107:
                                if (name.equals("update-ownership")) {
                                    c = '$';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 802332808:
                                if (name.equals("allow-in-data-usage-save")) {
                                    c = '\n';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 953292141:
                                if (name.equals("assign-permission")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 968751633:
                                if (name.equals("rollback-whitelisted-app")) {
                                    c = 31;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1005096720:
                                if (name.equals("apex-library")) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1044015374:
                                if (name.equals("oem-permissions")) {
                                    c = 23;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1046683496:
                                if (name.equals("whitelisted-staged-installer")) {
                                    c = '!';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1121420326:
                                if (name.equals("app-link")) {
                                    c = 15;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1269564002:
                                if (name.equals("split-permission")) {
                                    c = 3;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1347585732:
                                if (name.equals("app-data-isolation-whitelisted-app")) {
                                    c = 26;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1567330472:
                                if (name.equals("default-enabled-vr-app")) {
                                    c = 17;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1633270165:
                                if (name.equals("disabled-until-used-preinstalled-carrier-associated-app")) {
                                    c = 20;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1723146313:
                                if (name.equals("privapp-permissions")) {
                                    c = 22;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1723586945:
                                if (name.equals("bugreport-whitelisted")) {
                                    c = 27;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1748566401:
                                if (name.equals("initial-package-state")) {
                                    c = '%';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1793277898:
                                if (name.equals("overlay-config-signature")) {
                                    c = 30;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1954925533:
                                if (name.equals("allow-implicit-broadcast")) {
                                    c = 14;
                                    break;
                                }
                                c = 65535;
                                break;
                            default:
                                c = 65535;
                                break;
                        }
                        FileReader fileReader4 = fileReader2;
                        String str5 = str4;
                        boolean z17 = isLowRamDeviceStatic;
                        boolean z18 = z11;
                        boolean z19 = z10;
                        boolean z20 = z9;
                        String str6 = null;
                        switch (c) {
                            case 0:
                                z13 = z12;
                                if (z) {
                                    String attributeValue = xmlPullParser.getAttributeValue(null, "gid");
                                    if (attributeValue != null) {
                                        this.mGlobalGids = ArrayUtils.appendInt(this.mGlobalGids, Process.getGidForName(attributeValue));
                                    } else {
                                        Slog.w("SystemConfig", "<" + name + "> without gid in " + file + " at " + xmlPullParser.getPositionDescription());
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                break;
                            case 1:
                                z13 = z12;
                                if (z4) {
                                    String attributeValue2 = xmlPullParser.getAttributeValue(null, "name");
                                    if (attributeValue2 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without name in " + file + " at " + xmlPullParser.getPositionDescription());
                                        XmlUtils.skipCurrentTag(xmlPullParser);
                                        break;
                                    } else {
                                        readPermission(xmlPullParser, attributeValue2.intern());
                                        break;
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                    XmlUtils.skipCurrentTag(xmlPullParser);
                                    break;
                                }
                            case 2:
                                z13 = z12;
                                if (z4) {
                                    String attributeValue3 = xmlPullParser.getAttributeValue(null, "name");
                                    if (attributeValue3 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without name in " + file + " at " + xmlPullParser.getPositionDescription());
                                        XmlUtils.skipCurrentTag(xmlPullParser);
                                        break;
                                    } else {
                                        String attributeValue4 = xmlPullParser.getAttributeValue(null, "uid");
                                        if (attributeValue4 == null) {
                                            Slog.w("SystemConfig", "<" + name + "> without uid in " + file + " at " + xmlPullParser.getPositionDescription());
                                            XmlUtils.skipCurrentTag(xmlPullParser);
                                            break;
                                        } else {
                                            int uidForName = Process.getUidForName(attributeValue4);
                                            if (uidForName < 0) {
                                                Slog.w("SystemConfig", "<" + name + "> with unknown uid \"" + attributeValue4 + "  in " + file + " at " + xmlPullParser.getPositionDescription());
                                                XmlUtils.skipCurrentTag(xmlPullParser);
                                                break;
                                            } else {
                                                String intern = attributeValue3.intern();
                                                ArraySet<String> arraySet = this.mSystemPermissions.get(uidForName);
                                                if (arraySet == null) {
                                                    arraySet = new ArraySet<>();
                                                    this.mSystemPermissions.put(uidForName, arraySet);
                                                }
                                                arraySet.add(intern);
                                            }
                                        }
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                break;
                            case 3:
                                z13 = z12;
                                if (z4) {
                                    readSplitPermission(xmlPullParser, file);
                                    break;
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                    XmlUtils.skipCurrentTag(xmlPullParser);
                                    break;
                                }
                            case 4:
                            case 5:
                                if (z2) {
                                    String attributeValue5 = xmlPullParser.getAttributeValue(null, "name");
                                    String attributeValue6 = xmlPullParser.getAttributeValue(null, "file");
                                    String attributeValue7 = xmlPullParser.getAttributeValue(null, "dependency");
                                    String attributeValue8 = xmlPullParser.getAttributeValue(null, "min-device-sdk");
                                    z13 = z12;
                                    String attributeValue9 = xmlPullParser.getAttributeValue(null, "max-device-sdk");
                                    if (attributeValue5 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without name in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else if (attributeValue6 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without file in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        if (attributeValue8 != null && !isAtLeastSdkLevel(attributeValue8)) {
                                            z14 = false;
                                            if (attributeValue9 != null && !isAtMostSdkLevel(attributeValue9)) {
                                                z15 = false;
                                                exists = new File(attributeValue6).exists();
                                                if (!z14 && z15 && exists) {
                                                    this.mSharedLibraries.put(attributeValue5, new SharedLibraryEntry(attributeValue5, attributeValue6, attributeValue7 == null ? new String[0] : attributeValue7.split(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR), xmlPullParser.getAttributeValue(null, "on-bootclasspath-since"), xmlPullParser.getAttributeValue(null, "on-bootclasspath-before")));
                                                } else {
                                                    StringBuilder sb = new StringBuilder("Ignore shared library ");
                                                    sb.append(attributeValue5);
                                                    sb.append(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                                    if (!z14) {
                                                        sb.append(" min-device-sdk=");
                                                        sb.append(attributeValue8);
                                                    }
                                                    if (!z15) {
                                                        sb.append(" max-device-sdk=");
                                                        sb.append(attributeValue9);
                                                    }
                                                    if (!exists) {
                                                        sb.append(" ");
                                                        sb.append(attributeValue6);
                                                        sb.append(" does not exist");
                                                    }
                                                    Slog.i("SystemConfig", sb.toString());
                                                }
                                            }
                                            z15 = true;
                                            exists = new File(attributeValue6).exists();
                                            if (!z14) {
                                            }
                                            StringBuilder sb2 = new StringBuilder("Ignore shared library ");
                                            sb2.append(attributeValue5);
                                            sb2.append(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                            if (!z14) {
                                            }
                                            if (!z15) {
                                            }
                                            if (!exists) {
                                            }
                                            Slog.i("SystemConfig", sb2.toString());
                                        }
                                        z14 = true;
                                        if (attributeValue9 != null) {
                                            z15 = false;
                                            exists = new File(attributeValue6).exists();
                                            if (!z14) {
                                            }
                                            StringBuilder sb22 = new StringBuilder("Ignore shared library ");
                                            sb22.append(attributeValue5);
                                            sb22.append(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                            if (!z14) {
                                            }
                                            if (!z15) {
                                            }
                                            if (!exists) {
                                            }
                                            Slog.i("SystemConfig", sb22.toString());
                                        }
                                        z15 = true;
                                        exists = new File(attributeValue6).exists();
                                        if (!z14) {
                                        }
                                        StringBuilder sb222 = new StringBuilder("Ignore shared library ");
                                        sb222.append(attributeValue5);
                                        sb222.append(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                                        if (!z14) {
                                        }
                                        if (!z15) {
                                        }
                                        if (!exists) {
                                        }
                                        Slog.i("SystemConfig", sb222.toString());
                                    }
                                } else {
                                    z13 = z12;
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                break;
                            case 6:
                                if (z3) {
                                    String attributeValue10 = xmlPullParser.getAttributeValue(null, "name");
                                    int readIntAttribute = XmlUtils.readIntAttribute(xmlPullParser, "version", 0);
                                    boolean z21 = !z17 ? true : !"true".equals(xmlPullParser.getAttributeValue(null, "notLowRam"));
                                    if (attributeValue10 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without name in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else if (z21) {
                                        addFeature(attributeValue10, readIntAttribute);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 7:
                                if (z3) {
                                    String attributeValue11 = xmlPullParser.getAttributeValue(null, "name");
                                    if (attributeValue11 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without name in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mUnavailableFeatures.add(attributeValue11);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\b':
                                if (z19) {
                                    String attributeValue12 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue12 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mAllowInPowerSaveExceptIdle.add(attributeValue12);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\t':
                                if (z19) {
                                    String attributeValue13 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue13 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mAllowInPowerSave.add(attributeValue13);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\n':
                                if (z19) {
                                    String attributeValue14 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue14 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mAllowInDataUsageSave.add(attributeValue14);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 11:
                                if (z19) {
                                    String attributeValue15 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue15 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mAllowUnthrottledLocation.add(attributeValue15);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\f':
                                if (z19) {
                                    String attributeValue16 = xmlPullParser.getAttributeValue(null, "package");
                                    String attributeValue17 = xmlPullParser.getAttributeValue(null, "attributionTag");
                                    if (attributeValue16 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        ArraySet<String> arraySet2 = this.mAllowAdasSettings.get(attributeValue16);
                                        if (arraySet2 == null || !arraySet2.isEmpty()) {
                                            if (arraySet2 == null) {
                                                arraySet2 = new ArraySet<>(1);
                                                this.mAllowAdasSettings.put(attributeValue16, arraySet2);
                                            }
                                            if (!"*".equals(attributeValue17)) {
                                                if (!"null".equals(attributeValue17)) {
                                                    str6 = attributeValue17;
                                                }
                                                arraySet2.add(str6);
                                            }
                                        }
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\r':
                                if (z19) {
                                    String attributeValue18 = xmlPullParser.getAttributeValue(null, "package");
                                    String attributeValue19 = xmlPullParser.getAttributeValue(null, "attributionTag");
                                    if (attributeValue18 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        ArraySet<String> arraySet3 = this.mAllowIgnoreLocationSettings.get(attributeValue18);
                                        if (arraySet3 == null || !arraySet3.isEmpty()) {
                                            if (arraySet3 == null) {
                                                arraySet3 = new ArraySet<>(1);
                                                this.mAllowIgnoreLocationSettings.put(attributeValue18, arraySet3);
                                            }
                                            if (!"*".equals(attributeValue19)) {
                                                if (!"null".equals(attributeValue19)) {
                                                    str6 = attributeValue19;
                                                }
                                                arraySet3.add(str6);
                                            }
                                        }
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 14:
                                if (z18) {
                                    String attributeValue20 = xmlPullParser.getAttributeValue(null, "action");
                                    if (attributeValue20 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without action in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mAllowImplicitBroadcasts.add(attributeValue20);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 15:
                                if (z5) {
                                    String attributeValue21 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue21 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mLinkedApps.add(attributeValue21);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 16:
                                if (z19) {
                                    String attributeValue22 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue22 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mBgRestrictionExemption.add(attributeValue22);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 17:
                                if (z5) {
                                    String attributeValue23 = xmlPullParser.getAttributeValue(null, "package");
                                    String attributeValue24 = xmlPullParser.getAttributeValue(null, "class");
                                    if (attributeValue23 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else if (attributeValue24 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without class in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mDefaultVrComponents.add(new ComponentName(attributeValue23, attributeValue24));
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 18:
                                readComponentOverrides(xmlPullParser, file);
                                z13 = z12;
                                break;
                            case 19:
                                if (z3) {
                                    String attributeValue25 = xmlPullParser.getAttributeValue(null, "service");
                                    if (attributeValue25 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without service in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue25);
                                        if (unflattenFromString == null) {
                                            Slog.w("SystemConfig", "<" + name + "> with invalid service name " + attributeValue25 + " in " + file + " at " + xmlPullParser.getPositionDescription());
                                        } else {
                                            this.mBackupTransportWhitelist.add(unflattenFromString);
                                        }
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 20:
                                if (z5) {
                                    String attributeValue26 = xmlPullParser.getAttributeValue(null, "package");
                                    String attributeValue27 = xmlPullParser.getAttributeValue(null, "carrierAppPackage");
                                    if (attributeValue26 != null && attributeValue27 != null) {
                                        String attributeValue28 = xmlPullParser.getAttributeValue(null, "addedInSdk");
                                        if (TextUtils.isEmpty(attributeValue28)) {
                                            parseInt = -1;
                                        } else {
                                            try {
                                                parseInt = Integer.parseInt(attributeValue28);
                                            } catch (NumberFormatException unused) {
                                                Slog.w("SystemConfig", "<" + name + "> addedInSdk not an integer in " + file + " at " + xmlPullParser.getPositionDescription());
                                                XmlUtils.skipCurrentTag(xmlPullParser);
                                                z13 = z12;
                                                z12 = z13;
                                                fileReader2 = fileReader4;
                                                str4 = str5;
                                                isLowRamDeviceStatic = z17;
                                                z11 = z18;
                                                z10 = z19;
                                                z9 = z20;
                                                i3 = 1;
                                            }
                                        }
                                        List<CarrierAssociatedAppEntry> list = this.mDisabledUntilUsedPreinstalledCarrierAssociatedApps.get(attributeValue27);
                                        if (list == null) {
                                            list = new ArrayList<>();
                                            this.mDisabledUntilUsedPreinstalledCarrierAssociatedApps.put(attributeValue27, list);
                                        }
                                        list.add(new CarrierAssociatedAppEntry(attributeValue26, parseInt));
                                    }
                                    Slog.w("SystemConfig", "<" + name + "> without package or carrierAppPackage in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                            case 21:
                                if (z5) {
                                    String attributeValue29 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue29 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mDisabledUntilUsedPreinstalledCarrierApps.add(attributeValue29);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 22:
                                if (z6) {
                                    if (!file.toPath().startsWith(Environment.getVendorDirectory().toPath() + "/")) {
                                        if (!file.toPath().startsWith(Environment.getOdmDirectory().toPath() + "/")) {
                                            z16 = false;
                                            boolean startsWith = file.toPath().startsWith(Environment.getProductDirectory().toPath() + "/");
                                            boolean startsWith2 = file.toPath().startsWith(Environment.getSystemExtDirectory().toPath() + "/");
                                            Path path = file.toPath();
                                            StringBuilder sb3 = new StringBuilder();
                                            sb3.append(Environment.getApexDirectory().toPath());
                                            sb3.append("/");
                                            boolean z22 = !path.startsWith(sb3.toString()) && ((Boolean) ApexProperties.updatable().orElse(Boolean.FALSE)).booleanValue();
                                            if (!z16) {
                                                readPrivAppPermissions(xmlPullParser, this.mPermissionAllowlist.getVendorPrivilegedAppAllowlist());
                                            } else if (startsWith) {
                                                readPrivAppPermissions(xmlPullParser, this.mPermissionAllowlist.getProductPrivilegedAppAllowlist());
                                            } else if (startsWith2) {
                                                readPrivAppPermissions(xmlPullParser, this.mPermissionAllowlist.getSystemExtPrivilegedAppAllowlist());
                                            } else if (z22) {
                                                readApexPrivAppPermissions(xmlPullParser, file, Environment.getApexDirectory().toPath());
                                            } else {
                                                readPrivAppPermissions(xmlPullParser, this.mPermissionAllowlist.getPrivilegedAppAllowlist());
                                            }
                                        }
                                    }
                                    z16 = true;
                                    boolean startsWith3 = file.toPath().startsWith(Environment.getProductDirectory().toPath() + "/");
                                    boolean startsWith22 = file.toPath().startsWith(Environment.getSystemExtDirectory().toPath() + "/");
                                    Path path2 = file.toPath();
                                    StringBuilder sb32 = new StringBuilder();
                                    sb32.append(Environment.getApexDirectory().toPath());
                                    sb32.append("/");
                                    if (path2.startsWith(sb32.toString())) {
                                    }
                                    if (!z16) {
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                    XmlUtils.skipCurrentTag(xmlPullParser);
                                }
                                z13 = z12;
                                break;
                            case 23:
                                if (z7) {
                                    readOemPermissions(xmlPullParser);
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                    XmlUtils.skipCurrentTag(xmlPullParser);
                                }
                                z13 = z12;
                                break;
                            case 24:
                                if (z8) {
                                    String attributeValue30 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue30 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mHiddenApiPackageWhitelist.add(attributeValue30);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 25:
                                if (z20) {
                                    String attributeValue31 = xmlPullParser.getAttributeValue(null, "target");
                                    if (attributeValue31 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without target in " + file + " at " + xmlPullParser.getPositionDescription());
                                        XmlUtils.skipCurrentTag(xmlPullParser);
                                    } else {
                                        String attributeValue32 = xmlPullParser.getAttributeValue(null, "allowed");
                                        if (attributeValue32 == null) {
                                            Slog.w("SystemConfig", "<" + name + "> without allowed in " + file + " at " + xmlPullParser.getPositionDescription());
                                            XmlUtils.skipCurrentTag(xmlPullParser);
                                        } else {
                                            String intern2 = attributeValue31.intern();
                                            String intern3 = attributeValue32.intern();
                                            ArraySet<String> arraySet4 = this.mAllowedAssociations.get(intern2);
                                            if (arraySet4 == null) {
                                                arraySet4 = new ArraySet<>();
                                                this.mAllowedAssociations.put(intern2, arraySet4);
                                            }
                                            Slog.i("SystemConfig", "Adding association: " + intern2 + " <- " + intern3);
                                            arraySet4.add(intern3);
                                        }
                                    }
                                    z13 = z12;
                                    break;
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                            case 26:
                                String attributeValue33 = xmlPullParser.getAttributeValue(null, "package");
                                if (attributeValue33 == null) {
                                    Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    this.mAppDataIsolationWhitelistedApps.add(attributeValue33);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 27:
                                String attributeValue34 = xmlPullParser.getAttributeValue(null, "package");
                                if (attributeValue34 == null) {
                                    Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    this.mBugreportWhitelistedPackages.add(attributeValue34);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 28:
                                readInstallInUserType(xmlPullParser, this.mPackageToUserTypeWhitelist, this.mPackageToUserTypeBlacklist);
                                z13 = z12;
                                break;
                            case 29:
                                String safeIntern = TextUtils.safeIntern(xmlPullParser.getAttributeValue(null, "namespace"));
                                String attributeValue35 = xmlPullParser.getAttributeValue(null, "name");
                                String safeIntern2 = TextUtils.safeIntern(xmlPullParser.getAttributeValue(null, "package"));
                                if (TextUtils.isEmpty(safeIntern)) {
                                    Slog.wtf("SystemConfig", "<" + name + "> without namespace in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (TextUtils.isEmpty(attributeValue35)) {
                                    Slog.wtf("SystemConfig", "<" + name + "> without actor name in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (TextUtils.isEmpty(safeIntern2)) {
                                    Slog.wtf("SystemConfig", "<" + name + "> without package name in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (PackageManagerShellCommandDataLoader.PACKAGE.equalsIgnoreCase(safeIntern)) {
                                    throw new IllegalStateException("Defining " + attributeValue35 + " as " + safeIntern2 + " for the android namespace is not allowed");
                                } else {
                                    if (this.mNamedActors == null) {
                                        this.mNamedActors = new ArrayMap();
                                    }
                                    Map<String, String> map = this.mNamedActors.get(safeIntern);
                                    if (map == null) {
                                        map = new ArrayMap<>();
                                        this.mNamedActors.put(safeIntern, map);
                                    } else if (map.containsKey(attributeValue35)) {
                                        throw new IllegalStateException("Duplicate actor definition for " + safeIntern + "/" + attributeValue35 + "; defined as both " + map.get(attributeValue35) + " and " + safeIntern2);
                                    }
                                    map.put(attributeValue35, safeIntern2);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 30:
                                if (z) {
                                    String attributeValue36 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue36 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else if (TextUtils.isEmpty(this.mOverlayConfigSignaturePackage)) {
                                        this.mOverlayConfigSignaturePackage = attributeValue36.intern();
                                    } else {
                                        throw new IllegalStateException("Reference signature package defined as both " + this.mOverlayConfigSignaturePackage + " and " + attributeValue36);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case 31:
                                String attributeValue37 = xmlPullParser.getAttributeValue(null, "package");
                                if (attributeValue37 == null) {
                                    Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    this.mRollbackWhitelistedPackages.add(attributeValue37);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case ' ':
                                String attributeValue38 = xmlPullParser.getAttributeValue(null, "package");
                                if (attributeValue38 == null) {
                                    Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    this.mAutomaticRollbackDenylistedPackages.add(attributeValue38);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '!':
                                if (z5) {
                                    String attributeValue39 = xmlPullParser.getAttributeValue(null, "package");
                                    boolean readBooleanAttribute = XmlUtils.readBooleanAttribute(xmlPullParser, "isModulesInstaller", false);
                                    if (attributeValue39 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mWhitelistedStagedInstallers.add(attributeValue39);
                                    }
                                    if (readBooleanAttribute) {
                                        if (this.mModulesInstallerPackageName != null) {
                                            throw new IllegalStateException("Multiple modules installers");
                                        }
                                        this.mModulesInstallerPackageName = attributeValue39;
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '\"':
                                if (z12) {
                                    String attributeValue40 = xmlPullParser.getAttributeValue(null, "package");
                                    String attributeValue41 = xmlPullParser.getAttributeValue(null, "installerPackage");
                                    if (attributeValue40 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    }
                                    if (attributeValue41 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without installerPackage in " + file + " at " + xmlPullParser.getPositionDescription());
                                    }
                                    if (attributeValue40 != null && attributeValue41 != null) {
                                        this.mAllowedVendorApexes.put(attributeValue40, attributeValue41);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '#':
                                if (z5) {
                                    String attributeValue42 = xmlPullParser.getAttributeValue(null, "package");
                                    if (attributeValue42 == null) {
                                        Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                    } else {
                                        this.mInstallConstraintsAllowlist.add(attributeValue42);
                                    }
                                } else {
                                    logNotAllowedInPartition(name, file, xmlPullParser);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '$':
                                String attributeValue43 = xmlPullParser.getAttributeValue(null, "package");
                                String attributeValue44 = xmlPullParser.getAttributeValue(null, "installer");
                                if (TextUtils.isEmpty(attributeValue43)) {
                                    Slog.w("SystemConfig", "<" + name + "> without valid package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (TextUtils.isEmpty(attributeValue44)) {
                                    Slog.w("SystemConfig", "<" + name + "> without valid installer in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else {
                                    this.mUpdateOwnersForSystemApps.put(attributeValue43, attributeValue44);
                                }
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                z13 = z12;
                                break;
                            case '%':
                                String attributeValue45 = xmlPullParser.getAttributeValue(null, "package");
                                String attributeValue46 = xmlPullParser.getAttributeValue(null, "stopped");
                                if (TextUtils.isEmpty(attributeValue45)) {
                                    Slog.w("SystemConfig", "<" + name + "> without package in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (TextUtils.isEmpty(attributeValue46)) {
                                    Slog.w("SystemConfig", "<" + name + "> without stopped in " + file + " at " + xmlPullParser.getPositionDescription());
                                } else if (!Boolean.parseBoolean(attributeValue46)) {
                                    this.mInitialNonStoppedSystemPackages.add(attributeValue45);
                                }
                            default:
                                z13 = z12;
                                Slog.w("SystemConfig", "Tag " + name + " is unknown in " + file + " at " + xmlPullParser.getPositionDescription());
                                XmlUtils.skipCurrentTag(xmlPullParser);
                                break;
                        }
                        z12 = z13;
                        fileReader2 = fileReader4;
                        str4 = str5;
                        isLowRamDeviceStatic = z17;
                        z11 = z18;
                        z10 = z19;
                        z9 = z20;
                        i3 = 1;
                    }
                } else {
                    IoUtils.closeQuietly(fileReader2);
                }
            }
        } catch (FileNotFoundException unused2) {
            Slog.w("SystemConfig", "Couldn't find or open permissions file " + file);
        }
    }

    public final void enableIpSecTunnelMigrationOnVsrUAndAbove() {
        if (SystemProperties.getInt("ro.vendor.api_level", Build.VERSION.DEVICE_INITIAL_SDK_INT) > 33) {
            addFeature("android.software.ipsec_tunnel_migration", 0);
        }
    }

    public final void addFeature(String str, int i) {
        FeatureInfo featureInfo = this.mAvailableFeatures.get(str);
        if (featureInfo == null) {
            FeatureInfo featureInfo2 = new FeatureInfo();
            featureInfo2.name = str;
            featureInfo2.version = i;
            this.mAvailableFeatures.put(str, featureInfo2);
            return;
        }
        featureInfo.version = Math.max(featureInfo.version, i);
    }

    public final void removeFeature(String str) {
        if (this.mAvailableFeatures.remove(str) != null) {
            Slog.d("SystemConfig", "Removed unavailable feature " + str);
        }
    }

    public void readPermission(XmlPullParser xmlPullParser, String str) throws IOException, XmlPullParserException {
        if (this.mPermissions.containsKey(str)) {
            throw new IllegalStateException("Duplicate permission definition for " + str);
        }
        PermissionEntry permissionEntry = new PermissionEntry(str, XmlUtils.readBooleanAttribute(xmlPullParser, "perUser", false));
        this.mPermissions.put(str, permissionEntry);
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
                if ("group".equals(xmlPullParser.getName())) {
                    String attributeValue = xmlPullParser.getAttributeValue(null, "gid");
                    if (attributeValue != null) {
                        permissionEntry.gids = ArrayUtils.appendInt(permissionEntry.gids, Process.getGidForName(attributeValue));
                    } else {
                        Slog.w("SystemConfig", "<group> without gid at " + xmlPullParser.getPositionDescription());
                    }
                }
                XmlUtils.skipCurrentTag(xmlPullParser);
            }
        }
    }

    public final void readPrivAppPermissions(XmlPullParser xmlPullParser, ArrayMap<String, ArrayMap<String, Boolean>> arrayMap) throws IOException, XmlPullParserException {
        readPermissionAllowlist(xmlPullParser, arrayMap, "privapp-permissions");
    }

    public final void readInstallInUserType(XmlPullParser xmlPullParser, Map<String, Set<String>> map, Map<String, Set<String>> map2) throws IOException, XmlPullParserException {
        String attributeValue = xmlPullParser.getAttributeValue(null, "package");
        if (TextUtils.isEmpty(attributeValue)) {
            Slog.w("SystemConfig", "package is required for <install-in-user-type> in " + xmlPullParser.getPositionDescription());
            return;
        }
        Set<String> set = map.get(attributeValue);
        Set<String> set2 = map2.get(attributeValue);
        int depth = xmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            String name = xmlPullParser.getName();
            if ("install-in".equals(name)) {
                String attributeValue2 = xmlPullParser.getAttributeValue(null, "user-type");
                if (TextUtils.isEmpty(attributeValue2)) {
                    Slog.w("SystemConfig", "user-type is required for <install-in-user-type> in " + xmlPullParser.getPositionDescription());
                } else {
                    if (set == null) {
                        set = new ArraySet<>();
                        map.put(attributeValue, set);
                    }
                    set.add(attributeValue2);
                }
            } else if ("do-not-install-in".equals(name)) {
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "user-type");
                if (TextUtils.isEmpty(attributeValue3)) {
                    Slog.w("SystemConfig", "user-type is required for <install-in-user-type> in " + xmlPullParser.getPositionDescription());
                } else {
                    if (set2 == null) {
                        set2 = new ArraySet<>();
                        map2.put(attributeValue, set2);
                    }
                    set2.add(attributeValue3);
                }
            } else {
                Slog.w("SystemConfig", "unrecognized tag in <install-in-user-type> in " + xmlPullParser.getPositionDescription());
            }
        }
    }

    public void readOemPermissions(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        readPermissionAllowlist(xmlPullParser, this.mPermissionAllowlist.getOemAppAllowlist(), "oem-permissions");
    }

    public static void readPermissionAllowlist(XmlPullParser xmlPullParser, ArrayMap<String, ArrayMap<String, Boolean>> arrayMap, String str) throws IOException, XmlPullParserException {
        String attributeValue = xmlPullParser.getAttributeValue(null, "package");
        if (TextUtils.isEmpty(attributeValue)) {
            Slog.w("SystemConfig", "package is required for <" + str + "> in " + xmlPullParser.getPositionDescription());
            return;
        }
        ArrayMap<String, Boolean> arrayMap2 = arrayMap.get(attributeValue);
        if (arrayMap2 == null) {
            arrayMap2 = new ArrayMap<>();
        }
        int depth = xmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            String name = xmlPullParser.getName();
            if ("permission".equals(name)) {
                String attributeValue2 = xmlPullParser.getAttributeValue(null, "name");
                if (TextUtils.isEmpty(attributeValue2)) {
                    Slog.w("SystemConfig", "name is required for <permission> in " + xmlPullParser.getPositionDescription());
                } else {
                    arrayMap2.put(attributeValue2, Boolean.TRUE);
                }
            } else if ("deny-permission".equals(name)) {
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "name");
                if (TextUtils.isEmpty(attributeValue3)) {
                    Slog.w("SystemConfig", "name is required for <deny-permission> in " + xmlPullParser.getPositionDescription());
                } else {
                    arrayMap2.put(attributeValue3, Boolean.FALSE);
                }
            }
        }
        arrayMap.put(attributeValue, arrayMap2);
    }

    public final void readSplitPermission(XmlPullParser xmlPullParser, File file) throws IOException, XmlPullParserException {
        int parseInt;
        String attributeValue = xmlPullParser.getAttributeValue(null, "name");
        if (attributeValue == null) {
            Slog.w("SystemConfig", "<split-permission> without name in " + file + " at " + xmlPullParser.getPositionDescription());
            XmlUtils.skipCurrentTag(xmlPullParser);
            return;
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "targetSdk");
        if (TextUtils.isEmpty(attributeValue2)) {
            parseInt = FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG;
        } else {
            try {
                parseInt = Integer.parseInt(attributeValue2);
            } catch (NumberFormatException unused) {
                Slog.w("SystemConfig", "<split-permission> targetSdk not an integer in " + file + " at " + xmlPullParser.getPositionDescription());
                XmlUtils.skipCurrentTag(xmlPullParser);
                return;
            }
        }
        int depth = xmlPullParser.getDepth();
        ArrayList arrayList = new ArrayList();
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            if ("new-permission".equals(xmlPullParser.getName())) {
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "name");
                if (TextUtils.isEmpty(attributeValue3)) {
                    Slog.w("SystemConfig", "name is required for <new-permission> in " + xmlPullParser.getPositionDescription());
                } else {
                    arrayList.add(attributeValue3);
                }
            } else {
                XmlUtils.skipCurrentTag(xmlPullParser);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        this.mSplitPermissions.add(new PermissionManager.SplitPermissionInfo(attributeValue, arrayList, parseInt));
    }

    public final void readComponentOverrides(XmlPullParser xmlPullParser, File file) throws IOException, XmlPullParserException {
        String attributeValue = xmlPullParser.getAttributeValue(null, "package");
        if (attributeValue == null) {
            Slog.w("SystemConfig", "<component-override> without package in " + file + " at " + xmlPullParser.getPositionDescription());
            return;
        }
        String intern = attributeValue.intern();
        int depth = xmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            if ("component".equals(xmlPullParser.getName())) {
                String attributeValue2 = xmlPullParser.getAttributeValue(null, "class");
                String attributeValue3 = xmlPullParser.getAttributeValue(null, "enabled");
                if (attributeValue2 == null) {
                    Slog.w("SystemConfig", "<component> without class in " + file + " at " + xmlPullParser.getPositionDescription());
                    return;
                } else if (attributeValue3 == null) {
                    Slog.w("SystemConfig", "<component> without enabled in " + file + " at " + xmlPullParser.getPositionDescription());
                    return;
                } else {
                    if (attributeValue2.startsWith(".")) {
                        attributeValue2 = intern + attributeValue2;
                    }
                    String intern2 = attributeValue2.intern();
                    ArrayMap<String, Boolean> arrayMap = this.mPackageComponentEnabledState.get(intern);
                    if (arrayMap == null) {
                        arrayMap = new ArrayMap<>();
                        this.mPackageComponentEnabledState.put(intern, arrayMap);
                    }
                    arrayMap.put(intern2, Boolean.valueOf(!"false".equals(attributeValue3)));
                }
            }
        }
    }

    public final void readPublicNativeLibrariesList() {
        readPublicLibrariesListFile(new File("/vendor/etc/public.libraries.txt"));
        String[] strArr = {"/system/etc", "/system_ext/etc", "/product/etc"};
        for (int i = 0; i < 3; i++) {
            String str = strArr[i];
            File[] listFiles = new File(str).listFiles();
            if (listFiles == null) {
                Slog.w("SystemConfig", "Public libraries file folder missing: " + str);
            } else {
                for (File file : listFiles) {
                    String name = file.getName();
                    if (name.startsWith("public.libraries-") && name.endsWith(".txt")) {
                        readPublicLibrariesListFile(file);
                    }
                }
            }
        }
    }

    public final void readPublicLibrariesListFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    if (!readLine.isEmpty() && !readLine.startsWith("#")) {
                        String str = readLine.trim().split(" ")[0];
                        SharedLibraryEntry sharedLibraryEntry = new SharedLibraryEntry(str, str, new String[0], true);
                        this.mSharedLibraries.put(sharedLibraryEntry.name, sharedLibraryEntry);
                    }
                } else {
                    bufferedReader.close();
                    return;
                }
            }
        } catch (IOException e) {
            Slog.w("SystemConfig", "Failed to read public libraries file " + file, e);
        }
    }

    public final String getApexModuleNameFromFilePath(Path path, Path path2) {
        if (!path.startsWith(path2)) {
            throw new IllegalArgumentException("File " + path + " is not part of an APEX.");
        } else if (path.getNameCount() <= path2.getNameCount() + 1) {
            throw new IllegalArgumentException("File " + path + " is in the APEX partition, but not inside a module.");
        } else {
            return path.getName(path2.getNameCount()).toString();
        }
    }

    @VisibleForTesting
    public void readApexPrivAppPermissions(XmlPullParser xmlPullParser, File file, Path path) throws IOException, XmlPullParserException {
        String apexModuleNameFromFilePath = getApexModuleNameFromFilePath(file.toPath(), path);
        ArrayMap<String, ArrayMap<String, ArrayMap<String, Boolean>>> apexPrivilegedAppAllowlists = this.mPermissionAllowlist.getApexPrivilegedAppAllowlists();
        ArrayMap<String, ArrayMap<String, Boolean>> arrayMap = apexPrivilegedAppAllowlists.get(apexModuleNameFromFilePath);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            apexPrivilegedAppAllowlists.put(apexModuleNameFromFilePath, arrayMap);
        }
        readPrivAppPermissions(xmlPullParser, arrayMap);
    }

    public static boolean isSystemProcess() {
        return Process.myUid() == 1000;
    }

    public static boolean isErofsSupported() {
        try {
            return Files.exists(Paths.get("/sys/fs/erofs", new String[0]), new LinkOption[0]);
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isKernelVersionAtLeast(int i, int i2) {
        String[] split = VintfRuntimeInfo.getKernelVersion().split("\\.");
        if (split.length < 2) {
            return false;
        }
        try {
            int parseInt = Integer.parseInt(split[0]);
            return parseInt > i || (parseInt == i && Integer.parseInt(split[1]) >= i2);
        } catch (NumberFormatException unused) {
            return false;
        }
    }
}
