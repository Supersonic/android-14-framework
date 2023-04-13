package com.android.internal.backup;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.RestoreDescription;
import android.app.backup.RestoreSet;
import android.content.Intent;
import android.content.p001pm.PackageInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import com.android.internal.backup.ITransportStatusCallback;
import com.android.internal.infra.AndroidFuture;
import java.util.List;
/* loaded from: classes4.dex */
public interface IBackupTransport extends IInterface {
    void abortFullRestore(ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void cancelFullBackup(ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void checkFullBackupSize(long j, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void clearBackupData(PackageInfo packageInfo, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void configurationIntent(AndroidFuture<Intent> androidFuture) throws RemoteException;

    void currentDestinationString(AndroidFuture<String> androidFuture) throws RemoteException;

    void dataManagementIntent(AndroidFuture<Intent> androidFuture) throws RemoteException;

    void dataManagementIntentLabel(AndroidFuture<CharSequence> androidFuture) throws RemoteException;

    void finishBackup(ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void finishRestore(ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void getAvailableRestoreSets(AndroidFuture<List<RestoreSet>> androidFuture) throws RemoteException;

    void getBackupManagerMonitor(AndroidFuture<IBackupManagerMonitor> androidFuture) throws RemoteException;

    void getBackupQuota(String str, boolean z, AndroidFuture<Long> androidFuture) throws RemoteException;

    void getCurrentRestoreSet(AndroidFuture<Long> androidFuture) throws RemoteException;

    void getNextFullRestoreDataChunk(ParcelFileDescriptor parcelFileDescriptor, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void getRestoreData(ParcelFileDescriptor parcelFileDescriptor, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void getTransportFlags(AndroidFuture<Integer> androidFuture) throws RemoteException;

    void initializeDevice(ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void isAppEligibleForBackup(PackageInfo packageInfo, boolean z, AndroidFuture<Boolean> androidFuture) throws RemoteException;

    void name(AndroidFuture<String> androidFuture) throws RemoteException;

    void nextRestorePackage(AndroidFuture<RestoreDescription> androidFuture) throws RemoteException;

    void performBackup(PackageInfo packageInfo, ParcelFileDescriptor parcelFileDescriptor, int i, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void performFullBackup(PackageInfo packageInfo, ParcelFileDescriptor parcelFileDescriptor, int i, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void requestBackupTime(AndroidFuture<Long> androidFuture) throws RemoteException;

    void requestFullBackupTime(AndroidFuture<Long> androidFuture) throws RemoteException;

    void sendBackupData(int i, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void startRestore(long j, PackageInfo[] packageInfoArr, ITransportStatusCallback iTransportStatusCallback) throws RemoteException;

    void transportDirName(AndroidFuture<String> androidFuture) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IBackupTransport {
        @Override // com.android.internal.backup.IBackupTransport
        public void name(AndroidFuture<String> result) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void configurationIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void currentDestinationString(AndroidFuture<String> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void dataManagementIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void dataManagementIntentLabel(AndroidFuture<CharSequence> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void transportDirName(AndroidFuture<String> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void requestBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void initializeDevice(ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd, int flags, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void clearBackupData(PackageInfo packageInfo, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void finishBackup(ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getAvailableRestoreSets(AndroidFuture<List<RestoreSet>> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getCurrentRestoreSet(AndroidFuture<Long> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void startRestore(long token, PackageInfo[] packages, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void nextRestorePackage(AndroidFuture<RestoreDescription> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getRestoreData(ParcelFileDescriptor outFd, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void finishRestore(ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void requestFullBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket, int flags, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void checkFullBackupSize(long size, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void sendBackupData(int numBytes, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void cancelFullBackup(ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void isAppEligibleForBackup(PackageInfo targetPackage, boolean isFullBackup, AndroidFuture<Boolean> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getBackupQuota(String packageName, boolean isFullBackup, AndroidFuture<Long> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getNextFullRestoreDataChunk(ParcelFileDescriptor socket, ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void abortFullRestore(ITransportStatusCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getTransportFlags(AndroidFuture<Integer> resultFuture) throws RemoteException {
        }

        @Override // com.android.internal.backup.IBackupTransport
        public void getBackupManagerMonitor(AndroidFuture<IBackupManagerMonitor> resultFuture) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IBackupTransport {
        public static final String DESCRIPTOR = "com.android.internal.backup.IBackupTransport";
        static final int TRANSACTION_abortFullRestore = 26;
        static final int TRANSACTION_cancelFullBackup = 22;
        static final int TRANSACTION_checkFullBackupSize = 20;
        static final int TRANSACTION_clearBackupData = 10;
        static final int TRANSACTION_configurationIntent = 2;
        static final int TRANSACTION_currentDestinationString = 3;
        static final int TRANSACTION_dataManagementIntent = 4;
        static final int TRANSACTION_dataManagementIntentLabel = 5;
        static final int TRANSACTION_finishBackup = 11;
        static final int TRANSACTION_finishRestore = 17;
        static final int TRANSACTION_getAvailableRestoreSets = 12;
        static final int TRANSACTION_getBackupManagerMonitor = 28;
        static final int TRANSACTION_getBackupQuota = 24;
        static final int TRANSACTION_getCurrentRestoreSet = 13;
        static final int TRANSACTION_getNextFullRestoreDataChunk = 25;
        static final int TRANSACTION_getRestoreData = 16;
        static final int TRANSACTION_getTransportFlags = 27;
        static final int TRANSACTION_initializeDevice = 8;
        static final int TRANSACTION_isAppEligibleForBackup = 23;
        static final int TRANSACTION_name = 1;
        static final int TRANSACTION_nextRestorePackage = 15;
        static final int TRANSACTION_performBackup = 9;
        static final int TRANSACTION_performFullBackup = 19;
        static final int TRANSACTION_requestBackupTime = 7;
        static final int TRANSACTION_requestFullBackupTime = 18;
        static final int TRANSACTION_sendBackupData = 21;
        static final int TRANSACTION_startRestore = 14;
        static final int TRANSACTION_transportDirName = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBackupTransport asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IBackupTransport)) {
                return (IBackupTransport) iin;
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
                    return "name";
                case 2:
                    return "configurationIntent";
                case 3:
                    return "currentDestinationString";
                case 4:
                    return "dataManagementIntent";
                case 5:
                    return "dataManagementIntentLabel";
                case 6:
                    return "transportDirName";
                case 7:
                    return "requestBackupTime";
                case 8:
                    return "initializeDevice";
                case 9:
                    return "performBackup";
                case 10:
                    return "clearBackupData";
                case 11:
                    return "finishBackup";
                case 12:
                    return "getAvailableRestoreSets";
                case 13:
                    return "getCurrentRestoreSet";
                case 14:
                    return "startRestore";
                case 15:
                    return "nextRestorePackage";
                case 16:
                    return "getRestoreData";
                case 17:
                    return "finishRestore";
                case 18:
                    return "requestFullBackupTime";
                case 19:
                    return "performFullBackup";
                case 20:
                    return "checkFullBackupSize";
                case 21:
                    return "sendBackupData";
                case 22:
                    return "cancelFullBackup";
                case 23:
                    return "isAppEligibleForBackup";
                case 24:
                    return "getBackupQuota";
                case 25:
                    return "getNextFullRestoreDataChunk";
                case 26:
                    return "abortFullRestore";
                case 27:
                    return "getTransportFlags";
                case 28:
                    return "getBackupManagerMonitor";
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
                            AndroidFuture<String> _arg0 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            name(_arg0);
                            break;
                        case 2:
                            AndroidFuture<Intent> _arg02 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            configurationIntent(_arg02);
                            break;
                        case 3:
                            AndroidFuture<String> _arg03 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            currentDestinationString(_arg03);
                            break;
                        case 4:
                            AndroidFuture<Intent> _arg04 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            dataManagementIntent(_arg04);
                            break;
                        case 5:
                            AndroidFuture<CharSequence> _arg05 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            dataManagementIntentLabel(_arg05);
                            break;
                        case 6:
                            AndroidFuture<String> _arg06 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            transportDirName(_arg06);
                            break;
                        case 7:
                            AndroidFuture<Long> _arg07 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            requestBackupTime(_arg07);
                            break;
                        case 8:
                            ITransportStatusCallback _arg08 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            initializeDevice(_arg08);
                            break;
                        case 9:
                            PackageInfo _arg09 = (PackageInfo) data.readTypedObject(PackageInfo.CREATOR);
                            ParcelFileDescriptor _arg1 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            int _arg2 = data.readInt();
                            ITransportStatusCallback _arg3 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            performBackup(_arg09, _arg1, _arg2, _arg3);
                            break;
                        case 10:
                            PackageInfo _arg010 = (PackageInfo) data.readTypedObject(PackageInfo.CREATOR);
                            ITransportStatusCallback _arg12 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            clearBackupData(_arg010, _arg12);
                            break;
                        case 11:
                            ITransportStatusCallback _arg011 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            finishBackup(_arg011);
                            break;
                        case 12:
                            AndroidFuture<List<RestoreSet>> _arg012 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getAvailableRestoreSets(_arg012);
                            break;
                        case 13:
                            AndroidFuture<Long> _arg013 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getCurrentRestoreSet(_arg013);
                            break;
                        case 14:
                            long _arg014 = data.readLong();
                            PackageInfo[] _arg13 = (PackageInfo[]) data.createTypedArray(PackageInfo.CREATOR);
                            ITransportStatusCallback _arg22 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startRestore(_arg014, _arg13, _arg22);
                            break;
                        case 15:
                            AndroidFuture<RestoreDescription> _arg015 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            nextRestorePackage(_arg015);
                            break;
                        case 16:
                            ParcelFileDescriptor _arg016 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ITransportStatusCallback _arg14 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getRestoreData(_arg016, _arg14);
                            break;
                        case 17:
                            ITransportStatusCallback _arg017 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            finishRestore(_arg017);
                            break;
                        case 18:
                            AndroidFuture<Long> _arg018 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            requestFullBackupTime(_arg018);
                            break;
                        case 19:
                            PackageInfo _arg019 = (PackageInfo) data.readTypedObject(PackageInfo.CREATOR);
                            ParcelFileDescriptor _arg15 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            int _arg23 = data.readInt();
                            ITransportStatusCallback _arg32 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            performFullBackup(_arg019, _arg15, _arg23, _arg32);
                            break;
                        case 20:
                            long _arg020 = data.readLong();
                            ITransportStatusCallback _arg16 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            checkFullBackupSize(_arg020, _arg16);
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            ITransportStatusCallback _arg17 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sendBackupData(_arg021, _arg17);
                            break;
                        case 22:
                            ITransportStatusCallback _arg022 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelFullBackup(_arg022);
                            break;
                        case 23:
                            PackageInfo _arg023 = (PackageInfo) data.readTypedObject(PackageInfo.CREATOR);
                            boolean _arg18 = data.readBoolean();
                            AndroidFuture<Boolean> _arg24 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            isAppEligibleForBackup(_arg023, _arg18, _arg24);
                            break;
                        case 24:
                            String _arg024 = data.readString();
                            boolean _arg19 = data.readBoolean();
                            AndroidFuture<Long> _arg25 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getBackupQuota(_arg024, _arg19, _arg25);
                            break;
                        case 25:
                            ParcelFileDescriptor _arg025 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ITransportStatusCallback _arg110 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getNextFullRestoreDataChunk(_arg025, _arg110);
                            break;
                        case 26:
                            ITransportStatusCallback _arg026 = ITransportStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            abortFullRestore(_arg026);
                            break;
                        case 27:
                            AndroidFuture<Integer> _arg027 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getTransportFlags(_arg027);
                            break;
                        case 28:
                            AndroidFuture<IBackupManagerMonitor> _arg028 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getBackupManagerMonitor(_arg028);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IBackupTransport {
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

            @Override // com.android.internal.backup.IBackupTransport
            public void name(AndroidFuture<String> result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void configurationIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void currentDestinationString(AndroidFuture<String> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void dataManagementIntent(AndroidFuture<Intent> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void dataManagementIntentLabel(AndroidFuture<CharSequence> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void transportDirName(AndroidFuture<String> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void requestBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void initializeDevice(ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void performBackup(PackageInfo packageInfo, ParcelFileDescriptor inFd, int flags, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(packageInfo, 0);
                    _data.writeTypedObject(inFd, 0);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void clearBackupData(PackageInfo packageInfo, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(packageInfo, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void finishBackup(ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getAvailableRestoreSets(AndroidFuture<List<RestoreSet>> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getCurrentRestoreSet(AndroidFuture<Long> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void startRestore(long token, PackageInfo[] packages, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(token);
                    _data.writeTypedArray(packages, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void nextRestorePackage(AndroidFuture<RestoreDescription> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getRestoreData(ParcelFileDescriptor outFd, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(outFd, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void finishRestore(ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void requestFullBackupTime(AndroidFuture<Long> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void performFullBackup(PackageInfo targetPackage, ParcelFileDescriptor socket, int flags, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(targetPackage, 0);
                    _data.writeTypedObject(socket, 0);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void checkFullBackupSize(long size, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(size);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void sendBackupData(int numBytes, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(numBytes);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void cancelFullBackup(ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void isAppEligibleForBackup(PackageInfo targetPackage, boolean isFullBackup, AndroidFuture<Boolean> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(targetPackage, 0);
                    _data.writeBoolean(isFullBackup);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getBackupQuota(String packageName, boolean isFullBackup, AndroidFuture<Long> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeBoolean(isFullBackup);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getNextFullRestoreDataChunk(ParcelFileDescriptor socket, ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(socket, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void abortFullRestore(ITransportStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getTransportFlags(AndroidFuture<Integer> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.IBackupTransport
            public void getBackupManagerMonitor(AndroidFuture<IBackupManagerMonitor> resultFuture) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(resultFuture, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 27;
        }
    }
}
