package android.security.identity;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.security.identity.ICredential;
/* loaded from: classes3.dex */
public interface ISession extends IInterface {
    public static final String DESCRIPTOR = "android.security.identity.ISession";

    long getAuthChallenge() throws RemoteException;

    ICredential getCredentialForPresentation(String str) throws RemoteException;

    byte[] getEphemeralKeyPair() throws RemoteException;

    void setReaderEphemeralPublicKey(byte[] bArr) throws RemoteException;

    void setSessionTranscript(byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISession {
        @Override // android.security.identity.ISession
        public byte[] getEphemeralKeyPair() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ISession
        public long getAuthChallenge() throws RemoteException {
            return 0L;
        }

        @Override // android.security.identity.ISession
        public void setReaderEphemeralPublicKey(byte[] publicKey) throws RemoteException {
        }

        @Override // android.security.identity.ISession
        public void setSessionTranscript(byte[] sessionTranscript) throws RemoteException {
        }

        @Override // android.security.identity.ISession
        public ICredential getCredentialForPresentation(String credentialName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISession {
        static final int TRANSACTION_getAuthChallenge = 2;
        static final int TRANSACTION_getCredentialForPresentation = 5;
        static final int TRANSACTION_getEphemeralKeyPair = 1;
        static final int TRANSACTION_setReaderEphemeralPublicKey = 3;
        static final int TRANSACTION_setSessionTranscript = 4;

        public Stub() {
            attachInterface(this, ISession.DESCRIPTOR);
        }

        public static ISession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISession.DESCRIPTOR);
            if (iin != null && (iin instanceof ISession)) {
                return (ISession) iin;
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
                    return "getEphemeralKeyPair";
                case 2:
                    return "getAuthChallenge";
                case 3:
                    return "setReaderEphemeralPublicKey";
                case 4:
                    return "setSessionTranscript";
                case 5:
                    return "getCredentialForPresentation";
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
                data.enforceInterface(ISession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _result = getEphemeralKeyPair();
                            reply.writeNoException();
                            reply.writeByteArray(_result);
                            break;
                        case 2:
                            long _result2 = getAuthChallenge();
                            reply.writeNoException();
                            reply.writeLong(_result2);
                            break;
                        case 3:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setReaderEphemeralPublicKey(_arg0);
                            reply.writeNoException();
                            break;
                        case 4:
                            byte[] _arg02 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setSessionTranscript(_arg02);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            ICredential _result3 = getCredentialForPresentation(_arg03);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISession.DESCRIPTOR;
            }

            @Override // android.security.identity.ISession
            public byte[] getEphemeralKeyPair() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISession.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ISession
            public long getAuthChallenge() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISession.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ISession
            public void setReaderEphemeralPublicKey(byte[] publicKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISession.DESCRIPTOR);
                    _data.writeByteArray(publicKey);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ISession
            public void setSessionTranscript(byte[] sessionTranscript) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISession.DESCRIPTOR);
                    _data.writeByteArray(sessionTranscript);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ISession
            public ICredential getCredentialForPresentation(String credentialName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISession.DESCRIPTOR);
                    _data.writeString(credentialName);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    ICredential _result = ICredential.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
