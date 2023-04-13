package android.content;

import android.accounts.Account;
import android.content.ISyncStatusObserver;
import android.database.IContentObserver;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IContentService extends IInterface {
    void addPeriodicSync(Account account, String str, Bundle bundle, long j) throws RemoteException;

    void addStatusChangeListener(int i, ISyncStatusObserver iSyncStatusObserver) throws RemoteException;

    void cancelRequest(SyncRequest syncRequest) throws RemoteException;

    void cancelSync(Account account, String str, ComponentName componentName) throws RemoteException;

    void cancelSyncAsUser(Account account, String str, ComponentName componentName, int i) throws RemoteException;

    Bundle getCache(String str, Uri uri, int i) throws RemoteException;

    List<SyncInfo> getCurrentSyncs() throws RemoteException;

    List<SyncInfo> getCurrentSyncsAsUser(int i) throws RemoteException;

    int getIsSyncable(Account account, String str) throws RemoteException;

    int getIsSyncableAsUser(Account account, String str, int i) throws RemoteException;

    boolean getMasterSyncAutomatically() throws RemoteException;

    boolean getMasterSyncAutomaticallyAsUser(int i) throws RemoteException;

    List<PeriodicSync> getPeriodicSyncs(Account account, String str, ComponentName componentName) throws RemoteException;

    String getSyncAdapterPackageAsUser(String str, String str2, int i) throws RemoteException;

    String[] getSyncAdapterPackagesForAuthorityAsUser(String str, int i) throws RemoteException;

    SyncAdapterType[] getSyncAdapterTypes() throws RemoteException;

    SyncAdapterType[] getSyncAdapterTypesAsUser(int i) throws RemoteException;

    boolean getSyncAutomatically(Account account, String str) throws RemoteException;

    boolean getSyncAutomaticallyAsUser(Account account, String str, int i) throws RemoteException;

    SyncStatusInfo getSyncStatus(Account account, String str, ComponentName componentName) throws RemoteException;

    SyncStatusInfo getSyncStatusAsUser(Account account, String str, ComponentName componentName, int i) throws RemoteException;

    boolean isSyncActive(Account account, String str, ComponentName componentName) throws RemoteException;

    boolean isSyncPending(Account account, String str, ComponentName componentName) throws RemoteException;

    boolean isSyncPendingAsUser(Account account, String str, ComponentName componentName, int i) throws RemoteException;

    void notifyChange(Uri[] uriArr, IContentObserver iContentObserver, boolean z, int i, int i2, int i3, String str) throws RemoteException;

    void onDbCorruption(String str, String str2, String str3) throws RemoteException;

    void putCache(String str, Uri uri, Bundle bundle, int i) throws RemoteException;

    void registerContentObserver(Uri uri, boolean z, IContentObserver iContentObserver, int i, int i2) throws RemoteException;

    void removePeriodicSync(Account account, String str, Bundle bundle) throws RemoteException;

    void removeStatusChangeListener(ISyncStatusObserver iSyncStatusObserver) throws RemoteException;

    void requestSync(Account account, String str, Bundle bundle, String str2) throws RemoteException;

    void resetTodayStats() throws RemoteException;

    void setIsSyncable(Account account, String str, int i) throws RemoteException;

    void setIsSyncableAsUser(Account account, String str, int i, int i2) throws RemoteException;

    void setMasterSyncAutomatically(boolean z) throws RemoteException;

    void setMasterSyncAutomaticallyAsUser(boolean z, int i) throws RemoteException;

    void setSyncAutomatically(Account account, String str, boolean z) throws RemoteException;

    void setSyncAutomaticallyAsUser(Account account, String str, boolean z, int i) throws RemoteException;

    void sync(SyncRequest syncRequest, String str) throws RemoteException;

    void syncAsUser(SyncRequest syncRequest, int i, String str) throws RemoteException;

    void unregisterContentObserver(IContentObserver iContentObserver) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IContentService {
        @Override // android.content.IContentService
        public void unregisterContentObserver(IContentObserver observer) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer, int userHandle, int targetSdkVersion) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void notifyChange(Uri[] uris, IContentObserver observer, boolean observerWantsSelfNotifications, int flags, int userHandle, int targetSdkVersion, String callingPackage) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void requestSync(Account account, String authority, Bundle extras, String callingPackage) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void sync(SyncRequest request, String callingPackage) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void syncAsUser(SyncRequest request, int userId, String callingPackage) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void cancelSync(Account account, String authority, ComponentName cname) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void cancelSyncAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void cancelRequest(SyncRequest request) throws RemoteException {
        }

        @Override // android.content.IContentService
        public boolean getSyncAutomatically(Account account, String providerName) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public boolean getSyncAutomaticallyAsUser(Account account, String providerName, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public void setSyncAutomatically(Account account, String providerName, boolean sync) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void setSyncAutomaticallyAsUser(Account account, String providerName, boolean sync, int userId) throws RemoteException {
        }

        @Override // android.content.IContentService
        public List<PeriodicSync> getPeriodicSyncs(Account account, String providerName, ComponentName cname) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public void addPeriodicSync(Account account, String providerName, Bundle extras, long pollFrequency) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void removePeriodicSync(Account account, String providerName, Bundle extras) throws RemoteException {
        }

        @Override // android.content.IContentService
        public int getIsSyncable(Account account, String providerName) throws RemoteException {
            return 0;
        }

        @Override // android.content.IContentService
        public int getIsSyncableAsUser(Account account, String providerName, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.content.IContentService
        public void setIsSyncable(Account account, String providerName, int syncable) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void setIsSyncableAsUser(Account account, String providerName, int syncable, int userId) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void setMasterSyncAutomatically(boolean flag) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void setMasterSyncAutomaticallyAsUser(boolean flag, int userId) throws RemoteException {
        }

        @Override // android.content.IContentService
        public boolean getMasterSyncAutomatically() throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public boolean getMasterSyncAutomaticallyAsUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public List<SyncInfo> getCurrentSyncs() throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public List<SyncInfo> getCurrentSyncsAsUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public SyncAdapterType[] getSyncAdapterTypes() throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public SyncAdapterType[] getSyncAdapterTypesAsUser(int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public String[] getSyncAdapterPackagesForAuthorityAsUser(String authority, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public String getSyncAdapterPackageAsUser(String accountType, String authority, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public boolean isSyncActive(Account account, String authority, ComponentName cname) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public SyncStatusInfo getSyncStatus(Account account, String authority, ComponentName cname) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public SyncStatusInfo getSyncStatusAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public boolean isSyncPending(Account account, String authority, ComponentName cname) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public boolean isSyncPendingAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
            return false;
        }

        @Override // android.content.IContentService
        public void addStatusChangeListener(int mask, ISyncStatusObserver callback) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void removeStatusChangeListener(ISyncStatusObserver callback) throws RemoteException {
        }

        @Override // android.content.IContentService
        public void putCache(String packageName, Uri key, Bundle value, int userId) throws RemoteException {
        }

        @Override // android.content.IContentService
        public Bundle getCache(String packageName, Uri key, int userId) throws RemoteException {
            return null;
        }

        @Override // android.content.IContentService
        public void resetTodayStats() throws RemoteException {
        }

        @Override // android.content.IContentService
        public void onDbCorruption(String tag, String message, String stacktrace) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IContentService {
        public static final String DESCRIPTOR = "android.content.IContentService";
        static final int TRANSACTION_addPeriodicSync = 15;
        static final int TRANSACTION_addStatusChangeListener = 36;
        static final int TRANSACTION_cancelRequest = 9;
        static final int TRANSACTION_cancelSync = 7;
        static final int TRANSACTION_cancelSyncAsUser = 8;
        static final int TRANSACTION_getCache = 39;
        static final int TRANSACTION_getCurrentSyncs = 25;
        static final int TRANSACTION_getCurrentSyncsAsUser = 26;
        static final int TRANSACTION_getIsSyncable = 17;
        static final int TRANSACTION_getIsSyncableAsUser = 18;
        static final int TRANSACTION_getMasterSyncAutomatically = 23;
        static final int TRANSACTION_getMasterSyncAutomaticallyAsUser = 24;
        static final int TRANSACTION_getPeriodicSyncs = 14;
        static final int TRANSACTION_getSyncAdapterPackageAsUser = 30;
        static final int TRANSACTION_getSyncAdapterPackagesForAuthorityAsUser = 29;
        static final int TRANSACTION_getSyncAdapterTypes = 27;
        static final int TRANSACTION_getSyncAdapterTypesAsUser = 28;
        static final int TRANSACTION_getSyncAutomatically = 10;
        static final int TRANSACTION_getSyncAutomaticallyAsUser = 11;
        static final int TRANSACTION_getSyncStatus = 32;
        static final int TRANSACTION_getSyncStatusAsUser = 33;
        static final int TRANSACTION_isSyncActive = 31;
        static final int TRANSACTION_isSyncPending = 34;
        static final int TRANSACTION_isSyncPendingAsUser = 35;
        static final int TRANSACTION_notifyChange = 3;
        static final int TRANSACTION_onDbCorruption = 41;
        static final int TRANSACTION_putCache = 38;
        static final int TRANSACTION_registerContentObserver = 2;
        static final int TRANSACTION_removePeriodicSync = 16;
        static final int TRANSACTION_removeStatusChangeListener = 37;
        static final int TRANSACTION_requestSync = 4;
        static final int TRANSACTION_resetTodayStats = 40;
        static final int TRANSACTION_setIsSyncable = 19;
        static final int TRANSACTION_setIsSyncableAsUser = 20;
        static final int TRANSACTION_setMasterSyncAutomatically = 21;
        static final int TRANSACTION_setMasterSyncAutomaticallyAsUser = 22;
        static final int TRANSACTION_setSyncAutomatically = 12;
        static final int TRANSACTION_setSyncAutomaticallyAsUser = 13;
        static final int TRANSACTION_sync = 5;
        static final int TRANSACTION_syncAsUser = 6;
        static final int TRANSACTION_unregisterContentObserver = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContentService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContentService)) {
                return (IContentService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "unregisterContentObserver";
                case 2:
                    return "registerContentObserver";
                case 3:
                    return "notifyChange";
                case 4:
                    return "requestSync";
                case 5:
                    return "sync";
                case 6:
                    return "syncAsUser";
                case 7:
                    return "cancelSync";
                case 8:
                    return "cancelSyncAsUser";
                case 9:
                    return "cancelRequest";
                case 10:
                    return "getSyncAutomatically";
                case 11:
                    return "getSyncAutomaticallyAsUser";
                case 12:
                    return "setSyncAutomatically";
                case 13:
                    return "setSyncAutomaticallyAsUser";
                case 14:
                    return "getPeriodicSyncs";
                case 15:
                    return "addPeriodicSync";
                case 16:
                    return "removePeriodicSync";
                case 17:
                    return "getIsSyncable";
                case 18:
                    return "getIsSyncableAsUser";
                case 19:
                    return "setIsSyncable";
                case 20:
                    return "setIsSyncableAsUser";
                case 21:
                    return "setMasterSyncAutomatically";
                case 22:
                    return "setMasterSyncAutomaticallyAsUser";
                case 23:
                    return "getMasterSyncAutomatically";
                case 24:
                    return "getMasterSyncAutomaticallyAsUser";
                case 25:
                    return "getCurrentSyncs";
                case 26:
                    return "getCurrentSyncsAsUser";
                case 27:
                    return "getSyncAdapterTypes";
                case 28:
                    return "getSyncAdapterTypesAsUser";
                case 29:
                    return "getSyncAdapterPackagesForAuthorityAsUser";
                case 30:
                    return "getSyncAdapterPackageAsUser";
                case 31:
                    return "isSyncActive";
                case 32:
                    return "getSyncStatus";
                case 33:
                    return "getSyncStatusAsUser";
                case 34:
                    return "isSyncPending";
                case 35:
                    return "isSyncPendingAsUser";
                case 36:
                    return "addStatusChangeListener";
                case 37:
                    return "removeStatusChangeListener";
                case 38:
                    return "putCache";
                case 39:
                    return "getCache";
                case 40:
                    return "resetTodayStats";
                case 41:
                    return "onDbCorruption";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IContentObserver _arg0 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterContentObserver(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            Uri _arg02 = (Uri) data.readTypedObject(Uri.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            IContentObserver _arg2 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            data.enforceNoDataAvail();
                            registerContentObserver(_arg02, _arg1, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            break;
                        case 3:
                            Uri[] _arg03 = (Uri[]) data.createTypedArray(Uri.CREATOR);
                            IContentObserver _arg12 = IContentObserver.Stub.asInterface(data.readStrongBinder());
                            boolean _arg22 = data.readBoolean();
                            int _arg32 = data.readInt();
                            int _arg42 = data.readInt();
                            int _arg5 = data.readInt();
                            String _arg6 = data.readString();
                            data.enforceNoDataAvail();
                            notifyChange(_arg03, _arg12, _arg22, _arg32, _arg42, _arg5, _arg6);
                            reply.writeNoException();
                            break;
                        case 4:
                            Account _arg04 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg13 = data.readString();
                            Bundle _arg23 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            requestSync(_arg04, _arg13, _arg23, _arg33);
                            reply.writeNoException();
                            break;
                        case 5:
                            SyncRequest _arg05 = (SyncRequest) data.readTypedObject(SyncRequest.CREATOR);
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            sync(_arg05, _arg14);
                            reply.writeNoException();
                            break;
                        case 6:
                            SyncRequest _arg06 = (SyncRequest) data.readTypedObject(SyncRequest.CREATOR);
                            int _arg15 = data.readInt();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            syncAsUser(_arg06, _arg15, _arg24);
                            reply.writeNoException();
                            break;
                        case 7:
                            Account _arg07 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg16 = data.readString();
                            ComponentName _arg25 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            cancelSync(_arg07, _arg16, _arg25);
                            reply.writeNoException();
                            break;
                        case 8:
                            Account _arg08 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg17 = data.readString();
                            ComponentName _arg26 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg34 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelSyncAsUser(_arg08, _arg17, _arg26, _arg34);
                            reply.writeNoException();
                            break;
                        case 9:
                            SyncRequest _arg09 = (SyncRequest) data.readTypedObject(SyncRequest.CREATOR);
                            data.enforceNoDataAvail();
                            cancelRequest(_arg09);
                            reply.writeNoException();
                            break;
                        case 10:
                            Account _arg010 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = getSyncAutomatically(_arg010, _arg18);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 11:
                            Account _arg011 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg19 = data.readString();
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = getSyncAutomaticallyAsUser(_arg011, _arg19, _arg27);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 12:
                            Account _arg012 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg110 = data.readString();
                            boolean _arg28 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSyncAutomatically(_arg012, _arg110, _arg28);
                            reply.writeNoException();
                            break;
                        case 13:
                            Account _arg013 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg111 = data.readString();
                            boolean _arg29 = data.readBoolean();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            setSyncAutomaticallyAsUser(_arg013, _arg111, _arg29, _arg35);
                            reply.writeNoException();
                            break;
                        case 14:
                            Account _arg014 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg112 = data.readString();
                            ComponentName _arg210 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            List<PeriodicSync> _result3 = getPeriodicSyncs(_arg014, _arg112, _arg210);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 15:
                            Account _arg015 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg113 = data.readString();
                            Bundle _arg211 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            long _arg36 = data.readLong();
                            data.enforceNoDataAvail();
                            addPeriodicSync(_arg015, _arg113, _arg211, _arg36);
                            reply.writeNoException();
                            break;
                        case 16:
                            Account _arg016 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg114 = data.readString();
                            Bundle _arg212 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            removePeriodicSync(_arg016, _arg114, _arg212);
                            reply.writeNoException();
                            break;
                        case 17:
                            Account _arg017 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg115 = data.readString();
                            data.enforceNoDataAvail();
                            int _result4 = getIsSyncable(_arg017, _arg115);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 18:
                            Account _arg018 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg116 = data.readString();
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = getIsSyncableAsUser(_arg018, _arg116, _arg213);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 19:
                            Account _arg019 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg117 = data.readString();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            setIsSyncable(_arg019, _arg117, _arg214);
                            reply.writeNoException();
                            break;
                        case 20:
                            Account _arg020 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg118 = data.readString();
                            int _arg215 = data.readInt();
                            int _arg37 = data.readInt();
                            data.enforceNoDataAvail();
                            setIsSyncableAsUser(_arg020, _arg118, _arg215, _arg37);
                            reply.writeNoException();
                            break;
                        case 21:
                            boolean _arg021 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMasterSyncAutomatically(_arg021);
                            reply.writeNoException();
                            break;
                        case 22:
                            boolean _arg022 = data.readBoolean();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            setMasterSyncAutomaticallyAsUser(_arg022, _arg119);
                            reply.writeNoException();
                            break;
                        case 23:
                            boolean _result6 = getMasterSyncAutomatically();
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = getMasterSyncAutomaticallyAsUser(_arg023);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 25:
                            List<SyncInfo> _result8 = getCurrentSyncs();
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        case 26:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            List<SyncInfo> _result9 = getCurrentSyncsAsUser(_arg024);
                            reply.writeNoException();
                            reply.writeTypedList(_result9, 1);
                            break;
                        case 27:
                            SyncAdapterType[] _result10 = getSyncAdapterTypes();
                            reply.writeNoException();
                            reply.writeTypedArray(_result10, 1);
                            break;
                        case 28:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            SyncAdapterType[] _result11 = getSyncAdapterTypesAsUser(_arg025);
                            reply.writeNoException();
                            reply.writeTypedArray(_result11, 1);
                            break;
                        case 29:
                            String _arg026 = data.readString();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            String[] _result12 = getSyncAdapterPackagesForAuthorityAsUser(_arg026, _arg120);
                            reply.writeNoException();
                            reply.writeStringArray(_result12);
                            break;
                        case 30:
                            String _arg027 = data.readString();
                            String _arg121 = data.readString();
                            int _arg216 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result13 = getSyncAdapterPackageAsUser(_arg027, _arg121, _arg216);
                            reply.writeNoException();
                            reply.writeString(_result13);
                            break;
                        case 31:
                            Account _arg028 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg122 = data.readString();
                            ComponentName _arg217 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result14 = isSyncActive(_arg028, _arg122, _arg217);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 32:
                            Account _arg029 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg123 = data.readString();
                            ComponentName _arg218 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            SyncStatusInfo _result15 = getSyncStatus(_arg029, _arg123, _arg218);
                            reply.writeNoException();
                            reply.writeTypedObject(_result15, 1);
                            break;
                        case 33:
                            Account _arg030 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg124 = data.readString();
                            ComponentName _arg219 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg38 = data.readInt();
                            data.enforceNoDataAvail();
                            SyncStatusInfo _result16 = getSyncStatusAsUser(_arg030, _arg124, _arg219, _arg38);
                            reply.writeNoException();
                            reply.writeTypedObject(_result16, 1);
                            break;
                        case 34:
                            Account _arg031 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg125 = data.readString();
                            ComponentName _arg220 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result17 = isSyncPending(_arg031, _arg125, _arg220);
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 35:
                            Account _arg032 = (Account) data.readTypedObject(Account.CREATOR);
                            String _arg126 = data.readString();
                            ComponentName _arg221 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result18 = isSyncPendingAsUser(_arg032, _arg126, _arg221, _arg39);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 36:
                            int _arg033 = data.readInt();
                            ISyncStatusObserver _arg127 = ISyncStatusObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addStatusChangeListener(_arg033, _arg127);
                            reply.writeNoException();
                            break;
                        case 37:
                            ISyncStatusObserver _arg034 = ISyncStatusObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeStatusChangeListener(_arg034);
                            reply.writeNoException();
                            break;
                        case 38:
                            String _arg035 = data.readString();
                            Uri _arg128 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg222 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg310 = data.readInt();
                            data.enforceNoDataAvail();
                            putCache(_arg035, _arg128, _arg222, _arg310);
                            reply.writeNoException();
                            break;
                        case 39:
                            String _arg036 = data.readString();
                            Uri _arg129 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg223 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result19 = getCache(_arg036, _arg129, _arg223);
                            reply.writeNoException();
                            reply.writeTypedObject(_result19, 1);
                            break;
                        case 40:
                            resetTodayStats();
                            reply.writeNoException();
                            break;
                        case 41:
                            String _arg037 = data.readString();
                            String _arg130 = data.readString();
                            String _arg224 = data.readString();
                            data.enforceNoDataAvail();
                            onDbCorruption(_arg037, _arg130, _arg224);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IContentService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.content.IContentService
            public void unregisterContentObserver(IContentObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void registerContentObserver(Uri uri, boolean notifyForDescendants, IContentObserver observer, int userHandle, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    _data.writeBoolean(notifyForDescendants);
                    _data.writeStrongInterface(observer);
                    _data.writeInt(userHandle);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void notifyChange(Uri[] uris, IContentObserver observer, boolean observerWantsSelfNotifications, int flags, int userHandle, int targetSdkVersion, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(uris, 0);
                    _data.writeStrongInterface(observer);
                    _data.writeBoolean(observerWantsSelfNotifications);
                    _data.writeInt(flags);
                    _data.writeInt(userHandle);
                    _data.writeInt(targetSdkVersion);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void requestSync(Account account, String authority, Bundle extras, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(extras, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void sync(SyncRequest request, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void syncAsUser(SyncRequest request, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void cancelSync(Account account, String authority, ComponentName cname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void cancelSyncAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void cancelRequest(SyncRequest request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean getSyncAutomatically(Account account, String providerName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean getSyncAutomaticallyAsUser(Account account, String providerName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setSyncAutomatically(Account account, String providerName, boolean sync) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeBoolean(sync);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setSyncAutomaticallyAsUser(Account account, String providerName, boolean sync, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeBoolean(sync);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public List<PeriodicSync> getPeriodicSyncs(Account account, String providerName, ComponentName cname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeTypedObject(cname, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    List<PeriodicSync> _result = _reply.createTypedArrayList(PeriodicSync.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void addPeriodicSync(Account account, String providerName, Bundle extras, long pollFrequency) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeTypedObject(extras, 0);
                    _data.writeLong(pollFrequency);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void removePeriodicSync(Account account, String providerName, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public int getIsSyncable(Account account, String providerName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public int getIsSyncableAsUser(Account account, String providerName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeInt(userId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setIsSyncable(Account account, String providerName, int syncable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeInt(syncable);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setIsSyncableAsUser(Account account, String providerName, int syncable, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(providerName);
                    _data.writeInt(syncable);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setMasterSyncAutomatically(boolean flag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(flag);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void setMasterSyncAutomaticallyAsUser(boolean flag, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(flag);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean getMasterSyncAutomatically() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean getMasterSyncAutomaticallyAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public List<SyncInfo> getCurrentSyncs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    List<SyncInfo> _result = _reply.createTypedArrayList(SyncInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public List<SyncInfo> getCurrentSyncsAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    List<SyncInfo> _result = _reply.createTypedArrayList(SyncInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public SyncAdapterType[] getSyncAdapterTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    SyncAdapterType[] _result = (SyncAdapterType[]) _reply.createTypedArray(SyncAdapterType.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public SyncAdapterType[] getSyncAdapterTypesAsUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    SyncAdapterType[] _result = (SyncAdapterType[]) _reply.createTypedArray(SyncAdapterType.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public String[] getSyncAdapterPackagesForAuthorityAsUser(String authority, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(authority);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public String getSyncAdapterPackageAsUser(String accountType, String authority, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountType);
                    _data.writeString(authority);
                    _data.writeInt(userId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean isSyncActive(Account account, String authority, ComponentName cname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public SyncStatusInfo getSyncStatus(Account account, String authority, ComponentName cname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    SyncStatusInfo _result = (SyncStatusInfo) _reply.readTypedObject(SyncStatusInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public SyncStatusInfo getSyncStatusAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    SyncStatusInfo _result = (SyncStatusInfo) _reply.readTypedObject(SyncStatusInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean isSyncPending(Account account, String authority, ComponentName cname) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public boolean isSyncPendingAsUser(Account account, String authority, ComponentName cname, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(account, 0);
                    _data.writeString(authority);
                    _data.writeTypedObject(cname, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void addStatusChangeListener(int mask, ISyncStatusObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mask);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void removeStatusChangeListener(ISyncStatusObserver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void putCache(String packageName, Uri key, Bundle value, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(key, 0);
                    _data.writeTypedObject(value, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public Bundle getCache(String packageName, Uri key, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(key, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void resetTodayStats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.IContentService
            public void onDbCorruption(String tag, String message, String stacktrace) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag);
                    _data.writeString(message);
                    _data.writeString(stacktrace);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 40;
        }
    }
}
