package android.app.backup;

import android.annotation.SystemApi;
import android.content.Intent;
import android.content.p001pm.PackageInfo;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import com.android.internal.backup.IBackupTransport;
import com.android.internal.backup.ITransportStatusCallback;
import com.android.internal.infra.AndroidFuture;
import java.util.Arrays;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public class BackupTransport {
    public static final int AGENT_ERROR = -1003;
    public static final int AGENT_UNKNOWN = -1004;
    public static final String EXTRA_TRANSPORT_REGISTRATION = "android.app.backup.extra.TRANSPORT_REGISTRATION";
    public static final int FLAG_DATA_NOT_CHANGED = 8;
    public static final int FLAG_INCREMENTAL = 2;
    public static final int FLAG_NON_INCREMENTAL = 4;
    public static final int FLAG_USER_INITIATED = 1;
    public static final int NO_MORE_DATA = -1;
    public static final int TRANSPORT_ERROR = -1000;
    public static final int TRANSPORT_NON_INCREMENTAL_BACKUP_REQUIRED = -1006;
    public static final int TRANSPORT_NOT_INITIALIZED = -1001;
    public static final int TRANSPORT_OK = 0;
    public static final int TRANSPORT_PACKAGE_REJECTED = -1002;
    public static final int TRANSPORT_QUOTA_EXCEEDED = -1005;
    IBackupTransport mBinderImpl = new TransportImpl();

    public IBinder getBinder() {
        return this.mBinderImpl.asBinder();
    }

    public String name() {
        throw new UnsupportedOperationException("Transport name() not implemented");
    }

    public Intent configurationIntent() {
        return null;
    }

    public String currentDestinationString() {
        throw new UnsupportedOperationException("Transport currentDestinationString() not implemented");
    }

    public Intent dataManagementIntent() {
        return null;
    }

    @Deprecated
    public String dataManagementLabel() {
        throw new UnsupportedOperationException("Transport dataManagementLabel() not implemented");
    }

    public CharSequence dataManagementIntentLabel() {
        return dataManagementLabel();
    }

    public String transportDirName() {
        throw new UnsupportedOperationException("Transport transportDirName() not implemented");
    }

    public int initializeDevice() {
        return -1000;
    }

    public int clearBackupData(PackageInfo packageInfo) {
        return -1000;
    }

    public int finishBackup() {
        return -1000;
    }

    public long requestBackupTime() {
        return 0L;
    }

    public int performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd, int flags) {
        return performBackup(packageInfo, inFd);
    }

    public int performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd) {
        return -1000;
    }

    public RestoreSet[] getAvailableRestoreSets() {
        return null;
    }

    public long getCurrentRestoreSet() {
        return 0L;
    }

    public int startRestore(long token, PackageInfo[] packages) {
        return -1000;
    }

    public RestoreDescription nextRestorePackage() {
        return null;
    }

    public int getRestoreData(ParcelFileDescriptor outFd) {
        return -1000;
    }

    public void finishRestore() {
        throw new UnsupportedOperationException("Transport finishRestore() not implemented");
    }

    public long requestFullBackupTime() {
        return 0L;
    }

    public int performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket, int flags) {
        return performFullBackup(targetPackage, socket);
    }

    public int performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket) {
        return -1002;
    }

    public int checkFullBackupSize(long size) {
        return 0;
    }

    public int sendBackupData(int numBytes) {
        return -1000;
    }

    public void cancelFullBackup() {
        throw new UnsupportedOperationException("Transport cancelFullBackup() not implemented");
    }

    public boolean isAppEligibleForBackup(PackageInfo targetPackage, boolean isFullBackup) {
        return true;
    }

    public long getBackupQuota(String packageName, boolean isFullBackup) {
        return Long.MAX_VALUE;
    }

    public int getNextFullRestoreDataChunk(ParcelFileDescriptor socket) {
        return 0;
    }

    public int abortFullRestore() {
        return 0;
    }

    public int getTransportFlags() {
        return 0;
    }

    public BackupManagerMonitor getBackupManagerMonitor() {
        return null;
    }

    /* loaded from: classes.dex */
    class TransportImpl extends IBackupTransport.Stub {
        TransportImpl() {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void name(AndroidFuture<String> resultFuture) throws RemoteException {
            try {
                String result = BackupTransport.this.name();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void configurationIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
            try {
                Intent result = BackupTransport.this.configurationIntent();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void currentDestinationString(AndroidFuture<String> resultFuture) throws RemoteException {
            try {
                String result = BackupTransport.this.currentDestinationString();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void dataManagementIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
            try {
                Intent result = BackupTransport.this.dataManagementIntent();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void dataManagementIntentLabel(AndroidFuture<CharSequence> resultFuture) throws RemoteException {
            try {
                CharSequence result = BackupTransport.this.dataManagementIntentLabel();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void transportDirName(AndroidFuture<String> resultFuture) throws RemoteException {
            try {
                String result = BackupTransport.this.transportDirName();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void requestBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
            try {
                long result = BackupTransport.this.requestBackupTime();
                resultFuture.complete(Long.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void initializeDevice(ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.initializeDevice();
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd, int flags, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.performBackup(packageInfo, inFd, flags);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void clearBackupData(PackageInfo packageInfo, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.clearBackupData(packageInfo);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void finishBackup(ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.finishBackup();
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getAvailableRestoreSets(AndroidFuture<List<RestoreSet>> resultFuture) throws RemoteException {
            try {
                RestoreSet[] result = BackupTransport.this.getAvailableRestoreSets();
                resultFuture.complete(Arrays.asList(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getCurrentRestoreSet(AndroidFuture<Long> resultFuture) throws RemoteException {
            try {
                long result = BackupTransport.this.getCurrentRestoreSet();
                resultFuture.complete(Long.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void startRestore(long token, PackageInfo[] packages, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.startRestore(token, packages);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void nextRestorePackage(AndroidFuture<RestoreDescription> resultFuture) throws RemoteException {
            try {
                RestoreDescription result = BackupTransport.this.nextRestorePackage();
                resultFuture.complete(result);
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getRestoreData(ParcelFileDescriptor outFd, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.getRestoreData(outFd);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void finishRestore(ITransportStatusCallback callback) throws RemoteException {
            try {
                BackupTransport.this.finishRestore();
                callback.onOperationComplete();
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void requestFullBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
            try {
                long result = BackupTransport.this.requestFullBackupTime();
                resultFuture.complete(Long.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket, int flags, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.performFullBackup(targetPackage, socket, flags);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void checkFullBackupSize(long size, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.checkFullBackupSize(size);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void sendBackupData(int numBytes, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.sendBackupData(numBytes);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void cancelFullBackup(ITransportStatusCallback callback) throws RemoteException {
            try {
                BackupTransport.this.cancelFullBackup();
                callback.onOperationComplete();
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void isAppEligibleForBackup(PackageInfo targetPackage, boolean isFullBackup, AndroidFuture<Boolean> resultFuture) throws RemoteException {
            try {
                boolean result = BackupTransport.this.isAppEligibleForBackup(targetPackage, isFullBackup);
                resultFuture.complete(Boolean.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getBackupQuota(String packageName, boolean isFullBackup, AndroidFuture<Long> resultFuture) throws RemoteException {
            try {
                long result = BackupTransport.this.getBackupQuota(packageName, isFullBackup);
                resultFuture.complete(Long.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getTransportFlags(AndroidFuture<Integer> resultFuture) throws RemoteException {
            try {
                int result = BackupTransport.this.getTransportFlags();
                resultFuture.complete(Integer.valueOf(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getNextFullRestoreDataChunk(ParcelFileDescriptor socket, ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.getNextFullRestoreDataChunk(socket);
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void abortFullRestore(ITransportStatusCallback callback) throws RemoteException {
            try {
                int result = BackupTransport.this.abortFullRestore();
                callback.onOperationCompleteWithStatus(result);
            } catch (RuntimeException e) {
                callback.onOperationCompleteWithStatus(-1000);
            }
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getBackupManagerMonitor(AndroidFuture<IBackupManagerMonitor> resultFuture) {
            try {
                BackupManagerMonitor result = BackupTransport.this.getBackupManagerMonitor();
                resultFuture.complete(new BackupManagerMonitorWrapper(result));
            } catch (RuntimeException e) {
                resultFuture.cancel(true);
            }
        }
    }
}
