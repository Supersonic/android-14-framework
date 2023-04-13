package android.net.sip;

import android.net.sip.ISipSessionListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface ISipSession extends IInterface {
    void answerCall(String str, int i) throws RemoteException;

    void changeCall(String str, int i) throws RemoteException;

    void endCall() throws RemoteException;

    String getCallId() throws RemoteException;

    String getLocalIp() throws RemoteException;

    SipProfile getLocalProfile() throws RemoteException;

    SipProfile getPeerProfile() throws RemoteException;

    int getState() throws RemoteException;

    boolean isInCall() throws RemoteException;

    void makeCall(SipProfile sipProfile, String str, int i) throws RemoteException;

    void register(int i) throws RemoteException;

    void setListener(ISipSessionListener iSipSessionListener) throws RemoteException;

    void unregister() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISipSession {
        @Override // android.net.sip.ISipSession
        public String getLocalIp() throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipSession
        public SipProfile getLocalProfile() throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipSession
        public SipProfile getPeerProfile() throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipSession
        public int getState() throws RemoteException {
            return 0;
        }

        @Override // android.net.sip.ISipSession
        public boolean isInCall() throws RemoteException {
            return false;
        }

        @Override // android.net.sip.ISipSession
        public String getCallId() throws RemoteException {
            return null;
        }

        @Override // android.net.sip.ISipSession
        public void setListener(ISipSessionListener listener) throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void register(int duration) throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void unregister() throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void makeCall(SipProfile callee, String sessionDescription, int timeout) throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void answerCall(String sessionDescription, int timeout) throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void endCall() throws RemoteException {
        }

        @Override // android.net.sip.ISipSession
        public void changeCall(String sessionDescription, int timeout) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISipSession {
        public static final String DESCRIPTOR = "android.net.sip.ISipSession";
        static final int TRANSACTION_answerCall = 11;
        static final int TRANSACTION_changeCall = 13;
        static final int TRANSACTION_endCall = 12;
        static final int TRANSACTION_getCallId = 6;
        static final int TRANSACTION_getLocalIp = 1;
        static final int TRANSACTION_getLocalProfile = 2;
        static final int TRANSACTION_getPeerProfile = 3;
        static final int TRANSACTION_getState = 4;
        static final int TRANSACTION_isInCall = 5;
        static final int TRANSACTION_makeCall = 10;
        static final int TRANSACTION_register = 8;
        static final int TRANSACTION_setListener = 7;
        static final int TRANSACTION_unregister = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISipSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISipSession)) {
                return (ISipSession) iin;
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
                            String _result = getLocalIp();
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            SipProfile _result2 = getLocalProfile();
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            SipProfile _result3 = getPeerProfile();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            int _result4 = getState();
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 5:
                            boolean _result5 = isInCall();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 6:
                            String _result6 = getCallId();
                            reply.writeNoException();
                            reply.writeString(_result6);
                            break;
                        case 7:
                            ISipSessionListener _arg0 = ISipSessionListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            register(_arg02);
                            reply.writeNoException();
                            break;
                        case 9:
                            unregister();
                            reply.writeNoException();
                            break;
                        case 10:
                            SipProfile _arg03 = (SipProfile) data.readTypedObject(SipProfile.CREATOR);
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            makeCall(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case TRANSACTION_answerCall /* 11 */:
                            String _arg04 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            answerCall(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case TRANSACTION_endCall /* 12 */:
                            endCall();
                            reply.writeNoException();
                            break;
                        case TRANSACTION_changeCall /* 13 */:
                            String _arg05 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            changeCall(_arg05, _arg13);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISipSession {
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

            @Override // android.net.sip.ISipSession
            public String getLocalIp() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public SipProfile getLocalProfile() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SipProfile _result = (SipProfile) _reply.readTypedObject(SipProfile.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public SipProfile getPeerProfile() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    SipProfile _result = (SipProfile) _reply.readTypedObject(SipProfile.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public int getState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public boolean isInCall() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public String getCallId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void setListener(ISipSessionListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void register(int duration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(duration);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void unregister() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void makeCall(SipProfile callee, String sessionDescription, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(callee, 0);
                    _data.writeString(sessionDescription);
                    _data.writeInt(timeout);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void answerCall(String sessionDescription, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionDescription);
                    _data.writeInt(timeout);
                    this.mRemote.transact(Stub.TRANSACTION_answerCall, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void endCall() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_endCall, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.sip.ISipSession
            public void changeCall(String sessionDescription, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionDescription);
                    _data.writeInt(timeout);
                    this.mRemote.transact(Stub.TRANSACTION_changeCall, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
