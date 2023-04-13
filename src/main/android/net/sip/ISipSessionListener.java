package android.net.sip;

import android.net.sip.ISipSession;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface ISipSessionListener extends IInterface {
    void onCallBusy(ISipSession iSipSession) throws RemoteException;

    void onCallChangeFailed(ISipSession iSipSession, int i, String str) throws RemoteException;

    void onCallEnded(ISipSession iSipSession) throws RemoteException;

    void onCallEstablished(ISipSession iSipSession, String str) throws RemoteException;

    void onCallTransferring(ISipSession iSipSession, String str) throws RemoteException;

    void onCalling(ISipSession iSipSession) throws RemoteException;

    void onError(ISipSession iSipSession, int i, String str) throws RemoteException;

    void onRegistering(ISipSession iSipSession) throws RemoteException;

    void onRegistrationDone(ISipSession iSipSession, int i) throws RemoteException;

    void onRegistrationFailed(ISipSession iSipSession, int i, String str) throws RemoteException;

    void onRegistrationTimeout(ISipSession iSipSession) throws RemoteException;

    void onRinging(ISipSession iSipSession, SipProfile sipProfile, String str) throws RemoteException;

    void onRingingBack(ISipSession iSipSession) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISipSessionListener {
        @Override // android.net.sip.ISipSessionListener
        public void onCalling(ISipSession session) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRinging(ISipSession session, SipProfile caller, String sessionDescription) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRingingBack(ISipSession session) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onCallEstablished(ISipSession session, String sessionDescription) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onCallEnded(ISipSession session) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onCallBusy(ISipSession session) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onCallTransferring(ISipSession newSession, String sessionDescription) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onError(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onCallChangeFailed(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRegistering(ISipSession session) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRegistrationDone(ISipSession session, int duration) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRegistrationFailed(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
        }

        @Override // android.net.sip.ISipSessionListener
        public void onRegistrationTimeout(ISipSession session) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISipSessionListener {
        public static final String DESCRIPTOR = "android.net.sip.ISipSessionListener";
        static final int TRANSACTION_onCallBusy = 6;
        static final int TRANSACTION_onCallChangeFailed = 9;
        static final int TRANSACTION_onCallEnded = 5;
        static final int TRANSACTION_onCallEstablished = 4;
        static final int TRANSACTION_onCallTransferring = 7;
        static final int TRANSACTION_onCalling = 1;
        static final int TRANSACTION_onError = 8;
        static final int TRANSACTION_onRegistering = 10;
        static final int TRANSACTION_onRegistrationDone = 11;
        static final int TRANSACTION_onRegistrationFailed = 12;
        static final int TRANSACTION_onRegistrationTimeout = 13;
        static final int TRANSACTION_onRinging = 2;
        static final int TRANSACTION_onRingingBack = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISipSessionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISipSessionListener)) {
                return (ISipSessionListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ISipSession _arg0 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCalling(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            ISipSession _arg02 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            SipProfile _arg1 = (SipProfile) data.readTypedObject(SipProfile.CREATOR);
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onRinging(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 3:
                            ISipSession _arg03 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onRingingBack(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            ISipSession _arg04 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            onCallEstablished(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            ISipSession _arg05 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCallEnded(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            ISipSession _arg06 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCallBusy(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            ISipSession _arg07 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            onCallTransferring(_arg07, _arg13);
                            reply.writeNoException();
                            break;
                        case 8:
                            ISipSession _arg08 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            int _arg14 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg08, _arg14, _arg22);
                            reply.writeNoException();
                            break;
                        case 9:
                            ISipSession _arg09 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            int _arg15 = data.readInt();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            onCallChangeFailed(_arg09, _arg15, _arg23);
                            reply.writeNoException();
                            break;
                        case 10:
                            ISipSession _arg010 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onRegistering(_arg010);
                            reply.writeNoException();
                            break;
                        case TRANSACTION_onRegistrationDone /* 11 */:
                            ISipSession _arg011 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            onRegistrationDone(_arg011, _arg16);
                            reply.writeNoException();
                            break;
                        case TRANSACTION_onRegistrationFailed /* 12 */:
                            ISipSession _arg012 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            String _arg24 = data.readString();
                            data.enforceNoDataAvail();
                            onRegistrationFailed(_arg012, _arg17, _arg24);
                            reply.writeNoException();
                            break;
                        case TRANSACTION_onRegistrationTimeout /* 13 */:
                            ISipSession _arg013 = ISipSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onRegistrationTimeout(_arg013);
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
        public static class Proxy implements ISipSessionListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCalling(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRinging(ISipSession session, SipProfile caller, String sessionDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeTypedObject(caller, 0);
                    _data.writeString(sessionDescription);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRingingBack(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCallEstablished(ISipSession session, String sessionDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeString(sessionDescription);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCallEnded(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCallBusy(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCallTransferring(ISipSession newSession, String sessionDescription) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(newSession);
                    _data.writeString(sessionDescription);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onError(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeInt(errorCode);
                    _data.writeString(errorMessage);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onCallChangeFailed(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeInt(errorCode);
                    _data.writeString(errorMessage);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRegistering(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRegistrationDone(ISipSession session, int duration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeInt(duration);
                    this.mRemote.transact(Stub.TRANSACTION_onRegistrationDone, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRegistrationFailed(ISipSession session, int errorCode, String errorMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeInt(errorCode);
                    _data.writeString(errorMessage);
                    this.mRemote.transact(Stub.TRANSACTION_onRegistrationFailed, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSessionListener
            public void onRegistrationTimeout(ISipSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    this.mRemote.transact(Stub.TRANSACTION_onRegistrationTimeout, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
