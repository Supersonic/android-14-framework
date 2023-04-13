package com.android.internal.compat;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IPlatformCompatNative extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.compat.IPlatformCompatNative";

    boolean isChangeEnabledByPackageName(long j, String str, int i) throws RemoteException;

    boolean isChangeEnabledByUid(long j, int i) throws RemoteException;

    void reportChangeByPackageName(long j, String str, int i) throws RemoteException;

    void reportChangeByUid(long j, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IPlatformCompatNative {
        @Override // com.android.internal.compat.IPlatformCompatNative
        public void reportChangeByPackageName(long changeId, String packageName, int userId) throws RemoteException {
        }

        @Override // com.android.internal.compat.IPlatformCompatNative
        public void reportChangeByUid(long changeId, int uid) throws RemoteException {
        }

        @Override // com.android.internal.compat.IPlatformCompatNative
        public boolean isChangeEnabledByPackageName(long changeId, String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.compat.IPlatformCompatNative
        public boolean isChangeEnabledByUid(long changeId, int uid) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IPlatformCompatNative {
        static final int TRANSACTION_isChangeEnabledByPackageName = 3;
        static final int TRANSACTION_isChangeEnabledByUid = 4;
        static final int TRANSACTION_reportChangeByPackageName = 1;
        static final int TRANSACTION_reportChangeByUid = 2;

        public Stub() {
            attachInterface(this, IPlatformCompatNative.DESCRIPTOR);
        }

        public static IPlatformCompatNative asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPlatformCompatNative.DESCRIPTOR);
            if (iin != null && (iin instanceof IPlatformCompatNative)) {
                return (IPlatformCompatNative) iin;
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
                    return "reportChangeByPackageName";
                case 2:
                    return "reportChangeByUid";
                case 3:
                    return "isChangeEnabledByPackageName";
                case 4:
                    return "isChangeEnabledByUid";
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
                data.enforceInterface(IPlatformCompatNative.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPlatformCompatNative.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            long _arg0 = data.readLong();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            reportChangeByPackageName(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            long _arg02 = data.readLong();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            reportChangeByUid(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            long _arg03 = data.readLong();
                            String _arg13 = data.readString();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = isChangeEnabledByPackageName(_arg03, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            long _arg04 = data.readLong();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = isChangeEnabledByUid(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IPlatformCompatNative {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPlatformCompatNative.DESCRIPTOR;
            }

            @Override // com.android.internal.compat.IPlatformCompatNative
            public void reportChangeByPackageName(long changeId, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPlatformCompatNative.DESCRIPTOR);
                    _data.writeLong(changeId);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.compat.IPlatformCompatNative
            public void reportChangeByUid(long changeId, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPlatformCompatNative.DESCRIPTOR);
                    _data.writeLong(changeId);
                    _data.writeInt(uid);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.compat.IPlatformCompatNative
            public boolean isChangeEnabledByPackageName(long changeId, String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPlatformCompatNative.DESCRIPTOR);
                    _data.writeLong(changeId);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.compat.IPlatformCompatNative
            public boolean isChangeEnabledByUid(long changeId, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPlatformCompatNative.DESCRIPTOR);
                    _data.writeLong(changeId);
                    _data.writeInt(uid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
