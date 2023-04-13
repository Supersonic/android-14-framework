package android.security.identity;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IWritableCredential extends IInterface {
    public static final String DESCRIPTOR = "android.security.identity.IWritableCredential";

    byte[] getCredentialKeyCertificateChain(byte[] bArr) throws RemoteException;

    byte[] personalize(AccessControlProfileParcel[] accessControlProfileParcelArr, EntryNamespaceParcel[] entryNamespaceParcelArr, long j) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWritableCredential {
        @Override // android.security.identity.IWritableCredential
        public byte[] getCredentialKeyCertificateChain(byte[] challenge) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.IWritableCredential
        public byte[] personalize(AccessControlProfileParcel[] accessControlProfiles, EntryNamespaceParcel[] entryNamespaces, long secureUserId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWritableCredential {
        static final int TRANSACTION_getCredentialKeyCertificateChain = 1;
        static final int TRANSACTION_personalize = 2;

        public Stub() {
            attachInterface(this, IWritableCredential.DESCRIPTOR);
        }

        public static IWritableCredential asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWritableCredential.DESCRIPTOR);
            if (iin != null && (iin instanceof IWritableCredential)) {
                return (IWritableCredential) iin;
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
                    return "getCredentialKeyCertificateChain";
                case 2:
                    return "personalize";
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
                data.enforceInterface(IWritableCredential.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWritableCredential.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result = getCredentialKeyCertificateChain(_arg0);
                            reply.writeNoException();
                            reply.writeByteArray(_result);
                            break;
                        case 2:
                            AccessControlProfileParcel[] _arg02 = (AccessControlProfileParcel[]) data.createTypedArray(AccessControlProfileParcel.CREATOR);
                            EntryNamespaceParcel[] _arg1 = (EntryNamespaceParcel[]) data.createTypedArray(EntryNamespaceParcel.CREATOR);
                            long _arg2 = data.readLong();
                            data.enforceNoDataAvail();
                            byte[] _result2 = personalize(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeByteArray(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IWritableCredential {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWritableCredential.DESCRIPTOR;
            }

            @Override // android.security.identity.IWritableCredential
            public byte[] getCredentialKeyCertificateChain(byte[] challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWritableCredential.DESCRIPTOR);
                    _data.writeByteArray(challenge);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.IWritableCredential
            public byte[] personalize(AccessControlProfileParcel[] accessControlProfiles, EntryNamespaceParcel[] entryNamespaces, long secureUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWritableCredential.DESCRIPTOR);
                    _data.writeTypedArray(accessControlProfiles, 0);
                    _data.writeTypedArray(entryNamespaces, 0);
                    _data.writeLong(secureUserId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
