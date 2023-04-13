package com.android.server.backup.restore;

import android.app.IBackupAgent;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.Settings;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.FileMetadata;
import com.android.server.backup.KeyValueAdbRestoreEngine;
import com.android.server.backup.OperationStorage;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.FullBackupObbConnection;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.BytesReadListener;
import com.android.server.backup.utils.FullBackupRestoreObserverUtils;
import com.android.server.backup.utils.RestoreUtils;
import com.android.server.backup.utils.TarBackupReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
/* loaded from: classes.dex */
public class FullRestoreEngine extends RestoreEngine {
    public IBackupAgent mAgent;
    public String mAgentPackage;
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public final boolean mAllowApks;
    public long mAppVersion;
    public final BackupEligibilityRules mBackupEligibilityRules;
    public final UserBackupManagerService mBackupManagerService;
    public final byte[] mBuffer;
    public final HashSet<String> mClearedPackages;
    public final RestoreDeleteObserver mDeleteObserver;
    public final int mEphemeralOpToken;
    public final boolean mIsAdbRestore;
    public final HashMap<String, Signature[]> mManifestSignatures;
    public final IBackupManagerMonitor mMonitor;
    public final BackupRestoreTask mMonitorTask;
    public FullBackupObbConnection mObbConnection;
    public IFullBackupRestoreObserver mObserver;
    public final PackageInfo mOnlyPackage;
    public final OperationStorage mOperationStorage;
    public final HashMap<String, String> mPackageInstallers;
    public final HashMap<String, RestorePolicy> mPackagePolicies;
    public ParcelFileDescriptor[] mPipes;
    @GuardedBy({"mPipesLock"})
    public boolean mPipesClosed;
    public final Object mPipesLock;
    public FileMetadata mReadOnlyParent;
    public ApplicationInfo mTargetApp;
    public final int mUserId;
    public byte[] mWidgetData;

    public static /* synthetic */ void lambda$restoreOneFile$0(long j) {
    }

    public FullRestoreEngine(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, BackupRestoreTask backupRestoreTask, IFullBackupRestoreObserver iFullBackupRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, PackageInfo packageInfo, boolean z, int i, boolean z2, BackupEligibilityRules backupEligibilityRules) {
        this.mDeleteObserver = new RestoreDeleteObserver();
        this.mObbConnection = null;
        this.mPackagePolicies = new HashMap<>();
        this.mPackageInstallers = new HashMap<>();
        this.mManifestSignatures = new HashMap<>();
        this.mClearedPackages = new HashSet<>();
        this.mPipes = null;
        this.mPipesLock = new Object();
        this.mWidgetData = null;
        this.mReadOnlyParent = null;
        this.mBackupManagerService = userBackupManagerService;
        this.mOperationStorage = operationStorage;
        this.mEphemeralOpToken = i;
        this.mMonitorTask = backupRestoreTask;
        this.mObserver = iFullBackupRestoreObserver;
        this.mMonitor = iBackupManagerMonitor;
        this.mOnlyPackage = packageInfo;
        this.mAllowApks = z;
        this.mBuffer = new byte[32768];
        BackupAgentTimeoutParameters agentTimeoutParameters = userBackupManagerService.getAgentTimeoutParameters();
        Objects.requireNonNull(agentTimeoutParameters, "Timeout parameters cannot be null");
        this.mAgentTimeoutParameters = agentTimeoutParameters;
        this.mIsAdbRestore = z2;
        this.mUserId = userBackupManagerService.getUserId();
        this.mBackupEligibilityRules = backupEligibilityRules;
    }

    @VisibleForTesting
    public FullRestoreEngine() {
        this.mDeleteObserver = new RestoreDeleteObserver();
        this.mObbConnection = null;
        this.mPackagePolicies = new HashMap<>();
        this.mPackageInstallers = new HashMap<>();
        this.mManifestSignatures = new HashMap<>();
        this.mClearedPackages = new HashSet<>();
        this.mPipes = null;
        this.mPipesLock = new Object();
        this.mWidgetData = null;
        this.mReadOnlyParent = null;
        this.mIsAdbRestore = false;
        this.mAllowApks = false;
        this.mEphemeralOpToken = 0;
        this.mUserId = 0;
        this.mBackupEligibilityRules = null;
        this.mAgentTimeoutParameters = null;
        this.mBuffer = null;
        this.mBackupManagerService = null;
        this.mOperationStorage = null;
        this.mMonitor = null;
        this.mMonitorTask = null;
        this.mOnlyPackage = null;
    }

    public IBackupAgent getAgent() {
        return this.mAgent;
    }

    public byte[] getWidgetData() {
        return this.mWidgetData;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0404 A[Catch: IOException -> 0x04b5, TryCatch #2 {IOException -> 0x04b5, blocks: (B:146:0x0404, B:149:0x041b, B:151:0x0423, B:153:0x0426, B:162:0x043a, B:164:0x0458, B:167:0x046e, B:152:0x0425, B:140:0x03ee, B:143:0x03fb, B:158:0x0434), top: B:223:0x0289, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:165:0x0468  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x046e A[Catch: IOException -> 0x04b5, TRY_LEAVE, TryCatch #2 {IOException -> 0x04b5, blocks: (B:146:0x0404, B:149:0x041b, B:151:0x0423, B:153:0x0426, B:162:0x043a, B:164:0x0458, B:167:0x046e, B:152:0x0425, B:140:0x03ee, B:143:0x03fb, B:158:0x0434), top: B:223:0x0289, inners: #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:173:0x04b2  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x04bb  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x04c2 A[Catch: IOException -> 0x04e9, TryCatch #20 {IOException -> 0x04e9, blocks: (B:169:0x049e, B:171:0x04a9, B:182:0x04c2, B:185:0x04d0, B:187:0x04d6, B:189:0x04d9, B:188:0x04d8), top: B:231:0x049e }] */
    /* JADX WARN: Removed duplicated region for block: B:206:0x0515  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x0526  */
    /* JADX WARN: Removed duplicated region for block: B:211:0x0529  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x052c  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01b1 A[Catch: IOException -> 0x04eb, TRY_LEAVE, TryCatch #8 {IOException -> 0x04eb, blocks: (B:61:0x01ab, B:63:0x01b1, B:68:0x01be, B:81:0x0216, B:83:0x021a, B:85:0x023b, B:87:0x0243, B:88:0x0262, B:92:0x026b, B:94:0x0275, B:96:0x027e, B:56:0x0199), top: B:226:0x0199 }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01be A[Catch: IOException -> 0x04eb, TRY_ENTER, TRY_LEAVE, TryCatch #8 {IOException -> 0x04eb, blocks: (B:61:0x01ab, B:63:0x01b1, B:68:0x01be, B:81:0x0216, B:83:0x021a, B:85:0x023b, B:87:0x0243, B:88:0x0262, B:92:0x026b, B:94:0x0275, B:96:0x027e, B:56:0x0199), top: B:226:0x0199 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01d9 A[Catch: NameNotFoundException | IOException -> 0x0216, TryCatch #21 {NameNotFoundException | IOException -> 0x0216, blocks: (B:70:0x01c2, B:72:0x01d9, B:76:0x01f3, B:75:0x01e9, B:77:0x01f8, B:80:0x0208), top: B:232:0x01c2 }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0207  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x021a A[Catch: IOException -> 0x04eb, TryCatch #8 {IOException -> 0x04eb, blocks: (B:61:0x01ab, B:63:0x01b1, B:68:0x01be, B:81:0x0216, B:83:0x021a, B:85:0x023b, B:87:0x0243, B:88:0x0262, B:92:0x026b, B:94:0x0275, B:96:0x027e, B:56:0x0199), top: B:226:0x0199 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x023b A[Catch: IOException -> 0x04eb, TryCatch #8 {IOException -> 0x04eb, blocks: (B:61:0x01ab, B:63:0x01b1, B:68:0x01be, B:81:0x0216, B:83:0x021a, B:85:0x023b, B:87:0x0243, B:88:0x0262, B:92:0x026b, B:94:0x0275, B:96:0x027e, B:56:0x0199), top: B:226:0x0199 }] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0268  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x026b A[Catch: IOException -> 0x04eb, TryCatch #8 {IOException -> 0x04eb, blocks: (B:61:0x01ab, B:63:0x01b1, B:68:0x01be, B:81:0x0216, B:83:0x021a, B:85:0x023b, B:87:0x0243, B:88:0x0262, B:92:0x026b, B:94:0x0275, B:96:0x027e, B:56:0x0199), top: B:226:0x0199 }] */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v18, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r2v26 */
    /* JADX WARN: Type inference failed for: r2v27 */
    /* JADX WARN: Type inference failed for: r2v28 */
    /* JADX WARN: Type inference failed for: r2v29 */
    /* JADX WARN: Type inference failed for: r2v36 */
    /* JADX WARN: Type inference failed for: r2v39 */
    /* JADX WARN: Type inference failed for: r2v53 */
    /* JADX WARN: Type inference failed for: r2v54 */
    /* JADX WARN: Type inference failed for: r2v57 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean restoreOneFile(InputStream inputStream, boolean z, byte[] bArr, PackageInfo packageInfo, boolean z2, int i, IBackupManagerMonitor iBackupManagerMonitor) {
        boolean z3;
        FileMetadata fileMetadata;
        FileMetadata fileMetadata2;
        boolean z4;
        FileMetadata readTarHeaders;
        TarBackupReader tarBackupReader;
        boolean z5;
        InputStream inputStream2;
        byte[] bArr2;
        long restoreAgentTimeoutMillis;
        TarBackupReader tarBackupReader2;
        long j;
        String str;
        String str2;
        boolean z6;
        boolean z7;
        boolean z8;
        String str3;
        String str4;
        boolean z9;
        String str5;
        String str6;
        String str7;
        String str8;
        RestorePolicy restorePolicy;
        String str9 = inputStream;
        if (!isRunning()) {
            Slog.w("BackupManagerService", "Restore engine used after halting");
            return false;
        }
        BytesReadListener bytesReadListener = new BytesReadListener() { // from class: com.android.server.backup.restore.FullRestoreEngine$$ExternalSyntheticLambda0
            @Override // com.android.server.backup.utils.BytesReadListener
            public final void onBytesRead(long j2) {
                FullRestoreEngine.lambda$restoreOneFile$0(j2);
            }
        };
        TarBackupReader tarBackupReader3 = new TarBackupReader(str9, bytesReadListener, iBackupManagerMonitor);
        try {
            readTarHeaders = tarBackupReader3.readTarHeaders();
        } catch (IOException e) {
            e = e;
            z3 = true;
        }
        if (readTarHeaders != null) {
            String str10 = readTarHeaders.packageName;
            if (!str10.equals(this.mAgentPackage)) {
                if (packageInfo != null && !str10.equals(packageInfo.packageName)) {
                    Slog.w("BackupManagerService", "Expected data for " + packageInfo + " but saw " + str10);
                    setResult(-3);
                    setRunning(false);
                    return false;
                }
                if (!this.mPackagePolicies.containsKey(str10)) {
                    this.mPackagePolicies.put(str10, RestorePolicy.IGNORE);
                }
                if (this.mAgent != null) {
                    Slog.d("BackupManagerService", "Saw new package; finalizing old one");
                    tearDownPipes();
                    tearDownAgent(this.mTargetApp, this.mIsAdbRestore);
                    this.mTargetApp = null;
                    this.mAgentPackage = null;
                }
            }
            if (readTarHeaders.path.equals("_manifest")) {
                Signature[] readAppManifestAndReturnSignatures = tarBackupReader3.readAppManifestAndReturnSignatures(readTarHeaders);
                this.mAppVersion = readTarHeaders.version;
                RestorePolicy chooseRestorePolicy = tarBackupReader3.chooseRestorePolicy(this.mBackupManagerService.getPackageManager(), z2, readTarHeaders, readAppManifestAndReturnSignatures, (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class), this.mUserId, this.mBackupEligibilityRules);
                this.mManifestSignatures.put(readTarHeaders.packageName, readAppManifestAndReturnSignatures);
                this.mPackagePolicies.put(str10, chooseRestorePolicy);
                this.mPackageInstallers.put(str10, readTarHeaders.installerPackageName);
                tarBackupReader3.skipTarPadding(readTarHeaders.size);
                this.mObserver = FullBackupRestoreObserverUtils.sendOnRestorePackage(this.mObserver, str10);
            } else if (readTarHeaders.path.equals("_meta")) {
                tarBackupReader3.readMetadata(readTarHeaders);
                this.mWidgetData = tarBackupReader3.getWidgetData();
                tarBackupReader3.getMonitor();
                tarBackupReader3.skipTarPadding(readTarHeaders.size);
            } else {
                int i2 = C05611.$SwitchMap$com$android$server$backup$restore$RestorePolicy[this.mPackagePolicies.get(str10).ordinal()];
                if (i2 != 1) {
                    if (i2 == 2) {
                        try {
                            if (readTarHeaders.domain.equals("a")) {
                                Slog.d("BackupManagerService", "APK file; installing");
                                z3 = true;
                                try {
                                    boolean installApk = RestoreUtils.installApk(inputStream, this.mBackupManagerService.getContext(), this.mDeleteObserver, this.mManifestSignatures, this.mPackagePolicies, readTarHeaders, this.mPackageInstallers.get(str10), bytesReadListener, this.mUserId);
                                    HashMap<String, RestorePolicy> hashMap = this.mPackagePolicies;
                                    if (installApk) {
                                        restorePolicy = RestorePolicy.ACCEPT;
                                    } else {
                                        restorePolicy = RestorePolicy.IGNORE;
                                    }
                                    hashMap.put(str10, restorePolicy);
                                    tarBackupReader3.skipTarPadding(readTarHeaders.size);
                                    return true;
                                } catch (IOException e2) {
                                    e = e2;
                                    str9 = "BackupManagerService";
                                    fileMetadata = null;
                                }
                            } else {
                                fileMetadata = null;
                                tarBackupReader = tarBackupReader3;
                                z3 = true;
                                z3 = true;
                                try {
                                    this.mPackagePolicies.put(str10, RestorePolicy.IGNORE);
                                } catch (IOException e3) {
                                    e = e3;
                                    str9 = "BackupManagerService";
                                    Slog.w((String) str9, "io exception on restore socket read: " + e.getMessage());
                                    setResult(-3);
                                    fileMetadata2 = fileMetadata;
                                    if (fileMetadata2 == null) {
                                    }
                                    if (fileMetadata2 != null) {
                                    }
                                }
                            }
                        } catch (IOException e4) {
                            e = e4;
                            z3 = true;
                            fileMetadata = null;
                            str9 = "BackupManagerService";
                            Slog.w((String) str9, "io exception on restore socket read: " + e.getMessage());
                            setResult(-3);
                            fileMetadata2 = fileMetadata;
                            if (fileMetadata2 == null) {
                            }
                            if (fileMetadata2 != null) {
                            }
                        }
                    } else {
                        try {
                        } catch (IOException e5) {
                            e = e5;
                            fileMetadata = null;
                            str9 = "BackupManagerService";
                            z3 = true;
                        }
                        if (i2 == 3) {
                            if (readTarHeaders.domain.equals("a")) {
                                Slog.d("BackupManagerService", "apk present but ACCEPT");
                            } else {
                                tarBackupReader = tarBackupReader3;
                                z5 = true;
                                z3 = true;
                                z5 = (isRestorableFile(readTarHeaders) || !isCanonicalFilePath(readTarHeaders.path)) ? false : false;
                                if (z5 && this.mAgent == null) {
                                    try {
                                        this.mTargetApp = this.mBackupManagerService.getPackageManager().getApplicationInfoAsUser(str10, 0, this.mUserId);
                                        if (!this.mClearedPackages.contains(str10)) {
                                            boolean shouldForceClearAppDataOnFullRestore = shouldForceClearAppDataOnFullRestore(this.mTargetApp.packageName);
                                            if (this.mTargetApp.backupAgentName == null || shouldForceClearAppDataOnFullRestore) {
                                                Slog.d("BackupManagerService", "Clearing app data preparatory to full restore");
                                                this.mBackupManagerService.clearApplicationDataBeforeRestore(str10);
                                            }
                                            this.mClearedPackages.add(str10);
                                        }
                                        setUpPipes();
                                        this.mAgent = this.mBackupManagerService.bindToAgentSynchronous(this.mTargetApp, "k".equals(readTarHeaders.domain) ? 0 : 3, this.mBackupEligibilityRules.getBackupDestination());
                                        this.mAgentPackage = str10;
                                    } catch (PackageManager.NameNotFoundException | IOException unused) {
                                    }
                                    if (this.mAgent == null) {
                                        Slog.e("BackupManagerService", "Unable to create agent for " + str10);
                                        tearDownPipes();
                                        this.mPackagePolicies.put(str10, RestorePolicy.IGNORE);
                                        z5 = false;
                                    }
                                }
                                if (z5 && !str10.equals(this.mAgentPackage)) {
                                    Slog.e("BackupManagerService", "Restoring data for " + str10 + " but agent is for " + this.mAgentPackage);
                                    z5 = false;
                                }
                                if (shouldSkipReadOnlyDir(readTarHeaders)) {
                                    z5 = false;
                                }
                                if (z5) {
                                    long j2 = readTarHeaders.size;
                                    if (str10.equals("com.android.sharedstoragebackup")) {
                                        restoreAgentTimeoutMillis = this.mAgentTimeoutParameters.getSharedBackupAgentTimeoutMillis();
                                    } else {
                                        try {
                                            restoreAgentTimeoutMillis = this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis(this.mTargetApp.uid);
                                        } catch (IOException e6) {
                                            e = e6;
                                            str9 = "BackupManagerService";
                                            fileMetadata = null;
                                            Slog.w((String) str9, "io exception on restore socket read: " + e.getMessage());
                                            setResult(-3);
                                            fileMetadata2 = fileMetadata;
                                            if (fileMetadata2 == null) {
                                            }
                                            if (fileMetadata2 != null) {
                                            }
                                        }
                                    }
                                    try {
                                        try {
                                            this.mBackupManagerService.prepareOperationTimeout(i, restoreAgentTimeoutMillis, this.mMonitorTask, 1);
                                            try {
                                                if ("obb".equals(readTarHeaders.domain)) {
                                                    try {
                                                        Slog.d("BackupManagerService", "Restoring OBB file for " + str10 + " : " + readTarHeaders.path);
                                                        z9 = z5;
                                                        String str11 = "BackupManagerService";
                                                        tarBackupReader2 = tarBackupReader;
                                                        try {
                                                        } catch (RemoteException unused2) {
                                                            j = j2;
                                                        } catch (IOException unused3) {
                                                            j = j2;
                                                        }
                                                        try {
                                                            j = j2;
                                                            this.mObbConnection.restoreObbFile(str10, this.mPipes[0], readTarHeaders.size, readTarHeaders.type, readTarHeaders.path, readTarHeaders.mode, readTarHeaders.mtime, i, this.mBackupManagerService.getBackupManagerBinder());
                                                            str = str10;
                                                            str5 = str11;
                                                            str6 = str11;
                                                        } catch (RemoteException unused4) {
                                                            j = j2;
                                                            str = str10;
                                                            str4 = str11;
                                                            str9 = str4;
                                                            Slog.e(str9, "Agent crashed during full restore");
                                                            str2 = str9;
                                                            z6 = false;
                                                            z7 = false;
                                                            str9 = str2;
                                                            if (z6) {
                                                            }
                                                            if (!z8) {
                                                            }
                                                            z5 = z6;
                                                            if (!z5) {
                                                            }
                                                            fileMetadata2 = readTarHeaders;
                                                            if (fileMetadata2 == null) {
                                                            }
                                                            if (fileMetadata2 != null) {
                                                            }
                                                        } catch (IOException unused5) {
                                                            j = j2;
                                                            str = str10;
                                                            str3 = str11;
                                                            str9 = str3;
                                                            Slog.d(str9, "Couldn't establish restore");
                                                            str2 = str9;
                                                            z6 = false;
                                                            z7 = false;
                                                            str9 = str2;
                                                            if (z6) {
                                                            }
                                                            if (!z8) {
                                                            }
                                                            z5 = z6;
                                                            if (!z5) {
                                                            }
                                                            fileMetadata2 = readTarHeaders;
                                                            if (fileMetadata2 == null) {
                                                            }
                                                            if (fileMetadata2 != null) {
                                                            }
                                                        }
                                                    } catch (RemoteException unused6) {
                                                        tarBackupReader2 = tarBackupReader;
                                                        j = j2;
                                                        str9 = "BackupManagerService";
                                                        str = str10;
                                                        Slog.e(str9, "Agent crashed during full restore");
                                                        str2 = str9;
                                                        z6 = false;
                                                        z7 = false;
                                                        str9 = str2;
                                                        if (z6) {
                                                        }
                                                        if (!z8) {
                                                        }
                                                        z5 = z6;
                                                        if (!z5) {
                                                        }
                                                        fileMetadata2 = readTarHeaders;
                                                        if (fileMetadata2 == null) {
                                                        }
                                                        if (fileMetadata2 != null) {
                                                        }
                                                    } catch (IOException unused7) {
                                                        tarBackupReader2 = tarBackupReader;
                                                        j = j2;
                                                        str9 = "BackupManagerService";
                                                        str = str10;
                                                        Slog.d(str9, "Couldn't establish restore");
                                                        str2 = str9;
                                                        z6 = false;
                                                        z7 = false;
                                                        str9 = str2;
                                                        if (z6) {
                                                        }
                                                        if (!z8) {
                                                        }
                                                        z5 = z6;
                                                        if (!z5) {
                                                        }
                                                        fileMetadata2 = readTarHeaders;
                                                        if (fileMetadata2 == null) {
                                                        }
                                                        if (fileMetadata2 != null) {
                                                        }
                                                    }
                                                } else {
                                                    z9 = z5;
                                                    String str12 = "BackupManagerService";
                                                    tarBackupReader2 = tarBackupReader;
                                                    j = j2;
                                                    try {
                                                        if ("k".equals(readTarHeaders.domain)) {
                                                            try {
                                                                String str13 = str12;
                                                                Slog.d(str13, "Restoring key-value file for " + str10 + " : " + readTarHeaders.path);
                                                                readTarHeaders.version = this.mAppVersion;
                                                                UserBackupManagerService userBackupManagerService = this.mBackupManagerService;
                                                                new Thread(new KeyValueAdbRestoreEngine(userBackupManagerService, userBackupManagerService.getDataDir(), readTarHeaders, this.mPipes[0], this.mAgent, i), "restore-key-value-runner").start();
                                                                str8 = str13;
                                                            } catch (RemoteException unused8) {
                                                                str9 = str12;
                                                                str = str10;
                                                                Slog.e(str9, "Agent crashed during full restore");
                                                                str2 = str9;
                                                                z6 = false;
                                                                z7 = false;
                                                                str9 = str2;
                                                                if (z6) {
                                                                }
                                                                if (!z8) {
                                                                }
                                                                z5 = z6;
                                                                if (!z5) {
                                                                }
                                                                fileMetadata2 = readTarHeaders;
                                                                if (fileMetadata2 == null) {
                                                                }
                                                                if (fileMetadata2 != null) {
                                                                }
                                                            } catch (IOException unused9) {
                                                                str9 = str12;
                                                                str = str10;
                                                                Slog.d(str9, "Couldn't establish restore");
                                                                str2 = str9;
                                                                z6 = false;
                                                                z7 = false;
                                                                str9 = str2;
                                                                if (z6) {
                                                                }
                                                                if (!z8) {
                                                                }
                                                                z5 = z6;
                                                                if (!z5) {
                                                                }
                                                                fileMetadata2 = readTarHeaders;
                                                                if (fileMetadata2 == null) {
                                                                }
                                                                if (fileMetadata2 != null) {
                                                                }
                                                            }
                                                        } else {
                                                            str9 = str12;
                                                            if (this.mTargetApp.processName.equals("system")) {
                                                                Slog.d(str9, "system process agent - spinning a thread");
                                                                new Thread(new RestoreFileRunnable(this.mBackupManagerService, this.mAgent, readTarHeaders, this.mPipes[0], i), "restore-sys-runner").start();
                                                                str8 = str9;
                                                            } else {
                                                                str = str10;
                                                                try {
                                                                    this.mAgent.doRestoreFile(this.mPipes[0], readTarHeaders.size, readTarHeaders.type, readTarHeaders.domain, readTarHeaders.path, readTarHeaders.mode, readTarHeaders.mtime, i, this.mBackupManagerService.getBackupManagerBinder());
                                                                    str5 = str9;
                                                                    str6 = str12;
                                                                } catch (RemoteException unused10) {
                                                                    Slog.e(str9, "Agent crashed during full restore");
                                                                    str2 = str9;
                                                                    z6 = false;
                                                                    z7 = false;
                                                                    str9 = str2;
                                                                    if (z6) {
                                                                    }
                                                                    if (!z8) {
                                                                    }
                                                                    z5 = z6;
                                                                    if (!z5) {
                                                                    }
                                                                    fileMetadata2 = readTarHeaders;
                                                                    if (fileMetadata2 == null) {
                                                                    }
                                                                    if (fileMetadata2 != null) {
                                                                    }
                                                                } catch (IOException unused11) {
                                                                    Slog.d(str9, "Couldn't establish restore");
                                                                    str2 = str9;
                                                                    z6 = false;
                                                                    z7 = false;
                                                                    str9 = str2;
                                                                    if (z6) {
                                                                    }
                                                                    if (!z8) {
                                                                    }
                                                                    z5 = z6;
                                                                    if (!z5) {
                                                                    }
                                                                    fileMetadata2 = readTarHeaders;
                                                                    if (fileMetadata2 == null) {
                                                                    }
                                                                    if (fileMetadata2 != null) {
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        str = str10;
                                                        str5 = str8;
                                                        str6 = str12;
                                                    } catch (RemoteException unused12) {
                                                    } catch (IOException unused13) {
                                                    }
                                                }
                                                z6 = z9;
                                                str7 = z3 ? 1 : 0;
                                                str9 = str5;
                                                z7 = str7;
                                            } catch (RemoteException unused14) {
                                                str = str7;
                                                str4 = str6;
                                            } catch (IOException unused15) {
                                                str = str7;
                                                str3 = str6;
                                            }
                                        } catch (IOException e7) {
                                            e = e7;
                                            str9 = str9;
                                            fileMetadata = null;
                                            Slog.w((String) str9, "io exception on restore socket read: " + e.getMessage());
                                            setResult(-3);
                                            fileMetadata2 = fileMetadata;
                                            if (fileMetadata2 == null) {
                                            }
                                            if (fileMetadata2 != null) {
                                            }
                                        }
                                    } catch (RemoteException unused16) {
                                        str9 = "BackupManagerService";
                                        tarBackupReader2 = tarBackupReader;
                                        j = j2;
                                    } catch (IOException unused17) {
                                        str9 = "BackupManagerService";
                                        tarBackupReader2 = tarBackupReader;
                                        j = j2;
                                    }
                                    if (z6) {
                                        FileOutputStream fileOutputStream = new FileOutputStream(this.mPipes[z3 ? 1 : 0].getFileDescriptor());
                                        boolean z10 = z3 ? 1 : 0;
                                        long j3 = j;
                                        while (true) {
                                            if (j3 <= 0) {
                                                inputStream2 = inputStream;
                                                bArr2 = bArr;
                                                break;
                                            }
                                            bArr2 = bArr;
                                            inputStream2 = inputStream;
                                            int read = inputStream2.read(bArr2, 0, j3 > ((long) bArr2.length) ? bArr2.length : (int) j3);
                                            if (read <= 0) {
                                                break;
                                            }
                                            j3 -= read;
                                            if (z10) {
                                                try {
                                                    fileOutputStream.write(bArr2, 0, read);
                                                } catch (IOException e8) {
                                                    Slog.e((String) str9, "Failed to write to restore pipe: " + e8.getMessage());
                                                    z10 = false;
                                                }
                                            }
                                        }
                                        tarBackupReader2.skipTarPadding(readTarHeaders.size);
                                        z8 = this.mBackupManagerService.waitUntilOperationComplete(i);
                                    } else {
                                        inputStream2 = inputStream;
                                        bArr2 = bArr;
                                        z8 = z7;
                                    }
                                    if (!z8) {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Agent failure restoring ");
                                        String str14 = str;
                                        sb.append(str14);
                                        sb.append("; ending restore");
                                        Slog.w((String) str9, sb.toString());
                                        this.mBackupManagerService.getBackupHandler().removeMessages(18);
                                        tearDownPipes();
                                        tearDownAgent(this.mTargetApp, false);
                                        fileMetadata = null;
                                        try {
                                            this.mAgent = null;
                                            this.mPackagePolicies.put(str14, RestorePolicy.IGNORE);
                                            if (packageInfo != null) {
                                                setResult(-2);
                                                setRunning(false);
                                                return false;
                                            }
                                        } catch (IOException e9) {
                                            e = e9;
                                        }
                                    }
                                    z5 = z6;
                                } else {
                                    inputStream2 = str9;
                                    bArr2 = bArr;
                                }
                                if (!z5) {
                                    long j4 = (readTarHeaders.size + 511) & (-512);
                                    for (long j5 = 0; j4 > j5; j5 = 0) {
                                        long read2 = inputStream2.read(bArr2, 0, j4 > ((long) bArr2.length) ? bArr2.length : (int) j4);
                                        if (read2 <= 0) {
                                            break;
                                        }
                                        j4 -= read2;
                                    }
                                }
                                fileMetadata2 = readTarHeaders;
                                if (fileMetadata2 == null) {
                                    tearDownPipes();
                                    z4 = false;
                                    setRunning(false);
                                    if (z) {
                                        tearDownAgent(this.mTargetApp, this.mIsAdbRestore);
                                    }
                                } else {
                                    z4 = false;
                                }
                                return fileMetadata2 != null ? z3 : z4;
                            }
                        } else {
                            Slog.e("BackupManagerService", "Invalid policy from manifest");
                            this.mPackagePolicies.put(str10, RestorePolicy.IGNORE);
                        }
                        z5 = false;
                        z3 = true;
                        tarBackupReader = tarBackupReader3;
                        if (isRestorableFile(readTarHeaders)) {
                        }
                        if (z5) {
                            this.mTargetApp = this.mBackupManagerService.getPackageManager().getApplicationInfoAsUser(str10, 0, this.mUserId);
                            if (!this.mClearedPackages.contains(str10)) {
                            }
                            setUpPipes();
                            this.mAgent = this.mBackupManagerService.bindToAgentSynchronous(this.mTargetApp, "k".equals(readTarHeaders.domain) ? 0 : 3, this.mBackupEligibilityRules.getBackupDestination());
                            this.mAgentPackage = str10;
                            if (this.mAgent == null) {
                            }
                        }
                        if (z5) {
                            Slog.e("BackupManagerService", "Restoring data for " + str10 + " but agent is for " + this.mAgentPackage);
                            z5 = false;
                        }
                        if (shouldSkipReadOnlyDir(readTarHeaders)) {
                        }
                        if (z5) {
                        }
                        if (!z5) {
                        }
                        fileMetadata2 = readTarHeaders;
                        if (fileMetadata2 == null) {
                        }
                        if (fileMetadata2 != null) {
                        }
                    }
                    Slog.w((String) str9, "io exception on restore socket read: " + e.getMessage());
                    setResult(-3);
                    fileMetadata2 = fileMetadata;
                    if (fileMetadata2 == null) {
                    }
                    if (fileMetadata2 != null) {
                    }
                } else {
                    z3 = true;
                    tarBackupReader = tarBackupReader3;
                }
                z5 = false;
                if (isRestorableFile(readTarHeaders)) {
                }
                if (z5) {
                }
                if (z5) {
                }
                if (shouldSkipReadOnlyDir(readTarHeaders)) {
                }
                if (z5) {
                }
                if (!z5) {
                }
                fileMetadata2 = readTarHeaders;
                if (fileMetadata2 == null) {
                }
                if (fileMetadata2 != null) {
                }
            }
        }
        z3 = true;
        fileMetadata2 = readTarHeaders;
        if (fileMetadata2 == null) {
        }
        if (fileMetadata2 != null) {
        }
    }

    /* renamed from: com.android.server.backup.restore.FullRestoreEngine$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C05611 {
        public static final /* synthetic */ int[] $SwitchMap$com$android$server$backup$restore$RestorePolicy;

        static {
            int[] iArr = new int[RestorePolicy.values().length];
            $SwitchMap$com$android$server$backup$restore$RestorePolicy = iArr;
            try {
                iArr[RestorePolicy.IGNORE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$server$backup$restore$RestorePolicy[RestorePolicy.ACCEPT_IF_APK.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$server$backup$restore$RestorePolicy[RestorePolicy.ACCEPT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public boolean shouldSkipReadOnlyDir(FileMetadata fileMetadata) {
        if (isValidParent(this.mReadOnlyParent, fileMetadata)) {
            return true;
        }
        if (isReadOnlyDir(fileMetadata)) {
            this.mReadOnlyParent = fileMetadata;
            Slog.w("BackupManagerService", "Skipping restore of " + fileMetadata.path + " and its contents as read-only dirs are currently not supported.");
            return true;
        }
        this.mReadOnlyParent = null;
        return false;
    }

    public static boolean isValidParent(FileMetadata fileMetadata, FileMetadata fileMetadata2) {
        return fileMetadata != null && fileMetadata2.packageName.equals(fileMetadata.packageName) && fileMetadata2.domain.equals(fileMetadata.domain) && fileMetadata2.path.startsWith(getPathWithTrailingSeparator(fileMetadata.path));
    }

    public static String getPathWithTrailingSeparator(String str) {
        String str2 = File.separator;
        if (str.endsWith(str2)) {
            return str;
        }
        return str + str2;
    }

    public static boolean isReadOnlyDir(FileMetadata fileMetadata) {
        return fileMetadata.type == 2 && (fileMetadata.mode & ((long) OsConstants.S_IWUSR)) == 0;
    }

    public final void setUpPipes() throws IOException {
        synchronized (this.mPipesLock) {
            this.mPipes = ParcelFileDescriptor.createPipe();
            this.mPipesClosed = false;
        }
    }

    public final void tearDownPipes() {
        ParcelFileDescriptor[] parcelFileDescriptorArr;
        synchronized (this.mPipesLock) {
            if (!this.mPipesClosed && (parcelFileDescriptorArr = this.mPipes) != null) {
                try {
                    parcelFileDescriptorArr[0].close();
                    this.mPipes[1].close();
                    this.mPipesClosed = true;
                } catch (IOException e) {
                    Slog.w("BackupManagerService", "Couldn't close agent pipes", e);
                }
            }
        }
    }

    public final void tearDownAgent(ApplicationInfo applicationInfo, boolean z) {
        if (this.mAgent != null) {
            if (z) {
                try {
                    int generateRandomIntegerToken = this.mBackupManagerService.generateRandomIntegerToken();
                    long fullBackupAgentTimeoutMillis = this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
                    AdbRestoreFinishedLatch adbRestoreFinishedLatch = new AdbRestoreFinishedLatch(this.mBackupManagerService, this.mOperationStorage, generateRandomIntegerToken);
                    this.mBackupManagerService.prepareOperationTimeout(generateRandomIntegerToken, fullBackupAgentTimeoutMillis, adbRestoreFinishedLatch, 1);
                    if (this.mTargetApp.processName.equals("system")) {
                        new Thread(new AdbRestoreFinishedRunnable(this.mAgent, generateRandomIntegerToken, this.mBackupManagerService), "restore-sys-finished-runner").start();
                    } else {
                        this.mAgent.doRestoreFinished(generateRandomIntegerToken, this.mBackupManagerService.getBackupManagerBinder());
                    }
                    adbRestoreFinishedLatch.await();
                } catch (RemoteException unused) {
                    Slog.d("BackupManagerService", "Lost app trying to shut down");
                }
            }
            this.mBackupManagerService.tearDownAgentAndKill(applicationInfo);
            this.mAgent = null;
        }
    }

    public void handleTimeout() {
        tearDownPipes();
        setResult(-2);
        setRunning(false);
    }

    public final boolean isRestorableFile(FileMetadata fileMetadata) {
        if (this.mBackupEligibilityRules.getBackupDestination() == 1) {
            return true;
        }
        if ("c".equals(fileMetadata.domain)) {
            return false;
        }
        return ("r".equals(fileMetadata.domain) && fileMetadata.path.startsWith("no_backup/")) ? false : true;
    }

    public static boolean isCanonicalFilePath(String str) {
        return (str.contains("..") || str.contains("//")) ? false : true;
    }

    public final boolean shouldForceClearAppDataOnFullRestore(String str) {
        String stringForUser = Settings.Secure.getStringForUser(this.mBackupManagerService.getContext().getContentResolver(), "packages_to_clear_data_before_full_restore", this.mUserId);
        if (TextUtils.isEmpty(stringForUser)) {
            return false;
        }
        return Arrays.asList(stringForUser.split(";")).contains(str);
    }
}
