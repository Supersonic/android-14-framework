package android.content.rollback;

import android.content.IntentSender;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IRollbackManager extends IInterface {
    public static final String DESCRIPTOR = "android.content.rollback.IRollbackManager";

    void blockRollbackManager(long j) throws RemoteException;

    void commitRollback(int i, ParceledListSlice parceledListSlice, String str, IntentSender intentSender) throws RemoteException;

    void expireRollbackForPackage(String str) throws RemoteException;

    ParceledListSlice getAvailableRollbacks() throws RemoteException;

    ParceledListSlice getRecentlyCommittedRollbacks() throws RemoteException;

    int notifyStagedSession(int i) throws RemoteException;

    void reloadPersistedData() throws RemoteException;

    void snapshotAndRestoreUserData(String str, int[] iArr, int i, long j, String str2, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IRollbackManager {
        @Override // android.content.rollback.IRollbackManager
        public ParceledListSlice getAvailableRollbacks() throws RemoteException {
            return null;
        }

        @Override // android.content.rollback.IRollbackManager
        public ParceledListSlice getRecentlyCommittedRollbacks() throws RemoteException {
            return null;
        }

        @Override // android.content.rollback.IRollbackManager
        public void commitRollback(int rollbackId, ParceledListSlice causePackages, String callerPackageName, IntentSender statusReceiver) throws RemoteException {
        }

        @Override // android.content.rollback.IRollbackManager
        public void snapshotAndRestoreUserData(String packageName, int[] userIds, int appId, long ceDataInode, String seInfo, int token) throws RemoteException {
        }

        @Override // android.content.rollback.IRollbackManager
        public void reloadPersistedData() throws RemoteException {
        }

        @Override // android.content.rollback.IRollbackManager
        public void expireRollbackForPackage(String packageName) throws RemoteException {
        }

        @Override // android.content.rollback.IRollbackManager
        public int notifyStagedSession(int sessionId) throws RemoteException {
            return 0;
        }

        @Override // android.content.rollback.IRollbackManager
        public void blockRollbackManager(long millis) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IRollbackManager {
        static final int TRANSACTION_blockRollbackManager = 8;
        static final int TRANSACTION_commitRollback = 3;
        static final int TRANSACTION_expireRollbackForPackage = 6;
        static final int TRANSACTION_getAvailableRollbacks = 1;
        static final int TRANSACTION_getRecentlyCommittedRollbacks = 2;
        static final int TRANSACTION_notifyStagedSession = 7;
        static final int TRANSACTION_reloadPersistedData = 5;
        static final int TRANSACTION_snapshotAndRestoreUserData = 4;

        public Stub() {
            attachInterface(this, IRollbackManager.DESCRIPTOR);
        }

        public static IRollbackManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRollbackManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IRollbackManager)) {
                return (IRollbackManager) iin;
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
                    return "getAvailableRollbacks";
                case 2:
                    return "getRecentlyCommittedRollbacks";
                case 3:
                    return "commitRollback";
                case 4:
                    return "snapshotAndRestoreUserData";
                case 5:
                    return "reloadPersistedData";
                case 6:
                    return "expireRollbackForPackage";
                case 7:
                    return "notifyStagedSession";
                case 8:
                    return "blockRollbackManager";
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
                data.enforceInterface(IRollbackManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRollbackManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParceledListSlice _result = getAvailableRollbacks();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            ParceledListSlice _result2 = getRecentlyCommittedRollbacks();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int _arg0 = data.readInt();
                            ParceledListSlice _arg1 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            String _arg2 = data.readString();
                            IntentSender _arg3 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            commitRollback(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg02 = data.readString();
                            int[] _arg12 = data.createIntArray();
                            int _arg22 = data.readInt();
                            long _arg32 = data.readLong();
                            String _arg4 = data.readString();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            snapshotAndRestoreUserData(_arg02, _arg12, _arg22, _arg32, _arg4, _arg5);
                            reply.writeNoException();
                            break;
                        case 5:
                            reloadPersistedData();
                            reply.writeNoException();
                            break;
                        case 6:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            expireRollbackForPackage(_arg03);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = notifyStagedSession(_arg04);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 8:
                            long _arg05 = data.readLong();
                            data.enforceNoDataAvail();
                            blockRollbackManager(_arg05);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IRollbackManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRollbackManager.DESCRIPTOR;
            }

            @Override // android.content.rollback.IRollbackManager
            public ParceledListSlice getAvailableRollbacks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public ParceledListSlice getRecentlyCommittedRollbacks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public void commitRollback(int rollbackId, ParceledListSlice causePackages, String callerPackageName, IntentSender statusReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    _data.writeInt(rollbackId);
                    _data.writeTypedObject(causePackages, 0);
                    _data.writeString(callerPackageName);
                    _data.writeTypedObject(statusReceiver, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public void snapshotAndRestoreUserData(String packageName, int[] userIds, int appId, long ceDataInode, String seInfo, int token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeIntArray(userIds);
                    _data.writeInt(appId);
                    _data.writeLong(ceDataInode);
                    _data.writeString(seInfo);
                    _data.writeInt(token);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public void reloadPersistedData() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public void expireRollbackForPackage(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public int notifyStagedSession(int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.content.rollback.IRollbackManager
            public void blockRollbackManager(long millis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IRollbackManager.DESCRIPTOR);
                    _data.writeLong(millis);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
