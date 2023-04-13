package com.android.server.backup.keyvalue;

import android.app.IBackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.IBackupCallback;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ConditionVariable;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.WorkSource;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.DataChangedJournal;
import com.android.server.backup.KeyValueBackupJob;
import com.android.server.backup.OperationStorage;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.PerformFullTransportBackupTask;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.remote.RemoteCall;
import com.android.server.backup.remote.RemoteCallable;
import com.android.server.backup.remote.RemoteResult;
import com.android.server.backup.transport.BackupTransportClient;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.transport.TransportNotAvailableException;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class KeyValueBackupTask implements BackupRestoreTask, Runnable {
    @VisibleForTesting
    public static final String NEW_STATE_FILE_SUFFIX = ".new";
    @VisibleForTesting
    static final String NO_DATA_END_SENTINEL = "@end@";
    @VisibleForTesting
    public static final String STAGING_FILE_SUFFIX = ".data";
    public static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    public IBackupAgent mAgent;
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public ParcelFileDescriptor mBackupData;
    public File mBackupDataFile;
    public final BackupEligibilityRules mBackupEligibilityRules;
    public final UserBackupManagerService mBackupManagerService;
    public final File mBlankStateFile;
    public final ConditionVariable mCancelAcknowledged = new ConditionVariable(false);
    public volatile boolean mCancelled = false;
    public final int mCurrentOpToken;
    public PackageInfo mCurrentPackage;
    public final File mDataDirectory;
    public PerformFullTransportBackupTask mFullBackupTask;
    public boolean mHasDataToBackup;
    public final DataChangedJournal mJournal;
    public ParcelFileDescriptor mNewState;
    public File mNewStateFile;
    public boolean mNonIncremental;
    public final OperationStorage mOperationStorage;
    public final List<String> mOriginalQueue;
    public final PackageManager mPackageManager;
    public volatile RemoteCall mPendingCall;
    public final List<String> mPendingFullBackups;
    public final List<String> mQueue;
    public final Object mQueueLock;
    public final KeyValueBackupReporter mReporter;
    public ParcelFileDescriptor mSavedState;
    public File mSavedStateFile;
    public final File mStateDirectory;
    public final OnTaskFinishedListener mTaskFinishedListener;
    public final TransportConnection mTransportConnection;
    public final int mUserId;
    public final boolean mUserInitiated;

    @Override // com.android.server.backup.BackupRestoreTask
    public void execute() {
    }

    public final int getBackupFinishedStatus(boolean z, int i) {
        if (z) {
            return -2003;
        }
        return (i == -1005 || i == -1002 || i == 0) ? 0 : -1000;
    }

    public final int getPerformBackupFlags(boolean z, boolean z2) {
        return (z2 ? 4 : 2) | (z ? 1 : 0);
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void operationComplete(long j) {
    }

    public static KeyValueBackupTask start(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, TransportConnection transportConnection, String str, List<String> list, DataChangedJournal dataChangedJournal, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, OnTaskFinishedListener onTaskFinishedListener, List<String> list2, boolean z, boolean z2, BackupEligibilityRules backupEligibilityRules) {
        KeyValueBackupTask keyValueBackupTask = new KeyValueBackupTask(userBackupManagerService, operationStorage, transportConnection, str, list, dataChangedJournal, new KeyValueBackupReporter(userBackupManagerService, iBackupObserver, iBackupManagerMonitor), onTaskFinishedListener, list2, z, z2, backupEligibilityRules);
        Thread thread = new Thread(keyValueBackupTask, "key-value-backup-" + THREAD_COUNT.incrementAndGet());
        thread.start();
        KeyValueBackupReporter.onNewThread(thread.getName());
        return keyValueBackupTask;
    }

    @VisibleForTesting
    public KeyValueBackupTask(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, TransportConnection transportConnection, String str, List<String> list, DataChangedJournal dataChangedJournal, KeyValueBackupReporter keyValueBackupReporter, OnTaskFinishedListener onTaskFinishedListener, List<String> list2, boolean z, boolean z2, BackupEligibilityRules backupEligibilityRules) {
        this.mBackupManagerService = userBackupManagerService;
        this.mOperationStorage = operationStorage;
        this.mPackageManager = userBackupManagerService.getPackageManager();
        this.mTransportConnection = transportConnection;
        this.mOriginalQueue = list;
        this.mQueue = new ArrayList(list);
        this.mJournal = dataChangedJournal;
        this.mReporter = keyValueBackupReporter;
        this.mTaskFinishedListener = onTaskFinishedListener;
        this.mPendingFullBackups = list2;
        this.mUserInitiated = z;
        this.mNonIncremental = z2;
        BackupAgentTimeoutParameters agentTimeoutParameters = userBackupManagerService.getAgentTimeoutParameters();
        Objects.requireNonNull(agentTimeoutParameters, "Timeout parameters cannot be null");
        this.mAgentTimeoutParameters = agentTimeoutParameters;
        File file = new File(userBackupManagerService.getBaseStateDir(), str);
        this.mStateDirectory = file;
        this.mDataDirectory = userBackupManagerService.getDataDir();
        this.mCurrentOpToken = userBackupManagerService.generateRandomIntegerToken();
        this.mQueueLock = userBackupManagerService.getQueueLock();
        this.mBlankStateFile = new File(file, "blank_state");
        this.mUserId = userBackupManagerService.getUserId();
        this.mBackupEligibilityRules = backupEligibilityRules;
    }

    public final void registerTask() {
        this.mOperationStorage.registerOperation(this.mCurrentOpToken, 0, this, 2);
    }

    public final void unregisterTask() {
        this.mOperationStorage.removeOperation(this.mCurrentOpToken);
    }

    @Override // java.lang.Runnable
    public void run() {
        Process.setThreadPriority(10);
        int i = 0;
        this.mHasDataToBackup = false;
        HashSet hashSet = new HashSet();
        try {
            startTask();
            while (!this.mQueue.isEmpty() && !this.mCancelled) {
                String remove = this.mQueue.remove(0);
                try {
                    if ("@pm@".equals(remove)) {
                        backupPm();
                    } else {
                        backupPackage(remove);
                    }
                    setSuccessState(remove, true);
                    hashSet.add(remove);
                } catch (AgentException e) {
                    setSuccessState(remove, false);
                    if (e.isTransitory()) {
                        this.mBackupManagerService.dataChangedImpl(remove);
                    }
                }
            }
            informTransportOfUnchangedApps(hashSet);
        } catch (TaskException e2) {
            if (e2.isStateCompromised()) {
                this.mBackupManagerService.resetBackupState(this.mStateDirectory);
            }
            revertTask();
            i = e2.getStatus();
        }
        finishTask(i);
    }

    public final void informTransportOfUnchangedApps(Set<String> set) {
        String[] succeedingPackages = getSucceedingPackages();
        if (succeedingPackages == null) {
            return;
        }
        int i = this.mUserInitiated ? 9 : 8;
        try {
            BackupTransportClient connectOrThrow = this.mTransportConnection.connectOrThrow("KVBT.informTransportOfEmptyBackups()");
            boolean z = false;
            for (String str : succeedingPackages) {
                if (set.contains(str)) {
                    Log.v("KVBT", "Skipping package which was backed up this time: " + str);
                } else {
                    try {
                        PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 0);
                        if (!isEligibleForNoDataCall(packageInfo)) {
                            clearStatus(str);
                        } else {
                            sendNoDataChangedTo(connectOrThrow, packageInfo, i);
                            z = true;
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                        clearStatus(str);
                    }
                }
            }
            if (z) {
                PackageInfo packageInfo2 = new PackageInfo();
                packageInfo2.packageName = NO_DATA_END_SENTINEL;
                sendNoDataChangedTo(connectOrThrow, packageInfo2, i);
            }
        } catch (RemoteException | TransportNotAvailableException e) {
            Log.e("KVBT", "Could not inform transport of all unchanged apps", e);
        }
    }

    public final boolean isEligibleForNoDataCall(PackageInfo packageInfo) {
        return this.mBackupEligibilityRules.appIsKeyValueOnly(packageInfo) && this.mBackupEligibilityRules.appIsRunningAndEligibleForBackupWithTransport(this.mTransportConnection, packageInfo.packageName);
    }

    public final void sendNoDataChangedTo(BackupTransportClient backupTransportClient, PackageInfo packageInfo, int i) throws RemoteException {
        try {
            ParcelFileDescriptor open = ParcelFileDescriptor.open(this.mBlankStateFile, 402653184);
            try {
                int performBackup = backupTransportClient.performBackup(packageInfo, open, i);
                if (performBackup != -1000 && performBackup != -1001) {
                    backupTransportClient.finishBackup();
                    return;
                }
                Log.w("KVBT", "Aborting informing transport of unchanged apps, transport errored");
            } finally {
                IoUtils.closeQuietly(open);
            }
        } catch (FileNotFoundException unused) {
            Log.e("KVBT", "Unable to find blank state file, aborting unchanged apps signal.");
        }
    }

    public final String[] getSucceedingPackages() {
        File topLevelSuccessStateDirectory = getTopLevelSuccessStateDirectory(false);
        if (topLevelSuccessStateDirectory == null) {
            return null;
        }
        return topLevelSuccessStateDirectory.list();
    }

    public final void setSuccessState(String str, boolean z) {
        File successStateFileFor = getSuccessStateFileFor(str);
        if (successStateFileFor == null || successStateFileFor.exists() == z) {
            return;
        }
        if (!z) {
            clearStatus(str, successStateFileFor);
            return;
        }
        try {
            if (successStateFileFor.createNewFile()) {
                return;
            }
            Log.w("KVBT", "Unable to permanently record success for " + str);
        } catch (IOException e) {
            Log.w("KVBT", "Unable to permanently record success for " + str, e);
        }
    }

    public final void clearStatus(String str) {
        File successStateFileFor = getSuccessStateFileFor(str);
        if (successStateFileFor == null) {
            return;
        }
        clearStatus(str, successStateFileFor);
    }

    public final void clearStatus(String str, File file) {
        if (!file.exists() || file.delete()) {
            return;
        }
        Log.w("KVBT", "Unable to remove status file for " + str);
    }

    public final File getSuccessStateFileFor(String str) {
        File topLevelSuccessStateDirectory = getTopLevelSuccessStateDirectory(true);
        if (topLevelSuccessStateDirectory == null) {
            return null;
        }
        return new File(topLevelSuccessStateDirectory, str);
    }

    public final File getTopLevelSuccessStateDirectory(boolean z) {
        File file = new File(this.mStateDirectory, "backing-up");
        if (file.exists() || !z || file.mkdirs()) {
            return file;
        }
        Log.e("KVBT", "Unable to create backing-up state directory");
        return null;
    }

    public final int sendDataToTransport(PackageInfo packageInfo) throws AgentException, TaskException {
        try {
            return sendDataToTransport();
        } catch (IOException e) {
            this.mReporter.onAgentDataError(packageInfo.packageName, e);
            throw TaskException.causedBy(e);
        }
    }

    public final void startTask() throws TaskException {
        if (this.mBackupManagerService.isBackupOperationInProgress()) {
            this.mReporter.onSkipBackup();
            throw TaskException.create();
        }
        this.mFullBackupTask = createFullBackupTask(this.mPendingFullBackups);
        registerTask();
        if (this.mQueue.isEmpty() && this.mPendingFullBackups.isEmpty()) {
            this.mReporter.onEmptyQueueAtStart();
            return;
        }
        if (this.mQueue.remove("@pm@") || !this.mNonIncremental) {
            this.mQueue.add(0, "@pm@");
        } else {
            this.mReporter.onSkipPm();
        }
        this.mReporter.onQueueReady(this.mQueue);
        File file = new File(this.mStateDirectory, "@pm@");
        try {
            BackupTransportClient connectOrThrow = this.mTransportConnection.connectOrThrow("KVBT.startTask()");
            String name = connectOrThrow.name();
            if (name.contains("EncryptedLocalTransport")) {
                this.mNonIncremental = true;
            }
            this.mReporter.onTransportReady(name);
            if (file.length() <= 0) {
                this.mReporter.onInitializeTransport(name);
                this.mBackupManagerService.resetBackupState(this.mStateDirectory);
                int initializeDevice = connectOrThrow.initializeDevice();
                this.mReporter.onTransportInitialized(initializeDevice);
                if (initializeDevice == 0) {
                    return;
                }
                throw TaskException.stateCompromised();
            }
        } catch (TaskException e) {
            throw e;
        } catch (Exception e2) {
            this.mReporter.onInitializeTransportError(e2);
            throw TaskException.stateCompromised();
        }
    }

    public final PerformFullTransportBackupTask createFullBackupTask(List<String> list) {
        return new PerformFullTransportBackupTask(this.mBackupManagerService, this.mOperationStorage, this.mTransportConnection, null, (String[]) list.toArray(new String[list.size()]), false, null, new CountDownLatch(1), this.mReporter.getObserver(), this.mReporter.getMonitor(), this.mTaskFinishedListener, this.mUserInitiated, this.mBackupEligibilityRules);
    }

    public final void backupPm() throws TaskException {
        this.mReporter.onStartPackageBackup("@pm@");
        PackageInfo packageInfo = new PackageInfo();
        this.mCurrentPackage = packageInfo;
        packageInfo.packageName = "@pm@";
        try {
            try {
                extractPmAgentData(packageInfo);
                cleanUpAgentForTransportStatus(sendDataToTransport(this.mCurrentPackage));
            } catch (TaskException e) {
                throw TaskException.stateCompromised(e);
            }
        } catch (AgentException | TaskException e2) {
            this.mReporter.onExtractPmAgentDataError(e2);
            cleanUpAgentForError(e2);
            if (e2 instanceof TaskException) {
                throw ((TaskException) e2);
            }
            throw TaskException.stateCompromised(e2);
        }
    }

    public final void backupPackage(String str) throws AgentException, TaskException {
        this.mReporter.onStartPackageBackup(str);
        PackageInfo packageForBackup = getPackageForBackup(str);
        this.mCurrentPackage = packageForBackup;
        try {
            extractAgentData(packageForBackup);
            BackupManagerMonitorUtils.monitorAgentLoggingResults(this.mReporter.getMonitor(), this.mCurrentPackage, this.mAgent);
            cleanUpAgentForTransportStatus(sendDataToTransport(this.mCurrentPackage));
        } catch (AgentException | TaskException e) {
            cleanUpAgentForError(e);
            throw e;
        }
    }

    public final PackageInfo getPackageForBackup(String str) throws AgentException {
        try {
            PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(str, 134217728, this.mUserId);
            ApplicationInfo applicationInfo = packageInfoAsUser.applicationInfo;
            if (!this.mBackupEligibilityRules.appIsEligibleForBackup(applicationInfo)) {
                this.mReporter.onPackageNotEligibleForBackup(str);
                throw AgentException.permanent();
            } else if (this.mBackupEligibilityRules.appGetsFullBackup(packageInfoAsUser)) {
                this.mReporter.onPackageEligibleForFullBackup(str);
                throw AgentException.permanent();
            } else if (this.mBackupEligibilityRules.appIsStopped(applicationInfo)) {
                this.mReporter.onPackageStopped(str);
                throw AgentException.permanent();
            } else {
                return packageInfoAsUser;
            }
        } catch (PackageManager.NameNotFoundException e) {
            this.mReporter.onAgentUnknown(str);
            throw AgentException.permanent(e);
        }
    }

    public final IBackupAgent bindAgent(PackageInfo packageInfo) throws AgentException {
        String str = packageInfo.packageName;
        try {
            IBackupAgent bindToAgentSynchronous = this.mBackupManagerService.bindToAgentSynchronous(packageInfo.applicationInfo, 0, this.mBackupEligibilityRules.getBackupDestination());
            if (bindToAgentSynchronous != null) {
                return bindToAgentSynchronous;
            }
            this.mReporter.onAgentError(str);
            throw AgentException.transitory();
        } catch (SecurityException e) {
            this.mReporter.onBindAgentError(str, e);
            throw AgentException.transitory(e);
        }
    }

    public final void finishTask(int i) {
        for (String str : this.mQueue) {
            this.mBackupManagerService.dataChangedImpl(str);
        }
        DataChangedJournal dataChangedJournal = this.mJournal;
        if (dataChangedJournal != null && !dataChangedJournal.delete()) {
            this.mReporter.onJournalDeleteFailed(this.mJournal);
        }
        long currentToken = this.mBackupManagerService.getCurrentToken();
        String str2 = null;
        if (this.mHasDataToBackup && i == 0 && currentToken == 0) {
            try {
                BackupTransportClient connectOrThrow = this.mTransportConnection.connectOrThrow("KVBT.finishTask()");
                str2 = connectOrThrow.name();
                this.mBackupManagerService.setCurrentToken(connectOrThrow.getCurrentRestoreSet());
                this.mBackupManagerService.writeRestoreTokens();
            } catch (Exception e) {
                this.mReporter.onSetCurrentTokenError(e);
            }
        }
        synchronized (this.mQueueLock) {
            this.mBackupManagerService.setBackupRunning(false);
            if (i == -1001) {
                this.mReporter.onTransportNotInitialized(str2);
                try {
                    triggerTransportInitializationLocked();
                } catch (Exception e2) {
                    this.mReporter.onPendingInitializeTransportError(e2);
                    i = -1000;
                }
            }
        }
        unregisterTask();
        this.mReporter.onTaskFinished();
        if (this.mCancelled) {
            this.mCancelAcknowledged.open();
        }
        if (!this.mCancelled && i == 0 && this.mFullBackupTask != null && !this.mPendingFullBackups.isEmpty()) {
            this.mReporter.onStartFullBackup(this.mPendingFullBackups);
            new Thread(this.mFullBackupTask, "full-transport-requested").start();
            return;
        }
        PerformFullTransportBackupTask performFullTransportBackupTask = this.mFullBackupTask;
        if (performFullTransportBackupTask != null) {
            performFullTransportBackupTask.unregisterTask();
        }
        this.mTaskFinishedListener.onFinished("KVBT.finishTask()");
        this.mReporter.onBackupFinished(getBackupFinishedStatus(this.mCancelled, i));
        this.mBackupManagerService.getWakelock().release();
    }

    @GuardedBy({"mQueueLock"})
    public final void triggerTransportInitializationLocked() throws Exception {
        this.mBackupManagerService.getPendingInits().add(this.mTransportConnection.connectOrThrow("KVBT.triggerTransportInitializationLocked").name());
        deletePmStateFile();
        this.mBackupManagerService.backupNow();
    }

    public final void deletePmStateFile() {
        new File(this.mStateDirectory, "@pm@").delete();
    }

    public final void extractPmAgentData(PackageInfo packageInfo) throws AgentException, TaskException {
        Preconditions.checkArgument(packageInfo.packageName.equals("@pm@"));
        IBackupAgent asInterface = IBackupAgent.Stub.asInterface(this.mBackupManagerService.makeMetadataAgentWithEligibilityRules(this.mBackupEligibilityRules).onBind());
        this.mAgent = asInterface;
        extractAgentData(packageInfo, asInterface);
    }

    public final void extractAgentData(PackageInfo packageInfo) throws AgentException, TaskException {
        this.mBackupManagerService.setWorkSource(new WorkSource(packageInfo.applicationInfo.uid));
        try {
            IBackupAgent bindAgent = bindAgent(packageInfo);
            this.mAgent = bindAgent;
            extractAgentData(packageInfo, bindAgent);
        } finally {
            this.mBackupManagerService.setWorkSource(null);
        }
    }

    public final void extractAgentData(PackageInfo packageInfo, final IBackupAgent iBackupAgent) throws AgentException, TaskException {
        String str = packageInfo.packageName;
        this.mReporter.onExtractAgentData(str);
        this.mSavedStateFile = new File(this.mStateDirectory, str);
        File file = this.mDataDirectory;
        this.mBackupDataFile = new File(file, str + STAGING_FILE_SUFFIX);
        File file2 = this.mStateDirectory;
        this.mNewStateFile = new File(file2, str + NEW_STATE_FILE_SUFFIX);
        this.mReporter.onAgentFilesReady(this.mBackupDataFile);
        boolean z = false;
        try {
            this.mSavedState = ParcelFileDescriptor.open(this.mNonIncremental ? this.mBlankStateFile : this.mSavedStateFile, 402653184);
            this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataFile, 1006632960);
            this.mNewState = ParcelFileDescriptor.open(this.mNewStateFile, 1006632960);
            if (this.mUserId == 0 && !SELinux.restorecon(this.mBackupDataFile)) {
                this.mReporter.onRestoreconFailed(this.mBackupDataFile);
            }
            BackupTransportClient connectOrThrow = this.mTransportConnection.connectOrThrow("KVBT.extractAgentData()");
            final long backupQuota = connectOrThrow.getBackupQuota(str, false);
            final int transportFlags = connectOrThrow.getTransportFlags();
            z = true;
            checkAgentResult(packageInfo, remoteCall(new RemoteCallable() { // from class: com.android.server.backup.keyvalue.KeyValueBackupTask$$ExternalSyntheticLambda1
                @Override // com.android.server.backup.remote.RemoteCallable
                public final void call(Object obj) {
                    KeyValueBackupTask.this.lambda$extractAgentData$0(iBackupAgent, backupQuota, transportFlags, (IBackupCallback) obj);
                }
            }, this.mAgentTimeoutParameters.getKvBackupAgentTimeoutMillis(), "doBackup()"));
        } catch (Exception e) {
            this.mReporter.onCallAgentDoBackupError(str, z, e);
            if (z) {
                throw AgentException.transitory(e);
            }
            throw TaskException.create();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$extractAgentData$0(IBackupAgent iBackupAgent, long j, int i, IBackupCallback iBackupCallback) throws RemoteException {
        iBackupAgent.doBackup(this.mSavedState, this.mBackupData, this.mNewState, j, iBackupCallback, i);
    }

    public final void checkAgentResult(PackageInfo packageInfo, RemoteResult remoteResult) throws AgentException, TaskException {
        if (remoteResult == RemoteResult.FAILED_THREAD_INTERRUPTED) {
            this.mCancelled = true;
            this.mReporter.onAgentCancelled(packageInfo);
            throw TaskException.create();
        } else if (remoteResult == RemoteResult.FAILED_CANCELLED) {
            this.mReporter.onAgentCancelled(packageInfo);
            throw TaskException.create();
        } else if (remoteResult == RemoteResult.FAILED_TIMED_OUT) {
            this.mReporter.onAgentTimedOut(packageInfo);
            throw AgentException.transitory();
        } else {
            Preconditions.checkState(remoteResult.isPresent());
            long j = remoteResult.get();
            if (j == -1) {
                this.mReporter.onAgentResultError(packageInfo);
                throw AgentException.transitory();
            } else {
                Preconditions.checkState(j == 0);
            }
        }
    }

    public final void agentFail(IBackupAgent iBackupAgent, String str) {
        try {
            iBackupAgent.fail(str);
        } catch (Exception unused) {
            this.mReporter.onFailAgentError(this.mCurrentPackage.packageName);
        }
    }

    public final String SHA1Checksum(byte[] bArr) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(bArr);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Integer.toHexString(b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            this.mReporter.onDigestError(e);
            return "00";
        }
    }

    public final void writeWidgetPayloadIfAppropriate(FileDescriptor fileDescriptor, String str) throws IOException {
        String str2;
        byte[] widgetState = AppWidgetBackupBridge.getWidgetState(str, this.mUserId);
        File file = this.mStateDirectory;
        File file2 = new File(file, str + "_widget");
        boolean exists = file2.exists();
        if (exists || widgetState != null) {
            this.mReporter.onWriteWidgetData(exists, widgetState);
            if (widgetState != null) {
                str2 = SHA1Checksum(widgetState);
                if (exists) {
                    FileInputStream fileInputStream = new FileInputStream(file2);
                    try {
                        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                        String readUTF = dataInputStream.readUTF();
                        dataInputStream.close();
                        fileInputStream.close();
                        if (Objects.equals(str2, readUTF)) {
                            return;
                        }
                    } catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
            } else {
                str2 = null;
            }
            BackupDataOutput backupDataOutput = new BackupDataOutput(fileDescriptor);
            if (widgetState != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
                    dataOutputStream.writeUTF(str2);
                    dataOutputStream.close();
                    fileOutputStream.close();
                    backupDataOutput.writeEntityHeader("￭￭widget", widgetState.length);
                    backupDataOutput.writeEntityData(widgetState, widgetState.length);
                    return;
                } catch (Throwable th3) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                    throw th3;
                }
            }
            backupDataOutput.writeEntityHeader("￭￭widget", -1);
            file2.delete();
        }
    }

    public final int sendDataToTransport() throws AgentException, TaskException, IOException {
        Preconditions.checkState(this.mBackupData != null);
        checkBackupData(this.mCurrentPackage.applicationInfo, this.mBackupDataFile);
        String str = this.mCurrentPackage.packageName;
        writeWidgetPayloadIfAppropriate(this.mBackupData.getFileDescriptor(), str);
        int transportPerformBackup = transportPerformBackup(this.mCurrentPackage, this.mBackupDataFile, this.mSavedStateFile.length() == 0);
        handleTransportStatus(transportPerformBackup, str, this.mBackupDataFile.length());
        return transportPerformBackup;
    }

    public final int transportPerformBackup(PackageInfo packageInfo, File file, boolean z) throws TaskException {
        String str = packageInfo.packageName;
        if (file.length() <= 0) {
            this.mReporter.onEmptyData(packageInfo);
            return 0;
        }
        this.mHasDataToBackup = true;
        try {
            ParcelFileDescriptor open = ParcelFileDescriptor.open(file, 268435456);
            BackupTransportClient connectOrThrow = this.mTransportConnection.connectOrThrow("KVBT.transportPerformBackup()");
            this.mReporter.onTransportPerformBackup(str);
            int performBackup = connectOrThrow.performBackup(packageInfo, open, getPerformBackupFlags(this.mUserInitiated, z));
            if (performBackup == 0) {
                performBackup = connectOrThrow.finishBackup();
            } else if (performBackup == -1001) {
                this.mReporter.onTransportNotInitialized(connectOrThrow.name());
            }
            if (open != null) {
                open.close();
            }
            if (z && performBackup == -1006) {
                this.mReporter.onPackageBackupNonIncrementalAndNonIncrementalRequired(str);
                throw TaskException.create();
            }
            return performBackup;
        } catch (Exception e) {
            this.mReporter.onPackageBackupTransportError(str, e);
            throw TaskException.causedBy(e);
        }
    }

    public final void handleTransportStatus(int i, String str, long j) throws TaskException, AgentException {
        if (i == 0) {
            this.mReporter.onPackageBackupComplete(str, j);
        } else if (i == -1006) {
            this.mReporter.onPackageBackupNonIncrementalRequired(this.mCurrentPackage);
            this.mQueue.add(0, str);
        } else if (i == -1002) {
            this.mReporter.onPackageBackupRejected(str);
            throw AgentException.permanent();
        } else if (i == -1005) {
            this.mReporter.onPackageBackupQuotaExceeded(str);
            agentDoQuotaExceeded(this.mAgent, str, j);
            throw AgentException.permanent();
        } else {
            this.mReporter.onPackageBackupTransportFailure(str);
            throw TaskException.forStatus(i);
        }
    }

    public final void agentDoQuotaExceeded(final IBackupAgent iBackupAgent, String str, final long j) {
        if (iBackupAgent != null) {
            try {
                final long backupQuota = this.mTransportConnection.connectOrThrow("KVBT.agentDoQuotaExceeded()").getBackupQuota(str, false);
                remoteCall(new RemoteCallable() { // from class: com.android.server.backup.keyvalue.KeyValueBackupTask$$ExternalSyntheticLambda0
                    @Override // com.android.server.backup.remote.RemoteCallable
                    public final void call(Object obj) {
                        iBackupAgent.doQuotaExceeded(j, backupQuota, (IBackupCallback) obj);
                    }
                }, this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis(), "doQuotaExceeded()");
            } catch (Exception e) {
                this.mReporter.onAgentDoQuotaExceededError(e);
            }
        }
    }

    public final void checkBackupData(ApplicationInfo applicationInfo, File file) throws IOException, AgentException {
        if (applicationInfo == null || (applicationInfo.flags & 1) != 0) {
            return;
        }
        ParcelFileDescriptor open = ParcelFileDescriptor.open(file, 268435456);
        try {
            BackupDataInput backupDataInput = new BackupDataInput(open.getFileDescriptor());
            while (backupDataInput.readNextHeader()) {
                String key = backupDataInput.getKey();
                if (key != null && key.charAt(0) >= 65280) {
                    this.mReporter.onAgentIllegalKey(this.mCurrentPackage, key);
                    IBackupAgent iBackupAgent = this.mAgent;
                    agentFail(iBackupAgent, "Illegal backup key: " + key);
                    throw AgentException.permanent();
                }
                backupDataInput.skipEntityData();
            }
            open.close();
        } catch (Throwable th) {
            if (open != null) {
                try {
                    open.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void handleCancel(boolean z) {
        Preconditions.checkArgument(z, "Can't partially cancel a key-value backup task");
        markCancel();
        waitCancel();
    }

    @VisibleForTesting
    public void markCancel() {
        this.mReporter.onCancel();
        this.mCancelled = true;
        RemoteCall remoteCall = this.mPendingCall;
        if (remoteCall != null) {
            remoteCall.cancel();
        }
    }

    @VisibleForTesting
    public void waitCancel() {
        this.mCancelAcknowledged.block();
    }

    public final void revertTask() {
        long j;
        this.mReporter.onRevertTask();
        try {
            j = this.mTransportConnection.connectOrThrow("KVBT.revertTask()").requestBackupTime();
        } catch (Exception e) {
            this.mReporter.onTransportRequestBackupTimeError(e);
            j = 0;
        }
        KeyValueBackupJob.schedule(this.mBackupManagerService.getUserId(), this.mBackupManagerService.getContext(), j, this.mBackupManagerService);
        for (String str : this.mOriginalQueue) {
            this.mBackupManagerService.dataChangedImpl(str);
        }
    }

    public final void cleanUpAgentForError(BackupException backupException) {
        cleanUpAgent(1);
    }

    public final void cleanUpAgentForTransportStatus(int i) {
        if (i == -1006) {
            cleanUpAgent(2);
        } else if (i == 0) {
            cleanUpAgent(0);
        } else {
            throw new AssertionError();
        }
    }

    public final void cleanUpAgent(int i) {
        applyStateTransaction(i);
        File file = this.mBackupDataFile;
        if (file != null) {
            file.delete();
        }
        this.mBlankStateFile.delete();
        this.mSavedStateFile = null;
        this.mBackupDataFile = null;
        this.mNewStateFile = null;
        tryCloseFileDescriptor(this.mSavedState, "old state");
        tryCloseFileDescriptor(this.mBackupData, "backup data");
        tryCloseFileDescriptor(this.mNewState, "new state");
        this.mSavedState = null;
        this.mBackupData = null;
        this.mNewState = null;
        ApplicationInfo applicationInfo = this.mCurrentPackage.applicationInfo;
        if (applicationInfo != null) {
            this.mBackupManagerService.unbindAgent(applicationInfo);
        }
        this.mAgent = null;
    }

    public final void applyStateTransaction(int i) {
        if (i == 0) {
            this.mNewStateFile.renameTo(this.mSavedStateFile);
        } else if (i == 1) {
            File file = this.mNewStateFile;
            if (file != null) {
                file.delete();
            }
        } else if (i == 2) {
            this.mSavedStateFile.delete();
            this.mNewStateFile.delete();
        } else {
            throw new IllegalArgumentException("Unknown state transaction " + i);
        }
    }

    public final void tryCloseFileDescriptor(Closeable closeable, String str) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
                this.mReporter.onCloseFileDescriptorError(str);
            }
        }
    }

    public final RemoteResult remoteCall(RemoteCallable<IBackupCallback> remoteCallable, long j, String str) throws RemoteException {
        this.mPendingCall = new RemoteCall(this.mCancelled, remoteCallable, j);
        RemoteResult call = this.mPendingCall.call();
        this.mReporter.onRemoteCallReturned(call, str);
        this.mPendingCall = null;
        return call;
    }
}
