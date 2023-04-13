package android.app.usage;

import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IStorageStatsManager extends IInterface {
    long getCacheBytes(String str, String str2) throws RemoteException;

    long getCacheQuotaBytes(String str, int i, String str2) throws RemoteException;

    long getFreeBytes(String str, String str2) throws RemoteException;

    long getTotalBytes(String str, String str2) throws RemoteException;

    boolean isQuotaSupported(String str, String str2) throws RemoteException;

    boolean isReservedSupported(String str, String str2) throws RemoteException;

    ParceledListSlice queryCratesForPackage(String str, String str2, int i, String str3) throws RemoteException;

    ParceledListSlice queryCratesForUid(String str, int i, String str2) throws RemoteException;

    ParceledListSlice queryCratesForUser(String str, int i, String str2) throws RemoteException;

    ExternalStorageStats queryExternalStatsForUser(String str, int i, String str2) throws RemoteException;

    StorageStats queryStatsForPackage(String str, String str2, int i, String str3) throws RemoteException;

    StorageStats queryStatsForUid(String str, int i, String str2) throws RemoteException;

    StorageStats queryStatsForUser(String str, int i, String str2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IStorageStatsManager {
        @Override // android.app.usage.IStorageStatsManager
        public boolean isQuotaSupported(String volumeUuid, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.usage.IStorageStatsManager
        public boolean isReservedSupported(String volumeUuid, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // android.app.usage.IStorageStatsManager
        public long getTotalBytes(String volumeUuid, String callingPackage) throws RemoteException {
            return 0L;
        }

        @Override // android.app.usage.IStorageStatsManager
        public long getFreeBytes(String volumeUuid, String callingPackage) throws RemoteException {
            return 0L;
        }

        @Override // android.app.usage.IStorageStatsManager
        public long getCacheBytes(String volumeUuid, String callingPackage) throws RemoteException {
            return 0L;
        }

        @Override // android.app.usage.IStorageStatsManager
        public long getCacheQuotaBytes(String volumeUuid, int uid, String callingPackage) throws RemoteException {
            return 0L;
        }

        @Override // android.app.usage.IStorageStatsManager
        public StorageStats queryStatsForPackage(String volumeUuid, String packageName, int userId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public StorageStats queryStatsForUid(String volumeUuid, int uid, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public StorageStats queryStatsForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public ExternalStorageStats queryExternalStatsForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public ParceledListSlice queryCratesForPackage(String volumeUuid, String packageName, int userId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public ParceledListSlice queryCratesForUid(String volumeUuid, int uid, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.app.usage.IStorageStatsManager
        public ParceledListSlice queryCratesForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IStorageStatsManager {
        public static final String DESCRIPTOR = "android.app.usage.IStorageStatsManager";
        static final int TRANSACTION_getCacheBytes = 5;
        static final int TRANSACTION_getCacheQuotaBytes = 6;
        static final int TRANSACTION_getFreeBytes = 4;
        static final int TRANSACTION_getTotalBytes = 3;
        static final int TRANSACTION_isQuotaSupported = 1;
        static final int TRANSACTION_isReservedSupported = 2;
        static final int TRANSACTION_queryCratesForPackage = 11;
        static final int TRANSACTION_queryCratesForUid = 12;
        static final int TRANSACTION_queryCratesForUser = 13;
        static final int TRANSACTION_queryExternalStatsForUser = 10;
        static final int TRANSACTION_queryStatsForPackage = 7;
        static final int TRANSACTION_queryStatsForUid = 8;
        static final int TRANSACTION_queryStatsForUser = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStorageStatsManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStorageStatsManager)) {
                return (IStorageStatsManager) iin;
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
                    return "isQuotaSupported";
                case 2:
                    return "isReservedSupported";
                case 3:
                    return "getTotalBytes";
                case 4:
                    return "getFreeBytes";
                case 5:
                    return "getCacheBytes";
                case 6:
                    return "getCacheQuotaBytes";
                case 7:
                    return "queryStatsForPackage";
                case 8:
                    return "queryStatsForUid";
                case 9:
                    return "queryStatsForUser";
                case 10:
                    return "queryExternalStatsForUser";
                case 11:
                    return "queryCratesForPackage";
                case 12:
                    return "queryCratesForUid";
                case 13:
                    return "queryCratesForUser";
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
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result = isQuotaSupported(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result2 = isReservedSupported(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            long _result3 = getTotalBytes(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeLong(_result3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            long _result4 = getFreeBytes(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            long _result5 = getCacheBytes(_arg05, _arg15);
                            reply.writeNoException();
                            reply.writeLong(_result5);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg16 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            long _result6 = getCacheQuotaBytes(_arg06, _arg16, _arg2);
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg17 = data.readString();
                            int _arg22 = data.readInt();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            StorageStats _result7 = queryStatsForPackage(_arg07, _arg17, _arg22, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            int _arg18 = data.readInt();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            StorageStats _result8 = queryStatsForUid(_arg08, _arg18, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            int _arg19 = data.readInt();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            StorageStats _result9 = queryStatsForUser(_arg09, _arg19, _arg24);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg110 = data.readInt();
                            String _arg25 = data.readString();
                            data.enforceNoDataAvail();
                            ExternalStorageStats _result10 = queryExternalStatsForUser(_arg010, _arg110, _arg25);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            String _arg111 = data.readString();
                            int _arg26 = data.readInt();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result11 = queryCratesForPackage(_arg011, _arg111, _arg26, _arg32);
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            int _arg112 = data.readInt();
                            String _arg27 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result12 = queryCratesForUid(_arg012, _arg112, _arg27);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            int _arg113 = data.readInt();
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result13 = queryCratesForUser(_arg013, _arg113, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IStorageStatsManager {
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

            @Override // android.app.usage.IStorageStatsManager
            public boolean isQuotaSupported(String volumeUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public boolean isReservedSupported(String volumeUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public long getTotalBytes(String volumeUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public long getFreeBytes(String volumeUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public long getCacheBytes(String volumeUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public long getCacheQuotaBytes(String volumeUuid, int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public StorageStats queryStatsForPackage(String volumeUuid, String packageName, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    StorageStats _result = (StorageStats) _reply.readTypedObject(StorageStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public StorageStats queryStatsForUid(String volumeUuid, int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    StorageStats _result = (StorageStats) _reply.readTypedObject(StorageStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public StorageStats queryStatsForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    StorageStats _result = (StorageStats) _reply.readTypedObject(StorageStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public ExternalStorageStats queryExternalStatsForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    ExternalStorageStats _result = (ExternalStorageStats) _reply.readTypedObject(ExternalStorageStats.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public ParceledListSlice queryCratesForPackage(String volumeUuid, String packageName, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public ParceledListSlice queryCratesForUid(String volumeUuid, int uid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(uid);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.usage.IStorageStatsManager
            public ParceledListSlice queryCratesForUser(String volumeUuid, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(volumeUuid);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
