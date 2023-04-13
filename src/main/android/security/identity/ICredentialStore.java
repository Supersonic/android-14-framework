package android.security.identity;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.security.identity.ICredential;
import android.security.identity.ISession;
import android.security.identity.IWritableCredential;
/* loaded from: classes3.dex */
public interface ICredentialStore extends IInterface {
    public static final String DESCRIPTOR = "android.security.identity.ICredentialStore";
    public static final int ERROR_ALREADY_PERSONALIZED = 2;
    public static final int ERROR_AUTHENTICATION_KEY_NOT_FOUND = 9;
    public static final int ERROR_CIPHER_SUITE_NOT_SUPPORTED = 4;
    public static final int ERROR_DOCUMENT_TYPE_NOT_SUPPORTED = 8;
    public static final int ERROR_EPHEMERAL_PUBLIC_KEY_NOT_FOUND = 5;
    public static final int ERROR_GENERIC = 1;
    public static final int ERROR_INVALID_ITEMS_REQUEST_MESSAGE = 10;
    public static final int ERROR_INVALID_READER_SIGNATURE = 7;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_NOT_SUPPORTED = 12;
    public static final int ERROR_NO_AUTHENTICATION_KEY_AVAILABLE = 6;
    public static final int ERROR_NO_SUCH_CREDENTIAL = 3;
    public static final int ERROR_SESSION_TRANSCRIPT_MISMATCH = 11;

    IWritableCredential createCredential(String str, String str2) throws RemoteException;

    ISession createPresentationSession(int i) throws RemoteException;

    ICredential getCredentialByName(String str, int i) throws RemoteException;

    SecurityHardwareInfoParcel getSecurityHardwareInfo() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICredentialStore {
        @Override // android.security.identity.ICredentialStore
        public SecurityHardwareInfoParcel getSecurityHardwareInfo() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredentialStore
        public IWritableCredential createCredential(String credentialName, String docType) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredentialStore
        public ICredential getCredentialByName(String credentialName, int cipherSuite) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredentialStore
        public ISession createPresentationSession(int cipherSuite) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICredentialStore {
        static final int TRANSACTION_createCredential = 2;
        static final int TRANSACTION_createPresentationSession = 4;
        static final int TRANSACTION_getCredentialByName = 3;
        static final int TRANSACTION_getSecurityHardwareInfo = 1;

        public Stub() {
            attachInterface(this, ICredentialStore.DESCRIPTOR);
        }

        public static ICredentialStore asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICredentialStore.DESCRIPTOR);
            if (iin != null && (iin instanceof ICredentialStore)) {
                return (ICredentialStore) iin;
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
                    return "getSecurityHardwareInfo";
                case 2:
                    return "createCredential";
                case 3:
                    return "getCredentialByName";
                case 4:
                    return "createPresentationSession";
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
                data.enforceInterface(ICredentialStore.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICredentialStore.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SecurityHardwareInfoParcel _result = getSecurityHardwareInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            IWritableCredential _result2 = createCredential(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            ICredential _result3 = getCredentialByName(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            ISession _result4 = createPresentationSession(_arg03);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICredentialStore {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICredentialStore.DESCRIPTOR;
            }

            @Override // android.security.identity.ICredentialStore
            public SecurityHardwareInfoParcel getSecurityHardwareInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialStore.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    SecurityHardwareInfoParcel _result = (SecurityHardwareInfoParcel) _reply.readTypedObject(SecurityHardwareInfoParcel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredentialStore
            public IWritableCredential createCredential(String credentialName, String docType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialStore.DESCRIPTOR);
                    _data.writeString(credentialName);
                    _data.writeString(docType);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IWritableCredential _result = IWritableCredential.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredentialStore
            public ICredential getCredentialByName(String credentialName, int cipherSuite) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialStore.DESCRIPTOR);
                    _data.writeString(credentialName);
                    _data.writeInt(cipherSuite);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    ICredential _result = ICredential.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredentialStore
            public ISession createPresentationSession(int cipherSuite) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredentialStore.DESCRIPTOR);
                    _data.writeInt(cipherSuite);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ISession _result = ISession.Stub.asInterface(_reply.readStrongBinder());
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
