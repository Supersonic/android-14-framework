package com.android.server.backup;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IBackupAgent;
import android.app.PendingIntent;
import android.app.backup.BackupAgent;
import android.app.backup.BackupRestoreEventLogger;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.app.backup.IFullBackupRestoreObserver;
import android.app.backup.IRestoreSession;
import android.app.backup.ISelectBackupTransportCallback;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.FeatureFlagUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.LocalServices;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.FullBackupEntry;
import com.android.server.backup.fullbackup.PerformFullTransportBackupTask;
import com.android.server.backup.internal.BackupHandler;
import com.android.server.backup.internal.ClearDataObserver;
import com.android.server.backup.internal.LifecycleOperationStorage;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.internal.PerformInitializeTask;
import com.android.server.backup.internal.RunInitializeReceiver;
import com.android.server.backup.internal.SetupObserver;
import com.android.server.backup.keyvalue.BackupRequest;
import com.android.server.backup.params.AdbBackupParams;
import com.android.server.backup.params.AdbParams;
import com.android.server.backup.params.AdbRestoreParams;
import com.android.server.backup.params.BackupParams;
import com.android.server.backup.params.ClearParams;
import com.android.server.backup.params.ClearRetryParams;
import com.android.server.backup.params.RestoreParams;
import com.android.server.backup.restore.ActiveRestoreSession;
import com.android.server.backup.restore.PerformUnifiedRestoreTask;
import com.android.server.backup.transport.OnTransportRegisteredListener;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.transport.TransportNotAvailableException;
import com.android.server.backup.transport.TransportNotRegisteredException;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.BackupObserverUtils;
import com.android.server.backup.utils.SparseArrayUtils;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.google.android.collect.Sets;
import dalvik.annotation.optimization.NeverCompile;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes.dex */
public class UserBackupManagerService {
    public ActiveRestoreSession mActiveRestoreSession;
    public final IActivityManager mActivityManager;
    public final ActivityManagerInternal mActivityManagerInternal;
    public final SparseArray<AdbParams> mAdbBackupRestoreConfirmations;
    public final Object mAgentConnectLock;
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public final AlarmManager mAlarmManager;
    public volatile long mAncestralBackupDestination;
    public Set<String> mAncestralPackages;
    public File mAncestralSerialNumberFile;
    public long mAncestralToken;
    public boolean mAutoRestore;
    public final BackupHandler mBackupHandler;
    public final IBackupManager mBackupManagerBinder;
    public final SparseArray<HashSet<String>> mBackupParticipants;
    public final BackupPasswordManager mBackupPasswordManager;
    public final UserBackupPreferences mBackupPreferences;
    public volatile boolean mBackupRunning;
    public final File mBaseStateDir;
    public final Object mClearDataLock;
    public volatile boolean mClearingData;
    public IBackupAgent mConnectedAgent;
    public volatile boolean mConnecting;
    public final BackupManagerConstants mConstants;
    public final Context mContext;
    public long mCurrentToken;
    public final File mDataDir;
    public boolean mEnabled;
    @GuardedBy({"mQueueLock"})
    public ArrayList<FullBackupEntry> mFullBackupQueue;
    public final File mFullBackupScheduleFile;
    public Runnable mFullBackupScheduleWriter;
    @GuardedBy({"mPendingRestores"})
    public boolean mIsRestoreInProgress;
    public DataChangedJournal mJournal;
    public final File mJournalDir;
    public volatile long mLastBackupPass;
    public final AtomicInteger mNextToken;
    public final LifecycleOperationStorage mOperationStorage;
    public final PackageManager mPackageManager;
    public final IPackageManager mPackageManagerBinder;
    public BroadcastReceiver mPackageTrackingReceiver;
    public final HashMap<String, BackupRequest> mPendingBackups;
    public final ArraySet<String> mPendingInits;
    @GuardedBy({"mPendingRestores"})
    public final Queue<PerformUnifiedRestoreTask> mPendingRestores;
    public PowerManager mPowerManager;
    public ProcessedPackagesJournal mProcessedPackagesJournal;
    public final Object mQueueLock;
    public final long mRegisterTransportsRequestedTime;
    public final SecureRandom mRng;
    public final PendingIntent mRunInitIntent;
    public final BroadcastReceiver mRunInitReceiver;
    @GuardedBy({"mQueueLock"})
    public PerformFullTransportBackupTask mRunningFullBackupTask;
    public final BackupEligibilityRules mScheduledBackupEligibility;
    public boolean mSetupComplete;
    public final ContentObserver mSetupObserver;
    public File mTokenFile;
    public final Random mTokenGenerator;
    public final TransportManager mTransportManager;
    public final int mUserId;
    public final BackupWakeLock mWakelock;

    public static /* synthetic */ void lambda$cancelBackups$2(int i) {
    }

    /* loaded from: classes.dex */
    public static class BackupWakeLock {
        public boolean mHasQuit = false;
        public final PowerManager.WakeLock mPowerManagerWakeLock;
        public int mUserId;

        public BackupWakeLock(PowerManager.WakeLock wakeLock, int i) {
            this.mPowerManagerWakeLock = wakeLock;
            this.mUserId = i;
        }

        public synchronized void acquire() {
            if (this.mHasQuit) {
                int i = this.mUserId;
                Slog.v("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(i, "Ignore wakelock acquire after quit: " + this.mPowerManagerWakeLock.getTag()));
                return;
            }
            this.mPowerManagerWakeLock.acquire();
            int i2 = this.mUserId;
            Slog.v("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(i2, "Acquired wakelock:" + this.mPowerManagerWakeLock.getTag()));
        }

        public synchronized void release() {
            if (this.mHasQuit) {
                int i = this.mUserId;
                Slog.v("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(i, "Ignore wakelock release after quit: " + this.mPowerManagerWakeLock.getTag()));
                return;
            }
            this.mPowerManagerWakeLock.release();
            int i2 = this.mUserId;
            Slog.v("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(i2, "Released wakelock:" + this.mPowerManagerWakeLock.getTag()));
        }

        public synchronized void quit() {
            while (this.mPowerManagerWakeLock.isHeld()) {
                int i = this.mUserId;
                Slog.v("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(i, "Releasing wakelock: " + this.mPowerManagerWakeLock.getTag()));
                this.mPowerManagerWakeLock.release();
            }
            this.mHasQuit = true;
        }
    }

    public static UserBackupManagerService createAndInitializeService(int i, Context context, BackupManagerService backupManagerService, Set<ComponentName> set) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "backup_transport", i);
        if (TextUtils.isEmpty(stringForUser)) {
            stringForUser = null;
        }
        Slog.v("BackupManagerService", addUserIdToLogMessage(i, "Starting with transport " + stringForUser));
        TransportManager transportManager = new TransportManager(i, context, set, stringForUser);
        File baseStateDir = UserBackupManagerFiles.getBaseStateDir(i);
        File dataDir = UserBackupManagerFiles.getDataDir(i);
        HandlerThread handlerThread = new HandlerThread("backup-" + i, 10);
        handlerThread.start();
        Slog.d("BackupManagerService", addUserIdToLogMessage(i, "Started thread " + handlerThread.getName()));
        return createAndInitializeService(i, context, backupManagerService, handlerThread, baseStateDir, dataDir, transportManager);
    }

    @VisibleForTesting
    public static UserBackupManagerService createAndInitializeService(int i, Context context, BackupManagerService backupManagerService, HandlerThread handlerThread, File file, File file2, TransportManager transportManager) {
        return new UserBackupManagerService(i, context, backupManagerService, handlerThread, file, file2, transportManager);
    }

    public static boolean getSetupCompleteSettingForUser(Context context, int i) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "user_setup_complete", 0, i) != 0;
    }

    @VisibleForTesting
    public UserBackupManagerService(Context context, PackageManager packageManager, LifecycleOperationStorage lifecycleOperationStorage, TransportManager transportManager) {
        this.mPendingInits = new ArraySet<>();
        this.mBackupParticipants = new SparseArray<>();
        this.mPendingBackups = new HashMap<>();
        this.mQueueLock = new Object();
        this.mAgentConnectLock = new Object();
        this.mClearDataLock = new Object();
        this.mAdbBackupRestoreConfirmations = new SparseArray<>();
        this.mRng = new SecureRandom();
        this.mPendingRestores = new ArrayDeque();
        this.mTokenGenerator = new Random();
        this.mNextToken = new AtomicInteger();
        this.mAncestralPackages = null;
        this.mAncestralToken = 0L;
        this.mCurrentToken = 0L;
        this.mFullBackupScheduleWriter = new Runnable() { // from class: com.android.server.backup.UserBackupManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (UserBackupManagerService.this.mQueueLock) {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(IInstalld.FLAG_USE_QUOTA);
                        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream.writeInt(1);
                        int size = UserBackupManagerService.this.mFullBackupQueue.size();
                        dataOutputStream.writeInt(size);
                        for (int i = 0; i < size; i++) {
                            FullBackupEntry fullBackupEntry = (FullBackupEntry) UserBackupManagerService.this.mFullBackupQueue.get(i);
                            dataOutputStream.writeUTF(fullBackupEntry.packageName);
                            dataOutputStream.writeLong(fullBackupEntry.lastBackup);
                        }
                        dataOutputStream.flush();
                        AtomicFile atomicFile = new AtomicFile(UserBackupManagerService.this.mFullBackupScheduleFile);
                        FileOutputStream startWrite = atomicFile.startWrite();
                        startWrite.write(byteArrayOutputStream.toByteArray());
                        atomicFile.finishWrite(startWrite);
                    } catch (Exception e) {
                        Slog.e("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(UserBackupManagerService.this.mUserId, "Unable to write backup schedule!"), e);
                    }
                }
            }
        };
        this.mPackageTrackingReceiver = new C05572();
        this.mContext = context;
        this.mUserId = 0;
        this.mRegisterTransportsRequestedTime = 0L;
        this.mPackageManager = packageManager;
        this.mOperationStorage = lifecycleOperationStorage;
        this.mTransportManager = transportManager;
        this.mBaseStateDir = null;
        this.mDataDir = null;
        this.mJournalDir = null;
        this.mFullBackupScheduleFile = null;
        this.mSetupObserver = null;
        this.mRunInitReceiver = null;
        this.mRunInitIntent = null;
        this.mAgentTimeoutParameters = null;
        this.mActivityManagerInternal = null;
        this.mAlarmManager = null;
        this.mConstants = null;
        this.mWakelock = null;
        this.mBackupHandler = null;
        this.mBackupPreferences = null;
        this.mBackupPasswordManager = null;
        this.mPackageManagerBinder = null;
        this.mActivityManager = null;
        this.mBackupManagerBinder = null;
        this.mScheduledBackupEligibility = null;
    }

    public UserBackupManagerService(int i, Context context, BackupManagerService backupManagerService, HandlerThread handlerThread, File file, File file2, final TransportManager transportManager) {
        this.mPendingInits = new ArraySet<>();
        SparseArray<HashSet<String>> sparseArray = new SparseArray<>();
        this.mBackupParticipants = sparseArray;
        this.mPendingBackups = new HashMap<>();
        this.mQueueLock = new Object();
        this.mAgentConnectLock = new Object();
        this.mClearDataLock = new Object();
        this.mAdbBackupRestoreConfirmations = new SparseArray<>();
        SecureRandom secureRandom = new SecureRandom();
        this.mRng = secureRandom;
        this.mPendingRestores = new ArrayDeque();
        this.mTokenGenerator = new Random();
        this.mNextToken = new AtomicInteger();
        this.mAncestralPackages = null;
        this.mAncestralToken = 0L;
        this.mCurrentToken = 0L;
        this.mFullBackupScheduleWriter = new Runnable() { // from class: com.android.server.backup.UserBackupManagerService.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (UserBackupManagerService.this.mQueueLock) {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(IInstalld.FLAG_USE_QUOTA);
                        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream.writeInt(1);
                        int size = UserBackupManagerService.this.mFullBackupQueue.size();
                        dataOutputStream.writeInt(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            FullBackupEntry fullBackupEntry = (FullBackupEntry) UserBackupManagerService.this.mFullBackupQueue.get(i2);
                            dataOutputStream.writeUTF(fullBackupEntry.packageName);
                            dataOutputStream.writeLong(fullBackupEntry.lastBackup);
                        }
                        dataOutputStream.flush();
                        AtomicFile atomicFile = new AtomicFile(UserBackupManagerService.this.mFullBackupScheduleFile);
                        FileOutputStream startWrite = atomicFile.startWrite();
                        startWrite.write(byteArrayOutputStream.toByteArray());
                        atomicFile.finishWrite(startWrite);
                    } catch (Exception e) {
                        Slog.e("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(UserBackupManagerService.this.mUserId, "Unable to write backup schedule!"), e);
                    }
                }
            }
        };
        this.mPackageTrackingReceiver = new C05572();
        this.mUserId = i;
        Objects.requireNonNull(context, "context cannot be null");
        this.mContext = context;
        PackageManager packageManager = context.getPackageManager();
        this.mPackageManager = packageManager;
        this.mPackageManagerBinder = AppGlobals.getPackageManager();
        this.mActivityManager = ActivityManager.getService();
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mScheduledBackupEligibility = getEligibilityRules(packageManager, i, context, 0);
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        Objects.requireNonNull(backupManagerService, "parent cannot be null");
        this.mBackupManagerBinder = IBackupManager.Stub.asInterface(backupManagerService.asBinder());
        BackupAgentTimeoutParameters backupAgentTimeoutParameters = new BackupAgentTimeoutParameters(Handler.getMain(), context.getContentResolver());
        this.mAgentTimeoutParameters = backupAgentTimeoutParameters;
        backupAgentTimeoutParameters.start();
        LifecycleOperationStorage lifecycleOperationStorage = new LifecycleOperationStorage(i);
        this.mOperationStorage = lifecycleOperationStorage;
        Objects.requireNonNull(handlerThread, "userBackupThread cannot be null");
        BackupHandler backupHandler = new BackupHandler(this, lifecycleOperationStorage, handlerThread);
        this.mBackupHandler = backupHandler;
        ContentResolver contentResolver = context.getContentResolver();
        this.mSetupComplete = getSetupCompleteSettingForUser(context, i);
        this.mAutoRestore = Settings.Secure.getIntForUser(contentResolver, "backup_auto_restore", 1, i) != 0;
        SetupObserver setupObserver = new SetupObserver(this, backupHandler);
        this.mSetupObserver = setupObserver;
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, setupObserver, i);
        Objects.requireNonNull(file, "baseStateDir cannot be null");
        this.mBaseStateDir = file;
        if (i == 0) {
            file.mkdirs();
            if (!SELinux.restorecon(file)) {
                Slog.w("BackupManagerService", addUserIdToLogMessage(i, "SELinux restorecon failed on " + file));
            }
        }
        Objects.requireNonNull(file2, "dataDir cannot be null");
        this.mDataDir = file2;
        this.mBackupPasswordManager = new BackupPasswordManager(context, file, secureRandom);
        RunInitializeReceiver runInitializeReceiver = new RunInitializeReceiver(this);
        this.mRunInitReceiver = runInitializeReceiver;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.app.backup.intent.INIT");
        context.registerReceiverAsUser(runInitializeReceiver, UserHandle.of(i), intentFilter, "android.permission.BACKUP", null);
        Intent intent = new Intent("android.app.backup.intent.INIT");
        intent.addFlags(1073741824);
        this.mRunInitIntent = PendingIntent.getBroadcastAsUser(context, 0, intent, 67108864, UserHandle.of(i));
        File file3 = new File(file, "pending");
        this.mJournalDir = file3;
        file3.mkdirs();
        this.mJournal = null;
        BackupManagerConstants backupManagerConstants = new BackupManagerConstants(backupHandler, context.getContentResolver());
        this.mConstants = backupManagerConstants;
        backupManagerConstants.start();
        synchronized (sparseArray) {
            addPackageParticipantsLocked(null);
        }
        Objects.requireNonNull(transportManager, "transportManager cannot be null");
        this.mTransportManager = transportManager;
        transportManager.setOnTransportRegisteredListener(new OnTransportRegisteredListener() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda0
            @Override // com.android.server.backup.transport.OnTransportRegisteredListener
            public final void onTransportRegistered(String str, String str2) {
                UserBackupManagerService.this.onTransportRegistered(str, str2);
            }
        });
        this.mRegisterTransportsRequestedTime = SystemClock.elapsedRealtime();
        Objects.requireNonNull(transportManager);
        backupHandler.postDelayed(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TransportManager.this.registerTransports();
            }
        }, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        backupHandler.postDelayed(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                UserBackupManagerService.this.parseLeftoverJournals();
            }
        }, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        this.mBackupPreferences = new UserBackupPreferences(context, file);
        PowerManager powerManager = this.mPowerManager;
        this.mWakelock = new BackupWakeLock(powerManager.newWakeLock(1, "*backup*-" + i + PackageManagerShellCommandDataLoader.STDIN_PATH + handlerThread.getThreadId()), i);
        this.mFullBackupScheduleFile = new File(file, "fb-schedule");
        initPackageTracking();
    }

    @VisibleForTesting
    public void initializeBackupEnableState() {
        setBackupEnabled(readEnabledState(), false);
    }

    @VisibleForTesting
    public void tearDownService() {
        this.mAgentTimeoutParameters.stop();
        this.mConstants.stop();
        this.mContext.getContentResolver().unregisterContentObserver(this.mSetupObserver);
        this.mContext.unregisterReceiver(this.mRunInitReceiver);
        this.mContext.unregisterReceiver(this.mPackageTrackingReceiver);
        this.mBackupHandler.stop();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public BackupManagerConstants getConstants() {
        return this.mConstants;
    }

    public BackupAgentTimeoutParameters getAgentTimeoutParameters() {
        return this.mAgentTimeoutParameters;
    }

    public Context getContext() {
        return this.mContext;
    }

    public PackageManager getPackageManager() {
        return this.mPackageManager;
    }

    public IPackageManager getPackageManagerBinder() {
        return this.mPackageManagerBinder;
    }

    public IActivityManager getActivityManager() {
        return this.mActivityManager;
    }

    public AlarmManager getAlarmManager() {
        return this.mAlarmManager;
    }

    @VisibleForTesting
    public void setPowerManager(PowerManager powerManager) {
        this.mPowerManager = powerManager;
    }

    public TransportManager getTransportManager() {
        return this.mTransportManager;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public boolean isSetupComplete() {
        return this.mSetupComplete;
    }

    public void setSetupComplete(boolean z) {
        this.mSetupComplete = z;
    }

    public BackupWakeLock getWakelock() {
        return this.mWakelock;
    }

    @VisibleForTesting
    public void setWorkSource(WorkSource workSource) {
        this.mWakelock.mPowerManagerWakeLock.setWorkSource(workSource);
    }

    public Handler getBackupHandler() {
        return this.mBackupHandler;
    }

    public PendingIntent getRunInitIntent() {
        return this.mRunInitIntent;
    }

    public HashMap<String, BackupRequest> getPendingBackups() {
        return this.mPendingBackups;
    }

    public Object getQueueLock() {
        return this.mQueueLock;
    }

    public boolean isBackupRunning() {
        return this.mBackupRunning;
    }

    public void setBackupRunning(boolean z) {
        this.mBackupRunning = z;
    }

    public void setLastBackupPass(long j) {
        this.mLastBackupPass = j;
    }

    public Object getClearDataLock() {
        return this.mClearDataLock;
    }

    public void setClearingData(boolean z) {
        this.mClearingData = z;
    }

    public boolean isRestoreInProgress() {
        return this.mIsRestoreInProgress;
    }

    public void setRestoreInProgress(boolean z) {
        this.mIsRestoreInProgress = z;
    }

    public Queue<PerformUnifiedRestoreTask> getPendingRestores() {
        return this.mPendingRestores;
    }

    public ActiveRestoreSession getActiveRestoreSession() {
        return this.mActiveRestoreSession;
    }

    public SparseArray<AdbParams> getAdbBackupRestoreConfirmations() {
        return this.mAdbBackupRestoreConfirmations;
    }

    public File getBaseStateDir() {
        return this.mBaseStateDir;
    }

    public File getDataDir() {
        return this.mDataDir;
    }

    @VisibleForTesting
    public BroadcastReceiver getPackageTrackingReceiver() {
        return this.mPackageTrackingReceiver;
    }

    public DataChangedJournal getJournal() {
        return this.mJournal;
    }

    public void setJournal(DataChangedJournal dataChangedJournal) {
        this.mJournal = dataChangedJournal;
    }

    public SecureRandom getRng() {
        return this.mRng;
    }

    public void setAncestralPackages(Set<String> set) {
        this.mAncestralPackages = set;
    }

    public void setAncestralToken(long j) {
        this.mAncestralToken = j;
    }

    public void setAncestralBackupDestination(int i) {
        this.mAncestralBackupDestination = i;
    }

    public long getCurrentToken() {
        return this.mCurrentToken;
    }

    public void setCurrentToken(long j) {
        this.mCurrentToken = j;
    }

    public ArraySet<String> getPendingInits() {
        return this.mPendingInits;
    }

    public void clearPendingInits() {
        this.mPendingInits.clear();
    }

    public void setRunningFullBackupTask(PerformFullTransportBackupTask performFullTransportBackupTask) {
        this.mRunningFullBackupTask = performFullTransportBackupTask;
    }

    public int generateRandomIntegerToken() {
        int nextInt = this.mTokenGenerator.nextInt();
        if (nextInt < 0) {
            nextInt = -nextInt;
        }
        return (this.mNextToken.incrementAndGet() & 255) | (nextInt & (-256));
    }

    public BackupAgent makeMetadataAgentWithEligibilityRules(BackupEligibilityRules backupEligibilityRules) {
        PackageManagerBackupAgent packageManagerBackupAgent = new PackageManagerBackupAgent(this.mPackageManager, this.mUserId, backupEligibilityRules);
        packageManagerBackupAgent.attach(this.mContext);
        packageManagerBackupAgent.onCreate(UserHandle.of(this.mUserId));
        return packageManagerBackupAgent;
    }

    public PackageManagerBackupAgent makeMetadataAgent(List<PackageInfo> list) {
        PackageManagerBackupAgent packageManagerBackupAgent = new PackageManagerBackupAgent(this.mPackageManager, list, this.mUserId);
        packageManagerBackupAgent.attach(this.mContext);
        packageManagerBackupAgent.onCreate(UserHandle.of(this.mUserId));
        return packageManagerBackupAgent;
    }

    public final void initPackageTracking() {
        this.mTokenFile = new File(this.mBaseStateDir, "ancestral");
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(this.mTokenFile)));
            if (dataInputStream.readInt() == 1) {
                this.mAncestralToken = dataInputStream.readLong();
                this.mCurrentToken = dataInputStream.readLong();
                int readInt = dataInputStream.readInt();
                if (readInt >= 0) {
                    this.mAncestralPackages = new HashSet();
                    for (int i = 0; i < readInt; i++) {
                        this.mAncestralPackages.add(dataInputStream.readUTF());
                    }
                }
            }
            dataInputStream.close();
        } catch (FileNotFoundException unused) {
            Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "No ancestral data"));
        } catch (IOException e) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to read token file"), e);
        }
        ProcessedPackagesJournal processedPackagesJournal = new ProcessedPackagesJournal(this.mBaseStateDir);
        this.mProcessedPackagesJournal = processedPackagesJournal;
        processedPackagesJournal.init();
        synchronized (this.mQueueLock) {
            this.mFullBackupQueue = readFullBackupSchedule();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mPackageTrackingReceiver, UserHandle.of(this.mUserId), intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiverAsUser(this.mPackageTrackingReceiver, UserHandle.of(this.mUserId), intentFilter2, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:81:0x016d  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01aa  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x01ad  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ArrayList<FullBackupEntry> readFullBackupSchedule() {
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2;
        Throwable th;
        Throwable th2;
        ArrayList<FullBackupEntry> arrayList;
        boolean z;
        List<PackageInfo> storableApplications = PackageManagerBackupAgent.getStorableApplications(this.mPackageManager, this.mUserId, this.mScheduledBackupEligibility);
        boolean z2 = false;
        if (this.mFullBackupScheduleFile.exists()) {
            try {
                fileInputStream = new FileInputStream(this.mFullBackupScheduleFile);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
            } catch (Exception e) {
                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to read backup schedule"), e);
                this.mFullBackupScheduleFile.delete();
            }
            try {
                try {
                    DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                    try {
                        int readInt = dataInputStream.readInt();
                        if (readInt != 1) {
                            try {
                                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unknown backup schedule version " + readInt));
                                try {
                                    dataInputStream.close();
                                    bufferedInputStream.close();
                                    fileInputStream.close();
                                    return null;
                                } catch (Throwable th3) {
                                    th = th3;
                                    bufferedInputStream2 = bufferedInputStream;
                                    try {
                                        bufferedInputStream2.close();
                                    } catch (Throwable th4) {
                                        th.addSuppressed(th4);
                                    }
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th2 = th5;
                                dataInputStream.close();
                                throw th2;
                            }
                        }
                        int readInt2 = dataInputStream.readInt();
                        ArrayList<FullBackupEntry> arrayList2 = new ArrayList<>(readInt2);
                        HashSet hashSet = new HashSet(readInt2);
                        int i = 0;
                        while (i < readInt2) {
                            String readUTF = dataInputStream.readUTF();
                            BufferedInputStream bufferedInputStream3 = bufferedInputStream;
                            try {
                                long readLong = dataInputStream.readLong();
                                hashSet.add(readUTF);
                                try {
                                    PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(readUTF, 0, this.mUserId);
                                    if (this.mScheduledBackupEligibility.appGetsFullBackup(packageInfoAsUser) && this.mScheduledBackupEligibility.appIsEligibleForBackup(packageInfoAsUser.applicationInfo)) {
                                        arrayList2.add(new FullBackupEntry(readUTF, readLong));
                                    } else {
                                        Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Package " + readUTF + " no longer eligible for full backup"));
                                    }
                                } catch (PackageManager.NameNotFoundException unused) {
                                    Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Package " + readUTF + " not installed; dropping from full backup"));
                                }
                                i++;
                                bufferedInputStream = bufferedInputStream3;
                            } catch (Throwable th6) {
                                th = th6;
                                th2 = th;
                                dataInputStream.close();
                                throw th2;
                            }
                        }
                        BufferedInputStream bufferedInputStream4 = bufferedInputStream;
                        for (PackageInfo packageInfo : storableApplications) {
                            if (this.mScheduledBackupEligibility.appGetsFullBackup(packageInfo) && this.mScheduledBackupEligibility.appIsEligibleForBackup(packageInfo.applicationInfo) && !hashSet.contains(packageInfo.packageName)) {
                                arrayList2.add(new FullBackupEntry(packageInfo.packageName, 0L));
                                z2 = true;
                            }
                        }
                        Collections.sort(arrayList2);
                        dataInputStream.close();
                        bufferedInputStream4.close();
                        fileInputStream.close();
                        arrayList = arrayList2;
                        if (arrayList != null) {
                            arrayList = new ArrayList<>(storableApplications.size());
                            for (PackageInfo packageInfo2 : storableApplications) {
                                if (this.mScheduledBackupEligibility.appGetsFullBackup(packageInfo2) && this.mScheduledBackupEligibility.appIsEligibleForBackup(packageInfo2.applicationInfo)) {
                                    arrayList.add(new FullBackupEntry(packageInfo2.packageName, 0L));
                                }
                            }
                            z = true;
                        } else {
                            z = z2;
                        }
                        if (z) {
                            writeFullBackupScheduleAsync();
                        }
                        return arrayList;
                    } catch (Throwable th7) {
                        th = th7;
                    }
                } catch (Throwable th8) {
                    th = th8;
                    th = th;
                    bufferedInputStream2.close();
                    throw th;
                }
            } catch (Throwable th9) {
                th = th9;
                bufferedInputStream2 = bufferedInputStream;
                th = th;
                bufferedInputStream2.close();
                throw th;
            }
        }
        arrayList = null;
        if (arrayList != null) {
        }
        if (z) {
        }
        return arrayList;
    }

    public final void writeFullBackupScheduleAsync() {
        this.mBackupHandler.removeCallbacks(this.mFullBackupScheduleWriter);
        this.mBackupHandler.post(this.mFullBackupScheduleWriter);
    }

    public final void parseLeftoverJournals() {
        ArrayList<DataChangedJournal> listJournals = DataChangedJournal.listJournals(this.mJournalDir);
        listJournals.removeAll(Collections.singletonList(this.mJournal));
        if (!listJournals.isEmpty()) {
            int i = this.mUserId;
            Slog.i("BackupManagerService", addUserIdToLogMessage(i, "Found " + listJournals.size() + " stale backup journal(s), scheduling."));
        }
        final LinkedHashSet linkedHashSet = new LinkedHashSet();
        Iterator<DataChangedJournal> it = listJournals.iterator();
        while (it.hasNext()) {
            DataChangedJournal next = it.next();
            try {
                next.forEach(new Consumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        UserBackupManagerService.this.lambda$parseLeftoverJournals$0(linkedHashSet, (String) obj);
                    }
                });
            } catch (IOException e) {
                int i2 = this.mUserId;
                Slog.e("BackupManagerService", addUserIdToLogMessage(i2, "Can't read " + next), e);
            }
        }
        if (linkedHashSet.isEmpty()) {
            return;
        }
        Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Stale backup journals: Scheduled " + linkedHashSet.size() + " package(s) total"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$parseLeftoverJournals$0(Set set, String str) {
        if (set.add(str)) {
            dataChangedImpl(str);
        }
    }

    public Set<String> getExcludedRestoreKeys(String str) {
        return this.mBackupPreferences.getExcludedRestoreKeysForPackage(str);
    }

    public byte[] randomBytes(int i) {
        byte[] bArr = new byte[i / 8];
        this.mRng.nextBytes(bArr);
        return bArr;
    }

    public boolean setBackupPassword(String str, String str2) {
        return this.mBackupPasswordManager.setBackupPassword(str, str2);
    }

    public boolean hasBackupPassword() {
        return this.mBackupPasswordManager.hasBackupPassword();
    }

    public boolean backupPasswordMatches(String str) {
        return this.mBackupPasswordManager.backupPasswordMatches(str);
    }

    public void recordInitPending(boolean z, String str, String str2) {
        synchronized (this.mQueueLock) {
            File file = new File(new File(this.mBaseStateDir, str2), "_need_init_");
            if (z) {
                this.mPendingInits.add(str);
                try {
                    new FileOutputStream(file).close();
                } catch (IOException unused) {
                }
            } else {
                file.delete();
                this.mPendingInits.remove(str);
            }
        }
    }

    public void resetBackupState(File file) {
        File[] listFiles;
        int i;
        synchronized (this.mQueueLock) {
            this.mProcessedPackagesJournal.reset();
            this.mCurrentToken = 0L;
            writeRestoreTokens();
            for (File file2 : file.listFiles()) {
                if (!file2.getName().equals("_need_init_")) {
                    file2.delete();
                }
            }
        }
        synchronized (this.mBackupParticipants) {
            int size = this.mBackupParticipants.size();
            for (i = 0; i < size; i++) {
                HashSet<String> valueAt = this.mBackupParticipants.valueAt(i);
                if (valueAt != null) {
                    Iterator<String> it = valueAt.iterator();
                    while (it.hasNext()) {
                        dataChangedImpl(it.next());
                    }
                }
            }
        }
    }

    public final void onTransportRegistered(String str, String str2) {
        int i = this.mUserId;
        Slog.d("BackupManagerService", addUserIdToLogMessage(i, "Transport " + str + " registered " + (SystemClock.elapsedRealtime() - this.mRegisterTransportsRequestedTime) + "ms after first request (delay = " + BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS + "ms)"));
        File file = new File(this.mBaseStateDir, str2);
        file.mkdirs();
        if (new File(file, "_need_init_").exists()) {
            synchronized (this.mQueueLock) {
                this.mPendingInits.add(str);
                this.mAlarmManager.set(0, System.currentTimeMillis() + 60000, this.mRunInitIntent);
            }
        }
    }

    /* renamed from: com.android.server.backup.UserBackupManagerService$2 */
    /* loaded from: classes.dex */
    public class C05572 extends BroadcastReceiver {
        public C05572() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String[] strArr;
            boolean equals;
            boolean z;
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_CHANGED".equals(action)) {
                Uri data = intent.getData();
                if (data == null) {
                    return;
                }
                final String schemeSpecificPart = data.getSchemeSpecificPart();
                strArr = schemeSpecificPart != null ? new String[]{schemeSpecificPart} : null;
                if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                    final String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                    UserBackupManagerService.this.mBackupHandler.post(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            UserBackupManagerService.C05572.this.lambda$onReceive$0(schemeSpecificPart, stringArrayExtra);
                        }
                    });
                    return;
                }
                equals = "android.intent.action.PACKAGE_ADDED".equals(action);
                z = extras.getBoolean("android.intent.extra.REPLACING", false);
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action)) {
                strArr = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                equals = true;
                z = false;
            } else {
                strArr = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action) ? intent.getStringArrayExtra("android.intent.extra.changed_package_list") : null;
                equals = false;
                z = false;
            }
            if (strArr == null || strArr.length == 0) {
                return;
            }
            int i = extras.getInt("android.intent.extra.UID");
            if (equals) {
                synchronized (UserBackupManagerService.this.mBackupParticipants) {
                    if (z) {
                        UserBackupManagerService.this.removePackageParticipantsLocked(strArr, i);
                    }
                    UserBackupManagerService.this.addPackageParticipantsLocked(strArr);
                }
                long currentTimeMillis = System.currentTimeMillis();
                for (final String str : strArr) {
                    try {
                        PackageInfo packageInfoAsUser = UserBackupManagerService.this.mPackageManager.getPackageInfoAsUser(str, 0, UserBackupManagerService.this.mUserId);
                        if (UserBackupManagerService.this.mScheduledBackupEligibility.appGetsFullBackup(packageInfoAsUser) && UserBackupManagerService.this.mScheduledBackupEligibility.appIsEligibleForBackup(packageInfoAsUser.applicationInfo)) {
                            UserBackupManagerService.this.enqueueFullBackup(str, currentTimeMillis);
                            UserBackupManagerService.this.scheduleNextFullBackupJob(0L);
                        } else {
                            synchronized (UserBackupManagerService.this.mQueueLock) {
                                UserBackupManagerService.this.dequeueFullBackupLocked(str);
                            }
                            UserBackupManagerService.this.writeFullBackupScheduleAsync();
                        }
                        UserBackupManagerService.this.mBackupHandler.post(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$2$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                UserBackupManagerService.C05572.this.lambda$onReceive$1(str);
                            }
                        });
                    } catch (PackageManager.NameNotFoundException unused) {
                        Slog.w("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(UserBackupManagerService.this.mUserId, "Can't resolve new app " + str));
                    }
                }
                UserBackupManagerService.this.dataChangedImpl("@pm@");
                return;
            }
            if (!z) {
                synchronized (UserBackupManagerService.this.mBackupParticipants) {
                    UserBackupManagerService.this.removePackageParticipantsLocked(strArr, i);
                }
            }
            for (final String str2 : strArr) {
                UserBackupManagerService.this.mBackupHandler.post(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$2$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        UserBackupManagerService.C05572.this.lambda$onReceive$2(str2);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(String str, String[] strArr) {
            UserBackupManagerService.this.mTransportManager.onPackageChanged(str, strArr);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$1(String str) {
            UserBackupManagerService.this.mTransportManager.onPackageAdded(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$2(String str) {
            UserBackupManagerService.this.mTransportManager.onPackageRemoved(str);
        }
    }

    public final void addPackageParticipantsLocked(String[] strArr) {
        List<PackageInfo> allAgentPackages = allAgentPackages();
        if (strArr != null) {
            for (String str : strArr) {
                addPackageParticipantsLockedInner(str, allAgentPackages);
            }
            return;
        }
        addPackageParticipantsLockedInner(null, allAgentPackages);
    }

    public final void addPackageParticipantsLockedInner(String str, List<PackageInfo> list) {
        for (PackageInfo packageInfo : list) {
            if (str == null || packageInfo.packageName.equals(str)) {
                int i = packageInfo.applicationInfo.uid;
                HashSet<String> hashSet = this.mBackupParticipants.get(i);
                if (hashSet == null) {
                    hashSet = new HashSet<>();
                    this.mBackupParticipants.put(i, hashSet);
                }
                hashSet.add(packageInfo.packageName);
                this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(16, packageInfo.packageName));
            }
        }
    }

    public final void removePackageParticipantsLocked(String[] strArr, int i) {
        if (strArr == null) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "removePackageParticipants with null list"));
            return;
        }
        for (String str : strArr) {
            HashSet<String> hashSet = this.mBackupParticipants.get(i);
            if (hashSet != null && hashSet.contains(str)) {
                removePackageFromSetLocked(hashSet, str);
                if (hashSet.isEmpty()) {
                    this.mBackupParticipants.remove(i);
                }
            }
        }
    }

    public final void removePackageFromSetLocked(HashSet<String> hashSet, String str) {
        if (hashSet.contains(str)) {
            hashSet.remove(str);
            this.mPendingBackups.remove(str);
        }
    }

    public final List<PackageInfo> allAgentPackages() {
        ApplicationInfo applicationInfo;
        int i;
        List<PackageInfo> installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(134217728, this.mUserId);
        for (int size = installedPackagesAsUser.size() - 1; size >= 0; size--) {
            PackageInfo packageInfo = installedPackagesAsUser.get(size);
            try {
                applicationInfo = packageInfo.applicationInfo;
                i = applicationInfo.flags;
            } catch (PackageManager.NameNotFoundException unused) {
                installedPackagesAsUser.remove(size);
            }
            if ((32768 & i) != 0 && applicationInfo.backupAgentName != null && (67108864 & i) == 0) {
                ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(packageInfo.packageName, 1024, this.mUserId);
                ApplicationInfo applicationInfo2 = packageInfo.applicationInfo;
                applicationInfo2.sharedLibraryFiles = applicationInfoAsUser.sharedLibraryFiles;
                applicationInfo2.sharedLibraryInfos = applicationInfoAsUser.sharedLibraryInfos;
            }
            installedPackagesAsUser.remove(size);
        }
        return installedPackagesAsUser;
    }

    public void logBackupComplete(String str) {
        String[] backupFinishedNotificationReceivers;
        if (str.equals("@pm@")) {
            return;
        }
        for (String str2 : this.mConstants.getBackupFinishedNotificationReceivers()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.BACKUP_FINISHED");
            intent.setPackage(str2);
            intent.addFlags(268435488);
            intent.putExtra("packageName", str);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.of(this.mUserId));
        }
        this.mProcessedPackagesJournal.addPackage(str);
    }

    public void writeRestoreTokens() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.mTokenFile, "rwd");
            randomAccessFile.writeInt(1);
            randomAccessFile.writeLong(this.mAncestralToken);
            randomAccessFile.writeLong(this.mCurrentToken);
            Set<String> set = this.mAncestralPackages;
            if (set == null) {
                randomAccessFile.writeInt(-1);
            } else {
                randomAccessFile.writeInt(set.size());
                int i = this.mUserId;
                Slog.v("BackupManagerService", addUserIdToLogMessage(i, "Ancestral packages:  " + this.mAncestralPackages.size()));
                for (String str : this.mAncestralPackages) {
                    randomAccessFile.writeUTF(str);
                }
            }
            randomAccessFile.close();
        } catch (IOException e) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to write token file:"), e);
        }
    }

    public IBackupAgent bindToAgentSynchronous(ApplicationInfo applicationInfo, int i, int i2) {
        IBackupAgent iBackupAgent;
        synchronized (this.mAgentConnectLock) {
            this.mConnecting = true;
            iBackupAgent = null;
            this.mConnectedAgent = null;
            try {
                if (this.mActivityManager.bindBackupAgent(applicationInfo.packageName, i, this.mUserId, i2)) {
                    int i3 = this.mUserId;
                    Slog.d("BackupManagerService", addUserIdToLogMessage(i3, "awaiting agent for " + applicationInfo));
                    long currentTimeMillis = System.currentTimeMillis() + 10000;
                    while (this.mConnecting && this.mConnectedAgent == null && System.currentTimeMillis() < currentTimeMillis) {
                        try {
                            this.mAgentConnectLock.wait(5000L);
                        } catch (InterruptedException e) {
                            int i4 = this.mUserId;
                            Slog.w("BackupManagerService", addUserIdToLogMessage(i4, "Interrupted: " + e));
                            this.mConnecting = false;
                            this.mConnectedAgent = null;
                        }
                    }
                    if (this.mConnecting) {
                        int i5 = this.mUserId;
                        Slog.w("BackupManagerService", addUserIdToLogMessage(i5, "Timeout waiting for agent " + applicationInfo));
                        this.mConnectedAgent = null;
                    }
                    int i6 = this.mUserId;
                    Slog.i("BackupManagerService", addUserIdToLogMessage(i6, "got agent " + this.mConnectedAgent));
                    iBackupAgent = this.mConnectedAgent;
                }
            } catch (RemoteException unused) {
            }
        }
        if (iBackupAgent == null) {
            this.mActivityManagerInternal.clearPendingBackup(this.mUserId);
        }
        return iBackupAgent;
    }

    public void unbindAgent(ApplicationInfo applicationInfo) {
        try {
            this.mActivityManager.unbindBackupAgent(applicationInfo);
        } catch (RemoteException unused) {
        }
    }

    public void clearApplicationDataAfterRestoreFailure(String str) {
        clearApplicationDataSynchronous(str, true, false);
    }

    public void clearApplicationDataBeforeRestore(String str) {
        clearApplicationDataSynchronous(str, false, true);
    }

    public final void clearApplicationDataSynchronous(String str, boolean z, boolean z2) {
        try {
            ApplicationInfo applicationInfo = this.mPackageManager.getPackageInfoAsUser(str, 0, this.mUserId).applicationInfo;
            if (!z || applicationInfo.targetSdkVersion < 29 ? (applicationInfo.flags & 64) != 0 : (applicationInfo.privateFlags & 67108864) != 0) {
                ClearDataObserver clearDataObserver = new ClearDataObserver(this);
                synchronized (this.mClearDataLock) {
                    this.mClearingData = true;
                    try {
                        this.mActivityManager.clearApplicationUserData(str, z2, clearDataObserver, this.mUserId);
                    } catch (RemoteException unused) {
                    }
                    long currentTimeMillis = System.currentTimeMillis() + 30000;
                    while (this.mClearingData && System.currentTimeMillis() < currentTimeMillis) {
                        try {
                            this.mClearDataLock.wait(5000L);
                        } catch (InterruptedException e) {
                            this.mClearingData = false;
                            int i = this.mUserId;
                            Slog.w("BackupManagerService", addUserIdToLogMessage(i, "Interrupted while waiting for " + str + " data to be cleared"), e);
                        }
                    }
                    if (this.mClearingData) {
                        int i2 = this.mUserId;
                        Slog.w("BackupManagerService", addUserIdToLogMessage(i2, "Clearing app data for " + str + " timed out"));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            int i3 = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i3, "Tried to clear data for " + str + " but not found"));
        }
    }

    public final BackupEligibilityRules getEligibilityRulesForRestoreAtInstall(long j) {
        if (this.mAncestralBackupDestination == 1 && j == this.mAncestralToken) {
            return getEligibilityRulesForOperation(1);
        }
        return this.mScheduledBackupEligibility;
    }

    public long getAvailableRestoreToken(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getAvailableRestoreToken");
        long j = this.mAncestralToken;
        synchronized (this.mQueueLock) {
            if (this.mCurrentToken != 0 && this.mProcessedPackagesJournal.hasBeenProcessed(str)) {
                j = this.mCurrentToken;
            }
        }
        return j;
    }

    public int requestBackup(String[] strArr, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, int i) {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "requestBackup");
        if (strArr == null || strArr.length < 1) {
            Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "No packages named for backup request"));
            BackupObserverUtils.sendBackupFinished(iBackupObserver, -1000);
            BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, 49, null, 1, null);
            throw new IllegalArgumentException("No packages are provided for backup");
        } else if (!this.mEnabled || !this.mSetupComplete) {
            int i2 = this.mUserId;
            Slog.i("BackupManagerService", addUserIdToLogMessage(i2, "Backup requested but enabled=" + this.mEnabled + " setupComplete=" + this.mSetupComplete));
            BackupObserverUtils.sendBackupFinished(iBackupObserver, -2001);
            BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, this.mSetupComplete ? 13 : 14, null, 3, null);
            return -2001;
        } else {
            try {
                TransportManager transportManager = this.mTransportManager;
                String transportDirName = transportManager.getTransportDirName(transportManager.getCurrentTransportName());
                final TransportConnection currentTransportClientOrThrow = this.mTransportManager.getCurrentTransportClientOrThrow("BMS.requestBackup()");
                int backupDestinationFromTransport = getBackupDestinationFromTransport(currentTransportClientOrThrow);
                OnTaskFinishedListener onTaskFinishedListener = new OnTaskFinishedListener() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda8
                    @Override // com.android.server.backup.internal.OnTaskFinishedListener
                    public final void onFinished(String str) {
                        UserBackupManagerService.this.lambda$requestBackup$1(currentTransportClientOrThrow, str);
                    }
                };
                BackupEligibilityRules eligibilityRulesForOperation = getEligibilityRulesForOperation(backupDestinationFromTransport);
                Message obtainMessage = this.mBackupHandler.obtainMessage(15);
                obtainMessage.obj = getRequestBackupParams(strArr, iBackupObserver, iBackupManagerMonitor, i, eligibilityRulesForOperation, currentTransportClientOrThrow, transportDirName, onTaskFinishedListener);
                this.mBackupHandler.sendMessage(obtainMessage);
                return 0;
            } catch (RemoteException | TransportNotAvailableException | TransportNotRegisteredException unused) {
                BackupObserverUtils.sendBackupFinished(iBackupObserver, -1000);
                BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, 50, null, 1, null);
                return -1000;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestBackup$1(TransportConnection transportConnection, String str) {
        this.mTransportManager.disposeOfTransportClient(transportConnection, str);
    }

    @VisibleForTesting
    public BackupParams getRequestBackupParams(String[] strArr, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, int i, BackupEligibilityRules backupEligibilityRules, TransportConnection transportConnection, String str, OnTaskFinishedListener onTaskFinishedListener) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String str2 : strArr) {
            if ("@pm@".equals(str2)) {
                arrayList2.add(str2);
            } else {
                try {
                    PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(str2, 134217728, this.mUserId);
                    if (!backupEligibilityRules.appIsEligibleForBackup(packageInfoAsUser.applicationInfo)) {
                        BackupObserverUtils.sendBackupOnPackageResult(iBackupObserver, str2, -2001);
                    } else if (backupEligibilityRules.appGetsFullBackup(packageInfoAsUser)) {
                        arrayList.add(packageInfoAsUser.packageName);
                    } else {
                        arrayList2.add(packageInfoAsUser.packageName);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    BackupObserverUtils.sendBackupOnPackageResult(iBackupObserver, str2, -2002);
                }
            }
        }
        EventLog.writeEvent(2828, Integer.valueOf(strArr.length), Integer.valueOf(arrayList2.size()), Integer.valueOf(arrayList.size()));
        return new BackupParams(transportConnection, str, arrayList2, arrayList, iBackupObserver, iBackupManagerMonitor, onTaskFinishedListener, true, (i & 1) != 0, backupEligibilityRules);
    }

    public void cancelBackups() {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "cancelBackups");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (Integer num : this.mOperationStorage.operationTokensForOpType(2)) {
                this.mOperationStorage.cancelOperation(num.intValue(), true, new IntConsumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda7
                    @Override // java.util.function.IntConsumer
                    public final void accept(int i) {
                        UserBackupManagerService.lambda$cancelBackups$2(i);
                    }
                });
            }
            KeyValueBackupJob.schedule(this.mUserId, this.mContext, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS, this);
            FullBackupJob.schedule(this.mUserId, this.mContext, 7200000L, this);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void prepareOperationTimeout(int i, long j, BackupRestoreTask backupRestoreTask, int i2) {
        if (i2 != 0 && i2 != 1) {
            int i3 = this.mUserId;
            Slog.wtf("BackupManagerService", addUserIdToLogMessage(i3, "prepareOperationTimeout() doesn't support operation " + Integer.toHexString(i) + " of type " + i2));
            return;
        }
        this.mOperationStorage.registerOperation(i, 0, backupRestoreTask, i2);
        this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(getMessageIdForOperationType(i2), i, 0, backupRestoreTask), j);
    }

    public final int getMessageIdForOperationType(int i) {
        if (i != 0) {
            if (i != 1) {
                int i2 = this.mUserId;
                Slog.wtf("BackupManagerService", addUserIdToLogMessage(i2, "getMessageIdForOperationType called on invalid operation type: " + i));
                return -1;
            }
            return 18;
        }
        return 17;
    }

    public boolean waitUntilOperationComplete(int i) {
        return this.mOperationStorage.waitUntilOperationComplete(i, new IntConsumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda14
            @Override // java.util.function.IntConsumer
            public final void accept(int i2) {
                UserBackupManagerService.this.lambda$waitUntilOperationComplete$3(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$waitUntilOperationComplete$3(int i) {
        this.mBackupHandler.removeMessages(getMessageIdForOperationType(i));
    }

    public void handleCancel(int i, boolean z) {
        this.mOperationStorage.cancelOperation(i, z, new IntConsumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda13
            @Override // java.util.function.IntConsumer
            public final void accept(int i2) {
                UserBackupManagerService.this.lambda$handleCancel$4(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleCancel$4(int i) {
        if (i == 0 || i == 1) {
            this.mBackupHandler.removeMessages(getMessageIdForOperationType(i));
        }
    }

    public boolean isBackupOperationInProgress() {
        return this.mOperationStorage.isBackupOperationInProgress();
    }

    public void tearDownAgentAndKill(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return;
        }
        try {
            this.mActivityManager.unbindBackupAgent(applicationInfo);
            if (UserHandle.isCore(applicationInfo.uid) || applicationInfo.packageName.equals("com.android.backupconfirm")) {
                return;
            }
            this.mActivityManager.killApplicationProcess(applicationInfo.processName, applicationInfo.uid);
        } catch (RemoteException unused) {
            Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Lost app trying to shut down"));
        }
    }

    public void scheduleNextFullBackupJob(long j) {
        synchronized (this.mQueueLock) {
            if (this.mFullBackupQueue.size() > 0) {
                long currentTimeMillis = System.currentTimeMillis() - this.mFullBackupQueue.get(0).lastBackup;
                long fullBackupIntervalMilliseconds = this.mConstants.getFullBackupIntervalMilliseconds();
                FullBackupJob.schedule(this.mUserId, this.mContext, Math.max(j, currentTimeMillis < fullBackupIntervalMilliseconds ? fullBackupIntervalMilliseconds - currentTimeMillis : 0L), this);
            } else {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Full backup queue empty; not scheduling"));
            }
        }
    }

    @GuardedBy({"mQueueLock"})
    public final void dequeueFullBackupLocked(String str) {
        for (int size = this.mFullBackupQueue.size() - 1; size >= 0; size--) {
            if (str.equals(this.mFullBackupQueue.get(size).packageName)) {
                this.mFullBackupQueue.remove(size);
            }
        }
    }

    public void enqueueFullBackup(String str, long j) {
        int i;
        FullBackupEntry fullBackupEntry = new FullBackupEntry(str, j);
        synchronized (this.mQueueLock) {
            dequeueFullBackupLocked(str);
            if (j > 0) {
                i = this.mFullBackupQueue.size() - 1;
                while (true) {
                    if (i < 0) {
                        break;
                    } else if (this.mFullBackupQueue.get(i).lastBackup <= j) {
                        this.mFullBackupQueue.add(i + 1, fullBackupEntry);
                        break;
                    } else {
                        i--;
                    }
                }
            } else {
                i = -1;
            }
            if (i < 0) {
                this.mFullBackupQueue.add(0, fullBackupEntry);
            }
        }
        writeFullBackupScheduleAsync();
    }

    public final boolean fullBackupAllowable(String str) {
        if (!this.mTransportManager.isTransportRegistered(str)) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Transport not registered; full data backup not performed"));
            return false;
        }
        try {
            if (new File(new File(this.mBaseStateDir, this.mTransportManager.getTransportDirName(str)), "@pm@").length() <= 0) {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Full backup requested but dataset not yet initialized"));
                return false;
            }
            return true;
        } catch (Exception e) {
            int i = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i, "Unable to get transport name: " + e.getMessage()));
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:118:0x015d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0151  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0153  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01a8 A[Catch: all -> 0x01f0, TryCatch #0 {, blocks: (B:17:0x0051, B:19:0x0055, B:20:0x0062, B:23:0x0067, B:25:0x006f, B:70:0x0161, B:71:0x016e, B:73:0x018b, B:82:0x01a8, B:83:0x01cd, B:86:0x01d0, B:87:0x01e5, B:79:0x0199, B:26:0x007e, B:30:0x008e, B:37:0x00a7, B:39:0x00b9, B:42:0x00cc, B:45:0x00d4, B:50:0x00e3, B:52:0x00f3, B:54:0x0107, B:60:0x0148), top: B:100:0x0051 }] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x01d0 A[Catch: all -> 0x01f0, TryCatch #0 {, blocks: (B:17:0x0051, B:19:0x0055, B:20:0x0062, B:23:0x0067, B:25:0x006f, B:70:0x0161, B:71:0x016e, B:73:0x018b, B:82:0x01a8, B:83:0x01cd, B:86:0x01d0, B:87:0x01e5, B:79:0x0199, B:26:0x007e, B:30:0x008e, B:37:0x00a7, B:39:0x00b9, B:42:0x00cc, B:45:0x00d4, B:50:0x00e3, B:52:0x00f3, B:54:0x0107, B:60:0x0148), top: B:100:0x0051 }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01e7 A[LOOP:0: B:23:0x0067->B:89:0x01e7, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean beginFullBackup(FullBackupJob fullBackupJob) {
        long fullBackupIntervalMilliseconds;
        long keyValueBackupIntervalMilliseconds;
        long j;
        boolean z;
        long j2;
        long j3;
        int i;
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this.mConstants) {
            fullBackupIntervalMilliseconds = this.mConstants.getFullBackupIntervalMilliseconds();
            keyValueBackupIntervalMilliseconds = this.mConstants.getKeyValueBackupIntervalMilliseconds();
        }
        int i2 = 0;
        if (this.mEnabled && this.mSetupComplete) {
            if (this.mPowerManager.getPowerSaveState(4).batterySaverEnabled) {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Deferring scheduled full backups in battery saver mode"));
                FullBackupJob.schedule(this.mUserId, this.mContext, keyValueBackupIntervalMilliseconds, this);
                return false;
            }
            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Beginning scheduled full backup operation"));
            synchronized (this.mQueueLock) {
                if (this.mRunningFullBackupTask != null) {
                    Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Backup triggered but one already/still running!"));
                    return false;
                }
                FullBackupEntry fullBackupEntry = null;
                long j4 = fullBackupIntervalMilliseconds;
                int i3 = 1;
                while (this.mFullBackupQueue.size() != 0) {
                    if (!fullBackupAllowable(this.mTransportManager.getCurrentTransportName())) {
                        j4 = keyValueBackupIntervalMilliseconds;
                        i3 = i2;
                    }
                    if (i3 != 0) {
                        fullBackupEntry = this.mFullBackupQueue.get(i2);
                        long j5 = currentTimeMillis - fullBackupEntry.lastBackup;
                        i3 = j5 >= fullBackupIntervalMilliseconds ? 1 : i2;
                        if (i3 == 0) {
                            j4 = fullBackupIntervalMilliseconds - j5;
                            i2 = i3;
                            break;
                        }
                        try {
                            PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(fullBackupEntry.packageName, i2, this.mUserId);
                            if (!this.mScheduledBackupEligibility.appGetsFullBackup(packageInfoAsUser)) {
                                try {
                                    this.mFullBackupQueue.remove(i2);
                                    j2 = currentTimeMillis;
                                    j3 = keyValueBackupIntervalMilliseconds;
                                    i = 1;
                                } catch (PackageManager.NameNotFoundException unused) {
                                    j2 = currentTimeMillis;
                                    j3 = keyValueBackupIntervalMilliseconds;
                                    i = i2;
                                    i3 = this.mFullBackupQueue.size() > 1 ? 1 : 0;
                                    if (i != 0) {
                                    }
                                }
                            } else {
                                ApplicationInfo applicationInfo = packageInfoAsUser.applicationInfo;
                                i = ((applicationInfo.privateFlags & IInstalld.FLAG_FORCE) == 0 && this.mActivityManagerInternal.isAppForeground(applicationInfo.uid)) ? 1 : i2;
                                if (i != 0) {
                                    try {
                                        j2 = currentTimeMillis;
                                    } catch (PackageManager.NameNotFoundException unused2) {
                                        j2 = currentTimeMillis;
                                    }
                                    try {
                                        long currentTimeMillis2 = System.currentTimeMillis() + ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS + this.mTokenGenerator.nextInt(7200000);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        j3 = keyValueBackupIntervalMilliseconds;
                                        try {
                                            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Full backup time but " + fullBackupEntry.packageName + " is busy; deferring to " + simpleDateFormat.format(new Date(currentTimeMillis2))));
                                            enqueueFullBackup(fullBackupEntry.packageName, currentTimeMillis2 - fullBackupIntervalMilliseconds);
                                        } catch (PackageManager.NameNotFoundException unused3) {
                                            i3 = this.mFullBackupQueue.size() > 1 ? 1 : 0;
                                            if (i != 0) {
                                            }
                                        }
                                    } catch (PackageManager.NameNotFoundException unused4) {
                                        j3 = keyValueBackupIntervalMilliseconds;
                                        i3 = this.mFullBackupQueue.size() > 1 ? 1 : 0;
                                        if (i != 0) {
                                        }
                                    }
                                } else {
                                    j2 = currentTimeMillis;
                                    j3 = keyValueBackupIntervalMilliseconds;
                                }
                            }
                        } catch (PackageManager.NameNotFoundException unused5) {
                            j2 = currentTimeMillis;
                            j3 = keyValueBackupIntervalMilliseconds;
                            i = 0;
                        }
                    } else {
                        j2 = currentTimeMillis;
                        j3 = keyValueBackupIntervalMilliseconds;
                        i = 0;
                    }
                    if (i != 0) {
                        i2 = i3;
                        break;
                    }
                    currentTimeMillis = j2;
                    keyValueBackupIntervalMilliseconds = j3;
                    i2 = 0;
                }
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Backup queue empty; doing nothing"));
                long j6 = j4;
                if (i2 != 0) {
                    try {
                        j = j6;
                        z = true;
                        try {
                            this.mRunningFullBackupTask = PerformFullTransportBackupTask.newWithCurrentTransport(this, this.mOperationStorage, null, new String[]{fullBackupEntry.packageName}, true, fullBackupJob, new CountDownLatch(1), null, null, false, "BMS.beginFullBackup()", getEligibilityRulesForOperation(0));
                        } catch (IllegalStateException e) {
                            e = e;
                            Slog.w("BackupManagerService", "Failed to start backup", e);
                            i2 = 0;
                            if (i2 != 0) {
                            }
                        }
                    } catch (IllegalStateException e2) {
                        e = e2;
                        z = true;
                        j = j6;
                    }
                } else {
                    j = j6;
                    z = true;
                }
                if (i2 != 0) {
                    int i4 = this.mUserId;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Nothing pending full backup or failed to start the operation; rescheduling +");
                    long j7 = j;
                    sb.append(j7);
                    Slog.i("BackupManagerService", addUserIdToLogMessage(i4, sb.toString()));
                    FullBackupJob.schedule(this.mUserId, this.mContext, j7, this);
                    return false;
                }
                this.mFullBackupQueue.remove(0);
                this.mWakelock.acquire();
                new Thread(this.mRunningFullBackupTask).start();
                return z;
            }
        }
        return false;
    }

    public void endFullBackup() {
        new Thread(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                PerformFullTransportBackupTask performFullTransportBackupTask;
                synchronized (UserBackupManagerService.this.mQueueLock) {
                    performFullTransportBackupTask = UserBackupManagerService.this.mRunningFullBackupTask != null ? UserBackupManagerService.this.mRunningFullBackupTask : null;
                }
                if (performFullTransportBackupTask != null) {
                    Slog.i("BackupManagerService", UserBackupManagerService.addUserIdToLogMessage(UserBackupManagerService.this.mUserId, "Telling running backup to stop"));
                    performFullTransportBackupTask.handleCancel(true);
                }
            }
        }, "end-full-backup").start();
    }

    public void restoreWidgetData(String str, byte[] bArr) {
        AppWidgetBackupBridge.restoreWidgetState(str, bArr, this.mUserId);
    }

    public void dataChangedImpl(String str) {
        dataChangedImpl(str, dataChangedTargets(str));
    }

    public final void dataChangedImpl(String str, HashSet<String> hashSet) {
        if (hashSet == null) {
            int i = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i, "dataChanged but no participant pkg='" + str + "' uid=" + Binder.getCallingUid()));
            return;
        }
        synchronized (this.mQueueLock) {
            if (hashSet.contains(str)) {
                if (this.mPendingBackups.put(str, new BackupRequest(str)) == null) {
                    writeToJournalLocked(str);
                }
            }
        }
        KeyValueBackupJob.schedule(this.mUserId, this.mContext, this);
    }

    public final HashSet<String> dataChangedTargets(String str) {
        HashSet<String> union;
        HashSet<String> hashSet;
        if (this.mContext.checkPermission("android.permission.BACKUP", Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
            synchronized (this.mBackupParticipants) {
                hashSet = this.mBackupParticipants.get(Binder.getCallingUid());
            }
            return hashSet;
        } else if ("@pm@".equals(str)) {
            return Sets.newHashSet(new String[]{"@pm@"});
        } else {
            synchronized (this.mBackupParticipants) {
                union = SparseArrayUtils.union(this.mBackupParticipants);
            }
            return union;
        }
    }

    public final void writeToJournalLocked(String str) {
        try {
            if (this.mJournal == null) {
                this.mJournal = DataChangedJournal.newJournal(this.mJournalDir);
            }
            this.mJournal.addPackage(str);
        } catch (IOException e) {
            int i = this.mUserId;
            Slog.e("BackupManagerService", addUserIdToLogMessage(i, "Can't write " + str + " to backup journal"), e);
            this.mJournal = null;
        }
    }

    public void dataChanged(final String str) {
        final HashSet<String> dataChangedTargets = dataChangedTargets(str);
        if (dataChangedTargets == null) {
            int i = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i, "dataChanged but no participant pkg='" + str + "' uid=" + Binder.getCallingUid()));
            return;
        }
        this.mBackupHandler.post(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService.4
            @Override // java.lang.Runnable
            public void run() {
                UserBackupManagerService.this.dataChangedImpl(str, dataChangedTargets);
            }
        });
    }

    public void initializeTransports(String[] strArr, IBackupObserver iBackupObserver) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "initializeTransport");
        int i = this.mUserId;
        Slog.v("BackupManagerService", addUserIdToLogMessage(i, "initializeTransport(): " + Arrays.asList(strArr)));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mWakelock.acquire();
            this.mBackupHandler.post(new PerformInitializeTask(this, strArr, iBackupObserver, new OnTaskFinishedListener() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda4
                @Override // com.android.server.backup.internal.OnTaskFinishedListener
                public final void onFinished(String str) {
                    UserBackupManagerService.this.lambda$initializeTransports$5(str);
                }
            }));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializeTransports$5(String str) {
        this.mWakelock.release();
    }

    public void setAncestralSerialNumber(long j) {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "setAncestralSerialNumber");
        int i = this.mUserId;
        Slog.v("BackupManagerService", addUserIdToLogMessage(i, "Setting ancestral work profile id to " + j));
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(getAncestralSerialNumberFile(), "rwd");
            randomAccessFile.writeLong(j);
            randomAccessFile.close();
        } catch (IOException e) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to write to work profile serial mapping file:"), e);
        }
    }

    public long getAncestralSerialNumber() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(getAncestralSerialNumberFile(), "r");
            try {
                long readLong = randomAccessFile.readLong();
                randomAccessFile.close();
                return readLong;
            } catch (Throwable th) {
                try {
                    randomAccessFile.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (FileNotFoundException unused) {
            return -1L;
        } catch (IOException e) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to read work profile serial number file:"), e);
            return -1L;
        }
    }

    public final File getAncestralSerialNumberFile() {
        if (this.mAncestralSerialNumberFile == null) {
            this.mAncestralSerialNumberFile = new File(UserBackupManagerFiles.getBaseStateDir(getUserId()), "serial_id");
        }
        return this.mAncestralSerialNumberFile;
    }

    @VisibleForTesting
    public void setAncestralSerialNumberFile(File file) {
        this.mAncestralSerialNumberFile = file;
    }

    public void clearBackupData(String str, String str2) {
        HashSet<String> packagesCopy;
        int i = this.mUserId;
        Slog.v("BackupManagerService", addUserIdToLogMessage(i, "clearBackupData() of " + str2 + " on " + str));
        try {
            PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(str2, 134217728, this.mUserId);
            if (this.mContext.checkPermission("android.permission.BACKUP", Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
                packagesCopy = this.mBackupParticipants.get(Binder.getCallingUid());
            } else {
                packagesCopy = this.mProcessedPackagesJournal.getPackagesCopy();
            }
            if (packagesCopy.contains(str2)) {
                this.mBackupHandler.removeMessages(12);
                synchronized (this.mQueueLock) {
                    final TransportConnection transportClient = this.mTransportManager.getTransportClient(str, "BMS.clearBackupData()");
                    if (transportClient == null) {
                        this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(12, new ClearRetryParams(str, str2)), ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
                        return;
                    }
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    OnTaskFinishedListener onTaskFinishedListener = new OnTaskFinishedListener() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda9
                        @Override // com.android.server.backup.internal.OnTaskFinishedListener
                        public final void onFinished(String str3) {
                            UserBackupManagerService.this.lambda$clearBackupData$6(transportClient, str3);
                        }
                    };
                    this.mWakelock.acquire();
                    this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(4, new ClearParams(transportClient, packageInfoAsUser, onTaskFinishedListener)));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
            int i2 = this.mUserId;
            Slog.d("BackupManagerService", addUserIdToLogMessage(i2, "No such package '" + str2 + "' - not clearing backup data"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearBackupData$6(TransportConnection transportConnection, String str) {
        this.mTransportManager.disposeOfTransportClient(transportConnection, str);
    }

    public void backupNow() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "backupNow");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!this.mPowerManager.getPowerSaveState(5).batterySaverEnabled) {
                Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Scheduling immediate backup pass"));
                synchronized (getQueueLock()) {
                    if (getPendingInits().size() > 0) {
                        try {
                            getAlarmManager().cancel(this.mRunInitIntent);
                            this.mRunInitIntent.send();
                        } catch (PendingIntent.CanceledException unused) {
                            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Run init intent cancelled"));
                        }
                        return;
                    }
                    if (isEnabled() && isSetupComplete()) {
                        this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(1));
                        KeyValueBackupJob.cancel(this.mUserId, this.mContext);
                    }
                    int i = this.mUserId;
                    Slog.w("BackupManagerService", addUserIdToLogMessage(i, "Backup pass but enabled=" + isEnabled() + " setupComplete=" + isSetupComplete()));
                    return;
                }
            }
            Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Not running backup while in battery save mode"));
            KeyValueBackupJob.schedule(this.mUserId, this.mContext, this);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void adbBackup(ParcelFileDescriptor parcelFileDescriptor, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, boolean z8, String[] strArr) {
        IOException iOException;
        String str;
        int i;
        StringBuilder sb;
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "adbBackup");
        if (UserHandle.getCallingUserId() != 0) {
            throw new IllegalStateException("Backup supported only for the device owner");
        }
        if (!z5 && !z3 && (strArr == null || strArr.length == 0)) {
            throw new IllegalArgumentException("Backup requested but neither shared nor any apps named");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mSetupComplete) {
                int i2 = this.mUserId;
                Slog.v("BackupManagerService", addUserIdToLogMessage(i2, "Requesting backup: apks=" + z + " obb=" + z2 + " shared=" + z3 + " all=" + z5 + " system=" + z6 + " includekeyvalue=" + z8 + " pkgs=" + Arrays.toString(strArr)));
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Beginning adb backup..."));
                AdbBackupParams adbBackupParams = new AdbBackupParams(parcelFileDescriptor, z, z2, z3, z4, z5, z6, z7, z8, strArr, getEligibilityRulesForOperation(2));
                int generateRandomIntegerToken = generateRandomIntegerToken();
                synchronized (this.mAdbBackupRestoreConfirmations) {
                    this.mAdbBackupRestoreConfirmations.put(generateRandomIntegerToken, adbBackupParams);
                }
                int i3 = this.mUserId;
                Slog.d("BackupManagerService", addUserIdToLogMessage(i3, "Starting backup confirmation UI, token=" + generateRandomIntegerToken));
                if (startConfirmationUi(generateRandomIntegerToken, "fullback")) {
                    this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                    startConfirmationTimeout(generateRandomIntegerToken, adbBackupParams);
                    Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Waiting for backup completion..."));
                    waitForCompletion(adbBackupParams);
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        int i4 = this.mUserId;
                        Slog.e("BackupManagerService", addUserIdToLogMessage(i4, "IO error closing output for adb backup: " + e.getMessage()));
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Adb backup processing complete."));
                    return;
                }
                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to launch backup confirmation UI"));
                this.mAdbBackupRestoreConfirmations.delete(generateRandomIntegerToken);
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e2) {
                    iOException = e2;
                    str = "BackupManagerService";
                    i = this.mUserId;
                    sb = new StringBuilder();
                    sb.append("IO error closing output for adb backup: ");
                    sb.append(iOException.getMessage());
                    Slog.e(str, addUserIdToLogMessage(i, sb.toString()));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Adb backup processing complete."));
                }
            } else {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Backup not supported before setup"));
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e3) {
                    iOException = e3;
                    str = "BackupManagerService";
                    i = this.mUserId;
                    sb = new StringBuilder();
                    sb.append("IO error closing output for adb backup: ");
                    sb.append(iOException.getMessage());
                    Slog.e(str, addUserIdToLogMessage(i, sb.toString()));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Adb backup processing complete."));
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Adb backup processing complete."));
        } catch (Throwable th) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e4) {
                int i5 = this.mUserId;
                Slog.e("BackupManagerService", addUserIdToLogMessage(i5, "IO error closing output for adb backup: " + e4.getMessage()));
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Adb backup processing complete."));
            throw th;
        }
    }

    public void fullTransportBackup(String[] strArr) {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "fullTransportBackup");
        if (UserHandle.getCallingUserId() != 0) {
            throw new IllegalStateException("Restore supported only for the device owner");
        }
        if (!fullBackupAllowable(this.mTransportManager.getCurrentTransportName())) {
            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Full backup not currently possible -- key/value backup not yet run?"));
        } else {
            Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "fullTransportBackup()"));
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                PerformFullTransportBackupTask newWithCurrentTransport = PerformFullTransportBackupTask.newWithCurrentTransport(this, this.mOperationStorage, null, strArr, false, null, countDownLatch, null, null, false, "BMS.fullTransportBackup()", getEligibilityRulesForOperation(0));
                this.mWakelock.acquire();
                new Thread(newWithCurrentTransport, "full-transport-master").start();
                while (true) {
                    try {
                        countDownLatch.await();
                        break;
                    } catch (InterruptedException unused) {
                    }
                }
                long currentTimeMillis = System.currentTimeMillis();
                for (String str : strArr) {
                    enqueueFullBackup(str, currentTimeMillis);
                }
            } catch (IllegalStateException e) {
                Slog.w("BackupManagerService", "Failed to start backup: ", e);
                return;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Done with full transport backup."));
    }

    public void adbRestore(ParcelFileDescriptor parcelFileDescriptor) {
        String str;
        int i;
        StringBuilder sb;
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "adbRestore");
        if (UserHandle.getCallingUserId() != 0) {
            throw new IllegalStateException("Restore supported only for the device owner");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mSetupComplete) {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Beginning restore..."));
                AdbRestoreParams adbRestoreParams = new AdbRestoreParams(parcelFileDescriptor);
                int generateRandomIntegerToken = generateRandomIntegerToken();
                synchronized (this.mAdbBackupRestoreConfirmations) {
                    this.mAdbBackupRestoreConfirmations.put(generateRandomIntegerToken, adbRestoreParams);
                }
                int i2 = this.mUserId;
                Slog.d("BackupManagerService", addUserIdToLogMessage(i2, "Starting restore confirmation UI, token=" + generateRandomIntegerToken));
                if (startConfirmationUi(generateRandomIntegerToken, "fullrest")) {
                    this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                    startConfirmationTimeout(generateRandomIntegerToken, adbRestoreParams);
                    Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Waiting for restore completion..."));
                    waitForCompletion(adbRestoreParams);
                    try {
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        int i3 = this.mUserId;
                        Slog.w("BackupManagerService", addUserIdToLogMessage(i3, "Error trying to close fd after adb restore: " + e));
                    }
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "adb restore processing complete."));
                    return;
                }
                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unable to launch restore confirmation"));
                this.mAdbBackupRestoreConfirmations.delete(generateRandomIntegerToken);
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e2) {
                    e = e2;
                    str = "BackupManagerService";
                    i = this.mUserId;
                    sb = new StringBuilder();
                    sb.append("Error trying to close fd after adb restore: ");
                    sb.append(e);
                    Slog.w(str, addUserIdToLogMessage(i, sb.toString()));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "adb restore processing complete."));
                }
            } else {
                Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Full restore not permitted before setup"));
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e3) {
                    e = e3;
                    str = "BackupManagerService";
                    i = this.mUserId;
                    sb = new StringBuilder();
                    sb.append("Error trying to close fd after adb restore: ");
                    sb.append(e);
                    Slog.w(str, addUserIdToLogMessage(i, sb.toString()));
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "adb restore processing complete."));
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "adb restore processing complete."));
        } catch (Throwable th) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e4) {
                int i4 = this.mUserId;
                Slog.w("BackupManagerService", addUserIdToLogMessage(i4, "Error trying to close fd after adb restore: " + e4));
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "adb restore processing complete."));
            throw th;
        }
    }

    public void excludeKeysFromRestore(String str, List<String> list) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "excludeKeysFromRestore");
        this.mBackupPreferences.addExcludedKeys(str, list);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v3, types: [com.android.server.backup.TransportManager] */
    public void reportDelayedRestoreResult(String str, List<BackupRestoreEventLogger.DataTypeResult> list) {
        String currentTransportName = this.mTransportManager.getCurrentTransportName();
        if (currentTransportName == null) {
            Slog.w("BackupManagerService", "Failed to send delayed restore logs as no transport selected");
            return;
        }
        TransportConnection transportConnection = null;
        try {
            try {
                PackageInfo packageInfoAsUser = getPackageManager().getPackageInfoAsUser(str, PackageManager.PackageInfoFlags.of(0L), getUserId());
                transportConnection = this.mTransportManager.getTransportClientOrThrow(currentTransportName, "BMS.reportDelayedRestoreResult");
                BackupManagerMonitorUtils.sendAgentLoggingResults(transportConnection.connectOrThrow("BMS.reportDelayedRestoreResult").getBackupManagerMonitor(), packageInfoAsUser, list);
            } catch (PackageManager.NameNotFoundException | RemoteException | TransportNotAvailableException | TransportNotRegisteredException e) {
                Slog.w("BackupManagerService", "Failed to send delayed restore logs: " + e);
                if (transportConnection == null) {
                    return;
                }
            }
            this = this.mTransportManager;
            this.disposeOfTransportClient(transportConnection, "BMS.reportDelayedRestoreResult");
        } catch (Throwable th) {
            if (transportConnection != null) {
                this.mTransportManager.disposeOfTransportClient(transportConnection, "BMS.reportDelayedRestoreResult");
            }
            throw th;
        }
    }

    public final boolean startConfirmationUi(int i, String str) {
        try {
            Intent intent = new Intent(str);
            intent.setClassName("com.android.backupconfirm", "com.android.backupconfirm.BackupRestoreConfirmation");
            intent.putExtra("conftoken", i);
            intent.addFlags(536870912);
            this.mContext.startActivityAsUser(intent, UserHandle.SYSTEM);
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }

    public final void startConfirmationTimeout(int i, AdbParams adbParams) {
        this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(9, i, 0, adbParams), 60000L);
    }

    public final void waitForCompletion(AdbParams adbParams) {
        synchronized (adbParams.latch) {
            while (!adbParams.latch.get()) {
                try {
                    adbParams.latch.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    public void signalAdbBackupRestoreCompletion(AdbParams adbParams) {
        synchronized (adbParams.latch) {
            adbParams.latch.set(true);
            adbParams.latch.notifyAll();
        }
    }

    public void acknowledgeAdbBackupOrRestore(int i, boolean z, String str, String str2, IFullBackupRestoreObserver iFullBackupRestoreObserver) {
        int i2 = this.mUserId;
        Slog.d("BackupManagerService", addUserIdToLogMessage(i2, "acknowledgeAdbBackupOrRestore : token=" + i + " allow=" + z));
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "acknowledgeAdbBackupOrRestore");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mAdbBackupRestoreConfirmations) {
                AdbParams adbParams = this.mAdbBackupRestoreConfirmations.get(i);
                if (adbParams != null) {
                    this.mBackupHandler.removeMessages(9, adbParams);
                    this.mAdbBackupRestoreConfirmations.delete(i);
                    if (z) {
                        int i3 = adbParams instanceof AdbBackupParams ? 2 : 10;
                        adbParams.observer = iFullBackupRestoreObserver;
                        adbParams.curPassword = str;
                        adbParams.encryptPassword = str2;
                        this.mWakelock.acquire();
                        this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(i3, adbParams));
                    } else {
                        Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "User rejected full backup/restore operation"));
                        signalAdbBackupRestoreCompletion(adbParams);
                    }
                } else {
                    Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Attempted to ack full backup/restore with invalid token"));
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setBackupEnabled(boolean z) {
        setBackupEnabled(z, true);
    }

    public final void setBackupEnabled(boolean z, boolean z2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setBackupEnabled");
        int i = this.mUserId;
        Slog.i("BackupManagerService", addUserIdToLogMessage(i, "Backup enabled => " + z));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            boolean z3 = this.mEnabled;
            synchronized (this) {
                if (z2) {
                    writeEnabledState(z);
                }
                this.mEnabled = z;
            }
            updateStateOnBackupEnabled(z3, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public synchronized void setFrameworkSchedulingEnabled(boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setFrameworkSchedulingEnabled");
        if (isFrameworkSchedulingEnabled() == z) {
            return;
        }
        int i = this.mUserId;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "Enabling" : "Disabling");
        sb.append(" backup scheduling");
        Slog.i("BackupManagerService", addUserIdToLogMessage(i, sb.toString()));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "backup_scheduling_enabled", z ? 1 : 0, this.mUserId);
        if (!z) {
            KeyValueBackupJob.cancel(this.mUserId, this.mContext);
            FullBackupJob.cancel(this.mUserId, this.mContext);
        } else {
            KeyValueBackupJob.schedule(this.mUserId, this.mContext, this);
            scheduleNextFullBackupJob(0L);
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public synchronized boolean isFrameworkSchedulingEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "backup_scheduling_enabled", 1, this.mUserId) == 1;
    }

    @VisibleForTesting
    public void updateStateOnBackupEnabled(boolean z, boolean z2) {
        synchronized (this.mQueueLock) {
            if (z2 && !z) {
                if (this.mSetupComplete) {
                    KeyValueBackupJob.schedule(this.mUserId, this.mContext, this);
                    scheduleNextFullBackupJob(0L);
                }
            }
            if (!z2) {
                KeyValueBackupJob.cancel(this.mUserId, this.mContext);
                if (z && this.mSetupComplete) {
                    final ArrayList arrayList = new ArrayList();
                    final ArrayList arrayList2 = new ArrayList();
                    this.mTransportManager.forEachRegisteredTransport(new Consumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda11
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            UserBackupManagerService.this.lambda$updateStateOnBackupEnabled$7(arrayList, arrayList2, (String) obj);
                        }
                    });
                    for (int i = 0; i < arrayList.size(); i++) {
                        recordInitPending(true, (String) arrayList.get(i), (String) arrayList2.get(i));
                    }
                    this.mAlarmManager.set(0, System.currentTimeMillis(), this.mRunInitIntent);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStateOnBackupEnabled$7(List list, List list2, String str) {
        try {
            String transportDirName = this.mTransportManager.getTransportDirName(str);
            list.add(str);
            list2.add(transportDirName);
        } catch (TransportNotRegisteredException e) {
            Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Unexpected unregistered transport"), e);
        }
    }

    @VisibleForTesting
    public void writeEnabledState(boolean z) {
        UserBackupManagerFilePersistedSettings.writeBackupEnableState(this.mUserId, z);
    }

    @VisibleForTesting
    public boolean readEnabledState() {
        return UserBackupManagerFilePersistedSettings.readBackupEnableState(this.mUserId);
    }

    public void setAutoRestore(boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setAutoRestore");
        int i = this.mUserId;
        Slog.i("BackupManagerService", addUserIdToLogMessage(i, "Auto restore => " + z));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "backup_auto_restore", z ? 1 : 0, this.mUserId);
                this.mAutoRestore = z;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isBackupEnabled() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "isBackupEnabled");
        return this.mEnabled;
    }

    public String getCurrentTransport() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getCurrentTransport");
        return this.mTransportManager.getCurrentTransportName();
    }

    public ComponentName getCurrentTransportComponent() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getCurrentTransportComponent");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ComponentName currentTransportComponent = this.mTransportManager.getCurrentTransportComponent();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return currentTransportComponent;
        } catch (TransportNotRegisteredException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return null;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public String[] listAllTransports() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "listAllTransports");
        return this.mTransportManager.getRegisteredTransportNames();
    }

    public ComponentName[] listAllTransportComponents() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "listAllTransportComponents");
        return this.mTransportManager.getRegisteredTransportComponents();
    }

    public void updateTransportAttributes(ComponentName componentName, String str, Intent intent, String str2, Intent intent2, CharSequence charSequence) {
        updateTransportAttributes(Binder.getCallingUid(), componentName, str, intent, str2, intent2, charSequence);
    }

    @VisibleForTesting
    public void updateTransportAttributes(int i, ComponentName componentName, String str, Intent intent, String str2, Intent intent2, CharSequence charSequence) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "updateTransportAttributes");
        Objects.requireNonNull(componentName, "transportComponent can't be null");
        Objects.requireNonNull(str, "name can't be null");
        Objects.requireNonNull(str2, "currentDestinationString can't be null");
        Preconditions.checkArgument((intent2 == null) == (charSequence == null), "dataManagementLabel should be null iff dataManagementIntent is null");
        try {
            if (i != this.mContext.getPackageManager().getPackageUidAsUser(componentName.getPackageName(), 0, this.mUserId)) {
                throw new SecurityException("Only the transport can change its description");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mTransportManager.updateTransportAttributes(componentName, str, intent, str2, intent2, charSequence);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException("Transport package not found", e);
        }
    }

    @Deprecated
    public String selectBackupTransport(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "selectBackupTransport");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!this.mTransportManager.isTransportRegistered(str)) {
                int i = this.mUserId;
                Slog.v("BackupManagerService", addUserIdToLogMessage(i, "Could not select transport " + str + ", as the transport is not registered."));
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return null;
            }
            String selectTransport = this.mTransportManager.selectTransport(str);
            updateStateForTransport(str);
            int i2 = this.mUserId;
            Slog.v("BackupManagerService", addUserIdToLogMessage(i2, "selectBackupTransport(transport = " + str + "): previous transport = " + selectTransport));
            return selectTransport;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void selectBackupTransportAsync(final ComponentName componentName, final ISelectBackupTransportCallback iSelectBackupTransportCallback) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "selectBackupTransportAsync");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            String flattenToShortString = componentName.flattenToShortString();
            int i = this.mUserId;
            Slog.v("BackupManagerService", addUserIdToLogMessage(i, "selectBackupTransportAsync(transport = " + flattenToShortString + ")"));
            this.mBackupHandler.post(new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    UserBackupManagerService.this.lambda$selectBackupTransportAsync$8(componentName, iSelectBackupTransportCallback);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$selectBackupTransportAsync$8(ComponentName componentName, ISelectBackupTransportCallback iSelectBackupTransportCallback) {
        int registerAndSelectTransport = this.mTransportManager.registerAndSelectTransport(componentName);
        String str = null;
        if (registerAndSelectTransport == 0) {
            try {
                str = this.mTransportManager.getTransportName(componentName);
                updateStateForTransport(str);
            } catch (TransportNotRegisteredException unused) {
                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Transport got unregistered"));
                registerAndSelectTransport = -1;
            }
        }
        try {
            if (str != null) {
                iSelectBackupTransportCallback.onSuccess(str);
            } else {
                iSelectBackupTransportCallback.onFailure(registerAndSelectTransport);
            }
        } catch (RemoteException unused2) {
            Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "ISelectBackupTransportCallback listener not available"));
        }
    }

    public List<PackageInfo> filterUserFacingPackages(List<PackageInfo> list) {
        if (shouldSkipUserFacingData()) {
            ArrayList arrayList = new ArrayList(list.size());
            for (PackageInfo packageInfo : list) {
                if (shouldSkipPackage(packageInfo.packageName)) {
                    Slog.i("BackupManagerService", "Will skip backup/restore for " + packageInfo.packageName);
                } else {
                    arrayList.add(packageInfo);
                }
            }
            return arrayList;
        }
        return list;
    }

    @VisibleForTesting
    public boolean shouldSkipUserFacingData() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "backup_skip_user_facing_packages", 0) != 0;
    }

    @VisibleForTesting
    public boolean shouldSkipPackage(String str) {
        return "com.android.wallpaperbackup".equals(str);
    }

    public final void updateStateForTransport(String str) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "backup_transport", str, this.mUserId);
        TransportConnection transportClient = this.mTransportManager.getTransportClient(str, "BMS.updateStateForTransport()");
        if (transportClient != null) {
            try {
                this.mCurrentToken = transportClient.connectOrThrow("BMS.updateStateForTransport()").getCurrentRestoreSet();
            } catch (Exception unused) {
                this.mCurrentToken = 0L;
                int i = this.mUserId;
                Slog.w("BackupManagerService", addUserIdToLogMessage(i, "Transport " + str + " not available: current token = 0"));
            }
            this.mTransportManager.disposeOfTransportClient(transportClient, "BMS.updateStateForTransport()");
            return;
        }
        int i2 = this.mUserId;
        Slog.w("BackupManagerService", addUserIdToLogMessage(i2, "Transport " + str + " not registered: current token = 0"));
        this.mCurrentToken = 0L;
    }

    public Intent getConfigurationIntent(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getConfigurationIntent");
        try {
            return this.mTransportManager.getTransportConfigurationIntent(str);
        } catch (TransportNotRegisteredException e) {
            int i = this.mUserId;
            Slog.e("BackupManagerService", addUserIdToLogMessage(i, "Unable to get configuration intent from transport: " + e.getMessage()));
            return null;
        }
    }

    public String getDestinationString(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDestinationString");
        try {
            return this.mTransportManager.getTransportCurrentDestinationString(str);
        } catch (TransportNotRegisteredException e) {
            int i = this.mUserId;
            Slog.e("BackupManagerService", addUserIdToLogMessage(i, "Unable to get destination string from transport: " + e.getMessage()));
            return null;
        }
    }

    public Intent getDataManagementIntent(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDataManagementIntent");
        try {
            return this.mTransportManager.getTransportDataManagementIntent(str);
        } catch (TransportNotRegisteredException e) {
            int i = this.mUserId;
            Slog.e("BackupManagerService", addUserIdToLogMessage(i, "Unable to get management intent from transport: " + e.getMessage()));
            return null;
        }
    }

    public CharSequence getDataManagementLabel(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDataManagementLabel");
        try {
            return this.mTransportManager.getTransportDataManagementLabel(str);
        } catch (TransportNotRegisteredException e) {
            int i = this.mUserId;
            Slog.e("BackupManagerService", addUserIdToLogMessage(i, "Unable to get management label from transport: " + e.getMessage()));
            return null;
        }
    }

    public void agentConnected(String str, IBinder iBinder) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                int i = this.mUserId;
                Slog.d("BackupManagerService", addUserIdToLogMessage(i, "agentConnected pkg=" + str + " agent=" + iBinder));
                this.mConnectedAgent = IBackupAgent.Stub.asInterface(iBinder);
                this.mConnecting = false;
            } else {
                int i2 = this.mUserId;
                Slog.w("BackupManagerService", addUserIdToLogMessage(i2, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent connected"));
            }
            this.mAgentConnectLock.notifyAll();
        }
    }

    public void agentDisconnected(final String str) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                this.mConnectedAgent = null;
                this.mConnecting = false;
            } else {
                int i = this.mUserId;
                Slog.w("BackupManagerService", addUserIdToLogMessage(i, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent disconnected"));
            }
            Slog.w("BackupManagerService", "agentDisconnected: the backup agent for " + str + " died: cancel current operations");
            getThreadForAsyncOperation("agent-disconnected", new Runnable() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    UserBackupManagerService.this.lambda$agentDisconnected$9(str);
                }
            }).start();
            this.mAgentConnectLock.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$agentDisconnected$9(String str) {
        for (Integer num : this.mOperationStorage.operationTokensForPackage(str)) {
            handleCancel(num.intValue(), true);
        }
    }

    @VisibleForTesting
    public Thread getThreadForAsyncOperation(String str, Runnable runnable) {
        return new Thread(runnable, str);
    }

    public void restoreAtInstall(String str, int i) {
        boolean z;
        if (Binder.getCallingUid() != 1000) {
            int i2 = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i2, "Non-system process uid=" + Binder.getCallingUid() + " attemping install-time restore"));
            return;
        }
        long availableRestoreToken = getAvailableRestoreToken(str);
        int i3 = this.mUserId;
        Slog.v("BackupManagerService", addUserIdToLogMessage(i3, "restoreAtInstall pkg=" + str + " token=" + Integer.toHexString(i) + " restoreSet=" + Long.toHexString(availableRestoreToken)));
        boolean z2 = availableRestoreToken == 0;
        final TransportConnection currentTransportClient = this.mTransportManager.getCurrentTransportClient("BMS.restoreAtInstall()");
        if (currentTransportClient == null) {
            Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "No transport client"));
            z2 = true;
        }
        if (!this.mAutoRestore) {
            int i4 = this.mUserId;
            Slog.w("BackupManagerService", addUserIdToLogMessage(i4, "Non-restorable state: auto=" + this.mAutoRestore));
            z2 = true;
        }
        if (!z2) {
            try {
                this.mWakelock.acquire();
                OnTaskFinishedListener onTaskFinishedListener = new OnTaskFinishedListener() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda10
                    @Override // com.android.server.backup.internal.OnTaskFinishedListener
                    public final void onFinished(String str2) {
                        UserBackupManagerService.this.lambda$restoreAtInstall$10(currentTransportClient, str2);
                    }
                };
                Message obtainMessage = this.mBackupHandler.obtainMessage(3);
                obtainMessage.obj = RestoreParams.createForRestoreAtInstall(currentTransportClient, null, null, availableRestoreToken, str, i, onTaskFinishedListener, getEligibilityRulesForRestoreAtInstall(availableRestoreToken));
                this.mBackupHandler.sendMessage(obtainMessage);
            } catch (Exception e) {
                int i5 = this.mUserId;
                Slog.e("BackupManagerService", addUserIdToLogMessage(i5, "Unable to contact transport: " + e.getMessage()));
                z = true;
            }
        }
        z = z2;
        if (z) {
            if (currentTransportClient != null) {
                this.mTransportManager.disposeOfTransportClient(currentTransportClient, "BMS.restoreAtInstall()");
            }
            Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Finishing install immediately"));
            try {
                this.mPackageManagerBinder.finishPackageInstall(i, false);
            } catch (RemoteException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restoreAtInstall$10(TransportConnection transportConnection, String str) {
        this.mTransportManager.disposeOfTransportClient(transportConnection, str);
        this.mWakelock.release();
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x011c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public IRestoreSession beginRestoreSession(String str, String str2) {
        TransportConnection transportConnection;
        Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "beginRestoreSession: pkg=" + str + " transport=" + str2));
        boolean z = true;
        if (str2 == null) {
            str2 = this.mTransportManager.getCurrentTransportName();
            if (str != null) {
                try {
                    if (this.mPackageManager.getPackageInfoAsUser(str, 0, this.mUserId).applicationInfo.uid == Binder.getCallingUid()) {
                        z = false;
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    Slog.w("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Asked to restore nonexistent pkg " + str));
                    throw new IllegalArgumentException("Package " + str + " not found");
                }
            }
        }
        if (z) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "beginRestoreSession");
        } else {
            Slog.d("BackupManagerService", addUserIdToLogMessage(this.mUserId, "restoring self on current transport; no permission needed"));
        }
        TransportConnection transportConnection2 = null;
        try {
            transportConnection = this.mTransportManager.getTransportClientOrThrow(str2, "BMS.beginRestoreSession");
            try {
                try {
                    int backupDestinationFromTransport = getBackupDestinationFromTransport(transportConnection);
                    if (transportConnection != null) {
                        this.mTransportManager.disposeOfTransportClient(transportConnection, "BMS.beginRestoreSession");
                    }
                    synchronized (this) {
                        if (this.mActiveRestoreSession != null) {
                            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Restore session requested but one already active"));
                            return null;
                        } else if (this.mBackupRunning) {
                            Slog.i("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Restore session requested but currently running backups"));
                            return null;
                        } else {
                            this.mActiveRestoreSession = new ActiveRestoreSession(this, str, str2, getEligibilityRulesForOperation(backupDestinationFromTransport));
                            this.mBackupHandler.sendEmptyMessageDelayed(8, this.mAgentTimeoutParameters.getRestoreSessionTimeoutMillis());
                            return this.mActiveRestoreSession;
                        }
                    }
                } catch (RemoteException | TransportNotAvailableException | TransportNotRegisteredException e) {
                    e = e;
                    Slog.w("BackupManagerService", "Failed to get operation type from transport: " + e);
                    if (transportConnection != null) {
                        this.mTransportManager.disposeOfTransportClient(transportConnection, "BMS.beginRestoreSession");
                    }
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                transportConnection2 = transportConnection;
                if (transportConnection2 != null) {
                    this.mTransportManager.disposeOfTransportClient(transportConnection2, "BMS.beginRestoreSession");
                }
                throw th;
            }
        } catch (RemoteException | TransportNotAvailableException | TransportNotRegisteredException e2) {
            e = e2;
            transportConnection = null;
        } catch (Throwable th2) {
            th = th2;
            if (transportConnection2 != null) {
            }
            throw th;
        }
    }

    public void clearRestoreSession(ActiveRestoreSession activeRestoreSession) {
        synchronized (this) {
            if (activeRestoreSession != this.mActiveRestoreSession) {
                Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "ending non-current restore session"));
            } else {
                Slog.v("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Clearing restore session and halting timeout"));
                this.mActiveRestoreSession = null;
                this.mBackupHandler.removeMessages(8);
            }
        }
    }

    public void opComplete(int i, final long j) {
        this.mOperationStorage.onOperationComplete(i, j, new Consumer() { // from class: com.android.server.backup.UserBackupManagerService$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UserBackupManagerService.this.lambda$opComplete$11(j, (BackupRestoreTask) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$opComplete$11(long j, BackupRestoreTask backupRestoreTask) {
        this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(21, Pair.create(backupRestoreTask, Long.valueOf(j))));
    }

    public boolean isAppEligibleForBackup(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "isAppEligibleForBackup");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            TransportConnection currentTransportClient = this.mTransportManager.getCurrentTransportClient("BMS.isAppEligibleForBackup");
            boolean appIsRunningAndEligibleForBackupWithTransport = this.mScheduledBackupEligibility.appIsRunningAndEligibleForBackupWithTransport(currentTransportClient, str);
            if (currentTransportClient != null) {
                this.mTransportManager.disposeOfTransportClient(currentTransportClient, "BMS.isAppEligibleForBackup");
            }
            return appIsRunningAndEligibleForBackupWithTransport;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public String[] filterAppsEligibleForBackup(String[] strArr) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "filterAppsEligibleForBackup");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            TransportConnection currentTransportClient = this.mTransportManager.getCurrentTransportClient("BMS.filterAppsEligibleForBackup");
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                if (this.mScheduledBackupEligibility.appIsRunningAndEligibleForBackupWithTransport(currentTransportClient, str)) {
                    arrayList.add(str);
                }
            }
            if (currentTransportClient != null) {
                this.mTransportManager.disposeOfTransportClient(currentTransportClient, "BMS.filterAppsEligibleForBackup");
            }
            return (String[]) arrayList.toArray(new String[0]);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public BackupEligibilityRules getEligibilityRulesForOperation(int i) {
        return getEligibilityRules(this.mPackageManager, this.mUserId, this.mContext, i);
    }

    public static BackupEligibilityRules getEligibilityRules(PackageManager packageManager, int i, Context context, int i2) {
        return new BackupEligibilityRules(packageManager, (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class), i, context, i2);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (strArr != null) {
            try {
                for (String str : strArr) {
                    if ("agents".startsWith(str)) {
                        dumpAgents(printWriter);
                        return;
                    } else if ("transportclients".equals(str.toLowerCase())) {
                        this.mTransportManager.dumpTransportClients(printWriter);
                        return;
                    } else if ("transportstats".equals(str.toLowerCase())) {
                        this.mTransportManager.dumpTransportStats(printWriter);
                        return;
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        dumpInternal(printWriter);
    }

    public final void dumpAgents(PrintWriter printWriter) {
        List<PackageInfo> allAgentPackages = allAgentPackages();
        printWriter.println("Defined backup agents:");
        for (PackageInfo packageInfo : allAgentPackages) {
            printWriter.print("  ");
            printWriter.print(packageInfo.packageName);
            printWriter.println(':');
            printWriter.print("      ");
            printWriter.println(packageInfo.applicationInfo.backupAgentName);
        }
    }

    @NeverCompile
    public final void dumpInternal(PrintWriter printWriter) {
        String str = this.mUserId == 0 ? "" : "User " + this.mUserId + XmlUtils.STRING_ARRAY_SEPARATOR;
        synchronized (this.mQueueLock) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("Backup Manager is ");
            sb.append(this.mEnabled ? "enabled" : "disabled");
            sb.append(" / ");
            sb.append(!this.mSetupComplete ? "not " : "");
            sb.append("setup complete / ");
            sb.append(this.mPendingInits.size() == 0 ? "not " : "");
            sb.append("pending init");
            printWriter.println(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Auto-restore is ");
            sb2.append(this.mAutoRestore ? "enabled" : "disabled");
            printWriter.println(sb2.toString());
            if (this.mBackupRunning) {
                printWriter.println("Backup currently running");
            }
            printWriter.println(isBackupOperationInProgress() ? "Backup in progress" : "No backups running");
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Framework scheduling is ");
            sb3.append(isFrameworkSchedulingEnabled() ? "enabled" : "disabled");
            printWriter.println(sb3.toString());
            printWriter.println("Last backup pass started: " + this.mLastBackupPass + " (now = " + System.currentTimeMillis() + ')');
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  next scheduled: ");
            sb4.append(KeyValueBackupJob.nextScheduled(this.mUserId));
            printWriter.println(sb4.toString());
            printWriter.println(str + "Transport whitelist:");
            for (ComponentName componentName : this.mTransportManager.getTransportWhitelist()) {
                printWriter.print("    ");
                printWriter.println(componentName.flattenToShortString());
            }
            printWriter.println(str + "Available transports:");
            String[] listAllTransports = listAllTransports();
            if (listAllTransports != null) {
                for (String str2 : listAllTransports) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(str2.equals(this.mTransportManager.getCurrentTransportName()) ? "  * " : "    ");
                    sb5.append(str2);
                    printWriter.println(sb5.toString());
                    try {
                        File file = new File(this.mBaseStateDir, this.mTransportManager.getTransportDirName(str2));
                        printWriter.println("       destination: " + this.mTransportManager.getTransportCurrentDestinationString(str2));
                        printWriter.println("       intent: " + this.mTransportManager.getTransportConfigurationIntent(str2));
                        File[] listFiles = file.listFiles();
                        int length = listFiles.length;
                        for (int i = 0; i < length; i++) {
                            File file2 = listFiles[i];
                            printWriter.println("       " + file2.getName() + " - " + file2.length() + " state bytes");
                        }
                    } catch (Exception e) {
                        Slog.e("BackupManagerService", addUserIdToLogMessage(this.mUserId, "Error in transport"), e);
                        printWriter.println("        Error: " + e);
                    }
                }
            }
            this.mTransportManager.dumpTransportClients(printWriter);
            printWriter.println(str + "Pending init: " + this.mPendingInits.size());
            Iterator<String> it = this.mPendingInits.iterator();
            while (it.hasNext()) {
                printWriter.println("    " + it.next());
            }
            printWriter.print(str + "Ancestral: ");
            printWriter.println(Long.toHexString(this.mAncestralToken));
            printWriter.print(str + "Current:   ");
            printWriter.println(Long.toHexString(this.mCurrentToken));
            int size = this.mBackupParticipants.size();
            printWriter.println(str + "Participants:");
            for (int i2 = 0; i2 < size; i2++) {
                int keyAt = this.mBackupParticipants.keyAt(i2);
                printWriter.print("  uid: ");
                printWriter.println(keyAt);
                Iterator<String> it2 = this.mBackupParticipants.valueAt(i2).iterator();
                while (it2.hasNext()) {
                    printWriter.println("    " + it2.next());
                }
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append("Ancestral packages: ");
            Set<String> set = this.mAncestralPackages;
            sb6.append(set == null ? "none" : Integer.valueOf(set.size()));
            printWriter.println(sb6.toString());
            Set<String> set2 = this.mAncestralPackages;
            if (set2 != null) {
                Iterator<String> it3 = set2.iterator();
                while (it3.hasNext()) {
                    printWriter.println("    " + it3.next());
                }
            }
            Set<String> packagesCopy = this.mProcessedPackagesJournal.getPackagesCopy();
            printWriter.println(str + "Ever backed up: " + packagesCopy.size());
            Iterator<String> it4 = packagesCopy.iterator();
            while (it4.hasNext()) {
                printWriter.println("    " + it4.next());
            }
            printWriter.println(str + "Pending key/value backup: " + this.mPendingBackups.size());
            Iterator<BackupRequest> it5 = this.mPendingBackups.values().iterator();
            while (it5.hasNext()) {
                printWriter.println("    " + it5.next());
            }
            printWriter.println(str + "Full backup queue:" + this.mFullBackupQueue.size());
            Iterator<FullBackupEntry> it6 = this.mFullBackupQueue.iterator();
            while (it6.hasNext()) {
                FullBackupEntry next = it6.next();
                printWriter.print("    ");
                printWriter.print(next.lastBackup);
                printWriter.print(" : ");
                printWriter.println(next.packageName);
            }
            printWriter.println(str + "Agent timeouts:");
            printWriter.println("    KvBackupAgentTimeoutMillis: " + this.mAgentTimeoutParameters.getKvBackupAgentTimeoutMillis());
            printWriter.println("    FullBackupAgentTimeoutMillis: " + this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis());
            printWriter.println("    SharedBackupAgentTimeoutMillis: " + this.mAgentTimeoutParameters.getSharedBackupAgentTimeoutMillis());
            printWriter.println("    RestoreAgentTimeoutMillis (system): " + this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis(9999));
            printWriter.println("    RestoreAgentTimeoutMillis: " + this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis(FrameworkStatsLog.WIFI_BYTES_TRANSFER));
            printWriter.println("    RestoreAgentFinishedTimeoutMillis: " + this.mAgentTimeoutParameters.getRestoreAgentFinishedTimeoutMillis());
            printWriter.println("    QuotaExceededTimeoutMillis: " + this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis());
        }
    }

    @VisibleForTesting
    public int getBackupDestinationFromTransport(TransportConnection transportConnection) throws TransportNotAvailableException, RemoteException {
        if (shouldUseNewBackupEligibilityRules()) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if ((transportConnection.connectOrThrow("BMS.getBackupDestinationFromTransport").getTransportFlags() & 2) != 0) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return 1;
                }
                return 0;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return 0;
    }

    @VisibleForTesting
    public boolean shouldUseNewBackupEligibilityRules() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_use_new_backup_eligibility_rules");
    }

    public static String addUserIdToLogMessage(int i, String str) {
        return "[UserID:" + i + "] " + str;
    }

    public IBackupManager getBackupManagerBinder() {
        return this.mBackupManagerBinder;
    }
}
